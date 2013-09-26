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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * AddPath.
 * @author pooja_deshpande
 */
public class AddPath
{
	/**
	 * specify superClass And SubClasses Map.
	 */
	private static Map<String, List<String>> superSubClassMap = new HashMap<String, List<String>>();
	/**
	 * specify superClass And Associations Map.
	 */
	private static Map<String, List<String>> superClassAssoMap = new HashMap<String, List<String>>();

	/**
	 * specify superClass name and Description Map.
	 */
	private static Map<String,String> superClassDescMap = new HashMap<String, String>();
	/**
	 * specify identifier.
	 */
	private int identifier = 0;

	/**
	 * Initialize Data.
	 */
	public static void initData()
	{
		List<String> subClassesList = new ArrayList<String>();
		subClassesList.add("BaseSolidTissuePathologyAnnotation");
		superSubClassMap.put("Annotations", subClassesList);

		List<String> associationsList = new ArrayList<String>();
		associationsList.add("Questionnaire");
		superClassAssoMap
				.put("Annotations", associationsList);
		superClassDescMap.put("Annotations", "ClinicalAnnotations--Annotations");

	}

	/**
	 * This method parses the maps, calls the getInsertPaths()
	 * method and returns the insert statements.
	 * @param stmt Statement
	 * @param connection Connection
	 * @return Insert Path Statements.
	 * @throws SQLException SQL Exception
	 */
	public List<String> parseMaps(Statement stmt, Connection connection)
			throws SQLException
	{
		final List<String> insertPathSQL = new ArrayList<String>();
		ResultSet resultSet;

		resultSet = stmt.executeQuery("select max(PATH_ID) from path");
		if (resultSet.next())
		{
			this.identifier = resultSet.getInt(1) + 1;
		}
		String sql;
		final Set<String> keySet = superClassAssoMap.keySet();
		final Iterator<String> iterator = keySet.iterator();
		while (iterator.hasNext())
		{
			final String key = iterator.next();
			sql = getSqlForDesc(key);
			stmt = connection.createStatement();
			resultSet = stmt.executeQuery(sql);
			if (resultSet.next())
			{
				resultSet = getInsertPaths(stmt, connection, insertPathSQL,
						resultSet, key);
			}
			else
			{
				System.out.println("Entity with name : " + key + " not found");
			}
		}
		resultSet.close();
		stmt.close();
		return insertPathSQL;
	}

	/**
	 * Get insert path statements.
	 * @param stmt Statement Object
	 * @param connection Connection object
	 * @param insertPathSQL list of insert statements
	 * @param resultSet ResultSet object
	 * @param key key
	 * @return List of insert statements
	 * @throws SQLException SQLException
	 */
	private ResultSet getInsertPaths(Statement stmt, Connection connection,
			final List<String> insertPathSQL, ResultSet resultSet,
			final String key) throws SQLException
	{
		String sql;
		String entityId;
		String assocEntityId;
		entityId = String.valueOf(resultSet.getLong(1));
		final List<String> associationsList = superClassAssoMap.get(key);
		for (final String associatedEntityName : associationsList)
		{
			sql = "Select IDENTIFIER from dyextn_abstract_metadata where NAME = "
					+ "'"+ associatedEntityName + "'";
			final Statement stmt4 = connection.createStatement();
			resultSet = stmt4.executeQuery(sql);
			if (resultSet.next())
			{
				assocEntityId = String.valueOf(resultSet.getLong(1));
				sql = "Select INTERMEDIATE_PATH from PATH where FIRST_ENTITY_ID = "
						+ entityId + " and LAST_ENTITY_ID = " + assocEntityId;
				final Statement stmt5 = connection.createStatement();
				resultSet = stmt5.executeQuery(sql);
				while (resultSet.next())
				{
					final String intermediatePathId = resultSet.getString(1);
					if(intermediatePathId.indexOf('_') == -1)
					{
						final List<String> subClassList = superSubClassMap.get(key);
						for (final String subClassEntity : subClassList)
						{
							String subClassEntityId;
							final Statement stmt1 = connection.createStatement();
							sql = "Select IDENTIFIER from"+" dyextn_abstract_metadata " +
								"where NAME = "+  "'"+subClassEntity + "'";
							final ResultSet rs1 = stmt.executeQuery(sql);
							if (rs1.next())
							{
								subClassEntityId = String.valueOf(rs1.getLong(1));
								if (key.equals(associatedEntityName))
								{
									insertPathSQL.add("insert into path values(" + this.identifier++
									+ "," + subClassEntityId + ","+ intermediatePathId + ","
									+ subClassEntityId + ")");
								}
								else
								{
									insertPathSQL.add("insert into path values("
											+ this.identifier++ + "," + subClassEntityId + ","
											+ intermediatePathId + "," + assocEntityId+ ")");
								}
							}
							stmt1.close();
						}
					}
				}
				stmt5.close();
				if (!(key.equals(associatedEntityName)))
				{
					insertPaths(connection, insertPathSQL, entityId,
							assocEntityId, key);
				}
			}
			stmt4.close();
		}
		return resultSet;
	}

	/**
	 * @param key key
	 * @return The sql
	 */
	private String getSqlForDesc(final String key)
	{
		String sql;
		final String description = superClassDescMap.get(key);
		if(description != null)
		{
			sql = "Select IDENTIFIER from dyextn_abstract_metadata where NAME = "+
			"'" + key + "' and DESCRIPTION = '"+description+"'";
		}
		else
		{
			sql = "Select IDENTIFIER from dyextn_abstract_metadata where NAME = "+
					"'" + key + "'";
		}
		return sql;
	}

	/**
	 * @param connection Connection object
	 * @param insertPathSQL list of insert path statements
	 * @param entityId entity id
	 * @param associatedId associated entity id
	 * @param key key
	 * @throws SQLException SQLException
	 */
	private void insertPaths(Connection connection,
			final List<String> insertPathSQL, String entityId,
			String associatedId, final String key) throws SQLException
	{
		ResultSet resultSet;
		String sql;
		sql = "Select INTERMEDIATE_PATH"
				+ " from PATH where FIRST_ENTITY_ID = " + associatedId
				+ " and LAST_ENTITY_ID = " + entityId;
		final Statement stmt2 = connection.createStatement();
		resultSet = stmt2.executeQuery(sql);
		while (resultSet.next())
		{
			final String intermediateId = resultSet.getString(1);
			if(intermediateId.indexOf('_') == -1)
			{
				final List<String> subClassList = superSubClassMap
						.get(key);
				for (final String subClassEntity : subClassList)
				{
					String subClassEntityId;
					final Statement stmt3 = connection.createStatement();
					sql = "Select IDENTIFIER" + " from dyextn_abstract_metadata"
							+ " where NAME = "
							+ "'"
							+ subClassEntity + "'";
					final ResultSet rs1 = stmt3.executeQuery(sql);
					if (rs1.next())
					{
						subClassEntityId = String.valueOf(rs1.getLong(1));
						insertPathSQL.add("insert into path values("
								+ this.identifier++ + "," + associatedId
								+ "," + intermediateId + "," + subClassEntityId
								+ ")");
					}
					stmt3.close();
				}
			}
		}
		stmt2.close();
	}
}
