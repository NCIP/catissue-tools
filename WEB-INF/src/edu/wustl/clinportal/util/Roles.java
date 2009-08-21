/**
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright: (c) Washington University, School of Medicine 2004</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *@author Aarti Sharma
 *@version 1.0
 */ 
package edu.wustl.clinportal.util;

public interface Roles extends edu.wustl.security.global.Roles
{
	String PI = "PI";
    String READ_ONLY = "READ_ONLY";
    String USE_ONLY = "USE_ONLY";
    String UPDATE_ONLY = "UPDATE_ONLY";
    String COORDINATOR = "Coordinator";
    String DATA_ENTRY_PERSON = "DataEntryPerson";
}
