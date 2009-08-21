
package edu.wustl.clinportal.action.querysuite;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.action.BaseAction;
import edu.wustl.common.bizlogic.IBizLogic;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;
import edu.wustl.common.querysuite.queryobject.IParameterizedQuery;
import edu.wustl.common.querysuite.queryobject.impl.ParameterizedQuery;
import edu.wustl.common.util.global.ApplicationProperties;

public class DeleteQueryAction extends BaseAction
{

	private static final String QUERY_ID = "queryId";

	/**
	 * Action Handler for Deletes Save query from database 
	 * @param mapping object of ActionMapping
	 * @param form object of ActionForm
	 * @param request object of HttpServletRequest
	 * @param response object of HttpServletResponse
	 * @throws Exception generic exception
	 * @return value for ActionForward object
	 *
	 * @see edu.wustl.common.action.BaseAction#executeAction(org.apache.struts.action.ActionMapping, 
	 * org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected ActionForward executeAction(ActionMapping actionMapping, ActionForm actionForm,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String target = Constants.FAILURE;

		String queryIdStr = request.getParameter(QUERY_ID);
		HttpSession session = request.getSession();
		String qryDelInLastReqst = (String) session.getAttribute(Constants.QUERY_ALREADY_DELETED);
		Long queryId = Long.parseLong(queryIdStr);
		if (queryId != null && (!queryIdStr.equalsIgnoreCase(qryDelInLastReqst)))
		{
			session.setAttribute(Constants.QUERY_ALREADY_DELETED, queryIdStr);
			/*IBizLogic bizLogic = AbstractBizLogicFactory.getBizLogic(ApplicationProperties
					.getValue("app.bizLogicFactory"), "getBizLogic",
					Constants.CATISSUECORE_QUERY_INTERFACE_ID);*/
			IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
			IBizLogic bizLogic = factory.getBizLogic(Constants.CATISSUECORE_QUERY_INTERFACE_ID);
			//IBizLogic bizLogic = BizLogicFactory.getInstance().getBizLogic(Constants.CATISSUECORE_QUERY_INTERFACE_ID);
			try
			{
				List<IParameterizedQuery> queryList = bizLogic.retrieve(ParameterizedQuery.class
						.getName(), Constants.ID, queryId);
				if (!queryList.isEmpty())
				{
					IParameterizedQuery paramQury = queryList.get(0);
					bizLogic.delete(paramQury, Constants.HIBERNATE_DAO);
					target = Constants.SUCCESS;
					setActionError(request, ApplicationProperties
							.getValue("query.deletedSuccessfully.message"));
					session.setAttribute(Constants.QUERY_ALREADY_DELETED, queryIdStr);
				}
				else
				{
					session.removeAttribute(Constants.QUERY_ALREADY_DELETED);
				}
			}
			catch (BizLogicException exception)
			{
				setActionError(request, exception.getMessage());
			}

		}
		return actionMapping.findForward(target);
	}

	/**
	 * Set Error Action
	 * @param request
	 * @param errorMessage
	 */
	private void setActionError(HttpServletRequest request, String errorMessage)
	{
		ActionErrors errors = new ActionErrors();
		ActionError error = new ActionError("errors.item", errorMessage);
		errors.add(ActionErrors.GLOBAL_ERROR, error);
		saveErrors(request, errors);
	}

}
