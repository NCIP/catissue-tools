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
 * Created on November 10, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package edu.wustl.clinportal.action;

//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.StringTokenizer;
//import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//import javax.swing.tree.DefaultMutableTreeNode;

//import org.apache.struts.action.ActionError;
//import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

//import edu.wustl.clinportal.bizlogic.AdvanceQueryBizlogic;
//import edu.wustl.clinportal.bizlogic.BizLogicFactory;
//import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.action.BaseAction;
//import edu.wustl.common.beans.SessionDataBean;
//import edu.wustl.common.bizlogic.IBizLogic;
//import edu.wustl.common.factory.AbstractFactoryConfig;
//import edu.wustl.common.factory.IFactory;
//import edu.wustl.simplequery.bizlogic.QueryBizLogic;
//import edu.wustl.simplequery.bizlogic.SimpleQueryBizLogic;
//import edu.wustl.common.util.global.QuerySessionData;
//import edu.wustl.common.factory.AbstractBizLogicFactory;
//import edu.wustl.common.query.AdvancedConditionsImpl;
//import edu.wustl.common.query.AdvancedConditionsNode;
//import edu.wustl.common.query.AdvancedQuery;
//import edu.wustl.simplequery.query.Condition;
//import edu.wustl.simplequery.query.DataElement;
//import edu.wustl.simplequery.query.Operator;
//import edu.wustl.simplequery.query.Query;
//import edu.wustl.simplequery.query.QueryFactory;
//import edu.wustl.simplequery.query.Table;
//import edu.wustl.common.util.XMLPropertyHandler;
//import edu.wustl.common.util.global.ApplicationProperties;
//import edu.wustl.common.util.logger.Logger;

/**
 * @author rukhsana
 *
 * Action which forms the Advance query object and 
 * creates a Temporary table with the search results
 */
public class AdvanceSearchResultsAction extends BaseAction
{

	@Override
	//remove the method
	/**
	 * 
	 * @param mapping object of ActionMapping
	 * @param form object of ActionForm
	 * @param request object of HttpServletRequest
	 * @param response object of HttpServletResponse
	 * @return 
	 * @throws Exception generic exception
	 */
	protected ActionForward executeAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Overrides the execute method of Action class.
	 * @param mapping object of ActionMapping
	 * @param form object of ActionForm
	 * @param request object of HttpServletRequest
	 * @param response object of HttpServletResponse
	 * @throws Exception generic exception
	 */
	/*public ActionForward executeAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{

		//Query Start object
		String aliasName = Constants.PARTICIPANT;
		String pageOf = edu.wustl.query.util.global.AQConstants.PAGEOF_QUERY_RESULTS;
		AdvanceQueryBizlogic advBizLogic = (AdvanceQueryBizlogic) BizLogicFactory.getInstance()
				.getBizLogic(Constants.ADVANCE_QUERY_INTERFACE_ID);
//		IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
//		AdvanceQueryBizlogic advBizLogic = (AdvanceQueryBizlogic) factory.getBizLogic(Constants.ADVANCE_QUERY_INTERFACE_ID);
		
		String target = Constants.SUCCESS;
		//Get the advance query root object from session 
		HttpSession session = request.getSession();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) session
				.getAttribute(Constants.ADVANCED_CONDITIONS_ROOT);
		//Bug 2004:Make a copy of the original query root object so that any modifications to the tree object does 
		//not reflect when query is redefined.
		DefaultMutableTreeNode originalQueryObj = new DefaultMutableTreeNode();
		copy(root, originalQueryObj);
		if (originalQueryObj.getChildCount() == 0)
		{
			ActionErrors errors = new ActionErrors();
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("advanceQuery.noConditions"));
			saveErrors(request, errors);
			pageOf = Constants.PAGEOF_ADVANCE_QUERY_INTERFACE;
			target = Constants.FAILURE;
		}
		else
		{
			//Create Advance Query object
			Query query = QueryFactory.getInstance().newQuery(Query.ADVANCED_QUERY, aliasName);

			//Set the root object as the where conditions
			((AdvancedConditionsImpl) ((AdvancedQuery) query).getWhereConditions())
					.setWhereCondition(originalQueryObj);
			//Set activity Status conditions to filter data which are disabled 
			Vector tablesVector = new Vector();
			tablesVector.add(Query.PARTICIPANT);
			tablesVector.add(Query.SPECIMEN_PROTOCOL);
			tablesVector.add(Query.COLLECTION_PROTOCOL_REGISTRATION);
			tablesVector.add(Query.SPECIMEN_COLLECTION_GROUP);
			tablesVector.add(Query.SPECIMEN);
			String actStatusConds = advBizLogic.formActivityStatusConditions(tablesVector, query
					.getTableSufix());
			query.setActivityStatusConditions(actStatusConds);

			//Set the table set for join Condition 
			Set tableSet = new HashSet();
			advBizLogic.setTables(originalQueryObj, tableSet);
			//setTablesDownTheHierarchy(tableSet);
			query.setTableSet(tableSet);

			List columnNames = new ArrayList();
			SimpleQueryBizLogic bizLogic = new SimpleQueryBizLogic();

			//Set attribute type in the DataElement	
			setAttributeType(originalQueryObj);

			//Set Identifier of Participant,Collection Protocol, Specimen Collection Group and Specimen if not existing in the resultView
			tablesVector.remove(Query.SPECIMEN_PROTOCOL);
			tablesVector.add(Query.COLLECTION_PROTOCOL);
			query.getIdentifierColumnIds(tablesVector);

			//Set tables for Configuration.
			if (query.getTableNamesSet().contains(Query.BIO_HAZARD))
			{
				tablesVector.add(Query.BIO_HAZARD);
			}
			Object[] selectedTables = tablesVector.toArray();
			session.setAttribute(edu.wustl.common.util.global.Constants.TABLE_ALIAS_NAME, selectedTables);

			Logger.out.debug("tableSet from query before setting resultview :"
					+ query.getTableNamesSet());
			//Set the result view for Advance Search
			Vector selDataElements = bizLogic.getSelectDataElements(null, new ArrayList(
					tablesVector), columnNames, false);

			query.setResultView(selDataElements);

			Map quyResObjDataMap = new HashMap();

			SimpleQueryBizLogic sipQueryBLogic = new SimpleQueryBizLogic();

			sipQueryBLogic.createQueryResultObjectData(query.getTableNamesSet(), quyResObjDataMap,
					query);
			sipQueryBLogic.addObjectIdentifierColumnsToQuery(quyResObjDataMap, query);

			sipQueryBLogic.setDependentIdentifiedColumnIds(quyResObjDataMap, query);

			Map columnIdsMap = query.getColumnIdsMap();
			session.setAttribute(Constants.COLUMN_ID_MAP, columnIdsMap);
			Logger.out.debug("map of column ids:" + columnIdsMap + ":" + columnIdsMap.size());

			SessionDataBean sessionData = getSessionData(request);
			//Temporary table name with userID attached
			String tempTableName = edu.wustl.simplequery.global.Constants.QUERY_RESULTS_TABLE + "_" + sessionData.getUserId();

//			QueryBizLogic queryBizLogic = (QueryBizLogic) AbstractBizLogicFactory.getBizLogic(
//					ApplicationProperties.getValue("app.bizLogicFactory"), "getBizLogic",
//					edu.wustl.common.util.global.Constants.QUERY_INTERFACE_ID);
			QueryBizLogic queryBizLogic = (QueryBizLogic) BizLogicFactory
					.getInstance().getBizLogic(edu.wustl.common.util.global.Constants.QUERY_INTERFACE_ID);
			
			//Create temporary table with the data from the Advance Query Search 
			String sql = query.getString();
			queryBizLogic.insertQuery(sql, getSessionData(request));

			Logger.out.debug("no. of tables in tableSet after table created"
					+ query.getTableNamesSet().size() + ":" + query.getTableNamesSet());
			Logger.out.debug("Advance Query Sql" + sql);
			*//**
			 * 
			 * Description: Query performance issue. Instead of saving complete query results in session, resultd will be fetched for each result page navigation.
			 * object of class QuerySessionData will be saved in session, which will contain the required information for query execution while navigating through query result pages.
			 *//*
			boolean hasCondOnIdFld = query.hasConditionOnIdentifiedField();
			int noOfRecords = advBizLogic.createTempTable(sql, tempTableName, sessionData,
					quyResObjDataMap, hasCondOnIdFld);

			if (noOfRecords != 0)
			{
				//Set the columnDisplayNames in session
				session.setAttribute(Constants.COLUMN_DISPLAY_NAMES, columnNames);

				//Remove configured columns from session for previous query in same session
				session.setAttribute(edu.wustl.simplequery.global.Constants.CONFIGURED_SELECT_COLUMN_LIST, null);
				session.setAttribute(Constants.CONFIGURED_COLUMN_DISPLAY_NAMES, null);
				session.setAttribute(Constants.CONFIGURED_COLUMN_NAMES, null);
				session.removeAttribute(Constants.SPECIMENT_VIEW_ATTRIBUTE);
				//Remove the spreadsheet column display names from session
				session.setAttribute(edu.wustl.common.util.global.Constants.SPREADSHEET_COLUMN_LIST, null);

				//Remove selected node from session
				session.setAttribute(Constants.SELECTED_NODE, null);

				//Remove select columnList from Session
				session.setAttribute(Constants.SELECT_COLUMN_LIST, null);

				*//**
				 *
				 * Description: Query performance issue. Instead of saving complete query results in session, resultd will be fetched for each result page navigation.
				 * object of class QuerySessionData will be saved in session, which will contain the required information for query execution while navigating through query result pages.
				 *//*
				int recordsPerPage;
				String recPerPageSessVal = (String) session
						.getAttribute(Constants.RESULTS_PER_PAGE);
				if (recPerPageSessVal == null)
				{
					recordsPerPage = Integer.parseInt(XMLPropertyHandler
							.getValue(edu.wustl.common.util.global.Constants.RECORDS_PER_PAGE_PROPERTY_NAME));
					session.setAttribute(Constants.RESULTS_PER_PAGE, recordsPerPage + "");
				}
				else
				{
					recordsPerPage = Integer.valueOf(recPerPageSessVal);
				}
				// saving required query data in Session so that can be used later on while navigating through result pages using pagination.
				QuerySessionData querySessionData = new QuerySessionData();
				querySessionData.setQueryResultObjectDataMap(quyResObjDataMap);
				querySessionData.setSecureExecute(false);
				querySessionData.setHasConditionOnIdentifiedField(hasCondOnIdFld);
				querySessionData.setRecordsPerPage(recordsPerPage);

				session.setAttribute(edu.wustl.common.util.global.Constants.QUERY_SESSION_DATA, querySessionData);
				*//**
				 * 
				 * Bug ID: 4359
				 * Description: Setting hyperlinkColumnMap to null so that no hyperlinks appear in spreadsheet of advance query interface 
				 *//*
				session.setAttribute(edu.wustl.query.util.global.AQConstants.HYPERLINK_COLUMN_MAP, null);
			}
			else
			{
				ActionErrors errors = new ActionErrors();
				errors.add(ActionErrors.GLOBAL_ERROR,
						new ActionError("advanceQuery.noRecordsFound"));
				saveErrors(request, errors);
				pageOf = Constants.PAGEOF_ADVANCE_QUERY_INTERFACE;
				target = edu.wustl.simplequery.global.Constants.SIMPLE_QUERY_NO_RESULTS;
			}
		}

		request.setAttribute(Constants.PAGEOF, pageOf);
		return mapping.findForward(target);
	}

	*//**
	 * This method Parse the root and set the attribute type in the DataElement
	 * @param tree object of DefaultMutableTreeNode
	 * @throws Exception generic exception
	 *//*
	private void setAttributeType(DefaultMutableTreeNode tree) throws Exception
	{
		DefaultMutableTreeNode child;
		int childCount = tree.getChildCount();
		Logger.out.debug("childCount" + childCount);
		for (int i = 0; i < childCount; i++)
		{
			child = (DefaultMutableTreeNode) tree.getChildAt(i);
			AdvancedConditionsNode advNode = (AdvancedConditionsNode) child.getUserObject();
			Vector conditions = advNode.getObjectConditions();
			Iterator conditionsItr = conditions.iterator();
			while (conditionsItr.hasNext())
			{
				Condition condition = (Condition) conditionsItr.next();
				String columnName = condition.getDataElement().getField();
				String aliasName = condition.getDataElement().getTableAliasName();
				QueryBizLogic bizLogic = new QueryBizLogic();//(QueryBizLogic)BizLogicFactory.getBizLogic(Constants.SIMPLE_QUERY_INTERFACE_ID);
				StringTokenizer tableTokens = new StringTokenizer(aliasName, ".");
				if (tableTokens.countTokens() == 2)
				{
					aliasName = tableTokens.nextToken();
					tableTokens.nextToken();
				}

				String attributeType = bizLogic.getAttributeType(columnName, aliasName);
				condition.getDataElement().setFieldType(attributeType);
			}
			setAttributeType(child);
		}
	}

	*//**
	 * Method to deep copy the DefaultMutableTreeNode object
	 * @param oldCopy old object of DefaultMutableTreeNode
	 * @param newCopy updated object of DefaultMutableTreeNode
	 *//*
	private void copy(DefaultMutableTreeNode oldCopy, DefaultMutableTreeNode newCopy)
	{
		DefaultMutableTreeNode child = new DefaultMutableTreeNode();
		if (oldCopy != null)
		{
			int childCount = oldCopy.getChildCount();
			for (int i = 0; i < childCount; i++)
			{
				child = (DefaultMutableTreeNode) oldCopy.getChildAt(i);
				AdvancedConditionsNode advNode = (AdvancedConditionsNode) child.getUserObject();
				AdvancedConditionsNode newAdvNode = new AdvancedConditionsNode(new String(advNode
						.getObjectName()));
				Vector conditions = advNode.getObjectConditions();
				Operator opWithChild = advNode.getOperationWithChildCondition();
				Vector newConditions = new Vector();
				Iterator itr1 = conditions.iterator();
				while (itr1.hasNext())
				{
					Condition con = (Condition) itr1.next();
					DataElement data = con.getDataElement();
					DataElement newData = new DataElement(new Table(new String(data
							.getTableAliasName())), new String(data.getField()));
					Condition newCon = new Condition(newData, new Operator(con.getOperator()),
							new String(con.getValue()));
					newConditions.add(newCon);
				}
				newAdvNode.setObjectConditions(newConditions);
				newAdvNode.setOperationWithChildCondition(new Operator(opWithChild));
				DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(newAdvNode);
				newCopy.add(newChild);
				copy(child, newChild);
			}
		}
	}*/
}