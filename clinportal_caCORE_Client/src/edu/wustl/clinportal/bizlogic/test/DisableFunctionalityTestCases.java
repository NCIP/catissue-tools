
package edu.wustl.clinportal.bizlogic.test;

import java.text.ParseException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

import edu.wustl.clinportal.domain.ClinicalStudy;
import edu.wustl.clinportal.domain.ClinicalStudyConsentTier;
import edu.wustl.clinportal.domain.ClinicalStudyConsentTierResponse;
import edu.wustl.clinportal.domain.ClinicalStudyRegistration;
import edu.wustl.clinportal.domain.Participant;
import edu.wustl.clinportal.domain.User;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.logger.Logger;

public class DisableFunctionalityTestCases extends ClinportalBaseTestCase
{

	public void testParticipantRegistrationUnderClosedCS()
	{
		try
		{
			ClinicalStudy cs = BaseTestCaseUtility.initClinicalStudy();
			cs = (ClinicalStudy) appService.createObject(cs);

			cs.setActivityStatus("Closed");
			cs = (ClinicalStudy) appService.updateObject(cs);

			Participant participant = BaseTestCaseUtility.initParticipant();

			try
			{
				participant = (Participant) appService.createObject(participant);
			}
			catch (Exception e)
			{
				Logger.out.error(e.getMessage(), e);
				e.printStackTrace();
				assertFalse("Failed to create participant", true);
			}
			System.out.println("Participant:" + participant.getFirstName());
			ClinicalStudyRegistration clinicalStudyRegistration = new ClinicalStudyRegistration();
			clinicalStudyRegistration.setClinicalStudy(cs);
			clinicalStudyRegistration.setParticipant(participant);
			clinicalStudyRegistration.setClinicalStudyParticipantIdentifier("");
			clinicalStudyRegistration.setActivityStatus("Active");
			try
			{
				clinicalStudyRegistration.setRegistrationDate(Utility.parseDate("08/15/1975", Utility.datePattern("08/15/1975")));
				clinicalStudyRegistration.setConsentSignatureDate(Utility.parseDate("11/23/2006", Utility.datePattern("11/23/2006")));

			}
			catch (ParseException e)
			{
				e.printStackTrace();
			}
			clinicalStudyRegistration.setSignedConsentDocumentURL("D:/doc/consentDoc.doc");
			User user = (User) TestCaseUtility.getObjectMap(User.class);
			clinicalStudyRegistration.setConsentWitness(user);

			Collection<ClinicalStudyConsentTierResponse> consentTierResponseCollection = new LinkedHashSet<ClinicalStudyConsentTierResponse>();
			Collection consentTierCollection = new LinkedHashSet();
			consentTierCollection = cs.getConsentTierCollection();

			Iterator consentTierItr = consentTierCollection.iterator();
			while (consentTierItr.hasNext())
			{
				ClinicalStudyConsentTier consent = (ClinicalStudyConsentTier) consentTierItr.next();
				ClinicalStudyConsentTierResponse response = new ClinicalStudyConsentTierResponse();
				response.setResponse("Yes");
				response.setConsentTier(consent);
				consentTierResponseCollection.add(response);
			}

			clinicalStudyRegistration.setConsentTierResponseCollection(consentTierResponseCollection);

			System.out.println("Creating CSR");

			clinicalStudyRegistration = (ClinicalStudyRegistration) appService.createObject(clinicalStudyRegistration);
			assertFalse("Successfully registered participant to closed Clinical Study", true);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("Clinical Study is already marked as closed", true);
		}

	}

	public void testParticipantRegistrationUnderDisabledCS()
	{
		try
		{
			ClinicalStudy cs = BaseTestCaseUtility.initClinicalStudy();
			cs = (ClinicalStudy) appService.createObject(cs);

			cs.setActivityStatus("Disabled");
			cs = (ClinicalStudy) appService.updateObject(cs);

			System.out.println("cs title:" + cs.getTitle());
			System.out.println("cs short title:" + cs.getShortTitle());
			System.out.println("cs id:" + cs.getId());

			Participant participant = BaseTestCaseUtility.initParticipant();

			try
			{
				participant = (Participant) appService.createObject(participant);
			}
			catch (Exception e)
			{
				Logger.out.error(e.getMessage(), e);
				e.printStackTrace();
				assertFalse("Failed to create participant", true);
			}
			System.out.println("Participant:" + participant.getFirstName());
			ClinicalStudyRegistration clinicalStudyRegistration = new ClinicalStudyRegistration();
			clinicalStudyRegistration.setClinicalStudy(cs);
			clinicalStudyRegistration.setParticipant(participant);
			clinicalStudyRegistration.setClinicalStudyParticipantIdentifier("");
			clinicalStudyRegistration.setActivityStatus("Active");
			try
			{
				clinicalStudyRegistration.setRegistrationDate(Utility.parseDate("08/15/1975", Utility.datePattern("08/15/1975")));
				clinicalStudyRegistration.setConsentSignatureDate(Utility.parseDate("11/23/2006", Utility.datePattern("11/23/2006")));

			}
			catch (ParseException e)
			{
				e.printStackTrace();
			}
			clinicalStudyRegistration.setSignedConsentDocumentURL("D:/doc/consentDoc.doc");
			User user = (User) TestCaseUtility.getObjectMap(User.class);
			clinicalStudyRegistration.setConsentWitness(user);

			Collection<ClinicalStudyConsentTierResponse> consentTierResponseCollection = new LinkedHashSet<ClinicalStudyConsentTierResponse>();
			Collection consentTierCollection = new LinkedHashSet();
			consentTierCollection = cs.getConsentTierCollection();

			Iterator consentTierItr = consentTierCollection.iterator();
			while (consentTierItr.hasNext())
			{
				ClinicalStudyConsentTier consent = (ClinicalStudyConsentTier) consentTierItr.next();
				ClinicalStudyConsentTierResponse response = new ClinicalStudyConsentTierResponse();
				response.setResponse("Yes");
				response.setConsentTier(consent);
				consentTierResponseCollection.add(response);
			}

			clinicalStudyRegistration.setConsentTierResponseCollection(consentTierResponseCollection);

			System.out.println("Creating CSR");

			clinicalStudyRegistration = (ClinicalStudyRegistration) appService.createObject(clinicalStudyRegistration);
			System.out.println("Participant Fname:" + clinicalStudyRegistration.getParticipant().getFirstName());
			assertFalse("Successfully registered participant to disabled Clinical Study", true);

		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			assertTrue("Clinical Study is already marked as closed", true);
		}
	}

}
