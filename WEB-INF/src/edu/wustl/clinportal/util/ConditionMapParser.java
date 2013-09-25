/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */


package edu.wustl.clinportal.util;

//import java.util.ArrayList;
//import java.util.Arrays;
import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
//import java.util.Vector;

//import javax.servlet.http.HttpSession;
//import javax.swing.tree.DefaultMutableTreeNode;

//import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.util.logger.Logger;
//import edu.wustl.dao.exception.DAOException;
//import edu.wustl.simplequery.query.Condition;
//import edu.wustl.simplequery.query.DataElement;
//import edu.wustl.simplequery.query.Operator;

/**
 * @author rukhsana sameer
 *
 * ConditionMapParser is the parser class to parse the Condition object and 
 * create advancedConditionNode for Advance Search
 */
public class ConditionMapParser
{

	/**
	 * Given a Map, parseConditionForQuery function creates list of conditions for Advance Search query
	 * @param conditionMap
	 * @return
	 * @throws DAOException
	 * @throws ClassNotFoundException
	 */
	/*public List parseConditionForQuery(Map conditionMap) throws DAOException,
			ClassNotFoundException
	{
		List conditionList = new ArrayList();
		Iterator keyItr = conditionMap.keySet().iterator();
		while (keyItr.hasNext())
		{
			DataElement dataElement = new DataElement();
			String key = (String) keyItr.next();
			//Check for the keys of operators which is in the form Operator:TableAliasName:ColumnName
			if (key.startsWith("Operator"))
			{
				StringTokenizer stoken = new StringTokenizer(key, ":");
				String operator = (String) conditionMap.get(key);
				if (!operator.equals(edu.wustl.common.util.global.Constants.ANY))
				{
					String value = "";
					String value2 = "";
					String operator1 = "";
					String operator2 = "";
					while (stoken.hasMoreTokens())
					{
						stoken.nextToken();
						String aliasName = stoken.nextToken();

						dataElement.setTableName(aliasName);
						String columnName = stoken.nextToken();
						value = (String) conditionMap.get(aliasName + ":" + columnName);
						//Append the Event Parameters object name to the column name in the conditions.

						Logger.out.debug("column name in the condition parser " + columnName);
						dataElement.setField(columnName);

						//Create two different conditions in case of Between and Not Between operators.
						if (operator.equals(Operator.NOT_BETWEEN))
						{
							operator1 = Operator.LESS_THAN_OR_EQUALS;
							operator2 = Operator.GREATER_THAN_OR_EQUALS;
							value2 = (String) conditionMap.get(aliasName + ":" + columnName + ":"
									+ "HLIMIT");
						}
						else if (operator.equals(Operator.BETWEEN))
						{
							operator1 = Operator.GREATER_THAN_OR_EQUALS;
							operator2 = Operator.LESS_THAN_OR_EQUALS;
							value2 = (String) conditionMap.get(aliasName + ":" + columnName + ":"
									+ "HLIMIT");
						}
						Logger.out.debug("After changing value of condition obj:value1-" + value
								+ " value2-" + value2);
					}
					//String operatorValue = Operator.getOperator(operator);
					Condition condition = new Condition(dataElement, new Operator(operator), value);
					if (operator.equals(Operator.NOT_BETWEEN))
					{
						condition = new Condition(dataElement, new Operator(operator2), value2);
						Condition condition1 = new Condition(dataElement, new Operator(operator1),
								value);
						conditionList.add(condition1);
					}
					if (operator.equals(Operator.BETWEEN))
					{
						condition = new Condition(dataElement, new Operator(operator2), value2);
						Condition condition1 = new Condition(dataElement, new Operator(operator1),
								value);
						conditionList.add(condition1);
					}
					conditionList.add(condition);
				}
			}
		}
		return conditionList;
	}

	*//**
	 *Given a list of conditions, creates an advancedConditionNode and adds it to the root.
	 * @param list
	 * @param root
	 * @param objectName
	 * @param selectedNode
	 * @param advCondnNodesMap
	 * @param nodeId
	 * @param session
	 * @return
	 *//*
	public DefaultMutableTreeNode createAdvancedQueryObj(List list, DefaultMutableTreeNode root,
			String objectName, String selectedNode, Map advCondnNodesMap, Integer nodeId,
			HttpSession session)
	{
			//String tableObject = condition.getDataElement().getTable();
		Logger.out.debug("selectedNode" + selectedNode);
		//Split the selected node to get all the nodes which are checked.
		StringTokenizer selNodeTokens = new StringTokenizer(selectedNode, ",");
		Integer[] selectedNodeArray = new Integer[selNodeTokens.countTokens()];
		//int selectedNodeArray[] =new int[selectedNodeTokens.countTokens()];
		int idx = 0;
		while (selNodeTokens.hasMoreTokens())
		{
			//selectedNodeArray[i]=Integer.parseInt(selectedNodeTokens.nextToken());
			selectedNodeArray[idx] = Integer.valueOf(selNodeTokens.nextToken());
			Logger.out.debug("Selected Node after splitting :" + selectedNodeArray[idx]);
			idx++;
		}
		Vector objectConditions = new Vector(list);
		//		String prevTableObj;
		Logger.out.debug("nodeId--" + nodeId);

		//Condition for Add operation
		if (nodeId == null)
		{
			AdvancedConditionsNode advCondsNode = new AdvancedConditionsNode(objectName);
			advCondsNode.setObjectConditions(objectConditions);

			DefaultMutableTreeNode child = createDefaultMutableTreeNode(objectName,
					objectConditions);
//			setting AdvancedConditionsNode of the child node in session
			session.setAttribute("tempAdvConditionNode", child.getUserObject());

			if (root.getChildCount() == 0)
			{
				child = createHeirarchy(child, edu.wustl.common.util.global.Constants.ROOT);
				root.add(child);
			}
			else
			{

				//addNode1(root,selectedNodeArray,nodeCount,child,objectName);
				addNode(selectedNodeArray, child, advCondnNodesMap);
				Logger.out.debug("root size" + root.getDepth());

			}
		}
		//Else edit operation
		else
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) advCondnNodesMap.get(nodeId);
			AdvancedConditionsNode advCondsNode = (AdvancedConditionsNode) node.getUserObject();
			advCondsNode.setObjectConditions(objectConditions);
		}
		return root;
		return null;
	}

	*//**
	 * To create the intermediate node hierarchy. 
	 * @param node The Current node to be added.
	 * @param parentNodeObjName The parent Node till which the hierarchy t0 be created.
	 * @return The parent/grand parent of the child node to be added in the condition node tree.
	 *//*
	private DefaultMutableTreeNode createHeirarchy(DefaultMutableTreeNode node,
			String parentNodeObjName)
	{
		AdvancedConditionsNode advConditionNode = (AdvancedConditionsNode) node.getUserObject();
		String objectName = advConditionNode.getObjectName();
		List objectNames = Arrays.asList(Constants.ADVANCE_QUERY_TREE_HEIRARCHY);

		int index = objectNames.indexOf(objectName); // Index of object to be added.
		int parentIndex = objectNames.indexOf(parentNodeObjName); // Index of parent object

		DefaultMutableTreeNode childNode = node;
		DefaultMutableTreeNode tempChildNode;

		//creating hierarchy in reverse order i.e. specimen, specimen Collection Group, Collection protocol etc.
		for (int i = index - 1; i > parentIndex; i--)
		{
			tempChildNode = childNode;
			childNode = createDefaultMutableTreeNode(Constants.ADVANCE_QUERY_TREE_HEIRARCHY[i],
					null);
			childNode.add(tempChildNode);

		}
		return childNode; //returning actual node to be added in the hierarchy.
	}

	*//**
	 * To create An empty condition node for given objectName
	 * @param objectName The String representing AdvancedConditionsNode name.
	 * @param objectConditions The Vector of Condition objects.
	 * @return The DefaultMutableTreeNode reference to newly created node.
	 *//*
	private DefaultMutableTreeNode createDefaultMutableTreeNode(String objectName,
			Vector objectConditions)
	{
		AdvancedConditionsNode advCondsNode = new AdvancedConditionsNode(objectName);
		if (objectConditions != null)
		{
			advCondsNode.setObjectConditions(objectConditions);
		}
		
		return new DefaultMutableTreeNode(advCondsNode);
	}

	*//**
	 * @param selectedNode
	 * @param advCondNodesMap
	 *//*
	public void deleteSelectedNode(String selectedNode, Map advCondNodesMap)
	{
		//Split the selected node to get all the nodes which are checked.
		StringTokenizer selNodeTokens = new StringTokenizer(selectedNode, ",");

		while (selNodeTokens.hasMoreTokens())
		{
			//Gets node to be deleted from Map
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) advCondNodesMap.get(Integer
					.valueOf(selNodeTokens.nextToken()));
			DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();

			//Position of node to be deleted from its parent node
			int position = parent.getIndex(node);

			//remove node and its sub node from parent
			parent.remove(position);
			//Remove the or symbol if a sibling is deleted
			int childCount = parent.getChildCount();
			Logger.out.debug("child count after delete" + childCount);
			 setDefaultAndOr attribute to 'false' and OperationWithChildCondition to 'Or' 
			 * so that the 'or' or 'and' symbol is removed when a node is deleted and the number of children 
			 * of the parent of the deleted node is lesser than two.
			 
			if (childCount == 1 && !parent.isRoot())
			{
				AdvancedConditionsNode parentNode = (AdvancedConditionsNode) parent.getUserObject();
				//Logger.out.debug("reset all siblings"+parentNode.getOperationWithChildCondition().getOperator());
				//DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) parent.getFirstChild();
				//AdvancedConditionsNode child = (AdvancedConditionsNode) childNode.getUserObject();
				parentNode.setDefaultAndOr(false);
				parentNode.setOperationWithChildCondition(new Operator(Operator.OR));
			}

		}
	}

	*//**
	 * @param args
	 * @throws Exception
	 *//*
	public static void main(String[] args) throws Exception
	{
		Map map = new HashMap();
		map.put("EventName_1", "CellSpecimenReviewEventParameters");
		map.put("EventColumnName_1", "CellSpecimenReviewEventParameters.IDENTIFIER.bigint");
		map.put("EventColumnOperator_1", "=");
		map.put("EventColumnValue_1", "1");

		//ConditionMapParser conditionParser = new ConditionMapParser();
		String[] keys1 = {"Participant:LAST_NAME", "Participant:GENDER",
				"Operator:Participant:LAST_NAME", "Operator:Participant:GENDER"};
		String[] values1 = {"Part", "Male", "LIKE", "EQUALS"};
		String[] keys2 = {"CollectionProtocolRegistration:INDENTIFIER",
				"Operator:CollectionProtocolRegistration:INDENTIFIER"};
		String[] values2 = {"1", "GREATER_THAN"};
		String[] keys3 = {"CollectionProtocolRegistration:INDENTIFIER",
				"Operator:CollectionProtocolRegistration:INDENTIFIER"};
		String[] values3 = {"10", "LESS_THAN"};
		String[] keys4 = {"SpecimenCollectionGroup:CLINICAL_STATUS",
				"Operator:SpecimenCollectionGroup:CLINICAL_STATUS"};
		String[] values4 = {"Relapse", "Equal"};
		String[] keys5 = {"Participant:LAST_NAME", "Operator:Participant:LAST_NAME"};
		String[] values5 = {"A", "LIKE"};
		String[] keys6 = {"Specimen:TYPE", "Operator:Specimen:TYPE"};
		String[] values6 = {"cDNA", "EQUAL"};

	}

	//Add advancedConditionNode
	*//**
	 * @param selectedNode
	 * @param presentNode
	 * @param advCondnNodesMap
	 *//*
	private void addNode(Integer[] selectedNode, DefaultMutableTreeNode presentNode,
			Map advCondnNodesMap)
	{
		//		DefaultMutableTreeNode child = new DefaultMutableTreeNode();
		//		DefaultMutableTreeNode parent = new DefaultMutableTreeNode();
		DefaultMutableTreeNode selectedAdvNode;
		//boolean anyCondnExists = false;
		if (selectedNode.length == 0)
		{
			selectedAdvNode = (DefaultMutableTreeNode) advCondnNodesMap.get(Integer.valueOf(0));
			AdvancedConditionsNode advConditionNode = (AdvancedConditionsNode) presentNode
					.getUserObject();
			if (!advConditionNode.getObjectName().equals(Constants.PARTICIPANT))
			{
				presentNode = createHeirarchy(presentNode, Constants.ROOT);
			}
			selectedAdvNode.add(presentNode);
		}
		else
		{
			Logger.out.debug("AdvanceQueryMap: " + advCondnNodesMap);
			for (int i = 0; i < selectedNode.length; i++)
			{
				AdvancedConditionsNode advConditionNode = (AdvancedConditionsNode) presentNode
						.getUserObject();
				String parentNodeName;
				if (advConditionNode.getObjectName().equals(Constants.PARTICIPANT))
				{
					selectedAdvNode = (DefaultMutableTreeNode) advCondnNodesMap.get(Integer
							.valueOf(0));
					parentNodeName = edu.wustl.common.util.global.Constants.ROOT;
				}
				else
				{
					Logger.out.debug("selectedNode[i]-->" + selectedNode[i]);
					selectedAdvNode = (DefaultMutableTreeNode) advCondnNodesMap
							.get(selectedNode[i]);
					parentNodeName = ((AdvancedConditionsNode) selectedAdvNode.getUserObject())
							.getObjectName();
				}
				Logger.out.debug("AdvanceQueryMap for: " + selectedNode[i] + ":"
						+ advCondnNodesMap.get(selectedNode[i]));
				presentNode = createHeirarchy(presentNode, parentNodeName);

				selectedAdvNode.add(presentNode);

				Logger.out.debug("loop count " + i);
			}
		}
	}*/

	/**
	 * This function parses the event parameter map & returns it in a format
	 * suitable to parseCondition() function.
	 * @param eventMap A map of specimen event parameters that is to be parsed.
	 * @return Map the parsed map suitable for parseCondition().
	 */
	public static Map parseEventParameterMap(Map eventMap)
	{
		Logger.out.debug("Map of the events:" + eventMap);
		Map newMap = new HashMap();

		if (eventMap != null)
		{
			int rows = eventMap.size() / 4;

			//Constants for eventMap keys
			String columnKeyConstant = "EventColumnName_";
			String columnValConstant = "EventColumnValue_";
			String operatorConstant = "EventColumnOperator_";
			String eventNameConstant = "EventName_";

			for (int i = 1; i <= rows; i++)
			{
				//Preparing the eventMap keys
				String columnKey = columnKeyConstant + i;
				String columnValKey = columnValConstant + i;
				String operatorKey = operatorConstant + i;
				String eventNameKey = eventNameConstant + i;

				String columnKeyValue = (String) eventMap.get(columnKey);
				StringTokenizer tokenizer = new StringTokenizer(columnKeyValue, ".");

				//Extracting alias name & column name
				String aliasName = tokenizer.nextToken();
				String columnName = "";
				if (tokenizer.hasMoreTokens())
				{
					columnName = tokenizer.nextToken();
				}

				//Extracting actual column value & operator value
				String columnValue = (String) eventMap.get(columnValKey);
				Logger.out.debug("value of event parameters condition:" + columnValue);
				String operatorValue = (String) eventMap.get(operatorKey);
				Logger.out.debug("operator of event parameters condition:" + operatorValue);
				String eventName = (String) eventMap.get(eventNameKey);
				//Preparing keys for new map
				String newValKey = eventName + "." + aliasName + ":" + columnName;
				String newOpKey = "Operator:" + eventName + "." + aliasName + ":" + columnName;

				//Setting values in new map
				newMap.put(newValKey, columnValue);
				newMap.put(newOpKey, operatorValue);
			}
		}

		return newMap;
	}
}