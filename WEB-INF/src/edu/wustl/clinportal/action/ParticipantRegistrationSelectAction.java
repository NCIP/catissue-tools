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
 * <p>Title: ParticipantRegistrationSelectAction Class>
 * <p>Description:	This Class is used when participant is selected from the list.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 */

package edu.wustl.clinportal.action;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.clinportal.actionForm.ParticipantForm;
import edu.wustl.clinportal.bean.ConsentResponseBean;
import edu.wustl.clinportal.domain.Participant;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.action.CommonAddEditAction;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.bizlogic.IBizLogic;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IDomainObjectFactory;
import edu.wustl.common.factory.IFactory;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.logger.Logger;

/**
 * @author falguni_sachde
 *
 */
public class ParticipantRegistrationSelectAction extends CommonAddEditAction
{

	/* (non-Javadoc)
	 * @see edu.wustl.common.action.CommonAddEditAction#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	{
		ActionForward forward = null;
		try
		{
			AbstractDomainObject abstractDomain = null;

			ParticipantForm participantForm = (ParticipantForm) form;
			IDomainObjectFactory domainObjectFactory = AbstractFactoryConfig.getInstance()
					.getDomainObjectFactory();
			IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
			IBizLogic bizLogic = factory.getBizLogic(participantForm.getFormId());
			String objectName = domainObjectFactory
					.getDomainObjectName(participantForm.getFormId());

			Logger.out.info("Participant Id-------------------"
					+ request.getParameter("participantId"));

			List participants = bizLogic.retrieve(objectName, Constants.SYSTEM_IDENTIFIER, Long
					.valueOf(request.getParameter("participantId")));
			abstractDomain = (AbstractDomainObject) participants.get(0);
			Participant participant = (Participant) abstractDomain;

			Logger.out.info("Last name in ParticipantSelectAction:" + participant.getLastName());

			// To append the cpr to already existing cprs
			//Gets the collection Protocol Registration map from ActionForm
			Map mapCollProtoReg = participantForm.getCollectionProtocolRegistrationValues();
			int cprCount = participantForm.getCollectionProtocolRegistrationValueCounter();
			Collection constRespBeanColl = participantForm.getConsentResponseBeanCollection();
			Hashtable consentRespTbl = participantForm.getConsentResponseHashTable();
			Map mapPcpantMedId = participantMedicalIdentifierMap(participantForm.getValues());

			//Gets the collection Protocol Registration map from Database
			DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
			defaultBizLogic.populateUIBean(Participant.class.getName(), participant.getId(),
					participantForm);

			Map mapCPRegOld = participantForm.getCollectionProtocolRegistrationValues();
			int cprCountOld = participantForm.getCollectionProtocolRegistrationValueCounter();
			Collection cnstResBenColOld = participantForm.getConsentResponseBeanCollection();
			Hashtable consentRespHTOld = participantForm.getConsentResponseHashTable();

			Map mapCPRAppended = appendClinicalStudyRegistrations(mapCollProtoReg, cprCount,
					mapCPRegOld, cprCountOld);
			Map mapPpantMedIdOld = participantMedicalIdentifierMap(participantForm.getValues());

			if (constRespBeanColl != null)
			{
				updateConsentResponse(constRespBeanColl, cnstResBenColOld, consentRespHTOld);
			}

			participantForm.setCollectionProtocolRegistrationValues(mapCPRAppended);
			participantForm.setCollectionProtocolRegistrationValueCounter((cprCountOld + cprCount));
			participantForm.setValues(mapPpantMedIdOld);
			participantForm.setConsentResponseBeanCollection(cnstResBenColOld);
			participantForm.setConsentResponseHashTable(consentRespHTOld);

			forward = super.execute(mapping, participantForm, request, response);

			if (!forward.getName().equals("failure"))
			{
				request.removeAttribute("participantForm");
				request.setAttribute("participantForm1", participantForm);
				request.setAttribute("participantSelect", "yes");
			}
			else
			{
				participantForm.setCollectionProtocolRegistrationValues(mapCollProtoReg);
				participantForm.setCollectionProtocolRegistrationValueCounter(cprCount);
				participantForm.setValues(mapPcpantMedId);
				participantForm.setConsentResponseBeanCollection(constRespBeanColl);
				participantForm.setConsentResponseHashTable(consentRespTbl);
				request.setAttribute("continueLookup", "yes");
			}
		}
		catch (Exception e)
		{
			Logger.out.info(e.getMessage());
		}

		return forward;
	}

	/*
	 * This method is for updating consent response 
	 */
	/**
	 * @param csentResBeanColl
	 * @param csentRespBnColOld
	 * @param csentRespHTOld
	 */
	private void updateConsentResponse(Collection csentResBeanColl, Collection csentRespBnColOld,
			Hashtable csentRespHTOld)
	{
		Iterator iterator = csentResBeanColl.iterator();
		while (iterator.hasNext())
		{
			ConsentResponseBean csentResBean = (ConsentResponseBean) iterator.next();
			long cProtocolId = csentResBean.getCollectionProtocolID();
			if (cProtocolId > 0)
			{
				if (!isAlreadyExist(csentRespBnColOld, cProtocolId))
				{
					csentRespBnColOld.add(csentResBean);
					String key = Constants.CONSENT_RESPONSE_KEY + cProtocolId;
					csentRespHTOld.put(key, csentResBean);
				}
			}
		}
	}

	/*
	 * Checking that given collection protocol is already exist in consentResponseBeanCollection
	 */
	/**
	 * @param csentResBeanColl
	 * @param CProtocolId
	 * @return
	 */
	private boolean isAlreadyExist(Collection csentResBeanColl, long CProtocolId)
	{
		boolean flag = false;
		Iterator iterator = csentResBeanColl.iterator();
		while (iterator.hasNext())
		{
			ConsentResponseBean csentResBean = (ConsentResponseBean) iterator.next();
			long cpId = csentResBean.getCollectionProtocolID();
			if (cpId == CProtocolId)
			{
				flag = true;
				break;
			}
		}
		return flag;
	}

	/*
	 * This method will remove invalid entries from the Map

	 */
	/**
	 * @param pMIdentifier
	 * @return
	 */
	private Map participantMedicalIdentifierMap(Map pMIdentifier)
	{
		Validator validator = new Validator();
		String className = "ParticipantMedicalIdentifier:";
		String key1 = "_Site_" + Constants.SYSTEM_IDENTIFIER;
		String key2 = "_medicalRecordNumber";
		String key3 = "_" + Constants.SYSTEM_IDENTIFIER;
		int index = 1;

		while (true)
		{
			String keyOne = className + index + key1;
			String keyTwo = className + index + key2;
			String keyThree = className + index + key3;

			String value1 = (String) pMIdentifier.get(keyOne);
			String value2 = (String) pMIdentifier.get(keyTwo);

			if (value1 == null || value2 == null)
			{
				break;
			}
			else if (!validator.isValidOption(value1) && value2.trim().equals(""))
			{
				pMIdentifier.remove(keyOne);
				pMIdentifier.remove(keyTwo);
				pMIdentifier.remove(keyThree);
			}
			index++;
		}
		return pMIdentifier;
	}

	/**
	 *  This method is for appending clinical study registration for given participant.
	 * @param mapCPRegistration
	 * @param cprCount
	 * @param mapCPROld
	 * @param cprCountOld
	 * @return
	 * @throws Exception
	 */
	private Map appendClinicalStudyRegistrations(Map mapCPRegistration, int cprCount,
			Map mapCPROld, int cprCountOld)
	{
		int cprCountNew = cprCount + cprCountOld;
		String csRegistration = "ClinicalStudyRegistration:";

		for (int i = cprCountOld + 1; i <= cprCountNew; i++)
		{
			String colProtocolId = csRegistration + (i - cprCountOld) + "_ClinicalStudy_id";
			String colProtocolTitle = csRegistration + (i - cprCountOld)
					+ "_ClinicalStudy_shortTitle";
			String colProtocolPartId = csRegistration + (i - cprCountOld)
					+ "_clinicalStudyParticipantIdentifier";
			String colProtoRegDate = csRegistration + (i - cprCountOld) + "_registrationDate";
			String collProtoIdfier = csRegistration + (i - cprCountOld) + "_id";

			String isActive = csRegistration + (i - cprCountOld) + "_activityStatus";

			String collProtoIdNew = csRegistration + i + "_ClinicalStudy_id";
			String colProtoTitleNew = csRegistration + i + "_ClinicalStudy_shortTitle";
			String colProtoPartIdNew = csRegistration + i + "_clinicalStudyParticipantIdentifier";
			String colProtRegDateNew = csRegistration + i + "_registrationDate";
			String colProtIdNew = csRegistration + i + "_id";

			String isActiveNew = csRegistration + i + "_activityStatus";

			mapCPROld.put(collProtoIdNew, mapCPRegistration.get(colProtocolId));
			mapCPROld.put(colProtoTitleNew, mapCPRegistration.get(colProtocolTitle));
			mapCPROld.put(colProtoPartIdNew, mapCPRegistration.get(colProtocolPartId));
			mapCPROld.put(colProtRegDateNew, mapCPRegistration.get(colProtoRegDate));
			mapCPROld.put(colProtIdNew, mapCPRegistration.get(collProtoIdfier));

			String status = Constants.ACTIVITY_STATUS_ACTIVE;
			if (mapCPRegistration.get(isActive) != null)
			{
				status = (String) mapCPRegistration.get(isActive);
			}

			mapCPROld.put(isActiveNew, status);
		}

		participantClinicalStudyRegistration(mapCPROld);

		return mapCPROld;
	}

	/*
	 * This method will remove invalid entries from the Map
	 *
	 */
	/**
	 * @param colProtoRegValues
	 * @return
	 */
	private void participantClinicalStudyRegistration(Map colProtoRegValues)
	{
		Validator validator = new Validator();
		String colProtoClassName = "ClinicalStudyRegistration:";
		String colProtocolId = "_ClinicalStudy_id";
		String colProtoPartId = "_clinicalStudyParticipantIdentifier";
		String colProtoRegDate = "_registrationDate";
		String colProtoId = "_id";
		String isActive = "_activityStatus";
		String colProtoTitle = "_ClinicalStudy_shortTitle";
		int index = 1;
		while (true)
		{
			String keyOne = colProtoClassName + index + colProtocolId;
			String keyTwo = colProtoClassName + index + colProtoPartId;
			String keyThree = colProtoClassName + index + colProtoRegDate;
			String keyFour = colProtoClassName + index + colProtoId;
			String keySix = colProtoClassName + index + isActive;
			String KeySeven = colProtoClassName + index + colProtoTitle;
			String value1 = (String) colProtoRegValues.get(keyOne);
			String value2 = (String) colProtoRegValues.get(keyTwo);
			String value3 = (String) colProtoRegValues.get(keyThree);

			if (value1 == null || value2 == null || value3 == null)
			{
				break;
			}
			else if (!validator.isValidOption(value1))
			{
				colProtoRegValues.remove(keyOne);
				colProtoRegValues.remove(keyTwo);
				colProtoRegValues.remove(keyThree);
				colProtoRegValues.remove(keyFour);
				colProtoRegValues.remove(keySix);
				colProtoRegValues.remove(KeySeven);
			}
			index++;
		}
	}

}
