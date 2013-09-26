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
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright:TODO</p>
 *@author 
 *@version 1.0
 */

package edu.wustl.clinportal.actionForm;

import java.io.Serializable;
import java.util.List;

import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.domain.AbstractDomainObject;

public class AnnotationForm extends AbstractActionForm implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	private String annotationGroupsXML;
	/**
	 * 
	 */
	private String annotationEntitiesXML;
	/**
	 * 
	 */
	private List systemEntitiesList;
	/**
	 * 
	 */
	private String selectedStaticEntityId;
	/**
	 * 
	 */
	private List conditionalInstancesList;
	/**
	 * 
	 */
	private String[] conditionVal;

	/**
	 * @return
	 */
	public String[] getConditionVal()
	{
		return conditionVal;
	}

	/**
	 * @param conditionVal
	 */
	public void setConditionVal(String[] conditionVal)
	{
		this.conditionVal = conditionVal;
	}

	/**
	 * @return
	 */
	public String getSelectedStaticEntityId()
	{
		return this.selectedStaticEntityId;
	}

	/**
	 * @param selStaticEntityId
	 */
	public void setSelectedStaticEntityId(String selStaticEntityId)
	{
		this.selectedStaticEntityId = selStaticEntityId;
	}

	/**
	 * @return
	 */
	public List getSystemEntitiesList()
	{
		return this.systemEntitiesList;
	}

	/**
	 * @param sysEntitiesLst
	 */
	public void setSystemEntitiesList(List sysEntitiesLst)
	{
		this.systemEntitiesList = sysEntitiesLst;
	}

	/**
	 * @return
	 */
	public String getAnnotationEntitiesXML()
	{
		return this.annotationEntitiesXML;
	}

	/**
	 * @param annoEntitiesXML
	 */
	public void setAnnotationEntitiesXML(String annoEntitiesXML)
	{
		this.annotationEntitiesXML = annoEntitiesXML;
	}

	/**
	 * @return
	 */
	public String getAnnotationGroupsXML()
	{
		return this.annotationGroupsXML;
	}

	/**
	 * @param annonGroupsXML
	 */
	public void setAnnotationGroupsXML(String annonGroupsXML)
	{
		this.annotationGroupsXML = annonGroupsXML;
	}

	public int getFormId()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see edu.wustl.common.actionForm.IValueObject#setAllValues(edu.wustl.common.domain.AbstractDomainObject)
	 */
	public void setAllValues(AbstractDomainObject abstractDomain)
	{
		// TODO Auto-generated method stub

	}

	protected void reset()
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @return
	 */
	public List getConditionalInstancesList()
	{
		return conditionalInstancesList;
	}

	/**
	 * @param condInstancesList
	 */
	public void setConditionalInstancesList(List condInstancesList)
	{
		this.conditionalInstancesList = condInstancesList;
	}

	@Override
	public void setAddNewObjectIdentifier(String addNewFor, Long addObjectIdentifier)
	{
		// TODO Auto-generated method stub

	}
}
