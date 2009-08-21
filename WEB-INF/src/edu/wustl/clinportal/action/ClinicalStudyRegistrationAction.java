/**
 * 
 * <p>Description:	This class initializes the fields in the ClinicalStudyRegistration Add/Edit webpage.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Shital Lawhale
 * @version 1.00
 * Created on May 23rd, 2005
 */

package edu.wustl.clinportal.action;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.clinportal.actionForm.ClinicalStudyRegistrationForm;
import edu.wustl.clinportal.domain.ClinicalStudy;
import edu.wustl.clinportal.domain.Participant;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.action.SecureAction;
import edu.wustl.common.bizlogic.IBizLogic;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;
import edu.wustl.common.util.logger.Logger;

public class ClinicalStudyRegistrationAction extends SecureAction
{

	/**
	 * Overrides the execute method of Action class.
	 * Sets the various fields in Participant Registration Add/Edit webpage.
	 * */
	public ActionForward executeSecureAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		//Gets the value of the operation parameter.
		String operation = request.getParameter(Constants.OPERATION);
		request.setAttribute(Constants.OPERATION, operation);
		String pageOf = request.getParameter(Constants.PAGEOF);
		request.setAttribute(Constants.PAGEOF, pageOf);
		request.setAttribute(Constants.ACTIVITYSTATUSLIST, Constants.ACTIVITY_STATUS_VALUES);
		setValues(request, form);
		return mapping.findForward(pageOf);
	}

	/**
	 * it sets CLINICAL_STUDY_LIST and participant name
	 * @param request
	 * @param form
	 * @throws BizLogicException 
	 */
	private void setValues(HttpServletRequest request, ActionForm form) throws BizLogicException
	{
		IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();		
		IBizLogic bizLogic = factory.getBizLogic(Constants.CLINICAL_STUDY_REGISTRATION_FORM_ID);

		String sourceObjectName = ClinicalStudy.class.getName();
		String[] displayNameFields = {"title"};
		String valueField = Constants.SYSTEM_IDENTIFIER;
		List list = bizLogic.getList(sourceObjectName, displayNameFields, valueField, true);
		request.setAttribute(Constants.CLINICAL_STUDY_LIST, list);

		if (request.getParameter(Constants.CP_SEARCH_CP_ID) != null)
		{
			long cpSearchCpId = Long.valueOf(request.getParameter(Constants.CP_SEARCH_CP_ID));
			((ClinicalStudyRegistrationForm) form).setClinicalStudyId(cpSearchCpId);
		}
		HashMap forwardToHashMap = (HashMap) request.getAttribute("forwardToHashMap");
		sourceObjectName = Participant.class.getName();
		//this is used in case of Participant edit -> go to register participant
		if (forwardToHashMap != null)
		{
			Long participantId = (Long) forwardToHashMap.get("participantId");

			if ((request.getParameter("firstName").trim().length() > 0)
					|| (request.getParameter("lastName").trim().length() > 0)
					|| (request.getParameter("birthDate").trim().length() > 0)
					|| ((request.getParameter("socialSecurityNumberPartA").trim().length() > 0)
							&& (request.getParameter("socialSecurityNumberPartB").trim().length() > 0) && (request
							.getParameter("socialSecurityNumberPartC").trim().length() > 0)))
			{
				ClinicalStudyRegistrationForm cprForm = (ClinicalStudyRegistrationForm) form;
				cprForm.setParticipantID(participantId.longValue());
				List participantList = bizLogic.retrieve(sourceObjectName,
						Constants.SYSTEM_IDENTIFIER, participantId);
				if (participantList != null && !participantList.isEmpty())
				{
					Participant participant = (Participant) participantList.get(0);
					cprForm.setParticipantName(participant.getMessageLabel());
				}

			}
		}
		else
		{
			//this is when participant look up table -> click on particular participant
			if (request.getParameter("participantId") != null)
			{
				try
				{
					Long participantId = Long.valueOf(request.getParameter("participantId"));
					ClinicalStudyRegistrationForm cprForm = (ClinicalStudyRegistrationForm) form;
					cprForm.setParticipantID(participantId.longValue());
					//cprForm.setCheckedButton(true);
					List participantList = bizLogic.retrieve(sourceObjectName,
							Constants.SYSTEM_IDENTIFIER, participantId);
					if (participantList != null && !participantList.isEmpty())
					{
						Participant participant = (Participant) participantList.get(0);
						cprForm.setParticipantName(participant.getMessageLabel());
					}
				}
				catch (NumberFormatException e)
				{
					Logger.out.debug("NumberFormatException Occured :" + e);
				}

			}

			if (((ClinicalStudyRegistrationForm) form).getParticipantID() != 0)
			{
				try
				{
					Long participantId = Long.valueOf(((ClinicalStudyRegistrationForm) form)
							.getParticipantID());
					ClinicalStudyRegistrationForm cprForm = (ClinicalStudyRegistrationForm) form;
					cprForm.setParticipantID(participantId.longValue());
					List participantList = bizLogic.retrieve(sourceObjectName,
							Constants.SYSTEM_IDENTIFIER, participantId);
					if (participantList != null && !participantList.isEmpty())
					{
						Participant participant = (Participant) participantList.get(0);
						cprForm.setParticipantName(participant.getMessageLabel());
					}
				}
				catch (NumberFormatException e)
				{
					Logger.out.debug("NumberFormatException Occured :" + e);
				}
			}
		}
	}

}