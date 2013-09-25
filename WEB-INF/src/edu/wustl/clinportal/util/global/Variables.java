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
 */

package edu.wustl.clinportal.util.global;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class Variables //extends edu.wustl.common.util.global.Variables
{

	public static Vector databaseDefinitions = new Vector();
	public static String databaseDriver = "";
	public static String[] databasenames;
	public static String applicationCvsTag = "";
	public static int maximumTreeNodeLimit;
	public static boolean isSpecimenLabelGeneratorAvl = false;
	public static boolean isStorageContainerLabelGeneratorAvl = false;
	public static boolean isSpecimenBarcodeGeneratorAvl = false;
	public static boolean isStorageContainerBarcodeGeneratorAvl = false;
	public static boolean isCatissueModelAvailable = false;

	// Patch ID: SimpleSearchEdit_7
	public static Map<String, String> aliasAndPageOfMap = new HashMap<String, String>();

	public static List<String> queryReadDeniedObjectList = new ArrayList<String>();

	/**
	 * specify CONST_VARIABLES_VALUE.
	 */
	private static final String CONST_VARIABLES_VALUE = "";

	/**
	 * specify applicationName.
	 */
	public static String applicationHome = CONST_VARIABLES_VALUE;
	public static String applicationName = CONST_VARIABLES_VALUE;
	public static String applicationVersion = CONST_VARIABLES_VALUE;
	public static String datePattern = CONST_VARIABLES_VALUE;
	public static String timePattern = CONST_VARIABLES_VALUE;
	public static String timeFormatFunction = CONST_VARIABLES_VALUE;
	public static String dateFormatFunction = CONST_VARIABLES_VALUE;
	public static String strTodateFunction = CONST_VARIABLES_VALUE;
	public static String dateTostrFunction = CONST_VARIABLES_VALUE;
	public static String catissueURL = CONST_VARIABLES_VALUE;
	public static String databaseName = CONST_VARIABLES_VALUE;
	public static String propertiesDirPath = CONST_VARIABLES_VALUE;

	/**
	 * 
	 * @param dataColl
	 * @return
	 */
	public static String prepareColTypes(List dataColl)
	{
		return prepareColTypes(dataColl, false);
	}

	/**
	 * 
	 * @param dataColl
	 * @param createCheckBoxCol
	 * @return
	 */
	public static String prepareColTypes(List dataColl, boolean createCheckBoxCol)
	{
		String colType = "";
		if (dataColl != null && !dataColl.isEmpty())
		{
			List rowDataColl = (List) dataColl.get(0);

			Iterator itr = rowDataColl.iterator();
			if (createCheckBoxCol)
			{
				colType = "ch,";
			}
			while (itr.hasNext())
			{
				Object obj = itr.next();

				colType = getColumnType(obj, colType);
			}
		}
		if (colType.length() > 0)
		{
			colType = colType.substring(0, colType.length() - 1);
		}
		return colType;
	}

	/**
	 * 
	 * @param obj
	 * @param colType
	 * @return
	 */
	private static String getColumnType(Object obj, String colType)
	{
		String columnType;
		if (obj instanceof Number)
		{
			columnType = colType + "int,";
		}
		else if (obj instanceof Date)
		{
			columnType = colType + "date,";
		}
		else
		{
			columnType = colType + "str,";
		}

		return columnType;
	}
}