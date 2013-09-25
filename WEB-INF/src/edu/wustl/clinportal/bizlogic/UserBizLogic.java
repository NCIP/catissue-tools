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
 * <p>Title: UserBizLogic Class>
 * <p>Description:	UserBizLogic is used to add user information into the database using Hibernate.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 * Created on Apr 13, 2005
 */

package edu.wustl.clinportal.bizlogic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Vector;

import edu.wustl.clinportal.bizlogic.util.BizLogicUtility;
import edu.wustl.clinportal.domain.CancerResearchGroup;
import edu.wustl.clinportal.domain.Department;
import edu.wustl.clinportal.domain.Institution;
import edu.wustl.clinportal.domain.Password;
import edu.wustl.clinportal.domain.User;
import edu.wustl.clinportal.util.ApiSearchUtil;
import edu.wustl.clinportal.util.EmailHandler;
import edu.wustl.clinportal.util.Roles;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.actionForm.IValueObject;
import edu.wustl.common.audit.AuditManager;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.cde.CDEManager;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.AuditException;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.exception.PasswordEncryptionException;
import edu.wustl.common.exceptionformatter.DefaultExceptionFormatter;
import edu.wustl.common.util.XMLPropertyHandler;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.PasswordManager;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.QueryWhereClause;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.security.beans.SecurityDataBean;
import edu.wustl.security.exception.SMException;
import edu.wustl.security.global.Permissions;
import edu.wustl.security.manager.SecurityManagerFactory;
import edu.wustl.security.privilege.PrivilegeCache;
import edu.wustl.security.privilege.PrivilegeManager;
import edu.wustl.security.privilege.PrivilegeUtility;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElement;
import gov.nih.nci.security.authorization.domainobjects.Role;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;

/**
 * UserBizLogic is used to add user information into the database using Hibernate.
 * @author falguni_sachde
 *
 */
public class UserBizLogic extends ClinportalDefaultBizLogic
{

	private transient Logger logger = Logger.getCommonLogger(UserBizLogic.class);

	public static final int FAIL_SAME_AS_LAST_N = 8;
	public static final int FAIL_FIRST_LOGIN = 9;
	public static final int FAIL_EXPIRE = 10;
	public static final int FAIL_CHANGED_WITHIN_SOME_DAY = 11;
	public static final int FAIL_SAME_NAME_SURNAME_EMAIL = 12;
	public static final int FAIL_PASSWORD_EXPIRED = 13;

	public static final int SUCCESS = 0;

	/**
	 * Saves the user object in the database.
	 * @param obj The user object to be saved.
	 * @param dao
	 * @param sessionDataBean The session in which the object is saved.
	 * @throws BizLogicException
	 */
	protected void insert(Object obj, DAO dao, SessionDataBean sessionDataBean)
			throws BizLogicException
	{
		User user = (User) obj;
		gov.nih.nci.security.authorization.domainobjects.User csmUser = new gov.nih.nci.security.authorization.domainobjects.User();
		try
		{
			setUserData(user, dao);
			createCSMUser(user, csmUser);
			// Create address and the user in catissue tables.
			dao.insert(user.getAddress());
			AuditManager auditManager = getAuditManager(sessionDataBean);
			auditManager.insertAudit(dao, user.getAddress());
			dao.insert(user);
			auditManager.insertAudit(dao, user);
			Set protectionObjects = new HashSet();
			protectionObjects.add(user);

			EmailHandler emailHandler = new EmailHandler();
			// Send the user registration email to user and the administrator.
			if (Constants.PAGEOF_SIGNUP.equals(user.getPageOf()))
			{
				emailHandler.sendUserSignUpEmail(user);
			}
			else
			{
				// Send the user creation email to user and the administrator.			
				PrivilegeManager privilegeManager = PrivilegeManager.getInstance();
				privilegeManager.insertAuthorizationData(getAuthorizationData(user),
						protectionObjects, null, user.getObjectId());
				emailHandler.sendApprovalEmail(user);
			}
		}
		catch (DAOException daoExp)
		{
			logger.debug(daoExp.getMessage(), daoExp);
			BizLogicUtility.deleteCSMUser(csmUser);
			throw new BizLogicException(daoExp);
		}
		catch (SMException e)
		{
			// added to format constraint violation message
			BizLogicUtility.deleteCSMUser(csmUser);
			throw handleSMException(e);
		}
		catch (PasswordEncryptionException e)
		{
			logger.debug(e.getMessage(), e);
			BizLogicUtility.deleteCSMUser(csmUser);
			throw getBizLogicException(null, null, "Error in password encryption");
		}
		catch (AuditException e)
		{
			logger.debug(e.getMessage(), e);
			BizLogicUtility.deleteCSMUser(csmUser);
			throw getBizLogicException(e, e.getErrorKeyName(), e.getMsgValues());
		}
	}

	/**
	 * @param user
	 * @param csmUser
	 * @throws DAOException
	 * @throws SMException
	 * @throws PasswordEncryptionException
	 */
	private void createCSMUser(User user,
			gov.nih.nci.security.authorization.domainobjects.User csmUser) throws DAOException,
			SMException, PasswordEncryptionException
	{
		// If the page is of sign up user don't create the csm user.
		if (!user.getPageOf().equals(Constants.PAGEOF_SIGNUP))
		{
			String generatedPassword = PasswordManager.generatePassword();

			csmUser.setLoginName(user.getLoginName());
			csmUser.setLastName(user.getLastName());
			csmUser.setFirstName(user.getFirstName());
			csmUser.setEmailId(user.getEmailAddress());
			csmUser.setStartDate(user.getStartDate());
			csmUser.setPassword(generatedPassword);
			SecurityManagerFactory.getSecurityManager().createUser(csmUser);
			if (user.getRoleId() != null)
			{
				SecurityManagerFactory.getSecurityManager().assignRoleToUser(
						csmUser.getUserId().toString(), user.getRoleId());
			}
			user.setCsmUserId(csmUser.getUserId());
			//Add password of user in password table.Updated by Supriya Dankh		 
			Password password = new Password();
			ApiSearchUtil.setPasswordDefault(password);
			//End:-  Change for API Search 
			password.setUser(user);
			password.setPassword(PasswordManager.encrypt(generatedPassword));
			password.setUpdateDate(new Date());
			user.getPasswordCollection().add(password);
			logger.debug("password stored in password table");
		}
	}

	/**
	 * 
	 * @param user
	 * @param dao
	 * @throws DAOException
	 */
	private void setUserData(User user, DAO dao) throws DAOException
	{
		List list = dao.retrieve(Department.class.getName(), Constants.SYSTEM_IDENTIFIER, user
				.getDepartment().getId());
		Department department = null;
		if (!list.isEmpty())
		{
			department = (Department) list.get(0);
		}
		list = dao.retrieve(Institution.class.getName(), Constants.SYSTEM_IDENTIFIER, user
				.getInstitution().getId());
		Institution institution = null;
		if (!list.isEmpty())
		{
			institution = (Institution) list.get(0);
		}
		list = dao.retrieve(CancerResearchGroup.class.getName(), Constants.SYSTEM_IDENTIFIER, user
				.getCancerResearchGroup().getId());
		CancerResearchGroup cancerReserchGrp = null;
		if (!list.isEmpty())
		{
			cancerReserchGrp = (CancerResearchGroup) list.get(0);
		}
		user.setDepartment(department);
		user.setInstitution(institution);
		user.setCancerResearchGroup(cancerReserchGrp);

		/**
		 *  First time login is always set to true when a new user is created
		 */
		user.setFirstTimeLogin(Boolean.TRUE);
	}

	/**
	 * This method returns collection of UserGroupRoleProtectionGroup objects that speciefies the 
	 * user group protection group linkage through a role. It also specifies the groups the protection  
	 * elements returned by this class should be added to.
	 * @param obj
	 * @return
	 * @throws SMException
	 */
	private List getAuthorizationData(AbstractDomainObject obj) throws SMException
	{
		logger.debug("--------------- In here ---------------");

		List authorizationData = new ArrayList();
		Set group = new HashSet();
		User aUser = (User) obj;

		String userId = String.valueOf(aUser.getCsmUserId());
		gov.nih.nci.security.authorization.domainobjects.User user = SecurityManagerFactory
				.getSecurityManager().getUserById(userId);
		logger.debug(" User: " + user.getLoginName());
		group.add(user);

		// Protection group of User
		String protnGrpName = Constants.getUserPGName(aUser.getId());
		SecurityDataBean usGrRlPrtGrpBean = new SecurityDataBean();
		usGrRlPrtGrpBean.setUser(userId);
		usGrRlPrtGrpBean.setRoleName(Roles.UPDATE_ONLY);
		usGrRlPrtGrpBean.setGroupName(Constants.getUserGroupName(aUser.getId()));
		usGrRlPrtGrpBean.setProtGrpName(protnGrpName);
		usGrRlPrtGrpBean.setGroup(group);
		authorizationData.add(usGrRlPrtGrpBean);

		logger.debug(authorizationData.toString());
		return authorizationData;
	}

	/**
	 * Updates the persistent object in the database.
	 * @param dao
	 * @param obj The object to be updated.
	 * @param oldObj
	 * @param sessionDataBean The session in which the object is saved.
	 * @throws BizLogicException 
	 */
	protected void update(DAO dao, Object obj, Object oldObj, SessionDataBean sessionDataBean)
			throws BizLogicException
	{
		User user = (User) obj;
		User oldUser = (User) oldObj;
		boolean isLoginUserUpdate = false;
		AuditManager auditManager = getAuditManager(sessionDataBean);
		if (sessionDataBean.getUserName().equals(oldUser.getLoginName()))
		{
			isLoginUserUpdate = true;
		}

		checkActivityStatus(oldUser);

		try
		{
			// Get the csm userId if present. 
			String csmUserId = null;
			checkFirstTimeLogin(user, oldUser);
			if (user.getCsmUserId() != null)
			{
				csmUserId = user.getCsmUserId().toString();
			}
			gov.nih.nci.security.authorization.domainobjects.User csmUser = SecurityManagerFactory
					.getSecurityManager().getUserById(csmUserId);

			// If the page is of change password, 
			// update the password of the user in csm and catissue tables. 
			if (user.getPageOf().equals(Constants.PAGEOF_CHANGE_PASSWORD))
			{
				changePassword(user, oldUser, csmUser);
			}
			//Bug-1516: Administrator should be able to edit the password 
			else if (user.getPageOf().equals(Constants.PAGEOF_USER_ADMIN)
					&& !user.getNewPassword().equals(csmUser.getPassword()))
			{
				Validator validator = new Validator();
				if (!validator.isEmpty(user.getNewPassword()))
				{
					validatePasswordAndCheck(oldUser, user);
				}
				csmUser.setPassword(user.getNewPassword());
				// Assign Role only if the page is of Administrative user edit.
				if (!(Constants.PAGEOF_USER_PROFILE.equals(user.getPageOf()))
						&& !(Constants.PAGEOF_CHANGE_PASSWORD.equals(user.getPageOf())))
				{
					SecurityManagerFactory.getSecurityManager().assignRoleToUser(
							csmUser.getUserId().toString(), user.getRoleId());
				}
				// Set values in password domain object and adds changed password in Password Collection
				Password password = new Password(PasswordManager.encrypt(user.getNewPassword()),
						user);
				user.getPasswordCollection().add(password);
				user.setFirstTimeLogin(Boolean.TRUE);
			}
			else
			{
				csmUser.setLoginName(user.getLoginName());
				csmUser.setLastName(user.getLastName());
				csmUser.setFirstName(user.getFirstName());
				csmUser.setEmailId(user.getEmailAddress());
				// Assign Role only if the page is of Administrative user edit.
				if (!(Constants.PAGEOF_USER_PROFILE.equals(user.getPageOf()))
						&& !(Constants.PAGEOF_CHANGE_PASSWORD.equals(user.getPageOf())))
				{
					SecurityManagerFactory.getSecurityManager().assignRoleToUser(
							csmUser.getUserId().toString(), user.getRoleId());
				}
				dao.update(user.getAddress());
				// Audit of user address.
				auditManager.updateAudit(dao, user.getAddress(), oldUser.getAddress());
			}
			if (user.getPageOf().equals(Constants.PAGEOF_CHANGE_PASSWORD))
			{
				user.setFirstTimeLogin(Boolean.FALSE);
			}

			boolean isStChngdToClosd = false, flag = true;
			//Modify the csm user.
			SecurityManagerFactory.getSecurityManager().modifyUser(csmUser);
			if (isLoginUserUpdate)
			{
				sessionDataBean.setUserName(csmUser.getLoginName());
			}
			//Audit of user.
			if (Constants.ACTIVITY_STATUS_ACTIVE.equals(user.getActivityStatus()))
			{
				Set protectionObjects = new HashSet();
				protectionObjects.add(user);
				PrivilegeManager privilegeManager = PrivilegeManager.getInstance();
				PrivilegeUtility privilegeUtility = new PrivilegeUtility();
				try
				{
					ProtectionElement protectionElement = privilegeUtility
							.getUserProvisioningManager().getProtectionElement(
									User.class.getName() + "_" + user.getId().toString());
				}
				catch (CSObjectNotFoundException e)
				{
					flag = false;
					privilegeManager.insertAuthorizationData(getAuthorizationData(user),
							protectionObjects, null, user.getObjectId());
				}
				if (flag)
					privilegeManager.insertAuthorizationData(getAuthorizationData(user), null,
							null, user.getObjectId());
			}
			else if (Constants.ACTIVITY_STATUS_CLOSED.equals(user.getActivityStatus())
					&& Constants.ACTIVITY_STATUS_ACTIVE.equals(oldUser.getActivityStatus()))
			{
				isStChngdToClosd = true;
			}
			dao.update(user);
			auditManager.updateAudit(dao, user, oldUser);
			if (isStChngdToClosd)
			{
				EmailHandler emailHandler = new EmailHandler();
				emailHandler.sendUserAccountClosureEmail(user);
			}
		}
		catch (SMException e)
		{
			throw handleSMException(e);
		}
		catch (PasswordEncryptionException e)
		{
			throw getBizLogicException(null, null, "Error in password encryption");
		}
		catch (DAOException e)
		{
			throw new BizLogicException(e);
		}
		catch (AuditException e)
		{
			logger.debug(e.getMessage(), e);
			throw getBizLogicException(e, e.getErrorKeyName(), e.getMsgValues());
		}
	}

	/**
	 * 
	 * @param oldUser
	 * @throws BizLogicException
	 */
	private void checkActivityStatus(User oldUser) throws BizLogicException
	{
		//		If the user is rejected, its record cannot be updated.
		if (Constants.ACTIVITY_STATUS_REJECT.equals(oldUser.getActivityStatus()))
		{
			throw getBizLogicException(null, "errors.editRejectedUser", "");
		}
		else if (Constants.ACTIVITY_STATUS_NEW.equals(oldUser.getActivityStatus())
				|| Constants.ACTIVITY_STATUS_PENDING.equals(oldUser.getActivityStatus()))
		{
			//If the user is not approved yet, its record cannot be updated.			
			throw getBizLogicException(null, "errors.editNewPendingUser", "");
		}
	}

	/**
	 * 
	 * @param user
	 * @param oldUser
	 * @throws BizLogicException 
	 */
	private void checkFirstTimeLogin(User user, User oldUser) throws BizLogicException
	{
		/**
		 *  Changes done for Api
		 * User should not edit the first time login field.
		 */
		if (user.getFirstTimeLogin() == null)
		{
			throw getBizLogicException(null, "domain.object.null.err.msg", "First Time Login");
		}

		if (oldUser.getFirstTimeLogin() != null
				&& user.getFirstTimeLogin().booleanValue() != oldUser.getFirstTimeLogin()
						.booleanValue())
		{
			throw getBizLogicException(null, "errors.cannotedit.firsttimelogin", "");
		}
	}

	/**
	 * Update the password of the user in csm and clinportal tables.
	 * @param user
	 * @param oldUser
	 * @param csmUser
	 * @throws PasswordEncryptionException
	 * @throws BizLogicException 
	 */
	private void changePassword(User user, User oldUser,
			gov.nih.nci.security.authorization.domainobjects.User csmUser)
			throws PasswordEncryptionException, BizLogicException
	{
		String oldPassword = user.getOldPassword();
		if (!oldPassword.equals(csmUser.getPassword()))
		{
			throw getBizLogicException(null, "errors.oldPassword.wrong", "");
		}
		//Added for Password validation 
		Validator validator = new Validator();
		if (!validator.isEmpty(user.getNewPassword()) && !validator.isEmpty(oldPassword))
		{
			validatePasswordAndCheck(oldUser, user);

		}
		csmUser.setPassword(user.getNewPassword());
		// Set values in password domain object and adds changed password in Password Collection
		Password password = new Password(PasswordManager.encrypt(user.getNewPassword()), user);
		user.getPasswordCollection().add(password);
	}

	/**
	 * @param oldUser
	 * @param user
	 * @throws PasswordEncryptionException 
	 * @throws BizLogicException 
	 */
	private void validatePasswordAndCheck(User oldUser, User user) throws BizLogicException,
			PasswordEncryptionException
	{
		int result = validatePassword(oldUser, user.getNewPassword());
		logger.debug("return from Password validate " + result);
		//if validatePassword method returns value greater than zero then validation fails
		if (result != SUCCESS)
		{
			// get error message of validation failure 
			String errorMessage = getPasswordErrorMsg(result);
			logger.debug("Error Message from method" + errorMessage);
			throw getBizLogicException(null, "error.security", errorMessage);
		}
	}

	/**
	 * Returns the list of NameValueBeans with name as "LastName,Firstname" 
	 * and value as systemtIdentifier, of all users who are not disabled. 
	 * @param operation
	 * @return the list of NameValueBeans with name as "LastName,Firstname" 
	 * and value as systemtIdentifier, of all users who are not disabled.
	 * @throws BizLogicException 
	 */
	public List getUsers(String operation) throws BizLogicException
	{
		String sourceObjectName = User.class.getName();
		//Get only the fields required 
		String[] selectColumnName = {Constants.SYSTEM_IDENTIFIER, Constants.LASTNAME,
				Constants.FIRSTNAME};
		String[] whereColumnName;
		String[] whrColCondn;
		Object[] whereColumnValue;
		String joinCondition = null;
		List users = null;
		if (operation != null && operation.equalsIgnoreCase(Constants.ADD))
		{
			String[] tmpArray1 = {Constants.ACTIVITY_STATUS};
			String[] tmpArray2 = {Constants.EQUALS};
			String[] tmpArray3 = {Constants.ACTIVITY_STATUS_ACTIVE};
			whereColumnName = tmpArray1;
			whrColCondn = tmpArray2;
			whereColumnValue = tmpArray3;
			String hql = "from User usr where " + tmpArray1[0] + " " + tmpArray2[0] + " '"
					+ tmpArray3[0] + "' order by usr.lastName";
			users = this.executeQuery(hql);			
		}
		else
		{
			String[] tmpArray1 = {Constants.ACTIVITY_STATUS, Constants.ACTIVITY_STATUS};
			String[] tmpArray2 = {Constants.EQUALS, Constants.EQUALS};
			String[] tmpArray3 = {Constants.ACTIVITY_STATUS_ACTIVE,
					Constants.ACTIVITY_STATUS_CLOSED};
			whereColumnName = tmpArray1;
			whrColCondn = tmpArray2;
			whereColumnValue = tmpArray3;
			joinCondition = Constants.OR_JOIN_CONDITION;
			String hql = "from User usr where " + tmpArray1[0] + " " + tmpArray2[0] + " '"
					+ tmpArray3[0] + "' " + joinCondition + " " + tmpArray1[1] + " " + tmpArray2[1]
					+ " '" + tmpArray3[1] + "' order by usr.lastName";
			users = this.executeQuery(hql);
		}

		//executeQuery
		//Retrieve the users whose activity status is not disabled.

		List nameValuePairs = new ArrayList();
		nameValuePairs.add(new NameValueBean(Constants.SELECT_OPTION, String
				.valueOf(Constants.SELECT_OPTION_VALUE)));

		// If the list of users retrieved is not empty. 
		if (!users.isEmpty())
		{
			// Creating name value beans.
			for (int i = 0; i < users.size(); i++)
			{
				//Changes made to optimize the query to get only required fields data
				User userData = (User) users.get(i);
				NameValueBean nameValueBean = new NameValueBean(); // NOPMD - Instantiating NameValueBean  in loop
				nameValueBean.setName(userData.getLastName() + ", " + userData.getFirstName());
				nameValueBean.setValue(userData.getId());
				nameValuePairs.add(nameValueBean);

			}
		}
		
		return nameValuePairs;
	}

	/**
	 * Returns the list of NameValueBeans with name as "LastName,Firstname" 
	 * and value as systemtIdentifier, of all users who are not disabled. 
	 * @return the list of NameValueBeans with name as "LastName,Firstname" 
	 * and value as systemtIdentifier, of all users who are not disabled.
	 * @throws DAOException
	 * @throws SMException
	 */
	public List getCSMUsers() throws DAOException, SMException
	{
		//Retrieve the users whose activity status is not disabled.
		List users = SecurityManagerFactory.getSecurityManager().getUsers();

		List nameValuePairs = new ArrayList();
		nameValuePairs.add(new NameValueBean(Constants.SELECT_OPTION, String
				.valueOf(Constants.SELECT_OPTION_VALUE)));

		// If the list of users retrieved is not empty. 
		if (!users.isEmpty())
		{
			// Creating name value beans.
			for (int i = 0; i < users.size(); i++)
			{
				gov.nih.nci.security.authorization.domainobjects.User user = (gov.nih.nci.security.authorization.domainobjects.User) users
						.get(i);
				NameValueBean nameValueBean = new NameValueBean(); // NOPMD - Instantiating NameValueBean in loop
				nameValueBean.setName(user.getLastName() + ", " + user.getFirstName());
				nameValueBean.setValue(String.valueOf(user.getUserId()));
				logger.debug(nameValueBean.toString());
				nameValuePairs.add(nameValueBean);
			}
		}

		Collections.sort(nameValuePairs);
		return nameValuePairs;
	}

	/**
	 * Returns a list of users according to the column name and value.
	 * @param className
	 * @param colName column name on the basis of which the user list is to be retrieved.
	 * @param colValue Value for the column name.
	 * @return
	 * @throws BizLogicException
	 */
	public List retrieve(String className, String colName, Object colValue)
			throws BizLogicException
	{
		List userList = null;
		logger.debug("In user biz logic retrieve........................");
		try
		{
			// Get the caTISSUE user.
			userList = super.retrieve(className, colName, colValue);
			if (userList != null && !userList.isEmpty())
			{
				User appUser = (User) userList.get(0);

				if (appUser.getCsmUserId() != null)
				{
					//Get the role of the user.
					Role role = SecurityManagerFactory.getSecurityManager().getUserRole(
							appUser.getCsmUserId().longValue());
					//logger.debug("In USer biz logic.............role........id......." + role.getId().toString());

					if (role != null)
					{
						appUser.setRoleId(role.getId().toString());
					}
				}
			}
		}
		catch (SMException e)
		{
			throw handleSMException(e);
		}

		return userList;
	}

	/**
	 * Retrieves and sends the login details email to the user whose email address is passed 
	 * else returns the error key in case of an error.  
	 * @param emailAddress the email address of the user whose password is to be sent.
	 * @param sessionData
	 * @return the error key in case of an error.
	 * @throws DAOException
	 * @throws BizLogicException 
	 */
	public String sendForgotPassword(String emailAddress, SessionDataBean sessionData)
			throws DAOException, BizLogicException
	{
		String statusMessageKey = null;
		List list = retrieve(User.class.getName(), "emailAddress", emailAddress);
		if (list != null && !list.isEmpty())
		{
			User user = (User) list.get(0);
			if (user.getActivityStatus().equals(Constants.ACTIVITY_STATUS_ACTIVE))
			{
				EmailHandler emailHandler = new EmailHandler();

				//Send the login details email to the user.
				boolean emailStatus = false;
				try
				{
					emailStatus = emailHandler.sendLoginDetailsEmail(user, null);
				}
				catch (BizLogicException e)
				{
					e.printStackTrace();
				}
				if (emailStatus)
				{
					// if success commit 
					/**
					 *  Update the field FirstTimeLogin which will ensure user changes his password on login
					 *  Note --> We can not use CommonAddEditAction to update as the user has not still logged in
					 *  and user authorization will fail. So writing separate code for update. 
					 */
					user.setFirstTimeLogin(Boolean.TRUE);
					IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory();
					DAO dao = daoFactory.getDAO();
					dao.openSession(sessionData);
					dao.update(user);
					dao.commit();
					dao.closeSession();
					statusMessageKey = "password.send.success";
				}
				else
				{
					statusMessageKey = "password.send.failure";
				}
			}
			else
			{
				//Error key if the user is not active.
				statusMessageKey = "errors.forgotpassword.user.notApproved";
			}
		}
		else
		{
			// Error key if the user is not present.
			statusMessageKey = "errors.forgotpassword.user.unknown";
		}
		return statusMessageKey;
	}

	/**
	 * Overriding the parent class's method to validate the enumerated attribute values
	 * @see edu.wustl.common.bizlogic.DefaultBizLogic#validate(java.lang.Object, edu.wustl.common.dao.DAO, java.lang.String)
	 */
	protected boolean validate(Object obj, DAO dao, String operation) throws BizLogicException
	{
		User user = (User) obj;

		/**
		 * Start: Change for API Search   --- 
		 * In Case of Api Search, previously it was failing since there was default class level initialization 
		 * on domain object. For example in User object, it was initialized as protected String lastName=""; 
		 * So we removed default class level initialization on domain object and are initializing in method
		 * setAllValues() of domain object. But in case of Api Search, default values will not get set 
		 * since setAllValues() method of domainObject will not get called. To avoid null pointer exception,
		 * we are setting the default values same as we were setting in setAllValues() method of domainObject.
		 */
		ApiSearchUtil.setUserDefault(user);
		validateUserRelatedInformation(user);
		if (!Constants.PAGEOF_CHANGE_PASSWORD.equals(user.getPageOf()))
		{
			if (!Validator.isEnumeratedValue(CDEManager.getCDEManager().getPermissibleValueList(
					Constants.CDE_NAME_STATE_LIST, null), user.getAddress().getState()))
			{
				throw getBizLogicException(null, "state.errMsg", "");
			}

			if (!Validator.isEnumeratedValue(CDEManager.getCDEManager().getPermissibleValueList(
					Constants.CDE_NAME_COUNTRY_LIST, null), user.getAddress().getCountry()))
			{
				throw getBizLogicException(null, "country.errMsg", "");
			}

			if (Constants.PAGEOF_USER_ADMIN.equals(user.getPageOf()))
			{
				try
				{
					if (!Validator.isEnumeratedValue(getRoles(), user.getRoleId()))
					{
						throw getBizLogicException(null, "user.role.errMsg", "");
					}
				}
				catch (SMException e)
				{
					throw handleSMException(e);
				}

				if (operation.equals(Constants.ADD))
				{
					if (!Constants.ACTIVITY_STATUS_ACTIVE.equals(user.getActivityStatus()))
					{
						throw getBizLogicException(null, "activityStatus.active.errMsg", "");
					}
				}
				else
				{
					if (!Validator.isEnumeratedValue(Constants.USER_ACTIVITY_STATUS_VALUES, user
							.getActivityStatus()))
					{
						throw getBizLogicException(null, "activityStatus.errMsg", "");
					}
				}
			}

			apiValidate(user, dao, operation);

		}
		return true;
	}

	/**
	 * Validate user related data.
	 * @param user
	 * @throws BizLogicException 
	 */
	private void validateUserRelatedInformation(User user) throws BizLogicException
	{
		String errorMessageValue = "error.message.value";

		if (user != null)
		{
			if (user.getFirstName() != null && user.getFirstName().length() > 100)
			{
				throw getBizLogicException(null, errorMessageValue, "First Name :100");
			}
			else if (user.getLastName() != null && user.getLastName().length() > 100)
			{
				throw getBizLogicException(null, errorMessageValue, "Last Name :100");
			}
			else if (user.getEmailAddress() != null && user.getEmailAddress().length() > 100)
			{
				throw getBizLogicException(null, errorMessageValue, "Email Address :100");
			}

			validateAddress(user);

			if (user.getComments() != null && user.getComments().length() > 500)
			{
				throw getBizLogicException(null, errorMessageValue, "Comments :500");
			}
		}
	}

	/**
	 * 
	 * @param user
	 * @param errorList
	 * @throws BizLogicException 
	 */
	private void validateAddress(User user) throws BizLogicException
	{
		String errorMessageValue = "error.message.value";

		if (user.getAddress() != null)
		{
			if (user.getAddress().getCity() != null && user.getAddress().getCity().length() > 50)
			{
				throw getBizLogicException(null, errorMessageValue, "City name :50");
			}
			else if (user.getAddress().getStreet() != null
					&& user.getAddress().getStreet().length() > 255)
			{
				throw getBizLogicException(null, errorMessageValue, "Street description :255");
			}
			else if (user.getAddress().getZipCode() != null
					&& user.getAddress().getZipCode().length() > 30)
			{
				throw getBizLogicException(null, errorMessageValue, "Zip code :30");
			}
			else if (user.getAddress().getPhoneNumber() != null
					&& user.getAddress().getPhoneNumber().length() > 50)
			{
				throw getBizLogicException(null, errorMessageValue, "Phone Number  :50");
			}
			else if (user.getAddress().getFaxNumber() != null
					&& user.getAddress().getFaxNumber().length() > 50)
			{
				throw getBizLogicException(null, errorMessageValue, "Fax Number :50");

			}
		}
	}

	/**
	 * @param user user
	 * @param dao
	 * @param operation
	 * @return 
	 * @throws BizLogicException 
	 */
	private boolean apiValidate(User user, DAO dao, String operation) throws BizLogicException
	{
		Validator validator = new Validator();
		String message = "";
		String errorsItemRequired = "errors.item.required";
		boolean validate = true;

		if (Validator.isEmpty(user.getEmailAddress()))
		{
			message = ApplicationProperties.getValue("user.emailAddress");
			throw getBizLogicException(null, errorsItemRequired, message);

		}
		else if (!validator.isValidEmailAddress(user.getEmailAddress()))
		{
			message = ApplicationProperties.getValue("user.emailAddress");
			throw getBizLogicException(null, "errors.item.format", message);
		}

		// Following method is provided to verify if the email address is already present in the system or not. 

		else if (operation.equals(Constants.ADD)
				&& !(isUniqueEmailAddress(user.getEmailAddress(), dao)))
		{
			String[] arguments = null;
			arguments = new String[]{"User", ApplicationProperties.getValue("user.emailAddress")};
			String errMsg = new DefaultExceptionFormatter().getErrorMessage(
					"Err.ConstraintViolation", arguments);
			logger.debug("Unique Constraint Violated: " + errMsg);
			throw getBizLogicException(null, null, errMsg);
		}
		/** -- patch ends here -- */

		else if (Validator.isEmpty(user.getLastName()))
		{
			message = ApplicationProperties.getValue("user.lastName");
			throw getBizLogicException(null, errorsItemRequired, message);
		}
		else if (Validator.isEmpty(user.getFirstName()))
		{
			message = ApplicationProperties.getValue("user.firstName");
			throw getBizLogicException(null, errorsItemRequired, message);
		}
		else if (Validator.isEmpty(user.getAddress().getCity()))
		{
			message = ApplicationProperties.getValue("user.city");
			throw getBizLogicException(null, errorsItemRequired, message);
		}
		else if (!validator.isValidOption(user.getAddress().getState())
				|| Validator.isEmpty(user.getAddress().getState()))
		{
			message = ApplicationProperties.getValue("user.state");
			throw getBizLogicException(null, errorsItemRequired, message);
		}
		else if (Validator.isEmpty(user.getAddress().getZipCode()))
		{
			message = ApplicationProperties.getValue("user.zipCode");
			throw getBizLogicException(null, errorsItemRequired, message);
		}
		else if (!validator.isValidZipCode(user.getAddress().getZipCode()))
		{
			message = ApplicationProperties.getValue("user.zipCode");
			throw getBizLogicException(null, "errors.item.format", message);
		}
		else if (!validator.isValidOption(user.getAddress().getCountry())
				|| Validator.isEmpty(user.getAddress().getCountry()))
		{
			message = ApplicationProperties.getValue("user.country");
			throw getBizLogicException(null, errorsItemRequired, message);
		}
		else if (Validator.isEmpty(user.getAddress().getPhoneNumber()))
		{
			message = ApplicationProperties.getValue("user.phoneNumber");
			throw getBizLogicException(null, errorsItemRequired, message);
		}
		else if (user.getInstitution().getId() == null
				|| user.getInstitution().getId().longValue() <= 0)
		{
			message = ApplicationProperties.getValue("user.institution");
			throw getBizLogicException(null, errorsItemRequired, message);
		}
		else if (user.getDepartment().getId() == null
				|| user.getDepartment().getId().longValue() <= 0)
		{
			message = ApplicationProperties.getValue("user.department");
			throw getBizLogicException(null, errorsItemRequired, message);
		}
		else if (user.getCancerResearchGroup().getId() == null
				|| user.getCancerResearchGroup().getId().longValue() <= 0)
		{
			message = ApplicationProperties.getValue("user.cancerResearchGroup");
			throw getBizLogicException(null, errorsItemRequired, message);
		}
		else if (user.getRoleId() != null
				&& (!validator.isValidOption(user.getRoleId()) || Validator.isEmpty(String
						.valueOf(user.getRoleId()))))
		{
			message = ApplicationProperties.getValue("user.role");
			throw getBizLogicException(null, errorsItemRequired, message);
		}
		return validate;
	}

	/**
	 * Returns a list of all roles that can be assigned to a user.
	 * @return a list of all roles that can be assigned to a user.
	 * @throws SMException
	 */
	private List getRoles() throws SMException
	{
		//Sets the roleList attribute to be used in the Add/Edit User Page.
		Vector roleList = ((edu.wustl.clinportal.security.SecurityManager) SecurityManagerFactory
				.getSecurityManager()).getRoles();

		List rlNameValBenLst = new ArrayList();
		NameValueBean nameValueBean = new NameValueBean();
		nameValueBean.setName(Constants.SELECT_OPTION);
		nameValueBean.setValue("-1");
		rlNameValBenLst.add(nameValueBean);

		ListIterator iterator = roleList.listIterator();
		while (iterator.hasNext())
		{
			Role role = (Role) iterator.next();
			nameValueBean = new NameValueBean();
			nameValueBean.setName(role.getName());
			nameValueBean.setValue(String.valueOf(role.getId()));
			rlNameValBenLst.add(nameValueBean);
		}
		return rlNameValBenLst;
	}

	/**
	 * @param oldUser User object
	 * @param newPassword New Password value
	 * @return SUCCESS (constant int 0) if all condition passed 
	 *   else return respective error code (constant int) value  
	 * @throws PasswordEncryptionException 
	 */

	private int validatePassword(User oldUser, String newPassword)
			throws PasswordEncryptionException
	{
		List oldPwdList = new ArrayList(oldUser.getPasswordCollection());
		Collections.sort(oldPwdList);
		int returnVal = SUCCESS;

		if (oldPwdList != null && !oldPwdList.isEmpty())
		{
			//Check new password is equal to last n password if value
			PasswordManager.encrypt(newPassword);
			if (checkPwdNotSameAsLastN(newPassword, oldPwdList))
			{
				logger.debug("Password is not valid returning FAIL_SAME_AS_LAST_N");
				return FAIL_SAME_AS_LAST_N;
			}

			//Get the last updated date of the password
			Date lastestUpdateDate = ((Password) oldPwdList.get(0)).getUpdateDate();
			boolean firstTimeLogin = false;
			if (oldUser.getFirstTimeLogin() != null)
			{
				firstTimeLogin = oldUser.getFirstTimeLogin().booleanValue();
			}
			if (!firstTimeLogin && checkPwdUpdatedOnSameDay(lastestUpdateDate))
			{

				logger.debug("Password is not valid returning FAIL_CHANGED_WITHIN_SOME_DAY");
				return FAIL_CHANGED_WITHIN_SOME_DAY;
			}

			/**	
			 * to check password does not contain user name,surname,email address.  
			 * if same return FAIL_SAME_NAME_SURNAME_EMAIL
			 *  eg. username=XabcY@abc.com newpassword=abc is not valid
			 */

			String emailAddress = oldUser.getEmailAddress();
			int unameBefMaddrs = emailAddress.indexOf('@');
			// get substring of emailAddress before '@' character    
			emailAddress = emailAddress.substring(0, unameBefMaddrs);
			String userFirstName = oldUser.getFirstName();
			String userLastName = oldUser.getLastName();

			if (emailAddress != null
					&& newPassword.toLowerCase().indexOf(emailAddress.toLowerCase()) != -1)
			{
				logger.debug("Password is not valid returning FAIL_SAME_NAME_SURNAME_EMAIL");
				returnVal = FAIL_SAME_NAME_SURNAME_EMAIL;
			}
			else if (userFirstName != null
					&& newPassword.toLowerCase().indexOf(userFirstName.toLowerCase()) != -1)
			{
				logger.debug("Password is not valid returning FAIL_SAME_NAME_SURNAME_EMAIL");
				returnVal = FAIL_SAME_NAME_SURNAME_EMAIL;
			}
			else if (userLastName != null
					&& newPassword.toLowerCase().indexOf(userLastName.toLowerCase()) != -1)
			{
				logger.debug("Password is not valid returning FAIL_SAME_NAME_SURNAME_EMAIL");
				returnVal = FAIL_SAME_NAME_SURNAME_EMAIL;
			}

		}
		return returnVal;
	}

	/**
	 * This function checks whether user has logged in for first time or whether user's password is expired. 
	 * In both these case user needs to change his password so Error key is returned
	 * @param user - user object
	 * @return
	 */
	public String checkFirstLoginAndExpiry(User user)
	{
		List passwordList = new ArrayList(user.getPasswordCollection());
		String errorKey = Constants.SUCCESS;
		boolean firstTimeLogin = getFirstLogin(user);
		// If user has logged in for the first time, return key of Change password on first login
		if (firstTimeLogin)
		{
			errorKey = "errors.changePassword.changeFirstLogin";
		}
		else
		{
			Collections.sort(passwordList);
			Password lastPassword = (Password) passwordList.get(0);
			Date lastUpdateDate = lastPassword.getUpdateDate();

			Validator validator = new Validator();
			//Get difference in days between last password update date and current date.
			long dayDiff = validator.getDateDiff(lastUpdateDate, new Date());
			int expireDaysCount = Integer.parseInt(XMLPropertyHandler
					.getValue("password.expire_after_n_days"));
			if (dayDiff > expireDaysCount)
			{
				errorKey = "errors.changePassword.expire";
			}
		}

		return errorKey;
	}
	
	/**
	 * This function will check if the user is First time logging.
	 * @param user user object
	 * @return firstTimeLogin
	 */
	public boolean getFirstLogin(User user)
	{
		boolean firstTimeLogin = false;
		if (user.getFirstTimeLogin() != null)
		{
			firstTimeLogin = user.getFirstTimeLogin().booleanValue();
		}
		return firstTimeLogin;
	}

	/**
	 * @param newPassword
	 * @param oldPwdList
	 * @return
	 */
	private boolean checkPwdNotSameAsLastN(String newPassword, List oldPwdList)
	{
		int nofPwdNtSmeAsLstN = 0;
		String pwdNotSameAsLastN = XMLPropertyHandler.getValue("password.not_same_as_last_n");
		if (pwdNotSameAsLastN != null && !pwdNotSameAsLastN.equals(""))
		{
			nofPwdNtSmeAsLstN = Integer.parseInt(pwdNotSameAsLastN);
			nofPwdNtSmeAsLstN = Math.max(0, nofPwdNtSmeAsLstN);
		}

		boolean isSameFound = false;
		int loopCount = Math.min(oldPwdList.size(), nofPwdNtSmeAsLstN);
		for (int i = 0; i < loopCount; i++)
		{
			Password pasword = (Password) oldPwdList.get(i);
			if (newPassword.equals(pasword.getPassword()))
			{
				isSameFound = true;
				break;
			}
		}
		return isSameFound;
	}

	/**
	 * @param lastUpdateDate
	 * @return
	 */
	private boolean checkPwdUpdatedOnSameDay(Date lastUpdateDate)
	{
		Validator validator = new Validator();
		//Get difference in days between last password update date and current date.
		long dayDiff = validator.getDateDiff(lastUpdateDate, new Date());
		int dayDiffConstant = Integer.parseInt(XMLPropertyHandler.getValue("daysCount"));
		boolean flag = false;
		if (dayDiff <= dayDiffConstant)
		{
			logger.debug("Password is not valid returning FAIL_CHANGED_WITHIN_SOME_DAY");
			flag = true;
		}
		return flag;
	}

	/**
	 * @param errorCode int value return by validatePassword() method
	 * @return String error message with respect to error code 
	 */
	private String getPasswordErrorMsg(int errorCode)
	{
		String errMsg = "";
		switch (errorCode)
		{
			case FAIL_SAME_AS_LAST_N :
				List parameters = new ArrayList();
				String dayCount = ""
						+ Integer.parseInt(XMLPropertyHandler
								.getValue("password.not_same_as_last_n"));
				parameters.add(dayCount);
				errMsg = ApplicationProperties.getValue("errors.newPassword.sameAsLastn",
						parameters);
				break;
			case FAIL_FIRST_LOGIN :
				errMsg = ApplicationProperties.getValue("errors.changePassword.changeFirstLogin");
				break;
			case FAIL_EXPIRE :
				errMsg = ApplicationProperties.getValue("errors.changePassword.expire");
				break;
			case FAIL_CHANGED_WITHIN_SOME_DAY :
				errMsg = ApplicationProperties.getValue("errors.changePassword.afterSomeDays");
				break;
			case FAIL_SAME_NAME_SURNAME_EMAIL :
				errMsg = ApplicationProperties
						.getValue("errors.changePassword.sameAsNameSurnameEmail");
				break;
			case FAIL_PASSWORD_EXPIRED :
				errMsg = ApplicationProperties.getValue("errors.changePassword.expire");
			default :
				errMsg = PasswordManager.getErrorMessage(errorCode);
				break;
		}
		return errMsg;
	}

	/**
	 * Description: Wrong error message was displayed while adding user with existing email address in use.
	 * Following method is provided to verify whether the email address is already present in the system or not. 
	 */
	/**
	 * Method to check whether email address already exist or not
	 * @param emailAddress email address to be check
	 * @param dao an object of DAO
	 * @return isUnique boolean value to indicate presence of similar email address
	 * @throws BizLogicException database exception
	 */
	private boolean isUniqueEmailAddress(String emailAddress, DAO dao) throws BizLogicException
	{
		String sourceObjectName = User.class.getName();
		String[] selectColumnName = new String[]{"id"};
		String[] whereColumnName = new String[]{"emailAddress"};
		String[] whereColumnCondition = new String[]{"="};
		Object[] whereColumnValue = new String[]{emailAddress};
		String joinCondition = null;

		QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
		boolean isUnique = true;

		try
		{
			queryWhereClause.getWhereCondition(whereColumnName, whereColumnCondition,
					whereColumnValue, joinCondition);
			List userList = dao.retrieve(sourceObjectName, selectColumnName, queryWhereClause);
			if (!userList.isEmpty())
			{
				isUnique = false;
			}
		}
		catch (DAOException e)
		{
			throw new BizLogicException(e);
		}

		return isUnique;
	}

	/**
	 * Set Role to user object before populating actionForm out of it
	 * @param domainObj object of AbstractDomainObject
	 * @param uiForm object of the class which implements IValueObject
	 * @throws BizLogicException 
	 */
	protected void prePopulateUIBean(AbstractDomainObject domainObj, IValueObject uiForm)
			throws BizLogicException
	{
		logger.info("Inside prePopulateUIBean method of UserBizLogic...");

		User user = (User) domainObj;
		if (user.getCsmUserId() != null)
		{
			try
			{
				//Get the role of the user.
				Role role = SecurityManagerFactory.getSecurityManager().getUserRole(
						user.getCsmUserId().longValue());
				if (role != null)
				{
					user.setRoleId(role.getId().toString());
				}

			}
			catch (SMException e)
			{
				logger.error("SMException in prePopulateUIBean method of UserBizLogic..." + e);
			}
		}
	}

	/**
	 * Over-ridden for the case of Non - Admin user should be able to edit
	 * his/her details e.g. Password 
	 * (non-Javadoc)
	 * @param dao
	 * @param domainObject
	 * @param sessionDataBean
	 * @return
	 * @throws BizLogicException 
	 * 
	 */
	//@see edu.wustl.common.bizlogic.DefaultBizLogic#isAuthorized(edu.wustl.common.dao.AbstractDAO, java.lang.Object, edu.wustl.common.beans.SessionDataBean)
	public boolean isAuthorized(DAO dao, Object domainObject, SessionDataBean sessionDataBean)
			throws BizLogicException
	{
		boolean isAuthorized = false;
		isAuthorized = checkUser(domainObject, sessionDataBean);

		if (isAuthorized)
		{
			return true;
		}

		PrivilegeManager privilegeManager;
		try
		{
			privilegeManager = PrivilegeManager.getInstance();

			PrivilegeCache privilegeCache = privilegeManager.getPrivilegeCache(sessionDataBean
					.getUserName());

			isAuthorized = privilegeCache.hasPrivilege(
					getObjectIdForSecureMethodAccess(domainObject), Permissions.EXECUTE);
		}
		catch (SMException e)
		{
			throw getBizLogicException(e, "error.security", "Security Exception");
		}
		if (!isAuthorized)
		{
			throw getBizLogicException(null, "error.security",
					"Access denied- You are not authorized to perform this operation.");
		}

		return isAuthorized;
	}

	/**
	 * @param domainObject
	 * @param sessionDataBean
	 * @return
	 */
	public boolean checkUser(Object domainObject, SessionDataBean sessionDataBean)
	{
		boolean isAuthorized = false; // NOPMD - DD anomaly
		User user = (User) domainObject;

		if (user.getPageOf().equalsIgnoreCase("pageOfChangePassword")
				|| user.getPageOf().equalsIgnoreCase("pageOfSignUp"))
		{
			isAuthorized = true;
		}
		return isAuthorized;
	}
}