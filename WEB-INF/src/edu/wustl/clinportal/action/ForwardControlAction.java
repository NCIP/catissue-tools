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
 * 
 */
package edu.wustl.clinportal.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.clinportal.action.annotations.AnnotationConstants;
import edu.wustl.clinportal.actionForm.ClinicalStudyForm;
import edu.wustl.clinportal.util.CatissueCoreCacheManager;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.action.BaseAction;


/**
 * This class is used to store the ClinicalStudyForm in session 
 * and forwards to the DE Page.
 * 
 * @author srinivasarao_vassadi
 *
 */
public class ForwardControlAction extends BaseAction
{

	/*
	 * (non-Javadoc)
	 * 
	 */
	protected ActionForward executeAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		
		ClinicalStudyForm CSForm = (ClinicalStudyForm)form;
		//CatissueCoreCacheManager.getInstance().addObjectToCache(Constants.EVENT_COUNTER, request.getParameter("eventCounter"));
		request.getSession().setAttribute(Constants.EVENT_COUNTER, request.getParameter("eventCounter"));
		request.getSession().setAttribute(Constants.CLINICAL_STUDY_FORM, CSForm);
		
		ActionForward actionForward = new ActionForward();
		Long staticEntityId = (Long)CatissueCoreCacheManager.getInstance().getObjectFromCache(AnnotationConstants.RECORD_ENTRY_ENTITY_ID); 
		
		String path = "/BuildDynamicEntity.do?pageOf=pageOfClinicalStudy&staticEntityId="+staticEntityId+"&"+Constants.FORWARD_CONTROLLER+"=clinicalstudy";
		actionForward.setName("defineEntity");
		actionForward.setPath(path);
		actionForward.setRedirect(true);
			
		return actionForward;
	}
}
