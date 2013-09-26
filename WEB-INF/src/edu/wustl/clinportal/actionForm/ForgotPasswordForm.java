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
 * <p>Title: ForgotPasswordForm Class>
 * <p>Description:	ForgotPasswordForm Class is used to encapsulate all the request parameters passed 
 * from ForgotPassword webpage.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 */

package edu.wustl.clinportal.actionForm;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.Validator;

/**
 * ForgotPasswordForm Class is used to encapsulate all the request parameters passed 
 * from ForgotPassword web page.
 * @author deepali
 */
public class ForgotPasswordForm extends AbstractActionForm
{

	/**
	 * EmailAddress of the user whose password is to be searched.
	 */
	private String emailAddress;

	/**
	 * Returns the emailAddress of the user whose password is to be searched.
	 * @return the emailAddress of the user whose password is to be searched.
	 */
	public String getEmailAddress()
	{
		return emailAddress;
	}

	/**
	 * Sets the emailAddress of the user whose password is to be searched.
	 * @param emailAddress the emailAddress of the user whose password is to be searched.
	 */
	public void setEmailAddress(String emailAddress)
	{
		this.emailAddress = emailAddress;
	}

	/**
	 * @return FORGOT_PASSWORD_FORM_ID The form id of ForgotPasswordForm.
	 */
	public int getFormId()
	{
		return Constants.FORGOT_PASSWORD_FORM_ID;
	}

	/**
	 * Populates all the fields from the domain object to the form bean.
	 * @param abstractDomain An AbstractDomain Object  
	 */
	public void setAllValues(AbstractDomainObject abstractDomain)
	{

	}

	/**
	 * Overrides the validate method of ActionForm.
	 * @return error ActionErrors instance
	 * @param arg0 Action mapping instance
	 * @param arg1 HttpServletRequest instance
	 */
	public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	{
		ActionErrors errors = new ActionErrors();
		Validator validator = new Validator();

		if (validator.isEmpty(emailAddress))
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.required",
					ApplicationProperties.getValue("user.emailAddress")));
		}
		else
		{
			if (!validator.isValidEmailAddress(emailAddress))
			{
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.format",
						ApplicationProperties.getValue("user.emailAddress")));
			}
		}

		return errors;
	}

	/**
	 * Overrides the method in the AbstractActionForm.
	 */
	protected void reset()
	{

	}

	@Override
	public void setAddNewObjectIdentifier(String addNewFor, Long addObjectIdentifier)
	{
		// TODO Auto-generated method stub

	}
}