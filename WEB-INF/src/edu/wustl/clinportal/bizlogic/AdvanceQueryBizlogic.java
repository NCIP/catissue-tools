/*
 * Created on Nov 22, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package edu.wustl.clinportal.bizlogic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.tree.TreeDataInterface;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.simplequery.query.Operator;

/**
 * @author rukhsana
 *
 * 
 */
public class AdvanceQueryBizlogic extends ClinportalDefaultBizLogic implements TreeDataInterface
{

	//Creates a temporary table containing the Advance Query Search results given the AdvanceSearchQuery.
	/**
	 * @param searchQuery
	 * @param tempTableName
	 * @param sessionData
	 * @param qryResObjDataMap
	 * @param hasCondOnIdfdFld
	 * @return
	 * @throws Exception
	 */
	/*public int createTempTable(String searchQuery, String tempTableName,
			SessionDataBean sessionData, Map qryResObjDataMap, boolean hasCondOnIdfdFld)
			throws Exception
	{
		String sql = "Create table " + tempTableName + " as " + "(" + searchQuery + " AND 1!=1)";
		//String sql = "Create table "+tempTableName+" as "+"("+searchQuery+")";
		Logger.out.debug("sql for create table" + sql);
		//JDBCDAO jdbcDao = (JDBCDAO) DAOFactory.getInstance().getDAO(Constants.JDBC_DAO);
		IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory();
		JDBCDAO jdbcDao = daoFactory.getJDBCDAO ();
		jdbcDao.openSession(sessionData);
		int noOfRecords = 0;
		List dataList = null;
		try
		{
			//Delete if there is any table existing with same name
			jdbcDao.delete(tempTableName);
			//Create empty temporary table
			jdbcDao.createTable(sql);

			//Insert list of data into the temporary table created.
	//			dataList = jdbcDao.executeQuery(searchQuery, sessionData, true, hasCondOnIdfdFld,
	//					qryResObjDataMap);
			dataList = jdbcDao.executeQuery(searchQuery, sessionData, true, qryResObjDataMap);
			
			Iterator dataListItr = dataList.iterator();
			while (dataListItr.hasNext())
			{
				List rowList = (List) dataListItr.next();
				//	        	Logger.out.debug("list size to be inserted"+rowList.size()+":"+rowList);
				jdbcDao.insert(tempTableName, rowList);
			}
			jdbcDao.commit();
			noOfRecords = dataList.size();
		}
		catch (Exception e)
		{
			Logger.out.error(
					"Exception occured in createTempTable method of AdvanceQueryBizlogic: "
							+ e.getMessage(), e);
			Logger.out.error("Sql for creating temporary table : " + sql);
			Logger.out.error("Search query being executed : " + searchQuery);
			Logger.out.error("Full list to be inserted : " + dataList);
			throw e;
		}
		finally
		{
			jdbcDao.closeSession();
		}
		return noOfRecords;
	}*/

	/* Get the data for tree view from temporary table and create tree nodes.
	 */
	/**
	 * @param sessionData
	 * @param columnIdsMap
	 * @param disableSpecIdsLst
	 * @throws DAOException
	 * @throws ClassNotFoundException
	 * @return
	 * @see edu.wustl.common.tree.TreeDataInterface#
	 * getTreeViewData(edu.wustl.common.beans.SessionDataBean, java.util.Map, java.util.List)
	 */
	/*public Vector getTreeViewData(SessionDataBean sessionData, Map columnIdsMap,
			List disableSpecIdsLst) throws DAOException, ClassNotFoundException
	{
		String tempTableName = edu.wustl.simplequery.global.Constants.QUERY_RESULTS_TABLE + "_" + sessionData.getUserId();
		//JDBCDAO jdbcDao = (JDBCDAO) DAOFactory.getInstance().getDAO(Constants.JDBC_DAO);
		IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory();
		JDBCDAO jdbcDao = daoFactory.getJDBCDAO ();
		List dataList = null;
		jdbcDao.openSession(sessionData);
		Logger.out.debug("Temp table in adv bizlogic:" + tempTableName);
		try
		{
			//Retrieve all the data from the temporary table
			dataList = jdbcDao.retrieve(tempTableName);
		}
		catch (DAOException excp)
		{
			Logger.out.error(excp.getMessage(), excp);
			throw excp;
		}
		finally
		{
			jdbcDao.closeSession();
		}

		Logger.out.debug("List of data for identifiers:" + dataList);

		//Get column identifiers from the column ids map.
		int ppantColumnId = ((Integer) columnIdsMap.get(Constants.PARTICIPANT + "."
				+ edu.wustl.common.util.global.Constants.IDENTIFIER)).intValue() - 1;
		int colProtColId = ((Integer) columnIdsMap.get(Constants.COLLECTION_PROTOCOL + "."
				+ edu.wustl.common.util.global.Constants.IDENTIFIER)).intValue() - 1;
		int specColGrpColId = ((Integer) columnIdsMap.get(Constants.SPECIMEN_COLLECTION_GROUP + "."
				+ edu.wustl.common.util.global.Constants.IDENTIFIER)).intValue() - 1;
		int specimenColumnId = ((Integer) columnIdsMap.get(Constants.SPECIMEN + "."
				+ edu.wustl.common.util.global.Constants.IDENTIFIER)).intValue() - 1;
		int parntSpeciColId = ((Integer) columnIdsMap.get(Constants.SPECIMEN + "."
				+ edu.wustl.simplequery.global.Constants.PARENT_SPECIMEN_ID_COLUMN)).intValue() - 1;

		int ppantFnameColId = ((Integer) columnIdsMap.get(Constants.PARTICIPANT + "."
				+ Constants.PARTICIPANT_FIRST_NAME)).intValue() - 1;
		int ppantLnameColId = ((Integer) columnIdsMap.get(Constants.PARTICIPANT + "."
				+ Constants.PARTICIPANT_LAST_NAME)).intValue() - 1;
		int colProtoNameColId = ((Integer) columnIdsMap.get(Constants.SPECIMEN_PROTOCOL + "."
				+ Constants.SPECIMEN_PROTOCOL_SHORT_TITLE)).intValue() - 1;
		int scgNameColumnId = ((Integer) columnIdsMap.get(Constants.SPECIMEN_COLLECTION_GROUP + "."
				+ Constants.SPECIMEN_COLLECTION_GROUP_NAME)).intValue() - 1;
		int specLableColId = ((Integer) columnIdsMap.get(Constants.SPECIMEN + "."
				+ Constants.SPECIMEN_LABEL)).intValue() - 1;

		Vector treeDataVector = new Vector();
		Iterator dataListIterator = dataList.iterator();
		List rowList = new ArrayList();
		List disSpecimenIds = new ArrayList();

		//Create tree nodes
		while (dataListIterator.hasNext())
		{
			rowList = (List) dataListIterator.next();

			String participantName = rowList.get(ppantFnameColId) + " "
					+ rowList.get(ppantLnameColId);
			if (participantName.trim().equals(""))
			{
				participantName = "NA";
			}
			setQueryTreeNode((String) rowList.get(ppantColumnId), Constants.PARTICIPANT,
					participantName, null, null, null, null, treeDataVector);

			//Create tree nodes for Participant & Collection Protocol
			setQueryTreeNode((String) rowList.get(colProtColId), Constants.COLLECTION_PROTOCOL,
					(String) rowList.get(colProtoNameColId), (String) rowList.get(ppantColumnId),
					Constants.PARTICIPANT, null, null, treeDataVector);

			//Create tree nodes for Specimen Collection Group
			setQueryTreeNode((String) rowList.get(specColGrpColId),
					Constants.SPECIMEN_COLLECTION_GROUP, (String) rowList.get(scgNameColumnId),
					(String) rowList.get(colProtColId), Constants.COLLECTION_PROTOCOL,
					(String) rowList.get(ppantColumnId), Constants.PARTICIPANT, treeDataVector);

			String parentSpecimenId = (String) rowList.get(parntSpeciColId);
			Logger.out.debug("parentSpecimenId" + parentSpecimenId);

			//if parent specimen not present, form tree node directly under Specimen Collection group
			if ("".equals(parentSpecimenId) || "0".equals(parentSpecimenId))
			{
				Logger.out.debug("parent specimen not present");
				setQueryTreeNode((String) rowList.get(specimenColumnId), Constants.SPECIMEN,
						(String) rowList.get(specLableColId),
						(String) rowList.get(specColGrpColId), Constants.SPECIMEN_COLLECTION_GROUP,
						null, null, treeDataVector);
			}
			//if parent specimen id is present for the specimen hierarchy
			else
			{
				String specimenId = (String) rowList.get(specimenColumnId);
				//get the specimen hierarchy above the current specimen
				List specIdsHrchy = getSpecimenHeirarchy(specimenId);
				specIdsHrchy.add(specimenId);

			   *//**
					* Description: Tree view of advanced query search results was showing incorrect labels for specimen 
					* since for parent specimen wrong labels of children were getting set. 
					* Now labels for all specimen in hierarchy are being specifically obtained and set
					*/
	/*
					List specimenLabels = getSpecimenLabels(specIdsHrchy);

					Logger.out.debug("list of parent specimen ids:" + specIdsHrchy);
					if (!specIdsHrchy.isEmpty())
					{
						setQueryTreeNode((String) specIdsHrchy.get(0), Constants.SPECIMEN,
								(String) specimenLabels.get(0), (String) rowList.get(specColGrpColId),
								Constants.SPECIMEN_COLLECTION_GROUP, null, null, treeDataVector);

						Iterator specIdsHrchyItr = specIdsHrchy.iterator();
						String parentSpIdInHrchy = (String) specIdsHrchyItr.next();
						int cnt = 1;
						while (specIdsHrchyItr.hasNext())
						{
							specimenId = (String) specIdsHrchyItr.next();
							setQueryTreeNode(specimenId, Constants.SPECIMEN, (String) specimenLabels
									.get(cnt++), parentSpIdInHrchy, Constants.SPECIMEN, null, null,
									treeDataVector);
							parentSpIdInHrchy = specimenId;
						}
					}

					//Find the specimen ids which are not satisfying the query condition to disable in tree view.
					//get the specimen ids present in temp table
					List prntSpIdsInTmpTbl = getParentSpecimensInTempTable(Constants.COLUMN
							+ specimenColumnId, tempTableName);

					//list of specimens not satisfying the query but are shown in results tree view.
					// These specimen ids have to be disabled.
					specIdsHrchy.removeAll(prntSpIdsInTmpTbl);
					disSpecimenIds.addAll(specIdsHrchy);
					Logger.out.debug("Specimen ids to be disabled in tree view" + specIdsHrchy);
				}

				Logger.out.debug("Tree Data:" + rowList.get(ppantColumnId) + " "
						+ rowList.get(colProtColId) + " " + rowList.get(specColGrpColId) + " "
						+ rowList.get(parntSpeciColId) + " " + rowList.get(specimenColumnId));
			}

			disableSpecIdsLst.addAll(disSpecimenIds);
			Logger.out.debug("vector of tree nodes" + treeDataVector);

			return createHierarchy(treeDataVector);
		}*/

	/**
	 * This method returns the list of labels of all the specimens in specimenIds Hierarchy.
	 * Returned Labels are in the same order as specimens sent
	 * @author aarti_sharma
	 * @param specimenIdsHrchy
	 * @return List of specimen labels
	 * @throws DAOException
	 * @throws ClassNotFoundException
	 */
	/*private List getSpecimenLabels(List specimenIdsHrchy) throws DAOException,
			ClassNotFoundException
	{
		List specimenLabels = new ArrayList();
		String sql;
		StringBuffer specimenIds = new StringBuffer();

		 * Form a string of all specimens in the list sent
		 
		for (int i = 0; i < specimenIdsHrchy.size() - 1; i++)
		{
			specimenIds.append(specimenIdsHrchy.get(i) + ",");
		}
		specimenIds.append(specimenIdsHrchy.get(specimenIdsHrchy.size() - 1));

		
		 * Fire a query to find labels of all specimens in the list sent
		 
		//JDBCDAO jdbcDao = (JDBCDAO) DAOFactory.getInstance().getDAO(Constants.JDBC_DAO);
		IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory();
		JDBCDAO jdbcDao = daoFactory.getJDBCDAO ();
		jdbcDao.openSession(null);
		sql = "Select IDENTIFIER,LABEL from CATISSUE_SPECIMEN where IDENTIFIER IN ("
				+ specimenIds.toString() + ")";
		List dataList = jdbcDao.executeQuery(sql, null, false, null);
		jdbcDao.closeSession();

		
		 * Put the labels in a map where key is specimen identifier and label is the value
		 
		List rowList;
		Map specimenLabelsMap = new HashMap();
		for (int i = 0; i < dataList.size(); i++)
		{
			rowList = (List) dataList.get(i);
			specimenLabelsMap.put((String) rowList.get(0), (String) rowList.get(1));
		}

		
		 * Extract specimen labels from  the map formed in previous step and insert them in a list
		 * in the same order as of the specimens in specimenIds Hierarchy
		 
		for (int i = 0; i < specimenIdsHrchy.size(); i++)
		{
			specimenLabels.add(specimenLabelsMap.get(specimenIdsHrchy.get(i)));
		}

		return specimenLabels;
	}*/

	/**
	 * Creates and returns the vector of AdvanceQueryTreeNode nodes from the QueryTreeNodeData nodes passed. 
	 * @param oldNodes Vector of QueryTreeNodeData nodes.
	 * @return the vector of AdvanceQueryTreeNode nodes from the QueryTreeNodeData nodes passed.
	 */

	/*private Vector createHierarchy(Vector oldNodes)
	{
		Vector finalTreeNodes = new Vector();

		Iterator iterator = oldNodes.iterator();
		while (iterator.hasNext())
		{
			QueryTreeNodeData treeNode = (QueryTreeNodeData) iterator.next();
			AdvanceQueryTreeNode treeNodeImpl = new AdvanceQueryTreeNode(Long
					.valueOf((String) treeNode.getIdentifier()), treeNode.getObjectName(), treeNode
					.getDisplayName());

			//If the parent is null, the node is of participant. Add it in the vector.
			if (treeNode.getParentIdentifier() == null
					&& treeNode.getCombinedParentIdentifier() == null)
			{
				if (!finalTreeNodes.contains(treeNodeImpl))
				{
					finalTreeNodes.add(treeNodeImpl);
				}
				continue;
			}

			//The parent node of this node.
			AdvanceQueryTreeNode parentTreeNode = new AdvanceQueryTreeNode();
			if (treeNode.getParentIdentifier() != null)
			{
				parentTreeNode.setIdentifier(Long.valueOf((String) treeNode.getParentIdentifier()));
				parentTreeNode.setValue(treeNode.getParentObjectName());
				parentTreeNode.setDisplayName(treeNode.getDisplayName());
			}

			//In case of specimen collection group, the participant node is also required.
			//So creating the parent of the parent node.
			AdvanceQueryTreeNode parntOfPrntNode = new AdvanceQueryTreeNode();
			if (treeNode.getCombinedParentIdentifier() != null)
			{
				parntOfPrntNode.setIdentifier(Long.valueOf((String) treeNode
						.getCombinedParentIdentifier()));
				parntOfPrntNode.setValue(treeNode.getCombinedParentObjectName());
				parentTreeNode.setDisplayName(treeNode.getDisplayName());
				parentTreeNode.setParentNode(parntOfPrntNode);
			}

			//get the parent node from the final tree node vector.
			parentTreeNode = (AdvanceQueryTreeNode) getNode(finalTreeNodes, parentTreeNode);
			if (parentTreeNode != null)
			{
				treeNodeImpl.setParentNode(parentTreeNode);
				if (!parentTreeNode.getChildNodes().contains(treeNodeImpl))
				{
					parentTreeNode.getChildNodes().add(treeNodeImpl);
				}
			}
		}
		Utility.sortTreeVector(finalTreeNodes);
		return finalTreeNodes;
	}*/

	/**
	 * Searches and returns the given node in the vector fo nodes and its child nodes. 
	 * @param treeNodeVector the vector of tree nodes. 
	 * @param treeNode the node to be searched.
	 * @return the node equal to the given node.
	 */
	/*private TreeNode getNode(Vector treeNodeVector, AdvanceQueryTreeNode treeNode)
	{
		//The node equal to the given node.
		AdvanceQueryTreeNode returnNode = null;

		Iterator treeNodeVectItr = treeNodeVector.iterator();
		while (treeNodeVectItr.hasNext())
		{
			AdvanceQueryTreeNode treeNodeImpl = (AdvanceQueryTreeNode) treeNodeVectItr.next();

			//If the node is not equal to collection protocol, check only the nodes and not their parents.
			if (!treeNode.getValue().equals(Constants.COLLECTION_PROTOCOL))
			{
				if (treeNodeImpl.getIdentifier().equals(treeNode.getIdentifier())
						&& treeNodeImpl.getValue().equals(treeNode.getValue()))
				{
					return treeNodeImpl;
				}
			}
			else
			//In case of collection protocol, check the nodes as well as their parent nodes.
			{
				if (treeNodeImpl.getParentNode() != null)
				{
					AdvanceQueryTreeNode parentNode = (AdvanceQueryTreeNode) treeNode
							.getParentNode();
					if ((parentNode.getIdentifier().equals(
							((AdvanceQueryTreeNode) treeNodeImpl.getParentNode()).getIdentifier()) && parentNode
							.getValue().equals(
									((AdvanceQueryTreeNode) treeNodeImpl.getParentNode())
											.getValue()))
							&& (treeNode.getIdentifier().equals(treeNodeImpl.getIdentifier()) && treeNode
									.getValue().equals(treeNodeImpl.getValue())))
					{
						return treeNodeImpl;
					}
				}
			}

			returnNode = (AdvanceQueryTreeNode) getNode(treeNodeImpl.getChildNodes(), treeNode);
			if (returnNode != null)
			{
				break;
			}
		}

		return returnNode;
	}*/

	//Create QueryTreeNode given the Tree node data.
	/**
	 * @param identifier
	 * @param objectName
	 * @param displayName
	 * @param parentIdentifier
	 * @param parentObjectName
	 * @param combinedParentId
	 * @param combdPrentObjName
	 * @param vector
	 */
	/*private void setQueryTreeNode(String identifier, String objectName, String displayName,
			String parentIdentifier, String parentObjectName, String combinedParentId,
			String combdPrentObjName, Vector vector)
	{
		QueryTreeNodeData treeNode = new QueryTreeNodeData();
		treeNode.setIdentifier(identifier);
		treeNode.setObjectName(objectName);
		treeNode.setDisplayName(displayName);
		treeNode.setParentIdentifier(parentIdentifier);
		treeNode.setParentObjectName(parentObjectName);
		treeNode.setCombinedParentIdentifier(combinedParentId);
		treeNode.setCombinedParentObjectName(combdPrentObjName);

		vector.add(treeNode);
	}*/

	/**
	 * @throws DAOException
	 * @return
	 * @see edu.wustl.clinportal.bizlogic.TreeDataInterface#getTreeViewData()
	 */
	public Vector getTreeViewData() throws DAOException
	{
		return null;
	}

	//Recursive function to Traverse root and set tables in path
	/**
	 * @param tree
	 * @param tableSet
	 * @throws DAOException
	 * @throws ClassNotFoundException
	 */
	/*public void setTables(DefaultMutableTreeNode tree, Set tableSet) throws DAOException,
			ClassNotFoundException
	{
		//Set eventParametersTables=new HashSet();
		*//**Set all the tables selected in the condition.
			 * Set all the tables in path between parent-child in the Advance query root object.
			 * Sets all the tables in path between AdvanceCondition Node Object name and its related tables.    
			 */
	/*
			DefaultMutableTreeNode parent = new DefaultMutableTreeNode();
			DefaultMutableTreeNode child = new DefaultMutableTreeNode();
			QueryBizLogic bizLogic = (QueryBizLogic) BizLogicFactory.getInstance().getBizLogic(
					edu.wustl.simplequery.global.Constants.SIMPLE_QUERY_INTERFACE_ID);

			int childCount = tree.getChildCount();
			//Logger.out.debug("childCount"+childCount);
			for (int i = 0; i < childCount; i++)
			{
				parent = (DefaultMutableTreeNode) tree.getChildAt(i);
				AdvancedConditionsNode parentAdvNode = (AdvancedConditionsNode) parent.getUserObject();
				String parentTable = parentAdvNode.getObjectName();
				String parentTableId = bizLogic.getTableIdFromAliasName(parentTable);
				Logger.out.debug("parentTableId" + parentTableId);

				AdvancedConditionsNode advNode = (AdvancedConditionsNode) parent.getUserObject();
				Vector conditions = advNode.getObjectConditions();

				Table table;
				String tableId = "";
				*//**in case of Specimen conditions the specimen event parameters object names will have the all the 
				 * tables in path in the object name
				 * Check if there are more than 1 table, tokens it and set proper table in DataElement
				 * for eg: if CellSpecimenReviewParameters condition is selected for Specimen Id column then the 
				 * object name will be CellSpecimenReviewParameters.SpecimenEventParamters
				 */
	/*

				if (parentTable.equals(Constants.SPECIMEN))
				{
					Iterator conditionsItr = conditions.iterator();
					while (conditionsItr.hasNext())
					{
						Condition condition = (Condition) conditionsItr.next();
						table = condition.getDataElement().getTable();
						String tableName = condition.getDataElement().getTableAliasName();
						Logger.out.debug("table in specimen condition..." + tableName);
						StringTokenizer tableTokens = new StringTokenizer(tableName, ".");
						if (tableTokens.countTokens() == 2)
						{
							String firstTableName = tableTokens.nextToken();
							tableName = tableTokens.nextToken();
							//						Logger.out.debug("table in specimen condition token1..."+objectName);
							Logger.out.debug("table in specimen condition  token2..." + firstTableName);
							//						eventParametersTables.add(tableName);
							condition.getDataElement().setTableName(tableName);

							// Changes to add actual event table also to the query 
							//when the attribute selected belongs to the base specimen event parameters class.
							//this linking table is used to get the actual event the query was made on while 
							//formatting tree in catissuecoreAdvancedQueryImpl
							table.setLinkingTable(new Table(firstTableName));

						}
						tableId = bizLogic.getTableIdFromAliasName(tableName);
						Set tablesInPath = bizLogic.setTablesInPath(Long.valueOf(parentTableId), Long
								.valueOf(tableId));
						Logger.out.debug("tablesinpath for specimen event tables:" + tablesInPath);
						tableSet.addAll(tablesInPath);

					}
				}
				else
				{
					Iterator conditionsItr = conditions.iterator();
					while (conditionsItr.hasNext())
					{
						Condition condition = (Condition) conditionsItr.next();
						String tableName = condition.getDataElement().getTableAliasName();
						tableId = bizLogic.getTableIdFromAliasName(tableName);
						Set tablesInPath = bizLogic.setTablesInPath(Long.valueOf(parentTableId), Long
								.valueOf(tableId));
						tableSet.addAll(tablesInPath);
					}
				}
				tableSet.add(parentTable);
				//Set tablesInPath between parent & child if child exists.
				if (!parent.isLeaf())
				{
					child = (DefaultMutableTreeNode) parent.getFirstChild();
					AdvancedConditionsNode childAdvNode = (AdvancedConditionsNode) child
							.getUserObject();
					String childTable = childAdvNode.getObjectName();
					tableSet.add(childTable);
					String childTableId = bizLogic.getTableIdFromAliasName(childTable);
					Set tablesInPath = bizLogic.setTablesInPath(Long.valueOf(parentTableId), Long
							.valueOf(childTableId));
					Logger.out.debug("tablesInPath after method call:" + tablesInPath);
					tableSet.addAll(tablesInPath);
				}
				setTables(parent, tableSet);
			}

		}*/

	//Get the specimen Ids hierarchy above a given specimen ids.
	/**
	 * @param specimenId
	 * @return
	 * @throws DAOException
	 * @throws ClassNotFoundException
	 */
	/*private List getSpecimenHeirarchy(String specimenId) throws DAOException,
			ClassNotFoundException
	{
		List specimenIdsList = new ArrayList();
		List specIdsLstInOrder = new ArrayList();
		//JDBCDAO jdbcDao = (JDBCDAO) DAOFactory.getInstance().getDAO(Constants.JDBC_DAO);
		IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory();
		JDBCDAO jdbcDao = daoFactory.getJDBCDAO ();
		jdbcDao.openSession(null);
		Long whereColumnValue = Long.valueOf(specimenId);
		String sql = "";
		while (whereColumnValue != null)
		{
			sql = "Select PARENT_SPECIMEN_ID from CATISSUE_SPECIMEN where IDENTIFIER = "
					+ whereColumnValue;
			List dataList = jdbcDao.executeQuery(sql, null, false, null);
			Logger.out.debug("list size in speci heirarchy:" + dataList.size() + " " + dataList);
			List rowList = (List) dataList.get(0);
			if (!rowList.get(0).equals(""))
			{
				whereColumnValue = Long.valueOf((String) rowList.get(0));
				specimenIdsList.add(whereColumnValue.toString());
			}
			else
			{
				whereColumnValue = null;
			}

		}
		//Order the specimen ids in the order which the tree nodes have to be created
		Logger.out.debug("List specimenIdsList before order" + specimenIdsList);
		for (int i = specimenIdsList.size() - 1; i >= 0; i--)
		{
			specIdsLstInOrder.add(specimenIdsList.get(i));

		}
		Logger.out.debug("List specimenIdsList after order" + specIdsLstInOrder);
		jdbcDao.closeSession();
		return specIdsLstInOrder;
	}*/

	//Get all the specimen ids in temp table to check the existence of all the specimens of the heirarchy 
	//in the temp table
	/**
	 * @param specimenColumnId
	 * @param tempTable
	 * @return
	 * @throws DAOException
	 * @throws ClassNotFoundException
	 */
	/*private List getParentSpecimensInTempTable(String specimenColumnId, String tempTable)
			throws DAOException, ClassNotFoundException
	{
		List parentSpecIdsLst = new ArrayList();

		//JDBCDAO jdbcDao = (JDBCDAO) DAOFactory.getInstance().getDAO(Constants.JDBC_DAO);
		IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory();
		JDBCDAO jdbcDao = daoFactory.getJDBCDAO ();
		jdbcDao.openSession(null);
		String sql = "Select " + specimenColumnId + " from " + tempTable;
		List dataList = jdbcDao.executeQuery(sql, null, false, null);
		Logger.out.debug("list size in speci in temp table:" + dataList.size() + " " + dataList);
		Iterator dataListItr = dataList.iterator();
		while (dataListItr.hasNext())
		{
			List rowList = (List) dataListItr.next();
			parentSpecIdsLst.add(rowList.get(0));
		}
		jdbcDao.closeSession();
		return parentSpecIdsLst;
	}*/

	//Sets the activity status conditions for the query to filter disbaled data
	/**
	 * @param tablesVector
	 * @param tableSuffix
	 * @return
	 */
	public String formActivityStatusConditions(Vector tablesVector, int tableSuffix)
	{
		StringBuffer actStatusConds = new StringBuffer();
		Iterator tablesVectorItr = tablesVector.iterator();

		while (tablesVectorItr.hasNext())
		{
			actStatusConds.append(" " + Operator.AND + " " + tablesVectorItr.next() + tableSuffix
					+ "." + edu.wustl.common.util.global.Constants.ACTIVITY_STATUS_COLUMN + " "
					+ Operator.NOT_EQUALS + " '" + Constants.ACTIVITY_STATUS_DISABLED + "' ");

		}

		return actStatusConds.toString();
	}

	/**
	 * To create SQL for the Advance Query navigation. This will create query on Temporary table.
	 * @param whereColumnName The whereColumnNames
	 * @param whereColumnValue The whereColumnValues
	 * @param whrColCondition The whereColumnConditions
	 * @param columnList The column list that will form SELECT clause of the query.
	 * @param orderByAttributes The attributes that will appear in the order by clause of SQL.
	 * @param sessionDataBean The session data bean object.
	 * @return The SQL.
	 */
	public String createSQL(String[] whereColumnName, String[] whereColumnValue,
			String[] whrColCondition, String[] columnList, String[] orderByAttributes,
			SessionDataBean sessionDataBean)
	{
		String tmpResTbleName = edu.wustl.simplequery.global.Constants.QUERY_RESULTS_TABLE + "_"
				+ sessionDataBean.getUserId();
		if (whereColumnName[0].equals(edu.wustl.common.util.global.Constants.ROOT))
		{
			whereColumnName = null;
			whrColCondition = null;
			whereColumnValue = null;
		}

		StringBuffer sql = new StringBuffer("Select DISTINCT ");

		List<String> selectColumnNames = Arrays.asList(columnList);
		// Forming SELECT clause
		sql.append(columnList[0]);
		for (int index = 1; index < columnList.length; index++)
		{
			sql.append(", ").append(columnList[index]);
		}

		// Forming FROM clause
		sql.append(" From ").append(tmpResTbleName);

		// Forming WHERE clause
		if (whereColumnName != null && whereColumnName.length != 0)
		{
			sql.append(" WHERE ");
			for (int index = 0; index < whereColumnName.length; index++)
			{
				sql.append(whereColumnName[index]).append(whrColCondition[index]).append(
						whereColumnValue[index]);
				if (index != whereColumnName.length - 1)
				{
					sql.append(" " + Constants.AND_JOIN_CONDITION + " ");
				}
			}
		}

		// Adding ORDER BY clause to query.
		if (orderByAttributes != null && orderByAttributes.length != 0)
		{
			List<String> orderByAttrList = new ArrayList<String>();
			for (int index = 0; index < orderByAttributes.length; index++)
			{
				if (selectColumnNames.contains(orderByAttributes[index]))
				{
					orderByAttrList.add(orderByAttributes[index]);
				}
			}
			// adding only attributes in order by clause which are present in select clause. 
			if (!orderByAttrList.isEmpty())
			{
				sql.append(" ORDER BY ").append(orderByAttrList.get(0));

				for (int index = 1; index < orderByAttrList.size(); index++)
				{
					sql.append(" , ").append(orderByAttrList.get(index));
				}
			}
		}

		return sql.toString();
	}

	// to remove this as method is present already
	/**
	 * @param sessionData
	 * @param map
	 * @param list
	 * @throws DAOException
	 * @throws ClassNotFoundException
	 * @return
	 */
	public Vector getTreeViewData(SessionDataBean sessionData, Map map, List list)
			throws DAOException, ClassNotFoundException
	{
		// TODO Auto-generated method stub
		return null;
	}
}
