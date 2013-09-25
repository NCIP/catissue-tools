/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */

/*
 * Created on Jan 20, 2006
 *
 * Listener for cleanup after session invalidates.
 * 
 */

package edu.wustl.clinportal.util.listener;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.security.exception.SMException;
import edu.wustl.security.privilege.PrivilegeManager;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.logger.Logger;

/**
 * @author poornima_govindrao
 *
 * Listener for cleanup after session invalidates.
 */
public class ClinPortalSessionListener implements HttpSessionListener
{

	public void sessionCreated(HttpSessionEvent arg0)
	{
		// Tasks to do when session is created
	}

	//Cleanup after session invalidates.
	public void sessionDestroyed(HttpSessionEvent arg0)
	{
		HttpSession session = arg0.getSession();

		SessionDataBean sessionData = (SessionDataBean) session
				.getAttribute(Constants.SESSION_DATA);
		if (sessionData != null)
		{
			cleanUp(sessionData, (String) session.getAttribute(Constants.RANDOM_NUMBER));
		}

		// To remove PrivilegeCache from the session, requires user LoginName
		// Singleton instance of PrivilegeManager
		if (sessionData != null)
		{
			PrivilegeManager privilegeManager;
			try
			{
				privilegeManager = PrivilegeManager.getInstance();
				privilegeManager.removePrivilegeCache(sessionData.getUserName());
			}
			catch (SMException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private void cleanUp(SessionDataBean sessionData, String randomNumber)
	{
		//Delete Advance Query table if exists
		//Advance Query table name with userID attached
		String tempTableName = edu.wustl.simplequery.global.Constants.QUERY_RESULTS_TABLE + "_"
				+ sessionData.getUserId();
		String appName = CommonServiceLocator.getInstance().getAppName();
		IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);

		try
		{
			//JDBCDAO jdbcDao = (JDBCDAO) DAOFactory.getInstance().getDAO(Constants.JDBC_DAO);
			JDBCDAO jdbcDao = daoFactory.getJDBCDAO();
			jdbcDao.openSession(sessionData);
			jdbcDao.deleteTable(tempTableName);
			jdbcDao.closeSession();
		}
		catch (DAOException ex)
		{
			Logger.out.error("Could not delete the Advance Search temporary table:" + tempTableName
					+ ex.getMessage(), ex);
		}
		String tempQueryTable = Constants.TEMP_OUPUT_TREE_TABLE_NAME + sessionData.getUserId()
				+ randomNumber;
		try
		{
			//JDBCDAO jdbcDao = (JDBCDAO) DAOFactory.getInstance().getDAO(Constants.JDBC_DAO);
			JDBCDAO jdbcDao = daoFactory.getJDBCDAO();
			jdbcDao.openSession(sessionData);
			jdbcDao.deleteTable(tempQueryTable);
			jdbcDao.closeSession();
		}
		catch (DAOException ex)
		{
			Logger.out.error("Could not delete the Query Module Search temporary table:"
					+ tempQueryTable + ex.getMessage(), ex);
		}
	}
}
