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

package edu.wustl.clinportal.bizlogic;

import java.util.List;

import edu.wustl.clinportal.domain.RecordEntry;
import edu.wustl.clinportal.domain.StudyFormContext;
import edu.wustl.clinportal.util.global.Utility;
import edu.wustl.common.audit.AuditManager;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exception.AuditException;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.exception.DAOException;

public class RecordEntryBizLogic extends ClinportalDefaultBizLogic
{

	private transient Logger logger = Logger.getCommonLogger(RecordEntryBizLogic.class);

	@Override
	protected boolean validate(Object obj, DAO dao, String operation) throws BizLogicException
	{
		RecordEntry recordEntry = (RecordEntry) obj;
		if (recordEntry.getEventEntry() == null)
		{

			throw getBizLogicException(null, "eventEntry.required", "");
		}
		if (recordEntry.getStudyFormContext() == null)
		{

			throw getBizLogicException(null, "studyFormContext.required", "");
		}
		List recordEntryList = null;
		//check for closed status
		String statusHQL = "select eventEntry.clinicalStudyRegistration.clinicalStudy.activityStatus,"
				+ " eventEntry.clinicalStudyRegistration.participant.activityStatus,"
				+ " eventEntry.clinicalStudyRegistration.activityStatus "
				+ " from edu.wustl.clinportal.domain.EventEntry as eventEntry where eventEntry.id="
				+ recordEntry.getEventEntry().getId();
		try
		{

			recordEntryList = (List) dao.executeQuery(statusHQL);
			Utility.checkClosedStatus(recordEntryList);

			StudyFormContext studyFormContext = recordEntry.getStudyFormContext();

			if (studyFormContext.getCanHaveMultipleRecords() != null
					&& !studyFormContext.getCanHaveMultipleRecords().booleanValue())
			{
				StringBuffer hql = new StringBuffer();
				hql
						.append("select recordEntry.id from RecordEntry recordEntry where recordEntry.eventEntry.id ="
								+ recordEntry.getEventEntry().getId());
				hql.append(" and recordEntry.studyFormContext.id=" + studyFormContext.getId());

				recordEntryList = (List) dao.executeQuery(hql.toString());

				if (recordEntryList != null && !recordEntryList.isEmpty())
				{
					throw getBizLogicException(null, "errors.dataEntry.singleRecord",
							studyFormContext.getStudyFormLabel());
				}
			}
		}
		catch (DAOException daoException)
		{
			logger.debug(daoException.getMessage(), daoException);
			throw new BizLogicException(daoException);
		}

		return true;
	}

	/**
	 * Saves the ClinicalStudyEvent object in the database.
	 * @param obj The ClinicalStudyEvent object to be saved.
	 * @param session The session in which the object is saved.
	 * @throws BizLogicException 
	 */
	protected void insert(Object obj, DAO dao, SessionDataBean sessionDataBean)
			throws BizLogicException
	{
		RecordEntry recordEntry = (RecordEntry) obj;
		AuditManager auditManager = getAuditManager(sessionDataBean);
		try
		{
			dao.insert(recordEntry);
			auditManager.insertAudit(dao, recordEntry);

		}
		catch (AuditException e)
		{
			logger.debug(e.getMessage(), e);
			throw getBizLogicException(e, e.getErrorKeyName(), e.getMsgValues());
		}
		catch (DAOException e)
		{
			logger.debug(e.getMessage(), e);
			throw new BizLogicException(e);
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
		RecordEntry recordEntryNew = (RecordEntry) obj;
		AuditManager auditManager = getAuditManager(sessionDataBean);
		try
		{
			dao.update(recordEntryNew);
			auditManager.updateAudit(dao, recordEntryNew, oldObj);
		}
		catch (DAOException daoException)
		{
			logger.debug(daoException.getMessage(), daoException);
			throw new BizLogicException(daoException);
		}
		catch (AuditException e)
		{
			logger.debug(e.getMessage(), e);
			throw getBizLogicException(e, e.getErrorKeyName(), e.getMsgValues());
		}
	}

}