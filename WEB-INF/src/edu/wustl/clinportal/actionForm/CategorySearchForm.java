/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */


package edu.wustl.clinportal.actionForm;

import java.util.List;

import org.apache.struts.action.ActionForm;

import edu.wustl.common.beans.NameValueBean;

/**
 * FormBean representing the QueryModule's properties.  
 */

public class CategorySearchForm extends ActionForm
{

	/**
	 * String to store the text field value.
	 */
	private String textField = null;
	/**
	 * String to store the classChecked checkbox's  value.
	 */
	private String classChecked = null;
	/**
	 * String to store the attributeChecked checkbox's value.
	 */
	private String attributeChecked = null;
	/**
	 * String to store the permissibleValuesChecked checkbox's value.
	 */
	private String permissibleValuesChecked = null;
	/**
	 * String to store the the IncludeDescription checkbox's value.
	 */
	private String includeDescriptionChecked = null;
	/**
	 * String to store the radio button selected value.
	 */
	private String selected = null;
	/**
	 * String to store the radio button selected value.
	 */
	private String options = null;
	/**
	 * String to store the entityName value.
	 */
	private String entityName = null;
	/**
	 * String to store the string used To Create Query Object.
	 */
	private String stringToCreateQueryObject = null;
	/**
	 * String to store the errors value.
	 */
	private List errors = null;
	/**
	 * String to store the searchButton value.
	 */
	private String searchButton = null;
	/**
	 * String to store the nextOperation value.
	 */
	private String nextOperation = null;
	/**
	 * String to store the text field value.
	 */
	private String nodeId = null;
	/**
	 * String to store the text field value.
	 */
	private String currentPage = null;
	/**
	 * 
	 */
	private String booleanAttribute = null;

	/**
	 * 
	 */
	private String[] selectedColumnNames;
	/**
	 * 
	 */
	private String[] columnNames;
	/**
	 * 
	 */
	private List<NameValueBean> selectedColumnNameValueBeanList;
	/**
	 * String to store currentSelectedObject
	 */
	private String currentSelectedObject = null;
	/**
	* String to store currentSelectedObject
	*/
	private String currentSelectedNodeInTree = null;

	/**
	 * @return the currentPage
	 */
	public String getCurrentPage()
	{
		return currentPage;
	}

	/**
	 * @param currentPage the currentPage to set
	 */
	public void setCurrentPage(String currentPage)
	{
		this.currentPage = currentPage;
	}

	/**
	 * @return the nextOperation
	 */
	public String getNextOperation()
	{
		return nextOperation;
	}

	/**
	 * @param nextOperation the nextOperation to set
	 */
	public void setNextOperation(String nextOperation)
	{
		this.nextOperation = nextOperation;
	}

	/**
	 * @return the searchButton
	 */
	public String getSearchButton()
	{
		return searchButton;
	}

	/**
	 * @param searchButton the searchButton to set
	 */
	public void setSearchButton(String searchButton)
	{
		this.searchButton = searchButton;
	}

	/**
	 * @return the errors
	 */
	public List getErrors()
	{
		return errors;
	}

	/**
	 * @param errors the errors to set
	 */
	public void setErrors(List errors)
	{
		this.errors = errors;
	}

	/**
	 * @return the stringToCreateQueryObject
	 */
	public String getStringToCreateQueryObject()
	{
		return stringToCreateQueryObject;
	}

	/**
	 * @param strToCreateQryObj the stringToCreateQueryObject to set
	 */
	public void setStringToCreateQueryObject(String strToCreateQryObj)
	{
		this.stringToCreateQueryObject = strToCreateQryObj;
	}

	/**
	 * @return the selected
	 */
	public String getSelected()
	{
		return selected;
	}

	/**
	 * @param selected the selected to set
	 */
	public void setSelected(String selected)
	{
		this.selected = selected;
	}

	/**
	 * @return the textField
	 */
	public String getTextField()
	{
		return textField;
	}

	/**
	 * @param textField the textField to set
	 */
	public void setTextField(String textField)
	{
		this.textField = textField;
	}

	/**
	 * @return the attributeChecked
	 */
	public String getAttributeChecked()
	{
		return attributeChecked;
	}

	/**
	 * @param attributeChecked the attributeChecked to set
	 */
	public void setAttributeChecked(String attributeChecked)
	{
		this.attributeChecked = attributeChecked;
	}

	/**
	 * @return the classChecked
	 */
	public String getClassChecked()
	{
		return classChecked;
	}

	/**
	 * @param classChecked the classChecked to set
	 */
	public void setClassChecked(String classChecked)
	{
		this.classChecked = classChecked;
	}

	/**
	 * @return the permissibleValuesChecked
	 */
	public String getPermissibleValuesChecked()
	{
		return permissibleValuesChecked;
	}

	/**
	 * @param permValChecked the permissibleValuesChecked to set
	 */
	public void setPermissibleValuesChecked(String permValChecked)
	{
		this.permissibleValuesChecked = permValChecked;
	}

	/**
	 * @return the entityName
	 */
	public String getEntityName()
	{
		return entityName;
	}

	/**
	 * @param entityName the entityName to set
	 */
	public void setEntityName(String entityName)
	{
		this.entityName = entityName;
	}

	/**
	 * @return the nodeId
	 */
	public String getNodeId()
	{
		return nodeId;
	}

	/**
	 * @param nodeId the nodeId to set
	 */
	public void setNodeId(String nodeId)
	{
		this.nodeId = nodeId;
	}

	/**
	 * @return the booleanAttribute
	 */
	public String getBooleanAttribute()
	{
		return booleanAttribute;
	}

	/**
	 * @param booleanAttribute the booleanAttribute to set
	 */
	public void setBooleanAttribute(String booleanAttribute)
	{
		this.booleanAttribute = booleanAttribute;
	}

	public String getIncludeDescriptionChecked()
	{
		return includeDescriptionChecked;
	}

	public void setIncludeDescriptionChecked(String inclDescrChecked)
	{
		this.includeDescriptionChecked = inclDescrChecked;
	}

	/**
	 * @return the columnNames
	 */
	public String[] getColumnNames()
	{
		return columnNames;
	}

	/**
	 * @param columnNames the columnNames to set
	 */
	public void setColumnNames(String[] columnNames)
	{
		this.columnNames = columnNames;
	}

	/**
	 * @return the selectedColumnNames
	 */
	public String[] getSelectedColumnNames()
	{
		return selectedColumnNames;
	}

	/**
	 * @param selectedColNames the selectedColumnNames to set
	 */
	public void setSelectedColumnNames(String[] selectedColNames)
	{
		this.selectedColumnNames = selectedColNames;
	}

	/**
	 * @return the currentSelectedObject
	 */
	public String getCurrentSelectedObject()
	{
		return currentSelectedObject;
	}

	/**
	 * @param currentSelObj the currentSelectedObject to set
	 */
	public void setCurrentSelectedObject(String currentSelObj)
	{
		this.currentSelectedObject = currentSelObj;
	}

	/**
	 * @return the currentSelectedNodeInTree
	 */
	public String getCurrentSelectedNodeInTree()
	{
		return currentSelectedNodeInTree;
	}

	/**
	 * @param curSelNodeInTree the currentSelectedNodeInTree to set
	 */
	public void setCurrentSelectedNodeInTree(String curSelNodeInTree)
	{
		this.currentSelectedNodeInTree = curSelNodeInTree;
	}

	/**
	 * @return the selectedColumnNameValueBeanList
	 */
	public List<NameValueBean> getSelectedColumnNameValueBeanList()
	{
		return selectedColumnNameValueBeanList;
	}

	/**
	 * @param selColNameValBLst the selectedColumnNameValueBeanList to set
	 */
	public void setSelectedColumnNameValueBeanList(List<NameValueBean> selColNameValBLst)
	{
		this.selectedColumnNameValueBeanList = selColNameValBLst;
	}

	/**
	 * @return the options
	 */
	public String getOptions()
	{
		return options;
	}

	/**
	 * @param options the options to set
	 */
	public void setOptions(String options)
	{
		this.options = options;
	}
}
