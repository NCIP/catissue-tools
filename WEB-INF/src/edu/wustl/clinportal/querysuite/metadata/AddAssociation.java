/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */


package edu.wustl.clinportal.querysuite.metadata;

import java.io.IOException;
import java.sql.SQLException;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.dao.exception.DAOException;

public class AddAssociation
{

	/**
	 * @throws SQLException
	 * @throws IOException
	 * @throws DAOException
	 * @throws DynamicExtensionsSystemException 
	 */
	public void addAssociation() throws SQLException, IOException, DAOException, DynamicExtensionsSystemException
	{

		AddAssociations addAssociations = new AddAssociations();
		String entityName = "edu.wustl.clinportal.domain.ClinicalStudyRegistration";
		String targetEntityName = "edu.wustl.clinportal.domain.User";
		addAssociations.addAssociation(entityName, targetEntityName, "csregistration_user",
				"ASSOCIATION", "consentWitness", true, "", "", "CONSENT_WITNESS", 1, 1,
				"SRC_DESTINATION");
	}
}
