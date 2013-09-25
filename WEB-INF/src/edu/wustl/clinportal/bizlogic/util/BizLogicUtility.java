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
 *@author Rukhsana_Sameer
 *@version 1.0
 */

package edu.wustl.clinportal.bizlogic.util;

import edu.wustl.security.manager.ISecurityManager;
import edu.wustl.security.manager.SecurityManagerFactory;
import edu.wustl.security.exception.SMException;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.logger.Logger;

public class BizLogicUtility
{
	/**
	 * Deletes the csm user from the csm user table.
	 * @param csmUser The csm user to be deleted.
	 * @throws BizLogicException
	 */
	public static void deleteCSMUser(gov.nih.nci.security.authorization.domainobjects.User csmUser) throws BizLogicException
	{
		try
		{
			if (csmUser.getUserId() != null)
			{
				//SecurityManager.getInstance(ApproveUserBizLogic.class).removeUser(csmUser.getUserId().toString());
				ISecurityManager securityManager = SecurityManagerFactory.getSecurityManager();
				securityManager.removeUser(csmUser.getUserId().toString());
			}
		}
		catch (SMException smExp)
		{
			Logger.out.error("Exception in Authorization: " + smExp.getMessage(), smExp);
			StringBuffer sbMessage = new StringBuffer("Security Exception: " + smExp.getMessage());
			if (smExp.getCause() != null)
			{
				sbMessage.append(" : " + smExp.getCause().getMessage());
			}
			//throw new DAOException(sbMessage.toString(), smExp);
			//throw handleSMException(smExp);
			String message = "Security Exception: " + smExp.getMessage();
			if (smExp.getCause() != null)
				message = message + " : " + smExp.getCause().getMessage();
			throw new BizLogicException(ErrorKey.getErrorKey("error.security"), smExp, "Security Exception");
		}
	}
}
