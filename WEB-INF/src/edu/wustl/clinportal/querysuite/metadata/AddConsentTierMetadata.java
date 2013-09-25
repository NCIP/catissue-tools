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

public class AddConsentTierMetadata
{

	private List<String> entityList = new ArrayList<String>();
	private Map<String, List<String>> entityAttrMap = new HashMap<String, List<String>>();
	private Map<String, String> attrColumnMap = new HashMap<String, String>();
	private Map<String, String> attributeTypeMap = new HashMap<String, String>();
	private Map<String, String> attrPrimarkeyMap = new HashMap<String, String>();

	/**
	 * @throws SQLException
	 * @throws IOException
	 * @throws DAOException
	 * @throws DynamicExtensionsSystemException 
	 */
	public void addConsentTierMetadata() throws SQLException, IOException, DAOException,
			DynamicExtensionsSystemException
	{
		populateEntityList();
		populateEntityAttributeMap();
		populateAttributeColumnNameMap();
		populateAttributeDatatypeMap();
		populateAttributePrimaryKeyMap();

		AddEntity addEntity = new AddEntity();
		addEntity.addEntity(entityList, "CLINPORT_CONSENT_TIER", "NULL", 3, 0);

		AddAttribute addAttribute = new AddAttribute(entityAttrMap, attrColumnMap,
				attributeTypeMap, attrPrimarkeyMap, entityList);
		addAttribute.addAttribute();

		String entityName = "edu.wustl.clinportal.domain.ClinicalStudy";
		String targetEntityName = "edu.wustl.clinportal.domain.ClinicalStudyConsentTier";

		AddAssociations addAssociations = new AddAssociations();
		addAssociations.addAssociation(entityName, targetEntityName, "cs_consent", "ASSOCIATION",
				"consentTierCollection", true, "", "CLINICAL_STUDY_ID", null, 100, 1,
				"SRC_DESTINATION");

	}

	/**
	 * 
	 */
	private void populateEntityList()
	{
		entityList.add("edu.wustl.clinportal.domain.ClinicalStudyConsentTier");
	}

	/**
	 * 
	 */
	private void populateEntityAttributeMap()
	{
		List<String> attributes = new ArrayList<String>();
		attributes.add("id");
		attributes.add("statement");
		entityAttrMap.put("edu.wustl.clinportal.domain.ClinicalStudyConsentTier", attributes);
	}

	/**
	 * 
	 */
	private void populateAttributeColumnNameMap()
	{
		attrColumnMap.put("id", "IDENTIFIER");
		attrColumnMap.put("statement", "STATEMENT");
	}

	/**
	 * 
	 */
	private void populateAttributeDatatypeMap()
	{
		attributeTypeMap.put("id", "long");
		attributeTypeMap.put("statement", "string");
	}

	/**
	 * 
	 */
	private void populateAttributePrimaryKeyMap()
	{
		attrPrimarkeyMap.put("id", "1");
		attrPrimarkeyMap.put("statement", "0");
	}
}