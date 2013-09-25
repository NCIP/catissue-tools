/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */

                                       /*
 * @author
 *
 */
package edu.wustl.clinportal.action.annotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.common.action.BaseAction;

/**
 * @author preeti_munot
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DisplayAnnotationDataEntryPageAction extends BaseAction
{

    /* (non-Javadoc)
     * @see edu.wustl.common.action.BaseAction#executeAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
 
    protected ActionForward executeAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
    throws Exception
    {
        request.setAttribute("entityId",request.getParameter("entityId"));
        request.setAttribute("entityRecordId",request.getParameter("entityRecordId"));
        request.setAttribute(DEConstants.OBJ_IDENTIFIER,request.getParameter(DEConstants.OBJ_IDENTIFIER));
        request.setAttribute("staticEntityName",request.getParameter("staticEntityName"));
        
        
        return mapping.findForward(request.getParameter("pageOf"));
    }
}
