/**
 * <p>Title: InstitutionForm Class>
 * <p>Description:  This Class is used to encapsulate all the request parameters passed 
 * from Institute.jsp page. </p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 * Created on Mar 3, 2005
 */

package edu.wustl.clinportal.actionForm;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import edu.wustl.clinportal.domain.Institution;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.logger.Logger;

/**
 * This Class is used to encapsulate all the request parameters passed from Institute.jsp page.
 * @author falguni_sachde
 * */
public class InstitutionForm extends AbstractActionForm
{

	/**
	 * Specifies unique identifier.
	 * */
	private long id;

	/**
	 * A string containing the name of the institute.
	 */
	private String name = null;

	/**
	 * No argument constructor for InstitutionForm class 
	 */
	public InstitutionForm()
	{
		reset();
	}

	/**
	 * This function Copies the data from an institute object to a InstitutionForm object.
	 * @param abstractDomain An Institute object containing the information about the institute.  
	 */
	public void setAllValues(AbstractDomainObject abstractDomain)
	{
		Institution institute = (Institution) abstractDomain;
		this.id = institute.getId().longValue();
		this.name = institute.getName();
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
	 * Returns the login name of the institute.
	 * @return String representing the login name of the institute
	 * @see #setLoginName(String)
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * Sets the login name of this institute
	 * @param name login name of the institute.
	 * @see #getLoginName()
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return INSTITUTION_FORM_ID Returns the id assigned to form bean
	 */
	public int getFormId()
	{
		return Constants.INSTITUTION_FORM_ID;
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
	 * @param mapping Action mapping instance
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
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.required",
						ApplicationProperties.getValue("institution.name")));
			}
		}
		catch (Exception excp)
		{
			Logger.out.error(excp.getMessage(), excp);
		}
		return errors;
	}

	@Override
	public void setAddNewObjectIdentifier(String addNewFor, Long addObjectIdentifier)
	{
		// TODO Auto-generated method stub

	}

}