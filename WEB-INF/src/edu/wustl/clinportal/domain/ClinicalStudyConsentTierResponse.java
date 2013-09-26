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

import java.io.Serializable;

import edu.wustl.common.actionForm.IValueObject;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.AssignDataException;

/**
 * @hibernate.class table="CLINPORT_CONSENT_TIER_RESPONSE"
 *
 */
public class ClinicalStudyConsentTierResponse extends AbstractDomainObject implements Serializable
{

	private static final long serialVersionUID = -5511144004426312668L;
	/**
	 * System Identifier.
	 */
	protected Long id;
	/**
	 * The responses for consent tiers by participants.
	 */
	protected String response;
	/**
	 * The consent tier for this response.
	 */
	protected ClinicalStudyConsentTier consentTier;

	/**
	 * @return the response
	 * @hibernate.property name="response" type="string" length="10" column="RESPONSE"
	 */
	public ClinicalStudyConsentTierResponse(ClinicalStudyConsentTierResponse csentTierResp)
	{
		this.response = csentTierResp.getResponse();
		this.consentTier = csentTierResp.getConsentTier();

	}

	/**
	 * 
	 */
	public ClinicalStudyConsentTierResponse()
	{
	}

	/**
	 * @return
	 */
	public String getResponse()
	{
		return response;
	}

	/**
	 * @param response the response to set
	 */
	public void setResponse(String response)
	{
		this.response = response;
	}

	/**
	 * @hibernate.id unsaved-value="null" generator-class="native" type="long" column="IDENTIFIER"
	 * @hibernate.generator-param name="sequence" value="CLINPORT_CONSENT_TIER_RES_SEQ"
	 * 
	 */
	public Long getId()
	{
		return id;
	}

	/* (non-Javadoc)
	 * @see edu.wustl.common.domain.AbstractDomainObject#setAllValues(edu.wustl.common.actionForm.IValueObject)
	 */
	public void setAllValues(IValueObject abstractForm) throws AssignDataException
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
	 * @return the consentTier
	 * @hibernate.many-to-one column="CLINPORT_CONSENT_TIER_ID" class="edu.wustl.clinportal.domain.ClinicalStudyConsentTier" 
	 */
	public ClinicalStudyConsentTier getConsentTier()
	{
		return consentTier;
	}

	/**
	 * @param consentTier the consentTier to set
	 */
	public void setConsentTier(ClinicalStudyConsentTier consentTier)
	{
		this.consentTier = consentTier;
	}
}
