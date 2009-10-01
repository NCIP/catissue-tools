/**
 *
 * <p>Description:	UserHDAO is used to add user information into the database using Hibernate.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author shital Lawhale
 * @version 1.00
 * 
 */

package edu.wustl.clinportal.bizlogic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import edu.wustl.clinportal.domain.ClinicalStudy;
import edu.wustl.clinportal.domain.ClinicalStudyRegistration;
import edu.wustl.clinportal.domain.Participant;
import edu.wustl.clinportal.domain.ParticipantMedicalIdentifier;
import edu.wustl.clinportal.util.ParticipantRegistrationCacheManager;
import edu.wustl.clinportal.util.ParticipantRegistrationInfo;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.audit.AuditManager;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.exception.AuditException;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.exceptionformatter.DefaultExceptionFormatter;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.QueryWhereClause;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.security.exception.UserNotAuthorizedException;

/**
 * UserHDAO is used to add user information into the database using Hibernate.
 * 
 */
public class ClinicalStudyRegistrationBizLogic extends ClinportalDefaultBizLogic
{

	private transient Logger logger = Logger
			.getCommonLogger(ClinicalStudyRegistrationBizLogic.class);

	/**
	 * Saves the user object in the database.
	 * @param obj The user object to be saved.
	 * @param dao
	 * @param sessionDataBean The session in which the object is saved.
	 * @throws BizLogicException 
	 */
	protected void insert(Object obj, DAO dao, SessionDataBean sessionDataBean)
			throws BizLogicException
	{
		ClinicalStudyRegistration clStudyRegtion = (ClinicalStudyRegistration) obj;
		checkStatus(dao, clStudyRegtion.getClinicalStudy(), "Clinical Study");
		// Check for closed Participant
		checkStatus(dao, clStudyRegtion.getParticipant(), "Participant");
		try
		{
			checkUniqueConstraint(dao, clStudyRegtion, null);
			registerParticipantAndClinicalStudy(dao, clStudyRegtion, sessionDataBean);
			dao.insert(clStudyRegtion);
			AuditManager auditManager = getAuditManager(sessionDataBean);
			auditManager.insertAudit(dao, clStudyRegtion);
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
		catch (UserNotAuthorizedException e)
		{
			throw getBizLogicException(e, "error.common.clinicalStudyRegistration",
					"Exception while inserting data");
		}
	}

	/** It registers participant with clinical study
	 * 
	 * @param dao
	 * @param colProtoRegtion
	 * @param sessionDataBean
	 * @throws DAOException
	 * @throws UserNotAuthorizedException
	 * @throws AuditException 
	 */
	private void registerParticipantAndClinicalStudy(DAO dao,
			ClinicalStudyRegistration colProtoRegtion, SessionDataBean sessionDataBean)
			throws DAOException, UserNotAuthorizedException, AuditException
	{
		//Case of registering Participant on its participant ID
		Participant participant = null;

		if (colProtoRegtion.getParticipant() != null)
		{
			Object participantObj = dao.retrieveById(Participant.class.getName(), colProtoRegtion
					.getParticipant().getId());

			if (participantObj != null)
			{
				participant = (Participant) participantObj;
			}
		}
		else
		{
			participant = addDummyParticipant(dao, sessionDataBean);
		}

		colProtoRegtion.setParticipant(participant);

		Object clinicalStudyObj = dao.retrieveById(ClinicalStudy.class.getName(), colProtoRegtion
				.getClinicalStudy().getId());
		if (clinicalStudyObj != null)
		{
			ClinicalStudy clinicalStudy = (ClinicalStudy) clinicalStudyObj;
			colProtoRegtion.setClinicalStudy(clinicalStudy);
		}
	}

	/**
	 * Updates the persistent object in the database.
	 * @param obj The object to be updated.
	 * @param oldObj
	 * @param sessionDataBean The session in which the object is saved.
	 * @throws BizLogicException 
	 */
	protected void update(DAO dao, Object obj, Object oldObj, SessionDataBean sessionDataBean)
			throws BizLogicException
	{
		ClinicalStudyRegistration clStudyRegtion = (ClinicalStudyRegistration) obj;
		ClinicalStudyRegistration oldClStudyRegtion = (ClinicalStudyRegistration) oldObj;
		// Check for different clinical studies
		if (!clStudyRegtion.getClinicalStudy().getId().equals(
				oldClStudyRegtion.getClinicalStudy().getId()))
		{
			checkStatus(dao, clStudyRegtion.getClinicalStudy(), "Clinical Study");
		}

		// -- Check for different Participants and closed participant
		// old and new values are not null
		if (clStudyRegtion.getParticipant() != null && oldClStudyRegtion.getParticipant() != null
				&& clStudyRegtion.getParticipant().getId() != null
				&& oldClStudyRegtion.getParticipant().getId() != null)
		{
			if (!clStudyRegtion.getParticipant().getId().equals(
					oldClStudyRegtion.getParticipant().getId()))
			{
				checkStatus(dao, clStudyRegtion.getParticipant(), "Participant");
			}
		}

		//when old participant is null and new is not null
		if (clStudyRegtion.getParticipant() != null && oldClStudyRegtion.getParticipant() == null)
		{
			if (clStudyRegtion.getParticipant().getId() != null)
			{
				checkStatus(dao, clStudyRegtion.getParticipant(), "Participant");
			}
		}

		try
		{

			/**
			 * Case: While updating the registration if the participant is deselected then 
			 * we need to maintain the link between registration and participant by adding a dummy participant
			 * for query module. 
			 */
			if (clStudyRegtion.getParticipant() == null)
			{
				Participant oldParticipant = oldClStudyRegtion.getParticipant();

				//Check for if the older participant was also a dummy, if true use the same participant, 
				//otherwise create an another dummy participant
				if (oldParticipant != null)
				{
					String firstName = Utility.toString(oldParticipant.getFirstName());
					String lastName = Utility.toString(oldParticipant.getLastName());
					String birthDate = Utility.toString(oldParticipant.getBirthDate());
					String ssn = Utility.toString(oldParticipant.getSocialSecurityNumber());
					if (firstName.trim().length() == 0 && lastName.trim().length() == 0
							&& birthDate.trim().length() == 0 && ssn.trim().length() == 0)
					{
						clStudyRegtion.setParticipant(oldParticipant);
					}
					else
					{
						//create dummy participant.
						Participant participant = addDummyParticipant(dao, sessionDataBean);
						clStudyRegtion.setParticipant(participant);
					}

				} // oldParticipant != null
				else
				{
					//create dummy participant.
					Participant participant = addDummyParticipant(dao, sessionDataBean);
					clStudyRegtion.setParticipant(participant);
				}
			}
			checkUniqueConstraint(dao, clStudyRegtion, oldClStudyRegtion);
			dao.update(clStudyRegtion);
			AuditManager auditManager = getAuditManager(sessionDataBean);
			auditManager.updateAudit(dao, clStudyRegtion, oldClStudyRegtion);
		}
		catch (AuditException e)
		{
			logger.debug(e.getMessage(), e);
			throw getBizLogicException(e, e.getErrorKeyName(), e.getMsgValues());
		}
		catch (DAOException daoException)
		{
			logger.debug(daoException.getMessage(), daoException);
			throw new BizLogicException(daoException);
		}
		catch (UserNotAuthorizedException e)
		{
			logger.debug(e.getMessage(), e);
			throw getBizLogicException(e, "error.security", "Security Exception");
		}

		//Disable all specimen Collection group under this registration. 
		Logger.out.debug("collectionProtocolRegistration.getActivityStatus() "
				+ clStudyRegtion.getActivityStatus());
		if (clStudyRegtion.getActivityStatus().equals(Constants.ACTIVITY_STATUS_DISABLED))
		{
			Logger.out.debug("collectionProtocolRegistration.getActivityStatus() "
					+ clStudyRegtion.getActivityStatus());

		}
	}

	/**
	 * Add a dummy participant when participant is register to a protocol using 
	 * participant protocol id
	 * @param dao
	 * @param sessionDataBean
	 * @return
	 * @throws DAOException
	 * @throws UserNotAuthorizedException
	 * @throws AuditException 
	 */
	private Participant addDummyParticipant(DAO dao, SessionDataBean sessionDataBean)
			throws DAOException, UserNotAuthorizedException, AuditException
	{
		Participant participant = new Participant();

		participant.setLastName("");
		participant.setFirstName("");
		participant.setMiddleName("");
		participant.setSocialSecurityNumber(null);
		participant.setActivityStatus(Constants.ACTIVITY_STATUS_ACTIVE);

		//Create a dummy participant medical identifier.
		Set partMedIdColl = new HashSet();
		ParticipantMedicalIdentifier partMedIdentifier = new ParticipantMedicalIdentifier();
		partMedIdentifier.setMedicalRecordNumber(null);
		partMedIdentifier.setSite(null);
		partMedIdColl.add(partMedIdentifier);

		dao.insert(participant);
		AuditManager auditManager = getAuditManager(sessionDataBean);
		auditManager.insertAudit(dao, participant);

		partMedIdentifier.setParticipant(participant);
		dao.insert(partMedIdentifier);
		auditManager.insertAudit(dao, partMedIdentifier);

		/* inserting dummy participant in participant cache */
		ParticipantRegistrationCacheManager partRegCache = new ParticipantRegistrationCacheManager();
		partRegCache.addParticipant(participant);
		return participant;
	}

	/**
	 * Overriding the parent class's method to validate the enumerated attribute values
	 * @param obj The inserted object.
	 * @param dao the dao object
	 * @param operation
	 * @return
	 * @throws BizLogicException
	 * @see edu.wustl.common.bizlogic.DefaultBizLogic#
	 * validate(java.lang.Object, edu.wustl.common.dao.DAO, java.lang.String)
	 */
	protected boolean validate(Object obj, DAO dao, String operation) throws BizLogicException
	{
		ClinicalStudyRegistration registration = (ClinicalStudyRegistration) obj;

		if (registration == null)
		{
			throw getBizLogicException(null, "error.common.clinicalStudyRegistration",
					ApplicationProperties.getValue("domain.object.null.err.msg",
							"Clinical Study Registration"));
		}
		Validator validator = new Validator();
		String message = "";
		if (registration.getClinicalStudy() == null
				|| registration.getClinicalStudy().getId() == null)
		{
			message = ApplicationProperties.getValue("clinicalStudyReg.protocolTitle");
			throw getBizLogicException(null, "error.common.clinicalStudyRegistration",
					ApplicationProperties.getValue("errors.item.required", message));
		}

		String errorKey = validator.validateDate(Utility.parseDateToString(registration
				.getRegistrationDate(), Constants.DATE_PATTERN_MM_DD_YYYY), true);
		if (errorKey.trim().length() > 0)
		{
			message = ApplicationProperties
					.getValue("clinicalStudyReg.participantRegistrationDate");
			throw getBizLogicException(null, "error.common.clinicalStudyRegistration",
					ApplicationProperties.getValue("errors.item.required", message));
		}

		if (validator.isEmpty(registration.getClinicalStudyParticipantIdentifier()))
		{
			if (registration.getParticipant() == null
					|| registration.getParticipant().getId() == null)
			{
				throw getBizLogicException(null, "error.common.clinicalStudyRegistration",
						ApplicationProperties.getValue("errors.clinicalStudyregistration.atleast"));
			}
		}
		if (operation.equals(Constants.ADD))
		{
			if (!Constants.ACTIVITY_STATUS_ACTIVE.equals(registration.getActivityStatus()))
			{
				throw getBizLogicException(null, "error.common.clinicalStudyRegistration",
						ApplicationProperties.getValue("activityStatus.active.errMsg"));
			}
		}
		else
		{
			if (!Validator.isEnumeratedValue(Constants.ACTIVITY_STATUS_VALUES, registration
					.getActivityStatus()))
			{
				throw getBizLogicException(null, "error.common.clinicalStudyRegistration",
						ApplicationProperties.getValue("activityStatus.errMsg"));
			}
		}

		return true;
	}

	/**Checks whether same participant registers for same clinical study
	 * if yes then it invalidates unique constraints.
	 * @param dao
	 * @param clStudyRegn
	 * @param oldClStudyRegtn
	 * @throws DAOException
	 * @throws BizLogicException 
	 */
	public void checkUniqueConstraint(DAO dao, ClinicalStudyRegistration clStudyRegn,
			ClinicalStudyRegistration oldClStudyRegtn) throws DAOException, BizLogicException
	{
		ClinicalStudy clinicalStudy = clStudyRegn.getClinicalStudy();
		String sourceObjectName = clStudyRegn.getClass().getName();
		String[] selectColumns = null;
		String[] whereColumnName = null;
		String[] whereColumnCondition = new String[]{"=", "=", "="};
		Object[] whereColumnValue = null;
		String[] arguments = null;
		String errMsg = "";
		// check for update operation and old values equals to new values
		int count = 0;

		if (!(clStudyRegn.getClinicalStudyParticipantIdentifier() == null)
				&& !(clStudyRegn.getClinicalStudyParticipantIdentifier().equals("")))
		{

			selectColumns = new String[]{"clinicalStudy.id", "clinicalStudyParticipantIdentifier"};
			whereColumnName = new String[]{"clinicalStudy.id",
					"clinicalStudyParticipantIdentifier", "activityStatus"};
			whereColumnValue = new Object[]{clinicalStudy.getId(),
					clStudyRegn.getClinicalStudyParticipantIdentifier(),
					Constants.ACTIVITY_STATUS_ACTIVE};
			arguments = new String[]{"Clinical Study Registration ",
					"CLINICAL_STUDY_ID,CLINICAL_STUDY_PARTICIPANT_ID"};
			String joinCondition = Constants.AND_JOIN_CONDITION;
			QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
			queryWhereClause.getWhereCondition(whereColumnName, whereColumnCondition,
					whereColumnValue, joinCondition);
			List list = dao.retrieve(sourceObjectName, selectColumns, queryWhereClause);

			if (!list.isEmpty())
			{
				if (oldClStudyRegtn == null
						|| !(clStudyRegn.getClinicalStudyParticipantIdentifier()
								.equals(oldClStudyRegtn.getClinicalStudyParticipantIdentifier())))
				{
					// if list is not empty the Constraint Violation occurs
					Logger.out.debug("Unique Constraint Violated: " + list.get(0));
					errMsg = new DefaultExceptionFormatter().getErrorMessage(
							"Err.ConstraintViolation", arguments);
					Logger.out.debug("Unique Constraint Violated: " + errMsg);
					String csname = clStudyRegn.getClinicalStudy().getShortTitle();
					String cspid = clStudyRegn.getClinicalStudyParticipantIdentifier();
					ErrorKey errorKey = ErrorKey.getErrorKey("participant.uniqueconstviolation");
					throw new BizLogicException(errorKey, null, cspid + ":" + csname);

				}
				else
				{
					Logger.out.debug("Unique Constraint Passed");
				}
			}
			else
			{
				Logger.out.debug("Unique Constraint Passed");
			}

		}

		if (oldClStudyRegtn != null)
		{
			if (clStudyRegn.getParticipant() != null && oldClStudyRegtn.getParticipant() != null)
			{
				if (clStudyRegn.getParticipant().getId().equals(
						oldClStudyRegtn.getParticipant().getId()))
				{
					count++;
				}
				if (clStudyRegn.getClinicalStudy().getId().equals(
						oldClStudyRegtn.getClinicalStudy().getId()))
				{
					count++;
				}
			}
			else if (clStudyRegn.getClinicalStudyParticipantIdentifier() != null
					&& oldClStudyRegtn.getClinicalStudyParticipantIdentifier() != null)
			{
				if (clStudyRegn.getClinicalStudyParticipantIdentifier().equals(
						oldClStudyRegtn.getClinicalStudyParticipantIdentifier()))
				{
					count++;
				}
				if (clStudyRegn.getClinicalStudy().getId().equals(
						oldClStudyRegtn.getClinicalStudy().getId()))
				{
					count++;
				}
			}
			// if count=0 return i.e. old values equals new values 
			if (count == 2)
			{
				return;
			}
		}
		if (clStudyRegn.getParticipant() != null)
		{
			// build query for collectionProtocol_id AND participant_id
			Participant objParticipant = clStudyRegn.getParticipant();
			selectColumns = new String[]{"clinicalStudy.id", "participant.id"};
			whereColumnName = new String[]{"clinicalStudy.id", "participant.id", "activityStatus"};
			whereColumnValue = new Object[]{clinicalStudy.getId(), objParticipant.getId(),
					Constants.ACTIVITY_STATUS_ACTIVE};
			arguments = new String[]{"Clinical Study Registration ",
					"CLINICAL_STUDY_ID,PARTICIPANT_ID"};

			String joinCondition = Constants.AND_JOIN_CONDITION;
			QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
			queryWhereClause.getWhereCondition(whereColumnName, whereColumnCondition,
					whereColumnValue, joinCondition);
			List list = dao.retrieve(sourceObjectName, selectColumns, queryWhereClause);

			if (!list.isEmpty())
			{
				// if list is not empty the Constraint Violation occurs
				Logger.out.debug("Unique Constraint Violated: " + list.get(0));
				errMsg = new DefaultExceptionFormatter().getErrorMessage("Err.ConstraintViolation",
						arguments);
				Logger.out.debug("Unique Constraint Violated: " + errMsg);

				throw getBizLogicException(null, "error.common.clinicalStudyRegistration", errMsg);
			}
			else
			{
				Logger.out.debug("Unique Constraint Passed");
			}
		}
	}

	/**
	 * Disable all the related collection protocol registration for a given array of participant ids. 
	 * @param dao
	 * @param participantIDArr
	 * @throws BizLogicException
	 */
	public void disableRelatedObjectsForParticipant(DAO dao, Long[] participantIDArr)
			throws BizLogicException
	{
		super.disableObjects(dao, ClinicalStudyRegistration.class, "participant",
				"CATISSUE_CLINICAL_STUDY_REG", "PARTICIPANT_ID", participantIDArr);

	}

	/**
	 * 
	 * @param dao
	 * @param clStudyIDArr
	 * @throws DAOException
	 * @throws BizLogicException
	 */
	public void disableRelatedObjectsToClinicalStudy(DAO dao, Long[] clStudyIDArr)
			throws DAOException, BizLogicException
	{
		//disable registration and returns corresponding ids
		List listOfSubElement = super.disableObjects(dao, ClinicalStudyRegistration.class,
				"clinicalStudy", "CATISSUE_CLINICAL_STUDY_REG", "CLINICAL_STUDY_ID", clStudyIDArr);

		if (!listOfSubElement.isEmpty())
		{
			IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
			EventEntryBizlogic evntEntryBlogic = (EventEntryBizlogic) factory
					.getBizLogic(Constants.EVENT_ENTRY_FORM_ID);
			evntEntryBlogic.disableRelatedObjects(dao, "clinicalStudyRegistration", Utility
					.toLongArray(listOfSubElement));
		}
	}

	/**
	* Returns registration object on study and participant 
	* @param studyId
	* @param participantId
	* @return
	* @throws BizLogicException 
	*/
	public List<ClinicalStudyRegistration> getClinicalStudyRegistration(Long studyId,
			Long participantId) throws BizLogicException //throws DAOException
	{
		String[] whereColumnNames = {"participant.id", "clinicalStudy.id"};
		String[] whrColCondns = {"=", "="};
		Object[] whereColumnValues = {participantId, studyId};

		DefaultBizLogic bizLogic = new DefaultBizLogic();

		List<ClinicalStudyRegistration> evntEntryColn = bizLogic.retrieve(
				ClinicalStudyRegistration.class.getName(), whereColumnNames, whrColCondns,
				whereColumnValues, Constants.AND_JOIN_CONDITION);
		return evntEntryColn;
	}

	/**
	 * 
	 * @param studyId
	 * @param participantId
	 * @return
	 * @throws BizLogicException
	 */
	public ClinicalStudyRegistration getCSRegistration(Long studyId, Long participantId)
			throws BizLogicException
	{
		List<ClinicalStudyRegistration> regCollection = (List) getClinicalStudyRegistration(
				studyId, participantId);
		ClinicalStudyRegistration cstudyReg = null;
		if (regCollection != null && !regCollection.isEmpty())
		{
			cstudyReg = regCollection.get(0);
		}
		return cstudyReg;
	}

	/**
	* Returns registration object on study and participant 
	* @param participantId
	* @return
	* @throws BizLogicException 
	*/
	public List<ClinicalStudyRegistration> getClinicalStudyRegistration(Long participantId)
			throws BizLogicException
	{
		String[] whereColumnNames = {"participant.id"};
		String[] whereColCondns = {"="};
		Object[] whereColumnValues = {participantId};

		DefaultBizLogic bizLogic = new DefaultBizLogic();

		List<ClinicalStudyRegistration> csRegCollection = bizLogic.retrieve(
				ClinicalStudyRegistration.class.getName(), whereColumnNames, whereColCondns,
				whereColumnValues, Constants.AND_JOIN_CONDITION);
		return csRegCollection;
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
		ClinicalStudyRegistration clStudyRegn = (ClinicalStudyRegistration) obj;
		ParticipantRegistrationCacheManager partRegCacheMger = new ParticipantRegistrationCacheManager();
		partRegCacheMger.registerParticipant(clStudyRegn.getClinicalStudy().getId(), clStudyRegn
				.getParticipant().getId(), clStudyRegn.getClinicalStudyParticipantIdentifier());
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
		ParticipantRegistrationCacheManager partRegCacheMger = new ParticipantRegistrationCacheManager();
		ClinicalStudyRegistration clStudyRegn = (ClinicalStudyRegistration) currentObj;
		ClinicalStudyRegistration oldClStudyRegn = (ClinicalStudyRegistration) oldObj;

		Long oldCSId = oldClStudyRegn.getClinicalStudy().getId();
		Long newCSId = clStudyRegn.getClinicalStudy().getId();
		Long oldParticipantId = oldClStudyRegn.getParticipant().getId();
		Long newParticipantId = clStudyRegn.getParticipant().getId();
		String oldClStudyPartId = oldClStudyRegn.getClinicalStudyParticipantIdentifier();

		if (oldClStudyPartId == null)
		{
			oldClStudyPartId = "";
		}

		String newClStudyPartId = clStudyRegn.getClinicalStudyParticipantIdentifier();

		if (newClStudyPartId == null)
		{
			newClStudyPartId = "";
		}

		if (oldCSId.longValue() != newCSId.longValue()
				|| oldParticipantId.longValue() != newParticipantId.longValue()
				|| !oldClStudyPartId.equals(newClStudyPartId))
		{
			partRegCacheMger.deRegisterParticipant(oldCSId, oldParticipantId, oldClStudyPartId);
			partRegCacheMger.registerParticipant(newCSId, newParticipantId, newClStudyPartId);
		}

		if (clStudyRegn.getActivityStatus().equals(Constants.ACTIVITY_STATUS_DISABLED))
		{
			partRegCacheMger.deRegisterParticipant(newCSId, newParticipantId, newClStudyPartId);
		}

	}

	/**
	 * @return
	 * @throws DAOException
	 * @throws ClassNotFoundException
	 */
	public List getAllParticipantRegistrationInfo() throws DAOException, ClassNotFoundException
	{
		List partRegInfoList = new Vector();

		// Getting all the CollectionProtocol those do not have activityStatus
		// as 'Disabled'.
		String hql = "select cs.id ,cs.title, cs.shortTitle from " + ClinicalStudy.class.getName()
				+ " as cs where  cs.activityStatus != '" + Constants.ACTIVITY_STATUS_DISABLED + "'"; // and  (cs." + Constants.CP_TYPE + "= '" + Constants.PARENT_CP_TYPE + "' or cs.type = null)";

		String appName = CommonServiceLocator.getInstance().getAppName();
		IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
		DAO dao = daoFactory.getDAO();
		dao.openSession(null);

		List list = dao.executeQuery(hql);
		Logger.out.info("list size -----------:" + list.size());

		// Iterating over each Collection Protocol and finding out all its
		// registered participant
		if (list != null)
		{
			for (int i = 0; i < list.size(); i++)
			{
				// Getting participants for a particular CP.
				Object[] obj = (Object[]) list.get(i);
				Long csId = (Long) obj[0];
				String csTitle = (String) obj[1];
				String csShortTitle = (String) obj[2];

				// Getting all active participant registered with CP
				hql = "select p.id, csr.clinicalStudyParticipantIdentifier from "
						+ ClinicalStudyRegistration.class.getName()
						+ " as csr right outer join csr.participant as p where csr.participant.id = p.id and csr.clinicalStudy.id = "
						+ csId + " and csr.activityStatus != '"
						+ Constants.ACTIVITY_STATUS_DISABLED + "' and p.activityStatus != '"
						+ Constants.ACTIVITY_STATUS_DISABLED + "' order by p.id";

				List participantList = dao.executeQuery(hql);

				List participntInfoLst = new ArrayList();
				// If registered participant found then add them to
				// participantInfoList
				for (int j = 0; j < participantList.size(); j++)
				{
					Object[] participantObj = (Object[]) participantList.get(j);
					Long participantID = (Long) participantObj[0];
					if (participantID != null)
					{
						String participantInfo = participantID.toString() + ":";
						String clStudyPartpantId = (String) participantObj[1];
						if (clStudyPartpantId != null && !clStudyPartpantId.equals(""))
						{
							participantInfo = participantInfo + clStudyPartpantId;
						}
						participntInfoLst.add(participantInfo);

					}
				}

				// Creating Participant RegistrationInfo object and storing in a
				// vector participant RegistrationInfoList.
				ParticipantRegistrationInfo prInfo = new ParticipantRegistrationInfo();
				prInfo.setCsId(csId);
				prInfo.setCsTitle(csTitle);
				prInfo.setCsShortTitle(csShortTitle);
				prInfo.setParticipantInfoCollection(participntInfoLst);
				partRegInfoList.add(prInfo);
			}
		}
		dao.closeSession();
		return partRegInfoList;
	}

}