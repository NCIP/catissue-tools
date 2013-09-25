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
 * <p>Title: ParticipantHDAO Class>
 * <p>Description:  ParticipantHDAO is used to add Participant's information into the database using Hibernate.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author falguni_sachde
 * @version 1.00
 * Created on Jul 23, 2005
 */

package edu.wustl.clinportal.bizlogic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.ehcache.CacheException;
import edu.wustl.clinportal.client.CaCoreAPIService;
import edu.wustl.clinportal.domain.ClinicalStudy;
import edu.wustl.clinportal.domain.ClinicalStudyRegistration;
import edu.wustl.clinportal.domain.Participant;
import edu.wustl.clinportal.domain.ParticipantMedicalIdentifier;
import edu.wustl.clinportal.domain.Race;
import edu.wustl.clinportal.domain.Site;
import edu.wustl.clinportal.util.ApiSearchUtil;
import edu.wustl.clinportal.util.CatissueCoreCacheManager;
import edu.wustl.clinportal.util.EmailHandler;
import edu.wustl.clinportal.util.ParticipantRegistrationCacheManager;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.clinportal.util.global.EmailUtility;
import edu.wustl.common.audit.AuditManager;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.bizlogic.IBizLogic;
import edu.wustl.common.cde.CDEManager;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.exception.PasswordEncryptionException;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;
import edu.wustl.common.lookup.DefaultLookupParameters;
import edu.wustl.common.lookup.LookupLogic;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.XMLPropertyHandler;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.global.PasswordManager;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.security.exception.SMException;
import edu.wustl.security.global.Permissions;
import edu.wustl.security.manager.SecurityManagerFactory;
import edu.wustl.security.privilege.PrivilegeManager;

/**
 * ParticipantHDAO is used to to add Participant's information into the database using Hibernate.
 * 
 */
public class ParticipantBizLogic extends ClinportalDefaultBizLogic
{

	String isPartServEnabled = XMLPropertyHandler.getValue(Constants.PARTICIPANT_SERVICE_ENABLED);
	String catissueLoginId = XMLPropertyHandler.getValue(Constants.CATISSUE_ADMIN_LOGIN_ID);

	/**
	 * Saves the Participant object in the database.
	 * @param obj The storageType object to be saved.
	 * @param dao
	 * @param sessionDataBean The session in which the object is saved.
	 * @throws BizLogicException 
	 */
	@Override
	protected void insert(Object obj, DAO dao, SessionDataBean sessionDataBean)
			throws BizLogicException
	{
		try
		{
			AuditManager auditManager = getAuditManager(sessionDataBean);
			Participant participant = (Participant) obj;
			String csST = null;
			String csLT = null;
			String csPI =  null;
			String hospRegId = null;
			if (isPartServEnabled.equals(Constants.TRUE))
			{

				String userName = sessionDataBean.getUserName();
				boolean isAuthorized = isAuthorized(obj, sessionDataBean, Constants.INSERT);
				if (isAuthorized)
				{
					userName = catissueLoginId;
				}
				CaCoreAPIService caCoreAPIService = new CaCoreAPIService();
				String password = (String) getPassword(userName);
				Participant participantObject = (Participant) caCoreAPIService.createObject(
						participant, userName, password);
				participant.setId(participantObject.getId());

			}
			else
			{
				dao.insert(participant.getAddress());
				auditManager.insertAudit(dao, participant.getAddress());
				dao.insert(participant);
				auditManager.insertAudit(dao, participant);
				Collection partMedIdColl = participant.getParticipantMedicalIdentifierCollection();
				if (partMedIdColl == null)
				{
					partMedIdColl = new LinkedHashSet();
				}
				if (partMedIdColl.isEmpty())
				{
					//add a dummy participant MedicalIdentifier for Query.
					ParticipantMedicalIdentifier partMedId = new ParticipantMedicalIdentifier();
					partMedId.setMedicalRecordNumber(null);
					partMedId.setSite(null);
					partMedIdColl.add(partMedId);
				}

				//Inserting medical identifiers in the database after setting the participant associated.
				Iterator iterator = partMedIdColl.iterator();
				while (iterator.hasNext())
				{
					ParticipantMedicalIdentifier pmIdentifier = (ParticipantMedicalIdentifier) iterator
							.next();
					pmIdentifier.setParticipant(participant);
					dao.insert(pmIdentifier);
					auditManager.insertAudit(dao, pmIdentifier);
				}

			}

			//Inserting clinicalstudy Registration info in the database after setting the participant associated.

			Collection cStudyRegColln = participant.getClinicalStudyRegistrationCollection();
			ClinicalStudyRegistrationBizLogic clStudyRegBlogic = new ClinicalStudyRegistrationBizLogic();

			if (cStudyRegColln == null)
			{
				cStudyRegColln = new LinkedHashSet();
			}

			Iterator itCStudyRegColl = cStudyRegColln.iterator();
			while (itCStudyRegColl.hasNext())
			{
				ClinicalStudyRegistration cStudyRegId = (ClinicalStudyRegistration) itCStudyRegColl
						.next();
				cStudyRegId.setParticipant(participant);
				cStudyRegId.setActivityStatus(Constants.ACTIVITY_STATUS_ACTIVE);
				clStudyRegBlogic.insert(cStudyRegId, dao, sessionDataBean);
				
				csPI = cStudyRegId.getClinicalStudy().getPrincipalInvestigator().getFirstName()+ "," + cStudyRegId.getClinicalStudy().getPrincipalInvestigator().getLastName();
				csST = cStudyRegId.getClinicalStudy().getShortTitle();
				csLT = cStudyRegId.getClinicalStudy().getTitle();
				hospRegId = cStudyRegId.getClinicalStudyParticipantIdentifier();
			}

			Set protectionObjects = new HashSet();
			protectionObjects.add(participant);
			protectionObjects.add(participant.getAddress());

			PrivilegeManager privilegeManager = PrivilegeManager.getInstance();
			privilegeManager.insertAuthorizationData(null, protectionObjects, null, participant
					.getObjectId());
			if(!"".equals(participant.getEmailAddress()))
			{
				String thanks = "Thanks " + csPI + "\n";
				String body = "Dear " + participant.getLastName() + "," + participant.getFirstName() +"," +  "\n\n"
				+ ApplicationProperties.getValue("participantDetails") + "\n\n"
				+ EmailUtility.getParticipantDetailsEmailBody(participant, hospRegId)+"\n"
				+ csLT +"\n\n"
				+ thanks +"\n\n"
				+ ApplicationProperties.getValue("participantenddetails");
				String sub = ApplicationProperties.getValue("participantRegistration.request.subject") +""+ csST;
				EmailUtility.sendmail(body, participant.getEmailAddress(), sub);
			}
		}
		catch (SMException e)
		{
			throw handleSMException(e);
		}
		catch (DAOException e)
		{
			throw new BizLogicException(e);
		}
		catch (BizLogicException biz)
		{
			throw biz;
		}
		catch (Exception e)
		{
			throw getBizLogicException(e, "Error while inserting object", "");
		}
	}

	/**
	 * This method gets called after insert method. Any logic after insertnig object in database can be included here.
	 * @param obj The inserted object.
	 * @param dao the dao object
	 * @param sessionDataBean session specific data
	 * @throws BizLogicException
	 * */
	protected void postInsert(Object obj, DAO dao, SessionDataBean sessionDataBean)
			throws BizLogicException
	{
		if (!(isPartServEnabled.equals(Constants.TRUE)))
		{
			updateCache(obj);
		}
		Participant participant = (Participant) obj;
		Collection cStudyRegnColln = participant.getClinicalStudyRegistrationCollection();
		Iterator itCStudyRegnColl = cStudyRegnColln.iterator();

		ParticipantRegistrationCacheManager partRegCacheMger = new ParticipantRegistrationCacheManager();
		while (itCStudyRegnColl.hasNext())
		{
			ClinicalStudyRegistration cStudyRegn = (ClinicalStudyRegistration) itCStudyRegnColl
					.next();
			partRegCacheMger.registerParticipant(cStudyRegn.getClinicalStudy().getId(), cStudyRegn
					.getParticipant().getId(), cStudyRegn.getClinicalStudyParticipantIdentifier());
		}
	}

	/**
	 * This method gets called after update method. Any logic after updating into database can be included here.
	 * @param dao the dao object
	 * @param currentObj The object to be updated.
	 * @param oldObj The old object.
	 * @param sessionDataBean session specific data
	 * @throws BizLogicException 
	 * */
	protected void postUpdate(DAO dao, Object currentObj, Object oldObj,
			SessionDataBean sessionDataBean) throws BizLogicException
	{

		if (!(isPartServEnabled.equals(Constants.TRUE)))
		{
			updateCache(currentObj);
		}
		ClinicalStudyRegistrationBizLogic clinStudyBLogic = new ClinicalStudyRegistrationBizLogic();
		ParticipantRegistrationCacheManager ppantRegCacheMger = new ParticipantRegistrationCacheManager();
		Participant participant = (Participant) currentObj;
		Collection cStudyRegColln = participant.getClinicalStudyRegistrationCollection();
		Iterator itCStudyRegnColln = cStudyRegColln.iterator();

		Participant oldparticipant = (Participant) oldObj;
		Collection oldCStudyRegColn = oldparticipant.getClinicalStudyRegistrationCollection();
		Long csId;
		ClinicalStudyRegistration oldClStudyRegtion;
		ClinicalStudyRegistration cStudyRegtion;
		while (itCStudyRegnColln.hasNext())
		{
			cStudyRegtion = (ClinicalStudyRegistration) itCStudyRegnColln.next();
			csId = cStudyRegtion.getClinicalStudy().getId();

			//getting Old cs
			oldClStudyRegtion = getClinicalStudyRegistrationOld(csId, oldCStudyRegColn);
			if (oldClStudyRegtion == null)
			{
				processParticipantRegistration(cStudyRegtion, ppantRegCacheMger, csId);
			}
			else
			{
				clinStudyBLogic.postUpdate(dao, cStudyRegtion, oldClStudyRegtion, sessionDataBean);
			}
		}

	}

	/**
	 * Register/Deregister participant
	 * @param cStudyRegtion
	 * @param ppantRegCacheMger
	 * @param csId
	 */
	private void processParticipantRegistration(ClinicalStudyRegistration cStudyRegtion,
			ParticipantRegistrationCacheManager ppantRegCacheMger, Long csId)
	{
		Long participantId = cStudyRegtion.getParticipant().getId();
		String cStudyPartId = cStudyRegtion.getClinicalStudyParticipantIdentifier();

		if (cStudyPartId == null)
		{
			cStudyPartId = "";
		}

		if (cStudyRegtion.getActivityStatus().equals(Constants.ACTIVITY_STATUS_DISABLED))
		{
			ppantRegCacheMger.deRegisterParticipant(csId, participantId, cStudyPartId);
		}
		else
		{
			ppantRegCacheMger.registerParticipant(csId, participantId, cStudyPartId);
		}
	}

	/**
	 * @param clinicalStudyId
	 * @param cStudyRegtionColl
	 * @return
	 */
	private ClinicalStudyRegistration getClinicalStudyRegistrationOld(long clinicalStudyId,
			Collection cStudyRegtionColl)
	{
		ClinicalStudyRegistration cStudyRegtion = null;
		Iterator itCStudyRegnColln = cStudyRegtionColl.iterator();
		while (itCStudyRegnColln.hasNext())
		{
			cStudyRegtion = (ClinicalStudyRegistration) itCStudyRegnColln.next();
			long csId = cStudyRegtion.getClinicalStudy().getId();
			if (csId == clinicalStudyId)
			{
				//return cStudyRegtion;
				break;
			}
		}
		return cStudyRegtion;
	}

	/**
	 * This method updates the cache for MAP_OF_PARTICIPANTS, should be called in postInsert/postUpdate 
	 * @param obj - participant object
	 */
	public synchronized void updateCache(Object obj)
	{
		Participant participant = (Participant) obj;
		Map mapPpantMedIdCol = new HashMap();
		try
		{
			//getting instance of catissueCoreCacheManager and getting participantMap from cache
			CatissueCoreCacheManager catCoreCacheMager = CatissueCoreCacheManager.getInstance();
			HashMap participantMap = (HashMap) catCoreCacheMager
					.getObjectFromCache(Constants.MAP_OF_PARTICIPANTS);
			if (participant.getActivityStatus()
					.equalsIgnoreCase(Constants.ACTIVITY_STATUS_DISABLED))
			{
				participantMap.remove(participant.getId());
			}
			else
			{
				//Added retrieval of Participant Medical Identifier
				//(Virender Mehta)
				Collection ppantMedId = participant.getParticipantMedicalIdentifierCollection();
				if (ppantMedId != null)
				{
					Iterator partiMedIdItr = ppantMedId.iterator();
					while (partiMedIdItr.hasNext())
					{
						ParticipantMedicalIdentifier partipantId = (ParticipantMedicalIdentifier) partiMedIdItr
								.next();
						if (partipantId.getMedicalRecordNumber() == null)
						{
							participant.setParticipantMedicalIdentifierCollection(null);
						}
						else
						{
							String medicalRecordNo = partipantId.getMedicalRecordNumber();
							String siteId = partipantId.getSite().getId().toString();
							Long participantId = partipantId.getParticipant().getId();
							if (mapPpantMedIdCol.isEmpty()
									&& mapPpantMedIdCol.get(participantId) == null)
							{
								mapPpantMedIdCol.put(participantId, new ArrayList());
							}
							((ArrayList) mapPpantMedIdCol.get(participantId)).add(medicalRecordNo);
							((ArrayList) mapPpantMedIdCol.get(participantId)).add(siteId);
							participant
									.setParticipantMedicalIdentifierCollection((Collection) mapPpantMedIdCol
											.get(participantId));
						}
					}
				}
				participantMap.put(participant.getId(), participant);
			}
		}
		catch (CacheException e)
		{
			Logger.out.debug("Exception occured while getting instance of cachemanager");
			e.printStackTrace();
		}

	}

	/* (non-Javadoc) Updates the persistent object in the database.
	 * @see edu.wustl.common.bizlogic.DefaultBizLogic#update(edu.wustl.common.dao.DAO, java.lang.Object, java.lang.Object, edu.wustl.common.beans.SessionDataBean)
	 */
	protected void update(DAO dao, Object obj, Object oldObj, SessionDataBean sessionDataBean)
			throws BizLogicException
	{
		Participant participant = (Participant) obj;
		Participant oldParticipant = (Participant) oldObj;
		AuditManager auditManager = getAuditManager(sessionDataBean);
		try
		{

			if (isPartServEnabled.equals(Constants.TRUE))
			{
				String userName = sessionDataBean.getUserName();
				boolean isAuthorized = isAuthorized(obj, sessionDataBean, Constants.UPDATE);
				if (isAuthorized)
				{
					userName = catissueLoginId;
				}
				CaCoreAPIService caCoreAPIService = new CaCoreAPIService();
				String password = (String) getPassword(userName);
				Participant participantObject = (Participant) caCoreAPIService.updateObject(
						participant, userName, password);
				participant.setId(participantObject.getId());

			}
			else
			{
				dao.update(participant.getAddress());
				auditManager.updateAudit(dao, participant.getAddress(), oldParticipant.getAddress());
				dao.update(participant);
				auditManager.updateAudit(dao, participant, oldParticipant);
			}
			//Audit of Participant.
			Collection oldPartMedIdColln = (Collection) oldParticipant
					.getParticipantMedicalIdentifierCollection();
			Collection partiMedIdColln = participant.getParticipantMedicalIdentifierCollection();
			Iterator iterator = partiMedIdColln.iterator();

			//Updating the medical identifiers of the participant
			while (iterator.hasNext())
			{
				ParticipantMedicalIdentifier pmIdentifier = (ParticipantMedicalIdentifier) iterator
						.next();

				/**
				 * Start: Change for API Search   --- Jitendra 06/10/2006
				 * In Case of Api Search, previously it was failing since there was default class level initialization 
				 * on domain object. For example in User object, it was initialized as protected String lastName=""; 
				 * So we removed default class level initialization on domain object and are initializing in method
				 * setAllValues() of domain object. But in case of Api Search, default values will not get set 
				 * since setAllValues() method of domainObject will not get called. To avoid null pointer exception,
				 * we are setting the default values same as we were setting in setAllValues() method of domainObject.
				 */
				ApiSearchUtil.setParticipantMedicalIdentifierDefault(pmIdentifier);
				//End:-  Change for API Search 

				pmIdentifier.setParticipant(participant);

				if (pmIdentifier.getId() == null)
				{
					dao.insert(pmIdentifier);
					auditManager.insertAudit(dao, pmIdentifier);
				}
				else
				{
					//Audit of ParticipantMedicalIdentifier.
					ParticipantMedicalIdentifier oldPmIdentifier = (ParticipantMedicalIdentifier) getCorrespondingOldObject(
							oldPartMedIdColln, pmIdentifier.getId());
					dao.update(pmIdentifier);
					auditManager.updateAudit(dao, pmIdentifier, oldPmIdentifier);
				}
			}

			//Updating the CSR of the participant			
			ClinicalStudyRegistrationBizLogic cStudyRegBLogic = new ClinicalStudyRegistrationBizLogic();
			Collection oldCStudyRegColln = oldParticipant.getClinicalStudyRegistrationCollection();
			Collection clStudyRegnColln = participant.getClinicalStudyRegistrationCollection();

			Iterator itCStudyRegnColln = clStudyRegnColln.iterator();
			Iterator itOldCStudyRegnColln = oldCStudyRegColln.iterator();
			while (itCStudyRegnColln.hasNext())
			{
				ClinicalStudyRegistration clStudyRegtion = (ClinicalStudyRegistration) itCStudyRegnColln
						.next();
				ClinicalStudyRegistration oldStudyRegtion = (ClinicalStudyRegistration) itOldCStudyRegnColln
				.next();
				clStudyRegtion.setId(oldStudyRegtion.getId());
				ApiSearchUtil.setClinicalStudyRegistrationDefault(clStudyRegtion);

				clStudyRegtion.setParticipant(participant);

				if (clStudyRegtion.getId() == null) // If Clinical Study Registration is not happened for given participant
				{
					clStudyRegtion.setActivityStatus(Constants.ACTIVITY_STATUS_ACTIVE);
					cStudyRegBLogic.insert(clStudyRegtion, dao, sessionDataBean);
					continue;
				}

				//Audit of CollectionProtocolRegistration.
				ClinicalStudyRegistration oldClStudyRegn = (ClinicalStudyRegistration) getCorrespondingOldObject(
						oldCStudyRegColln, clStudyRegtion.getId());

				cStudyRegBLogic.update(dao, clStudyRegtion, oldClStudyRegn, sessionDataBean);
			}

			//Disable the associate collection protocol registration
			Logger.out.debug("participant.getActivityStatus() " + participant.getActivityStatus());
			if (participant.getActivityStatus().equals(Constants.ACTIVITY_STATUS_DISABLED))
			{
				Logger.out.debug("participant.getActivityStatus() "
						+ participant.getActivityStatus());
				Long participantIDArr[] = {participant.getId()};

				IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
				ClinicalStudyRegistrationBizLogic bizLogic = (ClinicalStudyRegistrationBizLogic) factory
						.getBizLogic(Constants.CLINICAL_STUDY_REGISTRATION_FORM_ID);
				bizLogic.disableRelatedObjectsForParticipant(dao, participantIDArr);

			}
			Logger.out.debug("oldParticipant.getClinicalStudyRegistrationCollection() "
					+ oldParticipant.getClinicalStudyRegistrationCollection().size());
		}
		catch (SMException e)
		{
			throw handleSMException(e);
		}
		catch (DAOException e)
		{
			throw new BizLogicException(e);
		}
		catch (BizLogicException biz)
		{
			throw biz;
		}
		catch (Exception exception)
		{
			throw getBizLogicException(exception, "Error while updating object", "");
		}
	}

	/**
	 * This method check for duplicate clinical study registration for given participant
	 * @param clStudyRegnColln
	 * @return
	 */
	private String isDuplicateClinicalStudy(Collection clStudyRegnColln)
	{
		String shortTitle = null;
		Collection newClStudyRegCol = new LinkedHashSet();
		Iterator itClStudyRegnColn = clStudyRegnColln.iterator();
		while (itClStudyRegnColn.hasNext())
		{
			ClinicalStudyRegistration clStudyRegn = (ClinicalStudyRegistration) itClStudyRegnColn
					.next();
			long clinicalStudyId = clStudyRegn.getClinicalStudy().getId().longValue();
			if (isClinicalStudyExist(newClStudyRegCol, clinicalStudyId))
			{
				shortTitle = clStudyRegn.getClinicalStudy().getShortTitle();
				break;
			}
			else
			{
				newClStudyRegCol.add(clStudyRegn);
			}
		}
		return shortTitle;
	}

	/**
	 * @param clStudyRegnColn
	 * @param clinicalStudyId
	 * @return
	 */
	private boolean isClinicalStudyExist(Collection clStudyRegnColn, long clinicalStudyId)
	{
		boolean isClStudyExist = false;
		Iterator itClStudyRegnCol = clStudyRegnColn.iterator();
		while (itClStudyRegnCol.hasNext())
		{
			ClinicalStudyRegistration clStudyRegn = (ClinicalStudyRegistration) itClStudyRegnCol
					.next();
			long csId = clStudyRegn.getClinicalStudy().getId().longValue();
			if (csId == clinicalStudyId)
			{
				isClStudyExist = true;
				break;
			}
		}

		return isClStudyExist;
	}

	/* (non-Javadoc) Overriding the parent class's method to validate the enumerated attribute values
	 * @see edu.wustl.common.bizlogic.DefaultBizLogic#validate(java.lang.Object, edu.wustl.common.dao.DAO, java.lang.String)
	 */
	protected boolean validate(Object obj, DAO dao, String operation) throws BizLogicException
	{
		Participant participant = (Participant) obj;
		Validator validator = new Validator();
		String message = "";
		if (participant == null)
		{
			throw getBizLogicException(null, "domain.object.null.err.msg", "Participant");
		}

		String erKeyForBirthDate = "";
		String erKeyForDeathDate = "";

		String birthDate = Utility.parseDateToString(participant.getBirthDate(),
				Constants.DATE_PATTERN_MM_DD_YYYY);
		if (!validator.isEmpty(birthDate))
		{
			erKeyForBirthDate = validator.validateDate(birthDate, true);
			if (erKeyForBirthDate.trim().length() > 0)
			{
				message = ApplicationProperties.getValue("participant.birthDate");
				throw getBizLogicException(null, erKeyForBirthDate, message);
			}
		}

		String deathDate = Utility.parseDateToString(participant.getDeathDate(),
				Constants.DATE_PATTERN_MM_DD_YYYY);
		if (!validator.isEmpty(deathDate))
		{
			erKeyForDeathDate = validator.validateDate(deathDate, true);
			if (erKeyForDeathDate.trim().length() > 0)
			{
				message = ApplicationProperties.getValue("participant.deathDate");
				throw getBizLogicException(null, erKeyForDeathDate, message);
			}
		}
		if (participant.getVitalStatus() == null || !participant.getVitalStatus().equals("Dead"))
		{
			if (!validator.isEmpty(deathDate))
			{
				throw getBizLogicException(null, "participant.invalid.enddate", "");
			}
		}
		if ((!validator.isEmpty(birthDate) && !validator.isEmpty(deathDate))
				&& (erKeyForDeathDate.trim().length() == 0 && erKeyForBirthDate.trim().length() == 0))
		{
			boolean errorKey1 = validator.compareDates(Utility.parseDateToString(participant
					.getBirthDate(), Constants.DATE_PATTERN_MM_DD_YYYY), Utility.parseDateToString(
					participant.getDeathDate(), Constants.DATE_PATTERN_MM_DD_YYYY));

			if (!errorKey1)
			{
				throw getBizLogicException(null, "participant.invaliddate", "");
			}
		}

		if (!validator.isEmpty(participant.getSocialSecurityNumber())
				&& !validator.isValidSSN(participant.getSocialSecurityNumber()))
		{
			message = ApplicationProperties.getValue("participant.socialSecurityNumber");
			throw getBizLogicException(null, "errors.invalid", message);
		}

		Collection prMedColln = participant.getParticipantMedicalIdentifierCollection();
		if (prMedColln != null && !prMedColln.isEmpty())
		{
			Iterator itr = prMedColln.iterator();
			while (itr.hasNext())
			{
				ParticipantMedicalIdentifier partiId = (ParticipantMedicalIdentifier) itr.next();
				Site site = partiId.getSite();
				String medicalRecordNo = partiId.getMedicalRecordNumber();
				if (validator.isEmpty(medicalRecordNo) || site == null || site.getId() == null)
				{
					throw getBizLogicException(null, "errors.participant.extiden.missing", "");
				}
			}
		}
		/*
		 * ClinicalStudyRegistration validations
		 */
		Collection clStudyRegnCol = participant.getClinicalStudyRegistrationCollection();
		if (clStudyRegnCol != null && !clStudyRegnCol.isEmpty())
		{
			Iterator itrClStudyRegn = clStudyRegnCol.iterator();
			while (itrClStudyRegn.hasNext())
			{
				ClinicalStudyRegistration clStudyRegnId = (ClinicalStudyRegistration) itrClStudyRegn
						.next();
				long clinicalStudyId = clStudyRegnId.getClinicalStudy().getId().longValue();
				String colProtoRegDate = Utility.parseDateToString(clStudyRegnId
						.getRegistrationDate(), Constants.DATE_PATTERN_MM_DD_YYYY);
				String errorKey = validator.validateDate(colProtoRegDate, true);
				if (clinicalStudyId <= 0 || errorKey.trim().length() > 0)
				{
					throw getBizLogicException(null,
							"errors.participant.clinicalStudyRegistration.missing", "");
				}
				boolean isValidDate = isValidateRegistrationDate(dao, clStudyRegnId,
						clinicalStudyId);
				if (!isValidDate)
				{
					throw getBizLogicException(null,
							"errors.participant.csr.registrationDate.invalid", clStudyRegnId
									.getClinicalStudy().getShortTitle());
				}
				if (clStudyRegnId.getConsentSignatureDate() != null
						&& clStudyRegnId.getConsentSignatureDate().before(
								clStudyRegnId.getRegistrationDate()))
				{
					throw getBizLogicException(null, "errors.participant.csr.consentDate.invalid",
							clStudyRegnId.getClinicalStudy().getShortTitle());
				}
			}
		}

		String duplCStdShortTitle = isDuplicateClinicalStudy(clStudyRegnCol);
		if (duplCStdShortTitle != null)
		{
			throw getBizLogicException(null, "errors.participant.duplicate.clinicalStudy",
					duplCStdShortTitle);
		}
		if (!validator.isEmpty(participant.getVitalStatus()))
		{
			List vitalStatusList = CDEManager.getCDEManager().getPermissibleValueList(
					Constants.CDE_VITAL_STATUS, null);
			if (!Validator.isEnumeratedOrNullValue(vitalStatusList, participant.getVitalStatus()))
			{
				throw getBizLogicException(null, "participant.vitalstatus.errMsg", "");
			}
		}

		if (!validator.isEmpty(participant.getGender()))
		{
			List genderList = CDEManager.getCDEManager().getPermissibleValueList(
					Constants.CDE_NAME_GENDER, null);

			if (!Validator.isEnumeratedOrNullValue(genderList, participant.getGender()))
			{

				throw getBizLogicException(null, "participant.gender.errMsg", "");
			}
		}

		if (!validator.isEmpty(participant.getSexGenotype()))
		{
			List genotypeList = CDEManager.getCDEManager().getPermissibleValueList(
					Constants.CDE_NAME_GENOTYPE, null);
			if (!Validator.isEnumeratedOrNullValue(genotypeList, participant.getSexGenotype()))
			{

				throw getBizLogicException(null, "participant.genotype.errMsg", "");
			}
		}

		Collection raceCollection = participant.getRaceCollection();
		if (raceCollection != null && !raceCollection.isEmpty())
		{
			List raceList = CDEManager.getCDEManager().getPermissibleValueList(
					Constants.CDE_NAME_RACE, null);
			Iterator itr = raceCollection.iterator();
			while (itr.hasNext())
			{
				Race race = (Race) itr.next();
				if (race != null)
				{
					String race_name = (String) race.getRaceName();
					if (!validator.isEmpty(race_name)
							&& !Validator.isEnumeratedOrNullValue(raceList, race_name))
					{

						throw getBizLogicException(null, "participant.race.errMsg", "");
					}
				}
			}
		}

		if (!validator.isEmpty(participant.getEthnicity()))
		{
			List ethnicityList = CDEManager.getCDEManager().getPermissibleValueList(
					Constants.CDE_NAME_ETHNICITY, null);
			if (!Validator.isEnumeratedOrNullValue(ethnicityList, participant.getEthnicity()))
			{

				throw getBizLogicException(null, "participant.ethnicity.errMsg", "");
			}
		}

		if (operation.equals(Constants.ADD))
		{
			if (!Constants.ACTIVITY_STATUS_ACTIVE.equals(participant.getActivityStatus()))
			{

				throw getBizLogicException(null, "activityStatus.active.errMsg", "");
			}
		}
		else
		{
			if (!Validator.isEnumeratedValue(Constants.ACTIVITY_STATUS_VALUES, participant
					.getActivityStatus()))
			{

				throw getBizLogicException(null, "activityStatus.errMsg", "");
			}
		}

		return true;
	}

	/**
	 * 
	 * @param dao
	 * @param clStudyRegId
	 * @param clinicalStudyId
	 * @return
	 * @throws BizLogicException
	 */
	private boolean isValidateRegistrationDate(DAO dao, ClinicalStudyRegistration clStudyRegId,
			Long clinicalStudyId) throws BizLogicException
	{
		boolean isValidDate = true;
		try
		{
			ClinicalStudy clinicalStudy = (ClinicalStudy) dao.retrieveById(ClinicalStudy.class
					.getName(), Long.valueOf(clinicalStudyId));

			if (clinicalStudy != null
					&& clStudyRegId.getRegistrationDate().before(clinicalStudy.getStartDate()))
			{
				isValidDate = false;
			}
		}
		catch (DAOException e)
		{
			throw new BizLogicException(e);
		}
		return isValidDate;
	}

	/**
	 * @param participant - participant object 
	 * @param sessionDataBean
	 * @param lookupAlgorithm
	 * @return - List 
	 * @throws Exception
	 */
	public List getListOfMatchingParticipants(Participant participant,
			SessionDataBean sessionDataBean, String lookupAlgorithm) throws Exception
	{

		List matchParticipantList = null;

		if (isPartServEnabled.equals(Constants.TRUE))
		{
			try
			{
				String userName = sessionDataBean.getUserName();
				boolean isAuthorized = isAuthorized(participant, sessionDataBean, Constants.INSERT);
				if (isAuthorized)
				{
					userName = catissueLoginId;
				}
				CaCoreAPIService caCoreAPIService = new CaCoreAPIService();
				String password = (String) getPassword(userName);
				matchParticipantList = (List) caCoreAPIService.getParticipantMatchingObects(
						participant, userName, password);
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
		}
		else
		{
			// getting the instance of ParticipantLookupLogic class
			LookupLogic partLookupLgic = null;
			if (lookupAlgorithm == null)
			{
				partLookupLgic = (LookupLogic) Utility.getObject(XMLPropertyHandler
						.getValue(Constants.PARTICIPANT_LOOKUP_ALGO));
			}
			else
			{
				partLookupLgic = (LookupLogic) Utility.getObject(XMLPropertyHandler
						.getValue(lookupAlgorithm));
			}
			// Creating the DefaultLookupParameters object to pass as argument to lookup function
			//  Object contains the Participant with which matching participant are to be found and the cutoff value.
			DefaultLookupParameters params = new DefaultLookupParameters();
			params.setObject(participant);
			//List lstOfPartipant = new ArrayList();
			//  getting instance of catissueCoreCacheManager and getting participantMap from cache
			CatissueCoreCacheManager catCoreCacheMger = CatissueCoreCacheManager.getInstance();
			HashMap participantMap = (HashMap) catCoreCacheMger
					.getObjectFromCache(Constants.MAP_OF_PARTICIPANTS);
			//Object participants[] = participantMap.values().toArray();
			//lstOfPartipant.addAll(participantMap.values());
			params.setListOfParticipants(participantMap);
			//calling the lookup function which returns the List of ParticipantResult objects.
			//ParticipantResult object contains the matching participant and the probability.
			matchParticipantList = partLookupLgic.lookup(params);
		}
		return matchParticipantList;

	}

	/**
	 * This function returns the list of all the objects present in the Participant table.
	 * Two queries are executed from this function to get participant data and race data.
	 * Participant objects are formed by retrieving only required data, making it time and space efficient 
	 * @return - List of participants 
	 * @throws BizLogicException 
	 */
	public Map getAllParticipants() throws BizLogicException
	{
		// Initialising instance of IBizLogic
		IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();

		IBizLogic bizLogic = factory.getBizLogic(Constants.DEFAULT_BIZ_LOGIC);
		String sourceObjectName = Participant.class.getName();
		String selectColumnNames[] = {"id", "lastName", "firstName", "middleName", "birthDate",
				"gender", "sexGenotype", "ethnicity", "socialSecurityNumber", "activityStatus",
				"deathDate", "vitalStatus"};
		String whereColumnName[] = {"activityStatus"};
		String whColCondn[] = {"!="};
		Object whereColumnValue[] = {Constants.ACTIVITY_STATUS_DISABLED};

		// getting all the participants from the database 
		List lstOfParticipts = bizLogic.retrieve(sourceObjectName, selectColumnNames,
				whereColumnName, whColCondn, whereColumnValue, null);

		String appName = CommonServiceLocator.getInstance().getAppName();
		IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
		List listOfRaceObjects = new ArrayList();
		List lstOfPartMedId = new ArrayList();
		try
		{
			JDBCDAO dao = daoFactory.getJDBCDAO();
			dao.openSession(null);

			String queryStr = "SELECT * FROM CATISSUE_RACE WHERE PARTICIPANT_ID IN "
					+ "(SELECT PARTICIPANT_ID FROM CATISSUE_PARTICIPANT WHERE ACTIVITY_STATUS!='DISABLED')";
			String partMedIdStr = "(SELECT * FROM catissue_part_medical_id WHERE "
					+ "(PARTICIPANT_ID IN (SELECT PARTICIPANT_ID FROM CATISSUE_PARTICIPANT "
					+ "WHERE ACTIVITY_STATUS!='DISABLED')) AND MEDICAL_RECORD_NUMBER!='NULL')";

			listOfRaceObjects = dao.executeQuery(queryStr);
			lstOfPartMedId = dao.executeQuery(partMedIdStr);

			dao.closeSession();
		}
		catch (DAOException ex)
		{

			throw new BizLogicException(ex);
		}

		Map mapOfRaceColl = new HashMap();
		for (int i = 0; i < listOfRaceObjects.size(); i++)
		{
			List objectArray = (ArrayList) listOfRaceObjects.get(i);
			Long participantId = (Long.valueOf(objectArray.get(0).toString()));
			String race = (String) objectArray.get(1);
			if (mapOfRaceColl.get(participantId) == null)
			{
				mapOfRaceColl.put(participantId, new HashSet());
			}
			((HashSet) mapOfRaceColl.get(participantId)).add(race);
		}
		//Added retrieval of Participant Medical Identifier

		Map mapOfPartMedIdCol = new HashMap();
		for (int i = 0; i < lstOfPartMedId.size(); i++)
		{
			List objectArray = (ArrayList) lstOfPartMedId.get(i);
			Long participantId = (Long.valueOf(objectArray.get(3).toString()));
			String partMedId = (String) objectArray.get(1);
			String partMediIdSite = (String) objectArray.get(2);
			if (mapOfPartMedIdCol.get(participantId) == null)
			{
				mapOfPartMedIdCol.put(participantId, new ArrayList());
			}
			((ArrayList) mapOfPartMedIdCol.get(participantId)).add(partMedId);
			((ArrayList) mapOfPartMedIdCol.get(participantId)).add(partMediIdSite);
		}
		Map mapOfParticipants = new HashMap();
		for (int i = 0; i < lstOfParticipts.size(); i++)
		{
			Object[] obj = (Object[]) lstOfParticipts.get(i);

			Long identifier = (Long) obj[0];
			String lastName = (String) obj[1];
			String firstName = (String) obj[2];
			String middleName = (String) obj[3];
			Date birthDate = (Date) obj[4];
			String gender = (String) obj[5];
			String sexGenotype = (String) obj[6];
			String ethnicity = (String) obj[7];
			String socSecurityNumber = (String) obj[8];
			String activityStatus = (String) obj[9];
			Date deathDate = (Date) obj[10];
			String vitalStatus = (String) obj[11];

			Participant participant = new Participant(identifier, lastName, firstName, middleName,
					birthDate, gender, sexGenotype, (Collection) mapOfRaceColl.get(identifier),
					ethnicity, socSecurityNumber, activityStatus, deathDate, vitalStatus,
					(Collection) mapOfPartMedIdCol.get(identifier));
			mapOfParticipants.put(participant.getId(), participant);
		}
		return mapOfParticipants;

	}

	/**
	 * This function takes identifier as parameter and returns corresponding Participant
	 * @param identifier
	 * @return - Participant object
	 * @throws Exception
	 */
	public Participant getParticipantById(Long identifier) throws Exception
	{
		// Initialising instance of IBizLogic
		IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();

		IBizLogic bizLogic = factory.getBizLogic(Constants.DEFAULT_BIZ_LOGIC);
		String sourceObjectName = Participant.class.getName();
		// getting all the participants from the database 
		List participantList = bizLogic.retrieve(sourceObjectName, Constants.ID, identifier);
		return (Participant) participantList.get(0);

	}

	/**
	 * 
	 * @param columnList
	 * @return
	 * @throws DAOException
	 */
	public List getColumnList(List columnList) throws DAOException
	{
		List displayList = new ArrayList();
		String appName = CommonServiceLocator.getInstance().getAppName();
		IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
		JDBCDAO jdbcDao = daoFactory.getJDBCDAO();
		jdbcDao.openSession(null);
		String sql = "SELECT  columnData.COLUMN_NAME,displayData.DISPLAY_NAME FROM "
				+ "CATISSUE_INTERFACE_COLUMN_DATA columnData,CATISSUE_TABLE_RELATION relationData,"
				+ "CATISSUE_QUERY_TABLE_DATA tableData,CATISSUE_SEARCH_DISPLAY_DATA displayData "
				+ "where relationData.CHILD_TABLE_ID = columnData.TABLE_ID and "
				+ "relationData.PARENT_TABLE_ID = tableData.TABLE_ID and "
				+ "relationData.RELATIONSHIP_ID = displayData.RELATIONSHIP_ID and "
				+ "columnData.IDENTIFIER = displayData.COL_ID and tableData.ALIAS_NAME = 'Participant'";

		Logger.out.debug("DATA ELEMENT SQL : " + sql);
		List list = jdbcDao.executeQuery(sql);
		Iterator iterator1 = columnList.iterator();

		while (iterator1.hasNext())
		{
			String colName1 = (String) iterator1.next();
			Logger.out.debug("colName1------------------------" + colName1);
			Iterator iterator2 = list.iterator();
			while (iterator2.hasNext())
			{
				List rowList = (List) iterator2.next();
				String colName2 = (String) rowList.get(0);
				Logger.out.debug("colName2------------------------" + colName2);
				if (colName1.equals(colName2))
				{
					displayList.add((String) rowList.get(1));
				}
			}
		}
		jdbcDao.closeSession();
		return displayList;
	}

	/**
	 * 
	 * @return
	 */
	public String getPageToShow()
	{
		return "";
	}

	/**
	 * 
	 * @return
	 */
	public List getMatchingObjects()
	{
		return new ArrayList();
	}

	/**
	 * @param loginName
	 * @return
	 * @throws DAOException
	 */
	private String getPassword(String loginName) throws DAOException
	{
		String appName = CommonServiceLocator.getInstance().getAppName();
		IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory("csm");
		JDBCDAO dao = daoFactory.getJDBCDAO();
		String password = "";

		String queryStr = "select PASSWORD from csm_user where LOGIN_NAME = '" + loginName + "'";

		try
		{
			dao.openSession(null);
			List list = dao.executeQuery(queryStr);
			if (list != null && !list.isEmpty())
			{
				List passwordList = (List) list.get(0);
				password = PasswordManager.decrypt((String) passwordList.get(0));
			}
		}
		catch (PasswordEncryptionException e)
		{
			throw new DAOException(null, null, "Error while encrypting password");
		}

		return password;
	}

	/**
	 * @param obj
	 * @param sessionDataBean
	 * @param permission
	 * @return
	 */
	private boolean isAuthorized(Object obj, SessionDataBean sessionDataBean, String permission)
	{
		boolean isAuthorized = true;
		try
		{
			if (null != sessionDataBean)
			{
				String userName = sessionDataBean.getUserName();
				if (userName != null)
				{
					String className = obj.getClass().getName();
					edu.wustl.clinportal.security.SecurityManager securityManager = (edu.wustl.clinportal.security.SecurityManager) SecurityManagerFactory
							.getSecurityManager();
					if ("insert".equals(permission))
					{
						isAuthorized = securityManager.isAuthorized(userName, className,
								Permissions.CREATE);
					}
					else
					{
						isAuthorized = securityManager.isAuthorized(userName, className,
								Permissions.UPDATE);
					}
				}
				else
				{
					isAuthorized = false;
				}
			}
			else
			{
				isAuthorized = false;
			}
		}
		catch (SMException e)
		{
			Logger.out.error(e.getMessage());
		}

		return isAuthorized;
	}

	/* (non-Javadoc)
	 * @see edu.wustl.common.bizlogic.DefaultBizLogic#isReadDeniedTobeChecked()
	 */
	public boolean isReadDeniedTobeChecked()
	{
		return true;
	}

	/* (non-Javadoc)
	 * @see edu.wustl.common.bizlogic.DefaultBizLogic#hasPrivilegeToView(java.lang.String, java.lang.Long, 
	 * edu.wustl.common.beans.SessionDataBean)
	 */
	public boolean hasPrivilegeToView(String objName, Long identifier,
			SessionDataBean sessionDataBean)
	{
		boolean hasPrivilege;

		if (sessionDataBean == null)
		{
			hasPrivilege = false;
		}
		else if (sessionDataBean.isAdmin())
		{
			hasPrivilege = true;
		}
		else
		{
			hasPrivilege = hasAccess(identifier, sessionDataBean);
		}

		return hasPrivilege;
	}

	/**
	 * 
	 * @param participantId
	 * @param sessionDataBean
	 * @return
	 */
	private boolean hasAccess(Long participantId, SessionDataBean sessionDataBean)
	{
		boolean hasAccess = false;
		ClinicalStudyRegistrationBizLogic bizlogic = new ClinicalStudyRegistrationBizLogic();
		try
		{
			String sourceObjectName = ClinicalStudyRegistration.class.getName();
			List<ClinicalStudyRegistration> csRegList = bizlogic.retrieve(sourceObjectName,
					"participant.id", participantId);
			Iterator iter = csRegList.iterator();
			ClinicalStudyBizLogic csBizlogic = new ClinicalStudyBizLogic();
			while (iter.hasNext())
			{
				ClinicalStudyRegistration csReg = (ClinicalStudyRegistration) iter.next();
				ClinicalStudy clinicalStudy = csReg.getClinicalStudy();
				clinicalStudy = (ClinicalStudy) csBizlogic.retrieve(ClinicalStudy.class.getName(),
						clinicalStudy.getId());
				hasAccess = csBizlogic.checkAccesForUser(clinicalStudy, sessionDataBean);

				if (hasAccess)
				{
					break;
				}
			}
		}
		catch (DAOException e)
		{
			Logger.out.error(e.getMessage());
		}
		catch (BizLogicException e)
		{
			Logger.out.error(e.getMessage());
		}
		return hasAccess;
	}

	/**
	 * 
	 * @param userName
	 * @return
	 * @throws SMException
	 */
	public boolean checkPermissionOnActivityStatus(String userName) throws SMException
	{
		boolean result = true;
		if (((edu.wustl.clinportal.security.SecurityManager) SecurityManagerFactory
				.getSecurityManager()).isAuthorized(userName, Participant.class.getName()
				+ "_ActivityStatus", Permissions.READ_DENIED))
		{
			result = false;
		}
		return result;
	}

}