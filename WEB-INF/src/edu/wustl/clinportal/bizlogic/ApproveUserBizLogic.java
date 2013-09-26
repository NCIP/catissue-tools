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
 * <p>Title: ApproveUserBizLogic Class>
 * <p>Description:	ApproveUserBizLogic is the bizLogic class for approve users.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 */

package edu.wustl.clinportal.bizlogic;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import edu.wustl.clinportal.bizlogic.util.BizLogicUtility;
import edu.wustl.clinportal.domain.Password;
import edu.wustl.clinportal.domain.User;
import edu.wustl.clinportal.util.ApiSearchUtil;
import edu.wustl.clinportal.util.EmailHandler;
import edu.wustl.clinportal.util.Roles;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.audit.AuditManager;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.AuditException;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.exception.PasswordEncryptionException;
import edu.wustl.common.util.global.PasswordManager;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.security.beans.SecurityDataBean;
import edu.wustl.security.exception.SMException;
import edu.wustl.security.manager.ISecurityManager;
import edu.wustl.security.manager.SecurityManagerFactory;
import edu.wustl.security.privilege.PrivilegeManager;
import gov.nih.nci.security.authorization.domainobjects.Role;

/**
 * ApproveUserBizLogic is the bizLogic class for approve users.
 * @author shital_lawhale
 */
public class ApproveUserBizLogic extends ClinportalDefaultBizLogic
{

	private transient Logger logger = Logger.getCommonLogger(ApproveUserBizLogic.class);

	/**
	 * Overrides the insert method of DefaultBizLogic. 
	 */
	/* (non-Javadoc)
	 * @see edu.wustl.common.bizlogic.DefaultBizLogic#update(edu.wustl.common.dao.DAO, java.lang.Object, java.lang.Object, edu.wustl.common.beans.SessionDataBean)
	 */
	protected void update(DAO dao, Object obj, Object oldObj, SessionDataBean sessionDataBean)
			throws BizLogicException
	{
		User user = (User) obj;

		ApiSearchUtil.setUserDefault(user);

		gov.nih.nci.security.authorization.domainobjects.User csmUser = new gov.nih.nci.security.authorization.domainobjects.User();

		try
		{
			//			Audit of User Update during approving user.
			User oldUser = (User) oldObj;
			dao.update(user.getAddress());
			//If the activity status is Active, create a csm user.
			if (Constants.ACTIVITY_STATUS_ACTIVE.equals(user.getActivityStatus()))
			{
				approveUser(user, csmUser, dao, sessionDataBean);
			}
			else
			{
				dao.update(user);
			}
			sendEmail(user);
			AuditManager auditManager = getAuditManager(sessionDataBean);
			auditManager.updateAudit(dao, user.getAddress(), oldUser.getAddress());
			auditManager.updateAudit(dao, obj, oldObj);

		}
		catch (DAOException daoExp)
		{
			logger.debug(daoExp.getMessage(), daoExp);
			BizLogicUtility.deleteCSMUser(csmUser);
			throw new BizLogicException(daoExp);
		}
		catch (SMException exp)
		{
			logger.debug(exp.getMessage(), exp);
			BizLogicUtility.deleteCSMUser(csmUser);
			throw handleSMException(exp);
		}
		catch (PasswordEncryptionException e)
		{
			logger.debug(e.getMessage(), e);
			BizLogicUtility.deleteCSMUser(csmUser);
			throw new BizLogicException(ErrorKey.getErrorKey("error.password.encryption"), e,
					"Error in password encryption");
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
	 * @throws BizLogicException 
	 */
	private void sendEmail(User user) throws BizLogicException
	{
		EmailHandler emailHandler = new EmailHandler();

		//If user is approved send approval and login details email to the user and administrator.
		if (Constants.ACTIVITY_STATUS_ACTIVE.equals(user.getActivityStatus()))
		{
			//Send approval email to the user and administrator.
			emailHandler.sendApprovalEmail(user);
		}
		else if (Constants.ACTIVITY_STATUS_REJECT.equals(user.getActivityStatus()))
		{
			//If user is rejected send rejection email to the user and administrator.
			//Send rejection email to the user and administrator.
			emailHandler.sendRejectionEmail(user);
		}

	}

	/**
	 * Populates CsmUser data & creates Protection elements for User
	 * 
	 * @param user
	 * @param csmUser
	 * @throws SMTransactionException
	 * @throws SMException
	 * @throws PasswordEncryptionException
	 * @throws DAOException 
	 */
	private void approveUser(User user,
			gov.nih.nci.security.authorization.domainobjects.User csmUser, DAO dao,
			SessionDataBean sessionDataBean) throws SMException, PasswordEncryptionException,
			DAOException
	{
		csmUser.setLoginName(user.getLoginName());
		csmUser.setLastName(user.getLastName());
		csmUser.setFirstName(user.getFirstName());
		csmUser.setEmailId(user.getEmailAddress());
		csmUser.setStartDate(Calendar.getInstance().getTime());

		String generatedPassword = PasswordManager.generatePassword();

		if (user.getActivityStatus().equals(Constants.ACTIVITY_STATUS_ACTIVE))
		{
			csmUser.setPassword(generatedPassword);
		}
		ISecurityManager securityManager = SecurityManagerFactory.getSecurityManager();
		securityManager.createUser(csmUser);

		if (user.getRoleId() != null)
		{
			securityManager.assignRoleToUser(csmUser.getUserId().toString(), user.getRoleId());
		}

		user.setCsmUserId(csmUser.getUserId());

		Password password = new Password(PasswordManager.encrypt(generatedPassword), user);
		user.getPasswordCollection().add(password);
		Logger.out.debug("password stored in password table");
		PrivilegeManager privilegeManager = PrivilegeManager.getInstance();
		Set protectionObjects = new HashSet();
		protectionObjects.add(user);

		privilegeManager.insertAuthorizationData(getAuthorizationData(user), protectionObjects,
				null, user.getObjectId());
		dao.update(user);
	}

	/**
	 * Deletes the csm user from the csm user table.
	 * @param csmUser The csm user to be deleted.
	 * @throws DAOException
	 */

	/**
	 * This method returns collection of UserGroupRoleProtectionGroup objects that specifies the 
	 * user group protection group linkage through a role. It also specifies the groups the protection  
	 * elements returned by this class should be added to.
	 * @param obj
	 * @return
	 * @throws SMException
	 */
	private Vector getAuthorizationData(AbstractDomainObject obj) throws SMException
	{
		Logger.out.debug("--------------- In here ---------------");
		Vector authorizationData = new Vector();
		Set group = new HashSet();
		SecurityDataBean usrGrpRlPrtGrpBn;
		String protnGrpName;

		User aUser = (User) obj;
		String userId = String.valueOf(aUser.getCsmUserId());
		gov.nih.nci.security.authorization.domainobjects.User user = SecurityManagerFactory
				.getSecurityManager().getUserById(userId);
		Logger.out.debug(" User: " + user.getLoginName());
		group.add(user);

		// Protection group of PI
		protnGrpName = Constants.getUserPGName(aUser.getId());
		usrGrpRlPrtGrpBn = new SecurityDataBean();
		usrGrpRlPrtGrpBn.setUser(userId);
		usrGrpRlPrtGrpBn.setRoleName(Roles.UPDATE_ONLY);
		usrGrpRlPrtGrpBn.setGroupName(Constants.getUserGroupName(aUser.getId()));
		usrGrpRlPrtGrpBn.setProtGrpName(protnGrpName);
		usrGrpRlPrtGrpBn.setGroup(group);
		authorizationData.add(usrGrpRlPrtGrpBn);

		logger.debug(authorizationData.toString());
		return authorizationData;
	}

	/**
	 * Returns the list of users according to the column name and value passed.
	 * @return the list of users according to the column name and value passed.
	 */
	/* (non-Javadoc)
	 * @see edu.wustl.common.bizlogic.DefaultBizLogic#retrieve(java.lang.String, java.lang.String, java.lang.Object)
	 */
	public List retrieve(String className, String colName, Object colValue)
			throws BizLogicException
	{
		List userList = null;
		try
		{
			// Get the caTISSUE user.
			userList = super.retrieve(className, colName, colValue);

			edu.wustl.clinportal.domain.User appUser = null;
			if (!userList.isEmpty())
			{
				appUser = (edu.wustl.clinportal.domain.User) userList.get(0);

				if (appUser.getCsmUserId() != null)
				{
					//Get the role of the user.
					Role role = SecurityManagerFactory.getSecurityManager().getUserRole(
							appUser.getCsmUserId().longValue());
					if (role != null)
					{
						appUser.setRoleId(role.getId().toString());
					}
				}
			}
		}
		catch (SMException smExp)
		{
			logger.debug(smExp.getMessage(), smExp);
			throw handleSMException(smExp);
		}

		return userList;
	}
}