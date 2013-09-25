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
 * <p>Title: SiteHDAO Class>
 * <p>Description:  SiteHDAO is used to add site type information into the database using Hibernate.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * 
 */

package edu.wustl.clinportal.bizlogic;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.wustl.clinportal.domain.Address;
import edu.wustl.clinportal.domain.Site;
import edu.wustl.clinportal.domain.User;
import edu.wustl.clinportal.util.ApiSearchUtil;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.audit.AuditManager;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.cde.CDEManager;
import edu.wustl.common.exception.AuditException;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.util.HibernateMetaData;
import edu.wustl.security.exception.SMException;
import edu.wustl.security.privilege.PrivilegeManager;

/**
 * SiteHDAO is used to add site type information into the database using Hibernate.
 * @author falguni sachde
 */
public class SiteBizLogic extends ClinportalDefaultBizLogic
{

	private transient Logger logger = Logger.getCommonLogger(SiteBizLogic.class);

	/**
	 * Saves the storageType object in the database.
	 * @param obj The storageType object to be saved.
	 * @param session The session in which the object is saved.
	 * @throws DAOException 
	 */
	protected void insert(Object obj, DAO dao, SessionDataBean sessionDataBean)
			throws BizLogicException
	{
		Site site = (Site) obj;
		checkStatus(dao, site.getCoordinator(), "Coordinator");
		Set protectionObjects = new HashSet();
		try
		{
			AuditManager auditManager = getAuditManager(sessionDataBean);
			setCordinator(dao, site);
			dao.insert(site.getAddress());
			auditManager.insertAudit(dao, site.getAddress());
			dao.insert(site);
			auditManager.insertAudit(dao, site);
		}
		catch (AuditException e)
		{
			logger.debug(e.getMessage(), e);
			throw getBizLogicException(e, e.getErrorKeyName(), e.getMsgValues());
		}
		catch (DAOException e)
		{
			logger.debug(e.getMessage(), e);
			throw new BizLogicException(e);
		}
		protectionObjects.add(site);
		protectionObjects.add(site.getAddress());
		PrivilegeManager privilegeManager;
		try
		{
			privilegeManager = PrivilegeManager.getInstance();
			privilegeManager.insertAuthorizationData(null, protectionObjects, null, site
					.getObjectId());
		}

		catch (SMException e)
		{
			logger.debug(e.getMessage(), e);
			e.printStackTrace();
		}

	}

	/**
	 * Updates the persistent object in the database.
	 * @param obj The object to be updated.
	 * @param session The session in which the object is saved.
	 * @throws DAOException 
	 */
	protected void update(DAO dao, Object obj, Object oldObj, SessionDataBean sessionDataBean)
			throws BizLogicException
	{
		Site site = (Site) obj;
		Site siteOld = (Site) oldObj;
		AuditManager auditManager = getAuditManager(sessionDataBean);
		try
		{
			List userList = dao.retrieve(User.class.getName(), "id", site.getCoordinator().getId());

			if (userList != null && !userList.isEmpty())
			{
				User user = (User) userList.get(0);
				site.setCoordinator(user);
			}

			if (!site.getCoordinator().getId().equals(siteOld.getCoordinator().getId()))
			{
				checkStatus(dao, site.getCoordinator(), "Coordinator");
			}

			setCordinator(dao, site);

			//			Audit of update.
			Site oldSite = (Site) oldObj;
			oldSite = (Site) HibernateMetaData.getProxyObjectImpl(oldSite);
			dao.update(site.getAddress());
			auditManager.updateAudit(dao, site.getAddress(), oldSite.getAddress());
			dao.update(site);
			auditManager.updateAudit(dao, site, oldSite);
		}
		catch (DAOException e)
		{
			logger.debug(e.getMessage(), e);
			throw new BizLogicException(e);
		}
		catch (AuditException e)
		{
			logger.debug(e.getMessage(), e);
			throw getBizLogicException(e, e.getErrorKeyName(), e.getMsgValues());
		}

	}

	// This method sets the coordinator for a particular site.
	/**
	 * @param dao
	 * @param site
	 * @throws DAOException
	 */
	private void setCordinator(DAO dao, Site site) throws DAOException
	{
		List list = dao.retrieve(User.class.getName(), "id", site.getCoordinator().getId());

		if (list.size() != 0)
		{
			User user = (User) list.get(0);
			site.setCoordinator(user);
		}
	}

	/**
	 * Overriding the parent class's method to validate the enumerated attribute values
	 */
	protected boolean validate(Object obj, DAO dao, String operation) throws BizLogicException
	{
		Site site = (Site) obj;

		/**
		 * Start: Change for API Search   --- Jitendra 06/10/2006
		 * In Case of Api Search, previously it was failing since there was default class level initialization 
		 * on domain object. For example in User object, it was initialized as protected String lastName=""; 
		 * So we removed default class level initialization on domain object and are initializing in method
		 * setAllValues() of domain object. But in case of Api Search, default values will not get set 
		 * since setAllValues() method of domainObject will not get called. To avoid null pointer exception,
		 * we are setting the default values same as we were setting in setAllValues() method of domainObject.
		 */
		ApiSearchUtil.setSiteDefault(site);
		//End:- Change for API Search

		// added by Ashish
		String message = "";
		if (site == null)
		{
			message = ApplicationProperties.getValue("app.site");
			throw getBizLogicException(null, "domain.object.null.err.msg", message);
		}
		Validator validator = new Validator();
		if (validator.isEmpty(site.getName()))
		{
			message = ApplicationProperties.getValue("site.name");
			throw getBizLogicException(null, "errors.item.required", message);
		}

		if (validator.isEmpty(site.getType()))
		{
			message = ApplicationProperties.getValue("site.type");
			throw getBizLogicException(null, "errors.item.required", message);
		}

		if (site.getCoordinator() == null || site.getCoordinator().getId() == null
				|| site.getCoordinator().getId() == 0
				|| site.getCoordinator().getId().longValue() == -1L)
		{
			message = ApplicationProperties.getValue("site.coordinator");
			throw getBizLogicException(null, "errors.item.required", message);
		}

		if (!validator.isEmpty(site.getEmailAddress())
				&& !validator.isValidEmailAddress(site.getEmailAddress()))
		{
			message = ApplicationProperties.getValue("site.emailAddress");
			throw getBizLogicException(null, "errors.item.format", message);
		}
		validateAddress(site.getAddress());

		if (site.getAddress() != null && !validator.isValidZipCode(site.getAddress().getZipCode()))
		{
			message = ApplicationProperties.getValue("site.zipCode");
			throw getBizLogicException(null, "errors.item.format", message);
		}

		if (operation.equals(Constants.EDIT) && !validator.isValidOption(site.getActivityStatus()))
		{
			message = ApplicationProperties.getValue("site.activityStatus");
			throw getBizLogicException(null, "errors.item.required", message);
		}

		// END

		List siteList = CDEManager.getCDEManager().getPermissibleValueList(
				Constants.CDE_NAME_SITE_TYPE, null);

		if (!Validator.isEnumeratedValue(siteList, site.getType()))
		{

			throw getBizLogicException(null, "type.errMsg", "");
		}

		if (!Validator.isEnumeratedValue(CDEManager.getCDEManager().getPermissibleValueList(
				Constants.CDE_NAME_STATE_LIST, null), site.getAddress().getState()))
		{
			throw getBizLogicException(null, "state.errMsg", "");
		}

		if (!Validator.isEnumeratedValue(CDEManager.getCDEManager().getPermissibleValueList(
				Constants.CDE_NAME_COUNTRY_LIST, null), site.getAddress().getCountry()))
		{
			throw getBizLogicException(null, "country.errMsg", "");
		}

		if (operation.equals(Constants.ADD))
		{
			if (!Constants.ACTIVITY_STATUS_ACTIVE.equals(site.getActivityStatus()))
			{
				throw getBizLogicException(null, "activityStatus.active.errMsg", "");
			}
		}
		else
		{
			if (!Validator.isEnumeratedValue(Constants.SITE_ACTIVITY_STATUS_VALUES, site
					.getActivityStatus()))
			{
				throw getBizLogicException(null, "activityStatus.errMsg", "");
			}
		}

		return true;
	}

	/**
	 * @param address
	 * @throws DAOException
	 */
	void validateAddress(Address address) throws BizLogicException
	{
		String message;

		if (address != null)
		{
			if (Validator.isEmpty(address.getStreet()))
			{
				message = ApplicationProperties.getValue("site.street");
				throw getBizLogicException(null, "errors.item.required", message);
			}

			if (Validator.isEmpty(address.getCity()))
			{
				message = ApplicationProperties.getValue("site.city");
				throw getBizLogicException(null, "errors.item.required", message);
			}

			if (Validator.isEmpty(address.getState()))
			{
				message = ApplicationProperties.getValue("site.state");
				throw getBizLogicException(null, "errors.item.required", message);
			}

			if (Validator.isEmpty(address.getCountry()))
			{
				message = ApplicationProperties.getValue("site.country");
				throw getBizLogicException(null, "errors.item.required", message);
			}

			if (Validator.isEmpty(address.getZipCode()))
			{
				message = ApplicationProperties.getValue("site.zipCode");
				throw getBizLogicException(null, "errors.item.required", message);
			}
		}

	}
}