/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */


package edu.wustl.clinportal.bizlogic.test;

import java.util.Date;

/**
 * <p>This class initializes the fields of UniqueKeyGeneratorUtil.java</p>
 * @author Ashwin Gupta
 * @version 1.1
 */
public class UniqueKeyGeneratorUtil
{
	private static Date date;

	/**
	 * gets unique key for name,barcode etc...
	 * @return string
	 */
	public static String getUniqueKey()
	{
		date = new Date();
		String uniqueKey = String.valueOf(date.getTime());
		return uniqueKey;
	}
}
