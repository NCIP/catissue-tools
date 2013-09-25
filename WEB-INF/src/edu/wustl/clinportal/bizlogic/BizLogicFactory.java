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
 * <p>Title: BizLogicFactory Class>
 * <p>Description:	BizLogicFactory is a factory for DAO instances of various domain objects.</p>
 * 
 */

package edu.wustl.clinportal.bizlogic;

import edu.wustl.clinportal.bizlogic.querysuite.CatissuecoreQueryBizLogic;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.bizlogic.CDEBizLogic;
import edu.wustl.common.bizlogic.IBizLogic;
import edu.wustl.common.factory.IFactory;
import edu.wustl.simplequery.bizlogic.QueryBizLogic;
import edu.wustl.common.util.logger.Logger;

/**
 * BizLogicFactory is a factory for DAO instances of various domain objects.
 * @author falguni_sachde
 */
public class BizLogicFactory implements IFactory
{

	/**
	 * Returns DAO instance according to the form bean type.
	 * @param FORM_ID The form bean type.
	 * @return An AbstractDAO object.
	 */
	public IBizLogic getBizLogic(int FORM_ID)
	{
		Logger.out.debug("In Biz Logic Factory , Form ID: " + FORM_ID);

		IBizLogic bizLogic = null;

		switch (FORM_ID)
		{
			case Constants.FORGOT_PASSWORD_FORM_ID :
			case Constants.USER_FORM_ID :
				bizLogic = new UserBizLogic();
				break;
			case Constants.APPROVE_USER_FORM_ID :
				bizLogic = new ApproveUserBizLogic();
				break;
			case Constants.REPORTED_PROBLEM_FORM_ID :
				bizLogic = new ReportedProblemBizLogic();
				break;
			case Constants.SITE_FORM_ID :
				bizLogic = new SiteBizLogic();
				break;
			case Constants.PARTICIPANT_FORM_ID :
				bizLogic = new ParticipantBizLogic();
				break;

			// for all event parameters same object will be returned	
			case Constants.FROZEN_EVENT_PARAMETERS_FORM_ID :
			case Constants.CHECKIN_CHECKOUT_EVENT_PARAMETERS_FORM_ID :
			case Constants.FLUID_SPECIMEN_REVIEW_EVENT_PARAMETERS_FORM_ID :
			case Constants.CELL_SPECIMEN_REVIEW_PARAMETERS_FORM_ID :
			case Constants.TISSUE_SPECIMEN_REVIEW_EVENT_PARAMETERS_FORM_ID :
			case Constants.MOLECULAR_SPECIMEN_REVIEW_PARAMETERS_FORM_ID :
			case Constants.RECEIVED_EVENT_PARAMETERS_FORM_ID :
			case Constants.COLLECTION_EVENT_PARAMETERS_FORM_ID :
			case Constants.TRANSFER_EVENT_PARAMETERS_FORM_ID :
			case Constants.THAW_EVENT_PARAMETERS_FORM_ID :
			case Constants.DISPOSAL_EVENT_PARAMETERS_FORM_ID :
			case Constants.SPUN_EVENT_PARAMETERS_FORM_ID :
			case Constants.EMBEDDED_EVENT_PARAMETERS_FORM_ID :
			case Constants.FIXED_EVENT_PARAMETERS_FORM_ID :

			case edu.wustl.simplequery.global.Constants.SIMPLE_QUERY_INTERFACE_ID :
				bizLogic = new QueryBizLogic();
				break;
			case edu.wustl.common.util.global.Constants.QUERY_INTERFACE_ID :
				bizLogic = new QueryBizLogic();
				break;

			case Constants.ASSIGN_PRIVILEGE_FORM_ID :
				bizLogic = new AssignPrivilegePageBizLogic();
				break;
			case Constants.INSTITUTION_FORM_ID :
				bizLogic = new InstitutionBizLogic();
				break;
			case Constants.DEPARTMENT_FORM_ID :
				bizLogic = new DepartmentBizLogic();
				break;
			case Constants.CANCER_RESEARCH_GROUP_FORM_ID :
				bizLogic = new CancerResearchBizLogic();
				break;
			case Constants.CDE_FORM_ID :
				bizLogic = new CDEBizLogic();
				break;
			case Constants.CATISSUECORE_QUERY_INTERFACE_ID :
				bizLogic = new CatissuecoreQueryBizLogic();
				break;

			case Constants.CLINICALSTUDY_FORM_ID :
				bizLogic = new ClinicalStudyBizLogic();
				break;
			case Constants.CLINICAL_STUDY_REGISTRATION_FORM_ID :
				bizLogic = new ClinicalStudyRegistrationBizLogic();
				break;
			case Constants.EVENT_ENTRY_FORM_ID :
				bizLogic = new EventEntryBizlogic();
				break;

			case Constants.DEFAULT_BIZ_LOGIC :
			default :
				bizLogic = new ClinportalDefaultBizLogic();
				break;

		}
		return bizLogic;
	}

	/**
	 * Returns DAO instance according to the fully qualified class name.
	 * @param className The name of the class.
	 * @return An AbstractDAO object.
	 */
	public IBizLogic getBizLogic(String className)
	{
		IBizLogic bizLogic = null;
		if (className.equals("edu.wustl.clinportal.domain.User"))
		{
			bizLogic = new UserBizLogic();
		}
		else if (className.equals("edu.wustl.clinportal.domain.Site"))
		{
			bizLogic = new SiteBizLogic();
		}
		else if (className.equals("edu.wustl.clinportal.domain.Participant"))
		{
			bizLogic = new ParticipantBizLogic();
		}
		else if (className.equals("edu.wustl.clinportal.domain.Department"))
		{
			bizLogic = new DepartmentBizLogic();
		}
		else if (className.equals("edu.wustl.clinportal.domain.CancerResearchGroup"))
		{
			bizLogic = new CancerResearchBizLogic();
		}
		else if (className.equals("edu.wustl.clinportal.domain.Institution"))
		{
			bizLogic = new InstitutionBizLogic();
		}
		else if (className.equals("edu.wustl.clinportal.domain.ParticipantMedicalIdentifier"))
		{
			bizLogic = new ParticipantMedicalIdentifierBizLogic();
		}
		else if (className.equals("edu.wustl.clinportal.domain.EventEntry"))
		{
			bizLogic = new EventEntryBizlogic();
		}
		else if (className.equals("edu.wustl.clinportal.domain.ClinicalStudy"))
		{
			bizLogic = new ClinicalStudyBizLogic();
		}
		else if (className.equals("edu.wustl.clinportal.domain.ClinicalStudyRegistration"))
		{
			bizLogic = new ClinicalStudyRegistrationBizLogic();
		}
		else
		{
			bizLogic = new ClinportalDefaultBizLogic();
		}
		return bizLogic;
	}

}