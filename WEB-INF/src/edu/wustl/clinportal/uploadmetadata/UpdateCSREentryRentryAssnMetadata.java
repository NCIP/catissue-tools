/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */


package edu.wustl.clinportal.uploadmetadata;

import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.clinportal.querysuite.metadata.AddAssociations;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;

/**
 * This class adds Tag Values for attributes and Paths 
 * @author falguni_sachde
 */
public class UpdateCSREentryRentryAssnMetadata
{

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}
	private static Logger LOGGER = Logger.getCommonLogger(UpdateCSREentryRentryAssnMetadata.class);

	/**
	 * Adds tag values for entities and attributes.
	 * @param args filename
	 */
	public static void main(String[] args)
	{
		try
		{
			//This will update existing de-association to containment type between   CSR and EventEntry
			String entityName = "edu.wustl.clinportal.domain.ClinicalStudyRegistration";
			String targetEntityName = "edu.wustl.clinportal.domain.EventEntry";
			String targetRoleName = "eventEntryCollection";

			AddAssociations addAssociations = new AddAssociations();
			addAssociations.updateAssociation(entityName, targetEntityName,
					Constants.CLINPORTAL_ENTITY_GROUP_NAME, targetRoleName,
					DEConstants.AssociationType.CONTAINTMENT);

			//This will add containment between RecordEntry and EventEntry
			entityName = "edu.wustl.clinportal.domain.EventEntry";
			targetEntityName = "edu.wustl.clinportal.domain.RecordEntry";
			targetRoleName = "recordEntryCollection";
			addAssociations.updateAssociation(entityName, targetEntityName,
					Constants.CLINPORTAL_ENTITY_GROUP_NAME, targetRoleName,
					DEConstants.AssociationType.CONTAINTMENT);

		}
		catch (Exception e)
		{
			LOGGER.info("--UpdateCSREentryRentryAssnMetadata Task throws error.");
		}
	}
}