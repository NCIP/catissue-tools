/**
 * <p>Title: InstitutionAction Class>
 * <p>Description:	This class initializes the fields in the Institution.jsp Page</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Mandar Deshmukh
 * @version 1.00
 * Created on Mar 22, 2005
 */

package edu.wustl.clinportal.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.action.SecureAction;
import edu.wustl.security.global.Permissions;
import edu.wustl.security.privilege.PrivilegeCache;
import edu.wustl.security.privilege.PrivilegeManager;

public class InstitutionAction extends SecureAction
{

	/**
	 * Overrides the execute method of Action class.
	 * Initializes the various drop down fields in Institution.jsp Page.
	 * @param mapping object of ActionMapping
	 * @param form object of ActionForm
	 * @param request object of HttpServletRequest
	 * @param response object of HttpServletResponse
	 * @throws Exception generic exception
	 * @return value for ActionForward object
	 */
	protected ActionForward executeSecureAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		//Gets the value of the operation parameter.
		String operation = request.getParameter(Constants.OPERATION);

		if (!isAuthorizedToExecute(request))
		{
			ActionErrors errors = new ActionErrors();
			ActionError error = new ActionError("access.execute.action.denied");
			errors.add(ActionErrors.GLOBAL_ERROR, error);
			saveErrors(request, errors);

			return mapping.findForward(edu.wustl.common.util.global.Constants.ACCESS_DENIED);
		}
		//Sets the operation attribute to be used in the Add/Edit Institution Page. 
		request.setAttribute(Constants.OPERATION, operation);

		return mapping.findForward((String) request.getParameter(Constants.PAGEOF));
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