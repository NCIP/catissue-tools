/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */

/**
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright:TODO</p>
 *@author shital Lawhale
 *@version 1.0
 */

package edu.wustl.clinportal.bizlogic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.clinportal.domain.ClinicalStudy;
import edu.wustl.clinportal.domain.ClinicalStudyConsentTier;
import edu.wustl.clinportal.domain.ClinicalStudyEvent;
import edu.wustl.clinportal.domain.ClinicalStudyRegistration;
import edu.wustl.clinportal.domain.RecordEntry;
import edu.wustl.clinportal.domain.StudyFormContext;
import edu.wustl.clinportal.domain.User;
import edu.wustl.clinportal.util.DataEntryUtil;
import edu.wustl.clinportal.util.ParticipantRegistrationCacheManager;
import edu.wustl.clinportal.util.Roles;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.audit.AuditManager;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.AuditException;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.QueryWhereClause;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.util.HibernateMetaData;
import edu.wustl.security.beans.SecurityDataBean;
import edu.wustl.security.exception.SMException;
import edu.wustl.security.exception.UserNotAuthorizedException;
import edu.wustl.security.global.Permissions;
import edu.wustl.security.manager.SecurityManagerFactory;
import edu.wustl.security.privilege.PrivilegeManager;
import edu.wustl.security.privilege.PrivilegeUtility;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.Role;
import gov.nih.nci.security.dao.GroupSearchCriteria;
import gov.nih.nci.security.dao.SearchCriteria;
import gov.nih.nci.security.exceptions.CSException;

public class ClinicalStudyBizLogic extends SpecimenProtocolBizLogic implements Roles
{

	private transient Logger logger = Logger.getCommonLogger(ClinicalStudyBizLogic.class);

	/**
	 * Saves the ClinicalStudy object in the database.
	 * @param obj The ClinicalStudy object to be saved.
	 * @param dao
	 * @param sessionDataBean The session in which the object is saved.
	 * @throws BizLogicException 
	 */
	protected void insert(Object obj, DAO dao, SessionDataBean sessionDataBean)
			throws BizLogicException
	{
		ClinicalStudy clinicalStudy = (ClinicalStudy) obj;

		checkStatus(dao, clinicalStudy.getPrincipalInvestigator(), "Principal Investigator");
		try
		{
			setPrincipalInvestigator(dao, clinicalStudy);
			setCoordinatorCollection(dao, clinicalStudy);

			dao.insert(clinicalStudy);
			AuditManager auditManager = getAuditManager(sessionDataBean);
			auditManager.insertAudit(dao, clinicalStudy);

			insertCSEvents(dao, clinicalStudy, auditManager);
			Collection<ClinicalStudyConsentTier> newConsentColl = clinicalStudy
					.getConsentTierCollection();
			for (ClinicalStudyConsentTier newConsent : newConsentColl)
			{
				auditManager.insertAudit(dao, newConsent);
			}
			HashSet protectionObjects = new HashSet();
			protectionObjects.add(clinicalStudy);

			PrivilegeManager privilegeManager = PrivilegeManager.getInstance();
			privilegeManager.insertAuthorizationData(getAuthorizationData(clinicalStudy),
					protectionObjects, null, clinicalStudy.getObjectId());

		}
		catch (SMException e)
		{
			throw handleSMException(e);
		}
		catch (DAOException e)
		{
			throw new BizLogicException(e);
		}
		catch (AuditException e)
		{
			logger.debug(e.getMessage(), e);
			throw getBizLogicException(e, e.getErrorKeyName(), e.getMsgValues());
		}

	}

	/**
	 * This method returns collection of UserGroupRoleProtectionGroup objects that specifies the 
	 * user group protection group linkage through a role.  
	 * @param obj
	 * @return
	 * @throws SMException
	 * @throws DAOException 
	 */
	private List getAuthorizationData(AbstractDomainObject obj) throws SMException, DAOException
	{
		List<SecurityDataBean> authorizationData = new ArrayList<SecurityDataBean>();
		Set group = new HashSet();

		ClinicalStudy clinicalStudy = (ClinicalStudy) obj;
		Long csmUserId = clinicalStudy.getPrincipalInvestigator().getCsmUserId();
		String userId = String.valueOf(csmUserId);
		gov.nih.nci.security.authorization.domainobjects.User user = SecurityManagerFactory
				.getSecurityManager().getUserById(userId);
		group.add(user);

		// Protection group of PI
		String protnGrpName = new String(Constants.getClinicalStudyPGName(clinicalStudy.getId()));
		SecurityDataBean usrGpRolProtGpBen = new SecurityDataBean();
		usrGpRolProtGpBen.setUser(userId);
		usrGpRolProtGpBen.setRoleName(PI);
		usrGpRolProtGpBen
				.setGroupName(Constants.getClinicalStudyPIGroupName(clinicalStudy.getId()));
		usrGpRolProtGpBen.setProtGrpName(protnGrpName);
		usrGpRolProtGpBen.setGroup(group);
		authorizationData.add(usrGpRolProtGpBen);

		protnGrpName = "CLIN_SCIENTIST_PROTECTION_GROUP";
		usrGpRolProtGpBen = new SecurityDataBean();
		usrGpRolProtGpBen.setUser(userId);
		usrGpRolProtGpBen.setRoleName(DATA_ENTRY_PERSON);
		usrGpRolProtGpBen
				.setGroupName(Constants.getClinicalStudyPIGroupName(clinicalStudy.getId()));
		usrGpRolProtGpBen.setProtGrpName(protnGrpName);
		usrGpRolProtGpBen.setGroup(group);
		authorizationData.add(usrGpRolProtGpBen);
		// Protection group for ClinicalStudy coordinators

		Collection coordinators = clinicalStudy.getCoordinatorCollection();
		group = new HashSet();

		for (Iterator it = coordinators.iterator(); it.hasNext();)
		{
			User aUser = (User) it.next();
			userId = String.valueOf(aUser.getCsmUserId());
			user = SecurityManagerFactory.getSecurityManager().getUserById(userId);
			group.add(user);

		}

		protnGrpName = new String(Constants.getClinicalStudyPGName(clinicalStudy.getId()));
		usrGpRolProtGpBen = new SecurityDataBean();
		usrGpRolProtGpBen.setUser(userId);
		usrGpRolProtGpBen.setRoleName(COORDINATOR);
		usrGpRolProtGpBen.setGroupName(Constants.getClinicalStudyCoordinatorGroupName(clinicalStudy
				.getId()));
		usrGpRolProtGpBen.setProtGrpName(protnGrpName);
		usrGpRolProtGpBen.setGroup(group);
		authorizationData.add(usrGpRolProtGpBen);

		protnGrpName = "CLIN_SCIENTIST_PROTECTION_GROUP";//new String(Constants.getClinicalStudyPGName(clinicalStudy.getId()));
		usrGpRolProtGpBen = new SecurityDataBean();
		usrGpRolProtGpBen.setUser(userId);
		usrGpRolProtGpBen.setRoleName(DATA_ENTRY_PERSON);
		usrGpRolProtGpBen.setGroupName(Constants.getClinicalStudyCoordinatorGroupName(clinicalStudy
				.getId()));
		usrGpRolProtGpBen.setProtGrpName(protnGrpName);
		usrGpRolProtGpBen.setGroup(group);
		authorizationData.add(usrGpRolProtGpBen);

		return authorizationData;
	}

	/**
	 * 
	 * @param dao
	 * @param clinicalStudy
	 * @throws DAOException
	 * @throws AuditException 
	 */
	private void insertCSEvents(DAO dao, ClinicalStudy clinicalStudy, AuditManager auditManager)
			throws DAOException, AuditException
	{
		Iterator evntIterate = clinicalStudy.getClinicalStudyEventCollection().iterator();
		while (evntIterate.hasNext())
		{
			ClinicalStudyEvent clStudyEvent = (ClinicalStudyEvent) evntIterate.next();
			if (clStudyEvent.getNoOfEntries().equals(Integer.valueOf(-1)))
			{
				clStudyEvent.setNoOfEntries(Integer.valueOf(0));
				clStudyEvent.setIsInfiniteEntry(Boolean.TRUE);
			}
			clStudyEvent.setActivityStatus(Constants.ACTIVITY_STATUS_ACTIVE);
			clStudyEvent.setClinicalStudy(clinicalStudy);
			dao.insert(clStudyEvent);
			auditManager.insertAudit(dao, clStudyEvent);
			insertStudyFormContext(dao, clStudyEvent, auditManager);
		}
	}

	/**
	 * 
	 * @param dao
	 * @param clStudyEvent
	 * @throws DAOException
	 * @throws AuditException 
	 */
	private void insertStudyFormContext(DAO dao, ClinicalStudyEvent clStudyEvent,
			AuditManager auditManager) throws DAOException//, UserNotAuthorizedException
			, AuditException
	{
		Iterator studyFormIterate = clStudyEvent.getStudyFormContextCollection().iterator();
		while (studyFormIterate.hasNext())
		{
			StudyFormContext studyFormContext = (StudyFormContext) studyFormIterate.next();
			studyFormContext.setActivityStatus(Constants.ACTIVITY_STATUS_ACTIVE);
			studyFormContext.setClinicalStudyEvent(clStudyEvent);
			if (studyFormContext.getCanHaveMultipleRecords() == null
					|| !studyFormContext.getCanHaveMultipleRecords())
			{
				studyFormContext.setCanHaveMultipleRecords(false);
			}
			else
			{
				studyFormContext.setCanHaveMultipleRecords(true);
			}
			dao.insert(studyFormContext);
			auditManager.insertAudit(dao, studyFormContext);
		}
	}

	/**
	 * This method 
	 * add activity status, associate events with clinical study, disables the events.    
	 * @param dao
	 * @param clinicalStudy
	 * @param clinicalStudyOld
	 * @param auditManager 
	 * @throws BizLogicException 
	 * @throws DAOException 
	 * @throws AuditException 
	 */
	private void updateCSEvents(DAO dao, ClinicalStudy clinicalStudy,
			ClinicalStudy clinicalStudyOld, AuditManager auditManager) throws BizLogicException,
			DAOException, AuditException
	{
		Iterator evntIterate = clinicalStudy.getClinicalStudyEventCollection().iterator();
		Collection oldClEvntColn = (Collection) clinicalStudyOld.getClinicalStudyEventCollection();
		while (evntIterate.hasNext())
		{
			ClinicalStudyEvent clStudyEvent = (ClinicalStudyEvent) evntIterate.next();

			clStudyEvent.setActivityStatus(clinicalStudy.getActivityStatus());

			if (oldClEvntColn.contains(clStudyEvent))
			{
				//gets all FC from old object and compare with new 
				getSpecifiedEventFromColl(dao, oldClEvntColn, clStudyEvent, auditManager);
				oldClEvntColn.remove(clStudyEvent);
				//removed as to mark remained as disabled 
			}
			else
			{
				updateStudyFormContext(dao, null, clStudyEvent, auditManager);
			}
			clStudyEvent.setClinicalStudy(clinicalStudy);
		}
		disableEvents(dao, oldClEvntColn, auditManager);
	}

	/**
	 * Disables events => activity status marked as disabled
	 * @param dao
	 * @param oldClEvntColn
	 * @throws DAOException
	 * @throws BizLogicException 
	 * @throws AuditException 
	 */
	private void disableEvents(DAO dao, Collection oldClEvntColn, AuditManager auditManager)
			throws DAOException, BizLogicException, AuditException
	{
		Iterator clEventIterate = oldClEvntColn.iterator();
		while (clEventIterate.hasNext())
		{
			ClinicalStudyEvent clStudyEvent = (ClinicalStudyEvent) clEventIterate.next();
			ClinicalStudyEvent event = (ClinicalStudyEvent) dao.retrieveById(
					ClinicalStudyEvent.class.getName(), clStudyEvent.getId());
			if (event.getActivityStatus() != null
					&& !event.getActivityStatus().equals(Constants.ACTIVITY_STATUS_DISABLED))
			{
				Long clStudyEventIDArr[] = {event.getId()};
				event.setActivityStatus(Constants.ACTIVITY_STATUS_DISABLED);

				IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
				EventEntryBizlogic evntEntryBlogic = (EventEntryBizlogic) factory
						.getBizLogic(Constants.EVENT_ENTRY_FORM_ID);
				evntEntryBlogic.disableRelatedObjects(dao, "clinicalStudyEvent", clStudyEventIDArr);
				dao.update(event);
				auditManager.updateAudit(dao, event, clStudyEvent);
			}
		}
	}

	/**
	 * 
	 * @param dao
	 * @param oldClEventColn
	 * @param clStudyEvent
	 * @throws BizLogicException 
	 * @throws DAOException 
	 * @throws AuditException 
	 */
	private void getSpecifiedEventFromColl(DAO dao, Collection oldClEventColn,
			ClinicalStudyEvent clStudyEvent, AuditManager auditManager) throws BizLogicException,
			DAOException, AuditException
	{
		Iterator evntIt = oldClEventColn.iterator();
		while (evntIt.hasNext())
		{
			ClinicalStudyEvent eventOld = (ClinicalStudyEvent) evntIt.next();
			if (eventOld.getId() != null && eventOld.getId().equals(clStudyEvent.getId()))
			{
				updateStudyFormContext(dao, eventOld, clStudyEvent, auditManager);
				break;
			}
		}

	}

	/**
	 * 
	 * @param dao
	 * @param clStudyEvntOld
	 * @param clStudyEventNew
	 * @throws BizLogicException 
	 * @throws DAOException
	 * @throws AuditException 
	 * @throws  
	 */
	private void updateStudyFormContext(DAO dao, ClinicalStudyEvent clStudyEvntOld,
			ClinicalStudyEvent clStudyEventNew, AuditManager auditManager)
			throws BizLogicException, DAOException, AuditException
	{
		Iterator studyFormIterate = clStudyEventNew.getStudyFormContextCollection().iterator();
		Collection studyFrmCollOld = new HashSet();
		int value = clStudyEventNew.getNoOfEntries();
		if (clStudyEvntOld != null)
		{
			studyFrmCollOld = (Collection) clStudyEvntOld.getStudyFormContextCollection();

			if (clStudyEvntOld.getIsInfiniteEntry() != null
					&& clStudyEvntOld.getIsInfiniteEntry().booleanValue()
					&& !clStudyEventNew.getNoOfEntries().equals(Integer.valueOf("-1")))
			{
				//old event entry= infinite and edited event is not infinite          
				value = -1;
			}
			else if (clStudyEvntOld.getIsInfiniteEntry() != null
					&& clStudyEvntOld.getIsInfiniteEntry().booleanValue()
					&& clStudyEventNew.getNoOfEntries().equals(Integer.valueOf("-1")))
			{
				// old event entry= infinite and edited event is infinite
				clStudyEventNew.setNoOfEntries(clStudyEvntOld.getNoOfEntries());
				clStudyEventNew.setIsInfiniteEntry(Boolean.TRUE);
				value = 0;
			}
			else
			{
				if (clStudyEventNew.getNoOfEntries().equals(Integer.valueOf("-1")))
				{
					// old event entry= not infinite and edited event is infinite
					clStudyEventNew.setNoOfEntries(clStudyEvntOld.getNoOfEntries());
					clStudyEventNew.setIsInfiniteEntry(Boolean.TRUE);
					value = 0;
				}
				else
				{
					value = clStudyEventNew.getNoOfEntries().compareTo(
							clStudyEvntOld.getNoOfEntries());
				}
			}
		}
		else
		{
			//bug id: 7319
			//If new event has no.of entries = infinite then only set IsInfiniteEntry to true . 

			if (clStudyEventNew.getNoOfEntries().intValue() == -1)
			{
				clStudyEventNew.setNoOfEntries(Integer.valueOf("0"));
				clStudyEventNew.setIsInfiniteEntry(Boolean.TRUE);
			}

		}

		while (studyFormIterate.hasNext())
		{
			StudyFormContext studyFormContext = (StudyFormContext) studyFormIterate.next();
			if (studyFrmCollOld != null && !studyFrmCollOld.isEmpty()
					&& studyFrmCollOld.contains(studyFormContext))
			{
				studyFrmCollOld.remove(studyFormContext);
			}
			if (value < 0 && clStudyEvntOld != null)
			//as if new event is get added then its value=-1 so in that case no exception should be thrown 
			{
				throw new BizLogicException(ErrorKey.getErrorKey(""), null,
						"No of entries can't be minimized");
			}

			studyFormContext.setActivityStatus(clStudyEventNew.getActivityStatus());
			if (studyFormContext.getCanHaveMultipleRecords() == null
					|| !studyFormContext.getCanHaveMultipleRecords())
			{
				studyFormContext.setCanHaveMultipleRecords(false);
			}
			else
			{
				studyFormContext.setCanHaveMultipleRecords(true);
			}

			studyFormContext.setClinicalStudyEvent(clStudyEventNew);
		}
		disableForms(dao, studyFrmCollOld, auditManager);
	}

	/**
	 * Disables study forms => activity status marked as disabled
	 * @param dao
	 * @param studyFrmCollOld
	 * @throws DAOException
	 * @throws AuditException 
	 */
	private void disableForms(DAO dao, Collection studyFrmCollOld, AuditManager auditManager)
			throws DAOException, AuditException
	{
		Iterator studyFormIterate = studyFrmCollOld.iterator();
		while (studyFormIterate.hasNext())
		{
			StudyFormContext studyFormContext = (StudyFormContext) studyFormIterate.next();
			StudyFormContext formContext = (StudyFormContext) dao.retrieveById(
					StudyFormContext.class.getName(), studyFormContext.getId());
			if (formContext.getActivityStatus() != null
					&& !formContext.getActivityStatus().equals(Constants.ACTIVITY_STATUS_DISABLED))
			{
				formContext.setActivityStatus(Constants.ACTIVITY_STATUS_DISABLED);
				Collection recColl = (Collection) formContext.getRecordEntryCollection();
				Iterator recIt = recColl.iterator();
				while (recIt.hasNext())
				{
					RecordEntry rec = (RecordEntry) recIt.next();
					rec.setActivityStatus(formContext.getActivityStatus());
					rec.setStudyFormContext(formContext);
				}
				dao.update(formContext);
				auditManager.updateAudit(dao, formContext, studyFormContext);
			}
		}
	}

	/**
	 * Updates the persistent object in the database.
	 * @param dao
	 * @param obj The object to be updated.
	 * @param oldObj
	 * @param sessionDataBean The session in which the object is saved.
	 * @throws BizLogicException 
	 */
	protected void update(DAO dao, Object obj, Object oldObj, SessionDataBean sessionDataBean)
			throws BizLogicException
	{
		AuditManager auditManager = getAuditManager(sessionDataBean);
		ClinicalStudy clinicalStudy = (ClinicalStudy) obj;
		ClinicalStudy clinicalStudyOld = (ClinicalStudy) HibernateMetaData
				.getProxyObjectImpl(oldObj);
		boolean isValidStartDate = validateStartDateForCS(clinicalStudy.getStartDate(),
				clinicalStudyOld);
		if (!isValidStartDate)
		{
			String message = ApplicationProperties.getValue("specimenprotocol.invalidStartdate");
			throw new BizLogicException(ErrorKey.getErrorKey("error.invalid.date"), null, message);
		}

		checkPIStatus(clinicalStudy, clinicalStudyOld, dao);
		try
		{
			handleCoordinatorsForStudy(clinicalStudy, clinicalStudyOld, dao);

			checkForChangedStatus(clinicalStudy, clinicalStudyOld);
			//need to update as CSEvent doesn't have CS and Study form doesn't have CSEvnt 
			updateCSEvents(dao, clinicalStudy, clinicalStudyOld, auditManager);

			if (clinicalStudy.getActivityStatus().equals(Constants.ACTIVITY_STATUS_DISABLED))
			{
				disableReleatedObjectsToClinicalStudy(dao, clinicalStudy);
			}

			ClinicalStudy clinicalStudyOld1 = getOldClinicalStudy(clinicalStudy);
			dao.update(clinicalStudy);

			auditAssociatedObjects(sessionDataBean, dao, clinicalStudy, clinicalStudyOld1);
			auditManager.updateAudit(dao, clinicalStudy, clinicalStudyOld);

			//Remove PI and Coordinator for Old Object
			updatePIAndCoordinatorGroup(dao, clinicalStudyOld, true);
			Long csmUserId = getCSMUserId(dao, clinicalStudy.getPrincipalInvestigator());
			if (csmUserId != null)
			{
				clinicalStudy.getPrincipalInvestigator().setCsmUserId(csmUserId);
				//Assign PI and Coordinator for new Object
				updatePIAndCoordinatorGroup(dao, clinicalStudy, false);
			}

		}
		catch (SMException smException)
		{
			throw handleSMException(smException);
		}
		catch (AuditException e)
		{
			throw getBizLogicException(e, e.getErrorKeyName(), e.getMsgValues());
		}
		catch (DAOException daoException)
		{
			throw new BizLogicException(daoException);
		}
	}

	/**
	 * 
	 * @param clinicalStudy
	 * @param clinicalStudyOld
	 * @param dao
	 * @throws BizLogicException
	 */
	private void checkPIStatus(ClinicalStudy clinicalStudy, ClinicalStudy clinicalStudyOld, DAO dao)
			throws BizLogicException
	{
		if (!clinicalStudy.getPrincipalInvestigator().getId().equals(
				clinicalStudyOld.getPrincipalInvestigator().getId()))
		{
			checkStatus(dao, clinicalStudy.getPrincipalInvestigator(), "Principal Investigator");
		}
	}

	/**
	 * 
	 * @param clinicalStudy
	 * @param clinicalStudyOld
	 * @param dao
	 * @throws DAOException
	 * @throws BizLogicException 
	 */
	private void handleCoordinatorsForStudy(ClinicalStudy clinicalStudy,
			ClinicalStudy clinicalStudyOld, DAO dao) throws DAOException, BizLogicException
	{
		//		Resolved -- lazy Iterator 
		Collection cordColn = clinicalStudy.getCoordinatorCollection();
		Iterator coOrdtrColItr = cordColn.iterator();
		while (coOrdtrColItr.hasNext())
		{
			User coordinator = (User) coOrdtrColItr.next();

			if (coordinator.getId().equals(clinicalStudy.getPrincipalInvestigator().getId()))
			{
				coOrdtrColItr.remove();
			}
			else
			{
				if (!hasCoordinator(dao, coordinator, clinicalStudyOld))
				{
					checkStatus(dao, coordinator, "Coordinator");
				}
			}
		}
	}

	/**
	 * @param csStartDate
	 * @param clinicalStudyOld
	 * @return
	 */
	private boolean validateStartDateForCS(Date csStartDate, ClinicalStudy clinicalStudyOld)
	{
		boolean returnVal = true; // NOPMD - DD anomaly
		Collection<ClinicalStudyRegistration> csrCollection = clinicalStudyOld
				.getClinicalStudyRegistrationCollection();
		for (ClinicalStudyRegistration objCSregistration : csrCollection)
		{
			if (csStartDate.after(objCSregistration.getRegistrationDate()))
			{
				returnVal = false;
				break;
			}

		}
		return returnVal;
	}

	/**
	 * This methods audits the ClinicalStudyEvent object
	 * @param sessionDataBean 
	 * @param dao
	 * @param clinicalStudy
	 * @param clinicalStudyOld
	 * @throws DAOException
	 * @throws AuditException 
	 */
	private void auditAssociatedObjects(SessionDataBean sessionDataBean, DAO dao,
			ClinicalStudy clinicalStudy, ClinicalStudy clinicalStudyOld) throws DAOException,
			AuditException
	{
		Collection<ClinicalStudyEvent> newEventColl = clinicalStudy
				.getClinicalStudyEventCollection();
		Collection<ClinicalStudyEvent> oldEventColl = null;
		AuditManager auditManager = getAuditManager(sessionDataBean);
		for (ClinicalStudyEvent newEvent : newEventColl)
		{
			boolean auditdone = false;
			oldEventColl = clinicalStudyOld.getClinicalStudyEventCollection();
			for (ClinicalStudyEvent oldEvent : oldEventColl)
			{
				if (newEvent.getId().equals(oldEvent.getId()))
				{
					//newEvent = (ClinicalStudyEvent) HibernateMetaData.getProxyObjectImpl(newEvent);
					//oldEvent = (ClinicalStudyEvent) HibernateMetaData.getProxyObjectImpl(oldEvent);

					newEvent = (ClinicalStudyEvent) newEvent;
					oldEvent = (ClinicalStudyEvent) oldEvent;

					//change after enhancement of audit.
					auditManager.updateAudit((HibernateDAO) dao, newEvent, oldEvent);
					auditFormContext(auditManager, dao, newEvent, oldEvent);
					auditdone = true;
					break;
				}
			}
			if (!auditdone)
			{
				auditManager.insertAudit(dao, newEvent);
				Collection<StudyFormContext> newFormColl = newEvent.getStudyFormContextCollection();
				for (StudyFormContext newForm : newFormColl)
				{
					auditManager.insertAudit(dao, newForm);
				}

			}
		}
		Collection<ClinicalStudyConsentTier> newConsentColl = clinicalStudy
				.getConsentTierCollection();
		Collection<ClinicalStudyConsentTier> oldConsentColl = null;
		for (ClinicalStudyConsentTier newConsent : newConsentColl)
		{
			boolean auditdone = false;
			oldConsentColl = clinicalStudyOld.getConsentTierCollection();
			for (ClinicalStudyConsentTier oldConsent : oldConsentColl)
			{
				if (newConsent.getId().equals(oldConsent.getId()))
				{
					newConsent = (ClinicalStudyConsentTier) newConsent;
					oldConsent = (ClinicalStudyConsentTier) oldConsent;
					auditManager.updateAudit((HibernateDAO) dao, newConsent, oldConsent);
					auditdone = true;
					break;
				}
			}
			if (!auditdone)
			{
				auditManager.insertAudit(dao, newConsent);

			}

		}
	}

	/**
	 * This methods audits the StudyFormContext object
	 * @param dao
	 * @param newEvent
	 * @param oldEvent
	 * @throws DAOException
	 * @throws AuditException 
	 */
	private void auditFormContext(AuditManager auditManager, DAO dao, ClinicalStudyEvent newEvent,
			ClinicalStudyEvent oldEvent) throws DAOException, AuditException
	{
		Collection<StudyFormContext> newFormColl = newEvent.getStudyFormContextCollection();
		Collection<StudyFormContext> oldFormColl = null;

		for (StudyFormContext newForm : newFormColl)
		{
			oldFormColl = oldEvent.getStudyFormContextCollection();
			boolean auditdone = false;
			for (StudyFormContext oldForm : oldFormColl)
			{
				if (newForm.getId().equals(oldForm.getId()))
				{
					auditManager.updateAudit((HibernateDAO) dao, (StudyFormContext) newForm,
							(StudyFormContext) oldForm);
					auditdone = true;
					break;
				}
			}
			if (!auditdone)
			{
				auditManager.insertAudit(dao, newForm);

			}
		}
	}

	/**
	 * @param clinicalStudy
	 * @return
	 * @throws BizLogicException
	 */
	private ClinicalStudy getOldClinicalStudy(ClinicalStudy clinicalStudy) throws BizLogicException
	{
		ClinicalStudy clinicalStudyOld;
		String appName = CommonServiceLocator.getInstance().getAppName();
		IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
		DAO dao = null;
		try
		{

			dao = daoFactory.getDAO();
			dao.openSession(null);
			clinicalStudyOld = (ClinicalStudy) dao.retrieveById(ClinicalStudy.class.getName(),
					clinicalStudy.getId());
		}
		catch (Exception e)
		{
			//throw new DAOException(e.getMessage(), e);
			throw new BizLogicException(ErrorKey.getErrorKey("error.fetch.clinicalStudy"), e,
					"Error while getting old clinical study");
		}
		finally
		{//ASK --ITS giving close connection yourself error
			/*try
			{
				dao.closeSession();
			}
			catch (DAOException e)
			{
				e.printStackTrace();
			}*/
		}
		return clinicalStudyOld;
	}

	/**
	 * To disable registration,eventEntry and recordEntry associated to the study 
	 * @param dao
	 * @param clinicalStudy
	 * @throws DAOException
	 * @throws UserNotAuthorizedException
	 * @throws BizLogicException 
	 */
	private void disableReleatedObjectsToClinicalStudy(DAO dao, ClinicalStudy clinicalStudy)
			throws DAOException, UserNotAuthorizedException, BizLogicException
	{
		Long clStudyIDArr[] = {clinicalStudy.getId()};

		IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();

		ClinicalStudyRegistrationBizLogic bizLogic = (ClinicalStudyRegistrationBizLogic) factory
				.getBizLogic(Constants.CLINICAL_STUDY_REGISTRATION_FORM_ID);
		bizLogic.disableRelatedObjectsToClinicalStudy(dao, clStudyIDArr);

	}

	/**
	* returns the csm user Identifier
	* @param dao
	* @param user
	* @return
	* @throws DAOException
	*/
	private Long getCSMUserId(DAO dao, User user) throws DAOException
	{
		String[] selectColumnNames = {Constants.CSM_USER_ID};
		String[] whereColumnNames = {Constants.SYSTEM_IDENTIFIER};
		String[] whereColumnCondition = {"="};
		Object[] whereColumnValues = {user.getId()};

		String sourceObjectName = User.class.getName();
		String joinCondition = null;
		QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
		queryWhereClause.getWhereCondition(whereColumnNames, whereColumnCondition,
				whereColumnValues, joinCondition);
		List csmUserIdList = dao.retrieve(sourceObjectName, selectColumnNames, queryWhereClause);

		Long csmUserId = null;
		if (!csmUserIdList.isEmpty())
		{
			csmUserId = (Long) csmUserIdList.get(0);

		}
		return csmUserId;
	}

	/**
	 * This method removes or assigns user to the given group
	 * @param dao
	 * @param clinicalStudy
	 * @param operation
	 * @throws SMException
	 * @throws DAOException
	 */
	private void updatePIAndCoordinatorGroup(DAO dao, ClinicalStudy clinicalStudy, boolean operation)
			throws SMException, DAOException
	{
		Long prinInvstgtorId = clinicalStudy.getPrincipalInvestigator().getCsmUserId();
		String userGroupName = Constants.getClinicalStudyPIGroupName(clinicalStudy.getId());
		try
		{
			if (getUserGroup(userGroupName) == null)
			{
				HashSet<ClinicalStudy> protectionObjects = new HashSet<ClinicalStudy>();
				protectionObjects.add(clinicalStudy);
				PrivilegeManager privilegeManager = PrivilegeManager.getInstance();
				privilegeManager.insertAuthorizationData(getAuthorizationData(clinicalStudy),
						protectionObjects, null, clinicalStudy.getObjectId());
			}
			else
			{
				handlePI(operation, userGroupName, prinInvstgtorId);
				userGroupName = Constants.getClinicalStudyCoordinatorGroupName(clinicalStudy
						.getId());
				handleCoOrdinator(dao, userGroupName, operation, clinicalStudy);
			}
		}
		catch (CSException e)
		{
			throw new SMException(ErrorKey.getErrorKey("sm.operation.error"), e,
					"ClinicalStudyBizLogic-updatePIAndCoordinatorGroup");
		}
	}

	/**
	 * 
	 * @param dao
	 * @param userGroupName
	 * @param operation
	 * @param clinicalStudy
	 * @throws SMException
	 * @throws DAOException
	 */
	private void handleCoOrdinator(DAO dao, String userGroupName, boolean operation,
			ClinicalStudy clinicalStudy) throws SMException, DAOException
	{

		Collection<User> coordCollection = clinicalStudy.getCoordinatorCollection();
		Iterator<User> iterator = coordCollection.iterator();
		while (iterator.hasNext())
		{
			User user = iterator.next();
			if (operation)
			{
				SecurityManagerFactory.getSecurityManager().removeUserFromGroup(userGroupName,
						user.getCsmUserId().toString());
			}
			else
			{
				Long csmUserId = getCSMUserId(dao, user);
				if (csmUserId != null)
				{
					SecurityManagerFactory.getSecurityManager().assignUserToGroup(userGroupName,
							csmUserId.toString());
				}
			}
		}
	}

	/**
	 * 
	 * @param operation
	 * @param userGroupName
	 * @param prinInvstgtorId
	 * @throws SMException
	 */
	private void handlePI(boolean operation, String userGroupName, Long prinInvstgtorId)
			throws SMException
	{
		if (operation)
		{
			SecurityManagerFactory.getSecurityManager().removeUserFromGroup(userGroupName,
					prinInvstgtorId.toString());
		}
		else
		{
			SecurityManagerFactory.getSecurityManager().assignUserToGroup(userGroupName,
					prinInvstgtorId.toString());
		}
	}

	/**
	 * @param userGroupname
	 * @return
	 * @throws SMException
	 * @throws CSException
	 */
	private Group getUserGroup(String userGroupname) throws SMException, CSException
	{
		Group group = new Group();
		group.setGroupName(userGroupname);
		SearchCriteria searchCriteria = new GroupSearchCriteria(group);
		PrivilegeUtility utility = new PrivilegeUtility();
		List list = utility.getObjects(searchCriteria);
		if (!list.isEmpty())
		{
			Logger.out.debug("list size********************" + list.size());
			group = (Group) list.get(0);
		}
		else
		{
			group = null;
		}
		return group;
	}

	/**
	 * checks current user with old object's user 
	 * and returns true if present 
	 * @param dao
	 * @param coordinator
	 * @param clinicalStudy
	 * @return
	 * @throws DAOException 
	 */
	private boolean hasCoordinator(DAO dao, User coordinator, ClinicalStudy clinicalStudy)
			throws DAOException
	{
		boolean hasCoordinator = false;

		Collection coordCollection = (Collection) dao.retrieveAttribute(ClinicalStudy.class,
				Constants.SYSTEM_IDENTIFIER, clinicalStudy.getId(),
				"elements(coordinatorCollection)");

		Iterator coordinatorColItr = coordCollection.iterator();
		while (coordinatorColItr.hasNext())
		{
			User coordinatorOld = (User) coordinatorColItr.next();
			if (coordinator.getId().equals(coordinatorOld.getId()))
			{
				hasCoordinator = true;
				break;
			}
		}
		return hasCoordinator;
	}

	/**
	 * set user coordinator collection with clinical study
	 * @param dao
	 * @param clinicalStudy
	 * @throws DAOException
	 * @throws BizLogicException 
	 */
	private void setCoordinatorCollection(DAO dao, ClinicalStudy clinicalStudy)
			throws DAOException, BizLogicException
	{
		Long piID = clinicalStudy.getPrincipalInvestigator().getId();
		Collection<User> coordinatorColl = new HashSet<User>();

		Collection coordCollection = clinicalStudy.getCoordinatorCollection();
		Iterator coOrdnatrColItr = coordCollection.iterator();

		while (coOrdnatrColItr.hasNext())
		{
			User aUser = (User) coOrdnatrColItr.next();
			if (aUser.getId() != null && !aUser.getId().equals(piID))
			{
				Logger.out.debug("Coordinator ID :" + aUser.getId());
				List userObjList = dao.retrieve(User.class.getName(), Constants.SYSTEM_IDENTIFIER,
						aUser.getId());
				Object userObj = userObjList.get(0);
				if (userObj != null)
				{
					User coordinator = (User) userObj;
					checkStatus(dao, coordinator, "coordinator");
					coordinatorColl.add(coordinator);
					coordinator.getClinicalStudyCollection().add(clinicalStudy);
				}
			}
		}
		clinicalStudy.setCoordinatorCollection(coordinatorColl);
	}

	/** This method sets the Principal Investigator.
	 * 
	 */
	/**
	 * @param dao
	 * @param clinicalStudy
	 * @throws DAOException
	 */
	private void setPrincipalInvestigator(DAO dao, ClinicalStudy clinicalStudy) throws DAOException
	{
		Object obj = dao.retrieveById(User.class.getName(), clinicalStudy
				.getPrincipalInvestigator().getId());
		if (obj != null)
		{
			User user = (User) obj;//list.get(0);
			clinicalStudy.setPrincipalInvestigator(user);
		}
	}

	/**
	 * @param obj
	 * @return
	 * @throws BizLogicException 
	 */
	protected boolean validateEvent(Object obj) throws BizLogicException //throws DAOException
	{
		ClinicalStudy clinicalStudy = (ClinicalStudy) obj;
		Validator validator = new Validator();

		Collection clStudyEventColn = clinicalStudy.getClinicalStudyEventCollection();
		if (clStudyEventColn != null && !clStudyEventColn.isEmpty())
		{
			Iterator clStudyEvntItr = clStudyEventColn.iterator();
			while (clStudyEvntItr.hasNext())
			{
				ClinicalStudyEvent clStudyEvent = (ClinicalStudyEvent) clStudyEvntItr.next();

				if (clStudyEvent == null)
				{
					throw getBizLogicException(null, "domain.object.null.err.msg", "");
				}

				Iterator clStudyEventItr = clStudyEventColn.iterator();
				while (clStudyEventItr.hasNext())
				{
					ClinicalStudyEvent clStudyEventObj = (ClinicalStudyEvent) clStudyEventItr
							.next();
					if (clStudyEvent != (clStudyEventObj)
							&& clStudyEvent.getCollectionPointLabel().equals(
									(String) clStudyEventObj.getCollectionPointLabel()))
					{
						throw getBizLogicException(null, "clinicalStudyEvent.not.unique", "");
					}
				}

				if (clStudyEvent.getStudyCalendarEventPoint() == null
						|| !(validator.isDouble(clStudyEvent.getStudyCalendarEventPoint()
								.toString())))
				{
					String message = ApplicationProperties
							.getValue("clinicalStudyEvent.studycalendartitle");

					throw getBizLogicException(null, "errors.item.required", "");
				}

				if (clStudyEvent.getNoOfEntries() != -1
						&& (clStudyEvent.getNoOfEntries() == null || !(validator
								.isNumeric(clStudyEvent.getNoOfEntries().toString()))))
				{

					String message = ApplicationProperties
							.getValue("clinicalStudyEvent.noOfEntries");
					throw getBizLogicException(null, "errors.item.required", message);
				}

			}

		}

		return true;
	}

	/**
	 * @param obj
	 * @return
	 * @throws BizLogicException 
	 */
	protected boolean validateStudyForm(Object obj) throws BizLogicException //throws DAOException
	{
		ClinicalStudy clinicalStudy = (ClinicalStudy) obj;
		Collection clStudyEvntColn = clinicalStudy.getClinicalStudyEventCollection();
		if (clStudyEvntColn != null && !clStudyEvntColn.isEmpty())
		{
			Iterator clStudyEvntItr = clStudyEvntColn.iterator();
			while (clStudyEvntItr.hasNext())
			{
				ClinicalStudyEvent clStudyEvent = (ClinicalStudyEvent) clStudyEvntItr.next();
				Collection StdyFrmCntxtColn = clStudyEvent.getStudyFormContextCollection();
				if (StdyFrmCntxtColn != null && !StdyFrmCntxtColn.isEmpty())
				{
					Iterator StudyFrmCtextItr = StdyFrmCntxtColn.iterator();
					while (StudyFrmCtextItr.hasNext())
					{
						StudyFormContext studyFormContext = (StudyFormContext) StudyFrmCtextItr
								.next();
						Iterator StdyFrmContxtItr = StdyFrmCntxtColn.iterator();
						///////study form context label is compared to another study form context label within a event
						while (StdyFrmContxtItr.hasNext())
						{
							StudyFormContext stdyFrmCntextObj = (StudyFormContext) StdyFrmContxtItr
									.next();
							validateStudyFormContextObject(stdyFrmCntextObj, studyFormContext);
						}
					}
				}
			}
		}

		return true;
	}

	/**
	 * 
	 * @param stdyFrmCntextObj
	 * @param studyFormContext
	 * @throws BizLogicException 
	 */
	private void validateStudyFormContextObject(StudyFormContext stdyFrmCntextObj,
			StudyFormContext studyFormContext) throws BizLogicException //throws DAOException
	{
		if (stdyFrmCntextObj != null
				&& (stdyFrmCntextObj.getStudyFormLabel() == null || (stdyFrmCntextObj
						.getStudyFormLabel().length() == 0)))
		{
			throw getBizLogicException(null, "clinicalStudyEvent.studyFormLabel.not.unique", "");
		}

		if (stdyFrmCntextObj.getContainerId() != null && stdyFrmCntextObj.getContainerId() == -1)
		{
			throw getBizLogicException(null, "clinicalStudyEvent.select.valid.study.form", "");
		}
		if (stdyFrmCntextObj != studyFormContext
				&& (stdyFrmCntextObj.getStudyFormLabel()).equals(studyFormContext
						.getStudyFormLabel()))
		{
			throw getBizLogicException(null, "clinicalStudyEvent.studyFormLabel.not.unique", "");
		}
	}

	/* (non-Javadoc)
	 * @see edu.wustl.common.bizlogic.DefaultBizLogic#validate(java.lang.Object, edu.wustl.dao.DAO, java.lang.String)
	 */
	/**
	 * @param obj
	 * @param dao
	 * @param operation
	 * @return
	 * @throws BizLogicException
	 */
	protected boolean validate(Object obj, DAO dao, String operation) throws BizLogicException
	{
		ClinicalStudy clinicalStudy = (ClinicalStudy) obj;
		clinicalStudy.getClinicalStudyEventCollection();
		Validator validator = new Validator();
		String message = "";

		if (clinicalStudy == null)
		{
			throw getBizLogicException(null, "clinicalStudyEvent.studyFormLabel.not.unique", "");
		}

		validateClinicalStudyData(clinicalStudy);

		if (validator.isEmpty(clinicalStudy.getTitle()))
		{
			message = ApplicationProperties.getValue("clinicalstudy.protocoltitle");
			throw getBizLogicException(null, "errors.item.required", message);
		}

		if (validator.isEmpty(clinicalStudy.getShortTitle()))
		{
			message = ApplicationProperties.getValue("clinicalstudy.shorttitle");

			throw getBizLogicException(null, "errors.item.required", message);
		}

		if (validator.isEmpty(clinicalStudy.getIrbIdentifier()))
		{
			message = ApplicationProperties.getValue("clinicalstudy.irbid");

			throw getBizLogicException(null, "errors.item.required", message);
		}

		String startDate = Utility.parseDateToString(clinicalStudy.getStartDate(),
				Constants.DATE_PATTERN_MM_DD_YYYY);

		if (validator.isEmpty(startDate))
		{
			message = ApplicationProperties.getValue("clinicalstudy.startdate");

			throw getBizLogicException(null, "errors.item.required", message);
		}

		if (!validator.isEmpty(startDate))
		{
			String errorKey = validator.validateDate(startDate, true);
			if (errorKey.trim().length() > 0)
			{
				message = ApplicationProperties.getValue("clinicalstudy.startdate");
				throw getBizLogicException(null, errorKey, message);
			}
		}

		if (clinicalStudy.getPrincipalInvestigator().getId() == -1)
		{

			throw getBizLogicException(null, "errors.item.required", "Principal Investigator");
		}

		Collection protoCrdtrColn = clinicalStudy.getCoordinatorCollection();
		if (protoCrdtrColn != null && !protoCrdtrColn.isEmpty())
		{
			Iterator protoCoordItr = protoCrdtrColn.iterator();
			while (protoCoordItr.hasNext())
			{
				User protoCoordtor = (User) protoCoordItr.next();
				if (protoCoordtor.getId() == clinicalStudy.getPrincipalInvestigator().getId())
				{

					throw getBizLogicException(null, "errors.pi.coordinator.same", "");
				}
			}
		}
		validateEvent(obj);
		validateStudyForm(obj);

		return true;
	}

	/**
	 * Validate clinical study related data.
	 * @param clinicalStudy
	 * @throws BizLogicException 
	 */
	private void validateClinicalStudyData(ClinicalStudy clinicalStudy) throws BizLogicException //throws DAOException
	{
		//List<String> errorList = new ArrayList<String>();

		if (clinicalStudy != null)
		{
			if (clinicalStudy.getTitle() != null && clinicalStudy.getTitle().length() > 255)
			{
				//errorList.add("Title ");
				//errorList.add("255");

				throw getBizLogicException(null, "error.message.value", "Title :255");

			}
			if (clinicalStudy.getShortTitle() != null
					&& clinicalStudy.getShortTitle().length() > 255)
			{
				//errorList.add("Short Title ");
				//errorList.add("255");

				throw getBizLogicException(null, "error.message.value", "Short Title :255");
			}
			if (clinicalStudy.getIrbIdentifier() != null
					&& clinicalStudy.getIrbIdentifier().length() > 255)
			{
				//errorList.add("IRB ID ");
				//errorList.add("255");

				throw getBizLogicException(null, "error.message.value", "IRB ID :255");
			}
			if (clinicalStudy.getDescriptionURL() != null
					&& clinicalStudy.getDescriptionURL().length() > 255)
			{
				//errorList.add("Description URL ");
				//errorList.add("255");

				throw getBizLogicException(null, "error.message.value", "Description URL :255");
			}
		}
	}

	/**
	 * @param staticEntityId
	 * @return
	 * @throws NumberFormatException
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	public List getStudyFormsList(Long staticEntityId) throws NumberFormatException,
			DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{

		NameValueBean selectOptionBean = new NameValueBean(Constants.SELECT_OPTION,
				Constants.SELECT_OPTION_VALUE);

		AnnotationBizLogic annotationBizLogic = new AnnotationBizLogic();
		Collection dynAnnotationList = annotationBizLogic.getListOfDynamicEntities(staticEntityId);
		((List) dynAnnotationList).add(0, selectOptionBean);

		return (List) dynAnnotationList;

	}

	/**
	 * @param sessionDataBean
	 * @return
	 * @throws BizLogicException
	 */
	public List getCsList(SessionDataBean sessionDataBean) throws BizLogicException
	{

		List<NameValueBean> csList = new ArrayList<NameValueBean>();

		//Getting the instance of participantRegistrationCacheManager
		ParticipantRegistrationCacheManager partRegCacheMger = new ParticipantRegistrationCacheManager();

		//Getting the CP List 
		List<NameValueBean> csColl = partRegCacheMger.getCSDetailCollection();
		Collections.sort(csColl);

		csList.add(new NameValueBean(Constants.SELECT_OPTION, Constants.SELECT_OPTION_VALUE));
		String userName = sessionDataBean.getUserName();
		try
		{
			Role role = SecurityManagerFactory.getSecurityManager().getUserRole(
					Long.valueOf(sessionDataBean.getCsmUserId()));

			edu.wustl.clinportal.security.SecurityManager securityManager = (edu.wustl.clinportal.security.SecurityManager) SecurityManagerFactory
					.getSecurityManager();

			for (NameValueBean bean : csColl)
			{
				NameValueBean csBean = new NameValueBean(); // NOPMD - Instantiated NameValueBean in loop				
				if (role.getName().equals(edu.wustl.security.global.Constants.ADMINISTRATOR)
						|| securityManager.isAuthorized(userName, ClinicalStudy.class.getName()
								+ "_" + Long.valueOf(bean.getValue()), Permissions.PHI))
				{
					csBean.setName(bean.getName());
					csBean.setValue(bean.getValue());
					csList.add(csBean);
				}
			}
		}
		catch (SMException e)
		{
			throw handleSMException(e);
		}
		return csList;
	}

	/**
	 * @param obj The inserted object.
	 * @param dao the dao object
	 * @param sessionDataBean session specific data
	 * @throws BizLogicException
	 * @see edu.wustl.common.bizlogic.DefaultBizLogic#
	 * postInsert(java.lang.Object, edu.wustl.common.dao.DAO, edu.wustl.common.beans.SessionDataBean)
	 */
	public void postInsert(Object obj, DAO dao, SessionDataBean sessionDataBean)
			throws BizLogicException
	{
		ClinicalStudy clinicalStudy = (ClinicalStudy) obj;
		ParticipantRegistrationCacheManager partRegnCacheMger = new ParticipantRegistrationCacheManager();
		partRegnCacheMger.addNewCS(clinicalStudy.getId(), clinicalStudy.getTitle(), clinicalStudy
				.getShortTitle());

	}

	/**
	 * @param dao the dao object
	 * @param currentObj The object to be updated.
	 * @param oldObj The old object.
	 * @param sessionDataBean session specific data
	 * @throws BizLogicException
	 * @see edu.wustl.common.bizlogic.DefaultBizLogic#
	 * postUpdate(edu.wustl.common.dao.DAO, java.lang.Object, java.lang.Object, edu.wustl.common.beans.SessionDataBean)
	 */
	public void postUpdate(DAO dao, Object currentObj, Object oldObj,
			SessionDataBean sessionDataBean) throws BizLogicException
	{
		ClinicalStudy clinicalStudy = (ClinicalStudy) currentObj;
		ClinicalStudy clinicalStudyOld = (ClinicalStudy) oldObj;
		ParticipantRegistrationCacheManager partRegnCacheMger = new ParticipantRegistrationCacheManager();
		if (!clinicalStudy.getTitle().equals(clinicalStudyOld.getTitle()))
		{
			partRegnCacheMger.updateCSTitle(clinicalStudy.getId(), clinicalStudy.getTitle());
		}

		if (!clinicalStudy.getShortTitle().equals(clinicalStudyOld.getShortTitle()))
		{
			partRegnCacheMger.updateCSShortTitle(clinicalStudy.getId(), clinicalStudy
					.getShortTitle());
		}

		if (clinicalStudy.getActivityStatus().equals(Constants.ACTIVITY_STATUS_DISABLED))
		{
			partRegnCacheMger.removeCS(clinicalStudy.getId());
		}

	}

	/**
	 * This method returns consent collection for given CS id.	
	 * @param csId
	 * @return
	 * @throws BizLogicException
	 */
	public Collection getConsentCollection(Long csId) throws BizLogicException
	{
		Collection consentCollection = null;
		if (csId != null)
		{
			String hql = "select cstudy.consentTierCollection from "
					+ "edu.wustl.clinportal.domain.ClinicalStudy cstudy where cstudy.id="
					+ csId.toString();
			DataEntryUtil util = new DataEntryUtil();
			try
			{
				consentCollection = (Collection) util.executeQuery(hql);

			}
			catch (DAOException e)
			{
				throw new BizLogicException(e);
			}
		}
		return consentCollection;

	}

	/**
	 * @param clinicalStudy
	 * @param sessionDataBean
	 * @return
	 * @throws DAOException
	 */
	public boolean checkAccesForUser(ClinicalStudy clinicalStudy, SessionDataBean sessionDataBean)
			throws DAOException
	{
		boolean hasAccess = false;
		Long userId = sessionDataBean.getUserId();
		User prinInvestigator = clinicalStudy.getPrincipalInvestigator();

		if (userId.equals(prinInvestigator.getId()))
		{
			hasAccess = true;
		}
		else
		{
			IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory();
			DAO dao = daoFactory.getDAO();

			try
			{
				dao.openSession(sessionDataBean);

				User coordinator = new User();
				coordinator.setId(userId);
				hasAccess = hasCoordinator(dao, coordinator, clinicalStudy);
			}
			finally
			{
				dao.closeSession();
			}
		}
		return hasAccess;
	}

	/**
	 * @return
	 * @throws DAOException
	 */
	public List getCatissueCP() throws DAOException
	{
		List cpCollection = null;
		List nameValuePairs = new ArrayList();
		JDBCDAO jdbcDao = null;
		String appName = CommonServiceLocator.getInstance().getAppName();
		IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
		String[] whereColumnNames = {"ACTIVITY_STATUS"};
		String[] whereColumnCondition = {"="};
		Object[] whereColumnValues = {"Active"};

		String sourceObjectName = "SYN_CAT_SPECIMEN_PROTOCOL";
		String joinCondition = null;
		QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
		queryWhereClause.getWhereCondition(whereColumnNames, whereColumnCondition,
				whereColumnValues, joinCondition);

		try
		{
			jdbcDao = daoFactory.getJDBCDAO();
			jdbcDao.openSession(null);

			String[] colNames = new String[]{"IDENTIFIER", "SHORT_TITLE"};
			cpCollection = (List) jdbcDao.retrieve(sourceObjectName, colNames, queryWhereClause);

			nameValuePairs.add(new NameValueBean(Constants.SELECT_OPTION, String
					.valueOf(Constants.SELECT_OPTION_VALUE)));

			// If the list of users retrieved is not empty. 
			if (cpCollection.isEmpty() == false)
			{
				// Creating name value beans.
				for (int i = 0; i < cpCollection.size(); i++)
				{
					//Changes made to optimize the query to get only required fields data
					ArrayList cpDetails = (ArrayList) cpCollection.get(i);
					NameValueBean nameValueBean = new NameValueBean();
					nameValueBean.setName(cpDetails.get(1));
					nameValueBean.setValue(cpDetails.get(0));
					nameValuePairs.add(nameValueBean);
				}
			}
			Collections.sort(nameValuePairs);

		}
		finally
		{
			jdbcDao.closeSession();
		}
		return nameValuePairs;

	}

	/**
	 * @param cpId
	 * @return
	 * @throws DAOException
	 */
	public List getCatissueCPEvent(Long cpId) throws DAOException
	{
		JDBCDAO jdbcDao = null;
		String appName = CommonServiceLocator.getInstance().getAppName();
		IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
		List nameValuePairs = new ArrayList();
		nameValuePairs.add(new NameValueBean(Constants.SELECT_OPTION, String
				.valueOf(Constants.SELECT_OPTION_VALUE)));
		try
		{
			if (cpId != -1)
			{
				jdbcDao = daoFactory.getJDBCDAO();
				jdbcDao.openSession(null);
				String selQuery = "SELECT IDENTIFIER, COLLECTION_POINT_LABEL from  SYN_CAT_COLL_PROT_EVENT where COLLECTION_PROTOCOL_ID ='"
						+ cpId + "'";
				List cpeventColn = (List) jdbcDao.executeQuery(selQuery);

				for (int i = 0; i < cpeventColn.size(); i++)
				{
					ArrayList cpeDetails = (ArrayList) cpeventColn.get(i);
					NameValueBean nameVal = new NameValueBean();
					nameVal.setName(cpeDetails.get(1));
					nameVal.setValue(cpeDetails.get(0));
					nameValuePairs.add(nameVal);
				}
				Collections.sort(nameValuePairs);
			}

		}
		finally
		{
			if (jdbcDao != null)
			{
				jdbcDao.closeSession();
			}
		}
		return nameValuePairs;

	}
}