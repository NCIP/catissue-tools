/**
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright:TODO</p>
 *@author 
 *@version 1.0
 */

package edu.wustl.clinportal.bizlogic;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import net.sf.ehcache.CacheException;
import edu.common.dynamicextensions.DEIntegration.DEIntegration;
import edu.common.dynamicextensions.domain.integration.EntityMap;
import edu.common.dynamicextensions.domain.integration.EntityMapCondition;
import edu.common.dynamicextensions.domain.integration.EntityMapRecord;
import edu.common.dynamicextensions.domain.integration.FormContext;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.clinportal.action.annotations.AnnotationConstants;
import edu.wustl.clinportal.domain.RecordEntry;
import edu.wustl.clinportal.util.CatissueCoreCacheManager;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.global.Constants;
import edu.wustl.dao.DAO;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.security.exception.UserNotAuthorizedException;

/**
 * @author falguni_sachde
 *
 */
public class AnnotationBizLogic extends ClinportalDefaultBizLogic
{

	/**
	 * 
	 * @param staticEntityId
	 * @return List of all dynamic entities Objects from a given static entity
	 * eg: returns all dynamic entity objects from a Participant,Specimen etc
	 * @throws DynamicExtensionsApplicationException 
	 * @throws DynamicExtensionsSystemException 
	 */
	public Collection getListOfDynamicEntities(Long staticEntityId)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		/**
		 * get all associated de entities with static entity and get its container id
		 */
		DEIntegration integration = new DEIntegration();
		Collection catContainerLst = integration
				.getCategoriesContainerIdFromHookEntity(staticEntityId);
		return new ArrayList(catContainerLst);
	}

	/**
	 * 
	 * @param staticEntityId
	 * @param typeId
	 * @param staticRecordId
	 * @return List of all dynamic entities id from a given static entity based on its protocol linkage
	 * eg: returns all dynamic entity id from a Participant,Specimen etc which is linked to Protocol 1, Protocol 2 etc
	 */
	public List getListOfDynamicEntitiesIds(long staticEntityId, long typeId, long staticRecordId)
	{
		List dynamicList = new ArrayList();

		String[] selectColumnName = {"containerId"};
		String[] whereColumnName = {"staticEntityId", "typeId", "staticRecordId"};
		String[] whrColnCondn = {"=", "=", "="};
		Object[] whereColumnValue = {Long.valueOf(staticEntityId), Long.valueOf(typeId),
				Long.valueOf(staticRecordId)};
		String joinCondition = Constants.AND_JOIN_CONDITION;
		try
		{
			dynamicList = retrieve(EntityMap.class.getName(), selectColumnName, whereColumnName,
					whrColnCondn, whereColumnValue, joinCondition);
		}
		catch (BizLogicException e)
		{

			e.printStackTrace();
		}
		return dynamicList;
	}

	/**
	 * 
	 * @param entityRecord
	 * Updates the Entity Record object in database
	 */
	public void updateEntityRecord(EntityMapRecord entityRecord)
	{
		try
		{
			update(entityRecord, edu.wustl.clinportal.util.global.Constants.HIBERNATE_DAO);
		}
		catch (BizLogicException e)
		{

			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param entityRecord
	 * Inserts a new EntityRecord record in Database
	 * @throws DAOException 
	 * @throws CacheException 
	 * @throws DynamicExtensionsSystemException 
	 * @throws DynamicExtensionsApplicationException 
	 * @throws NumberFormatException 
	 */
	public void insertEntityRecord(EntityMapRecord entityRecord) throws DAOException,
			CacheException, NumberFormatException, DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException
	{
		try
		{
			insert(entityRecord, edu.wustl.clinportal.util.global.Constants.HIBERNATE_DAO);
			Long entityMapId = entityRecord.getFormContext().getEntityMap().getId();
			Long statEntRecId = entityRecord.getStaticEntityRecordId();
			Long dynExtRecordId = entityRecord.getDynamicEntityRecordId();
			associateRecords(entityMapId, Long.valueOf(statEntRecId), Long.valueOf(dynExtRecordId));
		}
		catch (BizLogicException e)
		{

			e.printStackTrace();
		}
	}

	/**
	 * @param entityMapId
	 * @param long1
	 * @param long2
	 * @throws DAOException 
	 * @throws CacheException 
	 * @throws BizLogicException 
	 * @throws DynamicExtensionsSystemException 
	 * @throws DynamicExtensionsApplicationException 
	 * @throws NumberFormatException 
	 */
	public void associateRecords(Long containerId, Long staticEntyRecId, Long dynaEntyRecId)
			throws DAOException, CacheException, DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException, BizLogicException
	{

		CatissueCoreCacheManager catCoreCacheMgr = CatissueCoreCacheManager.getInstance();
		String staticEntityId = catCoreCacheMgr.getObjectFromCache(
				AnnotationConstants.RECORD_ENTRY_ENTITY_ID).toString();
		edu.wustl.clinportal.DEIntegration.DEIntegration integrate = new edu.wustl.clinportal.DEIntegration.DEIntegration();
		integrate.associateRecords(containerId, staticEntyRecId, dynaEntyRecId, Long
				.valueOf(staticEntityId));

	}

	/**
	 * 
	 * @param entityMap
	 * Updates the Entity Map object in database
	 */
	public void updateEntityMap(EntityMap entityMap)
	{
		try
		{
			update(entityMap, edu.wustl.clinportal.util.global.Constants.HIBERNATE_DAO);
		}
		catch (BizLogicException e)
		{

			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param entityMap
	 * Inserts a new EntityMap record in Database
	 */
	public void insertEntityMap(EntityMap entityMap)
	{
		try
		{

			Long dynamicEntityId = entityMap.getContainerId();
			CatissueCoreCacheManager catCoreCacheMger = CatissueCoreCacheManager.getInstance();
			String recEntryId = catCoreCacheMger.getObjectFromCache(
					AnnotationConstants.RECORD_ENTRY_ENTITY_ID).toString();
			Long staticEntityId = Long.valueOf(recEntryId);
			Long deAssociationID = AnnotationUtil.addAssociation(staticEntityId, dynamicEntityId,
					false);
			if (deAssociationID != null)
			{
				insert(entityMap, edu.wustl.clinportal.util.global.Constants.HIBERNATE_DAO);
			}
		}
		catch (Exception e)
		{

			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param dynaEntyContrId
	 * @return List of Static Entity Id from a given Dynamic Entity Id
	 * 
	 */
	public List getListOfStaticEntitiesIds(long dynaEntyContrId)
	{
		List dynamicList = new ArrayList();
		String[] selectColumnName = {"staticEntityId"};
		String[] whereColumnName = {"containerId"};
		String[] whrColCondition = {"="};
		Object[] whereColumnValue = {Long.valueOf(dynaEntyContrId)};
		String joinCondition = null;

		try
		{
			dynamicList = retrieve(EntityMap.class.getName(), selectColumnName, whereColumnName,
					whrColCondition, whereColumnValue, joinCondition);
		}
		catch (BizLogicException e)
		{

			e.printStackTrace();
		}

		return dynamicList;
	}

	/**
	 * 
	 * @param dyEntyContrId
	 * @return List of Static Entity Objects from a given Dynamic Entity Id
	 * 
	 */
	public List getListOfStaticEntities(long dyEntyContrId)
	{
		List dynamicList = new ArrayList();

		try
		{
			dynamicList = retrieve(EntityMap.class.getName(), "containerId", Long
					.valueOf(dyEntyContrId));
		}
		catch (BizLogicException e)
		{

			e.printStackTrace();
		}

		return dynamicList;
	}

	/**
	 * 
	 * @param entityMapId
	 * @return EntityMap object for its given id
	 */
	public List getEntityMap(long entityMapId)
	{
		List dynamicList = new ArrayList();

		try
		{
			dynamicList = retrieve(EntityMap.class.getName(), "id", Long.valueOf(entityMapId));
		}
		catch (BizLogicException e)
		{

			e.printStackTrace();
		}

		return dynamicList;
	}

	/**
	 * @param entityMapids
	 * @param staticRecordId
	 * @return
	 */
	public List getEntityMapRecordList(List entityMapids, long staticRecordId)
	{
		List dynamicList = new ArrayList();

		String[] selectColumnName = null;
		String[] whereColumnName = {"staticEntityRecordId", "formContext.entityMap.id"};
		String[] whrColCondn = {"=", "="};
		String joinCondition = Constants.AND_JOIN_CONDITION;

		Iterator iter = entityMapids.iterator();
		while (iter.hasNext())
		{
			Long entityMapId = (Long) iter.next();
			if (entityMapId != null)
			{
				Object[] whereColumnValue = {Long.valueOf(staticRecordId), entityMapId};
				try
				{
					List list = retrieve(EntityMapRecord.class.getName(), selectColumnName,
							whereColumnName, whrColCondn, whereColumnValue, joinCondition);
					if (list != null)
					{
						dynamicList.addAll(list);
					}
				}
				catch (BizLogicException e)
				{

					e.printStackTrace();
				}
			}

		}

		return dynamicList;
	}

	/**
	 * @param entityMapId
	 * @param dynaEntRecId
	 */
	public void deleteEntityMapRecord(long entityMapId, long dynaEntRecId)
	{
		List dynamicList = new ArrayList();

		String[] selectColumnName = null;
		String[] whereColumnName = {"formContext.entityMap.id", "dynamicEntityRecordId"};
		String[] whrColCondition = {"=", "="};
		Object[] whereColumnValue = {Long.valueOf(entityMapId), Long.valueOf(dynaEntRecId)};
		String joinCondition = Constants.AND_JOIN_CONDITION;

		try
		{
			dynamicList = retrieve(EntityMapRecord.class.getName(), selectColumnName,
					whereColumnName, whrColCondition, whereColumnValue, joinCondition);
		}
		catch (BizLogicException e)
		{

			e.printStackTrace();
		}

		if (dynamicList != null && !dynamicList.isEmpty())
		{

			try
			{
				EntityMapRecord entityRecord = (EntityMapRecord) dynamicList.get(0);
				entityRecord
						.setLinkStatus(edu.wustl.clinportal.util.global.Constants.ACTIVITY_STATUS_DISABLED);
				update(entityRecord, edu.wustl.clinportal.util.global.Constants.HIBERNATE_DAO);
			}
			catch (BizLogicException e1)
			{
				e1.printStackTrace();
			}
		}
	}

	/**
	 * @param containerId
	 * @param recordIdList
	 * @throws BizLogicException
	 */
	public void deleteAnnotationRecords(Long containerId, List<Long> recordIdList)
			throws BizLogicException
	{
		EntityManagerInterface entyMgerIntface = EntityManager.getInstance();
		try
		{
			entyMgerIntface.deleteRecords(containerId, recordIdList);
		}
		catch (Exception e)
		{
			e.printStackTrace();

		}
	}

	/**
	 * Deletes an object from the database.
	 * @param obj The object to be deleted.
	 * @throws DAOException
	 * @throws UserNotAuthorizedException TODO
	 */
	protected void delete(Object obj, DAO dao) throws BizLogicException
	{
		try
		{
			dao.delete(obj);
		}
		catch (DAOException e)
		{
			throw getBizLogicException(e, "biz.delete.error", "Exception in delete operation.");
		}
	}

	/**
	 * @param dynEntitiesList
	 * @param cpIdList
	 * @return
	 */
	public List getAnnotationIdsBasedOnCondition(List dynEntitiesList, List cpIdList)
	{
		List dynEntitiesIdList = new ArrayList();
		if (dynEntitiesList != null && !dynEntitiesList.isEmpty())
		{
			Iterator dynEntitesItr = dynEntitiesList.iterator();
			while (dynEntitesItr.hasNext())
			{
				EntityMap entityMap = (EntityMap) dynEntitesItr.next();
				Iterator formIterator = entityMap.getFormContextCollection().iterator();
				while (formIterator.hasNext())
				{
					FormContext formContext = (FormContext) formIterator.next();
					if ((formContext.getNoOfEntries() == null || formContext.getNoOfEntries()
							.equals(""))
							&& (formContext.getStudyFormLabel() == null || formContext
									.getStudyFormLabel().equals("")))
					{
						if (formContext.getEntityMapConditionCollection() != null
								&& !formContext.getEntityMapConditionCollection().isEmpty())
						{
							boolean check = checkStaticRecId(formContext
									.getEntityMapConditionCollection(), cpIdList);
							if (check)
							{
								dynEntitiesIdList.add(entityMap.getContainerId());
							}
						}
						else
						{
							dynEntitiesIdList.add(entityMap.getContainerId());
						}
					}
				}
			}
		}
		return dynEntitiesIdList;
	}

	/**
	 * @param entyMapCondnColn
	 * @param cpIdList
	 * @return
	 */
	private boolean checkStaticRecId(Collection entyMapCondnColn, List cpIdList)
	{
		Iterator entMapCondItr = entyMapCondnColn.iterator();
		try
		{
			CatissueCoreCacheManager cache = CatissueCoreCacheManager.getInstance();
			if (cpIdList != null && !cpIdList.isEmpty())
			{
				while (entMapCondItr.hasNext())
				{
					EntityMapCondition entityMapCond = (EntityMapCondition) entMapCondItr.next();
					if (entityMapCond.getTypeId().toString().equals(
							cache.getObjectFromCache(
									AnnotationConstants.COLLECTION_PROTOCOL_ENTITY_ID).toString())
							&& cpIdList.contains(entityMapCond.getStaticRecordId()))
					{
						return true;
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * @param entityMapId
	 * @return EntityMap object for its given id
	 */
	public List getEntityMapOnContainer(long containerId)
	{
		List dynamicList = new ArrayList();

		try
		{
			dynamicList = retrieve(EntityMap.class.getName(), "containerId", Long
					.valueOf(containerId));
		}
		catch (BizLogicException e)
		{
			e.printStackTrace();
		}

		return dynamicList;
	}

	/**
	 * @param entyMapCondn
	 */
	public void insertEntityMapCondition(EntityMapCondition entyMapCondn)
	{
		try
		{
			insert(entyMapCondn, edu.wustl.clinportal.util.global.Constants.HIBERNATE_DAO);
		}
		catch (Exception e)
		{

			e.printStackTrace();
		}

	}

	/**
	 * @param recordEntry
	 */
	public void insertRecordEntry(RecordEntry recordEntry)
	{
		try
		{
			insert(recordEntry, edu.wustl.clinportal.util.global.Constants.HIBERNATE_DAO);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	//	to get all entity map entries for a dynamic entity container
	/**
	 * @param deContainerId
	 * @return
	 * @throws DAOException
	 */
	public Collection getEntityMapsForContainer(Long deContainerId) throws BizLogicException
	{
		List entityMaps = retrieve(EntityMap.class.getName(), "containerId", deContainerId);
		return entityMaps;
	}

	/**
	 * this method returns DynamicRecord From association id
	 * @param recEntryId
	 * @return
	 * @throws DynamicExtensionsApplicationException 
	 * @throws DynamicExtensionsSystemException 
	 * @throws CacheException 
	 * @throws SQLException 
	 * @throws DAOException 
	 */
	public Collection getDynamicRecordFromStaticId(String recEntryId, Long containerId,
			String recEntryEntityId) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException, CacheException, SQLException, DAOException
	{

		Collection recList = new HashSet();

		DEIntegration integrate = new DEIntegration();
		recList = integrate.getDynamicRecordFromStaticId(recEntryId, containerId, recEntryEntityId);

		return recList;
	}
}
