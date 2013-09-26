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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.dao.exception.DAOException;

/**
 * This class is used to update DE metadata.
 * @author pooja_deshpande
 *
 */
public class UpdateMetadataFrom1_2_1_to_1_3_1
{
	/**
	 * Connection instance.
	 */
	private static Connection connection = null;
	/**
	 *  The Name of the server for the database. For example : localhost.
	 */
	private static String dbServerName;
	/**
	 *  The Port number of the server for the database.
	 */
	private static String dbPortNo;
	/**
	 *  The Type of Database. Use one of the two values 'MySQL', 'Oracle', MsSqlServer.
	 */
	private static String dbType;
	/**
	 * 	Name of the Database.
	 */
	private static String dbName;
	/**
	 *  Database User name.
	 */
	private static String dbUsername;
	/**
	 *  Database Password.
	 */
	private static String dbPassword;
	/**
	 *  The database Driver.
	 */
	private static String dbDriver;

	/**
	 *
	 * @param args Command Line Arguments
	 * @throws SQLException SQLException
	 * @throws IOException IOException
	 * @throws ClassNotFoundException ClassNotFoundException
	 * @throws DAOException DAOException
	 * @throws DynamicExtensionsSystemException DynamicExtensionsSystemException
	 */
	public static void main(String[] args) throws SQLException, IOException,
	ClassNotFoundException, DAOException, DynamicExtensionsSystemException
	{
		configureDBConnection(args);
		connection = getConnection();
		connection.setAutoCommit(true);

		deleteMetadata();
		addPaths();
	}

	/**
	 * Delete metadata.
	 * @throws DAOException DAOException
	 * @throws SQLException SQLException
	 */
	private static void deleteMetadata() throws DAOException, SQLException
	{
		deleteAssociation("Questionnaire","MedicalCondition");
	}

	/**
	 * Delete the associations.
	 * @param sourceEntity source entity
	 * @param targetEntity target entity
	 * @throws DAOException DAOException
	 * @throws SQLException SQLException
	 */
	private static void deleteAssociation(String sourceEntity, String targetEntity)
	throws DAOException, SQLException
	{
		final int sourceEntityId = getEntityIdByName(sourceEntity);
		final int targetEntityId = getEntityIdByName(targetEntity);

		final List<String> deleteSQLs = new ArrayList<String>();

		getDeleteSQLs(deleteSQLs,sourceEntityId,targetEntityId);

		for(String sql : deleteSQLs)
		{
			System.out.print("\n"+sql+";");
		}
		executeDeleteStatements(deleteSQLs);
	}

	/**
	 * Execute the delete statements.
	 * @param deleteSQLs List of delete statements to be executed
	 * @throws SQLException SQLException
	 */
	private static void executeDeleteStatements(List<String> deleteSQLs) throws SQLException
	{
		Statement stmt = connection.createStatement();
		try
		{
			for (final String sql : deleteSQLs)
			{
				try
				{
					stmt.execute(sql);
				}
				catch (final SQLException e)
				{
					e.printStackTrace();
				}
			}
		}
		finally
		{
			stmt.close();
		}
	}

	/**
	 * Get the entity id from the specified entity name .
	 * @param entityName Name of the entity
	 * @return entityId Entity Id
	 * @throws DAOException DAOException
	 * @throws SQLException SQLException
	 */
	private static int getEntityIdByName(String entityName) throws DAOException, SQLException
	{
		int entityId = 0;

		final String sql = "select identifier from dyextn_abstract_metadata where name like '"
			+ entityName + "'";
		Statement stmt = connection.createStatement();
		ResultSet resultSet = stmt.executeQuery(sql);

		if(resultSet.next())
		{
			entityId = resultSet.getInt(1);
		}
		if (entityId == 0)
		{
			System.out.println("Entity not found of name " + entityName);
		}
		stmt.close();
		return entityId;
	}

	/**
	 * Get delete statements to be executed.
	 * @param deleteSQL List of delete statements
	 * @param sourceEntityId source entity id
	 * @param targetEntityId target entity id
	 * @return deAssociationId DE association Id
	 * @throws SQLException SQLException
	 */
	private static Long getDeleteSQLs(List<String> deleteSQL, int sourceEntityId, int targetEntityId)
	throws SQLException
	{
		String sql;
		Long srcRoleId = null;
		Long targetRoleId = null;
		Long deAssociationId = null;

		final List<Long> roleIdMap = new ArrayList<Long>();

		sql = "select INTERMEDIATE_PATH from path where FIRST_ENTITY_ID =" + sourceEntityId
				+ " AND LAST_ENTITY_ID = " + targetEntityId;

		Statement stmt = connection.createStatement();
		final ResultSet rs = stmt.executeQuery(sql);
		try
		{
			while (rs.next())
			{
				final String intermediatePathId = rs.getString(1);

				sql = "delete from path where INTERMEDIATE_PATH ='" + intermediatePathId + "'";
				deleteSQL.add(sql);

				sql = "select DE_ASSOCIATION_ID"
					+ " from intra_model_association where ASSOCIATION_ID=" + intermediatePathId;

				final Statement stmt2 = connection.createStatement();
				final ResultSet rs2 = stmt2.executeQuery(sql);
				if (rs2.next())
				{
					deAssociationId = rs2.getLong(1);
					sql = "select DIRECTION,SOURCE_ROLE_ID,TARGET_ROLE_ID"
						+ " from dyextn_association where identifier = " + deAssociationId;
					final Statement stmt3 = connection.createStatement();
					final ResultSet rs3 = stmt3.executeQuery(sql);
					if (rs3.next())
					{
							srcRoleId = rs3.getLong(2);
							targetRoleId = rs3.getLong(3);
							roleIdMap.add(srcRoleId);
							roleIdMap.add(targetRoleId);
					}
					rs3.close();
					stmt3.close();
				}
				rs2.close();
				stmt2.close();

				sql = "select identifier" + " from dyextn_constraint_properties"
						+ " where ASSOCIATION_ID = " + deAssociationId;
				final Statement stmt1 = connection.createStatement();
				final ResultSet rs1 = stmt1.executeQuery(sql);
				if (rs1.next())
				{
					final Long constraintId = rs1.getLong(1);
					sql = "select identifier from DYEXTN_CONSTRAINTKEY_PROP" +
							" where src_constraint_key_id = "+constraintId;
					populateDeleteStmts(deleteSQL, sql, constraintId);

					sql = "delete from dyextn_constraint_properties" + " where identifier = "
							+ constraintId;
					deleteSQL.add(sql);

					sql = "delete from dyextn_database_properties where identifier = "
							+ constraintId;
					deleteSQL.add(sql);
				}
				rs1.close();
				stmt1.close();

				sql = "delete from intra_model_association" + " where ASSOCIATION_ID ="
				+ intermediatePathId;
				deleteSQL.add(sql);

				sql = "delete from association where ASSOCIATION_ID =" + intermediatePathId;
				deleteSQL.add(sql);

				sql = "delete from dyextn_association where IDENTIFIER=" + deAssociationId;
				deleteSQL.add(sql);

				sql = "delete from dyextn_attribute where identifier =" + deAssociationId;
				deleteSQL.add(sql);

				sql = "select identifier from DYEXTN_CONTROL where BASE_ABST_ATR_ID =" + deAssociationId;
				final Long controlId;
				final Statement stmt6 = connection.createStatement();
				final ResultSet rs6 = stmt6.executeQuery(sql);
				if(rs6.next())
				{
					controlId = rs6.getLong(1);
					sql = "delete from DYEXTN_CONTAINMENT_CONTROL where identifier =" + controlId;
					deleteSQL.add(sql);

					sql = "delete from DYEXTN_ABSTR_CONTAIN_CTR where identifier =" + controlId;
					deleteSQL.add(sql);
					rs6.close();
				}
				stmt6.close();

				sql = "delete from DYEXTN_CONTROL where BASE_ABST_ATR_ID =" + deAssociationId;
				deleteSQL.add(sql);

				sql = "delete from DYEXTN_BASE_ABSTRACT_ATTRIBUTE where identifier =" + deAssociationId;
				deleteSQL.add(sql);

				sql = "delete from dyextn_abstract_metadata where identifier="
						+ deAssociationId;
				deleteSQL.add(sql);
			}
			if (srcRoleId != null)
			{
				sql = "delete from dyextn_role where IDENTIFIER=" + srcRoleId;
				deleteSQL.add(sql);
			}

			if (targetRoleId != null)
			{
				sql = "delete from dyextn_role where IDENTIFIER=" + targetRoleId;
				deleteSQL.add(sql);
			}
		}
		finally
		{
			rs.close();
		}
		return deAssociationId;
	}

	/**
	 * @param deleteSQL List of delete sqls
	 * @param sql sql
	 * @param constraintId constraint id
	 * @throws SQLException SQLException
	 */
	private static void populateDeleteStmts(List<String> deleteSQL, String sql,
			final Long constraintId) throws SQLException
	{
		final Long constraintKeyId;
		final Statement stmt4 = connection.createStatement();
		final ResultSet rs4 = stmt4.executeQuery(sql);
		if(rs4.next())
		{
			constraintKeyId = rs4.getLong(1);
			sql = "delete from DYEXTN_COLUMN_PROPERTIES where CNSTR_KEY_PROP_ID = "+constraintKeyId;
			deleteSQL.add(sql);

			sql = "delete from DYEXTN_CONSTRAINTKEY_PROP where identifier = "+constraintKeyId;
			deleteSQL.add(sql);

		}
		else
		{
			sql = "select identifier from DYEXTN_CONSTRAINTKEY_PROP" +
			" where tgt_constraint_key_id = "+constraintId;
			final Statement stmt5 = connection.createStatement();
			final ResultSet rs5 = stmt5.executeQuery(sql);
			if(rs5.next())
			{
				constraintKeyId = rs5.getLong(1);
				sql = "delete from DYEXTN_COLUMN_PROPERTIES where CNSTR_KEY_PROP_ID = "+constraintKeyId;
				deleteSQL.add(sql);

				sql = "delete from DYEXTN_CONSTRAINTKEY_PROP where identifier = "+constraintKeyId;
				deleteSQL.add(sql);
			}
			rs5.close();
			stmt5.close();
		}
		rs4.close();
		stmt4.close();
	}

	/**
	 * This method will create a database connection using configuration info.
	 * @return Connection : Database connection object
	 * @throws ClassNotFoundException Class Not Found Exception
	 * @throws SQLException SQL Exception
	 */
	public static Connection getConnection() throws ClassNotFoundException, SQLException
	{
		Connection connection = null;
		// Load the JDBC driver
		Class.forName(dbDriver);
		// Create a connection to the database
		String url = "";
		if ("MySQL".equalsIgnoreCase(dbType))
		{
			url = "jdbc:mysql://" + dbServerName + ":" + dbPortNo + "/"
					+ dbName; // a JDBC url
		}
		if ("Oracle".equalsIgnoreCase(dbType))
		{
			url = "jdbc:oracle:thin:@" + dbServerName + ":" + dbPortNo
					+ ":" + dbName;
		}

		connection = DriverManager.getConnection(url, dbUsername, dbPassword);
		return connection;
	}

	/**
	 * Configuration.
	 * @param args arguments
	 */
	public static void configureDBConnection(String[] args)
	{
		if (args.length < 7)
		{
			throw new RuntimeException("In sufficient number of arguments");
		}
		dbServerName = args[0];
		dbPortNo = args[1];
		dbType = args[2];
		dbName = args[3];
		dbUsername = args[4];
		dbPassword = args[5];
		dbDriver = args[6];
	}

	/**
	 * Copy paths from parent entity to child entity.
	 * @throws SQLException SQLException
	 */
	private static void addPaths() throws SQLException
	{
		List<String> insertPathSQL = new ArrayList<String>();
		Statement stmt = connection.createStatement();
		AddPath.initData();
		final AddPath pathObject = new AddPath();
		insertPathSQL = pathObject.parseMaps(stmt, connection);
		stmt.close();
		for(String sql : insertPathSQL)
		{
			System.out.println(sql+";");
		}

		executeInsertPathStatements(insertPathSQL);
	}

	/**
	 * Execute insert path statements.
	 * @param insertPathSQL insert sqls
	 * @throws SQLException SQLException
	 */
	private static void executeInsertPathStatements(List<String> insertPathSQL) throws SQLException
	{
		Statement stmt = connection.createStatement();
		try
		{
			for (final String sql : insertPathSQL)
			{
				try
				{
					stmt.executeUpdate(sql);
				}
				catch (final SQLException e)
				{
					e.printStackTrace();
				}
			}
		}
		finally
		{
			stmt.close();
		}
	}
}