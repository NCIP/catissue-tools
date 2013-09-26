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
 * 
 */

package edu.wustl.clinportal.domain;

import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.actionForm.IValueObject;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.AssignDataException;

/**
 * @author falguni sachde
 * @hibernate.class table="CATISUE_CLIN_STUDY_RECORD_NTRY"
 */
public class RecordEntry extends AbstractDomainObject
{

	/*
	 * 
	 */
	private static final long serialVersionUID = 1234567890L;
	/*
	 * 
	 */
	protected Long id;

	/**
	 * 
	 */
	protected StudyFormContext studyFormContext;
	/**
	 * 
	 */
	protected EventEntry eventEntry;
	/**
	 * 
	 */
	private String activityStatus;

	/**
	 * @hibernate.id name="id" column="IDENTIFIER" type="long" length="30"
	 * unsaved-value="null" generator-class="native"
	 * @hibernate.generator-param name="sequence" value="CATISSUE_REC_ENTRY_SEQ"
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * @param abstractForm
	 * @throws AssignDataException
	 */
	public void setAllValues(AbstractActionForm abstractForm) throws AssignDataException
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see edu.wustl.common.domain.AbstractDomainObject#setId(java.lang.Long)
	 */
	public void setId(Long idt)
	{
		this.id = idt;
	}

	/**
	 * @return the activityStatus
	 */
	public String getActivityStatus()
	{
		return activityStatus;
	}

	/**
	 * @param activityStatus the activityStatus to set
	 */
	public void setActivityStatus(String activityStatus)
	{
		this.activityStatus = activityStatus;
	}

	@Override
	public void setAllValues(IValueObject arg0) throws AssignDataException
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object recordEntry)
	{
		boolean flag = false;
		if (this.getId() != null)
		{
			RecordEntry recEntry = (RecordEntry) recordEntry;
			if (((RecordEntry) this).getId().equals(recEntry.getId()))
			{
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * @return the studyFormContext
	 * @hibernate.many-to-one column="STUDY_FORM_CONTXT_ID" class="edu.wustl.clinportal.domain.StudyFormContext"
	 * constrained="true" 
	 */

	public StudyFormContext getStudyFormContext()
	{
		return studyFormContext;
	}

	/**
	 * @param studyFormContext
	 */
	public void setStudyFormContext(StudyFormContext studyFormContext)
	{
		this.studyFormContext = studyFormContext;
	}

	/**
	 * @return the eventEntry
	 * @hibernate.many-to-one column="EVENT_ENTRY_ID" class="edu.wustl.clinportal.domain.EventEntry"
	 * constrained="true" 
	 */

	public EventEntry getEventEntry()
	{
		return eventEntry;
	}

	/**
	 * @param eventEntry
	 */
	public void setEventEntry(EventEntry eventEntry)
	{
		this.eventEntry = eventEntry;
	}

}
