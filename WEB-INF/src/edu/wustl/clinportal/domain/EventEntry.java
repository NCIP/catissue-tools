/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */


package edu.wustl.clinportal.domain;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import edu.wustl.common.actionForm.IValueObject;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.AssignDataException;

/**
 * 
 *
 *@hibernate.class table="CATISSUE_EVENT_ENTRY"
 */
/**
 * @author falguni_sachde
 *
 */
public class EventEntry extends AbstractDomainObject
{

	protected Long id;
	protected Date encounterDate;
	protected String activityStatus;
	protected ClinicalStudyEvent clinicalStudyEvent;
	protected ClinicalStudyRegistration clinicalStudyRegistration;
	protected Collection recordEntryCollection = new HashSet();
	protected Integer entryNumber;

	/**
	 * @return Returns the entryNumber.
	 *  @hibernate.property name="entryNumber" column="ENTRY_NUMBER" type="int" length="10" 
	 */
	public Integer getEntryNumber()
	{
		return entryNumber;
	}

	/**
	 * @param entryNumber
	 */
	public void setEntryNumber(Integer entryNumber)
	{
		this.entryNumber = entryNumber;
	}

	/**
	 * @return
	 * @hibernate.one-to-many class="edu.wustl.clinportal.domain.ClinicalStudyRegistration" column="CLINICAL_STUDY_REG_ID" 
	 */
	public ClinicalStudyRegistration getClinicalStudyRegistration()
	{
		return clinicalStudyRegistration;
	}

	/**
	 * @param clStudyRegn
	 */
	public void setClinicalStudyRegistration(ClinicalStudyRegistration clStudyRegn)
	{
		this.clinicalStudyRegistration = clStudyRegn;
	}

	/**
	 * @return
	 * @hibernate.one-to-many class="edu.wustl.clinportal.domain.ClinicalStudyEvent" column="CLINICAL_STUDY_EVENT_ID" 
	 */
	public ClinicalStudyEvent getClinicalStudyEvent()
	{
		return clinicalStudyEvent;
	}

	/**
	 * @param clStudyEvent
	 */
	public void setClinicalStudyEvent(ClinicalStudyEvent clStudyEvent)
	{
		this.clinicalStudyEvent = clStudyEvent;
	}

	/* (non-Javadoc)
	 * @see edu.wustl.common.domain.AbstractDomainObject#setAllValues(edu.wustl.common.actionForm.IValueObject)
	 */
	public void setAllValues(IValueObject arg0) throws AssignDataException
	{
	}

	/**
	 * 
	 * @return the activityStatus
	 * @hibernate.property name="activityStatus" column="ACTIVITY_STATUS" type="string" length="10"
	 */
	public String getActivityStatus()
	{
		return activityStatus;
	}

	/**
	 * @param activityStatus
	 */
	public void setActivityStatus(String activityStatus)
	{
		this.activityStatus = activityStatus;
	}

	/**
	 * @return
	 * @hibernate.set name="recordEntryCollection" table="CATISUE_CLIN_STUDY_RECORD_NTRY"
	 * inverse="true" cascade="all" lazy="true"
	 * @hibernate.collection-key column="EVENT_ENTRY_ID"
	 * @hibernate.collection-one-to-many class="edu.wustl.clinportal.domain.RecordEntry"
	 */
	public Collection getRecordEntryCollection()
	{
		return recordEntryCollection;
	}

	/**
	 * @param recordEntryColn
	 */
	public void setRecordEntryCollection(Collection recordEntryColn)
	{
		this.recordEntryCollection = recordEntryColn;
	}

	/**
	 * @hibernate.id name="id" column="IDENTIFIER" type="long" length="30"
	 * unsaved-value="null" generator-class="native"
	 * @hibernate.generator-param name="sequence" value="CATISSUE_EVNTNTRY_SEQ"
	 */

	public Long getId()
	{
		return id;
	}

	/* (non-Javadoc)
	 * @see edu.wustl.common.domain.AbstractDomainObject#setId(java.lang.Long)
	 */
	public void setId(Long idt)
	{
		this.id = idt;
	}

	/**
	 * @hibernate.property name="encounterDate" column="ENCOUNTER_DATE" type="date"
	 */

	public Date getEncounterDate()
	{
		return encounterDate;
	}

	/**
	 * @param encounterDate
	 */
	public void setEncounterDate(Date encounterDate)
	{
		this.encounterDate = encounterDate;
	}

}
