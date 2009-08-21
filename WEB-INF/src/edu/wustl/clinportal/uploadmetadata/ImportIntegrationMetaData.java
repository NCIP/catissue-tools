/*
 * Created on Sep 15, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package edu.wustl.clinportal.uploadmetadata;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

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
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.TaggedValueInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ColumnPropertiesInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ConstraintPropertiesInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.TablePropertiesInterface;
import edu.common.dynamicextensions.entitymanager.EntityGroupManager;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationType;
import edu.common.dynamicextensions.util.global.DEConstants.Cardinality;
import edu.common.dynamicextensions.util.global.DEConstants.InheritanceStrategy;
import edu.wustl.clinportal.action.annotations.AnnotationConstants;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.dao.DAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;

/**
 * @author falguni sachde
 *
 * This class reads the meta data i.e. the database properties from the Catissue's hbm files
 * and updates the database & generate the updated information log in "ImportLog.txt" file. 
 * 
 */
public class ImportIntegrationMetaData
{

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}
	private transient static Logger LOGGER = Logger
			.getCommonLogger(ImportIntegrationMetaData.class);

	private static Configuration cfg;
	static private BufferedWriter writter = null;

	/**
	 * @param hibConfigFileName
	 * @param importLogFileName
	 * @param contAssnFileName 
	 */
	public static void initHibernateMetaData(String hibConfigFileName, String importLogFileName,
			String idfAttrFileName, String contAssnFileName)
	{
		try
		{
			File configFile = new File(hibConfigFileName);
			cfg = new Configuration().configure(configFile);
			cfg.buildSessionFactory();
			writter = new BufferedWriter(new FileWriter(importLogFileName));
			importDatabaseMetadata(importLogFileName, idfAttrFileName, contAssnFileName);
			writter.flush();
			writter.close();
		}
		catch (Exception e)
		{
			LOGGER.info("--initHibernateMetaData Task throws error." + e.getMessage());
		}
	}

	/**
	 * This map will contain all entities.
	 */
	static Map<String, EntityInterface> entityMap;

	static Map<Long, String> constraintMap;
	/**
	 * This list will contain all objects like TablePropertiesInterface, ConstraintPropertiesInterface, ColumnPropertiesInterface. that will be persisted in db.
	 */
	static List<DynamicExtensionBaseDomainObjectInterface> objectList = null;
	/**
	 * List of entities whose parent needs to be set as null.
	 */
	static List<EntityInterface> parentNullEntityList = null;
	static Map<String, String> parentMap = null;
	/**
	 * The list of entities whose inheritance related information to be updated.
	 */
	static List<EntityInterface> inheritanceEntityList = null;

	static HashMap<String, List<String>> entityAssnMap = null;

	/**
	 * To create map of all entities.
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private static void createMap() throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		EntityGroupInterface egroup = EntityGroupManager.getInstance().getEntityGroupByName(
				AnnotationConstants.CAT_GROUPNAME_IN_CP);

		Collection<EntityInterface> allEntities = egroup.getEntityCollection();
		entityMap = new HashMap<String, EntityInterface>();
		for (EntityInterface entity : allEntities)
		{
			entityMap.put(entity.getName(), entity);
		}
		objectList = new ArrayList<DynamicExtensionBaseDomainObjectInterface>();
		parentNullEntityList = new ArrayList<EntityInterface>();
		parentMap = new HashMap<String, String>();
		inheritanceEntityList = new ArrayList<EntityInterface>();
		constraintMap = new HashMap<Long, String>();
	}

	/**
	 * To save all the data that is to be modified.
	 * It will save all data i.e.:
	 * - All Database properties, constraint properties & table properties objects.
	 * - saving parent ids null for required entities.
	 * - saving inheritance related information such as: inheritance strategy, discriminatory column & discriminatory  value.
	 * @throws IOException
	 */
	private static void saveAll() throws IOException
	{
		// Save database properties, constraint properties & table properties using Hibernate dao.

		IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory("dynamicExtention");
		DAO dao = null;

		writter
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
			dao.closeSession();
		}
		catch (Exception ex)
		{
			throw new RuntimeException("error in saving object!!!!", ex);
		}
		finally
		{

		}
		// using JDBC for further updates.
		JDBCDAO jdbcDao = null;

		try
		{
			jdbcDao = daoFactory.getJDBCDAO();
			jdbcDao.openSession(null);

			List<TaggedValueInterface> derivedTags = new ArrayList<TaggedValueInterface>();
			// setting parent ids null for the required entities.
			writter.write("\nSaving Parent ids null.....");
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
				writter.write(sql);
				jdbcDao.executeUpdate(sql);
			}
			writter.write("\nRemoving tagged values.....");
			for (TaggedValueInterface tag : derivedTags)
			{
				String sql = "delete from DYEXTN_TAGGED_VALUE where IDENTIFIER = "
						+ ((TaggedValue) tag).getId();
				writter.write("\n" + sql);
				jdbcDao.executeUpdate(sql);
			}

			// updating inheritance data.
			writter.write("\nSaving Inheritance related information.....");
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
					buffer.append("null");
				else
					buffer.append("'" + entity.getDiscriminatorValue() + "'");

				buffer.append(" where IDENTIFIER = " + entity.getId());
				writter.write("\nSQL:" + buffer.toString());
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
					subEntity.setInheritanceStrategy(InheritanceStrategy.TABLE_PER_HEIRARCHY);
					subEntity.setDiscriminatorValue(discrtorValue);
					subEntity.setDiscriminatorColumn(discriColumnName);
					writter.write("\nInheritance change for Entity:" + subEntity.getId() + ":"
							+ subEntity.getName());
				}
				else
				{
					TablePropertiesInterface tblInter = subEntity.getTableProperties();
					tblInter.setName(subClass.getTable().getName());
					//System.out.println("table name ===" + tblInter.getName());
					subEntity.setInheritanceStrategy(InheritanceStrategy.TABLE_PER_SUB_CLASS);
					subEntity.setTableProperties(tblInter);
					writter.write("\nInheritance change for Entity:" + subEntity.getId() + ":"
							+ subEntity.getName());
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.info("--initHibernateMetaData Task throws error." + e.getMessage());
			writter.write(e.getMessage());
		}
	}

	/**
	 * This method returns the list of subclasses of the className
	 * 
	 * @param className
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static List getSubClassList(String className) throws ClassNotFoundException
	{
		List list = new ArrayList();

		Iterator it = cfg.getClassMapping(className).getDirectSubclasses();
		while (it.hasNext())
		{
			Subclass subClass = (Subclass) it.next();
			list.add(subClass);
		}
		return list;
	}

	/**
	 * This method iterates through all the classes and uploads the metadata 
	 * related to all those classes.
	 * @param idfAttrFileName 
	 * @param contAssnFileName 
	 * @throws DynamicExtensionsApplicationException 
	 * @throws DynamicExtensionsSystemException 
	 * @throws IOException 
	 *
	 */
	public static void importDatabaseMetadata(String importLogFileName, String idfAttrFileName,
			String contAssnFileName) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException, IOException
	{
		PersistentClass perClass = null;

		DomainObjectFactory factory = DomainObjectFactory.getInstance();

		createMap();
		readAssociationFile(contAssnFileName);

		Iterator classIter = cfg.getClassMappings();

		List<PersistentClass> lstOfOneToOneClas = new ArrayList<PersistentClass>();
		List<PersistentClass> lstOfOneSidAsnCls = new ArrayList<PersistentClass>();
		List<PersistentClass> lstOfNoPrptColCls = new ArrayList<PersistentClass>();
		Map<AssociationInterface, AssociationInterface> specCaseAssonMap = new HashMap<AssociationInterface, AssociationInterface>();
		List<String> list1 = new ArrayList<String>();

		while (classIter.hasNext())
		{
			perClass = (PersistentClass) classIter.next();
			EntityInterface entity;
			entity = entityMap.get(perClass.getClassName());

			if (entity != null)
			{
				writter.write("\n-------------------------------");

				//Setting the table name			
				TablePropertiesInterface tblProperties = entity.getTableProperties();
				tblProperties.setName(perClass.getTable().getName());
				writter.write("\nEntity:" + entity.getId() + ":" + entity.getName() + ":"
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
									lstOfNoPrptColCls.add(perClass);
								}
								objectList.add(constPropties);
								while (colIter.hasNext())
								{
									Column column = (Column) colIter.next();
									//Find the type of associations
									if ((card == Cardinality.MANY)
											&& (targetcard == Cardinality.ONE))
									{
										constraintMap.put(constPropties.getId(), column.getName());
										constPropties.getSrcEntityConstraintKeyProperties()
												.getTgtForiegnKeyColumnProperties().setName(
														column.getName());

									}
									else if ((card == Cardinality.ONE)
											&& (targetcard == Cardinality.ONE))
									{
										constraintMap.put(constPropties.getId(), column.getName());
										if (constPropties.getTgtEntityConstraintKeyProperties() == null)
										{
											constPropties.getSrcEntityConstraintKeyProperties()
													.getTgtForiegnKeyColumnProperties().setName(
															column.getName());
										}
										else
										{
											constPropties.getTgtEntityConstraintKeyProperties()
													.getTgtForiegnKeyColumnProperties().setName(
															column.getName());
										}
										if (assInter.getTargetEntity().getAssociationCollection()
												.isEmpty())
										{
											lstOfOneSidAsnCls.add(perClass);
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
													String a = " OPPOSITE   "
															+ assInter.getEntity().getName()
															+ " ----"
															+ assInter.getTargetEntity().getName();
													list1.add(a);
													constraintMap.put(constPropties.getId(), column
															.getName());
													constPropties
															.getTgtEntityConstraintKeyProperties()
															.getTgtForiegnKeyColumnProperties()
															.setName(column.getName());
												}
												else
												{
													lstOfOneToOneClas.add(perClass);
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
										writter.write("\nError::::::roleName not found: "
												+ roleName);
									}
									else
									{
										Iterator keyIter = col.getKey().getColumnIterator();
										while (keyIter.hasNext())
										{
											Column column = (Column) keyIter.next();
											constraintMap.put(constPropties.getId(), column
													.getName());
											constPropties.getTgtEntityConstraintKeyProperties()
													.getTgtForiegnKeyColumnProperties().setName(
															column.getName());

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
										writter.write("\nError::::::roleName not found: "
												+ roleName);
									}
									else
									{
										Iterator keyIter = col.getKey().getColumnIterator();

										while (keyIter.hasNext())
										{

											Column column = (Column) keyIter.next();
											constraintMap.put(constPropties.getId(), column
													.getName());
											constPropties.getSrcEntityConstraintKeyProperties()
													.getTgtForiegnKeyColumnProperties().setName(
															column.getName());

										}

										Iterator keyIter1 = col.getElement().getColumnIterator();

										while (keyIter1.hasNext())
										{
											Column column = (Column) keyIter1.next();
											constraintMap.put(constPropties.getId(), column
													.getName());
											constPropties.getTgtEntityConstraintKeyProperties()
													.getTgtForiegnKeyColumnProperties().setName(
															column.getName());
										}
										//Storing the mapping table name										
										constPropties.setName(col.getCollectionTable().getName());

									}
								}
								if (contAssnFileName != null)
								{
									setContainmentAssociation(assInter, entity);
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
								writter.write("\nAttribute:" + attribute.getId() + ":"
										+ attribute.getName() + ":"
										+ attribute.getColumnProperties().getName());
							}
						}//end of if
					}//end of try block
					catch (Exception e)
					{
						if (attribute == null)
						{
							writter.write("Error in Association\t" + assInter.getEntity().getName()
									+ "\t" + assInter.getTargetEntity().getName() + "\t("
									+ assInter.getSourceRole().getName() + ":"
									+ assInter.getTargetRole().getName() + ")");
						}
						else
						{
							writter.write("Error in Attribute\t" + attribute.getEntity().getName()
									+ "." + attribute.getName());
						}
					}
				}

			}
		}//end of outside while loop

		addMappingsForSpecialCases(specCaseAssonMap);
		//Entering Inheritance meta data

		classIter = cfg.getClassMappings();
		EntityInterface entityNew = null;
		while (classIter.hasNext())
		{

			perClass = (PersistentClass) classIter.next();
			entityNew = entityMap.get(perClass.getClassName());
			if (entityNew != null)
			{
				//check methods inside it for my change 
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

		classIter = cfg.getClassMappings();
		writter.write("\nProcessing entity for Inheritance.....................................");
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
		writter.write("\nProcessing Done.......................................");
		writter.write("\nSaving all Data.........");
		//set table name of specimenevetnparameter to edu.wustl.clinportal.domain.ReturnEventParameters
		EntityInterface entity = entityMap.get("edu.wustl.clinportal.domain.ReturnEventParameters");
		if (entity != null)
		{
			TablePropertiesInterface tblProperties = entity.getTableProperties();
			tblProperties.setName("SYN_CAT_SPECIMEN_EVENT_PARAM");
			objectList.add(tblProperties);
		}

		setIdentifiedAttribute(idfAttrFileName);
		saveAll();
		setAssnConstraintName();

		System.out.println("Import integration metadata task finished.See " + importLogFileName
				+ " for more details.");
	}

	/**
	 * @param assInter
	 * @param entity
	 */
	private static void setContainmentAssociation(AssociationInterface assInter,
			EntityInterface entity)
	{

		List<String> associatedEntityList = entityAssnMap.get(entity.getName());
		for (String targetEntityName : associatedEntityList)
		{
			if (assInter.getTargetEntity().getName().equals(targetEntityName))
			{
				assInter.getTargetRole().setAssociationsType(AssociationType.CONTAINTMENT);
				objectList.add(assInter);
				break;

			}
		}
	}

	/**
	 * This method add the constraint's name as per metadata fk name 
	 */
	private static void setAssnConstraintName()
	{

		Iterator it = constraintMap.keySet().iterator();
		JDBCDAO jdbcDao = null;
		try
		{
			IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(
					"dynamicExtention");
			jdbcDao = daoFactory.getJDBCDAO();
			jdbcDao.openSession(null);

			while (it.hasNext())
			{
				Long constid = (Long) it.next();
				String assfkValue = constraintMap.get(constid);
				String sql = "update DYEXTN_DATABASE_PROPERTIES set NAME = '" + assfkValue
						+ "' where IDENTIFIER='" + constid + "'";
				jdbcDao.executeUpdate(sql);
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
	 * @param idfAttrFileName
	 * @throws IOException
	 */
	private static void setIdentifiedAttribute(String idfAttrFileName) throws IOException
	{
		HashMap<String, List<String>> idfEntity = readFile(idfAttrFileName);

		Iterator itr = idfEntity.keySet().iterator();
		while (itr.hasNext())
		{
			String entityName = itr.next().toString();
			EntityInterface entity = entityMap.get(entityName);
			for (AttributeInterface attr : entity.getAllAttributes())
			{
				for (String attrName : idfEntity.get(entityName))
				{
					if (attr.getName().equals(attrName))
					{
						attr.setIsIdentified(Boolean.TRUE);
						objectList.add(attr);
					}
				}
			}
		}

	}

	/**
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	private static HashMap<String, List<String>> readFile(String fileName) throws IOException
	{
		HashMap<String, List<String>> idfEntity = new HashMap<String, List<String>>();
		List<String> paths = new ArrayList<String>();
		File file = new File(fileName);

		BufferedReader bufRdr = new BufferedReader(new FileReader(file));
		String line = null;

		//read each line of text file
		while ((line = bufRdr.readLine()) != null)
		{
			StringTokenizer tokenizer = new StringTokenizer(line, "::");
			String entityName = tokenizer.nextToken();
			String attributeNames = tokenizer.nextToken();
			StringTokenizer tokenizer1 = new StringTokenizer(attributeNames, "~");
			List<String> attrList = new ArrayList<String>();
			while (tokenizer1.hasMoreTokens())
			{
				attrList.add(tokenizer1.nextToken());
			}
			idfEntity.put(entityName, attrList);

		}

		return idfEntity;
	}

	/**
	 * @param fileName
	 * @throws IOException
	 */
	private static void readAssociationFile(String fileName) throws IOException
	{
		if (fileName != null)
		{
			File file = new File(fileName);
			BufferedReader bufRdr = new BufferedReader(new FileReader(file));
			String line = null;
			entityAssnMap = new HashMap<String, List<String>>();
			//read each line of text file
			while ((line = bufRdr.readLine()) != null)
			{
				StringTokenizer tokenizer = new StringTokenizer(line, "#");
				String entityName = tokenizer.nextToken();
				String assnNames = tokenizer.nextToken();
				StringTokenizer tokenizer1 = new StringTokenizer(assnNames, "~");
				List<String> assList = new ArrayList<String>();
				while (tokenizer1.hasMoreTokens())
				{
					String name = tokenizer1.nextToken();
					StringTokenizer tokenizer2 = new StringTokenizer(name, ":");
					tokenizer2.nextToken();
					String targetEntityName = tokenizer2.nextToken();
					assList.add(targetEntityName);
				}
				entityAssnMap.put(entityName, assList);

			}
		}
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

			if (firstContPropties.getTgtEntityConstraintKeyProperties() != null)
			{
				if (secndConstPrpties.getTgtEntityConstraintKeyProperties() != null)
				{
					constraintMap.put(firstContPropties.getId(), secndConstPrpties
							.getTgtEntityConstraintKeyProperties()
							.getTgtForiegnKeyColumnProperties().getName());
					firstContPropties.getTgtEntityConstraintKeyProperties()
							.getTgtForiegnKeyColumnProperties().setName(
									secndConstPrpties.getTgtEntityConstraintKeyProperties()
											.getTgtForiegnKeyColumnProperties().getName());
				}
				else
				{
					constraintMap.put(firstContPropties.getId(), secndConstPrpties
							.getSrcEntityConstraintKeyProperties()
							.getTgtForiegnKeyColumnProperties().getName());
					firstContPropties.getTgtEntityConstraintKeyProperties()
							.getTgtForiegnKeyColumnProperties().setName(
									secndConstPrpties.getSrcEntityConstraintKeyProperties()
											.getTgtForiegnKeyColumnProperties().getName());

				}
			}
			else if (firstContPropties.getSrcEntityConstraintKeyProperties() != null)
			{
				if (secndConstPrpties.getTgtEntityConstraintKeyProperties() != null)
				{

					constraintMap.put(firstContPropties.getId(), secndConstPrpties
							.getTgtEntityConstraintKeyProperties()
							.getTgtForiegnKeyColumnProperties().getName());
					firstContPropties.getSrcEntityConstraintKeyProperties()
							.getTgtForiegnKeyColumnProperties().setName(
									secndConstPrpties.getTgtEntityConstraintKeyProperties()
											.getTgtForiegnKeyColumnProperties().getName());
				}
				else
				{
					constraintMap.put(firstContPropties.getId(), secndConstPrpties
							.getSrcEntityConstraintKeyProperties()
							.getTgtForiegnKeyColumnProperties().getName());
					firstContPropties.getSrcEntityConstraintKeyProperties()
							.getTgtForiegnKeyColumnProperties().setName(
									secndConstPrpties.getSrcEntityConstraintKeyProperties()
											.getTgtForiegnKeyColumnProperties().getName());

				}
			}
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
												constraintMap.put(constPropties1.getId(), column
														.getName());
												if (constPropties1
														.getTgtEntityConstraintKeyProperties() != null)
												{
													constPropties1
															.getTgtEntityConstraintKeyProperties()
															.getTgtForiegnKeyColumnProperties()
															.setName(column.getName());
												}
												else
												{
													constPropties1
															.getSrcEntityConstraintKeyProperties()
															.getTgtForiegnKeyColumnProperties()
															.setName(column.getName());
												}
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
					LOGGER.info("--initHibernateMetaData Task throws error." + e.getMessage());
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
									constraintMap.put(constrProperts.getId(), column.getName());
									constrProperts.getTgtEntityConstraintKeyProperties()
											.getTgtForiegnKeyColumnProperties().setName(
													column.getName());
								}
								specCaseEntities.add("MappingsForOneSideAssociationCases"
										+ association.getId() + "_____" + objEntity.getName());
							}
						}
					}
				}
				catch (Exception e)
				{
					LOGGER.info("--initHibernateMetaData Task throws error." + e.getMessage());
				}
			}
		}
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
		if (str1 != null)
		{
			return str1.equals(str2);
		}
		else if (str2 == null)
			return true;

		return false;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String hibConfigFileName;
		String logFileName;
		String idfAttrFileName;
		String contAssnFileName;
		if (args.length == 4)
		{
			hibConfigFileName = args[0];
			logFileName = args[1];
			idfAttrFileName = args[2];
			contAssnFileName = args[3];
			ImportIntegrationMetaData.initHibernateMetaData(hibConfigFileName, logFileName,
					idfAttrFileName, contAssnFileName);

		}
	}
}