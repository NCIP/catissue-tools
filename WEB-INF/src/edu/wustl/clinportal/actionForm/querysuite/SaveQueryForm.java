/**
 * 
 */

package edu.wustl.clinportal.actionForm.querysuite;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.querysuite.queryobject.IParameterizedQuery;
import edu.wustl.common.util.global.Validator;

/**
 * @author falguni_sachde
 *
 */
public class SaveQueryForm extends AbstractActionForm
{

	private static final long serialVersionUID = 1L;

	private Collection<IParameterizedQuery> paramQueryCollection;

	private String title;

	private String description;

	private Long queryId;

	private String queryString;

	/**
	 * @return
	 */
	public String getQueryString()
	{
		return queryString;
	}

	/**
	 * @param queryString
	 */
	public void setQueryString(String queryString)
	{
		this.queryString = queryString;
	}

	/* (non-Javadoc)
	 * @see edu.wustl.common.actionForm.AbstractActionForm#getFormId()
	 */
	public int getFormId()
	{
		return 0;
	}

	/* (non-Javadoc)
	 * @see edu.wustl.common.actionForm.AbstractActionForm#reset()
	 */
	protected void reset()
	{

	}

	/* (non-Javadoc)
	 * @see edu.wustl.common.actionForm.IValueObject#setAllValues(edu.wustl.common.domain.AbstractDomainObject)
	 */
	public void setAllValues(AbstractDomainObject arg0)
	{

	}

	/**
	 * @return the parameterizedQueryCollection
	 */
	public Collection<IParameterizedQuery> getParameterizedQueryCollection()
	{
		return paramQueryCollection;
	}

	/**
	 * @param paramQryColn the parameterizedQueryCollection to set
	 */
	public void setParameterizedQueryCollection(Collection<IParameterizedQuery> paramQryColn)
	{
		this.paramQueryCollection = paramQryColn;
	}

	/**
	 * @return the title
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * Overrides the validate method of ActionForm.
	 * @param mapping ActionMapping mapping
	 * @param request HttpServletRequest request
	 * @return ActionErrors
	 * */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request)
	{
		ActionErrors errors = new ActionErrors();
		Validator validator = new Validator();
		if (title == null || validator.isEmpty(title))
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.required", "Title"));
		}
		return errors;
	}

	/**
	 * @return the queryId
	 */
	public Long getQueryId()
	{
		return queryId;
	}

	/**
	 * @param queryId the queryId to set
	 */
	public void setQueryId(Long queryId)
	{
		this.queryId = queryId;
	}

	@Override
	public void setAddNewObjectIdentifier(String addNewFor, Long addObjectIdentifier) {
		// TODO Auto-generated method stub
		
	}

}
