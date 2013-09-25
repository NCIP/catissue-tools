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
 * 
 */

package edu.wustl.clinportal.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.clinportal.util.global.Variables;
import edu.wustl.common.action.BaseAction;
import edu.wustl.common.util.logger.Logger;

/**
 * Action class to forward the request to the proper action based on the Alias Name of the object to be edited in Simple Search.
 *  
 * Description: Action class for forwarding request to the proper Action.
 */
public class SimpleSearchEditAction extends BaseAction
{

	private transient Logger logger = Logger.getCommonLogger(SimpleSearchEditAction.class);

	/**
	 * This method sets the pageOf attribute based on value of the aliasName parameter passed through request & redirect to the corresponding Action.
	 * @param mapping Action Mappings
	 * @param form The form object associated with the request.
	 * @param request The reference to HttpServletRequest
	 * @param response The reference to the HttpServletResponse.
	 * @see edu.wustl.common.action.BaseAction#executeAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected ActionForward executeAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long identifier = Long.valueOf(request.getParameter(Constants.SYSTEM_IDENTIFIER));
		request.setAttribute(Constants.SYSTEM_IDENTIFIER, identifier);
		String aliasName = request
				.getParameter(edu.wustl.common.util.global.Constants.TABLE_ALIAS_NAME);
		String pageOf = Variables.aliasAndPageOfMap.get(aliasName);
		request.setAttribute(Constants.PAGEOF, pageOf);
		logger.debug("identifier:" + identifier + " TABLE_ALIAS_NAME:" + aliasName + ":PAGEOF:"
				+ pageOf);
		return mapping.findForward(pageOf);
	}

}
