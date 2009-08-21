/**
 * <p>Title: CollectionProtocolRegistration Class>
 * <p>Description:  A registration of a Participant to a Clinical Study.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Shital Lawhale
 * @version 1.00
 */

package edu.wustl.clinportal.domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

import edu.wustl.clinportal.actionForm.ClinicalStudyRegistrationForm;
import edu.wustl.clinportal.bean.ConsentBean;
import edu.wustl.clinportal.util.SearchUtil;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.actionForm.IValueObject;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.AssignDataException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.MapDataParser;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.logger.Logger;

/**
 * A registration of a Participant to a Clinical Study.
 * @hibernate.class table="CATISSUE_CLINICAL_STUDY_REG"
 * 
 */
public class ClinicalStudyRegistration extends AbstractDomainObject implements Serializable
{
    private static final long serialVersionUID = 1234567890L;

    /**
     * System generated unique id.
     */
    protected Long id;
    
    /**
     * A unique number given by a User to a Participant 
     * registered to a Clinical Study.
     */
    protected String clinicalStudyParticipantIdentifier;
    
    /**
     * Date on which the Participant is registered to the Collection Protocol.
     */
    protected Date registrationDate;

    /**
     * An individual from whom a specimen is to be collected.
     */
    protected Participant participant = null;
    
    
    /**
     * A set of written procedures that describe how a 
     * biospecimen is prospectively collected.
     */   
    private ClinicalStudy clinicalStudy;

    protected Collection eventEntryCollection = new HashSet(); 
    
    /**
     * Defines whether this ClinicalStudyRegistration record can be queried (Active) or not queried (Inactive) by any actor
     * */
    protected String activityStatus;

    /**
	 * The signed consent document URL.
	 */
	protected String signedConsentDocumentURL;
	/**
	 * The date on which consent document was signed.
	 */
	protected Date consentSignatureDate;
	/**
	 * The witness for the signed consent document.
	 */
	protected User consentWitness;
	/**
	 * The collection of responses of multiple participants for a particular consent.
	 */
	protected Collection consentTierResponseCollection;
    
	/*
	 * To perform operation based on withdraw button clicked.
	 * Default No Action to allow normal behaviour. 
	 */
	protected String consentWithdrawalOption=Constants.WITHDRAW_RESPONSE_NOACTION;
	
	//Consent Availability for collection protocol
	protected String isConsentAvailable;
	public Date getConsentSignatureDate()
	{
		return consentSignatureDate;
	}
	
	/**
	 * @param consentSignatureDate the consentSignatureDate to set
	 */
	public void setConsentSignatureDate(Date consentSignatureDate)
	{
		this.consentSignatureDate = consentSignatureDate;
	}
	
	/**
	 * @return the signedConsentDocumentURL
	 * @hibernate.property name="signedConsentDocumentURL" type="string" length="1000" column="CONSENT_DOC_URL"
	 */
	public String getSignedConsentDocumentURL()
	{
		return signedConsentDocumentURL;
	}
	
	/**
	 * @param signedConsentDocumentURL the signedConsentDocumentURL to set
	 */
	public void setSignedConsentDocumentURL(String signedConsentDocumentURL)
	{
		this.signedConsentDocumentURL = signedConsentDocumentURL;
	}
	
	/**
	 * @return the consentTierResponseCollection
	 * @hibernate.collection-one-to-many class="edu.wustl.clinportal.domain.ClinicalStudyConsentTierResponse" lazy="true" cascade="save-update"
	 * @hibernate.set name="consentTierResponseCollection" table="CLINPORT_CONSENT_TIER_RESPONSE" 
	 * @hibernate.collection-key column="CLINICAL_STUDY_REG_ID"
	 */
	public Collection getConsentTierResponseCollection()
	{
		return consentTierResponseCollection;
	}
	
	/**
	 * @param consentTierResponseCollection the consentTierResponseCollection to set
	 */
	public void setConsentTierResponseCollection(Collection consentTierResponseCollection)
	{
		this.consentTierResponseCollection = consentTierResponseCollection;
	}
	
	/**
	 * @return the consentWitness
	 * @hibernate.many-to-one class="edu.wustl.clinportal.domain.User" constrained="true" column="CONSENT_WITNESS"
	 */
	public User getConsentWitness()
	{
		return consentWitness;
	}
	
	/**
	 * @param consentWitness the consentWitness to set
	 */
	public void setConsentWitness(User consentWitness)
	{
		this.consentWitness = consentWitness;
	}
	
    public ClinicalStudyRegistration()
    {

    }

    
    /**
     * one argument constructor
     * @param CollectionProtocolRegistrationFrom object 
     */
    public ClinicalStudyRegistration(AbstractActionForm form) throws AssignDataException
    {
        setAllValues(form);
    }

    /**
     * Returns the system generated unique id.
     * @hibernate.id name="id" column="IDENTIFIER" type="long" length="30"
     * unsaved-value="null" generator-class="native"
     * @hibernate.generator-param name="sequence" value="CATISSUE_STUDY_REG_SEQ"
     * @return the system generated unique id.
     * @see #setId(Long)
     * */
    public Long getId()
    {
        return id;
    }

    /**
     * Sets the system generated unique id.
     * @param idt the system generated unique id.
     * @see #getId()
     * */
    public void setId(Long idt)
    {
        this.id = idt;
    }

    /**
     * Returns the unique number given by a User to a Participant 
     * registered to a Clinical Study.
     * @hibernate.property name="clinicalStudyParticipantIdentifier" type="string"
     * column="CLINICAL_STUDY_PARTICIPANT_ID" length="255"
     * @return the unique number given by a User to a Participant 
     * registered to a Clinical Study.
     * @see #setClinicalStudyParticipantIdentifier(Long)
     */
    public String getClinicalStudyParticipantIdentifier()
    {
        return clinicalStudyParticipantIdentifier;
    }

    /**
     * Sets the unique number given by a User to a Participant 
     * registered to a Collection Protocol.
     * @param clinicalStudyParticipantIdentifier the unique number given by a User to a Participant 
     * registered to a Collection Protocol.
     * @see #getClinicalStudyParticipantIdentifier()
     */
    public void setClinicalStudyParticipantIdentifier(String clinicalStudyParticipantIdentifier)
    {
        this.clinicalStudyParticipantIdentifier = clinicalStudyParticipantIdentifier;
    }

    /**
     * Returns the date on which the Participant is 
     * registered to the clinicalStudy.
     * @hibernate.property name="registrationDate" column="REGISTRATION_DATE" type="date"
     * @return the date on which the Participant is 
     * registered to the clinicalStudy.
     * @see #setRegistrationDate(Date)
     */
    public Date getRegistrationDate()
    {
        return registrationDate;
    }

    /**
     * Sets the date on which the Participant is 
     * registered to the Collection Protocol.
     * @param registrationDate the date on which the Participant is 
     * registered to the Collection Protocol.
     * @see #getRegistrationDate()
     */
    public void setRegistrationDate(Date registrationDate)
    {
        this.registrationDate = registrationDate;
    }

    /**
     * Returns the individual from whom a specimen is to be collected.
     * @hibernate.many-to-one column="PARTICIPANT_ID"
     * class="edu.wustl.clinportal.domain.Participant" constrained="true"
     * @return the individual from whom a specimen is to be collected.
     * @see #setParticipant(Participant)
     */
    public Participant getParticipant()
    {
        return participant;
    }

    /**
     * Sets the individual from whom a specimen is to be collected.
     * @param participant the individual from whom a specimen is to be collected.
     * @see #getParticipant()
     */
    public void setParticipant(Participant participant)
    {
        this.participant = participant;
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
    
    public String getIsConsentAvailable() {
		return isConsentAvailable;
	}

	public void setIsConsentAvailable(String isConsentAvailable) {
		this.isConsentAvailable = isConsentAvailable;
	}
	public String getConsentWithdrawalOption() {
		return consentWithdrawalOption;
	}

	public void setConsentWithdrawalOption(String consentWithdrawalOption) {
		this.consentWithdrawalOption = consentWithdrawalOption;
	}
    /** 
     * Set all values from clinicalStudyRegistrationForm to the member variables of class
     * @param clinicalStudyRegistrationForm object  
     */
    public void setAllValues(IValueObject abstractForm) throws AssignDataException
    {
        ClinicalStudyRegistrationForm form = (ClinicalStudyRegistrationForm) abstractForm;
        try
        {
            this.activityStatus      = form.getActivityStatus();
            
            if (SearchUtil.isNullobject(clinicalStudy))
            {
                clinicalStudy = new ClinicalStudy();
            }
            
                        
            if (SearchUtil.isNullobject(this.registrationDate))
            {
                registrationDate  = new Date();
            }

            this.clinicalStudy.setId(Long.valueOf(form.getClinicalStudyId()));
            
            setParticipantValue(form);
            
            setCSParticipantIdentifier(form);
            
            this.registrationDate = Utility.parseDate(form.getRegistrationDate(),Utility.datePattern(form.getRegistrationDate()));          
       
       		//Setting the consent sign date.
			this.consentSignatureDate = Utility.parseDate(form.getConsentDate());
			//Setting the signed doc url
			setSignedConsentDocumentURLValue(form);
			
			//Setting the consent witness
			if(form.getWitnessId()>0)
			{
				this.consentWitness = new User();
				consentWitness.setId(Long.valueOf(form.getWitnessId()));
			}
			//Preparing  Consent tier response Collection 
			this.consentTierResponseCollection = prepareParticipantResponseCollection(form);
			
			// For withdraw options
			this.consentWithdrawalOption = form.getWithdrawlButtonStatus();
            }
        catch (Exception e)
        {
            Logger.out.error(e.getMessage());
            throw new AssignDataException(ErrorKey.getErrorKey("error.assign.data"), e, "Error while setting values");
        }
    }

    private void setParticipantValue(ClinicalStudyRegistrationForm form)
    {
    	if(form.getParticipantID() != -1 && form.getParticipantID() != 0)
        {
            this.participant = new Participant();
            this.participant.setId(Long.valueOf(form.getParticipantID()));
        }
        else
        {
            this.participant = null;
        }
    }
    
    private void setCSParticipantIdentifier(ClinicalStudyRegistrationForm form)
    {
    	this.clinicalStudyParticipantIdentifier = form.getParticipantClinicalStudyID().trim();
        if("".equals(clinicalStudyParticipantIdentifier))
        {
            this.clinicalStudyParticipantIdentifier = null;
        }
    }
    
    private void setSignedConsentDocumentURLValue(ClinicalStudyRegistrationForm form)
    {
    	this.signedConsentDocumentURL = form.getSignedConsentUrl();
		if("".equals(signedConsentDocumentURL))
		{
			this.signedConsentDocumentURL=null;
		}
    }
    
    /**
	* For Consent Tracking
	* Setting the Domain Object 
	* @param  form CollectionProtocolRegistrationForm
	* @return consentResponseColl
	*/
	private Collection prepareParticipantResponseCollection(ClinicalStudyRegistrationForm form) 
	{
		MapDataParser mapdataParser = new MapDataParser("edu.wustl.clinportal.bean");
        Collection beanObjColl=null;
		try
		{
			beanObjColl = mapdataParser.generateData(form.getConsentResponseValues());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
        Iterator iter = beanObjColl.iterator();
        Collection consentResponseColl = new HashSet();
        while(iter.hasNext())
        {
        	ConsentBean consentBean = (ConsentBean)iter.next();
        	
        	ClinicalStudyConsentTierResponse consentTierResponse = getConsentTierResponse(consentBean);
        	consentResponseColl.add(consentTierResponse);
        }
        return consentResponseColl;
	}
	
	private ClinicalStudyConsentTierResponse getConsentTierResponse(ConsentBean consentBean)
	{
		ClinicalStudyConsentTierResponse consentTierResponse = new ClinicalStudyConsentTierResponse();
		//Setting response
		consentTierResponse.setResponse(consentBean.getParticipantResponse());
		if (consentBean.getParticipantResponseID() != null && consentBean.getParticipantResponseID().trim().length() > 0)
		{
			consentTierResponse.setId(Long.parseLong(consentBean.getParticipantResponseID()));
		}
		//Setting consent tier
		ClinicalStudyConsentTier consentTier = new ClinicalStudyConsentTier();
		consentTier.setId(Long.parseLong(consentBean.getConsentTierID()));
		consentTierResponse.setConsentTier(consentTier);
		return consentTierResponse;
	}
	
	
    /** 
     * @hibernate.many-to-one column="CLINICAL_STUDY_ID" class="edu.wustl.clinportal.domain.ClinicalStudy"
     * constrained="true"
     * @return
     */
    public ClinicalStudy getClinicalStudy()
    {
        return clinicalStudy;
    }


    
    public void setClinicalStudy(ClinicalStudy clinicalStudy)
    {
        this.clinicalStudy = clinicalStudy;
    }


    /**
     * Returns message label to display on success add or edit
     * @return String
     */
    public String getMessageLabel() 
    {       
        if (SearchUtil.isNullobject(clinicalStudy))
        {
            clinicalStudy = new ClinicalStudy();
        }

        StringBuffer message = new StringBuffer();
        message.append(this.clinicalStudy.getTitle());
        message.append(" ");
        
        if (this.participant != null) {
            if (this.participant != null) {
            	 message.append(edu.wustl.clinportal.util.global.Utility.getLabel(participant.getLastName(), participant.getFirstName()));
            } 
        }   
        else if (this.getClinicalStudyParticipantIdentifier()!= null)
        {
           message.append(this.getClinicalStudyParticipantIdentifier());
        }  
        
        return message.toString();
    }


    /**
     * Returns collection of clinicalData of this clinicalStudy.
     * @return collection of clinicalData of this clinicalStudy.
     * @hibernate.set name="eventEntryCollection" table="CATISSUE_EVENT_ENTRY"
     * inverse="true" cascade="all" lazy="true"
     * @hibernate.collection-key column="CLINICAL_STUDY_REG_ID"
     * @hibernate.collection-one-to-many class="edu.wustl.clinportal.domain.EventEntry"
     * 
     */
    public Collection getEventEntryCollection()
    {
        return eventEntryCollection;
    }

    public void setEventEntryCollection(Collection eventEntryCollection)
    {
        this.eventEntryCollection = eventEntryCollection;
    }
    
}