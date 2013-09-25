/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */

/*
 * Created on Sep 27, 2007
 * @author
 *
 */

package edu.wustl.clinportal.annotations.xmi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import edu.common.dynamicextensions.domain.Entity;
import edu.common.dynamicextensions.domain.integration.EntityMap;
import edu.common.dynamicextensions.domaininterface.AbstractEntityInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerConstantsInterface;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.clinportal.bizlogic.AnnotationBizLogic;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.dao.exception.DAOException;

/**
 * @author falguni_sachde
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class XMIUtility
{

	/**
	 * 
	 */
	private static EntityManagerInterface entityManager = EntityManager.getInstance();

	/**
	 * @param groupName
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	public static EntityGroupInterface getEntityGroup(String groupName)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		EntityGroupInterface egroup = null;
		if (groupName != null)
		{

			egroup = entityManager.getEntityGroupByName(groupName);
		}
		System.out.println("Entity Group found :" + groupName);
		return egroup;
	}

	/**
	 * @param entityGroup
	 * @throws DAOException
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	public static void addHookEntitiesToGroup(EntityGroupInterface entityGroup)
			throws DAOException, DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{

		Collection<ContainerInterface> mainContainers = entityGroup.getMainContainerCollection();
		System.out.println("mainContainers.size(): " + mainContainers.size());
		EntityInterface xmiStaticEntity = null;
		EntityInterface staticEntity = null;

		staticEntity = entityManager.getEntityByName("edu.wustl.clinportal.domain.RecordEntry");
		xmiStaticEntity = getHookEntityDetailsForXMI(staticEntity);
		for (ContainerInterface mainContainer : mainContainers)
		{

			AssociationInterface association = getHookEntityAssociation(staticEntity,
					(EntityInterface) mainContainer.getAbstractEntity());
			System.out.println("Association = " + association);
			xmiStaticEntity.addAssociation(association);
		}
		entityGroup.addEntity(xmiStaticEntity);
		System.out.println("Total Associations = " + xmiStaticEntity.getAllAssociations().size());

	}

	/**
	 * @param staticEntity
	 * @return
	 * @throws DynamicExtensionsApplicationException 
	 * @throws DynamicExtensionsSystemException 
	 */
	public static EntityInterface getHookEntityDetailsForXMI(EntityInterface srcEntity)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		//For XMI : add only id , name and table properties
		EntityInterface xmiEntity = new Entity();
		xmiEntity.setName(getHookEntityName(srcEntity.getName()));
		xmiEntity.setDescription(srcEntity.getDescription());
		xmiEntity.setTableProperties(srcEntity.getTableProperties());
		xmiEntity.setId(srcEntity.getId());
		xmiEntity.addAttribute(getIdAttribute(srcEntity));
		//	xmiEntity.addAssociation(getHookEntityAssociation(srcEntity,targetEntity));

		return xmiEntity;
	}

	/**
	 * @param staticEntity
	 * @return
	 * @throws DynamicExtensionsApplicationException 
	 * @throws DynamicExtensionsSystemException 
	 */
	public static EntityInterface getHookEntityDetailsForXMI(EntityInterface srcEntity,
			EntityInterface targetEntity) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		//For XMI : add only id , name and table properties
		EntityInterface xmiEntity = new Entity();
		xmiEntity.setName(getHookEntityName(srcEntity.getName()));
		xmiEntity.setDescription(srcEntity.getDescription());
		xmiEntity.setTableProperties(srcEntity.getTableProperties());
		xmiEntity.setId(srcEntity.getId());
		xmiEntity.addAttribute(getIdAttribute(srcEntity));
		//	xmiEntity.addAssociation(getHookEntityAssociation(srcEntity,targetEntity));

		return xmiEntity;
	}

	/**
	 * @param name
	 * @return
	 */
	public static String getHookEntityName(String name)
	{
		//Return last token from name
		String hookEntityname = null;
		StringTokenizer strTokenizer = new StringTokenizer(name, ".");
		while (strTokenizer.hasMoreElements())
		{
			hookEntityname = strTokenizer.nextToken();
		}
		return hookEntityname;
	}

	/**
	 * @param srcEntity
	 * @param targetEntity
	 * @return
	 * @throws DynamicExtensionsApplicationException 
	 * @throws DynamicExtensionsSystemException 
	 */
	private static AssociationInterface getHookEntityAssociation(EntityInterface srcEntity,
			EntityInterface targetEntity) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		if ((srcEntity != null) && (targetEntity != null))
		{
			Collection<AssociationInterface> associations = srcEntity.getAllAssociations();

			if (associations != null)
			{
				Iterator assocIter = associations.iterator();
				while (assocIter.hasNext())
				{
					AssociationInterface association = (AssociationInterface) assocIter.next();
					if (association.getTargetEntity().equals(targetEntity))
					{
						String srcEntityName = getHookEntityName(srcEntity.getName());
						//Change name of association 
						association
								.setName("Assoc_" + srcEntityName + "_" + targetEntity.getName());
						association.getSourceRole().setName(srcEntityName);
						association.getTargetRole().setName(targetEntity.getName() + "Collection");
						return association;
					}
				}

			}
		}
		return null;
	}

	/**
	 * @param entity
	 * @return
	 */
	public static AttributeInterface getIdAttribute(EntityInterface entity)
	{
		if (entity != null)
		{
			Collection<AttributeInterface> attributes = entity.getAllAttributes();
			if (attributes != null)
			{
				Iterator attributesIter = attributes.iterator();
				while (attributesIter.hasNext())
				{
					AttributeInterface attribute = (AttributeInterface) attributesIter.next();
					if ((attribute != null)
							&& (EntityManagerConstantsInterface.ID_ATTRIBUTE_NAME.equals(attribute
									.getName())))
					{
						return attribute;
					}
				}
			}
		}
		return null;
	}

	/**
	 * @param entityGroup
	 * @return
	 */
	public static ContainerInterface getMainContainer(EntityGroupInterface entityGroup)
	{
		if (entityGroup != null)
		{
			Collection<ContainerInterface> mainContainers = entityGroup
					.getMainContainerCollection();
			if (mainContainers != null)
			{
				Iterator mainContainerIter = mainContainers.iterator();
				if (mainContainerIter.hasNext())
				{
					//Return just the first main container
					ContainerInterface mainContainer = (ContainerInterface) mainContainerIter
							.next();
					//System.out.println("mainContainer " + mainContainer);
					return mainContainer;
				}
			}
		}
		return null;
	}

	/**
		 * This method will return the list of static entities which are associated with Entitygroup within Catissue
		 * It includes Participant,SCG or Specimen, either of it ,with which the model has been hooked.
		 * @param entityGroup
		 * @return
		 * @throws DAOException
		 * @throws DynamicExtensionsSystemException
		 * @throws DynamicExtensionsApplicationException
	 * @throws BizLogicException 
		 */
	public static List<String> addCatissueHookEntitiesToGroup(EntityGroupInterface entityGroup)
			throws DAOException, DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException, BizLogicException
	{

		Map<String, Object> returnstaticEntity = new HashMap<String, Object>();
		Collection<ContainerInterface> mainContainers = entityGroup.getMainContainerCollection();
		if (mainContainers != null)
		{
			AnnotationBizLogic annotationBizLogic = new AnnotationBizLogic();
			for (ContainerInterface mainContainer : mainContainers)
			{
				//System.out.println(mainContainer.getId());
				Collection entityMapsForContainer = annotationBizLogic
						.getEntityMapsForContainer(mainContainer.getId());

				if (entityMapsForContainer != null)
				{
					Iterator entityMapsForContainerIter = entityMapsForContainer.iterator();
					//Add all associated static entities to uml model 
					while (entityMapsForContainerIter.hasNext())
					{
						Object entityMap = entityMapsForContainerIter.next();

						if (entityMap != null)
						{
							Long staticEntityId = ((EntityMap) entityMap).getStaticEntityId();
							EntityInterface staticEntity = entityManager
									.getEntityByIdentifier(staticEntityId);
							if (staticEntity != null)
							{
								EntityInterface xmiStaticEntity = null;
								AssociationInterface association = getCatissueHookEntityAssociation(
										staticEntity, mainContainer.getAbstractEntity());
								Collection<EntityInterface> entityColl = entityGroup
										.getEntityCollection();
								for (EntityInterface entity : entityColl)
								{
									if (entity.getId().compareTo(staticEntity.getId()) == 0)
									{
										xmiStaticEntity = entity;

										break;
									}
								}

								if (xmiStaticEntity != null)
								{
									xmiStaticEntity.addAssociation(association);
								}
								else
								{

									xmiStaticEntity = getCatissueHookEntityDetailsForXMI(
											staticEntity, mainContainer.getAbstractEntity());
									xmiStaticEntity.setEntityGroup(entityGroup);
									entityGroup.addEntity(xmiStaticEntity);

								}
								returnstaticEntity.put(xmiStaticEntity.getName(), null);
							}

						}
					}
				}
			}
		}
		return new ArrayList<String>(returnstaticEntity.keySet());
	}

	/**
	 * @param srcEntity
	 * @param targetEntity
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private static AssociationInterface getCatissueHookEntityAssociation(EntityInterface srcEntity,
			AbstractEntityInterface targetEntity) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		if ((srcEntity != null) && (targetEntity != null))
		{
			Collection<AssociationInterface> associations = srcEntity.getAllAssociations();
			if (associations != null)
			{
				Iterator assocIter = associations.iterator();
				while (assocIter.hasNext())
				{
					AssociationInterface association = (AssociationInterface) assocIter.next();
					if (association.getTargetEntity().equals(targetEntity))
					{
						String srcEntityName = getHookEntityName(srcEntity.getName());
						//Change name of association 
						association
								.setName("Assoc_" + srcEntityName + "_" + targetEntity.getName());
						association.getSourceRole().setName(srcEntityName);
						association.getTargetRole().setName(targetEntity.getName() + "Collection");
						return association;
					}
				}

			}
		}
		return null;
	}

	/**
	 * @param staticEntity
	 * @return
	 * @throws DynamicExtensionsApplicationException 
	 * @throws DynamicExtensionsSystemException 
	 */
	public static EntityInterface getCatissueHookEntityDetailsForXMI(EntityInterface srcEntity,
			AbstractEntityInterface targetEntity) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		//For XMI : add only id , name and table properties
		EntityInterface xmiEntity = new Entity();
		xmiEntity.setName(getHookEntityName(srcEntity.getName()));
		xmiEntity.setDescription(srcEntity.getDescription());
		xmiEntity.setTableProperties(srcEntity.getTableProperties());
		xmiEntity.setId(srcEntity.getId());
		xmiEntity.addAttribute(getIdAttribute(srcEntity));
		xmiEntity.addAssociation(getCatissueHookEntityAssociation(srcEntity, targetEntity));

		return xmiEntity;
	}

}
