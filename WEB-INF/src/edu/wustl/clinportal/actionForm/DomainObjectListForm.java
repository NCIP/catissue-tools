/**
 * <p>Title: ApproveUserForm Class>
 * <p>Description:  ApproveUserForm Class is used to encapsulate all the request parameters passed 
 * from User Approve/Reject webpage. </p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 * Created on Apr 26, 2005
 */

package edu.wustl.clinportal.actionForm;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.util.logger.Logger;

/**
 * ApproveUserForm Class is used to encapsulate all the request parameters passed 
 * from User Approve/Reject web page.
 * @author rukhsana_sameer
 */
public class DomainObjectListForm extends AbstractActionForm
{

	/**
	 * A String containing the operation(Add/Edit) to be performed.
	 * */
	private String operation;

	/**
	 * Map of users whose registration is to be approved/rejected.
	 */
	protected Map values = new HashMap();

	/**
	 * Comments given by the approver.
	 */
	protected String comments;

	/**
	 * Associates the specified object with the specified key in the map.
	 * @param key the key to which the object is mapped.
	 * @param value the object which is mapped.
	 */
	public void setValue(String key, Object value)
	{
		if (!"on".equals(value))
		{
			values.put(key, value);
		}

	}

	/**
	 * Returns the operation(Add/Edit) to be performed.
	 * @return Returns the operation.
	 * @see #setOperation(String)
	 */
	public String getOperation()
	{
		return operation;
	}

	/**
	 * Sets the operation to be performed.
	 * @param operation The operation to set.
	 * @see #getOperation()
	 */
	public void setOperation(String operation)
	{
		this.operation = operation;
	}

	/**
	 * Returns the object to which this map maps the specified key.
	 * @param key the required key.
	 * @return the object to which this map maps the specified key.
	 */
	public Object getValue(String key)
	{
		return values.get(key);
	}

	/**
	 * Returns the comments given by the approver.
	 * @return the comments given by the approver.
	 * @see #setComments(String)
	 */
	public String getComments()
	{
		return comments;
	}

	/**
	 * Sets the comments given by the approver.
	 * @param comments the comments given by the approver.
	 * @see #getComments() 
	 */
	public void setComments(String comments)
	{
		this.comments = comments;
	}

	/**
	 * Initializes an empty ApproveUserForm instance.
	 */
	public DomainObjectListForm()
	{
		reset();
	}

	/**
	 * Resets all the values.
	 */
	protected void reset()
	{
		comments = null;
		values = new HashMap();
	}

	/**
	 * Returns all values in the map.
	 * @return collection of values in the map. 
	 */
	public Collection getAllValues()
	{
		return values.values();
	}

	/**
	 * @return APPROVE_USER_FORM_ID
	 * @see edu.wustl.clinportal.actionForm.AbstractForm#getFormId()
	 */
	public int getFormId()
	{
		return Constants.APPROVE_USER_FORM_ID;
	}

	/**
	 * @param abstractDomain An AbstractDomainObject obj
	 * @see edu.wustl.clinportal.actionForm.AbstractForm#setAllValues(edu.wustl.clinportal.domain.AbstractDomain)
	 */
	public void setAllValues(AbstractDomainObject abstractDomain)
	{
		//overridden method
	}

	/**
	 * Overrides the validate method of ActionForm.
	 * @param mapping Actionmapping instance
	 * @param request HttpServletRequest instance
	 * @return error ActionErrors instance
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request)
	{
		ActionErrors errors = new ActionErrors();

		try
		{
			if (values.isEmpty() && (!operation.equals(Constants.USER_DETAILS)))
			{
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
						"errors.approveUser.required", operation));
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
