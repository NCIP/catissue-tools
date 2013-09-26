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
 * <p>Title: AdvanceQueryInterfaceDefaultPageAction Class>
 * <p>Description:	This class initializes the fields of AdvanceQueryInterfaceDefaultPage.jsp Page</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Prafull Kadam
 * @version 1.00
 * Created on Sept 8, 2006
 */

package edu.wustl.clinportal.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.action.BaseAction;
import edu.wustl.common.util.logger.Logger;

public class AdvanceQueryInterfaceDefaultPageAction extends BaseAction
{

	/**
	 * Overrides the execute method of Action class.
	 * Initializes the various fields in AdvanceQueryInterface.jsp Page.
	 * */
	public ActionForward executeAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException
	{
		//String pageOf = (String)request.getParameter(Constants.PAGEOF);
		request.setAttribute(edu.wustl.common.util.global.Constants.MENU_SELECTED, "17");
		Logger.out.debug(edu.wustl.common.util.global.Constants.MENU_SELECTED
				+ " set in Advance Query Default Page Action : 17  -- ");
		return mapping.findForward(Constants.SUCCESS);
	}

}
