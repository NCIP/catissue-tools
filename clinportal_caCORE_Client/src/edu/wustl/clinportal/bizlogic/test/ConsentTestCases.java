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

import edu.wustl.clinportal.domain.ClinicalStudy;
import edu.wustl.clinportal.domain.ClinicalStudyConsentTier;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ConsentTestCases extends ClinportalBaseTestCase
{

	public void testAddNewConsentToCS()
	{
		ClinicalStudy clinicalStudy = BaseTestCaseUtility.initClinicalStudy();

		Collection<ClinicalStudyConsentTier> consentTierCollection = clinicalStudy
				.getConsentTierCollection();

		ClinicalStudyConsentTier c4 = new ClinicalStudyConsentTier();
		c4.setStatement("Consent for research 4");
		consentTierCollection.add(c4);
		ClinicalStudyConsentTier c5 = new ClinicalStudyConsentTier();
		c5.setStatement("Consent for research 5");
		consentTierCollection.add(c5);

		try
		{
			appService.createObject(clinicalStudy);
			assertTrue("Clinical Study Object with Consents added successfully", true);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
			assertFalse("Could not update Clinical Study after adding consents", true);
		}

	}

	public void testEditConsentForCS()
	{
		ClinicalStudy clinicalStudy = (ClinicalStudy) TestCaseUtility
				.getObjectMap(ClinicalStudy.class);
		Collection<ClinicalStudyConsentTier> consentTierCollection = clinicalStudy
				.getConsentTierCollection();

		int size = consentTierCollection.size();
		System.out.println("Initial size is : " + size);
		for (Iterator iter = consentTierCollection.iterator(); iter.hasNext();)
		{
			ClinicalStudyConsentTier consentTier = (ClinicalStudyConsentTier) iter.next();
			consentTier.setStatement("Changing the statement");
			break;
		}
		try
		{
			appService.updateObject(clinicalStudy);
			assertTrue("Clinical Study Object with Consents updated successfully", true);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
			assertFalse("Could not update Clinical Study after updating consents", true);
		}

	}

	public void testDeleteConsentTier()
	{
		ClinicalStudy clinicalStudy = (ClinicalStudy) TestCaseUtility
				.getObjectMap(ClinicalStudy.class);
		Collection<ClinicalStudyConsentTier> consentTierCollection = clinicalStudy
				.getConsentTierCollection();
		ClinicalStudyConsentTier consentTier = null;

		for (Iterator iter = consentTierCollection.iterator(); iter.hasNext();)
		{
			consentTier = (ClinicalStudyConsentTier) iter.next();
			break;
		}

		consentTierCollection.remove(consentTier);
		try
		{
			appService.updateObject(clinicalStudy);
			assertTrue("Successfully updated Clinical Study after deleting consent tier", true);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
			assertFalse("Could not update Clinical Study after deleting consent tier", true);
		}

	}

	public void testAddConsentWithUnsignedFormURL()
	{
		ClinicalStudy clinicalStudy = (ClinicalStudy) TestCaseUtility
				.getObjectMap(ClinicalStudy.class);
		String unsignedConsentDocumentURL = "http://www.google.com";
		clinicalStudy.setUnsignedConsentDocumentURL(unsignedConsentDocumentURL);

		try
		{
			appService.updateObject(clinicalStudy);
			assertTrue("Successfully updated Clinical Study after adding consent document URL",
					true);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
			assertFalse("Could not update Clinical Study after adding consent document URL", true);
		}
	}

}
