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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.clinportal.util.global.Constants;
/**
 *Common tab action class defined for forwarding the action to the corresponding Add action from the simpleQueryInterface.jsp file 
 * @author nitesh_marwaha
 *
 */
public class CommonTabAction extends Action{
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)throws IOException, ServletException
	{
		String page=(String)request.getParameter(Constants.PAGE_OF);
		if(page== null)
		{
			return mapping.findForward(Constants.SUCCESS);
		}
		return mapping.findForward(page);
	}
}
