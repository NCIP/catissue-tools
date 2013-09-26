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

import java.util.HashMap;
import java.util.Map;

public class TestCaseUtility
{

	//public final static long CS_WITH_ALLOW_READ_PRIV = 1;
	//public final static long CS_WITH_DISALLOW_READ_PRIV = 2;
	//public final static long CS_WITH_SCIENTIST_AS_PI = 3;
	public final static long SITE_WITH_ALLOWUSE_PRIV = 1;
	public final static long SITE_WITH_DISALLOWUSE_PRIV = 2;
	public final static String USER_WITH_ROLE_AS_SCIENTIST = "UserWithRoleAsScientist";
	public final static String PARTICIPANT_WITH_SCIENTIST_AS_PI = "ParticipantWithScientistAsPI";
	public final static String PARTICIPANT_WITH_ADMIN_AS_PI = "ParticipantWithAdminAsPI";
	public final static String CSR_WITH_SCIENTIST_AS_PI = "CSRWithScientistAsPI";
	public final static String CSR_WITH_ADMIN_AS_PI = "CSRWithAdminAsPI";
	public final static String CS_WITH_SCIENTIST_AS_PI = "CSWithScientistAsPI";
	public final static String CS_WITH_ADMIN_AS_PI = "CSWithAdminAsPI";

	private static Map objectMap = new HashMap();

	public static Object getObjectMap(Class className)
	{
		return objectMap.get(className.getName());
	}

	public static void setObjectMap(Object object, Class className)
	{
		objectMap.put(className.getName(), object);
	}

	public static Object getNameObjectMap(String objName)
	{
		return objectMap.get(objName);
	}

	public static void setNameObjectMap(String ObjName, Object object)
	{
		objectMap.put(ObjName, object);
	}

}
