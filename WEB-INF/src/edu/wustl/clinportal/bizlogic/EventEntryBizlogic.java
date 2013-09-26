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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.wustl.clinportal.domain.ClinicalStudyEvent;
import edu.wustl.clinportal.domain.EventEntry;
import edu.wustl.clinportal.domain.RecordEntry;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.clinportal.util.global.Utility;
import edu.wustl.common.audit.AuditManager;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exception.AuditException;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.exception.DAOException;

/**
 * @author deepali
 * @version
 */
public class EventEntryBizlogic extends ClinportalDefaultBizLogic
{

	private transient Logger logger = Logger.getCommonLogger(EventEntryBizlogic.class);

	/**
	 * @param obj
	 * @param dao
	 * @param operation
	 * @return
	 * @throws BizLogicException
	 */
	@Override
	protected boolean validate(Object obj, DAO dao, String operation) throws BizLogicException
	{
		EventEntry eventEntry = (EventEntry) obj;
		//check for closed status
		List recordEntryList = null;
		//cehck for closed status
		String statusHQL = "select csr.activityStatus,csr.participant.activityStatus,csr.activityStatus from edu.wustl.clinportal.domain.ClinicalStudyRegistration as csr where csr.id="
				+ eventEntry.getClinicalStudyRegistration().getId();
		try
		{
			recordEntryList = (List) dao.executeQuery(statusHQL);
			Utility.checkClosedStatus(recordEntryList);

			List eventList = (List) dao.retrieve(ClinicalStudyEvent.class.getName(), "id",
					eventEntry.getClinicalStudyEvent().getId());
			//ClinicalStudyEvent event = null;
			if (eventList != null && !eventList.isEmpty())
			{
				ClinicalStudyEvent event = (ClinicalStudyEvent) eventList.get(0);
				//List<Object[]> eventEnryList = null;
				StringBuffer hql = new StringBuffer(
						"select eventEntry.id,eventEntry.entryNumber from EventEntry eventEntry "
								+ "where eventEntry.clinicalStudyEvent.id =");
				hql.append(event.getId());
				hql.append(" and eventEntry.clinicalStudyRegistration.id=");
				hql.append(eventEntry.getClinicalStudyRegistration().getId());

				List<Object[]> eventEnryList = (List) dao.executeQuery(hql.toString());
				if (eventEnryList == null)
				{
					eventEnryList = new ArrayList();
				}
				if (eventEntry.getEntryNumber() == null)
				{
					eventEntry.setEntryNumber(eventEnryList.size() + 1);
				}
				else
				{
					if (operation != null && operation.equals(Constants.ADD))
					{

						for (Object[] objects : eventEnryList)
						{
							if (objects[1] != null
									&& objects[1].toString().equals(
											eventEntry.getEntryNumber().toString()))
							{
								RecordEntry recEntry = getCurrentRecordEntry(eventEntry, dao);
								String studyFormLabel = recEntry.getStudyFormContext()
										.getStudyFormLabel();
								throw getBizLogicException(null, "eventEntry.exists",
										studyFormLabel);
							}
						}
					}
				}

				if (event.getIsInfiniteEntry() != null
						&& !event.getIsInfiniteEntry().booleanValue()
						&& (event.getNoOfEntries().compareTo(eventEntry.getEntryNumber()) < 0))
				{
					throw getBizLogicException(null, "errors.eventEntry.number", "");
				}

			}
			else
			{

				throw getBizLogicException(null, "clinicalStudyEvent.required", "");
			}
		}
		catch (DAOException daoException)
		{
			throw new BizLogicException(daoException);
		}
		return true;
	}

	/**
	 * returns the eventEntry object whose id is null
	 * @param eventEntry
	 * @param dao
	 * @return
	 * @throws DAOException 
	 */
	private RecordEntry getCurrentRecordEntry(EventEntry eventEntry, DAO dao) throws DAOException
	{
		Collection<RecordEntry> recEntryColn = null;
		if (eventEntry.getId() == null)
		{
			recEntryColn = eventEntry.getRecordEntryCollection();
		}
		else
		{
			recEntryColn = (Collection<RecordEntry>) dao.retrieveAttribute(EventEntry.class,
					Constants.SYSTEM_IDENTIFIER, eventEntry.getId(),
					"elements(recordEntryCollection)");
		}
		for (RecordEntry entry : recEntryColn)
		{
			if (entry.getId() == null)
			{
				return entry;
			}
		}

		return null;
	}

	/**
	 * Saves the ClinicalStudyEvent object in the database.
	 * @param obj The ClinicalStudyEvent object to be saved.
	 * @param dao
	 * @param sessionDataBean The session in which the object is saved.
	 * @throws BizLogicException 
	 */
	protected void insert(Object obj, DAO dao, SessionDataBean sessionDataBean)
			throws BizLogicException
	{
		EventEntry eventEntry = (EventEntry) obj;
		try
		{
			dao.insert(eventEntry);
			AuditManager auditManager = getAuditManager(sessionDataBean);
			auditManager.insertAudit(dao, eventEntry);
		}
		catch (DAOException e)
		{
			throw new BizLogicException(e);
		}
		catch (AuditException e)
		{
			logger.debug(e.getMessage(), e);
			throw getBizLogicException(e, e.getErrorKeyName(), e.getMsgValues());
		}

	}

	/**
	 * Updates the persistent object in the database.
	 * @param dao
	 * @param obj The object to be updated.
	 * @param oldObj
	 * @param sessionDataBean The session in which the object is saved.
	 * @throws BizLogicException 
	 */
	protected void update(DAO dao, Object obj, Object oldObj, SessionDataBean sessionDataBean)
			throws BizLogicException
	{
		EventEntry eventEntry = (EventEntry) obj;
		EventEntry eventEntryOldObj = (EventEntry) oldObj;
		try
		{
			dao.update(eventEntry);
			AuditManager auditManager = getAuditManager(sessionDataBean);
			auditManager.updateAudit(dao, eventEntry, eventEntryOldObj);
		}
		catch (DAOException daoException)
		{
			throw new BizLogicException(daoException);
		}
		catch (AuditException e)
		{
			logger.debug(e.getMessage(), e);
			throw getBizLogicException(e, e.getErrorKeyName(), e.getMsgValues());
		}
	}

	//To disable Event entries and Record entries.
	/**
	 * @param dao
	 * @param objectType
	 * @param objectIDArr
	 * @throws DAOException
	 * @throws BizLogicException
	 */
	public void disableRelatedObjects(DAO dao, String objectType, Long objectIDArr[])
			throws DAOException, BizLogicException
	{
		List listOfSubElement = getRelatedObjects(dao, EventEntry.class, objectType, objectIDArr);
		if (!listOfSubElement.isEmpty())
		{
			disableRelatedObjects(dao, "CATISSUE_EVENT_ENTRY",
					Constants.SYSTEM_IDENTIFIER_COLUMN_NAME, edu.wustl.common.util.Utility
							.toLongArray(listOfSubElement));
			auditDisabledObjects(dao, "CATISSUE_EVENT_ENTRY", listOfSubElement);
			super.disableObjects(dao, RecordEntry.class, "eventEntry",
					"CATISUE_CLIN_STUDY_RECORD_NTRY", "EVENT_ENTRY_ID",
					edu.wustl.common.util.Utility.toLongArray(listOfSubElement));
		}

	}
}