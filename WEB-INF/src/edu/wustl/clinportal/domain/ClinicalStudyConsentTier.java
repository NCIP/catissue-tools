package edu.wustl.clinportal.domain;

import java.io.Serializable;

import edu.wustl.common.actionForm.IValueObject;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.AssignDataException;


/**
 * @hibernate.class table="CLINPORT_CONSENT_TIER"
 */
public class ClinicalStudyConsentTier extends AbstractDomainObject implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The consent statements.
	 */
	protected String statement;

	/**
     * System generated unique id.
     */
    protected Long id;
	/**
	 * @return the statement
	 * @hibernate.property name="statement" type="string" length="500" column="STATEMENT"
	 */
	public String getStatement()
	{
		return statement;
	}

	
	/**
	 * @param statement the statement to set
	 */
	public void setStatement(String statement)
	{
		this.statement = statement;
	}


	/**
	 * @hibernate.id name="id" type="long" column="IDENTIFIER" length="30" unsaved-value="null" generator-class="native"
	 * @hibernate.generator-param name="sequence" value="CLINPORT_CONSENT_TIER_SEQ"
	 */
	public Long getId()
	{		
		return id;
	}


	
	public void setAllValues(IValueObject abstractForm) throws AssignDataException
	{
		// TODO Auto-generated method stub
		
	}


	
	public void setId(Long idt)
	{
		this.id = idt;		
	}
	
	public ClinicalStudyConsentTier()
	{
		
	}
	public ClinicalStudyConsentTier(ClinicalStudyConsentTier consentTier)
	{
		if(consentTier.getId() != null && consentTier.getId().toString().trim().length() > 0)
		{
			this.id = consentTier.getId();
		}
		this.statement = consentTier.getStatement();
	}
}
