/**
 * <p>Title: AssignPrivilegesForm Class>
 * <p>Description:  This Class is used to encapsulate all the request parameters passed 
 * from AssignPrivileges.jsp page. </p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Aniruddha Phadnis
 * @version 1.00
 * Created on Jul 15, 2005
 */

package edu.wustl.clinportal.actionForm;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.util.global.ApplicationProperties;

/**
 * This Class is used to encapsulate all the request parameters passed from AssignPrivileges.jsp page.
 * @author falguni_sachde
 * */
public class AssignPrivilegesForm extends ActionForm
{

	/**
	 * A String containing the assignment operation(Allow/Disallow) to be performed.
	 * */
	private String assignOperation;

	/**
	 * A String array containing the list of privileges.
	 * */
	private String privilege = "READ";

	/**
	 * A String array containing the list of object-types.
	 * */
	private String objectType;

	/**
	 * A String array containing the list of record identifiers.
	 * */
	private String[] recordIds;

	/**
	 * A String array containing the list of groups/users.
	 * */
	private String[] groups;

	private static final String itemRequired = "errors.item.required";

	/**
	 * Default Constructor
	 */
	/*public AssignPrivilegesForm()
	{
	}*/

	/**
	 * Returns the assignment operation.
	 * @return String the assignment operation.
	 * @see #setAssignOperation(String)
	 */
	public String getAssignOperation()
	{
		return assignOperation;
	}

	/**
	 * Sets the assignment operation.
	 * @param assignOperation String the assignment operation.
	 * @see #getAssignOperation()
	 */
	public void setAssignOperation(String assignOperation)
	{
		this.assignOperation = assignOperation;
	}

	/**
	 * Returns the list of groups/users.
	 * @return String[] the list of groups/users.
	 * @see #setGroups(String[])
	 */
	public String[] getGroups()
	{
		return groups;
	}

	/**
	 * Sets the list of groups/users.
	 * @param groups String[] the list of groups/users.
	 * @see #getGroups()
	 */
	public void setGroups(String[] groups)
	{
		this.groups = groups;
	}

	/**
	 * Returns the object type.
	 * @return String the object type.
	 * @see #setObjectType(String)
	 */
	public String getObjectType()
	{
		return objectType;
	}

	/**
	 * Sets the object type.
	 * @param objectTypes String the object type.
	 * @see #getObjectType()
	 */
	public void setObjectType(String objectTypes)
	{
		this.objectType = objectTypes;
	}

	/**
	 * Returns the privilege name.
	 * @return String the privilege name.
	 * @see #setPrivilege(String)
	 */
	public String getPrivilege()
	{
		return privilege;
	}

	/**
	 * Sets the privilege name.
	 * @param privileges String the privilege name.
	 * @see #getPrivilege()
	 */
	public void setPrivilege(String privileges)
	{
		this.privilege = privileges;
	}

	/**
	 * Returns the list of record identifiers.
	 * @return String[] the list of record identifiers.
	 * @see #setRecordIds(String[])
	 */
	public String[] getRecordIds()
	{
		return recordIds;
	}

	/**
	 * Sets the list of record identifiers.
	 * @param recordIds String[] the list of record identifiers.
	 * @see #getRecordIds()
	 */
	public void setRecordIds(String[] recordIds)
	{
		this.recordIds = recordIds;
	}

	/**
	 * Overrides the validate method of ActionForm.
	 * @param mapping Action mapping instance
	 * @param request HttpServletRequest instance
	 * @return error ActionErrors instance
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request)
	{
		ActionErrors errors = new ActionErrors();

		if (assignOperation == null)
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(itemRequired,
					ApplicationProperties.getValue("assignPrivileges.assign")));
		}

		if (privilege == null)
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(itemRequired,
					ApplicationProperties.getValue("assignPrivileges.privileges")));
		}

		if (objectType == null)
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(itemRequired,
					ApplicationProperties.getValue("assignPrivileges.objectType")));
		}

		if (recordIds == null)
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(itemRequired,
					ApplicationProperties.getValue("assignPrivileges.recordId")));
		}

		if (groups == null)
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(itemRequired,
					ApplicationProperties.getValue("assignPrivileges.group")));
		}

		return errors;
	}
}