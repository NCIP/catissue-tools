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

import edu.wustl.clinportal.domain.Address;
import edu.wustl.clinportal.domain.CancerResearchGroup;
import edu.wustl.clinportal.domain.Department;
import edu.wustl.clinportal.domain.Institution;
import edu.wustl.clinportal.domain.User;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.util.logger.Logger;

public class UserTestCases extends ClinportalBaseTestCase
{

	AbstractDomainObject domainObject = null;

	public void testAddUser()
	{
		try
		{
			User user = BaseTestCaseUtility.initUser();
			user = (User) appService.createObject(user);
			TestCaseUtility.setObjectMap(user, User.class);
			Logger.out.info("User Object created successfully");
			System.out.println("User Object created successfully");
			assertTrue("User Object added successfully", true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertFalse("Could not add object", true);
		}
	}

	public void testAddScientist()
	{
		try
		{
			User userObj = new User();
			userObj.setEmailAddress("sctNB" + UniqueKeyGeneratorUtil.getUniqueKey() + "@admin.com");
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

			Institution inst = new Institution();
			inst.setId(new Long(1));
			userObj.setInstitution(inst);

			Department department = new Department();
			department.setId(new Long(1));
			userObj.setDepartment(department);

			CancerResearchGroup cancerResearchGroup = new CancerResearchGroup();
			cancerResearchGroup.setId(new Long(1));
			userObj.setCancerResearchGroup(cancerResearchGroup);

			userObj.setRoleId("7");
			userObj.setActivityStatus("Active");
			userObj.setPageOf(Constants.PAGEOF_USER_ADMIN);

			userObj = (User) appService.createObject(userObj);

			userObj.setNewPassword("Test123");

			userObj.setRoleId("7");
			userObj.setActivityStatus("Active");
			userObj.setPageOf(Constants.PAGEOF_USER_ADMIN);
			userObj = (User) appService.updateObject(userObj);

			TestCaseUtility.setNameObjectMap(TestCaseUtility.USER_WITH_ROLE_AS_SCIENTIST, userObj);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertFalse("could not add object", true);
		}
	}

	public void testSignUpUser()
	{
		try
		{
			User userObj = new User();
			userObj.setEmailAddress("appUser" + UniqueKeyGeneratorUtil.getUniqueKey()
					+ "@admin.com");
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

			Institution inst = new Institution();
			inst.setId(new Long(1));
			userObj.setInstitution(inst);

			Department department = new Department();
			department.setId(new Long(1));
			userObj.setDepartment(department);

			CancerResearchGroup cancerResearchGroup = new CancerResearchGroup();
			cancerResearchGroup.setId(new Long(1));
			userObj.setCancerResearchGroup(cancerResearchGroup);

			//userObj.setRoleId("7");
			userObj.setActivityStatus("New");
			userObj.setPageOf(Constants.PAGEOF_SIGNUP);
			userObj.setFirstTimeLogin(Boolean.TRUE);
			userObj = (User) appService.createObject(userObj);

			TestCaseUtility.setNameObjectMap("User_For_Approval", userObj);
			assertTrue("Sign Up User Object added successfully", true);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertFalse("could not add object", true);
		}
	}

	/*public void testApproveUser()
	{
		try
		{
			User user = (User)TestCaseUtility.getNameObjectMap("User_For_Approval");
			user.setActivityStatus("Approve");
			user.setRoleId("7");
			user.setPageOf("pageOfApproveUser");
			user.setAdminuser(false);
			Long uformid = new Long(Constants.APPROVE_USER_FORM_ID ); 
			System.out.println("UserTestCases.testApproveUser()"+uformid);
			//user.setId(uformid);
			appService.updateObject(user);
			assertTrue("User Object approved successfully", true);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertFalse("could not approve object", true);
		}
	}*/

	public void testSearchUser()
	{
		User user = (User) new User();
		User cachedUser = (User) TestCaseUtility.getObjectMap(User.class);

		if (cachedUser == null)
		{
			user.setId(new Long(1));
		}
		else
		{
			user.setId((Long) cachedUser.getId());
		}
		Logger.out.info("Searching User object");

		System.out.println("User Id for search is : " + user.getId());
		try
		{
			List resultList = appService.search(User.class, user);
			for (Iterator resultsIterator = resultList.iterator(); resultsIterator.hasNext();)
			{
				User returneduser = (User) resultsIterator.next();
				Logger.out.info(" User Object is successfully Found ---->  :: "
						+ returneduser.getEmailAddress());
				assertTrue("User Object is successfully Found", true);
			}
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			fail("could not find User object");
		}
	}

	public void testEditUser()
	{
		try
		{
			User userObj = new User();
			userObj.setEmailAddress("edituser" + UniqueKeyGeneratorUtil.getUniqueKey()
					+ "@admin.com");
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

			Institution inst = new Institution();
			inst.setId(new Long(1));
			userObj.setInstitution(inst);

			Department department = new Department();
			department.setId(new Long(1));
			userObj.setDepartment(department);

			CancerResearchGroup cancerResearchGroup = new CancerResearchGroup();
			cancerResearchGroup.setId(new Long(1));
			userObj.setCancerResearchGroup(cancerResearchGroup);

			userObj.setRoleId("7");
			userObj.setActivityStatus("Active");
			userObj.setPageOf(Constants.PAGEOF_USER_ADMIN);

			userObj = (User) appService.createObject(userObj);

			userObj.setNewPassword("Edit123");

			userObj.setRoleId("7");
			userObj.setActivityStatus("Active");

			Address add = userObj.getAddress();
			add.setPhoneNumber("123456");
			userObj.setAddress(add);

			userObj.setPageOf(Constants.PAGEOF_USER_ADMIN);
			System.out.println("Updating User---------------------");
			userObj = (User) appService.updateObject(userObj);
			System.out.println("User successfully updated-------------------");
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertFalse("Could not update user", true);
		}
	}

	public void testAddUserWithEmptyEmailAddress()
	{
		try
		{
			User user = (User) BaseTestCaseUtility.initUser();
			user.setEmailAddress("");
			user = (User) appService.createObject(user);
			Logger.out.info("User with empty email address");
			fail("For empty email address, it should throw exception");
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("For empty email address, it throws exception", true);
		}
	}

	public void testAddUserWithEmptyFirstName()
	{
		try
		{
			User user = (User) BaseTestCaseUtility.initUser();
			user.setFirstName("");
			user = (User) appService.createObject(user);
			Logger.out.info("User with empty first name");
			fail("For empty first name, it should throw exception");
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("For empty first name, it throws exception", true);
		}
	}

	public void testAddUserWithEmptyLastName()
	{
		try
		{
			User user = (User) BaseTestCaseUtility.initUser();
			user.setLastName("");
			user = (User) appService.createObject(user);
			Logger.out.info("User with empty last name");
			fail("For empty last name, it should throw exception");
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("For empty last name, it throws exception", true);
		}
	}

	public void testAddUserWithEmptyCityName()
	{
		try
		{
			User user = (User) BaseTestCaseUtility.initUser();
			Address address = new Address();
			address.setStreet("Main street");
			address.setCity("");
			address.setState("Alabama");
			address.setZipCode("12345");
			address.setCountry("United States");
			address.setPhoneNumber("21222324");
			address.setFaxNumber("21222324");
			user.setAddress(address);
			user = (User) appService.createObject(user);
			Logger.out.info("User with empty last name");
			fail("For empty city name, it should throw exception");
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("For empty city name, it throws exception", true);
		}
	}

	public void testAddUserWithEmptyStateName()
	{
		try
		{
			User user = (User) BaseTestCaseUtility.initUser();
			Address address = new Address();
			address.setStreet("Main street");
			address.setCity("New hampshier");
			address.setState("");
			address.setZipCode("12345");
			address.setCountry("United States");
			address.setPhoneNumber("21222324");
			address.setFaxNumber("21222324");
			user.setAddress(address);
			System.out.println(user);
			user = (User) appService.createObject(user);
			Logger.out.info("For empty State name, it should throw exception");
			fail("For empty State name, it should throw exception");
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("For empty State name, it throws exception", true);
		}
	}

	public void testAddUserWithEmptyZipCode()
	{
		try
		{
			User user = (User) BaseTestCaseUtility.initUser();
			Address address = new Address();
			address.setStreet("Main street");
			address.setCity("New hampshier");
			address.setState("Alabama");
			address.setZipCode("");
			address.setCountry("United States");
			address.setPhoneNumber("21222324");
			address.setFaxNumber("21222324");
			user.setAddress(address);
			System.out.println(user);
			user = (User) appService.createObject(user);
			Logger.out.info("For empty Zip Code, it should throw exception");
			fail("For empty Zip Code, it should throw exception");
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("For empty Zip Code, it throws exception", true);
		}
	}

	public void testAddUserWithNullInstitution()
	{
		try
		{
			User user = (User) BaseTestCaseUtility.initUser();
			user.setCancerResearchGroup(null);
			System.out.println(user);
			user = (User) appService.createObject(user);
			Logger.out.info("For null Institute name, it should throw exception");
			fail("For null Institute name, it should throw exception");
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("For empty Institute name, it throws exception", true);
		}
	}

	public void testAddUserWithNullDepartment()
	{
		try
		{
			User user = (User) BaseTestCaseUtility.initUser();
			user.setDepartment(null);
			System.out.println(user);
			user = (User) appService.createObject(user);
			Logger.out.info("For null department, it should throw exception");
			fail("For null department, it should throw exception");
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("For null Department, it throws exception", true);
		}
	}

	public void testAddUserWithNullCRG()
	{
		try
		{
			User user = (User) BaseTestCaseUtility.initUser();
			user.setDepartment(null);
			System.out.println(user);
			user = (User) appService.createObject(user);
			Logger.out.info("For null CRG, it should throw exception");
			fail("For null CRG, it should throw exception");

		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("For empty last name, it throws exception", true);
		}
	}

	public void testAddUserWithNullRoleId()
	{
		try
		{
			User user = BaseTestCaseUtility.initUser();
			user.setRoleId("");
			System.out.println(user);
			user = (User) appService.createObject(user);
			Logger.out.info("For invalid role id, it should throw exception");
			fail("For invalid role id, it should throw exception");
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("For invalid role id, it throws exception", true);
		}
	}

	public void testDuplicateEmailId()
	{
		try
		{
			User existingUser = (User) TestCaseUtility.getNameObjectMap("User_For_Approval");

			User user = BaseTestCaseUtility.initUser();
			user.setEmailAddress(existingUser.getEmailAddress());
			System.out.println(user.getEmailAddress());
			user = (User) appService.createObject(user);
			Logger.out.info("For duplicate email id, it should throw exception");
			fail("For duplicate email id, it should throw exception");
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("For duplicate email id, it throws exception", true);
		}
	}
}
