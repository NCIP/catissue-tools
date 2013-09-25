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
 * Created on November 30, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package edu.wustl.clinportal.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.clinportal.actionForm.AdvanceSearchForm;
import edu.wustl.clinportal.util.DAOUtil;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.action.BaseAction;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.common.util.logger.Logger;

/**
 * @author falguni sachde
 *
 * This action filters the data from the Advance Search Results according to the
 * configured columns.
 */
public class ConfigureAdvanceSearchResultsAction extends BaseAction
{

	/**
	 * @param mapping object of ActionMapping
	 * @param form object of ActionForm
	 * @param request object of HttpServletRequest
	 * @param response object of HttpServletResponse
	 * @return
	 * @throws Exception generic exception
	 * */
	public ActionForward executeAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		AdvanceSearchForm advForm = (AdvanceSearchForm) form;
		String target = Constants.SUCCESS;
		//get the columns selected in configureResultView
		String[] configuredColumns = advForm.getSelectedColumnNames();
		HttpSession session = request.getSession();
		//Get the column ids for the selected columns to search in the temporary table
		Map columnIdsMap = (Map) session.getAttribute(Constants.COLUMN_ID_MAP);
		String[] selectColumns = new String[configuredColumns.length];
		List colDisplNames = new ArrayList();
		for (int i = 0; i < configuredColumns.length; i++)
		{
			//Split the columns which is in the form tableAlias.columnName.columnDisplayNames
			StringTokenizer selColTokens = new StringTokenizer(configuredColumns[i], ".");
			//get the column Id of the selected column to search in the temporary table
			int columnId = ((Integer) columnIdsMap.get(selColTokens.nextToken() + "."
					+ selColTokens.nextToken())).intValue() - 1;
			Logger.out.debug("column id of configured column" + columnId);
			colDisplNames.add(selColTokens.nextToken());
			selectColumns[i] = Constants.COLUMN + columnId;
			/*if(selectedColumnsTokens.hasMoreTokens())
				selectedColumnsTokens.nextToken();*/

		}
		SessionDataBean sessionData = getSessionData(request);
		//temporary table name
		String tableName = edu.wustl.simplequery.global.Constants.QUERY_RESULTS_TABLE + "_" + sessionData.getUserId();
		JDBCDAO jdbcDAO = DAOUtil.getJDBCDAO(sessionData);
		
		String[] selectColumnNames = selectColumns;
		Logger.out.debug(" Selected Columns:" + selectColumns);
		
		try
		{
			//Bug#2003: For having unique records in result view
			List list = jdbcDAO.retrieve(tableName, selectColumnNames, true);
			//end Bug#2003

			session.setAttribute(edu.wustl.simplequery.global.Constants.CONFIGURED_SELECT_COLUMN_LIST, selectColumnNames);
			session.setAttribute(Constants.CONFIGURED_COLUMN_DISPLAY_NAMES, colDisplNames);
			session.setAttribute(Constants.CONFIGURED_COLUMN_NAMES, configuredColumns);
			session.removeAttribute(Constants.SPECIMENT_VIEW_ATTRIBUTE);
			request.setAttribute(Constants.SPREADSHEET_DATA_LIST, list);

			request.setAttribute(edu.wustl.common.util.global.Constants.SPREADSHEET_COLUMN_LIST, colDisplNames);
			request.setAttribute(Constants.PAGEOF, edu.wustl.query.util.global.AQConstants.PAGEOF_QUERY_RESULTS);
		}
		catch (DAOException exp)
		{
			Logger.out.error(exp.getMessage(), exp);
			ActionErrors errors = new ActionErrors();
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
					"advanceQuery.userAlreadyLoggedIn"));
			saveErrors(request, errors);
			target = Constants.FAILURE;
		}
		finally
		{
			DAOUtil.closeJDBCDAO(jdbcDAO);
		}
		return mapping.findForward(target);
	}

}
