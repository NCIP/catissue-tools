/**
 * <p>Title: CollectionProtocolRegistration Class>
 * <p>Description:  A registration of a Participant to a Collection Protocol.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Shital Lawhale
 * @version 1.00
 */

package edu.wustl.clinportal.actionForm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import edu.wustl.clinportal.domain.ClinicalStudyRegistration;
import edu.wustl.clinportal.domain.User;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.Status;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.logger.Logger;

/**
 * @author falguni_sachde
 *
 */
public class ClinicalStudyRegistrationForm extends AbstractActionForm
{

	/**
	 * Specifies unique identifier.
	 * */
	private long id;

	/**
	 * Activity Status.
	 */
	private String activityStatus = Status.ACTIVITY_STATUS_ACTIVE.getStatus();

	/**
	 * System generated unique collection protocol Identifier
	 */
	private long clinicalStudyId;

	/**
	 * System generated unique participant Identifier
	 */
	private long participantID;

	/**
	 * System generated unique participant protocol Identifier.
	 */
	protected String participantName = "";

	/**
	 * System generated unique participant protocol Identifier.
	 */
	protected String participantClinicalStudyID = "";

	/**
	 * Date on which the Participant is registered to the Collection Protocol.
	 */
	protected String registrationDate;

	/**
	 * Represents the weather participant Name is selected or not.
	 */
	protected boolean checkedButton;

	/**
	 * Map for Storing responses for Consent Tiers.
	 */
	protected Map consentResponseValues = new HashMap();
	/**
	 * No of Consent Tier
	 */
	private int consentTierCounter = 0;

	/**
	 * Signed Consent URL
	 */
	protected String signedConsentUrl = "";
	/**
	 * Witness name that may be PI
	 */
	protected long witnessId;

	/**
	 * Consent Date, Date on which Consent is Signed
	 */
	protected String consentDate = "";
	/**
	 * This will be set in case of withdraw popup
	 */
	protected String withdrawlButtonStatus = Constants.WITHDRAW_RESPONSE_NOACTION;
	/**
	 * offset
	 */
	protected int offset = 0;

	/**
	 * It will set all values of member variables from Domain Object
	 * @param CollectionProtocolRegistration domain object
	 */
	public void setAllValues(AbstractDomainObject abstractDomain)
	{
		ClinicalStudyRegistration registration = (ClinicalStudyRegistration) abstractDomain;
		this.id = registration.getId().longValue();
		this.activityStatus = registration.getActivityStatus();
		this.clinicalStudyId = registration.getClinicalStudy().getId().longValue();

		if (registration.getParticipant() != null)
		{
			setParticipantDetails(registration);
		}

		this.participantClinicalStudyID = edu.wustl.common.util.Utility.toString(registration
				.getClinicalStudyParticipantIdentifier());

		this.registrationDate = edu.wustl.common.util.Utility.parseDateToString(registration
				.getRegistrationDate(), Constants.DATE_PATTERN_MM_DD_YYYY);

		/**
		 * For Consent tracking setting UI attributes
		 */
		User witness = registration.getConsentWitness();
		if (witness != null)
		{
			this.witnessId = witness.getId();
		}
		this.signedConsentUrl = edu.wustl.common.util.Utility.toString(registration
				.getSignedConsentDocumentURL());
		this.consentDate = edu.wustl.common.util.Utility.parseDateToString(registration
				.getConsentSignatureDate(), Constants.DATE_PATTERN_MM_DD_YYYY);

	}

	/**
	 * 
	 * @param registration
	 */
	private void setParticipantDetails(ClinicalStudyRegistration registration)
	{
		String firstName = edu.wustl.common.util.Utility.toString(registration.getParticipant()
				.getFirstName());
		String lastName = edu.wustl.common.util.Utility.toString(registration.getParticipant()
				.getLastName());
		String birthDate = edu.wustl.common.util.Utility.toString(registration.getParticipant()
				.getBirthDate());
		String ssn = edu.wustl.common.util.Utility.toString(registration.getParticipant()
				.getSocialSecurityNumber());
		if ((firstName.trim().length() > 0 || lastName.trim().length() > 0
				|| birthDate.trim().length() > 0 || ssn.trim().length() > 0))
		{
			this.participantID = registration.getParticipant().getId().longValue();
			this.participantName = registration.getParticipant().getMessageLabel();

		}

	}

	/**
	* @return Returns the id assigned to form bean
	*/
	public int getFormId()
	{
		return Constants.CLINICAL_STUDY_REGISTRATION_FORM_ID;
	}

	/**
	 * Returns unique identifier.
	 * @return unique identifier.
	 * @see #setId(long)
	 * */
	public long getId()
	{
		return id;
	}

	/**
	 * Sets unique identifier.
	 * @param identifier unique identifier.
	 * @see #getId()
	 * */
	public void setId(long identifier)
	{
		if (isMutable())
		{
			this.id = identifier;
		}
	}

	/**
	 * This method gets the activity status.
	 * @return the activity Status.
	 * @see #setActivityStatus(String)
	 */
	public String getActivityStatus()
	{
		return activityStatus;
	}

	/**
	 * This method sets the activity status.
	 * @param activityStatus activity status.
	 * @see #getActivityStatus()
	 */
	public void setActivityStatus(String activityStatus)
	{
		this.activityStatus = activityStatus;
	}

	/**
	 * Returns the date on which the Participant is 
	 * registered to the Collection Protocol.
	 * @return the date on which the Participant is 
	 * registered to the Collection Protocol.
	 * @see #setRegistrationDate(String)
	 */
	public String getRegistrationDate()
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
	public void setRegistrationDate(String registrationDate)
	{
		this.registrationDate = registrationDate;
	}

	/**
	 * @return Returns unique participant ID 
	 */
	public long getParticipantID()
	{
		return participantID;
	}

	/**
	 * @param participantID sets unique participant ID 
	 */
	public void setParticipantID(long participantID)
	{
		this.participantID = participantID;
	}

	/**
	* @return returns participant Protocol ID
	*/
	public String getParticipantClinicalStudyID()
	{
		return participantClinicalStudyID;
	}

	/**
	 * @param participantProtocolID sets participant protocol ID
	*/
	public void setParticipantClinicalStudyID(String ppantClStudyID)
	{
		this.participantClinicalStudyID = ppantClStudyID;
	}

	/**
	 * @return consentDate The Date on Which Consent is Signed
	 */
	public String getConsentDate()
	{
		return consentDate;
	}

	/**
	 * @param consentDate The Date on Which Consent is Signed
	 */
	public void setConsentDate(String consentDate)
	{
		this.consentDate = consentDate;
	}

	/**
	 * @return signedConsentUrl The reference to the electric signed document(eg PDF file)
	 */
	public String getSignedConsentUrl()
	{
		return signedConsentUrl;
	}

	/**
	 * @param signedConsentUrl The reference to the electric signed document(eg PDF file)
	 */
	public void setSignedConsentUrl(String signedConsentUrl)
	{
		this.signedConsentUrl = signedConsentUrl;
	}

	/**
	 * @return witnessId The name of the witness to the consent Signature(PI or coordinator of the Collection Protocol)
	 */
	public long getWitnessId()
	{
		return witnessId;
	}

	/**
	 * @param witnessId The name of the witness to the consent Signature(PI or coordinator of the Collection Protocol)
	 */
	public void setWitnessId(long witnessId)
	{
		this.witnessId = witnessId;
	}

	/**
	 * @param key Key prepared for saving data.
	 * @param value Values corresponding to key
	 */
	public void setConsentResponseValue(String key, Object value)
	{
		if (isMutable())
		{
			consentResponseValues.put(key, value);
		}
	}

	/**
	 * 
	 * @param key Key prepared for saving data.
	 * @return consentResponseValues
	 */
	public Object getConsentResponseValue(String key)
	{
		return consentResponseValues.get(key);
	}

	/**
	 * @return values in map consentResponseValues
	 */
	public Collection getAllConsentResponseValue()
	{
		return consentResponseValues.values();
	}

	/**
	 * @return consentResponseValues The reference to the participant Response at CollectionprotocolReg Level
	 */
	public Map getConsentResponseValues()
	{
		return consentResponseValues;
	}

	/**
	 * @param csentRespseVals The reference to the participant Response at CollectionprotocolReg Level
	 */
	public void setConsentResponseValues(Map csentRespseVals)
	{
		this.consentResponseValues = csentRespseVals;
	}

	/**
	 *@return consentTierCounter  This will keep track of count of Consent Tier
	 */
	public int getConsentTierCounter()
	{
		return consentTierCounter;
	}

	/**
	 *@param csentTrCnter  This will keep track of count of Consent Tier
	 */
	public void setConsentTierCounter(int csentTrCnter)
	{
		this.consentTierCounter = csentTrCnter;
	}

	/**
	 * It returns status of button(return,discard,reset)
	 * @return withdrawlButtonStatus
	 */
	public String getWithdrawlButtonStatus()
	{
		return withdrawlButtonStatus;
	}

	/**
	 * It returns status of button(return,discard,reset)
	 * @param wdrawlButnSts return,discard,reset
	 */
	public void setWithdrawlButtonStatus(String wdrawlButnSts)
	{
		this.withdrawlButtonStatus = wdrawlButnSts;
	}

	/**
	 * This function creates Array of String of keys and add them into the consentTiersList.
	 * @return consentTiersList
	 */
	public Collection getConsentTiers()
	{
		Collection consentTiersList = new ArrayList();
		String responseValue = "consentResponseValue(ConsentBean:";
		int noOfConsents = this.getConsentTierCounter();
		for (int counter = 0; counter < noOfConsents; counter++)
		{
			String[] strArray = new String[4]; // NOPMD - Array instantiation in loop
			strArray[0] = responseValue + counter + "_consentTierID)";
			strArray[1] = responseValue + counter + "_statement)";
			strArray[2] = responseValue + counter + "_participantResponse)";
			strArray[3] = responseValue + counter + "_participantResponseID)";

			consentTiersList.add(strArray);
		}
		return consentTiersList;
	}

	/**
	 * This function returns the format of the response Key prepared. 
	 * @return consentResponseValue(ConsentBean:`_participantResponse)
	 */
	public String getConsentTierMap()
	{
		return "consentResponseValue(ConsentBean:`_participantResponse)";
	}

	/**
	* Overrides the validate method of ActionForm.
	* @param mapping
	* @param request
	* @return errors
	*/
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request)
	{

		ActionErrors errors = new ActionErrors();
		Validator validator = new Validator();
		try
		{
			setRedirectValue(validator);
			//check if Protocol Title is empty.
			if (clinicalStudyId == -1)
			{
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.required",
						ApplicationProperties.getValue("clinicalStudyReg.protocolTitle")));
			}
			validateParticipantDetails(validator, errors);
			String errorKey = validator.validateDate(registrationDate, true);
			if (errorKey.trim().length() > 0)
			{
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(errorKey,
						ApplicationProperties.getValue("collectionprotocolregistration.date")));
			}
			if (!validator.isValidOption(activityStatus))
			{
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.required",
						ApplicationProperties.getValue("clinicalStudyReg.activityStatus")));
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
	 * @param validator
	 * @param errors
	 */
	private void validateParticipantDetails(Validator validator, ActionErrors errors)
	{
		if (validator.isEmpty(participantClinicalStudyID)
				&& (participantID == -1 || participantID == 0))
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
					"errors.clinicalstudyregistration.atleast"));
		}
	}

	/**
	* Resets the values of all the fields.
	* Is called by the overridden reset method defined in ActionForm.  
	* */
	protected void reset()
	{
		//reset
	}

	/**
	 * This method sets Identifier of Objects inserted by AddNew activity in Form-Bean which initialized AddNew action
	 * @param formBeanId - FormBean ID of the object inserted
	 *  @param addObjId - Identifier of the Object inserted 
	 */
	public void setAddNewObjectIdentifier(String addNewFor, Long addObjId)
	{
		if ("clinicalStudyId".equals(addNewFor))
		{
			setClinicalStudyId(addObjId.longValue());
		}
		else if ("participantId".equals(addNewFor))
		{
			setParticipantID(addObjId.longValue());
		}
	}

	/**
	 * @return
	 */
	public String getParticipantName()
	{
		return participantName;
	}

	/**
	 * @param participantName
	 */
	public void setParticipantName(String participantName)
	{
		this.participantName = participantName;
	}

	/**
	 * @return
	 */
	public long getClinicalStudyId()
	{
		return clinicalStudyId;
	}

	/**
	 * @param clinicalStudyId
	 */
	public void setClinicalStudyId(long clinicalStudyId)
	{
		this.clinicalStudyId = clinicalStudyId;
	}

}