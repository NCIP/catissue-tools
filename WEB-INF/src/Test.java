/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



public class Test
{

	public static void main(String[] args)
	{
		try
		{
			Date d = edu.wustl.common.util.Utility.parseDate("06-04-2009","dd-MM-yyyy");
		System.out.println(d);
		}
		catch(Exception e)
		{
			e.printStackTrace();		}
	}
	
	public static String parseDateToString(Date date, String pattern)
	{
		String dateStr = "";
		if (date != null)
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
			dateStr = dateFormat.format(date);
		}
		return dateStr;
	}
}
