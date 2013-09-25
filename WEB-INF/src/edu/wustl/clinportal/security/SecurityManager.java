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

package edu.wustl.clinportal.security;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.idp.IdPManager;
import edu.wustl.common.util.XMLPropertyHandler;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.security.exception.SMException;
import edu.wustl.security.global.ProvisionManager;
import edu.wustl.security.global.Roles;
import edu.wustl.security.global.Utility;
import edu.wustl.security.locator.SecurityManagerPropertiesLocator;
import gov.nih.nci.security.UserProvisioningManager;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.Role;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.dao.GroupSearchCriteria;
import gov.nih.nci.security.exceptions.CSException;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;
import gov.nih.nci.security.exceptions.CSTransactionException;

/**
 * @author shital_lawhale
 * This method overrides the SecurityManager class in common package.
 * This class is used to define application specific roles and groups.
 * 
 */
public class SecurityManager extends edu.wustl.security.manager.SecurityManager
{

	private transient Logger logger = Logger.getCommonLogger(SecurityManager.class);
	public static final String TRUE = "true";
	public static final String IDP_ENABLED = "idp.enabled";

	/**
	 * This method returns Vector of all the role objects defined for the
	 * application from the database
	 * 
	 * @return @throws
	 *         SMException
	 */
	public Vector getRoles() throws SMException
	{
		Vector roles = new Vector();
		UserProvisioningManager userProvManager;
		try
		{
			userProvManager = ProvisionManager.getInstance().getUserProvisioningManager();
			roles.add(userProvManager.getRoleById(ProvisionManager.getInstance().getRoleID(
					edu.wustl.security.global.Constants.ROLE_ADMIN)));
			roles.add(userProvManager.getRoleById(ProvisionManager.getInstance().getRoleID(
					edu.wustl.security.global.Constants.SCIENTIST)));
		}
		catch (CSException e)
		{
			logger.debug("Unable to get roles: Exception: " + e.getMessage());
			throw new SMException(ErrorKey.getErrorKey("error.fetch.roleslist"), e,
					"Exception while getting roles");
		}
		return roles;
	}

	/**
	 * Assigns a Role to a User
	 * 
	 * @param userName -
	 *            the User Name to to whom the Role will be assigned
	 * @param roleID -
	 *            The id of the Role which is to be assigned to the user
	 * @throws SMException
	 */
	public void assignRoleToUser(String userID, String roleID) throws SMException
	{
		logger.debug("UserName: " + userID + " Role ID:" + roleID);
		UserProvisioningManager userProvManager;
		User user;
		String groupId;
		try
		{
			userProvManager = ProvisionManager.getInstance().getUserProvisioningManager();

			user = userProvManager.getUserById(userID);

			//Remove user from any other role if he is assigned some

			ProvisionManager provisionManager = ProvisionManager.getInstance();
			userProvManager.removeUserFromGroup(provisionManager.getGroupID(ADMIN_GROUP), String
					.valueOf(user.getUserId()));
			userProvManager.removeUserFromGroup(provisionManager.getGroupID(PUBLIC_GROUP), String
					.valueOf(user.getUserId()));

			//Add user to corresponding group
			groupId = getGroupIdForRole(roleID);

			if (groupId == null)
			{
				logger.debug(" User assigned no role");
			}
			else
			{
				assignAdditionalGroupsToUser(String.valueOf(user.getUserId()),
						new String[]{groupId});
				logger.debug(" User assigned role:" + groupId);
			}

		}
		catch (CSException e)
		{
			logger.debug("UNABLE TO ASSIGN ROLE TO USER: Exception: " + e.getMessage());
			throw new SMException(ErrorKey.getErrorKey("error.assign.user.role"), e,
					"Exception while assigning role to user");
		}
	}

	/**
	 * @param roleID
	 * @return
	 * @throws SMException 
	 */
	public String getGroupIdForRole(String roleID) throws SMException
	{
		String groupId = null;
		ProvisionManager provisionManager = ProvisionManager.getInstance();
		if (roleID.equals(provisionManager
				.getRoleID(edu.wustl.security.global.Constants.ROLE_ADMIN)))
		{
			logger.debug(" role corresponds to Administrator group");
			groupId = provisionManager.getGroupID(ADMIN_GROUP);
		}
		else if (roleID.equals(provisionManager
				.getRoleID(edu.wustl.security.global.Constants.SCIENTIST)))
		{
			logger.debug(" role corresponds to public group");
			groupId = provisionManager.getGroupID(PUBLIC_GROUP);
		}
		else
		{
			logger.debug("role corresponds to no group");
		}
		return groupId;
	}

	/* (non-Javadoc)
	 * @see edu.wustl.common.security.SecurityManager#getUserRole(long)
	 */
	public Role getUserRole(long userID) throws SMException
	{
		Set groups;
		UserProvisioningManager userProvManager;
		Iterator iterator;
		Group group;
		Role role = null;
		try
		{
			userProvManager = ProvisionManager.getInstance().getUserProvisioningManager();
			groups = userProvManager.getGroups(String.valueOf(userID));
			iterator = groups.iterator();
			while (iterator.hasNext())
			{
				group = (Group) iterator.next();
				if (group.getApplication().getApplicationName().equals(
						SecurityManagerPropertiesLocator.getInstance().getApplicationCtxName()))
				{
					role = getRoleFromGroup(userProvManager, group);
					if (role != null)
					{
						break;
					}
				}
			}
		}
		catch (CSException e)
		{
			logger.debug("Unable to get user role: Exception: " + e.getMessage());
			throw new SMException(ErrorKey.getErrorKey("error.fetch.user.role"), e,
					"Exception while getting user role");
		}
		return role;

	}

	/**
	 * @param userProvManager
	 * @param group
	 * @return
	 * @throws CSObjectNotFoundException
	 * @throws SMException 
	 */
	private Role getRoleFromGroup(UserProvisioningManager userProvManager, Group group)
			throws CSObjectNotFoundException, SMException
	{
		Role role = null;
		if (group.getGroupName().equals(ADMIN_GROUP))
		{
			role = userProvManager.getRoleById(ProvisionManager.getInstance().getRoleID(
					edu.wustl.security.global.Constants.ROLE_ADMIN));
		}
		else if (group.getGroupName().equals(PUBLIC_GROUP))
		{
			role = userProvManager.getRoleById(ProvisionManager.getInstance().getRoleID(
					edu.wustl.security.global.Constants.SCIENTIST));
		}
		return role;
	}

	/* (non-Javadoc)
	 * @see edu.wustl.common.security.SecurityManager#getUserGroup(long)
	 */
	public String getUserGroup(long userID) throws SMException
	{
		Set groups;
		UserProvisioningManager userProvManager;
		Iterator iterator;
		Group group;
		String rolename = null;
		try
		{
			userProvManager = ProvisionManager.getInstance().getUserProvisioningManager();
			groups = userProvManager.getGroups(String.valueOf(userID));
			iterator = groups.iterator();
			while (iterator.hasNext())
			{
				group = (Group) iterator.next();
				if (group.getApplication().getApplicationName().equals(
						SecurityManagerPropertiesLocator.getInstance().getApplicationCtxName()))
				{
					rolename = getRolename(group);
					if (rolename != null)
					{
						break;
					}
				}
			}
		}
		catch (CSException e)
		{
			logger.debug("Unable to get user group: Exception: " + e.getMessage());
			throw new SMException(ErrorKey.getErrorKey("error.fetch.usergroup"), e,
					"Exception while getting user group");
		}
		return rolename;
	}

	/**
	 * @param group
	 * @return
	 */
	private String getRolename(Group group)
	{
		String rolename = null;
		if (group.getGroupName().equals(ADMIN_GROUP))
		{
			rolename = Roles.ADMINISTRATOR;
		}
		else if (group.getGroupName().equals(SUPERVISOR_GROUP))
		{
			rolename = Roles.SUPERVISOR;
		}
		else if (group.getGroupName().equals(TECHNICIAN_GROUP))
		{
			rolename = Roles.TECHNICIAN;
		}
		else if (group.getGroupName().equals(PUBLIC_GROUP))
		{
			rolename = Roles.SCIENTIST;
		}
		return rolename;
	}

	/**
	 * This method returns array of CSM user id of all users who are administrators
	 * @return
	 * @throws SMException
	 */
	public Long[] getAllAdministrators() throws SMException
	{

		Group group = new Group();
		group.setGroupName(ADMIN_GROUP);
		GroupSearchCriteria grpSearch = new GroupSearchCriteria(group);

		List list = ProvisionManager.getInstance().getUserProvisioningManager().getObjects(
				grpSearch);
		logger.debug("Group Size: " + list.size());
		group = (Group) list.get(0);
		logger.debug("Group : " + group.getGroupName());
		Set users = group.getUsers();
		logger.debug("Users : " + users);
		Long[] userId = new Long[users.size()];
		Iterator iterator = users.iterator(); // NOPMD by rukhsana_sameer on 10/17/08 3:49 PM
		for (int i = 0; i < users.size(); i++)
		{
			userId[i] = ((User) iterator.next()).getUserId();
		}
		return userId;

	}

	/**
	 * 
	 * @param userName
	 * @param objectId
	 * @param privilegeName
	 * @return
	 * @throws SMException
	 */
	public boolean isAuthorized(String userName, String objectId, String privilegeName)
			throws SMException
	{
		boolean isAuthorized = false;
		try
		{
			isAuthorized = ProvisionManager.getInstance().getAuthorizationManager()
					.checkPermission(userName, objectId, privilegeName);
		}
		catch (CSException e)
		{
			Utility.getInstance().throwSMException(e, "Exception while checking permission",
					"error.check.authorization");
		}
		return isAuthorized;
	}

	/* (non-Javadoc)
	 * @see edu.wustl.security.manager.SecurityManager#createUser(gov.nih.nci.security.authorization.domainobjects.User)
	 */
	public void createUser(User user) throws SMException
	{
		try
		{
			ProvisionManager.getInstance().getUserProvisioningManager().createUser(user);
			if (isIdpEnabled())
			{
				IdPManager idp = IdPManager.getInstance();
				idp.addUserToQueue(SecurityManagerPropertiesLocator.getInstance()
						.getApplicationCtxName(), user);
			}
		}
		catch (CSTransactionException exception)
		{
			String mesg = "Unable to create user " + user.getEmailId();
			Utility.getInstance().throwSMException(exception, mesg, "sm.operation.error");
		}

	}

	/**
	 * @return
	 */
	private boolean isIdpEnabled()
	{
		boolean result = false;
		String idpEnabled = XMLPropertyHandler.getValue(this.IDP_ENABLED);
		if (this.TRUE.equalsIgnoreCase(idpEnabled))
		{
			result = true;
		}

		return result;

	}

	/* (non-Javadoc)
	 * @see edu.wustl.security.manager.SecurityManager#modifyUser(gov.nih.nci.security.authorization.domainobjects.User)
	 */
	public void modifyUser(User user) throws SMException
	{
		try
		{
			ProvisionManager.getInstance().getUserProvisioningManager().modifyUser(user);
			if (isIdpEnabled())
			{
				IdPManager idp = IdPManager.getInstance();
				idp.addUserToQueue(SecurityManagerPropertiesLocator.getInstance()
						.getApplicationCtxName(), user);
			}
		}
		catch (CSException exception)
		{
			String mesg = "Unable to modify user: Exception:  ";
			Utility.getInstance().throwSMException(exception, mesg, "sm.operation.error");
		}
	}
}