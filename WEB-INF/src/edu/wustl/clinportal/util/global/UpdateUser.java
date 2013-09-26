/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */

/**
 * 
 */

package edu.wustl.clinportal.util.global;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class UpdateUser
{

	//private static final Logger logger = Logger.getLogger(IdPManager.class);
	static String csmHost;
	static String csmUserName;
	static String csmPwd;
	static String csmUrl;
	static String csmPort;
	private static String csmDbName;
	static String emailId;
	private static String dbType;

	static String host;
	static String userName;
	static String pwd;
	static String url;
	static String port;
	private static String dbName;

	// private static final Logger logger = Logger.getLogger(UpdateUser.class);
	static String driver = "";

	public static void main(String arg[])
	{
		Properties prop = new Properties();
		File file = new File("ClinPortalInstall.properties");
		try
		{
			BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file));
			prop.load(stream);

			readProperties(prop);
			//   addCatissueUsersToClinportal();
			updateUser();

		}
		catch (FileNotFoundException e)
		{
			//         logger.info(e.getMessage(),e);
			e.printStackTrace();
		}
		catch (IOException e)
		{
			//          logger.info(e.getMessage(),e);
			e.printStackTrace();
		}

	}

	/**
	 * 
	 *
	 */
	public static void readProperties(Properties prop)
	{
		csmHost = prop.getProperty("csm.database.host");
		csmUserName = prop.getProperty("csm.database.username");
		csmPwd = prop.getProperty("csm.database.password");
		csmDbName = prop.getProperty("csm.database.name");
		csmPort = prop.getProperty("csm.database.port");

		host = prop.getProperty("database.host");
		userName = prop.getProperty("database.username");
		pwd = prop.getProperty("database.password");
		dbName = prop.getProperty("database.name");
		port = prop.getProperty("database.port");

		emailId = prop.getProperty("first.admin.emailAddress");
		dbType = prop.getProperty("database.type");
		StringBuffer buffer = new StringBuffer();
		buffer.append("jdbc:oracle:thin:@").append(csmHost).append(":").append(csmPort);
		buffer.append(":");
		buffer.append(csmDbName);
		csmUrl = buffer.toString();

		StringBuffer buffer1 = new StringBuffer();
		buffer1.append("jdbc:oracle:thin:@");
		buffer1.append(host);
		buffer1.append(":");
		buffer1.append(port);
		buffer1.append(":");
		buffer1.append(dbName);
		url = buffer1.toString();

		if (dbType.equalsIgnoreCase("ORACLE"))
		{
			driver = "oracle.jdbc.driver.OracleDriver";
		}
	}

	/**
	 * 0: get catissue_useer id=1
	 * 1: get csm user 
	 * 2: update new csm user with prv csm user
	 * 3: update catissue_user with csm_user_id
	 *
	 */
	public static void updateUser()
	{
		List<String> resultList = getUser();
		String csmUserId = updateCSMDBTable(resultList);
		Statement stmt = null;
		Connection conn = null;
		try
		{
			conn = getConnection(driver, userName, pwd, url);
			stmt = conn.createStatement();
			if (csmUserId != null && !csmUserId.equals(""))
			{
				String query = "update catissue_user set csm_user_id = '" + csmUserId
						+ "' where identifier='1'";
				stmt.executeQuery(query);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				stmt.close();
				conn.close();
			}
			catch (SQLException e)
			{
				//         logger.info(e.getMessage(),e);
				e.printStackTrace();
			}
		}
	}

	private static List<String> getUser()
	{
		String query = "select CSM_USER_ID from catissue_user where identifier='1'";
		Statement stmt = null;;
		ResultSet resultSet = null;
		List<String> resultList = new ArrayList<String>();
		Connection conn = null;
		try
		{
			conn = getConnection(driver, userName, pwd, url);
			stmt = conn.createStatement();
			resultSet = stmt.executeQuery(query);
			if (resultSet.next())
			{
				String csmId = resultSet.getString(1);
				query = "select LOGIN_NAME,PASSWORD,EMAIL_ID from csm_user where USER_ID='" + csmId
						+ "'";
				resultSet = stmt.executeQuery(query);
				if (resultSet.next())
				{
					resultList.add(resultSet.getString(1));
					resultList.add(resultSet.getString(2));
					resultList.add(resultSet.getString(3));
				}
			}

		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				resultSet.close();
				stmt.close();
				conn.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return resultList;
	}

	/**
	 * 
	 * @return
	 */
	private static String updateCSMDBTable(List<String> resultList)
	{
		String query = "select USER_ID from csm_user where LOGIN_NAME ='admin@admin11.com'";
		//            "select user_id from csm_user where login_name='"
		//                + emailId + "'";
		String csmUserId = "";
		ResultSet resultset = null;
		Statement stmt = null;

		Connection conn = null;
		try
		{
			conn = getConnection(driver, csmUserName, csmPwd, csmUrl);

			stmt = conn.createStatement();
			resultset = stmt.executeQuery(query);
			if (resultset.next())
			{
				csmUserId = resultset.getString(1);
				query = "update csm_user set LOGIN_NAME='" + resultList.get(0) + "',PASSWORD='"
						+ resultList.get(1) + "',EMAIL_ID='" + resultList.get(2)
						+ "' where LOGIN_NAME ='admin@admin11.com'";
				resultset = stmt.executeQuery(query);
			}
		}
		catch (Exception e)
		{
			//     logger.info(e.getMessage(),e);
			e.printStackTrace();
		}
		finally
		{
			try
			{
				resultset.close();
				stmt.close();
				conn.close();
			}
			catch (SQLException e)
			{
				//      logger.info(e.getMessage(),e);
				e.printStackTrace();
			}
		}
		return csmUserId;
	}

	/**
	 * 
	 * @param driver
	 * @param userName
	 * @param password
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static java.sql.Connection getConnection(String driver, String userName,
			String password, String url) throws Exception
	{
		//jdbc:oracle:thin:@mga.wustl.edu:1521:testdb
		//jdbc:mysql://localhost:3306/testcatissuecore
		Connection connection = null;
		try
		{
			Class.forName(driver);
			connection = DriverManager.getConnection(url, userName, password);
		}
		catch (SQLException e)
		{
			//          logger.info(e.getMessage(),e);
			e.printStackTrace();
			throw new Exception("Cannot Establish the Connection" + e);
		}
		catch (ClassNotFoundException e)
		{
			//        logger.info(e.getMessage(),e);
			e.printStackTrace();
			throw new Exception("Cannot Establish the Connection" + e);
		}
		return connection;
	}

}
