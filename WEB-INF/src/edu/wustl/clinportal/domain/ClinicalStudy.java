/**
 *@author shital lawhale
 *@version 1.0
 */

package edu.wustl.clinportal.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import edu.wustl.clinportal.actionForm.ClinicalStudyForm;
import edu.wustl.clinportal.bean.ConsentBean;
import edu.wustl.clinportal.compare.util.StudyEventComparator;
import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.actionForm.IValueObject;
import edu.wustl.common.bizlogic.IActivityStatus;
import edu.wustl.common.util.KeyComparator;
import edu.wustl.common.util.MapDataParser;
import edu.wustl.common.util.logger.Logger;

/**
 * A set of written procedures that describe how a biospecimen is prospectively collected.
 * @hibernate.joined-subclass table="CATISSUE_CLINICAL_STUDY"
 * @hibernate.joined-subclass-key column="IDENTIFIER" 
 * 
 */
public class ClinicalStudy extends SpecimenProtocol
		implements
			java.io.Serializable,
			IActivityStatus
{

	private static final long serialVersionUID = 3426153460514363640L;

	/**
	 * 
	 */
	public ClinicalStudy()
	{
		// constructor
	}

	/**
	 * @param form
	 */
	public ClinicalStudy(AbstractActionForm form)
	{
		setAllValues(form);
	}

	/**
	 * Collection of users associated with the CollectionProtocol.
	 */
	protected Collection coordinatorCollection = new HashSet();
	/**
	 * 
	 */
	protected Collection clinicalStudyEventCollection = new HashSet();

	/**
	 * The collection of consent tiers associated with the clinical study.
	 */
	protected Collection consentTierCollection;
	/**
	 * The unsigned document URL for the clinical study.
	 */
	protected String unsignedConsentDocumentURL;

	private Long relCPId;

	/**
	 * 
	 */
	protected Collection clinicalStudyRegistrationCollection = new HashSet();

	/**
	 * @return the unsignedConsentDocumentURL
	 * @hibernate.property name="unsignedConsentDocumentURL" type="string" length="1000" column="UNSIGNED_CONSENT_DOC_URL"
	 */
	public String getUnsignedConsentDocumentURL()
	{
		return unsignedConsentDocumentURL;
	}

	/**
	 * @param unsignedConsentDocumentURL the unsignedConsentDocumentURL to set
	 */
	public void setUnsignedConsentDocumentURL(String unsignedConsentDocumentURL)
	{
		this.unsignedConsentDocumentURL = unsignedConsentDocumentURL;
	}

	/**
	 * @return the consentTierCollection
	 * @hibernate.collection-one-to-many class="edu.wustl.clinportal.domain.ClinicalStudyConsentTier" cascade="save-update" lazy="true"
	 * @hibernate.set table="CLINPORT_CONSENT_TIER" inverse="false" name="consentTierCollection"
	 * @hibernate.collection-key column="COLL_PROTOCOL_ID"
	 */
	public Collection getConsentTierCollection()
	{
		return consentTierCollection;
	}

	/**
	 * @param consentTierCollection the consentTierCollection to set
	 */
	public void setConsentTierCollection(Collection consentTierCollection)
	{
		this.consentTierCollection = consentTierCollection;
	}

	//-----Consent Tracking End

	/**
	  * @return the clinicalStudyEventCollection
	  * @hibernate.set name="clinicalStudyEventCollection" table="CATISSUE_CLINICAL_STUDY_EVENT"
	  * inverse="true" cascade="save-update" lazy="true"
	  * @hibernate.collection-key column="CLINICAL_STUDY_ID"
	  * @hibernate.collection-one-to-many class="edu.wustl.clinportal.domain.ClinicalStudyEvent"
	  */
	public Collection getClinicalStudyEventCollection()
	{
		return clinicalStudyEventCollection;
	}

	/**
	 * @param clinicalStudyEventCollection the clinicalStudyEventCollection to set
	 */
	public void setClinicalStudyEventCollection(Collection clinicalStudyEventCollection)
	{
		this.clinicalStudyEventCollection = clinicalStudyEventCollection;
	}

	/**
	 * Returns the collection of Users(ProtocolCoordinators) for this ClinicalStudy.
	 * @hibernate.set name="userCollection" table="CATISSUE_STUDY_COORDINATORS" 
	 * cascade="none" inverse="false" lazy="true"
	 * @hibernate.collection-key column="CLINICAL_STUDY_ID"
	 * @hibernate.collection-many-to-many class="edu.wustl.clinportal.domain.User" column="USER_ID"
	 * @return The collection of Users.
	 */
	public Collection getCoordinatorCollection()
	{
		return coordinatorCollection;
	}

	/**
	 * @param coordinatorCollection
	 */
	public void setCoordinatorCollection(Collection coordinatorCollection)
	{
		this.coordinatorCollection = coordinatorCollection;
	}

	/**
	 * Returns collection of clinicalStudy registrations of this clinicalStudy.
	 * @return collection of clinicalStudy registrations of this clinicalStudy.
	 * @hibernate.set name="clinicalStudyRegistrationCollection" table="CATISSUE_CLINICAL_STUDY_REG"
	 * inverse="true" cascade="save-update"
	 * @hibernate.collection-key column="CLINICAL_STUDY_ID"
	 * @hibernate.collection-one-to-many class="edu.wustl.clinportal.domain.ClinicalStudyRegistration"
	 * @see setCollectionProtocolRegistrationCollection(Collection)
	 */
	public Collection getClinicalStudyRegistrationCollection()
	{
		return clinicalStudyRegistrationCollection;
	}

	/**
	 * Sets the collection protocol registrations of this participant.
	 * @param protocolRegistrationCollection collection of collection protocol registrations of this participant.
	 * @see #getCollectionProtocolRegistrationCollection()
	 */
	public void setClinicalStudyRegistrationCollection(
			Collection clinicalStudyRegistrationCollection)
	{
		this.clinicalStudyRegistrationCollection = clinicalStudyRegistrationCollection;
	}

	/**
	 * Returns message label to display on success add or edit
	 * @return String
	 */
	public String getMessageLabel()
	{
		return this.title;
	}

	/* (non-Javadoc)
	 * @see edu.wustl.clinportal.domain.SpecimenProtocol#setAllValues(edu.wustl.common.actionForm.IValueObject)
	 */
	@Override
	public void setAllValues(IValueObject abstractForm)
	{

		try
		{
			super.setAllValues(abstractForm);

			ClinicalStudyForm csForm = (ClinicalStudyForm) abstractForm;

			coordinatorCollection = new HashSet();
			long[] coordinatorsArr = csForm.getProtocolCoordinatorIds();
			if (coordinatorsArr != null)
			{
				for (int i = 0; i < coordinatorsArr.length; i++)
				{
					if (coordinatorsArr[i] != -1)
					{
						User coordinator = new User();
						coordinator.setId(Long.valueOf(coordinatorsArr[i]));
						coordinatorCollection.add(coordinator);
					}
				}
			}
			Map map = csForm.getFormValues();
			Map sortedMap = sortMapOnKey(map);
			MapDataParser parser = new MapDataParser("edu.wustl.clinportal.domain");
			List eventList =(List) parser.generateData(sortedMap,true);
	        Collections.sort(eventList,new StudyEventComparator());
	        clinicalStudyEventCollection=new HashSet();
	        setCSEventColln(eventList);
			//Setting the unsigned doc url.
			this.unsignedConsentDocumentURL = csForm.getUnsignedConsentURLName();
			//Setting the consent tier collection.
			this.consentTierCollection = prepareConsentTierCollection(csForm.getConsentValues());
			if (csForm.getColprotocolId() != -1 && csForm.getIsRelatedtoCP())
			{
				this.relCPId = csForm.getColprotocolId();
			}
			else
			{
				this.relCPId = null;
			}
		}
		catch (Exception excp)
		{
			Logger.out.error(excp.getMessage(), excp);
		}
	}

	/**
	 * putting events objects from Array in collection in the same order as entered through UI
	 * @param eventList
	 */
	private void setCSEventColln(List<ClinicalStudyEvent> eventList)
    {
	    ListIterator<ClinicalStudyEvent> eventIterator= eventList.listIterator(eventList.size());
	    while(eventIterator.hasPrevious())
	    {
	        ClinicalStudyEvent clinicalStudyEvent  = eventIterator.previous();
	        List<StudyFormContext> studyFormList=new ArrayList(clinicalStudyEvent.getStudyFormContextCollection());            
            setStudyFormColln(clinicalStudyEvent,studyFormList);
            clinicalStudyEventCollection.add(clinicalStudyEvent);
	    }        
    }
    /**
     * putting studyFormContext objects from Array in collection in the same order as entered through UI
     * @param clinicalStudyEvent
     * @param studyFormList
     */
    private void setStudyFormColln(ClinicalStudyEvent clinicalStudyEvent,
            List<StudyFormContext> studyFormList)
    {
        clinicalStudyEvent.setStudyFormContextCollection(new HashSet());
        for (StudyFormContext studyFormContext : studyFormList)
        {
            clinicalStudyEvent.getStudyFormContextCollection().add(studyFormContext);
        }        
    }

    /**
	 * @param relCPId
	 */
	public void setRelCPId(Long relCPId)
	{
		this.relCPId = relCPId;
	}

	/**
	 * @return
	 */
	public Long getRelCPId()
	{
		return relCPId;
	}

	/**
	 * @param consentTierMap Consent Tier Map
	 * @return consentStatementColl
	 * @throws Exception - Exception 
	 */
	public Collection prepareConsentTierCollection(Map consentTierMap) throws Exception
	{
		MapDataParser mapdataParser = new MapDataParser("edu.wustl.clinportal.bean");
		Collection beanObjColl = mapdataParser.generateData(consentTierMap);

		Collection<ClinicalStudyConsentTier> consentStatementColl = new HashSet<ClinicalStudyConsentTier>();
		Iterator iter = beanObjColl.iterator();
		while (iter.hasNext())
		{
			ConsentBean consentBean = (ConsentBean) iter.next();
			addConsentTier(consentBean, consentStatementColl);
		}
		return consentStatementColl;
	}

	/**
	 * @param consentBean
	 * @param consentStatementColl
	 */
	private void addConsentTier(ConsentBean consentBean,
			Collection<ClinicalStudyConsentTier> consentStatementColl)
	{
		ClinicalStudyConsentTier consentTier = new ClinicalStudyConsentTier();
		consentTier.setStatement(consentBean.getStatement());
		//To set ID for Edit case
		if (consentBean.getConsentTierID() != null
				&& consentBean.getConsentTierID().trim().length() > 0)
		{
			consentTier.setId(Long.parseLong(consentBean.getConsentTierID()));
		}
		//Check for empty consents
		if (consentBean.getStatement() != null && consentBean.getStatement().trim().length() > 0)
		{
			consentStatementColl.add(consentTier);
		}
	}

	/**
	 * 
	 * @param map
	 * @return
	 */
	private LinkedHashMap sortMapOnKey(Map map)
	{
		Object[] mapKeySet = map.keySet().toArray();
		int size = mapKeySet.length;
		ArrayList<String> mList = new ArrayList<String>();
		for (int i = 0; i < size; i++)
		{
			String key = (String) mapKeySet[i];
			mList.add(key);
		}

		KeyComparator keyComparator = new KeyComparator();
		Collections.sort(mList, keyComparator);

		LinkedHashMap<String, String> sortedMap = new LinkedHashMap<String, String>();
		for (int i = 0; i < size; i++)
		{
			String key = (String) mList.get(i);
			String value = (String) map.get(key);
			sortedMap.put(key, value);
		}
		return sortedMap;
	}

}