/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */

/*
 * Created on Jul 30, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package edu.wustl.clinportal.actionForm;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import edu.wustl.clinportal.domain.SpecimenProtocol;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.logger.Logger;

/**
 * @author deepali
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class SpecimenProtocolForm extends AbstractActionForm
{

	/**
	 * Principal Investigator id
	 */
	protected long principalInvestigatorId;

	/**
	 * Irb Id
	 */
	protected String irbID;

	/**
	 * Description URL
	 */
	protected String descriptionURL;

	/**
	 * Title
	 */
	protected String title;

	/**
	 * Short title
	 */
	protected String shortTitle;

	/**
	 * Start Date
	 */
	protected String startDate;

	/**
	 * End Date
	 */
	protected String endDate;

	/**
	 * Enrollment
	 */
	protected String enrollment;

	/**
	 * Patch Id : Collection_Event_Protocol_Order_8 (Changed from HashMap to LinkedHashMap)
	 * Description : To get CollectionProtocol Events in order
	 */
	/**
	 * Map to handle values of all the CollectionProtocol Events
	 */
	protected Map values = new LinkedHashMap();

	/**
	 * Associates the specified object with the specified key in the map.
	 * @param key the key to which the object is mapped.
	 * @param value the object which is mapped.
	 */
	public void setValue(String key, Object value)
	{
		if (isMutable())
		{
			values.put(key, value);
		}
	}

	/**
	 * Returns the object to which this map maps the specified key.
	 * 
	 * @param key
	 *            the required key.
	 * @return the object to which this map maps the specified key.
	 */
	public Object getValue(String key)
	{
		return values.get(key);
	}

	/**
	 * @return Returns the values.
	 */
	public Collection getAllValues()
	{
		return values.values();
	}

	/**
	 * @param values
	 *            The values to set.
	 */
	public void setValues(Map values)
	{
		this.values = values;
	}

	/**
	 * No argument constructor for CollectionProtocolForm class.
	 */
	public SpecimenProtocolForm()
	{
		reset();
	}

	/**
	 * @return Returns the description url.
	 */
	public String getDescriptionURL()
	{
		return descriptionURL;
	}

	/**
	 * @param descriptionurl
	 *            The description url to set.
	 */
	public void setDescriptionURL(String descriptionurl)
	{
		this.descriptionURL = descriptionurl;
	}

	/**
	 * @return Returns the endDate.
	 */
	public String getEndDate()
	{
		return endDate;
	}

	/**
	 * @param endDate
	 *            The endDate to set.
	 */
	public void setEndDate(String endDate)
	{
		this.endDate = endDate;
	}

	/**
	 * @return Returns the irb id.
	 */
	public String getIrbID()
	{
		return irbID;
	}

	/**
	 * @param irbid
	 *            The irb id to set.
	 */
	public void setIrbID(String irbid)
	{
		this.irbID = irbid;
	}

	/**
	 * @return Returns the participants.
	 */
	public String getEnrollment()
	{
		return enrollment;
	}

	/**
	 * @param participants
	 *            The participants to set.
	 */
	public void setEnrollment(String participants)
	{
		this.enrollment = participants;
	}

	/**
	 * @return Returns the principal investigator id.
	 */
	public long getPrincipalInvestigatorId()
	{
		return principalInvestigatorId;
	}

	/**
	 * @param prinInvestgatrId
	 *            The principal investigator to set.
	 */
	public void setPrincipalInvestigatorId(long prinInvestgatrId)
	{
		this.principalInvestigatorId = prinInvestgatrId;
	}

	/**
	 * @return Returns the short title.
	 */
	public String getShortTitle()
	{
		return shortTitle;
	}

	/**
	 * @param shorttitle
	 *            The short title to set.
	 */
	public void setShortTitle(String shorttitle)
	{
		this.shortTitle = shorttitle;
	}

	/**
	 * @return Returns the startDate.
	 */
	public String getStartDate()
	{
		return startDate;
	}

	/**
	 * @param startDate
	 *            The startDate to set.
	 */
	public void setStartDate(String startDate)
	{
		this.startDate = startDate;
	}

	/**
	 * @return Returns the title.
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * @param title
	 *            The title to set.
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}

	/**
	 * @return Returns the values.
	 */
	public Map getValues()
	{
		return values;
	}

	/**
	 * Copies the data from an AbstractDomain object to a CollectionProtocolForm
	 * object.
	 * 
	 * @param abstractDomain
	 *            An AbstractDomain object.
	 */
	public void setAllValues(AbstractDomainObject abstractDomain)
	{
		SpecimenProtocol protocol = (SpecimenProtocol) abstractDomain;
		this.setId(protocol.getId().longValue());
		if (protocol.getPrincipalInvestigator() != null
				&& protocol.getPrincipalInvestigator().getId() != null)
		{
			this.principalInvestigatorId = protocol.getPrincipalInvestigator().getId().longValue();
		}
		else
		{
			this.principalInvestigatorId = -1;
		}

		this.title = edu.wustl.common.util.Utility.toString(protocol.getTitle());
		this.shortTitle = edu.wustl.common.util.Utility.toString(protocol.getShortTitle());
		this.startDate = edu.wustl.common.util.Utility.parseDateToString(protocol.getStartDate(),
				Constants.DATE_PATTERN_DD_MM_YYYY);
		this.endDate = edu.wustl.common.util.Utility.parseDateToString(protocol.getEndDate(),
				Constants.DATE_PATTERN_DD_MM_YYYY);
		this.irbID = edu.wustl.common.util.Utility.toString(protocol.getIrbIdentifier());
		this.enrollment = edu.wustl.common.util.Utility.toString(protocol.getEnrollment());
		this.descriptionURL = edu.wustl.common.util.Utility.toString(protocol.getDescriptionURL());
		this
				.setActivityStatus(edu.wustl.common.util.Utility.toString(protocol
						.getActivityStatus()));
	}

	/**
	 * Resets the values of all the fields. Is called by the overridden reset
	 * method defined in ActionForm.
	 */
	protected void reset()
	{
		this.principalInvestigatorId = 0;
		this.title = null;
		this.shortTitle = null;
		this.startDate = null;
		this.endDate = null;
		this.irbID = null;
		this.enrollment = null;
		this.descriptionURL = null;

		values = new LinkedHashMap();
	}

	/**
	 * Overrides the validate method of ActionForm.
	 * @param mapping Action mapping instance
	 * @param request HttpServletRequest instance
	 * @return error ActionErrors instance
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request)
	{
		ActionErrors errors = new ActionErrors();
		Validator validator = new Validator();
		try
		{
			if (getOperation() != null
					&& (getOperation().equals(Constants.ADD) || getOperation().equals(
							Constants.EDIT)))
			{
				validatePI(errors);
				validateTitleAndShortTitle(validator, errors);
				validateStartAndEndDate(validator, errors);
				validateEnrollment(validator, errors);
			}
		}
		catch (Exception excp)
		{
			// use of logger as per bug 79
			Logger.out.error("error in SPForm : " + excp.getMessage(), excp);
			errors = new ActionErrors();
		}
		return errors;
	}

	/**
	 * 
	 * @param errors
	 */
	private void validatePI(ActionErrors errors)
	{
		if (this.principalInvestigatorId == -1)
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.selected",
					ApplicationProperties.getValue("collectionprotocol.principalinvestigator")));
		}
	}

	/**
	 * 
	 * @param validator
	 * @param errors
	 */
	private void validateTitleAndShortTitle(Validator validator, ActionErrors errors)
	{
		if (validator.isEmpty(this.title))
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.required",
					ApplicationProperties.getValue("collectionprotocol.protocoltitle")));
		}

		if (validator.isEmpty(this.shortTitle))
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.required",
					ApplicationProperties.getValue("collectionprotocol.shorttitle")));
		}
	}

	/**
	 * 
	 * @param validator
	 * @param errors
	 */
	private void validateStartAndEndDate(Validator validator, ActionErrors errors)
	{
		//		 --- startdate
		//  date validation according to bug id  722 and 730 and 939
		String errorKey = validator.validateDate(startDate, false);
		if (errorKey.trim().length() > 0)
		{
			Logger.out.debug("startdate errorKey : " + errorKey);
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(errorKey, ApplicationProperties
					.getValue("collectionprotocol.startdate")));
		}

		//  --- end date        		
		if (!validator.isEmpty(endDate))
		{
			//  date validation according to bug id  722 and 730 and 939
			errorKey = validator.validateDate(endDate, false);
			if (errorKey.trim().length() > 0 && !"".equals(errorKey))
			{
				Logger.out.debug("enddate errorKey: " + errorKey);
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(errorKey,
						ApplicationProperties.getValue("collectionprotocol.enddate")));
			}
		}

		compareDates(validator, errors);
	}

	/**
	 * 
	 * @param validator
	 * @param errors
	 */
	private void compareDates(Validator validator, ActionErrors errors)
	{
		//		 code added as per bug id 235 
		// code to validate start date less than end date
		// check the start date less than end date
		if (validator.checkDate(startDate) && validator.checkDate(endDate)
				&& !validator.compareDates(startDate, endDate))
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("specimenprotocol.invaliddate",
					ApplicationProperties.getValue("specimenprotocol.invaliddate")));
		}

	}

	/**
	 * 
	 * @param validator
	 * @param errors
	 */
	private void validateEnrollment(Validator validator, ActionErrors errors)
	{
		if (!validator.isEmpty(enrollment))
		{

			try
			{
				Integer.valueOf(enrollment);
			}
			catch (NumberFormatException e)
			{
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.enrollment",
						ApplicationProperties.getValue("collectionprotocol.participants")));
			}
		}
	}

}