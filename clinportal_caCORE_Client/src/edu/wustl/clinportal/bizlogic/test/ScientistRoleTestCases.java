
package edu.wustl.clinportal.bizlogic.test;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.wustl.clinportal.domain.CancerResearchGroup;
import edu.wustl.clinportal.domain.ClinicalStudy;
import edu.wustl.clinportal.domain.ClinicalStudyRegistration;
import edu.wustl.clinportal.domain.Department;
import edu.wustl.clinportal.domain.Institution;
import edu.wustl.clinportal.domain.Participant;
import edu.wustl.clinportal.domain.Site;
import edu.wustl.common.util.logger.Logger;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationServiceProvider;
import gov.nih.nci.system.comm.client.ClientSession;

public class ScientistRoleTestCases extends ClinportalBaseTestCase
{

	static ApplicationService appService = null;

	public void setUp()
	{
		appService = ApplicationServiceProvider.getApplicationService();
		ClientSession cs = ClientSession.getInstance();
		try
		{
			cs.startSession("sjain22@wustl.edu", "Login234");
		}
		catch (Exception ex)
		{
			System.out.println(ex.getMessage());
			ex.printStackTrace();
			fail("Fail to create connection");
			System.exit(1);
		}
	}

	public void testAddDepartmentWithScientistLogin()
	{
		try
		{
			Department dept = (Department) BaseTestCaseUtility.initDepartment();
			dept = (Department) appService.createObject(dept);
			System.out.println("Department object created successfully");
			assertFalse("Test failed. Dept successfully added", true);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("Access denied: You are not authorized to perform this operation. ", true);
		}
	}

	public void testUpdateDepartmentWithScientistLogin()
	{
		try
		{
			Department department = (Department) TestCaseUtility.getObjectMap(Department.class);
			BaseTestCaseUtility.updateDepartment(department);
			Logger.out.info("Updating Department object------->" + department);
			Department updatedDepartment = (Department) appService.updateObject(department);
			Logger.out.info("Department object successfully updated ---->" + updatedDepartment);
			assertFalse("Test failed. Dept successfully updated", true);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("Access denied: You are not authorized to perform this operation. ", true);
		}
	}

	public void testAddInstitutionWithScientistLogin()
	{
		try
		{
			Institution institution = BaseTestCaseUtility.initInstitution();
			System.out.println(institution);
			institution = (Institution) appService.createObject(institution);
			System.out.println("Inst Object created successfully");
			assertFalse("Test failed.Inst successfully added", true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue("Access denied: You are not authorized to perform this operation. ", true);
		}
	}

	/*public void testUpdateInstitutionWithScientistLogin()
	{

		try
		{
			Institution institution = (Institution) TestCaseUtility.getObjectMap(Institution.class);
			BaseTestCaseUtility.updateInstitution(institution);
			Logger.out.info("Updating Institution object------->" + institution);
			Institution updatedInst = (Institution) appService.updateObject(institution);
			Logger.out.info("Institution object successfully updated ---->" + updatedInst);
			assertFalse("Test failed. Inst successfully updated", true);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("Access denied: You are not authorized to perform this operation. ", true);
		}
	}

	public void testAddCancerResearchGrpWithScientistLogin()
	{
		try
		{
			CancerResearchGroup crg = BaseTestCaseUtility.initCancerResearchGrp();
			crg = (CancerResearchGroup) appService.createObject(crg);
			System.out.println("CRG object created successfully");
			assertFalse("Test failed. CRG successfully added", true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue("Access denied: You are not authorized to perform this operation. ", true);
		}
	}

	public void testUpdateCancerResearchGrpWithScientistLogin()
	{

		try
		{
			CancerResearchGroup crg = (CancerResearchGroup) TestCaseUtility.getObjectMap(CancerResearchGroup.class);
			BaseTestCaseUtility.updateCancerResearchGrp(crg);
			Logger.out.info("updating Cancer Research Group object------->" + crg);
			CancerResearchGroup updatedCRG = (CancerResearchGroup) appService.updateObject(crg);
			Logger.out.info("Cancer Research Group object successfully updated ---->" + updatedCRG);
			assertFalse("Test failed. CRG successfully updated", true);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("Access denied: You are not authorized to perform this operation. ", true);
		}
	}*/

	/*  public void testAddUser()
	 {
	 try{
	 User user = BaseTestCaseUtility.initUser();
	 user = (User)appService.createObject(user);
	 Logger.out.info("Object created successfully");
	 System.out.println("Object created successfully");
	 assertFalse("Test failed.User successfully added", true);
	 }
	 catch(Exception e){
	 e.printStackTrace();
	 assertTrue("Access denied: You are not authorized to perform this operation. ", true);
	 }
	 }*/

	/*public void testAddSiteWithScientistLogin()
	{
		try
		{
			Site site = BaseTestCaseUtility.initSite();
			System.out.println(site);
			site = (Site) appService.createObject(site);
			System.out.println("Site object created successfully");
			assertFalse("Test failed. Site successfully added", true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue("Access denied: You are not authorized to perform this operation. ", true);
		}
	}

	public void testUpdateSiteWithScientistLogin()
	{
		try
		{
			Site site = (Site) TestCaseUtility.getObjectMap(Site.class);
			BaseTestCaseUtility.updateSite(site);
			Logger.out.info("Updating Site object------->" + site);
			Site updatedSite = (Site) appService.updateObject(site);
			Logger.out.info("Site object successfully updated ---->" + updatedSite);
			assertFalse("Test failed. Site successfully updated", true);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("Access denied: You are not authorized to perform this operation. ", true);
		}
	}

	public void testAddClinicalStudyWithScientistLogin()
	{
		try
		{
			ClinicalStudy clinicalStudy = BaseTestCaseUtility.initClinicalStudy();
			clinicalStudy = (ClinicalStudy) appService.createObject(clinicalStudy);
			TestCaseUtility.setObjectMap(clinicalStudy, ClinicalStudy.class);
			assertFalse("Test failed. Clinical Study successfully added", true);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("Access denied: You are not authorized to perform this operation. ", true);
		}
	}

	public void testUpdateClinicalStudyWithScientistLogin()
	{
		try
		{
			ClinicalStudy clinicalStudy = (ClinicalStudy) TestCaseUtility.getObjectMap(ClinicalStudy.class);
			Logger.out.info("Updating Clinical Study object------->" + clinicalStudy);
			BaseTestCaseUtility.updateClinicalStudy(clinicalStudy);
			ClinicalStudy updatedClinicalStudy = (ClinicalStudy) appService.updateObject(clinicalStudy);
			Logger.out.info("Clinical Study object successfully updated ---->" + updatedClinicalStudy);
			assertFalse("Test failed. Clinical Study successfully added", true);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("Access denied: You are not authorized to perform this operation. ", true);
		}
	}

	public void testSearchDepartmetWithScientistLogin()
	{
		Department dept = new Department();
		Logger.out.info("Searching Department object");
		dept.setId(new Long(1));
		try
		{
			List resultList = appService.search(Department.class, dept);
			for (Iterator resultsIterator = resultList.iterator(); resultsIterator.hasNext();)
			{
				Department returnedDepartment = (Department) resultsIterator.next();
				Logger.out.info("Department Object is successfully Found ---->  :: " + returnedDepartment.getName());
			}
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertFalse("Could not find Department Object", true);
		}
	}

	public void testSearchCancerResearchGrpWithScientistLogin()
	{
		CancerResearchGroup crg = new CancerResearchGroup();
		Logger.out.info("Searching Cancer Research Group object");
		crg.setId(new Long(1));

		try
		{
			List resultList = appService.search(CancerResearchGroup.class, crg);
			for (Iterator resultsIterator = resultList.iterator(); resultsIterator.hasNext();)
			{
				CancerResearchGroup returnedInst = (CancerResearchGroup) resultsIterator.next();
				Logger.out.info("Cancer Research Group Object is successfully Found ---->  :: " + returnedInst.getName());
				// System.out.println(" Domain Object is successfully Found ---->  :: " + returnedDepartment.getName());
			}
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertFalse("Could not find Cancer Research Group Object", true);

		}
	}

	public void testSearchInstitutionWithScientistLogin()
	{
		Institution institution = new Institution();
		Logger.out.info("Searching Institution object");
		institution.setId(new Long(1));

		try
		{
			List resultList = appService.search(Institution.class, institution);
			for (Iterator resultsIterator = resultList.iterator(); resultsIterator.hasNext();)
			{
				Institution returnedInst = (Institution) resultsIterator.next();
				Logger.out.info("Institution Object is successfully Found ---->  :: " + returnedInst.getName());
				System.out.println("Institution Object is successfully Found ---->  :: " + returnedInst.getName());
			}
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertFalse("Could not find Institution Object", true);

		}
	}

	public void testSearchSiteWithScientistLogin()
	{
		Site site = new Site();
		Logger.out.info("Searching Site object");
		site.setId(new Long(1));
		try
		{
			List resultList = appService.search(Site.class, site);
			for (Iterator resultsIterator = resultList.iterator(); resultsIterator.hasNext();)
			{
				Site returnedSite = (Site) resultsIterator.next();
				Logger.out.info("Site Object is successfully Found ---->  :: " + returnedSite.getName());
				System.out.println("Site Object is successfully Found ---->  :: " + returnedSite.getName());
			}
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertFalse("Could not find Site Object", true);

		}
	}

	public void testSearchClinicalStudy()
	{
		ClinicalStudy clinicalStudy = new ClinicalStudy();
		ClinicalStudy cachedClinicalStudy = (ClinicalStudy) TestCaseUtility.getObjectMap(ClinicalStudy.class);
		cachedClinicalStudy.setId((Long) cachedClinicalStudy.getId());
		Logger.out.info("Searching Clinical Study object");
		try
		{
			List resultList = appService.search(ClinicalStudy.class, clinicalStudy);
			for (Iterator resultsIterator = resultList.iterator(); resultsIterator.hasNext();)
			{
				ClinicalStudy returnedClinicalStudy = (ClinicalStudy) resultsIterator.next();
				Logger.out.info("Clinical Study object is successfully Found ---->  :: " + returnedClinicalStudy.getTitle());
			}
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			//assertFalse("Doesnot found collection protocol", true);
			fail("Could not found Clinical Study");
		}
	}

	// verify the test case
	public void testAddParticipant()
	{
		try
		{
			Participant participant = BaseTestCaseUtility.initParticipant();
			System.out.println(participant);
			participant = (Participant) appService.createObject(participant);
			//TestCaseUtility.setObjectMap(participant, Participant.class);
			System.out.println("Participant object should not be created");
			assertFalse("Participant should not be created", true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue("Failed to add participant", true);

		}
	}*/

	//	 verify the test case
	/*public void testAddParticipantWithCSR()
	 {
	 try
	 {
	 Participant participant = BaseTestCaseUtility.initParticipantWithCSR();
	 System.out.println(participant);
	 participant = (Participant) appService.createObject(participant);
	 Collection clinicalStudyRegistrationCollection = participant.getClinicalStudyRegistrationCollection();
	 Iterator cprItr = clinicalStudyRegistrationCollection.iterator();
	 ClinicalStudyRegistration clinicalStudyRegistration = (ClinicalStudyRegistration) cprItr.next();

	 TestCaseUtility.setObjectMap(clinicalStudyRegistration, ClinicalStudyRegistration.class);
	 TestCaseUtility.setObjectMap(participant, Participant.class);
	 System.out.println("Participant object created successfully");
	 assertFalse("Participant registration created successfully", true);
	 }
	 catch (Exception e)
	 {
	 e.printStackTrace();
	 assertTrue("Failed to create Participant registration", true);

	 }
	 }*/

	//	 verify the test case
	/*public void testUpdateParticipant()
	 {
	 Participant participant = BaseTestCaseUtility.initParticipant();
	 Logger.out.info("Updating Participant object------->" + participant);
	 try
	 {
	 participant = (Participant) appService.createObject(participant);
	 BaseTestCaseUtility.updateParticipant(participant);
	 Participant updatedParticipant = (Participant) appService.updateObject(participant);
	 Logger.out.info("Participantobject successfully updated ---->" + updatedParticipant);
	 assertFalse("Participant successfully updated ---->" + updatedParticipant, true);
	 }
	 catch (Exception e)
	 {
	 Logger.out.error(e.getMessage(), e);
	 e.printStackTrace();
	 assertTrue("Failed to update Participant", true);

	 }
	 }
	
	public void testSearchParticipant()
	{
		Participant participant = new Participant();
		Logger.out.info("Searching Participant object");
		participant.setId(new Long(1));
		try
		{
			List resultList = appService.search(Participant.class, participant);
			for (Iterator resultsIterator = resultList.iterator(); resultsIterator.hasNext();)
			{
				Participant returnedParticipant = (Participant) resultsIterator.next();
				Logger.out.info("Participant Object is successfully Found ---->  :: " + returnedParticipant.getFirstName() + " "
						+ returnedParticipant.getLastName());
			}
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertFalse("Could not find Participant Object", true);

		}
	}*/

}