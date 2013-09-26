/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */

/*
 * Created on January 17, 2007
 * @author
 *
 */

package edu.wustl.clinportal.actionForm;

import java.io.Serializable;
import java.util.List;

import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.domain.AbstractDomainObject;

/**
 * @author falguni_sachde
 *
 * 
 */
@SuppressWarnings("PMD.LongVariable")
public class AnnotationDataEntryForm extends AbstractActionForm implements Serializable
{

	private static final long serialVersionUID = 5496516855615880241L;
	protected List annotationsList;
	protected String definedAnnotationsDataXML;
	protected String selectedAnnotation;
	protected String parentEntityId;
	private String selectedRecords;
	private String selectedStaticEntityId;
	private String selectedStaticEntityRecordId;

	/**
	 * @return
	 */
	public String getSelectedStaticEntityId()
	{
		return selectedStaticEntityId;
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
	public String getSelectedStaticEntityRecordId()
	{
		return selectedStaticEntityRecordId;
	}

	/**
	 * @param selStaticEntRecId
	 */
	public void setSelectedStaticEntityRecordId(String selStaticEntRecId)
	{
		this.selectedStaticEntityRecordId = selStaticEntRecId;
	}

	/**
	 * @return
	 */
	public String getSelectedRecords()
	{
		return selectedRecords;
	}

	/**
	 * @param selectedRecords
	 */
	public void setSelectedRecords(String selectedRecords)
	{
		this.selectedRecords = selectedRecords;
	}

	/**
	 * @return
	 */
	public String getDefinedAnnotationsDataXML()
	{
		return this.definedAnnotationsDataXML;
	}

	/**
	 * @param defAnnosDataXML
	 */
	public void setDefinedAnnotationsDataXML(String defAnnosDataXML)
	{
		this.definedAnnotationsDataXML = defAnnosDataXML;
	}

	/**
	 * @return
	 */
	public List getAnnotationsList()
	{
		return this.annotationsList;
	}

	/**
	 * @param annotationsList
	 */
	public void setAnnotationsList(List annotationsList)
	{
		this.annotationsList = annotationsList;
	}

	/**
	 * @return
	 */
	public String getSelectedAnnotation()
	{
		return this.selectedAnnotation;
	}

	/**
	 * @param selAnnotation
	 */
	public void setSelectedAnnotation(String selAnnotation)
	{
		this.selectedAnnotation = selAnnotation;
	}

	/**
	 * @return
	 */
	public String getParentEntityId()
	{
		return this.parentEntityId;
	}

	/**
	 * @param parentEntityId
	 */
	public void setParentEntityId(String parentEntityId)
	{
		this.parentEntityId = parentEntityId;
	}

	/* (non-Javadoc)
	 * @see edu.wustl.common.actionForm.AbstractActionForm#getFormId()
	 */
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

	/* (non-Javadoc)
	 * @see edu.wustl.common.actionForm.AbstractActionForm#reset()
	 */
	protected void reset()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setAddNewObjectIdentifier(String addNewFor, Long addObjectIdentifier)
	{
		// TODO Auto-generated method stub

	}
}