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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;

/**
 * @author falguni_sachde
 *
 */
public class AddAttribute
{

	private Map<String, List<String>> entityAttrMap = new HashMap<String, List<String>>();
	private Map<String, String> attrColumnMap = new HashMap<String, String>();
	private Map<String, String> attrTypeMap = new HashMap<String, String>();
	private Map<String, String> attrPrimarkeyMap = new HashMap<String, String>();
	private List<String> entityList = new ArrayList<String>();
	public final static String UNSIGNED_CONSENT = "unsignedConsentDocumentURL";
	public final static String SIGNED_CONSENT = "signedConsentDocumentURL";
	public final static String CONSENT_DATE = "consentSignatureDate";

	/**
	 * @throws SQLException
	 * @throws IOException
	 * @throws DAOException
	 */
	public void addAttribute() throws SQLException, IOException, DAOException
	{
		DAOConfigFactory daocConfigFactory = DAOConfigFactory.getInstance();
		IDAOFactory daoFactory = daocConfigFactory.getDAOFactory("dynamicExtention");
		JDBCDAO jdbcdao = null;
		Set<String> keySet = entityAttrMap.keySet();
		Iterator<String> iterator = keySet.iterator();
		while (iterator.hasNext())
		{
			String entityName = (String) iterator.next();
			List<String> attributes = entityAttrMap.get(entityName);
			for (String attr : attributes)
			{
				jdbcdao = daoFactory.getJDBCDAO();
				jdbcdao.openSession(null);
				int nextIdMetadata = getNextId("dyextn_abstract_metadata", jdbcdao);
				int nextIdAttrType = getNextId("dyextn_attribute_type_info", jdbcdao);
				int nextIdDbProp = getNextId("dyextn_database_properties", jdbcdao);
				String sql = "INSERT INTO dyextn_abstract_metadata values(" + nextIdMetadata
						+ ",NULL,NULL,NULL,'" + attr + "',null)";

				jdbcdao.executeUpdate(sql);

				sql = "INSERT INTO DYEXTN_BASE_ABSTRACT_ATTRIBUTE values(" + nextIdMetadata + ")";
				jdbcdao.executeUpdate(sql);

				int entityId = UpdateMetadataUtil.getEntityIdByName(entityName, jdbcdao);

				sql = "INSERT INTO dyextn_attribute values (" + nextIdMetadata + "," + entityId
						+ ")";
				jdbcdao.executeUpdate(sql);

				insertAttributeData(attr, nextIdMetadata, nextIdAttrType, jdbcdao);
				insertAttributeType(attr, nextIdAttrType, jdbcdao);
				insertDbProp(attr, nextIdMetadata, nextIdDbProp, jdbcdao);

				jdbcdao.commit();
				jdbcdao.closeSession();
			}
		}

	}

	/**
	 * @param attr
	 * @param nextIdMetadata
	 * @param nextIdAttrType
	 * @param jdbcdao
	 * @throws SQLException
	 * @throws DAOException
	 */
	private void insertAttributeData(String attr, int nextIdMetadata, int nextIdAttrType,
			JDBCDAO jdbcdao) throws SQLException, DAOException
	{
		String sql;
		String primaryKey = attrPrimarkeyMap.get(attr);
		sql = "insert into dyextn_primitive_attribute (IDENTIFIER,IS_IDENTIFIED,IS_PRIMARY_KEY,IS_NULLABLE)"
				+ " values (" + nextIdMetadata + ",NULL," + primaryKey + ",1)";

		jdbcdao.executeUpdate(sql);
		sql = "insert into dyextn_attribute_type_info (IDENTIFIER,PRIMITIVE_ATTRIBUTE_ID) values ("
				+ nextIdAttrType + "," + nextIdMetadata + ")";
		jdbcdao.executeUpdate(sql);
	}

	/**
	 * @param attr
	 * @param nextIdAttrType
	 * @param jdbcdao
	 * @throws SQLException
	 * @throws DAOException
	 */
	private void insertAttributeType(String attr, int nextIdAttrType, JDBCDAO jdbcdao)
			throws SQLException, DAOException
	{
		String dataType = getDataTypeOfAttribute(attr, attrTypeMap);
		String sql = null;
		if (!dataType.equalsIgnoreCase("String") && !dataType.equalsIgnoreCase("date"))
		{
			sql = "insert into dyextn_numeric_type_info (IDENTIFIER,MEASUREMENT_UNITS,DECIMAL_PLACES,NO_DIGITS) values ("
					+ nextIdAttrType + ",NULL,0,NULL)";
			jdbcdao.executeUpdate(sql);
		}
		if (dataType.equalsIgnoreCase("string"))
		{
			sql = "insert into dyextn_string_type_info (IDENTIFIER) values (" + nextIdAttrType
					+ ")";
		}
		else if (dataType.equalsIgnoreCase("date"))
		{
			sql = "insert into dyextn_date_type_info (IDENTIFIER,FORMAT) values (" + nextIdAttrType
					+ ",'MM-dd-yyyy')";
		}
		else
		{
			sql = insertNumericType(nextIdAttrType, dataType);
		}
		jdbcdao.executeUpdate(sql);
	}

	/**
	 * @param nextIdAttrType
	 * @param dataType
	 * @return
	 */
	private String insertNumericType(int nextIdAttrType, String dataType)
	{
		String sql = null;
		if (dataType.equalsIgnoreCase("double"))
		{
			sql = "insert into dyextn_double_type_info (IDENTIFIER) values (" + nextIdAttrType
					+ ")";
		}
		else if (dataType.equalsIgnoreCase("int"))
		{
			sql = "insert into dyextn_integer_type_info (IDENTIFIER) values (" + nextIdAttrType
					+ ")";
		}
		else if (dataType.equalsIgnoreCase("long"))
		{
			sql = "insert into dyextn_long_type_info (IDENTIFIER) values (" + nextIdAttrType + ")";
		}
		return sql;
	}

	/**
	 * @param attr
	 * @param nextIdMetadata
	 * @param nextIdDbProp
	 * @throws SQLException
	 * @throws DAOException
	 */
	private void insertDbProp(String attr, int nextIdMetadata, int nextIdDbProp, JDBCDAO jdbcdao)
			throws SQLException, DAOException
	{

		String sql;
		String columnName = getColumnNameOfAttribute(attr, attrColumnMap);
		sql = "insert into dyextn_database_properties (IDENTIFIER,NAME) values (" + nextIdDbProp
				+ ",'" + columnName + "')";
		jdbcdao.executeUpdate(sql);
		sql = "insert into dyextn_column_properties (IDENTIFIER,PRIMITIVE_ATTRIBUTE_ID) values ("
				+ nextIdDbProp + "," + nextIdMetadata + ")";
		jdbcdao.executeUpdate(sql);
	}

	/**
	 * @param attr
	 * @param attrColumnMap
	 * @return
	 */
	private String getColumnNameOfAttribute(String attr, Map<String, String> attrColumnMap)
	{
		return attrColumnMap.get(attr);
	}

	/**
	 * @param attr
	 * @param attrDatatypeMap
	 * @return
	 */
	private String getDataTypeOfAttribute(String attr, Map<String, String> attrDatatypeMap)
	{
		return attrDatatypeMap.get(attr);
	}

	/**
	 * 
	 */
	private void populateEntityAttributeMap()
	{
		List<String> attributes = new ArrayList<String>();
		attributes.add(UNSIGNED_CONSENT);
		entityAttrMap.put("edu.wustl.clinportal.domain.ClinicalStudy", attributes);

		attributes = new ArrayList<String>();
		attributes.add(SIGNED_CONSENT);
		attributes.add(CONSENT_DATE);
		entityAttrMap.put("edu.wustl.clinportal.domain.ClinicalStudyRegistration", attributes);
	}

	/**
	 * 
	 */
	private void populateAttributeColumnNameMap()
	{
		attrColumnMap.put(UNSIGNED_CONSENT, "UNSIGNED_CONSENT_DOC_URL");
		attrColumnMap.put(SIGNED_CONSENT, "CONSENT_DOC_URL");
		attrColumnMap.put(CONSENT_DATE, "CONSENT_SIGN_DATE");
	}

	/**
	 * 
	 */
	private void populateAttributeDatatypeMap()
	{
		attrTypeMap.put(UNSIGNED_CONSENT, "string");
		attrTypeMap.put(SIGNED_CONSENT, "string");
		attrTypeMap.put(CONSENT_DATE, "date");
	}

	/**
	 * 
	 */
	private void populateAttributePrimaryKeyMap()
	{
		attrPrimarkeyMap.put(UNSIGNED_CONSENT, "0");
		attrPrimarkeyMap.put(SIGNED_CONSENT, "0");
		attrPrimarkeyMap.put(CONSENT_DATE, "0");
	}

	/**
	 * 
	 */
	private void populateEntityList()
	{
		entityList.add("edu.wustl.clinportal.domain.ClinicalStudy");
		entityList.add("edu.wustl.clinportal.domain.ClinicalStudyRegistration");
	}

	/**
	 * @throws SQLException
	 */
	public AddAttribute() throws SQLException
	{

		populateEntityList();
		populateEntityAttributeMap();
		populateAttributeColumnNameMap();
		populateAttributeDatatypeMap();
		populateAttributePrimaryKeyMap();
	}

	/**
	 * @param connection
	 * @param entityAttrMap
	 * @param attrColumnMap
	 * @param attrTypeMap
	 * @param attrPrimkeyMap
	 * @param entityList
	 */
	public AddAttribute(Map<String, List<String>> entityAttrMap,
			Map<String, String> attrColumnMap, Map<String, String> attrTypeMap,
			Map<String, String> attrPrimkeyMap, List<String> entityList)
	{

		this.entityAttrMap = entityAttrMap;
		this.attrColumnMap = attrColumnMap;
		this.attrTypeMap = attrTypeMap;
		this.attrPrimarkeyMap = attrPrimkeyMap;
		this.entityList = entityList;
	}

	/**
	 * @param tablename
	 * @param jdbcdao
	 * @return
	 * @throws SQLException
	 * @throws DAOException
	 */
	private int getNextId(String tablename, JDBCDAO jdbcdao) throws SQLException, DAOException
	{
		String sql = "select max(identifier) from " + tablename;
		ResultSet resultSet = jdbcdao.getQueryResultSet(sql);
		int nextId = 0;
		if (resultSet.next())
		{
			int maxId = resultSet.getInt(1);
			nextId = maxId + 1;
		}
		jdbcdao.closeStatement(resultSet);
		return nextId;
	}
}