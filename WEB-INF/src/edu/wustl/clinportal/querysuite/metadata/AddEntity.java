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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;

public class AddEntity
{

	/**
	 * @param entityList
	 * @param tableName
	 * @param parentEntity
	 * @param inheritance
	 * @param isAbstract
	 * @throws SQLException
	 * @throws DAOException
	 */
	public void addEntity(List<String> entityList, String tableName, String parentEntity,
			int inheritance, int isAbstract) throws SQLException, DAOException
	{

		DAOConfigFactory daocConfigFactory = DAOConfigFactory.getInstance();
		IDAOFactory daoFactory = daocConfigFactory.getDAOFactory("dynamicExtention");
		JDBCDAO jdbcdao = null;

		for (String entityName : entityList)
		{
			jdbcdao = daoFactory.getJDBCDAO();
			jdbcdao.openSession(null);
			//insert statements
			String sql = "select max(identifier) from dyextn_abstract_metadata";
			ResultSet resultSet = jdbcdao.getQueryResultSet(sql);
			int nextIdMetadata = 0;
			if (resultSet.next())
			{
				int maxId = resultSet.getInt(1);
				nextIdMetadata = maxId + 1;
			}

			jdbcdao.closeStatement(resultSet);

			int nextIdDbprop = 0;
			sql = "select max(identifier) from dyextn_database_properties";
			resultSet = jdbcdao.getQueryResultSet(sql);
			if (resultSet.next())
			{
				int maxId = resultSet.getInt(1);
				nextIdDbprop = maxId + 1;
			}

			jdbcdao.closeStatement(resultSet);

			sql = "INSERT INTO dyextn_abstract_metadata values(" + nextIdMetadata + ",NULL,'"
					+ entityName + "',NULL,'" + entityName + "',null)";
			jdbcdao.executeUpdate(sql);

			sql = "INSERT INTO dyextn_abstract_entity values(" + nextIdMetadata + ")";
			jdbcdao.executeUpdate(sql);

			if ("NULL".equals(parentEntity))
			{
				sql = "INSERT INTO dyextn_entity values(" + nextIdMetadata + ",2,1," + isAbstract
						+ ",NULL," + inheritance + ",NULL,NULL)";

				jdbcdao.executeUpdate(sql);
			}
			insertDbProp(tableName, nextIdMetadata, nextIdDbprop, jdbcdao);
			jdbcdao.commit();
			jdbcdao.closeSession();
		}

	}

	/**
	 * @param tableName
	 * @param nextIdMetadata
	 * @param nextIdDbprop
	 * @param jdbcdao
	 * @throws SQLException
	 * @throws DAOException
	 */
	private void insertDbProp(String tableName, int nextIdMetadata, int nextIdDbprop,
			JDBCDAO jdbcdao) throws SQLException, DAOException
	{
		String sql;
		sql = "INSERT INTO dyextn_database_properties values(" + nextIdDbprop + ",'" + tableName
				+ "')";
		jdbcdao.executeUpdate(sql);

		sql = "INSERT INTO dyextn_table_properties (IDENTIFIER,ABSTRACT_ENTITY_ID) values("
				+ nextIdDbprop + "," + nextIdMetadata + ")";
		jdbcdao.executeUpdate(sql);
	}
}