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
import java.util.HashSet;

import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.actionForm.IValueObject;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.AssignDataException;

/**
 * 
 * @hibernate.class table="CATISSUE_CLINICAL_STUDY_EVENT"
 */
public class ClinicalStudyEvent extends AbstractDomainObject implements Comparable<ClinicalStudyEvent>
{

	private static final long serialVersionUID = 1L;

	/**
	 * System generated unique id.
	 */
	protected Long id;

	protected String collectionPointLabel;

	protected Integer studyCalendarEventPoint;

	protected Integer noOfEntries;

	protected Collection studyFormContextCollection = new HashSet();

	protected String activityStatus;

	protected Boolean isInfiniteEntry;

	protected ClinicalStudy clinicalStudy;

	protected Collection eventEntryCollection = new HashSet();

	protected Long relToCPEId;

	/**
	 * @return the clinicalStudy
	 * @hibernate.many-to-one column="CLINICAL_STUDY_ID" class="edu.wustl.clinportal.domain.ClinicalStudy"
	 * constrained="true" 
	 */
	public ClinicalStudy getClinicalStudy()
	{
		return clinicalStudy;
	}

	/*
	 * @param clinicalStudy the clinicalStudy to set
	 */
	public void setClinicalStudy(ClinicalStudy clinicalStudy)
	{
		this.clinicalStudy = clinicalStudy;
	}

	/**
	 * @return the collectionPointLabel
	 * @hibernate.property name="collectionPointLabel" column="COLLECTION_POINT_LABEL" type="string"
	 * length="255"
	 *  
	 */
	public String getCollectionPointLabel()
	{
		return collectionPointLabel;
	}

	/**
	 * @param colnPointLbl the collectionPointLabel to set
	 */
	public void setCollectionPointLabel(String colnPointLbl)
	{
		this.collectionPointLabel = colnPointLbl;
	}

	/**
	 * @return the studyCalendarEventPoint
	 * @hibernate.property name="studyCalendarEventPoint" column="EVENT_POINT" type="int"
	 * length="10"
	 */
	public Integer getStudyCalendarEventPoint()
	{
		return studyCalendarEventPoint;
	}

	/**
	 * @param studyCalEventPnt the studyCalendarEventPoint to set
	 */
	public void setStudyCalendarEventPoint(Integer studyCalEventPnt)
	{
		this.studyCalendarEventPoint = studyCalEventPnt;
	}

	/**
	 * @hibernate.id name="id" column="IDENTIFIER" type="long" length="30"
	 * unsaved-value="null" generator-class="native"
	 * @hibernate.generator-param name="sequence" value="CATISSUE_STUDY_EVNT_SEQ"
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
	 * @param arg0
	 * @throws AssignDataException
	 */
	public void setAllValues(AbstractActionForm arg0) throws AssignDataException
	{

	}

	/* (non-Javadoc)
	 * @see edu.wustl.common.domain.AbstractDomainObject#setAllValues(edu.wustl.common.actionForm.IValueObject)
	 */
	public void setAllValues(IValueObject arg0) throws AssignDataException
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @return the noOfEntries
	 * @hibernate.property name="noOfEntries" column="NO_OF_ENTRIES" type="int" length="30"
	 * 
	 */
	public Integer getNoOfEntries()
	{
		return noOfEntries;
	}

	/**
	 * @param noOfEntries the noOfEntries to set
	 */
	public void setNoOfEntries(Integer noOfEntries)
	{
		this.noOfEntries = noOfEntries;
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

	public void setActivityStatus(String activityStatus)
	{
		this.activityStatus = activityStatus;
	}

	/**
	 * @return Returns the studyFormLabel.
	 *  @hibernate.property name="isInfiniteEntry" column="IS_INFINITE_ENTRY" type="boolean" length="5" 
	 */

	public Boolean getIsInfiniteEntry()
	{
		return isInfiniteEntry;
	}

	public void setIsInfiniteEntry(Boolean isInfiniteEntry)
	{
		this.isInfiniteEntry = isInfiniteEntry;
	}

	/**
	 * Returns collection of form context.
	 * @return collection of studyForm context.
	 * @hibernate.set name="studyFormContextCollection" table="CATISSUE_STUDY_FORM_CONTEXT"
	 * inverse="true" cascade="save-update"
	 * @hibernate.collection-key column="CLINICAL_STUDY_EVENT_ID"
	 * @hibernate.collection-one-to-many class="edu.wustl.clinportal.domain.StudyFormContext"
	 */

	public Collection getStudyFormContextCollection()
	{
		return studyFormContextCollection;
	}

	/**
	 * @param studyFormCtxtColn
	 */
	public void setStudyFormContextCollection(Collection studyFormCtxtColn)
	{
		this.studyFormContextCollection = studyFormCtxtColn;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object clStudyEvent)
	{
		boolean returnVal = false;
		if (this.getId() != null)
		{
			ClinicalStudyEvent event = (ClinicalStudyEvent) clStudyEvent;
			if (this.getId().equals(event.getId()))
			{
				returnVal = true;
			}
		}
		return returnVal;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return 1;
	}

	/**
	 * @return
	 */
	public Collection getEventEntryCollection()
	{
		return eventEntryCollection;
	}

	/**
	 * Returns collection of form context.
	 * @return collection of studyForm context.
	 * @hibernate.set name="eventEntryCollection" table="CATISSUE_EVENT_ENTRY"
	 * inverse="true" cascade="all"
	 * @hibernate.collection-key column="CLINICAL_STUDY_EVENT_ID"
	 * @hibernate.collection-one-to-many class="edu.wustl.clinportal.domain.EventEntry"
	 */
	public void setEventEntryCollection(Collection eventEntryColn)
	{
		this.eventEntryCollection = eventEntryColn;
	}

	/**
	 * @param relToCPEId the relToCPEId to set
	 */
	public void setRelToCPEId(Long relToCPEId)
	{
		this.relToCPEId = relToCPEId;
	}

	/**
	 * @return the relToCPEId
	 */
	public Long getRelToCPEId()
	{
		return relToCPEId;
	}

    /**
     * 
     */
    public int compareTo(ClinicalStudyEvent event1)
    {
        if (this.getStudyCalendarEventPoint().equals(event1.getStudyCalendarEventPoint()))
        {
            if (this.getId() != null && event1.getId() != null)
            {
                return this.getId().compareTo(event1.getId());
            }
            else
                return 0;
        }
        else
            return this.getStudyCalendarEventPoint().compareTo(event1.getStudyCalendarEventPoint());
    }

}
