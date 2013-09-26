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
 * <p>Title: ParticipantForm Class>
 * <p>Description:  ParticipantForm Class is used to encapsulate all the request parameters passed 
 * from Participant Add/Edit webpage.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * 
 */

package edu.wustl.clinportal.actionForm;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import edu.wustl.clinportal.bean.ConsentBean;
import edu.wustl.clinportal.bean.ConsentResponseBean;
import edu.wustl.clinportal.domain.ClinicalStudyConsentTier;
import edu.wustl.clinportal.domain.ClinicalStudyConsentTierResponse;
import edu.wustl.clinportal.domain.ClinicalStudyRegistration;
import edu.wustl.clinportal.domain.Participant;
import edu.wustl.clinportal.domain.ParticipantMedicalIdentifier;
import edu.wustl.clinportal.domain.Race;
import edu.wustl.clinportal.domain.Site;
import edu.wustl.clinportal.domain.User;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.clinportal.util.global.DefaultValueManager;
import edu.wustl.clinportal.util.global.Utility;
import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.logger.Logger;

/**
 * ParticipantForm Class is used to encapsulate all the request parameters passed 
 * from Participant Add/Edit web page.
 * @author deepali_ahirrao
 * @version
 */

public class ParticipantForm extends AbstractActionForm implements Serializable
{
	private static final String invalidValueError = "errors.item.invalid.values";
	
	private static final long serialVersionUID = 1234567890L;

	/**
	 * Last Name of the Participant.
	 */
	protected String lastName = "";

	/**
	 * First Name of the Participant.
	 */
	protected String firstName = "";

	/**
	 * Middle Name of the Participant.
	 */
	protected String middleName = "";

	/**
	 * The gender of a participant.
	 */
	protected String gender = (String) DefaultValueManager
			.getDefaultValue(Constants.DEFAULT_GENDER);

	/**
	 * The geno type of a participant.
	 */
	protected String genotype = (String) DefaultValueManager
			.getDefaultValue(Constants.DEFAULT_GENOTYPE);

	/**
	 * Social Security Number of the Participant.
	 */
	protected String socialSecurityNumberPartA = "";
	/**
	 * Social Security Number of the Participant.
	 */
	protected String socialSecurityNumberPartB = "";
	/**
	 * Social Security Number of the Participant.
	 */
	protected String socialSecurityNumberPartC = "";

	/**
	 * The Date of Birth of the Participant.
	 */
	protected String birthDate = "";
	
	
	/**
	 * Defines the vital status of the participant like 'Dead', 'Alive' or 'Unknown'.
	 */
	protected String registrationDate = "";
	
	/**
	 * Defines the vital status of the participant like 'Dead', 'Alive' or 'Unknown'.
	 */
	protected String clinicalStudyId;
	
	/**
	 * Street Address of the site.
	 */
	private String street;

	/**
	 * The City in which the site is.
	 */
	private String city;

	private String state ;

	private String country ;

	/**
	 * The zip code of city where the site is.
	 */
	private String zipCode;
	
	/**
	 * The Health Insurance of the participant.
	 */
	protected String healthInsurance = "Yes";
	/**
	 * Patient referred by
	 */
	protected String refBy;

	/**
	 * The Emergency contact No of the participant.
	 */
	protected String emgContactNo;
	
protected String emailAddress;
	
	public String getEmailAddress()
	{
		return emailAddress;
	}


	
	public void setEmailAddress(String emailAddress)
	{
		this.emailAddress = emailAddress;
	}


	
	public String getBloodGroup()
	{
		return bloodGroup;
	}


	
	public void setBloodGroup(String bloodGroup)
	{
		this.bloodGroup = bloodGroup;
	}


	/**
	 * bloodGroup name of the participant.
	 */
	protected String bloodGroup;
	
	public String getHealthInsurance()
	{
		return healthInsurance;
	}

	
	public void setHealthInsurance(String healthInsurance)
	{
		this.healthInsurance = healthInsurance;
	}

	
	public String getRefBy()
	{
		return refBy;
	}

	
	public void setRefBy(String refBy)
	{
		this.refBy = refBy;
	}

	
	public String getEmgContactNo()
	{
		return emgContactNo;
	}

	
	public void setEmgContactNo(String emgContactNo)
	{
		this.emgContactNo = emgContactNo;
	}

	/**
	 * Phone number of the site.
	 * */
	private String phoneNumber;
	
	protected String familyName;
	protected String businessField;	
	
	public String getFamilyName()
	{
		return familyName;
	}

	
	public void setFamilyName(String familyName)
	{
		this.familyName = familyName;
	}

	
	
	public String getBusinessField()
	{
		return businessField;
	}

	
	public void setBusinessField(String businessField)
	{
		this.businessField = businessField;
	}


	public String getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(String registrationDate) {
		this.registrationDate = registrationDate;
	}

	public String getClinicalStudyId() {
		return clinicalStudyId;
	}

	public void setClinicalStudyId(String clinicalStudyId) {
		this.clinicalStudyId = clinicalStudyId;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getFaxNumber() {
		return faxNumber;
	}

	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}

	/**
	 * Fax number of the site.
	 */
	private String faxNumber;

	/**
	 * The race to which the Participant belongs.
	 */
	protected String[] raceTypes = {(String) DefaultValueManager
			.getDefaultValue(Constants.DEFAULT_RACE)};

	/**
	 * Participant's ethnicity status.
	 */
	protected String ethnicity = (String) DefaultValueManager
			.getDefaultValue(Constants.DEFAULT_ETHNICITY);

	/**
	 * The Date of Death of the Participant.
	 */
	protected String deathDate = "";

	/**
	 * Vital status of the Participant.
	 */
	protected String vitalStatus = (String) DefaultValueManager
			.getDefaultValue(Constants.DEFAULT_VITAL_STATUS);

	/**
	 * Map to handle values of all the Participant Medical Identifiers
	 */
	protected Map values = new LinkedHashMap();

	/**
	 * Map to handle values of registration of a Participant to a Collection Protocol. 
	 */
	protected Map collectionProtocolRegistrationValues = new LinkedHashMap();

	/**
	 * Consent Response Collection for given collection protocols 
	 */
	protected Collection<ConsentResponseBean> consentResponseBeanCollection;

	/**
	 * Consent Response hashtable entered by the user.
	 */
	protected Hashtable consentResponseHashTable;

	/**
	 * Counter that contains number of rows in the 'Add More' functionality for medical identifier.
	 */
	private int valueCounter;

	/**
	 * Counter that contains number of rows in the 'Add More' functionality for registration of a Participant to a Collection Protocol.
	 */
	private int collectionProtocolRegistrationValueCounter;

	/**
	 * 
	 */
	private long cpId = -1;

	/**
	 * Initializes an empty ParticipantForm object. 
	 */
	public ParticipantForm()
	{

	}

	/**
	 * @param ssnString Setting SSN number
	 */
	private void setSSN(String ssnString)
	{
		if (ssnString != null && !ssnString.equals(""))
		{
			try
			{
				StringTokenizer tok = new StringTokenizer(ssnString, "-");
				socialSecurityNumberPartA = tok.nextToken();
				socialSecurityNumberPartB = tok.nextToken();
				socialSecurityNumberPartC = tok.nextToken();
			}
			catch (Exception ex)
			{
				Logger.out.debug(ex.getMessage(), ex);
				socialSecurityNumberPartA = "";
				socialSecurityNumberPartB = "";
				socialSecurityNumberPartC = "";
			}
		}
	}

	/**
	 * Copies the data from an AbstractDomain object to a ParticipantForm object.
	 * @param abstractDomain An AbstractDomain object.  
	 */
	public void setAllValues(AbstractDomainObject abstractDomain)
	{
		Participant participant = (Participant) abstractDomain;
		this.setId(participant.getId().longValue());
		this.lastName = edu.wustl.common.util.Utility.toString(participant.getLastName());
		this.firstName = edu.wustl.common.util.Utility.toString(participant.getFirstName());
		this.middleName = edu.wustl.common.util.Utility.toString(participant.getMiddleName());
		this.familyName = edu.wustl.common.util.Utility.toString(participant.getFamilyName());
		this.businessField = edu.wustl.common.util.Utility.toString(participant.getBusinessField());
		
		this.birthDate = edu.wustl.common.util.Utility.parseDateToString(
				participant.getBirthDate(), Constants.DATE_PATTERN_DD_MM_YYYY);
		this.gender = participant.getGender();
		this.genotype = participant.getSexGenotype();
		setSSN(participant.getSocialSecurityNumber());
		this.street = participant.getAddress().getStreet();
		this.city = participant.getAddress().getCity();
		this.state = participant.getAddress().getState();
		this.country = participant.getAddress().getCountry();
		this.zipCode = participant.getAddress().getZipCode();
		this.phoneNumber = participant.getAddress().getPhoneNumber();
		this.faxNumber = participant.getAddress().getFaxNumber();
		this.healthInsurance = participant.getHealthInsurance();
		this.emgContactNo=participant.getEmgContactNo();
		this.refBy=participant.getRefBy();
		Collection raceCollection = participant.getRaceCollection();
		if (raceCollection != null)
		{
			this.raceTypes = new String[raceCollection.size()];
			int idx = 0;

			Iterator iterator = raceCollection.iterator();
			while (iterator.hasNext())
			{
				Race race = (Race) iterator.next();
				if (race != null)
				{

					this.raceTypes[idx] = race.getRaceName();
					idx++;
				}
			}
		}

		this.setActivityStatus(participant.getActivityStatus());
		this.ethnicity = participant.getEthnicity();
		this.deathDate = edu.wustl.common.util.Utility.parseDateToString(
				participant.getDeathDate(), Constants.DATE_PATTERN_DD_MM_YYYY);
		this.vitalStatus = participant.getVitalStatus();

		//Populating the map with the participant medical identifiers data 
		Collection medicalIdColn = participant.getParticipantMedicalIdentifierCollection();

		if (medicalIdColn != null)
		{
			values = new LinkedHashMap();
			int idx = 1;

			Iterator iterator = medicalIdColn.iterator();
			while (iterator.hasNext())
			{
				ParticipantMedicalIdentifier ppantMedicalId = (ParticipantMedicalIdentifier) iterator
						.next();

				String key1 = Utility.getParticipantMedicalIdentifierKeyFor(idx,
						Constants.PARTICIPANT_MEDICAL_IDENTIFIER_SITE_ID);
				String key2 = Utility.getParticipantMedicalIdentifierKeyFor(idx,
						Constants.PARTICIPANT_MEDICAL_IDENTIFIER_MEDICAL_NUMBER);
				String key3 = Utility.getParticipantMedicalIdentifierKeyFor(idx,
						Constants.PARTICIPANT_MEDICAL_IDENTIFIER_ID);
				Site site = ppantMedicalId.getSite();
				if (site == null)
				{
					values.put(key1, edu.wustl.common.util.Utility
							.toString(Constants.SELECT_OPTION));
				}
				else
				{
					values.put(key1, edu.wustl.common.util.Utility.toString(site.getId()));
				}

				values.put(key2, edu.wustl.common.util.Utility.toString(ppantMedicalId
						.getMedicalRecordNumber()));
				values.put(key3, edu.wustl.common.util.Utility.toString(ppantMedicalId.getId()));

				idx++;
			}
			valueCounter = medicalIdColn.size();
		}

		/*
		 * Populating the map with the registrations of a Participant to a ClinicalStudy.
		 * 
		 */
		Collection clStudyRegnColn = participant.getClinicalStudyRegistrationCollection();

		if (clStudyRegnColn != null)
		{
			Iterator iterator = clStudyRegnColn.iterator();
			while (iterator.hasNext())
			{
				ClinicalStudyRegistration clStudyRegn = (ClinicalStudyRegistration) iterator.next();
				if (clStudyRegn.getActivityStatus() != null
						&& !clStudyRegn.getActivityStatus().equalsIgnoreCase(Constants.DISABLED))
				{
					
					this.clinicalStudyId=clStudyRegn.getClinicalStudyParticipantIdentifier();
					this.registrationDate=edu.wustl.common.util.Utility.parseDateToString(
							clStudyRegn.getRegistrationDate(), Constants.DATE_PATTERN_DD_MM_YYYY);
					this.cpId=clStudyRegn.getClinicalStudy().getId();
				}
			
			}
			this.setBloodGroup(edu.wustl.common.util.Utility.toString(participant.getBloodGroup()));
			this.setEmailAddress(edu.wustl.common.util.Utility.toString(participant.getEmailAddress()));
			
		
			/*collectionProtocolRegistrationValues = new LinkedHashMap();
			if (consentResponseHashTable == null)
			{
				consentResponseHashTable = new Hashtable();
			}
			consentResponseBeanCollection = new LinkedHashSet<ConsentResponseBean>();
			int idx = 1;

			String csReg = "ClinicalStudyRegistration:";

			Iterator iterator = clStudyRegnColn.iterator();
			while (iterator.hasNext())
			{
				ClinicalStudyRegistration clStudyRegn = (ClinicalStudyRegistration) iterator.next();
				if (clStudyRegn.getActivityStatus() != null
						&& !clStudyRegn.getActivityStatus().equalsIgnoreCase(Constants.DISABLED))
				{
					String colProtocolId = csReg + idx + "_ClinicalStudy_id";
					String colnProtocolTitle = csReg + idx + "_ClinicalStudy_shortTitle";
					String colProtoPpantId = csReg + idx + "_clinicalStudyParticipantIdentifier";
					String colProtoRegnDate = csReg + idx + "_registrationDate";
					String colnProtocolId = csReg + idx + "_id";
					String isConsentAvl = csReg + idx + "_isConsentAvailable";
					String isActive = csReg + idx + "_activityStatus";

					Collection csentTierColn = clStudyRegn.getClinicalStudy()
							.getConsentTierCollection();
					if (csentTierColn != null && csentTierColn.isEmpty())
					{
						collectionProtocolRegistrationValues.put(isConsentAvl,
								Constants.NO_CONSENTS_DEFINED);
					}
					else if (csentTierColn != null && !csentTierColn.isEmpty())
					{
						collectionProtocolRegistrationValues.put(isConsentAvl,
								Constants.PARTICIPANT_CONSENT_ENTER_RESPONSE);
					}

					String date = edu.wustl.common.util.Utility.parseDateToString(clStudyRegn
							.getRegistrationDate(), Constants.DATE_PATTERN_MM_DD_YYYY);

					collectionProtocolRegistrationValues.put(colProtocolId,
							edu.wustl.common.util.Utility.toString(clStudyRegn.getClinicalStudy()
									.getId()));
					collectionProtocolRegistrationValues.put(colnProtocolTitle,
							edu.wustl.common.util.Utility.toString(clStudyRegn.getClinicalStudy()
									.getShortTitle()));
					collectionProtocolRegistrationValues.put(colProtoPpantId,
							edu.wustl.common.util.Utility.toString(clStudyRegn
									.getClinicalStudyParticipantIdentifier()));
					collectionProtocolRegistrationValues.put(colProtoRegnDate, date);
					collectionProtocolRegistrationValues.put(colnProtocolId,
							edu.wustl.common.util.Utility.toString(clStudyRegn.getId()));
					collectionProtocolRegistrationValues
							.put(isActive, edu.wustl.common.util.Utility.toString(clStudyRegn
									.getActivityStatus()));

					getConsentResponse(clStudyRegn);

					idx++;
				}
			}
			collectionProtocolRegistrationValueCounter = (idx - 1);*/
		}

		/*if (valueCounter == 0)
		{
			valueCounter = 1;
		}*/
	}

	/**
	 * @param clstudyRegn
	 */
	private void getConsentResponse(ClinicalStudyRegistration clstudyRegn)
	{
		try
		{
			long clinicalStudyID = clstudyRegn.getClinicalStudy().getId();
			String signedConsentURL = clstudyRegn.getSignedConsentDocumentURL();
			User consentWitness = clstudyRegn.getConsentWitness();
			long witnessId = -1;
			if (consentWitness != null)
			{
				witnessId = consentWitness.getId();
			}

			String csentSigDate = edu.wustl.common.util.Utility.parseDateToString(clstudyRegn
					.getConsentSignatureDate(), Constants.DATE_PATTERN_MM_DD_YYYY);
			Collection csentRespColn = clstudyRegn.getConsentTierResponseCollection();
			Collection consentResponse;

			if (csentRespColn == null || csentRespColn.isEmpty())
			{
				csentRespColn = clstudyRegn.getClinicalStudy().getConsentTierCollection();
				consentResponse = getConsentResponseCollection(csentRespColn, false);
			}
			else
			{
				consentResponse = getConsentResponseCollection(csentRespColn, true);
			}

			ConsentResponseBean csentRespBean = new ConsentResponseBean(clinicalStudyID,
					signedConsentURL, witnessId, csentSigDate, consentResponse,
					Constants.WITHDRAW_RESPONSE_NOACTION);

			String csentRespKey = Constants.CONSENT_RESPONSE_KEY + clinicalStudyID;
			if (consentResponseHashTable.containsKey(csentRespKey))
			{
				throw new BizLogicException(ErrorKey.getErrorKey("error.particiapnt.duplicate"),
						null, ApplicationProperties
								.getValue("errors.participant.duplicate.clinicalStudy"));
			}
			consentResponseHashTable.put(csentRespKey, csentRespBean);
			consentResponseBeanCollection.add(csentRespBean);
		}
		catch (Exception e)
		{
			Logger.out.debug(e.getMessage(), e);
		}
	}

	/*
	 * Returns the consentBean collection from consent tier response for given protocol collection id
	 * @param consentResponse
	 * @param isResponseExist
	 * @return
	 */
	private Collection getConsentResponseCollection(Collection consentResponse,
			boolean isResponseExist)
	{
		Collection<ConsentBean> csentBeanColn = new HashSet<ConsentBean>();
		if (consentResponse != null)
		{
			Iterator csentResponseItr = consentResponse.iterator();
			while (csentResponseItr.hasNext())
			{
				ConsentBean consentBean = new ConsentBean();
				if (isResponseExist)
				{
					ClinicalStudyConsentTierResponse csentTierResp = (ClinicalStudyConsentTierResponse) csentResponseItr
							.next();
					ClinicalStudyConsentTier consentTier = csentTierResp.getConsentTier();
					consentBean.setConsentTierID(edu.wustl.common.util.Utility.toString(consentTier
							.getId()));
					consentBean.setStatement(consentTier.getStatement());
					consentBean.setParticipantResponse(csentTierResp.getResponse());
					consentBean.setParticipantResponseID(edu.wustl.common.util.Utility
							.toString(csentTierResp.getId()));
				}
				else
				{
					ClinicalStudyConsentTier consentTier = (ClinicalStudyConsentTier) csentResponseItr
							.next();
					consentBean.setConsentTierID(edu.wustl.common.util.Utility.toString(consentTier
							.getId()));
					consentBean.setStatement(consentTier.getStatement());
					consentBean.setParticipantResponse("");
					consentBean.setParticipantResponseID("");
				}
				csentBeanColn.add(consentBean);
			}
		}
		return csentBeanColn;
	}

	/**
	  * Returns the last name of the Participant. 
	  * @return String the last name of the Participant.
	  * @see #setFirstName(String)
	  */
	public String getLastName()
	{
		return lastName;
	}

	/**
	 * Sets the last name of the Participant.
	 * @param lastName Last Name of the Participant.
	 * @see #getFirstName()
	 */
	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	/**
	 * Returns the first name of the Participant.
	 * @return String the first name of the Participant.
	 * @see #setFirstName(String)
	 */
	public String getFirstName()
	{
		return firstName;
	}

	/**
	 * Sets the first name of the Participant.
	 * @param firstName String representing the first name of the Participant.
	 * @see #getFirstName()
	 */
	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	/**
	 * Returns the middle name of the Participant.
	 * @return String the middle name of the Participant.
	 * @see #setMiddleName(String)
	 */
	public String getMiddleName()
	{
		return middleName;
	}

	/**
	 * Sets the middle name of the Participant.
	 * @param middleName String the middle name of the Participant.
	 * @see #getMiddleName()
	 */
	public void setMiddleName(String middleName)
	{
		this.middleName = middleName;
	}

	/**
	 * Returns the date of birth of the Participant.
	 * @return String the date of birth of the Participant.
	 * @see #setBirthDate(String)
	 */
	public String getBirthDate()
	{
		return birthDate;
	}

	/**
	 * Sets the date of birth of the Participant.
	 * @param dateOfBirth String the date of birth of the Participant.
	 * @see #getBirthDate()
	 */
	public void setBirthDate(String dateOfBirth)
	{
		this.birthDate = dateOfBirth;
	}

	/**
	 * Returns the genotype of the Participant.
	 * @return String the genotype of the Participant.
	 * @see #setGenotype(String)
	 */
	public String getGenotype()
	{
		return genotype;
	}

	/**
	 * Sets the genotype of the Participant.
	 * @param genotype String the genotype of the Participant.
	 * @see #getGenotype()
	 */
	public void setGenotype(String genotype)
	{
		this.genotype = genotype;
	}

	/**
	 * Returns the gender of the Participant.
	 * @return String the gender of the Participant.
	 * @see #setGender(String)
	 */
	public String getGender()
	{
		return gender;
	}

	/**
	 * Sets the gender of the Participant.
	 * @param gender String the gender of the Participant.
	 * @see #getGender()
	 */
	public void setGender(String gender)
	{
		this.gender = gender;
	}

	/**
	 * Returns the race of the Participant.
	 * @return String the race of the Participant.
	 * @see #setRace(String)
	 */
	public String[] getRaceTypes()
	{
		return raceTypes;
	}

	/**
	 * Sets the race of the Participant.
	 * @param raceTypes String the race of the Participant.
	 * @see #getRace()
	 */
	public void setRaceTypes(String[] raceTypes)
	{
		this.raceTypes = raceTypes;
	}

	/**
	 * Returns the ethnicity of the Participant.
	 * @return Ethnicity of the Participant.
	 * @see #setEthnicity(String)
	 */
	public String getEthnicity()
	{
		return ethnicity;
	}

	/**
	 * Sets the ethnicity of the Participant.
	 * @param ethnicity Ethnicity of the Participant.
	 * @see #getEthnicity()
	 */
	public void setEthnicity(String ethnicity)
	{
		this.ethnicity = ethnicity;
	}

	/**
	 * Returns the id assigned to form bean.
	 * @return the id assigned to form bean.
	 */
	public int getFormId()
	{
		return Constants.PARTICIPANT_FORM_ID;
	}

	/**
	 * Resets the values of all the fields.
	 */
	protected void reset()
	{
		// reset
	}

	/**
	 * Overrides the validate method of ActionForm.
	 * @param mapping Action mapping instance
	 * @param request HttpServletRequest instance
	 * @return error ActionErrors instance
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request)
	{
		ActionErrors errors = new ActionErrors();
		Validator validator = new Validator();
		// To get the consent response from session.
		HttpSession session = request.getSession();

		try
		{
			setRedirectValue(validator);

			String errKeyForBDate = "";
			String errKeyForDthDate = "";
			
			if (validator.isEmpty(lastName))
			{
				// date validation according to bug id  722 and 730
				errKeyForBDate = validator.validateDate(lastName, true);
				if (errKeyForBDate.trim().length() > 0)
				{
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(errKeyForBDate,
							ApplicationProperties.getValue("user.lastName")));
				}
			}
			if (validator.isEmpty(firstName))
			{
				// date validation according to bug id  722 and 730
				errKeyForBDate = validator.validateDate(firstName, true);
				if (errKeyForBDate.trim().length() > 0)
				{
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(errKeyForBDate,
							ApplicationProperties.getValue("user.firstName")));
				}
			}

			if (!validator.isEmpty(birthDate))
			{
				// date validation according to bug id  722 and 730
				errKeyForBDate = validator.validateDate(birthDate, true);
				if (errKeyForBDate.trim().length() > 0)
				{
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(errKeyForBDate,
							ApplicationProperties.getValue("participant.birthDate")));
				}
			}

			if (!validator.isEmpty(deathDate))
			{
				errKeyForDthDate = validator.validateDate(deathDate, true);
				if (errKeyForDthDate.trim().length() > 0)
				{
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(errKeyForDthDate,
							ApplicationProperties.getValue("participant.deathDate")));
				}
			}

			if ((!validator.isEmpty(birthDate) && !validator.isEmpty(deathDate))
					&& (errKeyForDthDate.trim().length() == 0 && errKeyForBDate.trim().length() == 0))
			{
				boolean errorKey1 = validator.compareDates(birthDate, deathDate);

				if (!errorKey1)
				{
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
							"participant.invaliddate", ApplicationProperties
									.getValue("participant.invaliddate")));
				}
			}

			String socSecurityNum = socialSecurityNumberPartA + "-" + socialSecurityNumberPartB
					+ "-" + socialSecurityNumberPartC;
			if (!validator.isEmpty(socialSecurityNumberPartA + socialSecurityNumberPartB
					+ socialSecurityNumberPartC)
					&& !validator.isValidSSN(socSecurityNum))
			{
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.invalid",
						ApplicationProperties.getValue("participant.socialSecurityNumber")));
			}

			//Validation for Blank Participant 
			if (validator.isEmpty(lastName)
					&& validator.isEmpty(firstName)
					&& validator.isEmpty(middleName)
					&& validator.isEmpty(birthDate)
					&& (validator.isEmpty(deathDate))
					&& !validator.isValidOption(gender)
					&& !validator.isValidOption(vitalStatus)
					&& !validator.isValidOption(genotype)
					&& "-1".equals(ethnicity)
					&& validator.isEmpty(socialSecurityNumberPartA + socialSecurityNumberPartB
							+ socialSecurityNumberPartC))
			{
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
						"errors.participant.atLeastOneFieldRequired"));
			}

			//Validations for Add-More Block
			String className = "ParticipantMedicalIdentifier:";
			String key1 = "_Site_" + Constants.SYSTEM_IDENTIFIER;
			String key2 = "_medicalRecordNumber";
			String key3 = "_" + Constants.SYSTEM_IDENTIFIER;
			int index = 1;

			while (true)
			{
				String keyOne = className + index + key1;
				String keyTwo = className + index + key2;

				String value1 = (String) values.get(keyOne);
				String value2 = (String) values.get(keyTwo);

				if (value1 == null || value2 == null)
				{
					break;
				}
				else if (!validator.isValidOption(value1) && value2.trim().equals(""))
				{
					String keyThree = className + index + key3;
					values.remove(keyOne);
					values.remove(keyTwo);
					values.remove(keyThree);
				}
				else if ((validator.isValidOption(value1) && value2.trim().equals(""))
						|| (!validator.isValidOption(value1) && !value2.trim().equals("")))
				{
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError( // NOPMD - ActionError instantiation in loop
							"errors.participant.missing", ApplicationProperties
									.getValue("participant.msg")));
					break;
				}
				index++;
			}

			
			
			//validation for add more block for collection protocol registration

			String colProClassName = "ClinicalStudyRegistration:";
			index = 1;
			int count = 0;
			while (true)
			{
				String keyOne = colProClassName + index + "_ClinicalStudy_id";
				String keyThree = colProClassName + index + "_registrationDate";
				String keySix = colProClassName + index + "_activityStatus";

				String value1 = (String) collectionProtocolRegistrationValues.get(keyOne);
				String value3 = (String) collectionProtocolRegistrationValues.get(keyThree);
				String value6 = (String) collectionProtocolRegistrationValues.get(keySix);

				if (value6 == null)
				{
					value6 = Constants.ACTIVITY_STATUS_ACTIVE;
				}
				if (value6.equalsIgnoreCase(Constants.DISABLED))
				{
					setForwardTo("blankPage");
					request.setAttribute(Constants.PAGEOF, getPageOf());
					request.setAttribute("participantId", String.valueOf(getId()));
					request.setAttribute("cpId", value1);
				}
				if (value1 == null)
				{
					break;
				}
				else if (!validator.isValidOption(value1))
				{
					String keyTwo = colProClassName + index + "_clinicalStudyParticipantIdentifier";
					String keyFour = colProClassName + index + "_id";
					String keyFive = colProClassName + index + "_isConsentAvailable";
					String keySeven = colProClassName + index + "_ClinicalStudy_shortTitle";
					collectionProtocolRegistrationValues.remove(keyOne);
					collectionProtocolRegistrationValues.remove(keyTwo);
					collectionProtocolRegistrationValues.remove(keyThree);
					collectionProtocolRegistrationValues.remove(keyFour);
					collectionProtocolRegistrationValues.remove(keyFive);
					collectionProtocolRegistrationValues.remove(keySix);
					collectionProtocolRegistrationValues.remove(keySeven);
					count++;
				}

				else if ((validator.isValidOption(value1) && !validator.isValidOption(value6))
						|| (!validator.isValidOption(value1) && !validator.isValidOption(value6)))
				{
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
							// NOPMD - ActionError instantiation
							"errors.participant.participantRegistration.missing",
							ApplicationProperties.getValue("participant.activityStatus")));
					break;
				}

				String errorKey = validator.validateDate(value3, true);
				if (errorKey.trim().length() > 0)
				{
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError( // NOPMD - ActionError instantiation
							errorKey, ApplicationProperties.getValue("participant.cpr.msg")));
					break;
				}

				index++;
			}

			collectionProtocolRegistrationValueCounter = collectionProtocolRegistrationValueCounter
					- count;
			//Getting ConsentRegistrationBean from the  session and creating consent response map.
			setConsentResponse(session);
			
			
			if (!validator.isEmpty(emailAddress))
			{
				if (!validator.isValidEmailAddress(emailAddress))
				{
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
							invalidValueError, ApplicationProperties
									.getValue("user.emailAddress")));
				}
			}

		}
		catch (Exception excp)
		{
			Logger.out.error(excp.getMessage());
		}

		return errors;
	}

	/**
	 * @param values The values to set.
	 */
	public void setValues(Map values)
	{
		this.values = values;
	}

	/**
	 * @return values
	 */
	public Map getValues()
	{
		return this.values;
	}

	/**
	 * Associates the specified object with the specified key in the map.
	 * @param key the key to which the object is mapped.
	 * @param value the object which is mapped.
	 */
	public void setValue(String key, Object value)
	{
		if (isMutable())
		{
			values.put(key, value);
		}
	}

	/**
	 * Returns the object to which this map maps the specified key.
	 * @param key the required key.
	 * @return the object to which this map maps the specified key.
	 */
	public Object getValue(String key)
	{
		return values.get(key);
	}

	/*
	 * This method will get the consent response from session and creates ConsentResponseBean Collection
	 * @param session
	 */
	private void setConsentResponse(HttpSession session)
	{

		Hashtable csentRespHTable = (Hashtable) session.getAttribute(Constants.CONSENT_RESPONSE);
		if (csentRespHTable != null)
		{
			int size = csentRespHTable.size();
			if (this.consentResponseBeanCollection == null)
			{
				this.consentResponseBeanCollection = new LinkedHashSet<ConsentResponseBean>();
			}

			for (int i = 1; i <= size; i++)
			{
				String clinicalStudyID = (String) collectionProtocolRegistrationValues
						.get("ClinicalStudyRegistration:" + i + "_ClinicalStudy_id");
				String csentRespKey = Constants.CONSENT_RESPONSE_KEY + clinicalStudyID;
				ConsentResponseBean csentRespBean = (ConsentResponseBean) csentRespHTable
						.get(csentRespKey);

				if (csentRespBean != null)
				{
					this.consentResponseBeanCollection.add(csentRespBean);
				}
			}
		}
	}

	/**
	 * @return Returns the values.
	 */
	public Collection getAllValues()
	{
		return values.values();
	}

	//Collection Protocol Registration Map

	/**
	* Associates the specified object with the specified key in the map.
	* @param key the key to which the object is mapped.
	* @param value the object which is mapped.
	*/
	public void setCollectionProtocolRegistrationValue(String key, Object value)
	{
		if (isMutable())
		{
			collectionProtocolRegistrationValues.put(key, value);
		}
	}

	/**
	 * @param key
	 * @param value
	 */
	public void setDefaultCollectionProtocolRegistrationValue(String key, Object value)
	{
		collectionProtocolRegistrationValues.put(key, value);
	}

	/**
	 * Returns the object to which this map maps the specified key.
	 * @param key the required key.
	 * @return the object to which this map maps the specified key.
	 */
	public Object getCollectionProtocolRegistrationValue(String key)
	{
		return collectionProtocolRegistrationValues.get(key);
	}

	/**
	 * @return Returns the values.
	 */
	public Collection getAllCollectionProtocolRegistrationValues()
	{
		return collectionProtocolRegistrationValues.values();
	}

	/**
	 * @return
	 */
	public Map getCollectionProtocolRegistrationValues()
	{
		return collectionProtocolRegistrationValues;
	}

	/**
	 * @param colProtoRegnVals
	 */
	public void setCollectionProtocolRegistrationValues(Map colProtoRegnVals)
	{
		this.collectionProtocolRegistrationValues = colProtoRegnVals;
	}

	/**
	* Returns the counter.
	* @return int the counter.
	* @see #setCounter(int)
	*/
	public int getValueCounter()
	{
		return valueCounter;
	}

	/**
	 * Sets the counter.
	 * @param counter The counter.
	 * @see #getCounter()
	 */
	public void setValueCounter(int counter)
	{
		this.valueCounter = counter;
	}

	//Collection Protocol Registration Counter

	/**
	 * Returns the counter.
	 * @return int the counter.
	 * @see #setCounter(int)
	 */
	public int getCollectionProtocolRegistrationValueCounter()
	{
		return collectionProtocolRegistrationValueCounter;
	}

	/**
	 * Sets the counter.
	 * @param counter The counter.
	 * @see #getCounter()
	 */
	public void setCollectionProtocolRegistrationValueCounter(int coProtRegValCnter)
	{
		this.collectionProtocolRegistrationValueCounter = coProtRegValCnter;
	}

	/**
	 * Returns the first part of Social Security Number.
	 * @return String First part of Social Security Number.
	 * @see #setSocialSecurityNumberPartA(String)
	 */
	public String getSocialSecurityNumberPartA()
	{
		return socialSecurityNumberPartA;
	}

	/**
	 * Sets the first part of Social Security Number.
	 * @param socSecNumPartA First part of Social Security Number.
	 * @see #getSocialSecurityNumberPartA()
	 */
	public void setSocialSecurityNumberPartA(String socSecNumPartA)
	{
		this.socialSecurityNumberPartA = socSecNumPartA;
	}

	/**
	 * Returns the second part of Social Security Number.
	 * @return String Second part of Social Security Number.
	 * @see #setSocialSecurityNumberPartB(String)
	 */
	public String getSocialSecurityNumberPartB()
	{
		return socialSecurityNumberPartB;
	}

	/**
	 * Sets the second part of Social Security Number.
	 * @param socSecNumPartB Second part of Social Security Number.
	 * @see #getSocialSecurityNumberPartB()
	 */
	public void setSocialSecurityNumberPartB(String socSecNumPartB)
	{
		this.socialSecurityNumberPartB = socSecNumPartB;
	}

	/**
	 * Returns the third part of Social Security Number.
	 * @return String Third part of Social Security Number.
	 * @see #setSocialSecurityNumberPartC(String)
	 */
	public String getSocialSecurityNumberPartC()
	{
		return socialSecurityNumberPartC;
	}

	/**
	 * Sets the third part of Social Security Number.
	 * @param soclSecNumPartC Third part of Social Security Number.
	 * @see #getSocialSecurityNumberPartC()
	 */
	public void setSocialSecurityNumberPartC(String soclSecNumPartC)
	{
		this.socialSecurityNumberPartC = soclSecNumPartC;
	}

	/**
	 * Returns the Death date of the Participant.
	 * @return Returns the deathDate.
	 */
	public String getDeathDate()
	{
		return deathDate;
	}

	/**
	 * Sets the Death date of the Participant.
	 * @param deathDate The deathDate to set.
	 */
	public void setDeathDate(String deathDate)
	{
		this.deathDate = deathDate;
	}

	/**
	 * Returns the Vital status of the Participant.
	 * @return Returns the vitalStatus.
	 */
	public String getVitalStatus()
	{
		return vitalStatus;
	}

	/**
	 * Sets the Vital status of the Participant.
	 * @param vitalStatus The vitalStatus to set.
	 */
	public void setVitalStatus(String vitalStatus)
	{
		this.vitalStatus = vitalStatus;
	}

	/**
	 * @return cpId
	 */
	public long getCpId()
	{
		return cpId;
	}

	/**
	 * @param cpId Set cpId
	 */
	public void setCpId(long cpId)
	{
		this.cpId = cpId;
	}

	/**
	 * Returns the Consent Response HashTable entered by the user. 
	 * @return
	 */
	public Hashtable getConsentResponseHashTable()
	{
		return consentResponseHashTable;
	}

	/**
	 * Returns the Consent Response Collection entered by the user. 
	 * @return
	 */
	public Collection<ConsentResponseBean> getConsentResponseBeanCollection()
	{
		return consentResponseBeanCollection;
	}

	/**
	 * @param csentRespBeanColn
	 */
	public void setConsentResponseBeanCollection(Collection<ConsentResponseBean> csentRespBeanColn)
	{
		this.consentResponseBeanCollection = csentRespBeanColn;
	}

	/**
	 * @param csentRespHTable
	 */
	public void setConsentResponseHashTable(Hashtable csentRespHTable)
	{
		this.consentResponseHashTable = csentRespHTable;
	}

	@Override
	public void setAddNewObjectIdentifier(String addNewFor, Long addObjectIdentifier)
	{
		// TODO Auto-generated method stub

	}

}