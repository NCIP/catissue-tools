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

public class UpdateColProtocolMetadata
{

	/**
	 * @param args
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main(String[] args) throws SQLException, IOException,
			ClassNotFoundException, DAOException
	{
		System.out.println("--UpdateColProtocolMetadata starts");
		try
		{
			addColPrototcolMetadata();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("--UpdateColProtocolMetadata finishes");
	}

	/**
	 * @throws SQLException
	 * @throws IOException
	 * @throws DynamicExtensionsSystemException 
	 */
	public static void addColPrototcolMetadata() throws SQLException, IOException, DAOException,
			DynamicExtensionsSystemException
	{

		AddAssociations addAssociations = new AddAssociations();

		String entityName = "edu.wustl.clinportal.domain.CollectionProtocol";
		String targetEntityName = "edu.wustl.clinportal.domain.ClinicalStudy";

		addAssociations.addAssociation(entityName, targetEntityName,
				"collectionProtocol_clinicalStudy", "ASSOCIATION", "collectionProtocol", true,
				"clinicalStudy", "REL_CP_ID", null, 1, 1, "BI_DIRECTIONAL");
		addAssociations.addAssociation(targetEntityName, entityName,
				"clinicalStudy_collectionProtocol", "ASSOCIATION", "clinicalStudy", false, "",
				"REL_CP_ID", null, 1, 0, "BI_DIRECTIONAL");

		String entityName1 = "edu.wustl.clinportal.domain.CollectionProtocolEvent";
		String targetEntityName1 = "edu.wustl.clinportal.domain.ClinicalStudyEvent";

		addAssociations.addAssociation(entityName1, targetEntityName1,
				"collectionProtocolevent_clinicalStudyevent", "ASSOCIATION",
				"collectionProtocolEvent", true, "clinicalStudyEvent", "REL_CPE_ID", null, 1, 1,
				"BI_DIRECTIONAL");
		addAssociations.addAssociation(targetEntityName1, entityName1,
				"clinicalStudyevent_collectionProtocolevent", "ASSOCIATION", "clinicalStudyEvent",
				false, "", "REL_CPE_ID", null, 1, 0, "BI_DIRECTIONAL");

	}

}
