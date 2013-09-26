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
 * <p>Title: CancerResearchGroupForm Class</p>
 * <p>Description:  CancerResearchGroupForm Class is used to handle transactions related to CancerResearchGroup   
 * from CancerResearchGroup Add/Edit webpage. </p>
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

import edu.wustl.clinportal.domain.CancerResearchGroup;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.logger.Logger;

/**
 * CancerResearchGroupForm Class is used to encapsulate all the request parameters passed 
 * from CancerResearchGroup Add/Edit web page.
 * @author Falguni Sachde
 * */
public class CancerResearchGroupForm extends AbstractActionForm
{
	/**
	 * Specifies unique identifier.
	 * */
	private long id;

	/**
	 * Name of the CancerResearchGroup.
	 */
	private String name;

	/**
	 * No argument constructor for UserForm class 
	 */
	public CancerResearchGroupForm()
	{
		reset();
	}

	/**
	 * Copies the data from an AbstractDomain object to a CancerResearchGroupForm object.
	 * @param abstractDomain An AbstractDomain object.  
	 */
	public void setAllValues(AbstractDomainObject abstractDomain)
	{
		CancerResearchGroup crGroup = (CancerResearchGroup) abstractDomain;
		this.id = crGroup.getId().longValue();
		this.name = crGroup.getName();
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
	 * Returns the name of the CancerResearchGroup.
	 * @return String representing the name of the CancerResearchGroup
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * Sets the name of this CancerResearchGroup
	 * @param name Name of the CancerResearchGroup.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Checks the operation to be performed is add operation.
	 * @return Returns true if operation is equal to "add", else it returns false
	 * */
	public boolean isAddOperation()
	{
		return getOperation().equals(Constants.ADD);
	}

	/**
	 * Returns the id assigned to form bean
	 * @return CANCER_RESEARCH_GROUP_FORM_ID
	 */
	public int getFormId()
	{
		return Constants.CANCER_RESEARCH_GROUP_FORM_ID;
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
						ApplicationProperties.getValue("cancerResearchGroup.name")));
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