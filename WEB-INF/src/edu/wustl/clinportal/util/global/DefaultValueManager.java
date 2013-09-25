/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */


package edu.wustl.clinportal.util.global;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.util.XMLPropertyHandler;
import edu.wustl.common.util.logger.Logger;

/**
 * This class is the Setter class for hash map.
 * CatissueCoreServeletContextListener.java class call validateDefaultValues() method, this method will
 * validate default Values from Configuration file i.e caTissueCore_Properties.xml for enumerated dropdowns and populate
 * defaultValue map which will have key value pair if validation fails for some value then empty String will be mapped to corresponding key.
 * Bug ID: defaultValueConfiguration_BugID
 * Patch ID:defaultValueConfiguration_BugID_3
 * See also:defaultValueConfiguration_BugID_1,2
 */
public class DefaultValueManager
{

	private static HashMap defaultValueMap = new HashMap();

	/**
	 * This method is getter method with parameter as Key and it will return value append to the key
	 * @param defaultConstant
	 * @return dafaultValueMap.get(defaultConstant);
	 */
	public static Object getDefaultValue(String defaultConstant)
	{
		return defaultValueMap.get(defaultConstant);
	}

	/**
	 * This method is setter method 
	 * It will map value with the key
	 * @param key
	 * @param value
	 */
	private static void setDefaultValue(String key, String value)
	{
		defaultValueMap.put(key, value);
	}

	/**
	 * Description: Validate and Configure default value for enumerated drop downs.
	 */
	public static void validateAndInitDefaultValueMap()
	{
		String specimenClassName = null;
		List permValueList = new ArrayList();
		for (int iCount = 0; iCount < Constants.defaultValueKeys.length; iCount++)
		{
			String defaultValue = XMLPropertyHandler
					.getValue(Constants.defaultValueKeys[iCount][0]);
			NameValueBean defaultValueBean = null;
			//converting defaultValue into NameValue object.
			if (defaultValue != null)
			{
				defaultValueBean = new NameValueBean(defaultValue, defaultValue);
			}
			if ((Constants.defaultValueKeys[iCount][0]).equals(Constants.DEFAULT_SPECIMEN))
			{
				specimenClassName = defaultValue;
			}
			if ((Constants.defaultValueKeys[iCount][0]).equals(Constants.DEFAULT_SPECIMEN_TYPE))
			{
				//Get the Specimen Type List. 
				permValueList = Utility.getSpecimenTypes(specimenClassName);
			}
			else
			{
				permValueList = Utility.getListFromCDE(Constants.defaultValueKeys[iCount][1]);
			}
			//If List contain default value then key,Value pair is set in default value map else empty string is set for that key
			if (permValueList != null && permValueList.contains(defaultValueBean))
			{
				DefaultValueManager.setDefaultValue(Constants.defaultValueKeys[iCount][0],
						defaultValueBean.getValue());
			}
			else
			{
				DefaultValueManager.setDefaultValue(Constants.defaultValueKeys[iCount][0], "");
				Logger.out.error("Default Value set for '" + Constants.defaultValueKeys[iCount][0]
						+ "' is not in the CDEList");
			}
		}
	}
}
