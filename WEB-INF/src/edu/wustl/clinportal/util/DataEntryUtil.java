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
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright:TODO</p>
 *@author 
 *@version 1.0
 */

package edu.wustl.clinportal.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.ehcache.CacheException;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;

import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.clinportal.bizlogic.AnnotationBizLogic;
import edu.wustl.clinportal.bizlogic.ClinicalStudyBizLogic;
import edu.wustl.clinportal.bizlogic.EventEntryBizlogic;
import edu.wustl.clinportal.bizlogic.RecordEntryBizLogic;
import edu.wustl.clinportal.domain.ClinicalStudyEvent;
import edu.wustl.clinportal.domain.ClinicalStudyRegistration;
import edu.wustl.clinportal.domain.EventEntry;
import edu.wustl.clinportal.domain.RecordEntry;
import edu.wustl.clinportal.domain.StudyFormContext;
import edu.wustl.clinportal.util.global.Utility;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.bizlogic.AbstractBizLogic;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.global.Constants;
import edu.wustl.dao.DAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.security.exception.UserNotAuthorizedException;

/**
 * @author falguni_sachde
 *
 */
public class DataEntryUtil
{

	/**
	 * 
	 * @param eventTreeObject
	 * @param dynExtRecordId
	 * @param request
	 * @throws CacheException
	 * @throws DAOException
	 * @throws BizLogicException
	 * @throws UserNotAuthorizedException
	 * @throws DynamicExtensionsSystemException 
	 * @throws DynamicExtensionsApplicationException 
	 */
	public void insertEventEntry(EventTreeObject eventTreeObject, String dynExtRecordId,
			HttpServletRequest request) throws CacheException, DAOException, BizLogicException,
			UserNotAuthorizedException, DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException
	{
		ClinicalStudyEvent event = null;
		StudyFormContext frmCntxt = null;
		ClinicalStudyRegistration csReg = null;
		ClinicalStudyBizLogic bizLogic = new ClinicalStudyBizLogic();
		List objList = bizLogic.retrieve(ClinicalStudyEvent.class.getName(), "id", Long
				.valueOf(eventTreeObject.getEventId()));
		if (objList != null && !objList.isEmpty())
		{
			event = (ClinicalStudyEvent) objList.get(0);
		}
		objList = new ArrayList();
		objList = bizLogic.retrieve(ClinicalStudyRegistration.class.getName(), "id", Long
				.valueOf(eventTreeObject.getRegistrationId()));
		if (objList != null && !objList.isEmpty())
		{
			csReg = (ClinicalStudyRegistration) objList.get(0);
		}
		objList = new ArrayList();
		objList = bizLogic.retrieve(StudyFormContext.class.getName(), "id", Long
				.valueOf(eventTreeObject.getFormContextId()));
		if (objList != null && !objList.isEmpty())
		{
			frmCntxt = (StudyFormContext) objList.get(0);
		}

		formEventEntry(eventTreeObject, csReg, event, frmCntxt, dynExtRecordId, request);

	}

	/**
	 * This method populates objects using keys and only inserts into database
	 * if record is updated then nothing will happen
	 * @param eventTreeObject
	 * @param csReg
	 * @param event
	 * @param frmCntxt
	 * @param dynExtRecordId
	 * @param request
	 * @throws CacheException
	 * @throws DAOException
	 * @throws BizLogicException
	 * @throws UserNotAuthorizedException
	 * @throws DynamicExtensionsSystemException 
	 * @throws DynamicExtensionsApplicationException 
	 */

	private void formEventEntry(EventTreeObject eventTreeObject, ClinicalStudyRegistration csReg,
			ClinicalStudyEvent event, StudyFormContext frmCntxt, String dynExtRecordId,
			HttpServletRequest request) throws CacheException, DAOException, BizLogicException,
			UserNotAuthorizedException, DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException
	{
		SessionDataBean sessionBean = (SessionDataBean) request.getSession().getAttribute(
				Constants.SESSION_DATA);

		EventEntry eventEntry = new EventEntry();

		AbstractBizLogic evntEntrybLogic = new EventEntryBizlogic();
		String eventEntryID = eventTreeObject.getEventEntryId();
		String recordEntryId = eventTreeObject.getRecEntryId();

		eventEntry.setEntryNumber(Integer.valueOf(eventTreeObject.getEventEntryNumber()));
		eventEntry.setClinicalStudyRegistration(csReg);
		eventEntry
				.setActivityStatus(edu.wustl.clinportal.util.global.Constants.ACTIVITY_STATUS_ACTIVE);
		eventEntry.setClinicalStudyEvent(event);

		RecordEntry recEntry = new RecordEntry();
		recEntry
				.setActivityStatus(edu.wustl.clinportal.util.global.Constants.ACTIVITY_STATUS_ACTIVE);
		recEntry.setEventEntry(eventEntry);
		recEntry.setStudyFormContext(frmCntxt);

		// If EventEntry and RecordEntry doesn't exists 
		if (("0".equals(eventEntryID) || eventEntryID.contains("-")) && "0".equals(recordEntryId))
		{
			Collection<RecordEntry> recEntryColn = new HashSet<RecordEntry>();
			recEntryColn.add(recEntry);
			eventEntry.setRecordEntryCollection(recEntryColn);
			evntEntrybLogic.insert(eventEntry, sessionBean,
					edu.wustl.clinportal.util.global.Constants.HIBERNATE_DAO);
		}
		//If EventEntry exists and RecordEntry doesn't exists 
		else if ((!"0".equals(eventEntryID) || !eventEntryID.contains("-"))
				&& "0".equals(recordEntryId))
		{
			EventEntry entry = null;
			eventEntry.setId(Long.valueOf(eventTreeObject.getEventEntryId()));
			//retrieve Event entry as encounterDate can be set in eventEntry
			List<EventEntry> objList = evntEntrybLogic.retrieve(EventEntry.class.getName(), "id",
					Long.valueOf(eventTreeObject.getEventEntryId()));
			if (objList != null && !objList.isEmpty())
			{
				entry = (EventEntry) objList.get(0);
			}
			recEntry.setEventEntry(entry);
			RecordEntryBizLogic recEntryBLogic = new RecordEntryBizLogic();
			recEntryBLogic.insert(recEntry, sessionBean,
					edu.wustl.clinportal.util.global.Constants.HIBERNATE_DAO);
		}
		//both EventEntry and RecordEntry exists 
		else
		{
			eventEntry.setId(Long.valueOf(eventEntryID));
			recEntry.setId(Long.valueOf(recordEntryId));
		}
		eventTreeObject.setEventEntryId(String.valueOf(eventEntry.getId()));
		eventTreeObject.setDynamicRecId(dynExtRecordId);
		eventTreeObject.setRecEntryId(String.valueOf(recEntry.getId()));
		if (recordEntryId != null && recordEntryId.equals("0"))
		{
			AnnotationBizLogic annoBizLogic = new AnnotationBizLogic();
			annoBizLogic.associateRecords(Long.valueOf(eventTreeObject.getContainerId()), recEntry
					.getId(), Long.valueOf(dynExtRecordId));
		}
	}

	/**
	 * Returns event entry object on given ClinicalStudyEvent and clinicalStudyRegistration object
	 * @param event
	 * @param clStudyRegistion
	 * @return
	 * @throws BizLogicException 
	 */
	public List<EventEntry> getEventEntry(ClinicalStudyEvent event,
			ClinicalStudyRegistration clStudyRegistion) throws BizLogicException
	{
		String[] whereColumnNames = {"clinicalStudyEvent.id", "clinicalStudyRegistration.id"};
		String[] whrColCondns = {"=", "="};
		Object[] whereColumnValues = {event.getId(), clStudyRegistion.getId()};

		DefaultBizLogic bizLogic = new DefaultBizLogic();

		List<EventEntry> evntEntryColn = bizLogic.retrieve(EventEntry.class.getName(),
				whereColumnNames, whrColCondns, whereColumnValues, Constants.AND_JOIN_CONDITION);
		return evntEntryColn;
	}

	/**
	 * Returns event entry object on given ClinicalStudyEvent and clinicalStudyRegistration object
	 * @param event
	 * @param clStudyRegtn
	 * @return
	 * @throws BizLogicException
	 */
	public boolean checkForEncounterDate(ClinicalStudyEvent event,
			ClinicalStudyRegistration clStudyRegtn) throws BizLogicException
	{
		String[] whereColumnNames = {"clinicalStudyEvent.id", "clinicalStudyRegistration.id",
				"encounterDate"};
		String[] whereColCondns = {"=", "=", "is not null"};
		Object[] whereColumnValues = {event.getId(), clStudyRegtn.getId()};

		DefaultBizLogic bizLogic = new DefaultBizLogic();
		List<EventEntry> evntEntryColn = bizLogic.retrieve(EventEntry.class.getName(),
				whereColumnNames, whereColCondns, whereColumnValues, Constants.AND_JOIN_CONDITION);

		boolean flag = false;
		if (evntEntryColn != null && !evntEntryColn.isEmpty())
		{
			flag = true;
		}
		return flag;
	}

	/**
	 * Returns event entry object on given ClinicalStudyEvent and clinicalStudyRegistration object
	 * @param event
	 * @param clStudyRegn
	 * @param entryNum
	 * @return
	 * @throws BizLogicException
	 */
	public List<EventEntry> getEventEntryFromEntryNum(ClinicalStudyEvent event,
			ClinicalStudyRegistration clStudyRegn, Integer entryNum) throws BizLogicException
	{
		String[] whereColumnNames = {"clinicalStudyEvent.id", "clinicalStudyRegistration.id",
				"entryNumber"};
		String[] whereColConds = {"=", "=", "="};
		Object[] whereColumnValues = {event.getId(), clStudyRegn.getId(), entryNum};

		DefaultBizLogic bizLogic = new DefaultBizLogic();
		List<EventEntry> evntEntryColn = bizLogic.retrieve(EventEntry.class.getName(),
				whereColumnNames, whereColConds, whereColumnValues, Constants.AND_JOIN_CONDITION);
		return evntEntryColn;
	}

	/**
	 * Insert event entry object on given ClinicalStudyEvent and clinicalStudyRegistration object
	 * @param event
	 * @param clStudyRegistion
	 * @param entryNum
	 * @param request
	 * @return
	 * @throws DAOException
	 * @throws BizLogicException 
	 * @throws UserNotAuthorizedException 
	 */
	public EventEntry createEventEntry(ClinicalStudyEvent event,
			ClinicalStudyRegistration clStudyRegistion, Integer entryNum, HttpServletRequest request)
			throws DAOException, UserNotAuthorizedException, BizLogicException
	{
		SessionDataBean sessionDataBean = (SessionDataBean) request.getSession().getAttribute(
				Constants.SESSION_DATA);
		AbstractBizLogic bizLogic = new EventEntryBizlogic();
		EventEntry eventEntry = new EventEntry();
		eventEntry
				.setActivityStatus(edu.wustl.clinportal.util.global.Constants.ACTIVITY_STATUS_ACTIVE);
		eventEntry.setEntryNumber(Integer.valueOf(entryNum));
		eventEntry.setClinicalStudyRegistration(clStudyRegistion);
		eventEntry.setClinicalStudyEvent(event);
		bizLogic.insert(eventEntry, sessionDataBean,
				edu.wustl.clinportal.util.global.Constants.HIBERNATE_DAO);
		return eventEntry;

	}

	/**
	 * Insert event entry object on given ClinicalStudyEvent and clinicalStudyRegistration object
	 * @param eventEntry
	 * @param studyFormContext
	 * @return
	 * @throws DAOException
	 * @throws BizLogicException 
	 * @throws UserNotAuthorizedException 
	 */
	public RecordEntry createRecordEntry(EventEntry eventEntry, StudyFormContext studyFormContext)
			throws DAOException, UserNotAuthorizedException, BizLogicException
	{

		AbstractBizLogic bizLogic = new EventEntryBizlogic();
		RecordEntry recordEntry = new RecordEntry();
		recordEntry.setEventEntry(eventEntry);
		recordEntry.setStudyFormContext(studyFormContext);
		eventEntry.getRecordEntryCollection().add(recordEntry);
		bizLogic.update(eventEntry, eventEntry,
				edu.wustl.clinportal.util.global.Constants.HIBERNATE_DAO, null);
		return recordEntry;

	}

	/**
	 * Returns record entry object on given entry and Study form context ids 
	 * @param eventEntryId
	 * @param frmCntxtId
	 * @return
	 * @throws BizLogicException
	 */
	public List<RecordEntry> getRecordEntry(Long eventEntryId, Long frmCntxtId)
			throws BizLogicException
	{
		String[] whereColumnNames = {"eventEntry.id", "studyFormContext.id"};
		String[] whrColConds = {"=", "="};
		Object[] whereColumnValues = {eventEntryId, frmCntxtId};
		DefaultBizLogic bizLogic = new DefaultBizLogic();

		List<RecordEntry> recEntryColln = bizLogic.retrieve(RecordEntry.class.getName(),
				whereColumnNames, whrColConds, whereColumnValues, Constants.AND_JOIN_CONDITION);
		return recEntryColln;
	}

	/**
	 * @param hql
	 * @return
	 * @throws DAOException
	 */
	public List executeQuery(String hql) throws DAOException
	{		
		String appName = CommonServiceLocator.getInstance().getAppName();
		IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
		DAO dao = daoFactory.getDAO();
		dao.openSession(null);
		List list;
		try
		{			
			list = dao.executeQuery(hql);
		}		
		finally
		{
			dao.closeSession();
		}
		return list;
	}

	/**
	 * this will generate event entry id and set the tree object accordingly
	 * @param treeObject
	 * @throws BizLogicException 
	 */
	public void genrateEventKey(EventTreeObject treeObject) throws BizLogicException
	{
		DataEntryUtil util = new DataEntryUtil();
		Long eventId = Long.valueOf(treeObject.getEventId());
		Long regId = Long.valueOf(treeObject.getRegistrationId());
		ClinicalStudyEvent event = new ClinicalStudyEvent();
		event.setId(eventId);

		ClinicalStudyRegistration reg = new ClinicalStudyRegistration();
		reg.setId(regId);
		List evententryList = util.getEventEntryFromEntryNum(event, reg, Integer.valueOf(treeObject
				.getEventEntryNumber()));
		if (evententryList != null && !evententryList.isEmpty())
		{
			EventEntry entry = (EventEntry) evententryList.get(0);
			treeObject.setEventEntryId(entry.getId().toString());
		}

	}

	/**
	 * @param request
	 * @param participantId
	 * @param studyId
	 * @param regId
	 * @return
	 * @throws DAOException
	 * This will return the activity status of participant.
	 */
	public ActionErrors getClosedStatus(HttpServletRequest request, String participantId,
			String studyId, String regId) throws DAOException
	{
		String activityStatus = null;
		String activityStatusHQL = "select cs.activityStatus "
				+ "from edu.wustl.clinportal.domain.ClinicalStudy " + "as cs where cs.id="
				+ studyId;

		List actStatusList = executeQuery(activityStatusHQL);
		ActionErrors errors = new ActionErrors();
		ActionError error = null;
		if (actStatusList != null && !actStatusList.isEmpty())
		{
			activityStatus = (actStatusList.get(0)).toString();
		}
		if (activityStatus
				.equals(edu.wustl.clinportal.util.global.Constants.ACTIVITY_STATUS_CLOSED))
		{
			error = new ActionError("clinicalstudy.closed");
			errors.add(ActionErrors.GLOBAL_ERROR, error);

		}
		else
		{
			Long partId = Long.valueOf(participantId);
			activityStatusHQL = "select part.activityStatus "
					+ "from edu.wustl.clinportal.domain.Participant " + "as part where part.id= "
					+ partId;
			actStatusList = executeQuery(activityStatusHQL);
			if (actStatusList != null && !actStatusList.isEmpty())
			{
				activityStatus = (actStatusList.get(0)).toString();
			}
			if (activityStatus
					.equals(edu.wustl.clinportal.util.global.Constants.ACTIVITY_STATUS_CLOSED))
			{
				error = new ActionError("participant.closed");
				errors.add(ActionErrors.GLOBAL_ERROR, error);

			}
			else
			{
				activityStatusHQL = "select csr.activityStatus "
						+ "from edu.wustl.clinportal.domain.ClinicalStudyRegistration"
						+ " as csr where csr.id=" + regId;
				actStatusList = executeQuery(activityStatusHQL);
				if (actStatusList != null && !actStatusList.isEmpty())
				{
					activityStatus = (actStatusList.get(0)).toString();
				}
				if (activityStatus
						.equals(edu.wustl.clinportal.util.global.Constants.ACTIVITY_STATUS_CLOSED))
				{
					error = new ActionError("clinicalStudyReg.closed");
					errors.add(ActionErrors.GLOBAL_ERROR, error);

				}
			}
		}
		return errors;
	}

	/**
	 * 
	 * @param eventId
	 * @param formContextId
	 * @return
	 * @throws DAOException
	 */
	public StudyFormContext getStudyFormContext(String eventId, String formContextId)
			throws DAOException
	{
		StudyFormContext studyFormContext = null;
		StringBuffer studyFormHQL = new StringBuffer(100);
		studyFormHQL.append("from ").append(StudyFormContext.class.getName());
		studyFormHQL.append(" as studyFrm where studyFrm.clinicalStudyEvent.id=");
		studyFormHQL.append(eventId);
		studyFormHQL.append(" and studyFrm.id=");
		studyFormHQL.append(formContextId);
		studyFormHQL.append(" order by studyFrm.id asc");
		Collection<StudyFormContext> studyFormColl = (Collection<StudyFormContext>) Utility
				.executeQuery(studyFormHQL.toString());
		if (!studyFormColl.isEmpty())
		{
			studyFormContext = studyFormColl.iterator().next();
		}
		return studyFormContext;
	}

	/**
	 * 
	 * @param recEntryId
	 * @param containerId
	 * @param deEntityId
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 * @throws CacheException
	 * @throws DAOException
	 * @throws SQLException
	 */
	public Long getDERecordId(Long recEntryId, Long containerId, Long deEntityId)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException,
			CacheException, DAOException, SQLException
	{
		AnnotationBizLogic annoBizLogic = new AnnotationBizLogic();
		Collection deIdList = annoBizLogic.getDynamicRecordFromStaticId(recEntryId.toString(),
				containerId, deEntityId.toString());

		Long dynamicRecId = Long.valueOf("0");
		if (deIdList != null && !deIdList.isEmpty())
		{
			dynamicRecId = (Long) deIdList.iterator().next();
		}
		return dynamicRecId;
	}

	/**
	 * @param cleanDAO
	 * @param hql
	 * @return
	 * @throws BizLogicException
	 * @throws DAOException
	 */
	private EventEntry getOldEventEntry(DAO cleanDAO, String hql) throws BizLogicException,
			DAOException
	{

		EventEntry oldEventEntry = null;
		List eventEntryList = cleanDAO.executeQuery(hql);
		if (eventEntryList.size() > 0)
		{
			oldEventEntry = (EventEntry) eventEntryList.get(0);
		}

		return oldEventEntry;
	}

}