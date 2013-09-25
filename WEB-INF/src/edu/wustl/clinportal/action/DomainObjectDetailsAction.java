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
 * <p>Title: UserDetailsAction Class>
 * <p>Description:	UserDetailsAction is used to display details of user 
 * whose membership is to be approved/Rejected.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 * Created on Apr 29, 2005
 */

package edu.wustl.clinportal.action;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.action.SecureAction;
import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.domain.AbstractDomainObject;

/**
 * UserDetailsAction is used to display details of user whose membership is to be approved/Rejected.
 * @author gautam_shetty
 */
public class DomainObjectDetailsAction extends SecureAction
{

	/**
	 * Overrides the execute method in Action.
	 * @param mapping object of ActionMapping
	 * @param form object of ActionForm
	 * @param request object of HttpServletRequest
	 * @param response object of HttpServletResponse
	 * @throws Exception generic exception
	 * @return value for ActionForward object
	 */
	public ActionForward executeSecureAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception
	{

		HttpSession session = request.getSession();
		List list = (List) session.getAttribute(Constants.ORIGINAL_DOMAIN_OBJECT_LIST);

		long identifier = Long.parseLong(request.getParameter(Constants.SYSTEM_IDENTIFIER));
		Iterator iterator = list.iterator();

		AbstractDomainObject curDomObject = null;
		Long prevIdentifier = null, nextIdentifier = null;

		while (iterator.hasNext())
		{
			curDomObject = (AbstractDomainObject) iterator.next();
			if (identifier == curDomObject.getId().longValue())
			{
				break;
			}
			prevIdentifier = curDomObject.getId();
		}

		if (iterator.hasNext())
		{
			AbstractDomainObject nextDomainObject = (AbstractDomainObject) iterator.next();
			nextIdentifier = nextDomainObject.getId();
		}

		AbstractActionForm absActionForm = (AbstractActionForm) form;
		/**
		 *  
		 * Instead of setAllValues() method retrieveForEditMode() method is called to bypass lazy loading error in domain object
		 */
		//abstractActionForm.setAllValues(currentDomainObject);
		DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
		defaultBizLogic.populateUIBean(curDomObject.getClass().getName(), curDomObject.getId(), absActionForm);

		request.setAttribute(Constants.PREVIOUS_PAGE, prevIdentifier);
		request.setAttribute(Constants.NEXT_PAGE, nextIdentifier);

		return mapping.findForward(Constants.SUCCESS);
	}
}
