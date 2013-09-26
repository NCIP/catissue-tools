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
 * 
 */

package edu.wustl.clinportal.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.clinportal.domain.ClinicalStudyEvent;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.action.BaseAction;
import edu.wustl.common.bizlogic.DefaultBizLogic;

/**
 * @author falguni
 *
 * This action is to disable the events
 */
public class DisableCSEventsAction extends BaseAction
{

	/* (non-Javadoc)
	 * @see edu.wustl.common.action.BaseAction#executeAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected ActionForward executeAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{

		String eventId = request.getParameter(Constants.PROTOCOL_EVENT_ID);

		DefaultBizLogic defBizLogic = new DefaultBizLogic();
		/*
		 * Get the eventObject from the id, and disable its foreign key constraint (CLINICAL_STUDY_ID)
		 * and update the event.
		 * 
		 */
		List eventList = defBizLogic.retrieve(ClinicalStudyEvent.class.getName(), "id", eventId);

		if (eventList != null && !eventList.isEmpty())
		{
			ClinicalStudyEvent eventObj = (ClinicalStudyEvent) eventList.get(0);

			eventObj.setClinicalStudy(null);
			defBizLogic.update(eventObj, Constants.HIBERNATE_DAO);
		
		}

		String statusMessage = "clinicalStudyEvent.disable.success";
		request.setAttribute(edu.wustl.common.util.global.Constants.STATUS_MESSAGE_KEY, statusMessage);

		return mapping.findForward(Constants.SUCCESS);
	}

}
