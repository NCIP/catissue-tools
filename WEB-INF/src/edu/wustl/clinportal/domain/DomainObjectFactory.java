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
 * <p>Title: DomainObjectFactory Class>
 * <p>Description:	DomainObjectFactory is a factory for instances of various domain objects.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 */

package edu.wustl.clinportal.domain;

import java.lang.reflect.Method;

import edu.wustl.clinportal.actionForm.CancerResearchGroupForm;
import edu.wustl.clinportal.actionForm.DepartmentForm;
import edu.wustl.clinportal.actionForm.InstitutionForm;
import edu.wustl.clinportal.actionForm.LoginForm;
import edu.wustl.clinportal.actionForm.ParticipantForm;
import edu.wustl.clinportal.actionForm.ReportedProblemForm;
import edu.wustl.clinportal.actionForm.SiteForm;
import edu.wustl.clinportal.actionForm.UserForm;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.exception.AssignDataException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.factory.IDomainObjectFactory;
//import edu.wustl.common.factory.AbstractDomainObjectFactory;
import edu.wustl.common.util.logger.Logger;

/**
 * DomainObjectFactory is a factory for instances of various domain objects.
 * 
 */
public class DomainObjectFactory implements IDomainObjectFactory//extends AbstractDomainObjectFactory
{

	/**
	 * Returns the fully qualified name of the class according to the form bean type.
	 * @param FORM_TYPE Form bean Id.
	 * @return the fully qualified name of the class according to the form bean type.
	 */
	public String getDomainObjectName(int FORM_TYPE)
	{
		String className = null;
		switch (FORM_TYPE)
		{
			case Constants.PARTICIPANT_FORM_ID :
				className = Participant.class.getName();
				break;
			case Constants.INSTITUTION_FORM_ID :
				className = Institution.class.getName();
				break;
			case Constants.REPORTED_PROBLEM_FORM_ID :
				className = ReportedProblem.class.getName();
				break;
			case Constants.USER_FORM_ID :
				className = User.class.getName();
				break;
			case Constants.DEPARTMENT_FORM_ID :
				className = Department.class.getName();
				break;
			case Constants.APPROVE_USER_FORM_ID :
				className = User.class.getName();
				break;
			case Constants.SITE_FORM_ID :
				className = Site.class.getName();
				break;
			case Constants.CANCER_RESEARCH_GROUP_FORM_ID :
				className = CancerResearchGroup.class.getName();
				break;
			case Constants.NEW_SPECIMEN_FORM_ID :
			case Constants.CLINICALSTUDY_FORM_ID :
				className = ClinicalStudy.class.getName();
				break;
			case Constants.CLINICAL_STUDY_REGISTRATION_FORM_ID :
				className = ClinicalStudyRegistration.class.getName();
				break;

		}
		return className;
	}

	/**
	 * Returns an AbstractDomain object copy of the form bean object. 
	 * @param FORM_TYPE Form bean Id.
	 * @param form Form bean object.
	 * @return an AbstractDomain object copy of the form bean object.
	 */
	public AbstractDomainObject getDomainObject(int FORM_TYPE, AbstractActionForm form)
			throws AssignDataException
	{
		AbstractDomainObject abstractDomain = null;
		switch (FORM_TYPE)
		{
			case Constants.USER_FORM_ID :
			case Constants.APPROVE_USER_FORM_ID :
				UserForm userForm = (UserForm) form;
				abstractDomain = new User(userForm);
				break;
			case Constants.PARTICIPANT_FORM_ID :
				abstractDomain = new Participant(form);
				break;
			case Constants.SITE_FORM_ID :
				abstractDomain = new Site(form);
				break;
			case Constants.REPORTED_PROBLEM_FORM_ID :
				ReportedProblemForm reptedProbForm = (ReportedProblemForm) form;
				abstractDomain = new ReportedProblem(reptedProbForm);
				break;
			case Constants.DEPARTMENT_FORM_ID :
				abstractDomain = new Department(form);
				break;
			case Constants.INSTITUTION_FORM_ID :
				abstractDomain = new Institution(form);
				break;
			case Constants.CANCER_RESEARCH_GROUP_FORM_ID :
				abstractDomain = new CancerResearchGroup(form);
				break;
			case Constants.CLINICALSTUDY_FORM_ID :
				abstractDomain = new ClinicalStudy(form);
				break;
			case Constants.CLINICAL_STUDY_REGISTRATION_FORM_ID :
				abstractDomain = new ClinicalStudyRegistration(form);
				break;
			//added as per bug 79
			default :
				abstractDomain = null;
				Logger.out.error("Incompatible object ID");
				break;
		}
		return abstractDomain;
	}
	
	/**
	 * Returns the instance of AbstractActionForm child as per given domain object.
	 * @param object The instance of domain object
	 * @param operation The operation(Add/Edit) that is to be performed
	 * @return the instance of AbstractActionForm child.
	 * @see #setMessageList(List)
	 */
	public AbstractActionForm getFormBean(Object object, String operation)
			throws ApplicationException
	{
		if (operation.equals(Constants.LOGIN))
		{
			LoginForm loginForm = new LoginForm();
			edu.wustl.clinportal.domain.User user = (edu.wustl.clinportal.domain.User) object;
			loginForm.setLoginName(user.getLoginName());
			//		    loginForm.setPassword(user.getPassword());
			return loginForm;
		}
		else if (operation.equals(Constants.LOGOUT))
		{
			return null;
		}

		if (object == null)
		{
			throw new ApplicationException(ErrorKey.getErrorKey(""), null,
					"Object should not be null while performing Add/Edit operation");
		}

		if (isIdNull(object))
		{
			throw new ApplicationException(ErrorKey.getErrorKey(""), null,
					"Id field of an object should not be null");
		}

		AbstractActionForm form = null;

		if (object instanceof User)
		{
			form = new UserForm();
		}
		else if (object instanceof Institution)
		{
			form = new InstitutionForm();
		}
		else if (object instanceof Department)
		{
			form = new DepartmentForm();
		}
		else if (object instanceof CancerResearchGroup)
		{
			form = new CancerResearchGroupForm();
		}
		else if (object instanceof Site)
		{
			form = new SiteForm();
		}
		else if (object instanceof Participant)
		{
			form = new ParticipantForm();
		}
		else
		{
			throw new ApplicationException(ErrorKey.getErrorKey(""), null,"Invalid Object for Add/Edit Operation");
		}

		form.setOperation(operation);
		return form;
	}
	
	/**
	 * Checking value of Id.
	 * @param domainObject Domain  onject
	 * @return boolean value
	 * @throws ApplicationException Application Exception.
	 * @throws Exception
	 */
	private static boolean isIdNull(Object domainObject) throws ApplicationException
	{
		boolean isNull = false;
		try
		{
			Method getIdMethod = domainObject.getClass().getMethod("getId", new Class[]{});
			Object object = getIdMethod.invoke(domainObject, new Object[]{});
			if (object == null)
			{
				isNull = true;
			}
			return isNull;
		}
		catch (Exception exception)
		{
			throw new ApplicationException(ErrorKey.getErrorKey(""), exception,"");
		}
		
	}
}