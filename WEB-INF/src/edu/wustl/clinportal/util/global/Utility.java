/**
 * <p>Title: Utility Class>
 * <p>Description:  Utility Class contain general methods used through out the application. </p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 */

package edu.wustl.clinportal.util.global;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerConstantsInterface;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.clinportal.action.annotations.AnnotationConstants;
import edu.wustl.clinportal.bizlogic.UserBizLogic;
import edu.wustl.clinportal.domain.StudyFormContext;
import edu.wustl.clinportal.domain.User;
import edu.wustl.clinportal.privilege.PrivilegeConstants;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.beans.QueryResultObjectData;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.bizlogic.CDEBizLogic;
import edu.wustl.common.cde.CDE;
import edu.wustl.common.cde.CDEManager;
import edu.wustl.common.cde.PermissibleValue;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;
import edu.wustl.common.util.PagenatedResultData;
import edu.wustl.common.util.QueryParams;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.global.QuerySessionData;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.security.exception.SMException;
import edu.wustl.security.global.ProvisionManager;
import edu.wustl.security.locator.SecurityManagerPropertiesLocator;
import edu.wustl.security.privilege.PrivilegeCache;
import edu.wustl.security.privilege.PrivilegeManager;
import edu.wustl.security.privilege.PrivilegeUtility;
import edu.wustl.simplequery.bizlogic.QueryBizLogic;
import gov.nih.nci.security.UserProvisioningManager;
import gov.nih.nci.security.authorization.domainobjects.Role;
import gov.nih.nci.security.dao.RoleSearchCriteria;
import gov.nih.nci.security.exceptions.CSTransactionException;

/**
 * Utility Class contain general methods used through out the application.
 * 
 * @author rukhsana_sameer
 */
public class Utility
{

	/**
	 * @return
	 */
	public static Map getSpecimenTypeMap()
	{
		CDE specimenClassCDE = CDEManager.getCDEManager().getCDE(Constants.CDE_NAME_SPECIMEN_CLASS);
		Set setPV = specimenClassCDE.getPermissibleValues();
		Iterator itr = setPV.iterator();

		List specimenClassList = CDEManager.getCDEManager().getPermissibleValueList(
				Constants.CDE_NAME_SPECIMEN_CLASS, null);
		Map subTypeMap = new HashMap();
		specimenClassList.add(new NameValueBean(Constants.SELECT_OPTION, "-1"));

		while (itr.hasNext())
		{
			List innerList = new ArrayList();
			Object obj = itr.next();
			PermissibleValue pvalue = (PermissibleValue) obj;
			String tmpStr = pvalue.getValue();
			specimenClassList.add(new NameValueBean(tmpStr, tmpStr));

			Set list1 = pvalue.getSubPermissibleValues();
			Iterator itr1 = list1.iterator();
			innerList.add(new NameValueBean(Constants.SELECT_OPTION, "-1"));

			while (itr1.hasNext())
			{
				Object obj1 = itr1.next();
				PermissibleValue pv1 = (PermissibleValue) obj1;
				// Setting Specimen Type
				String tmpInnerStr = pv1.getValue();
				innerList.add(new NameValueBean(tmpInnerStr, tmpInnerStr));
			}

			subTypeMap.put(pvalue.getValue(), innerList);
		}

		return subTypeMap;
	}

	/**
	 * @param specimenClass
	 * @return
	 */
	public static List getSpecimenTypes(String specimenClass)
	{
		Map specimenTypeMap = getSpecimenTypeMap();
		List typeList = (List) specimenTypeMap.get(specimenClass);

		return typeList;
	}

	/**
	 * @return
	 */
	public static List getSpecimenClassTypes()
	{
		CDE specimenClassCDE = CDEManager.getCDEManager().getCDE(Constants.CDE_NAME_SPECIMEN_CLASS);
		Set setPV = specimenClassCDE.getPermissibleValues();
		Iterator itr = setPV.iterator();

		List speciClassTypeLst = new ArrayList();

		while (itr.hasNext())
		{

			Object obj = itr.next();
			PermissibleValue pvalue = (PermissibleValue) obj;
			String tmpStr = pvalue.getValue();
			speciClassTypeLst.add(tmpStr);

		} // class and values set

		return speciClassTypeLst;

	}

	private static String pattern = "MM-dd-yyyy";

	/**
	 * @param date
	 *            String representation of date.
	 * @param pattern
	 *            Date pattern to be used.
	 * @return Month of the given date.
	 * @author mandar_deshmukh
	 */
	public static int getMonth(String date, String pattern)
	{
		int month = 0;
		month = getCalendar(date, pattern).get(Calendar.MONTH);
		month = month + 1;
		return month;
	}

	/**
	 * @param date
	 * @return
	 */
	public static int getMonth(String date)
	{
		int month = 0;

		month = getCalendar(date, pattern).get(Calendar.MONTH);
		month = month + 1;
		return month;
	}

	/**
	 * @param date
	 *            String representation of date.
	 * @param pattern
	 *            Date pattern to be used.
	 * @return Day of the given date.
	 * @author mandar_deshmukh
	 */
	public static int getDay(String date, String pattern)
	{
		int day = 0;
		day = getCalendar(date, pattern).get(Calendar.DAY_OF_MONTH);
		return day;
	}

	/**
	 * @param date
	 * @return
	 */
	public static int getDay(String date)
	{
		int day = 0;
		day = getCalendar(date, pattern).get(Calendar.DAY_OF_MONTH);
		return day;
	}

	/**
	 * @param date
	 *            String representation of date.
	 * @param pattern
	 *            Date pattern to be used.
	 * @return Year of the given date.
	 * @author mandar_deshmukh
	 */
	public static int getYear(String date, String pattern)
	{
		int year = 0;
		year = getCalendar(date, pattern).get(Calendar.YEAR);
		return year;
	}

	/**
	 * @param date
	 * @return
	 */
	public static int getYear(String date)
	{
		int year = 0;
		year = getCalendar(date, pattern).get(Calendar.YEAR);
		return year;
	}

	/*
	 * Method to validate the date given by the user and return a calendar
	 * object for the date instance. It returns a calendar object based on the
	 * date provided. If invalid date is provided it returns the current
	 * calendar instance.
	 */
	private static Calendar getCalendar(String date, String pattern)
	{
		Calendar calendar = Calendar.getInstance();
		try
		{
			SimpleDateFormat dateformat = new SimpleDateFormat(pattern);
			Date givenDate = dateformat.parse(date);
			calendar.setTime(givenDate);

		}
		catch (Exception e)
		{
			Logger.out.error(e);

		}
		return calendar;
	}

	/**
	 * This method returns a list of string values for a given CDE.
	 * 
	 * @param cdeName
	 * @return
	 */
	public static List getListForCDE(String cdeName)
	{
		CDE cde = CDEManager.getCDEManager().getCDE(cdeName);
		List valueList = new ArrayList();

		if (cde != null)
		{
			Iterator iterator = cde.getPermissibleValues().iterator();
			while (iterator.hasNext())
			{
				PermissibleValue permissibleValue = (PermissibleValue) iterator.next();

				valueList.addAll(loadPermissibleValue(permissibleValue));
			}
		}

		Collections.sort(valueList);
		valueList.add(0, Constants.SELECT_OPTION);
		return valueList;
	}

	/**
	 * returns list of all subPVs under this PV, recursively.
	 * 
	 * @param permissibleValue
	 * @return
	 */
	private static List loadPermissibleValue(PermissibleValue permissibleValue)
	{
		List pvList = new ArrayList();
		String value = permissibleValue.getValue();
		pvList.add(value);

		Iterator iterator = permissibleValue.getSubPermissibleValues().iterator();
		while (iterator.hasNext())
		{
			PermissibleValue subPermValue = (PermissibleValue) iterator.next();
			List subPVList = loadPermissibleValue(subPermValue);
			pvList.addAll(subPVList);
		}
		return pvList;
	}

	/**
	 * Changes the format of the string compatible to New Grid Format, removing
	 * escape characters and special characters from the string Also replaces
	 * comma with space as comma is used as a delimiter.
	 * 
	 * @param obj -     Unformatted obj to be printed in Grid Format
	 * @return obj - Foratted obj to print in Grid Format
	 */
	public static Object toNewGridFormat(Object obj)
	{
		obj = edu.wustl.common.util.Utility.toGridFormat(obj);
		if (obj instanceof String)
		{
			String objString = (String) obj;
			StringBuffer tokenedString = new StringBuffer();

			StringTokenizer tokenString = new StringTokenizer(objString, ",");

			while (tokenString.hasMoreTokens())
			{
				tokenedString.append(tokenString.nextToken() + " ");
			}
			String gridFormattedStr = new String(tokenedString);
			obj = gridFormattedStr;
		}

		return obj;
	}

	/**
	 * Prepare Response List
	 * 
	 * @param opr
	 *            If Operation = Edit then "Withdraw" is added in the List
	 * @return listOfResponces
	 */
	public static List responceList(String addeditOperation)
	{
		List listOfResponces = new ArrayList();
		listOfResponces.add(new NameValueBean(Constants.NOT_SPECIFIED, Constants.NOT_SPECIFIED));
		listOfResponces.add(new NameValueBean(Constants.BOOLEAN_YES, Constants.BOOLEAN_YES));
		listOfResponces.add(new NameValueBean(Constants.BOOLEAN_NO, Constants.BOOLEAN_NO));

		return listOfResponces;
	}

	public static Long toLong(String string) throws NumberFormatException
	{
		Long val = null;
		if ((string != null) && (string.trim() != ""))
		{
			val = Long.valueOf(string);
		}
		return val;
	}

	/**
	 * Changes the format of the string compatible to New Grid Format. Also
	 * create hyperlink for the columns that are to be shown as hyperlink.
	 * 
	 * @param row
	 *            The List representing row of that is to be shown in the Grid.
	 * @param hyperlkColMap
	 *            Map containing information about which column to be marked as
	 *            Hyperlink. It is map of the column index that are to be shown
	 *            as hyperlink verses the QueryResultObjectData, which contain
	 *            information of the aliasName of the Object to which this
	 *            column belongs & the index of the associated Id column.
	 * @param index
	 *            The index of the attribute in List whose format is to be
	 *            changed.
	 * @return The Formated object, Hypelink format if the Column is tobe marked
	 *         as hyperlink.
	 * @see edu.wustl.clinportal.util.global.Utility#toNewGridFormat(java.lang.Object)
	 *      Patch ID: SimpleSearchEdit_6
	 */
	public static Object toNewGridFormatWithHref(List<String> row,
			Map<Integer, QueryResultObjectData> hyperlkColMap, int index)
	{
		Object obj = row.get(index);

		if (obj instanceof String && !((String) obj).equals(Constants.HASHED_OUT))
		{
			obj = toNewGridFormat(obj);

			QueryResultObjectData queryResObjData = hyperlkColMap.get(index);

			if (queryResObjData != null)// This column is to be shown as
			// hyperlink.
			{
				if (obj == null || obj.equals(""))
				{
					obj = "NA";
				}

				/**
				 * 
				 * Edit User: password fields empty & error on submitting new
				 * password Added PageOf Attribute as request parameter in the
				 * link.
				 */
				String aliasName = queryResObjData.getAliasName();
				String link = "SimpleSearchEdit.do?"
						+ edu.wustl.common.util.global.Constants.TABLE_ALIAS_NAME + "=" + aliasName
						+ "&" + Constants.SYSTEM_IDENTIFIER + "="
						+ row.get(queryResObjData.getIdentifierColumnId()) + "&"
						+ Constants.PAGE_OF + "=" + Variables.aliasAndPageOfMap.get(aliasName);
				/**
				 * bug ID: 4225 Patch id: 4225_1 Description : Passing a
				 * different name to the pop up window
				 */
				String onclickStr = " onclick=javascript:NewWindow('" + link
						+ "','simpleSearch','800','600','yes') ";
				String hrefTag = "<a class='normalLink' href='#'" + onclickStr + ">" + obj + "</a>";
				obj = hrefTag;
			}
		}
		return obj;
	}

	/**
	 * This method creates a comma separated string of numbers representing
	 * column width.
	 * 
	 */
	public static String getColumnWidth(List columnNames)
	{
		String colWidth = getColumnWidth((String) columnNames.get(0));

		for (int col = 1; col < columnNames.size(); col++)
		{
			String columnName = (String) columnNames.get(col);
			colWidth = colWidth + "," + getColumnWidth(columnName);
		}
		return colWidth;
	}

	private static String getColumnWidth(String columnName)
	{
		/*
		 * Patch ID: Bug#3090_31 Description: The first column which is just a
		 * check box, used to select the rows, was always given a width of 100.
		 * Now width of 20 is set for the first column. Also, width of 100 was
		 * being applied to each column of the grid, which increasing the total
		 * width of the grid. Now the width of each column is set to 80.
		 */
		String columnWidth = null;
		if (columnName.trim().equals("ID"))
		{
			columnWidth = "0";
		}
		else if (columnName.trim().equals(""))
		{
			columnWidth = "20";
		}
		else
		{
			columnWidth = "80";
		}
		return columnWidth;
	}

	/**
	 * This method set TissueList with only Leaf node.
	 * 
	 * @return tissueList
	 * @throws BizLogicException 
	 */
	public static List tissueSiteList() throws BizLogicException
	{
		CDE cde = CDEManager.getCDEManager().getCDE(
				edu.wustl.simplequery.global.Constants.CDE_NAME_TISSUE_SITE);
		IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
		//		CDEBizLogic cdeBizLogic = (CDEBizLogic) BizLogicFactory.getInstance().getBizLogic(
		//				Constants.CDE_FORM_ID);
		CDEBizLogic cdeBizLogic = (CDEBizLogic) factory.getBizLogic(Constants.CDE_FORM_ID);
		List tissueList = new ArrayList();
		// set first index as --select-- option to display in combo.
		tissueList.add(new NameValueBean(Constants.SELECT_OPTION, ""
				+ Constants.SELECT_OPTION_VALUE));
		// get the filtered tissue list which is a leaf node
		cdeBizLogic.getFilteredCDE(cde.getPermissibleValues(), tissueList);
		return tissueList;
	}

	/**
	 * 
	 * Description: Signature of method is changed to pass only required
	 * parameters insert of whole object, so that the method become more
	 * maintainable
	 */
	/**
	 * Method to check type and class compatibility of specimen as a part of
	 * validation process.
	 * 
	 * @param specimenClass
	 *            specimen class
	 * @param specimenType
	 *            type of specimen
	 * @return boolean returns (true/false) depending on type and class are
	 *         compatible or not
	 * @throws BizLogicException 
	 */
	public static boolean validateSpecimenTypeClass(String specimenClass, String specimenType)
			throws BizLogicException
	{
		List specimenClassList = CDEManager.getCDEManager().getPermissibleValueList(
				Constants.CDE_NAME_SPECIMEN_CLASS, null);
		if (specimenClass == null || !Validator.isEnumeratedValue(specimenClassList, specimenClass))
		{
			//throw new DAOException(ApplicationProperties.getValue("protocol.class.errMsg"));
			throw new BizLogicException(ErrorKey.getErrorKey("error.security"), null,
					ApplicationProperties.getValue("protocol.class.errMsg"));
		}
		if (!Validator.isEnumeratedValue(Utility.getSpecimenTypes(specimenClass), specimenType))
		{
			//throw new DAOException(ApplicationProperties.getValue("protocol.type.errMsg"));
			throw new BizLogicException(ErrorKey.getErrorKey("error.security"), null,
					ApplicationProperties.getValue("protocol.type.errMsg"));
		}
		/* Patch ends here */
		return true;
	}

	/**
	 * This method will return Permissible Value List from CDE depending on the
	 * listType
	 * 
	 * @param listType
	 * @return
	 */
	public static List getListFromCDE(String listType)
	{
		List CDEList = CDEManager.getCDEManager().getPermissibleValueList(listType, null);
		return CDEList;
	}

	/**
	 * This method returns the user name of user of given id (format of name:
	 * LastName,FirstName)
	 * 
	 * @param userId
	 *            user id of which user name has to return
	 * @return userName in the given format
	 * @throws BizLogicException 
	 * @throws Exception
	 *             generic exception
	 */
	public static String getUserNameById(Long userId) throws BizLogicException
	{
		String className = User.class.getName();
		String colName = Constants.SYSTEM_IDENTIFIER;
		String colValue = userId.toString();
		String userName = "";
		IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
		/*UserBizLogic bizLogic = (UserBizLogic) BizLogicFactory.getInstance().getBizLogic(
				User.class.getName());*/
		UserBizLogic bizLogic = (UserBizLogic) factory.getBizLogic(User.class.getName());
		List userList = bizLogic.retrieve(className, colName, colValue);
		if (userList != null && !userList.isEmpty())
		{
			User user = (User) userList.get(0);
			userName = user.getLastName();
			userName += ", ";
			userName += user.getFirstName();
		}
		return userName;
	}

	/**
	 * Generates key for ParticipantMedicalIdentifierMap
	 * 
	 * @param idx
	 *            serial number
	 * @param keyFor
	 *            Attribute based on which respective key is to generate
	 * @return key for map attribute
	 */
	public static String getParticipantMedicalIdentifierKeyFor(int idx, String keyFor)
	{
		return (Constants.PARTICIPANT_MEDICAL_IDENTIFIER + idx + keyFor);
	}

	/**
	 * To get the array of ids from the given DomainObject collection.
	 * 
	 * @param domainObjColn
	 *            The collection of domain objects.
	 * @return The array of ids from the given DomainObject collection.
	 */
	public static long[] getobjectIds(Collection domainObjColn)
	{
		long ids[] = new long[domainObjColn.size()];
		int idx = 0;
		Iterator itr = domainObjColn.iterator();
		while (itr.hasNext())
		{
			AbstractDomainObject domainObject = (AbstractDomainObject) itr.next();
			ids[idx] = domainObject.getId().longValue();
			idx++;
		}
		return ids;
	}

	/**
	 * @param request
	 * @param recordsPerPage
	 * @param pageNum
	 * @param querySessionData
	 * @return
	 * @throws DAOException
	 */
	public static List getPaginationDataList(HttpServletRequest request,
			SessionDataBean sessionData, int recordsPerPage, int pageNum,
			QuerySessionData querySessionData) throws DAOException
	{
		List pagiDataList;
		querySessionData.setRecordsPerPage(recordsPerPage);
		int startIndex = recordsPerPage * (pageNum - 1);
		QueryBizLogic qBizLogic = new QueryBizLogic();
		PagenatedResultData pageResData = qBizLogic.execute(sessionData, querySessionData,
				startIndex);
		pagiDataList = pageResData.getResult();
		return pagiDataList;
	}

	/**
	 * Executes hql Query and returns the results.
	 * 
	 * @param hql
	 *            String hql
	 * @throws DAOException
	 *             DAOException
	 * @throws ClassNotFoundException
	 *             ClassNotFoundException
	 */
	public static List executeQuery(String hql) throws DAOException
	{
		//HibernateDAO dao = (HibernateDAO) DAOFactory.getInstance().getDAO(Constants.HIBERNATE_DAO);
		String appName = CommonServiceLocator.getInstance().getAppName();
		IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
		DAO dao = daoFactory.getDAO();
		dao.openSession(null);
		//List list = dao.executeQuery(hql, null, false, null);
		List list = dao.executeQuery(hql);
		dao.closeSession();
		return list;
	}

	// for conflictResolver pagination:kalpana
	/**
	 * @param sql
	 * @param sessionDataBean
	 * @param isSecureExecute
	 * @param quryResObjDataMap
	 * @param hasCondOnIdfFld
	 * @param startIndex
	 * @param totalRecords
	 * @return
	 * @throws BizLogicException
	 * @throws SQLException
	 */
	public static PagenatedResultData executeForPagination(String sql,
			SessionDataBean sessionDataBean, boolean isSecureExecute, Map quryResObjDataMap,
			boolean hasCondOnIdfFld, int startIndex, int totalRecords) throws BizLogicException,
			SQLException
	{
		try
		{
			//JDBCDAO dao = (JDBCDAO) DAOFactory.getInstance().getDAO(Constants.JDBC_DAO);
			String appName = CommonServiceLocator.getInstance().getAppName();
			IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
			JDBCDAO dao = daoFactory.getJDBCDAO();
			dao.openSession(null);
			Logger.out.debug("SQL************" + sql);
			/*PagenatedResultData pagenatedResultData = dao.executeQuery(sql, sessionDataBean,
					isSecureExecute, hasCondOnIdfFld, quryResObjDataMap, startIndex, totalRecords);*/
			///
			QueryParams queryParams = new QueryParams();

			queryParams.setQuery(sql);
			queryParams.setSessionDataBean(sessionDataBean);
			queryParams.setSecureToExecute(isSecureExecute);
			queryParams.setHasConditionOnIdentifiedField(hasCondOnIdfFld);
			queryParams.setQueryResultObjectDataMap(quryResObjDataMap);
			queryParams.setNoOfRecords(totalRecords);
			queryParams.setStartIndex(startIndex);
			PagenatedResultData pagenatedResultData = null;//= dao.executeQuery(queryParams);
			///

			dao.closeSession();
			return pagenatedResultData;
		}
		catch (DAOException daoExp)
		{

			throw new BizLogicException(daoExp);
		}

	}

	/**
	 * limits the title of the saved query to 125 characters to avoid horizontal
	 * scrollbar
	 * 
	 * @param title -
	 *            title of the saved query (may be greater than 125 characters)
	 * @return - query title upto 125 characters
	 */
	public static String getQueryTitle(String title)
	{
		String multilineTitle = "";
		if (title.length() <= Constants.CHARACTERS_IN_ONE_LINE)
		{
			multilineTitle = title;
		}
		else
		{
			multilineTitle = title.substring(0, Constants.CHARACTERS_IN_ONE_LINE) + ".....";
		}
		return multilineTitle;
	}

	/**
	 * returns the entire title to display it in tooltip .
	 * 
	 * @param title -
	 *            title of the saved query
	 * @return tooltip string
	 */
	public static String getTooltip(String title)
	{
		String tooltip = title.replaceAll("'", Constants.SINGLE_QUOTE_ESCAPE_SEQUENCE); // escape sequence
		// for '
		return tooltip;
	}

	/**
	 * Method to check the associated de identified report is quarantined or not
	 * 
	 * @param reportId
	 *            id of identified report
	 * @return boolean value for is quarantine
	 * @throws DAOException
	 * @throws ClassNotFoundException
	 */
	public static boolean isQuarantined(Long reportId) throws DAOException, ClassNotFoundException
	{
		String hqlString = "select ispr.deIdentifiedSurgicalPathologyReport.id "
				+ " from edu.wustl.clinportal.domain.pathology.IdentifiedSurgicalPathologyReport as ispr, "
				+ " edu.wustl.clinportal.domain.pathology.DeidentifiedSurgicalPathologyReport as deidReport"
				+ " where ispr.id = " + reportId
				+ " and ispr.deIdentifiedSurgicalPathologyReport.id=deidReport.id"
				+ " and ispr.deIdentifiedSurgicalPathologyReport.isQuanrantined='"
				+ Constants.QUARANTINE_REQUEST + "'";

		List reportIDList = Utility.executeQuery(hqlString);
		boolean flag = false;
		if (reportIDList != null && !reportIDList.isEmpty())
		{
			flag = true;
		}
		return flag;
	}

	/**
	 * Adds the attribute values in the list in sorted order and returns the
	 * list containing the attribute values in proper order
	 * 
	 * @param dataType -
	 *            data type of the attribute value
	 * @param value1 -
	 *            first attribute value
	 * @param value2 -
	 *            second attribute value
	 * @return List containing value1 and value2 in sorted order
	 */
	public static ArrayList<String> getAttributeValuesInProperOrder(String dataType, String value1,
			String value2)
	{
		ArrayList<String> attributeValues = new ArrayList<String>();

		String val1 = value1;
		String val2 = value2;
		if (dataType.equalsIgnoreCase(EntityManagerConstantsInterface.DATE_ATTRIBUTE_TYPE))
		{
			if (getYear(value1) > getYear(value2))
			{
				val1 = value2;
				val2 = value1;

			}
			else
			{
				if (getMonth(value1) > getMonth(value2))
				{
					val1 = value2;
					val2 = value1;
				}
				else
				{
					if (getDay(value1) > getDay(value2))
					{
						val1 = value2;
						val2 = value1;
					}
				}
			}
		}
		else
		{
			if (dataType.equalsIgnoreCase(EntityManagerConstantsInterface.INTEGER_ATTRIBUTE_TYPE)
					|| dataType
							.equalsIgnoreCase(EntityManagerConstantsInterface.LONG_ATTRIBUTE_TYPE))
			{
				if (Long.parseLong(value1) > Long.parseLong(value2))
				{
					val1 = value2;
					val2 = value1;
				}

			}
			else
			{
				if (dataType
						.equalsIgnoreCase(EntityManagerConstantsInterface.DOUBLE_ATTRIBUTE_TYPE))
				{
					if (Double.parseDouble(value1) > Double.parseDouble(value2))
					{
						val1 = value2;
						val2 = value1;
					}

				}
			}
		}
		attributeValues.add(val1);
		attributeValues.add(val2);
		return attributeValues;
	}

	/**
	 * This method returns the new Date by adding the days as specified in the
	 * Date which user passes.
	 * 
	 * @param date
	 *            Date in which days are to be added
	 * @param daysToBeAdded
	 *            Number of days to be added
	 * @return
	 */
	public static Date getNewDateByAdditionOfDays(Date date, int daysToBeAdded)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(calendar.DAY_OF_MONTH, daysToBeAdded);
		return calendar.getTime();
	}

	//// @@@@ -- CLINPORTAL METHODS -- @@@@ ////

	/**
	* @param entity_name_participant
	* @return
	* @throws DynamicExtensionsApplicationException 
	* @throws DynamicExtensionsSystemException 
	*/
	public static Long getEntityId(String entityName) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		Long val = Long.valueOf(0);
		if (entityName != null)
		{
			EntityManagerInterface entityManager = EntityManager.getInstance();
			Long entityId;
			entityId = entityManager.getEntityId(entityName);
			if (entityId != null)
			{
				val = entityId;
			}
		}
		return val;
	}

	/**
	* Creates a key for events 
	* @param object
	* @param eventCounter
	* @param formCounter
	* @param attribute
	* @return
	*/
	public static String createKey(String object, int eventCounter, int formCounter,
			String attribute)
	{
		String val = "";
		StringBuffer buffer = new StringBuffer();
		if (object != null)
		{
			if (object.equals(Constants.CLINICAL_STUDY_EVENT))
			{
				buffer.append(object);
				buffer.append(eventCounter);
				buffer.append("_");
				buffer.append(attribute);
				val = buffer.toString();
			}
			else if (object.contains(Constants.FORM_CONTEXT) && formCounter != 0)
			{
				buffer.append(Constants.CLINICAL_STUDY_EVENT);
				buffer.append(eventCounter);
				buffer.append("_");
				buffer.append(object);
				buffer.append(formCounter);
				buffer.append("_");
				buffer.append(attribute);
				val = buffer.toString();
			}
		}

		return val;
	}

	/**
	* To get the dummy Parameter to attach the URL.Added for solving IFrame
	* caching problem in case of IE
	* 
	* @return The Dummy parameter for attaching the Applet URL.
	*/
	public static String attachDummyParam()
	{
		Random random = new Random();
		int dummyParameter = random.nextInt();
		String param = "dummyParam=" + dummyParameter;
		return param;
	}

	/**
	 * @return
	 */
	public static String getLabel(String lastName, String firstName)
	{
		String val = null;
		if (lastName != null && !lastName.equals("") && firstName != null && !firstName.equals(""))
		{
			val = lastName + "," + firstName;
		}
		else if (lastName != null && !lastName.equals(""))
		{
			val = lastName;
		}
		else if (firstName != null && !firstName.equals(""))
		{
			val = firstName;
		}
		return val;
	}

	/**
	 * 
	 * @param xmlFileName
	 * @throws Exception
	 */
	public static void readPrivileges(String xmlFileName,
			Map<String, Map<String, Map<String, List<NameValueBean>>>> privilegeMap)
			throws BizLogicException
	{

		String rootElementName = PrivilegeConstants.ELEMENT_CLINICAL_STUDY;
		String nextElementName = PrivilegeConstants.ELEMENT_CLINICAL_STUDY_TITLE;
		SAXReader saxReader = new SAXReader();
		try
		{
			FileInputStream inputStream = new FileInputStream(xmlFileName);
			Document document = null;
			document = saxReader.read(inputStream);
			Element nameElement = null;
			Element rootElement = document.getRootElement();
			Iterator<?> elementIterator = rootElement.elementIterator(rootElementName);//rootElementName
			Element element = null;
			while (elementIterator.hasNext())
			{
				element = (Element) elementIterator.next();
				nameElement = element.element(nextElementName); ////title
				String title = nameElement.getText();
				if (title.length() == 0)
				{
					throw new BizLogicException(ErrorKey.getErrorKey("xml.parse.error"), null,
							ApplicationProperties.getValue("cs.name.required"));
				}
				Element csUsers = element.element(PrivilegeConstants.ELEMENT_CSUSERS);
				Element regEle = csUsers.element(PrivilegeConstants.PARTICIPANT_REGISTRATION);
				Iterator<?> eventEleItr = csUsers.elementIterator(PrivilegeConstants.ELEMENT_EVENT);
				Map eventMap = new HashMap();
				iterateEvents(eventEleItr, eventMap);
				nameElement = element.element(PrivilegeConstants.ELEMENT_CSNONUSERS);
				eventMap.put(PrivilegeConstants.ELEMENT_CSNONUSERS, iterateUsers(nameElement));
				eventMap.put(PrivilegeConstants.PARTICIPANT_REGISTRATION, iterateUsers(regEle));
				privilegeMap.put(title, eventMap);
			}
		}
		catch (FileNotFoundException e)
		{
			throw new BizLogicException(ErrorKey.getErrorKey("file.notfound"), null, e.getMessage());
		}
		catch (DocumentException e)
		{
			throw new BizLogicException(ErrorKey.getErrorKey("xml.parse.error"), e, e.getMessage());
		}

	}

	/**
	 * @param catissueEntityGrpName
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	public static boolean isCatissueModelAvailable() throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		boolean isCatissueModelAvl = false;
		EntityManagerInterface entityManager = EntityManager.getInstance();
		Long egroupId = entityManager.getEntityGroupId(AnnotationConstants.CAT_GROUPNAME_IN_CP);
		if (egroupId != null)
		{
			isCatissueModelAvl = true;
		}
		return isCatissueModelAvl;
	}

	/**
	 * 
	 * @param eventEleItr
	 * @param eventMap
	 */
	private static void iterateEvents(Iterator<?> eventEleItr, Map eventMap)
	{
		Element eventElement = null;
		Element studyFrmEle = null;
		Attribute attribute = null;
		while (eventEleItr.hasNext())
		{
			eventElement = (Element) eventEleItr.next();
			attribute = eventElement.attribute(PrivilegeConstants.ELEMENT_LABEL);
			String eventLabel = attribute.getText();
			Iterator<?> studyFrmIterator = eventElement
					.elementIterator(PrivilegeConstants.ELEMENT_STUDYFORM);
			Map<String, List<NameValueBean>> frmMap = new HashMap<String, List<NameValueBean>>();
			while (studyFrmIterator.hasNext())
			{
				studyFrmEle = (Element) studyFrmIterator.next();
				attribute = studyFrmEle.attribute(PrivilegeConstants.ELEMENT_LABEL);
				String frmLabel = attribute.getText();
				List<NameValueBean> users = new ArrayList<NameValueBean>();
				users = iterateUsers(studyFrmEle);
				frmMap.put(frmLabel, users);
				eventMap.put(eventLabel, frmMap);
			}
		}

	}

	/***
	 * 
	 * @param recordEntryList
	 * @throws BizLogicException 
	 */
	public static void checkClosedStatus(List recordEntryList) throws BizLogicException
	{
		if (recordEntryList != null && !recordEntryList.isEmpty())
		{
			Object[] list = (Object[]) recordEntryList.get(0);
			if (list[0] != null && list[0].toString().equals(Constants.ACTIVITY_STATUS_CLOSED))
			{
				//throw new DAOException(ApplicationProperties.getValue("clinicalStudy.closed"));
				throw new BizLogicException(ErrorKey.getErrorKey("error.utility.closedStatus"),
						null, ApplicationProperties.getValue("clinicalStudy.closed"));
			}
			else if (list[1] != null && list[1].toString().equals(Constants.ACTIVITY_STATUS_CLOSED))
			{
				//throw new DAOException(ApplicationProperties.getValue("participant.closed"));
				throw new BizLogicException(ErrorKey.getErrorKey("error.utility.closedStatus"),
						null, ApplicationProperties.getValue("participant.closed"));
			}
			else if (list[2] != null && list[2].toString().equals(Constants.ACTIVITY_STATUS_CLOSED))
			{
				//throw new DAOException(ApplicationProperties.getValue("clinicalStudyReg.closed"));
				throw new BizLogicException(ErrorKey.getErrorKey("error.utility.closedStatus"),
						null, ApplicationProperties.getValue("clinicalStudyReg.closed"));
			}
		}
	}

	/**
	 * @param selectedMenuID Menu that is clicked
	 * @param currentMenuID Menu that is being checked
	 * @param normalMenuClass style class for normal menu
	 * @param selectedMenuClass style class for selected menu
	 * @param menuHoverClass  style class for hover effect
	 * @return The String generated for the TD tag. Creates the selected menu or normal menu.
	 */
	public static String setSelectedMenuItem(int selectedMenuID, int currentMenuID,
			String normalMenuClass, String selectedMenuClass, String menuHoverClass)
	{
		String returnStr = "";
		if (selectedMenuID == currentMenuID)
		{
			returnStr = "<td class=\"" + selectedMenuClass
					+ "\" onmouseover=\"changeMenuStyle(this,\'" + selectedMenuClass
					+ "\')\" onmouseout=\"changeMenuStyle(this,\'" + selectedMenuClass + "\')\">";
		}
		else
		{
			returnStr = "<td class=\"" + normalMenuClass
					+ "\" onmouseover=\"changeMenuStyle(this,\'" + menuHoverClass
					+ "\')\" onmouseout=\"changeMenuStyle(this,\'" + normalMenuClass + "\')\">";
		}

		return returnStr;
	}

	/**
	 * This method is used to process Roles in case Custom role is added / edited
	 * On User and CP page
	 * @param roleName Role name
	 * @throws BizLogicException 
	 */
	public static void processRole(String roleName) throws BizLogicException
	{
		Role role = null;
		try
		{
			UserProvisioningManager upManager = ProvisionManager.getInstance()
					.getUserProvisioningManager();
			role = getRole(roleName);

			if (role != null && role.getId() != null)
			{
				upManager.removeRole(role.getId().toString());
			}
		}
		catch (SMException e)
		{
			throw new BizLogicException(null, e, null);
		}
		catch (CSTransactionException e)
		{
			throw new BizLogicException(null, e, null);
		}
	}

	/**
	 * 
	 * @param roleName
	 * @return
	 * @throws SMException
	 */
	public static Role getRole(String roleName) throws SMException
	{
		PrivilegeUtility privUtility = new PrivilegeUtility();
		if (roleName == null)
		{
			String mess = "Role name passed is null";
			edu.wustl.security.global.Utility.getInstance().throwSMException(null, mess,
					"sm.operation.error");
		}

		//Search for role by the name roleName
		Role role = new Role();
		role.setName(roleName);
		role.setApplication(privUtility.getApplication(SecurityManagerPropertiesLocator
				.getInstance().getApplicationCtxName()));
		RoleSearchCriteria roleSearchCriteria = new RoleSearchCriteria(role);
		List<Role> list;
		try
		{
			list = privUtility.getObjects(roleSearchCriteria);
			if (list != null && !list.isEmpty())
				role = (Role) list.get(0);
		}
		catch (SMException e)
		{
			String mess = "Role not found by name " + roleName;
			edu.wustl.security.global.Utility.getInstance().throwSMException(e, mess,
					"sm.operation.error");
		}
		return role;
	}

	/**
	 * 
	 * @param userEle
	 * @return
	 */
	private static List<NameValueBean> iterateUsers(Element userEle)
	{
		List<NameValueBean> users = new ArrayList<NameValueBean>();
		if (userEle != null)
		{
			Iterator<?> userIterator = userEle.elementIterator(PrivilegeConstants.ELEMENT_USER);

			while (userIterator.hasNext())
			{
				Element userElment = (Element) userIterator.next();
				Element privilege = userElment.element(PrivilegeConstants.ELEMENT_USER_PRIVILEGE);
				String userPrivilege = privilege.getText();
				Element loginName = userElment.element(PrivilegeConstants.ELEMENT_USER_LOGIN_NAME);
				String loginNam = loginName.getText();
				NameValueBean bean = new NameValueBean(loginNam, userPrivilege);
				users.add(bean);
			}
		}
		return users;
	}

	/**
	* returns rolename according to roleID
	* @param roleID
	* @return
	* @throws SMException
	*/
	public static String getRoleName(String roleID) throws SMException
	{
		String roleName = null;
		ProvisionManager provisionManager = ProvisionManager.getInstance();
		if (roleID.equals(provisionManager
				.getRoleID(edu.wustl.security.global.Constants.ROLE_ADMIN)))
		{
			roleName = edu.wustl.security.global.Constants.ROLE_ADMIN;
		}
		else if (roleID.equals(provisionManager
				.getRoleID(edu.wustl.security.global.Constants.SCIENTIST)))
		{
			roleName = edu.wustl.security.global.Constants.SCIENTIST;
		}
		return roleName;
	}

	/**
	 * 
	 * @param frmCntxtId
	 * @param userId
	 * @param userName
	 * @return
	 */
	public static Map<String, Boolean> checkFormPrivileges(String frmCntxtId, String userId,
			String userName) //throws SMException //throws Exception
	{
		boolean read = false, write = false, isPE = true;
		boolean hideForm = false, readOnly = false, defaultb = false;
		String objectId = StudyFormContext.class.getName() + "_" + frmCntxtId;
		try
		{
			PrivilegeManager privilegeManager = PrivilegeManager.getInstance();
			PrivilegeCache privilegeCache = privilegeManager.getPrivilegeCache(userName);
			String roleName = PrivilegeConstants.getRoleName(frmCntxtId, userId);
			Role role = getRole(roleName);
			if (role.getId() == null)
			{
				//that means no role exists so default behaviour
				read = write = true;
				isPE = false;
				defaultb = true;
			}
			else
			{
				read = privilegeCache.hasPrivilege(objectId, PrivilegeConstants.READ);
				write = privilegeCache.hasPrivilege(objectId, PrivilegeConstants.UPDATE);
			}
		}
		catch (SMException e)
		{
			//If exception that means no Role exists so default behaviour
			read = write = true;
			isPE = false;
			defaultb = true;
		}
		if (read && !write && isPE)
		{
			//If PE exits and only read :: display the DE form in read only format  
			readOnly = true;
		}
		else if (!read && !write && isPE)
		{
			//If PE exits and no privileges :: not able to see DE form
			hideForm = true;
		}
		Map<String, Boolean> privileges = new HashMap<String, Boolean>();
		privileges.put(Constants.READ_ONLY, readOnly);
		privileges.put(Constants.HIDEFORMS, hideForm);
		privileges.put(Constants.DEFAULT, defaultb);
		return privileges;
	}

	/**
	 * @param nameValuePairs
	 * @param id
	 * @return
	 */
	public static String getNameValue(List nameValuePairs, String id)
	{
		Iterator itr = nameValuePairs.iterator();
		String returnValue = null;
		while (itr.hasNext())
		{
			NameValueBean bean = (NameValueBean) itr.next();
			if (bean.getValue().equals(id))
			{
				returnValue = bean.getName();
				break;
			}

		}

		return returnValue;
	}

	/**
	 * @param strValue
	 * @param regxExpr
	 * @return
	 */
	public static boolean isSpecialCharStr(String strValue)
	{
		boolean flag = false;

		String strModified = strValue.replaceAll("[^a-zA-Z0-9-_ #()*/ ,]", "");
		if (!strModified.equals(strValue))
		{
			flag = true;
		}
		return flag;

	}

	/**
	 * @param strValue
	 * @param regxExpr
	 * @return
	 */
	public static String trim(String strValue)
	{
		if (strValue != null)
			strValue = strValue.trim();
		return strValue;
	}

	/**
	 * This method populates name value beans list as per query,
	 * i.e. word typed into the auto-complete drop-down text field.
	 * @param querySpecificNVBeans
	 * @param cplist
	 * @param query
	 */
	public static synchronized void populateQuerySpecificNameValueBeansList(
			List<NameValueBean> querySpecificNVBeans, List cplist, String query)
	{
		Locale locale = CommonServiceLocator.getInstance().getDefaultLocale();

		for (Object obj : cplist)
		{
			NameValueBean nvb = (NameValueBean) obj;

			if (nvb.getName().toLowerCase(locale).contains(query.toLowerCase(locale)))
			{
				querySpecificNVBeans.add(nvb);
			}
		}
	}
}