
package edu.wustl.clinportal.util;

import edu.wustl.clinportal.util.global.Constants;

/**
 * This class creates an object with given key which contains all the ids.
 * 
 * @author srinivasarao_vassadi
 * 
 */
public class EventTreeObject
{

	public String objectName;

	public String eventId;

	public String containerId;

	public String formContextId;

	public String registrationId;

	public String eventEntryId;

	public String eventEntryNumber;

	public String recEntryId;

	public String dynamicRecId;

	/**
	 * Default Constructor
	 */
	public EventTreeObject()
	{

	}

	/**
	 * 
	 * @param treeNodeKey
	 */
	public EventTreeObject(String treeNodeKey)
	{
		populateObject(treeNodeKey);
	}

	/**
	 * @return the objectName
	 */
	public String getObjectName()
	{
		return objectName;
	}

	/**
	 * @param objectName the objectName to set
	 */
	public void setObjectName(String objectName)
	{
		this.objectName = objectName;
	}

	/**
	 * @return the eventId
	 */
	public String getEventId()
	{
		return eventId;
	}

	/**
	 * @param eventId the eventId to set
	 */
	public void setEventId(String eventId)
	{
		this.eventId = eventId;
	}

	/**
	 * @return the containerId
	 */
	public String getContainerId()
	{
		return containerId;
	}

	/**
	 * @param containerId the containerId to set
	 */
	public void setContainerId(String containerId)
	{
		this.containerId = containerId;
	}

	/**
	 * @return the formContextId
	 */
	public String getFormContextId()
	{
		return formContextId;
	}

	/**
	 * @param formContextId the formContextId to set
	 */
	public void setFormContextId(String formContextId)
	{
		this.formContextId = formContextId;
	}

	/**
	 * @return the registrationId
	 */
	public String getRegistrationId()
	{
		return registrationId;
	}

	/**
	 * @param registrationId the registrationId to set
	 */
	public void setRegistrationId(String registrationId)
	{
		this.registrationId = registrationId;
	}

	/**
	 * @return the eventEntryId
	 */
	public String getEventEntryId()
	{
		return eventEntryId;
	}

	/**
	 * @param eventEntryId the eventEntryId to set
	 */
	public void setEventEntryId(String eventEntryId)
	{
		this.eventEntryId = eventEntryId;
	}

	/**
	 * @return the eventEntryNumber
	 */
	public String getEventEntryNumber()
	{
		return eventEntryNumber;
	}

	/**
	 * @param eventEntryNumber the eventEntryNumber to set
	 */
	public void setEventEntryNumber(String eventEntryNumber)
	{
		this.eventEntryNumber = eventEntryNumber;
	}

	/**
	 * @return the recEntryId
	 */
	public String getRecEntryId()
	{
		return recEntryId;
	}

	/**
	 * @param recEntryId the recEntryId to set
	 */
	public void setRecEntryId(String recEntryId)
	{
		this.recEntryId = recEntryId;
	}

	/**
	 * @return the dynamicRecId
	 */
	public String getDynamicRecId()
	{
		return dynamicRecId;
	}

	/**
	 * @param dynamicRecId the dynamicRecId to set
	 */
	public void setDynamicRecId(String dynamicRecId)
	{
		this.dynamicRecId = dynamicRecId;
	}

	/**
	 * EventTreeObject will be populated using the key, given as parameter.
	 */
	public void populateObject(String treeNodeKey)
	{
		if (treeNodeKey != null && !treeNodeKey.equals(""))
		{
			String[] keyArray = treeNodeKey.split("_");

			this.setObjectName(keyArray[0]);
			this.setEventId(keyArray[1]);
			this.setContainerId(keyArray[2]);
			this.setFormContextId(keyArray[3]);
			this.setRegistrationId(keyArray[4]);
			this.setEventEntryId(keyArray[5]);
			this.setEventEntryNumber(keyArray[6]);
			this.setRecEntryId(keyArray[7]);
			this.setDynamicRecId(keyArray[8]);
		}
	}

	/**
	 * This will return the tree view key using the object
	 * @return
	 */
	public String createKeyFromObject()
	{
		StringBuffer treeViewKey = new StringBuffer(this.getObjectName());
		treeViewKey.append('_');
		treeViewKey.append(this.getEventId());
		treeViewKey.append('_');
		treeViewKey.append(this.getContainerId());
		treeViewKey.append('_');
		treeViewKey.append(this.getFormContextId());
		treeViewKey.append('_');
		treeViewKey.append(this.getRegistrationId());
		treeViewKey.append('_');
		treeViewKey.append(this.getEventEntryId());
		treeViewKey.append('_');
		treeViewKey.append(this.getEventEntryNumber());
		treeViewKey.append('_');
		treeViewKey.append(this.getRecEntryId());
		treeViewKey.append('_');
		treeViewKey.append(this.getDynamicRecId());

		return treeViewKey.toString();
	}

	/*
	 * This method will return the EVENT ENTRY node 
	 */
	public String getEntryKey(String treeViewKey)
	{
		String[] keyArray = treeViewKey.split("_");
		StringBuffer key = new StringBuffer(Constants.EVENT_ENTRY_OBJECT);
		key.append("_");
		key.append(keyArray[1]);
		key.append("_");
		key.append("0");
		key.append("_");
		key.append("0");
		key.append("_");
		key.append(keyArray[4]);
		key.append("_");
		key.append(keyArray[5]);
		key.append("_");
		key.append(keyArray[6]);
		key.append("_");
		key.append("0");
		key.append("_");
		key.append("0");

		return key.toString();
	}

	public String getFormEntryKey(String treeViewKey)
	{

		String[] keyArray = treeViewKey.split("_");
		StringBuffer key = new StringBuffer(Constants.FORM_ENTRY_OBJECT);
		key.append("_");
		key.append(keyArray[1]);
		key.append("_");
		key.append(keyArray[2]);
		key.append("_");
		key.append(keyArray[3]);
		key.append("_");
		key.append(keyArray[4]);
		key.append("_");
		key.append(keyArray[5]);
		key.append("_");
		key.append(keyArray[6]);
		key.append("_");
		key.append("0");
		key.append("_");
		key.append("0");

		return key.toString();
	}

}
