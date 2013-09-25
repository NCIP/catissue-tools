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
 * Created on Aug 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package edu.wustl.clinportal.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.clinportal.actionForm.AdvanceSearchForm;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.clinportal.util.global.Utility;
import edu.wustl.common.action.BaseAction;
import edu.wustl.common.util.global.QuerySessionData;
import edu.wustl.common.util.logger.Logger;

/**
 * @author rukhsana
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SpreadsheetViewAction extends BaseAction
{

	private transient Logger logger = Logger.getCommonLogger(SpreadsheetViewAction.class);

	/**
	 * This method get call for simple search as well as advanced search.
	 * This method also get call when user clicks on page no of Pagination tag 
	 * from simple search result page as well as advanced search result page.
	 * Each time it calculates the paginationList to be displayed by grid control
	 * by getting pageNum from request object.
	 * @Override   
	 */
	protected ActionForward executeAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		/**
		 * Name: Deepti
		 * Description: Query performance issue. Instead of saving complete query results in session, resultd will be fetched for each result page navigation.
		 * object of class QuerySessionData will be saved session, which will contain the required information for query execution while navigating through query result pages.
		 * *  Here, extending this class from BaseAction  
		 */
		HttpSession session = request.getSession();
		//changes are done for check all 
		String checkAllPages = "";
		String check = (String) request.getParameter(Constants.CHECK_ALL_PAGES);
		if (check == null || check.equals(""))
		{
			checkAllPages = (String) session.getAttribute(Constants.CHECK_ALL_PAGES);
		}
		else
		{
			checkAllPages = check;
		}
		session.setAttribute(Constants.CHECK_ALL_PAGES, checkAllPages);
		String isAjax = (String) request.getParameter("isAjax");
		if (isAjax != null && isAjax.equals("true"))
		{
			response.setContentType("text/html");
			response.getWriter().write(checkAllPages);
			return null;
		}
		QuerySessionData querySessionData = (QuerySessionData) session
				.getAttribute(edu.wustl.common.util.global.Constants.QUERY_SESSION_DATA);

		String pageOf = (String) request.getAttribute(Constants.PAGEOF);
		if (pageOf == null)
		{
			pageOf = (String) request.getParameter(Constants.PAGEOF);
		}
		if (request.getAttribute(Constants.IDENTIFIER_FIELD_INDEX) == null)
		{
			String idFieldIndex = request.getParameter(Constants.IDENTIFIER_FIELD_INDEX);
			if (idFieldIndex != null && !idFieldIndex.equals(""))
			{
				request.setAttribute(Constants.IDENTIFIER_FIELD_INDEX, Integer
						.valueOf(idFieldIndex));
			}
		}
		logger.debug("Pageof in spreadsheetviewaction.........:" + pageOf);
		Object defViewAttribute = request.getAttribute(Constants.SPECIMENT_VIEW_ATTRIBUTE);
		if (defViewAttribute != null)// When the Default specimen view Check box is checked or unchecked, this will be evaluated.
		{
			List list = (List) request.getAttribute(Constants.SPREADSHEET_DATA_LIST);
			List columnNames = (List) request
					.getAttribute(edu.wustl.common.util.global.Constants.SPREADSHEET_COLUMN_LIST);
			session.setAttribute(edu.wustl.common.util.global.Constants.SPREADSHEET_COLUMN_LIST,
					columnNames);
			request.setAttribute(Constants.SPREADSHEET_DATA_LIST, list);
		}
		List list = null;
		String pagination = request.getParameter("isPaging");
		if (pagination == null || pagination.equals("false"))
		{
			list = (List) request.getAttribute(Constants.SPREADSHEET_DATA_LIST);
			List columnNames = (List) request
					.getAttribute(edu.wustl.common.util.global.Constants.SPREADSHEET_COLUMN_LIST);
			//Set the SPREADSHEET_DATA_LIST and SPREADSHEET_COLUMN_LIST in the session.
			//Next time when user clicks on pages of pagination Tag, it get the same list from the session
			//and based on current page no, it will calculate paginationDataList to be displayed by grid control.
			session.setAttribute(edu.wustl.common.util.global.Constants.SPREADSHEET_COLUMN_LIST,
					columnNames);
			request.setAttribute(Constants.PAGINATION_DATA_LIST, list);
			session.setAttribute(Constants.TOTAL_RESULTS, querySessionData
					.getTotalNumberOfRecords());
			AdvanceSearchForm advanceSearchForm = (AdvanceSearchForm) request
					.getAttribute("advanceSearchForm");
			if (advanceSearchForm != null)
			{
				session.setAttribute("advanceSearchForm", advanceSearchForm);
			}
		}

		int pageNum = Constants.START_PAGE;
		String recordsPerPageStr = (String) session.getAttribute(Constants.RESULTS_PER_PAGE);//Integer.parseInt(XMLPropertyHandler.getValue(Constants.NO_OF_RECORDS_PER_PAGE));
		List paginationDatalst = null, columnList = null;

		//Get the SPREADSHEET_DATA_LIST and SPREADSHEET_COLUMN_LIST from the session.
		columnList = (List) session
				.getAttribute(edu.wustl.common.util.global.Constants.SPREADSHEET_COLUMN_LIST);

		if (request.getParameter(Constants.PAGE_NUMBER) != null)
		{
			pageNum = Integer.parseInt(request.getParameter(Constants.PAGE_NUMBER));
		}
		else if (session.getAttribute(Constants.PAGE_NUMBER) != null)
		{
			pageNum = Integer.parseInt(session.getAttribute(Constants.PAGE_NUMBER).toString());
		}
		if (request.getParameter(Constants.RESULTS_PER_PAGE) != null)
		{
			recordsPerPageStr = request.getParameter(Constants.RESULTS_PER_PAGE);
		}
		else if (request.getAttribute(Constants.RESULTS_PER_PAGE) != null)
		{
			recordsPerPageStr = request.getAttribute(Constants.RESULTS_PER_PAGE).toString();
		}
		int recordsPerPage = Integer.valueOf(recordsPerPageStr);
		if (pagination != null && pagination.equalsIgnoreCase("true"))
		{
			paginationDatalst = Utility.getPaginationDataList(request, getSessionData(request),
					recordsPerPage, pageNum, querySessionData);
			request.setAttribute(Constants.PAGINATION_DATA_LIST, paginationDatalst);
		}
		request.setAttribute(edu.wustl.common.util.global.Constants.SPREADSHEET_COLUMN_LIST,
				columnList);
		request.setAttribute(Constants.PAGE_NUMBER, Integer.toString(pageNum));

		session.setAttribute(Constants.RESULTS_PER_PAGE, recordsPerPage + "");
		request.setAttribute(Constants.PAGEOF, pageOf);
		return mapping.findForward(pageOf);
	}
}