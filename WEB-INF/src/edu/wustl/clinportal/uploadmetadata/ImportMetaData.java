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
 * Created on Sep 15, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package edu.wustl.clinportal.uploadmetadata;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Subclass;

import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.TaggedValue;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.DynamicExtensionBaseDomainObjectInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.TaggedValueInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ColumnPropertiesInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ConstraintPropertiesInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.TablePropertiesInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.global.DEConstants.Cardinality;
import edu.common.dynamicextensions.util.global.DEConstants.InheritanceStrategy;
import edu.wustl.common.util.Utility;
import edu.wustl.dao.DAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;

/**
 * @author rukhsana_sammer
 *
 * This class reads the meta data i.e. the database properties from the hbm files
 * and updates the database & generate the updated information log in "ImportLog.txt" file. 
 * 
 */
public class ImportMetaData
{

	private static Configuration cfg;
	/**
	 * 
	 */
	static private BufferedWriter writer = null;

	/**
	 * This map will contain all entities.
	 */
	static Map<String, EntityInterface> entityMap;
	/**
	 * This list will contain all objects like TablePropertiesInterface, ConstraintPropertiesInterface, 
	 * ColumnPropertiesInterface. that will be persisted in db.
	 */
	static List<DynamicExtensionBaseDomainObjectInterface> objectList = null;
	/**
	 * List of entities whose parent needs to be set as null.
	 */
	static List<EntityInterface> parentNullEntityList = null;
	/**
	 * 
	 */
	static Map<String, String> parentMap = null;
	/**
	 * The list of entities whose inheritance related information to be updated.
	 */
	static List<EntityInterface> inheritanceEntityList = null;

	/**
	 * @param hibConfigFileName
	 * @param importLogFileName
	 */
	public static void initHibernateMetaData(String hibConfigFileName, String importLogFileName)
	{
		try
		{
			File configFile = new File(hibConfigFileName);
			cfg = new Configuration().configure(configFile);
			cfg.buildSessionFactory();
			writer = new BufferedWriter(new FileWriter(importLogFileName));
			importDatabaseMetadata(importLogFileName);
			writer.flush();
			writer.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * To create map of all entities.
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private static void createMap() throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		Collection<EntityInterface> allEntities = EntityManager.getInstance().getAllEntities();
		entityMap = new HashMap<String, EntityInterface>();
		System.out.println("List of All Entities:");
		for (EntityInterface entity : allEntities)
		{
			entityMap.put(entity.getName(), entity);
			System.out.println("-" + entity.getName());
		}
		objectList = new ArrayList<DynamicExtensionBaseDomainObjectInterface>();
		parentNullEntityList = new ArrayList<EntityInterface>();
		parentMap = new HashMap<String, String>();
		inheritanceEntityList = new ArrayList<EntityInterface>();
	}

	/**
	 * To save all the data that is to be modified.
	 * It will save all data i.e.:
	 * - All Database properties, constraint properties & table properties objects.
	 * - saving parent ids null for required entities.
	 * - saving inheritance related information such as: inheritance strategy, discriminatory column 
	 * & discriminatory  value.
	 * @throws IOException
	 */
	private static void saveAll() throws IOException
	{
		// Save database properties, constraint properties & table properties using Hibernate dao.
		//AbstractDAO dao = DAOFactory.getInstance().getDAO(Constants.HIBERNATE_DAO);
		IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory("dynamicExtention");
		DAO dao = null;
		writer
				.write("\nSaving database properties & constraint properties & table properties.....");
		try
		{
			dao = daoFactory.getDAO();
			dao.openSession(null);
			for (DynamicExtensionBaseDomainObjectInterface obj : objectList)
			{
				dao.update(obj);
			}
			dao.commit();
		}
		catch (Exception ex)
		{
			throw new RuntimeException("error in saving object!!!!", ex);
		}
		finally
		{
			try
			{
				dao.closeSession();
			}
			catch (DAOException daoEx)
			{
				throw new RuntimeException("error in saving object!!!!", daoEx);
			}
		}
		// using JDBC for further updates.
		//JDBCDAO jdbcDao = new JDBCDAOImpl();
		JDBCDAO jdbcDao = null;
		try
		{
			jdbcDao = daoFactory.getJDBCDAO();
			jdbcDao.openSession(null);
			List<TaggedValueInterface> derivedTags = new ArrayList<TaggedValueInterface>();
			// setting parent ids null for the required entities.
			writer.write("\nSaving Parent ids null.....");
			Set<String> keySet = parentMap.keySet();
			for (String entityName : keySet)
			{
				EntityInterface entity = entityMap.get(entityName);
				String parentName = parentMap.get(entityName);
				String sql = "update DYEXTN_ENTITY set PARENT_ENTITY_ID = ";
				if (parentName == null)
				{
					sql += " null ";
					Collection<AttributeInterface> attrColln = entity.getAttributeCollection();
					for (AttributeInterface attribute : attrColln)
					{
						for (TaggedValueInterface tag : attribute.getTaggedValueCollection())
						{
							if (tag.getKey().equals(
									edu.wustl.cab2b.common.util.Constants.TYPE_DERIVED))
							{
								derivedTags.add(tag);
							}
						}
					}
				}
				else
				{
					EntityInterface parentEntity = entityMap.get(parentName);
					sql += " " + parentEntity.getId() + " ";
				}
				sql += " where IDENTIFIER = " + entity.getId();
				writer.write(sql);
				jdbcDao.executeUpdate(sql);
			}
			writer.write("\nRemoving tagged values.....");
			for (TaggedValueInterface tag : derivedTags)
			{
				String sql = "delete from DYEXTN_TAGGED_VALUE where IDENTIFIER = "
						+ ((TaggedValue) tag).getId();
				writer.write("\n" + sql);
				jdbcDao.executeUpdate(sql);
			}

			// updating inheritance data.
			writer.write("\nSaving Inheritance related information.....");
			for (EntityInterface entity : inheritanceEntityList)
			{
				StringBuffer buffer = new StringBuffer("update DYEXTN_ENTITY set ");
				buffer.append("INHERITANCE_STRATEGY = "
						+ entity.getInheritanceStrategy().getValue());
				buffer.append(", DISCRIMINATOR_COLUMN_NAME = ");
				if (entity.getDiscriminatorColumn() == null)
				{
					buffer.append("null");
				}
				else
				{
					buffer.append("'" + entity.getDiscriminatorColumn() + "'");
				}

				buffer.append(", DISCRIMINATOR_VALUE = ");
				if (entity.getDiscriminatorValue() == null)
				{
					buffer.append("null");
				}
				else
				{
					buffer.append("'" + entity.getDiscriminatorValue() + "'");
				}

				buffer.append(" where IDENTIFIER = " + entity.getId());
				writer.write("\nSQL:" + buffer.toString());
				jdbcDao.executeUpdate(buffer.toString());
			}
			jdbcDao.commit();
		}
		catch (Exception ex)
		{
			throw new RuntimeException("error in saving object!!!!", ex);
		}
		finally
		{
			try
			{
				jdbcDao.closeSession();
			}
			catch (DAOException daoEx)
			{
				throw new RuntimeException("error in saving object!!!!", daoEx);
			}
		}
	}

	/**
	 * 
	 * @param entityNew
	 * @param perClass
	 * @param isDiscriminator
	 * @param factory
	 * 
	 * This method updates the inheritance meta data.
	 * For now it is assumed that we use only two inheritance strategy of
	 * the three inheritance strategies available to us.
	 * @throws IOException 
	 */

	public static void importInheritanceData(EntityInterface entityNew, PersistentClass perClass,
			boolean isDiscriminator, DomainObjectFactory factory) throws IOException
	{
		try
		{
			List subclassList = new ArrayList<Subclass>();
			subclassList = getSubClassList(entityNew.getName());
			String discriColumnName = null;
			if (isDiscriminator)
			{
				Iterator colItr = perClass.getDiscriminator().getColumnIterator();
				while (colItr.hasNext())
				{
					Column col = (Column) colItr.next();
					discriColumnName = col.getName();
				}
			}
			Iterator<Subclass> itr = subclassList.iterator();
			Subclass subClass = null;
			EntityInterface subEntity = null;
			while (itr.hasNext())
			{
				subClass = itr.next();
				subEntity = entityMap.get(subClass.getClassName());
				if (subEntity == null)
				{
					System.out.println("Entity Not Found:" + subClass.getClassName());
					continue;
				}
				inheritanceEntityList.add(subEntity);
				if (isDiscriminator)
				{
					//Setting the discriminatory values in the sub entities.
					String discrtorValue = subClass.getDiscriminatorValue();
					// subEntity = entityManagerInterface.getEntityByName(subClass.getName());
					subEntity.setInheritanceStrategy(InheritanceStrategy.TABLE_PER_HEIRARCHY);
					subEntity.setDiscriminatorValue(discrtorValue);
					subEntity.setDiscriminatorColumn(discriColumnName);
					writer.write("\nInheritance change for Entity:" + subEntity.getId() + ":"
							+ subEntity.getName());
				}
				else
				{
					TablePropertiesInterface tblInter = subEntity.getTableProperties();
					tblInter.setName(subClass.getTable().getName());
					subEntity.setInheritanceStrategy(InheritanceStrategy.TABLE_PER_SUB_CLASS);
					subEntity.setTableProperties(tblInter);
					writer.write("\nInheritance change for Entity:" + subEntity.getId() + ":"
							+ subEntity.getName());
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			writer.write(e.getMessage());
		}
	}

	/**
	 * This method returns the list of subclasses of the className
	 * @author aarti_sharma
	 * @param className
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static List getSubClassList(String className) throws ClassNotFoundException
	{
		List list = new ArrayList();

		Iterator itr = cfg.getClassMapping(className).getDirectSubclasses();
		while (itr.hasNext())
		{
			Subclass subClass = (Subclass) itr.next();
			list.add(subClass);
		}
		return list;
	}

	/**
	 * This method iterates through all the classes and uploads the metadata 
	 * related to all those classes.
	 * @param importLogFileName
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 * @throws IOException
	 */
	public static void importDatabaseMetadata(String importLogFileName)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException,
			IOException
	{
		PersistentClass perClass = null;

		DomainObjectFactory factory = DomainObjectFactory.getInstance();

		createMap();

		Iterator classIter = cfg.getClassMappings();

		List<PersistentClass> lstOfOneToOneClass = new ArrayList<PersistentClass>();
		List<PersistentClass> lstOfOneSidAsnClass = new ArrayList<PersistentClass>();
		List<PersistentClass> lstOfNoPrptColClass = new ArrayList<PersistentClass>();
		Map<AssociationInterface, AssociationInterface> specCaseAssonMap = new HashMap<AssociationInterface, AssociationInterface>();
		List<String> list1 = new ArrayList<String>();

		while (classIter.hasNext())
		{
			perClass = (PersistentClass) classIter.next();
			EntityInterface entity;
			entity = entityMap.get(perClass.getClassName());

			if (entity != null)
			{
				writer.write("\n-------------------------------");

				//Setting the table name				entity.get
				TablePropertiesInterface tblProperties = entity.getTableProperties();
				tblProperties.setName(perClass.getTable().getName());
				//					entity.setTableProperties(tblProperties);

				writer.write("\nEntity:" + entity.getId() + ":" + entity.getName() + ":"
						+ entity.getTableProperties().getName());

				objectList.add(tblProperties);
				//Setting the attribute names				
				AbstractAttributeInterface att = null;
				Iterator attrInter = entity.getAbstractAttributeCollection().iterator();

				while (attrInter.hasNext())
				{
					AttributeInterface attribute = null;
					AssociationInterface assInter = null;
					try
					{
						att = (AbstractAttributeInterface) attrInter.next();
						if (att != null)
						{
							if (att instanceof AssociationInterface)
							{
								attribute = null;
								assInter = (AssociationInterface) att;
								Cardinality card = assInter.getSourceRole().getMaximumCardinality();

								Cardinality targetcard = assInter.getTargetRole()
										.getMaximumCardinality();
								Property prop = perClass.getProperty(assInter.getTargetRole()
										.getName());
								Iterator colIter;
								if (prop.isComposite())
								{
									colIter = prop.getValue().getColumnIterator();
								}
								else
								{
									colIter = prop.getColumnIterator();
								}

								ConstraintPropertiesInterface constPropties = assInter
										.getConstraintProperties();
								if (!colIter.hasNext())
								{
									lstOfNoPrptColClass.add(perClass);
								}
								objectList.add(constPropties);
								while (colIter.hasNext())
								{
									Column column = (Column) colIter.next();
									//Find the type of associations
									if ((card == Cardinality.MANY)
											&& (targetcard == Cardinality.ONE))
									{
										constPropties.getSrcEntityConstraintKeyProperties().getTgtForiegnKeyColumnProperties().setName(column.getName());
										constPropties.getTgtEntityConstraintKeyProperties().getTgtForiegnKeyColumnProperties().setName(null);
									}

									else if ((card == Cardinality.ONE)
											&& (targetcard == Cardinality.ONE))
									{
										constPropties.getTgtEntityConstraintKeyProperties().getTgtForiegnKeyColumnProperties().setName(column.getName());
										if (assInter.getTargetEntity().getAssociationCollection()
												.isEmpty())
										{
											lstOfOneSidAsnClass.add(perClass);
											addMappingsForOneSideAssociationCases(perClass);
										}
										AssociationInterface oppAssociation = getOppositAssociation(assInter);

										Iterator<AssociationInterface> iterator = assInter
												.getTargetEntity().getAssociationCollection()
												.iterator();
										while (iterator.hasNext())
										{
											AssociationInterface element = (AssociationInterface) iterator
													.next();
											Cardinality revSourceCard = element.getSourceRole()
													.getMaximumCardinality();
											Cardinality revTargetcard = element.getTargetRole()
													.getMaximumCardinality();
											if (revSourceCard == Cardinality.ONE
													&& revTargetcard == Cardinality.ONE)
											{
												if (oppAssociation == null)
												{
													String str = " OPPOSITE   "
															+ assInter.getEntity().getName()
															+ " ----"
															+ assInter.getTargetEntity().getName();
													list1.add(str);
													constPropties.getSrcEntityConstraintKeyProperties().getTgtForiegnKeyColumnProperties().setName(column
															.getName());
													constPropties.getTgtEntityConstraintKeyProperties().getTgtForiegnKeyColumnProperties().setName(null);
												}
												else
												{
													lstOfOneToOneClass.add(perClass);
													addMappingsForOneToOneCases(perClass);

													specCaseAssonMap.put(element, assInter);
												}
											}
										}
									}
								}

								if ((card == Cardinality.ONE) && (targetcard == Cardinality.MANY))
								{
									String roleName = perClass.getClassName() + "."
											+ prop.getName();
									org.hibernate.mapping.Collection col = cfg
											.getCollectionMapping(roleName);
									if (col == null)
									{
										writer
												.write("\nError::::::roleName not found: "
														+ roleName);
									}
									else
									{
										Iterator keyIter = col.getKey().getColumnIterator();

										while (keyIter.hasNext())
										{
											Column column = (Column) keyIter.next();
											constPropties.getTgtEntityConstraintKeyProperties().getTgtForiegnKeyColumnProperties().setName(column.getName());
											constPropties.getSrcEntityConstraintKeyProperties().getTgtForiegnKeyColumnProperties().setName(null);
										}
									}
								}
								else if ((card == Cardinality.MANY)
										&& (targetcard == Cardinality.MANY))
								{

									String roleName = perClass.getClassName() + "."
											+ prop.getName();
									org.hibernate.mapping.Collection col = cfg
											.getCollectionMapping(roleName);

									if (col == null)
									{
										writer
												.write("\nError::::::roleName not found: "
														+ roleName);
									}
									else
									{
										Iterator keyIter = col.getKey().getColumnIterator();

										while (keyIter.hasNext())
										{

											Column column = (Column) keyIter.next();
											constPropties.getSrcEntityConstraintKeyProperties().getTgtForiegnKeyColumnProperties().setName(column.getName());

										}

										Iterator keyIter1 = col.getElement().getColumnIterator();

										while (keyIter1.hasNext())
										{
											Column column = (Column) keyIter1.next();
											constPropties.getTgtEntityConstraintKeyProperties().getTgtForiegnKeyColumnProperties().setName(column.getName());

										}
										//Storing the mapping table name										
										constPropties.setName(col.getCollectionTable().getName());

									}
								}
							}
							else
							{
								attribute = (AttributeInterface) att;
								if (!perClass.getIdentifierProperty().getName().equals(
										attribute.getName()))
								{
									Property prop = perClass.getProperty(attribute.getName());

									Iterator colIter = prop.getColumnIterator();
									while (colIter.hasNext())
									{
										Column column = (Column) colIter.next();
										ColumnPropertiesInterface colProp = ((AttributeInterface) att)
												.getColumnProperties();
										objectList.add(colProp);
										colProp.setName(column.getName());

									}

								}//end of else

								if (perClass.getIdentifierProperty().getName().equals(
										attribute.getName()))
								{
									((AttributeInterface) att).setIsPrimaryKey(true);
									Iterator colIter = perClass.getIdentifier().getColumnIterator();
									while (colIter.hasNext())
									{
										Column column = (Column) colIter.next();
										ColumnPropertiesInterface colProp = ((AttributeInterface) att)
												.getColumnProperties();
										objectList.add(colProp);
										colProp.setName(column.getName());
									}

								}
								writer.write("\nAttribute:" + attribute.getId() + ":"
										+ attribute.getName() + ":"
										+ attribute.getColumnProperties().getName());
							}
						}//end of if
					}//end of try block
					catch (Exception e)
					{
						if (attribute == null)
						{
							System.out.println("Error in Association\t"

							+ assInter.getEntity().getName() + "\t"
									+ assInter.getTargetEntity().getName() + "\t("
									+ assInter.getSourceRole().getName() + ":"
									+ assInter.getTargetRole().getName() + ")");
						}
						else
						{
							System.out.println("Error in Attribute\t"
									+ attribute.getEntity().getName() + "." + attribute.getName());
						}
					}
				}
				//					entity = entityManagerInterface.persistEntityMetadata(entity,true);			
			}
		}//end of outside while loop

		addMappingsForSpecialCases(specCaseAssonMap);
		//Entering Inheritance meta data
		System.out
				.println("------------------------------------Done Attribute list-----------------------------------"
						+ objectList.size());
		classIter = cfg.getClassMappings();
		EntityInterface entityNew = null;
		while (classIter.hasNext())
		{

			perClass = (PersistentClass) classIter.next();
			//					entityNew = entityManagerInterface.getEntityByName(perClass.getName());	
			entityNew = entityMap.get(perClass.getClassName());
			if (entityNew != null)
			{
				Class classObj;
				try
				{
					classObj = Class.forName(entityNew.getName());
					System.out.println("class object------------------------" + classObj.toString()
							+ "--" + cfg.getClassMapping(entityNew.getName()));
				}
				catch (ClassNotFoundException e)
				{

					e.printStackTrace();
				}

				//TODO check methods inside it for my change 
				if (perClass.getDiscriminator() != null)
				{
					importInheritanceData(entityNew, perClass, true, factory);
				}
				else
				{
					importInheritanceData(entityNew, perClass, false, factory);
				}
			}

		}

		System.out
				.println("-------------------------------Done Inheritance Metadata------------------------");

		classIter = cfg.getClassMappings();
		writer.write("\nProcessing entity for Inheritance.....................................");
		Map<String, PersistentClass> perClassMap = new HashMap<String, PersistentClass>();
		while (classIter.hasNext())
		{
			perClass = (PersistentClass) classIter.next();
			perClassMap.put(perClass.getClassName(), perClass);
		}
		Set<String> keySet = entityMap.keySet();
		for (String key : keySet)
		{
			perClass = perClassMap.get(key);
			if (perClass != null)
			{
				PersistentClass parentPerClass = perClass.getSuperclass();
				parentMap.put(key, parentPerClass == null ? null : parentPerClass.getClassName());
			}
		}
		writer.write("\nProcessing Done.......................................");
		writer.write("\nSaving all Data.........");
		saveAll();
		System.out.println("Done.........................................See " + importLogFileName
				+ " for more details.");
	}

	/**
	 * 
	 * @param speCaseAssonMap
	 */

	private static void addMappingsForSpecialCases(
			Map<AssociationInterface, AssociationInterface> speCaseAssonMap)
	{
		Set<AssociationInterface> keySet = speCaseAssonMap.keySet();
		Iterator<AssociationInterface> iterator = keySet.iterator();
		while (iterator.hasNext())
		{
			AssociationInterface association = (AssociationInterface) iterator.next();
			AssociationInterface AnotherAsson = speCaseAssonMap.get(association);
			ConstraintPropertiesInterface firstContPropties = association.getConstraintProperties();
			ConstraintPropertiesInterface secndConstPrpties = AnotherAsson
					.getConstraintProperties();
			firstContPropties.getSrcEntityConstraintKeyProperties().getTgtForiegnKeyColumnProperties().setName(null);
			firstContPropties.getTgtEntityConstraintKeyProperties().getTgtForiegnKeyColumnProperties().setName(secndConstPrpties.getSrcEntityConstraintKeyProperties().getTgtForiegnKeyColumnProperties().getName());
		}
	}

	/**
	 * @param perClass
	 */
	private static void addMappingsForOneToOneCases(PersistentClass perClass)
	{
		List<String> specCaseEntities = new ArrayList<String>();
		EntityInterface entity = entityMap.get(perClass.getClassName());
		if (entity != null)
		{
			Iterator attributeInter = entity.getAbstractAttributeCollection().iterator();
			AbstractAttributeInterface att = null;
			while (attributeInter.hasNext())
			{
				AssociationInterface association = null;
				try
				{
					att = (AbstractAttributeInterface) attributeInter.next();
					if (att != null)
					{
						if (att instanceof AssociationInterface)
						{
							association = (AssociationInterface) att;
							Cardinality card = association.getSourceRole().getMaximumCardinality();
							Cardinality targetcard = association.getTargetRole()
									.getMaximumCardinality();
							if ((card == Cardinality.ONE) && (targetcard == Cardinality.ONE))
							{
								Iterator<AssociationInterface> iterator1 = association
										.getTargetEntity().getAssociationCollection().iterator();
								while (iterator1.hasNext())
								{
									AssociationInterface element = (AssociationInterface) iterator1
											.next();
									Cardinality revSourceCard = element.getSourceRole()
											.getMaximumCardinality();
									Cardinality revTargetcard = element.getTargetRole()
											.getMaximumCardinality();
									if (revSourceCard == Cardinality.ONE
											&& revTargetcard == Cardinality.ONE)
									{
										String name = element.getSourceRole().getName();
										name = element.getTargetRole().getName();

										if (Utility.parseClassName(entity.getName())
												.equalsIgnoreCase(name))
										{
											Property prop = perClass.getProperty(association
													.getTargetRole().getName());
											Iterator colIter1;
											if (prop.isComposite())
											{
												colIter1 = prop.getValue().getColumnIterator();
											}
											else
											{
												colIter1 = prop.getColumnIterator();
											}

											ConstraintPropertiesInterface constPropties1 = association
													.getConstraintProperties();
											while (colIter1.hasNext())
											{
												Column column = (Column) colIter1.next();

												constPropties1.getSrcEntityConstraintKeyProperties().getTgtForiegnKeyColumnProperties().setName(column.getName());
												constPropties1.getTgtEntityConstraintKeyProperties().getTgtForiegnKeyColumnProperties().setName(null);
												specCaseEntities.add("Assocaitions"
														+ association.getId() + "_____"
														+ entity.getName());
											}
										}
									}
								}
							}
						}
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @param perClass
	 */
	private static void addMappingsForOneSideAssociationCases(PersistentClass perClass)
	{
		List<String> specCaseEntities = new ArrayList<String>();
		EntityInterface objEntity = entityMap.get(perClass.getClassName());
		if (objEntity != null)
		{
			Iterator attriInter = objEntity.getAbstractAttributeCollection().iterator();
			AbstractAttributeInterface attribute = null;
			while (attriInter.hasNext())
			{
				AssociationInterface association = null;
				try
				{
					attribute = (AbstractAttributeInterface) attriInter.next();
					if (attribute != null)
					{
						if (attribute instanceof AssociationInterface)
						{
							association = (AssociationInterface) attribute;
							Cardinality cardn = association.getSourceRole().getMaximumCardinality();
							Cardinality targetcard = association.getTargetRole()
									.getMaximumCardinality();
							if ((cardn == Cardinality.ONE) && (targetcard == Cardinality.ONE))
							{
								ConstraintPropertiesInterface constrProperts = association
										.getConstraintProperties();
								Property prop = perClass.getProperty(association.getTargetRole()
										.getName());
								Iterator columnItr;
								if (prop.isComposite())
								{
									columnItr = prop.getValue().getColumnIterator();
								}
								else
								{
									columnItr = prop.getColumnIterator();
								}
								while (columnItr.hasNext())
								{
									Column column = (Column) columnItr.next();
									constrProperts.getSrcEntityConstraintKeyProperties().getTgtForiegnKeyColumnProperties().setName(column.getName());
									constrProperts.getTgtEntityConstraintKeyProperties().getTgtForiegnKeyColumnProperties().setName(null);
								}
								specCaseEntities.add("MappingsForOneSideAssociationCases"
										+ association.getId() + "_____" + objEntity.getName());
							}
						}
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String hibConfigFileName = ".\\src\\hibernate.cfg.xml";
		String logFileName = "ImportLog.txt";
		if (args.length > 1)
		{
			hibConfigFileName = args[0];
			logFileName = args[1];
		}
		//Logger.configure();
		ImportMetaData.initHibernateMetaData(hibConfigFileName, logFileName);
	}

	/**
	 * @param association
	 * @return
	 */
	public static AssociationInterface getOppositAssociation(AssociationInterface association)
	{
		EntityInterface entity = association.getEntity();
		EntityInterface targetEntity = association.getTargetEntity();

		Collection<AssociationInterface> assonCollection = targetEntity.getAssociationCollection();
		for (AssociationInterface ass : assonCollection)
		{
			if (ass.getTargetEntity().equals(entity))
			{
				if (isStringEqual(association.getSourceRole().getName(), ass.getTargetRole()
						.getName())
						&& isStringEqual(ass.getSourceRole().getName(), association.getTargetRole()
								.getName()))
				{
					return ass;
				}
			}
		}
		return null;
	}

	/**
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static boolean isStringEqual(String str1, String str2)
	{
		boolean flag = false;
		if (str1 != null)
		{
			flag = str1.equals(str2);
		}
		else if (str2 == null)
		{
			flag = true;
		}

		return flag;
	}
}