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
 * <p>Title: ClinicalStudyAction Class>
 * <p>Description:  This class initializes the fields in the ClinicalStudy Add/Edit webpage.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Shital Lawhale
 * @version 1.00
 * Created on May 10, 2007
 */

package edu.wustl.clinportal.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.clinportal.action.annotations.AnnotationConstants;
import edu.wustl.clinportal.actionForm.ClinicalStudyForm;
import edu.wustl.clinportal.bizlogic.ClinicalStudyBizLogic;
import edu.wustl.clinportal.bizlogic.UserBizLogic;
import edu.wustl.clinportal.util.CatissueCoreCacheManager;
import edu.wustl.clinportal.util.DataEntryUtil;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.clinportal.util.global.Utility;
import edu.wustl.clinportal.util.global.Variables;
import edu.wustl.common.action.SecureAction;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.security.global.Permissions;
import edu.wustl.security.privilege.PrivilegeCache;
import edu.wustl.security.privilege.PrivilegeManager;

public class ClinicalStudyAction extends SecureAction
{

	/**
	 * This class is responsible to handle all the requests related to the ClinicalStudy object. It handles all the scenarios 
	 * viz. "add" "edit"  mode for the ClinicalStudy object. It populates all the required parameters that are needed 
	 * to successfully access the ClinicalStudy object. 
	 * @param form Action form which is associated with the class.
	 * @param mapping Action mappings specifying the mapping pages for the specified mapping attributes.
	 * @param request HTTPRequest which is submitted from the page.
	 * @param response HTTPRespons that is generated for the submitted request.
	 * @return ActionForward Actionforward instance specifying which page the control should go to.  
	 * 
	 */
	protected ActionForward executeSecureAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		if (!isAuthorizedToExecute(request))
		{
			ActionErrors errors = new ActionErrors();
			ActionError error = new ActionError("access.execute.action.denied");
			errors.add(ActionErrors.GLOBAL_ERROR, error);
			saveErrors(request, errors);
			return mapping.findForward(edu.wustl.common.util.global.Constants.ACCESS_DENIED);
		}
		String pageOf = (String) request.getParameter(Constants.PAGEOF);
		HttpSession session = request.getSession();

		//      Gets the value of the operation attribute.
		String operation = (String) request.getParameter(Constants.OPERATION);
		Long staticEntityId = (Long) CatissueCoreCacheManager.getInstance().getObjectFromCache(
				AnnotationConstants.RECORD_ENTRY_ENTITY_ID);

		if (pageOf == null)
		{
			pageOf = (String) session.getAttribute(Constants.PAGEOF);
		}

		request.setAttribute(Constants.PAGEOF, pageOf);
		ClinicalStudyForm clinicalStudyForm = setClinicalStudyForm(operation, request, session,
				form);
		setConsentAndForm(clinicalStudyForm, request, staticEntityId);
		request.setAttribute(Constants.CLINICAL_STUDY_FORM, clinicalStudyForm);

		// take operation value set in request, in method call setClinicalStudyForm above
		getUsers(request, (String) request.getAttribute(Constants.OPERATION),
				((ClinicalStudyForm) form).getProtocolCoordinatorIds());
		request.setAttribute(Constants.ACTIVITYSTATUSLIST, Constants.ACTIVITY_STATUS_VALUES);

		String isToDelete = request.getParameter(Constants.IS_TO_DELETE);
		deleteValues(isToDelete, clinicalStudyForm, request);
		if (isToDelete != null && isToDelete.trim().length() > 0)
		{
			request.setAttribute("delopr", true);

		}

		//check for recordEntrycollection of the studFormContext.
		if (Long.valueOf(clinicalStudyForm.getId()) != null
				&& !Long.valueOf(clinicalStudyForm.getId()).equals("0"))
		{
			Map frmValues = clinicalStudyForm.getFormValues();
			int outerCnt = clinicalStudyForm.getOuterCounter();
			checkForRecrdEntry(clinicalStudyForm, frmValues, outerCnt);
			if (clinicalStudyForm.getConsentTierCounter() != 0)
			{
				Map consentValues = clinicalStudyForm.getConsentValues();
				int consentCtr = clinicalStudyForm.getConsentTierCounter();
				checkForConsentResponse(consentValues, consentCtr);
			}
		}
		if (Variables.isCatissueModelAvailable)
		{
			setCatissueData(request, clinicalStudyForm);

		}
		return mapping.findForward(pageOf);
	}

	/**
	 * @param request
	 * @param clinicalStudyForm
	 * @throws BizLogicException
	 * @throws DAOException
	 */
	private void setCatissueData(HttpServletRequest request, ClinicalStudyForm clinicalStudyForm)
			throws BizLogicException, DAOException
	{
		getCatissueCP(request);
		if (request.getParameter("cpId") != null)
		{
			Long cpId = Long.valueOf(request.getParameter("cpId"));
			if (cpId != -1)
			{
				getCatissueCPEvent(request, cpId);
			}
		}
		if (clinicalStudyForm.getColprotocolId() != -1)
		{
			Long cpId = Long.valueOf(clinicalStudyForm.getColprotocolId());
			getCatissueCPEvent(request, cpId);
		}

	}

	/**
	 * @param request
	 * @return
	 * @throws Exception
	 */
	protected boolean isAuthorizedToExecute(HttpServletRequest request) throws Exception
	{
		PrivilegeManager privilegeManager = PrivilegeManager.getInstance();
		PrivilegeCache privilegeCache = privilegeManager
				.getPrivilegeCache(getUserLoginName(request));

		boolean isAuthorized = privilegeCache.hasPrivilege(
				getObjectIdForSecureMethodAccess(request), Permissions.EXECUTE);

		return isAuthorized;
	}

	/**
	 * This method generates a key (ConsentBean:1_disableConsentKey) 
	 * to check if the consent statement can be edited or not
	 * @param operation
	 * @param request
	 * @param session
	 * @param form
	 * @return
	 */
	private ClinicalStudyForm setClinicalStudyForm(String operation, HttpServletRequest request,
			HttpSession session, ActionForm form)
	{
		String strOp = operation;
		ClinicalStudyForm clinicalStudyForm = null;
		if (strOp == null)
		{
			// This means it is coming from DE Page
			strOp = (String) session.getAttribute(Constants.OPERATION);
			clinicalStudyForm = (ClinicalStudyForm) session
					.getAttribute(Constants.CLINICAL_STUDY_FORM);

			request.getSession().removeAttribute(Constants.FORWARD_CONTROLLER);
		}
		else
		{
			session.setAttribute(Constants.OPERATION, strOp);
			clinicalStudyForm = (ClinicalStudyForm) form;
		}
		request.setAttribute(Constants.OPERATION, strOp);
		return clinicalStudyForm;
	}

	/**
	 * @param isToDelete
	 * @param clinicalStudyForm
	 * @param request
	 */
	private void deleteValues(String isToDelete, ClinicalStudyForm clinicalStudyForm,
			HttpServletRequest request)
	{
		/*  If the user request is to delete Event/Form/Consent, the following code will be
		 * executed.*/
		if (isToDelete != null && !isToDelete.equals(Constants.CONSENT_DELETE))
		{
			deleteSpecifiedEntity(clinicalStudyForm, request.getParameter("status"));
		}
		else if (isToDelete != null)
		{
			deleteConsents(clinicalStudyForm, request.getParameter("status"));
		}

	}

	/**
	 * @param clinicalStudyForm
	 * @param request
	 * @param staticEntityId
	 * @throws BizLogicException
	 * @throws NumberFormatException
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private void setConsentAndForm(ClinicalStudyForm clinicalStudyForm, HttpServletRequest request,
			Long staticEntityId) throws BizLogicException, NumberFormatException,
			DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
		ClinicalStudyBizLogic bizLogic = (ClinicalStudyBizLogic) factory
				.getBizLogic(clinicalStudyForm.getFormId());
		if (request.getAttribute("org.apache.struts.action.ERROR") == null)
		{
			Collection consentColl = bizLogic.getConsentCollection(clinicalStudyForm.getId());
			clinicalStudyForm
					.setConsentValues(clinicalStudyForm.prepareConsentTierMap(consentColl));
		}
		/* Populate forms */
		List studyFormsList = bizLogic.getStudyFormsList(staticEntityId);
		request.setAttribute(Constants.STUDY_FORMS_LIST, studyFormsList);
	}

	/**
	 * @param consentValues
	 * @param consentCtr
	 * @throws BizLogicException
	 */
	private void checkForConsentResponse(Map consentValues, int consentCtr)
			throws BizLogicException
	{
		for (int ctr = consentCtr; ctr > 0; ctr--)
		{
			Object consentId = consentValues.get("ConsentBean:" + ctr + "_consentTierID");
			String consentdisableKey = "ConsentBean:" + ctr + "_disableConsentKey";
			if (checkConsentResponse(consentId))
			{
				consentValues.put(consentdisableKey, Boolean.TRUE);
			}
			else
			{
				consentValues.put(consentdisableKey, Boolean.FALSE);
			}
		}

	}

	/**
	 * This method checks if response has been recorded for a consent
	 * and if no response is recorded, the consent can be edited.
	 * @param consentId
	 * @return
	 * @throws BizLogicException
	 */
	private boolean checkConsentResponse(Object consentId) throws BizLogicException
	{

		boolean flag = false;
		if (consentId != null && !consentId.equals(""))
		{
			String hql = "select consentResponse.id from edu.wustl.clinportal.domain.ClinicalStudyConsentTierResponse consentResponse where consentResponse.consentTier="
					+ consentId.toString();
			DataEntryUtil util = new DataEntryUtil();
			try
			{
				Collection responseColl = (Collection) util.executeQuery(hql);
				if (responseColl != null && !responseColl.isEmpty())
				{
					flag = true;
				}
			}
			catch (DAOException e)
			{
				throw new BizLogicException(e);
			}
		}
		return flag;
	}

	/**
	 * 
	 * @param clinicalStudyForm
	 * @param frmValues
	 * @param outerCnt
	 * @throws DAOException 
	 */
	private void checkForRecrdEntry(ClinicalStudyForm clinicalStudyForm, Map frmValues, int outerCnt)
			throws BizLogicException
	{
		for (int eventCounter = outerCnt; eventCounter >= 1; eventCounter--)
		{
			int maxIntCount = 1;
			Object obj = clinicalStudyForm.getIvl("" + eventCounter);
			if (obj != null)
			{
				maxIntCount = Integer.parseInt(obj.toString());
			}
			for (int frmCounter = maxIntCount; frmCounter >= 1; frmCounter--)
			{
				String formContextIdkey = Utility.createKey(Constants.FORM_CONTEXT, eventCounter,
						frmCounter, "id");
				Object frmCntextId = (Object) frmValues.get(formContextIdkey);
				if (checkForRecEntryColl(frmCntextId))
				{
					//disable frm cntxt dropdown, checkboxes of frmcntext and event
					String disFrmcontKey = Utility.createKey(Constants.FORM_CONTEXT, eventCounter,
							frmCounter, "disableFrmContextKey");
					String disableEventKey = Utility.createKey(Constants.CLINICAL_STUDY_EVENT,
							eventCounter, 0, "disableEventKey");
					clinicalStudyForm.getFormValues().put(disFrmcontKey, Boolean.TRUE);
					clinicalStudyForm.getFormValues().put(disableEventKey, Boolean.TRUE);

				}

			}
		}
	}

	/**
	 * 
	 * @param frmCntextId
	 * @return
	 * @throws DAOException 
	 */
	private boolean checkForRecEntryColl(Object frmCntextId) throws BizLogicException
	{
		boolean flag = false;
		if (frmCntextId != null && !frmCntextId.equals(""))
		{
			String hql = "select studyFrmCntxt.recordEntryCollection from edu.wustl.clinportal.domain.StudyFormContext studyFrmCntxt where studyFrmCntxt.id="
					+ frmCntextId.toString();
			DataEntryUtil util = new DataEntryUtil();
			try
			{
				Collection recEntryColl = (Collection) util.executeQuery(hql);
				if (recEntryColl != null && !recEntryColl.isEmpty())
				{
					flag = true;
				}
			}
			catch (DAOException e)
			{
				throw new BizLogicException(e);
			}
		}
		return flag;
	}

	/**
	 * returns the user list present in the system
	 * @param request
	 * @param operation
	 * @throws BizLogicException 
	 * @throws DAOException
	 */
	private void getUsers(HttpServletRequest request, String operation, long[] prtcolCoordIds)
			throws BizLogicException //throws DAOException
	{
		IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
		UserBizLogic userBizLogic = (UserBizLogic) factory.getBizLogic(Constants.USER_FORM_ID);
		Collection users = userBizLogic.getUsers(operation);

		List<NameValueBean> coordinators = new ArrayList<NameValueBean>();

		if (prtcolCoordIds != null && prtcolCoordIds.length > 0)
		{
			List<Long> prtCordIds = new ArrayList<Long>();

			for (int i = 0; i < prtcolCoordIds.length; i++)
			{
				prtCordIds.add(prtcolCoordIds[i]);
			}

			for (Object object : users)
			{
				NameValueBean nvbean = (NameValueBean) object;
				if (prtCordIds.contains(Long.parseLong(nvbean.getValue())))
				{
					coordinators.add(nvbean);
				}
			}
		}

		request.setAttribute(Constants.USERLIST, users);

		// Set the selected coordinators.
		request.setAttribute(edu.common.dynamicextensions.ui.util.Constants.SELECTED_VALUES,
				coordinators);
	}

	/**
		 * @param request
		 * @param operation
		 * @throws DAOException 
		 * @throws DAOException
		 */
	private void getCatissueCP(HttpServletRequest request) throws BizLogicException, DAOException
	{
		IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
		ClinicalStudyBizLogic csBizLogic = (ClinicalStudyBizLogic) factory
				.getBizLogic(Constants.CLINICALSTUDY_FORM_ID);
		Collection cpCollection = csBizLogic.getCatissueCP();
		Collection cpeCollection = csBizLogic.getCatissueCPEvent(Long.valueOf(-1));

		request.setAttribute("CPLIST", cpCollection);
		request.setAttribute("CPELIST", cpeCollection);

	}

	/**
	 * @param request
	 * @param cpId
	 * @throws DAOException
	 * @throws BizLogicException 
	 */
	private void getCatissueCPEvent(HttpServletRequest request, Long cpId) throws DAOException,
			BizLogicException
	{
		IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
		ClinicalStudyBizLogic csBizLogic = (ClinicalStudyBizLogic) factory
				.getBizLogic(Constants.CLINICALSTUDY_FORM_ID);
		Collection cpeCollection = csBizLogic.getCatissueCPEvent(cpId);
		request.setAttribute("CPELIST", cpeCollection);

	}

	/**
	 * This method generates the consentValueMap after deleting consents
	 * @param clinicalStudyForm
	 * @param status
	 */
	private void deleteConsents(ClinicalStudyForm clinicalStudyForm, String status)
	{

		String strStatus = status;
		if (strStatus == null)
		{
			strStatus = Constants.FALSE;
		}
		int consentCount = 1;
		Map<String, Object> consentValueMap = clinicalStudyForm.getConsentValues();
		if (strStatus.equals(Constants.TRUE))
		{
			for (int counter = 1; counter <= consentValueMap.size(); counter++)
			{
				String key1 = "ConsentBean:" + counter + "_consentTierID";
				String key2 = "ConsentBean:" + counter + "_statement";
				String key3 = "ConsentBean:" + counter + "_predefinedConsents";
				if (consentValueMap.containsKey(key2))
				{
					String newkey1 = "ConsentBean:" + consentCount + "_consentTierID";
					String newkey2 = "ConsentBean:" + consentCount + "_statement";
					String newkey3 = "ConsentBean:" + consentCount + "_predefinedConsents";
					consentValueMap.put(newkey1, consentValueMap.get(key1));
					consentValueMap.put(newkey2, consentValueMap.get(key2));
					consentValueMap.put(newkey3, consentValueMap.get(key3));
					consentCount++;
				}
			}
		}
		clinicalStudyForm.setConsentTierCounter(consentCount - 1);
	}

	/**
	 * deletes the events or forms
	 * @param studyForm
	 * @param status
	 */
	private void deleteSpecifiedEntity(ClinicalStudyForm studyForm, String status)
	{
		String strStatus = status;
		if (strStatus == null)
		{
			strStatus = Constants.FALSE;
		}
		int eventCount = 1;
		Map<String, Object> formValueMap = studyForm.getFormValues();
		if (strStatus.equals(Constants.TRUE))
		{
			for (int eventCounter = 1; eventCounter <= formValueMap.size(); eventCounter++)
			{
				String eventKey1 = Utility.createKey(Constants.CLINICAL_STUDY_EVENT, eventCounter,
						0, "noOfEntries");
				String eventKey2 = Utility.createKey(Constants.CLINICAL_STUDY_EVENT, eventCounter,
						0, "collectionPointLabel");
				String eventKey3 = Utility.createKey(Constants.CLINICAL_STUDY_EVENT, eventCounter,
						0, "studyCalendarEventPoint");
				String eventKey4 = Utility.createKey(Constants.CLINICAL_STUDY_EVENT, eventCounter,
						0, "id");
				if (formValueMap.containsKey(eventKey1))
				{

					generateKey(eventCount, formValueMap, studyForm, eventKey1, eventKey2,
							eventKey3, eventKey4, eventCounter);

					eventCount++;
				}
			}

		}
	}

	/**
	 * @param eventCount
	 * @param formValueMap
	 * @param studyForm
	 * @param eventKey1
	 * @param eventKey2
	 * @param eventKey3
	 * @param eventKey4
	 * @param eventCounter
	 */
	private void generateKey(int eventCount, Map<String, Object> formValueMap,
			ClinicalStudyForm studyForm, String eventKey1, String eventKey2, String eventKey3,
			String eventKey4, int eventCounter)
	{
		String event1 = Utility.createKey(Constants.CLINICAL_STUDY_EVENT, eventCount, 0,
				"noOfEntries");
		String event2 = Utility.createKey(Constants.CLINICAL_STUDY_EVENT, eventCount, 0,
				"collectionPointLabel");
		String event3 = Utility.createKey(Constants.CLINICAL_STUDY_EVENT, eventCount, 0,
				"studyCalendarEventPoint");
		String event4 = Utility.createKey(Constants.CLINICAL_STUDY_EVENT, eventCount, 0, "id");

		formValueMap.put(event1, formValueMap.get(eventKey1));
		formValueMap.put(event2, formValueMap.get(eventKey2));
		formValueMap.put(event3, formValueMap.get(eventKey3));
		formValueMap.put(event4, formValueMap.get(eventKey4));

		//int innerMapCounter = Integer.parseInt(innerMap.get(i+"").toString());
		int formCount = 1;
		for (int formCounter = 1; formCounter <= formValueMap.size(); formCounter++)
		{
			String formKey1 = Utility.createKey(Constants.FORM_CONTEXT, eventCounter, formCounter,
					"containerId");
			String formKey2 = Utility.createKey(Constants.FORM_CONTEXT, eventCounter, formCounter,
					"studyFormLabel");
			String formKey3 = Utility.createKey(Constants.FORM_CONTEXT, eventCounter, formCounter,
					"id");

			if (formValueMap.containsKey(formKey1))
			{
				String studyIdKey = Utility.createKey(Constants.FORM_CONTEXT, eventCount,
						formCount, "containerId");
				String studyLabelKey = Utility.createKey(Constants.FORM_CONTEXT, eventCount,
						formCount, "studyFormLabel");
				String idKey = Utility.createKey(Constants.FORM_CONTEXT, eventCount, formCount,
						"id");

				formValueMap.put(studyIdKey, formValueMap.get(formKey1));
				formValueMap.put(studyLabelKey, formValueMap.get(formKey2));
				formValueMap.put(idKey, formValueMap.get(formKey3));
				formCount++;
			}

		}
		studyForm.setIvl(String.valueOf(eventCount), String.valueOf(formCount - 1));

	}

}