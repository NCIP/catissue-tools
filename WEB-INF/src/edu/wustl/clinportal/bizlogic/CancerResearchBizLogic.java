/**
 * <p>Title: CancerResearchBizLogic Class>
 * <p>Description:	CancerResearchBizLogic </p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Ashish Gupta
 * @version 1.00
 */

package edu.wustl.clinportal.bizlogic;

import edu.wustl.clinportal.domain.CancerResearchGroup;
import edu.wustl.common.audit.AuditManager;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exception.AuditException;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.exception.DAOException;

/**
 * @author falguni_sachde
 *
 */
public class CancerResearchBizLogic extends ClinportalDefaultBizLogic
{

	private transient Logger logger = Logger.getCommonLogger(CancerResearchBizLogic.class);

	/* (non-Javadoc)
	 * @see edu.wustl.common.bizlogic.DefaultBizLogic#validate(java.lang.Object, edu.wustl.common.dao.DAO, java.lang.String)
	 */
	protected boolean validate(Object obj, DAO dao, String operation) throws BizLogicException
	{
		CancerResearchGroup canResGroup = (CancerResearchGroup) obj;
		if (canResGroup == null)
		{
			String message = ApplicationProperties.getValue("app.cancerResearchGroup");
			throw getBizLogicException(null, "error.cancerResearchGroup.validate",
					ApplicationProperties.getValue("domain.object.null.err.msg", message));
		}

		Validator validate = new Validator();
		if (validate.isEmpty(canResGroup.getName()))
		{
			String message = ApplicationProperties.getValue("cancerResearchGroup.name");
			throw getBizLogicException(null, "error.cancerResearchGroup.validate",
					ApplicationProperties.getValue("errors.item.required", message));
		}
		return true;
	}

	/**
	* Updates the persistent object in the database.
	* @param obj The object to be updated.
	* @param session The session in which the object is saved.
	* @throws DAOException 
	*/

	protected void update(DAO dao, Object obj, Object oldObj, SessionDataBean sessionDataBean)
			throws BizLogicException
	{
		CancerResearchGroup canResGroup = (CancerResearchGroup) obj;
		try
		{
			dao.update(canResGroup);
			AuditManager auditManager = getAuditManager(sessionDataBean);
			auditManager.updateAudit(dao, obj, oldObj);
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
}