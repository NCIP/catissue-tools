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

import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.exception.DAOException;

/**
 * 
 * @author deepali_ahirrao
 * @version
 */
public class UpdateMetadataUtil
{

	/**
	 * @param entityName
	 * @param jdbcdao
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 * @throws DAOException
	 */
	public static int getEntityIdByName(String entityName, JDBCDAO jdbcdao) throws IOException,
			SQLException, DAOException
	{
		ResultSet resultSet = null;
		int entityId = 0;
		String sql = "select identifier from dyextn_abstract_metadata where name like '"
				+ entityName + "'";
		try
		{
			resultSet = jdbcdao.getQueryResultSet(sql);
			if (resultSet.next())
			{
				entityId = resultSet.getInt(1);
			}
			if (entityId == 0)
			{
				System.out.println("Entity not found of name " + entityName);
			}
			jdbcdao.closeStatement(resultSet);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return entityId;
	}
}