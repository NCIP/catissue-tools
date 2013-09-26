/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */


package edu.wustl.clinportal.uploadmetadata;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.jmi.model.ModelPackage;
import javax.jmi.model.MofPackage;
import javax.jmi.xmi.XmiReader;

import org.netbeans.api.mdr.MDRManager;
import org.netbeans.api.mdr.MDRepository;
import org.omg.uml.UmlPackage;
import org.openide.util.Lookup;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.DynamicExtensionBaseDomainObjectInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.TablePropertiesInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.EntityGroupManager;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.xmi.XMIConfiguration;
import edu.common.dynamicextensions.xmi.XMIConstants;
import edu.common.dynamicextensions.xmi.XMIUtilities;
import edu.common.dynamicextensions.xmi.importer.XMIImportProcessor;
import edu.wustl.clinportal.action.annotations.AnnotationConstants;
import edu.wustl.clinportal.annotations.PathObject;
import edu.wustl.clinportal.bizlogic.AnnotationUtil;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;

/**
 * This class is used for integrating DE models of Catissue within Clinportal
 * @author falguni_sachde
 *
 */
public class AutomateDynamicMetadataUpload
{

	// name of a UML extent (instance of UML meta model) that the UML models will be loaded into
	private static final String UML_INSTANCE = "UMLInstance";
	// name of a MOF extent that will contain definition of UML meta model
	private static final String UML_MM = "UML";

	// repository
	private static MDRepository rep;
	// UML extent
	private static UmlPackage uml;

	// XMI reader
	private static XmiReader reader;

	static private BufferedWriter logWriter = null;

	private static final String IMPORT_PARA_FILE = "ImportParameter.csv";
	private static final String IMPORT_LOG_FILE = "importMetadata.txt";
	private static final String DE_SYN_FILE = "createDESynonym.sql";

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			Long start = Long.valueOf(System.currentTimeMillis());
			automateDEmodel(args);
			Long end = Long.valueOf(System.currentTimeMillis());
			System.out.println("--Time required to finish program is " + (end - start) / 1000
					+ "seconds");
			System.out.println("--The integration metatada task finishes.");

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (logWriter != null)

				{
					logWriter.flush();
					logWriter.close();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * @throws IOException
	 */
	private static void removeFiles() throws IOException
	{
		ArrayList<ArrayList<String>> parameterList = new ArrayList<ArrayList<String>>();
		//Read parameter files for import related parameter details
		readParameterFile(parameterList);
		for (ArrayList<String> paramList : parameterList)
		{

			String egroupName = paramList.get(0);
			String fileName = AnnotationConstants.CAT_ENTITY_GROUP_PREFIX_IN_CP + egroupName
					+ ".xmi";
			File objXMIFile = new File(fileName);
			if (objXMIFile.delete())
			{
				System.out.println("--xmi file deleted after processing" + fileName);
			}
		}

	}

	/**
	 * 
	 * @param args[]
	 * @throws Exception
	 */
	private static void automateDEmodel(String[] args) throws Exception
	{
		if (!isCatissueStaticModelAvl())
		{
			System.out.println("ERROR:----");
			System.out
					.println("--Catissue Static model is not yet intgrated with ClinPortal .Please execute the ant task 'integrate_catissue_metadata' then execute this task again.");

		}
		else if (args[0].equals("importModel"))
		{
			importCatissueModel(args[1]);
			removeFiles();
		}

	}

	/**
	 * @return
	 */
	private static boolean isCatissueStaticModelAvl()
	{
		boolean isAvl = false;
		try
		{
			Long egroupId = EntityManager.getInstance().getEntityGroupId(
					AnnotationConstants.CAT_GROUPNAME_IN_CP);
			if (egroupId != null)
			{
				isAvl = true;
			}
		}
		catch (Exception desysExp)
		{
			desysExp.printStackTrace();
		}
		return isAvl;
	}

	/**
	 * @param mainContainerFilename 
	 * @param packageName 
	 * @param string 
	 * @throws IOException
	 */
	private static void importCatissueModel(String catissueSchemaName) throws IOException
	{

		logWriter = new BufferedWriter(new FileWriter(IMPORT_LOG_FILE));
		logWriter.write("\n-AutomateDynamicMetadataUpload.importCatissueModel method start");
		ArrayList<ArrayList<String>> parameterList = new ArrayList<ArrayList<String>>();
		//Read parameter files for import related parameter details
		readParameterFile(parameterList);

		//Iterate and import one-by-one catissue DE models within ClinPortal.

		for (ArrayList<String> paramList : parameterList)
		{

			String egroupName = paramList.get(0);
			String logicalpackageName = paramList.get(1);
			List<String> mainContainerList = getList(paramList.get(2));
			List<String> hookedEntityList = getList(paramList.get(3));
			List<ContainerInterface> importedContainerList;
			System.out.println("--Import xmi start :" + egroupName + "---" + logicalpackageName);
			try
			{
				importedContainerList = importDynamicModel(logicalpackageName, egroupName,
						mainContainerList, hookedEntityList, catissueSchemaName);
				logWriter.write("-After import the container list size="
						+ importedContainerList.size());
				System.out
						.println("--Import xmi task finishes ,adding paths and associating with hookentity");
				//Associate imported Containers with the hooked entity
				if (!hookedEntityList.isEmpty())
				{
					associateHookEntity(importedContainerList, hookedEntityList);
				}
				else
				{
					//if hooked entity is none (as in case of static model) 					
					Set<PathObject> processedPathList = new HashSet<PathObject>();
					JDBCDAO jdbcdao = null;
					String appName = CommonServiceLocator.getInstance().getAppName();
					IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
					try
					{

						jdbcdao = daoFactory.getJDBCDAO();
						jdbcdao.openSession(null);

						for (ContainerInterface mainContainer : importedContainerList)
						{
							AnnotationUtil.addQueryPathsForAllAssociatedEntities(
									((EntityInterface) mainContainer.getAbstractEntity()), null,
									null, processedPathList, true, jdbcdao);
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					finally
					{
						try
						{
							jdbcdao.commit();
							jdbcdao.closeSession();
						}
						catch (DAOException e)
						{
							throw new DynamicExtensionsSystemException(
									"Exception in addInheritancePathforSystemGenerated paths." + e);
						}
					}

					for (ContainerInterface mainContainer : importedContainerList)
					{

						AnnotationUtil
								.addInheritancePathforSystemGenerated(((EntityInterface) mainContainer
										.getAbstractEntity()));
					}

				}
			}
			catch (DynamicExtensionsSystemException e)
			{
				logWriter.write("\nException while importing model =entity group=" + egroupName);
			}
			catch (DynamicExtensionsApplicationException e)
			{
				logWriter.write("\nException while importing model =entity group=" + egroupName);
			}
			catch (BizLogicException e)
			{
				logWriter.write("\nException while importing model =entity group=" + egroupName);
			}
			logWriter.write("\n-Import xmi finshes for given entity group=" + egroupName);
		}
		logWriter.write("\n-AutomateDynamicMetadataUpload.importCatissueModel method finshes");
		System.out.println("--Import metadata task finishes.");
		System.out.println("--Please see the Importmetadata.txt file for more details.");
	}

	/**
	 * Parses the line of Parameters
	 * @param line
	 * @return
	 */
	private static List<String> getList(String line)
	{
		List<String> strList = new ArrayList<String>();
		StringTokenizer tokenizer = new StringTokenizer(line, ":");
		while (tokenizer.hasMoreTokens())
		{
			strList.add(tokenizer.nextToken());
		}

		return strList;
	}

	/**
	 * This method will read the ImportParameter.csv file ,which contains all information about exported Catissue models.
	 * @param parameterList
	 * @throws IOException
	 */
	private static void readParameterFile(ArrayList<ArrayList<String>> parameterList)
			throws IOException
	{
		logWriter.write("\n==AutomateDynamicMetadataUpload.readParameterFile ==");
		BufferedReader bufRdr = null;
		String fileName = IMPORT_PARA_FILE;
		File objFile = new File(fileName);
		if (objFile.exists())
		{
			try
			{
				bufRdr = new BufferedReader(new FileReader(IMPORT_PARA_FILE));
				String line = null;

				//read each line of text file
				while ((line = bufRdr.readLine()) != null)
				{
					ArrayList<String> paramList = new ArrayList<String>();
					StringTokenizer tokenizer = new StringTokenizer(line, ",");
					while (tokenizer.hasMoreTokens())
					{
						paramList.add(tokenizer.nextToken());
					}
					parameterList.add(paramList);
				}

			}
			finally
			{
				bufRdr.close();
			}
		}

	}

	/**
	 * This method will create synonym sql file for every  entity tables for given entity group
	 * @param entityGroupName
	 * @param skipEntityNames
	 * @param isEditedXmi 
	 * @param egroupName 
	 * @throws IOException
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private static void createSynonyms(String entityGroupName, List<String> skipEntityNames,
			boolean isEditedXmi, String egroupName, String catissueSchemaName) throws IOException,
			DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		logWriter.write("\n-Creating synonym for entity group:" + egroupName);
		BufferedWriter writter = null;
		List<DynamicExtensionBaseDomainObjectInterface> objectList = new ArrayList<DynamicExtensionBaseDomainObjectInterface>();
		try
		{
			File file = new File(DE_SYN_FILE);
			writter = new BufferedWriter(new FileWriter(file));
			EntityGroupInterface egroup = EntityGroupManager.getInstance().getEntityGroupByName(
					entityGroupName);
			Collection<EntityInterface> allEntities = egroup.getEntityCollection();

			//System.out.println("--List of All Entities within group:" + entityGroupName);
			for (EntityInterface entity : allEntities)
			{
				TablePropertiesInterface tblInter = entity.getTableProperties();
				String oldTablename = tblInter.getName();
				String synName = AnnotationConstants.SYNONYM_PREFIX + tblInter.getName();
				tblInter.setName(synName);

				//In case of new model or In case of edited xmi if the synonym already not exist then create
				if (!isEditedXmi || (isEditedXmi && !isSynonymExist(synName)))
				{

					logWriter.write("\n-Creating syn for table " + entity.getName() + "==table="
							+ tblInter.getName());
					entity.setTableProperties(tblInter);
					objectList.add(tblInter);
					writter.write("CREATE SYNONYM " + tblInter.getName() + " FOR "
							+ catissueSchemaName + "." + oldTablename);
					writter.write("\n");
				}
				else if (!isEditedXmi && isSynonymExist(synName))
				{
					//its new model and the synonym already exist then throw error
					logWriter.write("\n-Synonym already exist for entity name=" + entity.getName()
							+ " and table:" + tblInter.getName());
					throw new DynamicExtensionsSystemException(
							"Synonyms already exist for entity name" + entity.getName()
									+ ",table name=" + tblInter.getName());
				}
			}

		}
		catch (Exception e)
		{
			logWriter.write("\n-Error while createing synonyms of entitygroup=" + entityGroupName);
			throw new DynamicExtensionsSystemException(e.getMessage());
		}
		finally
		{
			writter.flush();
			writter.close();

		}
		//System.out.println("Creating Synonym sql===");
		//Execute synonym sql file this will create all entity table synonym for given entity group
		executeSynonymSQL(DE_SYN_FILE);
		//Save all method saves all  Entity object update

		saveAll(objectList);
		logWriter.write("\n-Synonyms created and TableProperties of Entity Updated");
	}

	/**
	 * This method checks whether given synonym is alread exist within ClinPortal database
	 * @param synName
	 * @return
	 * @throws IOException
	 * @throws DynamicExtensionsSystemException
	 */
	private static boolean isSynonymExist(String synName) throws IOException,
			DynamicExtensionsSystemException
	{

		boolean synonymExist = false;
		//logWriter.write("\n-isSynonymExist method=" + synName);
		JDBCDAO jdbcDao = null;
		//Its commonpackage logger ,instantiate it
		IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory();
		try
		{
			jdbcDao = daoFactory.getJDBCDAO();
			jdbcDao.openSession(null);
			String selSQL = "SELECT SYNONYM_NAME from user_synonyms WHERE SYNONYM_NAME='" + synName
					+ "'";
			ResultSet resultset = jdbcDao.getQueryResultSet(selSQL);

			while (resultset.next())
			{
				synonymExist = true;
			}
			jdbcDao.closeStatement(resultset);
		}

		catch (Exception e)
		{

			logWriter.write("\n-Error while executing isSynonymExist ");
			throw new DynamicExtensionsSystemException(e.getMessage());
		}
		finally
		{
			try
			{

				jdbcDao.closeSession();
			}
			catch (DAOException daoEx)
			{

				logWriter.write("\n Error while executing isSynonymExist ");
				throw new DynamicExtensionsSystemException(daoEx.getMessage());
			}
		}

		return synonymExist;
	}

	/**
	 * @param synFile
	 * @throws IOException
	 * @throws DynamicExtensionsSystemException 
	 */
	private static void executeSynonymSQL(String synFile) throws IOException,
			DynamicExtensionsSystemException
	{

		logWriter.write("\n-Executing SynonymSQL file");
		BufferedReader bufRdr = new BufferedReader(new FileReader(synFile));
		String line = null;

		JDBCDAO jdbcDao = null;
		//Its commonpackage logger ,instantiate it
		IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory();
		try
		{
			jdbcDao = daoFactory.getJDBCDAO();
			jdbcDao.openSession(null);
			while ((line = bufRdr.readLine()) != null)
			{

				jdbcDao.executeUpdate(line);
			}
		}
		catch (Exception e)
		{
			logWriter.write("\n-Error while executing synonyms ");
			throw new DynamicExtensionsSystemException(e.getMessage());
		}
		finally
		{
			try
			{
				jdbcDao.closeSession();
			}
			catch (DAOException daoEx)
			{
				logWriter.write("\n-Error while executing synonyms ");
				throw new DynamicExtensionsSystemException(daoEx.getMessage());
			}
		}
		System.out.println("--Synonym creation completes.");

	}

	/**
	 * This method save database properties, constraint properties & table properties using Hibernate dao.
	 * @param objectList
	 * @throws IOException
	 * @throws DynamicExtensionsSystemException 
	 */
	private static void saveAll(List<DynamicExtensionBaseDomainObjectInterface> objectList)
			throws IOException, DynamicExtensionsSystemException
	{
		logWriter.write("\n-Inside  Save All method");

		//DAOConfigFactory daocConfigFactory = DAOConfigFactory.getInstance();
		//IDAOFactory daoFactory = daocConfigFactory.getDAOFactory("dynamicExtention");
		HibernateDAO dao = null;

		try
		{
			dao = DynamicExtensionsUtility.getHibernateDAO();
			dao.openSession(null);

			for (DynamicExtensionBaseDomainObjectInterface obj : objectList)
			{
				dao.update(obj);
			}
			//System.out.println("sava all finshes ------");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			logWriter.write("\n-Error while saveall method");
			throw new DynamicExtensionsSystemException(e.getMessage());
		}
		finally
		{
			try
			{
				dao.commit();
				dao.closeSession();
			}
			catch (DAOException daoEx)
			{
				daoEx.printStackTrace();
				logWriter.write("\n-Error while saveAll method ");
				throw new DynamicExtensionsSystemException(daoEx.getMessage());
			}
		}
		System.out.println("--saveAll objects finishes.");
	}

	/**
	 * This method imports given named DE model to Clinportal.
	 * @param logicalPackageName
	 * @param egroupName
	 * @param mainContainerList
	 * @param skipEntityNames
	 * @return
	 * @throws IOException
	 * @throws DynamicExtensionsSystemException
	 */
	private static List<ContainerInterface> importDynamicModel(String logicalPackageName,
			String egroupName, List<String> mainContainerList, List<String> skipEntityNames,
			String catissueSchemaName) throws IOException, DynamicExtensionsSystemException

	{
		// deletes unwanted repository files if exist
		XMIUtilities.cleanUpRepository();
		FileInputStream fileInput = null;
		boolean isEditedXmi = false;
		String fileName = null;
		List<ContainerInterface> importedContainerList = new ArrayList<ContainerInterface>();
		try
		{

			logWriter.write("\n-AutomateDynamicMetadataUpload.importDynamicModel");
			fileName = AnnotationConstants.CAT_ENTITY_GROUP_PREFIX_IN_CP + egroupName + ".xmi";
			System.out.println("--Importing xmi file:" + fileName);
			validateParam(fileName, mainContainerList);
			logWriter.write("\n-Filename=" + fileName);
			for (String entity : skipEntityNames)
			{
				logWriter.write("\n-Skip entity name=" + entity);

			}

			File file = new File(fileName);

			String packageName = AnnotationConstants.LOGICAL_VIEW_LOGICAL_MODEL
					+ logicalPackageName;
			//skipentityGroup is the name of Static Catissue model's entity group name within Clinportal
			String skipentityGroup = AnnotationConstants.CAT_GROUPNAME_IN_CP;

			int indexOfExtension = file.getName().lastIndexOf(".");
			String domainModelName = "";

			if (indexOfExtension == -1)
			{
				domainModelName = file.getName();
			}
			else
			{
				domainModelName = file.getName().substring(0, indexOfExtension);
			}

			logWriter.write("\n-packageName=" + packageName);
			logWriter.write("\n-skipentityGroup=" + skipentityGroup);
			logWriter.write("\n-domainModelName=" + domainModelName);
			System.out.println("--Importxmi task starts for " + fileName);
			// get the default repository
			rep = MDRManager.getDefault().getDefaultRepository();
			// create an XMIReader
			reader = (XmiReader) Lookup.getDefault().lookup(XmiReader.class);

			init();
			fileInput = new FileInputStream(file);

			// start a read-only transaction
			rep.beginTrans(true);

			// read the document
			reader.read(fileInput, null, uml);
			//XMIImportProcessor xmiImpProcsor = new XMIImportProcessor();

			List<String> containerNames = mainContainerList;

			//Set isEntGrpSysGented=true because they will not going to be associated with Clinical Study
			boolean isEntGrpSysGented = true;
			//Set isCreateTable=true because the db-table creation is not required ,instead Synonyms will be created for each DE table.
			boolean isCreateTable = false;
			//Set isDefaultPackage=false because ,the exported xmi contains hooked entity like Participant,scg,specimen whose package name
			//starts with default catissue package ,but its entity name not required to change while importing.   
			boolean isDefaultPackage = false;

			logWriter.write("\n-isCreateTable=" + isCreateTable);
			logWriter.write("\n-skipentityGroup=" + skipentityGroup);
			XMIConfiguration xmiConfiguration = XMIConfiguration.getInstance();
			xmiConfiguration.setCreateTable(isCreateTable);
			xmiConfiguration.setAddIdAttr(true);
			xmiConfiguration.setAddColumnForInherianceInChild(false);
			xmiConfiguration.setAddInheritedAttribute(false);
			xmiConfiguration.setEntityGroupSystemGenerated(isEntGrpSysGented);
			xmiConfiguration.setSkipEntityNames(skipEntityNames);
			xmiConfiguration.setDefaultPackage(isDefaultPackage);
			xmiConfiguration.setSkipEntityGroup(skipentityGroup);
			xmiConfiguration.setDefaultPackagePrefix(AnnotationConstants.CAT_DOMAIN_PREFIX);

			XMIImportProcessor xmiImportProcessor = new XMIImportProcessor();
			xmiImportProcessor.setXmiConfigurationObject(xmiConfiguration);
			importedContainerList = xmiImportProcessor.processXmi(uml, domainModelName,
					packageName, containerNames);

			isEditedXmi = xmiImportProcessor.isEditedXmi;
			System.out.println("--isEditedXmi = " + isEditedXmi);
			logWriter.write("\nisEditedXmi=" + isEditedXmi);
			logWriter.write("\n-Forms have been created ");

			System.out.println("--Forms have been created !!!!");
			System.out.println("--Associating with hook entity.");

			//if isCreateTable = true ,then only associate with hook entity because in case of static & dynamic model integration 
			//we are not creating table ,so no need to associate it with hook entity

			Set<PathObject> processedPathList = new HashSet<PathObject>();
			AnnotationUtil.setRecordEntryId();
			JDBCDAO jdbcdao = null;
			String appName = CommonServiceLocator.getInstance().getAppName();
			IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
			try
			{

				jdbcdao = daoFactory.getJDBCDAO();
				jdbcdao.openSession(null);
				//This will add query paths for all imported container entity containers			
				for (ContainerInterface mainContainer : importedContainerList)
				{
					logWriter.write("\n-Calling addQueryPathsForAllAssociatedEntities for  "
							+ mainContainer.getAbstractEntity().getName());

					AnnotationUtil.addQueryPathsForAllAssociatedEntities(
							((EntityInterface) mainContainer.getAbstractEntity()), null, null,
							processedPathList, isEntGrpSysGented, jdbcdao);
				}
				//Following will  add  Parent Entity's association paths to child Entity also.

				for (ContainerInterface mainContainer : importedContainerList)
				{
					AnnotationUtil
							.addInheritancePathforSystemGenerated(((EntityInterface) mainContainer
									.getAbstractEntity()));
				}

			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					jdbcdao.commit();
					jdbcdao.closeSession();
				}
				catch (DAOException e)
				{
					throw new DynamicExtensionsSystemException(
							"Exception in addInheritancePathforSystemGenerated paths." + e);
				}
			}
			//Create Synonyms for given DE model's entity to existing DE tables within catissue database 
			createSynonyms(domainModelName, skipEntityNames, isEditedXmi, egroupName,
					catissueSchemaName);
		}
		catch (Exception exp)
		{
			exp.printStackTrace();
			throw new DynamicExtensionsSystemException(exp.getMessage());
		}
		finally
		{
			// release the transaction
			try
			{
				rep.endTrans();
				MDRManager.getDefault().shutdownAll();
				fileInput.close();
			}
			catch (Exception exp)
			{
				System.out.println("Error::- Specified file does not exist.");
				throw new DynamicExtensionsSystemException(exp.getMessage());
			}
			finally
			{
				XMIUtilities.cleanUpRepository();
			}

		}
		logWriter.write("\n-Import xmi finish for file:" + fileName);
		return importedContainerList;

	}

	/**
	 * @param mainContainerList
	 * @param catissuehookedEntityList
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 * @throws BizLogicException
	 */
	private static void associateHookEntity(List<ContainerInterface> mainContainerList,
			List<String> catissuehookedEntityList) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException, BizLogicException
	{

		//The catissuehookedEntityList containes either Participatn,SCG or Specimen
		//The hookedEntity must be taken from the Catissue entitygroup available within ClinPortal.
		EntityGroupInterface egroupStatic = EntityGroupManager.getInstance().getEntityGroupByName(
				AnnotationConstants.CAT_GROUPNAME_IN_CP);
		//Associate with every hooked entity in list
		for (String hookEntityName : catissuehookedEntityList)
		{
			hookEntityName = XMIConstants.CATISSUE_PACKAGE + hookEntityName;
			EntityInterface sEntity = egroupStatic.getEntityByName(hookEntityName);
			Set<PathObject> processedPathList = new HashSet<PathObject>();
			JDBCDAO jdbcdao = null;
			String appName = CommonServiceLocator.getInstance().getAppName();
			IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
			try
			{

				jdbcdao = daoFactory.getJDBCDAO();
				jdbcdao.openSession(null);
				for (ContainerInterface container : mainContainerList)
				{
					for (AssociationInterface assn : sEntity.getAssociationCollection())
					{
						if (assn.getTargetEntity().getId().equals(
								container.getAbstractEntity().getId()))
						{
							EntityInterface dynEntity = (EntityInterface) container
									.getAbstractEntity();

							AnnotationUtil.addQueryPathsForAllAssociatedEntities(dynEntity,
									sEntity, assn.getId(), processedPathList, false, jdbcdao);

						}
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					jdbcdao.commit();
					jdbcdao.closeSession();
				}
				catch (DAOException e)
				{
					throw new DynamicExtensionsSystemException(
							"Exception in addInheritancePathforSystemGenerated paths." + e);
				}
			}
		}
	}

	/**
	 * @throws Exception
	 */
	private static void init() throws Exception
	{
		uml = (UmlPackage) rep.getExtent(UML_INSTANCE);

		if (uml == null)
		{
			// UML extent does not exist -> create it (note that in case one want's to instantiate
			// a metamodel other than MOF, they need to provide the second parameter of the createExtent
			// method which indicates the metamodel package that should be instantiated)
			uml = (UmlPackage) rep.createExtent(UML_INSTANCE, getUmlPackage());
		}
	}

	/** Finds "UML" package -> this is the topmost package of UML meta model - that's the
	 * package that needs to be instantiated in order to create a UML extent
	 * @return
	 * @throws Exception
	 */
	private static MofPackage getUmlPackage() throws Exception
	{
		// get the MOF extent containing definition of UML metamodel
		ModelPackage umlMM = (ModelPackage) rep.getExtent(UML_MM);
		if (umlMM == null)
		{
			// it is not present -> create it
			umlMM = (ModelPackage) rep.createExtent(UML_MM);
		}
		// find package named "UML" in this extent
		MofPackage result = getUmlPackage(umlMM);
		if (result == null)
		{
			// it cannot be found -> UML metamodel is not loaded -> load it from XMI
			reader.read(UmlPackage.class.getResource("resources/01-02-15_Diff.xml").toString(),
					umlMM);
			// try to find the "UML" package again
			result = getUmlPackage(umlMM);
		}
		return result;
	}

	/** Finds "UML" package in a given extent
	 * @param umlMM MOF extent that should be searched for "UML" package.
	 * @return
	 */
	private static MofPackage getUmlPackage(ModelPackage umlMM)
	{
		MofPackage returnpkg = null;
		// iterate through all instances of package

		for (Iterator it = umlMM.getMofPackage().refAllOfClass().iterator(); it.hasNext();)
		{
			MofPackage pkg = (MofPackage) it.next();
			System.out.println("\n\nName = " + pkg.getName());

			// is the package topmost and is it named "UML"?
			if (pkg.getContainer() == null && "UML".equals(pkg.getName()))
			{
				// yes -> return it
				returnpkg = pkg;
				break;
			}
		}
		// a topmost package named "UML" could not be found
		return returnpkg;
	}

	/**
	 * This method 
	 * @param filename
	 * @param mainContinerList
	 * @throws DynamicExtensionsSystemException
	 */
	private static void validateParam(String filename, List<String> mainContinerList)
			throws DynamicExtensionsSystemException
	{
		if (filename == null)
		{
			throw new DynamicExtensionsSystemException("No xmi available to import");
		}
		if (mainContinerList.size() < 1)
		{
			throw new DynamicExtensionsSystemException("Main Container names not available");
		}

	}
}
