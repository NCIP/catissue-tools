/*
 * @author
 *
 */

package edu.wustl.clinportal.action.annotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.ehcache.CacheException;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.webui.util.WebUIManager;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.clinportal.util.DataEntryUtil;
import edu.wustl.clinportal.util.EventTreeObject;
import edu.wustl.common.action.BaseAction;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.security.exception.UserNotAuthorizedException;

/**
 * @author falguni_sachde
 *
 *
 */
public class LoadAnnotationDataEntryPageAction extends BaseAction
{

	private transient Logger logger = Logger
			.getCommonLogger(LoadAnnotationDataEntryPageAction.class);

	/* (non-Javadoc)
	 * @see edu.wustl.common.action.BaseAction#executeAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected ActionForward executeAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{

		String staticEntityId = null, staticEntityName = null;
		String comingFrom = (String) request.getSession().getAttribute(
				edu.wustl.clinportal.util.global.Constants.FORWARD_CONTROLLER);
	
		try
		{
			if (request.getParameter(WebUIManager.getOperationStatusParameterName()) != null)
			{
				//Return from dynamic extensions
				processResponseFromDynamicExtensions(request);
				staticEntityId = (String) request.getSession().getAttribute(
						AnnotationConstants.SELECTED_STATIC_ENTITYID);
				staticEntityName = request.getParameter("staticEntityName");
				if (staticEntityName != null)
				{
					request.getSession().setAttribute("staticEntityName", staticEntityName);
				}
				if (staticEntityName == null)
				{
					staticEntityName = (String) request.getSession().getAttribute(
							"staticEntityName");
				}
			}
			else
			{
				staticEntityName = (String) request.getParameter("staticEntityName");
				if (staticEntityName != null)
				{
					request.getSession().setAttribute("staticEntityName", staticEntityName);
				}
				if (staticEntityName == null)
				{
					staticEntityName = (String) request.getSession().getAttribute(
							"staticEntityName");
				}
				staticEntityId = request.getParameter(AnnotationConstants.REQST_PARAM_ENTITY_ID);
				updateCache(request);
			}
			logger.info("Updating for Entity Id " + staticEntityId);
		}
		catch (BizLogicException excp)
		{
			ActionErrors errors = new ActionErrors();
			if (excp.getMessage() != null
					&& excp.getMessage().contains(
							ApplicationProperties.getValue("eventEntry.already.exists")))
			{
				String errs[] = excp.getMessage().split(" ");
				String arg = "";
				if (errs != null && errs.length > 0)
				{
					arg = errs[errs.length - 2];
				}
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item",
						ApplicationProperties.getValue("errors.dataEntry.singleRecord", arg)));
			}
			else
			{
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item", excp
						.getMessage()));
			}
			String key = (String) request.getSession().getAttribute(
					AnnotationConstants.TREE_VIEW_KEY);
			EventTreeObject treeObject = new EventTreeObject(key);
			new DataEntryUtil().genrateEventKey(treeObject);
			String treeViewKey = treeObject.createKeyFromObject();
			request.getSession().setAttribute(AnnotationConstants.TREE_VIEW_KEY, treeViewKey);
			saveErrors(request, errors);

		}
		if (comingFrom != null && !comingFrom.equals(""))
		{
			return mapping.findForward(comingFrom);
		}
		else
		{
			return mapping.findForward(DEConstants.SUCCESS);
		}
	}

	/**
	 * @param request
	 * @throws CacheException 
	 */
	private void updateCache(HttpServletRequest request) throws CacheException
	{
		String parentEntityId = request.getParameter(AnnotationConstants.REQST_PARAM_ENTITY_ID);
		String parEntityRecId = request
				.getParameter(AnnotationConstants.REQST_PARAM_ENTITY_RECORD_ID);
		String entIdForCondon = request
				.getParameter(AnnotationConstants.REQST_PARAM_CONDITION_ENTITY_ID);
		String entRecIdFrCondn = request
				.getParameter(AnnotationConstants.REQST_PARAM_CONDITION_ENTITY_RECORD_ID);
		//Set into Cache
		HttpSession session = request.getSession();

		if (session != null)
		{
			session.setAttribute(AnnotationConstants.SELECTED_STATIC_ENTITYID, parentEntityId);
			session.setAttribute(AnnotationConstants.SELECTED_STATIC_ENTITY_RECORDID,
					parEntityRecId);
			session.setAttribute(AnnotationConstants.ENTITY_ID_IN_CONDITION, entIdForCondon);
			session.setAttribute(AnnotationConstants.ENTITY_RECORDID_IN_CONDITION, entRecIdFrCondn);
		}
	}

	/**
	 * @param request
	 * @throws CacheException 
	 * @throws BizLogicException
	 * @throws DAOException 
	 * @throws UserNotAuthorizedException 
	 * @throws DynamicExtensionsSystemException 
	 * @throws DynamicExtensionsApplicationException 
	 */
	private void processResponseFromDynamicExtensions(HttpServletRequest request)
			throws CacheException, BizLogicException, DAOException, UserNotAuthorizedException,
			DynamicExtensionsApplicationException, DynamicExtensionsSystemException
	{

		String operationStatus = request.getParameter(WebUIManager
				.getOperationStatusParameterName());
		if ((operationStatus != null)
				&& (operationStatus.trim().equals(WebUIManagerConstants.SUCCESS)))
		{
			String dynExtRecordId = request.getParameter(WebUIManager
					.getRecordIdentifierParameterName());

			request.getSession().setAttribute(AnnotationConstants.DYNAMIC_ENTITY_RECORD_ID,
					dynExtRecordId);
			logger.info("Dynamic Entity Record Id [" + dynExtRecordId + "]");

			insertEventEntry(dynExtRecordId, request);
		}
	}
	/**	 
	 * This method creates eventEntry object and associate De record with static entity
	 * @throws CacheException
	 * @throws DAOException 
	 * @throws UserNotAuthorizedException 
	 * @throws BizLogicException 
	 * @throws DynamicExtensionsSystemException 
	 * @throws DynamicExtensionsApplicationException 
	 */
	private void insertEventEntry(String dynExtRecordId, HttpServletRequest request)
			throws CacheException, DAOException, BizLogicException, UserNotAuthorizedException,
			DynamicExtensionsApplicationException, DynamicExtensionsSystemException
	{
		String key = (String) request.getSession().getAttribute(AnnotationConstants.TREE_VIEW_KEY);
		EventTreeObject treeObject = new EventTreeObject(key);
		DataEntryUtil dataEntryUtil = new DataEntryUtil();
		dataEntryUtil.insertEventEntry(treeObject, dynExtRecordId, request);
		request.getSession().setAttribute(AnnotationConstants.TREE_VIEW_KEY,
				treeObject.createKeyFromObject());

	}
}