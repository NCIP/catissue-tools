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
 * <p>Title: User </p>
 * <p>Description: A person who interacts with the caTISSUE Core 
 * data system and/or participates in the process of biospecimen 
 * collection, processing, or utilization.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author deepali_ahirrao
 * @version 1.00
 */

package edu.wustl.clinportal.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import edu.wustl.clinportal.actionForm.UserForm;
import edu.wustl.clinportal.util.SearchUtil;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.actionForm.IValueObject;
import edu.wustl.common.bizlogic.IActivityStatus;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.security.exception.SMException;
import edu.wustl.security.manager.SecurityManagerFactory;
import gov.nih.nci.security.authorization.domainobjects.Role;

/**
 * A person who interacts with the caTISSUE Core data system
 * and/or participates in the process of bio specimen collection,
 * processing, or utilization.
 * @hibernate.class table="CATISSUE_USER"
 */
public class User extends AbstractDomainObject implements Serializable, IActivityStatus
{

	/**
	 * System generated unique id.
	 */
	protected Long id;

	//Change for API Search   --- Ashwin 04/10/2006
	/**
	 * A string containing the Last Name of the user.
	 */
	protected String lastName;

	//Change for API Search   --- Ashwin 04/10/2006
	/**
	 * A string containing the First Name of the user.
	 */
	protected String firstName;

	//Change for API Search   --- Ashwin 04/10/2006
	/**
	 * A string containing the login name of the user.
	 */
	protected String loginName;

	//Change for API Search   --- Ashwin 04/10/2006
	/**
	 * EmailAddress of the user.
	 */
	protected String emailAddress;

	/**
	 * Old password of this user.
	 */
	protected String oldPassword;

	/**
	 * EmailAddress Address of the user.
	 */
	protected String newPassword;

	/**
	 * Date of user registration.
	 */
	protected Date startDate;

	//Change for API Search   --- Ashwin 04/10/2006
	/**
	 * Institute of the user.
	 */
	protected Institution institution;

	//Change for API Search   --- Ashwin 04/10/2006
	/**
	 * Department of the user.
	 */
	protected Department department;

	//Change for API Search   --- Ashwin 04/10/2006
	/**
	 * Contact address of the User.
	 */
	protected Address address;

	//Change for API Search   --- Ashwin 04/10/2006
	/**
	 * Cancer Research Group to which the user belongs.
	 */
	protected CancerResearchGroup cancerResearchGroup;

	/**
	 * Activity Status of user, it could be CLOSED, ACTIVE, DISABLED.
	 */
	protected String activityStatus;

	/**
	 * Comments given by the approver.
	 */
	protected String comments;

	/**
	 * Role id of the user.
	 */
	protected String roleId;

	protected Collection clinicalStudyCollection = new HashSet();

	protected String pageOf;

	/**
	 * Identifier of this user in csm user table. 
	 */
	protected Long csmUserId;

	//Change for API Search   --- Ashwin 04/10/2006
	/**
	 * whether user is logging for the first time
	 */
	protected Boolean firstTimeLogin;

	protected Boolean adminuser;

	protected Collection<Site> siteCollection = new HashSet<Site>();

	/**
	 * Initialize a new User instance.
	 * Note: Hibernate invokes this constructor through reflection API.  
	 */

	/**
	 * Set of passwod collection for the user.
	 */
	protected Collection passwordCollection = new HashSet();

	/**
	 * 
	 */
	public User()
	{
	}

	/**
	 * This Constructor Copies the data from an UserForm object to a User object.
	 * @param user An UserForm object containing the information about the user. 
	 */
	public User(UserForm uform)
	{
		this();
		setAllValues(uform);
	}

	/**
	 * Returns the id assigned to user.
	 * @hibernate.id name="id" column="IDENTIFIER" type="long" length="30"
	 * unsaved-value="null" generator-class="native"
	 * @hibernate.generator-param name="sequence" value="CATISSUE_USER_SEQ"
	 * @return Returns the id.
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * @param idt The id to set.
	 */
	public void setId(Long idt)
	{
		this.id = idt;
	}

	/**
	 * Returns the password assigned to user.
	 * @hibernate.property name="emailAddress" type="string" column="EMAIL_ADDRESS" length="255"
	 * @return Returns the password.
	 */
	public String getEmailAddress()
	{
		return emailAddress;
	}

	/**
	 * @param emailAddress The emailAddress to set.
	 */
	public void setEmailAddress(String emailAddress)
	{
		this.emailAddress = emailAddress;
	}

	/**
	 * @return Returns the oldPassword.
	 */
	public String getOldPassword()
	{
		return oldPassword;
	}

	/**
	 * @param oldPassword The oldPassword to set.
	 */
	public void setOldPassword(String oldPassword)
	{
		this.oldPassword = oldPassword;
	}

	//@hibernate.property name="password" type="string" column="PASSWORD" length="50"
	/**
	 * Returns the newPassword assigned to user.
	 * @return Returns the newPassword.
	 */
	public String getNewPassword()
	{
		return newPassword;
	}

	/**
	 * @param newPassword The new Password to set.
	 */
	public void setNewPassword(String newPassword)
	{
		this.newPassword = newPassword;
	}

	/**
	 * Returns the firstname assigned to user.
	 * @hibernate.property name="firstName" type="string" column="FIRST_NAME" length="255"
	 * @return Returns the firstName.
	 */
	public String getFirstName()
	{
		return firstName;
	}

	/**
	 * @param firstName The firstName to set.
	 */
	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	/**
	 * Returns the lastname assigned to user.
	 * @hibernate.property name="lastName" type="string" column="LAST_NAME" length="255"
	 * @return Returns the lastName.
	 */
	public String getLastName()
	{
		return lastName;
	}

	/**
	 * @param lastName The lastName to set.
	 */
	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	/**
	 * Returns the loginname assigned to user.
	 * @hibernate.property name="loginName" type="string" column="LOGIN_NAME" length="255" 
	 * not-null="true" unique="true"
	 * @return Returns the loginName.
	 */
	public String getLoginName()
	{
		return loginName;
	}

	/**
	 * @param loginName The loginName to set.
	 */
	public void setLoginName(String loginName)
	{
		this.loginName = loginName;
	}

	/**
	 * Returns the date when the user is added to the system.
	 * @hibernate.property name="startDate" type="date" column="START_DATE"
	 * @return Returns the dateAdded.
	 */
	public Date getStartDate()
	{
		return startDate;
	}

	/**
	 * @param startDate The startDate to set.
	 */
	public void setStartDate(Date startDate)
	{
		this.startDate = startDate;
	}

	/**
	 * Returns the activitystatus of the user.
	 * @hibernate.property name="activityStatus" type="string" column="ACTIVITY_STATUS" length="50"
	 * @return Returns the activityStatus.
	 */
	public String getActivityStatus()
	{
		return activityStatus;
	}

	/**
	 * @param activityStatus The activityStatus to set.
	 */
	public void setActivityStatus(String activityStatus)
	{
		this.activityStatus = activityStatus;
	}

	/**
	 * Returns the department of the user.
	 * @hibernate.many-to-one column="DEPARTMENT_ID" class="edu.wustl.clinportal.domain.Department"
	 * constrained="true"
	 * @return the department of the user.
	 */
	public Department getDepartment()
	{
		return department;
	}

	/**
	 * @param department The department to set.
	 */
	public void setDepartment(Department department)
	{
		this.department = department;
	}

	/**
	 * Returns the cancerResearchGroup of the user.
	 * @hibernate.many-to-one column="CANCER_RESEARCH_GROUP_ID" class="edu.wustl.clinportal.domain.CancerResearchGroup"
	 * constrained="true"
	 * @return the cancerResearchGroup of the user.
	 */
	public CancerResearchGroup getCancerResearchGroup()
	{
		return cancerResearchGroup;
	}

	/**
	 * @param cancerResearchGrp The cancerResearchGroup to set.
	 */
	public void setCancerResearchGroup(CancerResearchGroup cancerResearchGrp)
	{
		this.cancerResearchGroup = cancerResearchGrp;
	}

	/**
	 * Returns the institution of the user.
	 * @hibernate.many-to-one column="INSTITUTION_ID" class="edu.wustl.clinportal.domain.Institution"
	 * constrained="true"
	 * @return the institution of the user.
	 */
	public Institution getInstitution()
	{
		return institution;
	}

	/**
	 * @param institution The institution to set.
	 */
	public void setInstitution(Institution institution)
	{
		this.institution = institution;
	}

	/**
	 * Returns the address of the user.
	 * @hibernate.many-to-one column="ADDRESS_ID" class="edu.wustl.clinportal.domain.Address"
	 * constrained="true"
	 * @return the address of the user.
	 */
	public Address getAddress()
	{
		return address;
	}

	/**
	 * @param address The address to set.
	 */
	public void setAddress(Address address)
	{
		this.address = address;
	}

	/**
	 * @return Returns the pageOf.
	 */
	public String getPageOf()
	{
		return pageOf;
	}

	/**
	 * @param pageOf The pageOf to set.
	 */
	public void setPageOf(String pageOf)
	{
		this.pageOf = pageOf;
	}

	/**
	 * Returns the password assigned to user.
	 * @hibernate.property name="csmUserId" type="long" column="CSM_USER_ID" length="20"
	 * @return Returns the password.
	 */
	public Long getCsmUserId()
	{
		return csmUserId;
	}

	/**
	 * @param csmUserId The csmUserId to set.
	 */
	public void setCsmUserId(Long csmUserId)
	{
		this.csmUserId = csmUserId;
	}

	/**
	 * Returns the comments given by the approver. 
	 * @hibernate.property name="comments" type="string" 
	 * column="STATUS_COMMENT" length="2000" 
	 * @return the comments given by the approver.
	 * @see #setComments(String)
	 */
	public String getComments()
	{
		return comments;
	}

	/**
	 * Sets the comments given by the approver.
	 * @param comments The comments to set.
	 * @see #getComments()
	 */
	public void setComments(String commentString)
	{
		if (commentString == null)
		{
			this.comments = "";
		}
		else
		{
			this.comments = commentString;
		}
	}

	/**
	 * @return Returns the roleId.
	 */
	public String getRoleId()
	{
		String roleId = this.roleId;

		try
		{
			if ((roleId == null || roleId.equals("")) && id != null && id != 0 && csmUserId != null)
			{
				Role role = SecurityManagerFactory.getSecurityManager().getUserRole(csmUserId);
				if (role != null && role.getId() != null)
					roleId = role.getId().toString();
			}
		}
		catch (SMException e)
		{
			Logger.out.error(e.getMessage());
		}

		return roleId;
	}

	/**
	 * @param roleId The roleId to set.
	 */
	public void setRoleId(String roleId)
	{
		this.roleId = roleId;
	}

	/**
	 * @hibernate.set name="passwordCollection" table="CATISSUE_PASSWORD"
	 * cascade="save-update" inverse="true" lazy="true"
	 * @hibernate.collection-key column="USER_ID"
	 * @hibernate.collection-one-to-many class="edu.wustl.clinportal.domain.Password"
	 */
	public Collection getPasswordCollection()
	{
		return passwordCollection;
	}

	/**
	 * @return Returns the passwordCollection.
	 */
	public void setPasswordCollection(Collection passwordColln)
	{
		this.passwordCollection = passwordColln;
	}

	/**
	 * This function Copies the data from an UserForm object to a User object.
	 * @param user An UserForm object containing the information about the user.  
	 * */
	public void setAllValues(IValueObject abstractForm)
	{
		try
		{
			//Change for API Search   --- Ashwin 04/10/2006
			if (SearchUtil.isNullobject(lastName))
			{
				lastName = "";
			}

			//Change for API Search   --- Ashwin 04/10/2006
			if (SearchUtil.isNullobject(firstName))
			{
				firstName = "";
			}

			//Change for API Search   --- Ashwin 04/10/2006
			if (SearchUtil.isNullobject(loginName))
			{
				loginName = "";
			}

			//Change for API Search   --- Ashwin 04/10/2006
			if (SearchUtil.isNullobject(emailAddress))
			{
				emailAddress = "";
			}

			//Change for API Search   --- Ashwin 04/10/2006
			if (SearchUtil.isNullobject(address))
			{
				address = new Address();
			}

			//Change for API Search   --- Ashwin 04/10/2006
			if (SearchUtil.isNullobject(institution))
			{
				institution = new Institution();
			}

			//Change for API Search   --- Ashwin 04/10/2006
			if (SearchUtil.isNullobject(department))
			{
				department = new Department();
			}

			//Change for API Search   --- Ashwin 04/10/2006
			if (SearchUtil.isNullobject(cancerResearchGroup))
			{
				cancerResearchGroup = new CancerResearchGroup();
			}

			//Change for API Search   --- Ashwin 04/10/2006
			if (SearchUtil.isNullobject(firstTimeLogin))
			{
				firstTimeLogin = Boolean.TRUE;
			}

			UserForm uform = (UserForm) abstractForm;
			this.pageOf = uform.getPageOf();

			if (pageOf.equals(Constants.PAGEOF_CHANGE_PASSWORD))
			{
				this.newPassword = uform.getNewPassword();
				this.oldPassword = uform.getOldPassword();
			}
			else
			{

				if (!pageOf.equalsIgnoreCase("pageOfSignUp"))
				{
					String[] siteIds = uform.getSiteIds();

					Collection newSiteCollection = new HashSet();
					for (String siteId : siteIds)
					{
						if (!siteId.equals("-1"))
						{
							Site site = new Site();
							site.setId(Long.valueOf(siteId));
							newSiteCollection.add(site);
						}
					}

					this.getSiteCollection().clear();
					this.getSiteCollection().addAll(newSiteCollection);
				}
				this.id = Long.valueOf(uform.getId());
				this.setLoginName(uform.getEmailAddress());
				this.setLastName(uform.getLastName());
				this.setFirstName(uform.getFirstName());
				this.setEmailAddress(uform.getEmailAddress());
				this.setAdminuser(Boolean.valueOf(uform.getAdminuser()));
				this.institution.setId(Long.valueOf(uform.getInstitutionId()));

				this.department.setId(Long.valueOf(uform.getDepartmentId()));
				this.cancerResearchGroup.setId(Long.valueOf(uform.getCancerResearchGroupId()));
				if (Constants.PAGEOF_USER_PROFILE.equals(pageOf) == false)
				{
					this.activityStatus = uform.getActivityStatus();
				}

				if (pageOf.equals(Constants.PAGEOF_SIGNUP))
				{
					this.setStartDate(Calendar.getInstance().getTime());
					this.activityStatus = Constants.ACTIVITY_STATUS_NEW;
				}

				if (!pageOf.equals(Constants.PAGEOF_SIGNUP)
						&& !pageOf.equals(Constants.PAGEOF_USER_PROFILE))
				{
					this.comments = uform.getComments();
				}

				if (uform.getPageOf().equals(Constants.PAGEOF_USER_ADMIN)
						&& uform.getOperation().equals(Constants.ADD))
				{
					this.activityStatus = Constants.ACTIVITY_STATUS_ACTIVE;
					this.setStartDate(Calendar.getInstance().getTime());
				}

				//Bug-1516: Jitendra
				if (uform.getPageOf().equals(Constants.PAGEOF_USER_ADMIN)
						&& uform.getOperation().equals(Constants.EDIT))
				{
					this.newPassword = uform.getNewPassword();
				}

				if (uform.getPageOf().equals(Constants.PAGEOF_APPROVE_USER))
				{
					if (uform.getStatus().equals(Constants.APPROVE_USER_APPROVE_STATUS))
					{
						this.activityStatus = Constants.ACTIVITY_STATUS_ACTIVE;
					}
					else if (uform.getStatus().equals(Constants.APPROVE_USER_REJECT_STATUS))
					{
						this.activityStatus = Constants.ACTIVITY_STATUS_REJECT;
					}
					else
					{
						this.activityStatus = Constants.ACTIVITY_STATUS_PENDING;
					}
				}

				this.roleId = uform.getRole();
				this.address.setStreet(uform.getStreet());
				this.address.setCity(uform.getCity());
				this.address.setState(uform.getState());
				this.address.setCountry(uform.getCountry());
				this.address.setZipCode(uform.getZipCode());
				this.address.setPhoneNumber(uform.getPhoneNumber());
				this.address.setFaxNumber(uform.getFaxNumber());

				if (Constants.PAGEOF_USER_ADMIN.equals(pageOf))
				{
					this.csmUserId = uform.getCsmUserId();
				}
			}
		}
		catch (Exception excp)
		{
			Logger.out.error(excp.getMessage());
		}
	}

	/**
	 * Returns message label to display on success add or edit
	 * @return String
	 */
	public String getMessageLabel()
	{
		return edu.wustl.clinportal.util.global.Utility.getLabel(this.lastName, this.firstName);
	}

	/**
	 * @return
	 */
	public String getLatestPassword()
	{
		String latestPassword = null;
		List pwdList = new ArrayList(this.getPasswordCollection());
		if (pwdList != null)
		{
			Collections.sort(pwdList);
			if (!pwdList.isEmpty())
			{
				Password pwd = ((Password) pwdList.get(0));
				latestPassword = pwd.getPassword();
			}
		}
		return latestPassword;
	}

	/**
	 * Returns true if a new user is added
	 * @hibernate.property name="firstTimeLogin" type="boolean" column="FIRST_TIME_LOGIN" 
	 * @return true if a new user is created
	 * @see #setFirstTimeLogin(Boolean)
	 */
	public Boolean getFirstTimeLogin()
	{
		return firstTimeLogin;
	}

	/**
	 * @param firstTimeLogin The firstTimeLogin to set.
	 */
	public void setFirstTimeLogin(Boolean firstTimeLogin)
	{
		this.firstTimeLogin = firstTimeLogin;
	}

	/**
	 * @return Returns the clinicalStudyCollection.
	 * @hibernate.set name="clinicalStudyCollection" table="CATISSUE_CLINICAL_STUDY_COORDINATORS" 
	 * cascade="save-update" inverse="true" lazy="true"
	 * @hibernate.collection-key column="USER_ID"
	 * @hibernate.collection-many-to-many class="edu.wustl.clinportal.domain.ClinicalStudy" column="CLINICAL_STUDY_ID"
	 */
	public Collection getClinicalStudyCollection()
	{
		return clinicalStudyCollection;
	}

	/**
	 * @param collectionProtocolCollection The collectionProtocolCollection to set.
	 */
	public void setClinicalStudyCollection(Collection clinicalStudyColn)
	{
		this.clinicalStudyCollection = clinicalStudyColn;
	}

	/**
	 * @return Returns the siteCollection.
	 * @hibernate.set name="siteCollection" table="CATISSUE_SITE_USERS" 
	 * cascade="none" inverse="true" lazy="true"
	 * @hibernate.collection-key column="USER_ID"
	 * @hibernate.collection-many-to-many class="edu.wustl.clinportal.domain.Site" column="SITE_ID"
	 */
	public Collection<Site> getSiteCollection()
	{
		return siteCollection;
	}

	/**
	 * @param siteCollection
	 */
	public void setSiteCollection(Collection<Site> siteCollection)
	{
		this.siteCollection = siteCollection;
	}

	/**
	 * @return
	 */
	public Boolean getAdminuser()
	{
		return adminuser;
	}

	/**
	 * @param adminuser
	 */
	public void setAdminuser(Boolean adminuser)
	{
		this.adminuser = adminuser;
	}

}