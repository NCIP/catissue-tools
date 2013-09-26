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
 * <p>Title: DepartmentForm Class</p>
 * <p>Description:  DepartmentForm Class is used to handle transactions related to Department   
 * from Department Add/Edit webpage. </p>
 * Copyright:    Copyright (c) 2005
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Mandar Deshmukh
 * @version 1.00
 * Created on May 23rd, 2005
 */

package edu.wustl.clinportal.actionForm;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import edu.wustl.clinportal.domain.Department;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.logger.Logger;

/**
 * DepartmentForm Class is used to encapsulate all the request parameters passed 
 * from Department Add/Edit webpage.
 * @author Falguni Sachde
 * */
public class DepartmentForm extends AbstractActionForm
{
	/**
	 * Specifies unique identifier.
	 * */
	private long id;
	
	/**
	 * Name of the Department.
	 */
	private String name;

	/**
	 * No argument constructor for UserForm class 
	 */
	public DepartmentForm()
	{
		reset();
	}

	/**
	 * Copies the data from an AbstractDomain object to a DepartmentForm object.
	 * @param abstractDomain An AbstractDomain object.  
	 */
	public void setAllValues(AbstractDomainObject abstractDomain)
	{
		Department department = (Department) abstractDomain;
		this.id = department.getId().longValue();
		this.name = department.getName();
	}

	/**
	 * Returns unique identifier.
	 * @return unique identifier.
	 * @see #setId(long)
	 * */
	public long getId()
	{
		return id;
	}

	/**
	 * Sets unique identifier.
	 * @param identifier unique identifier.
	 * @see #getId()
	 * */
	public void setId(long identifier)
	{
		if (isMutable())
		{
			this.id = identifier;
		}
	}
	
	/**
	 * Returns the name of the Department.
	 * @return String representing the name of the Department
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * Sets the name of this Department
	 * @param name Name of the Department.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return DEPARTMENT_FORM_ID Returns the id assigned to form bean
	 */
	public int getFormId()
	{
		return Constants.DEPARTMENT_FORM_ID;
	}

	/**
	 * Resets the values of all the fields.
	 * Is called by the overridden reset method defined in ActionForm.  
	 * */
	protected void reset()
	{
		this.name = null;
	}

	/**
	 * Overrides the validate method of ActionForm.
	 * @return error ActionErrors instance
	 * @param mapping Actionmapping instance
	 * @param request HttpServletRequest instance
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request)
	{
		ActionErrors errors = new ActionErrors();
		Validator validator = new Validator();
		try
		{
			if (validator.isEmpty(name))
			{
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.required", ApplicationProperties.getValue("department.name")));
			}
		}
		catch (Exception excp)
		{
			Logger.out.error(excp.getMessage(), excp);
		}
		return errors;
	}

	@Override
	public void setAddNewObjectIdentifier(String addNewFor, Long addObjectIdentifier) {
		// TODO Auto-generated method stub
		
	}

}