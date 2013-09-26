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
 * <p>Title: CaCoreAppServicesDelegator Class>
 * <p>Description:	This class contains the basic methods that are required
 * for HTTP APIs. It just passes on the request at proper place.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Aniruddha Phadnis
 * @version 1.00
 * Created on Jan 10, 2006
 */

package edu.wustl.clinportal.client;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.wustl.clinportal.bizlogic.ParticipantBizLogic;
import edu.wustl.clinportal.bizlogic.UserBizLogic;
import edu.wustl.clinportal.domain.Participant;
import edu.wustl.clinportal.domain.User;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.clinportal.util.global.DefaultValueManager;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.bizlogic.IBizLogic;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.global.PasswordManager;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.util.HibernateMetaData;
import edu.wustl.security.exception.SMException;
import edu.wustl.security.global.Permissions;
import edu.wustl.security.global.ProvisionManager;
import edu.wustl.security.manager.ISecurityManager;
import edu.wustl.security.manager.SecurityManagerFactory;
import edu.wustl.simplequery.bizlogic.QueryBizLogic;
import gov.nih.nci.security.authorization.domainobjects.Role;

/**
 * This class contains the basic methods that are required for HTTP APIs. 
 * It just passes on the request at proper place.
 * @author deepali_ahirrao
 * @version
 */
public class CaCoreAppServicesDelegator
{

	public static final String DOMAIN_OBJECT = "Domain Object";

	/**
	 * Passes User credentials to CaTissueHTTPClient to connect User with caTISSUE Core Application
	 * @param userName userName of the User to connect to caTISSUE Core Application
	 * @param password password of the User to connect to caTISSUE Core Application
	 * @throws Exception
	 * @return the sessionID of user if he/she has successfully logged in else null 
	 */
	public Boolean delegateLogin(String userName, String password) throws Exception
	{
		User validUser = getUser(userName);
		Boolean authenticated = Boolean.FALSE;
		if (validUser != null)
		{
			String encodedPassword = PasswordManager.encode(password);
			boolean loginOK = SecurityManagerFactory.getSecurityManager().login(userName,
					encodedPassword);
			authenticated = Boolean.valueOf(loginOK);
		}
		return authenticated;
	}

	/**
	 * Disconnects User from caTISSUE Core Application
	 * @param sessionKey
	 * @return returns the status of logout to caTISSUE Core Application
	 */
	public boolean delegateLogout(String sessionKey)// throws Exception
	{
		return false;
	}

	/**
	 * Passes caCore Like domain object to  caTissue Core biz logic to perform Add operation.
	 * @param domainObject the caCore Like object to add using HTTP API
	 * @param userName user name
	 * @return returns the Added caCore Like object/Exception object if exception occurs performing Add operation
	 * @throws Exception
	 */
	public Object delegateAdd(String userName, Object domainObject) throws Exception
	{
		try
		{
			/*
			if (domainObject == null) 
			{
				throw new Exception("Please enter valid domain object!! Domain object should not be NULL");
			}
			*/
			checkNullObject(domainObject, DOMAIN_OBJECT);
			IBizLogic bizLogic = getBizLogic(domainObject.getClass().getName());
			bizLogic.insert(domainObject, getSessionDataBean(userName), Constants.HIBERNATE_DAO);
			Logger.out.info(" Domain Object has been successfully inserted " + domainObject);
		}
		catch (Exception e)
		{
			Logger.out.error("Delegate Add-->" + e.getMessage());
			throw e;
		}
		return domainObject;
	}

	/**
	 * Passes caCore Like domain object to caTissue Core biz logic to perform Edit operation.
	 * @param domainObject the caCore Like object to edit using HTTP API
	 * @param userName  user name
	 * @return returns the Edited caCore Like object/Exception object if exception occurs performing Edit operation
	 * @throws Exception
	 */
	public Object delegateEdit(String userName, Object domainObject) throws Exception
	{
		String appName = CommonServiceLocator.getInstance().getAppName();
		IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
		DAO dao = null;
		try
		{

			checkNullObject(domainObject, DOMAIN_OBJECT);
			String objectName = domainObject.getClass().getName();
			IBizLogic bizLogic = getBizLogic(objectName);
			AbstractDomainObject absDomainObj = (AbstractDomainObject) domainObject;
			// not null check for Id
			checkNullObject(absDomainObj.getId(), "Identifier");
			List list = bizLogic.retrieve(objectName, Constants.SYSTEM_IDENTIFIER, absDomainObj
					.getId());

			if ((list == null) || (list.isEmpty()))
			{
				throw new Exception(
						"No such domain object found for update !! Please enter valid domain object for edit");
			}
			AbstractDomainObject abstractDomainOld = (AbstractDomainObject) list.get(0);
			//Session sessionClean = DBUtil.getCleanSession();
			dao = daoFactory.getDAO();
			/*Session sessionClean = dao.getCleanSession();
			
			abstractDomainOld = (AbstractDomainObject) sessionClean.load(Class.forName(objectName),
					Long.valueOf(absDomainObj.getId()));*/
			dao.openSession(null);
			dao.retrieveById(objectName, Long.valueOf(absDomainObj.getId()));
			bizLogic.update(absDomainObj, abstractDomainOld, Constants.HIBERNATE_DAO,
					getSessionDataBean(userName));
			//sessionClean.close();
			//dao.closeCleanSession();

			Logger.out.info(" Domain Object has been successfully updated " + domainObject);
		}
		catch (Exception e)
		{
			Logger.out.error("Delegate Edit" + e.getMessage());
			throw e;
		}
		finally
		{
			if (dao != null)
			{
				dao.closeSession();
			}
		}
		return domainObject;
	}

	/**
	 * @param userName user name
	 * @param domainObject domain object
	 * @return list of objects
	 * @throws Exception
	 */
	public List delegateGetObjects(String userName, Object domainObject) throws Exception
	{
		List searchObjects = new ArrayList();
		checkNullObject(domainObject, DOMAIN_OBJECT);
		String objectName = domainObject.getClass().getName();
		IBizLogic bizLogic = getBizLogic(objectName);
		AbstractDomainObject absDomainObj = (AbstractDomainObject) domainObject;
		// not null check for Id
		checkNullObject(absDomainObj.getId(), "Identifier");
		searchObjects = bizLogic.retrieve(objectName, Constants.SYSTEM_IDENTIFIER, absDomainObj
				.getId());

		if (searchObjects.isEmpty())
		{
			throw new Exception("Please enter valid domain object for search operation!!");
		}
		return searchObjects;
	}

	/**
	 * @param userName
	 * @param list
	 * @return
	 * @throws Exception
	 */
	public List delegateSearchFilter(String userName, List list) throws Exception
	{
		Logger.out.debug("User Name : " + userName);
		Logger.out.debug("list obtained from ApplicationService Search************** : "
				+ list.getClass().getName());
		Logger.out.debug("Super Class ApplicationService Search************** : "
				+ list.getClass().getSuperclass().getName());
		List filteredObjects = null;//new ArrayList();
		User validUser = getUser(userName);
		String reviewerRole = null;
		ISecurityManager securityManager = SecurityManagerFactory.getSecurityManager();
		try
		{
			Role role = securityManager.getUserRole(validUser.getCsmUserId());
			reviewerRole = role.getName();
		}
		catch (SMException ex)
		{
			Logger.out.info("Review Role not found!");
		}
		if (reviewerRole != null
				&& (reviewerRole
						.equalsIgnoreCase(edu.wustl.security.global.Constants.ADMINISTRATOR)))
		{
			filteredObjects = list;
		}
		else
		{
			try
			{
				filteredObjects = filterObjects(userName, list);
			}
			catch (Exception exp)
			{
				exp.printStackTrace();
				throw exp;
			}
		}

		return filteredObjects;
	}

	/**
	 * Filters the list of objects returned by the search depending on the privilege of the user on the objects.
	 * Also sets the identified data to null if the user doesn'r has privilege to see the identified data. 
	 * @param userName The name of the user whose privilege are to be checked.
	 * @param objectList The list of the objects which are to be filtered.
	 * @return The filtered list of objects according to the privilege of the user.
	 * @throws Exception 
	 */
	private List filterObjects(String userName, List objectList) throws Exception
	{
		Logger.out.debug("In Filter Objects ......");

		// boolean that indicates whether user has READ_DENIED privilege on the main object.
		boolean hasRdDeniedMain = false;

		// boolean that indicates whether user has privilege on identified data.
		boolean hasPrivOnIdfData = false;
		List filteredObjects = new ArrayList();

		Logger.out.debug("Total Objects>>>>>>>>>>>>>>>>>>>>>" + objectList.size());
		Iterator iterator = objectList.iterator();
		while (iterator.hasNext())
		{

			Object absDomainObj = (Object) iterator.next();//objectList.get(i);

			//Get identifier of the object. 
			Object identifier = getFieldObject(absDomainObj, "id");
			Logger.out.debug("object Identifier......................: " + identifier);

			String aliasName = getAliasName(absDomainObj);

			// Check the permission of the user on the main object.
			/*hasRdDeniedMain = SecurityManagerFactory.getSecurityManager()
					.checkPermission(userName, aliasName, identifier, Permissions.READ_DENIED);*/
			hasRdDeniedMain = ProvisionManager.getInstance().getAuthorizationManager()
					.checkPermission(userName, aliasName, identifier.toString(),
							Permissions.READ_DENIED);

			Logger.out.debug("Main object:" + aliasName + " Has READ_DENIED privilege:"
					+ hasRdDeniedMain);

			if (!hasRdDeniedMain)// In case of no READ_DENIED privilege, check for privilege on identified data. 
			{
				//Check the permission of the user on the identified data of the object.
				/*hasPrivOnIdfData = SecurityManager.getInstance(CaCoreAppServicesDelegator.class)
						.checkPermission(userName, aliasName, identifier, Permissions.PHI);*/
				hasPrivOnIdfData = ProvisionManager.getInstance().getAuthorizationManager()
						.checkPermission(userName, aliasName, identifier.toString(),
								Permissions.PHI);

				Logger.out.debug("hasPrivilegeOnIdentifiedData:" + hasPrivOnIdfData);
				// If has no read privilege on identified data, set the identified attributes as NULL. 
				if (hasPrivOnIdfData == false)
				{
					// commented because of lazy initialization problem
					removeIdentifiedDataFromObject(absDomainObj);
				}

				filteredObjects.add(absDomainObj);
				Logger.out.debug("Intermediate Size of filteredObjects .............."
						+ filteredObjects.size());
			}
		}

		Logger.out.debug("Before Final Objects >>>>>>>>>>>>>>>>>>>>>>>>>" + filteredObjects.size());

		return filteredObjects;
	}

	/**
	 * Returns the alias name of the domain object passed.   
	 * @param object The domain object whose alias name is to be found. 
	 * @return the alias name of the domain object passed.
	 * @throws ClassNotFoundException
	 * @throws DAOException
	 * @throws BizLogicException 
	 */
	private String getAliasName(Object object) throws ClassNotFoundException, DAOException,
			BizLogicException
	{
		Class className = object.getClass();
		String domainObjClsName = edu.wustl.common.util.Utility.parseClassName(className.getName());
		String domainClassName = domainObjClsName;

		Logger.out.debug("Class Name >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + domainClassName);
		Logger.out.info("Class Name >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + domainClassName);
		try
		{
			className = Class.forName("edu.wustl.clinportal.domain." + domainClassName);
		}
		catch (ClassNotFoundException ex)
		{
			Logger.out
					.error("ClassNotFoundException in CaCoreAppServicesDelegator.getAliasName() method");
			className = Class.forName(object.getClass().getName());
		}
		String tableName = "'" + HibernateMetaData.getTableName(className) + "'";

		IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
		/*QueryBizLogic bizLogic = (QueryBizLogic) BizLogicFactory.getInstance().getBizLogic(
				edu.wustl.common.util.global.Constants.QUERY_INTERFACE_ID);*/
		QueryBizLogic bizLogic = (QueryBizLogic) factory
				.getBizLogic(edu.wustl.common.util.global.Constants.QUERY_INTERFACE_ID);
		String aliasName = bizLogic.getAliasName(Constants.TABLE_NAME_COLUMN, tableName);
		return aliasName;
	}

	/**
	 * Removes the identified data from Participant object.
	 * @param object The Participant object.
	 */
	private void removeParticipantIdentifiedData(Object object)
	{
		Participant participant = (Participant) object;
		participant.setFirstName(null);
		participant.setLastName(null);
		participant.setMiddleName(null);
		participant.setBirthDate(null);
		participant.setSocialSecurityNumber(null);

	}

	/**
	 * Sets value of the identified data fields as null in the passed domain object. 
	 * Checks the type of the object and calls the respective method which filters the identified data.
	 * @param object The domain object whose identified data is to be removed.
	 * @throws DAOException 
	 * @throws BizLogicException 
	 */
	private void removeIdentifiedDataFromObject(Object object) throws DAOException,
			BizLogicException
	{
		Class classObject = object.getClass();
		Logger.out.debug("Identified Class>>>>>>>>>>>>>>>>>>>>>>" + classObject.getName());
		if (classObject.equals(Participant.class))
		{
			removeParticipantIdentifiedData(object);
		}

	}

	/**
	 * Returns the field object from the class object and field name passed.
	 * @param object
	 * @param fieldName
	 * @return
	 */
	private Object getFieldObject(Object object, String fieldName)
	{
		Object childObject = null;
		String field = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		Logger.out.debug("Method Name***********************" + field);

		try
		{
			childObject = (Object) object.getClass().getMethod(field, null).invoke(object, null);
		}
		catch (NoSuchMethodException noMetExp)
		{
			Logger.out.debug(noMetExp.getMessage(), noMetExp);
		}
		catch (IllegalAccessException illAccExp)
		{
			Logger.out.debug(illAccExp.getMessage(), illAccExp);
		}
		catch (InvocationTargetException invoTarExp)
		{
			Logger.out.debug(invoTarExp.getMessage(), invoTarExp);
		}

		return childObject;
	}

	/**
	 * Get Biz Logic based on domain object class name.
	 * @param domainObjectName name of domain object
	 * @return biz logic
	 * @throws BizLogicException 
	 */
	private IBizLogic getBizLogic(String domainObjectName) throws BizLogicException
	{
		//BizLogicFactory factory = BizLogicFactory.getInstance();
		IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
		return factory.getBizLogic(domainObjectName);
	}

	/**
	 * Get session data bean.
	 * @param userName user name
	 * @return session data bean
	 */
	private SessionDataBean getSessionDataBean(String userName)
	{
		SessionDataBean sessionDataBean = new SessionDataBean();
		sessionDataBean.setUserName(userName);

		return sessionDataBean;
	}

	/**
	 * check whether the object is null or not
	 * @param domainObject domain object
	 * @param messageToken  message token 
	 * @throws Exception
	 */
	private void checkNullObject(Object domainObject, String messageToken) throws Exception
	{
		if (domainObject == null)
		{
			throw new Exception("Please enter valid " + messageToken + "!! " + messageToken
					+ " should not be NULL");
		}
	}

	/**
	 * Gets the user detail on the basis of login name
	 * @param loginName login Name
	 * @return User object
	 * @throws BizLogicException 
	 * @throws DAOException
	 */
	private User getUser(String loginName) throws BizLogicException //throws DAOException
	{
		User validUser = null;
		IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
		/*	UserBizLogic userBizLogic = (UserBizLogic) BizLogicFactory.getInstance().getBizLogic(
					User.class.getName());*/
		UserBizLogic userBizLogic = (UserBizLogic) factory.getBizLogic(User.class.getName());
		String[] whereColumnName = {"activityStatus", "loginName"};
		String[] whrColCondition = {"=", "="};
		String[] whereColumnValue = {Constants.ACTIVITY_STATUS_ACTIVE, loginName};

		List users = userBizLogic.retrieve(User.class.getName(), whereColumnName, whrColCondition,
				whereColumnValue, Constants.AND_JOIN_CONDITION);

		if (!users.isEmpty())
		{
			validUser = (User) users.get(0);
		}
		return validUser;
	}

	/**
	 * Find out the matching participant list based on the participant object provided
	 * @param userName
	 * @param domainObject
	 * @return
	 * @throws Exception
	 */
	public List delegateGetParticipantMatchingObects(String userName, Object domainObject)
			throws Exception
	{
		List matchingObjects = new ArrayList();
		checkNullObject(domainObject, DOMAIN_OBJECT);
		String className = domainObject.getClass().getName();
		IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
		/*ParticipantBizLogic bizLogic = (ParticipantBizLogic) BizLogicFactory.getInstance()
				.getBizLogic(className);*/
		ParticipantBizLogic bizLogic = (ParticipantBizLogic) factory.getBizLogic(className);
		//AbstractDomainObject absDomainObj = (AbstractDomainObject) domainObject;

		matchingObjects = bizLogic.getListOfMatchingParticipants((Participant) domainObject,
				getSessionDataBean(userName), null);
		return matchingObjects;
	}

	/**
	 * Method to get default value for given key using default value manager
	 * @param userName
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public String delegateGetDefaultValue(String userName, Object obj) throws Exception
	{
		return ((String) DefaultValueManager.getDefaultValue((String) obj));
	}

	public void auditAPIQuery(String queryObject, String userName) throws Exception
	{
		System.out.println("CaCoreAppServicesDelegator.auditAPIQuery()");
		System.out.println("--" + queryObject + "===" + userName);
		SessionDataBean sessionDataBean = getSessionDataBean(userName);
		insertQuery(queryObject, sessionDataBean);
	}

	/***
	 * Copy paste from Query Bizlogic class to insert API query Log
	 * @param sqlQuery
	 * @param sessionData
	 * @throws DAOException
	 * @throws ClassNotFoundException
	 */
	private void insertQuery(String sqlQuery, SessionDataBean sessionData) throws Exception
	{
		System.out.println("CaCoreAppServicesDelegator.insertQuery()" + sqlQuery);
		/*String appName = CommonServiceLocator.getInstance().getAppName();
		IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
		JDBCDAO jdbcDAO = (JDBCDAO) daoFactory.getJDBCDAO();
		try
		{

			String sqlQuery1 = sqlQuery.replaceAll("'", "''");
			long no = 1;

			jdbcDAO.openSession(null);

			SimpleDateFormat fSDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String timeStamp = fSDateFormat.format(new Date());

			String ipAddr = sessionData.getIpAddress();

			String userId = sessionData.getUserId().toString();
			String comments = "APIQueryLog";

			
				String sql = "select CATISSUE_AUDIT_EVENT_PARAM_SEQ.nextVal from dual";

				List list = jdbcDAO.retrieve(sql);

				if (!list.isEmpty())
				{

					List columnList = (List) list.get(0);
					if (!columnList.isEmpty())
					{
						String str = (String) columnList.get(0);
						if (!str.equals(""))
						{
							no = Long.parseLong(str);

						}
					}
				}
				String sqlForAudiEvent = "insert into catissue_audit_event(IDENTIFIER,IP_ADDRESS,EVENT_TIMESTAMP,USER_ID ,COMMENTS) values ('"
						+ no
						+ "','"
						+ ipAddr
						+ "',to_date('"
						+ timeStamp
						+ "','yyyy-mm-dd HH24:MI:SS'),'" + userId + "','" + comments + "')";
				Logger.out.info("sqlForAuditLog:" + sqlForAudiEvent);
				jdbcDAO.executeUpdate(sqlForAudiEvent);

				long queryNo = 1;
				sql = "select CATISSUE_AUDIT_EVENT_QUERY_SEQ.nextVal from dual";

				list = jdbcDAO.retrieve(sql);

				if (!list.isEmpty())
				{

					List columnList = (List) list.get(0);
					if (!columnList.isEmpty())
					{
						String str = (String) columnList.get(0);
						if (!str.equals(""))
						{
							queryNo = Long.parseLong(str);

						}
					}
				}
				String sqlForQueryLog = "insert into catissue_audit_event_query_log(IDENTIFIER,QUERY_DETAILS,AUDIT_EVENT_ID) "
						+ "values (" + queryNo + ",EMPTY_CLOB(),'" + no + "')";
				jdbcDAO.executeUpdate(sqlForQueryLog);
				String sql1 = "select QUERY_DETAILS from catissue_audit_event_query_log where IDENTIFIER="+queryNo+" for update";
				list = jdbcDAO.retrieve(sql1 );
				System.out.println("CaCoreAppServicesDelegator.insertQuery()---2=="+sql1);
				CLOB clob=null;
				
				if (!list.isEmpty())
				{

					List columnList = (List) list.get(0);
					if (!columnList.isEmpty())
					{
						clob = (CLOB)columnList.get(0);
					}
				}
		//					get output stream from the CLOB object
				OutputStream os = clob.getAsciiOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(os);
				
		//				use that output stream to write character data to the Oracle data store
				osw.write(sqlQuery1.toCharArray());
				//write data and commit
				osw.flush();
				osw.close();
				os.close();
				jdbcDAO.commit();
				Logger.out.info("sqlForQueryLog:" + sqlForQueryLog);


			
		}
		catch(Exception e)
		{ 
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		finally
		{
			jdbcDAO.closeSession();
		}*/
		/*String sqlForQueryLog = "insert into catissue_audit_event_query_log(IDENTIFIER,QUERY_DETAILS) values ('"
		 + no + "','" + sqlQuery1 + "')";
		 jdbcDAO.executeUpdate(sqlForQueryLog);*/

	}

}