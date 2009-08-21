/**
 * <p>Title: ForwardToProcessor Class>
 * <p>Description:	ForwardToProcessor populates data required for ForwardTo activity</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Krunal Thakkar
 * @version 1.00
 * Created on May 08, 2006
 */

package edu.wustl.clinportal.util;

import java.util.HashMap;

import edu.wustl.clinportal.actionForm.ParticipantForm;
import edu.wustl.clinportal.domain.Participant;
import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.util.AbstractForwardToProcessor;

/**
 * ForwardToProcessor populates data required for ForwardTo activity
 * @author Krunal Thakkar
 */
public class ForwardToProcessor extends AbstractForwardToProcessor
{

	public HashMap populateForwardToData(AbstractActionForm actionForm, AbstractDomainObject domainObject)
	{
		HashMap forwardToHashMap = new HashMap();

		if (domainObject instanceof Participant)
		{
			ParticipantForm participantForm = (ParticipantForm) actionForm;

			forwardToHashMap.put("participantId", domainObject.getId());
			if (participantForm.getCpId() != -1)
			{
				forwardToHashMap.put("collectionProtocolId", Long.valueOf(participantForm.getCpId()));
			}

		}

		return forwardToHashMap;
	}
}