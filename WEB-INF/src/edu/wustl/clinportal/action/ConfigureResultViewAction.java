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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.action.BaseAction;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.QueryWhereClause;
import edu.wustl.simplequery.bizlogic.QueryBizLogic;
import edu.wustl.simplequery.query.QueryTableData;

/**
 * This is the action class for configuring columns in result view 
 * @author falguni sachde
 *  
 */
public class ConfigureResultViewAction extends BaseAction
{

	/**
	 * @param mapping object of ActionMapping
	 * @param form object of ActionForm
	 * @param request object of HttpServletRequest
	 * @param response object of HttpServletResponse
	 * @throws Exception generic exception
	 * */
	protected ActionForward executeAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
		/*IBizLogic bizlogic = BizLogicFactory.getInstance().getBizLogic(
				Constants.CONFIGURE_RESULT_VIEW_ID);*/
		//IBizLogic bizlogic = factory.getBizLogic(Constants.CONFIGURE_RESULT_VIEW_ID);
		DefaultBizLogic bizlogic = new DefaultBizLogic();
		String pageOf = (String) request.getAttribute(Constants.PAGEOF);
		if (pageOf == null)
		{
			pageOf = (String) request.getParameter(Constants.PAGEOF);
		}

		HttpSession session = request.getSession();

		Object[] tables = (Object[]) session
				.getAttribute(edu.wustl.common.util.global.Constants.TABLE_ALIAS_NAME);
		String sourceObjectName = QueryTableData.class.getName();
		String[] displayNameField = {"displayName"};
		String valueField = "aliasName";

		String[] whereColumnNames = {"aliasName"};
		String[] whereCondition = {"in"};
		Object[] whereColumnValues = {tables};
		//List of objects containing TableNames and aliasName
		// commented the code as the DefaultBizlogic implementation of getList method 
		// passes null values for 'where' clause
		/*List tableList = bizlogic.getList(sourceObjectName, displayNameField, valueField,
				whereColumnNames, whereCondition, whereColumnValues, null, null, false);*/

		QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
		queryWhereClause.getWhereCondition(whereColumnNames, whereCondition, whereColumnValues,
				null);

		String[] selectColumnName = new String[displayNameField.length + 1];
		System.arraycopy(displayNameField, 0, selectColumnName, 0, displayNameField.length);
		selectColumnName[displayNameField.length] = valueField;

		List tableList = bizlogic.getList(sourceObjectName, selectColumnName, null,
				queryWhereClause);

		Map tabColDataMap = new HashMap();

		Iterator itr = tableList.iterator();
		while (itr.hasNext())
		{
			NameValueBean tableData = (NameValueBean) itr.next();
			if (!tableData.getName().equals(Constants.SELECT_OPTION))
			{
				/*QueryBizLogic bizLogic = (QueryBizLogic) BizLogicFactory.getInstance().getBizLogic(
						edu.wustl.simplequery.global.Constants.SIMPLE_QUERY_INTERFACE_ID);*/
				QueryBizLogic bizLogic = (QueryBizLogic) factory
						.getBizLogic(edu.wustl.simplequery.global.Constants.SIMPLE_QUERY_INTERFACE_ID);
				List columnList = bizLogic.setColumnNames(tableData.getValue());
				tabColDataMap.put(tableData, columnList);

			}
			Logger.out.debug("Table Name" + tableData.getValue());

		}

		Logger.out.debug("Table Map" + tabColDataMap);
		request.setAttribute(Constants.TABLE_COLUMN_DATA_MAP, tabColDataMap);
		request.setAttribute(Constants.PAGEOF, pageOf);
		Logger.out.debug("pageOf in configure result view:" + pageOf);

		return mapping.findForward(pageOf);

	}

}