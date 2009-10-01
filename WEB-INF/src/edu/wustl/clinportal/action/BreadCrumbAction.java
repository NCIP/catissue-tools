
package edu.wustl.clinportal.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.clinportal.bizlogic.ClinicalStudyEventBizlogic;
import edu.wustl.clinportal.domain.ClinicalStudyEvent;
import edu.wustl.clinportal.domain.ClinicalStudyRegistration;
import edu.wustl.clinportal.domain.EventEntry;
import edu.wustl.clinportal.domain.Participant;
import edu.wustl.clinportal.domain.RecordEntry;
import edu.wustl.clinportal.domain.StudyFormContext;
import edu.wustl.clinportal.util.DataEntryUtil;
import edu.wustl.clinportal.util.EventTreeObject;
import edu.wustl.clinportal.util.ParticipantCache;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.clinportal.util.global.Utility;
import edu.wustl.common.action.BaseAction;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.exception.DAOException;

/**
 * This class generate the breadCrumb string depending on the treeView key.
 * @author suhas_khot
 */
public class BreadCrumbAction extends BaseAction
{

	/**
	 *  Arrow delimiter image path for breadCrumb string.
	 */
	private final static String BREADCRUMBARROW = "<img alt=\"\" src=\"images/uIEnhancementImages/breadcrumb_arrow.png\">";

	/**
	 * BreadCrumb image path
	 */
	private final static String BREADCRUMBIMG = "<img align=\"top\" alt=\"BreadCrumb\" src=\"images/uIEnhancementImages/info.png\">";

	/**
	 * HTML hyperLink end tag
	 */
	private final static String HREF_END_TAG = "</a>";

	/* (non-Javadoc)
	 * @see edu.wustl.common.action.BaseAction#executeAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected ActionForward executeAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String breadCrumbString = null;
		try
		{
			breadCrumbString = getLabelForBreadCrumb(request);
			request.getSession().setAttribute("breadCrumbURLString", breadCrumbString);
			sendResponse(breadCrumbString, response);
			return null;
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage());
			return mapping.getInputForward();
		}
	}

	/**
	 * @param request request object
	 * @return breadCrumb string
	 * @throws NumberFormatException if participantId is null
	 * @throws BizLogicException fails to retrieve objects from database
	 * @throws DAOException fails to execute query
	 */
	public String getLabelForBreadCrumb(HttpServletRequest request) throws NumberFormatException,
			BizLogicException, DAOException
	{
		String participantId = request.getParameter(Constants.PARTICIPANT_ID);
		String key = request.getParameter(Constants.TREE_KEY);
		String cpId = request.getParameter(Constants.CP_SEARCH_CP_ID);
		String formId = request.getParameter(Constants.FORM_ID);
		String eventId = request.getParameter(Constants.EVENT_ID);

		ParticipantCache participantCache = new ParticipantCache();
		Participant participant = participantCache.getParticipantInfo(Long.valueOf(participantId));
		EventTreeObject eventTreeObj = new EventTreeObject();
		StringBuffer stringBuffer = new StringBuffer(525);
		eventTreeObj.populateObject(key);

		//StudyFormContext studyFormContext = getstudyFormContext(eventTreeObj);
		DataEntryUtil dataEntryUtil = new DataEntryUtil();
		StudyFormContext studyFormContext = dataEntryUtil.getStudyFormContext(eventTreeObj
				.getEventId(), eventTreeObj.getFormContextId());
		stringBuffer
				.append("<table style=\"background-color: #eef5fd;border:solid;border-width:1px;border-color:#c9ebff;padding-left: 12px;\" width=\"98%\"><tr style=\"width:100%;\"><td width=\"100%\" style=\"font-family: arial;font-size: 12px;font-weight: normal;\">&nbsp;"
						+ BREADCRUMBIMG + Constants.HTML_BLANK_SPACE1);
		stringBuffer.append(Constants.HTML_BLANK_SPACE2);
		stringBuffer
				.append("<a style=\"color:#0000ff; font-family:arial; font-size:12px; font-weight:normal; cursor:pointer;\" onclick=\"javascript:viewParticipantDetails("
						+ participant.getId() + Constants.COMMA + cpId + ");\">");
		stringBuffer.append(getParticipantName(participant));
		stringBuffer.append(Constants.HTML_BLANK_SPACE1);
		stringBuffer.append(Constants.OPENING_ROUND_BRACKET);
		stringBuffer.append(getClinicalStudyParticipantId(participant.getId(), Long.valueOf(cpId)));
		stringBuffer.append(Constants.CLOSING_ROUND_BRACKET);
		stringBuffer.append(HREF_END_TAG);
		stringBuffer.append(Constants.HTML_BLANK_SPACE2);

		if ((eventTreeObj.getObjectName().equalsIgnoreCase(Constants.FORM_ENTRY_OBJECT)
				|| eventTreeObj.getObjectName().equals(Constants.FORM_CONTEXT_OBJECT)
				|| eventTreeObj.getObjectName().equalsIgnoreCase(Constants.SINGLE_RECORD_FORM) || eventTreeObj
				.getObjectName().equalsIgnoreCase(Constants.SINGLE_RECORD_FORM_EDIT))
				&& eventId.trim().length() > 0
				&& eventTreeObj.getEventEntryNumber().trim().length() > 0
				&& formId.trim().length() > 0)
		{
			String recordLabel = getStudyFormLabel(studyFormContext, eventTreeObj);
			stringBuffer.append(BREADCRUMBARROW + Constants.HTML_BLANK_SPACE2);
			stringBuffer
					.append("<a style=\"color: #0000ff;font-family: arial;font-size: 12px;font-weight: normal;cursor:pointer;\" onclick=\"javascript:breadcrumb('ClinicalStudyEvent_"
							+ eventId
							+ "_0_0_0_0_0_0_0'"
							+ Constants.COMMA
							+ participant.getId()
							+ Constants.COMMA + cpId + Constants.COMMA + eventId + ");\">");
			stringBuffer.append(getEventNameById(Long.valueOf(eventTreeObj.getEventId())));
			stringBuffer.append(HREF_END_TAG);
			stringBuffer.append(Constants.HTML_BLANK_SPACE2 + BREADCRUMBARROW
					+ Constants.HTML_BLANK_SPACE2);
			stringBuffer
					.append("<a style=\"color: #0000ff;font-family: arial;font-size: 12px;font-weight: normal;cursor:pointer;\" onclick=\"javascript:breadcrumb('EventEntry_"
							+ eventId
							+ "_0_0_"
							+ eventTreeObj.getRegistrationId()
							+ Constants.UNDER_SCORE
							+ eventTreeObj.getEventEntryId()
							+ Constants.UNDER_SCORE
							+ eventTreeObj.getEventEntryNumber()
							+ "_0_0'"
							+ Constants.COMMA
							+ participant.getId()
							+ Constants.COMMA
							+ cpId
							+ Constants.COMMA + eventId + ");\">");
			stringBuffer.append(getVisitLabelForBreadCrumb(eventTreeObj));
			stringBuffer.append(HREF_END_TAG);
			stringBuffer.append(Constants.HTML_BLANK_SPACE2 + BREADCRUMBARROW
					+ Constants.HTML_BLANK_SPACE2);
			if (eventTreeObj.getObjectName().equalsIgnoreCase(Constants.FORM_CONTEXT_OBJECT)
					&& studyFormContext.getCanHaveMultipleRecords())
			{
				stringBuffer
						.append("<a style=\"color: #0000ff;font-family: arial;font-size: 12px;font-weight: normal;cursor:pointer;\" onclick=\"javascript:breadcrumb('"
								+ Constants.FORM_ENTRY_OBJECT
								+ Constants.UNDER_SCORE
								+ eventId
								+ Constants.UNDER_SCORE
								+ eventTreeObj.getContainerId()
								+ Constants.UNDER_SCORE
								+ eventTreeObj.getFormContextId()
								+ Constants.UNDER_SCORE
								+ eventTreeObj.getRegistrationId()
								+ Constants.UNDER_SCORE
								+ eventTreeObj.getEventEntryId()
								+ Constants.UNDER_SCORE
								+ eventTreeObj.getEventEntryNumber()
								+ "_0_0'"
								+ Constants.COMMA
								+ participant.getId()
								+ Constants.COMMA
								+ cpId + Constants.COMMA + eventId + ");\">");
				stringBuffer.append(studyFormContext.getStudyFormLabel());
				stringBuffer.append(HREF_END_TAG);
				stringBuffer.append(Constants.HTML_BLANK_SPACE2 + BREADCRUMBARROW
						+ Constants.HTML_BLANK_SPACE2);
			}
			stringBuffer.append(recordLabel);
		}
		else if (eventTreeObj.getObjectName().equalsIgnoreCase(Constants.EVENT_ENTRY_OBJECT)
				&& eventTreeObj.getEventId().trim().length() > 0
				&& eventTreeObj.getEventEntryNumber().trim().length() > 0)
		{
			stringBuffer.append(Constants.HTML_BLANK_SPACE1 + BREADCRUMBARROW
					+ Constants.HTML_BLANK_SPACE2);
			stringBuffer
					.append("<a onclick=\"javascript:breadcrumb('ClinicalStudyEvent_"
							+ eventId
							+ "_0_0_0_0_0_0_0'"
							+ ","
							+ participant.getId()
							+ Constants.COMMA
							+ cpId
							+ Constants.COMMA
							+ eventId
							+ ");\" style=\"color: #0000ff;font-family: arial;font-size: 12px;font-weight: normal;cursor:pointer;\">");
			stringBuffer.append(getEventNameById(Long.valueOf(eventTreeObj.getEventId())));
			stringBuffer.append(HREF_END_TAG);
			stringBuffer.append(Constants.HTML_BLANK_SPACE2 + BREADCRUMBARROW
					+ Constants.HTML_BLANK_SPACE2);
			stringBuffer.append(getVisitLabelForBreadCrumb(eventTreeObj));
		}
		else if (eventTreeObj.getObjectName().equalsIgnoreCase(
				Constants.CLINICAL_STUDY_EVENT_OBJECT)
				&& eventTreeObj.getEventId().trim().length() > 0)
		{
			stringBuffer.append(Constants.HTML_BLANK_SPACE1 + BREADCRUMBARROW
					+ Constants.HTML_BLANK_SPACE2);
			stringBuffer.append(getEventNameById(Long.valueOf(eventTreeObj.getEventId())));
		}
		stringBuffer.append("</td></tr></table>");
		return stringBuffer.toString();
	}

	/**
	 * @param responseXML output to be send as response
	 * @param response response object
	 * @throws IOException fails to get writer object
	 */
	private void sendResponse(String responseXML, HttpServletResponse response) throws IOException
	{
		PrintWriter out = response.getWriter();
		response.setContentType(Constants.CONTENT_TYPE_XML);
		out.write(responseXML);
	}

	/**
	* @param eventId event ID to retrieve event name
	* @return event name based on event Id
	* @throws BizLogicException fails to retrieve event name
	*/
	private String getEventNameById(Long eventId) throws BizLogicException
	{
		ClinicalStudyEventBizlogic bizlogic = new ClinicalStudyEventBizlogic();
		ClinicalStudyEvent event = bizlogic.getClinicalStudyEventById(eventId);
		String eventName = null;
		if (event != null)
		{
			eventName = event.getCollectionPointLabel();
		}
		return eventName;
	}

	/**
	 * @param eventTreeObj eventTree object
	 * @return visit label with or without encounter date.
	 * @throws DAOException fails to retrieve encounter date
	 */
	private String getVisitLabelForBreadCrumb(EventTreeObject eventTreeObj) throws DAOException
	{
		StringBuffer visitLabel = new StringBuffer();
		visitLabel.append(Constants.VISIT_DASH);
		visitLabel.append(eventTreeObj.getEventEntryNumber());
		String encounterdate = getEncounterDateForVisit(eventTreeObj.getEventId(), eventTreeObj
				.getRegistrationId(), eventTreeObj.getEventEntryId(), eventTreeObj
				.getEventEntryNumber());
		if (encounterdate != null && encounterdate.trim().length() > 0)
		{
			visitLabel.append(Constants.OPENING_ROUND_BRACKET);
			visitLabel.append(encounterdate);
			visitLabel.append(Constants.CLOSING_ROUND_BRACKET);
		}
		return visitLabel.toString();
	}

	/**
	 * @param studyFormContext studyFormContext object
	 * @param eventTreeObj eventTree Object
	 * @return Study Form Label for a study
	 * @throws DAOException fails to retrieve record.
	 */
	private String getStudyFormLabel(StudyFormContext studyFormContext, EventTreeObject eventTreeObj)
			throws DAOException
	{
		StringBuffer recEntryHQL = new StringBuffer(125);
		recEntryHQL.append("select recEntry.id,recEntry.studyFormContext.id from ");
		recEntryHQL.append(RecordEntry.class.getName());
		recEntryHQL.append(" recEntry where recEntry.eventEntry.id=");
		recEntryHQL.append(eventTreeObj.getEventEntryId());
		recEntryHQL.append(" order by recEntry.id");
		String recordLabel = null;
		List<Object[]> recEntryList = null;
		recEntryList = Utility.executeQuery(recEntryHQL.toString());
		if (recEntryList != null && !recEntryList.isEmpty()
				&& eventTreeObj.getObjectName().equalsIgnoreCase(Constants.FORM_CONTEXT_OBJECT)
				&& studyFormContext.getCanHaveMultipleRecords())
		{
			recordLabel = getRecordLabel(recEntryList, eventTreeObj);
		}
		else
		{
			recordLabel = studyFormContext.getStudyFormLabel();
		}
		return recordLabel;
	}

	/**
	 * @param recEntryList collection of records for a event
	 * @param eventTreeObj
	 * @return record label
	 */
	private String getRecordLabel(List<Object[]> recEntryList, EventTreeObject eventTreeObj)
	{
		int cnt = 1;
		for (Object[] recordEntry : recEntryList)
		{
			if (eventTreeObj.getFormContextId().equalsIgnoreCase(recordEntry[1].toString())
					&& recordEntry[0].toString().equalsIgnoreCase(eventTreeObj.getRecEntryId()))
			{
				break;
			}
			else if (eventTreeObj.getFormContextId().equalsIgnoreCase(recordEntry[1].toString())
					&& !recordEntry[0].toString().equalsIgnoreCase(eventTreeObj.getRecEntryId()))
			{
				cnt = cnt + 1;
			}
		}
		return (Constants.RECORD_DASH + cnt);

	}

	/**
	 * @param eventId event Id
	 * @param registrationId clinical study registration Id
	 * @param eventEntryId event entry Id
	 * @param eventEntryNumber number of events
	 * @return encounterDate For Visit
	 * @throws DAOException fails to execute query
	 */
	private String getEncounterDateForVisit(String eventId, String registrationId,
			String eventEntryId, String eventEntryNumber) throws DAOException
	{
		String encounterDate = null;
		if (eventId != null && eventId.trim().length() > 0 && registrationId != null
				&& registrationId.trim().length() > 0 && eventEntryNumber != null
				&& eventEntryNumber.trim().length() > 0)
		{
			StringBuffer eventEntryHQL = new StringBuffer();
			eventEntryHQL
					.append("select eventEntry.id,eventEntry.encounterDate,eventEntry.entryNumber from ");
			eventEntryHQL.append(EventEntry.class.getName());
			eventEntryHQL.append(" as eventEntry where eventEntry.clinicalStudyEvent.id=");
			eventEntryHQL.append(eventId);
			eventEntryHQL.append(" and eventEntry.clinicalStudyRegistration.id=");
			eventEntryHQL.append(registrationId);
			List<Object[]> evntEntryColn = Utility.executeQuery(eventEntryHQL.toString());
			for (Object[] eventNtry : evntEntryColn)
			{
				if (eventNtry[Constants.EVENT_ENTRY_ID] != null
						&& eventNtry[Constants.EVENT_ENTRY_ID].toString().equals(eventEntryId)
						&& eventNtry[Constants.ENTRY_NUM_INDEX] != null
						&& eventNtry[Constants.ENTRY_NUM_INDEX].toString().equals(eventEntryNumber)
						&& eventNtry[Constants.ENCOUNTERDATE_INDEX] != null)
				{
					encounterDate = eventNtry[Constants.ENCOUNTERDATE_INDEX].toString();
					break;
				}
			}
		}
		return encounterDate;
	}

	/**
	 * @param participant
	 * @return name of participant
	 */
	private String getParticipantName(Participant participant)
	{
		StringBuffer participantName = new StringBuffer();
		if (participant.getFirstName() == null && participant.getLastName() == null)
		{
			participantName.append("N/A");
		}
		else if (participant.getLastName() != null && participant.getLastName().trim().length() > 0
				&& participant.getFirstName() != null
				&& participant.getFirstName().trim().length() > 0)
		{
			participantName.append(participant.getLastName());
			participantName.append(Constants.HTML_BLANK_SPACE1);
			participantName.append(Constants.COMMA);
			participantName.append(Constants.HTML_BLANK_SPACE1);
			participantName.append(participant.getFirstName());
		}
		else if (participant.getFirstName() != null
				&& participant.getFirstName().trim().length() > 0)
		{
			participantName.append(participant.getFirstName());
		}
		else if (participant.getLastName() != null && participant.getLastName().trim().length() > 0)
		{
			participantName.append(participant.getLastName());
		}
		return participantName.toString();
	}

	/**
	 * @param participantId participant id
	 * @param clinicalStudyId clinical study id
	 * @return clinical study participantId
	 * @throws DAOException fails to get clinical study participant id
	 */
	private String getClinicalStudyParticipantId(Long participantId, Long clinicalStudyId)
			throws DAOException
	{
		StringBuffer recEntryHQL = new StringBuffer(125);
		recEntryHQL.append("select CSReg.clinicalStudyParticipantIdentifier from ");
		recEntryHQL.append(ClinicalStudyRegistration.class.getName());
		recEntryHQL.append(" CSReg where CSReg.participant.id=");
		recEntryHQL.append(participantId);
		recEntryHQL.append(" and CSReg.clinicalStudy.id=");
		recEntryHQL.append(clinicalStudyId);
		String clinicalStudyParticipantId = null;
		List<Object> clinStudyPartIdColl = Utility.executeQuery(recEntryHQL.toString()); // NOPMD by suhas_khot on 8/27/09 4:51 PM
		if (clinStudyPartIdColl != null && !clinStudyPartIdColl.isEmpty()
				&& clinStudyPartIdColl.iterator().next() != null)
		{
			clinicalStudyParticipantId = clinStudyPartIdColl.iterator().next()
					.toString();
		}
		else
		{
			clinicalStudyParticipantId = "N/A";
		}
		return clinicalStudyParticipantId;
	}
}