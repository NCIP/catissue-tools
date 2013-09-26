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

public class AddRaceMetadata
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
	public void addRaceMetadata() throws SQLException, IOException, DAOException,
			DynamicExtensionsSystemException
	{
		populateEntityList();
		populateEntityAttributeMap();
		populateAttributeColumnNameMap();
		populateAttributeDatatypeMap();
		populateAttributePrimaryKeyMap();

		AddEntity addEntity = new AddEntity();
		addEntity.addEntity(entityList, "CATISSUE_RACE", "NULL", 3, 0);

		AddAttribute addAttribute = new AddAttribute(entityAttrMap, attrColumnMap,
				attributeTypeMap, attrPrimarkeyMap, entityList);
		addAttribute.addAttribute();

		String entityName = "edu.wustl.clinportal.domain.Participant";
		String targetEntityName = "edu.wustl.clinportal.domain.Race";

		AddAssociations addAssociations = new AddAssociations();
		addAssociations.addAssociation(entityName, targetEntityName, "part_race", "CONTAINTMENT",
				"raceCollection", true, "", "PARTICIPANT_ID", null, 100, 1, "SRC_DESTINATION");

	}

	/**
	 * 
	 */
	private void populateEntityList()
	{
		entityList.add("edu.wustl.clinportal.domain.Race");
	}

	/**
	 * 
	 */
	private void populateEntityAttributeMap()
	{
		List<String> attributes = new ArrayList<String>();
		attributes.add("id");
		attributes.add("raceName");
		entityAttrMap.put("edu.wustl.clinportal.domain.Race", attributes);
	}

	/**
	 * 
	 */
	private void populateAttributeColumnNameMap()
	{
		attrColumnMap.put("id", "IDENTIFIER");
		attrColumnMap.put("raceName", "RACE_NAME");
	}

	/**
	 * 
	 */
	private void populateAttributeDatatypeMap()
	{
		attributeTypeMap.put("id", "long");
		attributeTypeMap.put("raceName", "string");
	}

	/**
	 * 
	 */
	private void populateAttributePrimaryKeyMap()
	{
		attrPrimarkeyMap.put("id", "1");
		attrPrimarkeyMap.put("raceName", "0");
	}
}