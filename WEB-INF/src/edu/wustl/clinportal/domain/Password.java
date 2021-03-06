/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */


package edu.wustl.clinportal.domain;

import java.io.Serializable;
import java.util.Date;

import edu.wustl.clinportal.util.SearchUtil;
import edu.wustl.common.actionForm.IValueObject;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.AssignDataException;

/**
 * Set of attributes which defines updated information related to password of the user.
 * @hibernate.class table="CATISSUE_PASSWORD"
 */
public class Password extends AbstractDomainObject implements Serializable, Comparable
{

	private static final long serialVersionUID = 2084916630660576930L;

	/**
	 * System generated unique id.
	 */
	protected Long id;

	/**
	 * A string containing the password of the user.
	 */
	protected String password;

	/**
	 * Last updated date of the password .
	 */
	protected Date updateDate;

	////Change for API Search   --- Ashwin 04/10/2006
	protected User user;

	public Password()
	{

	}

	public Password(String password, User user)
	{
		this.password = password;
		this.user = user;
		updateDate = new Date();
	}

	/**
	 * Returns the id assigned to password.
	 * @return Returns the id.
	 * @hibernate.id name="id" column="IDENTIFIER" type="long" length="30"
	 * unsaved-value="null" generator-class="native"
	 * @hibernate.generator-param name="sequence" value="CATISSUE_PASSWORD_SEQ"
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * Sets an id for the password.
	 * @param idt Unique id to be assigned to the password.
	 */
	public void setId(Long idt)
	{
		this.id = idt;
	}

	/**
	 * Returns the password assigned to user.
	 * @hibernate.property name="password" type="string" column="PASSWORD" length="255"
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * Sets password of the user.
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}

	/**
	 * Returns the last updated date of password.
	 * @hibernate.property name="updateDate" type="date" column="UPDATE_DATE"
	 */
	public Date getUpdateDate()
	{
		return updateDate;
	}

	/**
	 * Sets last updated date of password.
	 */
	public void setUpdateDate(Date updateDate)
	{
		this.updateDate = updateDate;
	}

	/**
	 * @return Returns the user.
	 * @hibernate.many-to-one column="USER_ID" class="edu.wustl.clinportal.domain.User"
	 * constrained="true"
	 */
	public User getUser()
	{
		return user;
	}

	/**
	 * @param user The user to set.
	 */
	public void setUser(User user)
	{
		this.user = user;
	}

	/* (non-Javadoc)
	 * @see edu.wustl.common.domain.AbstractDomainObject#setAllValues(edu.wustl.common.actionForm.IValueObject)
	 */
	public void setAllValues(IValueObject abstractForm) throws AssignDataException
	{

		if (SearchUtil.isNullobject(user))
		{
			user = new User();
		}
	}

	/**
	 *  This method will compare two date Objects. When Collections.sort() is called, it will return a list 
	 *  of dates in which most recent date will be the first one. 
	 */
	public int compareTo(Object obj)
	{
		Password pwd = (Password) obj;
		return pwd.getUpdateDate().compareTo(this.updateDate);
	}

}