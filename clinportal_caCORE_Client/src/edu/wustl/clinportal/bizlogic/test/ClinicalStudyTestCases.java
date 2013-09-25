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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.wustl.clinportal.domain.ClinicalStudy;
import edu.wustl.clinportal.domain.ClinicalStudyRegistration;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.logger.Logger;

import edu.wustl.clinportal.util.global.Constants;

public class ClinicalStudyTestCases extends ClinportalBaseTestCase
{

	AbstractDomainObject domainObject = null;

	public void testAddClinicalStudy()
	{
		try
		{
			ClinicalStudy clinicalStudy = BaseTestCaseUtility.initClinicalStudy();
			clinicalStudy = (ClinicalStudy) appService.createObject(clinicalStudy);
			TestCaseUtility.setObjectMap(clinicalStudy, ClinicalStudy.class);
			assertTrue("Clinical Study Object added successfully", true);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			fail("Could not add Clinical Study object");
		}
	}

	public void testUpdateClinicalStudy()
	{
		try
		{
			ClinicalStudy clinicalStudy = (ClinicalStudy) TestCaseUtility
					.getObjectMap(ClinicalStudy.class);

			Logger.out.info("updating domain object------->" + clinicalStudy);
			BaseTestCaseUtility.updateClinicalStudy(clinicalStudy);
			Collection<ClinicalStudyRegistration> csrCollection = clinicalStudy
					.getClinicalStudyRegistrationCollection();
			System.out.println("ClinicalStudyTestCases.testUpdateClinicalStudy()"
					+ csrCollection.size());
			for (ClinicalStudyRegistration objCSregistration : csrCollection)
			{
				System.out.println("----");
			}
			ClinicalStudy updatedClinicalStudy = (ClinicalStudy) appService
					.updateObject(clinicalStudy);
			Logger.out.info("Clinical Study object successfully updated ---->"
					+ updatedClinicalStudy);
			assertTrue("Clinical Study object updated successfully", true);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			fail("Failed to update Clinical Study");
		}
	}

	public void testChangeCSActivityStatusClosed()
	{
		try
		{
			ClinicalStudy clinicalStudy = (ClinicalStudy) TestCaseUtility
					.getObjectMap(ClinicalStudy.class);
			Logger.out.info("updating domain object------->" + clinicalStudy);
			BaseTestCaseUtility.updateClinicalStudy(clinicalStudy);
			clinicalStudy.setActivityStatus(Constants.ACTIVITY_STATUS_CLOSED);
			ClinicalStudy updatedClinicalStudy = (ClinicalStudy) appService
					.updateObject(clinicalStudy);
			Logger.out.info("Clinical Study object successfully updated ---->"
					+ updatedClinicalStudy);
			assertTrue("Clinical Study object updated successfully", true);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			fail("Failed to update Clinical Study");
		}
	}

	public void testChangeCSActivityStatusActive()
	{
		try
		{
			ClinicalStudy clinicalStudy = (ClinicalStudy) TestCaseUtility
					.getObjectMap(ClinicalStudy.class);
			Logger.out.info("updating domain object------->" + clinicalStudy);
			BaseTestCaseUtility.updateClinicalStudy(clinicalStudy);
			clinicalStudy.setActivityStatus(Constants.ACTIVITY_STATUS_ACTIVE);
			ClinicalStudy updatedClinicalStudy = (ClinicalStudy) appService
					.updateObject(clinicalStudy);
			Logger.out.info("Clinical Study object successfully updated ---->"
					+ updatedClinicalStudy);
			assertTrue("Clinical Study object updated successfully", true);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			fail("Failed to update Clinical Study");
		}
	}

	public void testAddClinicalStudyWIthLongTitle()
	{
		try
		{
			ClinicalStudy clinicalStudy = BaseTestCaseUtility.initClinicalStudy();
			clinicalStudy
					.setTitle("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"
							+ "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"
							+ "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
			clinicalStudy = (ClinicalStudy) appService.createObject(clinicalStudy);
			assertFalse("Clinical Study Object with long title should throw exception", true);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("Could not add Clinical Study object with long title", true);
		}
	}

	public void testAddClinicalStudyWIthLengthyShortTitle()
	{
		try
		{
			ClinicalStudy clinicalStudy = BaseTestCaseUtility.initClinicalStudy();
			clinicalStudy
					.setShortTitle("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"
							+ "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"
							+ "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
			clinicalStudy = (ClinicalStudy) appService.createObject(clinicalStudy);
			assertFalse("Clinical Study Object with lengthy short title should throw exception",
					true);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("Could not add Clinical Study object with lengthy short title", true);
		}
	}

	public void testAddClinicalStudyWIthLengthyIRBIdentifier()
	{
		try
		{
			ClinicalStudy clinicalStudy = BaseTestCaseUtility.initClinicalStudy();
			clinicalStudy
					.setIrbIdentifier("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"
							+ "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"
							+ "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
			clinicalStudy = (ClinicalStudy) appService.createObject(clinicalStudy);
			assertFalse("Clinical Study Object with lengthy IRB Identifier should throw exception",
					true);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("Could not add Clinical Study object with lengthy IRB Identifier", true);
		}
	}

	public void testAddClinicalStudyWIthEmptyIRBIdentifier()
	{
		try
		{
			ClinicalStudy clinicalStudy = BaseTestCaseUtility.initClinicalStudy();
			clinicalStudy.setIrbIdentifier("");
			clinicalStudy = (ClinicalStudy) appService.createObject(clinicalStudy);
			assertFalse("Clinical Study Object with empty IRB Identifier should throw exception",
					true);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("Could not add Clinical Study object with empty IRB Identifier", true);
		}
	}

	public void testAddClinicalStudyWIthLengthyDescriptionURL()
	{
		try
		{
			ClinicalStudy clinicalStudy = BaseTestCaseUtility.initClinicalStudy();
			clinicalStudy
					.setDescriptionURL("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"
							+ "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"
							+ "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
			clinicalStudy = (ClinicalStudy) appService.createObject(clinicalStudy);
			assertFalse(
					"Clinical Study Object with lengthy Description URL should throw exception",
					true);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("Could not add Clinical Study object with lengthy Description URL", true);
		}
	}

	public void testSearchClinicalStudy()
	{
		ClinicalStudy clinicalStudy = new ClinicalStudy();
		ClinicalStudy cachedClinicalStudy = (ClinicalStudy) TestCaseUtility
				.getObjectMap(ClinicalStudy.class);
		cachedClinicalStudy.setId((Long) cachedClinicalStudy.getId());
		Logger.out.info("Searching Clinical Study object");
		try
		{
			List resultList = appService.search(ClinicalStudy.class, clinicalStudy);
			for (Iterator resultsIterator = resultList.iterator(); resultsIterator.hasNext();)
			{
				ClinicalStudy returnedClinicalStudy = (ClinicalStudy) resultsIterator.next();
				Logger.out.info("Clinical Study Object is successfully Found ---->  :: "
						+ returnedClinicalStudy.getTitle());
			}
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			fail("Could not find Clinical Study");
		}
	}

	public void testClinicalStudyWithEmptyTitle()
	{
		try
		{
			ClinicalStudy clinicalStudy = BaseTestCaseUtility.initClinicalStudy();
			clinicalStudy.setTitle("");
			Logger.out.info("updating ClinicalStudy object------->" + clinicalStudy);
			clinicalStudy = (ClinicalStudy) appService.createObject(clinicalStudy);
			Logger.out.info("Clinical Study object with empty title ---->" + clinicalStudy);
			fail("Clinical Study should throw exception");
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("Failed to create Clinical Study object", true);
		}
	}

	public void testClinicalStudyWithSamePIAndCoordinator()
	{
		try
		{
			ClinicalStudy clinicalStudy = BaseTestCaseUtility.initClinicalStudy();
			Collection clinicalStudyCordinatorCollection = clinicalStudy.getCoordinatorCollection();
			clinicalStudyCordinatorCollection.add(clinicalStudy.getPrincipalInvestigator());
			clinicalStudy = (ClinicalStudy) appService.createObject(clinicalStudy);
			Logger.out.info("Clinical Study object with Same PI And Coordinator ---->"
					+ clinicalStudy);
			assertFalse("Clinical Study with Same PI And Coordinator should throw exception", true);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("Failed to create Clinical Study object with Same PI And Coordinator", true);
		}
	}

	public void testClinicalStudyWithDuplicateClinicalStudyTitle()
	{

		try
		{
			ClinicalStudy clinicalStudy = BaseTestCaseUtility.initClinicalStudy();
			ClinicalStudy dupClinicalStudy = BaseTestCaseUtility.initClinicalStudy();
			dupClinicalStudy.setTitle(clinicalStudy.getTitle());
			clinicalStudy = (ClinicalStudy) appService.createObject(clinicalStudy);
			dupClinicalStudy = (ClinicalStudy) appService.createObject(dupClinicalStudy);
			assertFalse("Test Failed. Duplicate Clinical Study name should throw exception", true);
			fail("Test Failed. Duplicate Clinical Study name should throw exception");
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue(
					"Submission failed since a Clinical Study with the same NAME already exists",
					true);

		}

	}

	public void testClinicalStudyWithEmptyShortTitle()
	{

		try
		{
			ClinicalStudy clinicalStudy = BaseTestCaseUtility.initClinicalStudy();
			clinicalStudy.setShortTitle("");
			Logger.out.info("updating domain object------->" + clinicalStudy);
			clinicalStudy = (ClinicalStudy) appService.createObject(clinicalStudy);
			Logger.out.info("Clinical Study object with empty short title ---->" + clinicalStudy);
			fail("Clinical Study should throw exception");
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("Failed to create Clinical Study object", true);
		}
	}

	public void testClinicalStudyWithEmptyStartDate()
	{
		try
		{
			ClinicalStudy clinicalStudy = BaseTestCaseUtility.initClinicalStudy();

			//clinicalStudy.setStartDate(Utility.parseDate("", Utility.datePattern("08/15/1975")));
			clinicalStudy.setStartDate(new java.util.Date(""));

			Logger.out.info("updating domain object------->" + clinicalStudy);
			clinicalStudy = (ClinicalStudy) appService.createObject(clinicalStudy);
			Logger.out.info("Clinical Study object with empty date ---->" + clinicalStudy);
			assertFalse("Clinical Study should throw exception ---->" + clinicalStudy, true);
			fail("Clinical Study should throw exception");
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("failed to create Clinical Study object", true);
		}
	}

	public void testClinicalStudyWithStartDateGreaterThanTodaysDate()
	{
		try
		{
			ClinicalStudy clinicalStudy = BaseTestCaseUtility.initClinicalStudy();

			//clinicalStudy.setStartDate(Utility.parseDate("08/15/2015", Utility.datePattern("08/15/1975")));
			clinicalStudy.setStartDate(new java.util.Date("08/15/2015"));

			clinicalStudy = (ClinicalStudy) appService.createObject(clinicalStudy);
			assertFalse("Clinical Study should throw exception ---->" + clinicalStudy, true);
			fail("Clinical Study should throw exception when start date is greater than todays date");
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue(
					"Did not create Clinical Study object as start date is greater than todays date",
					true);
		}
	}

	
}
