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
 * 
 */

package edu.wustl.clinportal.util.querysuite;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.RoleInterface;
import edu.common.dynamicextensions.domaininterface.TaggedValueInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.query.beans.QueryResultObjectDataBean;
import edu.wustl.security.global.Utility;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.common.querysuite.queryobject.IQueryEntity;
import edu.wustl.common.query.queryobject.impl.OutputTreeDataNode;
import edu.wustl.common.query.queryobject.impl.metadata.QueryOutputTreeAttributeMetadata;
import edu.wustl.common.util.global.ApplicationProperties;

/**
 * @author Rukhsana_Sameer
 * This class contains all the methods required for CSM integration in query.
 */
public abstract class QueryCSMUtil
{

	/**This method will check if main objects for all the dependent objects are present in query or not.
	 * If yes then will create map of entity as key and main entity list as value.
	 * If not then will set error message in session.
	 * @param query
	 * @param session
	 * @param uniqueIdNodesMap
	 * @return
	 */
	public static Map<EntityInterface, List<EntityInterface>> setMainObjectErrorMessage(
			IQuery query, HttpSession session, Map<String, OutputTreeDataNode> uniqueIdNodesMap)
	{

		Map<EntityInterface, List<EntityInterface>> mainEntityMap = getMainEntitiesForAllQueryNodes(uniqueIdNodesMap);
		getAllDEEntities(query);
		String errorMessg = getErrorMessage(mainEntityMap, uniqueIdNodesMap);
		if (!errorMessg.equals(""))
		{
			session.setAttribute(Constants.NO_MAIN_OBJECT_IN_QUERY, errorMessg);
			return null;
		}
		session.setAttribute(Constants.MAIN_ENTITY_MAP, mainEntityMap);

		return mainEntityMap;
	}

	/**
	 * This method will return error message for main object.
	 * @param mainEntityMap main entity map.
	 * @param uniqueIdNodesMap contains all the entities in the query 
	 * @return error message if main entity of node not present.
	 */
	private static String getErrorMessage(
			Map<EntityInterface, List<EntityInterface>> mainEntityMap,
			Map<String, OutputTreeDataNode> uniqueIdNodesMap)
	{
		String errorMsg = "";
		//iterate through the uniqueIdNodesMap and check if main entities of all the nodes are present
		for (Iterator idMapIterator = uniqueIdNodesMap.entrySet().iterator(); idMapIterator
				.hasNext();)
		{
			Map.Entry<String, OutputTreeDataNode> IdmapValue = (Map.Entry<String, OutputTreeDataNode>) idMapIterator
					.next();
			// get the node
			OutputTreeDataNode node = IdmapValue.getValue();
			//get the entity
			EntityInterface mapEntity = node.getOutputEntity().getDynamicExtensionsEntity();
			// get the main entities list for the entity
			List<EntityInterface> mainEntityList = (List<EntityInterface>) mainEntityMap
					.get(mapEntity);
			//mainEntityList is null if the entity itself is main entity;
			EntityInterface mainEntity = null;
			if (mainEntityList != null)
			{
				//check if main entity of the dependent entity is present in the query
				mainEntity = getMainEntity(mainEntityList, node);
				if (mainEntity == null) // if main entity is not present
				{
					//set the error message 
					String mainEntityNames = "";
					for (EntityInterface entity : mainEntityList)
					{
						//get the names of all the main entities for the dependent entity
						String name = entity.getName();
						name = name.substring(name.lastIndexOf(".") + 1, name.length()); //TODO: use Utility Method for getting className
						mainEntityNames = mainEntityNames + name + " or ";
					}
					mainEntityNames = mainEntityNames.substring(0,
							mainEntityNames.lastIndexOf("r") - 1);
					String message = ApplicationProperties.getValue("query.mainObjectError");
					String entityName = mapEntity.getName();
					entityName = entityName.substring(entityName.lastIndexOf(".") + 1, entityName
							.length());//TODO: use Utility Method for getting className
					Object[] arguments = new Object[]{entityName, mainEntityNames};
					errorMsg = MessageFormat.format(message, arguments);
					break;
				}
			}
		}
		return errorMsg;
	}

	/**
	 * @param query
	 * @return
	 */
	private static List<Long> getAllDEEntities(IQuery query)
	{
		Set<IQueryEntity> queryEntities = query.getConstraints().getQueryEntities();
		List<Long> queryDeEntities = new ArrayList<Long>();
		for (IQueryEntity queryEntity : queryEntities)
		{
			queryDeEntities.add(queryEntity.getDynamicExtensionsEntity().getId());
		}
		return queryDeEntities;
	}

	/**
	 * This method will return map of a entity as value and list of all the main entities of this perticuler entity as value. 
	 * @param uniqueIdNodesMap Map that will all nodes present in query as node id as key and node as value. 
	 * @return mainEntityMap Map of all main entities present in query.
	 */
	private static Map<EntityInterface, List<EntityInterface>> getMainEntitiesForAllQueryNodes(
			Map<String, OutputTreeDataNode> uniqueIdNodesMap)
	{
		Map<EntityInterface, List<EntityInterface>> mainEntityMap = new HashMap<EntityInterface, List<EntityInterface>>();

		for (OutputTreeDataNode queryNode : uniqueIdNodesMap.values())
		{
			List<EntityInterface> mainEntityList = new ArrayList<EntityInterface>();
			EntityInterface dExtenEntity = queryNode.getOutputEntity().getDynamicExtensionsEntity();
			mainEntityList = getAllMainEntities(dExtenEntity, mainEntityList);
			if (!(mainEntityList != null && mainEntityList.size() == 1 && mainEntityList.get(0)
					.equals(dExtenEntity)))
			{
				mainEntityMap.put(dExtenEntity, mainEntityList);
			}
		}
		return mainEntityMap;
	}

	/**This is a recursive method that will create list of all main entities (Entities for which entity passed to it is having containment association ) 
	 * @param entity
	 * @param mainEntityList
	 */
	private static List<EntityInterface> getAllMainEntities(EntityInterface entity,
			List<EntityInterface> mainEntityList)
	{
		try
		{
			List<AssociationInterface> associationList = getIncomingContainmentAssociations(entity);
			if (associationList.isEmpty())
			{
				mainEntityList.add(entity);

			}
			else
			{
				for (AssociationInterface association : associationList)
				{
					mainEntityList = getAllMainEntities(association.getEntity(), mainEntityList);
				}
			}
		}
		catch (DynamicExtensionsSystemException deException)
		{
			deException.printStackTrace();
		}

		return mainEntityList;
	}

	/**
	 * This method will create queryResultObjectDataBean for a node passed to it.
	 * @param node node for which QueryResultObjectDataBean is to be created.
	 * @param mainEntityMap main entity map. 
	 * @return queryResultObjectDataBean.
	 */
	public static QueryResultObjectDataBean getQueryResulObjectDataBean(OutputTreeDataNode node,
			Map<EntityInterface, List<EntityInterface>> mainEntityMap)
	{
		QueryResultObjectDataBean qryResObjDataBean = new QueryResultObjectDataBean();
		boolean readDeniedObject = false;
		if (node != null)
		{
			EntityInterface dExtEntity = node.getOutputEntity().getDynamicExtensionsEntity();
			String entityName;
			//qryResObjDataBean.setPrivilegeType(Utility.getPrivilegeType(dExtEntity));
			// Replacing above line of code
			Map<String,String> tagKeyValueMap = new HashMap<String, String>();
			Collection<TaggedValueInterface> taggedValueCollection = dExtEntity.getTaggedValueCollection();
			for (TaggedValueInterface taggedValueInterface : taggedValueCollection) {
				tagKeyValueMap.put(taggedValueInterface.getKey(),taggedValueInterface.getValue());
			}
			qryResObjDataBean.setPrivilegeType(Utility.getInstance().getPrivilegeType(tagKeyValueMap));
			// Replacement end
			qryResObjDataBean.setEntity(dExtEntity);

			List<EntityInterface> mainEntityList = mainEntityMap.get(dExtEntity);
			if (mainEntityList == null)
			{
				entityName = dExtEntity.getName();
			}
			else
			{
				EntityInterface mainEntity = getMainEntity(mainEntityList, node);
				qryResObjDataBean.setMainEntity(mainEntity);
				entityName = mainEntity.getName();

			}

			qryResObjDataBean.setCsmEntityName(entityName);
			//setEntityName(queryResultObjectDataBean, entityName);
			readDeniedObject = isReadDeniedObject(qryResObjDataBean.getCsmEntityName());
			qryResObjDataBean.setReadDeniedObject(readDeniedObject);
		}
		return qryResObjDataBean;
	}

	/**This method will check if for an entity read denied has to checked or not. All theses entities are present in 
	 * Variables.queryReadDeniedObjectList list.
	 * @param name
	 * @return
	 */
	private static boolean isReadDeniedObject(String entityName)
	{
		if (edu.wustl.query.util.global.Variables.queryReadDeniedObjectList.contains(entityName))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Searches for main entity in parent hierarchy or child hierarchy
	 * @param mainEntityList - list of all main Entities
	 * @param node - current node
	 * @return - main Entity if found in parent or child hierarchy. Returns null if not found
	 */
	private static EntityInterface getMainEntity(List<EntityInterface> mainEntityList,
			OutputTreeDataNode node)
	{
		//check if node itself is main entity
		EntityInterface entity = null;

		// check if main entity is present in parent hierarchy
		if (node.getParent() != null)
		{
			entity = getMainEntityFromParentHierarchy(mainEntityList, node.getParent());
		}
		if (entity != null)
		{
			return entity;
		}

		//check if main entity is present in child hierarchy

		entity = getMainEntityFromChildHierarchy(mainEntityList, node);

		return entity;
	}

	/**
	 * To check whether the given Entity in OutputTreeDataNode is mainEntity or not 
	 * @param mainEntityList the list of main entities.
	 * @param node the OutputTreeDataNode
	 * @return The reference to entity in the OutputTreeDataNode, if its present in the mainEntityList.
	 */
	private static EntityInterface isMainEntity(List<EntityInterface> mainEntityList,
			OutputTreeDataNode node)
	{
		EntityInterface dExtEntity = node.getOutputEntity().getDynamicExtensionsEntity();
		if (mainEntityList.contains(dExtEntity))
		{
			return dExtEntity;
		}
		else
		{
			return null;
		}
	}

	/**
	 * recursively checks in parent hierarchy for main entity
	 * @param mainEntityList
	 * @param node
	 * @return main Entity if found in parent Hierarchy
	 */
	private static EntityInterface getMainEntityFromParentHierarchy(
			List<EntityInterface> mainEntityList, OutputTreeDataNode node)
	{

		EntityInterface entity = isMainEntity(mainEntityList, node);
		if (entity == null && node.getParent() != null)
		{

			return getMainEntityFromParentHierarchy(mainEntityList, node.getParent());

		}
		return entity;
	}

	/**
	 * recursively checks in child hierarchy for main entity
	 * @param mainEntityList
	 * @param node
	 * @return main Entity if found in child Hierarchy
	 */
	private static EntityInterface getMainEntityFromChildHierarchy(
			List<EntityInterface> mainEntityList, OutputTreeDataNode node)
	{
		EntityInterface entity = isMainEntity(mainEntityList, node);
		if (entity == null)
		{
			List<OutputTreeDataNode> children = node.getChildren();

			for (OutputTreeDataNode childNode : children)
			{
				entity = getMainEntityFromChildHierarchy(mainEntityList, childNode);
				if (entity != null)
				{
					return entity;
				}
			}
		}
		return entity;
	}

	/**This method will internally call  getIncomingAssociationIds of DE which will return all incoming associations 
	 * for entities passed.This method will filter out all incoming containment associations and return list of them.
	 * @param entity
	 */
	public static List<AssociationInterface> getIncomingContainmentAssociations(
			EntityInterface entity) throws DynamicExtensionsSystemException
	{
		EntityManagerInterface entityManager = EntityManager.getInstance();
		List<Long> allIds = (List<Long>) entityManager.getIncomingAssociationIds(entity);
		List<AssociationInterface> list = new ArrayList<AssociationInterface>();
		EntityCache cache = EntityCache.getInstance();
		for (Long id : allIds)
		{
			AssociationInterface associationById = cache.getAssociationById(id);

			RoleInterface targetRole = associationById.getTargetRole();
			if (associationById != null
					&& targetRole.getAssociationsType().getValue().equals(
							Constants.CONTAINTMENT_ASSOCIATION))
			{
				list.add(associationById);
			}
		}
		return list;
	}

	/**
	 * @param qryResObjDataBean
	 * @param columnIndex
	 * @param selectSql 
	 * @param entityIdIndexMap 
	 * @param uniqueIdNodesMap2 
	 * @param defineViewEntityList 
	 */
	public static String updateEntityIdIndexMap(QueryResultObjectDataBean qryResObjDataBean,
			int columnIndex, String selectSql, List<EntityInterface> defViewNodeLst,
			Map<EntityInterface, Integer> entityIdIndexMap,
			Map<String, OutputTreeDataNode> uniqueIdNodesMap)
	{
		List<String> selSqlColList = getListOfSelectedColumns(selectSql);
		if (defViewNodeLst == null)
		{
			OutputTreeDataNode oputTreeDataNode = getMatchingEntityNode(qryResObjDataBean
					.getMainEntity(), uniqueIdNodesMap);
			Map sqlIndexMap = putIdColumnsInSql(columnIndex, selectSql, entityIdIndexMap,
					selSqlColList, oputTreeDataNode);
			selectSql = (String) sqlIndexMap.get(Constants.SQL);
			columnIndex = (Integer) sqlIndexMap.get(Constants.ID_COLUMN_ID);
		}
		else
		{
			//Map<String, OutputTreeDataNode> uniqueIdNodesMap = QueryModuleUtil.uniqueIdNodesMap;
			Set<String> keySet = uniqueIdNodesMap.keySet();
			for (Iterator iterator = keySet.iterator(); iterator.hasNext();)
			{
				String key = "";
				Object nextObject = iterator.next();
				if (nextObject instanceof String)
				{
					key = (String) nextObject;
					OutputTreeDataNode oputTreDataNode = uniqueIdNodesMap.get(key);
					Map sqlIndexMap = putIdColumnsInSql(columnIndex, selectSql, entityIdIndexMap,
							selSqlColList, oputTreDataNode);
					selectSql = (String) sqlIndexMap.get(Constants.SQL);
					columnIndex = (Integer) sqlIndexMap.get(Constants.ID_COLUMN_ID);
				}
			}

		}
		if (qryResObjDataBean != null)
		{
			qryResObjDataBean.setEntityIdIndexMap(entityIdIndexMap);
			if (entityIdIndexMap.get(qryResObjDataBean.getMainEntity()) != null)
			{
				qryResObjDataBean.setMainEntityIdentifierColumnId(entityIdIndexMap
						.get(qryResObjDataBean.getMainEntity()));
			}
		}
		return selectSql;
	}

	/**
	 * To add the Id columns of MainEntities in the SQL if its not present. 
	 * It will also populate entityIdIndexMap passes it. 
	 * @param columnIndex
	 * @param selectSql
	 * @param entityIdIndexMap
	 * @param selSqlColList
	 * @param oputTreDataNode
	 * @return The modified SQL string.
	 */
	private static Map putIdColumnsInSql(int columnIndex, String selectSql,
			Map<EntityInterface, Integer> entityIdIndexMap, List<String> selSqlColList,
			OutputTreeDataNode oputTreDataNode)
	{
		Map sqlIndexMap = new HashMap();
		if (oputTreDataNode != null)
		{
			List<QueryOutputTreeAttributeMetadata> attributes = oputTreDataNode.getAttributes();
			for (QueryOutputTreeAttributeMetadata attributeMetaData : attributes)
			{
				AttributeInterface attribute = attributeMetaData.getAttribute();
				String sqlColumnName = attributeMetaData.getColumnName().trim();
				if (attribute.getName().equals(Constants.ID))
				{
					int index = selSqlColList.indexOf(sqlColumnName);

					if (index >= 0)
					{
						entityIdIndexMap.put(attribute.getEntity(), index);
						break;
					}
					else
					{
						if (selectSql.equals(""))
						{
							selectSql += sqlColumnName;
						}
						else
						{
							selectSql += ", " + sqlColumnName;
						}
						entityIdIndexMap.put(attribute.getEntity(), columnIndex);
						columnIndex++;
						break;
					}
				}
			}
		}
		sqlIndexMap.put(Constants.SQL, selectSql);
		sqlIndexMap.put(Constants.ID_COLUMN_ID, columnIndex);
		return sqlIndexMap;
	}

	/**
	 * TO the list of selectColumn Names in the selectSql.
	 * @param selectSql the Select part of SQL.
	 * @return The list of selectColumn Names in the selectSql.
	 */
	private static List<String> getListOfSelectedColumns(String selectSql)
	{
		String[] selSqlColArray = selectSql.split(",");
		List<String> selSqlColList = new ArrayList<String>();
		for (int i = 0; i < selSqlColArray.length; i++)
		{
			selSqlColList.add(selSqlColArray[i].trim());
		}
		return selSqlColList;
	}

	/**This method will return node corresponding to an entity from query.
	 * @param entity
	 * @param uniqueIdNodesMap 
	 * @return outputTreeDataNode
	 */
	private static OutputTreeDataNode getMatchingEntityNode(EntityInterface entity,
			Map<String, OutputTreeDataNode> uniqueIdNodesMap)
	{
		Iterator<OutputTreeDataNode> iterator = uniqueIdNodesMap.values().iterator();
		while (iterator.hasNext())
		{
			OutputTreeDataNode oputTreDataNode = (OutputTreeDataNode) iterator.next();
			if (oputTreDataNode.getOutputEntity().getDynamicExtensionsEntity().equals(entity))
			{
				return oputTreDataNode;
			}
		}
		return null;
	}

}
