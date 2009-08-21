
package edu.wustl.clinportal.action;

/**
 * Common action class used for initializing field's value of JSP 
 * @author Falguni Sachde
 *
 */
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.StringTokenizer;
//import java.util.Vector;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpSession;
//import javax.swing.tree.DefaultMutableTreeNode;
//
//import edu.wustl.clinportal.actionForm.AdvanceSearchForm;
import edu.wustl.common.action.BaseAction;
//import edu.wustl.simplequery.bizlogic.QueryBizLogic;
//import edu.wustl.simplequery.query.Condition;
//import edu.wustl.simplequery.query.DataElement;
//import edu.wustl.simplequery.query.Operator;
//import edu.wustl.simplequery.query.Query;
//import edu.wustl.common.util.global.Constants;
//import edu.wustl.common.util.logger.Logger;
//import edu.wustl.common.vo.SearchFieldData;

public abstract class AdvanceSearchUIAction extends BaseAction
{
	//private static org.apache.log4j.Logger logger = Logger.getLogger(AdvanceSearchUIAction.class);
	
	/**
	 * Used for setting value of map of form in edit case
	 * @param aForm Common form form for all Search Pages
	 * @param str represents id as string
	 * @param request HttpServletRequest
	 * @throws Exception generic exception
	 */
	/*protected void setMapValue(AdvanceSearchForm aForm, String str, HttpServletRequest request)
			throws Exception
	{
		//Represents checked checbox's id
		Integer nodeId = Integer.decode(str);
		HttpSession session = request.getSession();

		//ConditionNode represents map of condition given by user in jsp page
		Map conditionNode = (HashMap) session.getAttribute(edu.wustl.clinportal.util.global.Constants.ADVANCED_CONDITION_NODES_MAP);

		//Represent conditions of according to id
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) conditionNode.get(nodeId);

		AdvancedConditionsNode advConditionNode = (AdvancedConditionsNode) node.getUserObject();

		//Represent vector of conditions of particular row
		Vector vectorOfCondtions = advConditionNode.getObjectConditions();

		Condition con = null;
		DataElement dataElement = null;
		Operator operation = null;

		int eventCounter = 1;

		//Field name of page
		String column = "";

		//Temporary variable for checking kay of map
		String temp = "";

		//Temporary variable for putting value Between & Not between in page
		String tempOperator = "";

		//Provides condition of putting value of valueKey in the map
		boolean boolValuekey = false;

		//Temporary map for all the values of page
		Map map = new HashMap();
		Iterator iterator = vectorOfCondtions.iterator();

		while (iterator.hasNext())
		{
			con = (Condition) iterator.next();
			dataElement = con.getDataElement();

			//Used for creating key of field
			String tableName = dataElement.getTableAliasName();
			StringTokenizer tableTokens = new StringTokenizer(tableName, ".");
			String superTable = "";
			if (tableTokens.countTokens() == 2)
			{
				tableName = tableTokens.nextToken();
				superTable = tableTokens.nextToken();
			}

			operation = con.getOperator();
			column = dataElement.getField();
			String valuekey = "";

			//Condition for checking date values as operator keys are same
			if (temp.equals(tableName + ":" + column))
			{
				//Key for value field 
				valuekey = tableName + ":" + column + ":HLIMIT";
				boolValuekey = true;

			}
			else
			{
				//Key for value field 
				valuekey = tableName + ":" + column;
				boolValuekey = false;
			}

			//Key for value field ie 3rd column
			String opKey = "Operator:" + tableName + ":" + column;

			logger.debug("opKey--" + opKey);
			logger.debug("valuekey--" + valuekey);
			//Setting value of map in edit case
			map.put(valuekey, con.getValue());

			if (boolValuekey)
			{
				//				Explicitly setting operator keys value as they are same
				if (tempOperator.equals(Operator.GREATER_THAN_OR_EQUALS))
				{
					map.put(opKey, Operator.BETWEEN);
				}
				else
				{
					map.put(opKey, Operator.NOT_BETWEEN);
				}
				temp = "";
			}
			else
			{
				map.put(opKey, operation.getOperator());
				tempOperator = operation.getOperator();
				temp = valuekey;
			}
			if (tableName.indexOf(Query.PARAM) != -1)
			{
				setEventParameterMap(aForm, tableName, column, operation.getOperator(), con
						.getValue(), eventCounter, superTable);
				eventCounter++;
			}

		}
		logger.debug("map in advSearchUIAction----******-->" + map);
		//Setting map in form
		aForm.setValues(map);
	}

	*//**
	 * Used for setting value of map of form in Edit case
	 * @param searchFieldData Instance of class for setting field's value
	 * @param map Map from form containing keys of field
	 *//*

	protected void setIsDisableValue(SearchFieldData[] searchFieldData, Map map)
	{
		for (int i = 0; i < searchFieldData.length; i++)
		{
			//Represents name of field i.e value(...)
			String temp = searchFieldData[i].getValueField().getName();

			//Removing brackets from valueField's name 
			String name = temp.substring(temp.indexOf("(") + 1, temp.indexOf(")"));

			//Enabling or disabling the field according condition whether map contains value or not
			Object element = map.get(name);
			if ((element == null) || (element.equals("Any")))
			{
				searchFieldData[i].getValueField().setDisabled(true);
			}
			else
			{
				searchFieldData[i].getValueField().setDisabled(false);
			}
		}
	}

	*//**
	 * @param aForm object of AdvanceSearchForm
	 * @param tableName String containing table name 
	 * @param column String for column name
	 * @param operatorVal String operator value
	 * @param value String value
	 * @param counter Integer count value
	 * @param superTable String super table name
	 * @throws Exception generic exception
	 *//*
	protected void setEventParameterMap(AdvanceSearchForm aForm, String tableName, String column,
			String operatorVal, String value, int counter, String superTable) throws Exception
	{
		QueryBizLogic bizlogic = new QueryBizLogic();
		String eventClassName = "EventName_" + counter;
		String eventColumn = "EventColumnName_" + counter;
		String eventOperator = "EventColumnOperator_" + counter;
		String eventValue = "EventColumnValue_" + counter;
		String dataType = "";
		dataType = bizlogic.getAttributeType(column, tableName);

		logger.debug("Key for event map" + tableName + "." + column + "." + dataType);
		aForm.setEventMap(eventColumn, superTable + "." + column + "." + dataType);//bizlogic.getColumnDisplayNames(tableName,column
		aForm.setEventMap(eventClassName, tableName);
		aForm.setEventMap(eventOperator, operatorVal);
		aForm.setEventMap(eventValue, value);
		Map eventParaMap = (Map) aForm.getEventValues();
		logger.debug("eventParametersMap in advSUI****-->" + eventParaMap);

	}*/

}