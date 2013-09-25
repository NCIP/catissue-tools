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
 * Created on Nov 9, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package edu.wustl.clinportal.query;

import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpSession;
import javax.swing.tree.DefaultMutableTreeNode;
import edu.wustl.common.util.logger.Logger;

/**
 * @author rukhsana
 * @version
 * This class is for displaying the tree in query view of Advanced Search 
 * 
 */
public class TreeView
{
	private transient Logger logger = Logger.getCommonLogger(TreeView.class);
	//Variable which holds node ID in the tree. 
	private int nodeId = 0;
	private boolean andOrBool = false;
	private boolean noOrBool = false;

	//Recursive function to create the tree
	/**
	 * @param node
	 * @param parentId
	 * @param tree
	 * @param advCondnNodesMap
	 * @param checkedNode
	 * @param operation
	 * @param session
	 * @throws Exception
	 */
	public void arrangeTree(DefaultMutableTreeNode node, int parentId, Vector tree,
			Map advCondnNodesMap, int checkedNode, String operation, HttpSession session)
			throws Exception
	{
		/*nodeId++;
		//Checking whether parent rule has more than 1 child rule or not(OR condition)
		if (node.getChildCount() > 1)
		{
			logger.debug("childcount::" + node.getChildCount());
			AdvancedConditionsNode advConDefltOrNode = (AdvancedConditionsNode) node
					.getUserObject();
			logger.debug("advConditionDefaultOrNode::" + advConDefltOrNode);

			//Setting to true to display DOT image,symbol of OR condition
			if (advConDefltOrNode != null)
			{
				advConDefltOrNode.setDefaultAndOr(true);
			}
		}

		//Loop for all the children for the current node.
		for (int i = 0; i < node.getChildCount(); i++)
		{
			//nodeCount++;
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
			DefaultMutableTreeNode parent = (DefaultMutableTreeNode) child.getParent();

			//Condition that allow to start from Participant as a parent node
			if (!parent.isRoot())
			{
				AdvancedConditionsNode parentAdvCondNode = (AdvancedConditionsNode) parent
						.getUserObject();
				Operator operator = parentAdvCondNode.getOperationWithChildCondition();
				String temp = operator.getOperator();
				logger.debug("operator " + temp);
				//Condition to provide Pseudo And 
				if (temp.equals(Operator.EXIST)
						&& operator.getOperatorParams()[0].equals(Operator.AND))
				{
					andOrBool = true;
				}
				else
				{
					andOrBool = false;
				}

				//Boolean for setting value false in (String array)rule which will display DOT image 
				if (parentAdvCondNode.isDefaultAndOr())
				{
					noOrBool = true;
				}
				else
				{
					noOrBool = false;
				}
			}
			else
			{
				//Condition only when Parent is Root
				if (parent.getChildCount() > 1)
				{
					noOrBool = true;
				}
				else
				{
					noOrBool = false;
				}
			}
			AdvancedConditionsNode advConditionNode = (AdvancedConditionsNode) child
					.getUserObject();
			advCondnNodesMap.put(Integer.valueOf(nodeId), child);

			AdvancedConditionsNode temp = (AdvancedConditionsNode) session
					.getAttribute("tempAdvConditionNode");
			if (advConditionNode.getObjectName().equals(temp.getObjectName()))
			{
				session.setAttribute("lastNodeId", ("" + nodeId));
			}

			if (nodeId == checkedNode)
			{
				logger.debug("operation inside if nodeid clicked" + operation);
				if (operation.equals(Operator.EXIST))
				{
					logger.debug("Setting the child condition");
					//Condition to set value only when selected node has child
					if (child.getChildCount() > 0)
					{
						advConditionNode
								.setOperationWithChildCondition(new Operator(Operator.EXIST));
					}
				}
				else
				{
					advConditionNode.setOperationWithChildCondition(new Operator(Operator.OR));
				}
			}

			Vector vectorOfCondtions = advConditionNode.getObjectConditions();
			String tableName = advConditionNode.getObjectName();
			String str = "";
			Condition con = null;
			DataElement data = null;

			for (int k = 0; k < vectorOfCondtions.size(); k++)
			{
				con = (Condition) vectorOfCondtions.get(k);
				data = con.getDataElement();
				Operator oper = con.getOperator();
				String value = con.getValue();
				String columnName = data.getField();
				String table = data.getTableAliasName();
				//split column name in case of Specimen event parameters to remove aliasName
				//StringTokenizer columnNameTokenizer = new StringTokenizer(columnName,".");
				QueryBizLogic bizLogic = (QueryBizLogic) BizLogicFactory.getInstance().getBizLogic(
						edu.wustl.simplequery.global.Constants.SIMPLE_QUERY_INTERFACE_ID);
				//String columnDisplayName = bizLogic.getColumnDisplayNames(table,columnName);

				int formId = SearchUtil.getFormId(tableName);
				String columnDisplayName = SearchUtil.getColumnDisplayName(formId, table,
						columnName);

				//append table name to the column name in case of event parameters conditions.
				StringTokenizer tableTokens = new StringTokenizer(table, ".");
				//String superTable = "";
				if (tableTokens.countTokens() == 2)
				{
					logger.debug("table before tokenizing:" + table);
					table = tableTokens.nextToken();
					tableTokens.nextToken(); // superTable

					Map evntParamDispName = SearchUtil.getEventParametersDisplayNames(bizLogic,
							SearchUtil.getEventParametersTables(bizLogic));
					columnDisplayName = (String) evntParamDispName.get(table + "." + columnName);
				}
				logger.debug("column display name for event parameters" + columnDisplayName);
				if (columnDisplayName.equals(""))
				{
					columnDisplayName = columnName;
				}
				logger.debug("Column Display name in tree view:" + columnDisplayName);
				//String column = data.getField();
				if (k == 0)
				{

					str = nodeId + "|" + parentId + "|" + columnDisplayName + " "
							+ oper.getOperator() + " " + value + "";
				}
				else

				{
					str = str + " " + "<font color='red'>AND</font>" + " " + columnDisplayName
							+ " " + oper.getOperator() + " " + value + "";
				}
				// entered by Mandar for validation of single quotes around the values.
				logger.debug("STR :---------- : " + str);
			}
			if (data != null)
			{
				str = str + "|" + tableName;

				if (andOrBool)
				{
					str = str + "|true";
				}
				else
				{
					if (noOrBool)
					{
						str = str + "|false";
					}
					else
					//Appending default which will not display any image
					{
						str = str + "|default";
					}
				}
			}

			if (con == null)
			{
				str = nodeId + "|" + parentId + "|" + "ANY" + "|"
						+ advConditionNode.getObjectName();

				if (andOrBool)
				{
					str = str + "|true";
				}
				else
				{
					if (noOrBool)
					{
						str = str + "|false";
					}
					else
					{
						str = str + "|default";
					}
				}
			}
			logger.debug("STR in TREVIEW--" + str);
			tree.add(str);
			andOrBool = false;
			if (child.isLeaf())
			{
				nodeId++;
			}
			else
			{
				arrangeTree(child, nodeId, tree, advCondnNodesMap, checkedNode, operation, session);
			}
		}*/
	}
}
