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
 * <p>Title: ParticipantMedicalIdentifier Class>
 * <p>Description:  A medical record identification information that refers to a Participant. </p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine
 * 
 * @version 1.00
 */

package edu.wustl.clinportal.domain;

import edu.wustl.clinportal.util.SearchUtil;
import edu.wustl.common.actionForm.IValueObject;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.AssignDataException;

/**
 * A medical record identification number that refers to a Participant.
 * @hibernate.class table="CATISSUE_PART_MEDICAL_ID"
 */
public class ParticipantMedicalIdentifier extends AbstractDomainObject
		implements
			java.io.Serializable
{

	private static final long serialVersionUID = 1234567890L;

	/**
	 * System generated unique id
	 */
	protected Long id;

	/**
	 * Participant's medical record number used in their medical treatment.
	 */
	protected String medicalRecordNumber;

	/**
	 * Source of medical record number.
	 */
	protected Site site;

	/**
	 * 
	 */
	protected Participant participant;

	/**
	 * Returns System generated unique identifier.
	 * @hibernate.id name="id" column="IDENTIFIER" type="long" length="30"
	 * unsaved-value="null" generator-class="native"
	 * @hibernate.generator-param name="sequence" value="CATISSUE_PART_MEDICAL_ID_SEQ"
	 * @return System generated unique id.
	 * @see #setId(Long)
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * Sets a system id for a particular medical record.
	 * @param idt id for a particular medical record.
	 * @see #getId()
	 * */
	public void setId(Long idt)
	{
		this.id = idt;
	}

	/**
	 * Returns the Medical Record Number.
	 * @hibernate.property name="medicalRecordNumber" type="string" 
	 * column="MEDICAL_RECORD_NUMBER" length="50"
	 * @return the Medical Record Number.
	 * @see #setMedicalRecordNumber(String)
	 */
	public String getMedicalRecordNumber()
	{
		return medicalRecordNumber;
	}

	/**
	 * Sets a Medical Record Number.
	 * @param medicalRecNumber Medical Record Number.
	 * @see #getMedicalRecordNumber()
	 */
	public void setMedicalRecordNumber(String medicalRecNumber)
	{
		this.medicalRecordNumber = medicalRecNumber;
	}

	/**
	 * Returns the source of medical record number.
	 * @hibernate.many-to-one column="SITE_ID"  class="edu.wustl.clinportal.domain.Site" constrained="true"
	 * @return the source of medical record number.
	 * @see #setSite(Site)
	 */
	public Site getSite()
	{
		return site;
	}

	/**
	 * Returns the source of medical record number.
	 * @return the source of medical record number.
	 * @see #setSite(Site)
	 */
	public void setSite(Site site)
	{
		this.site = site;
	}

	/**
	 * @hibernate.many-to-one column="PARTICIPANT_ID"  class="edu.wustl.clinportal.domain.Participant" constrained="true"
	 * @see #setParticipant(Site)
	 */
	public Participant getParticipant()
	{
		return participant;
	}

	/**
	 * @param participant The participant to set.
	 */
	public void setParticipant(Participant participant)
	{
		this.participant = participant;
	}

	/* (non-Javadoc)
	 * @see edu.wustl.clinportal.domain.AbstractDomainObject#setAllValues(edu.wustl.clinportal.actionForm.AbstractActionForm)
	 */
	public void setAllValues(IValueObject abstractForm) throws AssignDataException
	{

		if (SearchUtil.isNullobject(site))
		{
			site = new Site();
		}

	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return "ParticipantMedicalIdentifier: " + id + ", " + medicalRecordNumber + ", " + site;
	}

}