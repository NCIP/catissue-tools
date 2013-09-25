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
 *<p>Copyright:TODO</p>
 *@author 
 *@version 1.0
 */

package edu.wustl.clinportal.DEIntegration;

import java.util.Collection;
import java.util.HashSet;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.clinportal.bizlogic.AnnotationUtil;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.dao.exception.DAOException;

/**
 * 
 * @author shital_lawhale
 *
 */
public class DEIntegration extends edu.common.dynamicextensions.DEIntegration.DEIntegration
{

	/**
	 * 
	 * @param hookEntityId
	 * @param dynamicEntityId
	 * @param isEntityFromXmi
	 * @param isCategory
	 * @return
	 * @throws DynamicExtensionsApplicationException 
	 * @throws DynamicExtensionsSystemException
	 * @throws BizLogicException 
	 */
	public Long addAssociation(Long hookEntityId, Long dynamicEntityId, boolean isEntityFromXmi,
			boolean isCategory) throws DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException, BizLogicException
	{
		Long id = null;
		if (!isCategory)
		{
			id = AnnotationUtil.addAssociation(hookEntityId, dynamicEntityId, isEntityFromXmi);
		}

		return id;

	}

	/**
	 * 
	 * @param containerId
	 * @param staticEntityRecId
	 * @param dynaEntityRecId
	 * @param hookEntityId
	 * @throws DynamicExtensionsApplicationException 
	 * @throws DynamicExtensionsSystemException
	 * @throws BizLogicException 
	 * @throws DAOException
	 */
	public void associateRecords(Long containerId, Long staticEntityRecId, Long dynaEntityRecId,
			Long hookEntityId) throws DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException, BizLogicException, DAOException
	{
		EntityManagerInterface entityManager = EntityManager.getInstance();
		try
		{
			if (isCategory(containerId))
			{
				//Then take container id of its root entity and then associate the record.
				//				Long catContainerId = containerId;
				containerId = entityManager.getCategoryRootContainerId(containerId);
				//Commented this code since for category now 
				//DE returns original entity record id of root category entity.
				//Hence not required to to this stuff here
				//			Collection<Long> recIDColl = getCategoryRecordIdBasedOnCategoryId(catContainerId,
				//						dynaEntityRecId);
				//			if (recIDColl != null && !recIDColl.isEmpty())
				//			{
				//				Iterator iterateRecId = recIDColl.iterator();
				//				dynaEntityRecId = (Long) iterateRecId.next();
				//			}
				//RecordIdBasedOnCategoryId(containerId,dynamicEntityRecordId);
			}
			Long dynamicEntityId = entityManager.getEntityIdByContainerId(containerId);
			EntityInterface dynamicEntity = EntityCache.getInstance()
					.getEntityById(dynamicEntityId);
			EntityInterface staticEntity = EntityCache.getInstance().getEntityById(
					Long.valueOf(hookEntityId));
			Collection<AssociationInterface> asntCollection = staticEntity
					.getAssociationCollection();
			do
			{
				AssociationInterface asntInterface = null;
				for (AssociationInterface association : asntCollection)
				{
					if (association.getTargetEntity().equals(dynamicEntity))
					{
						asntInterface = association;
						break;
					}
				}
				entityManager.associateEntityRecords(asntInterface, staticEntityRecId,
						dynaEntityRecId);
				dynamicEntity = dynamicEntity.getParentEntity();
			}
			while (dynamicEntity != null);
		}
		catch (DynamicExtensionsSystemException e)
		{
			throw new DAOException(ErrorKey.getErrorKey("de.common.error"),e,"Can not associate static and dynamic records");
		}

	}

	/**
	 * 
	 * @param containerId
	 * @return whether this entity is simple DE form /category. 
	 * @throws DynamicExtensionsSystemException
	 */
	public boolean isCategory(Long containerId) throws DynamicExtensionsSystemException
	{
		edu.common.dynamicextensions.DEIntegration.DEIntegration integrate = 
			new edu.common.dynamicextensions.DEIntegration.DEIntegration();
		return integrate.isCategory(containerId);

	}

	/**
	 * 
	 * @param hookEntityId
	 * @return
	 * @throws DynamicExtensionsSystemException 
	 */
	public Collection getDynamicEntitiesContainerIdFromHookEntity(Long hookEntityId)
			throws DynamicExtensionsSystemException
	{
		Collection dynamicList = new HashSet();

		return dynamicList;
	}
}
