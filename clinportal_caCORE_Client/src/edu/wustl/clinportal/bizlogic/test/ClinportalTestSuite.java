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

import junit.framework.TestSuite;

public class ClinportalTestSuite
{

	public static void main(String[] args)
	{
		junit.swingui.TestRunner.run(ClinportalTestSuite.class);
	}

	public static junit.framework.Test suite()
	{
		TestSuite suite = new TestSuite("Test for edu.wustl.clinportal.bizlogic.test");
		//$JUnit-BEGIN$
		suite.addTestSuite(InstitutionTestCases.class);
		suite.addTestSuite(DepartmentTestCases.class);
		suite.addTestSuite(CancerResearchGrpTestCases.class);
		suite.addTestSuite(UserTestCases.class);
		suite.addTestSuite(SiteTestCases.class);

		suite.addTestSuite(ClinicalStudyTestCases.class);
		suite.addTestSuite(ParticipantTestCases.class);
		suite.addTestSuite(ConsentTestCases.class);
		suite.addTestSuite(ReportedProblemTestCases.class);
		suite.addTestSuite(ScientistRoleTestCases.class);
		//$JUnit-END$
		return suite;
	}

}
