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
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author falguni_sachde
 * @version 1.00
 * Created on Sep 19, 2006
 */

package edu.wustl.clinportal.bizlogic;

import edu.wustl.clinportal.domain.Department;
import edu.wustl.common.audit.AuditManager;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exception.AuditException;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.exception.DAOException;

public class DepartmentBizLogic extends ClinportalDefaultBizLogic
{

	private transient Logger logger = Logger.getCommonLogger(DepartmentBizLogic.class);

	/* (non-Javadoc)
	 * @see edu.wustl.common.bizlogic.DefaultBizLogic#validate(java.lang.Object, edu.wustl.dao.DAO, java.lang.String)
	 */
	protected boolean validate(Object obj, DAO dao, String operation) throws BizLogicException
	{

		Department department = (Department) obj;
		if (department == null)
		{
			String message = ApplicationProperties.getValue("app.department");
			throw getBizLogicException(null, "domain.object.null.err.msg", message);

		}
		if (Validator.isEmpty(department.getName()))
		{
			String message = ApplicationProperties.getValue("department.name");
			throw getBizLogicException(null, "errors.item.required", message);

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
		Department department = (Department) obj;
		try
		{
			dao.update(department);
			AuditManager auditManager = getAuditManager(sessionDataBean);
			auditManager.updateAudit(dao, department, oldObj);
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

}