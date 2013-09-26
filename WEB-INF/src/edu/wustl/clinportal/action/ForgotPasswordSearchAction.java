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
 * <p>Title: ForgotPasswordSearchAction Class>
 * <p>Description:	This Class is used to retrieve the password of the user and mail it to his email address.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 * Created on Apr 19, 2005
 */

package edu.wustl.clinportal.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.clinportal.actionForm.ForgotPasswordForm;
import edu.wustl.clinportal.bizlogic.UserBizLogic;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.exception.DAOException;

/**
 * This Class is used to retrieve the password of the user and mail it to his email address.
 * @author gautam_shetty
 */
public class ForgotPasswordSearchAction extends Action
{

	/**
	 * Overrides the execute method of Action class.
	 * Adds the user information in the database.
	 *
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException
	{
		String target = null;
		try
		{
			ForgotPasswordForm fPasswordForm = (ForgotPasswordForm) form;
			IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
			UserBizLogic userBizLogic = (UserBizLogic) factory.getBizLogic(fPasswordForm
					.getFormId());

			//Retrieves and sends the password to the user whose email address is passed 
			//else returns the error key in case of an error.
			SessionDataBean sessionData = new SessionDataBean();
			sessionData.setUserName(fPasswordForm.getEmailAddress());
			String message = userBizLogic.sendForgotPassword(fPasswordForm.getEmailAddress(),
					sessionData);
			request
					.setAttribute(edu.wustl.common.util.global.Constants.STATUS_MESSAGE_KEY,
							message);
			target = new String(Constants.SUCCESS);
		}
		catch (DAOException excp)
		{
			target = new String(Constants.FAILURE);
			Logger.out.error(excp.getMessage());
		}
		catch (BizLogicException e)
		{
			target = new String(Constants.FAILURE);
			Logger.out.error(e.getMessage());
		}
		return mapping.findForward(target);
	}
}