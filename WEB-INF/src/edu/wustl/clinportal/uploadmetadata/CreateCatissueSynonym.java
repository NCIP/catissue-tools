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
import java.io.FileNotFoundException;
import java.io.FileReader;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.dao.DAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;

/**
 * @author falguni_sachde
 *
 */
public class CreateCatissueSynonym
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			createSynonyms(args[0], args[1]);
			System.out.println("--CreateCatissueSynonym Task finishes.");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * @param synFile
	 * @param catissueSchemaName
	 * @throws FileNotFoundException
	 * @throws DynamicExtensionsSystemException
	 */
	private static void createSynonyms(String synFile, String catissueSchemaName)
			throws FileNotFoundException, DynamicExtensionsSystemException
	{
		//LoggerConfig.configureLogger(System.getProperty("user.dir") + "log4j.properties");
		IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory("dynamicExtention");
		DAO dao = null;
		BufferedReader bufRdr = new BufferedReader(new FileReader(synFile));
		String line = null;
		JDBCDAO jdbcDao = null;
		try
		{
			jdbcDao = daoFactory.getJDBCDAO();
			jdbcDao.openSession(null);
			while ((line = bufRdr.readLine()) != null)
			{
				//System.out.println("line..."+line);
				String synsql = line.replace("{CATISSUE_SCHEMA_NAME}", catissueSchemaName);
				//System.out.println("creatin syn=" + synsql);
				jdbcDao.executeUpdate(synsql);

			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new DynamicExtensionsSystemException(e.getMessage());
		}
		finally
		{
			try
			{
				jdbcDao.closeSession();
			}
			catch (DAOException daoEx)
			{

				throw new DynamicExtensionsSystemException(daoEx.getMessage());
			}
		}

	}
}
