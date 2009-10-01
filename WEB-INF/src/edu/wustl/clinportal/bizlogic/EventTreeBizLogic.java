/**
 * 
 */

package edu.wustl.clinportal.bizlogic;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.ehcache.CacheException;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.clinportal.action.annotations.AnnotationConstants;
import edu.wustl.clinportal.domain.ClinicalStudyEvent;
import edu.wustl.clinportal.domain.ClinicalStudyRegistration;
import edu.wustl.clinportal.domain.EventEntry;
import edu.wustl.clinportal.domain.RecordEntry;
import edu.wustl.clinportal.domain.StudyFormContext;
import edu.wustl.clinportal.util.CatissueCoreCacheManager;
import edu.wustl.clinportal.util.DataEntryUtil;
import edu.wustl.clinportal.util.EventTreeObject;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.clinportal.util.global.Utility;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.dao.DAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.security.exception.SMException;
import edu.wustl.security.privilege.PrivilegeCache;
import edu.wustl.security.privilege.PrivilegeManager;

/**
 * @author deepali
 *
 */
public class EventTreeBizLogic
{

	PrivilegeCache privilegeCache = null;

	/**
	 * This method takes all events under study and generate tree structure for each event
	 * @param clinicalStudyId
	 * @param participantId
	 * @param node
	 * @param request
	 * @throws IllegalStateException
	 * @throws CacheException
	 * @throws DynamicExtensionsSystemException
	 * @throws DAOException
	 * @throws DynamicExtensionsApplicationException
	 * @throws SQLException
	 * @throws BizLogicException 
	 * @throws SMException 
	 * @return
	 */
	public String getCSEvents(Long clinicalStudyId, Long participantId, String node,
			HttpServletRequest request) throws IllegalStateException, CacheException,
			DynamicExtensionsSystemException, DAOException, DynamicExtensionsApplicationException,
			SQLException, BizLogicException, SMException
	{
		PrivilegeManager privilegeManager = PrivilegeManager.getInstance();
		SessionDataBean sessionDataBean = (SessionDataBean) request.getSession().getAttribute(
				Constants.SESSION_DATA);
		privilegeCache = privilegeManager.getPrivilegeCache(sessionDataBean.getUserName());

		ClinicalStudyRegistrationBizLogic bizlogic = new ClinicalStudyRegistrationBizLogic();
		//DataEntryUtil dataBizLogic = new DataEntryUtil();
		List eventCollection = null;
		String strCStudyEvntHql = "select CSE From " + ClinicalStudyEvent.class.getName()
				+ " as CSE where CSE.clinicalStudy.id =" + clinicalStudyId
				+ " and (CSE.activityStatus IS NOT NULL and CSE.activityStatus !='"
				+ Constants.ACTIVITY_STATUS_DISABLED + "') order by  CSE.studyCalendarEventPoint asc,CSE.id asc ";
		eventCollection = executeQuery(strCStudyEvntHql);

		List regCollection = (List) bizlogic.getClinicalStudyRegistration(clinicalStudyId,
				participantId);
		Date registrationDate = null;
		ClinicalStudyRegistration clStudyRegstn = null;
		if (regCollection != null && !regCollection.isEmpty())
		{
			clStudyRegstn = (ClinicalStudyRegistration) regCollection.get(0);
			registrationDate = clStudyRegstn.getRegistrationDate();
		}
		Iterator evntIter = eventCollection.iterator();
		StringBuffer xmlString = new StringBuffer();
		xmlString.append("<node>");
		while (evntIter.hasNext())
		{
			int nodeId = 0;

			ClinicalStudyEvent event = (ClinicalStudyEvent) evntIter.next();
			{
				int noOfEntries = 1;

				String eventLabel = event.getCollectionPointLabel();
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(registrationDate);
				calendar.add(Calendar.DATE, event.getStudyCalendarEventPoint().intValue());

				SimpleDateFormat customFormat = new SimpleDateFormat("MMM-dd-yyyy");
				String eventDisplayLabel = "";
				//Checked for the encountered date
				if (isEncounteredDateExist(clStudyRegstn, event))
				{
					eventDisplayLabel = eventLabel;
				}
				else
				{
					Date expectedDate = new Date(calendar.getTimeInMillis());
					eventDisplayLabel = eventLabel + ":" + customFormat.format(expectedDate);
				}
				/* OBJECT.NAME_eventId_containerId_formContextId_regId_eventEntryId_eventEntryCount_recEntryId_dynamicRecId
				 */
				String eventNodeId = event.getId() + "_0_0_0_0_0_0_0";

				xmlString.append("<node id= \"");
				xmlString.append(Constants.CLINICAL_STUDY_EVENT_OBJECT + "_" + event.getId()
						+ "_0_0_0_0_0_0_0");
				xmlString.append("\" " + "name=\"" + eventDisplayLabel + "\" " + "toolTip=\""
						+ eventDisplayLabel + "\">");

				EventTreeObject tree = new EventTreeObject();
				tree.populateObject(node);
				{
					StringBuffer eventEntryHQL = new StringBuffer();
					eventEntryHQL
							.append("select eventEntry.id,eventEntry.encounterDate,eventEntry.entryNumber from ");
					eventEntryHQL.append(EventEntry.class.getName());
					eventEntryHQL.append(" as eventEntry where eventEntry.clinicalStudyEvent.id=");
					eventEntryHQL.append(event.getId());
					eventEntryHQL.append(" and eventEntry.clinicalStudyRegistration.id=");
					eventEntryHQL.append(clStudyRegstn.getId());

					List<Object[]> evntEntryColn = executeQuery(eventEntryHQL.toString());
					Integer eventNoOfEntry = 0;
					if (event.getIsInfiniteEntry() != null && event.getIsInfiniteEntry())
					{
						if (!evntEntryColn.isEmpty())
						{
							eventNoOfEntry = evntEntryColn.size();
						}
						else
						{
							eventNoOfEntry = 1;
							DataEntryUtil dataBizLogic = new DataEntryUtil();
							EventEntry eventEntry = dataBizLogic.createEventEntry(event,
									clStudyRegstn, evntEntryColn.size() + 1, request);
							Object[] entry = {eventEntry.getId(), null, eventEntry.getEntryNumber()};
							evntEntryColn.add(entry);
						}

					}
					else
					{
						eventNoOfEntry = event.getNoOfEntries();
					}

					StringBuffer studyFormHQL = new StringBuffer();
					studyFormHQL
							.append("select studyFrm.id,studyFrm.containerId,studyFrm.studyFormLabel,studyFrm.canHaveMultipleRecords from ");
					studyFormHQL.append(StudyFormContext.class.getName());
					studyFormHQL.append(" as studyFrm where studyFrm.clinicalStudyEvent.id=");
					studyFormHQL.append(event.getId());
					studyFormHQL.append(" and studyFrm.activityStatus!='");
					studyFormHQL.append(Constants.ACTIVITY_STATUS_DISABLED);
					studyFormHQL.append("' order by studyFrm.id asc");

					Collection<Object[]> studyFormColl = executeQuery(studyFormHQL.toString());
					for (; noOfEntries <= eventNoOfEntry; noOfEntries++)
					{
						nodeId++;
						//String envntEntryId = null;
						// Create EvetnEntry Nodes depending on no of entries for current Event
						String envntEntryId = generateEntryTree(nodeId, noOfEntries, clStudyRegstn, event,
								xmlString, eventNodeId, evntEntryColn);
						{
							if (studyFormColl != null && !studyFormColl.isEmpty())
							{
								/**
								 * Create Node for Form and its records 
								 */
								generateFormTreeForEachEntry(nodeId, studyFormColl, envntEntryId,
										event, clStudyRegstn.getId(), noOfEntries, xmlString, tree,
										sessionDataBean);
							}
						}
						xmlString.append("</node>");
					}
				}
			}
			xmlString.append("</node>");
		}
		xmlString.append("</node>");
		return xmlString.toString();
	}

	/**
	 * This method will generate entry nodes  
	 * @param nodeId
	 * @param noOfEntries
	 * @param clStudyRegtion
	 * @param event
	 * @param xmlString
	 * @param parentId
	 * @param evntEntryColn
	 * @throws DAOException
	 * @return
	 */
	private String generateEntryTree(int nodeId, Integer noOfEntries,
			ClinicalStudyRegistration clStudyRegtion, ClinicalStudyEvent event,
			StringBuffer xmlString, String parentId, List<Object[]> evntEntryColn)
			throws DAOException
	{

		Date encounterDate = null;
		String entryId = "-" + Integer.toString(nodeId);
		//String entryNodeText = "Visit-";
		String eventEntryId = "";

		for (Object[] eventNtry : evntEntryColn)
		{
			if (eventNtry[Constants.EVENT_ENTRY_ID] != null
					&& eventNtry[Constants.ENTRY_NUM_INDEX] != null
					&& eventNtry[Constants.ENTRY_NUM_INDEX].toString().equals(
							noOfEntries.toString()))
			{

				eventEntryId = eventNtry[Constants.EVENT_ENTRY_ID].toString();
				encounterDate = (Date) eventNtry[Constants.ENCOUNTERDATE_INDEX];
				break;
			}
		}
		if (eventEntryId != null && !eventEntryId.equals(""))
		{
			entryId = eventEntryId;
		}

		/*  OBJECT.NAME_eventId_containerId_formContextId_regId_eventEntryId_eventEntryCount_recEntryId_dynamicRecId
		 */
		//entryNodeText = entryNodeText + noOfEntries;
		String entryNodeText = "Visit-" + noOfEntries;
		if (encounterDate != null)
		{
			SimpleDateFormat customFormat = new SimpleDateFormat("MMM-dd-yyyy");
			entryNodeText = entryNodeText + ":" + customFormat.format(encounterDate);
		}
		String entryNodeId = Constants.EVENT_ENTRY_OBJECT + "_" + event.getId() + "_0_0_"
				+ clStudyRegtion.getId() + "_" + entryId + "_" + noOfEntries + "_0_0";
		xmlString.append("<node id= \"");
		xmlString.append(entryNodeId);
		xmlString.append("\" " + "name=\"");
		xmlString.append(entryNodeText);
		xmlString.append("\" " + "toolTip=\"");
		xmlString.append(event.getCollectionPointLabel());
		xmlString.append("\">");
		String eventid = eventEntryId;
		if ("".equals(eventEntryId))
		{
			eventid = null;
		}

		return eventid;

	}

	/**
	 * This method will generate form entries under each entry node
	 * The  for creating FormCOntext and records node:
	 * 1. Query to get all recordEntry id and its corresponding formcontext id for current EntryId
	 * 2. Store above result in TempMap where key is  formcontextid and Value is List of recordEntryid
	 * 3. Iterate over StudyFormCotext of Current Event
	 * 4. Create Node of StudyFormCotext
	 * 5. Get RecordEntryID list from TempMap
	 * 6. Iterate over RecordEntryID list 
	 * 7. Get DE_RecordID of current recordEntryID and cormContext_Containerid
	 * 6. Iterate over result form 6.
	 * 7. Create nodes for record
	 * 8. append nodes created in 7 to Form context Node.  
	 * @param nodeId
	 * @param studyFormColl
	 * @param eventNtryId
	 * @param event
	 * @param regId
	 * @param entryNo
	 * @param xmlString
	 * @param tree
	 * @param sessionDataBean
	 * @throws DAOException
	 * @throws DynamicExtensionsApplicationException 
	 * @throws DynamicExtensionsSystemException 
	 * @throws SQLException 
	 * @throws CacheException 
	 * @throws SMException 
	 */
	private void generateFormTreeForEachEntry(int nodeId, Collection<Object[]> studyFormColl,
			String eventNtryId, ClinicalStudyEvent event, Long regId, int entryNo,
			StringBuffer xmlString, EventTreeObject tree, SessionDataBean sessionDataBean)
			throws DAOException, DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException, CacheException, SQLException, SMException
	{
		String userId = sessionDataBean.getUserId().toString();
		String userName = sessionDataBean.getUserName().toString();
		String eventEntryId = '-' + String.valueOf(nodeId);
		Hashtable<Long, List<Long>> tempHt = new Hashtable<Long, List<Long>>();
		if (eventNtryId != null && !eventNtryId.equals(""))
		{

			StringBuffer recEntryHQL = new StringBuffer();
			recEntryHQL.append("select recEntry.id,recEntry.studyFormContext.id from ");
			recEntryHQL.append(RecordEntry.class.getName());
			recEntryHQL.append(" recEntry where recEntry.eventEntry.id=");
			recEntryHQL.append(eventNtryId);
			recEntryHQL.append(" order by recEntry.id");
			List<Object[]> recEntryList = executeQuery(recEntryHQL.toString());
			if (recEntryList != null && !recEntryList.isEmpty())
			{
				for (Object[] recEntry : recEntryList)
				{
					Long recordEntryId = (Long) recEntry[0];
					Long formContextId = (Long) recEntry[1];
					List recEntryListId = tempHt.get(formContextId);
					if (recEntryListId == null)
					{
						recEntryListId = new ArrayList<Long>();
						recEntryListId.add(recordEntryId);
						tempHt.put(formContextId, recEntryListId);
					}
					else
					{
						recEntryListId.add(recordEntryId);
					}
				}
			}

		}
		Collection recIdList = new HashSet();
		for (Object[] frmCntxtObjects : studyFormColl)
		{
			Map<String, Boolean> privMap = Utility.checkFormPrivileges(
					frmCntxtObjects[Constants.FORMCONTEXT_ID_INDEX].toString(), userId, userName);
			boolean hideForm = privMap.get(Constants.HIDEFORMS);
			if (!hideForm)
			{

				if (eventNtryId != null && !eventNtryId.equals(""))
				{
					eventEntryId = eventNtryId;

				}
				StringBuffer buffer = new StringBuffer();

				buffer.append(Constants.FORM_ENTRY_OBJECT);
				buffer.append('_');
				buffer.append(event.getId());
				buffer.append('_');
				buffer.append(frmCntxtObjects[Constants.CONTAINER_ID_INDEX]);
				buffer.append('_');
				buffer.append(frmCntxtObjects[Constants.FORMCONTEXT_ID_INDEX]);
				buffer.append('_');
				buffer.append(regId);
				buffer.append('_');
				buffer.append(eventEntryId);
				buffer.append('_');
				buffer.append(entryNo);
				buffer.append('_');
				buffer.append("0");
				buffer.append('_');
				buffer.append("0");

				String entryNodeId = buffer.toString();
				String nodeLabel = "Record-";
				if ((Boolean) frmCntxtObjects[Constants.CAN_HOLD_MULTIPLE_RECORD_INDEX])
				{
					xmlString.append("<node id= \"");
					xmlString.append(entryNodeId);
					xmlString.append("\" " + "name=\"");
					xmlString.append(frmCntxtObjects[Constants.STUDY_FORM_LABLE_INDEX]);
					xmlString.append("\" " + "toolTip=\"");
					xmlString.append(frmCntxtObjects[Constants.STUDY_FORM_LABLE_INDEX]);
					xmlString.append("\">");
					
					generateRecordTree(eventNtryId, tempHt, frmCntxtObjects, entryNodeId,
							recIdList, xmlString, nodeLabel);

					xmlString.append("</node>");
				}
				else
				{
					nodeLabel = frmCntxtObjects[Constants.STUDY_FORM_LABLE_INDEX].toString();
					generateRecordTree(eventNtryId, tempHt, frmCntxtObjects, entryNodeId,
							recIdList, xmlString, nodeLabel);
				}
			}
		}
	}

	/**
	 * @param eventNtryId
	 * @param tempHt
	 * @param frmCntxtObjects
	 * @param entryNodeId
	 * @param recIdList
	 * @param xmlString
	 * @param nodeLabel
	 * @throws CacheException
	 * @throws NumberFormatException
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 * @throws SQLException
	 * This method generates the EventTreeObject for records in the form
	 * @throws DAOException 
	 * 
	 */
	private void generateRecordTree(String eventNtryId, Hashtable<Long, List<Long>> tempHt,
			Object[] frmCntxtObjects, String entryNodeId, Collection recIdList,
			StringBuffer xmlString, String nodeLabel) throws CacheException, NumberFormatException,
			DynamicExtensionsSystemException, DynamicExtensionsApplicationException, SQLException,
			DAOException
	{

		AnnotationBizLogic annoBizLogic = new AnnotationBizLogic();
		CatissueCoreCacheManager cache = CatissueCoreCacheManager.getInstance();
		String recEntryEntityId = cache.getObjectFromCache(
				AnnotationConstants.RECORD_ENTRY_ENTITY_ID).toString();

		EventTreeObject eventtree = new EventTreeObject();
		eventtree.populateObject(entryNodeId);
		eventtree.setObjectName(Constants.SINGLE_RECORD_FORM);
		String dynamicRecId = "0";
		List<Long> recEntryIdList = tempHt.get(frmCntxtObjects[Constants.FORMCONTEXT_ID_INDEX]);

		/*
		 * This will check eventNtryId, recEntryIdList, if any one of them is empty and event entry hold single form
		 * then this will create the EventTreeObject and append the node to xmlString
		 */
		if (!(Boolean) frmCntxtObjects[Constants.CAN_HOLD_MULTIPLE_RECORD_INDEX]
				&& ((eventNtryId == null || eventNtryId.equals("")) || recEntryIdList == null))
		{
			setXmlString(eventtree, xmlString, nodeLabel,
					frmCntxtObjects[Constants.STUDY_FORM_LABLE_INDEX].toString());
		}
		else
		{
			if (eventNtryId != null && !eventNtryId.equals(""))
			{
				if (recEntryIdList != null)
				{
					int cnt = 1;
					/*
					 * If the Evententry is having only one form then its object name should be
					 * SingleRecordFormEdit 
					 */
					if ((Boolean) frmCntxtObjects[Constants.CAN_HOLD_MULTIPLE_RECORD_INDEX])
					{
						eventtree.setObjectName(Constants.FORM_CONTEXT_OBJECT);
					}
					else
					{
						eventtree.setObjectName(Constants.SINGLE_RECORD_FORM_EDIT);

					}

					for (Long recEntryId : recEntryIdList)
					{
						String nodeLabelNew = nodeLabel;
						recIdList = annoBizLogic.getDynamicRecordFromStaticId(
								recEntryId.toString(), Long
										.valueOf(frmCntxtObjects[Constants.CONTAINER_ID_INDEX]
												.toString()), recEntryEntityId);
						if (recIdList != null && !recIdList.isEmpty())
						{
							dynamicRecId = ((Long) recIdList.iterator().next()).toString();
						}

						eventtree.setRecEntryId(recEntryId.toString());
						eventtree.setDynamicRecId(dynamicRecId);

						//This check is for nodeLabel
						if ((Boolean) frmCntxtObjects[Constants.CAN_HOLD_MULTIPLE_RECORD_INDEX])
						{
							nodeLabelNew = nodeLabel + cnt;
						}
						setXmlString(eventtree, xmlString, nodeLabelNew,
								frmCntxtObjects[Constants.STUDY_FORM_LABLE_INDEX].toString());
						cnt++;
					}
				}

			}
		}
	}

	//this will append EventTreeObject,node to XmlString 
	/**
	 * @param eventtree
	 * @param xmlString
	 * @param nodeLabel
	 * @param tooltip
	 */
	private void setXmlString(EventTreeObject eventtree, StringBuffer xmlString, String nodeLabel,
			String tooltip)
	{

		xmlString.append("<node id= \"");
		xmlString.append(eventtree.createKeyFromObject());
		xmlString.append("\" " + "name=\"");
		xmlString.append(nodeLabel);
		xmlString.append("\" toolTip=\"");
		xmlString.append(tooltip);
		xmlString.append("\"></node>");

	}

	/**
	 * @param hql
	 * @throws DAOException
	 * @return
	 */
	private List executeQuery(String hql) throws DAOException
	{

		String appName = CommonServiceLocator.getInstance().getAppName();
		IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
		DAO dao = daoFactory.getDAO();
		dao.openSession(null);
		List list;
		try
		{
			list = dao.executeQuery(hql);
		}
		finally
		{
			dao.closeSession();
		}
		return list;
	}

	/**
	 * //Checked for the encountered date
	 * @param clStudyRegtion
	 * @param event
	 * @throws BizLogicException
	 * @return 
	 */
	private boolean isEncounteredDateExist(ClinicalStudyRegistration clStudyRegtion,
			ClinicalStudyEvent event) throws BizLogicException
	{
		DataEntryUtil dataEntryUtil = new DataEntryUtil();
		return dataEntryUtil.checkForEncounterDate(event, clStudyRegtion);
	}

}
