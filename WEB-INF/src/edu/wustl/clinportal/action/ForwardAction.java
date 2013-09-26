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
 * Created on Sep 1, 2005
 * This class is used to redirect the user to the Home / SignIn Page after session is timedOut.
 */

package edu.wustl.clinportal.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.action.BaseAction;

/**
 * @author mandar_deshmukh
 *
 * This class is used to redirect the user to the Home / SignIn Page after session is timedOut.
 */
public class ForwardAction extends BaseAction
{

	public ActionForward executeAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
		return mapping.findForward(Constants.SUCCESS);
	}

}