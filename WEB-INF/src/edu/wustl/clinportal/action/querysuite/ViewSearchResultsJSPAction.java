/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */


package edu.wustl.clinportal.action.querysuite;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.clinportal.actionForm.CategorySearchForm;
import edu.wustl.clinportal.applet.AppletConstants;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.action.BaseAction;

/**
 * This is a action called whern user clicks on search button from addlimits .jsp. The result data lists are already
 * stored in session through an applet action ViewSearchResultsAction. This class just forwards the control to ViewSearchResults.jsp.
 * @author deepti_shelar
 *
 */
public class ViewSearchResultsJSPAction extends BaseAction
{

	/**
	 * This method loads the data required for ViewSearchResults.jsp
	 * @param mapping mapping
	 * @param form form
	 * @param request request
	 * @param response response
	 * @throws Exception Exception
	 * @return ActionForward actionForward
	 */
	protected ActionForward executeAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		CategorySearchForm actionForm = (CategorySearchForm) form;
		String nextOperation = actionForm.getNextOperation();
		ActionForward fwd = mapping.findForward(Constants.SUCCESS);
		if (nextOperation != null
				&& nextOperation.equalsIgnoreCase(AppletConstants.SHOW_ERROR_PAGE))
		{
			fwd = mapping.findForward(Constants.FAILURE);
		}
		return fwd;
	}
}
