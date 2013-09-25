/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */


package edu.wustl.clinportal.actionForm;

import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.domain.AbstractDomainObject;

public class CSSearchForm extends AbstractActionForm
{

	/**
	 * 
	 */
	Long csId = null;
	/**
	 * 
	 */
	Long participantId = null;

	public Long getParticipantId()
	{
		return participantId;
	}

	public void setParticipantId(Long participantId)
	{
		this.participantId = participantId;
	}

	/**
	 * Returns the identifier assigned to form bean.
	 * @return The identifier assigned to form bean.
	 */
	public int getFormId()
	{
		return 0;
	}

	/**
	 * This method Copies the data from an Specimen object to a AliquotForm object.
	 * @param abstractDomain An object of Specimen class.  
	 */
	public void setAllValues(AbstractDomainObject abstractDomain)
	{
	}

	/**
	 * This method resets the form fields.
	 */
	public void reset()
	{
	}

	public Long getCsId()
	{
		return csId;
	}

	public void setCsId(Long csId)
	{
		this.csId = csId;
	}

	@Override
	public void setAddNewObjectIdentifier(String addNewFor, Long addObjectIdentifier)
	{
		// TODO Auto-generated method stub

	}
}
