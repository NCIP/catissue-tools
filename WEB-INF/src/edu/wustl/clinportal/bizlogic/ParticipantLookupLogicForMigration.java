/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */

package edu.wustl.clinportal.bizlogic;

import java.util.List;

import edu.wustl.clinportal.domain.Participant;
import edu.wustl.common.lookup.DefaultLookupResult;


/**
 * @author kunal_kamble
 * This is used for finding the matching participants during the migration
 *
 */
public class ParticipantLookupLogicForMigration extends ParticipantLookupLogic
{
	
	/* (non-Javadoc)
	 * @see edu.wustl.clinportal.bizlogic.ParticipantLookupLogic
	 * #updateParticipantList(java.util.List, double, edu.wustl.clinportal.domain.Participant)
	 */
	protected void updateParticipantList(List participants,double weight, Participant existPpant)
	{
		if (isSSNOrPMI || weight > cutoffPoints)
		{
			DefaultLookupResult result = new DefaultLookupResult();
			result.setObject(existPpant);
			participants.add(result);
			result.setWeight(weight);
			result.setExactMatching(exactMatch);
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.wustl.clinportal.bizlogic.ParticipantLookupLogic#calculateCutOffPoints()
	 */
	protected int calculateCutOffPoints()
	{
	 	return CUTOFFPOINTSFROMPROPERTIES;
	}
}
