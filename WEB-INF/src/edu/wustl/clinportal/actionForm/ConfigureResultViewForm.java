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
 *<p>Copyright: (c) Washington University, School of Medicine 2004</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *@author Poornima Govindrao
 *@version 1.0
 */

package edu.wustl.clinportal.actionForm;

import org.apache.struts.action.ActionForm;

/**
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright: (c) Washington University, School of Medicine 2005</p>
 *<p>Company: Washington University, School of Medicine</p>
 *@author falguni sachde
 *@version 1.0
 */

public class ConfigureResultViewForm extends ActionForm
{

	private String tableName;
	private String[] selectedColumnNames;
	private String[] columnNames;
	private String nextAction;
	private Long distributionId;
	private boolean reportAction = true;

	/**
	 * @return distributionId Returns the distributionId.
	 */
	public Long getDistributionId()
	{
		return distributionId;
	}

	/**
	 * @param distributionId The distributionId to set.
	 */
	public void setDistributionId(Long distributionId)
	{
		this.distributionId = distributionId;
	}

	/**
	 * @return Returns the columnNames.
	 */
	public String[] getColumnNames()
	{
		return columnNames;
	}

	/**
	 * @param columnNames The columnNames to set.
	 */
	public void setColumnNames(String[] columnNames)
	{
		this.columnNames = columnNames;
	}

	/**
	 * @return Returns the selectedColumnNames.
	 */
	public String[] getSelectedColumnNames()
	{
		return selectedColumnNames;
	}

	/**
	 * @param selColumnNames The selectedColumnNames to set.
	 */
	public void setSelectedColumnNames(String[] selColumnNames)
	{
		this.selectedColumnNames = selColumnNames;
	}

	/**
	 * @return Returns the tableName.
	 */
	public String getTableName()
	{
		return tableName;
	}

	/**
	 * @param tableName The tableName to set.
	 */
	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}

	/**
	 * @return Returns the nextAction.
	 */
	public String getNextAction()
	{
		return nextAction;
	}

	/**
	 * @param nextAction The nextAction to set.
	 */
	public void setNextAction(String nextAction)
	{
		this.nextAction = nextAction;
	}

	/**
	 * @return Returns the reportAction.
	 */
	public boolean isReportAction()
	{
		return reportAction;
	}

	/**
	 * @param reportAction The reportAction to set.
	 */
	public void setReportAction(boolean reportAction)
	{
		this.reportAction = reportAction;
	}
}
