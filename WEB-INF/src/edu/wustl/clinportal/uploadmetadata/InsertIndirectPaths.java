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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;

public class InsertIndirectPaths
{

	/**
	 * @param args
	 * @throws DynamicExtensionsSystemException 
	 * @throws DynamicExtensionsApplicationException 
	 */
	public static void main(String[] args) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException, IOException
	{

		String fileName = args[0];
		List<List<String>> pathList = readFile(fileName);

		for (List<String> entityList : pathList)
		{
			insertPath(entityList);
		}

	}

	private static List<List<String>> readFile(String fileName) throws IOException
	{
		List<List<String>> indirectPaths = new ArrayList<List<String>>();
		List<String> paths = new ArrayList<String>();
		File file = new File(fileName);

		BufferedReader bufRdr = new BufferedReader(new FileReader(file));
		String line = null;

		//read each line of text file
		while ((line = bufRdr.readLine()) != null)
		{
			StringTokenizer tokenizer = new StringTokenizer(line, ",");
			while (tokenizer.hasMoreTokens())
			{
				//get next token and store it in the array
				paths.add(tokenizer.nextToken());
			}
		}
		for (String path : paths)
		{
			List<String> singlePath = new ArrayList<String>();
			StringTokenizer tokenizer = new StringTokenizer(path, ":");
			while (tokenizer.hasMoreTokens())
			{
				singlePath.add(tokenizer.nextToken());
			}
			indirectPaths.add(singlePath);
		}
		return indirectPaths;
	}

	private static void insertPath(List<String> entityList)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir") + "log4j.properties");
		Connection conn = null;
		String sql = null;
		String appName = CommonServiceLocator.getInstance().getAppName();
		IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
		JDBCDAO dao = null;
		ResultSet resultSet = null;
		try
		{

			dao = daoFactory.getJDBCDAO();
			dao.openSession(null);
			List<Long> entityIdList = getEntityIDsList(dao, entityList);
			List<String> intraModelId = getIntermediatePaths(dao, entityIdList);
			StringBuffer intermediatePath = new StringBuffer(intraModelId.get(0));

			for (int index = 1; index < intraModelId.size(); index++)
			{
				intermediatePath.append("_");
				intermediatePath.append(intraModelId.get(index).toString());
			}
			Long maxPathId = null;
			resultSet = dao.getQueryResultSet("select max(PATH_ID) from path");
			if (resultSet.next())
			{
				maxPathId = resultSet.getLong(1) + 1;
			}
			dao.closeStatement(resultSet);

			sql = "INSERT INTO path values(" + maxPathId + "," + entityIdList.get(0) + ",'"
					+ intermediatePath.toString() + "',"
					+ entityIdList.get(entityIdList.size() - 1) + ")";
			dao.executeUpdate(sql);
			dao.commit();

		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		catch (DAOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				resultSet.close();
				dao.closeSession();
			}
			catch (DAOException e)
			{
				e.printStackTrace();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param dao
	 * @param entityList
	 * @return
	 * @throws SQLException
	 * @throws DAOException
	 */
	private static List<Long> getEntityIDsList(JDBCDAO dao, List<String> entityList)
			throws SQLException, DAOException
	{
		List<Long> entityIdList = new ArrayList<Long>();
		ResultSet rset = null;

		for (String entityName : entityList)
		{
			String sql = "select IDENTIFIER from DYEXTN_ABSTRACT_METADATA where NAME='"
					+ entityName + "'";
			rset = dao.getQueryResultSet(sql);
			while (rset.next())
			{
				entityIdList.add(rset.getLong(1));
			}
			dao.closeStatement(rset);
		}

		return entityIdList;
	}

	private static List<String> getIntermediatePaths(JDBCDAO dao, List<Long> entityIdList)
			throws SQLException, DAOException
	{
		List<String> intraModelId = new ArrayList<String>();

		ResultSet rset = null;

		for (int index = 0; index < entityIdList.size() - 1; index++)
		{
			if (index + 1 < entityIdList.size())
			{
				String sql = "select INTERMEDIATE_PATH from PATH where FIRST_ENTITY_ID="
						+ entityIdList.get(index) + "and LAST_ENTITY_ID="
						+ entityIdList.get(index + 1);
				rset = dao.getQueryResultSet(sql);
				while (rset.next())
				{
					intraModelId.add(rset.getString(1));
				}
				rset.close();
			}
		}

		return intraModelId;
	}

}
