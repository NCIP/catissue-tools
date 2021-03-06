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
 * <p>Title: SpecimenProtocol Class</p>
 * <p>Description:  A set of procedures that govern the collection and/or distribution of biospecimens.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * 
 * @version 1.00
 * Created on July 12, 2005
 */

package edu.wustl.clinportal.domain;

import java.util.Date;

import edu.wustl.clinportal.actionForm.SpecimenProtocolForm;
import edu.wustl.clinportal.util.SearchUtil;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.actionForm.IValueObject;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.logger.Logger;

/**
 * A set of procedures that govern the collection and/or distribution of bio specimens. 
 * 
 * @hibernate.class table="CATISSUE_SPECIMEN_PROTOCOL"
 */
public abstract class SpecimenProtocol extends AbstractDomainObject implements java.io.Serializable
{

	private static final long serialVersionUID = 1234567890L;

	/**
	 * System generated unique id.
	 */
	protected Long id = null;

	/**
	 * The current principal investigator of the protocol.
	 */
	protected User principalInvestigator;

	/**
	 * Full title assigned to the protocol.
	 */
	protected String title;

	/**
	 * Abbreviated title assigned to the protocol.
	 */
	protected String shortTitle;

	/**
	 * IRB approval number.
	 */
	protected String irbIdentifier;

	/**
	 * Date on which the protocol is activated.
	 */
	protected Date startDate;

	/**
	 * Date on which the protocol is marked as closed.
	 */
	protected Date endDate;

	/**
	 * Number of anticipated cases need for the protocol.
	 */
	protected Integer enrollment;

	/**
	 * URL to the document that describes detailed information for the biospecimen protocol.
	 */
	protected String descriptionURL;

	/**
	 * Defines whether this SpecimenProtocol record can be queried (Active) or not queried (Inactive) by any actor.
	 */
	protected String activityStatus;

	/**
	 * NOTE: Do not delete this constructor. Hibernet uses this by reflection API.
	 * */
	public SpecimenProtocol()
	{
		super();
	}

	/**
	 * Returns the id of the protocol.
	 * @hibernate.id name="id" column="IDENTIFIER" type="long" length="30"
	 * unsaved-value="null" generator-class="native"
	 * @hibernate.generator-param name="sequence" value="CATISSUE_SPECIMEN_PROTOCOL_SEQ"
	 * @return Returns the id.
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
	 * Returns the principal investigator of the protocol.
	 * @hibernate.many-to-one column="PRINCIPAL_INVESTIGATOR_ID" class="edu.wustl.clinportal.domain.User"
	 * constrained="true"
	 * @return the principal investigator of the protocol.
	 * @see #setPrincipalInvestigator(User)
	 */
	public User getPrincipalInvestigator()
	{
		return principalInvestigator;
	}

	/**
	 * @param principalInvtor The principalInvestigator to set.
	 */
	public void setPrincipalInvestigator(User principalInvtor)
	{
		this.principalInvestigator = principalInvtor;
	}

	/**
	 * Returns the title of the protocol.
	 * @hibernate.property name="title" type="string" column="TITLE" length="255" not-null="true" unique="true"
	 * @return Returns the title.
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * @param title The title to set.
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}

	/**
	 * Returns the short title of the protocol.
	 * @hibernate.property name="shortTitle" type="string" column="SHORT_TITLE"
	 * length="255"
	 * @return Returns the shortTitle.
	 */
	public String getShortTitle()
	{
		return shortTitle;
	}

	/**
	 * @param shortTitle The shortTitle to set.
	 */
	public void setShortTitle(String shortTitle)
	{
		this.shortTitle = shortTitle;
	}

	/**
	 * Returns the irb id of the protocol.
	 * @hibernate.property name="irbIdentifier" type="string" column="IRB_IDENTIFIER" length="255"
	 * @return Returns the irbIdentifier.
	 */
	public String getIrbIdentifier()
	{
		return irbIdentifier;
	}

	/**
	 * @param irbIdentifier The irbIdentifier to set.
	 */
	public void setIrbIdentifier(String irbIdentifier)
	{
		this.irbIdentifier = irbIdentifier;
	}

	/**
	 * Returns the startdate of the protocol.
	 * @hibernate.property name="startDate" type="date" column="START_DATE" length="50"
	 * @return Returns the startDate.
	 */
	public Date getStartDate()
	{
		return startDate;
	}

	/**
	 * @param startDate The startDate to set.
	 */
	public void setStartDate(Date startDate)
	{
		this.startDate = startDate;
	}

	/**
	 * Returns the enddate of the protocol.
	 * @hibernate.property name="endDate" type="date" column="END_DATE" length="50"
	 * @return Returns the endDate.
	 */
	public Date getEndDate()
	{
		return endDate;
	}

	/**
	 * @param endDate The endDate to set.
	 */
	public void setEndDate(Date endDate)
	{
		this.endDate = endDate;
	}

	/**
	 * Returns the enrollment.
	 * @hibernate.property name="enrollment" type="int" column="ENROLLMENT" length="50"
	 * @return Returns the enrollment.
	 */
	public Integer getEnrollment()
	{
		return enrollment;
	}

	/**
	 * @param enrollment The enrollment to set.
	 */
	public void setEnrollment(Integer enrollment)
	{
		this.enrollment = enrollment;
	}

	/**
	 * Returns the descriptionURL.
	 * @hibernate.property name="descriptionURL" type="string" column="DESCRIPTION_URL" length="255"
	 * @return Returns the descriptionURL.
	 */
	public String getDescriptionURL()
	{
		return descriptionURL;
	}

	/**
	 * @param descriptionURL The descriptionURL to set.
	 */
	public void setDescriptionURL(String descriptionURL)
	{
		this.descriptionURL = descriptionURL;
	}

	/**
	 * Returns the activityStatus.
	 * @hibernate.property name="activityStatus" type="string" column="ACTIVITY_STATUS" length="50"
	 * @return Returns the activityStatus.
	 */
	public String getActivityStatus()
	{
		return activityStatus;
	}

	/**
	 * @param activityStatus The activityStatus to set.
	 */
	public void setActivityStatus(String activityStatus)
	{
		this.activityStatus = activityStatus;
	}

	/* (non-Javadoc)
	 * @see edu.wustl.common.domain.AbstractDomainObject#setAllValues(edu.wustl.common.actionForm.IValueObject)
	 */
	public void setAllValues(IValueObject abstractForm)
	{
		Logger.out.debug("SpecimenProtocol: setAllValues ");
		try
		{
			//Change for API Search   --- Ashwin 04/10/2006
			if (SearchUtil.isNullobject(principalInvestigator))
			{
				principalInvestigator = new User();
			}

			SpecimenProtocolForm spForm = (SpecimenProtocolForm) abstractForm;

			this.title = spForm.getTitle();
			this.shortTitle = spForm.getShortTitle();
			this.irbIdentifier = spForm.getIrbID();

			this.startDate = Utility.parseDate(spForm.getStartDate(), Constants.DATE_PATTERN_DD_MM_YYYY);
			this.endDate = Utility.parseDate(spForm.getEndDate(),Constants.DATE_PATTERN_DD_MM_YYYY);

			if (spForm.getEnrollment() != null && spForm.getEnrollment().trim().length() > 0)
			{
				this.enrollment = Integer.valueOf(spForm.getEnrollment());
			}

			this.descriptionURL = spForm.getDescriptionURL();
			this.activityStatus = spForm.getActivityStatus();

			principalInvestigator = new User();
			this.principalInvestigator.setId(Long.valueOf(spForm.getPrincipalInvestigatorId()));
		}
		catch (Exception excp)
		{
			// use of logger as per bug 79
			Logger.out.error(excp.getMessage(), excp);
		}
	}
}