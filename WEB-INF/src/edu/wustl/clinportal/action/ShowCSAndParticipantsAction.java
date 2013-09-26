/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */


package edu.wustl.clinportal.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.clinportal.actionForm.CSSearchForm;
import edu.wustl.clinportal.util.ParticipantRegistrationCacheManager;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.action.BaseAction;
import edu.wustl.common.beans.NameValueBean;

/**
 * This action is for getting the collection protocol and 
 * participants registered for that collection protocol from cache
 *
 * */
public class ShowCSAndParticipantsAction extends BaseAction
{

	/**This class is responsible to get all clinical studies and associated participants
	 * @param form Action form which is associated with the class.
	 * @param mapping Action mappings specifying the mapping pages for the specified mapping attributes.
	 * @param request HTTPRequest which is submitted from the page.
	 * @param response HTTPRespons that is generated for the submitted request.
	 * @return ActionForward Action forward instance specifying which page the control should go to.  
	 * @see org.apache.struts.action.Action
	 * @see org.apache.struts.action.ActionForm
	 * @see org.apache.struts.action.ActionForward
	 * @see org.apache.struts.action.ActionMapping
	 * @see javax.servlet.http.HttpServletRequest
	 * @see javax.servlet.http.HttpServletResponse
	 * 
	 * */
	protected ActionForward executeAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		CSSearchForm csSearchForm = (CSSearchForm) form;

		//		Getting the instance of participantRegistrationCacheManager
		ParticipantRegistrationCacheManager pcpantRegCacheMgr = new ParticipantRegistrationCacheManager();

		//		Getting the CS list 
		Map<Long, String> csIDTitleMap = pcpantRegCacheMgr.getCSIDTitleMap();

		Vector nameValuePairs = new Vector();
		nameValuePairs.add(new NameValueBean(Constants.SELECT_OPTION, "-1"));
		NameValueBean nameValueBean;
		if (csIDTitleMap != null)
		{
			Iterator itr = csIDTitleMap.keySet().iterator();
			while (itr.hasNext())
			{
				nameValueBean = new NameValueBean();
				String keyValue = itr.next().toString();
				String name = (String) csIDTitleMap.get(Long.valueOf(keyValue));
				nameValueBean.setName(name);
				nameValueBean.setValue(keyValue);
				nameValuePairs.add(nameValueBean);
			}
		}

		List csColl = nameValuePairs;
		Collections.sort(csColl);
		request.setAttribute(Constants.CS_LIST, csColl);

		List participantColl = new ArrayList();
		Long csId = null;
		if (csSearchForm.getCsId() != null && csSearchForm.getCsId().longValue() != -1)
		{
			csId = csSearchForm.getCsId();
		}

		if (csId == null && request.getParameter("csId") != null)
		{
			csId = Long.valueOf(request.getParameter("csId"));
		}

		if (csId != null)
		{
			//			getting the list of participants from cache for particular CP.
			List pcpantNamesWithId = pcpantRegCacheMgr.getParticipantNames(csId);

			//Values in participantNamesWithID will be in format (ID:lastName firstName) 
			//Tokens the value and create nameValueBean with name as (lastName firstName) and value as participantId 
			//and store in the list
			Iterator itr = pcpantNamesWithId.iterator();
			while (itr.hasNext())
			{
				String pcpantIdAndName = (String) itr.next();
				int index = pcpantIdAndName.indexOf(":");
				Long identifier = null;
				String name = "";
				identifier = Long.valueOf(pcpantIdAndName.substring(0, index));
				name = pcpantIdAndName.substring(index + 1);
				participantColl.add(new NameValueBean(name, identifier));
			}

		}
		Collections.sort(participantColl);
		request.setAttribute(Constants.REGISTERED_PARTICIPANT_LIST, participantColl);

		if (request.getParameter("participantId") != null
				&& !request.getParameter("participantId").equals(""))
		{
			Long participantId = Long.valueOf(request.getParameter("participantId"));
			csSearchForm.setParticipantId(participantId);
		}
		//when clinicalStudy changed any participant should not be selected
		String csChange = request.getParameter("cpChange");
		if (csChange != null)
		{
			csSearchForm.setParticipantId(null);
		}

		return mapping.findForward(Constants.SUCCESS);
	}

}
