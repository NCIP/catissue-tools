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
 * <p>Title: Participant Class>
 * <p>Description:  An individual from whom a specimen is collected. </p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Aniruddha Phadnis
 * @author Gautam Shetty
 * @version 1.00
 * Created on Apr 7, 2005
 */

package edu.wustl.clinportal.domain;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;

import edu.wustl.clinportal.actionForm.ParticipantForm;
import edu.wustl.clinportal.bean.ConsentBean;
import edu.wustl.clinportal.bean.ConsentResponseBean;
import edu.wustl.clinportal.util.SearchUtil;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.actionForm.IValueObject;
import edu.wustl.common.bizlogic.IActivityStatus;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.util.MapDataParser;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.logger.Logger;

/**
 * An individual from whom a specimen is collected.
 * @hibernate.class table="CATISSUE_PARTICIPANT"
 
 */
public class Participant extends AbstractDomainObject
		implements
			java.io.Serializable,
			IActivityStatus
{

	private static final long serialVersionUID = 1234567890L;

	/**
	 * System generated unique id.
	 * */
	protected Long id;

	/**
	 * Last name of the participant.
	 */
	protected String lastName;

	/**
	 * First name of the participant.
	 */
	protected String firstName;

	/**
	 * Middle name of the participant.
	 */
	protected String middleName;

	/**
	 * Birth date of participant.
	 */
	protected Date birthDate;

	/**
	 * The gender of the participant.
	 */
	protected String gender;

	/**
	 * The genetic constitution of the individual.
	 */
	protected String sexGenotype;

	/**
	 * Participant's race origination.
	 */
	protected Collection raceCollection = new HashSet();

	/**
	 * Participant's ethnicity status.
	 */
	protected String ethnicity;

	/**
	 * Social Security Number of participant.
	 */
	protected String socialSecurityNumber;

	/**
	 * Defines whether this participant record can be queried (Active) or not queried (Inactive) by any actor
	 * */
	protected String activityStatus;

	/**
	 * Death date of participant.
	 */
	protected Date deathDate;

	/**
	 * Defines the vital status of the participant like 'Dead', 'Alive' or 'Unknown'.
	 */
	protected String vitalStatus;

	/**
	 * A collection of medical record identification number that refers to a Participant. 
	 * */
	protected Collection participantMedicalIdentifierCollection = new LinkedHashSet();// = new HashSet();

	/**
	 * A collection of registration of a Participant to a Collection Protocol. 
	 * */
	protected Collection collectionProtocolRegistrationCollection = new HashSet();

	protected Collection clinicalStudyRegistrationCollection = new HashSet();
	/**
	 * The Health Insurance of the participant.
	 */
	protected String healthInsurance;
	/**
	 * Patient referred by
	 */
	protected String refBy;

	protected String familyName;
	protected String businessField;	
	
	/**
	 * emailAddress name of the participant.
	 */
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


	/**
	 * The Emergency contact No of the participant.
	 */
	protected String emgContactNo;
	
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
	* Returns collection registrations of this participant.
	* @return collection of registrations of this participant.
	* @hibernate.set name="clinicalStudyRegistrationCollection" table="CATISSUE_CLINICAL_STUDY_REG"
	* @hibernate.collection-key column="PARTICIPANT_ID" lazy="true"
	* @hibernate.collection-one-to-many class="edu.wustl.clinportal.domain.ClinicalStudyRegistration"
	* @see setRegistrationCollection(Collection)
	*/
	public Collection getClinicalStudyRegistrationCollection()
	{
		return clinicalStudyRegistrationCollection;
	}

	/**
	* @param clStudyRegnColn
	*/
	public void setClinicalStudyRegistrationCollection(Collection clStudyRegnColn)
	{
		this.clinicalStudyRegistrationCollection = clStudyRegnColn;
	}
	
	/**
	 * The address of the site.
	 */
	private Address address;
	
	/**
	 * 
	 */
	/**
	 * 
	 */
	public Participant()
	{
	}

	public Address getAddress() 
	{
		return address;
	}

	public void setAddress(Address address) 
	{
		this.address = address;
	}

	/**
	 * @param form
	 */
	public Participant(AbstractActionForm form)
	{
		setAllValues(form);
	}

	/**
	 * @param id2
	 * @param lastName2
	 * @param firstName2
	 * @param middleName2
	 * @param birthDate2
	 * @param gender2
	 * @param sexGenotype2
	 * @param raceCollection2
	 * @param ethnicity2
	 * @param socialSecNumber2
	 * @param activityStatus2
	 * @param deathDate2
	 * @param vitalStatus2
	 * @param partiMediIdColn
	 */
	public Participant(Long id2, String lastName2, String firstName2, String middleName2,
			Date birthDate2, String gender2, String sexGenotype2, Collection raceCollection2,
			String ethnicity2, String socialSecNumber2, String activityStatus2, Date deathDate2,
			String vitalStatus2, Collection partiMediIdColn)
	{
		this.id = id2;
		this.lastName = lastName2;
		this.firstName = firstName2;
		this.middleName = middleName2;
		this.birthDate = birthDate2;
		this.gender = gender2;
		this.sexGenotype = sexGenotype2;
		this.raceCollection = raceCollection2;
		this.ethnicity = ethnicity2;
		this.socialSecurityNumber = socialSecNumber2;
		this.activityStatus = activityStatus2;
		this.deathDate = deathDate2;
		this.vitalStatus = vitalStatus2;
		this.participantMedicalIdentifierCollection = partiMediIdColn;
	}

	/**
	 * @param participant
	 */
	public Participant(Participant participant)
	{
		this.id = participant.getId();
		this.lastName = participant.getLastName();
		this.firstName = participant.getFirstName();
	}

	/**
	 * Returns System generated unique id.
	 * @return Long System generated unique id.
	 * @see #setId(Long)
	 * @hibernate.id name="id" column="IDENTIFIER" type="long" length="30"
	 * unsaved-value="null" generator-class="native" 
	 * @hibernate.generator-param name="sequence" value="CATISSUE_PARTICIPANT_SEQ"
	 */
	@Override
	public Long getId()
	{
		return id;
	}

	/**
	 * Sets system generated unique id.
	 * @param idt System generated unique id.
	 * @see #getId()
	 * */
	@Override
	public void setId(Long idt)
	{
		this.id = idt;
	}

	/**
	 * Returns the last name of the Participant. 
	 * @return String representing the last name of the Participant.
	 * @see #setLastName(String)
	 * @hibernate.property name="lastName" type="string" 
	 * column="LAST_NAME" length="255"
	 */
	public String getLastName()
	{
		return lastName;
	}

	/**
	 * Sets the last name of the Participant.
	 * @param lastName Last Name of the Participant.
	 * @see #getLastName()
	 */
	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	/**
	 * Returns the first name of the Participant.
	 * @return String representing the first name of the Participant.
	 * @see #setFirstName(String)
	 * @hibernate.property name="firstName" type="string" 
	 * column="FIRST_NAME" length="255"
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
	 * @return String representing the middle name of the Participant.
	 * @see #setMiddleName(String)
	 * @hibernate.property name="middleName" type="string" 
	 * column="MIDDLE_NAME" length="255"
	 */
	public String getMiddleName()
	{
		return middleName;
	}

	/**
	 * Sets the middle name of the Participant.
	 * @param middleName String representing the middle name of the Participant.
	 * @see #getMiddleName()
	 */
	public void setMiddleName(String middleName)
	{
		this.middleName = middleName;
	}

	/**
	 * Returns the date of birth of the Participant.
	 * @return String representing the middle name of the Participant.
	 * @see #setBirthDate(String)
	 * @hibernate.property name="birthDate" column="BIRTH_DATE" type="date"
	 */
	public Date getBirthDate()
	{
		return birthDate;
	}

	/**
	 * Sets the date of birth of the Participant.
	 * @param birthDate String representing the date of birth of the Participant.
	 * @see #getDateOfBirth()
	 */
	public void setBirthDate(Date birthDate)
	{
		this.birthDate = birthDate;
	}

	/**
	 * Returns the gender of a participant.
	 * @return String representing the gender of a participant.
	 * @see #setGender(String)
	 * @hibernate.property name="gender" type="string" 
	 * column="GENDER" length="20"
	 */
	public String getGender()
	{
		return gender;
	}

	/**
	 * Sets the gender of a participant.
	 * @param gender the gender of a participant.
	 * @see #getGender()
	 */
	public void setGender(String gender)
	{
		this.gender = gender;
	}

	/**
	 * Returns the genotype of a participant.
	 * @return String representing the genotype of a participant.
	 * @see #setSexGenotype(String)
	 * @hibernate.property name="sexGenotype" type="string" 
	 * column="GENOTYPE" length="50"
	 */
	public String getSexGenotype()
	{
		return sexGenotype;
	}

	/**
	 * Sets the genotype of a participant.
	 * @param sexGenotype the genotype of a participant.
	 * @see #getSexGenotype()
	 */
	public void setSexGenotype(String sexGenotype)
	{
		this.sexGenotype = sexGenotype;
	}

	/**
	  * @return Returns the raceCollection.
	  * @hibernate.set name="raceCollection" table="CATISSUE_RACE"
	 * cascade="save-update" inverse="false" lazy="false"
	 * @hibernate.collection-key column="PARTICIPANT_ID"
	 * @hibernate.element type="string" column="NAME" length="30"
	  */
	public Collection getRaceCollection()
	{
		return raceCollection;
	}

	/**
	 * @param raceCollection The raceCollection to set.
	 */
	public void setRaceCollection(Collection raceCollection)
	{
		this.raceCollection = raceCollection;
	}

	/**
	 * Returns the ethnicity of the Participant.
	 * @return Ethnicity of the Participant.
	 * @see #setEthnicity(String)
	 * @hibernate.property name="ethnicity" type="string"
	 * column="ETHNICITY" length="50" 
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
	 * Returns the Social Security Number of the Participant.
	 * @return String representing the Social Security Number of the Participant.
	 * @see #setSocialSecurityNumber(String)
	 * @hibernate.property name="socialSecurityNumber" type="string"
	 * column="SOCIAL_SECURITY_NUMBER" length="50" unique="true" 
	 */
	public String getSocialSecurityNumber()
	{
		return socialSecurityNumber;
	}

	/**
	 * Sets the Social Security Number of the Participant.
	 * @param birthDate String representing the Social Security Number of the Participant.
	 * @see #getSocialSecurityNumber()
	 */
	public void setSocialSecurityNumber(String socSecurityNumber)
	{
		this.socialSecurityNumber = socSecurityNumber;
	}

	/**
	 * Returns the activity status of the participant.
	 * @hibernate.property name="activityStatus" type="string"
	 * column="ACTIVITY_STATUS" length="50"
	 * @return Returns the activity status of the participant.
	 * @see #setActivityStatus(String)
	 */
	public String getActivityStatus()
	{
		return activityStatus;
	}

	/**
	 * Sets the activity status of the participant.
	 * @param activityStatus activity status of the participant.
	 * @see #getActivityStatus()
	 */
	public void setActivityStatus(String activityStatus)
	{
		this.activityStatus = activityStatus;
	}

	/**
	 * Returns the date of death of the Participant.
	 * @return Date representing the death date of the Participant.
	 * @see #setDeathDate(Date)
	 * @hibernate.property name="deathDate" column="DEATH_DATE" type="date"
	 */
	public Date getDeathDate()
	{
		return deathDate;
	}

	/**
	 * Sets the date of birth of the Participant.
	 * @param deathDate The deathDate to set.
	 */
	public void setDeathDate(Date deathDate)
	{
		this.deathDate = deathDate;
	}

	/**
	 * Returns the vital status of the participant.
	 * @return Returns the vital status of the participant.
	 * @see #setVitalStatus(String)
	 * @hibernate.property name="vitalStatus" type="string"
	 * column="VITAL_STATUS" length="50"
	 */
	public String getVitalStatus()
	{
		return vitalStatus;
	}

	/**
	 * Sets the vital status of the Participant.
	 * @param vitalStatus The vitalStatus to set.
	 */
	public void setVitalStatus(String vitalStatus)
	{
		this.vitalStatus = vitalStatus;
	}

	/**
	 * Returns collection of medical identifiers associated with this participant.
	 * @return collection of medical identifiers of this participant.
	 * @hibernate.set name="participantMedicalIdentifierCollection" table="CATISSUE_PART_MEDICAL_ID"
	 * cascade="none" inverse="true" lazy="false"
	 * @hibernate.collection-key column="PARTICIPANT_ID"
	 * @hibernate.collection-one-to-many class="edu.wustl.clinportal.domain.ParticipantMedicalIdentifier"
	 * @see setParticipantMedicalIdentifierCollection(Collection)
	 */
	public Collection getParticipantMedicalIdentifierCollection()
	{
		return participantMedicalIdentifierCollection;
	}

	/**
	 * Sets the collection of medical identifiers of this participant.
	 * @param partiMediIdColn collection of medical identifiers of this participant.
	 * @see #getParticipantMedicalIdentifierCollection()
	 */
	public void setParticipantMedicalIdentifierCollection(Collection partiMediIdColn)
	{
		this.participantMedicalIdentifierCollection = partiMediIdColn;
	}

	/**
	 * Returns collection of collection protocol registrations of this participant.
	 * @return collection of collection protocol registrations of this participant.
	 * @hibernate.set name="collectionProtocolRegistrationCollection" table="CATISSUE_COLL_PROT_REG"
	 * @hibernate.collection-key column="PARTICIPANT_ID"
	 * @hibernate.collection-one-to-many class="edu.wustl.clinportal.domain.CollectionProtocolRegistration"
	 * @see setCollectionProtocolRegistrationCollection(Collection)
	 */
	public Collection getCollectionProtocolRegistrationCollection()
	{
		return collectionProtocolRegistrationCollection;
	}

	/**
	 * Sets the collection protocol registrations of this participant.
	 * @param protocolRegistrationCollection collection of collection protocol registrations of this participant.
	 * @see #getCollectionProtocolRegistrationCollection()
	 */
	public void setCollectionProtocolRegistrationCollection(Collection colProtoRegColn)
	{
		this.collectionProtocolRegistrationCollection = colProtoRegColn;
	}

	/**
	 * This function Copies the data from a StorageTypeForm object to a StorageType object.
	 * @param storageTypeForm A StorageTypeForm object containing the information about the StorageType.  
	 * */
	@Override
	public void setAllValues(IValueObject abstractForm)
	{
		try
		{
			ParticipantForm form = (ParticipantForm) abstractForm;
			Validator validator = new Validator();

			this.activityStatus = form.getActivityStatus();
			this.firstName = form.getFirstName();
			this.middleName = form.getMiddleName();
			this.lastName = form.getLastName();
			this.familyName = form.getFamilyName();
			this.businessField = form.getBusinessField();

			populateGenderGenotypeAndInsurance(form, validator);
			this.emgContactNo = form.getEmgContactNo();
			this.refBy=form.getRefBy();
			if (validator.isValidOption(form.getEthnicity()))
			{
				this.ethnicity = form.getEthnicity();
			}
			else
			{
				this.ethnicity = null;
			}

			populateRaceCollection(form);

			populateSSN(form, validator);

			this.birthDate = Utility.parseDate(form.getBirthDate(),Constants.DATE_PATTERN_DD_MM_YYYY); 
					//Utility.datePattern(Constants.DATE_PATTERN_DD_MM_YYYY

			this.deathDate = Utility.parseDate(form.getDeathDate(), Constants.DATE_PATTERN_DD_MM_YYYY);

			populateVitalStatus(form, validator);
			populatePMICollection(form);

			//Collection Protocol Registration of the participant

			popualteCSRegistrationCollection(form);

			setConsentsResponseToCollectionProtocolRegistration(form);
			
			if (SearchUtil.isNullobject(address))
			{
				address = new Address();
			}
			this.address.setStreet(form.getStreet());
			this.address.setCity(form.getCity());
			this.address.setState(form.getState());
			this.address.setCountry(form.getCountry());
			this.address.setZipCode(form.getZipCode());
			this.address.setPhoneNumber(form.getPhoneNumber());
			this.address.setFaxNumber(form.getFaxNumber());
			this.setBloodGroup(edu.wustl.common.util.Utility.toString(form.getBloodGroup()));
			this.setEmailAddress(edu.wustl.common.util.Utility.toString(form.getEmailAddress()));
		}
		catch (Exception excp)
		{
			Logger.out.error(excp.getMessage(), excp);
		}
	}

	/**
	 * @param form
	 * @param validator
	 */
	private void populateGenderGenotypeAndInsurance(ParticipantForm form, Validator validator)
	{
		if (validator.isValidOption(form.getGender()))
		{
			this.gender = form.getGender();
		}
		else
		{
			this.gender = null;
		}

		if (validator.isValidOption(form.getGenotype()))
		{
			this.sexGenotype = form.getGenotype();
		}
		else
		{
			this.sexGenotype = null;
		}

		if (validator.isValidOption(form.getHealthInsurance()))
		{
			this.healthInsurance = form.getHealthInsurance();
		}
		else
		{
			this.healthInsurance = null;
		}
	}

	/**
	 * @param form
	 */
	private void populateRaceCollection(ParticipantForm form)
	{
		raceCollection.clear();
		String[] raceTypes = form.getRaceTypes();
		if (raceTypes != null)
		{
			for (int i = 0; i < raceTypes.length; i++)
			{
				if (!raceTypes[i].equals("-1"))
				{
					Race race = new Race();
					race.setRaceName(raceTypes[i]);
					race.setParticipant(this);
					raceCollection.add(race);
				}

			}
		}
	}

	/**
	 * @param form
	 * @param validator
	 */
	private void populateSSN(ParticipantForm form, Validator validator)
	{
		String sSNumberTemp = form.getSocialSecurityNumberPartA() + "-"
				+ form.getSocialSecurityNumberPartB() + "-" + form.getSocialSecurityNumberPartC();

		if (!validator.isEmpty(sSNumberTemp) && validator.isValidSSN(sSNumberTemp))
		{
			this.socialSecurityNumber = sSNumberTemp;
		}
		else
		{
			this.socialSecurityNumber = null;
		}
	}

	/**
	 * @param form
	 * @param validator
	 */
	private void populateVitalStatus(ParticipantForm form, Validator validator)
	{
		if (validator.isValidOption(form.getVitalStatus()))
		{
			this.vitalStatus = form.getVitalStatus();
		}
		else
		{
			this.vitalStatus = null;
		}
	}

	/**
	 * @param form
	 * @throws Exception
	 */
	private void populatePMICollection(ParticipantForm form) throws Exception
	{
		this.participantMedicalIdentifierCollection.clear();
		Map map = form.getValues();
		Logger.out.debug("Map " + map);
		MapDataParser parser = new MapDataParser("edu.wustl.clinportal.domain");
		this.participantMedicalIdentifierCollection = parser.generateData(map);
	}

	/**
	 * @param form
	 * @throws Exception
	 */
	private void popualteCSRegistrationCollection(ParticipantForm form) throws Exception
	{
		this.clinicalStudyRegistrationCollection.clear();
		//Map mapCStudyRegColn = form.getCollectionProtocolRegistrationValues();
		//MapDataParser parserCStRgCol = new MapDataParser("edu.wustl.clinportal.domain");
		ClinicalStudy cs = new ClinicalStudy();
		cs.setId(form.getCpId());
		Collection<ClinicalStudyRegistration> csStudyColnReg = new HashSet<ClinicalStudyRegistration>();
		ClinicalStudyRegistration csr = new ClinicalStudyRegistration();
		csr.setClinicalStudyParticipantIdentifier(form.getClinicalStudyId());
		csr.setClinicalStudy(cs);
		csr.setRegistrationDate(Utility.parseDate(form.getRegistrationDate(), Constants.DATE_PATTERN_DD_MM_YYYY));
		csr.setActivityStatus(form.getActivityStatus());
		csStudyColnReg.add(csr);
		this.clinicalStudyRegistrationCollection = csStudyColnReg;

	}

	/** 
	 * Setting Consent Response for the collection protocol
	 * @param form
	 * @throws Exception
	 */
	private void setConsentsResponseToCollectionProtocolRegistration(ParticipantForm form)
			throws Exception
	{
		Collection<ConsentResponseBean> csentRespBeanCol = form.getConsentResponseBeanCollection();
		Iterator itr = this.clinicalStudyRegistrationCollection.iterator();
		while (itr.hasNext())
		{
			ClinicalStudyRegistration clStudyRegn = (ClinicalStudyRegistration) itr.next();
			setConsentResponse(clStudyRegn, csentRespBeanCol);
		}
	}

	/**
	 * Set Consent Response for given collection protocol
	 * @param clStudyRegn
	 * @param csentRespBeanColn
	 * @throws Exception
	 */
	private void setConsentResponse(ClinicalStudyRegistration clStudyRegn,
			Collection csentRespBeanColn) throws Exception
	{
		if (csentRespBeanColn != null && !csentRespBeanColn.isEmpty())
		{
			Iterator itr = csentRespBeanColn.iterator();
			while (itr.hasNext())
			{
				ConsentResponseBean csentRespBean = (ConsentResponseBean) itr.next();
				long cpIDcolProtoReg = clStudyRegn.getClinicalStudy().getId().longValue();
				long cpIDcnsntRegnBean = csentRespBean.getCollectionProtocolID();
				if (cpIDcolProtoReg == cpIDcnsntRegnBean)
				{

					String signedConsentUrl = csentRespBean.getSignedConsentUrl();
					long witnessId = csentRespBean.getWitnessId();
					String consentDate = csentRespBean.getConsentDate();
					Collection csentTierRespColn = prepareConsentTierResponseCollection(
							csentRespBean.getConsentResponse(), true);

					clStudyRegn.setSignedConsentDocumentURL(signedConsentUrl);

					setConsentWitness(witnessId, clStudyRegn);

					clStudyRegn.setConsentSignatureDate(Utility.parseDate(consentDate));
					clStudyRegn.setConsentTierResponseCollection(csentTierRespColn);
					clStudyRegn.setConsentWithdrawalOption(csentRespBean
							.getConsentWithdrawalOption());
					break;
				}
			}
		}
	}

	/**
	 * @param witnessId
	 * @param clStudyRegn
	 */
	private void setConsentWitness(long witnessId, ClinicalStudyRegistration clStudyRegn)
	{
		if (witnessId > 0)
		{
			User consentWitness = new User();
			consentWitness.setId(Long.valueOf(witnessId));
			clStudyRegn.setConsentWitness(consentWitness);
		}
	}

	/**
	 *  Preparing consent response collection from entered response.
	 * @param consentResponse
	 * @param isResponse
	 * @return
	 */
	private Collection prepareConsentTierResponseCollection(Collection consentResponse,
			boolean isResponse)
	{
		Collection csentTierRespColn = new HashSet();
		if (consentResponse != null && !consentResponse.isEmpty())
		{
			if (isResponse)
			{
				Iterator iter = consentResponse.iterator();
				while (iter.hasNext())
				{
					ConsentBean consentBean = (ConsentBean) iter.next();
					ClinicalStudyConsentTierResponse csentTierResponse = new ClinicalStudyConsentTierResponse();
					//Setting response
					csentTierResponse.setResponse(consentBean.getParticipantResponse());
					if (consentBean.getParticipantResponseID() != null
							&& consentBean.getParticipantResponseID().trim().length() > 0)
					{
						csentTierResponse.setId(Long.parseLong(consentBean
								.getParticipantResponseID()));
					}
					//Setting consent tier
					ClinicalStudyConsentTier consentTier = new ClinicalStudyConsentTier();
					consentTier.setId(Long.parseLong(consentBean.getConsentTierID()));
					consentTier.setStatement(consentBean.getStatement());

					csentTierResponse.setConsentTier(consentTier);
					csentTierRespColn.add(csentTierResponse);
				}
			}
			else
			{
				Iterator iter = consentResponse.iterator();
				while (iter.hasNext())
				{
					ClinicalStudyConsentTier consentTier = (ClinicalStudyConsentTier) iter.next();
					ClinicalStudyConsentTierResponse csentTierResponse = new ClinicalStudyConsentTierResponse();
					csentTierResponse.setResponse(Constants.NOT_SPECIFIED);
					csentTierResponse.setConsentTier(consentTier);
					csentTierRespColn.add(csentTierResponse);
				}
			}
		}
		return csentTierRespColn;
	}

	/**
	* Returns message label to display on success add or edit
	* @return String
	*/
	public String getMessageLabel()
	{
		return edu.wustl.clinportal.util.global.Utility.getLabel(this.lastName, this.firstName);
	}
}