/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */

/**
 * <p>Title: UserAction Class>
 * <p>Description:	This class initializes the fields in the User Add/Edit webpage.</p>
 * Copyright:    Copyright (c)2008  year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Falguni Sachde
 * @version 1.00
 * 
 */

package edu.wustl.clinportal.action;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.clinportal.domain.CancerResearchGroup;
import edu.wustl.clinportal.domain.Department;
import edu.wustl.clinportal.domain.Institution;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.action.SecureAction;
import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.bizlogic.IBizLogic;
import edu.wustl.common.cde.CDEManager;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.QueryWhereClause;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.security.exception.SMException;
import edu.wustl.security.global.Permissions;
import edu.wustl.security.manager.ISecurityManager;
import edu.wustl.security.manager.SecurityManagerFactory;
import edu.wustl.security.privilege.PrivilegeCache;
import edu.wustl.security.privilege.PrivilegeManager;
import gov.nih.nci.security.authorization.domainobjects.Role;

/**
 * This class initializes the fields in the User Add/Edit web page.
 * @author falguni sachde
 */
public class UserAction extends SecureAction
{

	private transient Logger logger = Logger.getCommonLogger(UserAction.class);

	/**
	 * Overrides the execute method of Action class.
	 * Sets the various fields in User Add/Edit web page.
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward executeSecureAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String pageOf = request.getParameter(Constants.PAGEOF);
		if (!isAuthorizedToExecute(request, pageOf))
		{
			ActionErrors errors = new ActionErrors();
			ActionError error = new ActionError("access.execute.action.denied");
			errors.add(ActionErrors.GLOBAL_ERROR, error);
			saveErrors(request, errors);

			return mapping.findForward(edu.wustl.common.util.global.Constants.ACCESS_DENIED);
		}
		//Gets the value of the operation parameter.
		String operation = request.getParameter(Constants.OPERATION);

		//Sets the operation attribute to be used in the Add/Edit User Page. 
		request.setAttribute(Constants.OPERATION, operation);

		//Sets the countryList attribute to be used in the Add/Edit User Page.
		List countryList = CDEManager.getCDEManager().getPermissibleValueList(
				Constants.CDE_NAME_COUNTRY_LIST, null);
		request.setAttribute(Constants.COUNTRYLIST, countryList);

		//Sets the stateList attribute to be used in the Add/Edit User Page.
		List stateList = CDEManager.getCDEManager().getPermissibleValueList(
				Constants.CDE_NAME_STATE_LIST, null);
		request.setAttribute(Constants.STATELIST, stateList);

		//Sets the pageOf attribute (for Add,Edit or Query Interface).

		request.setAttribute(Constants.PAGEOF, pageOf);
		String target = pageOf;

		setRequestList(request);
		setDefaultValues(request);

		//Populate the activity status drop down if the operation is edit 
		//and the user page is of administrative tab.
		if (operation.equals(Constants.EDIT) && pageOf.equals(Constants.PAGEOF_USER_ADMIN))
		{
			request.setAttribute(Constants.ACTIVITYSTATUSLIST,
					Constants.USER_ACTIVITY_STATUS_VALUES);
		}
		setRequestAttribute(request, operation, pageOf);

		logger.debug("pageOf :---------- " + pageOf);

		// ------------- add new
		String reqPath = request.getParameter(Constants.REQ_PATH);

		request.setAttribute(Constants.REQ_PATH, reqPath);

		AbstractActionForm aForm = (AbstractActionForm) form;
		if (reqPath != null && aForm != null)
		{
			aForm.setRedirectTo(reqPath);
		}

		logger.debug("USerAction redirect :---------- " + reqPath);

		return mapping.findForward(target);
	}

	/**
	 * 
	 * @param form
	 * @throws BizLogicException
	 */
	private void setDefaultValues(HttpServletRequest request) throws DAOException,
			BizLogicException
	{
		String[] selectColumnNames = {Constants.SYSTEM_IDENTIFIER};
		String[] whereColumnNames = {Constants.NAME};
		String[] whereColumnCondition = {"="};
		Object[] whereColumnValues = {Constants.NOT_AVAILABLE};
		String sourceObjectName = CancerResearchGroup.class.getName();
		String joinCondition = Constants.AND_JOIN_CONDITION;
		QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
		queryWhereClause.getWhereCondition(whereColumnNames, whereColumnCondition,
				whereColumnValues, joinCondition);
		DefaultBizLogic bizLogic = new DefaultBizLogic();
		List crgList = bizLogic.retrieve(sourceObjectName, selectColumnNames, queryWhereClause);

		if (!crgList.isEmpty())
		{
			request.setAttribute(Constants.CRGID, crgList.get(0).toString());
		}
	}

	/**
	 * @param request
	 * @throws BizLogicException 
	 */
	private void setRequestList(HttpServletRequest request) throws BizLogicException
	{

		IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
		IBizLogic bizLogic = factory.getBizLogic(Constants.USER_FORM_ID);

		//Sets the instituteList attribute to be used in the Add/Edit User Page.
		String sourceObjectName = Institution.class.getName();
		String[] displayNameFields = {Constants.NAME};
		String valueField = Constants.SYSTEM_IDENTIFIER;

		List instituteList = bizLogic.getList(sourceObjectName, displayNameFields, valueField,
				false);
		request.setAttribute(Constants.INSTITUTIONLIST, instituteList);

		//Sets the departmentList attribute to be used in the Add/Edit User Page.
		sourceObjectName = Department.class.getName();
		List departmentList = bizLogic.getList(sourceObjectName, displayNameFields, valueField,
				false);
		request.setAttribute(Constants.DEPARTMENTLIST, departmentList);

		//Sets the cancerResearchGroupList attribute to be used in the Add/Edit User Page.
		sourceObjectName = CancerResearchGroup.class.getName();
		List crgList = bizLogic.getList(sourceObjectName, displayNameFields, valueField, false);
		request.setAttribute(Constants.CANCER_RESEARCH_GROUP_LIST, crgList);
	}

	/**
	 * @param request
	 * @param operation
	 * @param pageOf
	 * @throws SMException
	 */
	private void setRequestAttribute(HttpServletRequest request, String operation, String pageOf)
			throws SMException
	{
		//Populate the role drop down if the page is of approve user or (Add/Edit) user page of administrative tab. 
		if (pageOf.equals(Constants.PAGEOF_APPROVE_USER)
				|| pageOf.equals(Constants.PAGEOF_USER_ADMIN)
				|| pageOf.equals(Constants.PAGEOF_USER_PROFILE))
		{
			List roleNVBeanLst = getRoles();

			request.setAttribute(Constants.ROLELIST, roleNVBeanLst);
		}

		//Populate the status drop down for approve user page.(Approve,Reject,Pending)
		if (pageOf.equals(Constants.PAGEOF_APPROVE_USER))
		{
			request.setAttribute(Constants.APPROVE_USER_STATUS_LIST,
					Constants.APPROVE_USER_STATUS_VALUES);
		}

	}

	/**
	 * Returns a list of all roles that can be assigned to a user.
	 * @return a list of all roles that can be assigned to a user.
	 * @throws SMException
	 */
	private List getRoles() throws SMException
	{
		//Sets the roleList attribute to be used in the Add/Edit User Page.

		ISecurityManager securityManager = SecurityManagerFactory.getSecurityManager();
		List roleList = securityManager.getRoles();

		ListIterator iterator = roleList.listIterator();

		List roleNVBeanList = new ArrayList();
		NameValueBean nameValueBean = new NameValueBean();
		nameValueBean.setName(Constants.SELECT_OPTION);
		nameValueBean.setValue(String.valueOf(Constants.SELECT_OPTION_VALUE));
		roleNVBeanList.add(nameValueBean);

		while (iterator.hasNext())
		{
			Role role = (Role) iterator.next();
			nameValueBean = new NameValueBean();
			nameValueBean.setName(role.getName());
			nameValueBean.setValue(String.valueOf(role.getId()));
			roleNVBeanList.add(nameValueBean);
		}
		return roleNVBeanList;
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	protected SessionDataBean getSessionData(HttpServletRequest request)
	{
		SessionDataBean objSessionbean = new SessionDataBean();
		String pageOf = request.getParameter(Constants.PAGEOF);
		if (pageOf.equals(Constants.PAGEOF_USER_ADMIN)
				|| pageOf.equals(Constants.PAGEOF_APPROVE_USER))
		{
			objSessionbean = super.getSessionData(request);
		}
		return objSessionbean;
	}

	/**
	 * @param request
	 * @param pageOf 
	 * @return
	 * @throws Exception
	 */
	protected boolean isAuthorizedToExecute(HttpServletRequest request, String pageOf)
			throws Exception
	{
		boolean isAuthorized = true;
		if (pageOf.equals(Constants.PAGEOF_USER_ADMIN))
		{
			PrivilegeManager privilegeManager = PrivilegeManager.getInstance();
			PrivilegeCache privilegeCache = privilegeManager
					.getPrivilegeCache(getUserLoginName(request));
			isAuthorized = privilegeCache.hasPrivilege(getObjectIdForSecureMethodAccess(request),
					Permissions.EXECUTE);
		}
		if (pageOf.equals(Constants.PAGEOF_APPROVE_USER))
		{
			PrivilegeManager privilegeManager = PrivilegeManager.getInstance();
			PrivilegeCache privilegeCache = privilegeManager
					.getPrivilegeCache(getUserLoginName(request));
			isAuthorized = privilegeCache.hasPrivilege(getObjectIdForSecureMethodAccess(request),
					Permissions.EXECUTE);
		}
		return isAuthorized;
	}
}