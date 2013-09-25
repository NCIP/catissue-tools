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
 * <p>Title: Department Class</p>
 * <p>Description: A department to which a User belongs to.  </p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Mandar Deshmukh
 * @version 1.00
 */

package edu.wustl.clinportal.domain;

import edu.wustl.clinportal.actionForm.DepartmentForm;
import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.actionForm.IValueObject;
import edu.wustl.common.domain.AbstractDomainObject;
/**
 * A department to which a User belongs to.
 *  * @hibernate.class table="CATISSUE_DEPARTMENT"
 */
public class Department extends AbstractDomainObject implements java.io.Serializable
{
	private static final long serialVersionUID = 1234567890L;

	/**
	 * System generated unique id.
	 */
	protected Long id;

	/**
	 * Name of the department.
	 */
	protected String name;

	/**
	 * NOTE: Do not delete this constructor. Hibernet uses this by reflection API.
	 * */
	public Department()
	{
		
	}
	
	public Department(AbstractActionForm form)
	{
		setAllValues(form);
	}

	/**
	 * Returns the id assigned to department.
	 * @hibernate.id name="id" column="IDENTIFIER" type="long"
	 * length="30" unsaved-value="null" generator-class="native"
	 * @hibernate.generator-param name="sequence" value="CATISSUE_DEPARTMENT_SEQ"
	 * @return a unique id assigned to the department.
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * Sets an id for the department.
	 * @param idt Unique id to be assigned to the department.
	 */
	public void setId(Long idt)
	{
		this.id = idt;
	}

	/**
	 * Returns the name of the department.
	 * 
	 * @hibernate.property name="name" type="string" column="NAME" length="255"
	 * not-null="true" unique="true"
	 * @return name of the department.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the department.
	 * 
	 * @param name Name of the department.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see edu.wustl.clinportal.domain.AbstractDomainObject#setAllValues(edu.wustl.clinportal.actionForm.AbstractActionForm)
	 */
	public void setAllValues(IValueObject abstractForm) 
	{
		DepartmentForm departmentForm = (DepartmentForm)abstractForm;
		
		this.name = departmentForm.getName().trim();
	}
	
	 /**
     * Returns message label to display on success add or edit
     * @return String
     */
	public String getMessageLabel() {		
		return this.name;
	}
}