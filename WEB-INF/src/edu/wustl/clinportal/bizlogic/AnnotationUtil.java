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
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright:TODO</p>
 *@author
 *@version 1.0
 */

package edu.wustl.clinportal.bizlogic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import org.hibernate.HibernateException;
import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.Entity;
import edu.common.dynamicextensions.domain.userinterface.Container;
import edu.common.dynamicextensions.domaininterface.AbstractEntityInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.RoleInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ConstraintPropertiesInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationDirection;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationType;
import edu.common.dynamicextensions.util.global.DEConstants.Cardinality;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.cab2b.server.path.PathFinder;
import edu.wustl.clinportal.annotations.PathObject;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;

/**
 * @author falguni_sachde
 *
 */
public class AnnotationUtil
{

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}
	private static transient Logger LOGGER = Logger.getCommonLogger(AnnotationUtil.class);

	//private static org.apache.log4j.Logger logger = Logger.getLogger(AnnotationUtil.class);

	private static Long recordEntryId;
	private static Map<Long, List<EntityInterface>> entyVsChldEnts = new HashMap<Long, List<EntityInterface>>();
	private static boolean isMapPopulated = false;
	private static String pathInsertStatement = "insert into PATH (PATH_ID, FIRST_ENTITY_ID,"
			+ "INTERMEDIATE_PATH, LAST_ENTITY_ID) values (";

	/**
	 * @param staticEntityId
	 * @param dynamicEntityId
	 * @param isEntityFromXmi
	 * @return
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 * @throws BizLogicException
	 */
	public static synchronized Long addAssociation(Long staticEntityId, Long dynamicEntityId,
			boolean isEntityFromXmi) throws DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException, BizLogicException
	{

		//This change is done because when the hierarchy of objects grow in dynamic extensions,
		// then NonUniqueObjectException is thrown.
		//So static entity and dynamic entity are brought in one session and then associated.

		AssociationInterface association = null;
		HibernateDAO dao = null;
		EntityInterface staticEntity = null;
		EntityInterface dynamicEntity = null;

		try
		{
			dao = DynamicExtensionsUtility.getHibernateDAO();
			staticEntity = (EntityInterface) dao.retrieveById(Entity.class.getName(),
					staticEntityId);
			dynamicEntity = (EntityInterface) ((Container) dao.retrieveById(Container.class
					.getName(), dynamicEntityId)).getAbstractEntity();

		}
		catch (DAOException e)
		{
			throw new BizLogicException(ErrorKey.getErrorKey("error.common.hibernate"), e,
					"DAO Exception");
		}
		finally
		{

			try
			{
				DynamicExtensionsUtility.closeHibernateDAO(dao);
			}
			catch (DAOException e)
			{

			}
		}

		association = addAssociationForEntities(staticEntity, dynamicEntity);

		Long start = Long.valueOf(System.currentTimeMillis());

		staticEntity = EntityManager.getInstance().persistEntityMetadataForAnnotation(staticEntity,
				true, false, association);

		Long end = Long.valueOf(System.currentTimeMillis());
		//logger.info("Time required to persist one entity is " + (end - start) / 1000 + "seconds");

		//Add the column related to the association to the entity table of the associated entities.
		EntityManager.getInstance().addAssociationColumn(association);
		JDBCDAO jdbcdao = null;
		String appName = CommonServiceLocator.getInstance().getAppName();
		IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
		try
		{
			jdbcdao = daoFactory.getJDBCDAO();
			jdbcdao.openSession(null);

			addQueryPaths(staticEntity, dynamicEntity, isEntityFromXmi, association.getId(),
					jdbcdao);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				jdbcdao.commit();
				jdbcdao.closeSession();
			}
			catch (DAOException e)
			{
				throw new DynamicExtensionsSystemException("Exception in addAssociation ." + e);
			}
		}

		return association.getId();
	}

	/**
	 * 
	 * @param staticEntity
	 * @param dynamicEntity
	 * @param isEntityFromXmi
	 * @param associationId
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 * @throws BizLogicException
	 */
	public static void addQueryPaths(EntityInterface staticEntity, EntityInterface dynamicEntity,
			boolean isEntityFromXmi, Long associationId, JDBCDAO jdbcdao,
			HibernateDAO... hibernateDAO) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException, BizLogicException
	{
		Set<PathObject> processedPathList = new HashSet<PathObject>();

		if (!isMapPopulated || !isEntityFromXmi)
		{
			EntityGroupInterface entityGroup = dynamicEntity.getEntityGroup();
			populateEntityVsChildEntitiesMap(entityGroup);
			//Retrieving the RecordEntry identifier
			setRecordEntryId(hibernateDAO);
		}

		if (associationId == null)
		{
			Collection<Long> associationIds = EntityManager.getInstance().getAssociationIds(
					staticEntity.getId(), dynamicEntity.getId());
			for (Iterator<Long> iterator = associationIds.iterator(); iterator.hasNext();)
			{
				associationId = iterator.next();
				addQueryPathsForAllAssociatedEntities(dynamicEntity, staticEntity, associationId,
						processedPathList, false, jdbcdao);
			}
		}
		else
		{
			addQueryPathsForAllAssociatedEntities(dynamicEntity, staticEntity, associationId,
					processedPathList, false, jdbcdao);
		}

		addEntitiesToCache(isEntityFromXmi, dynamicEntity, staticEntity);
	}

	/**
	 * @param staticEntityId
	 * @param dynamicEntityId
	 * @param isEntityFromXmi
	 * @return
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 * @throws BizLogicException
	 */
	public static synchronized Long addNewPathsForExistingMainContainers(
			EntityInterface staticEntity, EntityInterface dynamicEntity, boolean isEntityFromXmi,
			JDBCDAO jdbcdao, HibernateDAO hibernateDAO)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException,
			BizLogicException
	{
		AssociationInterface association = null;
		try
		{
			association = getAssociationForEntity(staticEntity, dynamicEntity);
			addQueryPaths(staticEntity, dynamicEntity, isEntityFromXmi, association.getId(),
					jdbcdao, hibernateDAO);
		}
		catch (HibernateException e1)
		{

			System.out.println("Error while addNewPathsForExistingMainContainers");

		}
		return association.getId();
	}

	/**
	 * getAssociationForEntity.
	 * @param staticEntity
	 * @param dynamicEntity
	 * @return
	 */
	public static AssociationInterface getAssociationForEntity(EntityInterface staticEntity,
			AbstractEntityInterface dynamicEntity)
	{
		Collection<AssociationInterface> associationCollection = staticEntity
				.getAssociationCollection();
		for (AssociationInterface associationInteface : associationCollection)
		{
			if (associationInteface.getTargetEntity() != null
					&& associationInteface.getTargetEntity().equals(dynamicEntity))
			{
				return associationInteface;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param staticEntity
	 * @param dynamicEntity
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	public static synchronized AssociationInterface addAssociationForEntities(
			EntityInterface staticEntity, EntityInterface dynamicEntity)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		AssociationInterface association = null;

		//Create source role and target role for the association
		String roleName = staticEntity.getId().toString().concat("_").concat(
				dynamicEntity.getId().toString());
		RoleInterface sourceRole = getRole(AssociationType.CONTAINTMENT, roleName,
				Cardinality.ZERO, Cardinality.ONE);
		RoleInterface targetRole = getRole(AssociationType.CONTAINTMENT, roleName,
				Cardinality.ZERO, Cardinality.MANY);

		//Create association with the created source and target roles.
		association = getAssociation(dynamicEntity, AssociationDirection.SRC_DESTINATION, roleName,
				sourceRole, targetRole);

		//Create constraint properties for the created association.
		ConstraintPropertiesInterface constProperts = getConstraintProperties(staticEntity,
				dynamicEntity);
		association.setConstraintProperties(constProperts);

		//Add association to the static entity and save it.
		staticEntity.addAssociation(association);

		return association;

	}

	/**
	 * This method populates the map which contains an entity as key and all its child entities as value
	 * @param entityGroup
	 */
	private static void populateEntityVsChildEntitiesMap(EntityGroupInterface entityGroup)
	{
		Collection<EntityInterface> entityCollection = entityGroup.getEntityCollection();
		List<EntityInterface> childEntites = null;
		for (EntityInterface entity : entityCollection)
		{
			if (entity.getParentEntity() != null)
			{
				if (entyVsChldEnts.get(entity.getParentEntity().getId()) == null)
				{
					childEntites = new ArrayList<EntityInterface>();
				}
				else
				{
					childEntites = entyVsChldEnts.get(entity.getParentEntity().getId());
				}
				if (!childEntites.contains(entity))
				{
					childEntites.add(entity);
				}
				entyVsChldEnts.put(entity.getParentEntity().getId(), childEntites);
			}
		}
		isMapPopulated = true;
	}

	/**
	 * @param entityGroupColl
	 * @return
	 */
	public static boolean checkBaseEntityGroup(Collection<EntityGroupInterface> entityGroupColl)
	{
		for (EntityGroupInterface eg : entityGroupColl)
		{
			if (eg.getId().intValue() == Constants.CATISSUE_ENTITY_GROUP)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * @param dynamicEntity
	 * @param staticEntity
	 * @param associationId
	 * @param processedPathList
	 * @param isEntGrpSysGnrted
	 * @throws BizLogicException
	 * @throws DynamicExtensionsSystemException 
	 */
	public static void addQueryPathsForAllAssociatedEntities(EntityInterface dynamicEntity,
			EntityInterface staticEntity, Long associationId, Set<PathObject> processedPathList,
			boolean isEntGrpSysGnrted, JDBCDAO jdbcdao) throws BizLogicException,
			DynamicExtensionsSystemException
	{
		if (staticEntity != null)
		{
			PathObject pathObject = new PathObject();
			pathObject.setSourceEntity(staticEntity);
			pathObject.setTargetEntity(dynamicEntity);

			if (processedPathList.contains(pathObject))
			{
				return;
			}
			else
			{
				processedPathList.add(pathObject);
			}

			boolean ispathAdded = isDirectPathAdded(staticEntity.getId(), dynamicEntity.getId(),
					jdbcdao);

			if (!ispathAdded)
			{
				LOGGER.info(" " + staticEntity.getName() + " --> " + dynamicEntity.getName());
				AnnotationUtil.addPathsForQuery(staticEntity.getId(), dynamicEntity, associationId,
						isEntGrpSysGnrted, jdbcdao);
			}
		}

		Collection<AssociationInterface> associations = dynamicEntity.getAssociationCollection();
		for (AssociationInterface association : associations)
		{
			addQueryPathsForAllAssociatedEntities(association.getTargetEntity(), dynamicEntity,
					association.getId(), processedPathList, isEntGrpSysGnrted, jdbcdao);
		}
	}

	/**
	 * @param dynamicEntity
	 * @throws DynamicExtensionsSystemException
	 */
	public static void addInheritancePathforSystemGenerated(EntityInterface dynamicEntity)
			throws DynamicExtensionsSystemException
	{
		try
		{
			Long maxPathId = getMaxId("path_id", "path");
			maxPathId += 1;
			addInheritancePaths(maxPathId, dynamicEntity);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/*
	 * @param staticEntityId
	 * @param dynamicEntityId
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	public static boolean isPathAdded(Long staticEntityId, Long dynamicEntityId, JDBCDAO jdbcdao)/*, Long deAssociationId*/
	throws DynamicExtensionsSystemException
	{
		boolean ispathAdded = false; // NOPMD - DD anomaly
		ResultSet resultSet = null; // NOPMD - DD anomaly
		try
		{
			String checkForPathQuery = "select path_id from path where FIRST_ENTITY_ID = "
					+ staticEntityId + " and LAST_ENTITY_ID = " + dynamicEntityId;
			resultSet = jdbcdao.getQueryResultSet(checkForPathQuery);
			if (resultSet != null)
			{
				while (resultSet.next())
				{
					ispathAdded = true;
					break;
				}
			}
			if (resultSet != null)
			{
				jdbcdao.closeStatement(resultSet);
			}
		}
		catch (SQLException e)
		{
			throw new DynamicExtensionsSystemException("SQL Exception while adding paths.", e);
		}
		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException("DAOException in adding paths.", e);
		}

		return ispathAdded;
	}

	/**
	 * This method will check about Direct path between two entities.
	 * if intermediate paths does not concatenated then it will return true 
	 * @param staticEntityId
	 * @param dynamicEntityId
	 * @param jdbcdao
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	public static boolean isDirectPathAdded(Long staticEntityId, Long dynamicEntityId,
			JDBCDAO jdbcdao) throws DynamicExtensionsSystemException
	{
		boolean ispathAdded = false; // NOPMD - DD anomaly
		ResultSet resultSet = null; // NOPMD - DD anomaly
		try
		{
			String checkForPathQuery = "select path_id from path where FIRST_ENTITY_ID = "
					+ staticEntityId + " and LAST_ENTITY_ID = " + dynamicEntityId
					+ " and intermediate_path  NOT like '%_%' ESCAPE '\\\\'";

			resultSet = jdbcdao.getQueryResultSet(checkForPathQuery);
			if (resultSet != null)
			{
				while (resultSet.next())
				{
					ispathAdded = true;
					break;
				}
			}
			if (resultSet != null)
			{
				jdbcdao.closeStatement(resultSet);
			}
		}
		catch (SQLException e)
		{
			throw new DynamicExtensionsSystemException("SQL Exception while adding paths.", e);
		}
		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException("DAOException in adding paths.", e);
		}

		return ispathAdded;
	}

	/**
	 * @param isEntityFromXmi
	 * @param dynamicEntity
	 * @param staticEntity
	 * @throws BizLogicException
	 */
	public static void addEntitiesToCache(boolean isEntityFromXmi, EntityInterface dynamicEntity,
			EntityInterface staticEntity) throws BizLogicException
	{
		if (!isEntityFromXmi)
		{
			Set<EntityInterface> entitySet = new HashSet<EntityInterface>();
			entitySet.add(dynamicEntity);
			entitySet.add(staticEntity);
			DynamicExtensionsUtility.getAssociatedEntities(dynamicEntity, entitySet);
			for (EntityInterface entity : entitySet)
			{
				EntityCache.getInstance().addEntityToCache(entity);
			}

			Connection conn = null; // NOPMD - DD anomaly
			try
			{
				InitialContext ctx = new InitialContext();
				String DS_JNDI_NAME = "java:/catissuecore";
				DataSource datasource = (DataSource) ctx.lookup(DS_JNDI_NAME);
				conn = datasource.getConnection();
				PathFinder.getInstance(conn).refreshCache(conn, true);
			}
			catch (Exception e)
			{
				System.out.println("Exception while refreshing cache");
			}
			finally
			{
				try
				{
					conn.close();
				}
				catch (HibernateException e)
				{
					System.out.println("Exception while closing connection");
				}
				catch (SQLException e)
				{
					System.out.println("Exception while closing connection");
				}
			}

		}
	}

	/**
	 * @param staticEntity
	 * @param dynamicEntity
	 * @return
	 */
	private static ConstraintPropertiesInterface getConstraintProperties(
			EntityInterface staticEntity, EntityInterface dynamicEntity)
	{
		ConstraintPropertiesInterface constprop = DomainObjectFactory.getInstance()
				.createConstraintProperties();
		constprop.setName(dynamicEntity.getTableProperties().getName());
		for (AttributeInterface attribute : staticEntity.getPrimaryKeyAttributeCollection())
		{
			constprop.getTgtEntityConstraintKeyProperties().getTgtForiegnKeyColumnProperties()
					.setName(
							"DYEXTN_AS_" + staticEntity.getId().toString() + "_"
									+ dynamicEntity.getId().toString());
			constprop.getTgtEntityConstraintKeyProperties().setSrcPrimaryKeyAttribute(attribute);

		}
		constprop.getSrcEntityConstraintKeyPropertiesCollection().clear();
		return constprop;
	}

	/**
	 * @param targetEntity
	 * @param assonDirectn
	 * @param assoName
	 * @param sourceRole
	 * @param targetRole
	 * @return
	 * @throws DynamicExtensionsSystemException 
	 */
	private static AssociationInterface getAssociation(EntityInterface targetEntity,
			AssociationDirection assonDirectn, String assoName, RoleInterface sourceRole,
			RoleInterface targetRole) throws DynamicExtensionsSystemException
	{
		AssociationInterface association = DomainObjectFactory.getInstance().createAssociation();
		association.setTargetEntity(targetEntity);
		association.setAssociationDirection(assonDirectn);
		association.setName(assoName);
		association.setSourceRole(sourceRole);
		association.setTargetRole(targetRole);
		return association;
	}

	/**
	 * @param associationType associationType
	 * @param name name
	 * @param minCard  minCard
	 * @param maxCard maxCard
	 * @return  RoleInterface
	 */
	private static RoleInterface getRole(AssociationType associationType, String name,
			Cardinality minCard, Cardinality maxCard)
	{
		RoleInterface role = DomainObjectFactory.getInstance().createRole();
		role.setAssociationsType(associationType);
		role.setName(name);
		role.setMinimumCardinality(minCard);
		role.setMaximumCardinality(maxCard);
		return role;
	}

	/**
	 * @param staticEntityId
	 * @param dynamicEntityId
	 * @param deAssociationID
	 * @param isEntGrpSysGenrtd
	 * @throws DynamicExtensionsSystemException 
	 */
	public static void addPathsForQuery(Long staticEntityId, EntityInterface dynamicEntity,
			Long deAssociationID, boolean isEntGrpSysGenrtd, JDBCDAO jdbcdao)
			throws DynamicExtensionsSystemException
	{
		Long maxPathId = getMaxId("path_id", "path");
		maxPathId += 1;
		insertNewPaths(maxPathId, staticEntityId, dynamicEntity, deAssociationID,
				isEntGrpSysGenrtd, jdbcdao);
	}

	/**
	 * @param maxPathId
	 * @param staticEntityId
	 * @param dynamicEntityId
	 * @param deAssociationID
	 * @param isEntGrpSysGenrtd
	 * @throws DynamicExtensionsSystemException 
	 */
	private static void insertNewPaths(Long maxPathId, Long staticEntityId,
			EntityInterface dynamicEntity, Long deAssociationID, boolean isEntGrpSysGenrtd,
			JDBCDAO jdbcdao) throws DynamicExtensionsSystemException
	{
		Long intraModAssonId = getMaxId("ASSOCIATION_ID", "ASSOCIATION");
		intraModAssonId += 1;
		String appName = CommonServiceLocator.getInstance().getAppName();

		try
		{
			String associationQuery = "insert into ASSOCIATION (ASSOCIATION_ID, ASSOCIATION_TYPE) values ("
					+ intraModAssonId
					+ ","
					+ edu.wustl.cab2b.server.path.AssociationType.INTRA_MODEL_ASSOCIATION
							.getValue() + ")";
			String intraModelQuery = "insert into INTRA_MODEL_ASSOCIATION (ASSOCIATION_ID, DE_ASSOCIATION_ID) values ("
					+ intraModAssonId + "," + deAssociationID + ")";
			String directPathQuery = pathInsertStatement + maxPathId + "," + staticEntityId + ","
					+ intraModAssonId + "," + dynamicEntity.getId() + ")";

			List<String> list = new ArrayList<String>();
			list.add(associationQuery);
			list.add(intraModelQuery);
			list.add(directPathQuery);

			//adding paths for derived entities of static entity
			maxPathId = addChildPathsStaticEntity(maxPathId, staticEntityId, dynamicEntity.getId(),
					intraModAssonId.toString(), list, jdbcdao);

			//adding paths for derived entities of dynamic entity
			maxPathId = addChildPathsDynamicEntity(maxPathId, staticEntityId,
					dynamicEntity.getId(), intraModAssonId.toString(), list, jdbcdao);

			executeQuery(list);
			maxPathId += 1;
			if (!isEntGrpSysGenrtd)
			{
				maxPathId = addIndirectPaths(maxPathId, staticEntityId, dynamicEntity.getId(),
						intraModAssonId, jdbcdao);
			}

			addInheritancePaths(maxPathId, dynamicEntity);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		//logger.error("Exception while inserting new paths",e);

	}

	/**
	 * This method adds paths from derived entities of static entity to derived entities of dynamic entity
	 * @param maxPathId
	 * @param staticEntityId
	 * @param dynChldEntityId
	 * @param intraModAssonId
	 * @param list
	 * @return
	 * @throws DynamicExtensionsSystemException 
	 */
	private static Long addPathsChildStaticChildDynamic(Long maxPathId, Long staticEntityId,
			Long dynChldEntityId, String intraModAssonId, List<String> list, JDBCDAO jdbcdao)
			throws DynamicExtensionsSystemException
	{
		Collection<EntityInterface> statChldEntes = entyVsChldEnts.get(staticEntityId);
		if (statChldEntes != null)
		{
			for (EntityInterface staticChildEntity : statChldEntes)
			{
				boolean ispathAdded = isPathAdded(staticChildEntity.getId(), dynChldEntityId,
						jdbcdao);
				if (!ispathAdded)
				{
					maxPathId = maxPathId + 1;
					String childPathQuery = pathInsertStatement + maxPathId + ","
							+ staticChildEntity.getId() + ",'" + intraModAssonId + "',"
							+ dynChldEntityId + ")";
					list.add(childPathQuery);
				}
				maxPathId = addPathsChildStaticChildDynamic(maxPathId, staticChildEntity.getId(),
						dynChldEntityId, intraModAssonId, list, jdbcdao);

			}
		}
		return maxPathId;
	}

	/**
	 * This method adds paths for derived entities of dynamic entity
	 * @param maxPathId
	 * @param staticEntityId
	 * @param dynamicEntityId
	 * @param intraModlAssonId
	 * @param list
	 * @return maxPathId
	 * @throws DynamicExtensionsSystemException 
	 */
	private static Long addChildPathsDynamicEntity(Long maxPathId, Long staticEntityId,
			Long dynamicEntityId, String intraModlAssonId, List<String> list, JDBCDAO jdbcdao)
			throws DynamicExtensionsSystemException
	{
		if (recordEntryId != null && staticEntityId.compareTo(recordEntryId) != 0)
		{
			Collection<EntityInterface> childEntities = entyVsChldEnts.get(dynamicEntityId);
			boolean ispathAdded = false;
			if (childEntities != null)
			{
				for (EntityInterface childEntity : childEntities)
				{
					ispathAdded = isPathAdded(staticEntityId, childEntity.getId(), jdbcdao);
					if (!ispathAdded)
					{
						maxPathId = maxPathId + 1;
						String childPathQuery = pathInsertStatement + maxPathId + ","
								+ staticEntityId + ",'" + intraModlAssonId + "',"
								+ childEntity.getId() + ")";
						list.add(childPathQuery);
					}
					// add paths from derived entities of static entity to derived entities of dynamic entity
					maxPathId = addPathsChildStaticChildDynamic(maxPathId, staticEntityId,
							childEntity.getId(), intraModlAssonId.toString(), list, jdbcdao);

					maxPathId = addChildPathsDynamicEntity(maxPathId, staticEntityId, childEntity
							.getId(), intraModlAssonId, list, jdbcdao);
				}
			}
		}

		return maxPathId;
	}

	/**
	 * This method adds paths for derived entities of static entity
	 * @param maxPathId
	 * @param staticEntityId
	 * @param dynamicEntityId
	 * @param intrModAssnId
	 * @param list
	 * @return maxPathId
	 * @throws DynamicExtensionsSystemException 
	 */
	private static Long addChildPathsStaticEntity(Long maxPathId, Long staticEntityId,
			Long dynamicEntityId, String intrModAssnId, List<String> list, JDBCDAO jdbcdao)
			throws DynamicExtensionsSystemException
	{

		Collection<EntityInterface> childEntities = entyVsChldEnts.get(staticEntityId);
		boolean ispathAdded = false;
		if (childEntities != null)
		{
			for (EntityInterface childEntity : childEntities)
			{
				ispathAdded = isPathAdded(childEntity.getId(), dynamicEntityId, jdbcdao);
				if (!ispathAdded)
				{
					maxPathId++;
					String childPathQuery = pathInsertStatement + maxPathId + ","
							+ childEntity.getId() + ",'" + intrModAssnId + "'," + dynamicEntityId
							+ ")";
					list.add(childPathQuery);
				}
				maxPathId = addChildPathsStaticEntity(maxPathId, childEntity.getId(),
						dynamicEntityId, intrModAssnId, list, jdbcdao);
			}
		}
		return maxPathId;
	}

	/**
	 * This method replicates paths of parent entity for derived entity
	 * @param maxPathId
	 * @param dynamicEntityId
	 * @param conn
	 * @throws DynamicExtensionsSystemException 
	 * @throws DynamicExtensionsApplicationException 
	 */
	private static void addInheritancePaths(Long maxPathId, EntityInterface entity)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		ResultSet resultSet = null; // NOPMD - DD anomaly
		try
		{
			//This map is added because the following algo creates multiple paths between same entities
			//The map will contains only single unique path between entities
			Map<String, Object> mapQuery = new HashMap<String, Object>();
			List<String> query = new ArrayList<String>();
			String sql = "";
			String intermediatePath = "";
			Long last_entity_id = null;
			Long first_entity_id = null;
			boolean ispathAdded = false;
			String appName = CommonServiceLocator.getInstance().getAppName();
			IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
			JDBCDAO jdbcdao = null;

			while (entity.getParentEntity() != null)
			{
				try
				{
					jdbcdao = daoFactory.getJDBCDAO();
					jdbcdao.openSession(null);
					//replicate outgoing paths of parent entity (outgoing associations)
					Collection<AssociationInterface> allAssociations = entity.getParentEntity()
							.getAllAssociations();

					for (AssociationInterface association : allAssociations)
					{
						intermediatePath = "";
						sql = "select INTERMEDIATE_PATH,LAST_ENTITY_ID from path where FIRST_ENTITY_ID="
								+ association.getEntity().getId();
						resultSet = jdbcdao.getQueryResultSet(sql);
						List<ArrayList<String>> idlist = new ArrayList<ArrayList<String>>();
						while (resultSet.next())
						{
							ArrayList<String> temp = new ArrayList<String>();
							temp.add(0, resultSet.getString(1));
							temp.add(1, Long.toString(resultSet.getLong(2)));
							idlist.add(temp);
						}
						jdbcdao.closeStatement(resultSet);
						for (int cnt = 0; cnt < idlist.size(); cnt++)
						{
							ArrayList<String> temp = idlist.get(cnt);
							intermediatePath = temp.get(0);
							last_entity_id = Long.valueOf(temp.get(1));
							ispathAdded = isPathAdded(entity.getId(), last_entity_id, jdbcdao);
							if (!ispathAdded)
							{
								sql = "INSERT INTO path values(" + maxPathId + "," + entity.getId()
										+ ",'" + intermediatePath + "'," + last_entity_id + ")";
								String uniquepathStr = entity.getId() + "_" + intermediatePath
										+ "_" + last_entity_id;
								if (!mapQuery.containsKey(uniquepathStr))
								{
									mapQuery.put(uniquepathStr, null);
									query.add(sql);
									maxPathId++;
								}
							}
						}

					}

					// replicate incoming paths of parent entity (incoming associations)
					intermediatePath = "";
					sql = "select FIRST_ENTITY_ID,INTERMEDIATE_PATH from path where LAST_ENTITY_ID="
							+ entity.getParentEntity().getId();

					resultSet = jdbcdao.getQueryResultSet(sql);
					List<ArrayList<String>> idlist = new ArrayList<ArrayList<String>>();
					while (resultSet.next())
					{

						ArrayList<String> temp = new ArrayList<String>();
						temp.add(0, Long.toString(resultSet.getLong(1)));
						temp.add(1, resultSet.getString(2));
						idlist.add(temp);
					}
					jdbcdao.closeStatement(resultSet);
					for (int cnt = 0; cnt < idlist.size(); cnt++)
					{
						ArrayList<String> temp = idlist.get(cnt);
						first_entity_id = Long.valueOf(temp.get(0));
						intermediatePath = temp.get(1);
						if (first_entity_id.compareTo(recordEntryId) != 0)
						{

							ispathAdded = isPathAdded(first_entity_id, entity.getId(), jdbcdao);
							if (!ispathAdded)
							{
								sql = "INSERT INTO path values(" + maxPathId + ","
										+ first_entity_id + ",'" + intermediatePath + "',"
										+ entity.getId() + ")";
								String uniquepathStr = first_entity_id + "_" + intermediatePath
										+ "_" + entity.getId();

								if (!mapQuery.containsKey(uniquepathStr))
								{
									mapQuery.put(uniquepathStr, null);
									query.add(sql);
									maxPathId++;
								}
							}
						}
					}
				}
				finally
				{

					jdbcdao.closeSession();
				}
				entity = entity.getParentEntity();

			}//while

			executeQuery(query);

		}
		catch (SQLException e)
		{
			throw new DynamicExtensionsSystemException(
					"SQL Exception while adding paths for derived entity.", e);
		}
		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException(
					"DAO Exception while adding paths for derived entity.", e);
		}
	}

	/**
	 * @param maxPathId
	 * @param staticEntityId
	 * @param dynamicEntityId
	 * @param intrModAssonId
	 * @param conn
	 * @throws DynamicExtensionsSystemException 
	 */
	private static Long addIndirectPaths(Long maxPathId, Long staticEntityId, Long dynamicEntityId,
			Long intrModAssonId, JDBCDAO jdbcdao) throws DynamicExtensionsSystemException

	{
		ResultSet resultSet = null; // NOPMD - DD anomaly
		List<String> list = new ArrayList<String>();
		try
		{

			if (recordEntryId != null && staticEntityId.compareTo(recordEntryId) != 0)
			{
				String query = "select FIRST_ENTITY_ID,INTERMEDIATE_PATH from path where LAST_ENTITY_ID="
						+ staticEntityId;

				resultSet = jdbcdao.getQueryResultSet(query);
				while (resultSet.next())
				{

					Long firstEntityId = resultSet.getLong(1);
					String path = resultSet.getString(2);
					path = path.concat("_").concat(intrModAssonId.toString());

					String pathQuery = pathInsertStatement + maxPathId + "," + firstEntityId + ",'"
							+ path + "'," + dynamicEntityId + ")";

					list.add(pathQuery);
					maxPathId = addChildPathsDynamicEntity(maxPathId, firstEntityId,
							dynamicEntityId, path, list, jdbcdao);
					maxPathId++;
				}
				jdbcdao.closeStatement(resultSet);
				executeQuery(list);
			}

		}
		catch (SQLException e)
		{
			throw new DynamicExtensionsSystemException(
					"SQL Exception while adding indirect paths.", e);
		}
		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException(
					"DAO Exception while adding indirect paths.", e);
		}
		return maxPathId;
	}

	/**
	 * @param conn
	 * @param queryList
	 */

	private static void executeQuery(List<String> queryList)
	{
		JDBCDAO jdbcdao = null;
		try
		{
			String appName = CommonServiceLocator.getInstance().getAppName();
			IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);

			jdbcdao = daoFactory.getJDBCDAO();
			jdbcdao.openSession(null);

			for (String query : queryList)
			{
				try
				{
					jdbcdao.executeUpdate(query);
				}
				catch (DAOException e)
				{

					e.printStackTrace();
				}

			}
		}
		catch (DAOException e)
		{

			e.printStackTrace();
		}
		finally
		{

			try
			{
				jdbcdao.commit();
				jdbcdao.closeSession();
			}
			catch (DAOException e)
			{
				e.printStackTrace();
			}
		}

	}

	/**
	 * @param columnName
	 * @param tableName
	 * @return
	 */
	private static Long getMaxId(String columnName, String tableName)
	{
		String query = "select max(" + columnName + ") from " + tableName;
		ResultSet resultSet = null;
		Long maxId = null;
		String appName = CommonServiceLocator.getInstance().getAppName();
		IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
		JDBCDAO jdbcdao = null;
		try
		{
			jdbcdao = daoFactory.getJDBCDAO();
			jdbcdao.openSession(null);
			resultSet = jdbcdao.getQueryResultSet(query);
			resultSet.next();
			maxId = resultSet.getLong(1);
			jdbcdao.closeStatement(resultSet);
		}
		catch (Exception e)
		{
			System.out.println("Exception while getting max id");
		}
		finally
		{
			try
			{
				jdbcdao.closeSession();
			}
			catch (DAOException e)
			{
				System.out.println("DAOException while closing database objects");
			}
		}
		return maxId;
	}

	/**
	 * @param entityName
	 * @return
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	public static Long getEntityId(String entityName, HibernateDAO... hibernateDAO)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		Long entityId = Long.valueOf(0);

		if (entityName != null)
		{
			EntityManagerInterface entityManager = EntityManager.getInstance();
			EntityInterface entity;

			if (hibernateDAO != null && hibernateDAO.length > 0)
			{
				entity = entityManager.getEntityByName(entityName, hibernateDAO[0]);
			}
			else
			{
				entity = entityManager.getEntityByName(entityName);
			}

			if (entity != null)
			{
				entityId = entity.getId();
			}
		}

		return entityId;
	}

	/**
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	public static void setRecordEntryId(HibernateDAO... hibernateDAO)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		recordEntryId = getEntityId("edu.wustl.clinportal.domain.RecordEntry", hibernateDAO);
	}

}