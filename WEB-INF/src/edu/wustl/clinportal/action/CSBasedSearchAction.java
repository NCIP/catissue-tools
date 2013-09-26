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

//import edu.wustl.clinportal.bizlogic.UserBizLogic;
import edu.wustl.security.manager.ISecurityManager;
//import edu.wustl.security.manager.SecurityManager;
import edu.wustl.security.manager.SecurityManagerFactory;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.action.SecureAction;
import edu.wustl.common.beans.SessionDataBean;
import gov.nih.nci.security.authorization.domainobjects.Role;

/**
 * This Action is for forwarding to CSResultsView Page 
 * @author Shital Lawhale
 *
 */
public class CSBasedSearchAction extends SecureAction
{

	/**This class is responsible to handle all the requests related to the clinical study BasedSearch. 
	 * @param form Action form which is associated with the class.
	 * @param mapping Action mappings specifying the mapping pages for the specified mapping attributes.
	 * @param request HTTPRequest which is submitted from the page.
	 * @param response HTTPRespons that is generated for the submitted request.
	 * @return ActionForward Actionforward instance specifying which page the control should go to.  
	 * @see org.apache.struts.action.Action
	 * @see org.apache.struts.action.ActionForm
	 * @see org.apache.struts.action.ActionForward
	 * @see org.apache.struts.action.ActionMapping
	 * @see javax.servlet.http.HttpServletRequest
	 * @see javax.servlet.http.HttpServletResponse
	 * 
	 * */
	public ActionForward executeSecureAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{

		SessionDataBean sessionDataBean = (SessionDataBean) request.getSession().getAttribute(
				edu.wustl.common.util.global.Constants.SESSION_DATA);
		long csmUserId = Long.valueOf(sessionDataBean.getCsmUserId());
		
		//Role role = SecurityManager.getInstance(UserBizLogic.class).getUserRole(csmUserId);
		ISecurityManager securityManager = SecurityManagerFactory.getSecurityManager();
		Role role = securityManager.getUserRole(csmUserId);
		
		//Checking for the role of user , if role is technician then access is denied for viewing
		//participant information in CP Based view 
		if (role.getName() != null && role.getName().equals(Constants.TECHNICIAN))
		{
			request.getSession().setAttribute("Access", "Denied");
		}

		return mapping.findForward(Constants.SUCCESS);

	}

}
