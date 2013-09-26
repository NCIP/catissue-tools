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
 * <p>Title: ParticipantMedicalIdentifierBizLogic Class>
 * <p>Description:	ParticipantMedicalIdentifierBizLogic </p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author kalpana Thakur
 * @version 1.00
 */

package edu.wustl.clinportal.bizlogic;

import edu.wustl.clinportal.domain.Participant;
import edu.wustl.clinportal.domain.ParticipantMedicalIdentifier;
import edu.wustl.clinportal.domain.Site;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.global.Validator;
import edu.wustl.dao.DAO;

public class ParticipantMedicalIdentifierBizLogic extends ClinportalDefaultBizLogic
{

	/* (non-Javadoc)
	 * @see edu.wustl.common.bizlogic.DefaultBizLogic#validate(java.lang.Object, edu.wustl.dao.DAO, java.lang.String)
	 */
	protected boolean validate(Object obj, DAO dao, String operation) throws BizLogicException
	{

		ParticipantMedicalIdentifier partipantMedId = (ParticipantMedicalIdentifier) obj;

		Site site = partipantMedId.getSite();
		Participant participant = partipantMedId.getParticipant();
		String medRecrdNumber = partipantMedId.getMedicalRecordNumber();
		if (site == null || site.getId() == null)
		{
			throw getBizLogicException(null, "errors.participant.extiden.missing", "");
		}
		if (participant == null || participant.getId() == null)
		{
			throw getBizLogicException(null, "participant.medical.identifier.creation.error", "");
		}

		if (Validator.isEmpty(medRecrdNumber))
		{
			throw getBizLogicException(null, "errors.participant.extiden.missing", "");
		}
		return true;
	}

}