/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */


package edu.wustl.clinportal.uploadmetadata;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.clinportal.bizlogic.AnnotationUtil;
import edu.wustl.common.beans.NameValueBean;
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
public class UpdateCSRToEntityPath
{

	private static Long CSR_ENTITY_ID;
	private static Long RECENTRY_ENTITY_ID;
	private static Long EVENTENTRY_ENTITY_ID;

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}
	
	//Logger
	private static final Logger LOGGER = Logger.getCommonLogger(UpdateCSRToEntityPath.class);

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		LOGGER.info("---UpdateCSRToEntityPath start");
		addCuratedPathsFromCSRToAllEntities(null, null);
		LOGGER.info("---UpdateCSRToEntityPath finish");
	}

	/**
	 * @param entityGroupId
	 * @param newEntitiesIds 
	 */
	public static void addCuratedPathsFromCSRToAllEntities(List<Long> newEntitiesIds, HibernateDAO hibernatedao)
	{
		String staticEntAssnId;
		
		try
		{
			String csrEntName = "edu.wustl.clinportal.domain.ClinicalStudyRegistration";
			String eventEntryEntName = "edu.wustl.clinportal.domain.EventVisit";
			String recEntryEntName = "edu.wustl.clinportal.domain.RecordEntry";

			EntityManagerInterface entityManager = EntityManager.getInstance();
			
			CSR_ENTITY_ID = entityManager.getEntityId(csrEntName);
			EVENTENTRY_ENTITY_ID = entityManager.getEntityId(eventEntryEntName);
			RECENTRY_ENTITY_ID = entityManager.getEntityId(recEntryEntName);

			staticEntAssnId = getStaticEntityAssnIds(hibernatedao);
			addPathForEntityGroup(staticEntAssnId, newEntitiesIds);
		}
		catch (Exception e)
		{
			LOGGER.info("error in addCuratedPathsFromCSRToAllEntities" + e.getMessage());
		}
	}

	/**
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 * @throws DAOException
	 * @throws SQLException
	 */
	private static String getStaticEntityAssnIds(HibernateDAO hibernatedao) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException, DAOException, SQLException
	{
		Long deAssnId = null;
		
		// This will get association id between 'CSR' and 'EventEntry'.
		String targetRoleName = "eventEntryCollection";
		
		Collection<AssociationInterface> associations = null;
		EntityManagerInterface entityManager = EntityManager.getInstance();
		
		if (hibernatedao != null)
		{
			associations = entityManager.getAssociations(CSR_ENTITY_ID, EVENTENTRY_ENTITY_ID, hibernatedao);
		}
		else
		{
			associations = entityManager.getAssociations(CSR_ENTITY_ID, EVENTENTRY_ENTITY_ID);
		}
		
		for (AssociationInterface association : associations)
		{
			if (association.getTargetRole().getName().equalsIgnoreCase(targetRoleName))
			{
				deAssnId = association.getId();
				break;
			}
		}
		
		String strIntramoelAssnId = getIntraModelAssonId(deAssnId).toString();
		
		// This will get association id between 'EventEntry' and 'RecordEntry'.			
		targetRoleName = "recordEntryCollection";
		
		if (hibernatedao != null)
		{
			associations = entityManager.getAssociations(EVENTENTRY_ENTITY_ID, RECENTRY_ENTITY_ID, hibernatedao);
		}
		else
		{
			associations = entityManager.getAssociations(EVENTENTRY_ENTITY_ID, RECENTRY_ENTITY_ID);
		}
		
		for (AssociationInterface association : associations)
		{
			if (association.getTargetRole().getName().equalsIgnoreCase(targetRoleName))
			{
				deAssnId = association.getId();
				break;
			}
		}
		
		String finalIntraModelAssnId = strIntramoelAssnId + "_" + getIntraModelAssonId(deAssnId).toString();
		
		return finalIntraModelAssnId;
	}

	/**
	 * @param strDEAssnId
	 * @return
	 * @throws DAOException
	 * @throws SQLException
	 */
	private static Long getIntraModelAssonId(Long deAssnId) throws DAOException, SQLException
	{
		DAOConfigFactory daocConfigFactory = DAOConfigFactory.getInstance();
		IDAOFactory daoFactory = daocConfigFactory.getDAOFactory("dynamicExtention");
		JDBCDAO jdbcdao = null;
		jdbcdao = daoFactory.getJDBCDAO();
		jdbcdao.openSession(null);
		String sql = "select ASSOCIATION_ID from  intra_model_association where DE_ASSOCIATION_ID='"
				+ deAssnId + "'";
		ResultSet resultSet = jdbcdao.getQueryResultSet(sql);
		long intramodelId = 0;
		if (resultSet.next())
		{
			intramodelId = resultSet.getLong(1);

		}
		jdbcdao.closeStatement(resultSet);
		jdbcdao.closeSession();
		return intramodelId;
	}

	/**
	 * @param strAssnId
	 * @param entityGroupId 
	 * @param newEntitiesIds 
	 * @throws SQLException 
	 */
	static void addPathForEntityGroup(String strAssnId, List<Long> newEntitiesIds)
	{
		try
		{
			if (newEntitiesIds != null)
			{
				insertPathsForEntityGroup(strAssnId, null, newEntitiesIds);
			}
			else
			{
				//This will add path from CSR to all entities of all entity group
				//This is one-time task ,called from upgrade ant task
				Collection<NameValueBean> entityGrpLst = EntityManager.getInstance()
						.getAllEntityGroupBeans();
				NameValueBean groupBean = null;
				Iterator<NameValueBean> iterator = entityGrpLst.iterator();
				while (iterator.hasNext())
				{
					groupBean = iterator.next();
					if (groupBean != null)
					{
						LOGGER.info("Setting path for entity group==" + groupBean.getValue() + "=="
								+ groupBean.getName());
					}
					insertPathsForEntityGroup(strAssnId, Long.valueOf(groupBean.getValue()), null);
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.info("error addPathForEntityGroup" + e.getMessage());
		}

	}

	/**
	 * @param strAssnId
	 * @param entityGroupId
	 * @param newEntitiesIds 
	 * @throws DynamicExtensionsSystemException
	 * @throws DAOException
	 * @throws SQLException
	 */
	private static void insertPathsForEntityGroup(String strAssnId, Long entityGroupId,
			List<Long> newEntitiesIds) throws DynamicExtensionsSystemException, DAOException,
			SQLException
	{
		Collection<Long> entityIds = null;
		if (newEntitiesIds != null)
		{
			entityIds = newEntitiesIds;
		}
		else
		{
			//If newEntitiesIds is not given,then add paths from CSR to all entities within e.group
			entityIds = EntityManager.getInstance().getAllEntityIdsForEntityGroup(entityGroupId);
			LOGGER.info("Adding paths for all entities of group.size of list: " + entityIds.size());
		}
		insertPathsForEntities(strAssnId, entityIds);

	}

	/**
	 * @param strAssnId
	 * @param entityIds
	 * @throws DynamicExtensionsSystemException
	 * @throws DAOException
	 * @throws SQLException
	 */
	private static void insertPathsForEntities(String strAssnId, Collection<Long> entityIds)
			throws DynamicExtensionsSystemException, DAOException, SQLException
	{
		LOGGER.info("Adding paths for " + entityIds.size() + " entities....");
		DAOConfigFactory daocConfigFactory = DAOConfigFactory.getInstance();
		IDAOFactory daoFactory = daocConfigFactory.getDAOFactory("dynamicExtention");
		JDBCDAO jdbcdao = null;
		jdbcdao = daoFactory.getJDBCDAO();
		jdbcdao.openSession(null);
		for (Long entityId : entityIds)
		{
			if (!AnnotationUtil.isPathAdded(CSR_ENTITY_ID, entityId, jdbcdao))
			{
				// It is an intermediate path from record entry to given entity.
				String interPathAssn = getExistingInterMediatePath(RECENTRY_ENTITY_ID, entityId,
						jdbcdao);
				if (interPathAssn != null)
				{
					String curatePathString = strAssnId + "_" + interPathAssn;
					// Add a path between csr_deform.
					insertPath(CSR_ENTITY_ID, entityId, curatePathString, jdbcdao);
				}
			}
		}
		
		jdbcdao.commit();
		jdbcdao.closeSession();
	}

	/**
	 * @param recEntryEntityId
	 * @param entityId
	 * @param jdbcdao 
	 * @return
	 * @throws DAOException
	 * @throws SQLException
	 */
	private static String getExistingInterMediatePath(Long recEntryEntityId, Long entityId,
			JDBCDAO jdbcdao) throws DAOException, SQLException
	{
		String selSQL = "select intermediate_path  from path where first_entity_id ='"
				+ recEntryEntityId + "' and last_entity_id = '" + entityId + "'";
		ResultSet resultSet = jdbcdao.getQueryResultSet(selSQL);
		String interPathid = null;
		if (resultSet.next())
		{
			interPathid = resultSet.getString(1);
		}
		jdbcdao.closeStatement(resultSet);
		return interPathid;
	}

	/**
	 * @param firstEntityId
	 * @param secondEntityId
	 * @param newinterPathid
	 * @throws DAOException
	 * @throws SQLException
	 * @throws DynamicExtensionsSystemException
	 */
	static void insertPath(Long firstEntityId, Long secondEntityId, String newinterPathid,
			JDBCDAO jdbcdao) throws DAOException, SQLException, DynamicExtensionsSystemException
	{
		if (!AnnotationUtil.isPathAdded(firstEntityId, secondEntityId, jdbcdao))
		{
			String sql;
			long nextIdPath = getNextId("path", "PATH_ID", jdbcdao);
			sql = "insert into path values (" + nextIdPath + "," + firstEntityId + ",'"
					+ newinterPathid + "'," + secondEntityId + ")";
			jdbcdao.executeUpdate(sql);

		}
	}

	/**
	 * @param tablename
	 * @param coloumn
	 * @param jdbcdao
	 * @return
	 * @throws SQLException
	 * @throws DAOException
	 */
	private static Long getNextId(String tablename, String coloumn, JDBCDAO jdbcdao)
			throws SQLException, DAOException
	{
		String sql = "select max(" + coloumn + ") from " + tablename;
		ResultSet resultSet = jdbcdao.getQueryResultSet(sql);

		long nextId = 0;
		if (resultSet.next())
		{
			long maxId = resultSet.getLong(1);
			nextId = maxId + 1;
		}
		jdbcdao.closeStatement(resultSet);
		return nextId;
	}
}