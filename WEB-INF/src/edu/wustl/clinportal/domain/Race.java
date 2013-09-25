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

import edu.wustl.common.actionForm.IValueObject;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.AssignDataException;

public class Race extends AbstractDomainObject implements java.io.Serializable
{

	private static final long serialVersionUID = 1234567890L;
	protected Long id;
	protected String raceName;
	protected Participant participant;
	@Override
	public Long getId()
	{
		// TODO Auto-generated method stub
		return this.id;
	}

	@Override
	public void setAllValues(IValueObject arg0) throws AssignDataException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setId(Long idt)
	{
		this.id = idt;
		
	}

	public String getRaceName()
	{
		return raceName;
	}

	public void setRaceName(String raceName)
	{
		this.raceName = raceName;
	}

	public Participant getParticipant()
	{
		return participant;
	}

	public void setParticipant(Participant participant)
	{
		this.participant = participant;
	}
	
	

}
