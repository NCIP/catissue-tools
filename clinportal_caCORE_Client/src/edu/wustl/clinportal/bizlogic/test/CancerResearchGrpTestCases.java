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

import java.util.Iterator;
import java.util.List;

import edu.wustl.clinportal.domain.CancerResearchGroup;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.util.logger.Logger;


public class CancerResearchGrpTestCases extends ClinportalBaseTestCase
{
	AbstractDomainObject domainObject = null;
	
	public void testAddCancerResearchGrp()
	{
		try
		{
			CancerResearchGroup crg = BaseTestCaseUtility.initCancerResearchGrp();
			System.out.println(crg);
			crg = (CancerResearchGroup) appService.createObject(crg);
			TestCaseUtility.setObjectMap(crg, CancerResearchGroup.class);
			System.out.println("CRG Object created successfully");
			assertTrue("CRG Object added successfully", true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertFalse("Could not add CRG object", true);
		}
	}
	
	public void testSearchCancerResearchGrp()
	{
		CancerResearchGroup crg = new CancerResearchGroup();
		System.out.println("Searching CRG object");
		crg.setId(new Long(1));

		try
		{
			List resultList = appService.search(CancerResearchGroup.class, crg);
			for (Iterator resultsIterator = resultList.iterator(); resultsIterator.hasNext();)
			{
				CancerResearchGroup returnedInst = (CancerResearchGroup) resultsIterator.next();
				System.out.println("CRG Object is successfully Found ---->  :: " + returnedInst.getName());
				// System.out.println(" Domain Object is successfully Found ---->  :: " + returnedDepartment.getName());
			}
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertFalse("Could not find CRG Object", true);

		}
	}
	
	public void testUpdateCancerResearchGrp()
	{

		CancerResearchGroup crg = BaseTestCaseUtility.initCancerResearchGrp();
		System.out.println("updating CRG object------->" + crg);
		try
		{
			crg = (CancerResearchGroup) appService.createObject(crg);
			BaseTestCaseUtility.updateCancerResearchGrp(crg);
			CancerResearchGroup updatedCRG = (CancerResearchGroup) appService.updateObject(crg);
			System.out.println("Domain object successfully updated ---->" + updatedCRG);
			assertTrue("CRG object successfully updated ---->" + updatedCRG, true);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertFalse("Failed to update CRG Object", true);
		}
	}
	
	public void testWithEmptyCRGName()
	{
		try
		{
			CancerResearchGroup crg = BaseTestCaseUtility.initCancerResearchGrp();
			crg.setName("");
			System.out.println(crg);
			crg = (CancerResearchGroup) appService.createObject(crg);
			assertFalse("Test Failed. Empty CRG name should throw exception", true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue("CRG Name is required", true);
		}
	}
	
	public void testWithNullCRGObject()
	{
		try
		{
			CancerResearchGroup crg = null;
			appService.createObject(crg);
			assertFalse("Test Failed. Null CRG should throw exception", true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue("CRG Object is Null", true);
		}
	}

	public void testWithDuplicateCRGName()
	{
		try
		{
			CancerResearchGroup crg = BaseTestCaseUtility.initCancerResearchGrp();
			CancerResearchGroup dupCRGName = BaseTestCaseUtility.initCancerResearchGrp();
			dupCRGName.setName(crg.getName());
			crg = (CancerResearchGroup) appService.createObject(crg);
			dupCRGName = (CancerResearchGroup) appService.createObject(dupCRGName);
			assertFalse("Test Failed. Duplicate CRG name should throw exception", true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue("Submission failed since a CRG with the same NAME already exists", true);

		}
	}
	
	
//	public void testNullDomainObjectInInsert_CancerResearchGroup()
//	{
//		domainObject = new CancerResearchGroup(); 
//		testNullDomainObjectInInsert(domainObject);
//	}
//	
//	public void testNullSessionDataBeanInInsert_CancerResearchGroup()
//	{
//		domainObject = new CancerResearchGroup();
//		testNullSessionDataBeanInInsert(domainObject);
//	}
//		
//	public void testWrongDaoTypeInInsert_CancerResearchGroup()
//	{
//		domainObject = new CancerResearchGroup();
//		testWrongDaoTypeInInsert(domainObject);
//	}
//	public void testNullSessionDataBeanInUpdate_CancerResearchGroup()
//	{
//		domainObject = new CancerResearchGroup();
//		testNullSessionDataBeanInUpdate(domainObject);
//	}
//	
//	public void testNullOldDomainObjectInUpdate_CancerResearchGroup()
//	{
//		domainObject = new CancerResearchGroup();
//		testNullOldDomainObjectInUpdate(domainObject);
//	}
//	
//		
//	public void testNullCurrentDomainObjectInUpdate_CancerResearchGroup()
//	{
//		domainObject = new CancerResearchGroup();
//		testNullCurrentDomainObjectInUpdate(domainObject);
//	}
//	
//	public void testEmptyCurrentDomainObjectInUpdate_CancerResearchGroup()
//	{
//		domainObject = new CancerResearchGroup();
//		AbstractDomainObject initialisedDomainObject = BaseTestCaseUtility.initCancerResearchGrp();
//		testEmptyCurrentDomainObjectInUpdate(domainObject, initialisedDomainObject);
//	}
//	
//	public void testEmptyOldDomainObjectInUpdate_CancerResearchGroup()
//	{
//		domainObject = new CancerResearchGroup();
//		AbstractDomainObject initialisedDomainObject = BaseTestCaseUtility.initCancerResearchGrp();
//		testEmptyOldDomainObjectInUpdate(domainObject,initialisedDomainObject);
//	}
//	public void testNullDomainObjectInRetrieve_CancerResearchGroup()
//	{
//		domainObject = new CancerResearchGroup();
//		testNullCurrentDomainObjectInRetrieve(domainObject);
//	}
//
//	
}
