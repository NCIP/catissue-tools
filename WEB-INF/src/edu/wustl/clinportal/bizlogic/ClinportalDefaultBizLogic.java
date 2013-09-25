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
 * <p>Title: ClinportalDefaultBizLogic Class>
 * <p>Description:	ClinportalDefaultBizLogic is a class which contains the default 
 * implementations required for all the biz logic classes.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author deepali_ahirrao
 * @version 1.00
 * Created on Oct 16, 2008
 */

package edu.wustl.clinportal.bizlogic;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.dao.DAO;
import edu.wustl.security.exception.SMException;
import edu.wustl.security.global.Permissions;
import edu.wustl.security.privilege.PrivilegeCache;
import edu.wustl.security.privilege.PrivilegeManager;

/**
 * ClinportalDefaultBizLogic is a class which contains the default 
 * implementations required for all the biz logic classes in clinportal.
 * @author deepali_ahirrao
 */
public class ClinportalDefaultBizLogic extends DefaultBizLogic
{

	/**
	 * 
	 * This method return true if authorized user.
	 * @param dao DAO object.
	 * @param domainObject Domain object.
	 * @param sessionDataBean  SessionDataBean object.
	 * @throws BizLogicException generic BizLogic Exception
	 * @return true if authorized user.
	 * 
	 */

	//@see edu.wustl.common.bizlogic.IBizLogic#
	// isAuthorized(edu.wustl.common.dao.AbstractDAO, java.lang.Object, edu.wustl.common.beans.SessionDataBean)
	public boolean isAuthorized(DAO dao, Object domainObject, SessionDataBean sessionDataBean)
			throws BizLogicException
	{
		boolean isAuthorized = false;
		try
		{
			PrivilegeManager privilegeManager = PrivilegeManager.getInstance();
			if (sessionDataBean == null)
			{
				isAuthorized = false;
			}
			else
			{
				if (domainObject == null)
				{
					isAuthorized = true;
				}
				else
				{
					PrivilegeCache privilegeCache = privilegeManager
							.getPrivilegeCache(sessionDataBean.getUserName());
					isAuthorized = privilegeCache.hasPrivilege(
							getObjectIdForSecureMethodAccess(domainObject), Permissions.EXECUTE);
				}
			}

			if (!isAuthorized)
			{
				//ErrorKey errorKey = ErrorKey.getErrorKey("error.common.bizlogic");
				throw getBizLogicException(null,
						"Access denied- You are not authorized to perform this operation.", "");
			}
		}
		catch (SMException smException)
		{
			throw handleSMException(smException);
		}

		return isAuthorized;
	}

	/**
	 * Returns the object id of the protection element that represents
	 * the Action that is being requested for invocation.
	 * @param clazz
	 * @return
	 */
	protected String getObjectIdForSecureMethodAccess(Object domainObject)
	{
		//System.out.println("Object class is:-------------------"+domainObject.getClass().getName());
		return domainObject.getClass().getName();
	}

	/**
	 * @param e
	 * @return
	 */
	protected BizLogicException handleSMException(SMException e)
	{

		String message = "Security Exception: " + e.getMessage();
		if (e.getCause() != null)
			message = message + " : " + e.getCause().getMessage();
		return new BizLogicException(ErrorKey.getErrorKey("error.security"), e,
				"Security Exception");
	}
}