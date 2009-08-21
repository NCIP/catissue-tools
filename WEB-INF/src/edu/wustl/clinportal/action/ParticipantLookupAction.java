/**
 * <p>Title: ParticipantLookupAction Class>
 * <p>Description:	This Action Class invokes the Participant Lookup Algorithm and gets matching participants</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author vaishali_khandelwal
 * @Created on May 19, 2006
 */

package edu.wustl.clinportal.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import edu.wustl.clinportal.bizlogic.ParticipantBizLogic;
import edu.wustl.clinportal.domain.Participant;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.action.BaseAction;
import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IDomainObjectFactory;
import edu.wustl.common.factory.IFactory;
import edu.wustl.common.lookup.DefaultLookupResult;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.exception.DAOException;

/**
 * 
 * @author deepali_ahirrao
 *
 */
public class ParticipantLookupAction extends BaseAction
{

	/**
	 * @param mapping object of ActionMapping
	 * @param form object of ActionForm
	 * @param request object of HttpServletRequest
	 * @param response object of HttpServletResponse
	 * @throws Exception generic exception
	 * @return value for ActionForward object
	 */
	@Override
	public ActionForward executeAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String target = null; // NOPMD - DD anomaly

		Logger.out.debug("Participant Id :" + request.getParameter("participantId"));
		//checks weather participant is selected from the list and so forwarding to next action instead of participant lookup.
		boolean isForward = checkForwardToParticipantSelectAction(request);
		if (isForward)
		{
			target = "participantSelect";
		}
		else
		{
			AbstractActionForm abstractForm = (AbstractActionForm) form;
			IDomainObjectFactory domainObjectFactory = AbstractFactoryConfig.getInstance()
					.getDomainObjectFactory();
			AbstractDomainObject abstractDomain = domainObjectFactory.getDomainObject(abstractForm
					.getFormId(), abstractForm);
			Participant participant = (Participant) abstractDomain;

			boolean isCallToLkupLgic = false;
			//isCallToLookupLogicNeeded(participant);

			if (isCallToLkupLgic)
			{
				IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
				ParticipantBizLogic bizlogic = (ParticipantBizLogic) factory
						.getBizLogic(Constants.PARTICIPANT_FORM_ID);
				SessionDataBean sessionDataBean = getSessionData(request);
				List matchPartpantLst = bizlogic.getListOfMatchingParticipants(participant,
						sessionDataBean, null);
				if (matchPartpantLst != null && !matchPartpantLst.isEmpty())
				{
					ActionMessages messages = new ActionMessages();
					messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
							"participant.lookup.success",
							"Submit was not successful because some matching participants found."));
					//Creating the column headings for Data Grid
					List columnList = getColumnHeadingList(bizlogic);
					request.setAttribute(
							edu.wustl.common.util.global.Constants.SPREADSHEET_COLUMN_LIST,
							columnList);

					//Get the Participant List in Data Grid Format
					List pcpantDisplayLst = getParticipantDisplayList(matchPartpantLst);
					request.setAttribute(Constants.SPREADSHEET_DATA_LIST, pcpantDisplayLst);

					target = Constants.PARTICIPANT_LOOKUP_SUCCESS;

					if (request.getAttribute("continueLookup") == null)
					{
						saveMessages(request, messages);
					}
				}
				//	if no participant match found then add the participant in system
				else
				{
					target = Constants.PARTICIPANT_ADD_FORWARD;
				}
			}
			else
			{
				target = Constants.PARTICIPANT_ADD_FORWARD;
			}

			//if any matching participants are there then show the participants otherwise add the participant
			//setting the Submitted_for and Forward_to variable in request
			setRequestAttributes(request);
		}

		Logger.out.debug("target:" + target);
		return mapping.findForward(target);
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	private boolean checkForwardToParticipantSelectAction(HttpServletRequest request)
	{
		boolean isForward = false; // NOPMD - DD anomaly
		String participantId = "participantId";
		
		if (request.getParameter("continueLookup") == null && request.getAttribute("continueLookup") == null
				&& request.getParameter(participantId) != null
				&& !request.getParameter(participantId).equals("null")
				&& !request.getParameter(participantId).equals("")
				&& !request.getParameter(participantId).equals("0"))
		{
			Logger.out.info("inside the participant mapping");
			isForward = true;
		}

		return isForward;
	}

	/**
	 * 
	 * @param request
	 */
	private void setRequestAttributes(HttpServletRequest request)
	{
		if (request.getParameter(Constants.SUBMITTED_FOR) != null
				&& !request.getParameter(edu.wustl.common.util.global.Constants.SUBMITTED_FOR)
						.equals(""))
		{
			request.setAttribute(Constants.SUBMITTED_FOR, request
					.getParameter(Constants.SUBMITTED_FOR));
		}
		if (request.getParameter(Constants.FORWARD_TO) != null
				&& !request.getParameter(Constants.FORWARD_TO).equals(""))
		{
			request.setAttribute(Constants.FORWARD_TO, request.getParameter(Constants.FORWARD_TO));

		}

		request.setAttribute("participantId", "");
	}

	/**
	 * @param participant
	 * @return
	 */
	private boolean isCallToLookupLogicNeeded(Participant participant)
	{
		boolean flag = true; // NOPMD - DD anomaly
		if ((participant.getFirstName() == null || participant.getFirstName().length() == 0)
				&& (participant.getMiddleName() == null || participant.getMiddleName().length() == 0)
				&& (participant.getLastName() == null || participant.getLastName().length() == 0)
				&& (participant.getSocialSecurityNumber() == null || participant
						.getSocialSecurityNumber().length() == 0)
				&& participant.getBirthDate() == null
				&& (participant.getParticipantMedicalIdentifierCollection() == null || participant
						.getParticipantMedicalIdentifierCollection().size() == 0))
		{
			flag = false;
		}
		return flag;
	}

	/**
	 * This Function creates the Column Headings for Data Grid
	 * @param bizlogic instance of ParticipantBizLogic
	 * @throws Exception generic exception
	 * @return List Column List
	 * @throws DAOException 
	 */
	private List getColumnHeadingList(ParticipantBizLogic bizlogic) throws DAOException
	{
		//Creating the column list which is used in Data grid to display column headings
		String[] columnHeaderList = new String[]{Constants.PARTICIPANT_LAST_NAME,
				Constants.PARTICIPANT_FIRST_NAME, Constants.PARTICIPANT_MIDDLE_NAME,
				Constants.PARTICIPANT_BIRTH_DATE, Constants.PARTICIPANT_DEATH_DATE,
				Constants.PARTICIPANT_VITAL_STATUS, Constants.PARTICIPANT_GENDER,
				Constants.PARTICIPANT_SOCIAL_SECURITY_NUMBER,
				Constants.PARTICIPANT_MEDICAL_RECORD_NO};
		List columnList = new ArrayList();
		Logger.out.info("column List header size ;" + columnHeaderList.length);
		for (int i = 0; i < columnHeaderList.length; i++)
		{
			columnList.add(columnHeaderList[i]);
		}
		Logger.out.info("column List size ;" + columnList.size());
		List displayList = bizlogic.getColumnList(columnList);

		return displayList;
	}

	/**
	 * This functions creates Participant List with each participant information  with the match probability 
	 * @param participantList list of participant
	 * @return List of Participant Information  List
	 */
	private List getParticipantDisplayList(List participantList)
	{
		List pcpantDisplaylst = new ArrayList();
		Iterator itr = participantList.iterator();
		String medicalRecordNo = "";
		//String siteId = "";
		while (itr.hasNext())
		{
			DefaultLookupResult result = (DefaultLookupResult) itr.next();
			Participant participant = (Participant) result.getObject();

			List participantInfo = new ArrayList();

			participantInfo.add(Utility.toString(participant.getLastName()));
			participantInfo.add(Utility.toString(participant.getFirstName()));
			participantInfo.add(Utility.toString(participant.getMiddleName()));
			participantInfo.add(Utility.toString(participant.getBirthDate()));
			participantInfo.add(Utility.toString(participant.getDeathDate()));
			participantInfo.add(Utility.toString(participant.getVitalStatus()));
			participantInfo.add(Utility.toString(participant.getGender()));
			participantInfo.add(Utility.toString(participant.getSocialSecurityNumber()));
			if (participant.getParticipantMedicalIdentifierCollection() != null)
			{
				Iterator pcpantMedIdItr = participant.getParticipantMedicalIdentifierCollection()
						.iterator();
				while (pcpantMedIdItr.hasNext())
				{
					medicalRecordNo = (String) pcpantMedIdItr.next();
					pcpantMedIdItr.next();
				}
			}
			participantInfo.add(Utility.toString(medicalRecordNo));
			participantInfo.add(participant.getId());
			pcpantDisplaylst.add(participantInfo);

		}
		return pcpantDisplaylst;
	}
}