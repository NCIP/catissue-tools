/*
 * Created on January 17, 2007
 * @author
 *
 */

package edu.wustl.clinportal.action.annotations;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.ehcache.CacheException;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.common.dynamicextensions.ui.webui.util.WebUIManager;
import edu.wustl.clinportal.actionForm.AnnotationDataEntryForm;
import edu.wustl.clinportal.util.CatissueCoreCacheManager;
import edu.wustl.clinportal.util.DataEntryUtil;
import edu.wustl.clinportal.util.EventTreeObject;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.clinportal.util.global.Utility;
import edu.wustl.common.action.BaseAction;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.exception.DAOException;

public class LoadDynamicExtentionsDataEntryPageAction extends BaseAction
{

	private transient Logger logger = Logger
			.getCommonLogger(LoadDynamicExtentionsDataEntryPageAction.class);

	/* (non-Javadoc)
	 * @see edu.wustl.common.action.BaseAction#executeAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected ActionForward executeAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		AnnotationDataEntryForm annoDataEtryFrm = (AnnotationDataEntryForm) form;
		String dynExtRecordId = request.getParameter("recordId");
		request.getParameter(Constants.PROTOCOL_EVENT_ID);
		String treeViewKey = request.getParameter(AnnotationConstants.TREE_VIEW_KEY);
		EventTreeObject eventTreeObject = new EventTreeObject(treeViewKey);
		if (treeViewKey != null)
		{
			request.getSession().setAttribute(AnnotationConstants.TREE_VIEW_KEY, treeViewKey);
		}

		updateCache(request, annoDataEtryFrm, eventTreeObject);

		String comingFrom = request
				.getParameter(edu.wustl.clinportal.util.global.Constants.FORWARD_CONTROLLER);
		request.getSession().setAttribute(
				edu.wustl.clinportal.util.global.Constants.FORWARD_CONTROLLER, comingFrom);

		//Set as request attribute
		String dEDataEntryURL = getDynamicExtensionsDataEntryURL(request, annoDataEtryFrm);
		request
				.setAttribute(AnnotationConstants.DYNAMIC_EXTN_DATA_ENTRY_URL_ATTRIB,
						dEDataEntryURL);
		if (dynExtRecordId == null && checkForClosedStatus(request, true))
		{
			return mapping.findForward(Constants.FAILURE);
		}
		request.setAttribute(Constants.OPERATION, "DefineDynExtDataForAnnotations");
		return mapping.findForward(Constants.SUCCESS);

	}

	/**
	 * 
	 * @param request
	 * @throws DAOException
	 * @throws BizLogicException
	 */
	private boolean checkForClosedStatus(HttpServletRequest request, boolean saveErr)
			throws DAOException, BizLogicException
	{
		String studyId = request.getParameter(Constants.CP_SEARCH_CP_ID);
		DataEntryUtil dataEntryUtil = new DataEntryUtil();
		String participantId = request
				.getParameter(edu.wustl.clinportal.util.global.Constants.CP_SEARCH_PARTICIPANT_ID);
		String regId = getRegId(studyId, participantId);
		ActionErrors err = dataEntryUtil.getClosedStatus(request, participantId, studyId, regId);
		boolean flag = false;
		if (!err.isEmpty())
		{
			if (saveErr)
			{
				saveErrors(request, err);
			}
			flag = true;
		}
		if (!flag && !saveErr)
			flag = checkforPrivileges(request);
		return flag;
	}

	/**
	 * 
	 * @param request
	 * @return
	 * @throws DAOException
	 */
	private boolean checkforPrivileges(HttpServletRequest request) throws DAOException
	{
		SessionDataBean sessionDataBean = (SessionDataBean) request.getSession().getAttribute(
				Constants.SESSION_DATA);
		String frmCntxtId = request.getParameter(AnnotationConstants.FORM_CONTEXT_ID);
		String userId = sessionDataBean.getUserId().toString();
		String userName = sessionDataBean.getUserName().toString();
		Map<String, Boolean> privMap = Utility.checkFormPrivileges(frmCntxtId, userId, userName);
		boolean readOnly = privMap.get(Constants.READ_ONLY);
		return readOnly;
	}

	/**
	 * 
	 * @param studyId
	 * @param participantId
	 * @return
	 * @throws DAOException
	 */
	private String getRegId(String studyId, String participantId) throws DAOException
	{
		String regId = null;
		String activityStatusHQL = "select csr.id from edu.wustl.clinportal.domain.ClinicalStudyRegistration as csr where csr.participant.id="
				+ participantId + " and csr.clinicalStudy.id=" + studyId;
		List actStatusLst = new DataEntryUtil().executeQuery(activityStatusHQL);
		if (actStatusLst != null && !actStatusLst.isEmpty())
		{
			regId = actStatusLst.get(0).toString();
		}
		return regId;
	}

	/**
	 * @param request
	 * @param annotDataEtryFrm 
	 * @throws CacheException 
	 */
	private void updateCache(HttpServletRequest request, AnnotationDataEntryForm annotDataEtryFrm,
			EventTreeObject eventTreeObject) throws CacheException
	{
		String staticEntityId = annotDataEtryFrm.getParentEntityId();
		CatissueCoreCacheManager cacheManager = CatissueCoreCacheManager.getInstance();
		String recordId = request.getParameter("recordId");

		String eventId = request
				.getParameter(edu.wustl.clinportal.util.global.Constants.PROTOCOL_EVENT_ID);
		if (eventId != null)
		{
			request.getSession().setAttribute(
					edu.wustl.clinportal.util.global.Constants.PROTOCOL_EVENT_ID, eventId);
		}

		if (recordId == null && staticEntityId == null)//&& !recordId.equals("0") )
		{
			staticEntityId = cacheManager.getObjectFromCache(
					AnnotationConstants.RECORD_ENTRY_ENTITY_ID).toString();

		}
		/*
		 * Populate the EventTreeObject object using the received key
		 */

		String selStaticEntyId = annotDataEtryFrm.getSelectedStaticEntityId();
		if (selStaticEntyId == null)
		{
			selStaticEntyId = CatissueCoreCacheManager.getInstance().getObjectFromCache(
					AnnotationConstants.RECORD_ENTRY_ENTITY_ID).toString();
		}

		String staticEntyRecId = annotDataEtryFrm.getSelectedStaticEntityRecordId();
		if (staticEntyRecId == null)
		{
			staticEntyRecId = request
					.getParameter(edu.wustl.clinportal.util.global.Constants.CP_SEARCH_PARTICIPANT_ID);
		}
		//Set into Cache

		if (cacheManager != null)
		{

			request.getSession().setAttribute(AnnotationConstants.SELECTED_STATIC_ENTITYID,
					selStaticEntyId);
			request.getSession().setAttribute(AnnotationConstants.SELECTED_STATIC_ENTITY_RECORDID,
					staticEntyRecId);
			request.getSession().setAttribute(AnnotationConstants.FORM_CONTEXT_ID,
					request.getParameter(AnnotationConstants.FORM_CONTEXT_ID));

			//set the studyId in session.
			request.getSession().setAttribute(Constants.CP_SEARCH_CP_ID,
					request.getParameter(Constants.CP_SEARCH_CP_ID));

		}

	}

	/**
	 * @return
	 * @throws CacheException 
	 * @throws BizLogicException 
	 * @throws DAOException 
	 */
	private String getDynamicExtensionsDataEntryURL(HttpServletRequest request,
			AnnotationDataEntryForm annotDataEnryFrm) throws CacheException, DAOException,
			BizLogicException
	{
		//Append container id
		logger.info("Load data entry page for Dynamic Extension Entity ["
				+ annotDataEnryFrm.getSelectedAnnotation() + "]");
		String dynEntContainerId = annotDataEnryFrm.getSelectedAnnotation();
		if (dynEntContainerId == null)
		{
			dynEntContainerId = request.getParameter(AnnotationConstants.FORM_ID);
		}

		if (dynEntContainerId == null)
		{

			dynEntContainerId = (String) request.getSession().getAttribute(
					AnnotationConstants.FORM_ID);
		}
		else
		{

			request.getSession().setAttribute(AnnotationConstants.FORM_ID,
					Long.decode(dynEntContainerId));
		}

		String equalSign = "=";
		String andSign = "&";
		StringBuffer dExtDataEtryURL = new StringBuffer(request.getContextPath()
				+ WebUIManager.getLoadDataEntryFormActionURL());
		dExtDataEtryURL.append(andSign);
		dExtDataEtryURL.append(WebUIManager.CONATINER_IDENTIFIER_PARAMETER_NAME);
		dExtDataEtryURL.append(equalSign);
		dExtDataEtryURL.append(dynEntContainerId);

		String dynExtRecordId = request.getParameter("recordId");

		if (dynExtRecordId != null)
		{
			logger.info("Loading details of record id [" + dynExtRecordId + "]");
			dExtDataEtryURL.append(andSign);
			dExtDataEtryURL.append(WebUIManager.RECORD_IDENTIFIER_PARAMETER_NAME);
			dExtDataEtryURL.append(equalSign);
			dExtDataEtryURL.append(dynExtRecordId);
		}
		//append call back url
		dExtDataEtryURL.append(andSign);
		dExtDataEtryURL.append(WebUIManager.getCallbackURLParamName());
		dExtDataEtryURL.append(equalSign);
		dExtDataEtryURL.append(request.getContextPath());
		dExtDataEtryURL.append(AnnotationConstants.CALLBACK_URL_PATH_ANNOTATION_DATA_ENTRY);

		//append User Id
		SessionDataBean dataBean = (SessionDataBean) request.getSession().getAttribute(
				Constants.SESSION_DATA);

		dExtDataEtryURL.append(andSign);
		dExtDataEtryURL.append(WebUIManager.getUserIdParameterName());
		dExtDataEtryURL.append(equalSign);
		dExtDataEtryURL.append(dataBean.getCsmUserId());

		if (checkForClosedStatus(request, false))
		{
			dExtDataEtryURL.append(andSign);
			dExtDataEtryURL.append(WebUIManager.MODE_PARAM_NAME);
			dExtDataEtryURL.append("=view");
		}
		return dExtDataEtryURL.toString();
	}

}
