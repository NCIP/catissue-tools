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

import edu.wustl.cab2b.server.cache.EntityCache;
/**
 * This is a factory to get single instance of EntityCache , so that once initialised the same instance will be used everywhere.
 * @author deepti_shelar
 *
 */
public class EntityCacheFactory
{
	/**
	 * Returns the instance of EntityCache.
	 * @return EntityCache EntityCacheInstance 
	 */
	public static EntityCache getInstance()
	{
		return EntityCache.getInstance();
	}
}
