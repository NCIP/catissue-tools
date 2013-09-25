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
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright:TODO</p>
 *@author 
 *@version 1.0
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

import edu.wustl.common.idp.IdPManager;
import edu.wustl.common.idp.utility.Utility;
import edu.wustl.common.idpInterface.impl.catissuecore.CaTissueConstants;
import gov.nih.nci.security.authorization.domainobjects.User;

public class AddUsersToOtherApplications
{

	// private static final Logger logger = Logger.getLogger(IdPManager.class);
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
			addCatissueUsersToClinportal();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

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
		//System.out.println(csmUrl);

		if (dbType.equalsIgnoreCase("ORACLE"))
		{
			driver = "oracle.jdbc.driver.OracleDriver";
		}
	}

	private static void addCatissueUsersToClinportal()
	{
		IdPManager idp = IdPManager.getInstance();
		List<String> idList = new ArrayList<String>();
		String query = "select user_id from csm_user";
		ResultSet resultset = null;
		Statement stmt = null;
		Connection conn = null;
		try
		{
			conn = getConnection(driver, csmUserName, csmPwd, csmUrl);
			stmt = conn.createStatement();
			resultset = stmt.executeQuery(query);
			while (resultset.next())
			{
				idList.add(resultset.getString(1));
			}
			System.out.println("***** ADDING USERS TO CLINPORTAL...THIS WILL TAKE SOME TIME....");
			for (String id : idList)
			{
				User user = new User();
				user = Utility.getCsmUser(id);
				idp.addUserToQueue(CaTissueConstants.APPLICATION_NAME, user);
			}
		}
		catch (Exception e)
		{
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
				e.printStackTrace();
			}
		}

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
		Connection connection = null;
		try
		{
			Class.forName(driver);
			connection = DriverManager.getConnection(url, userName, password);
		}
		catch (SQLException e)
		{
			throw new Exception("Connot Establish the Connection" + e);
		}
		catch (ClassNotFoundException e)
		{
			throw new Exception("Connot Establish the Connection" + e);
		}
		return connection;
	}

}
