/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */


package edu.wustl.clinportal.querysuite.metadata;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationType;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.dao.DAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;

public class AddAssociations
{

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}
	//Logger
	private transient Logger LOGGER = Logger.getCommonLogger(AddAssociations.class);

	/**
	 * @param entityName
	 * @param associatedEntity
	 * @param associationName
	 * @param associationType
	 * @param roleName
	 * @param isSwap
	 * @param roleNameTable
	 * @param srcAssociationId
	 * @param trgtAsstnId
	 * @param maxCardinality
	 * @param isSystemGenerated
	 * @param direction
	 * @throws SQLException
	 * @throws IOException
	 * @throws DAOException
	 * @throws DynamicExtensionsSystemException 
	 */
	public void addAssociation(String entityName, String associatedEntity, String associationName,
			String associationType, String roleName, boolean isSwap, String roleNameTable,
			String srcAssociationId, String trgtAsstnId, int maxCardinality, int isSystemGenerated,
			String direction, String... entityGrpName) throws SQLException, IOException,
			DAOException, DynamicExtensionsSystemException
	{
		DAOConfigFactory daocConfigFactory = DAOConfigFactory.getInstance();
		IDAOFactory daoFactory = daocConfigFactory.getDAOFactory("dynamicExtention");
		JDBCDAO jdbcdao = null;
		jdbcdao = daoFactory.getJDBCDAO();
		jdbcdao.openSession(null);
		String sql;

		long nextIdMetadata = getNextId("dyextn_abstract_metadata", "identifier", jdbcdao);
		long nextIdOfDERole = getNextId("dyextn_role", "identifier", jdbcdao);
		long nextIdDBProp = getNextId("dyextn_database_properties", "identifier", jdbcdao);
		long nextIDAssociation = getNextId("intra_model_association", "ASSOCIATION_ID", jdbcdao);
		long nextIdPath = getNextId("path", "PATH_ID", jdbcdao);
		long entityId = 0;
		long associatedId = 0;
		if (entityGrpName != null)
		{
			entityId = getAssociatedEntityId(entityName, jdbcdao, entityGrpName);
			associatedId = getAssociatedEntityId(associatedEntity, jdbcdao, entityGrpName);
		}
		else
		{
			entityId = getAssociatedEntityId(entityName, jdbcdao);
			associatedId = getAssociatedEntityId(associatedEntity, jdbcdao);

		}
		insertAssociationData(associationName, nextIdMetadata, entityId);
		long roleId = nextIdOfDERole;

		if (isSwap)
		{
			roleId = nextIdOfDERole + 1;
			sql = "insert into dyextn_role values (" + nextIdOfDERole + ",'" + associationType
					+ "'," + maxCardinality + ",0,'" + roleName + "')";

			jdbcdao.executeUpdate(sql);
			sql = "insert into dyextn_role values (" + roleId + ",'ASSOCIATION',1,0,'"
					+ roleNameTable + "')";
			jdbcdao.executeUpdate(sql);

		}
		if (isSwap)
		{
			sql = "insert into dyextn_association values (" + nextIdMetadata + ",'" + direction
					+ "'," + associatedId + "," + roleId + "," + nextIdOfDERole + ",1,0)";
		}
		else
		{
			long lastIdOfDERole = nextIdOfDERole - 2;
			long idOfDERole = nextIdOfDERole - 1;
			sql = "insert into dyextn_association values (" + nextIdMetadata + ",'" + direction
					+ "'," + associatedId + "," + lastIdOfDERole + "," + idOfDERole + ","
					+ isSystemGenerated + ",0)";
		}
		jdbcdao.executeUpdate(sql);
		sql = "insert into dyextn_database_properties values (" + nextIdDBProp + ",'"
				+ associationName + "')";
		jdbcdao.executeUpdate(sql);

		if (isSwap)
		{
			sql = insertConstraintPropIfSwap(srcAssociationId, trgtAsstnId, nextIdMetadata,
					nextIdDBProp);
			jdbcdao.executeUpdate(sql);

		}
		else
		{
			sql = insertConstraintPropIfNotSwap(srcAssociationId, trgtAsstnId, nextIdMetadata,
					nextIdDBProp);
			jdbcdao.executeUpdate(sql);
		}
		//--changes due to constraint properties 
		String attidsql = "select attribute_id from dyextn_entiy_composite_key_rel where entity_id ='"
				+ entityId + "'";

		ResultSet resultSet = jdbcdao.getQueryResultSet(attidsql);
		Long attrid = null;
		if (resultSet.next())
		{
			attrid = resultSet.getLong(1);
		}

		jdbcdao.closeStatement(resultSet);

		String constraintidSQL = "select identifier from dyextn_constraint_properties where association_id ='"
				+ nextIdMetadata + "'";
		ResultSet resultSet1 = jdbcdao.getQueryResultSet(constraintidSQL);
		Long constraintid = null;
		if (resultSet1.next())
		{
			constraintid = resultSet1.getLong(1);
		}
		jdbcdao.closeStatement(resultSet1);

		long nextIddeconskeypro = getNextId("DYEXTN_CONSTRAINTKEY_PROP", "identifier", jdbcdao);
		String sqldeconskeyprop = null;
		if (isSwap)
		{
			sqldeconskeyprop = "insert into DYEXTN_CONSTRAINTKEY_PROP(IDENTIFIER , PRIMARY_ATTRIBUTE_ID ,TGT_CONSTRAINT_KEY_ID)"
					+ " values (" + nextIddeconskeypro + "," + attrid + "," + constraintid + ")";

		}
		else
		{
			sqldeconskeyprop = "insert into DYEXTN_CONSTRAINTKEY_PROP(IDENTIFIER , PRIMARY_ATTRIBUTE_ID ,SRC_CONSTRAINT_KEY_ID)"
					+ " values (" + nextIddeconskeypro + "," + attrid + "," + constraintid + ")";
		}
		jdbcdao.executeUpdate(sqldeconskeyprop);
		long nextIdDBPropforCol = getNextId("dyextn_database_properties", "identifier", jdbcdao);
		String sqlDBPropforCol = "insert into dyextn_database_properties values ("
				+ nextIdDBPropforCol + ",'" + srcAssociationId + "')";
		jdbcdao.executeUpdate(sqlDBPropforCol);
		String sqlColPropforConsKey = "insert into DYEXTN_COLUMN_PROPERTIES(IDENTIFIER,CNSTR_KEY_PROP_ID) values("
				+ nextIdDBPropforCol + "," + nextIddeconskeypro + ")";
		jdbcdao.executeUpdate(sqlColPropforConsKey);

		//----
		sql = "insert into association values(" + nextIDAssociation + ",2)";
		jdbcdao.executeUpdate(sql);

		sql = "insert into intra_model_association values(" + nextIDAssociation + ","
				+ nextIdMetadata + ")";
		jdbcdao.executeUpdate(sql);

		sql = "insert into path values (" + nextIdPath + "," + entityId + "," + nextIDAssociation
				+ "," + associatedId + ")";
		jdbcdao.executeUpdate(sql);

		jdbcdao.commit();
		jdbcdao.closeSession();

	}

	/**
	 * @param firstEntity
	 * @param secondEntity
	 * @param interEntity
	 * @throws DAOException
	 * @throws SQLException
	 * @throws DynamicExtensionsSystemException
	 */
	public void addCuratedPath(String firstEntity, String secondEntity, String interEntity)
			throws DAOException, SQLException, DynamicExtensionsSystemException
	{

		DAOConfigFactory daocConfigFactory = DAOConfigFactory.getInstance();
		IDAOFactory daoFactory = daocConfigFactory.getDAOFactory("dynamicExtention");
		Long entityGroupId = EntityManager.getInstance().getEntityGroupId(
				Constants.CLINPORTAL_ENTITY_GROUP_NAME);
		Long firstEntityId = EntityManager.getInstance().getEntityId(firstEntity, entityGroupId);
		Long secondEntityId = EntityManager.getInstance().getEntityId(secondEntity, entityGroupId);
		Long interEntityId = EntityManager.getInstance().getEntityId(interEntity, entityGroupId);

		JDBCDAO jdbcdao = null;
		jdbcdao = daoFactory.getJDBCDAO();
		jdbcdao.openSession(null);
		if (!isPathAdded(firstEntityId, secondEntityId, jdbcdao))
		{
			String selSQL = "select intermediate_path  from path where first_entity_id ='"
					+ firstEntityId + "' and last_entity_id = '" + interEntityId + "'";

			ResultSet resultSet = jdbcdao.getQueryResultSet(selSQL);
			Long interPathid = null;
			if (resultSet.next())
			{
				interPathid = resultSet.getLong(1);
			}
			jdbcdao.closeStatement(resultSet);

			String selinterSQL = "select intermediate_path  from path where first_entity_id ='"
					+ interEntityId + "' and last_entity_id = '" + secondEntityId + "'";

			ResultSet resultSet1 = jdbcdao.getQueryResultSet(selinterSQL);
			Long interPathid1 = null;
			if (resultSet1.next())
			{
				interPathid1 = resultSet1.getLong(1);
			}
			jdbcdao.closeStatement(resultSet1);

			String newinterPathid = interPathid + "_" + interPathid1;

			String sql;
			long nextIdPath = getNextId("path", "PATH_ID", jdbcdao);
			sql = "insert into path values (" + nextIdPath + "," + firstEntityId + ",'"
					+ newinterPathid + "'," + secondEntityId + ")";

			jdbcdao.executeUpdate(sql);
			jdbcdao.commit();
		}
		jdbcdao.closeSession();
	}

	/**
	 * @param staticEntityId
	 * @param dynamicEntityId
	 * @param jdbcdao
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	private static boolean isPathAdded(Long staticEntityId, Long dynamicEntityId, JDBCDAO jdbcdao)
			throws DynamicExtensionsSystemException
	{
		boolean ispathAdded = false;
		ResultSet resultSet = null;
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
	 * @param associationName
	 * @param nextIdMetadata
	 * @param entityId
	 * @throws SQLException
	 * @throws DAOException
	 */
	private void insertAssociationData(String associationName, Long nextIdMetadata, Long entityId)
			throws SQLException, DAOException
	{
		String sql;

		DAOConfigFactory daocConfigFactory = DAOConfigFactory.getInstance();
		IDAOFactory daoFactory = daocConfigFactory.getDAOFactory("dynamicExtention");
		JDBCDAO jdbcdao = null;
		jdbcdao = daoFactory.getJDBCDAO();
		jdbcdao.openSession(null);
		sql = "insert into dyextn_abstract_metadata values (" + nextIdMetadata
				+ ",null,null,null,'" + associationName + "',null)";
		jdbcdao.executeUpdate(sql);

		sql = "INSERT INTO DYEXTN_BASE_ABSTRACT_ATTRIBUTE values(" + nextIdMetadata + ")";
		jdbcdao.executeUpdate(sql);

		sql = "insert into dyextn_attribute values (" + nextIdMetadata + "," + entityId + ")";
		jdbcdao.executeUpdate(sql);

		jdbcdao.commit();
		jdbcdao.closeSession();

	}

	/**
	 * @param srcAssociationId
	 * @param targetId
	 * @param nextIdMetadata
	 * @param nextIdDbProp
	 * @return
	 */
	private String insertConstraintPropIfNotSwap(String srcAssociationId, String targetId,
			long nextIdMetadata, long nextIdDbProp)
	{
		String sql;
		if (targetId == null)
		{
			sql = "insert into dyextn_constraint_properties (IDENTIFIER,SOURCE_ENTITY_KEY,TARGET_ENTITY_KEY,ASSOCIATION_ID) values("
					+ nextIdDbProp + ",'" + srcAssociationId + "',null," + nextIdMetadata + ")";
		}
		else
		{
			sql = "insert into dyextn_constraint_properties (IDENTIFIER,SOURCE_ENTITY_KEY,TARGET_ENTITY_KEY,ASSOCIATION_ID) values("
					+ nextIdDbProp
					+ ",'"
					+ srcAssociationId
					+ "','"
					+ targetId
					+ "',"
					+ nextIdMetadata + ")";
		}
		return sql;
	}

	/**
	 * @param srcAssociationId
	 * @param targetId
	 * @param nextIdMetadata
	 * @param nextIdDbProp
	 * @return
	 */
	private String insertConstraintPropIfSwap(String srcAssociationId, String targetId,
			long nextIdMetadata, long nextIdDbProp)
	{
		String sql;
		if (targetId == null)
		{
			sql = "insert into dyextn_constraint_properties (IDENTIFIER,SOURCE_ENTITY_KEY,TARGET_ENTITY_KEY,ASSOCIATION_ID) values("
					+ nextIdDbProp + ",null,'" + srcAssociationId + "'," + nextIdMetadata + ")";
		}
		else
		{
			sql = "insert into dyextn_constraint_properties (IDENTIFIER,SOURCE_ENTITY_KEY,TARGET_ENTITY_KEY,ASSOCIATION_ID) values("
					+ nextIdDbProp
					+ ",'"
					+ targetId
					+ "','"
					+ srcAssociationId
					+ "',"
					+ nextIdMetadata + ")";
		}
		return sql;
	}

	/**
	 * @param associatedEntity
	 * @param jdbcdao
	 * @return
	 * @throws SQLException
	 * @throws DAOException
	 * @throws DynamicExtensionsSystemException 
	 */
	private Long getAssociatedEntityId(String associatedEntity, JDBCDAO jdbcdao,
			String... entgrpName) throws SQLException, DAOException,
			DynamicExtensionsSystemException
	{
		Long associatedId = 0L;
		if (entgrpName.length > 0)
		{
			Long entityGroupId = EntityManager.getInstance().getEntityGroupId(entgrpName[0]);
			associatedId = EntityManager.getInstance().getEntityId(associatedEntity, entityGroupId);

		}
		else
		{
			String sql;
			ResultSet resultSet;
			sql = "select identifier from dyextn_abstract_metadata where name like '"
					+ associatedEntity + "'";
			resultSet = jdbcdao.getQueryResultSet(sql);
			if (resultSet.next())
			{
				associatedId = resultSet.getLong(1);
			}
			jdbcdao.closeStatement(resultSet);
			if (associatedId == 0)
			{
				LOGGER.info("Entity not found of name ");
			}
		}
		return associatedId;
	}

	/**
	 * @param tablename
	 * @param coloumn
	 * @param jdbcdao
	 * @return
	 * @throws SQLException
	 * @throws DAOException
	 */
	private Long getNextId(String tablename, String coloumn, JDBCDAO jdbcdao) throws SQLException,
			DAOException
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

	/**
	 * @param sourceEntity
	 * @param targetEntity
	 * @param entityGroupName
	 * @param targetRolename
	 * @param associationType
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	public void updateAssociation(String sourceEntity, String targetEntity, String entityGroupName,
			String targetRolename, AssociationType associationType)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{

		Long entityGroupId = EntityManager.getInstance().getEntityGroupId(entityGroupName);
		Long srcEntityId = EntityManager.getInstance().getEntityId(sourceEntity, entityGroupId);
		Long targetEntityId = EntityManager.getInstance().getEntityId(targetEntity, entityGroupId);
		LOGGER.info(entityGroupId + "+" + srcEntityId + "+" + targetEntityId);
		Collection<AssociationInterface> colAssn = EntityManager.getInstance().getAssociations(
				srcEntityId, targetEntityId);
		try
		{

			AssociationInterface saveObject = null;
			for (AssociationInterface assn : colAssn)
			{
				LOGGER.info("--assn id " + assn.getId());
				if (assn.getTargetRole().getName().equalsIgnoreCase(targetRolename))
				{
					LOGGER.info("---setting " + targetRolename + "---" + associationType);
					assn.getTargetRole().setAssociationsType(associationType);
					saveObject = assn;
					break;
				}

			}
			IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(
					"dynamicExtention");
			DAO dao = null;
			dao = daoFactory.getDAO();
			dao.openSession(null);
			dao.update(saveObject);
			dao.commit();
			dao.closeSession();
		}
		catch (Exception ex)
		{
			throw new RuntimeException("error in saving object!!!!", ex);
		}
	}
}
