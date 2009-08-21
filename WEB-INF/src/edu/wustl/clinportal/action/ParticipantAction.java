/**
 * <p>Title: ParticipantAction Class>
 * <p>Description:  This class initializes the fields in the Participant Add/Edit webpage. </p>
 * Copyright:    Copyright (c)2008 year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Falguni Sachde
 * 
 * 
 */

package edu.wustl.clinportal.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
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

import edu.wustl.clinportal.actionForm.ParticipantForm;
import edu.wustl.clinportal.bizlogic.ClinicalStudyBizLogic;
import edu.wustl.clinportal.bizlogic.ParticipantBizLogic;
import edu.wustl.clinportal.domain.ClinicalStudy;
import edu.wustl.clinportal.domain.ClinicalStudyRegistration;
import edu.wustl.clinportal.domain.Participant;
import edu.wustl.clinportal.domain.ParticipantMedicalIdentifier;
import edu.wustl.clinportal.domain.Site;
import edu.wustl.clinportal.privilege.PrivilegeConstants;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.clinportal.util.global.Utility;
import edu.wustl.common.action.SecureAction;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.bizlogic.IBizLogic;
import edu.wustl.common.cde.CDEManager;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;
import edu.wustl.common.util.MapDataParser;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.security.exception.SMException;
import edu.wustl.security.privilege.PrivilegeCache;
import edu.wustl.security.privilege.PrivilegeManager;

/**
 * This class initializes the fields in the Participant Add/Edit web page.
 * @author gautam_shetty
 */
public class ParticipantAction extends SecureAction
{

	private transient Logger logger = Logger.getCommonLogger(ParticipantAction.class);

	/**
	 * Overrides the execute method of Action class.
	 * Sets the various fields in Participant Add/Edit web page.
	 * @param mapping object of ActionMapping
	 * @param form object of ActionForm
	 * @param request object of HttpServletRequest
	 * @param response object of HttpServletResponse
	 * @throws Exception generic exception
	 * @return value for ActionForward object
	 */

	protected ActionForward executeSecureAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		ParticipantForm participantForm = (ParticipantForm) form;
		HttpSession session = request.getSession();
		setRequestAttribute(request, participantForm, session);
		// Set Selected Cp id in participant form so that it is get reflected while adding SCG
		setCPId(request, participantForm);
		setConsentResponse(request, participantForm, session);
		Map map = prepareDateMap(request, participantForm);

		//Start Collection Protocol Registration For Participant
		List cprKey = new ArrayList();
		setCPRKeys(cprKey);
		Map mapCPRegistration = participantForm.getCollectionProtocolRegistrationValues();
		submitAction(request, form, mapCPRegistration, map);

		MapDataParser.deleteRow(cprKey, mapCPRegistration, "true");

		//Sets the collection Protocol if page is opened from collection protocol registration
		String pageOf = request.getParameter(Constants.PAGEOF);
	   //Added by Virender
		participantForm.setRegistrationDate(edu.wustl.common.util.Utility.parseDateToString(Calendar
				.getInstance().getTime(), Constants.DATE_PATTERN_DD_MM_YYYY));
		participantForm.setState("Maharashtra");
		participantForm.setCountry("India");
		if (participantForm.getOperation().equals(Constants.ADD)
				&& pageOf.equalsIgnoreCase(Constants.PAGE_OF_PARTICIPANT_CP_QUERY))
		{
			// If one is registering for given collection protocol
			String cpId = request.getParameter(Constants.CP_SEARCH_CP_ID);
			if (cpId != null)
			{
				setCPRegistration(participantForm, cpId);

			}

		}
		//Sets the collection Protocol if page is opened in add mode or if that participant doesn't have any registration
		if (mapCPRegistration != null && mapCPRegistration.isEmpty()
				|| participantForm.getCollectionProtocolRegistrationValueCounter() == 0)
		{
			String cprDateKey = "ClinicalStudyRegistration:1_registrationDate";
			String cprActivStausKey = "ClinicalStudyRegistration:1_activityStatus";
			String cprDateValue = edu.wustl.common.util.Utility.parseDateToString(Calendar
					.getInstance().getTime(), Constants.DATE_PATTERN_MM_DD_YYYY);
			participantForm.setDefaultCollectionProtocolRegistrationValue(cprDateKey, cprDateValue);
			participantForm.setDefaultCollectionProtocolRegistrationValue(cprActivStausKey,
					Constants.ACTIVITY_STATUS_ACTIVE);
			participantForm.setCollectionProtocolRegistrationValueCounter(1);
		}

		//End Collection Protocol Registration For Participant

		//Gets the value of the operation parameter.
		String operation = request.getParameter(Constants.OPERATION);
		//Sets the operation attribute to be used in the Add/Edit Participant Page. 
		request.setAttribute(Constants.OPERATION, operation);
		request.setAttribute(Constants.PAGEOF, pageOf);
		setGender(participantForm, request);
		setValueList(participantForm, request);

		//Sets the activityStatusList attribute to be used in the Site Add/Edit Page.
		request.setAttribute(Constants.ACTIVITYSTATUSLIST, Constants.ACTIVITY_STATUS_VALUES);

		IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
		ParticipantBizLogic bizlogic = (ParticipantBizLogic) factory
				.getBizLogic(Constants.PARTICIPANT_FORM_ID);

		//Sets the Site list of corresponding type.
		String sourceObjectName = Site.class.getName();
		String[] displayNameFields = {"name"};
		String valueField = Constants.SYSTEM_IDENTIFIER;
		List siteList = bizlogic.getList(sourceObjectName, displayNameFields, valueField, true);
		request.setAttribute(Constants.SITELIST, siteList);

		//Set the collection protocol title list
		ClinicalStudyBizLogic csBizLogic = new ClinicalStudyBizLogic();
		SessionDataBean sessionDataBean = (SessionDataBean) session
				.getAttribute(Constants.SESSION_DATA);
		List<NameValueBean> list = csBizLogic.getCsList(sessionDataBean);
		request.setAttribute(Constants.PROTOCOL_LIST, list);
		populateClinicalStudyActivityStatus(participantForm, session, csBizLogic, list);
		logger.debug("Inside Participant Action,pageOf :-----" + pageOf);
		if (checkForPrivileges(request))
		{
			pageOf = edu.wustl.common.util.global.Constants.ACCESS_DENIED;
		}
		checkPermissionOnActivityStatus(sessionDataBean, request);
		return mapping.findForward(pageOf);
	}

	/**
	 * 
	 * @param sessionDataBean
	 * @param request
	 * @throws SMException
	 */
	private void checkPermissionOnActivityStatus(SessionDataBean sessionDataBean,
			HttpServletRequest request) throws SMException
	{
		ParticipantBizLogic bizLogic = new ParticipantBizLogic();
		if (bizLogic.checkPermissionOnActivityStatus(sessionDataBean.getUserName()))
		{
			request.setAttribute(Constants.PARTICIPANT_ACTIVITY_PRIVILEGE, true);
		}
		else
		{
			request.setAttribute(Constants.PARTICIPANT_ACTIVITY_PRIVILEGE, false);
		}
	}

	/**
	 * 
	 * @param request
	 * @throws BizLogicException
	 */
	private boolean checkForPrivileges(HttpServletRequest request) throws BizLogicException
	{
		boolean result = false;
		try
		{

			PrivilegeManager privilegeManager = PrivilegeManager.getInstance();
			SessionDataBean sessionDataBean = (SessionDataBean) request.getSession().getAttribute(
					Constants.SESSION_DATA);
			PrivilegeCache privilegeCache = privilegeManager.getPrivilegeCache(sessionDataBean
					.getUserName());
			String csId = request.getParameter(Constants.CP_SEARCH_CP_ID);
			if (csId != null)
			{
				String objectId = ClinicalStudy.class.getName() + "_" + csId;
				result = privilegeCache.hasPrivilege(objectId, PrivilegeConstants.DISALLOW_REG);
				if (result)
				{
					ActionErrors errors = new ActionErrors();
					ActionError error = new ActionError("access.execute.action.denied");
					errors.add(ActionErrors.GLOBAL_ERROR, error);
					saveErrors(request, errors);
				}
			}
		}
		catch (SMException e)
		{
			logger.debug(e.getMessage(), e);
			throw new BizLogicException(null, null, ApplicationProperties
					.getValue("user.disallowReg"));
		}
		return result;
	}

	/**
	 * @param cprKey
	 */
	private void setCPRKeys(List cprKey)
	{
		cprKey.add("ClinicalStudyRegistration:outer_ClinicalStudy_id");
		cprKey.add("ClinicalStudyRegistration:outer_ClinicalStudy_shortTitle");
		cprKey.add("ClinicalStudyRegistration:outer_clinicalStudyParticipantIdentifier");
		cprKey.add("ClinicalStudyRegistration:outer_id");
		cprKey.add("ClinicalStudyRegistration:outer_registrationDate");
		cprKey.add("ClinicalStudyRegistration:outer_isConsentAvailable");
		cprKey.add("ClinicalStudyRegistration:outer_activityStatus");

	}

	/**
	 * @param request
	 * @param participantForm
	 * @param session
	 */
	private void setConsentResponse(HttpServletRequest request, ParticipantForm participantForm,
			HttpSession session)
	{
		if (participantForm.getOperation().equals(Constants.EDIT))
		{
			request.setAttribute("participantId", Long.valueOf(participantForm.getId()).toString());
			//Setting Consent Response Bean to Session
			Hashtable consentRespHtble = participantForm.getConsentResponseHashTable();
			if (consentRespHtble != null)
			{
				session.setAttribute(Constants.CONSENT_RESPONSE, consentRespHtble);
			}
		}

	}

	/**
	 * @param participantForm
	 * @param collProtoId
	 * @throws BizLogicException 
	 * @throws NumberFormatException 
	 */
	private void setCPRegistration(ParticipantForm participantForm, String collProtoId)
			throws NumberFormatException, BizLogicException
	{
		String cProtoIdKey = "ClinicalStudyRegistration:1_ClinicalStudy_id";
		String isCsentAvbleKey = "ClinicalStudyRegistration:1_isConsentAvailable";
		String cprActStausKey = "ClinicalStudyRegistration:1_activityStatus";
		String cprDateKey = "ClinicalStudyRegistration:1_registrationDate";

		participantForm.setCollectionProtocolRegistrationValue(cProtoIdKey, collProtoId);

		Collection consentList = getConsentList(collProtoId);
		if (consentList != null && consentList.isEmpty())
		{
			participantForm.setCollectionProtocolRegistrationValue(isCsentAvbleKey,
					Constants.NO_CONSENTS_DEFINED);
		}
		else if (consentList != null && !consentList.isEmpty())
		{
			participantForm.setCollectionProtocolRegistrationValue(isCsentAvbleKey,
					Constants.PARTICIPANT_CONSENT_ENTER_RESPONSE);
		}
		participantForm.setCollectionProtocolRegistrationValue(cprActStausKey,
				Constants.ACTIVITY_STATUS_ACTIVE);
		String cprDateValue = edu.wustl.common.util.Utility.parseDateToString(Calendar
				.getInstance().getTime(), Constants.DATE_PATTERN_MM_DD_YYYY);
		participantForm.setCollectionProtocolRegistrationValue(cprDateKey, cprDateValue);
		participantForm.setCollectionProtocolRegistrationValueCounter(1);

	}

	/**
	 * @param request
	 * @param participantForm
	 */
	private void setCPId(HttpServletRequest request, ParticipantForm participantForm)
	{
		String cpid = (String) request.getParameter("cpSearchCpId");
		if (participantForm.getCpId() == -1 && cpid != null)
		{
			participantForm.setCpId(Long.valueOf(cpid).longValue());
		}
	}

	/**
	 * @param participantForm
	 * @param request
	 */
	private void setValueList(ParticipantForm participantForm, HttpServletRequest request)
	{
		//Sets the genotypeList attribute to be used in the Add/Edit Participant Page.

		List genotypeList = CDEManager.getCDEManager().getPermissibleValueList(
				Constants.CDE_NAME_GENOTYPE, null);
		request.setAttribute(Constants.GENOTYPE_LIST, genotypeList);

		//Sets the ethnicityList attribute to be used in the Add/Edit Participant Page.
		List ethnicityList = CDEManager.getCDEManager().getPermissibleValueList(
				Constants.CDE_NAME_ETHNICITY, null);
		request.setAttribute(Constants.ETHNICITY_LIST, ethnicityList);

		//Sets the raceList attribute to be used in the Add/Edit Participant Page.
		List raceList = CDEManager.getCDEManager().getPermissibleValueList(Constants.CDE_NAME_RACE,
				null);
		request.setAttribute(Constants.RACELIST, raceList);

		//Sets the vitalStatus attribute to be used in the Add/Edit Participant Page.
		List vitalStatusList = CDEManager.getCDEManager().getPermissibleValueList(
				Constants.CDE_VITAL_STATUS, null);
		vitalStatusList.remove(0);
		request.setAttribute(Constants.VITAL_STATUS_LIST, vitalStatusList);
		if (participantForm.getVitalStatus() == null || participantForm.getVitalStatus().equals(""))
		{
			Iterator itr = vitalStatusList.iterator();
			while (itr.hasNext())
			{
				NameValueBean nvb = (NameValueBean) itr.next();
				participantForm.setVitalStatus(nvb.getValue());
				break;
			}

		}

	}

	/**
	 * @param participantForm
	 * @param request
	 */
	private void setGender(ParticipantForm participantForm, HttpServletRequest request)
	{
		//Sets the genderList attribute to be used in the Add/Edit Participant Page.
		List genderList = CDEManager.getCDEManager().getPermissibleValueList(
				Constants.CDE_NAME_GENDER, null);
		genderList.remove(0);
		request.setAttribute(Constants.GENDER_LIST, genderList);
		if (participantForm.getGender() == null || participantForm.getGender().equals(""))
		{
			Iterator itr = genderList.iterator();
			while (itr.hasNext())
			{
				NameValueBean nvb = (NameValueBean) itr.next();
				participantForm.setGender(nvb.getValue());
				break;
			}

		}

	}

	/**
	 * @param request
	 * @param form
	 * @param mapCProtRegn
	 * @param map
	 * @throws Exception
	 */
	private void submitAction(HttpServletRequest request, ActionForm form, Map mapCProtRegn, Map map)
			throws Exception
	{

		ParticipantForm participantForm = (ParticipantForm) form;
		String fromSubmitAction = request.getParameter("fromSubmitAction"); // If submit button is pressed
		if (fromSubmitAction != null)
		{
			// Following method call is added to set ParticipantMedicalNumber id in the map after add/edit operation
			int count = participantForm.getCollectionProtocolRegistrationValueCounter();
			setParticipantMedicalNumberId(participantForm.getId(), map);
			//Updating Collection Protocol Registration
			updateCollectionProtocolRegistrationCollection(participantForm, count);
			int cprCount = updateCollectionProtocolRegistrationMap(mapCProtRegn, count);
			participantForm.setCollectionProtocolRegistrationValueCounter(cprCount);
		}
		else if (mapCProtRegn != null && !mapCProtRegn.isEmpty())
		{
			setCollectionProtocolRegistrationValue(participantForm, mapCProtRegn);

		}

	}

	/**
	 * @param participantForm
	 * @param mapCPRegn
	 */
	private void setCollectionProtocolRegistrationValue(ParticipantForm participantForm,
			Map mapCPRegn)
	{
		int count = participantForm.getCollectionProtocolRegistrationValueCounter();
		for (int i = 1; i <= count; i++)
		{
			String cpractStausKey = "ClinicalStudyRegistration:" + i + "_activityStatus";
			if (mapCPRegn.get(cpractStausKey) == null)
			{
				participantForm.setCollectionProtocolRegistrationValue(cpractStausKey,
						Constants.ACTIVITY_STATUS_ACTIVE);
			}
		}

	}

	/**
	 * @param request
	 * @param participantForm
	 * @return
	 */
	private Map prepareDateMap(HttpServletRequest request, ActionForm participantForm)
	{
		//List of keys used in map of ActionForm
		List key = new ArrayList();
		key.add("ParticipantMedicalIdentifier:outer_Site_id");
		key.add("ParticipantMedicalIdentifier:outer_medicalRecordNumber");
		key.add("ParticipantMedicalIdentifier:outer_id");

		//Gets the map from ActionForm
		Map map = ((ParticipantForm) participantForm).getValues();

		String delRegtion = request.getParameter("deleteRegistration");
		String status = request.getParameter("status");
		if (delRegtion == null && status != null && status.equalsIgnoreCase("true"))
		{
			//Calling DeleteRow of BaseAction class
			MapDataParser.deleteRow(key, map, request.getParameter("status"));
		}
		return map;

	}

	/**
	 * @param request
	 * @param participantForm
	 * @param session
	 */
	private void setRequestAttribute(HttpServletRequest request,
            ActionForm participantForm, HttpSession session)
    {
        setRefreshParticipant(request);
        //This if condition is for participant lookup. When participant is selected from the list then 
        //that participant gets stored in request as participantform1.
        //After that we have to show the selected participant in o/p
        if (request.getAttribute("participantSelect") != null
                && request.getAttribute("participantForm1") != null)
        {
            participantForm = (ParticipantForm) request.getAttribute("participantForm1");
            ParticipantForm participantForm1 = (ParticipantForm) participantForm;
            try
            {
                setParticipantCollectionProtocolRegistrationId(participantForm1
                        .getId(), participantForm1.getCollectionProtocolRegistrationValues(),
                        participantForm1.getCollectionProtocolRegistrationValues().size());
            }
            catch (Exception e)
            {
                logger.info(e.getMessage());
                e.printStackTrace();
            }
            request.setAttribute("participantForm", participantForm);
        }
        String operation = ((ParticipantForm) participantForm).getOperation();
        clearConsent(operation, request, session);
    }

	/**
	 * @param operation
	 * @param request
	 * @param session
	 */
	private void clearConsent(String operation, HttpServletRequest request, HttpSession session)
	{
		String clrCosentSes = request.getParameter("clearConsentSession");
		if (operation.equals(Constants.ADD) && clrCosentSes != null && clrCosentSes.equals("true"))
		{
			session.removeAttribute(Constants.CONSENT_RESPONSE);

		}

	}

	/**
	 * @param request
	 */
	private void setRefreshParticipant(HttpServletRequest request)
	{
		String rfreshPpant = (String) request.getParameter("refresh");
		if (rfreshPpant != null)
		{
			request.setAttribute("refresh", rfreshPpant);

		}

	}

	/**
	 * @param participantForm
	 * @param session
	 * @param csBizLogic
	 * @param list
	 * @throws BizLogicException 
	 * @throws NumberFormatException 
	 */
	private void populateClinicalStudyActivityStatus(ParticipantForm participantForm,
			HttpSession session, ClinicalStudyBizLogic csBizLogic, List<NameValueBean> list)
			throws NumberFormatException, BizLogicException
	{
		Map<Long, String> cStudyActStatus = new HashMap<Long, String>();
		int counter = participantForm.getCollectionProtocolRegistrationValueCounter();
		for (int i = 1; i <= counter; i++)
		{
			String csidKey = "ClinicalStudyRegistration:" + i + "_ClinicalStudy_id";
			String csid = (String) participantForm.getCollectionProtocolRegistrationValue(csidKey);
			if (csid != null)
			{
				List collProtList = csBizLogic.retrieve(ClinicalStudy.class.getName(),
						Constants.ID, Long.valueOf(csid));
				if (collProtList.size() != 0)
				{
					ClinicalStudy clinicalStudy = (ClinicalStudy) collProtList.get(0);
					cStudyActStatus.put(clinicalStudy.getId(), clinicalStudy.getActivityStatus());
				}
			}
		}
		Iterator<NameValueBean> iterator = list.iterator();
		while (iterator.hasNext())
		{
			NameValueBean bean = iterator.next();
			if (!bean.getValue().equals("-1"))
			{
				List collProtList = csBizLogic.retrieve(ClinicalStudy.class.getName(),
						Constants.ID, Long.valueOf(bean.getValue()));
				ClinicalStudy clinicalStudy = (ClinicalStudy) collProtList.get(0);
				if (!cStudyActStatus.containsKey(clinicalStudy.getId()))
				{
					cStudyActStatus.put(clinicalStudy.getId(), clinicalStudy.getActivityStatus());
				}
			}
		}

		session.setAttribute(Constants.CS_ACTIVITY_STATUS, cStudyActStatus);
	}

	/**
	 * Consent List for given collection protocol
	 * @param cpID
	 * @return
	 * @throws BizLogicException 
	 * @throws NumberFormatException 
	 */
	private Collection getConsentList(String cpID) throws NumberFormatException, BizLogicException
	{
		IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
		IBizLogic bizLogic = factory.getBizLogic(Constants.DEFAULT_BIZ_LOGIC);
		List<ClinicalStudy> collProtList = bizLogic.retrieve(ClinicalStudy.class.getName(),
				Constants.ID, Long.valueOf(cpID));
		ClinicalStudy colProto = (ClinicalStudy) collProtList.get(0);
		//Setting consent tiers
		//Resolved lazy --- collectionProtocol.getConsentTierCollection()
		Collection csentTierCol = (Collection) bizLogic.retrieveAttribute(ClinicalStudy.class
				.getName(), colProto.getId(), "elements(consentTierCollection)");
		return csentTierCol;
	}

	/**
	 * Update collection protocol registration info for given participant.
	 * @param participantForm
	 * @param count
	 * @throws Exception
	 */
	private void updateCollectionProtocolRegistrationCollection(ParticipantForm participantForm,
			int count) throws Exception
	{
		//Gets the collection Protocol Registration map from ActionForm
		Map mapCPR = participantForm.getCollectionProtocolRegistrationValues();

		if (mapCPR != null && !mapCPR.isEmpty())
		{
			participantForm.getConsentResponseBeanCollection();
			setParticipantCollectionProtocolRegistrationId(participantForm.getId(), mapCPR, count);
		}
	}

	/**
	 * Removing collection protocol registration info if it is disabled.
	 * @param mapCPR
	 * @param count
	 * @return
	 * @throws Exception
	 */
	private int updateCollectionProtocolRegistrationMap(Map mapCPR, int count) throws Exception
	{
		int cprCount = 0;
		for (int i = 1; i <= count; i++)
		{
			String isActive = "ClinicalStudyRegistration:" + i + "_activityStatus";
			String colProtoTitle = "ClinicalStudyRegistration:" + i + "_ClinicalStudy_id";
			String activityStatus = (String) mapCPR.get(isActive);
			String cpId = (String) mapCPR.get(colProtoTitle);
			if (cpId == null)
			{
				cpId = "-1";

			}
			if (activityStatus == null)
			{
				mapCPR.put(isActive, Constants.ACTIVITY_STATUS_ACTIVE);
			}

			if (activityStatus != null && activityStatus.equalsIgnoreCase(Constants.DISABLED)
					|| cpId.equalsIgnoreCase("-1"))
			{

				String cpPartiId = "ClinicalStudyRegistration:" + i
						+ "_clinicalStudyParticipantIdentifier";
				String cprDate = "ClinicalStudyRegistration:" + i + "_registrationDate";
				String cprId = "ClinicalStudyRegistration:" + i + "_id";
				String isCsentAble = "ClinicalStudyRegistration:" + i + "_isConsentAvailable";
				String cpPartTitle = "ClinicalStudyRegistration:" + i + "_ClinicalStudy_shortTitle";

				mapCPR.remove(colProtoTitle);
				mapCPR.remove(cpPartiId);
				mapCPR.remove(cprDate);
				mapCPR.remove(cprId);
				mapCPR.remove(isActive);
				mapCPR.remove(isCsentAble);
				mapCPR.remove(cpPartTitle);
				cprCount++;
			}
		}
		return (count - cprCount);
	}

	/**
	* THis method sets the ParticipantMedicalNumber id in the map
	* Bug_id: 4386
	* After adding new participant medical number CommonAddEdit was unable to set id in the value map for participant medical number
	* Therefore here explicitly id of the participant medical number are set
	* 
	* @param participantId id of the current participant
	* @param map map that holds ParticipantMedicalNumber(s)
	* @throws Exception generic exception
	*/
	private void setParticipantMedicalNumberId(Long participantId, Map map) throws Exception
	{
		IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
		ParticipantBizLogic bizLogic = (ParticipantBizLogic) factory.getBizLogic(Participant.class
				.getName());
		Collection pmIdCollection = (Collection) bizLogic.retrieveAttribute(Participant.class
				.getName(), participantId, "elements(participantMedicalIdentifierCollection)");
		Iterator iter = pmIdCollection.iterator();
		int size=pmIdCollection.size();
		while (iter.hasNext())
		{
			ParticipantMedicalIdentifier pmi = (ParticipantMedicalIdentifier) iter.next();
			for (int i = 1; i <= size; i++)
			{
				//check for null medical record number since for participant having no PMI an empty PMI object is added
				if (pmi.getMedicalRecordNumber() != null
						&& pmi.getSite().getId().toString() != null)
				{
					// check for site id and medical number, if they both matches then set id to the respective participant medical number
				    if(handleMap(i,map,pmi))
				        break;
				}
			}
		}
	}

	/**
	 * 
	 * @param i
	 * @param map
	 * @param pmi
	 */
	private boolean handleMap(int i, Map map, ParticipantMedicalIdentifier pmi)
    {
	    boolean flag=false;
        String medIdKey = Utility.getParticipantMedicalIdentifierKeyFor(i,
                Constants.PARTICIPANT_MEDICAL_IDENTIFIER_MEDICAL_NUMBER);
        String medSiteIdKey = Utility.getParticipantMedicalIdentifierKeyFor(i,
                Constants.PARTICIPANT_MEDICAL_IDENTIFIER_SITE_ID);
        if (map.get(medIdKey) != null && map.get(medSiteIdKey) != null)// &&
        {
            if (((String) (map.get(medIdKey))).equalsIgnoreCase(pmi.getMedicalRecordNumber())
                    && ((String) (map.get(medSiteIdKey))).equalsIgnoreCase(pmi.getSite().getId().toString()))
            {
                map.put(Utility.getParticipantMedicalIdentifierKeyFor(i,
                        Constants.PARTICIPANT_MEDICAL_IDENTIFIER_ID), pmi
                        .getId().toString());
                flag=true;
            }
        }
        return flag;

    }

    /**
	 * This method sets the clinical Study Registration id in the map
	 * @param participantId
	 * @param map
	 * @param cprCount
	 * @throws Exception
	 */
	private void setParticipantCollectionProtocolRegistrationId(Long participantId, Map map,
			int cprCount) throws Exception
	{
		IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
		ParticipantBizLogic bizLogic = (ParticipantBizLogic) factory.getBizLogic(Participant.class
				.getName());
		Collection cStudyRegCol = (Collection) bizLogic.retrieveAttribute(Participant.class
				.getName(), participantId, "elements(clinicalStudyRegistrationCollection)");

		Iterator iter = cStudyRegCol.iterator();

		while (iter.hasNext())
		{
			ClinicalStudyRegistration cpri = (ClinicalStudyRegistration) iter.next();
			for (int i = 1; i <= cprCount; i++)
			{
				if (cpri.getClinicalStudy() != null)
				{
					String cStudyIdKey = "ClinicalStudyRegistration:" + i + "_ClinicalStudy_id";
					String csrIdKey = "ClinicalStudyRegistration:" + i + "_id";
					String isActive = "ClinicalStudyRegistration:" + i + "_activityStatus";
					if (map.containsKey(cStudyIdKey))
					{
						if (((String) map.get(cStudyIdKey)).equalsIgnoreCase(cpri
								.getClinicalStudy().getId().toString()))
						{
							map.put(csrIdKey, cpri.getId().toString());
							map.put(isActive, cpri.getActivityStatus());

							break;
						}
					}
				}
			}
		}
	}

}
