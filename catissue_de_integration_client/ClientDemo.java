/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */



import dedemo.BasePathologyAnnotation;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.entitymanager.EntityManagerUtil;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.clinportal.domain.ClinicalStudy;
import edu.wustl.clinportal.domain.ClinicalStudyEvent;
import edu.wustl.clinportal.domain.ClinicalStudyEventEntry;
import edu.wustl.clinportal.domain.ClinicalStudyRegistration;
import edu.wustl.clinportal.domain.EventEntry;
import edu.wustl.clinportal.domain.Participant;
import edu.wustl.clinportal.domain.RecordEntry;
import edu.wustl.clinportal.domain.StudyFormContext;
import edu.wustl.clinportal.domain.User;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationServiceProvider;
import gov.nih.nci.system.comm.client.ClientSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;


/**
 * ClientDemo.java demonstrates various ways to execute searches with and without
 * using Application Service Layer (convenience layer that abstracts building criteria
 * Uncomment different scenarios below to demonstrate the various types of searches
 */

public class ClientDemo
{

	private final static String STATIC_ENTITY_CLASS_NAME = "edu.wustl.clinportal.domain.RecordEntry";
	private final static String STATIC_CONDITION_ENTITY_CLASS_NAME = "edu.wustl.clinportal.domain.ClinicalStudyEvent";
	
	private final static String DE_CLASS_NAME = "BasePathologyAnnotation";
	
	static ApplicationService appServiceDEEntity = null;
	static ApplicationService appServiceCatissue = null;

	private static Long PARTICIPANT_IDENTIFIER = new Long(1);
	private static Long CLINICAL_STUDY_EVENT_ID = new Long(1);
	private static Long CLINICAL_STUDY_EVENT_ENTRY_ID = new Long(4);
	
	private static String CLINICAL_STUDY_TITLE = "CS44";
	private static String IRB_IDENTIFIER = "4444";
	private static String COLLECTION_POINT_LABEL = "Label44";
	
	private static String APPLICATION_URL = "https://localhost:8443/clinportal/http/remoteService";
	private static String JBOSS_HOME = "E:/jboss-4.2.2.GA";
	
	private static String USERNAME = "admin@admin.com";
	private static String PASSWORD = "Login123";

	static  List eventEntryList = new ArrayList();

	private static EntityManagerInterface entityManager = EntityManager.getInstance();

	public static void main(String[] args)
	{
		System.out.println("*** ClientDemo...");

		
		try
		{
			
			System.setProperty("javax.net.ssl.trustStore", JBOSS_HOME + "/server/default/conf/chap8.keystore");
			
			//caTissue Service
			initCaTissueService();
			ClientSession cs = ClientSession.getInstance();
			cs.startSession(USERNAME, PASSWORD);
			
			createClinicalStudy();
			
			/*EventEntry eventEntry = createEventEntry();
			
			RecordEntry re= new RecordEntry();
			re.setEventEntry(eventEntry);
	
			Long entityid = entityManager.getEntityId("BasePathologyAnnotation");
			Long containerID = entityManager.getContainerByEntityIdentifier(entityid).getId();
			StudyFormContext studyFormContext = getStudyFormContext(containerID);
			
			re.setStudyFormContext(studyFormContext);
			RecordEntry recordEntry = (RecordEntry)appServiceCatissue.createObject(re);

			BasePathologyAnnotation bp = new BasePathologyAnnotation();
			EntityInterface entityInterface = entityManager.getEntityByName("BasePathologyAnnotation");
			Long nextid = EntityManagerUtil.getNextIdentifier(entityInterface.getTableProperties().getName());
			bp.setId(nextid);
			bp.setComments("My Comments ABC");
			dedemo.RecordEntry dedemoRecordEntry  = new dedemo.RecordEntry();
			dedemoRecordEntry.setId(recordEntry.getId());
			bp.setRecordEntry(dedemoRecordEntry);
			
			
			initDEService();

			appServiceDEEntity.createObject(bp);*/
			
//			System.out.println("cid"+entityManager.getNextIdentifierForEntity("BasePathologyAnnotation"));
//			StudyFormContext sf = new StudyFormContext();
//			sf.setContainerId(cid);
//			ClinicalStudy cstudy = new ClinicalStudy();
//			cstudy.setTitle("CS2");
//			
//			List l = appServiceCatissue.search(ClinicalStudyEvent.class.getName(), cstudy);
//			System.out.println("l.size(): "+l.size());
//			for(int i=0;i<l.size();i++)
//			{
//				ClinicalStudyEvent returnedEvent= (ClinicalStudyEvent)l.get(i);
//				ClinicalStudyEvent newEvent = new ClinicalStudyEvent();
//				HashSet studyFormContextCollection = new HashSet();
//				studyFormContextCollection.add(sf);
//				newEvent.setStudyFormContextCollection(studyFormContextCollection);
//				newEvent.setId(returnedEvent.getId());
//				List list = appServiceCatissue.search(StudyFormContext.class.getName(), newEvent);
//				System.out.println("list.size(): "+list.size());
//				for(int j=0;j<list.size();j++)
//				{
//					StudyFormContext f = (StudyFormContext)list.get(j);
//					System.out.println(f.getStudyFormLabel());
//					studyFormContext = f;
//				}
//			}
			
			System.out.println("Finished");
			cs.terminateSession();
		}
		catch (Exception ex)
		{
			System.out.println(ex.getMessage());
			ex.printStackTrace();
			return;
		}
		
	}
	
	/**
	 * Method creates clinical study object
	 * @throws Exception
	 */
	private static void createClinicalStudy() throws Exception
	{
		ClinicalStudy cs = new ClinicalStudy();
		cs.setActivityStatus("Active");
		cs.setTitle(CLINICAL_STUDY_TITLE);
		cs.setShortTitle(CLINICAL_STUDY_TITLE);
		cs.setStartDate(new Date());
		User user = new User();
		user.setId(1L);
		cs.setPrincipalInvestigator(user);
		cs.setIrbIdentifier(IRB_IDENTIFIER);
		Collection<ClinicalStudyEvent> clinicalStudyEventCollection = new HashSet<ClinicalStudyEvent>();

		ClinicalStudyEvent csEvent = new ClinicalStudyEvent();
		csEvent.setActivityStatus("Active");
		csEvent.setClinicalStudy(cs);
		csEvent.setCollectionPointLabel(COLLECTION_POINT_LABEL);
		csEvent.setIsInfiniteEntry(true);
		csEvent.setStudyCalendarEventPoint(1);
		csEvent.setNoOfEntries(-1);

		Collection<StudyFormContext> studyFormContextCollection = new HashSet<StudyFormContext>();
		
		
	/*	String containerCaption = "BasePathologyAnnotation";
		System.out.println(containerCaption);
		StudyFormContext studyFormContext = new StudyFormContext();
		studyFormContext.setCanHaveMultipleRecords(true);
		studyFormContext.setContainerId(entityManager.getContainerIdByCaption(containerCaption));
		studyFormContext.setActivityStatus("Active");
		studyFormContext.setClinicalStudyEvent(csEvent);
		studyFormContext.setStudyFormLabel(containerCaption);
		studyFormContextCollection.add(studyFormContext);*/
		
		csEvent.setStudyFormContextCollection(studyFormContextCollection);
		clinicalStudyEventCollection.add(csEvent);
		cs.setClinicalStudyEventCollection(clinicalStudyEventCollection);

		appServiceCatissue.createObject(cs);
		
		System.out.println("CREATE CLINICAL STUDY SUCCESSFULLY");
	}
	
	/**
	 * 
	 * @return
	 */
	private static EventEntry createEventEntry() throws Exception
	{
		ClinicalStudyRegistration clinicalStudyRegistration = new ClinicalStudyRegistration();
		clinicalStudyRegistration.setId(getClinicalStudyRegistrationID());
		ClinicalStudyEvent clinicalStudyEvent = new ClinicalStudyEvent();
		clinicalStudyEvent.setId(CLINICAL_STUDY_EVENT_ID);
		EventEntry eventEntry = new EventEntry();
		eventEntry.setActivityStatus("Active");
		eventEntry.setClinicalStudyRegistration(clinicalStudyRegistration);
		eventEntry.setClinicalStudyEvent(clinicalStudyEvent);
		Date encounteredDate = new Date(System.currentTimeMillis());
		eventEntry.setEncounterDate(encounteredDate);
		
		EventEntry returnedEventEntry = (EventEntry)appServiceCatissue.createObject(eventEntry);
		EventEntry newEventEntry = new EventEntry();
		newEventEntry.setId(returnedEventEntry.getId());
		//newEventEntry.setId(21L);
		return newEventEntry;
		
	}
	/**
	 * @return
	 */
	private static void initCaTissueService()
	{
		
		appServiceCatissue = ApplicationServiceProvider
				.getRemoteInstance(APPLICATION_URL);
		System.out.println("appServiceCatissue = " + appServiceCatissue);
	}

	private static void initDEService()
	{
		appServiceDEEntity = ApplicationServiceProvider
				.getRemoteInstance("http://localhost:8080/dedemo/http/remoteService");
	}
/**
 * Get ClinicalStudy Registration ID Given Participant ID and Clinical Study Title
 * @return
 * @throws Exception
 */
	private static Long getClinicalStudyRegistrationID() throws Exception
	{
		Participant participant = new Participant();
		participant.setId(PARTICIPANT_IDENTIFIER);
		
		ClinicalStudy cs = new ClinicalStudy();
		cs.setTitle(CLINICAL_STUDY_TITLE);
		
		ClinicalStudyRegistration clinicalStudyRegistration = new ClinicalStudyRegistration();
		clinicalStudyRegistration.setParticipant(participant);
		clinicalStudyRegistration.setClinicalStudy(cs);
		List l = appServiceCatissue.search(ClinicalStudyRegistration.class.getName(), clinicalStudyRegistration);
		ClinicalStudyRegistration returnedCSR = (ClinicalStudyRegistration)l.get(0);
		return returnedCSR.getId();
	}
	
	private static StudyFormContext getStudyFormContext(Long containerID) throws Exception
	{
		ClinicalStudyEvent event = new ClinicalStudyEvent();
		event.setId(CLINICAL_STUDY_EVENT_ID);
		HashSet studyFormContextCollection = new HashSet();
		StudyFormContext sf = new StudyFormContext();
		sf.setContainerId(containerID);
		studyFormContextCollection.add(sf);
		event.setStudyFormContextCollection(studyFormContextCollection);
		List l = appServiceCatissue.search(StudyFormContext.class, event);
		StudyFormContext returnedForm = (StudyFormContext)l.get(0);
		System.out.println("returnedForm "+returnedForm.getStudyFormLabel());
		StudyFormContext newStudyFormContext = new StudyFormContext();
		newStudyFormContext.setId(returnedForm.getId());
		return newStudyFormContext;
		
		
	}
}
