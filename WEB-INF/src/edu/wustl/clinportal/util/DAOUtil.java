
package edu.wustl.clinportal.util;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.exception.DAOException;

/**
 * 
 * @author deepali_ahirrao
 *
 */
public class DAOUtil
{
	/**
	 * 
	 * @return
	 * @throws DAOException
	 */
	public static JDBCDAO getJDBCDAO(SessionDataBean sessionDataBean) throws DAOException
	{

		String appName = CommonServiceLocator.getInstance().getAppName();
		JDBCDAO jdbcDao = DAOConfigFactory.getInstance().getDAOFactory(appName).getJDBCDAO();
		jdbcDao.openSession(sessionDataBean);
		return jdbcDao;
	}

	/**
	 * 
	 * @return
	 * @throws DAOException
	 */
	public static HibernateDAO getHibernateDAO(SessionDataBean sessionDataBean) throws DAOException
	{
		String appName = CommonServiceLocator.getInstance().getAppName();
		HibernateDAO hibernateDao = (HibernateDAO) DAOConfigFactory.getInstance().getDAOFactory(appName).getDAO();
		hibernateDao.openSession(sessionDataBean);
		return hibernateDao;
	}

	/**
	 * 
	 * @param jdbcDao
	 * @throws DAOException
	 */
	public static void closeJDBCDAO(JDBCDAO jdbcDao) throws DAOException
	{
		jdbcDao.closeSession();
	}

	/**
	 * 
	 * @param hibernateDao
	 * @throws DAOException
	 */
	public static void closeHibernateDAO(HibernateDAO hibernateDao) throws DAOException
	{
		hibernateDao.closeSession();
	}
}
