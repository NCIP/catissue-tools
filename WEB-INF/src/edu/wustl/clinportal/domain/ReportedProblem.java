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
 * <p>Title: ReportedProblem Class>
 * <p>Description:  Models the Reported Problem information. </p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * 
 */

package edu.wustl.clinportal.domain;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import edu.wustl.clinportal.actionForm.ReportedProblemForm;
import edu.wustl.clinportal.util.SearchUtil;
import edu.wustl.common.actionForm.IValueObject;
import edu.wustl.common.domain.AbstractDomainObject;

/**
 * Models the Reported Problem information.
 * @hibernate.class table="CATISSUE_REPORTED_PROBLEM"
 * 
 */
public class ReportedProblem extends AbstractDomainObject implements Serializable
{

	/**
	 * id is a unique id assigned to each reported problem.
	 * */
	private Long id;

	/**
	 * The subject of the reported problem.
	 */
	private String subject;

	/**
	 * The email id of who reported the problem.
	 */
	private String from;

	/**
	 * The message body of the reported problem.
	 */
	private String messageBody;

	/**
	 * Date the problem was reported.
	 */
	protected Date reportedDate;

	/**
	 * Comments given by problem resolver.
	 */
	protected String comments;

	/**
	 * States the activity status of the reported problem.
	 */
	protected String activityStatus;

	private String nameOfReporter;

	private String affiliation;

	/**
	 * The affiliation of the user with the reported problem.
	 * @hibernate.property name="affiliation" type="string"
	 * column="AFFILIATION" length="255" not-null="true"
	 * @return The affiliation of the reported problem.
	 * @see #setAffiliation(String affiliation) 
	 */
	public String getAffiliation()
	{
		return affiliation;
	}

	/**
	 * @param affiliation The affiliation to set.
	 */
	public void setAffiliation(String affiliation)
	{
		this.affiliation = affiliation;
	}

	/**
	 * The name of the user who reported the problem.
	 * @hibernate.property name="nameOfReporter" type="string"
	 * column="NAME_OF_REPORTER" length="255" not-null="true"
	 * @return The name of the user who reported the problem.
	 * @see #setNameOfReporter(String nameOfReporter) 
	 */
	public String getNameOfReporter()
	{
		return nameOfReporter;
	}

	/**
	 * @param nameOfReporter The nameOfReporter to set.
	 */
	public void setNameOfReporter(String nameOfReporter)
	{
		this.nameOfReporter = nameOfReporter;
	}

	/**
	 * Instantiates an empty Reported Problem. 
	 */
	public ReportedProblem()
	{
		reportedDate = Calendar.getInstance().getTime();
	}

	/**
	 * Initializes a newly created reported problem so that it represents 
	 * the same problem as the argument.
	 * @param reportedProbForm A reported problem.
	 */
	public ReportedProblem(ReportedProblemForm reportedProbForm)
	{
		this();
		setAllValues(reportedProbForm);
	}

	/**
	 * Sets all values from the reportedProblemForm object.
	 * @param reportedProblemForm The reportedProblemForm object.
	 */
	public void setAllValues(IValueObject absActionForm)
	{

		if (SearchUtil.isNullobject(reportedDate))
		{
			reportedDate = new Date();
		}

		ReportedProblemForm reportedProbForm = (ReportedProblemForm) absActionForm;
		this.id = Long.valueOf(reportedProbForm.getId());
		this.subject = reportedProbForm.getSubject();
		this.from = reportedProbForm.getFrom();
		this.messageBody = reportedProbForm.getMessageBody();
		this.activityStatus = reportedProbForm.getActivityStatus();
		this.comments = reportedProbForm.getComments();
		this.nameOfReporter = reportedProbForm.getNameOfReporter();
		this.affiliation = reportedProbForm.getAffiliation();
	}

	/**
	 * Returns the id assigned to the reported problem.
	 * @return the id assigned to the reported problem.
	 * @hibernate.id name="id" column="IDENTIFIER" type="long" length="30"
	 * unsaved-value="null" generator-class="native"
	 * @hibernate.generator-param name="sequence" value="CATISSUE_REPORTED_PROBLEM_SEQ"
	 * @see #setIdentifier(Long)
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * Sets the id to the reported problem.
	 * @param idt The id to set.
	 * @see #getIdentifier()
	 */
	public void setId(Long idt)
	{
		this.id = idt;
	}

	/**
	 * Returns the email id of who reported the problem.
	 * @return the email id of who reported the problem.
	 * @hibernate.property name="from" type="string"
	 * column="REPORTERS_EMAIL_ID" length="255" not-null="true" 
	 * @see #setFrom(String)
	 */
	public String getFrom()
	{
		return from;
	}

	/**
	 * Sets the email id of who reported the problem.
	 * @param from the email id of who reported the problem.
	 * @see #getFrom()
	 */
	public void setFrom(String from)
	{
		this.from = from;
	}

	/**
	 * The message body of the reported problem.
	 * @hibernate.property name="messageBody" type="string"
	 * column="MESSAGE_BODY" length="200" not-null="true"
	 * @return The message body of the reported problem.
	 * @see #setMessageBody(String) 
	 */
	public String getMessageBody()
	{
		return messageBody;
	}

	/**
	 * Sets the message body of the reported problem.
	 * @param messageBody he message body of the reported problem.
	 * @see #getMessageBody()
	 */
	public void setMessageBody(String messageBody)
	{
		this.messageBody = messageBody;
	}

	/**
	 * Returns the subject of the reported problem.
	 * @return the subject of the reported problem.
	 * @hibernate.property name="subject" type="string" 
	 * column="SUBJECT" length="255"
	 * @see #setSubject(String)
	 */
	public String getSubject()
	{
		return subject;
	}

	/**
	 * Sets the subject of the reported problem.
	 * @param subject The subject to set.
	 * @see #getSubject()
	 */
	public void setSubject(String subject)
	{
		this.subject = subject;
	}

	/**
	 * Returns the date the problem was reported.
	 * @hibernate.property name="reportedDate" column="REPORTED_DATE" type="date"
	 * @return Returns the dateAdded.
	 * @see #setDateAdded(Date)
	 */
	public Date getReportedDate()
	{
		return reportedDate;
	}

	/**
	 * Sets the date the problem was reported.
	 * @param dateAdded The dateAdded to set.
	 * @see #getDateAdded() 
	 */
	public void setReportedDate(Date reportedDate)
	{
		this.reportedDate = reportedDate;
	}

	/**
	 * Returns the activity status of the participant.
	 * @hibernate.property name="activityStatus" type="string" 
	 * column="ACTIVITY_STATUS" length="100"
	 * @return Returns the activity status of the participant.
	 * @see #setActivityStatus(ActivityStatus)
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

	/**
	 * Returns the comments given by the resolver. 
	 * @hibernate.property name="comments" type="string" 
	 * column="COMMENTS" length="2000"
	 * @return the comments given by the resolver.
	 * @see #setComments(String)
	 */
	public String getComments()
	{
		return comments;
	}

	/**
	 * Sets the comments given by the resolver
	 * @param comments The comments to set.
	 * @see #getComments() 
	 */
	public void setComments(String comments)
	{
		this.comments = comments;
	}

}
