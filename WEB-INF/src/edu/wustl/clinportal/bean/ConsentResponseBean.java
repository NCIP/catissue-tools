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
 * <p>Title: ConsentResponseForm Class>
 * <p>Description: This class contains attributes to display on ParticipantConsentTracking.jsp. </p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Abhishek Mehta
 * @version 1.00
 * Created on Sept 5, 2007
 */

package edu.wustl.clinportal.bean;

import java.io.Serializable;
import java.util.Collection;

import edu.wustl.clinportal.util.global.Constants;

/**
 * @author falguni_sachde
 *
 */
public class ConsentResponseBean implements Serializable
{

	/**
	 * collection protocol Id
	 */
	protected long collectionProtocolID;

	/**
	 * Signed Consent URL
	 */
	protected String signedConsentUrl;

	/**
	 * Witness name that may be PI
	 */
	protected long witnessId;

	/**
	 * Consent Date, Date on which Consent is Signed
	 */
	protected String consentDate;

	/**
	 * Consent Withdraw status
	 */
	protected String consentWithdrawalOption = Constants.WITHDRAW_RESPONSE_NOACTION;

	/**
	 * Consent response
	 */
	protected Collection<ConsentBean> consentResponse;

	public ConsentResponseBean(long colProtocolID, String signedConsentUrl, long witnessId,
			String consentDate, Collection<ConsentBean> consentResponse, String csentWdrawOptn)
	{
		this.collectionProtocolID = colProtocolID;
		this.signedConsentUrl = signedConsentUrl;
		this.witnessId = witnessId;
		this.consentDate = consentDate;
		this.consentResponse = consentResponse;
		this.consentWithdrawalOption = csentWdrawOptn;

	}

	/**
	 * @return
	 */
	public long getCollectionProtocolID()
	{
		return collectionProtocolID;
	}

	/**
	 * @param colProtocolID
	 */
	public void setCollectionProtocolID(long colProtocolID)
	{
		this.collectionProtocolID = colProtocolID;
	}

	/**
	 * @return
	 */
	public String getSignedConsentUrl()
	{
		return signedConsentUrl;
	}

	/**
	 * @param signedConsentUrl
	 */
	public void setSignedConsentUrl(String signedConsentUrl)
	{
		this.signedConsentUrl = signedConsentUrl;
	}

	/**
	 * @return
	 */
	public long getWitnessId()
	{
		return witnessId;
	}

	/**
	 * @param witnessId
	 */
	public void setWitnessId(long witnessId)
	{
		this.witnessId = witnessId;
	}

	/**
	 * @return
	 */
	public String getConsentDate()
	{
		return consentDate;
	}

	/**
	 * @param consentDate
	 */
	public void setConsentDate(String consentDate)
	{
		this.consentDate = consentDate;
	}

	/**
	 * @return
	 */
	public Collection<ConsentBean> getConsentResponse()
	{
		return consentResponse;
	}

	/**
	 * @param consentResponse
	 */
	public void setConsentResponse(Collection<ConsentBean> consentResponse)
	{
		this.consentResponse = consentResponse;
	}

	/**
	 * @return
	 */
	public String getConsentWithdrawalOption()
	{
		return consentWithdrawalOption;
	}

	/**
	 * @param csentWithdrawOptn
	 */
	public void setConsentWithdrawalOption(String csentWithdrawOptn)
	{
		this.consentWithdrawalOption = csentWithdrawOptn;
	}
}
