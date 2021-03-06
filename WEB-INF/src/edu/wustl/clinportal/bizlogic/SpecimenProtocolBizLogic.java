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
 * <p>Title: SpecimenProtocolBizLogic Class>
 * <p>Description:	SpecimenProtocolBizLogic is a class which contains the common methods required for 
 * Collection Protocol and Distribution Protocol.</p>
 * Copyright:    Copyright (c) year
 * 
 * @version 1.00
 * Created on April 06, 2006
 */

package edu.wustl.clinportal.bizlogic;

import java.util.Calendar;
import java.util.Date;

import edu.wustl.clinportal.domain.SpecimenProtocol;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.util.logger.Logger;

/**
 * SpecimenProtocolBizLogic is a class which contains the common methods required for 
 * Collection Protocol and Distribution Protocol.
 * @author falguni
 */
public class SpecimenProtocolBizLogic extends ClinportalDefaultBizLogic
{

	/**
	 * This method checks for the change in the Activity status of the object. If change is found
	 *  then it calls the setClosedDate() to update the End date.
	 * @param newObject Object representing the current data.
	 * @param oldObject Object before the changes.
	 */
	protected void checkForChangedStatus(SpecimenProtocol newObject, SpecimenProtocol oldObject)
	{
		Logger.out.debug("newObject.getActivityStatus() : " + newObject.getActivityStatus());
		Logger.out.debug("oldObject.getActivityStatus()   " + oldObject.getActivityStatus());

		if (!newObject.getActivityStatus().equals(oldObject.getActivityStatus()))
		{
			setClosedDate(newObject);
		}
	}

	/**
	 * @author mandar_deshmukh 
	 * This method is used for setting the Stop / End date for the Protocol.
	 * @param protocol The domain object whose date is to be set
	 */
	private void setClosedDate(SpecimenProtocol protocol)
	{
		String activityStatus = protocol.getActivityStatus();
		Logger.out.debug("in setClosedDate of ActivityStatus  : " + activityStatus);
		if (activityStatus.equalsIgnoreCase(Constants.ACTIVITY_STATUS_CLOSED))
		{
			Date currentDate = Calendar.getInstance().getTime();
			protocol.setEndDate(currentDate);
			Logger.out.debug("EndDate set");
		}
		else if (activityStatus.equalsIgnoreCase(Constants.ACTIVITY_STATUS_ACTIVE))
		{
			protocol.setEndDate(null);
			Logger.out.debug("EndDate cleared");
		}
	}

}
