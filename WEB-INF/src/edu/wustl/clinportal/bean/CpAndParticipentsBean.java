/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */


package edu.wustl.clinportal.bean;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * @author falguni_sachde
 *
 */
public class CpAndParticipentsBean implements Externalizable
{

	private static final long serialVersionUID = 1L;

	private String name;

	private String value;

	/*
	 * Default Constructor
	 */
	public CpAndParticipentsBean()
	{

	}

	public CpAndParticipentsBean(String name, String value)
	{
		this.name = name;
		this.value = value;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name)
	{
		//System.out.println("The name written is:"+name); 
		this.name = name;
	}

	/**
	 * @return Returns the value.
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * @param value The value to set.
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	public void readExternal(ObjectInput objInput) throws IOException, ClassNotFoundException
	{
		name = objInput.readUTF();
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	public void writeExternal(ObjectOutput out) throws IOException
	{
		//System.out.println("In Cp & Participants bean class");
		//System.out.println("Cp written is:"+ name);
		out.writeUTF(name);
		out.writeUTF(value);

	}
}
