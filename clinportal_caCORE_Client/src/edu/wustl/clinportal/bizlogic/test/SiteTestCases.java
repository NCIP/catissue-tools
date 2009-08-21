
package edu.wustl.clinportal.bizlogic.test;

import java.util.Iterator;
import java.util.List;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.clinportal.domain.Address;
import edu.wustl.clinportal.domain.Site;
import edu.wustl.clinportal.domain.User;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.util.logger.Logger;

public class SiteTestCases extends ClinportalBaseTestCase
{

	AbstractDomainObject domainObject = null;

	public void testAddSite()
	{
		try
		{
			System.out.println("SiteTestCases.testAddSite()");
			Site site = BaseTestCaseUtility.initSite();
			site = (Site) appService.createObject(site);
			TestCaseUtility.setObjectMap(site, Site.class);
			System.out.println("Site Object created successfully");
			assertTrue("Site Object added successfully", true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertFalse("could not add site object", true);
		}
	}

	public void testSearchSite()
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
				Logger.out.info("Site Object is successfully Found ---->  :: "
						+ returnedSite.getName());
				// System.out.println(" Domain Object is successfully Found ---->  :: " + returnedDepartment.getName());
			}
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertFalse("Does not find Site Object", true);

		}
	}

	public void testUpdateSite()
	{
		Site site = BaseTestCaseUtility.initSite();
		Logger.out.info("Updating Site object------->" + site);
		try
		{
			site = (Site) appService.createObject(site);
			BaseTestCaseUtility.updateSite(site);
			Site updatedSite = (Site) appService.updateObject(site);
			Logger.out.info("Site object successfully updated ---->" + updatedSite);
			assertTrue("Site object successfully updated ---->" + updatedSite, true);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertFalse("Failed to update Site Object", true);
		}
	}

	public void testUpdateSiteWithInvalidActivityStatus()
	{
		Site site = BaseTestCaseUtility.initSite();
		try
		{
			site = (Site) appService.createObject(site);
			BaseTestCaseUtility.updateSite(site);
			site.setActivityStatus(edu.wustl.clinportal.util.global.Constants.INVALID);
			Site updatedSite = (Site) appService.updateObject(site);
			assertFalse("Invalid Activity status for Site should throw exception ---->"
					+ updatedSite, true);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("Invalid Activity status", true);
		}
	}

	public void testWithEmptySiteName()
	{
		try
		{
			System.out.println("SiteTestCases.testWithEmptySiteName()");
			Site site = BaseTestCaseUtility.initSite();
			site.setName("");
			site = (Site) appService.createObject(site);
			assertFalse("Empty site name should throw Exception", true);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("Name is required", true);
		}
	}

	public void testWithDuplicateSiteName()
	{
		try
		{
			Site site = BaseTestCaseUtility.initSite();
			Site dupSiteName = BaseTestCaseUtility.initSite();
			dupSiteName.setName(site.getName());
			site = (Site) appService.createObject(site);
			dupSiteName = (Site) appService.createObject(dupSiteName);
			assertFalse("Test Failed. Duplicate site name should throw exception", true);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("Submission failed since a Site with the same NAME already exists", true);

		}
	}

	public void testWithInvalidSiteType()
	{
		try
		{
			System.out.println("SiteTestCases.testWithInvalidSiteType()");
			Site site = BaseTestCaseUtility.initSite();
			site.setType("LaboratoryInvalid");
			site = (Site) appService.createObject(site);
			assertFalse("Invalid site type should throw Exception", true);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("Type is wrong", true);
		}
	}

	public void testWithEmptySiteType()
	{
		try
		{
			Site site = BaseTestCaseUtility.initSite();
			site.setType("");
			site = (Site) appService.createObject(site);
			assertFalse("Empty site type should throw Exception", true);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("Site Type is Empty", true);
		}
	}

	public void testWithInvalidSiteActivityStatus()
	{
		try
		{
			Site site = BaseTestCaseUtility.initSite();
			site.setActivityStatus("Invalid");
			System.out.println(site);
			site = (Site) appService.createObject(site);
			assertFalse("Invalid site type should throw Exception", true);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("Type is wrong", true);
		}
	}

	public void testWithInvalidSiteEmailAddress()
	{
		try
		{
			Site site = BaseTestCaseUtility.initSite();
			site.setEmailAddress("abc");
			site = (Site) appService.createObject(site);
			assertFalse("Invalid site email address should throw Exception", true);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("Site: Email address is wrong", true);
		}
	}

	public void testWithInvalidAddress_EmptyZipCode()
	{
		try
		{
			Site site = BaseTestCaseUtility.initSite();
			Address address = site.getAddress();
			address.setZipCode("");
			site = (Site) appService.createObject(site);
			assertFalse("Empty site address-zip code should throw Exception", true);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("Site: Zip code is empty", true);
		}
	}

	public void testWithInvalidAddress_InvalidZipCode()
	{
		try
		{
			Site site = BaseTestCaseUtility.initSite();
			Address address = site.getAddress();
			address.setZipCode("123");
			site = (Site) appService.createObject(site);
			assertFalse("Invalid site address-zip code should throw Exception", true);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("Site: Zip code is invalid", true);
		}
	}

	public void testWithInvalidAddress_EmptyCity()
	{
		try
		{
			Site site = BaseTestCaseUtility.initSite();
			Address address = site.getAddress();
			address.setCity("");
			site = (Site) appService.createObject(site);
			assertFalse("Empty site address-city should throw Exception", true);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("Site: City is empty", true);
		}
	}

	public void testWithInvalidAddress_EmptyStreet()
	{
		try
		{
			Site site = BaseTestCaseUtility.initSite();
			Address address = site.getAddress();
			address.setStreet("");
			site = (Site) appService.createObject(site);
			assertFalse("Empty site address-street should throw Exception", true);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("Site: Street is empty", true);
		}
	}

	public void testWithInvalidAddress_EmptyState()
	{
		try
		{
			Site site = BaseTestCaseUtility.initSite();
			Address address = site.getAddress();
			address.setState("");
			site = (Site) appService.createObject(site);
			assertFalse("Empty site address-state should throw Exception", true);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("Site: State is empty", true);
		}
	}

	public void testWithInvalidAddress_EmptyCountry()
	{
		try
		{
			Site site = BaseTestCaseUtility.initSite();
			Address address = site.getAddress();
			address.setCountry("");
			site = (Site) appService.createObject(site);
			assertFalse("Empty site address-country should throw Exception", true);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("Site: Country is empty", true);
		}
	}

	public void testWithInvalidCordinatorInSite()
	{
		try
		{
			Site site = BaseTestCaseUtility.initSite();
			User user = new User();
			user = BaseTestCaseUtility.initUser();
			site.setCoordinator(user);
			System.out.println(site);
			site = (Site) appService.createObject(site);
			assertFalse("It should throw exception", true);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("Cordinator is required", true);
		}
	}

	public void testUpdateSiteToClosedActivityStatus()
	{
		try
		{
			Site site = BaseTestCaseUtility.initSite();
			site = (Site) appService.createObject(site);
			site.setActivityStatus(Constants.ACTIVITY_STATUS_CLOSED);
			System.out.println(site);
			Site updatedSite = (Site) appService.updateObject(site);
			assertTrue("Site updated successfully", true);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertFalse("Failed to update site", true);
		}
	}

}