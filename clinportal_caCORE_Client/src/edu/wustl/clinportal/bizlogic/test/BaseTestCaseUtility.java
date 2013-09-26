/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */


package edu.wustl.clinportal.bizlogic.test;

import java.text.ParseException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;

import edu.wustl.clinportal.domain.Address;
import edu.wustl.clinportal.domain.CancerResearchGroup;
import edu.wustl.clinportal.domain.ClinicalStudy;
import edu.wustl.clinportal.domain.ClinicalStudyConsentTier;
import edu.wustl.clinportal.domain.ClinicalStudyConsentTierResponse;
import edu.wustl.clinportal.domain.ClinicalStudyEvent;
import edu.wustl.clinportal.domain.ClinicalStudyRegistration;
import edu.wustl.clinportal.domain.Department;
import edu.wustl.clinportal.domain.Institution;
import edu.wustl.clinportal.domain.Participant;
import edu.wustl.clinportal.domain.Race;
import edu.wustl.clinportal.domain.ReportedProblem;
import edu.wustl.clinportal.domain.Site;
import edu.wustl.clinportal.domain.StudyFormContext;
import edu.wustl.clinportal.domain.User;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.global.Status;
import edu.wustl.common.util.logger.Logger;

public class BaseTestCaseUtility
{

	public static ClinicalStudy initClinicalStudy()
	{
		ClinicalStudy clinicalStudy = new ClinicalStudy();

		Collection<ClinicalStudyConsentTier> consentTierColl = BaseTestCaseUtility
				.initConsentTier();
		clinicalStudy.setConsentTierCollection(consentTierColl);
		clinicalStudy.setDescriptionURL("");
		clinicalStudy.setActivityStatus(Constants.ACTIVITY_STATUS_ACTIVE);
		clinicalStudy.setEndDate(null);
		clinicalStudy.setEnrollment(null);
		clinicalStudy.setIrbIdentifier("testirb" + UniqueKeyGeneratorUtil.getUniqueKey());
		String strTitle = "CS_" + UniqueKeyGeneratorUtil.getUniqueKey();
		clinicalStudy.setTitle(strTitle);
		clinicalStudy.setShortTitle(strTitle.toLowerCase());
		//clinicalStudy.setEnrollment(2);

		try
		{
			//clinicalStudy.setStartDate(Utility.parseDate("08/15/2003", Utility.datePattern("08/15/1975")));
			clinicalStudy.setStartDate(new java.util.Date("08/15/2003"));

		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		Collection<ClinicalStudyEvent> clinicalStudyEventCollectionList = new LinkedHashSet<ClinicalStudyEvent>();
		ClinicalStudyEvent clinicalStudyEvent = null;
		for (int eventCount = 0; eventCount < 2; eventCount++)
		{
			clinicalStudyEvent = new ClinicalStudyEvent();
			setClinicalStudyEvent(clinicalStudyEvent);
			clinicalStudyEvent.setClinicalStudy(clinicalStudy);
			clinicalStudyEventCollectionList.add(clinicalStudyEvent);
		}
		clinicalStudy.setClinicalStudyEventCollection(clinicalStudyEventCollectionList);

		User principalInvestigator = new User();
		principalInvestigator.setId(new Long("1"));
		clinicalStudy.setPrincipalInvestigator(principalInvestigator);

		//User clinicalStudyCordinator = new User();
		//clinicalStudyCordinator.setId(new Long("2"));

		Collection clinicalStudyCordinatorCollection = new HashSet();
		//clinicalStudyCordinatorCollection.add(coordUsr);
		//clinicalStudyCordinatorCollection.add(clinicalStudyCordinator);
		clinicalStudy.setCoordinatorCollection(clinicalStudyCordinatorCollection);

		return clinicalStudy;
	}

	public static void setClinicalStudyEvent(ClinicalStudyEvent clinicalStudyEvent)
	{
		clinicalStudyEvent.setStudyCalendarEventPoint(1);
		clinicalStudyEvent.setCollectionPointLabel("PreStudy1" + Math.random());
		clinicalStudyEvent.setActivityStatus(Status.ACTIVITY_STATUS_ACTIVE.toString());
		clinicalStudyEvent.setNoOfEntries(1);

		Collection<StudyFormContext> studyFormContextCollection = new HashSet<StudyFormContext>();
		StudyFormContext studyFormContext = new StudyFormContext();
		studyFormContext.setActivityStatus(Status.ACTIVITY_STATUS_ACTIVE.toString());
		studyFormContext.setCanHaveMultipleRecords(false);
		studyFormContext.setClinicalStudyEvent(clinicalStudyEvent);
		studyFormContext.setStudyFormLabel("TestLabel" + UniqueKeyGeneratorUtil.getUniqueKey());

		// Need to change this
		studyFormContext.setContainerId(new Long(1));

		studyFormContextCollection.add(studyFormContext);

		clinicalStudyEvent.setStudyFormContextCollection(studyFormContextCollection);
	}

	public static void updateClinicalStudy(ClinicalStudy clinicalStudy)
	{
		System.out.println("BaseTestCaseUtility.updateClinicalStudy()" + clinicalStudy.getTitle());
		clinicalStudy.setIrbIdentifier("up" + UniqueKeyGeneratorUtil.getUniqueKey());
		clinicalStudy.setShortTitle("CS1" + UniqueKeyGeneratorUtil.getUniqueKey());
		clinicalStudy.setDescriptionURL("");
		clinicalStudy.setActivityStatus("Active"); //Active
		//clinicalStudy.setEndDate(null);
		clinicalStudy.setTitle("cs updated title" + UniqueKeyGeneratorUtil.getUniqueKey());
		try
		{
			//clinicalStudy.setStartDate(Utility.parseDate("08/15/1975", Utility.datePattern("08/15/1975")));
			clinicalStudy.setStartDate(new java.util.Date("08/15/1975"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static ClinicalStudyEvent initClinicalStudyEvent()
	{
		ClinicalStudyEvent clinicalStudyEvent = new ClinicalStudyEvent();
		clinicalStudyEvent.setActivityStatus(Status.ACTIVITY_STATUS_ACTIVE.toString());
		clinicalStudyEvent.setStudyCalendarEventPoint(1);

		//Setting collection point label
		clinicalStudyEvent.setCollectionPointLabel("User entered value"
				+ UniqueKeyGeneratorUtil.getUniqueKey());

		return clinicalStudyEvent;
	}

	public static User initUser()
	{
		User userObj = new User();
		userObj.setEmailAddress("testNB" + UniqueKeyGeneratorUtil.getUniqueKey() + "@admin.com");
		userObj.setLoginName(userObj.getEmailAddress());
		userObj.setLastName("last" + UniqueKeyGeneratorUtil.getUniqueKey());
		userObj.setFirstName("name" + UniqueKeyGeneratorUtil.getUniqueKey());
		userObj.setAdminuser(false);
		Address address = new Address();
		address.setStreet("Main street");
		address.setCity("New hampshier");
		address.setState("Alabama");
		address.setZipCode("12345");
		address.setCountry("United States");
		address.setPhoneNumber("21222324");
		address.setFaxNumber("21222324");

		userObj.setAddress(address);

		Institution inst = new Institution();
		inst.setId(new Long(1));
		userObj.setInstitution(inst);

		Department department = new Department();
		department.setId(new Long(1));
		userObj.setDepartment(department);

		CancerResearchGroup cancerResearchGroup = new CancerResearchGroup();
		cancerResearchGroup.setId(new Long(1));
		userObj.setCancerResearchGroup(cancerResearchGroup);

		userObj.setRoleId("1");
		userObj.setActivityStatus("Active");
		userObj.setPageOf(Constants.PAGEOF_SIGNUP);
		return userObj;
	}

	public static User initUpdateUser(User userObj)
	{
		userObj.setEmailAddress("sup" + UniqueKeyGeneratorUtil.getUniqueKey() + "@sup.com");
		userObj.setLoginName(userObj.getEmailAddress());
		userObj.setLastName("last" + UniqueKeyGeneratorUtil.getUniqueKey());
		userObj.setFirstName("name" + UniqueKeyGeneratorUtil.getUniqueKey());

		Address address = new Address();
		address.setStreet("Main street");
		address.setCity("New hampshier");
		address.setState("Alabama");
		address.setZipCode("12345");
		address.setCountry("United States");
		address.setPhoneNumber("21222324");
		address.setFaxNumber("21222324");

		userObj.setAddress(address);

		Institution inst = (Institution) TestCaseUtility.getObjectMap(Institution.class);
		//new Institution();
		//inst.setId(new Long(1));

		userObj.setInstitution(inst);

		Department department = (Department) TestCaseUtility.getObjectMap(Department.class);
		//new Department();
		//department.setId(new Long(1));
		userObj.setDepartment(department);

		CancerResearchGroup cancerResearchGroup = (CancerResearchGroup) TestCaseUtility
				.getObjectMap(CancerResearchGroup.class);
		//new CancerResearchGroup();
		//cancerResearchGroup.setId(new Long(1));
		userObj.setCancerResearchGroup(cancerResearchGroup);

		userObj.setRoleId("1");
		userObj.setActivityStatus("Active");

		return userObj;
	}

	public static Department initDepartment()
	{
		Department dept = new Department();
		dept.setName("department name" + UniqueKeyGeneratorUtil.getUniqueKey());
		return dept;
	}

	public static void updateDepartment(Department department)
	{
		department.setName("dt" + UniqueKeyGeneratorUtil.getUniqueKey());
	}

	public static ClinicalStudyRegistration initClinicalStudyRegistration(Participant participant)
	{
		//Logger.configure("");
		ClinicalStudyRegistration clinicalStudyRegistration = new ClinicalStudyRegistration();

		ClinicalStudy clinicalStudy = (ClinicalStudy) TestCaseUtility
				.getObjectMap(ClinicalStudy.class);
		//new CollectionProtocol();
		//collectionProtocol.setId(new Long(3));
		clinicalStudyRegistration.setClinicalStudy(clinicalStudy);

		clinicalStudyRegistration.setParticipant(participant);

		clinicalStudyRegistration.setClinicalStudyParticipantIdentifier("");
		clinicalStudyRegistration.setActivityStatus("Active");
		try
		{
			//clinicalStudyRegistration.setRegistrationDate(Utility.parseDate("08/15/1975", Utility.datePattern("08/15/1975")));
			clinicalStudyRegistration.setRegistrationDate(new java.util.Date("08/15/2003"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		//Setting Consent Tier Responses.
		try
		{
			//clinicalStudyRegistration.setConsentSignatureDate(Utility.parseDate("11/23/2006", Utility.datePattern("11/23/2006")));
			clinicalStudyRegistration.setConsentSignatureDate(new java.util.Date("08/15/2003"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		//clinicalStudyRegistration.setSignedConsentDocumentURL("D:/doc/consentDoc.doc");
		User user = (User) TestCaseUtility.getObjectMap(User.class);
		//User user = new User();
		//user.setId(new Long(1));
		clinicalStudyRegistration.setConsentWitness(user);

		Collection<ClinicalStudyConsentTierResponse> consentTierResponseCollection = new HashSet<ClinicalStudyConsentTierResponse>();
		Collection consentTierCollection = clinicalStudy.getConsentTierCollection();
		Iterator consentTierItr = consentTierCollection.iterator();
		while (consentTierItr.hasNext())
		{
			ClinicalStudyConsentTier consentTier = (ClinicalStudyConsentTier) consentTierItr.next();
			ClinicalStudyConsentTierResponse consentResponse = new ClinicalStudyConsentTierResponse();
			consentResponse.setConsentTier(consentTier);
			consentResponse.setResponse("Yes");
			consentTierResponseCollection.add(consentResponse);
		}
		//		ConsentTierResponse r1 = new ConsentTierResponse();
		//		ConsentTier consentTier = new ConsentTier();
		//		consentTier.setId(new Long(1));
		//		r1.setConsentTier(consentTier);
		//		r1.setResponse("Yes");
		//		consentTierResponseCollection.add(r1);
		//		
		//		ConsentTierResponse r2 = new ConsentTierResponse();
		//		ConsentTier consentTier2 = new ConsentTier();
		//		consentTier2.setId(new Long(2));
		//		r2.setConsentTier(consentTier2);
		//		r2.setResponse("Yes");
		//		consentTierResponseCollection.add(r2);
		//		
		//		ConsentTierResponse r3 = new ConsentTierResponse();
		//		ConsentTier consentTier3 = new ConsentTier();
		//		consentTier3.setId(new Long(3));
		//		r3.setConsentTier(consentTier3);
		//		r3.setResponse("No");
		//		consentTierResponseCollection.add(r3);
		//		
		//		collectionProtocolRegistration.setConsentTierResponseCollection(consentTierResponseCollection);		
		//		SpecimenCollectionGroup specimenCollectionGroup = createSCG(collectionProtocolRegistration);
		//		
		//		Collection specimenCollectionGroupCollection = new HashSet<SpecimenCollectionGroup>(); 
		//		collectionProtocolRegistration.setSpecimenCollectionGroupCollection(specimenCollectionGroupCollection);

		return clinicalStudyRegistration;
	}

	//public Collection initConsentTier(boolean empty)
	public static Collection<ClinicalStudyConsentTier> initConsentTier()
	{
		//		Setting consent tiers for this protocol.
		Collection<ClinicalStudyConsentTier> consentTierColl = new HashSet<ClinicalStudyConsentTier>();
		/*if(!empty)
		 {*/
		ClinicalStudyConsentTier c1 = new ClinicalStudyConsentTier();
		c1.setStatement("Consent for aids research");
		consentTierColl.add(c1);
		ClinicalStudyConsentTier c2 = new ClinicalStudyConsentTier();
		c2.setStatement("Consent for cancer research");
		consentTierColl.add(c2);
		ClinicalStudyConsentTier c3 = new ClinicalStudyConsentTier();
		c3.setStatement("Consent for Tb research");
		consentTierColl.add(c3);
		//}
		return consentTierColl;
	}

	public static Institution initInstitution()
	{
		Institution institutionObj = new Institution();
		institutionObj.setName("inst" + UniqueKeyGeneratorUtil.getUniqueKey());
		return institutionObj;
	}

	public static void updateInstitution(Institution institution)
	{
		institution.setName("inst" + UniqueKeyGeneratorUtil.getUniqueKey());
	}

	public static CancerResearchGroup initCancerResearchGrp()
	{
		CancerResearchGroup cancerResearchGroup = new CancerResearchGroup();
		cancerResearchGroup.setName("crgName" + UniqueKeyGeneratorUtil.getUniqueKey());
		return cancerResearchGroup;
	}

	public static void updateCancerResearchGrp(CancerResearchGroup cancerResearchGroup)
	{
		cancerResearchGroup.setName("crgName" + UniqueKeyGeneratorUtil.getUniqueKey());
	}

	public static Site initSite()
	{
		Site siteObj = new Site();
		User userObj = (User) TestCaseUtility.getObjectMap(User.class);
		//		User userObj = new User();
		//		userObj.setId(new Long(1));

		siteObj.setEmailAddress("admin@admin.com");
		siteObj.setName("sit" + UniqueKeyGeneratorUtil.getUniqueKey());
		siteObj.setType("Laboratory");
		siteObj.setActivityStatus("Active");
		siteObj.setCoordinator(userObj);

		Address addressObj = new Address();
		addressObj.setCity("Saint Louis");
		addressObj.setCountry("United States");
		addressObj.setFaxNumber("555-55-5555");
		addressObj.setPhoneNumber("123678");
		addressObj.setState("Missouri");
		addressObj.setStreet("4939 Children's Place");
		addressObj.setZipCode("63110");
		siteObj.setAddress(addressObj);
		return siteObj;
	}

	public static void updateSite(Site siteObj)
	{
		siteObj.setEmailAddress("admin1@admin.com");
		siteObj.setName("updatedSite" + UniqueKeyGeneratorUtil.getUniqueKey());
		siteObj.setType("Repository");
		siteObj.setActivityStatus("Active");
		siteObj.getAddress().setCity("Saint Louis1");
		siteObj.getAddress().setCountry("United States");
		siteObj.getAddress().setFaxNumber("777-77-77771");
		siteObj.getAddress().setPhoneNumber("1236781");
		siteObj.getAddress().setState("Missouri");
		siteObj.getAddress().setStreet("4939 Children's Place1");
		siteObj.getAddress().setZipCode("63111");
	}

	public static Participant initParticipant()
	{
		Participant participant = new Participant();
		participant.setLastName("lname" + UniqueKeyGeneratorUtil.getUniqueKey());
		participant.setFirstName("fname" + UniqueKeyGeneratorUtil.getUniqueKey());
		participant.setGender("Male");
		participant.setEthnicity("Unknown");
		participant.setSexGenotype("XX");

		Collection<Race> raceCollection = new HashSet<Race>();
		Race race = new Race();
		race.setRaceName("White");
		race.setParticipant(participant);
		raceCollection.add(race);
		//raceCollection.add("White");
		//raceCollection.add("Asian");
		participant.setRaceCollection(raceCollection);
		participant.setActivityStatus("Active");
		participant.setEthnicity("Hispanic or Latino");
		Logger.out.info("Participant added successfully -->Name:" + participant.getFirstName()
				+ " " + participant.getLastName());
		return participant;
	}

	public static Participant initParticipantWithCSR()
	{
		Participant participant = new Participant();
		participant.setLastName("lname" + UniqueKeyGeneratorUtil.getUniqueKey());
		participant.setFirstName("fname" + UniqueKeyGeneratorUtil.getUniqueKey());
		participant.setGender("Male");
		participant.setEthnicity("Unknown");
		participant.setSexGenotype("XX");

		Collection<Race> raceCollection = new HashSet<Race>();
		Race race = new Race();
		race.setRaceName("White");
		race.setParticipant(participant);
		raceCollection.add(race);
		//raceCollection.add("White");
		//raceCollection.add("Asian");
		participant.setRaceCollection(raceCollection);
		participant.setActivityStatus(Constants.ACTIVITY_STATUS_ACTIVE);
		participant.setEthnicity("Hispanic or Latino");
		Logger.out.info("Participant added successfully -->Name:" + participant.getFirstName()
				+ " " + participant.getLastName());
		Collection<ClinicalStudyRegistration> clinicalStudyRegistrationCollection = new HashSet<ClinicalStudyRegistration>();
		ClinicalStudyRegistration clinicalStudyRegistration = initClinicalStudyRegistration(participant);
		clinicalStudyRegistrationCollection.add(clinicalStudyRegistration);
		participant.setClinicalStudyRegistrationCollection(clinicalStudyRegistrationCollection);
		return participant;
	}

	public static void updateParticipant(Participant participant)
	{
		participant.setLastName("last" + UniqueKeyGeneratorUtil.getUniqueKey());
		participant.setFirstName("frst" + UniqueKeyGeneratorUtil.getUniqueKey());
		participant.setMiddleName("mdl" + UniqueKeyGeneratorUtil.getUniqueKey());

		participant.setVitalStatus("Alive"); //Dead
		participant.setGender("Male"); //
		participant.setSexGenotype(""); //XX

		Collection<Race> raceCollection = new HashSet<Race>();
		Race race = new Race();
		race.setRaceName("Black or African American");
		race.setParticipant(participant);
		raceCollection.add(race);
		//raceCollection.add("Black or African American"); //White
		//raceCollection.add("Unknown"); //Asian
		participant.setRaceCollection(raceCollection);
		participant.setActivityStatus("Active"); //Active
		participant.setEthnicity("Unknown"); //Hispanic or Latino

		Collection participantMedicalIdentifierCollection = new HashSet();
		participant
				.setParticipantMedicalIdentifierCollection(participantMedicalIdentifierCollection);

	}

	public static ReportedProblem initReportedProblem()
	{
		ReportedProblem reportedProblem = new ReportedProblem();
		reportedProblem.setSubject("Reporting Problem in application"
				+ UniqueKeyGeneratorUtil.getUniqueKey());
		reportedProblem.setMessageBody("Problem message body");
		reportedProblem.setAffiliation("affiliation");
		reportedProblem.setFrom("abc@admin.com");
		reportedProblem.setNameOfReporter("abc");
		return reportedProblem;
	}

	public static void updateReportedProblem(ReportedProblem reportedProblem)
	{
		reportedProblem.setSubject("Reporting Problem in clinportal application");
	}

}