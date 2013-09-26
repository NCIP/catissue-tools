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
 * <p>Title: DataViewAction Class>
 * <p>Description:	DataViewAction is used to show the query results data 
 * in spreadsheet or individual view.</p>
 * 
 */

package edu.wustl.clinportal.action;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

//import edu.wustl.clinportal.bizlogic.AdvanceQueryBizlogic;
//import edu.wustl.clinportal.bizlogic.BizLogicFactory;
import edu.wustl.clinportal.actionForm.AdvanceSearchForm;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.action.BaseAction;
import edu.wustl.common.beans.NameValueBean;
//import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;
import edu.wustl.simplequery.bizlogic.QueryBizLogic;
//import edu.wustl.common.util.global.QuerySessionData;
//import edu.wustl.common.util.PagenatedResultData;
//import edu.wustl.common.query.AdvancedQuery;
import edu.wustl.simplequery.query.Query;
//import edu.wustl.security.manager.ISecurityManager;
//import edu.wustl.security.manager.SecurityManager;
import edu.wustl.security.manager.SecurityManagerFactory;
import edu.wustl.security.global.Permissions;
import edu.wustl.common.util.Utility;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.common.util.logger.Logger;

/**
 * DataViewAction is used to show the query results data in spreadsheet or
 * Individual view.
 * 
 * @author rukhsana sameer
 */
public class DataViewAction extends BaseAction
{

	/**
	 * @param mapping object of ActionMapping
	 * @param form object of ActionForm
	 * @param request object of HttpServletRequest
	 * @param response object of HttpServletResponse
	 * @throws Exception generic exception
	 * @return
	 */
	public ActionForward executeAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		AdvanceSearchForm advForm = (AdvanceSearchForm) form;
		String[] selectedColumns = advForm.getSelectedColumnNames();
		Logger.out.debug("selected columns:" + selectedColumns);

		HttpSession session = request.getSession();
		String target = Constants.SUCCESS;
		
		String nodeName = request.getParameter("nodeName");
		if (nodeName == null)
		{
			nodeName = (String) session.getAttribute(Constants.SELECTED_NODE);
		}

		StringTokenizer str = new StringTokenizer(nodeName, ":");
		String name = str.nextToken().trim();

		String identifier = "";
		String parentName = "";
		String parentId = "";

		// Get the listData, column display names and select column names if it
		// is configured and set in session
		List filtColDisNames = (List) session
				.getAttribute(Constants.CONFIGURED_COLUMN_DISPLAY_NAMES);
		Logger.out.debug("ColumnDisplayNames from configuration" + filtColDisNames);
		String[] columnList = (String[]) session
				.getAttribute(edu.wustl.simplequery.global.Constants.CONFIGURED_SELECT_COLUMN_LIST);
		// boolean configured =true;
		if (selectedColumns == null || columnList != null)
		{
			selectedColumns = (String[]) session.getAttribute(Constants.CONFIGURED_COLUMN_NAMES);
			advForm.setSelectedColumnNames(selectedColumns);
			Logger.out.debug("selected columns:" + selectedColumns);
		}
		// Retrieve the columnIdsMap from session
		Map columnIdsMap = (Map) session.getAttribute(Constants.COLUMN_ID_MAP);

		if (!name.equals(edu.wustl.common.util.global.Constants.ROOT))
		{
			identifier = str.nextToken().trim();
		}
		/*
		 * In case of collection protocol selected, the whereCondition should
		 * contain the participant conditions also as Collection Protocol and
		 * Participant have many to many relationship
		 */
		if (str.hasMoreTokens())
		{
			parentName = str.nextToken();
			parentId = str.nextToken();
		}

		// get the type of view to show (spreadsheet/individual)
		String viewType = getViewType(request);
		
		if (viewType.equals(edu.wustl.simplequery.global.Constants.SPREADSHEET_VIEW))
		{
			if (columnList == null)
			{
				filtColDisNames = new ArrayList();
				columnList = getColumnNamesForFilter(name, filtColDisNames, columnIdsMap,
						selectedColumns, advForm);
				Logger.out.debug("name selected:" + name);
			}
			if (!name.equals(edu.wustl.common.util.global.Constants.ROOT))
			{
				// Commenting key variable to resolve "variable never read locally" 				
				//				String key = name + "." + Constants.IDENTIFIER;
				int columnId = ((Integer) columnIdsMap.get(name + "." + edu.wustl.common.util.global.Constants.IDENTIFIER))
						.intValue() - 1;
				name = Constants.COLUMN + columnId;
				if (!"".equals(parentName))
				{
					//					key = parentName + "." + Constants.IDENTIFIER;
					columnId = ((Integer) columnIdsMap.get(parentName + "." + edu.wustl.common.util.global.Constants.IDENTIFIER))
							.intValue() - 1;
					parentName = Constants.COLUMN + columnId;
				}
			}

			String[] whereColumnName = new String[1];

			String[] whereColumnValue = new String[1];

			String[] whereColCond = new String[1];

			if (!"".equals(parentName))
			{
				whereColumnName = new String[2];
				whereColumnValue = new String[2];
				whereColCond = new String[2];

				whereColumnName[1] = parentName;
				whereColumnValue[1] = parentId;
				whereColCond[1] = "=";
			}
			whereColumnName[0] = name;
			whereColumnValue[0] = identifier;
			whereColCond[0] = "=";

			/**
			 *
			 * Description: Query performance issue. Instead of saving complete query results in session, resulted will be fetched for each result page navigation.
			 * object of class QuerySessionData will be saved session, which will contain the required information for query execution while navigating through query result pages.
			 * Order by clause is added to the SQL to ensure the order of the result list for proper functioning of pagination results. 
			 */
			// creating order by attribute column names.
			/*String[] orderByAttributes = new String[AdvancedQuery.QUERY_ORDERBY_SEQUENCE.length];
			for (int index = 0; index < orderByAttributes.length; index++)
			{
				String idColumnName = AdvancedQuery.QUERY_ORDERBY_SEQUENCE[index] + "."
						+ edu.wustl.common.util.global.Constants.IDENTIFIER;
				orderByAttributes[index] = Constants.COLUMN
						+ ((Integer) columnIdsMap.get(idColumnName) - 1);
			}
			AdvanceQueryBizlogic bizLogic = new AdvanceQueryBizlogic();
			SessionDataBean sessionDataBean = getSessionData(request);
			String sql = bizLogic.createSQL(whereColumnName, whereColumnValue, whereColCond,
					columnList, orderByAttributes, sessionDataBean);
			String recordsPerPageStr = (String) session.getAttribute(Constants.RESULTS_PER_PAGE);
			int recordsPerPage = Integer.valueOf(recordsPerPageStr);
			QuerySessionData querySessionData = (QuerySessionData) session
					.getAttribute(edu.wustl.common.util.global.Constants.QUERY_SESSION_DATA);
			querySessionData.setSql(sql);
			querySessionData.setRecordsPerPage(recordsPerPage);
			PagenatedResultData pageResData = new QueryBizLogic().execute(sessionDataBean,
					querySessionData, 0);
			querySessionData.setTotalNumberOfRecords(pageResData.getTotalRecords());

			List list = pageResData.getResult();

			// If the result contains no data, show error message.
			if (list.isEmpty())
			{
				ActionErrors errors = new ActionErrors();
				errors.add(ActionErrors.GLOBAL_ERROR,
						new ActionError("advanceQuery.noRecordsFound"));
				saveErrors(request, errors);
				request.setAttribute(Constants.PAGEOF, edu.wustl.query.util.global.AQConstants.PAGEOF_QUERY_RESULTS);
				// target = Constants.FAILURE;
			}
			else
			{

				updateInnerList(list);
				
				request.setAttribute(edu.wustl.common.util.global.Constants.SPREADSHEET_COLUMN_LIST, filtColDisNames);
				request.setAttribute(Constants.SPREADSHEET_DATA_LIST, list);
				request.setAttribute(Constants.PAGEOF, edu.wustl.query.util.global.AQConstants.PAGEOF_QUERY_RESULTS);
				session.setAttribute(Constants.SELECT_COLUMN_LIST, columnList);
				session.setAttribute(Constants.SELECTED_NODE, nodeName);
			}*/
		}
		else
		{
			//String url = null;
			Logger.out.debug("selected node name in object view:" + name + "object");

			// Check whether user has use permission to update this object  or not
			edu.wustl.clinportal.security.SecurityManager securityManager = 
				(edu.wustl.clinportal.security.SecurityManager) SecurityManagerFactory
					.getSecurityManager();

			/*if (!SecurityManager.getInstance(this.getClass()).isAuthorized(
					getUserLoginName(request), Constants.PACKAGE_DOMAIN + "." + name,
					Permissions.UPDATE))*/
			if (securityManager.isAuthorized(getUserLoginName(request), Constants.PACKAGE_DOMAIN
					+ "." + name, Permissions.UPDATE))
			{
				ActionErrors errors = new ActionErrors();
				ActionError error = new ActionError("access.edit.object.denied",
						getUserLoginName(request), Constants.PACKAGE_DOMAIN + "." + name);
				errors.add(ActionErrors.GLOBAL_ERROR, error);
				saveErrors(request, errors);
				return mapping.findForward(Constants.FAILURE);
			}

			String url = getRequestURL(name, identifier);
			
			RequestDispatcher requestDispatcher = request.getRequestDispatcher(url);
			requestDispatcher.forward(request, response);
		}
		return mapping.findForward(target);
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	private String getViewType(HttpServletRequest request)
	{
		String viewType = request.getParameter(edu.wustl.query.util.global.AQConstants.VIEW_TYPE);
		if (viewType == null)
		{
			viewType = edu.wustl.simplequery.global.Constants.SPREADSHEET_VIEW;
		}
		return viewType;
	}
	
	/**
	 * 
	 * @param list
	 */
	/*private void updateInnerList(List list)
	{
		for (int i = 0; i < list.size(); i++)
		{
			List innerList = (ArrayList) list.get(i);
			for (int j = 0; j < innerList.size(); j++)
			{
				String data = (String) innerList.get(j);
				if (isMarkupData(data))
				{
					innerList.remove(j);
					innerList.add(j, "##");
				}
			}
		}
	}*/
	
	/**
	 * 
	 * @param name
	 * @param identifier
	 * @return
	 */
	private String getRequestURL(String name, String identifier)
	{
		String url = null;
		
		if (name.equals(Constants.PARTICIPANT))
		{
			url = new String(Constants.QUERY_PARTICIPANT_SEARCH_ACTION + identifier + "&"
					+ Constants.PAGEOF + "=" + Constants.PAGEOF_PARTICIPANT_QUERY_EDIT);
		}
		else if (name.equals(Constants.COLLECTION_PROTOCOL))
		{
			url = new String(Constants.QUERY_COLLECTION_PROTOCOL_SEARCH_ACTION + identifier
					+ "&" + Constants.PAGEOF + "="
					+ Constants.PAGEOF_COLLECTION_PROTOCOL_QUERY_EDIT);

		}
		else if (name.equals(Constants.SPECIMEN_COLLECTION_GROUP))
		{
			url = new String(Constants.QUERY_SPECIMEN_COLLECTION_GROUP_SEARCH_ACTION
					+ identifier + "&" + Constants.PAGEOF + "="
					+ Constants.PAGEOF_SPECIMEN_COLLECTION_GROUP_QUERY_EDIT);

		}
		else if (name.equals(Constants.SPECIMEN))
		{
			url = new String(Constants.QUERY_SPECIMEN_SEARCH_ACTION + identifier + "&"
					+ Constants.PAGEOF + "=" + Constants.PAGEOF_SPECIMEN_QUERY_EDIT);

		}
		
		return url;
	}
	
	/**
	 * 
	 * This function checks whether the data is markUp data. For Number -1 is
	 * used as MarkUp data For Date 1-1-9999 is used as markUp data Also refer
	 * bug 3576
	 * 
	 * @param data - String
	 * @return - boolean
	 */
	private boolean isMarkupData(String data)
	{
		boolean returnVal = false;

		if ("-1".equals(data))
		{
			returnVal = true;
		}
		else
		{
			/**
			 * Checking is done on date objects and not directly on Strings.
			 */
			try
			{
				java.util.Date date = Utility.parseDate("1-1-9999", "mm-dd-yyyy");
				java.util.Date date1 = Utility.parseDate(data, "mm-dd-yyyy");
				if (date != null && date1 != null && date.toString().equals(date1.toString()))
				{
					returnVal = true;
				}
			}
			catch (ParseException e)
			{
				Logger.out.error("Error parsing date");
			}
		}

		return returnVal;
	}

	// returns the filtered select column list to be shown when clicked on a
	// node of the results tree
	/**
	 * @param aliasName
	 * @param filtColDispNames
	 * @param columnIdsMap
	 * @param selectedColumns
	 * @param advForm
	 * @return
	 * @throws DAOException
	 * @throws ClassNotFoundException
	 * @throws BizLogicException 
	 */
	private String[] getColumnNamesForFilter(String aliasName, List filtColDispNames,
			Map columnIdsMap, String[] selectedColumns, AdvanceSearchForm advForm)
			throws DAOException, ClassNotFoundException, BizLogicException
	{
		List colDispNames = new ArrayList();
		IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
		/*QueryBizLogic bizLogic = (QueryBizLogic) BizLogicFactory.getInstance().getBizLogic(
				edu.wustl.simplequery.global.Constants.SIMPLE_QUERY_INTERFACE_ID);*/
		QueryBizLogic bizLogic = (QueryBizLogic) factory
				.getBizLogic(edu.wustl.simplequery.global.Constants.SIMPLE_QUERY_INTERFACE_ID);
		List columns = new ArrayList();
		// Filter the data according to the node clicked. Show only the data
		// lower in the hierarchy

		// Bug#2113: Default view consists of all attributes from Participant to
		// Specimen Thus removing all columns except the ones corresponding to the type
		// of object selected

		/**
		 * 
		 * Description: Query performance issue. Instead of saving complete query results in session, resulted will be fetched for each result page navigation.
		 * object of class QuerySessionData will be saved session, which will contain the required information for query execution while navigating through query result pages.
		 * 
		 *  Moved bizLogic.getColumnNames calls inside each if block to avoid unnecessary call to each method.
		 */
		if (aliasName.equals(edu.wustl.common.util.global.Constants.ROOT))
		{
			List parpantCol = bizLogic.getColumnNames(Query.PARTICIPANT, true);
			columns.addAll(parpantCol);
		}
		else if (aliasName.equals(Query.PARTICIPANT))
		{
			List cpcolColumns = bizLogic.getColumnNames(Query.COLLECTION_PROTOCOL, true);

			columns.addAll(cpcolColumns);

			List colProtRegCols = bizLogic.getColumnNames(Query.COLLECTION_PROTOCOL_REGISTRATION,
					true);
			columns.addAll(colProtRegCols);
		}
		else if (aliasName.equals(Query.COLLECTION_PROTOCOL))
		{
			List scgColumns = bizLogic.getColumnNames(Query.SPECIMEN_COLLECTION_GROUP, true);
			columns.addAll(scgColumns);
		}
		else if (aliasName.equals(Query.SPECIMEN_COLLECTION_GROUP)
				|| aliasName.equals(Query.SPECIMEN))
		{
			List specimenColumns = bizLogic.getColumnNames(Query.SPECIMEN, true);
			columns.addAll(specimenColumns);
		}

		String selectColumnList[] = new String[columns.size()];
		selectedColumns = new String[columns.size()];
		Iterator columnsItr = columns.iterator();
		int cnt = 0;
		while (columnsItr.hasNext())
		{
			NameValueBean columnsNameValues = (NameValueBean) columnsItr.next();
			StringTokenizer columnsTokens = new StringTokenizer(columnsNameValues.getValue(), ".");
			Logger.out.debug("value in namevaluebean:" + columnsNameValues.getValue());
			int columnId = ((Integer) columnIdsMap.get(columnsTokens.nextToken() + "."
					+ columnsTokens.nextToken())).intValue() - 1;
			selectedColumns[cnt] = columnsNameValues.getValue();
			selectColumnList[cnt++] = (Constants.COLUMN + columnId);
			colDispNames.add(columnsTokens.nextToken());
		}
		Logger.out.debug("size of the configure default columns after setting inside the method:"
				+ selectedColumns.length);
		advForm.setSelectedColumnNames(selectedColumns);
		filtColDispNames.addAll(colDispNames);
		return selectColumnList;
	}

}