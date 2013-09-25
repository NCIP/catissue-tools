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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.dao.exception.DAOException;

public class AddConsentResponseMetadata
{

	private List<String> entityList = new ArrayList<String>();
	private Map<String, List<String>> entityAttrMap = new HashMap<String, List<String>>();
	private Map<String, String> attrColumnMap = new HashMap<String, String>();
	private Map<String, String> attrTypeMap = new HashMap<String, String>();
	private Map<String, String> attrPrimarkeyMap = new HashMap<String, String>();
	public static final String IDENTIFIER = "id";
	public static final String RESPONSE = "response";
	public static final String CONSENT_RESPONSE = "edu.wustl.clinportal.domain.ClinicalStudyConsentTierResponse";

	/**
	 * @throws SQLException
	 * @throws IOException
	 * @throws DAOException
	 * @throws DynamicExtensionsSystemException 
	 */
	public void addConsentResponseMetadata() throws SQLException, IOException, DAOException,
			DynamicExtensionsSystemException
	{
		populateEntityList();
		populateEntityAttributeMap();
		populateAttributeColumnNameMap();
		populateAttributeDatatypeMap();
		populateAttributePrimaryKeyMap();

		AddEntity addEntity = new AddEntity();
		addEntity.addEntity(entityList, "CLINPORT_CONSENT_TIER_RESPONSE", "NULL", 3, 0);

		AddAttribute addAttribute = new AddAttribute(entityAttrMap, attrColumnMap, attrTypeMap,
				attrPrimarkeyMap, entityList);
		addAttribute.addAttribute();

		String entityName = "edu.wustl.clinportal.domain.ClinicalStudyRegistration";
		String targetEntityName = "edu.wustl.clinportal.domain.ClinicalStudyConsentTierResponse";

		AddAssociations addAssociations = new AddAssociations();
		addAssociations.addAssociation(entityName, targetEntityName, "csr_response", "ASSOCIATION",
				"consentTierResponseCollection", true, "", "CLINICAL_STUDY_REG_ID", null, 100, 1,
				"SRC_DESTINATION");

		entityName = "edu.wustl.clinportal.domain.ClinicalStudyConsentTierResponse";
		targetEntityName = "edu.wustl.clinportal.domain.ClinicalStudyConsentTier";
		addAssociations.addAssociation(entityName, targetEntityName, "response_consentTier",
				"ASSOCIATION", "consentTier", true, "", "", "CONSENT_TIER_ID", 1, 1,
				"SRC_DESTINATION");

	}

	/**
	 * 
	 */
	private void populateEntityList()
	{
		entityList.add(CONSENT_RESPONSE);
	}

	private void populateEntityAttributeMap()
	{
		List<String> attributes = new ArrayList<String>();
		attributes.add(IDENTIFIER);
		attributes.add(RESPONSE);
		entityAttrMap.put(CONSENT_RESPONSE, attributes);
	}

	private void populateAttributeColumnNameMap()
	{
		attrColumnMap.put(IDENTIFIER, "IDENTIFIER");
		attrColumnMap.put(RESPONSE, "RESPONSE");
	}

	private void populateAttributeDatatypeMap()
	{
		attrTypeMap.put(IDENTIFIER, "long");
		attrTypeMap.put(RESPONSE, "string");
	}

	private void populateAttributePrimaryKeyMap()
	{
		attrPrimarkeyMap.put(IDENTIFIER, "1");
		attrPrimarkeyMap.put(RESPONSE, "0");
	}
}
