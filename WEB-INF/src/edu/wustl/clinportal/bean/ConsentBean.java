/**
 * <p>Title: ConsentBean Class>
 * <p>Description:	This class contains attributes to display on SpecimenCollectionGroup.jsp,NewSpecimen.jsp 
 * and responses at Participant Level,Specimen Collection Group level and Specimen Level.<p>
 *  
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * 
 */

package edu.wustl.clinportal.bean;

import java.io.Serializable;

/**
 * @author falguni_sachde
 *
 */
public class ConsentBean implements Serializable
{

	private static final long serialVersionUID = 1L;
	/**
	 * The comments is for Id associated with every Consent Tier .
	 */
	protected String consentTierID;
	/**
	 * The comments is for Id associated with every Participant response.
	 */
	protected String participantResponseID;
	/**
	 * The comments is for Id associated with every SCG response.
	 */
	protected String specimenCollectionGroupLevelResponseID;
	/**
	 * The comments is for Id associated with every SCG response.
	 */
	protected String specimenLevelResponseID;
	/**
	 * The comments associated with the Consent Tier.
	 */
	protected String statement;
	/**
	 * The comments associated with Participant Response at CollectionProtocolRegistration.
	 */
	protected String participantResponse;
	/**
	 * The comments associated with Participant Response at Specimen Collection Group level.
	 */
	protected String specimenCollectionGroupLevelResponse;
	/**
	 * The comments associated with Participant Response at Specimen Level.
	 */
	protected String specimenLevelResponse;

	/**
	 * @return statement The comment associated with the Consent Tier
	 */

	public String getStatement()
	{
		return statement;
	}

	/**
	 * @param statement The comment associated with the Consent Tier
	 */
	public void setStatement(String statement)
	{
		this.statement = statement;
	}

	/**
	 * @return participantResponse The comments associated with Participant Response at CollectionProtocolRegistration
	 */
	public String getParticipantResponse()
	{
		return participantResponse;
	}

	/**
	 * @param ppantResponse The comments associated with Participant Response at CollectionProtocolRegistration
	 */
	public void setParticipantResponse(String ppantResponse)
	{
		this.participantResponse = ppantResponse;
	}

	/**
	 * @return consentTierID The Id associate with the Consent tier
	 */
	public String getConsentTierID()
	{
		return consentTierID;
	}

	/**
	 * @param consentTierID The Id associated with the Consent tier
	 */
	public void setConsentTierID(String consentTierID)
	{
		this.consentTierID = consentTierID;
	}

	/**
	 * @return participantResponseID The Id associate with the Consent tier response
	 */
	public String getParticipantResponseID()
	{
		return participantResponseID;
	}

	/**
	 * @param ppantResponseID The Id associated with the Consent tier response
	 */
	public void setParticipantResponseID(String ppantResponseID)
	{
		this.participantResponseID = ppantResponseID;
	}

}
