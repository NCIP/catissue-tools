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
 *
 */

package edu.wustl.clinportal.annotations.xmi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.jmi.model.ModelPackage;
import javax.jmi.model.MofPackage;
import javax.jmi.xmi.XmiReader;

import org.netbeans.api.mdr.MDRManager;
import org.netbeans.api.mdr.MDRepository;
import org.omg.uml.UmlPackage;
import org.openide.util.Lookup;

import edu.common.dynamicextensions.bizlogic.BizLogicFactory;
import edu.common.dynamicextensions.dao.impl.DynamicExtensionDAO;
import edu.common.dynamicextensions.domain.AbstractMetadata;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.xmi.XMIConfiguration;
import edu.common.dynamicextensions.xmi.XMIUtilities;
import edu.common.dynamicextensions.xmi.importer.XMIImportProcessor;
import edu.wustl.clinportal.action.annotations.AnnotationConstants;
import edu.wustl.clinportal.annotations.PathObject;
import edu.wustl.clinportal.bizlogic.AnnotationBizLogic;
import edu.wustl.clinportal.bizlogic.AnnotationUtil;
import edu.wustl.clinportal.domain.ClinicalStudy;
import edu.wustl.clinportal.domain.ClinicalStudyEvent;
import edu.wustl.clinportal.domain.StudyFormContext;
import edu.wustl.clinportal.uploadmetadata.UpdateCSRToEntityPath;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.QueryWhereClause;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.security.exception.UserNotAuthorizedException;

/**
 * @author falguni_sachde
 *
 */
public class ImportXmi
{

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}
	//Logger
	private static final Logger LOGGER = Logger.getCommonLogger(ImportXmi.class);

	//	 name of a UML extent (instance of UML meta model) that the UML models will be loaded into
	private static final String UML_INSTANCE = "UMLInstance";
	// name of a MOF extent that will contain definition of UML meta model
	private static final String UML_MM = "UML";

	// repository
	private static MDRepository rep;
	// UML extent
	private static UmlPackage uml;

	// XMI reader
	private static XmiReader reader;

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		FileInputStream fileInput = null;
		try
		{
			validate(args);
			//Fully qualified Name of the xmi file to be imported
			String fileName = args[0];
			File file = new File(fileName);
			LOGGER.info("File name = " + fileName);
			String packageName = "";
			String pathCsvFileName = "";
			String coRecObjCsvFName = "";
			String hookentity = "";
			boolean addQueryPaths = true;
			if (args.length > 1 && args[1].trim().length() > 0)
			{
				pathCsvFileName = args[1];
			}
			if (args.length > 2 && args[2].trim().length() > 0)
			{
				packageName = args[2];
			}
			if (args.length > 3 && args[3].trim().length() > 0)
			{
				addQueryPaths = Boolean.valueOf(args[3]);
			}
			if (args.length > 4 && args[4].trim().length() > 0)
			{
				coRecObjCsvFName = args[4];
			}
			if (args.length > 5 && args[5].trim().length() > 0)
			{
				hookentity = args[5];
			}

			LOGGER.info("CSV File name = " + pathCsvFileName);
			LOGGER.info("Package name = " + packageName);
			LOGGER.info("Add query paths = " + addQueryPaths);
			LOGGER.info("Condition record object CSV File name = " + coRecObjCsvFName);
			LOGGER.info("Hook entity = " + hookentity);

			List<Long> condObjIds = new ArrayList<Long>();
			if (!coRecObjCsvFName.equals("") && !coRecObjCsvFName.equals("\"\"")
					&& !coRecObjCsvFName.equals("''"))
			{
				List<String> condObjNames = readFile(coRecObjCsvFName);
				for (String conditionObjName : condObjNames)
				{
					Long csId = (Long) getObjectIdentifier(conditionObjName, ClinicalStudy.class
							.getName(), Constants.TITLE);
					if (csId == null)
					{
						throw new DynamicExtensionsSystemException(
								"Specified Clinical Study does not exist.");
					}
					condObjIds.add(csId);
				}
			}

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

			LOGGER.info("Domain model name = " + domainModelName);

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

			List<String> containerNames = readFile(pathCsvFileName);

			boolean isEntGrpSysGented = false;
			if (hookentity.equalsIgnoreCase(AnnotationConstants.NONE))
			{
				isEntGrpSysGented = true;
			}
			XMIConfiguration xmiConfiguration = XMIConfiguration.getInstance();
			xmiConfiguration.setCreateTable(true);
			xmiConfiguration.setAddIdAttr(true);
			xmiConfiguration.setAddColumnForInherianceInChild(false);
			xmiConfiguration.setAddInheritedAttribute(false);
			xmiConfiguration.setEntityGroupSystemGenerated(isEntGrpSysGented);
			XMIImportProcessor xmiImportProcessor = new XMIImportProcessor();
			xmiImportProcessor.setXmiConfigurationObject(xmiConfiguration);
			//LoggerConfig.configureLogger(System.getProperty("user.dir"));

			long processXMIStartTime = System.currentTimeMillis();

			List<ContainerInterface> mainContainerList = xmiImportProcessor.processXmi(uml,
					domainModelName, packageName, containerNames);

			long processXMIEndTime = System.currentTimeMillis();

			long processXMITotalTime = processXMIEndTime - processXMIStartTime;

			LOGGER.info(" ");
			LOGGER.info("##########################################");
			LOGGER.info("  IMPORT_XMI --> TASK : CREATE DE TABLES");
			LOGGER.info("  --------------------------------------");
			LOGGER.info("  Time taken (in minutes) = " + (processXMITotalTime / 1000) / 60);
			LOGGER.info("##########################################");
			LOGGER.info(" ");

			boolean isEditedXmi = xmiImportProcessor.isEditedXmi;
			LOGGER.info(" ");
			LOGGER.info(" ");
			LOGGER.info("EDIT_XMI case = " + (isEditedXmi ? "Y" : "N"));
			LOGGER.info(" ");
			LOGGER.info(" ");
			//LOGGER.info("Entities have been created!!!!");

			HibernateDAO hibernatedao = null;

			// Retrieve entity group id from first main container of list.
			Long entityGroupId = null;
			ContainerInterface containerInterface = mainContainerList.get(0);
			EntityInterface objEntity = (EntityInterface) containerInterface.getAbstractEntity();
			entityGroupId = objEntity.getEntityGroup().getId();
			if (isEditedXmi)
			{
				hibernatedao = DynamicExtensionsUtility.getHibernateDAO();
				mainContainerList = getMainContainerList(entityGroupId, hibernatedao);
			}

			long assoWithHEstartTime = System.currentTimeMillis();

			// Integrating with hook entity.
			if (!hookentity.equalsIgnoreCase(AnnotationConstants.NONE))
			{
				// For CLINPORTAL, there is only one hook entity object i.e. RECORD ENTRY
				//LOGGER.info("Number of main containers = " + mainContainerList.size());
				LOGGER.info(" ");
				LOGGER.info("Now associating with hook entity -> RecordEntry....");
				LOGGER.info(" ");
				associateHookEntity(mainContainerList, condObjIds, isEditedXmi, domainModelName,
						addQueryPaths, hibernatedao);
			}
			else
			{
				LOGGER.info("Hook entity is not provided.");
				LOGGER.info("Main Container list size " + mainContainerList.size());
				Set<PathObject> processedPathList = new HashSet<PathObject>();
				AnnotationUtil.setRecordEntryId();
				JDBCDAO jdbcdao = null;
				String appName = CommonServiceLocator.getInstance().getAppName();
				IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
				try
				{
					jdbcdao = daoFactory.getJDBCDAO();
					jdbcdao.openSession(null);
					for (ContainerInterface mainContainer : mainContainerList)
					{
						AnnotationUtil.addQueryPathsForAllAssociatedEntities(
								((EntityInterface) mainContainer.getAbstractEntity()), null, null,
								processedPathList, isEntGrpSysGented, jdbcdao);
					}
				}
				catch (Exception e)
				{
					LOGGER.error("Error while adding query paths for all associated entities", e);
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

				// Following will add Parent Entity's association paths to child Entity also.
				try
				{
					for (ContainerInterface mainContainer : mainContainerList)
					{
						AnnotationUtil
								.addInheritancePathforSystemGenerated(((EntityInterface) mainContainer
										.getAbstractEntity()));
					}
				}
				catch (Exception e)
				{
					LOGGER.error("Error while adding inheritance paths", e);
				}
			}

			long assoWithHEendTime = System.currentTimeMillis();
			long assoWithHEtotalTime = assoWithHEendTime - assoWithHEstartTime;

			LOGGER.info(" ");
			LOGGER.info(" ");
			LOGGER.info("######################################################################");
			LOGGER.info("  IMPORT_XMI --> TASK : ASSOCIATE WITH HOOK ENTITY & ADD QUERY PATHS");
			LOGGER.info("  ------------------------------------------------------------------");
			LOGGER.info("  Time taken (in minutes) = " + (assoWithHEtotalTime / 1000) / 60);
			LOGGER.info("######################################################################");
			LOGGER.info(" ");
			LOGGER.info(" ");
			LOGGER.info(" ");

			long csrStartTime = System.currentTimeMillis();

			// This will add paths from CSR to all entities of entity group.
			if (addQueryPaths)
			{
				LOGGER.info("Now adding CSR query paths for entities....");
				UpdateCSRToEntityPath.addCuratedPathsFromCSRToAllEntities(xmiConfiguration
						.getNewEntitiesIds(), hibernatedao);
			}

			long csrEndTime = System.currentTimeMillis();

			long csrTotalTime = csrEndTime - csrStartTime;

			LOGGER.info(" ");
			LOGGER.info("#############################################");
			LOGGER.info("  IMPORT_XMI --> TASK : ADD CSR QUERY PATHS");
			LOGGER.info("  -----------------------------------------");
			LOGGER.info("  Time taken (in minutes) = " + (csrTotalTime / 1000) / 60);
			LOGGER.info("#############################################");
			LOGGER.info(" ");
			LOGGER.info(" ");

			try
			{
				if (hibernatedao != null)
				{
					hibernatedao.commit();
					DynamicExtensionsUtility.closeHibernateDAO(hibernatedao);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			LOGGER.info(" ");
			LOGGER.info(" ");
			LOGGER.info(" ");
			LOGGER.info(" ");
			LOGGER.info("                    ##################                   ");
			LOGGER.info("------------------- ##   Finished   ## ------------------");
			LOGGER.info("                    ##################                   ");
			LOGGER.info(" ");
			LOGGER.info(" ");
			LOGGER.info(" ");
		}
		catch (Exception e)
		{
			LOGGER.fatal("Fatal error reading XMI!!", e);
		}
		finally
		{
			// release the transaction.
			rep.endTrans();
			MDRManager.getDefault().shutdownAll();
			try
			{
				fileInput.close();
			}
			catch (IOException io)
			{
				LOGGER.fatal("Error. Specified file does not exist.");
			}

			XMIUtilities.cleanUpRepository();
		}
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	private static void validate(String args[]) throws Exception
	{
		if (args.length == 0)
		{
			throw new Exception("Please Specify the file name to be imported");
		}
		if (args.length < 2)
		{
			throw new Exception("Please Specify the Main Container names CSV file name.");
		}
		if (args.length < 3)
		{
			throw new Exception("Please Specify the name of the Package to be imported");
		}
		if (args[0] != null && args[0].trim().length() == 0)
		{
			throw new Exception("Please Specify the file name to be imported");
		}
		if (args[1] != null && args[1].trim().length() == 0)
		{
			throw new Exception("Please Specify the Main Container names CSV file name.");
		}
		if (args[2] != null && args[2].trim().length() == 0)
		{
			throw new Exception("Please Specify the name of the Package to be imported");
		}
	}

	/**
	 * @param path
	 * @return
	 * @throws IOException
	 */
	private static List<String> readFile(String path) throws IOException
	{
		List<String> containerNames = new ArrayList<String>();
		File file = new File(path);

		BufferedReader bufRdr = new BufferedReader(new FileReader(file));
		String line = null;

		//read each line of text file
		while ((line = bufRdr.readLine()) != null)
		{
			StringTokenizer stoken = new StringTokenizer(line, ",");
			while (stoken.hasMoreTokens())
			{
				//get next token and store it in the array
				containerNames.add(stoken.nextToken());
			}
		}

		return containerNames;
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

	/** Finds "UML" package -> this is the topmost package of UML metamodel - that's the
	 * package that needs to be instantiated in order to create a UML extent
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
	 */
	private static MofPackage getUmlPackage(ModelPackage umlMM)
	{
		// iterate through all instances of package
		//LOGGER.info("Here");
		for (Iterator it = umlMM.getMofPackage().refAllOfClass().iterator(); it.hasNext();)
		{
			MofPackage pkg = (MofPackage) it.next();
			LOGGER.info("\n\nName = " + pkg.getName());

			// is the package topmost and is it named "UML"?
			if (pkg.getContainer() == null && "UML".equals(pkg.getName()))
			{
				// yes -> return it
				return pkg;
			}
		}
		// a topmost package named "UML" could not be found
		return null;
	}

	/**
	 * @param mainContainerList
	 * @return
	 */
	private static Map<Long, String> getMainContainerNames(
			List<ContainerInterface> mainContainerList)
	{
		Map<Long, String> mnContnerIdVsName = new HashMap<Long, String>();
		for (ContainerInterface container : mainContainerList)
		{
			mnContnerIdVsName.put(container.getId(), container.getCaption());
		}
		return mnContnerIdVsName;
	}

	/**
	 * @throws DAOException
	 * @throws DynamicExtensionsSystemException
	 * @throws UserNotAuthorizedException
	 * @throws BizLogicException
	 * @throws DynamicExtensionsApplicationException
	 *
	 */
	private static void associateHookEntity(List<ContainerInterface> mainContainerList,
			List<Long> clinicalStudyIds, boolean isEditedXmi, String domainModelName,
			boolean addQueryPaths, HibernateDAO hibernatedao) throws DAOException,
			DynamicExtensionsSystemException, BizLogicException, UserNotAuthorizedException,
			DynamicExtensionsApplicationException
	{
		String DEAppName = DynamicExtensionDAO.getInstance().getAppName();
		List<ClinicalStudyEvent> eventsList = getAllEvents(clinicalStudyIds);

		if (!isEditedXmi)
		{//Add Case				
			DefaultBizLogic defaultBizLogic = BizLogicFactory.getDefaultBizLogic();
			defaultBizLogic.setAppName(DEAppName);
			//hooked with the record Entry
			List staticEntityList = defaultBizLogic.retrieve(AbstractMetadata.class.getName(),
					Constants.NAME, "edu.wustl.clinportal.domain.RecordEntry");
			EntityInterface staticEntity = ((EntityInterface) staticEntityList.get(0));
			addNewIntegrationObjects(staticEntity, eventsList, mainContainerList, domainModelName,
					addQueryPaths, isEditedXmi, hibernatedao);
		}
		else
		{//Edit Case

			List staticEntityList = hibernatedao.retrieve(AbstractMetadata.class.getName(),
					Constants.NAME, "edu.wustl.clinportal.domain.RecordEntry");
			EntityInterface staticEntity = ((EntityInterface) staticEntityList.get(0));

			if (eventsList != null && !eventsList.isEmpty())
			{
				for (ClinicalStudyEvent event : eventsList)
				{//Retriving Study Form Context list
					List<StudyFormContext> studyFrmConxtLst = hibernatedao.retrieve(
							StudyFormContext.class.getName(), "staticRecordId", event.getId());

					if (studyFrmConxtLst != null && !studyFrmConxtLst.isEmpty())
					{//this is an existing event					

						List<ContainerInterface> newContainersList = getNewContainersList(
								mainContainerList, studyFrmConxtLst);
						if (!newContainersList.isEmpty())
						{
							addNewIntegrationObjects(staticEntity, eventsList, newContainersList,
									domainModelName, addQueryPaths, isEditedXmi, hibernatedao);
						}
					}
					else
					{//New event has been added
						addNewIntegrationObjects(staticEntity, eventsList, mainContainerList,
								domainModelName, addQueryPaths, isEditedXmi, hibernatedao);

					}
				}
			}
			else
			{
				List<ContainerInterface> newContainers = new ArrayList<ContainerInterface>();
				List<ContainerInterface> existingContainers = new ArrayList<ContainerInterface>();
				for (ContainerInterface mainContainer : mainContainerList)
				{
					boolean isAssonPresent = false;
					EntityInterface entity = (EntityInterface) mainContainer.getAbstractEntity();
					Collection<AssociationInterface> allAssociations = staticEntity
							.getAllAssociations();
					for (AssociationInterface association : allAssociations)
					{
						if (association.getTargetEntity().getId().compareTo(entity.getId()) == 0)
						{
							isAssonPresent = true;
							break;
						}
					}
					if (!isAssonPresent)
					{
						newContainers.add(mainContainer);
					}
					else
					{
						existingContainers.add(mainContainer);
					}

				}
				if (!newContainers.isEmpty())
				{
					addNewIntegrationObjects(staticEntity, eventsList, newContainers,
							domainModelName, addQueryPaths, isEditedXmi, hibernatedao);
				}
				if (!existingContainers.isEmpty())
				{
					JDBCDAO jdbcdao = null;
					String appName = CommonServiceLocator.getInstance().getAppName();
					IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
					try
					{
						jdbcdao = daoFactory.getJDBCDAO();
						jdbcdao.openSession(null);
						LOGGER.info(" ");
						LOGGER.info("Now adding new query paths  for new containers....");
						LOGGER.info(" ");
						for (ContainerInterface mainContainer : existingContainers)
						{
							AnnotationUtil.addNewPathsForExistingMainContainers(staticEntity,
									((EntityInterface) mainContainer.getAbstractEntity()), true,
									jdbcdao, hibernatedao);
						}
						LOGGER.info(" ");
					}
					catch (Exception e)
					{
						LOGGER
								.error("Error while adding new paths for existing main containers",
										e);
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

		}

	}

	/**
	 * @param mainContainerList
	 * @param entityMapConditionList
	 * @return
	 */
	private static List<ContainerInterface> getNewContainersList(
			List<ContainerInterface> mainContainerList, List<StudyFormContext> studyFrmCntextLst)
	{
		List<ContainerInterface> newContainersList = new ArrayList<ContainerInterface>();
		for (ContainerInterface container : mainContainerList)
		{
			boolean containerFound = false;
			for (StudyFormContext studyFormContext : studyFrmCntextLst)
			{
				if (container.getId().compareTo(studyFormContext.getContainerId()) == 0)
				{
					containerFound = true;
					break;
				}
			}
			if (!containerFound)
			{
				newContainersList.add(container);
			}
		}
		return newContainersList;
	}

	/**
	 * @param mainContainerList
	 * @param staticEntity
	 * @param typeId
	 * @param eventIds
	 * @throws DynamicExtensionsSystemException
	 * @throws DAOException
	 * @throws BizLogicException
	 * @throws UserNotAuthorizedException
	 * @throws DynamicExtensionsApplicationException
	 */
	private static void addNewIntegrationObjects(EntityInterface staticEntity,
			List<ClinicalStudyEvent> eventsList, List<ContainerInterface> mainContainerList,
			String domainModelName, boolean addQueryPaths, boolean isEditedXmi,
			HibernateDAO hibernatedao) throws DynamicExtensionsSystemException, DAOException,
			BizLogicException, UserNotAuthorizedException, DynamicExtensionsApplicationException
	{
		Map<Long, String> mnConterIdVsName = getMainContainerNames(mainContainerList);
		Collection<StudyFormContext> formContextColl = new HashSet<StudyFormContext>();
		for (ClinicalStudyEvent event : eventsList)
		{
			getStudyFormContext(event, mnConterIdVsName, domainModelName, formContextColl);
			event.setStudyFormContextCollection(formContextColl);
		}
		insertStudyFormContextobject(formContextColl, hibernatedao);

		List<AssociationInterface> asso = new ArrayList<AssociationInterface>();
		for (Iterator<ContainerInterface> iterator = mainContainerList.iterator(); iterator
				.hasNext();)
		{

			ContainerInterface containerInterface = iterator.next();
			AssociationInterface association = AnnotationUtil.addAssociationForEntities(
					staticEntity, (EntityInterface) containerInterface.getAbstractEntity());

			asso.add(association);
			staticEntity.addAssociation(association);
		}

		if (hibernatedao != null)
		{
			EntityManager.getInstance().persistEntityMetadataForAnnotation(staticEntity, true,
					false, null, hibernatedao);
		}
		else
		{
			EntityManager.getInstance().persistEntityMetadataForAnnotation(staticEntity, true,
					false, null);
		}

		for (AssociationInterface associationInterface : asso)
		{
			EntityManager.getInstance().addAssociationColumn(associationInterface);
		}

		if (addQueryPaths)
		{
			JDBCDAO jdbcdao = null;
			String appName = CommonServiceLocator.getInstance().getAppName();
			IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
			try
			{
				jdbcdao = daoFactory.getJDBCDAO();
				jdbcdao.openSession(null);
				for (ContainerInterface mainContainer : mainContainerList)
				{
					AnnotationUtil.addQueryPaths(staticEntity, ((EntityInterface) mainContainer
							.getAbstractEntity()), true, null, jdbcdao);
				}
			}
			catch (Exception e)
			{
				LOGGER.error("Error while adding query paths", e);
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
	 * @param entityMapColl
	 * @param staticEntity
	 * @throws BizLogicException
	 * @throws UserNotAuthorizedException
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	/*private static void insertEntityMaps(List<EntityMap> entityMapColl, EntityInterface staticEntity)
			throws BizLogicException, UserNotAuthorizedException,
			DynamicExtensionsApplicationException, DynamicExtensionsSystemException
	{
		//		Saving all entity maps
		for (EntityMap entityMap : entityMapColl)
		{
			AnnotationBizLogic annotation = new AnnotationBizLogic();
			if (entityMap.getId() == null)
			{
				annotation.insert(entityMap, Constants.HIBERNATE_DAO);
			}
			else
			{
				annotation.updateEntityMap(entityMap);
			}

		}
	}*/

	/**
	 * This method is used to insert study form object to the tables
	 * @param studyFrmConxtCol
	 * @throws BizLogicException
	 * @throws UserNotAuthorizedException
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 * @throws DAOException 
	 */
	private static void insertStudyFormContextobject(Collection<StudyFormContext> studyFrmConxtCol,
			HibernateDAO hibernateDAO) throws BizLogicException, UserNotAuthorizedException,
			DynamicExtensionsApplicationException, DynamicExtensionsSystemException, DAOException
	{
		if (hibernateDAO != null)
		{
			for (StudyFormContext studyFormContext : studyFrmConxtCol)
			{
				if (studyFormContext.getId() == null)
				{
					hibernateDAO.insert(studyFormContext);
				}
				else
				{
					hibernateDAO.update(studyFormContext);
				}
			}

		}
		else
		{
			AnnotationBizLogic annotation = new AnnotationBizLogic();
			//		Saving Study Form Object
			for (StudyFormContext studyFormContext : studyFrmConxtCol)
			{
				// for add case
				if (studyFormContext.getId() == null)
				{
					annotation.insert(studyFormContext, Constants.HIBERNATE_DAO);
				}
				//for updating existing object
				else
				{
					annotation.update(studyFormContext, Constants.HIBERNATE_DAO);
				}

			}
		}
	}

	/**
	 * @param mainContainerList
	 * @param staticEntity
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DAOException
	 */
	/*private static List<EntityMap> createAllEntityMaps(List<ContainerInterface> mainContainerList,
			EntityInterface staticEntity) throws DynamicExtensionsSystemException, DAOException,
			BizLogicException
	{
		String DEAppName = DynamicExtensionDAO.getInstance().getAppName();
		DefaultBizLogic defaultBizLogic = BizLogicFactory.getDefaultBizLogic();
		defaultBizLogic.setAppName(DEAppName);
		List<EntityMap> entityMapColl = new ArrayList<EntityMap>();
		//		creating 1 entitymap for each form
		for (ContainerInterface container : mainContainerList)
		{
			List<EntityMap> entityMapList = defaultBizLogic.retrieve(EntityMap.class.getName(),
					"containerId", container.getId());
			EntityMap entityMap = null;
			if (entityMapList != null && !entityMapList.isEmpty())
			{
				entityMap = entityMapList.get(0);
			}
			else
			{
				entityMap = getEntityMap(container, staticEntity.getId());
			}
			entityMapColl.add(entityMap);
		}
		return entityMapColl;
	}*/

	/**
	 * @param clinicalStudyIds
	 * @return
	 * @throws DAOException
	 */
	private static List<ClinicalStudyEvent> getAllEvents(List<Long> clinicalStudyIds)
			throws BizLogicException
	{

		List<ClinicalStudyEvent> eventsList = new ArrayList<ClinicalStudyEvent>();
		DefaultBizLogic defaultBizLogic = BizLogicFactory.getDefaultBizLogic();
		for (Long clinicalStudyId : clinicalStudyIds)
		{
			List eventCollection = defaultBizLogic.retrieve(ClinicalStudyEvent.class.getName(),
					"clinicalStudy.id", clinicalStudyId);
			LOGGER.info("EVENTS FOR STUDY ID " + clinicalStudyId + " ARE " + eventCollection);
			eventsList.addAll(eventCollection);
		}
		return eventsList;
	}

	/**
	 * @param entityMap
	 * @param collectionProtocolName
	 * @param typeId
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DAOException
	 */
	/*	private static Collection<FormContext> getFormContext(EntityMap entityMap,
				ClinicalStudyEvent event, Object typeId, Map<Long, String> mnContrIdVsName)
				throws DynamicExtensionsSystemException, DAOException
		{
			Collection<FormContext> formContextColl = new HashSet<FormContext>();
			FormContext formContext = new FormContext();
			formContext.setEntityMap(entityMap);

			Collection<EntityMapCondition> entyMapCondCol = new HashSet<EntityMapCondition>();
			if (event != null)
			{
				entyMapCondCol.add(getEntityMapCondition(formContext, event.getId(), typeId));
			}
			//No of entries
			int noOfEntries = event.getNoOfEntries().intValue();
			if (noOfEntries == -1)
			{
				formContext.setNoOfEntries(Integer.valueOf(1));
				formContext.setIsInfiniteEntry(Boolean.TRUE);
			}
			else
			{
				formContext.setNoOfEntries(event.getNoOfEntries());
				formContext.setIsInfiniteEntry(Boolean.FALSE);
			}
			String formName = mnContrIdVsName.get(entityMap.getContainerId());
			//Form Label
			formContext.setStudyFormLabel(event.getCollectionPointLabel() + formName);
			formContext.setEntityMapConditionCollection(entyMapCondCol);

			formContextColl.add(formContext);

			return formContextColl;
		}
	*/
	/**
	 * 
	 * @param event
	 * @param mnContrIdVsName
	 * @param domainModelName
	 * @param formContextColl
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DAOException
	 */
	private static Collection<StudyFormContext> getStudyFormContext(ClinicalStudyEvent event,
			Map<Long, String> mnContrIdVsName, String domainModelName,
			Collection<StudyFormContext> formContextColl) throws DynamicExtensionsSystemException,
			DAOException
	{
		//No of entries
		Set<Long> keySet = mnContrIdVsName.keySet();
		for (Long key : keySet)
		{

			StudyFormContext studyFormContext = new StudyFormContext();
			String formName = mnContrIdVsName.get(key);
			studyFormContext.setStudyFormLabel(domainModelName + event.getCollectionPointLabel()
					+ formName);
			studyFormContext.setContainerId(key);
			studyFormContext.setClinicalStudyEvent(event);
			studyFormContext.setActivityStatus("Active");
			formContextColl.add(studyFormContext);
		}
		return formContextColl;
	}

	/**
	 * @param formContext
	 * @param collectionProtocolName
	 * @param typeId
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DAOException
	 */
	/*private static EntityMapCondition getEntityMapCondition(FormContext formContext, Long eventId,
			Object typeId) throws DynamicExtensionsSystemException, DAOException
	{
		EntityMapCondition entityMapCond = new EntityMapCondition();
		entityMapCond.setStaticRecordId(eventId);
		entityMapCond.setTypeId(((Long) typeId));
		entityMapCond.setFormContext(formContext);
		return entityMapCond;
	}*/

	/**
	 * @param container
	 * @param hookEntityName
	 * @return
	 * @throws DAOException
	 * @throws DynamicExtensionsSystemException
	 */
	/*private static EntityMap getEntityMap(ContainerInterface container, Object staticEntityId)
			throws DAOException, DynamicExtensionsSystemException
	{
		EntityMap entityMap = new EntityMap();
		entityMap.setContainerId(container.getId());
		entityMap.setCreatedBy("");
		entityMap.setCreatedDate(new Date());
		entityMap.setLinkStatus(AnnotationConstants.STATUS_ATTACHED);
		entityMap.setStaticEntityId(((Long) staticEntityId));
		return entityMap;
	}*/

	/**
	 * @param hookEntityName
	 * @return
	 * @throws DAOException
	 */
	private static Object getObjectIdentifier(String whereColumnValue, String selectObjName,
			String whereColumnName) throws BizLogicException, DAOException
	{
		String DEAppName = DynamicExtensionDAO.getInstance().getAppName();
		DefaultBizLogic defaultBizLogic = BizLogicFactory.getDefaultBizLogic();
		defaultBizLogic.setAppName(DEAppName);
		String[] selectColName = {Constants.SYSTEM_IDENTIFIER};
		String[] whereColName = {whereColumnName};
		Object[] whereColValue = {whereColumnValue};
		String[] whereColCondition = {Constants.EQUALS};
		String joinCondition = Constants.AND_JOIN_CONDITION;

		QueryWhereClause queryWhereClause = new QueryWhereClause(selectObjName);
		queryWhereClause.getWhereCondition(whereColName, whereColCondition, whereColValue,
				joinCondition);
		List identifiers = defaultBizLogic.retrieve(selectObjName, selectColName, queryWhereClause);
		Object obj = null;
		if (identifiers != null && !identifiers.isEmpty())
		{
			obj = identifiers.get(0);
		}
		return obj;
	}

	/**
	 * @param egroupId
	 * @param hibernateDAO
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 * @throws IOException
	 * @throws DAOException
	 */
	private static List<ContainerInterface> getMainContainerList(Long egroupId,
			HibernateDAO hibernateDAO) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException, IOException, DAOException
	{
		String hql = "from EntityGroup entityGroup   join entityGroup.mainContainerCollection as mainContainers 	where entityGroup.id = '"
				+ egroupId + "'";
		List objectList = hibernateDAO.executeQuery(hql);
		List<ContainerInterface> mainContList = new ArrayList<ContainerInterface>();
		for (int cnt = 0; cnt < objectList.size(); cnt++)
		{
			Object[] o = (Object[]) objectList.get(cnt);

			mainContList.add((ContainerInterface) o[1]);
		}
		return mainContList;
	}
}