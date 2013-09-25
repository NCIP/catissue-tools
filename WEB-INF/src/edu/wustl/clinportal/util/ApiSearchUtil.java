/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */


package edu.wustl.clinportal.util;

import java.util.Date;

import edu.wustl.clinportal.domain.Address;
import edu.wustl.clinportal.domain.CancerResearchGroup;
import edu.wustl.clinportal.domain.ClinicalStudy;
import edu.wustl.clinportal.domain.ClinicalStudyRegistration;
import edu.wustl.clinportal.domain.Department;
import edu.wustl.clinportal.domain.Institution;
import edu.wustl.clinportal.domain.ParticipantMedicalIdentifier;
import edu.wustl.clinportal.domain.Password;
import edu.wustl.clinportal.domain.ReportedProblem;
import edu.wustl.clinportal.domain.Site;
import edu.wustl.clinportal.domain.User;
import edu.wustl.clinportal.util.global.Constants;

/**
 * This is the utility class for API Search.
 * This class set the default values for various domain object
 */
public class ApiSearchUtil
{

	/**
	 * @param user
	 */
	public static void setUserDefault(User user)
	{
		if (SearchUtil.isNullobject(user.getLastName()))
		{
			user.setLastName("");
		}

		if (SearchUtil.isNullobject(user.getFirstName()))
		{
			user.setFirstName("");
		}

		if (SearchUtil.isNullobject(user.getLoginName()))
		{
			user.setLoginName("");
		}

		if (SearchUtil.isNullobject(user.getEmailAddress()))
		{
			user.setEmailAddress("");
		}

		if (SearchUtil.isNullobject(user.getAddress()))
		{
			user.setAddress(new Address());
		}

		if (SearchUtil.isNullobject(user.getInstitution()))
		{
			user.setInstitution(new Institution());
		}

		if (SearchUtil.isNullobject(user.getDepartment()))
		{
			user.setDepartment(new Department());
		}

		if (SearchUtil.isNullobject(user.getCancerResearchGroup()))
		{
			user.setCancerResearchGroup(new CancerResearchGroup());
		}

		if (SearchUtil.isNullobject(user.getFirstTimeLogin()))
		{
			user.setFirstTimeLogin(Boolean.TRUE);
		}

		if (SearchUtil.isNullobject(user.getActivityStatus()))
		{
			user.setActivityStatus(Constants.ACTIVITY_STATUS_NEW);
		}
	}

	/**
	 * @param site
	 */
	public static void setSiteDefault(Site site)
	{
		if (SearchUtil.isNullobject(site.getCoordinator()))
		{
			site.setCoordinator(new User());
		}
		if (SearchUtil.isNullobject(site.getAddress()))
		{
			site.setAddress(new Address());
		}
	}

	/**
	 * @param reportedProblem
	 */
	public static void setReportedProblemDefault(ReportedProblem reportedProblem)
	{
		if (SearchUtil.isNullobject(reportedProblem.getReportedDate()))
		{
			reportedProblem.setReportedDate(new Date());
		}
	}

	/**
	 * @param password
	 */
	public static void setPasswordDefault(Password password)
	{
		if (SearchUtil.isNullobject(password.getUser()))
		{
			password.setUser(new User());
		}
	}

	/**
	 * @param partMedIdentifier
	 */
	public static void setParticipantMedicalIdentifierDefault(
			ParticipantMedicalIdentifier partMedIdentifier)
	{
		if (SearchUtil.isNullobject(partMedIdentifier.getSite()))
		{
			partMedIdentifier.setSite(new Site());
		}
	}

	/**
	 * To set the default values for the Clinical Study
	 * 
	 * @param clStudyRegtion
	 */
	public static void setClinicalStudyRegistrationDefault(ClinicalStudyRegistration clStudyRegtion)
	{
		if (SearchUtil.isNullobject(clStudyRegtion.getClinicalStudy()))
		{
			clStudyRegtion.setClinicalStudy(new ClinicalStudy());
		}

		if (SearchUtil.isNullobject(clStudyRegtion.getRegistrationDate()))
		{
			clStudyRegtion.setRegistrationDate(new Date());
		}
	}

}
