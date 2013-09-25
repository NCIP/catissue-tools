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
 * <p>Title: ConsentResponseDisplayAction Class>
 * <p>Description: ConsentResponseDisplayAction class is for displaying consent response on ParticipantConsentTracking.jsp </p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, 
 * @author Rukhsana Sameer
 * @version 1.00
 * 
 */

package edu.wustl.clinportal.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.clinportal.actionForm.ConsentResponseForm;
import edu.wustl.clinportal.bean.ConsentBean;
import edu.wustl.clinportal.bean.ConsentResponseBean;
import edu.wustl.clinportal.bizlogic.ClinicalStudyBizLogic;
import edu.wustl.clinportal.bizlogic.ClinicalStudyRegistrationBizLogic;
import edu.wustl.clinportal.domain.ClinicalStudy;
import edu.wustl.clinportal.domain.ClinicalStudyConsentTier;
import edu.wustl.clinportal.domain.ClinicalStudyRegistration;
import edu.wustl.clinportal.domain.User;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.clinportal.util.global.Utility;
import edu.wustl.common.action.BaseAction;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.bizlogic.IBizLogic;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;
import edu.wustl.common.util.logger.Logger;

public class ConsentResponseDisplayAction extends BaseAction
{

	private transient Logger logger = Logger.getCommonLogger(ConsentResponseDisplayAction.class);

	//This will keep track of no of consents for a particular participant
	int consentCounter;

	/* (non-Javadoc)
	 * @see edu.wustl.common.action.BaseAction#executeAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected ActionForward executeAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		ConsentResponseForm consentForm = (ConsentResponseForm) form;
		HttpSession session = request.getSession();

		//Gets the value of the operation parameter.
		String operation = request.getParameter(Constants.OPERATION);

		//Gets the value of collection protocol id.
		String cProtocolId = null;
		if (request.getParameter(Constants.CP_SEARCH_CP_ID) != null)
		{
			cProtocolId = request.getParameter(Constants.CP_SEARCH_CP_ID);
		}
		String cProtoRegIdValue = null;
		if (request.getParameter("collectionProtocolRegIdValue") != null)
		{
			cProtoRegIdValue = request.getParameter("collectionProtocolRegIdValue");
		}

		long cpId = Long.valueOf(cProtocolId);
		if (cProtoRegIdValue != null && !(cProtoRegIdValue.equals("")))
		{
			getcsrObj(cProtoRegIdValue);
		}
		//Getting witness name list for CollectionProtocolID
		List witnessList = getWitnessNameList(cProtocolId);
		//Getting ResponseList if Operation=Edit then "Withdraw" is added to the List 
		List responseList = Utility.responceList(operation);
		//Getting consent response map.
		String csentResKey = Constants.CONSENT_RESPONSE_KEY + cpId;
		Hashtable csentResHTable = (Hashtable) session.getAttribute(Constants.CONSENT_RESPONSE);

		Map csentResMap = setConsentMap(csentResHTable, csentResKey, consentForm, cProtocolId);
		consentForm.setCollectionProtocolID(cpId);
		consentForm.setConsentResponseValues(csentResMap);
		consentForm.setConsentTierCounter(consentCounter);
		String pageOf = request.getParameter(Constants.PAGEOF);

		request.setAttribute("witnessList", witnessList);
		request.setAttribute("responseList", responseList);
		request.setAttribute("cpId", cProtocolId);
		request.setAttribute(Constants.PAGEOF, pageOf);
		request.setAttribute(csentResKey, csentResMap);

		return mapping.findForward(pageOf);
	}

	/**
	 * @param csentResHTable
	 * @param csentResKey
	 * @param consentForm
	 * @param cProtocolId
	 * @return
	 * @throws NumberFormatException
	 * @throws BizLogicException
	 */
	private Map setConsentMap(Hashtable csentResHTable, String csentResKey,
			ConsentResponseForm consentForm, String cProtocolId) throws NumberFormatException,
			BizLogicException
	{
		Map csentResMap;
		if (csentResHTable != null && csentResHTable.containsKey(csentResKey)) // If Map already exist in session
		{
			ConsentResponseBean csentResBean = (ConsentResponseBean) csentResHTable
					.get(csentResKey);
			Collection csentResCol = csentResBean.getConsentResponse();
			csentResMap = getConsentResponseMap(csentResCol, true);
			consentForm.setSignedConsentUrl(csentResBean.getSignedConsentUrl());
			consentForm.setWitnessId(csentResBean.getWitnessId());
			consentForm.setConsentDate(csentResBean.getConsentDate());
			Collection consentCollection = getConsentList(cProtocolId);
			addNewConsentsToMap(csentResMap, consentCollection);

		}
		else
		{
			Collection csentResCol = getConsentList(cProtocolId);
			csentResMap = getConsentResponseMap(csentResCol, false);
		}
		return csentResMap;

	}

	/*
	 * This method adds the consents that have been added to the clinical study 
	 * after the participant was registered, to the responseMap of 
	 * already registered participants. 
	 */
	/**
	 * @param csentResponseMap
	 * @param consentCollection
	 */
	private void addNewConsentsToMap(Map csentResponseMap, Collection consentCollection)
	{
		Iterator consentIterator = consentCollection.iterator();
		String idKey = null;
		String statementKey = null;
		String responsekey = null;
		String partiResIdKey = null;
		int cnt = consentCounter;
		while (consentIterator.hasNext())
		{
			ClinicalStudyConsentTier consent = (ClinicalStudyConsentTier) consentIterator.next();
			idKey = "ConsentBean:" + cnt + "_consentTierID";
			statementKey = "ConsentBean:" + cnt + "_statement";
			responsekey = "ConsentBean:" + cnt + "_participantResponse";
			partiResIdKey = "ConsentBean:" + cnt + "_participantResponseID";
			if (!csentResponseMap.containsValue(consent.getId().toString()))
			{
				csentResponseMap.put(idKey, consent.getId());
				csentResponseMap.put(statementKey, consent.getStatement());
				csentResponseMap.put(responsekey, "");
				csentResponseMap.put(partiResIdKey, "");
				cnt++;
				consentCounter++;
			}

		}

	}

	/**
	 * This function will return ClinicalStudyRegistration object 
	**/

	/**
	 * @param csr_id
	 * @return
	 * @throws BizLogicException 
	 */
	private ClinicalStudyRegistration getcsrObj(String csr_id) throws BizLogicException
	{
		IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
		ClinicalStudyRegistrationBizLogic csrBizLogic = (ClinicalStudyRegistrationBizLogic) factory
				.getBizLogic(Constants.CLINICALSTUDY_FORM_ID);
		String colName = "id";
		List getCSRIdFromDB = csrBizLogic.retrieve(ClinicalStudyRegistration.class.getName(),
				colName, csr_id);
		ClinicalStudyRegistration cStudyRegObject = (ClinicalStudyRegistration) getCSRIdFromDB
				.get(0);
		return cStudyRegObject;
	}

	/**
	 * Returns the Map of consent responses for given collection protocol.
	 * @param consentResponse
	 * @param isMapExist
	 * @return
	 */
	private Map getConsentResponseMap(Collection consentResponse, boolean isMapExist)
	{
		Map csentResMap = new LinkedHashMap();
		if (consentResponse != null)
		{
			int cnt = 0;
			Iterator csentResIter = consentResponse.iterator();
			String idKey = null;
			String statementKey = null;
			String responsekey = null;
			String partResponIdKey = null;
			while (csentResIter.hasNext())
			{
				idKey = "ConsentBean:" + cnt + "_consentTierID";
				statementKey = "ConsentBean:" + cnt + "_statement";
				responsekey = "ConsentBean:" + cnt + "_participantResponse";
				partResponIdKey = "ConsentBean:" + cnt + "_participantResponseID";

				if (isMapExist)
				{
					ConsentBean consentBean = (ConsentBean) csentResIter.next();
					csentResMap.put(idKey, consentBean.getConsentTierID());
					csentResMap.put(statementKey, consentBean.getStatement());
					csentResMap.put(responsekey, consentBean.getParticipantResponse());
					csentResMap.put(partResponIdKey, consentBean.getParticipantResponseID());
				}
				else
				{
					ClinicalStudyConsentTier consent = (ClinicalStudyConsentTier) csentResIter
							.next();
					csentResMap.put(idKey, consent.getId());
					csentResMap.put(statementKey, consent.getStatement());
					csentResMap.put(responsekey, "");
					csentResMap.put(partResponIdKey, "");
				}
				cnt++;
			}
			consentCounter = cnt;
		}
		return csentResMap;
	}

	/**
	 * Adding name,value pair in NameValueBean for Witness Name
	 *
	 */
	/**
	 * @param csID
	 * @return
	 * @throws BizLogicException 
	 * @throws NumberFormatException 
	 */
	public Collection getConsentList(String csID) throws NumberFormatException, BizLogicException
	{
		IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();

		ClinicalStudyBizLogic csBizLogic = (ClinicalStudyBizLogic) factory
				.getBizLogic(Constants.CLINICALSTUDY_FORM_ID);
		List csList = csBizLogic.retrieve(ClinicalStudy.class.getName(), Constants.ID, Long
				.valueOf(csID));
		ClinicalStudy clinicalStudy = (ClinicalStudy) csList.get(0);
		//Setting consent tiers
		//Resolved lazy --- collectionProtocol.getConsentTierCollection()
		Collection csentTierCol = (Collection) csBizLogic.retrieveAttribute(ClinicalStudy.class
				.getName(), clinicalStudy.getId(), "elements(consentTierCollection)");
		return csentTierCol;
	}

	/**
	 * Adding name,value pair in NameValueBean for Witness Name
	 * @param collProtId Get Witness List for this ID
	 * @return consentWitnessList
	 * @throws BizLogicException 
	 * @throws NumberFormatException 
	 */
	private List getWitnessNameList(String csId) throws NumberFormatException, BizLogicException
	{
		logger.info("ConsentResponseDisplayAction.getWitnessNameList()");
		IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
		IBizLogic bizLogic = factory.getBizLogic(Constants.DEFAULT_BIZ_LOGIC);

		List csList = bizLogic.retrieve(ClinicalStudy.class.getName(), Constants.ID, Long
				.valueOf(csId));
		ClinicalStudy clinicalStudy = (ClinicalStudy) csList.get(0);
		//Setting the consent witness
		String witnessFullName = "";
		List csentWitList = new ArrayList();
		csentWitList.add(new NameValueBean(Constants.SELECT_OPTION, "-1"));
		Collection userCollection = null;
		if (clinicalStudy.getId() != null)
		{
			userCollection = (Collection) bizLogic.retrieveAttribute(ClinicalStudy.class.getName(),
					clinicalStudy.getId(), "elements(coordinatorCollection)");
		}

		Iterator iter = userCollection.iterator();
		while (iter.hasNext())
		{
			User user = (User) iter.next();
			witnessFullName = user.getLastName() + ", " + user.getFirstName();
			csentWitList.add(new NameValueBean(witnessFullName, user.getId()));
		}
		//Setting the PI
		User pripalInvestor = (User) bizLogic.retrieveAttribute(ClinicalStudy.class.getName(),
				clinicalStudy.getId(), "principalInvestigator");
		String piFullName = pripalInvestor.getLastName() + ", " + pripalInvestor.getFirstName();
		csentWitList.add(new NameValueBean(piFullName, pripalInvestor.getId()));
		return csentWitList;
	}
}