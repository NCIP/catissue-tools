
package edu.wustl.clinportal.bizlogic.test;

import edu.wustl.clinportal.bizlogic.test.BaseTestCaseUtility;
import edu.wustl.clinportal.bizlogic.test.ClinportalBaseTestCase;
import edu.wustl.clinportal.domain.ReportedProblem;
import edu.wustl.common.util.logger.Logger;

public class ReportedProblemTestCases extends ClinportalBaseTestCase
{

	public void testAddReportedProblem()
	{
		try
		{
			ReportedProblem rp = BaseTestCaseUtility.initReportedProblem();
			rp = (ReportedProblem) appService.createObject(rp);
			//TestCaseUtility.setObjectMap(rp, ReportedProblem.class);
			System.out.println("ReportedProblem Object created successfully");
			assertTrue("ReportedProblem Object added successfully", true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertFalse("Could not add ReportedProblem object", true);
		}
	}

	public void testUpdateReportedProblem()
	{
		ReportedProblem rp = BaseTestCaseUtility.initReportedProblem();
		Logger.out.info("Updating ReportedProblem object------->" + rp);
		try
		{
			rp = (ReportedProblem) appService.createObject(rp);
			BaseTestCaseUtility.updateReportedProblem(rp);
			ReportedProblem updatedRP = (ReportedProblem) appService.updateObject(rp);
			Logger.out.info("ReportedProblem object successfully updated ---->" + updatedRP);
			assertTrue("ReportedProblem object successfully updated ---->" + updatedRP, true);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertFalse("Failed to update ReportedProblem Object", true);
		}
	}
}
