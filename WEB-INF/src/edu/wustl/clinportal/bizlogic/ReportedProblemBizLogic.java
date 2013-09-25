/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */

/*
 * Created on May 9, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package edu.wustl.clinportal.bizlogic;

import edu.wustl.clinportal.domain.ReportedProblem;
import edu.wustl.clinportal.util.ApiSearchUtil;
import edu.wustl.clinportal.util.EmailHandler;
import edu.wustl.common.audit.AuditManager;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exception.AuditException;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.exception.DAOException;

/**
 * @author falguni_sachde
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ReportedProblemBizLogic extends ClinportalDefaultBizLogic
{

	private transient Logger logger = Logger.getCommonLogger(ReportedProblemBizLogic.class);

	/* (non-Javadoc)
	 * @see edu.wustl.common.dao.HibernateDAO#add(java.lang.Object)
	 */
	protected void insert(Object obj, DAO dao, SessionDataBean sessionDataBean)
			throws BizLogicException
	{
		ReportedProblem reportedProblem = (ReportedProblem) obj;

		/**
		 * Start: Change for API Search   --- Jitendra 06/10/2006
		 * In Case of Api Search, previously it was failing since there was default class level initialization 
		 * on domain object. For example in User object, it was initialized as protected String lastName=""; 
		 * So we removed default class level initialization on domain object and are initializing in method
		 * setAllValues() of domain object. But in case of Api Search, default values will not get set 
		 * since setAllValues() method of domainObject will not get called. To avoid null pointer exception,
		 * we are setting the default values same as we were setting in setAllValues() method of domainObject.
		 */
		ApiSearchUtil.setReportedProblemDefault(reportedProblem);
		//End:-  Change for API Search 

		try
		{
			dao.insert(obj);
			AuditManager auditManager = getAuditManager(sessionDataBean);
			auditManager.insertAudit(dao, obj);
		}
		catch (DAOException e)
		{
			logger.debug(e.getMessage(), e);
			throw new BizLogicException(e);
		}
		catch (AuditException e)
		{
			logger.debug(e.getMessage(), e);
			throw getBizLogicException(e, e.getErrorKeyName(), e.getMsgValues());
		}

		// Send the reported problem to the administrator and the user who reported it.
		EmailHandler emailHandler = new EmailHandler();
		emailHandler.sendReportedProblemEmail(reportedProblem);
	}

}