/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */

package edu.wustl.clinportal.util.querysuite;


/**
 * enumerator for Query Error codes
 * @author vijay_pande
 *
 */
public enum QueryModuleError 
{
	SUCCESS (0),
	EMPTY_DAG (1),
	MULTIPLE_ROOT (2),
	NO_RESULT_PRESENT (3),
	SQL_EXCEPTION (4),
	DAO_EXCEPTION (5),
	CLASS_NOT_FOUND (6),
	RESULTS_MORE_THAN_LIMIT (10),
	NO_MAIN_OBJECT_IN_QUERY (11);
	
	/**
	 * Variable to hold value for error code
	 */
	private final int errorCode;
	/**
	 * Default constructor for enum
	 * @param errorCode
	 */
	QueryModuleError(int errorCode)
	{
		this.errorCode=errorCode;
	}
	/**
	 * Method to get error code of particular instance
	 * @return
	 */
	public int getErrorCode()
	{
		return errorCode;
	}
}
