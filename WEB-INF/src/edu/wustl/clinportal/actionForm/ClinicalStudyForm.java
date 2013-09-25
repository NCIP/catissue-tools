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
* <p>Title: ClinicalStudyForm Class>
* <p>Description:  ClinicalStudyForm Class is used to encapsulate all the request parameters passed 
* from User Add/Edit webpage. </p>
* Copyright:    Copyright (c) year
* Company: Washington University, School of Medicine, St. Louis. *
* @author Shital lawhale 
*/

package edu.wustl.clinportal.actionForm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import edu.wustl.clinportal.compare.util.StudyEventComparator;
import edu.wustl.clinportal.domain.ClinicalStudy;
import edu.wustl.clinportal.domain.ClinicalStudyConsentTier;
import edu.wustl.clinportal.domain.ClinicalStudyEvent;
import edu.wustl.clinportal.domain.StudyFormContext;
import edu.wustl.clinportal.domain.User;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.clinportal.util.global.Utility;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.logger.Logger;

/**
 * CollectionProtocolForm Class is used to encapsulate all the request
 * parameters passed from collection protocol Add/Edit page.
 * 
 * @author falguni_sachde
 */
public class ClinicalStudyForm extends SpecimenProtocolForm
{

	private static final long serialVersionUID = 1L;

	protected long[] protocolCoordinatorIds;

	protected long colprotocolId;

	/**
	 * 
	 */
	public ClinicalStudyForm()
	{
		super();
	}

	/**
	 * Counter that contains number of rows in the 'Add More' functionality. outer block
	 */
	private int outerCounter = 1;

	/**
	 * @return Returns the innerLoopValues.
	 * 
	 */
	protected Map innerLoopValues = new HashMap();

	/**
	 * Used another map for storing events and forms in order to avoid collition
	 * between map in SimpleQueryInterface and the ClinicalStudyForm
	 * 
	 */
	protected Map<String, Object> formValues = new HashMap<String, Object>();

	/**
	 * Unsigned Form Url for the Consents
	 */
	protected String unsignedConsentURLName;

	/**
	 * Map for Storing Values of Consent Tiers.
	 */
	protected Map consentValues = new HashMap();

	/**
	 * No of Consent Tier
	 */
	private int consentTierCounter = 0;

	/**
	 * is Related to CP
	 */
	private boolean isRelatedtoCP;

	/**
	 * @return
	 */
	public boolean getIsRelatedtoCP()
	{
		return isRelatedtoCP;
	}

	/**
	 * @param isRelatedtoCP
	 */
	public void setIsRelatedtoCP(boolean isRelatedtoCP)
	{
		this.isRelatedtoCP = isRelatedtoCP;
	}

	/**
	 * @return the innerLoopValues
	 */
	public Map getInnerLoopValues()
	{
		return innerLoopValues;
	}

	/**
	 * @param innerLoopValues the innerLoopValues to set
	 */
	public void setInnerLoopValues(Map innerLoopValues)
	{
		this.innerLoopValues = innerLoopValues;
	}

	/**
	 * @return the outerCounter
	 */
	public int getOuterCounter()
	{
		return outerCounter;
	}

	/**
	 * @param outerCounter the outerCounter to set
	 */
	public void setOuterCounter(int outerCounter)
	{
		this.outerCounter = outerCounter;
	}

	/**
	 * Associates the specified object with the specified key in the map.
	 * @param key the key to which the object is mapped.
	 * @param value the object which is mapped.
	 */
	public void setIvl(String key, Object value)///changes here
	{
		if (isMutable())
		{
			innerLoopValues.put(key, value);
		}
	}

	/**
	 * Returns the object to which this map maps the specified key.
	 * 
	 * @param key the required key.
	 * @return the object to which this map maps the specified key.
	 */
	public Object getIvl(String key)
	{
		return innerLoopValues.get(key);
	}

	/**
	 * for reset
	 */
	protected void reset()
	{
		//reset
	}

	/**
	 * @return Returns the protocol coordinator id.
	 */
	public long[] getProtocolCoordinatorIds()
	{
		return protocolCoordinatorIds;
	}

	/**
	 * @param protoCoordIds The protocolCoordinatorIds to set.
	 */
	public void setProtocolCoordinatorIds(long[] protoCoordIds)
	{
		this.protocolCoordinatorIds = protoCoordIds;
	}

	/**
	 * @return Returns the protocol coordinator id.
	 */
	public long getColprotocolId()
	{
		return colprotocolId;
	}

	/**
	 * @param protoCoordIds The protocolCoordinatorIds to set.
	 */
	public void setColprotocolId(long colprotocolId)
	{
		this.colprotocolId = colprotocolId;
	}

	/**
	 * Copies the data from an AbstractDomain object to a Clinicalstudy object.
	 * @param abstractDomain An AbstractDomain object.
	 */
	public void setAllValues(AbstractDomainObject abstractDomain)
	{
		super.setAllValues(abstractDomain);

		ClinicalStudy clinicalStudy = (ClinicalStudy) abstractDomain;
		Collection userCollection = clinicalStudy.getCoordinatorCollection();
		if (userCollection != null)
		{
			protocolCoordinatorIds = new long[userCollection.size()];
			int cnt = 0;
			Iterator iterator = userCollection.iterator();
			while (iterator.hasNext())
			{
				User user = (User) iterator.next();
				protocolCoordinatorIds[cnt] = user.getId().longValue();
				cnt++;
			}
		}
		if (clinicalStudy.getRelCPId() != null && clinicalStudy.getRelCPId() != null)
		{
			this.colprotocolId = clinicalStudy.getRelCPId().longValue();
			this.isRelatedtoCP = true;
		}
		else
		{
			this.colprotocolId = -1;
			this.isRelatedtoCP = false;
		}
		setId(abstractDomain.getId());
		populateEventMap(clinicalStudy);
		//For Consent Tracking 
		this.unsignedConsentURLName = clinicalStudy.getUnsignedConsentDocumentURL();
		this.consentValues = prepareConsentTierMap(clinicalStudy.getConsentTierCollection());
	}

	/**
	 * For Consent Tracking
	 * Setting the consentValuesMap 
	 * @param consentTierColl This Contains the collection of ConsentTier
	 * @return tempMap
	 */
	public Map prepareConsentTierMap(Collection consentTierColl)
	{
		Map tempMap = new HashMap();
		if (consentTierColl != null)
		{
			String consentBean = "ConsentBean:";
			Iterator consTierCollIter = consentTierColl.iterator();
			int idx = 1;
			while (consTierCollIter.hasNext())
			{
				ClinicalStudyConsentTier consent = (ClinicalStudyConsentTier) consTierCollIter
						.next();
				String statement = consentBean + idx + "_statement";
				String preDefStmtkey = consentBean + idx + "_predefinedConsents";
				String statementkey = consentBean + idx + "_consentTierID";
				tempMap.put(statement, consent.getStatement());
				tempMap.put(preDefStmtkey, consent.getStatement());
				tempMap.put(statementkey, consent.getId());
				idx++;
			}
			consentTierCounter = consentTierColl.size();
		}
		return tempMap;
	}

	/**
	 * 
	 * @param clinicalStudy
	 */
	private void populateEventMap(ClinicalStudy clinicalStudy)
	{
		ClinicalStudy cstudy = (ClinicalStudy) clinicalStudy;
		if (cstudy.getClinicalStudyEventCollection() != null)
		{
			List<ClinicalStudyEvent> eventList = new ArrayList<ClinicalStudyEvent>();
			eventList.addAll(cstudy.getClinicalStudyEventCollection());
			Collections.sort(eventList, new StudyEventComparator());
			Iterator evntIterate = eventList.iterator();
			int eventCounter = 1;
			outerCounter = 0;
			while (evntIterate.hasNext())
			{
				ClinicalStudyEvent event = (ClinicalStudyEvent) evntIterate.next();
				if (event.getActivityStatus() != null
						&& !event.getActivityStatus().equals(Constants.ACTIVITY_STATUS_DISABLED))
				{
					String labelKey = Utility.createKey(Constants.CLINICAL_STUDY_EVENT,
							eventCounter, 0, "collectionPointLabel");
					String timePoint = Utility.createKey(Constants.CLINICAL_STUDY_EVENT,
							eventCounter, 0, "studyCalendarEventPoint");
					String noOfEntriesKey = Utility.createKey(Constants.CLINICAL_STUDY_EVENT,
							eventCounter, 0, "noOfEntries");
					String relCPEventIdKey = Utility.createKey(Constants.CLINICAL_STUDY_EVENT,
							eventCounter, 0, "relToCPEId");
					formValues.put(labelKey, event.getCollectionPointLabel());
					formValues.put(timePoint, event.getStudyCalendarEventPoint().toString());
					if (event.getIsInfiniteEntry() != null
							&& event.getIsInfiniteEntry().booleanValue())
					{
						formValues.put(noOfEntriesKey, Constants.HASH);
					}
					else
					{
						formValues.put(noOfEntriesKey, event.getNoOfEntries().toString());
					}
					if (event.getRelToCPEId() != null)
					{
						formValues.put(relCPEventIdKey, event.getRelToCPEId().toString());
					}
					else
					{
						formValues.put(relCPEventIdKey, "-1");
					}
					/* 
					 * Forming the key for storing the id of Event and FormContext
					 */
					String eventIdKey = Utility.createKey(Constants.CLINICAL_STUDY_EVENT,
							eventCounter, 0, "id");
					formValues.put(eventIdKey, event.getId());
					populateFormMap(event, eventCounter);
					eventCounter++;
					outerCounter++;
				}
			}
			if (outerCounter == 0)
			{
				outerCounter = 1;
			}

		}

	}

	/**
	 * 
	 * @param event
	 * @param eventCounter
	 */
	private void populateFormMap(ClinicalStudyEvent event, int eventCounter)
	{
		if (event.getStudyFormContextCollection() != null)
		{
			List formList = new ArrayList();
			formList.addAll(event.getStudyFormContextCollection());
			Collections.sort(formList);
			Iterator studyFrmIterate = formList.iterator();
			int formCounter = 0;
			while (studyFrmIterate.hasNext())
			{
				StudyFormContext frmContext = (StudyFormContext) studyFrmIterate.next();
				if (frmContext.getActivityStatus() != null
						&& !frmContext.getActivityStatus().equals(
								Constants.ACTIVITY_STATUS_DISABLED))
				{
					formCounter++;
					String formIdKey = Utility.createKey(Constants.FORM_CONTEXT, eventCounter,
							formCounter, "containerId");
					String formLabelKey = Utility.createKey(Constants.FORM_CONTEXT, eventCounter,
							formCounter, "studyFormLabel");
					String canHaveMulRecsKey = Utility.createKey(Constants.FORM_CONTEXT,
							eventCounter, formCounter, "canHaveMultipleRecords");

					String formContextIdkey = Utility.createKey(Constants.FORM_CONTEXT,
							eventCounter, formCounter, "id");
					formValues.put(formIdKey, frmContext.getContainerId().toString());
					formValues.put(formLabelKey, frmContext.getStudyFormLabel());

					formValues.put(canHaveMulRecsKey, frmContext.getCanHaveMultipleRecords());

					formValues.put(formContextIdkey, frmContext.getId());

				}

			}

			int innerCounter = getInnerCounter(formCounter);

			String innerCounterKey = String.valueOf(eventCounter);
			innerLoopValues.put(innerCounterKey, String.valueOf(innerCounter));
		}

	}

	/**
	 * @param formCounter
	 * @return
	 */
	private int getInnerCounter(int formCounter)
	{
		int innerCounter = formCounter;
		if (innerCounter == 0)
		{
			innerCounter = 1;
		}
		return innerCounter;
	}

	/**
	 * 
	 * Overrides the validate method of ActionForm.
	 * @param mapping ActionMapping
	 * @param request HttpServletRequest
	 * @return errors
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request)
	{
		ActionErrors errors = super.validate(mapping, request);
		Validator validator = new Validator();
		try
		{

			validateCSCoordinators(errors);

			if (validator.isEmpty(this.irbID))
			{
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.required",
						ApplicationProperties.getValue("clinicalstudy.irbid")));
			}

			String startDate = this.startDate;

			if (!validator.isEmpty(startDate))
			{
				String errorKey = validator.validateDate(startDate, true);
				if (errorKey.trim().length() > 0)
				{
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item",
							ApplicationProperties.getValue("errors.invalid.date", "Start date")));
				}
			}
			// check clinical study events data
			validateClinicalStudyEvents(validator, errors);
			// check consent data
			validateConsents(errors);
			validateRelToCP(errors);
		}
		catch (Exception excp)
		{
			// use of logger as per bug 79
			Logger.out.error(excp.getMessage(), excp);
			Logger.out.debug(excp);
			errors = new ActionErrors();
		}
		return errors;
	}

	/**
	 * @param errors
	 */
	private void validateRelToCP(ActionErrors errors)
	{
		if (this.isRelatedtoCP && this.colprotocolId == -1)
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("cp.select"));
		}

	}

	/**
	 * @param errors
	 */
	private void validateCSCoordinators(ActionErrors errors)
	{
		if (this.protocolCoordinatorIds != null && this.principalInvestigatorId != -1)
		{
			for (int ind = 0; ind < protocolCoordinatorIds.length; ind++)
			{
				if (protocolCoordinatorIds[ind] == this.principalInvestigatorId)
				{
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError( //NOPMD - ActionError instantiation
							"errors.pi.coordinator.same"));
					break;
				}
			}
		}
	}

	/**
	 * Validate consent data
	 * @param errors
	 */
	private void validateConsents(ActionErrors errors)
	{
		//check whether previous consent has been deleted or not. 
		//the consent which are deleted ,their id is still present in consentValues map but 
		//ConsentBean:" + consentno + "_statement is null for it.So first create list of this type of consent
		//then deleted it 
		//For newly added consent ,the "ConsentBean:" + consentno + "_consentTierID" is null
		//but statement is present in consentValues map ,so validate that for this type of consent statement cannotbe empty
		if (unsignedConsentURLName != null && unsignedConsentURLName.length() > 500)
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError( //NOPMD - ActionError instantiation
					"errors.consent.unsignedConsentURLName.overSize"));
		}
		ArrayList<String> keyToBeDeleted = new ArrayList<String>();
		if (!consentValues.isEmpty())
		{
			Iterator<String> itr = consentValues.keySet().iterator();
			while (itr.hasNext())
			{
				String keyName = itr.next();
				if (keyName.contains("_statement"))
				{
					String statement = (String) consentValues.get(keyName);
					if (statement != null && statement.trim().length() == 0)
					{
						errors.add(ActionErrors.GLOBAL_ERROR, new ActionError( //NOPMD - ActionError instantiation
								"errors.consent.statement.empty"));
						break;
					}
					if (statement != null && statement.trim().length() > 500)
					{
						errors.add(ActionErrors.GLOBAL_ERROR, new ActionError( //NOPMD - ActionError instantiation
								"errors.consent.statement.overSize"));
						break;
					}

				}
				else if (keyName.contains("_consentTierID"))
				{
					String consentno = keyName.substring(12, 13);
					String statement = "ConsentBean:" + consentno + "_statement";
					if (consentValues.get(statement) == null)
					{
						keyToBeDeleted.add(keyName);
					}

				}
			}
			//remove keys for consent statement which are already deleted by delete button ,so that it will
			//not get saved through default  logic 			
			for (String key : keyToBeDeleted)
			{
				consentValues.remove(key);
			}

		}
	}

	/**
	 * Validate Clinical Study events data
	 * @param validator
	 * @param errors
	 */
	private void validateClinicalStudyEvents(Validator validator, ActionErrors errors)
	{
		Iterator iter = this.formValues.keySet().iterator();

		if (formValues.isEmpty())
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.one.item.required",
					ApplicationProperties.getValue("clinicalStudyEvent.eventTitle")));
		}

		while (iter.hasNext())
		{
			String key = (String) iter.next();
			String value = (String) formValues.get(key);

			validateCollectionPointLabel(validator, errors, key, value);
			validateStudyCalendarEventPoint(validator, errors, key, value);
			validateStudyFormContext(errors, key, value);

			if (key.indexOf("noOfEntries") != -1)
			{
				validateNoOfEntries(validator, errors, key, value);
			}
		}
	}

	/**
	 * @param validator
	 * @param errors
	 * @param key
	 * @param value
	 */
	private void validateCollectionPointLabel(Validator validator, ActionErrors errors, String key,
			String value)
	{
		if (key.indexOf("collectionPointLabel") != -1 && validator.isEmpty(value))
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.required",
					ApplicationProperties.getValue("clinicalStudyEvent.label")));
		}
	}

	/**
	 * @param validator
	 * @param errors
	 * @param key
	 * @param value
	 */
	private void validateStudyCalendarEventPoint(Validator validator, ActionErrors errors,
			String key, String value)
	{
		if (key.indexOf("studyCalendarEventPoint") != -1
				&& (!(validator.isNumeric(value)) || validator.isEmpty(value)))
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.required",
					ApplicationProperties.getValue("clinicalStudyEvent.studycalendartitle")));
		}
	}

	/**
	 * @param errors
	 * @param key
	 * @param value
	 */
	private void validateStudyFormContext(ActionErrors errors, String key, String value)
	{
		if (key != null && key.contains("StudyFormContext") && key.contains("containerId")
				&& value.equals("-1"))
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item",
					ApplicationProperties.getValue("clinicalStudyEvent.select.valid.study.form")));
		}
	}

	/**
	 * @param validator
	 * @param errors
	 * @param key
	 * @param value
	 */
	private void validateNoOfEntries(Validator validator, ActionErrors errors, String key,
			String value)
	{
		boolean isError = checkNoOfEntriesRequired(validator, errors, value);
		if (!isError)
		{
			if ("#".equals(value))
			{
				formValues.put(key, "-1");
			}
			else if ("0".equals(value))
			{
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.cannot.be.zero",
						ApplicationProperties.getValue("clinicalStudyEvent.noOfEntries.error")));
			}

		}
	}

	/**
	 * @param validator
	 * @param errors
	 * @param value
	 * @return
	 */
	private boolean checkNoOfEntriesRequired(Validator validator, ActionErrors errors, String value)
	{
		boolean returnVal = false; //NOPMD - DD-anomaly 
		if (validator.isEmpty(value) || !("#".equals(value) || validator.isNumeric(value)))
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.required",
					ApplicationProperties.getValue("clinicalStudyEvent.noOfEntries.error")));
			returnVal = true;
		}

		return returnVal;
	}

	/**
	 *@return Returns the id assigned to form bean
	 */
	public int getFormId()
	{
		return Constants.CLINICALSTUDY_FORM_ID;
	}

	/**
	 * This method sets Identifier of Objects inserted by AddNew activity in Form-Bean which initialized AddNew action
	 * @param addNewFor - FormBean ID of the object inserted
	 * @param addObjId - Identifier of the Object inserted 
	 */
	public void setAddNewObjectIdentifier(String addNewFor, Long addObjId)
	{
		if ("principalInvestigator".equals(addNewFor))
		{
			setPrincipalInvestigatorId(addObjId.longValue());
		}
		else if ("protocolCoordinator".equals(addNewFor))
		{
			long[] pcoordIDs = {Long.parseLong(addObjId.toString())};
			if (this.protocolCoordinatorIds != null && this.protocolCoordinatorIds.length > 0)
			{
				long[] temp = new long[pcoordIDs.length + protocolCoordinatorIds.length];

				System.arraycopy(protocolCoordinatorIds, 0, temp, 0, protocolCoordinatorIds.length);
				System.arraycopy(pcoordIDs, 0, temp, protocolCoordinatorIds.length,
						pcoordIDs.length);

				setProtocolCoordinatorIds(temp);
			}
			else
			{
				setProtocolCoordinatorIds(pcoordIDs);
			}
		}
	}

	/**
	 * @return the formValues
	 */
	public Map<String, Object> getFormValues()
	{
		return formValues;
	}

	/**
	 * @param formValues the formValues to set
	 */
	public void setFormValues(Map<String, Object> formValues)
	{
		this.formValues = formValues;
	}

	/**
	 * Associates the specified object with the specified key in the map.
	 * @param key the key to which the object is mapped.
	 * @param value the object which is mapped.
	 */
	public void setFormValue(String key, Object value)
	{
		if (isMutable())
		{
			formValues.put(key, value);
		}
	}

	/**
	 * Returns the object to which this map maps the specified key.
	 * 
	 * @param key
	 *            the required key.
	 * @return the object to which this map maps the specified key.
	 */
	public Object getFormValue(String key)
	{
		return formValues.get(key);
	}

	/**
	 * @return unsignedConsentURLName  Get Unsigned Signed URL name  
	 */
	public String getUnsignedConsentURLName()
	{
		return unsignedConsentURLName;
	}

	/**
	 * @param usigdCsentURLName  Set Unsigned Signed URL name
	 */
	public void setUnsignedConsentURLName(String usigdCsentURLName)
	{
		this.unsignedConsentURLName = usigdCsentURLName;
	}

	/**
	 * @param key Key
	 * @param value Value
	 */
	public void setConsentValue(String key, Object value)
	{
		if (isMutable())
		{
			consentValues.put(key, value);
		}
	}

	/**
	 * @param key Key
	 * @return Statements
	 */
	public Object getConsentValue(String key)
	{
		return consentValues.get(key);
	}

	/**
	 * 
	 * @return consentValues   Set Consents into the Map
	 */
	public Map getConsentValues()
	{
		return consentValues;
	}

	/**
	 * @param consentValues Set Consents into the Map
	 */
	public void setConsentValues(Map consentValues)
	{
		this.consentValues = consentValues;
	}

	/**
	 *@return consentTierCounter  This will keep track of count of Consent Tier
	 */
	public int getConsentTierCounter()
	{
		return consentTierCounter;
	}

	/**
	 * 
	 * @param csentTierCnter  This will keep track of count of Consent Tier
	 */
	public void setConsentTierCounter(int csentTierCnter)
	{
		this.consentTierCounter = csentTierCnter;
	}

}