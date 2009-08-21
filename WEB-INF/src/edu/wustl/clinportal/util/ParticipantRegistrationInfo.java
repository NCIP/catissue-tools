
package edu.wustl.clinportal.util;

import java.util.List;

/**
 * ParticipantRegistrationInfo is used to store CPId, CPTitle and participant collection registered for CP.
 * @author falguni_sachde
 */
public class ParticipantRegistrationInfo
{

	private Long cpId;
	private String cpTitle;
	private String cpShortTitle;
	private List participantInfoCollection;

	/**
	 * this method returns the CP id
	 * @return CP ID
	 */
	public Long getCsId()
	{
		return cpId;
	}

	/**
	 * This Method sets CP ID
	 * @param cpId
	 */
	public void setCsId(Long cpId)
	{
		this.cpId = cpId;
	}

	/**
	 * This method returns CP title
	 * @return CP title
	 */
	public String getCsTitle()
	{
		return cpTitle;
	}

	/**
	 * This method sets the CP title
	 * @param cpTitle
	 */
	public void setCsTitle(String cpTitle)
	{
		this.cpTitle = cpTitle;
	}

	/**
	 * This method returns CP Short title
	 * @return CP Short title
	 */
	public String getCsShortTitle()
	{
		return cpShortTitle;
	}

	/**
	 * This method sets the CP Short title
	 * @param cpShortTitle
	 */
	public void setCsShortTitle(String cpShortTitle)
	{
		this.cpShortTitle = cpShortTitle;
	}

	/**
	 * This method returns the Participant Info collection
	 * @return Participant Info Collection
	 */
	public List getParticipantInfoCollection()
	{
		return participantInfoCollection;
	}

	/**
	 * This method sets the participant Info Collection
	 * @param partipantInfoColn
	 */
	public void setParticipantInfoCollection(List partipantInfoColn)
	{
		this.participantInfoCollection = partipantInfoColn;
	}

}
