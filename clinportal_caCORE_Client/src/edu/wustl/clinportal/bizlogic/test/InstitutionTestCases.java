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

import edu.wustl.clinportal.domain.Institution;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.util.logger.Logger;


public class InstitutionTestCases extends ClinportalBaseTestCase
{
	AbstractDomainObject domainObject = null;

	public void testAddInstitution()
	{
		try
		{
			Institution institution = BaseTestCaseUtility.initInstitution();
			System.out.println(institution);
			institution = (Institution) appService.createObject(institution);
			TestCaseUtility.setObjectMap(institution, Institution.class);
			System.out.println("Institution object created successfully");
			assertTrue("Institution object added successfully", true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertFalse("Could not add object", true);
		}
	}

	public void testUpdateInstitution()
	{
		Institution institution = BaseTestCaseUtility.initInstitution();
		Logger.out.info("Updating Institution object------->" + institution);
		try
		{
			institution = (Institution) appService.createObject(institution);
			BaseTestCaseUtility.updateInstitution(institution);
			Institution updatedInst = (Institution) appService.updateObject(institution);
			Logger.out.info("Institution object successfully updated ---->" + updatedInst);
			assertTrue("Institution object successfully updated ---->" + updatedInst, true);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertFalse("Failed to update Institution Object", true);
		}
	}

	public void testWithEmptyInstitutionName()
	{
		try
		{
			Institution inst = BaseTestCaseUtility.initInstitution();
			inst.setName("");
			System.out.println(inst);
			inst = (Institution) appService.createObject(inst);
			assertFalse("Test Failed. Empty institution name should throw exception", true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue("Institution Name is required", true);
		}
	}

	public void testWithDuplicateInstitutionName()
	{
		try
		{
			Institution inst = BaseTestCaseUtility.initInstitution();
			Institution dupInstName = BaseTestCaseUtility.initInstitution();
			dupInstName.setName(inst.getName());
			inst = (Institution) appService.createObject(inst);
			dupInstName = (Institution) appService.createObject(dupInstName);
			assertFalse("Test Failed. Duplicate institution name should throw exception", true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue("Submission failed since a Institution with the same NAME already exists", true);

		}
	}

	public void testSearchInstitution()
	{
		Institution institution = new Institution();
		System.out.println("Searching Institution object");
		institution.setId(new Long(1));

		try
		{
			List resultList = appService.search(Institution.class, institution);
			for (Iterator resultsIterator = resultList.iterator(); resultsIterator.hasNext();)
			{
				Institution returnedInst = (Institution) resultsIterator.next();
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
	
//	public void testNullDomainObjectInInsert_Institution()
//	{
//		domainObject = new Institution(); 
//		testNullDomainObjectInInsert(domainObject);
//	}
//	
//	public void testNullSessionDataBeanInInsert_Institution()
//	{
//		domainObject = new Institution();
//		testNullSessionDataBeanInInsert(domainObject);
//	}
//		
//	public void testWrongDaoTypeInInsert_Institution()
//	{
//		domainObject = new Institution();
//		testWrongDaoTypeInInsert(domainObject);
//	}
//	public void testNullSessionDataBeanInUpdate_Institution()
//	{
//		domainObject = new Institution();
//		testNullSessionDataBeanInUpdate(domainObject);
//	}
//	
//	public void testNullOldDomainObjectInUpdate_Institution()
//	{
//		domainObject = new Institution();
//		testNullOldDomainObjectInUpdate(domainObject);
//	}
//	
//		
//	public void testNullCurrentDomainObjectInUpdate_Institution()
//	{
//		domainObject = new Institution();
//		testNullCurrentDomainObjectInUpdate(domainObject);
//	}
//	
//	public void testEmptyCurrentDomainObjectInUpdate_Institution()
//	{
//		domainObject = new Institution();
//		AbstractDomainObject initialisedDomainObject = BaseTestCaseUtility.initInstitution();
//		testEmptyCurrentDomainObjectInUpdate(domainObject, initialisedDomainObject);
//	}
//	
//	public void testEmptyOldDomainObjectInUpdate_Institution()
//	{
//		domainObject = new Institution();
//		AbstractDomainObject initialisedDomainObject = BaseTestCaseUtility.initInstitution();
//		testEmptyOldDomainObjectInUpdate(domainObject, initialisedDomainObject);
//	}
//	
//	public void testNullDomainObjectInRetrieve_Institution()
//	{
//		domainObject = new Institution();
//		testNullCurrentDomainObjectInRetrieve(domainObject);
//	}
	
}
