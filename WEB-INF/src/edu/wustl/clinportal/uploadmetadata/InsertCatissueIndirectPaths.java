
package edu.wustl.clinportal.uploadmetadata;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.clinportal.action.annotations.AnnotationConstants;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;

public class InsertCatissueIndirectPaths
{

	static private BufferedWriter writter = null;

	/**
	 * @param args
	 * @throws DynamicExtensionsSystemException 
	 * @throws DynamicExtensionsApplicationException 
	 * @throws DAOException 
	 */
	public static void main(String[] args) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException, IOException, DAOException
	{

		String fileName = args[0];
		List<List<String>> pathList = readFile(fileName);
		writter = new BufferedWriter(new FileWriter("Indirctlog.txt"));
		for (List<String> entityList : pathList)
		{

			insertPath(entityList);
		}
		writter.flush();
		writter.close();
		System.out.println("--The catissue indirect paths insertion finishes.");
	}

	/**
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
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

	/**
	 * @param entityList
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 * @throws IOException
	 * @throws DAOException 
	 */
	private static void insertPath(List<String> entityList)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException,
			IOException, DAOException
	{

		String sql = null;
		//LoggerConfig.configureLogger(System.getProperty("user.dir") + "log4j.properties");
		String appName = CommonServiceLocator.getInstance().getAppName();
		IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
		JDBCDAO dao = null;
		ResultSet resultSet = null;
		try
		{

			String eGroupname = AnnotationConstants.CAT_GROUPNAME_IN_CP;
			Long entityGrpId = EntityManager.getInstance().getEntityGroupId(eGroupname);
			dao = daoFactory.getJDBCDAO();
			dao.openSession(null);
			ResultSet rs = null;
			//System.out.println("getting entities of group===== " + entityGrpId);
			List<Long> entityIdList = getEntityIDsList(dao, entityList, entityGrpId);
			//System.out.println("entity id list size ====" + entityIdList.size());
			List<Long> parentEntityIdList = getParentEntityIdsList(dao, entityIdList);

			List<String> intraModelId = getIntermediatePaths(dao, entityIdList, parentEntityIdList);
			StringBuffer intermediatePath = new StringBuffer(intraModelId.get(0));

			for (int index = 1; index < intraModelId.size(); index++)
			{
				intermediatePath.append("_");
				intermediatePath.append(intraModelId.get(index).toString());
			}

			Long maxPathId = null;
			rs = dao.getQueryResultSet("select max(PATH_ID) from path");
			if (rs.next())
			{
				maxPathId = rs.getLong(1) + 1;
			}
			dao.closeStatement(rs);
			sql = "INSERT INTO path values(" + maxPathId + "," + entityIdList.get(0) + ",'"
					+ intermediatePath.toString() + "',"
					+ entityIdList.get(entityIdList.size() - 1) + ")";
			dao.executeUpdate(sql);
			dao.commit();
		}
		catch (SQLException e)
		{
			throw new DynamicExtensionsSystemException("error in inserting path");
		}
	}

	/**
	 * @param conn
	 * @param entityIdList
	 * @return
	 * @throws SQLException
	 * @throws DAOException 
	 */
	private static List<Long> getParentEntityIdsList(JDBCDAO dao, List<Long> entityIdList)
			throws SQLException, DAOException
	{
		List<Long> parententityIdList = new ArrayList<Long>();

		ResultSet rs = null;
		for (int c = 0; c < entityIdList.size(); c++)
		{
			Long entityId = entityIdList.get(c);
			String sql = "select PARENT_ENTITY_ID from DYEXTN_ENTITY where IDENTIFIER='" + entityId
					+ "'";
			rs = dao.getQueryResultSet(sql);
			while (rs.next())
			{
				parententityIdList.add(rs.getLong(1));
			}
			dao.closeStatement(rs);

		}
		return parententityIdList;
	}

	/**
	 * @param conn
	 * @param entityList
	 * @param entityGrpId
	 * @return
	 * @throws SQLException
	 * @throws DAOException 
	 */
	private static List<Long> getEntityIDsList(JDBCDAO dao, List<String> entityList,
			Long entityGrpId) throws SQLException, DAOException
	{

		List<Long> entityIdList = new ArrayList<Long>();

		ResultSet rs = null;

		for (String entityName : entityList)
		{

			String sql = "select dam.IDENTIFIER from DYEXTN_ABSTRACT_METADATA dam ,DYEXTN_ENTITY de where dam.NAME='"
					+ entityName
					+ "' and  dam.identifier= de.identifier and de.ENTITY_GROUP_ID = '"
					+ entityGrpId + "'";
			rs = dao.getQueryResultSet(sql);
			while (rs.next())
			{
				entityIdList.add(rs.getLong(1));
			}
			dao.closeStatement(rs);
		}

		return entityIdList;
	}

	/**
	 * @param conn
	 * @param entityIdList
	 * @param parentEntityIdList
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 * @throws DAOException 
	 */
	private static List<String> getIntermediatePaths(JDBCDAO dao, List<Long> entityIdList,
			List<Long> parentEntityIdList) throws SQLException, IOException, DAOException
	{
		List<String> intraModelId = new ArrayList<String>();

		ResultSet rs = null;

		for (int index = 0; index < entityIdList.size() - 1; index++)
		{
			if (index + 1 < entityIdList.size())
			{
				String sql = "select INTERMEDIATE_PATH from PATH where FIRST_ENTITY_ID="
						+ entityIdList.get(index) + " and LAST_ENTITY_ID="
						+ entityIdList.get(index + 1);
				rs = dao.getQueryResultSet(sql);

				boolean notfound = true;
				while (rs.next())
				{
					notfound = false;
					intraModelId.add(rs.getString(1));
				}
				dao.closeStatement(rs);
				if (notfound)
				{
					ResultSet rs1;
					String sql1 = "select INTERMEDIATE_PATH from PATH where FIRST_ENTITY_ID="
							+ parentEntityIdList.get(index) + " and LAST_ENTITY_ID="
							+ entityIdList.get(index + 1);

					rs1 = dao.getQueryResultSet(sql1);
					boolean f = false;
					while (rs1.next())
					{
						intraModelId.add(rs1.getString(1));
						f = true;
					}
					dao.closeStatement(rs1);
					if (!f)
					{
						ResultSet rs2;
						String sql2 = "select INTERMEDIATE_PATH from PATH where FIRST_ENTITY_ID="
								+ entityIdList.get(index) + " and LAST_ENTITY_ID="
								+ parentEntityIdList.get(index + 1);
						rs2 = dao.getQueryResultSet(sql2);
						boolean flag = false;
						while (rs2.next())
						{

							intraModelId.add(rs2.getString(1));
							flag = true;
						}
						dao.closeStatement(rs2);
						if (!flag)
						{
							System.out.println("------error----");
							writter.write("--" + entityIdList + "\n");

							writter.write("sql-" + sql);
							writter.write("\n1--" + sql1);

							writter.write("\n--2=" + sql2);
							writter.write("\n intraModelId==" + intraModelId);
						}

					}

				}

			}

		}

		return intraModelId;
	}

}
