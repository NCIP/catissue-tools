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
 * <p>Title: CheckConsents Class>
 * <p>Description:	Ajax Action Class for Checking if consents available or not.</p>
 * Copyright:    Copyright (c) 2008 year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Rukhsana Sameer
 * @version 1.00
 * 
 */

package edu.wustl.clinportal.action;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.clinportal.bizlogic.ClinicalStudyBizLogic;
import edu.wustl.clinportal.domain.ClinicalStudy;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.action.BaseAction;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;

public class CheckConsents extends BaseAction
{

	/**
	* Overrides the execute method in Action class.
	* @param mapping ActionMapping object
	* @param form ActionForm object
	* @param request HttpServletRequest object
	* @param response HttpServletResponse object
	* @return ActionForward object
	* @throws Exception object
	*/
	@Override
	protected ActionForward executeAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		PrintWriter out = response.getWriter();
		String showConsents = request.getParameter(Constants.SHOW_CONSENTS);
		if (showConsents != null && showConsents.equalsIgnoreCase(Constants.YES))
		{
			// Checking consent for participant page in collection protocol registration

			String colProtocolId = request.getParameter(Constants.CP_SEARCH_CP_ID);
			if (colProtocolId != null)
			{
				setConsentTitle(colProtocolId, out);

			}

		}
		return null;
	}

	/**
	 * @param colProtocolId
	 * @param out
	 * @throws NumberFormatException
	 * @throws BizLogicException 
	 */
	private void setConsentTitle(String colProtocolId, PrintWriter out)
			throws NumberFormatException, BizLogicException
	{
		if (colProtocolId.equalsIgnoreCase("-1"))
		{
			out.print(Constants.NO_CONSENTS_DEFINED);//No Consents
		}
		else
		{
			IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
			ClinicalStudyBizLogic clinicalStudyBizLogic = (ClinicalStudyBizLogic)factory.getBizLogic(Constants.CLINICALSTUDY_FORM_ID);
			
			/*ClinicalStudyBizLogic cstudyBizlogic = (ClinicalStudyBizLogic) BizLogicFactory
					.getInstance().getBizLogic(Constants.CLINICALSTUDY_FORM_ID);*/
			String colName = "id";
			List csList = clinicalStudyBizLogic.retrieve(ClinicalStudy.class.getName(), colName, Long
					.valueOf(colProtocolId));
			ClinicalStudy collProtocol = (ClinicalStudy) csList.get(0);
			Collection csentTierCol = (Collection) clinicalStudyBizLogic.retrieveAttribute(
					ClinicalStudy.class.getName(), collProtocol.getId(),
					"elements(consentTierCollection)");

			if (csentTierCol.isEmpty())
			{
				//Writing to response
				out.print(Constants.NO_CONSENTS_DEFINED);//No Consents
			}
			else
			{
				out.print(Constants.PARTICIPANT_CONSENT_ENTER_RESPONSE);//Consent Response
			}
		}

	}
}
