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
 * <p>Title: SiteAction Class>
 * <p>Description:	This class initializes the fields of the Site Add/Edit webpage.</p>
 * Copyright:    Copyright (c) 2008 year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Falguni Sachde
 * @version 1.00
 * 
 */

package edu.wustl.clinportal.action;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.clinportal.actionForm.SiteForm;
import edu.wustl.clinportal.bizlogic.UserBizLogic;
import edu.wustl.clinportal.domain.Address;
import edu.wustl.clinportal.domain.User;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.action.SecureAction;
import edu.wustl.common.cde.CDEManager;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.security.global.Permissions;
import edu.wustl.security.privilege.PrivilegeCache;
import edu.wustl.security.privilege.PrivilegeManager;

/**
 * This class initializes the fields of the Site Add/Edit web page.
 * @author falguni sachde
 */
public class SiteAction extends SecureAction
{

	private transient Logger logger = Logger.getCommonLogger(SiteAction.class);

	/**
	 * Overrides the execute method of Action class.
	 * Sets the various fields in Site Add/Edit web page.
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

		if (!isAuthorizedToExecute(request))
		{
			ActionErrors errors = new ActionErrors();
			ActionError error = new ActionError("access.execute.action.denied");
			errors.add(ActionErrors.GLOBAL_ERROR, error);
			saveErrors(request, errors);

			return mapping.findForward(edu.wustl.common.util.global.Constants.ACCESS_DENIED);
		}
		SiteForm siteForm = (SiteForm) form;
		//Gets the value of the operation parameter.
		String operation = request.getParameter(Constants.OPERATION);

		//Sets the operation attribute to be used in the Add/Edit User Page.
		if (operation != null)
		{
			request.setAttribute(Constants.OPERATION, operation);
		}

		//Sets the countryList attribute to be used in the Add/Edit User Page.
		List countryList = CDEManager.getCDEManager().getPermissibleValueList(
				Constants.CDE_NAME_COUNTRY_LIST, null);
		request.setAttribute(Constants.COUNTRYLIST, countryList);

		//Sets the stateList attribute to be used in the Add/Edit User Page.
		List stateList = CDEManager.getCDEManager().getPermissibleValueList(
				Constants.CDE_NAME_STATE_LIST, null);
		request.setAttribute(Constants.STATELIST, stateList);

		//Sets the activityStatusList attribute to be used in the Site Add/Edit Page.
		request.setAttribute(Constants.ACTIVITYSTATUSLIST, Constants.SITE_ACTIVITY_STATUS_VALUES);

		//Sets the siteTypeList attribute to be used in the Site Add/Edit Page.
		List siteList = CDEManager.getCDEManager().getPermissibleValueList(
				Constants.CDE_NAME_SITE_TYPE, null);
		request.setAttribute(Constants.SITETYPELIST, siteList);

		IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
		UserBizLogic userBizLogic = (UserBizLogic) factory.getBizLogic(Constants.USER_FORM_ID);
		Collection coll = userBizLogic.getUsers(operation);
		request.setAttribute(Constants.USERLIST, coll);

		boolean isOnChange = getIsOnChange(request);
		Long coordinatorId = getCoordinatorId(request);
		setFormValues(siteForm, isOnChange, coordinatorId);
		String pageOf = (String) request.getParameter(Constants.PAGEOF);

		if (pageOf != null)
		{
			request.setAttribute(Constants.PAGEOF, pageOf);
		}
		return mapping.findForward(pageOf);
	}

	/**
	 * @param siteForm
	 * @param isOnChange
	 * @param coordinatorId
	 * @throws DAOException
	 * @throws BizLogicException 
	 */
	private void setFormValues(SiteForm siteForm, boolean isOnChange, Long coordinatorId)
			throws DAOException, BizLogicException
	{
		if (siteForm != null && isOnChange && coordinatorId != null)
		{
			IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
			UserBizLogic userBizLogic = (UserBizLogic) factory.getBizLogic(Constants.USER_FORM_ID);
			List userList = userBizLogic.retrieve(User.class.getName(),
					Constants.SYSTEM_IDENTIFIER, coordinatorId);
			if (!userList.isEmpty() && userList.get(0) != null)
			{
				User user = (User) userList.get(0);

				String emailAddress = user.getEmailAddress();
				logger.debug("Email Id of Coordinator of Site : " + emailAddress);

				siteForm.setEmailAddress(emailAddress);
				setAddress(user.getAddress(), siteForm);

			}
		}

	}

	/**
	 * @param address
	 * @param siteForm
	 */
	private void setAddress(Address address, SiteForm siteForm)
	{
		if (address != null)
		{

			String street = (String) address.getStreet();
			siteForm.setStreet(street);

			String city = (String) address.getCity();
			siteForm.setCity(city);

			String state = (String) address.getState();
			siteForm.setState(state);

			String country = (String) address.getCountry();
			siteForm.setCountry(country);

			String zipCode = (String) address.getZipCode();
			siteForm.setZipCode(zipCode);

			String phoneNo = (String) address.getPhoneNumber();
			siteForm.setPhoneNumber(phoneNo);

			String faxNumber = (String) address.getFaxNumber();
			siteForm.setFaxNumber(faxNumber);

		}
	}

	/**
	 * method for getting coordinatorId from request
	 * @param request :object of HttpServletResponse
	 * @return coordinatorId
	 */
	private Long getCoordinatorId(HttpServletRequest request)
	{
		Long coordinatorId = null;
		String coordinatorIdStr = request.getParameter("coordinatorId");
		try
		{
			if (!Validator.isEmpty(coordinatorIdStr))
			{
				coordinatorId = Long.valueOf(coordinatorIdStr);
			}
		}
		catch (Exception e)
		{
			coordinatorId = null;
		}
		return coordinatorId;
	}

	/**
	 * method for getting isOnChange from request
	 * @param request:object of HttpServletResponse
	 * @return isOnChange :boolean 
	 */
	private boolean getIsOnChange(HttpServletRequest request)
	{
		boolean isOnChange = false;
		String str = request.getParameter("isOnChange");
		if (str != null && str.equals("true"))
		{
			isOnChange = true;
		}
		return isOnChange;
	}

	/**
	* @param request
	* @return
	* @throws Exception
	*/
	protected boolean isAuthorizedToExecute(HttpServletRequest request) throws Exception
	{
		PrivilegeManager privilegeManager = PrivilegeManager.getInstance();
		PrivilegeCache privilegeCache = privilegeManager
				.getPrivilegeCache(getUserLoginName(request));

		boolean isAuthorized = privilegeCache.hasPrivilege(
				getObjectIdForSecureMethodAccess(request), Permissions.EXECUTE);

		return isAuthorized;
	}
}