
package edu.wustl.clinportal.action;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.clinportal.actionForm.LoginForm;
import edu.wustl.clinportal.bizlogic.UserBizLogic;
import edu.wustl.clinportal.domain.User;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.clinportal.util.global.Utility;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.security.exception.SMException;
import edu.wustl.security.global.Roles;
import edu.wustl.security.manager.ISecurityManager;
import edu.wustl.security.manager.SecurityManagerFactory;
import edu.wustl.security.privilege.PrivilegeManager;

/**
 * 
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright: (c) Washington University, School of Medicine 2005</p>
 *<p>Company: Washington University, School of Medicine, </p>
 *@author Rukhsana Sameer
 *@version 1.0
 */
public class LoginAction extends Action
{

	private transient Logger logger = Logger.getCommonLogger(LoginAction.class);

	/**
	 * Overrides the execute method of Action class.
	 * Initializes the various drop down fields in Institute.jsp Page.
	 * @param mapping object of ActionMapping
	 * @param form object of ActionForm
	 * @param request object of HttpServletRequest
	 * @param response object of HttpServletResponse
	 * @throws IOException I/O exception
	 * @throws ServletException servlet exception
	 * @return value for ActionForward object
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException
	{
		if (form == null)
		{
			logger.debug("Form is Null");
			return mapping.findForward(Constants.FAILURE);
		}
		HttpSession prevSession = request.getSession();
		if (prevSession != null)
		{
			prevSession.invalidate();
		}
		LoginForm loginForm = (LoginForm) form;
		logger.info("Inside Login Action, Just before validation");

		String loginName = loginForm.getLoginName();
		try
		{
			User validUser = getUser(loginName, Constants.ACTIVITY_STATUS_ACTIVE);
			String password = loginForm.getPassword();
			if (validUser != null)
			{

				ISecurityManager securityManager = SecurityManagerFactory.getSecurityManager();
				boolean loginOK = securityManager.login(loginName, password);
				if (loginOK)
				{

					PrivilegeManager privilegeManager = PrivilegeManager.getInstance();
					privilegeManager.getPrivilegeCache(loginName);
					logger.info(">>>>>>>>>>>>> SUCCESSFUL LOGIN A <<<<<<<<< ");
					HttpSession session = request.getSession(true);

					Long userId = validUser.getId();
					String ipAddress = request.getRemoteAddr();
					boolean adminUser = false;
					String roleName = null; 
					if(validUser.getRoleId()!=null)
					    roleName=Utility.getRoleName(validUser.getRoleId().toString());
					if (roleName != null
							&& roleName.equals(edu.wustl.security.global.Constants.ROLE_ADMIN))
					{
						adminUser = true;
					}
					SessionDataBean sessionData = new SessionDataBean();
					sessionData.setAdmin(adminUser);
					sessionData.setUserName(loginName);
					sessionData.setIpAddress(ipAddress);
					sessionData.setUserId(userId);
					sessionData.setFirstName(validUser.getFirstName());
					sessionData.setLastName(validUser.getLastName());
					session.setAttribute("USER_ROLE",validUser.getRoleId());
					logger
							.info("CSM USer ID ....................... : "
									+ validUser.getCsmUserId());
					sessionData.setCsmUserId(validUser.getCsmUserId().toString());
					session.setAttribute(Constants.SESSION_DATA, sessionData);
					IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
					UserBizLogic userBizLogic = (UserBizLogic) factory
							.getBizLogic(Constants.USER_FORM_ID);

					String result = userBizLogic.checkFirstLoginAndExpiry(validUser);

					setSecurityParamsInSessionData(validUser, sessionData);

					String forwardToPage = getForwardToPageOnLogin(validUser.getCsmUserId()
							.longValue());
					if (forwardToPage == null)
					{
						ActionErrors errors = new ActionErrors();
						errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.noRole"));
						saveErrors(request, errors);
						session.setAttribute(Constants.SESSION_DATA, null);
						return mapping.findForward(Constants.FAILURE);
					}

					if (!result.equals(Constants.SUCCESS))
					{
						ActionErrors errors = new ActionErrors();
						errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(result));
						saveErrors(request, errors);
						session.setAttribute(Constants.SESSION_DATA, null);
						session.setAttribute(
								edu.wustl.common.util.global.Constants.TEMP_SESSION_DATA,
								sessionData);
						request.setAttribute(Constants.PAGEOF, Constants.PAGEOF_CHANGE_PASSWORD);
						return mapping
								.findForward(edu.wustl.common.util.global.Constants.ACCESS_DENIED);
					}

					// Forwarding to default page depending on user role		 

					return mapping.findForward(forwardToPage);
				}
				else
				{
					logger.info("User " + loginName
							+ " Invalid user. Sending back to the login Page");
					handleError(request, "errors.incorrectLoginNamePassword");
					return mapping.findForward(Constants.FAILURE);
				}
			} // if valid user
			else
			{
				//Check if User account is closed.
				if (getUser(loginName, Constants.ACTIVITY_STATUS_CLOSED) != null)
				{
					logger.info("User " + loginName
							+ " Closed user. Sending back to the login Page");
					handleError(request, "errors.closedUser");
					return mapping.findForward(Constants.FAILURE);
				}
				else
				{
					logger.info("User " + loginName
							+ " Invalid user. Sending back to the login Page");
					handleError(request, "errors.incorrectLoginNamePassword");
					return mapping.findForward(Constants.FAILURE);
				}// invalid user
			}
		}
		catch (Exception e)
		{
			logger.info("Exception: " + e.getMessage(), e);
			handleError(request, "errors.incorrectLoginNamePassword");
			return mapping.findForward(Constants.FAILURE);
		}
	}

	/**
	 * To set the Security Parameters in the given SessionDataBean object depending upon the role of the user.
	 * @param validUser reference to the User.
	 * @param sessionData The reference to the SessionDataBean object.
	 * @throws SMException
	 */
	private void setSecurityParamsInSessionData(User validUser, SessionDataBean sessionData)
			throws SMException
	{
		ISecurityManager securityManager = SecurityManagerFactory.getSecurityManager();
		String userRole = securityManager.getRoleName(validUser.getCsmUserId()); // verify changes
		if (userRole != null
				&& (userRole.equalsIgnoreCase(Roles.ADMINISTRATOR) || userRole
						.equalsIgnoreCase(Roles.SUPERVISOR)))
		{
			sessionData.setSecurityRequired(false);
		}
		else
		{
			sessionData.setSecurityRequired(true);
		}
	}

	/**
	 * Patch ID: 3842_2
	 * This function will take LoginID for user and return the appropriate default page.
	 * Get role from security manager and modify the role name where first 
	 * character is in upper case and rest all are in lower case add prefix "pageOf" 
	 * to modified role name and forward to that page.  
	 * @param loginId
	 * @return
	 * @throws SMException
	 */

	private String getForwardToPageOnLogin(Long loginId) throws SMException
	{
		ISecurityManager securityManager = SecurityManagerFactory.getSecurityManager();
		String roleName = securityManager.getRoleName(loginId);

		if (roleName != null && !roleName.equals(""))
		{
			String modifiedRolename = roleName.substring(0, 1).toUpperCase()
					+ roleName.substring(1, roleName.length()).toLowerCase();
			return (Constants.PAGEOF + modifiedRolename);
		}
		return null;
	}

	/**
	 * 
	 * @param request
	 * @param errorKey
	 */
	private void handleError(HttpServletRequest request, String errorKey)
	{
		ActionErrors errors = new ActionErrors();
		errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(errorKey));
		//Report any errors we have discovered
		if (!errors.isEmpty())
		{
			saveErrors(request, errors);
		}
	}

	/**
	 * 
	 * @param loginName
	 * @param activityStatus
	 * @return
	 * @throws BizLogicException
	 */
	private User getUser(String loginName, String activityStatus) throws BizLogicException
	{
		IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
		UserBizLogic userBizLogic = (UserBizLogic) factory.getBizLogic(Constants.USER_FORM_ID);
		String[] whereColumnName = {"activityStatus", "loginName"};
		String[] whereColCond = {"=", "="};
		String[] whereColumnValue = {activityStatus, loginName};

		List users = userBizLogic.retrieve(User.class.getName(), whereColumnName, whereColCond,
				whereColumnValue, Constants.AND_JOIN_CONDITION);

		if (users != null && !users.isEmpty())
		{
			User validUser = (User) users.get(0);
			return validUser;
		}
		return null;
	}
}