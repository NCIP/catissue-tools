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
 * <p>Title: ConsentResponseSubmitAction Class>
 * <p>Description: ConsentResponseSubmitAction class is for creating session object of consent response for selected collection protocol registration </p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Abhishek Mehta
 * @version 1.00
 * Created on Sept 5, 2007
 */

package edu.wustl.clinportal.action;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.clinportal.actionForm.ConsentResponseForm;
import edu.wustl.clinportal.bean.ConsentResponseBean;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.action.BaseAction;
import edu.wustl.common.util.MapDataParser;

public class ConsentResponseSubmitAction extends BaseAction
{

	protected ActionForward executeAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception
	{

		ConsentResponseForm consentForm = (ConsentResponseForm) form;
		HttpSession session = request.getSession();
		if (consentForm != null)
		{
			long clinicalStudyID = consentForm.getCollectionProtocolID();
			String signedConsentUrl = consentForm.getSignedConsentUrl();
			long witnessId = consentForm.getWitnessId();
			String csentSigDate = consentForm.getConsentDate();
			Map csentRespVals = consentForm.getConsentResponseValues();
			MapDataParser mapdataParser = new MapDataParser("edu.wustl.clinportal.bean");
			Collection csentResCol = mapdataParser.generateData(csentRespVals);
			String withButStatus = consentForm.getWithdrawlButtonStatus();
			ConsentResponseBean csentResBean = new ConsentResponseBean(clinicalStudyID, signedConsentUrl, witnessId, csentSigDate, csentResCol,
					withButStatus);
			String csentResKey = Constants.CONSENT_RESPONSE_KEY + clinicalStudyID;
			Hashtable csentResHtable = (Hashtable) session.getAttribute(Constants.CONSENT_RESPONSE);
			if (csentResHtable == null)
			{
				csentResHtable = new Hashtable();
			}
			if (csentResHtable.containsKey(csentResKey))
			{
				csentResHtable.remove(csentResKey);
			}
			csentResHtable.put(csentResKey, csentResBean);
			session.setAttribute(Constants.CONSENT_RESPONSE, csentResHtable);
		}
		return null;
	}
}
