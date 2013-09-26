/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */


package edu.wustl.clinportal.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.action.BaseAction;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.security.manager.ISecurityManager;
import edu.wustl.security.manager.SecurityManagerFactory;
import edu.wustl.security.exception.SMException;
import edu.wustl.security.global.Roles;

/**
 * This class is called when user clicks on BioSpecimen tab.
 * Technicians can not access cp based view. But other users can access , so this class checks if the logged user is a technician .If yes then 
 * default page shown is specimen search. For users other than technician default page is CP based view.  
 * 
 */
/**
 * @author falguni_sachde
 *
 */
public class RoleBasedForwardAction extends BaseAction
{

	/**
	 * Overrides the executeACtion method of BaseAction class.Checks if the logged user is a 
	 * technician then access is denied for cp based view.
	 * @param mapping object of ActionMapping
	 * @param form object of ActionForm
	 * @param request object of HttpServletRequest
	 * @param response object of HttpServletResponse
	 * @throws Exception generic exception
	 * @return value for ActionForward object
	 */
	protected ActionForward executeAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String roleName = getRoleNameForUser(request);
		ActionForward fwd = mapping.findForward(Constants.SUCCESS);
		if (roleName.equalsIgnoreCase(Roles.TECHNICIAN))
		{
			fwd = mapping.findForward(edu.wustl.common.util.global.Constants.ACCESS_DENIED);
		}
		return fwd;
	}

	/**
	 * @param request HttpServletRequest
	 * @return String role name
	 * @throws NumberFormatException NumberFormatException
	 * @throws SMException SMException
	 */
	private String getRoleNameForUser(HttpServletRequest request) throws NumberFormatException,
			SMException
	{
		SessionDataBean sessionData = getSessionData(request);

		/*SecurityManager securityManager = SecurityManager.getInstance(LoginAction.class);
		String roleName = securityManager.getUserGroup(Long.valueOf(sessionData.getCsmUserId()));*/
		ISecurityManager securityManager = SecurityManagerFactory.getSecurityManager();
		String roleName = securityManager.getRoleName(Long.valueOf(sessionData.getCsmUserId()));
		
		return roleName;
	}
}
