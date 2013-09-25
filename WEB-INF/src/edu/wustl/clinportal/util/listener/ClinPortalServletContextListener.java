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
 * This class handles ServletContext  related operations on web server startup
 * @author Falguni_Sachde
 * @author Rukhsana
 * @author Deepali
 */

package edu.wustl.clinportal.util.listener;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.sf.ehcache.CacheException;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.clinportal.action.annotations.AnnotationConstants;
import edu.wustl.clinportal.bizlogic.ClinicalStudyRegistrationBizLogic;
import edu.wustl.clinportal.bizlogic.ParticipantBizLogic;
import edu.wustl.clinportal.domain.Address;
import edu.wustl.clinportal.domain.Participant;
import edu.wustl.clinportal.domain.Site;
import edu.wustl.clinportal.domain.User;
import edu.wustl.clinportal.util.CatissueCoreCacheManager;
import edu.wustl.clinportal.util.ProtectionGroups;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.clinportal.util.global.DefaultValueManager;
import edu.wustl.clinportal.util.global.Utility;
import edu.wustl.clinportal.util.global.Variables;
import edu.wustl.common.cde.CDEManager;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;
import edu.wustl.common.util.CVSTagReader;
import edu.wustl.common.util.XMLPropertyHandler;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.query.util.listener.QueryCoreServletContextListenerUtil;
import edu.wustl.simplequery.bizlogic.QueryBizLogic;
import edu.wustl.simplequery.query.Client;

public class ClinPortalServletContextListener implements ServletContextListener
{

	private static Logger logger = Logger.getCommonLogger(ClinPortalServletContextListener.class);

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent sce)
	{

		/**
		 * Getting Application Properties file path
		 */
		String propDirPath = sce.getServletContext().getRealPath("WEB-INF")
				+ System.getProperty("file.separator") + "classes";
		String appResourcesPath = propDirPath + System.getProperty("file.separator")
				+ sce.getServletContext().getInitParameter("applicationproperties");

		CommonServiceLocator.getInstance().setAppHome(sce.getServletContext().getRealPath(""));
		System.out.println(":::::::::::::Application home ::::::::::::"
				+ CommonServiceLocator.getInstance().getAppHome());

		/**
		 * Configuring the Logger class so that it can be utilized by
		 * the entire application
		 */
		LoggerConfig.configureLogger(propDirPath);
		try
		{
			
			logger.info("Initializing Clinportal application");
			System.out.println("Initializing Clinportal application");
			/**
			 * Initializing ApplicationProperties with the class 
			 * corresponding to resource bundle of the application
			 */
			ApplicationProperties.initBundle(sce.getServletContext().getInitParameter(
					"resourcebundleclass"));

			/**
			 * Getting and storing Home path for the application
			 */
			Variables.applicationHome = sce.getServletContext().getRealPath("");

			/**
			 * Creating Logs Folder inside catissue home
			 */

			File logfolder = new File(Variables.applicationHome + "/Logs");
			if (!logfolder.exists())
			{
				logfolder.mkdir();
			}
		}
		catch (Exception ex)
		{
			logger.error(ex.getMessage(), ex);
		}

		addDefaultProtectionGroupsToMap();

		Variables.applicationName = ApplicationProperties.getValue("app.name");
		Variables.applicationVersion = ApplicationProperties.getValue("app.version");

		QueryBizLogic queryBizLogic = new QueryBizLogic();
		queryBizLogic.initializeQueryData();

		initClinportQueryData();
		logApplnInfo();

		//Initialize CDE Manager
		initCDEManager();

		//Initialize XML properties Manager
		try
		{
			String path = System.getProperty("app.propertiesFile");
			XMLPropertyHandler.init(path);

			DefaultValueManager.validateAndInitDefaultValueMap();

			File propertiesDirPath = new File(path);
			Variables.propertiesDirPath = propertiesDirPath.getParent();
			logger.debug("propetiesDirPath " + Variables.propertiesDirPath);

			String propertyValue = XMLPropertyHandler.getValue("server.port");
			logger.debug("property Value " + propertyValue);
		}
		catch (Exception ex)
		{
			logger
					.error("Could not initialized application, Error in creating XML Property handler.");
			logger.error(ex.getMessage(), ex);
		}

		logger.debug("System property : " + System.getProperty("gov.nih.nci.security.configFile"));
		logger.debug("System property : "
				+ System.getProperty("edu.wustl.catissuecore.contactUsFile"));

		initCatissueCache();

		initEntityCache();

		int maxTreeNodeLimit = Integer.parseInt(XMLPropertyHandler
				.getValue(Constants.MAXIMUM_TREE_NODE_LIMIT));

		Variables.maximumTreeNodeLimit = maxTreeNodeLimit;
		//Added list of objects on which read denied has to be checked while filtration of result for csm-query performance.(Supriya Dankh)
		//A map that contains entity name as key and sql to get Collection Protocol Ids for that entity id as value for csm-query performance.(Supriya Dankh)
		edu.wustl.query.util.global.Utility.setReadDeniedAndEntitySqlMap();

		createAliasAndPageOfMap();
		
		QueryCoreServletContextListenerUtil.contextInitialized(sce,"java:/query");
	}

	/**
	 * 
	 */
	private void initClinportQueryData()
	{
		// For ClinicalStudyRegistration
		Vector identifiedData = new Vector();
		identifiedData.add("REGISTRATION_DATE");
		Client.identifiedDataMap.put("ClinicalStudyRegistration", identifiedData);

		// For Event Entry
		identifiedData = new Vector();
		identifiedData.add("ENCOUNTER_DATE");
		Client.identifiedDataMap.put("ClinicalStudyEventEntry", identifiedData);

	}

	/**
	 *
	 */
	private void initEntityCache()
	{
		// Initialising Entity cache
		try
		{
			CatissueCoreCacheManager cacheManager = CatissueCoreCacheManager.getInstance();

			//Stores the list of system entities into the cache.-- Vishvesh.
			// AnnotationUtil.getSystemEntityList();
			//Stores the ids in the cache
			Long participantId = Utility.getEntityId(AnnotationConstants.ENTITY_NAME_PARTICIPANT);
			cacheManager.addObjectToCache(AnnotationConstants.PARTICIPANT_ENTITY_ID, participantId);

			Long csEntityId = Utility.getEntityId(AnnotationConstants.ENTITY_NAME_CLINICAL_STUDY);
			cacheManager.addObjectToCache(AnnotationConstants.CLINICAL_STUDY_ENTITY_ID, csEntityId);

			Long csEventEntityId = Utility
					.getEntityId(AnnotationConstants.ENTITY_NAME_CLINICAL_STUDY_EVENT);
			cacheManager.addObjectToCache(AnnotationConstants.CLINICAL_STUDY_EVENT_ENTITY_ID,
					csEventEntityId);

			Long recEntryEntityId = Utility
					.getEntityId(AnnotationConstants.ENTITY_NAME_RECORD_ENTRY);
			//System.out.println("recEntryEntityId----------" + recEntryEntityId);
			cacheManager.addObjectToCache(AnnotationConstants.RECORD_ENTRY_ENTITY_ID,
					recEntryEntityId);
			logger.debug("***********ENTITY_NAME_RECORD_ENTRY: " + recEntryEntityId);
			EntityCache.getInstance();
			Variables.isCatissueModelAvailable = Utility.isCatissueModelAvailable();
			logger.debug("Entity Cache is initialised...");
		}
		catch (Exception e)
		{
			logger.debug("Exception occured while initialising entity cache");
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	public void initCatissueCache()
	{
		/**
		 *  Following code is added for caching the Map of all participants.
		 *  Get the map of participants from ParticipantBizLogic and add it to cache
		 */

		Map participantMap = null;
		List partRegInfoList = null;
		try
		{
			IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
			ParticipantBizLogic bizlogic = (ParticipantBizLogic) factory
					.getBizLogic(Constants.PARTICIPANT_FORM_ID);

			participantMap = bizlogic.getAllParticipants();

			ClinicalStudyRegistrationBizLogic cBizLogic = (ClinicalStudyRegistrationBizLogic) factory
					.getBizLogic(Constants.CLINICAL_STUDY_REGISTRATION_FORM_ID);
			partRegInfoList = cBizLogic.getAllParticipantRegistrationInfo();
		}
		catch (Exception e)
		{
			logger
					.debug("Exception occured while getting List of Participants and Participant's reg info");
			e.printStackTrace();
		}

		try
		{
			// getting instance of catissueCoreCacheManager and adding participantMap to cache
			CatissueCoreCacheManager cacheManager = CatissueCoreCacheManager.getInstance();
			cacheManager.addObjectToCache(Constants.MAP_OF_PARTICIPANTS, (HashMap) participantMap);
			cacheManager.addObjectToCache(Constants.LIST_OF_REGISTRATION_INFO,
					(Vector) partRegInfoList);
		}
		catch (Exception e)
		{
			logger.debug("Exception occured while creating instance of CatissueCoreCacheManager");
			e.printStackTrace();
		}
	}

	/**
	 *
	 */
	private void initCDEManager()
	{
		try
		{
			CDEManager.init();
		}
		catch (Exception ex)
		{
			logger.error("Could not initialized application, Error in creating CDE manager.");
			logger.error(ex.getMessage(), ex);
		}
	}

	/**
	 * 
	 *
	 */
	private void logApplnInfo()
	{
		StringBuffer fileName = new StringBuffer();
		fileName.append(Variables.applicationHome).append(System.getProperty("file.separator"))
				.append(ApplicationProperties.getValue("application.version.file"));

		CVSTagReader cvsTagReader = new CVSTagReader();
		String cvsTag = cvsTagReader.readTag(fileName.toString());
		Variables.applicationCvsTag = cvsTag;
		logger.info("========================================================");
		logger.info("Application Information");
		logger.info("Name: " + Variables.applicationName);
		logger.info("Version: " + Variables.applicationVersion);
		logger.info("CVS TAG: " + Variables.applicationCvsTag);
		logger.info("Path: " + Variables.applicationHome);
		logger.info("========================================================");
	}

	/**
	 * 
	 *
	 */
	private void addDefaultProtectionGroupsToMap()
	{
		Map<String, String[]> protectionGroups = new HashMap<String, String[]>();
		protectionGroups
				.put(Site.class.getName(), new String[]{ProtectionGroups.PUBLIC_DATA_GROUP});
		protectionGroups.put(Address.class.getName(),
				new String[]{ProtectionGroups.PUBLIC_DATA_GROUP});
		protectionGroups
				.put(User.class.getName(), new String[]{ProtectionGroups.PUBLIC_DATA_GROUP});
		protectionGroups.put(Participant.class.getName(),
				new String[]{ProtectionGroups.PUBLIC_DATA_GROUP});

		edu.wustl.security.global.Constants.STATIC_PG_FOR_OBJ_TYPES.putAll(protectionGroups);
	}

	/**
	 * TO create map of Alias verses corresponding pageOf values. 
	 * This is required in Simple Query Edit feature, It contains mapping of alias name for the query tables & the corresponding pageOf values.
	 * Patch ID: SimpleSearchEdit_9
	 */
	private void createAliasAndPageOfMap()
	{
		Variables.aliasAndPageOfMap.put(Constants.ALIAS_BIOHAZARD, Constants.PAGE_OF_BIOHAZARD);
		Variables.aliasAndPageOfMap.put(Constants.ALIAS_CANCER_RESEARCH_GROUP,
				Constants.PAGE_OF_CANCER_RESEARCH_GROUP);
		Variables.aliasAndPageOfMap.put(Constants.ALIAS_COLLECTION_PROTOCOL,
				Constants.PAGE_OF_COLLECTION_PROTOCOL);
		Variables.aliasAndPageOfMap.put(Constants.ALIAS_COLLECTION_PROTOCOL_REG,
				Constants.PAGE_OF_COLLECTION_PROTOCOL_REG);
		Variables.aliasAndPageOfMap.put(Constants.ALIAS_DEPARTMENT, Constants.PAGE_OF_DEPARTMENT);
		Variables.aliasAndPageOfMap.put(Constants.ALIAS_DISTRIBUTION,
				Constants.PAGE_OF_DISTRIBUTION);
		Variables.aliasAndPageOfMap.put(Constants.ALIAS_DISTRIBUTION_PROTOCOL,
				Constants.PAGE_OF_DISTRIBUTION_PROTOCOL);
		Variables.aliasAndPageOfMap.put(Constants.ALIAS_DISTRIBUTION_ARRAY,
				Constants.PAGE_OF_DISTRIBUTION_ARRAY);
		Variables.aliasAndPageOfMap.put(Constants.ALIAS_INSTITUTE, Constants.PAGE_OF_INSTITUTE);
		Variables.aliasAndPageOfMap.put(Constants.ALIAS_PARTICIPANT, Constants.PAGE_OF_PARTICIPANT);
		Variables.aliasAndPageOfMap.put(Constants.ALIAS_SITE, Constants.PAGE_OF_SITE);
		Variables.aliasAndPageOfMap.put(Constants.ALIAS_SPECIMEN, Constants.PAGEOF_NEW_SPECIMEN);
		Variables.aliasAndPageOfMap.put(Constants.ALIAS_SPECIMEN_ARRAY,
				Constants.PAGE_OF_SPECIMEN_ARRAY);
		Variables.aliasAndPageOfMap.put(Constants.ALIAS_SPECIMEN_ARRAY_TYPE,
				Constants.PAGE_OF_SPECIMEN_ARRAY_TYPE);
		Variables.aliasAndPageOfMap.put(Constants.ALIAS_SPECIMEN_COLLECTION_GROUP,
				Constants.PAGE_OF_SPECIMEN_COLLECTION_GROUP);
		Variables.aliasAndPageOfMap.put(Constants.ALIAS_STORAGE_CONTAINER,
				Constants.PAGE_OF_STORAGE_CONTAINER);
		Variables.aliasAndPageOfMap.put(Constants.ALIAS_STORAGE_TYPE,
				Constants.PAGE_OF_STORAGE_TYPE);
		Variables.aliasAndPageOfMap.put(Constants.ALIAS_USER, Constants.PAGE_OF_USER);
		Variables.aliasAndPageOfMap.put(Constants.ALIAS_CLINICAL_STUDY,
				Constants.PAGE_OF_CLINICAL_STUDY);
		logger.debug("Initialization of aliasAndPageOf Map completed...");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent sce)
	{
		//  shut down the cacheManager
		try
		{
			CatissueCoreCacheManager cacheManager = CatissueCoreCacheManager.getInstance();
			cacheManager.shutdown();
		}
		catch (CacheException e)
		{
			logger.debug("Exception occured while shutdown instance of CatissueCoreCacheManager");
			e.printStackTrace();
		}
	}
}