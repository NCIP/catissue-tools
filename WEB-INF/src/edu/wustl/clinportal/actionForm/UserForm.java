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
 * <p>Title: UserForm Class>
 * <p>Description:  UserForm Class is used to encapsulate all the request parameters passed 
 * from User Add/Edit webpage. </p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 * Created on Mar 3, 2005
 */

package edu.wustl.clinportal.actionForm;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import edu.wustl.clinportal.domain.Site;
import edu.wustl.clinportal.domain.User;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.cde.CDEManager;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.PasswordManager;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.security.exception.SMException;
import edu.wustl.security.manager.ISecurityManager;
import edu.wustl.security.manager.SecurityManagerFactory;

/**
 * UserForm Class is used to encapsulate all the request parameters passed 
 * from User Add/Edit web page.
 * @author Falguni_Sachde
 * */
public class UserForm extends AbstractActionForm
{

	private static final String invalidValueError = "errors.item.invalid.values";

	/**
	 * 
	 */
	private static final long serialVersionUID = 9121703882368803846L;

	/**
	 * Last Name of the user.
	 */
	private String lastName;

	/**
	 * First Name of the user.
	 */
	private String firstName;

	/**
	 * Institution name of the user.
	 */
	private long institutionId;

	/**
	 * EmailAddress Address of the user.
	 */
	private String emailAddress;

	/**
	 * Old Password of the user.
	 */
	private String oldPassword;

	/**
	 * New Password of the user.
	 */
	private String newPassword;

	/**
	 * Confirmed new password of the user.
	 */
	private String confirmNewPassword;

	/**
	 * Department name of the user.
	 */
	private long departmentId;

	/**
	 * Street Address of the user.
	 */
	private String street;

	/**
	 * The City where the user stays.
	 */
	private String city;

	/**
	 * The State where the user stays.
	 */
	private String state;

	/**
	 * The Country where the user stays.
	 */
	private String country;

	/**
	 * The zip code of city where the user stays.
	 * */
	private String zipCode;

	/**
	 * Phone number of the user.
	 * */
	private String phoneNumber;

	/**
	 * Fax number of the user.
	 * */
	private String faxNumber;

	/**
	 * Role of the user.
	 * */
	private String role;

	/**
	 * Cancer Research Group of the user.  
	 */
	private long cancerResearchGroupId;

	/**
	 * Comments given by user.
	 */
	private String comments;

	/**
	 * Status of user in the system.
	 */
	private String status;

	private Long csmUserId;

	private String adminuser = "false";

	/**
	 * COnfirm EmailAddress of the user.
	 */
	private String confirmEmailAddress;

	protected String[] siteIds = new String[]{"-1"};

	public String[] getSiteIds()
	{
		return this.siteIds;
	}

	public void setSiteIds(String[] siteIds)
	{
		this.siteIds = siteIds;
	}

	/**
	 * No argument constructor for UserForm class. 
	 */
	public UserForm()
	{
		reset();
	}

	/**
	 * @return Returns the confirmEmailAddress.
	 */
	public String getConfirmEmailAddress()
	{
		return confirmEmailAddress;
	}

	/**
	 * @param confEAddress The confirmEmailAddress to set.
	 */
	public void setConfirmEmailAddress(String confEAddress)
	{
		this.confirmEmailAddress = confEAddress;
	}

	/**
	 * Returns the last name of the user 
	 * @return String representing the last name of the user.
	 * @see #setFirstName(String)
	 */
	public String getLastName()
	{
		return this.lastName;
	}

	/**
	 * Sets the last name of the user.
	 * @param lastName Last Name of the user
	 * @see #getFirstName()
	 */
	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	/**
	 * Returns the first name of the user.
	 * @return String representing the first name of the user.
	 * @see #setFirstName(String)
	 */
	public String getFirstName()
	{
		return this.firstName;
	}

	/**
	 * Sets the first name of the user.
	 * @param firstName String representing the first name of the user.
	 * @see #getFirstName()
	 */
	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	/**
	 * Returns the institutionId name of the user.
	 * @return String representing the institutionId of the user. 
	 * @see #setinstitution(String)
	 */
	public long getInstitutionId()
	{
		return this.institutionId;
	}

	/**
	 * Sets the institutionId name of the user.
	 * @param institution String representing the institutionId of the user.
	 * @see #getinstitution()
	 */
	public void setInstitutionId(long institution)
	{
		this.institutionId = institution;
	}

	/**
	 * Returns the emailAddress Address of the user.
	 * @return String representing the emailAddress address of the user.
	 */
	public String getEmailAddress()
	{
		return this.emailAddress;
	}

	/**
	 * Sets the emailAddress address of the user.
	 * @param emailAddress String representing emailAddress address of the user
	 * @see #getEmailAddress()
	 */
	public void setEmailAddress(String emailAddress)
	{
		this.emailAddress = emailAddress;
	}

	/**
	 * @return Returns the confirmNewPassword.
	 */
	public String getConfirmNewPassword()
	{
		return confirmNewPassword;
	}

	/**
	 * @return Returns the newPassword.
	 */
	public String getNewPassword()
	{
		return newPassword;
	}

	/**
	 * @return Returns the oldPassword.
	 */
	public String getOldPassword()
	{
		return oldPassword;
	}

	/**
	 * @param confNewPassword The confirmNewPassword to set.
	 */
	public void setConfirmNewPassword(String confNewPassword)
	{
		this.confirmNewPassword = confNewPassword;
	}

	/**
	 * @param newPassword The newPassword to set.
	 */
	public void setNewPassword(String newPassword)
	{
		this.newPassword = newPassword;
	}

	/**
	 * @param oldPassword The oldPassword to set.
	 */
	public void setOldPassword(String oldPassword)
	{
		this.oldPassword = oldPassword;
	}

	/**
	 * Returns the Department Name of the user.
	 * @return String representing departmentId of the user.
	 * @see #getDepartmentId()
	 */
	public long getDepartmentId()
	{
		return this.departmentId;
	}

	/**
	 * Sets the Department Name of the user.
	 * @param department String representing departmentId of the user.
	 * @see #getDepartmentId()
	 */
	public void setDepartmentId(long department)
	{
		this.departmentId = department;
	}

	/**
	 * Returns the cancer research group the user belongs.
	 * @return Returns the cancerResearchGroupId.
	 * @see #setCancerResearchGroupId(String)
	 */
	public long getCancerResearchGroupId()
	{
		return cancerResearchGroupId;
	}

	/**
	 * Sets the cancer research group the user belongs.
	 * @param cResearchGroupId The cancerResearchGroupId to set.
	 * @see #getCancerResearchGroupId()
	 */
	public void setCancerResearchGroupId(long cResearchGroupId)
	{
		this.cancerResearchGroupId = cResearchGroupId;
	}

	/**
	 * Returns the Street Address of the user.
	 * @return String representing mailing address of the user.
	 * @see #setStreet(String)
	 */
	public String getStreet()
	{
		return this.street;
	}

	/**
	 * Sets the Street Address of the user.
	 * @param street String representing mailing address of the user.
	 * @see #getStreet()
	 */
	public void setStreet(String street)
	{
		this.street = street;
	}

	/**
	 * Returns the City where the user stays.
	 * @return String representing city of the user.
	 * @see #setCity(String)
	 */
	public String getCity()
	{
		return this.city;
	}

	/**
	 * Returns the fax number of the user.
	 * @return Returns the fax.
	 * @see #setFax(String)
	 */
	public String getFaxNumber()
	{
		return this.faxNumber;
	}

	/**
	 * Sets the City where the user stays.
	 * @param city String representing city of the user.
	 * @see #getCity()
	 */
	public void setCity(String city)
	{
		this.city = city;
	}

	/**
	 * Returns the State where the user stays.
	 * @return String representing state of the user.
	 * @see #setState(String)
	 */
	public String getState()
	{
		return this.state;
	}

	/**
	 * Sets the State where the user stays.
	 * @param state String representing state of the user.
	 * @see #getState()
	 */
	public void setState(String state)
	{
		this.state = state;
	}

	/**
	 * Sets the fax number of the user.
	 * @param faxNumber The fax to set.
	 * @see #getFax()
	 */
	public void setFaxNumber(String faxNumber)
	{
		this.faxNumber = faxNumber;
	}

	/**
	 * Returns the role of the user.
	 * @return the role of the user.
	 * @see #setRoleCollection(String)
	 */
	public String getRole()
	{
		return role;
	}

	/**
	 * Sets the role of the user.
	 * @param role the role of the user.
	 * @see #getRole()
	 */
	public void setRole(String role)
	{
		this.role = role;
	}

	/**
	 * @return Returns the comments.
	 */
	public String getComments()
	{
		return comments;
	}

	/**
	 * @param comments The comments to set.
	 */
	public void setComments(String comments)
	{
		this.comments = comments;
	}

	/**
	 * @return Returns the id assigned to form bean.
	 */
	public int getFormId()
	{
		int formId = Constants.APPROVE_USER_FORM_ID;
		if ((getPageOf() != null) && !(Constants.PAGEOF_APPROVE_USER.equals(getPageOf())))
		{
			formId = Constants.USER_FORM_ID;
		}
		Logger.out.debug("................formId...................." + formId);
		return formId;
	}

	/**
	 * @return Returns the status.
	 */
	public String getStatus()
	{
		return status;
	}

	/**
	 * @param status The status to set.
	 */
	public void setStatus(String status)
	{
		this.status = status;
	}

	/**
	 * @return Returns the csmUserId.
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
	 * Returns the Country where the user stays.
	 * @return String representing country of the user.
	 * @see #setCountry(String)
	 */
	public String getCountry()
	{
		return this.country;
	}

	/**
	 * Sets the Country where the user stays.
	 * @param country String representing country of the user.
	 * @see #getCountry()
	 */
	public void setCountry(String country)
	{
		this.country = country;
	}

	/**
	 * Returns the zip code of the user's city. 
	 * @return Returns the zip.
	 * @see #setZip(String)
	 */
	public String getZipCode()
	{
		return zipCode;
	}

	/**
	 * Sets the zip code of the user's city.
	 * @param zipCode The zip code to set.
	 * @see #getZip()
	 */
	public void setZipCode(String zipCode)
	{
		this.zipCode = zipCode;
	}

	/**
	 * Returns the phone number of the user.
	 * @return Returns the phone number.
	 * @see #setPhone(String)
	 */
	public String getPhoneNumber()
	{
		return phoneNumber;
	}

	/**
	 * Sets the phone number of the user. 
	 * @param phoneNumber The phone number to set.
	 * @see #getphoneNumber()
	 */
	public void setPhoneNumber(String phoneNumber)
	{
		this.phoneNumber = phoneNumber;
	}

	/**
	 * Resets the values of all the fields.
	 * Is called by the overridden reset method defined in ActionForm.  
	 * */
	protected void reset()
	{
		// reset
	}

	/**
	 * Copies the data from an AbstractDomain object to a UserForm object.
	 * @param abstractDomain An AbstractDomain object.  
	 */
	public void setAllValues(AbstractDomainObject abstractDomain)
	{
		String pageOf = getPageOf();

		if (!Constants.PAGEOF_CHANGE_PASSWORD.equals(pageOf))
		{
			User user = (User) abstractDomain;
			this.setId(user.getId().longValue());
			this.lastName = user.getLastName();
			this.firstName = user.getFirstName();
			if (user.getSiteCollection() != null && !user.getSiteCollection().isEmpty())
			{
				siteIds = new String[user.getSiteCollection().size()];
				int idx = 0;
				for (Site site : user.getSiteCollection())
				{
					siteIds[idx] = site.getId().toString();
					idx++;
				}
			}
			// Check for null entries (for admin)
			if (!edu.wustl.common.util.Utility.isNull(user.getInstitution()))
			{
				this.institutionId = user.getInstitution().getId().longValue();
			}

			this.emailAddress = user.getEmailAddress();
			this.adminuser = user.getAdminuser().toString();
			confirmEmailAddress = this.emailAddress;
			if (!edu.wustl.common.util.Utility.isNull(user.getDepartment()))
			{
				this.departmentId = user.getDepartment().getId().longValue();
			}

			if (!edu.wustl.common.util.Utility.isNull(user.getCancerResearchGroup()))
			{
				this.cancerResearchGroupId = user.getCancerResearchGroup().getId().longValue();
			}

			if (!edu.wustl.common.util.Utility.isNull(user.getAddress()))
			{
				this.street = user.getAddress().getStreet();
				this.city = user.getAddress().getCity();
				this.state = user.getAddress().getState();
				this.country = user.getAddress().getCountry();
				this.zipCode = user.getAddress().getZipCode();
				this.phoneNumber = user.getAddress().getPhoneNumber();
				this.faxNumber = user.getAddress().getFaxNumber();
			}

			//Populate the activity status, comments and role for approve user and user edit.  
			if ((getFormId() == Constants.APPROVE_USER_FORM_ID)
					|| ((pageOf != null) && (Constants.PAGEOF_USER_ADMIN.equals(pageOf))))
			{
				this.setActivityStatus(user.getActivityStatus());

				if (!edu.wustl.common.util.Utility.isNull(user.getComments()))
				{
					this.comments = user.getComments();
				}

				this.role = user.getRoleId();

				if (getFormId() == Constants.APPROVE_USER_FORM_ID)
				{
					this.status = user.getActivityStatus();
					if (getActivityStatus().equals(Constants.ACTIVITY_STATUS_ACTIVE))
					{
						this.status = Constants.APPROVE_USER_APPROVE_STATUS;
					}
					else if (getActivityStatus().equals(Constants.ACTIVITY_STATUS_CLOSED))
					{
						this.status = Constants.APPROVE_USER_REJECT_STATUS;
					}
					else if (getActivityStatus().equals(Constants.ACTIVITY_STATUS_NEW))
					{
						this.status = Constants.APPROVE_USER_PENDING_STATUS;
					}
				}
			}

			if (Constants.PAGEOF_USER_ADMIN.equals(pageOf))
			{
				this.setCsmUserId(user.getCsmUserId());
				try
				{
					if (this.csmUserId != null) //in case user not approved
					{
						PasswordManager.decode(user.getLatestPassword());
						ISecurityManager securityManager = SecurityManagerFactory
								.getSecurityManager();
						gov.nih.nci.security.authorization.domainobjects.User csmUser = securityManager
								.getUserById(this.getCsmUserId().toString());

						if (csmUser != null)
						{
							this.setNewPassword(csmUser.getPassword());
							this.setConfirmNewPassword(csmUser.getPassword());
						}
					}
					else
					{
						this.setNewPassword("");
						this.setConfirmNewPassword("");
					}
				}
				catch (SMException e)
				{
					Logger.out.debug("Error getting csm user");
				}

			}

			if (Constants.PAGEOF_USER_PROFILE.equals(pageOf))
			{
				this.role = user.getRoleId();
			}
		}
		Logger.out.debug("this.activityStatus............." + this.getActivityStatus());
		Logger.out.debug("this.comments" + this.comments);
		Logger.out.debug("this.role" + this.role);
		Logger.out.debug("this.status" + this.status);
		Logger.out.debug("this.csmUserid" + this.csmUserId);
	}

	/**
	 * Overrides the validate method of ActionForm.
	 * @return error ActionErrors instance
	 * @param mapping Action mapping instance
	 * @param request HttpServletRequest instance
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request)
	{
		ActionErrors errors = new ActionErrors();
		Validator validator = new Validator();
		String pageOf = getPageOf();
		String pageFrom = request.getParameter("pageFrom");
		try
		{
			if (getOperation() != null)
			{

				if (pageOf.equals(Constants.PAGEOF_CHANGE_PASSWORD))
				{
					if (validator.isEmpty(oldPassword))
					{
						errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
								"errors.item.required", ApplicationProperties
										.getValue("user.oldPassword")));
					}

					validatePassword(errors);
					if (!validator.isEmpty(newPassword) && !validator.isEmpty(oldPassword))
					{
						// Call static method PasswordManager.validatePasswordOnFormBean() where parameter are
						// new password,old password,user name

						int result = PasswordManager
								.validatePasswordOnFormBean(
										newPassword,
										oldPassword,
										(Boolean) request
												.getSession()
												.getAttribute(
														edu.wustl.common.util.global.Constants.PASSWORD_CHANGE_IN_SESSION));

						if (result != PasswordManager.SUCCESS)
						{
							// get error message of validation failure where param is result of validate() method
							String errorMessage = PasswordManager.getErrorMessage(result);
							Logger.out.debug("error from Password validate " + errorMessage);
							errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item",
									errorMessage));
						}
					}

				}
				else
				{
					setRedirectValue(validator);
					Logger.out.debug("user form ");

					if (getOperation().equals(Constants.ADD)
							|| getOperation().equals(Constants.EDIT))
					{

						if (validator.isEmpty(emailAddress))
						{
							errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
									invalidValueError, ApplicationProperties
											.getValue("user.emailAddress")));
						}
						else
						{
							if (!validator.isValidEmailAddress(emailAddress))
							{
								errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
										invalidValueError, ApplicationProperties
												.getValue("user.emailAddress")));
							}
						}

						if (!pageOf.equals(Constants.PAGEOF_USER_PROFILE))
						{
							if (validator.isEmpty(confirmEmailAddress))
							{
								errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
										invalidValueError, ApplicationProperties
												.getValue("user.confirmemailAddress")));
							}
							else
							{
								if (!validator.isValidEmailAddress(confirmEmailAddress))
								{
									errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
											invalidValueError, ApplicationProperties
													.getValue("user.confirmemailAddress")));
								}
							}
							if (!confirmEmailAddress.equals(emailAddress))
							{
								errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
										"errors.email.mismatch"));
							}

						}

						if (validator.isEmpty(lastName))
						{
							errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
									invalidValueError, ApplicationProperties
											.getValue("user.lastName")));
						}

						if (validator.isEmpty(firstName))
						{
							errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
									invalidValueError, ApplicationProperties
											.getValue("user.firstName")));
						}

						if (validator.isEmpty(city))
						{
							errors
									.add(ActionErrors.GLOBAL_ERROR, new ActionError(
											invalidValueError, ApplicationProperties
													.getValue("user.city")));
						}

						if (!validator.isValidOption(state))
						{
							errors.add(ActionErrors.GLOBAL_ERROR,
									new ActionError(invalidValueError, ApplicationProperties
											.getValue("user.state")));
						}

						if (validator.isEmpty(zipCode))
						{
							errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
									invalidValueError, ApplicationProperties
											.getValue("user.zipCode")));
						}
						else
						{
							if (!validator.isValidZipCode(zipCode))
							{
								errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
										"errors.zipCode.format", ApplicationProperties
												.getValue("user.zipCode")));
							}
						}
						if (!validator.isValidOption(country))
						{
							errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
									invalidValueError, ApplicationProperties
											.getValue("user.country")));
						}

						if (validator.isEmpty(phoneNumber))
						{
							errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
									invalidValueError, ApplicationProperties
											.getValue("user.phoneNumber")));
						}

						if (!validator.isValidOption(getActivityStatus()))
						{
							errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
									invalidValueError, ApplicationProperties
											.getValue("user.activityStatus")));
						}
						if (institutionId == 0)
						{
							errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
									invalidValueError, ApplicationProperties
											.getValue("user.institution")));
						}
						if (departmentId == 0)
						{
							errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
									invalidValueError, ApplicationProperties
											.getValue("user.department")));
						}
						if (cancerResearchGroupId == 0)
						{
							errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
									invalidValueError, ApplicationProperties
											.getValue("user.cancerResearchGroup")));
						}
						if (!(pageOf.equals(Constants.PAGEOF_SIGNUP) || pageOf
								.equals(Constants.PAGEOF_APPROVE_USER))
								&& !"ApproveUser".equals(pageFrom)
								&& (role.equalsIgnoreCase("undefined") || validator.isEmpty(role)))
						{
							errors
									.add(ActionErrors.GLOBAL_ERROR, new ActionError(
											invalidValueError, ApplicationProperties
													.getValue("user.role")));
						}
						if (validator.isValidOption(state)
								&& !Validator.isEnumeratedValue(CDEManager.getCDEManager()
										.getPermissibleValueList(Constants.CDE_NAME_STATE_LIST,
												null), state))
						{
							errors.add(ActionErrors.GLOBAL_ERROR,
									new ActionError(invalidValueError, ApplicationProperties
											.getValue("user.state")));
						}

						if (validator.isValidOption(country)
								&& !Validator.isEnumeratedValue(CDEManager.getCDEManager()
										.getPermissibleValueList(Constants.CDE_NAME_COUNTRY_LIST,
												null), country))
						{
							errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
									invalidValueError, ApplicationProperties
											.getValue("user.country")));
						}
					}

					if (pageOf.equals(Constants.PAGEOF_APPROVE_USER))
					{
						if (!validator.isValidOption(status))
						{
							errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
									invalidValueError, ApplicationProperties
											.getValue("user.approveOperation")));
						}
					}

					if (pageOf.equals(Constants.PAGEOF_USER_ADMIN)
							|| (pageOf.equals(Constants.PAGEOF_APPROVE_USER) && !status
									.equalsIgnoreCase("REJECT")))
					{
						if (role != null && !role.equals("") && role.equals("-1"))
						{

							if (!validator.isValidOption(role))
							{
								errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
										invalidValueError, ApplicationProperties
												.getValue("user.role")));
							}
						}
					}

					//Bug- 1516:  
					if (pageOf.equals(Constants.PAGEOF_USER_ADMIN)
							&& getOperation().equals(Constants.EDIT))
					{

						if (!"ApproveUser".equals(pageFrom))
						{
							validatePassword(errors);
							int result=validateForSameSesssion(request);
							if(result==0)
							result = PasswordManager.validatePasswordOnFormBean(
											newPassword,oldPassword,null);

							if (result != PasswordManager.SUCCESS)
							{
								// get error message of validation failure where parameter is result of validate() method
								String errorMessage = PasswordManager.getErrorMessage(result);
								Logger.out.debug("error from Password validate " + errorMessage);
								errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
										"errors.item", errorMessage));
							}
						}
					}

				}
			}
		}
		catch (Exception excp)
		{
			Logger.out.error(excp.getMessage(), excp);
		}
		return errors;
	}

	/**
	 * 
	 * @param request
	 * @return
	 * @throws SMException 
	 */
	private int validateForSameSesssion(HttpServletRequest request) throws SMException
    {
	    if((Boolean) request.getSession().getAttribute(edu.wustl.common.util.global.Constants.PASSWORD_CHANGE_IN_SESSION))
	    {
	        ISecurityManager securityManager = SecurityManagerFactory.getSecurityManager();
	        gov.nih.nci.security.authorization.domainobjects.User csmUser = securityManager
            .getUserById(this.getCsmUserId().toString());
	        if (csmUser != null)
	        {
	            if(!newPassword.equals(csmUser.getPassword()))
	                return PasswordManager.FAIL_SAME_SESSION;
	        }
	    }
        return 0;
    }

    /**
	 * 
	 * @param errors
	 */
	private void validatePassword(ActionErrors errors)
	{
		Validator validator = new Validator();
		if (validator.isEmpty(newPassword))
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.required",
					ApplicationProperties.getValue("user.newPassword")));
		}

		if (validator.isEmpty(confirmNewPassword))
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.required",
					ApplicationProperties.getValue("user.confirmNewPassword")));
		}

		if (!validator.isEmpty(newPassword) && !validator.isEmpty(confirmNewPassword))
		{
			if (!newPassword.equals(confirmNewPassword))
			{
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
						"errors.confirmNewPassword.reType"));
			}
		}
	}

	/**
	 * This method sets Identifier of Objects inserted by AddNew activity in Form-Bean which initialized AddNew action
	 * @param addNewFor - FormBean ID of the object inserted
	 *  @param addObjIdentifier - Identifier of the Object inserted 
	 */
	public void setAddNewObjectIdentifier(String addNewFor, Long addObjIdentifier)
	{
		if ("institution".equals(addNewFor))
		{
			setInstitutionId(addObjIdentifier.longValue());
		}
		else if ("department".equals(addNewFor))
		{
			setDepartmentId(addObjIdentifier.longValue());
		}
		else if ("cancerResearchGroup".equals(addNewFor))
		{
			setCancerResearchGroupId(addObjIdentifier.longValue());
		}
	}

	/**
	 * @return
	 */
	public String getAdminuser()
	{
		return adminuser;
	}

	/**
	 * @param adminuser
	 */
	public void setAdminuser(String adminuser)
	{
		this.adminuser = adminuser;
	}

}