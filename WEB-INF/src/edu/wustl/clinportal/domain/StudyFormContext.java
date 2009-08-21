/**
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright:TODO</p>
 *@author 
 *@version 1.0
 */

package edu.wustl.clinportal.domain;

import java.util.Collection;
import java.util.HashSet;

import edu.wustl.common.actionForm.IValueObject;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.AssignDataException;

/**
 * 
 * @author shital_lawhale
 *@hibernate.class table="CATISSUE_STUDY_FORM_CONTEXT"
 */
public class StudyFormContext extends AbstractDomainObject implements Comparable<StudyFormContext>
{

	private static final long serialVersionUID = 1L;

	protected Long id;
	protected String studyFormLabel;
	protected Long containerId;
	protected String activityStatus;
	protected ClinicalStudyEvent clinicalStudyEvent;
	protected Collection recordEntryCollection = new HashSet();
	protected Boolean canHaveMultipleRecords;

	/**
	 * @return clinicalStudyEvent
	 * @hibernate.many-to-one column="CLINICAL_STUDY_EVENT_ID" class="edu.wustl.clinportal.domain.ClinicalStudyEvent"
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

	/**
	 * @hibernate.id name="id" column="IDENTIFIER" type="long" length="30"
	 * unsaved-value="null" generator-class="native"
	 * @hibernate.generator-param name="sequence" value="CATISSUE_STDY_FRM_CONTXT_SEQ"
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
	 * 
	 * @returns studyFormLabel
	 * @hibernate.property name="studyFormLabel" type="string" column="STUDY_FORM_LABEL" length="255"
	 */
	public String getStudyFormLabel()
	{
		return studyFormLabel;
	}

	/**
	 * @param studyFormLabel
	 */
	public void setStudyFormLabel(String studyFormLabel)
	{
		this.studyFormLabel = studyFormLabel;
	}

	/**
	 * 
	 * @return container identifier
	 * @hibernate.property name="containerId" type="long" length="30" column="CONTAINER_ID"
	 */
	public Long getContainerId()
	{
		return containerId;
	}

	/**
	 * @param containerId
	 */
	public void setContainerId(Long containerId)
	{
		this.containerId = containerId;
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
	 * 
	 */
	public void setAllValues(IValueObject arg0) throws AssignDataException
	{

	}

	/**
	 * @return
	 * @hibernate.set name="recordEntryCollection" table="CATISUE_CLIN_STUDY_RECORD_NTRY"
	 * inverse="true" cascade="all" lazy="true"
	 * @hibernate.collection-key column="STUDY_FORM_CONTXT_ID"
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

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object studyFormContext)
	{
		boolean flag = false;
		if (this.getId() != null)
		{
			StudyFormContext frm = (StudyFormContext) studyFormContext;
			if (((StudyFormContext) this).getId().equals(frm.getId()))
			{
				flag = true;
			}
		}
		return flag;
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
	public Boolean getCanHaveMultipleRecords()
	{
		return canHaveMultipleRecords;
	}

	/**
	 * @param canHaveMultRecs
	 */
	public void setCanHaveMultipleRecords(Boolean canHaveMultRecs)
	{
		this.canHaveMultipleRecords = canHaveMultRecs;
	}
	/**
     * 
     */
    public int compareTo(StudyFormContext form1)
    {
        if (this.getId() != null && form1.getId() != null)
        {
            return this.getId().compareTo(form1.getId());
        }
        else
            return 0;
    }

}
