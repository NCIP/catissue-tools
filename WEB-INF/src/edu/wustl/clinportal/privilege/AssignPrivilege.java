
package edu.wustl.clinportal.privilege;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.wustl.clinportal.bizlogic.ClinicalStudyBizLogic;
import edu.wustl.clinportal.domain.ClinicalStudy;
import edu.wustl.clinportal.domain.ClinicalStudyEvent;
import edu.wustl.clinportal.domain.SpecimenProtocol;
import edu.wustl.clinportal.domain.StudyFormContext;
import edu.wustl.clinportal.domain.User;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.clinportal.util.global.Utility;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.security.exception.SMException;
import edu.wustl.security.global.Permissions;
import edu.wustl.security.global.ProvisionManager;
import edu.wustl.security.global.Roles;
import edu.wustl.security.locator.SecurityManagerPropertiesLocator;
import edu.wustl.security.manager.ISecurityManager;
import edu.wustl.security.manager.SecurityManagerFactory;
import edu.wustl.security.privilege.PrivilegeManager;
import edu.wustl.security.privilege.PrivilegeUtility;
import gov.nih.nci.security.UserProvisioningManager;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.Privilege;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElement;
import gov.nih.nci.security.authorization.domainobjects.ProtectionGroup;
import gov.nih.nci.security.authorization.domainobjects.Role;
import gov.nih.nci.security.dao.GroupSearchCriteria;
import gov.nih.nci.security.dao.PrivilegeSearchCriteria;
import gov.nih.nci.security.dao.SearchCriteria;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;
import gov.nih.nci.security.exceptions.CSTransactionException;

/**
 * @author falguni_sachde
 *
 */
public class AssignPrivilege
{

	private static Map<String, Map<String, Map<String, List<NameValueBean>>>> privilegeMap = new HashMap<String, Map<String, Map<String, List<NameValueBean>>>>();
	private static Set<gov.nih.nci.security.authorization.domainobjects.User> PICordinatorUsers = new HashSet<gov.nih.nci.security.authorization.domainobjects.User>();
	private static Map<String, String> privilegeIds = new HashMap<String, String>();

	/**
	* @param args
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	*/
	public static void main(String[] args) throws InstantiationException, IllegalAccessException
	{
		try
		{
			ErrorKey.init("~");
			ApplicationProperties.initBundle("ApplicationResources");
			/*String fileName = "C://Documents and Settings//shital_lawhale//Desktop//AssignPrivilage.xml";//args[0];
			System.setProperty(
			                     "gov.nih.nci.security.configFile",
			                     "F:/caBIG/caBigworkspace/clinportal_svn/catissuecore-properties/ApplicationSecurityConfig.xml");
			*/if (args.length < 2)
			{
				throw new BizLogicException(ErrorKey.getErrorKey("filename.required"), null,
						"Improper arguments");
			}
			String fileName = args[0];
			System.setProperty("gov.nih.nci.security.configFile", args[1]);
			Utility.readPrivileges(fileName, privilegeMap);
			cachePrivileges();
			validate();
			changeprivilege();
		}
		catch (ApplicationException e)
		{
			if (e.getLogMessage() != null)
			{
				System.out.println(e.getLogMessage());
			}
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * 
	  * @throws DAOException
	 */
	private static void cachePrivileges() throws DAOException
	{
		cachePrivilegeId(PrivilegeConstants.READ);
		cachePrivilegeId(PrivilegeConstants.UPDATE);
	}

	/**
	 * 
	 * @param key
	 * @throws DAOException
	 */
	private static void cachePrivilegeId(String key) throws DAOException
	{
		try
		{
			Privilege privilege = new Privilege();
			privilege.setName(key);
			PrivilegeSearchCriteria privSearchCriteria = new PrivilegeSearchCriteria(privilege);
			List list = ProvisionManager.getInstance().getUserProvisioningManager().getObjects(
					privSearchCriteria);
			if (!list.isEmpty())
			{
				privilege = (Privilege) list.get(0);
				privilegeIds.put(key, privilege.getId().toString());
			}
		}
		catch (SMException e)
		{
			throw new DAOException(ErrorKey.getErrorKey("csm.getinstance.error"), e, null);
		}
	}

	/**
	 * 
	 * @param csId
	 * @throws DAOException
	 */
	private static void getCSUsers(String csId) throws DAOException
	{
		try
		{
			PICordinatorUsers = new HashSet<gov.nih.nci.security.authorization.domainobjects.User>();
			Long PIGrpId = getUserGroupID(PrivilegeConstants.PI_USER_GROUP + csId);
			Long coordinatorGrpId = getUserGroupID(PrivilegeConstants.CO0RDINATOR_USER_GROUP + csId);
			PrivilegeUtility privUtility = new PrivilegeUtility();
			PICordinatorUsers.addAll(privUtility.getUserProvisioningManager().getUsers(
					PIGrpId.toString()));
			PICordinatorUsers.addAll(privUtility.getUserProvisioningManager().getUsers(
					coordinatorGrpId.toString()));
		}
		catch (CSObjectNotFoundException e)
		{
			throw new DAOException(ErrorKey.getErrorKey("csm.getinstance.error"), e,
					"User not found");
		}
		catch (SMException e)
		{
			throw new DAOException(ErrorKey.getErrorKey("csm.getinstance.error"), e, null);
		}
	}

	/**
	 * 
	 * @throws DAOException
	 * @throws BizLogicException
	 */
	private static void validate() throws DAOException, BizLogicException
	{
		Set<String> clinicalStudySet = privilegeMap.keySet();
		DefaultBizLogic bizLogic = new ClinicalStudyBizLogic();
		for (String title : clinicalStudySet)
		{
			String csId = getCSId(title);
			getCSUsers(csId);
			List<ClinicalStudy> cslist = bizLogic.retrieve(SpecimenProtocol.class.getName(),
					PrivilegeConstants.CLINICAL_STUDY_TITLE, title);
			if (cslist.isEmpty()
					|| cslist.get(0).getActivityStatus().equals(Constants.ACTIVITY_STATUS_DISABLED))
			{
				throw new BizLogicException(ErrorKey.getErrorKey("reviewSPR.report.request"), null,
						"Disabled study");
			}
			ClinicalStudy clinicalStudy = cslist.get(0);
			iterateEvents(title, clinicalStudy);
		}
	}

	/**
	 * 
	 * @param title
	 * @param clinicalStudy
	 * @throws DAOException
	 * @throws BizLogicException
	 */
	private static void iterateEvents(String title, ClinicalStudy clinicalStudy)
			throws DAOException, BizLogicException
	{
		Map<String, Map<String, List<NameValueBean>>> eventMap = privilegeMap.get(title);
		Set<String> eventKeys = eventMap.keySet();
		for (String evntLabel : eventKeys)
		{
			if (!"".equals(evntLabel) && evntLabel.equals(PrivilegeConstants.ELEMENT_CSNONUSERS))
			{
				List<NameValueBean> users = (List<NameValueBean>) eventMap
						.get(PrivilegeConstants.ELEMENT_CSNONUSERS);
				checkForValidUser(users);
				checkNonUsers(clinicalStudy, users);
			}
			else if (!"".equals(evntLabel)
					&& evntLabel.equals(PrivilegeConstants.PARTICIPANT_REGISTRATION))
			{
				List<NameValueBean> users = (List<NameValueBean>) eventMap
						.get(PrivilegeConstants.PARTICIPANT_REGISTRATION);
				checkForValidUser(users);
				checkPICord(clinicalStudy, PrivilegeConstants.PARTICIPANT_REGISTRATION, users);
			}
			else
			{
				Map<String, List<NameValueBean>> formMap = eventMap.get(evntLabel);
				Set<String> frmKeys = formMap.keySet();
				for (String frmLabel : frmKeys)
				{
					List<NameValueBean> users = formMap.get(frmLabel);
					checkForValidUser(users);
					checkPICord(clinicalStudy, frmLabel, users);
				}
			}
		}
	}

	/**
	 * 
	 * @param users
	 * @throws DAOException 
	 */
	private static void checkForValidUser(List<NameValueBean> users) throws DAOException
	{
		for (NameValueBean nameValueBean : users)
		{
			if (!nameValueBean.getName().equalsIgnoreCase(Constants.USER_ALL))
			{
				getUserId(nameValueBean.getName());
			}
		}
	}

	/**
	 * 
	 * @param clinicalStudy
	 * @param frmLabel
	 * @param users
	 * @throws BizLogicException
	 */
	private static void checkPICord(ClinicalStudy clinicalStudy, String frmLabel,
			List<NameValueBean> users) throws BizLogicException
	{
		boolean flag = false;
		User principalInvestigator = clinicalStudy.getPrincipalInvestigator();
		for (NameValueBean userBean : users)
		{
			if (!userBean.getName().equalsIgnoreCase(Constants.USER_ALL))
			{
				flag = false;
				if (principalInvestigator.getLoginName().equals(userBean.getName()))
				{
					flag = true;
				}
				else
				{
					flag = checkCoordinators(userBean);
				}
				if (!flag)
				{
					throw new BizLogicException(ErrorKey.getErrorKey("xml.parse.error"), null,
							"In " + frmLabel + " node of XML - User " + userBean.getName()
									+ " not PI/Coord of study " + clinicalStudy.getTitle());
				}
			}
		}
	}

	/**
	 * 
	 * @param userBean
	 * @return
	 */
	private static boolean checkCoordinators(NameValueBean userBean)
	{
		boolean flag = false;
		for (gov.nih.nci.security.authorization.domainobjects.User user : PICordinatorUsers)
		{
			if (user.getLoginName().equals(userBean.getName())
					&& !userBean.getName().equalsIgnoreCase(Constants.USER_ALL))
			{
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * 
	 * @param clinicalStudy
	 * @param users
	 * @throws BizLogicException
	 */
	private static void checkNonUsers(ClinicalStudy clinicalStudy, List<NameValueBean> users)
			throws BizLogicException
	{
		User principalInvestigator = clinicalStudy.getPrincipalInvestigator();
		for (NameValueBean userBean : users)
		{
			if (!userBean.getName().equalsIgnoreCase(Constants.USER_ALL))
			{
				if (principalInvestigator.getLoginName().equals(userBean.getName()))
				{
					throw new BizLogicException(ErrorKey.getErrorKey("xml.parse.error"), null,
							"In csNonUsers node of XML - User " + userBean.getName()
									+ " is PI/Coord of study " + clinicalStudy.getTitle());
				}
				else
				{
					if (checkCoordinators(userBean))
					{
						throw new BizLogicException(ErrorKey.getErrorKey("xml.parse.error"), null,
								"In XML - User " + userBean.getName() + " is PI/Coord of study "
										+ clinicalStudy.getTitle());
					}

				}
			}
		}
	}

	/**
	 * @throws DAOException 
	 * @throws BizLogicException
	 */
	private static void changeprivilege() throws BizLogicException, DAOException
	{
		Set<String> clinicalStudySet = privilegeMap.keySet();
		List msg = new ArrayList();
		//  try
		{
			for (String title : clinicalStudySet)
			{
				String csId = getCSId(title);
				Map<String, Map<String, List<NameValueBean>>> eventMap = privilegeMap.get(title);
				Set<String> eventKeys = eventMap.keySet();
				for (String evntLabel : eventKeys)
				{
					if (!"".equals(evntLabel)
							&& evntLabel.equals(PrivilegeConstants.ELEMENT_CSNONUSERS))
					{
						List<NameValueBean> users = (List<NameValueBean>) eventMap
								.get(PrivilegeConstants.ELEMENT_CSNONUSERS);
						assignPrivilegesToNonCSUsers(csId, title, users, msg);
					}
					else if (!"".equals(evntLabel)
							&& !evntLabel.equals(PrivilegeConstants.ELEMENT_CSNONUSERS)
							&& !evntLabel.equals(PrivilegeConstants.PARTICIPANT_REGISTRATION))
					{
						assignPrivilegesToFormUsers(title, eventMap, msg);
					}
					else if (!"".equals(evntLabel)
							&& evntLabel.equals(PrivilegeConstants.PARTICIPANT_REGISTRATION))
					{
						List<NameValueBean> users = (List<NameValueBean>) eventMap
								.get(PrivilegeConstants.PARTICIPANT_REGISTRATION);
						assignPrivilegesForParticipantReg(csId, users, title, msg);
					}
				}
			}
			System.out.println("=================================================");
			for (int i = 0; i < msg.size(); i++)
			{
				System.out.println(msg.get(i));
			}
			System.out.println("=================================================");
		}
	}

	/**
	 * 
	 * @param csId
	 * @param users
	 * @throws DAOException
	 */
	private static void assignPrivilegesForParticipantReg(String csId, List<NameValueBean> users,
			String title, List msg) throws DAOException
	{
		try
		{
			PrivilegeUtility privUtility = new PrivilegeUtility();
			Role disallowRegRole = Utility.getRole(PrivilegeConstants.DISALLOW_REG);
			String[] rolesArray = new String[1];
			String protectGrpName = PrivilegeConstants.CLINICAL_STUDY_PROTECTION_GROUP + csId;
			ProtectionGroup protectionGroup = privUtility.getProtectionGroup(protectGrpName);
			Iterator<NameValueBean> userIterator = users.iterator();
			UserProvisioningManager upManager = ProvisionManager.getInstance()
					.getUserProvisioningManager();
			List<NameValueBean> allusrs = getAllCSUsers(users);
			if (!allusrs.isEmpty())
			{
				userIterator = allusrs.iterator();
			}
			while (userIterator.hasNext())
			{
				NameValueBean objUserDetail = userIterator.next();
				String userId = getUserId(objUserDetail.getName());
				ISecurityManager securityManager = SecurityManagerFactory.getSecurityManager();
				gov.nih.nci.security.authorization.domainobjects.User csmUser = securityManager
						.getUser(objUserDetail.getName());
				if (userId != null && csmUser != null)
				{
					String userGroupName = PrivilegeConstants.USER_PROTECTION_GROUP + userId;
					Long userGrpId = getUserGroupIDs(userGroupName, csmUser.getUserId().toString());
					if (objUserDetail.getValue().equalsIgnoreCase(
							PrivilegeConstants.XML_DISALLOW_REG))
					{
						rolesArray[0] = disallowRegRole.getId().toString();
						upManager.assignGroupRoleToProtectionGroup(protectionGroup
								.getProtectionGroupId().toString(), userGrpId.toString(),
								rolesArray);
						msg.add("Assigned " + objUserDetail.getValue() + " Privilege to User: "
								+ objUserDetail.getName() + " on Clinical Study: " + title);
					}
					else if (objUserDetail.getValue().equalsIgnoreCase(
							PrivilegeConstants.XML_ALLOW_REG))
					{
						rolesArray[0] = disallowRegRole.getId().toString();
						upManager.removeGroupRoleFromProtectionGroup(protectionGroup
								.getProtectionGroupId().toString(), userGrpId.toString(),
								rolesArray);
						msg.add("Assigned " + objUserDetail.getValue() + " Privilege to User: "
								+ objUserDetail.getName() + " on Clinical Study: " + title);
					}
				}
			}
		}
		catch (SMException e)
		{
			throw new DAOException(ErrorKey.getErrorKey("csm.getinstance.error"), e, null);
		}
		catch (CSTransactionException e)
		{
			throw new DAOException(ErrorKey.getErrorKey("sm.operation.error"), e,
					"AssignPrivilege assignGroupRoleToProtectionGroup()");
		}

	}

	private static List<NameValueBean> getAllCSUsers(List<NameValueBean> users)
	{
		Iterator<NameValueBean> userIterator = users.iterator();
		List<NameValueBean> userList = new ArrayList<NameValueBean>();
		while (userIterator.hasNext())
		{
			NameValueBean userBean = userIterator.next();
			String name = userBean.getName();
			String privilege = userBean.getValue();
			if (name.equalsIgnoreCase(Constants.USER_ALL))
			{
				for (gov.nih.nci.security.authorization.domainobjects.User user : PICordinatorUsers)
				{
					NameValueBean userBean1 = new NameValueBean(user.getLoginName(), privilege);
					userList.add(userBean1);
				}
			}
			else
			{
				userList.add(userBean);
			}
		}
		return userList;

	}

	/**
	 * 
	 * @param title
	 * @param eventMap
	 * @throws DAOException 
	 * @throws BizLogicException 
	 */
	private static void assignPrivilegesToFormUsers(String title,
			Map<String, Map<String, List<NameValueBean>>> eventMap, List msg) throws DAOException,
			BizLogicException
	{
		Set<String> eventKeys = eventMap.keySet();
		for (String evntLabel : eventKeys)
		{
			if (!evntLabel.equals(PrivilegeConstants.ELEMENT_CSNONUSERS)
					&& !evntLabel.equals(PrivilegeConstants.PARTICIPANT_REGISTRATION))
			{
				String eventId = getEventId(evntLabel, title);
				Map<String, List<NameValueBean>> formMap = eventMap.get(evntLabel);
				Set<String> frmLabelKeys = formMap.keySet();
				for (String frmLabel : frmLabelKeys)
				{
					String frmCntxtId = getFrmCntxtId(frmLabel, eventId);
					List<NameValueBean> userBeanList = formMap.get(frmLabel);
					for (NameValueBean userBean : userBeanList)
					{
						if (userBean.getName().equalsIgnoreCase(Constants.USER_ALL))
						{
							getAllPICoordUsers(frmCntxtId, userBean.getValue(), frmLabel, msg);
						}
						else
						{
							insertAuthorizationData(frmCntxtId, userBean, frmLabel, msg);
						}
						//						System.out
						//								.println("=============================================================");
						//						System.out.println("Assigned " + userBean.getValue()
						//								+ " Privilege to User: " + userBean.getName() + " on FORM: "
						//								+ frmLabel);
						//						System.out
						//								.println("=============================================================");

					}
				}
			}
		}
	}

	/**
	 * 
	 * @param frmCntxtId
	 * @param privilege
	 * @throws DAOException
	 * @throws BizLogicException
	 */
	private static void getAllPICoordUsers(String frmCntxtId, String privilege, String frmLabel,
			List msg) throws DAOException, BizLogicException
	{
		for (gov.nih.nci.security.authorization.domainobjects.User user : PICordinatorUsers)
		{
			NameValueBean userBean = new NameValueBean(user.getLoginName(), privilege);
			insertAuthorizationData(frmCntxtId, userBean, frmLabel, msg);
		}
	}

	/**
	 * 
	 * @param title
	 * @return
	 * @throws DAOException
	 */
	private static String getCSId(String title) throws DAOException
	{
		String csId = null;
		String csQuery = "select id from " + ClinicalStudy.class.getName()
				+ " where activityStatus = 'Active' and title= '" + title + "'";
		List result = Utility.executeQuery(csQuery);
		if (result != null && !result.isEmpty())
		{
			csId = result.get(0).toString();
		}
		if (csId == null)
		{
			throw new DAOException(ErrorKey.getErrorKey("biz.ret.error"), null, title
					+ " Study doesnt exists");
		}
		return csId;
	}

	/**
	 * 
	 * @param loginName
	 * @return
	 * @throws DAOException
	 */
	private static String getUserId(String loginName) throws DAOException
	{
		String userId = null;
		boolean flag = false;
		String userQuery = "select id,activityStatus from " + User.class.getName()
				+ " where loginName= '" + loginName + "'";
		if (null != loginName && (loginName.contains("'") || loginName.contains(" ")))
		{
			flag = true;
		}
		if (!flag)
		{
			List result = Utility.executeQuery(userQuery);
			if (result != null && !result.isEmpty())
			{
				Object[] obj = (Object[]) result.get(0);

				if (obj[1].toString().equals(Constants.ACTIVITY_STATUS_DISABLED))
					userId = "0";
				else
					userId = obj[0].toString();
			}
			if (userId == null)
			{
				throw new DAOException(ErrorKey.getErrorKey("biz.ret.error"), null, loginName
						+ " User doesnt exists");
			}
			if (userId.equals(Constants.ZERO_ID))
				userId = null;
		}
		return userId;
	}

	/**
	 * 
	 * @param evntLabel
	 * @param title
	 * @return
	 * @throws DAOException
	 */
	private static String getEventId(String evntLabel, String title) throws DAOException
	{
		String evntId = null;
		String eventQuery = "select id from " + ClinicalStudyEvent.class.getName()
				+ " where activityStatus = 'Active' and collectionPointLabel= '" + evntLabel
				+ "' and clinicalStudy.title='" + title + "'";

		List result = Utility.executeQuery(eventQuery);
		if (result != null && !result.isEmpty())
		{
			evntId = result.get(0).toString();
		}
		if (evntId == null)
		{
			throw new DAOException(ErrorKey.getErrorKey("biz.ret.error"), null, evntLabel
					+ " Event doesnt exists");
		}
		return evntId;
	}

	/**
	 * 
	 * @param frmLabel
	 * @param eventId
	 * @return
	 * @throws DAOException 
	 */
	private static String getFrmCntxtId(String frmLabel, String eventId) throws DAOException
	{
		String frmCntxtId = null;
		String frmCntxtQuery = "select id from " + StudyFormContext.class.getName()
				+ " where activityStatus = 'Active' and studyFormLabel= '" + frmLabel
				+ "' and clinicalStudyEvent.id='" + eventId + "'";
		List result = Utility.executeQuery(frmCntxtQuery);
		if (result != null && !result.isEmpty())
		{
			frmCntxtId = result.get(0).toString();
		}
		if (frmCntxtId == null)
		{
			throw new DAOException(ErrorKey.getErrorKey("biz.ret.error"), null, frmLabel
					+ " StudyFormContext doesnt exists");
		}
		return frmCntxtId;
	}

	/**
	 * 
	 * @param frmCntxtId 
	 * @param userBean
	 * @throws DAOException 
	 * @throws BizLogicException 
	 */
	private static void insertAuthorizationData(String frmCntxtId, NameValueBean userBean,
			String frmLabel, List msg) throws DAOException, BizLogicException
	{
		try
		{
			UserProvisioningManager upManager = ProvisionManager.getInstance()
					.getUserProvisioningManager();
			Group group = createGroup(userBean);
			Role role = createRole(frmCntxtId, userBean);
			ProtectionGroup protectionGroup = createProtectionGroup(frmCntxtId);
			if (protectionGroup.getProtectionGroupId() != null && group.getGroupId() != null
					&& role.getId() != null)
			{
				String[] rolesArray = {role.getId().toString()};
				upManager.assignGroupRoleToProtectionGroup(protectionGroup.getProtectionGroupId()
						.toString(), group.getGroupId().toString(), rolesArray);
				msg.add("Assigned " + userBean.getValue() + " Privilege to User: "
						+ userBean.getName() + " on FORM: " + frmLabel);
			}
		}
		catch (SMException e)
		{
			throw new DAOException(ErrorKey.getErrorKey("csm.getinstance.error"), e, null);
		}
		catch (CSTransactionException e)
		{
			throw new DAOException(ErrorKey.getErrorKey("sm.operation.error"), e,
					"AssignPrivilege assignGroupRoleToProtectionGroup()");
		}
	}

	/**
	 * 
	 * @param frmCntxtId
	 * @return
	 * @throws BizLogicException
	 */
	private static ProtectionGroup createProtectionGroup(String frmCntxtId)
			throws BizLogicException
	{
		ProtectionGroup protectionGroup = new ProtectionGroup();
		String pgName = PrivilegeConstants.getFrmcntxtPG(frmCntxtId);
		try
		{
			PrivilegeUtility privilegeUtility = new PrivilegeUtility();
			protectionGroup = privilegeUtility.getProtectionGroup(pgName);
		}
		catch (SMException e)
		{
			throw new BizLogicException(ErrorKey.getErrorKey("sm.operation.error"), e, null);
		}
		catch (Exception e)
		{
			try
			{
				protectionGroup = createPG(pgName, frmCntxtId);
			}
			catch (SMException e1)
			{
				throw new BizLogicException(ErrorKey.getErrorKey("sm.operation.error"), e1, null);
			}
			catch (CSTransactionException e1)
			{
				throw new BizLogicException(ErrorKey.getErrorKey("sm.operation.error"), e1, null);
			}
			catch (Exception e1)
			{
				System.out.println(e1.getMessage());
			}
		}
		return protectionGroup;
	}

	/**
	 * 
	 * @param pgName
	 * @param frmCntxtId
	 * @throws SMException
	 * @throws CSTransactionException
	 */
	private static ProtectionGroup createPG(String pgName, String frmCntxtId) throws SMException,
			CSTransactionException
	{
		UserProvisioningManager upManager = ProvisionManager.getInstance()
				.getUserProvisioningManager();
		ProtectionGroup protectionGroup = new ProtectionGroup();
		protectionGroup.setProtectionGroupName(pgName);
		ProtectionElement protectionElement = new ProtectionElement();
		protectionElement.setProtectionElementName(StudyFormContext.class.getName() + "_"
				+ frmCntxtId);
		protectionElement.setObjectId(StudyFormContext.class.getName() + "_" + frmCntxtId);

		upManager.createProtectionElement(protectionElement);
		upManager.createProtectionGroup(protectionGroup);
		upManager.assignProtectionElement(pgName, StudyFormContext.class.getName() + "_"
				+ frmCntxtId);
		return protectionGroup;
	}

	/**
	 * 
	 * @param userBean
	 * @return
	 * @throws BizLogicException
	 * @throws DAOException
	 */
	private static Group createGroup(NameValueBean userBean) throws BizLogicException, DAOException
	{
		Group group = new Group();
		try
		{
			ISecurityManager securityManager = SecurityManagerFactory.getSecurityManager();

			String userId = getUserId(userBean.getName());
			gov.nih.nci.security.authorization.domainobjects.User csmUser = securityManager
					.getUser(userBean.getName());
			if (userId != null && csmUser != null)
			{
				Set<gov.nih.nci.security.authorization.domainobjects.User> users = new HashSet<gov.nih.nci.security.authorization.domainobjects.User>();
				users.add(csmUser);
				group.setUsers(users);
				group.setGroupName(PrivilegeConstants.getUserGroup(userId));
				//Long grpId = getUserGroupID(PrivilegeConstants.getUserGroup(userId));
				Long grpId = getUserGroupIDs(PrivilegeConstants.getUserGroup(userId), csmUser
						.getUserId().toString());
				if (grpId == null)
				{
					PrivilegeUtility privilegeUtility = new PrivilegeUtility();
					privilegeUtility.getUserProvisioningManager().createGroup(group);
				}
				else
				{
					group.setGroupId(grpId);
				}
			}
		}
		catch (SMException e)
		{
			throw new DAOException(ErrorKey.getErrorKey("csm.getinstance.error"), e, null);
		}
		catch (CSTransactionException e)
		{
			throw new BizLogicException(ErrorKey.getErrorKey("sm.operation.error"), e, null);
		}
		return group;
	}

	/**
	 * 
	 * @param frmCntxtId
	 * @param userBean
	 * @return
	 * @throws DAOException
	 * @throws BizLogicException
	 */
	private static Role createRole(String frmCntxtId, NameValueBean userBean) throws DAOException,
			BizLogicException
	{
		Role role = new Role();
		try
		{
			Set<String> privileges = new HashSet<String>();
			String userId = getUserId(userBean.getName());
			if (userId != null)
			{
				String roleName = PrivilegeConstants.getRoleName(frmCntxtId, userId);
				Utility.processRole(roleName);
				if (userBean.getValue().equalsIgnoreCase(PrivilegeConstants.ALLOW_READ))
				{
					privileges.add(privilegeIds.get(PrivilegeConstants.READ));
				}
				else if (userBean.getValue().equalsIgnoreCase(PrivilegeConstants.ALLOW_READ_WRITE))
				{
					privileges.add(privilegeIds.get(PrivilegeConstants.UPDATE));
					privileges.add(privilegeIds.get(PrivilegeConstants.READ));
				}
				PrivilegeManager.getInstance().createRole(roleName, privileges);
				role = Utility.getRole(roleName);
			}
		}
		catch (SMException e)
		{
			throw new DAOException(ErrorKey.getErrorKey("sm.operation.error"), e, null);
		}
		return role;
	}

	/**
	 * 
	 * @param csID
	 * @param title
	 * @param users
	 * @param msg
	 * @throws DAOException
	 * @throws BizLogicException 
	 */
	private static void assignPrivilegesToNonCSUsers(String csID, String title,
			List<NameValueBean> users, List msg) throws DAOException, BizLogicException
	{
		try
		{
			UserProvisioningManager upManager = ProvisionManager.getInstance()
					.getUserProvisioningManager();
			PrivilegeUtility privUtility = new PrivilegeUtility();
			Long csId = Long.valueOf(csID);
			Iterator<NameValueBean> userIterator = users.iterator();
			String protectGrpName = PrivilegeConstants.CLINICAL_STUDY_PROTECTION_GROUP + csId;
			ProtectionGroup protectionGroup = privUtility.getProtectionGroup(protectGrpName);
			List<NameValueBean> allusrs = getAllUsers(users);
			if (!allusrs.isEmpty())
			{
				userIterator = allusrs.iterator();
			}
			while (userIterator.hasNext())
			{
				NameValueBean objUserDetail = userIterator.next();
				String loginName = objUserDetail.getName();
				String privilege = objUserDetail.getValue();
				String userId = getUserId(loginName);
				if (userId != null)
				{
					ISecurityManager securityManager = SecurityManagerFactory.getSecurityManager();
					gov.nih.nci.security.authorization.domainobjects.User csmUser = securityManager
							.getUser(loginName);
					if (csmUser != null)
					{
						Role adminRole = securityManager.getUserRole(csmUser.getUserId());
						if (adminRole == null
								|| !adminRole.getName().toString().equalsIgnoreCase(
										Roles.ADMINISTRATOR))
						{

							Long PIGrpId = getUserGroupID(PrivilegeConstants.PI_USER_GROUP + csId);
							Long coordinatorGrpId = getUserGroupID(PrivilegeConstants.CO0RDINATOR_USER_GROUP
									+ csId);

							Set<gov.nih.nci.security.authorization.domainobjects.User> PICordinatorUsers = new HashSet<gov.nih.nci.security.authorization.domainobjects.User>();
							PICordinatorUsers.addAll(privUtility.getUserProvisioningManager()
									.getUsers(PIGrpId.toString()));
							PICordinatorUsers.addAll(privUtility.getUserProvisioningManager()
									.getUsers(coordinatorGrpId.toString()));
							boolean isUsrPICoordtor = false;
							for (gov.nih.nci.security.authorization.domainobjects.User user : PICordinatorUsers)
							{
								if (user.getEmailId().equals(loginName))
								{
									List<String> placeHolders = new ArrayList<String>();
									placeHolders.add(loginName);
									placeHolders.add(title);
									msg.add(ApplicationProperties.getValue("user.invalid",
											placeHolders));
									isUsrPICoordtor = true;
								}
							}
							if (!isUsrPICoordtor)
							{

								Role role = privUtility.getRole(Permissions.READ_DENIED);
								String[] rolesArray = {role.getId().toString()};

								String userGroupName = PrivilegeConstants.USER_PROTECTION_GROUP
										+ userId;
								Long userGrpId = //getUserGroupID(userGroupName);
								getUserGroupIDs(userGroupName, csmUser.getUserId().toString());

								if (privilege.equalsIgnoreCase(PrivilegeConstants.PRIVILEGE_DENY))
								{
									upManager.assignGroupRoleToProtectionGroup(protectionGroup
											.getProtectionGroupId().toString(), userGrpId
											.toString(), rolesArray);
									msg.add("User: " + loginName
											+ " is DENIED to view clinical study " + title);

								}
								else if (privilege
										.equalsIgnoreCase(PrivilegeConstants.PRIVILEGE_ALLOW))
								{
									upManager.removeGroupRoleFromProtectionGroup(protectionGroup
											.getProtectionGroupId().toString(), userGrpId
											.toString(), rolesArray);
									msg.add("User: " + loginName
											+ " is ALLOWED to view clinical study " + title);
								}
								else
								{
									msg.add(ApplicationProperties.getValue("privilege.invalid",
											privilege));
								}
							}
						}
						else if (adminRole != null)
						{
							msg.add("Cannot change privilege for ADMINISTRATOR " + loginName);
						}
					}
				}
			}
		}
		catch (SMException e)
		{
			throw new DAOException(ErrorKey.getErrorKey("sm.operation.error"), e, null);
		}
		catch (CSTransactionException e)
		{
			throw new DAOException(ErrorKey.getErrorKey("sm.operation.error"), e,
					"AssignPrivilege assignGroupRoleToProtectionGroup()");
		}
		catch (CSObjectNotFoundException e)
		{
			throw new DAOException(ErrorKey.getErrorKey("sm.operation.error"), e,
					"AssignPrivilege getUsers()");
		}

	}

	/**
	 * 
	 * @param userGroupName
	 * @param csmUserId
	 * @return
	 * @throws DAOException
	 */
	private static Long getUserGroupIDs(String userGroupName, String csmUserId) throws DAOException
	{
		Long gid = null;
		try
		{
			UserProvisioningManager upManager = ProvisionManager.getInstance()
					.getUserProvisioningManager();
			Group group = new Group();
			group.setGroupName(userGroupName);
			SearchCriteria searchCriteria = new GroupSearchCriteria(group);
			group.setApplication(new PrivilegeUtility()
					.getApplication(SecurityManagerPropertiesLocator.getInstance()
							.getApplicationCtxName()));
			List list = ProvisionManager.getInstance().getUserProvisioningManager().getObjects(
					searchCriteria);
			if (!list.isEmpty())
			{
				group = (Group) list.get(0);
				gid = group.getGroupId();
			}
			else
			{
				upManager.createGroup(group);
				new PrivilegeUtility().assignAdditionalGroupsToUser(csmUserId, new String[]{String
						.valueOf(group.getGroupId())});
				gid = group.getGroupId();
			}

		}
		catch (SMException e)
		{
			throw new DAOException(ErrorKey.getErrorKey("csm.getinstance.error"), e, null);
		}
		catch (CSTransactionException e)
		{
			throw new DAOException(ErrorKey.getErrorKey("csm.getinstance.error"), e, null);
		}
		return gid;
	}

	/**
	 * 
	 * @param users
	 * @return
	 * @throws DAOException
	 */
	private static List<NameValueBean> getAllUsers(List<NameValueBean> users) throws DAOException
	{
		Iterator<NameValueBean> userIterator = users.iterator();
		List<NameValueBean> userList = new ArrayList<NameValueBean>();
		while (userIterator.hasNext())
		{
			NameValueBean userBean = userIterator.next();
			String name = userBean.getName();
			String privilege = userBean.getValue();
			if (name.equalsIgnoreCase(Constants.USER_ALL))
			{
				userList = getAllNonUsersList(privilege);
			}
			else
			{
				userList.add(userBean);
			}
		}
		return userList;
	}

	/**
	 * 
	 * @param privileg
	 * @return
	 * @throws DAOException
	 */
	private static List<NameValueBean> getAllNonUsersList(String privileg) throws DAOException
	{
		StringBuffer buffer = new StringBuffer();
		for (gov.nih.nci.security.authorization.domainobjects.User user : PICordinatorUsers)
		{
			buffer.append("'").append(user.getLoginName()).append("',");
		}
		if (buffer.length() > 1)
		{
			buffer.deleteCharAt(buffer.length() - 1);
		}

		String query = "select loginName from " + User.class.getName()
				+ " where activityStatus='Active' and loginName not in (" + buffer.toString() + ")";
		System.out.println(query);
		List<String> result = Utility.executeQuery(query);
		List<NameValueBean> userList = new ArrayList<NameValueBean>();
		for (String string : result)
		{
			userList.add(new NameValueBean(string, privileg));
		}
		return userList;
	}

	/**
	 * @param userGroupName
	 * @return
	 * @throws DAOException
	 */
	private static Long getUserGroupID(String userGroupName) throws DAOException
	{
		Long gid = null;
		try
		{
			Group group = new Group();
			group.setGroupName(userGroupName);
			SearchCriteria searchCriteria = new GroupSearchCriteria(group);
			group.setApplication(new PrivilegeUtility()
					.getApplication(SecurityManagerPropertiesLocator.getInstance()
							.getApplicationCtxName()));
			List list = ProvisionManager.getInstance().getUserProvisioningManager().getObjects(
					searchCriteria);
			if (!list.isEmpty())
			{
				group = (Group) list.get(0);
				gid = group.getGroupId();
			}
		}
		catch (SMException e)
		{
			throw new DAOException(ErrorKey.getErrorKey("csm.getinstance.error"), e, null);
		}
		return gid;
	}
}
