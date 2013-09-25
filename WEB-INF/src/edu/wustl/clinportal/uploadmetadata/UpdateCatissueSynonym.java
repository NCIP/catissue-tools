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
import java.util.ArrayList;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;

/**
 * @author falguni_sachde
 *
 */
public class UpdateCatissueSynonym
{

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}
	private static final Logger LOGGER = Logger.getCommonLogger(UpdateCatissueSynonym.class);

	private static final String CREATE_SYN = "CREATE OR REPLACE SYNONYM ";

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			dropandcreateSynonyms(args[0]);
			LOGGER.info("--UpdateCatissueSynonym Task finishes.");
		}
		catch (Exception e)
		{
			LOGGER.info("--UpdateCatissueSynonym Task throws error." + e.getMessage());
		}
	}

	/**
	 * @param synFile
	 * @param catissueSchemaName
	 * @throws FileNotFoundException
	 * @throws DynamicExtensionsSystemException
	 */
	private static void dropandcreateSynonyms(String catissueSchemaName)
			throws DynamicExtensionsSystemException
	{

		LOGGER.info("--UpdateCatissueSynonym mofiying synonyms for Catissueschemaname:"
				+ catissueSchemaName);
		IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory();
		JDBCDAO jdbcDao = null;
		ArrayList<String> lstSyn = new ArrayList<String>();
		try
		{
			jdbcDao = daoFactory.getJDBCDAO();
			jdbcDao.openSession(null);
			final String sql = "select synonym_name,table_name from user_synonyms";
			ResultSet resultSet = jdbcDao.getQueryResultSet(sql);
			while (resultSet.next())
			{
				String synName = resultSet.getString(1);
				String tableName = resultSet.getString(2);
				String createSql = CREATE_SYN + synName + " FOR " + catissueSchemaName + "."
						+ tableName;
				lstSyn.add(createSql);
			}
			jdbcDao.closeStatement(resultSet);
			createSynonyms(lstSyn, jdbcDao);
			jdbcDao.commit();
		}
		catch (Exception e)
		{
			throw new DynamicExtensionsSystemException(e.getMessage());
		}
		finally
		{
			if (jdbcDao != null)
			{
				try
				{
					jdbcDao.closeSession();
				}
				catch (DAOException e)
				{
					LOGGER.info("--UpdateCatissueSynonym Task throws error." + e.getMessage());
				}
			}
		}
	}

	/**
	 * @param lstSyn
	 * @param jdbcDao
	 * @throws DAOException 
	 */
	private static void createSynonyms(ArrayList<String> lstSyn, JDBCDAO jdbcDao)
			throws DAOException
	{
		for (String createSql : lstSyn)
		{
			LOGGER.info("--" + createSql);
			jdbcDao.executeUpdate(createSql);

		}
	}
}