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
 * <p>Description: ConsentResponseForm Class is used to encapsulate all the request parameters passed 
 * from Collection Protocol Registration's response link on Participant Add/Edit webpage.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Abhishek Mehta
 * @version 1.00
 * Created on Sept 5, 2007
 */

package edu.wustl.clinportal.actionForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.domain.AbstractDomainObject;

/**
 * @author falguni_sachde
 *
 */
public class ConsentResponseForm extends AbstractActionForm
		implements
			Serializable,
			ConsentTierData
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7258847401432740404L;

	/**
	* collection protocol Id
	*/
	protected long collectionProtocolID;

	/**
	 * Signed Consent URL
	 */
	protected String signedConsentUrl = "";

	/**
	 * Witness name that may be PI
	 */
	protected long witnessId;

	/**
	 * Consent Date, Date on which Consent is Signed
	 */
	protected String consentDate = "";

	protected Map consentResponseValues = new LinkedHashMap();

	private int consentTierCounter = 0;

	/*
	 * To perform operation based on withdraw button clicked.
	 * Default No Action to allow normal behavior. 
	 */
	protected String withdrawlButtonStatus = Constants.WITHDRAW_RESPONSE_NOACTION;

	/* (non-Javadoc)
	 * @see edu.wustl.common.actionForm.IValueObject#setAllValues(edu.wustl.common.domain.AbstractDomainObject)
	 */
	public void setAllValues(AbstractDomainObject abstractDomain)
	{

	}

	@Override
	public int getFormId()
	{
		return Constants.CONSENT_FORM_ID;
	}

	@Override
	protected void reset()
	{
		// TODO Auto-generated method stub

	}

	/**
	* @param key Key prepared for saving data.
	* @param value Values corresponding to key
	*/
	public void setConsentResponseValue(String key, Object value)
	{
		if (isMutable())
		{
			consentResponseValues.put(key, value);
		}
	}

	/**
	 * 
	 * @param key Key prepared for saving data.
	 * @return consentResponseValues
	 */
	public Object getConsentResponseValue(String key)
	{
		return consentResponseValues.get(key);
	}

	/**
	 * @return values in map consentResponseValues
	 */
	public Collection getAllConsentResponseValue()
	{
		return consentResponseValues.values();
	}

	/**
	 * @return consentResponseValues The reference to the participant Response at CollectionprotocolReg Level
	 */
	public Map getConsentResponseValues()
	{
		return consentResponseValues;
	}

	/**
	 * @param consentRespValues The reference to the participant Response at CollectionprotocolReg Level
	 */
	public void setConsentResponseValues(Map consentRespValues)
	{
		this.consentResponseValues = consentRespValues;
	}

	/**
	 * @return
	 */
	public int getConsentTierCounter()
	{
		return consentTierCounter;
	}

	/**
	 * @param csentTrCounter
	 */
	public void setConsentTierCounter(int csentTrCounter)
	{
		this.consentTierCounter = csentTrCounter;
	}

	/**
	 * @return
	 */
	public long getCollectionProtocolID()
	{
		return collectionProtocolID;
	}

	/**
	 * @param colnProtocolID
	 */
	public void setCollectionProtocolID(long colnProtocolID)
	{
		this.collectionProtocolID = colnProtocolID;
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
	 * This function creates Array of String of keys and add them into the consentTiersList.
	 * @return consentTiersList
	 */
	public Collection getConsentTiers()
	{
		Collection consentTiersList = new ArrayList();
		String[] strArray = null;
		int noOfConsents = this.getConsentTierCounter();
		for (int consentCounter = 0; consentCounter < noOfConsents; consentCounter++)
		{
			strArray = new String[4];
			strArray[0] = "consentResponseValue(ConsentBean:" + consentCounter + "_consentTierID)";
			strArray[1] = "consentResponseValue(ConsentBean:" + consentCounter + "_statement)";
			strArray[2] = "consentResponseValue(ConsentBean:" + consentCounter
					+ "_participantResponse)";
			strArray[3] = "consentResponseValue(ConsentBean:" + consentCounter
					+ "_participantResponseID)";

			consentTiersList.add(strArray);
		}
		return consentTiersList;
	}

	/**
	 * This function returns the format of the response Key prepared. 
	 * @return consentResponseValue(ConsentBean:`_participantResponse)
	 */
	public String getConsentTierMap()
	{
		return "consentResponseValue(ConsentBean:`_participantResponse)";
	}

	/**
	 * @return
	 */
	public String getWithdrawlButtonStatus()
	{
		return withdrawlButtonStatus;
	}

	/**
	 * @param witdrButnStatus
	 */
	public void setWithdrawlButtonStatus(String witdrButnStatus)
	{
		this.withdrawlButtonStatus = witdrButnStatus;
	}

	@Override
	public void setAddNewObjectIdentifier(String addNewFor, Long addObjectIdentifier)
	{
		// TODO Auto-generated method stub

	}

}