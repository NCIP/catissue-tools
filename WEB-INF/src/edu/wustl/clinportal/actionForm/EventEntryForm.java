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
 * 
 */

package edu.wustl.clinportal.actionForm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import edu.wustl.clinportal.domain.EventEntry;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.global.Validator;

/**
 * @author falguni_sachde
 *
 */
public class EventEntryForm extends ActionForm
{

	private String encounterDate;

	private String activityStatus;

	private String participantActivityStatus;
	private String CSRActivityStatus;
	private String CSActivityStatus;

	private Integer entryNumber;

	/**
	 * @return the encounterDate
	 */
	public String getEncounterDate()
	{
		return encounterDate;
	}

	/**
	 * @param encounterDate the encounterDate to set
	 */
	public void setEncounterDate(String encounterDate)
	{
		this.encounterDate = encounterDate;
	}

	/**
	 * @return the activityStatus
	 */
	public String getActivityStatus()
	{
		return activityStatus;
	}

	/**
	 * @param activityStatus the activityStatus to set
	 */
	public void setActivityStatus(String activityStatus)
	{
		this.activityStatus = activityStatus;
	}

	/**
	 * @return the entryNUmber
	 */
	public Integer getEntryNumber()
	{
		return entryNumber;
	}

	/**
	 * @param entryNUmber the entryNUmber to set
	 */
	public void setEntryNUmber(Integer entryNumber)
	{
		this.entryNumber = entryNumber;
	}

	/* (non-Javadoc)
	 * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request)
	{

		edu.wustl.clinportal.util.global.Validator validator = new edu.wustl.clinportal.util.global.Validator();
		ActionErrors errors = new ActionErrors();
		if (validator.isEmpty(this.getEncounterDate()))
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("Please enter date"));
		}
		String errorKey = validator.validateDate(this.getEncounterDate(), true);
		if (errorKey.trim().length() > 0)
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item",
					ApplicationProperties.getValue("errors.invalid.date", "Encountered Date")));
		}

		return errors;
	}

	/**
	 * @param domainObj
	 */
	public void setAllValues(AbstractDomainObject domainObj)
	{
		EventEntry eventEntry = (EventEntry) domainObj;
		if (eventEntry.getEncounterDate() == null || eventEntry.getEncounterDate().equals(""))
		{
			this.encounterDate = edu.wustl.common.util.Utility.parseDateToString(new Date(System
					.currentTimeMillis()), Constants.DATE_PATTERN_DD_MM_YYYY);
		}
		else
		{
			this.encounterDate = edu.wustl.common.util.Utility.parseDateToString(eventEntry
					.getEncounterDate(), Constants.DATE_PATTERN_DD_MM_YYYY);
		}
		this.entryNumber = eventEntry.getEntryNumber();
	}

	/**
	 * @return the cSActivityStatus
	 */
	public String getCSActivityStatus()
	{
		return CSActivityStatus;
	}

	/**
	 * @param activityStatus the cSActivityStatus to set
	 */
	public void setCSActivityStatus(String activityStatus)
	{
		CSActivityStatus = activityStatus;
	}

	/**
	 * @return the cSRActivityStatus
	 */
	public String getCSRActivityStatus()
	{
		return CSRActivityStatus;
	}

	/**
	 * @param activityStatus the cSRActivityStatus to set
	 */
	public void setCSRActivityStatus(String activityStatus)
	{
		CSRActivityStatus = activityStatus;
	}

	/**
	 * @return the participantActivityStatus
	 */
	public String getParticipantActivityStatus()
	{
		return participantActivityStatus;
	}

	/**
	 * @param partActStatus the participantActivityStatus to set
	 */
	public void setParticipantActivityStatus(String partActStatus)
	{
		this.participantActivityStatus = partActStatus;
	}

}
