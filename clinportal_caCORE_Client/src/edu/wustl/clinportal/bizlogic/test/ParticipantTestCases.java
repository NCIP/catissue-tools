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
import java.util.List;

import edu.wustl.clinportal.domain.ClinicalStudy;
import edu.wustl.clinportal.domain.ClinicalStudyConsentTier;
import edu.wustl.clinportal.domain.ClinicalStudyConsentTierResponse;
import edu.wustl.clinportal.domain.ClinicalStudyRegistration;
import edu.wustl.clinportal.domain.Participant;
import edu.wustl.clinportal.domain.ParticipantMedicalIdentifier;
import edu.wustl.clinportal.domain.Site;
import edu.wustl.clinportal.domain.User;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.logger.Logger;

public class ParticipantTestCases extends ClinportalBaseTestCase
{

	AbstractDomainObject domainObject = null;

	public void testAddParticipantWithCSR()
	{
		try
		{
			Participant participant = BaseTestCaseUtility.initParticipantWithCSR();
			System.out.println(participant);
			participant = (Participant) appService.createObject(participant);

			Collection clinicalStudyRegistrationCollection = participant
					.getClinicalStudyRegistrationCollection();
			Iterator cprItr = clinicalStudyRegistrationCollection.iterator();
			ClinicalStudyRegistration clinicalStudyRegistration = (ClinicalStudyRegistration) cprItr
					.next();

			TestCaseUtility
					.setObjectMap(clinicalStudyRegistration, ClinicalStudyRegistration.class);
			TestCaseUtility.setObjectMap(participant, Participant.class);
			System.out.println("Participant object created successfully");
			assertTrue("Participant object added successfully", true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertFalse("Could not add Participant object", true);
		}
	}

	public void testMatchingParticipant()
	{
		try
		{
			System.out.println("testMatchingParticipant----------------->");
			Participant participant = BaseTestCaseUtility.initParticipantWithCSR();
			List matchingParticipants = appService.getParticipantMatchingObects(participant);
			System.out.println("Participant matching list created successfully : "
					+ matchingParticipants);
			assertTrue("Participant matching list created successfully", true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertFalse("Could not create Participant matching list", true);
		}
	}

	public void testAddParticipantWithCSRWithInvalidSSN()
	{
		try
		{
			Participant participant = BaseTestCaseUtility.initParticipantWithCSR();
			participant.setSocialSecurityNumber("1234");
			participant = (Participant) appService.createObject(participant);

			/*Collection clinicalStudyRegistrationCollection = participant.getClinicalStudyRegistrationCollection();
			Iterator cprItr = clinicalStudyRegistrationCollection.iterator();
			ClinicalStudyRegistration clinicalStudyRegistration = (ClinicalStudyRegistration) cprItr.next();

			TestCaseUtility.setObjectMap(clinicalStudyRegistration, ClinicalStudyRegistration.class);
			TestCaseUtility.setObjectMap(participant, Participant.class);*/

			assertFalse("Participant with invalid SSN should throw exception", true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue("Participant object with invalid SSN", true);
		}
	}

	public void testSearchParticipant()
	{
		Participant participant = new Participant();
		Logger.out.info(" searching domain object");
		participant.setId(new Long(1));

		try
		{
			List resultList = appService.search(Participant.class, participant);
			for (Iterator resultsIterator = resultList.iterator(); resultsIterator.hasNext();)
			{
				Participant returnedParticipant = (Participant) resultsIterator.next();
				Logger.out.info("Participant Object is successfully Found ---->  :: "
						+ returnedParticipant.getFirstName() + " "
						+ returnedParticipant.getLastName());
				// System.out.println(" Domain Object is successfully Found ---->  :: " + returnedDepartment.getName());
			}
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertFalse("Could not find Participant Object", true);

		}
	}

	public void testUpdateParticipant()
	{
		Participant participant = BaseTestCaseUtility.initParticipantWithCSR();
		Logger.out.info("updating Participant object------->" + participant);
		try
		{
			participant = (Participant) appService.createObject(participant);
			BaseTestCaseUtility.updateParticipant(participant);
			Participant updatedParticipant = (Participant) appService.updateObject(participant);
			Logger.out.info("Participant object successfully updated ---->" + updatedParticipant);
			assertTrue("Participant object successfully updated ---->" + updatedParticipant, true);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertFalse("Failed to update Participant Object", true);
		}
	}

	public void testAddParticipantWithUniquePMI()
	{
		try
		{
			System.out.println("************************@@@");

			Participant participant = BaseTestCaseUtility.initParticipant();
			Collection<ParticipantMedicalIdentifier> participantMedicalIdentifierCollection = new HashSet<ParticipantMedicalIdentifier>();
			ParticipantMedicalIdentifier pmi = new ParticipantMedicalIdentifier();
			Site site = (Site) TestCaseUtility.getObjectMap(Site.class);
			pmi.setSite(site);
			pmi.setMedicalRecordNumber(UniqueKeyGeneratorUtil.getUniqueKey());
			pmi.setParticipant(participant);
			System.out.println("ParticipantTestCases.testAddParticipantWithUniquePMI()----"
					+ pmi.getMedicalRecordNumber());
			participantMedicalIdentifierCollection.add(pmi);
			participant
					.setParticipantMedicalIdentifierCollection(participantMedicalIdentifierCollection);
			participant = (Participant) appService.createObject(participant);
			assertTrue("Participant object with unique PMI created successfully", true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertFalse("Could not add Participant object", true);
		}
	}

	/*	public void testAddParticipantWithDuplicatePMI()
		{
			try
			{
				Participant participant = BaseTestCaseUtility.initParticipant();
				Collection<ParticipantMedicalIdentifier> participantMedicalIdentifierCollection = new HashSet<ParticipantMedicalIdentifier>();
				ParticipantMedicalIdentifier pmi = new ParticipantMedicalIdentifier();
				Site site = (Site) TestCaseUtility.getObjectMap(Site.class);
				pmi.setSite(site);
				pmi.setMedicalRecordNumber("1234");
				pmi.setParticipant(participant);
				participantMedicalIdentifierCollection.add(pmi);
				participant.setParticipantMedicalIdentifierCollection(participantMedicalIdentifierCollection);
				participant = (Participant) appService.createObject(participant);
				assertFalse("Participant with duplicate PMI created successfully", true);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				assertTrue("Submission failed because Participant with same PMI is already exist", true);
			}
		}
	*/
	public void testAddParticipantRegistrationWithUniquePMIdentifier()
	{

		Participant participant = BaseTestCaseUtility.initParticipant();
		Collection<ParticipantMedicalIdentifier> participantMedicalIdentifierCollection = new HashSet<ParticipantMedicalIdentifier>();
		ParticipantMedicalIdentifier pmi = new ParticipantMedicalIdentifier();
		Site site = (Site) TestCaseUtility.getObjectMap(Site.class);
		pmi.setSite(site);
		pmi.setMedicalRecordNumber(UniqueKeyGeneratorUtil.getUniqueKey());
		pmi.setParticipant(participant);
		participantMedicalIdentifierCollection.add(pmi);
		participant
				.setParticipantMedicalIdentifierCollection(participantMedicalIdentifierCollection);
		try
		{
			participant = (Participant) appService.createObject(participant);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertFalse("Unable to create participant", true);
		}
		ClinicalStudyRegistration clinicalStudyRegistration = new ClinicalStudyRegistration();

		ClinicalStudy clinicalStudy = (ClinicalStudy) TestCaseUtility
				.getObjectMap(ClinicalStudy.class);
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
			clinicalStudyRegistration.setConsentSignatureDate(new java.util.Date("11/23/2006"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		clinicalStudyRegistration.setSignedConsentDocumentURL("F:/doc/consentDoc.doc");
		User user = (User) TestCaseUtility.getObjectMap(User.class);
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
		clinicalStudyRegistration.setConsentTierResponseCollection(consentTierResponseCollection);
		try
		{
			clinicalStudyRegistration = (ClinicalStudyRegistration) appService
					.createObject(clinicalStudyRegistration);
			assertTrue("Participant Registration successfully created", true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertFalse("Could not add Participant object", true);
		}
	}

	/*public void testParticipantRegistrationWithDuplicatePMIdentifier()
	{
		Participant participant = BaseTestCaseUtility.initParticipant();
		Collection<ParticipantMedicalIdentifier> participantMedicalIdentifierCollection = new HashSet<ParticipantMedicalIdentifier>();
		ParticipantMedicalIdentifier pmi = new ParticipantMedicalIdentifier();
		Site site = (Site) TestCaseUtility.getObjectMap(Site.class);
		pmi.setSite(site);
		pmi.setMedicalRecordNumber("3434");
		pmi.setParticipant(participant);
		participantMedicalIdentifierCollection.add(pmi);
		participant.setParticipantMedicalIdentifierCollection(participantMedicalIdentifierCollection);
		try
		{
			participant = (Participant) appService.createObject(participant);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue("Unable to create participant", true);
		}
		ClinicalStudyRegistration clinicalStudyRegistration = new ClinicalStudyRegistration();

		ClinicalStudy clinicalStudy = (ClinicalStudy) TestCaseUtility.getObjectMap(ClinicalStudy.class);
		clinicalStudyRegistration.setClinicalStudy(clinicalStudy);

		clinicalStudyRegistration.setParticipant(participant);
		clinicalStudyRegistration.setClinicalStudyParticipantIdentifier("");
		clinicalStudyRegistration.setActivityStatus("Active");
		try
		{
			//clinicalStudyRegistration.setRegistrationDate(Utility.parseDate("08/15/1975", Utility.datePattern("08/15/1975")));
			clinicalStudyRegistration.setRegistrationDate(new java.util.Date("08/15/1975"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		//Setting Consent Tier Responses.
		try
		{
			//clinicalStudyRegistration.setConsentSignatureDate(Utility.parseDate("11/23/2006", Utility.datePattern("11/23/2006")));
			clinicalStudyRegistration.setConsentSignatureDate(new java.util.Date("11/23/2006"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		clinicalStudyRegistration.setSignedConsentDocumentURL("F:/doc/consentDoc.doc");
		User user = (User) TestCaseUtility.getObjectMap(User.class);
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
		clinicalStudyRegistration.setConsentTierResponseCollection(consentTierResponseCollection);
		try
		{
			clinicalStudyRegistration = (ClinicalStudyRegistration) appService.createObject(clinicalStudyRegistration);
			assertFalse("Should not create Participant object", true);

		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue("Participant with same medical identifier ia already present", true);
		}
	}*/

	public void testParticipantRegistrationWithUniquePCSIdentifier()
	{

		Participant participant = BaseTestCaseUtility.initParticipant();
		try
		{
			participant = (Participant) appService.createObject(participant);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertFalse("Unable to create participant", true);
		}
		ClinicalStudyRegistration clinicalStudyRegistration = new ClinicalStudyRegistration();
		ClinicalStudy clinicalStudy = (ClinicalStudy) TestCaseUtility
				.getObjectMap(ClinicalStudy.class);
		clinicalStudyRegistration.setClinicalStudy(clinicalStudy);
		clinicalStudyRegistration.setParticipant(participant);
		clinicalStudyRegistration.setClinicalStudyParticipantIdentifier(UniqueKeyGeneratorUtil
				.getUniqueKey());
		clinicalStudyRegistration.setActivityStatus("Active");
		try
		{
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
			clinicalStudyRegistration.setConsentSignatureDate(new java.util.Date("11/23/2006"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		clinicalStudyRegistration.setSignedConsentDocumentURL("F:/doc/consentDoc.doc");
		User user = (User) TestCaseUtility.getObjectMap(User.class);
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
		clinicalStudyRegistration.setConsentTierResponseCollection(consentTierResponseCollection);
		try
		{
			clinicalStudyRegistration = (ClinicalStudyRegistration) appService
					.createObject(clinicalStudyRegistration);

		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertFalse("Could not add object", true);
		}
	}

	/*	public void testParticipantRegistrationWithDuplicatePCSIdentifier()
		{

			Participant participant = BaseTestCaseUtility.initParticipant();
			try
			{
				participant = (Participant) appService.createObject(participant);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				assertFalse("Unable to create participant", true);
			}
			ClinicalStudyRegistration clinicalStudyRegistration = new ClinicalStudyRegistration();

			ClinicalStudy clinicalStudy = (ClinicalStudy) TestCaseUtility.getObjectMap(ClinicalStudy.class);
			clinicalStudyRegistration.setClinicalStudy(clinicalStudy);

			clinicalStudyRegistration.setParticipant(participant);

			clinicalStudyRegistration.setClinicalStudyParticipantIdentifier(UniqueKeyGeneratorUtil.getUniqueKey());
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
				clinicalStudyRegistration.setConsentSignatureDate(new java.util.Date("11/23/2006"));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			clinicalStudyRegistration.setSignedConsentDocumentURL("F:/doc/consentDoc.doc");
			User user = (User) TestCaseUtility.getObjectMap(User.class);
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
			clinicalStudyRegistration.setConsentTierResponseCollection(consentTierResponseCollection);
			try
			{
				clinicalStudyRegistration = (ClinicalStudyRegistration) appService.createObject(clinicalStudyRegistration);
				assertFalse("Failed to create CSR with duplicate participant clinical study identifier", true);
			}
			catch (Exception e)
			{
				Logger.out.error(e.getMessage(), e);
				e.printStackTrace();
				assertTrue("Should not not create object", true);
			}
		}
	*/
	// change it to check registration date less than CS start date
	/*public void testAddParticipantRegistrationWithConsentDateLessThanCSStartDate()
	{

		Participant participant = BaseTestCaseUtility.initParticipant();
		Collection<ParticipantMedicalIdentifier> participantMedicalIdentifierCollection = new HashSet<ParticipantMedicalIdentifier>();

		try
		{
			participant = (Participant) appService.createObject(participant);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertFalse("Unable to create participant", true);
		}
		ClinicalStudyRegistration clinicalStudyRegistration = new ClinicalStudyRegistration();

		ClinicalStudy clinicalStudy = (ClinicalStudy) TestCaseUtility.getObjectMap(ClinicalStudy.class);
		
		clinicalStudyRegistration.setClinicalStudy(clinicalStudy);

		clinicalStudyRegistration.setParticipant(participant);

		clinicalStudyRegistration.setClinicalStudyParticipantIdentifier("");

		clinicalStudyRegistration.setActivityStatus("Active");
		try
		{
			clinicalStudyRegistration.setRegistrationDate(Utility.parseDate("08/15/1975", Utility.datePattern("08/15/1975")));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		//Setting Consent Tier Responses.
		try
		{
			// set date less than clinical study start date : 08/15/2003
			clinicalStudyRegistration.setConsentSignatureDate(Utility.parseDate("11/23/1970", Utility.datePattern("11/23/1970")));
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		clinicalStudyRegistration.setSignedConsentDocumentURL("F:/doc/consentDoc.doc");
		User user = (User) TestCaseUtility.getObjectMap(User.class);
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
		clinicalStudyRegistration.setConsentTierResponseCollection(consentTierResponseCollection);
		try
		{
			clinicalStudyRegistration = (ClinicalStudyRegistration) appService.createObject(clinicalStudyRegistration);
			assertFalse("Participant Registration successfully created with Consent Date less than CS start date", true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue("Exception thrown since consent date is less than CS start date", true);
		}
	}*/

	/*	public void testInvalidParticipantActivityStatus()
	 {
	 try{
	 Participant participant = BaseTestCaseUtility.initParticipant();		
	 participant.setActivityStatus("Invalid");
	 System.out.println(participant);
	 participant = (Participant) appService.createObject(participant); 
	 assertFalse("Test Failed.Invalid Activity status should throw Exception", true);
	 }
	 catch(Exception e){
	 Logger.out.error(e.getMessage(),e);
	 e.printStackTrace();
	 assertTrue("Name is required", true);
	 }
	 }

	 */

}
