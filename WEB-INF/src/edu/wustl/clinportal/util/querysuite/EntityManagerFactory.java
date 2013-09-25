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

import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;

/**
 * Returns the instance of EntityManager.
 * @author deepti_shelar
 *
 */
public class EntityManagerFactory
{
	/**
	 * 
	 * @return EntityManager EntityManagerInstance
	 */
	public static EntityManagerInterface getEntityManager()
	{
		//return EntityManager.getInstance();
		return EntityManager.getInstance();
	}
}
