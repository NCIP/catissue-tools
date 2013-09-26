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
 * <p>Title: SpreadsheetExportAction Class>
 * <p>Description:	This class exports the data of a spreadsheet to a file.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * 
 */

package edu.wustl.clinportal.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.clinportal.actionForm.AdvanceSearchForm;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.clinportal.util.global.Utility;
import edu.wustl.clinportal.util.global.Variables;
import edu.wustl.common.action.BaseAction;
import edu.wustl.common.util.ExportReport;
import edu.wustl.common.util.SendFile;
import edu.wustl.common.util.global.QuerySessionData;
import edu.wustl.common.util.logger.Logger;

/**
 * @author falguni_sachde
 *
 */
public class SpreadsheetExportAction extends BaseAction
{

	private transient Logger logger = Logger.getCommonLogger(SpreadsheetExportAction.class);

	/**
	 * This class exports the data of a spreadsheet to a file.
	 */
	public ActionForward executeAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		AdvanceSearchForm searchForm = (AdvanceSearchForm) form;
		HttpSession session = request.getSession();
		String fileName = Variables.applicationHome + System.getProperty("file.separator")
				+ session.getId() + ".csv";
		String isChkAlAcrAlChked = (String) request
				.getParameter(Constants.CHECK_ALL_ACROSS_ALL_PAGES);

		//Extracting map from form bean which gives the serial numbers of selected rows
		Map map = searchForm.getValues();
		Object[] obj = map.keySet().toArray();

		//Getting column data & grid data from session
		List columnList = (List) session
				.getAttribute(edu.wustl.common.util.global.Constants.SPREADSHEET_COLUMN_LIST);

		/**
		 * 
		 * Description: Query performance issue. Instead of saving complete query results in session, resulted will be fetched for each result page navigation.
		 * object of class QuerySessionData will be saved in session, which will contain the required information for query execution while navigating through query result pages.
		 * 
		 * Here, as results are not stored in session, the sql is fired again to form the shopping cart list.  
		 */
		String pageNo = (String) request.getParameter(Constants.PAGE_NUMBER);
		String recordsPerPageStr = (String) session.getAttribute(Constants.RESULTS_PER_PAGE);//Integer.parseInt(XMLPropertyHandler.getValue(Constants.NO_OF_RECORDS_PER_PAGE));
		if (pageNo != null)
		{
			request.setAttribute(Constants.PAGE_NUMBER, pageNo);
		}
		int recordsPerPage = Integer.valueOf(recordsPerPageStr);
		int pageNum = Integer.valueOf(pageNo);
		if (isChkAlAcrAlChked != null && isChkAlAcrAlChked.equalsIgnoreCase("true"))
		{
			Integer totalRecords = (Integer) session.getAttribute(Constants.TOTAL_RESULTS);
			recordsPerPage = totalRecords;
			pageNum = 1;
		}
		QuerySessionData querySessionData = (QuerySessionData) session
				.getAttribute(edu.wustl.common.util.global.Constants.QUERY_SESSION_DATA);
		List dataList = Utility.getPaginationDataList(request, getSessionData(request),
				recordsPerPage, pageNum, querySessionData);
		// Extra ID columns displayed.  start

		logger
				.debug("---------------------------------------------------------------------------------");
		logger.debug("06-apr-06 : cl size :-" + columnList.size());
		logger.debug(columnList);
		logger.debug("--");
		logger.debug("06-apr-06 : dl size :-" + dataList.size());
		logger.debug(dataList);

		List tmpColumnList = new ArrayList();
		int idColCount = 0;
		// count no. of ID columns
		for (int cnt = 0; cnt < columnList.size(); cnt++)
		{
			String columnName = (String) columnList.get(cnt);
			Logger.out.debug(columnName + " : " + columnName.length());
			if (columnName.trim().equalsIgnoreCase("ID"))
			{
				idColCount++;
			}
		}
		// remove ID columns
		for (int cnt = 0; cnt < (columnList.size() - idColCount); cnt++)
		{
			tmpColumnList.add(columnList.get(cnt));
		}

		// data list filtration for ID data.
		List tmpDataList = new ArrayList();
		for (int dataListCnt = 0; dataListCnt < dataList.size(); dataListCnt++)
		{
			List tmpList = (List) dataList.get(dataListCnt);
			List tmpNewList = new ArrayList();
			for (int cnt = 0; cnt < (tmpList.size() - idColCount); cnt++)
			{
				tmpNewList.add(tmpList.get(cnt));
			}
			tmpDataList.add(tmpNewList);
		}

		logger.debug("--");
		logger.debug("tmpcollist :" + tmpColumnList.size());
		logger.debug(tmpColumnList);
		logger.debug("--");
		logger.debug("tmpdatalist :" + tmpDataList.size());
		logger.debug(tmpDataList);

		logger
				.debug("---------------------------------------------------------------------------------");
		columnList = tmpColumnList;
		dataList = tmpDataList;
		//     Extra ID columns end  

		List exportList = new ArrayList();

		//Adding first row(column names) to exportData
		exportList.add(columnList);
		if (isChkAlAcrAlChked != null && isChkAlAcrAlChked.equalsIgnoreCase("true"))
		{
			for (int i = 0; i < dataList.size(); i++)
			{
				exportList.add(dataList.get(i));
			}
		}
		else
		{
			for (int i = 0; i < obj.length; i++)
			{
				int indexOf = obj[i].toString().indexOf("_") + 1;
				int index = Integer.parseInt(obj[i].toString().substring(indexOf));
				exportList.add((List) dataList.get(index));
			}
		}
		String delimiter = edu.wustl.common.util.global.Constants.DELIMETER;
		//Exporting the data to the given file & sending it to user
		ExportReport report = new ExportReport(fileName);
		report.writeData(exportList, delimiter);
		report.closeFile();

		SendFile.sendFileToClient(response, fileName, Constants.SEARCH_RESULT,
				"application/download");

		return null;
	}
}