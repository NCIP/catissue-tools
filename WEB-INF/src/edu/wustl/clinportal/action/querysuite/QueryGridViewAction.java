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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.action.BaseAction;
/**
 * This action is called when user clicks on Search button after forming the query object.This class loads required grid data in session/request.
 * And then it forwards control to simpleSearchDataView.jsp.
 * @author deepti_shelar
 */
public class QueryGridViewAction extends BaseAction
{ 
	protected ActionForward executeAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		HttpSession session = request.getSession();
		int pageNum = Constants.START_PAGE;
		request.setAttribute(Constants.PAGE_NUMBER,Integer.toString(pageNum));
		List dataList =(List)session.getAttribute(Constants.PAGINATION_DATA_LIST);
		request.setAttribute(Constants.PAGINATION_DATA_LIST,dataList);
		List<String> columnsList = (List<String>)session.getAttribute(edu.wustl.common.util.global.Constants.SPREADSHEET_COLUMN_LIST);
		request.setAttribute(edu.wustl.common.util.global.Constants.SPREADSHEET_COLUMN_LIST,columnsList);
		session.setAttribute(Constants.PAGINATION_DATA_LIST,null);
		String pageOf = (String)request.getParameter(Constants.PAGEOF);
		request.setAttribute(Constants.PAGEOF,pageOf);
		return mapping.findForward(Constants.SUCCESS);
	}
}
