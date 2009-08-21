/**
 * <p>Title: ActionFormFactory Class>
 * <p>Description:	ActionFormFactory is a factory that returns instances of action formbeans 
 * as per the domain objects.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Aniruddha Phadnis
 * @version 1.00
 * Created on Dec 19, 2005
 */

package edu.wustl.clinportal.actionForm;

import java.lang.reflect.Method;
import java.util.List;

import edu.wustl.clinportal.domain.CancerResearchGroup;
import edu.wustl.clinportal.domain.Department;
import edu.wustl.clinportal.domain.Institution;
import edu.wustl.clinportal.domain.Participant;
import edu.wustl.clinportal.domain.Site;
import edu.wustl.clinportal.domain.User;
import edu.wustl.common.actionForm.AbstractActionForm;

/**
 * ActionFormFactory is a factory that returns instances of action form beans 
 * as per the domain objects.
 * @author falguni_sachde
 */
public class ActionFormFactory
{

	/**
	 * Returns the instance of form bean as per given domain object.
	 * @param object The instance of domain object
	 * @param operation The operation(Add/Edit) that is to be performed
	 * @return the instance of form bean.
	 * @see #setMessageList(List)
	 */
	public AbstractActionForm getFormBean(Object object, String operation) throws Exception
	{
		if (operation.equals(edu.wustl.common.util.global.Constants.LOGIN))
		{
			LoginForm loginForm = new LoginForm();
			edu.wustl.clinportal.domain.User user = (edu.wustl.clinportal.domain.User) object;
			loginForm.setLoginName(user.getLoginName());
			return loginForm;
		}
		else if (operation.equals(edu.wustl.common.util.global.Constants.LOGOUT))
		{
			return null;
		}

		if (object == null)
		{
			throw new Exception("Object should not be null while performing Add/Edit operation");
		}

		if (isIdNull(object))
		{
			throw new Exception("Id field of an object should not be null");
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
			throw new Exception("Invalid Object for Add/Edit Operation");
		}

		form.setOperation(operation);
		return form;
	}

	/**
	 * Checking value of Id.
	 * @param domainObject Domain  onject
	 * @return boolean value
	 * @throws Exception
	 */
	private static boolean isIdNull(Object domainObject) throws Exception
	{
		Method getIdMethod = domainObject.getClass().getMethod("getId", new Class[]{});
		Object object = getIdMethod.invoke(domainObject, new Object[]{});
		boolean returnFlag = false;
		if (object == null)
		{
			returnFlag = true;
		}
		return returnFlag;

	}
}