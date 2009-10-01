/**
 * 
 */

package edu.wustl.clinportal.bizlogic;

import java.util.List;

import edu.wustl.clinportal.domain.ClinicalStudy;
import edu.wustl.clinportal.domain.ClinicalStudyEvent;
import edu.wustl.common.audit.AuditManager;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exception.AuditException;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.exception.DAOException;

/**
 * @author Deepali
 *
 */
public class ClinicalStudyEventBizlogic extends ClinportalDefaultBizLogic
{

	private transient Logger logger = Logger.getCommonLogger(ClinicalStudyEventBizlogic.class);

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
		ClinicalStudyEvent clStudyEvent = (ClinicalStudyEvent) obj;

		ClinicalStudy clinicalStudy = new ClinicalStudy();
		clinicalStudy.setTitle("ClinicalStudy");

		clStudyEvent.setClinicalStudy(clinicalStudy);
		clStudyEvent.setCollectionPointLabel("UniqueLabel");
		clStudyEvent.setStudyCalendarEventPoint(Integer.valueOf(1));

		try
		{
			AuditManager auditManager = getAuditManager(sessionDataBean);
			dao.insert(clStudyEvent);
			auditManager.insertAudit(dao, clStudyEvent);
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
		ClinicalStudyEvent clStudyEvent = (ClinicalStudyEvent) obj;
		try
		{
			AuditManager auditManager = getAuditManager(sessionDataBean);
			dao.update(clStudyEvent);
			auditManager.updateAudit(dao, clStudyEvent, oldObj);
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

	/**
	 * Method returns the ClinicalStudyEvent for the given Id
	 * @param eventId
	 * @return
	 * @throws NumberFormatException
	 * @throws BizLogicException
	 */
	public ClinicalStudyEvent getClinicalStudyEventById(Long eventId) throws NumberFormatException,
			BizLogicException
	{
		List eventList = (List) retrieve(ClinicalStudyEvent.class.getName(), "id", eventId);
		ClinicalStudyEvent event = null;
		if (eventList != null && !eventList.isEmpty())
		{
			event = (ClinicalStudyEvent) eventList.get(0);
		}
		return event;
	}
}