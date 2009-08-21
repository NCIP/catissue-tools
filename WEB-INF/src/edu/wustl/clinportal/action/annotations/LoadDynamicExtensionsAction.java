/*
 * Created on January 8, 2007
 * @author
 *
 */

package edu.wustl.clinportal.action.annotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.common.dynamicextensions.ui.webui.util.WebUIManager;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.clinportal.actionForm.AnnotationForm;
import edu.wustl.common.action.BaseAction;

/**
 * @author falguni_sachde
 *
 */
public class LoadDynamicExtensionsAction extends BaseAction
{

	/* (non-Javadoc)
	 * @see edu.wustl.common.action.BaseAction#executeAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected ActionForward executeAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{

		AnnotationForm annotationForm = (AnnotationForm) form;
		//Get static entity id and store in cache
		String staticEntityId = annotationForm.getSelectedStaticEntityId();
		String comingFrom = request
				.getParameter(edu.wustl.clinportal.util.global.Constants.FORWARD_CONTROLLER);
		request.getSession().setAttribute(
				edu.wustl.clinportal.util.global.Constants.FORWARD_CONTROLLER, comingFrom);
		if (staticEntityId == null)
		{
			staticEntityId = request.getParameter("staticEntityId");
		}

		String[] conditions = annotationForm.getConditionVal();

		HttpSession session = request.getSession();
		session.setAttribute(AnnotationConstants.SELECTED_STATIC_ENTITYID, staticEntityId);
		session.setAttribute(AnnotationConstants.SELECTED_STATIC_RECORDID, conditions);

		//Get Dynamic extensions URL
		String dExtenonsURL = getDynamicExtensionsURL(request);
		//Set as request attribute
		request.setAttribute(AnnotationConstants.DYNAMIC_EXTN_URL_ATTRIB, dExtenonsURL);
		return mapping.findForward(DEConstants.SUCCESS);
	}

	/**
	 * @param request 
	 * @return
	 */
	private String getDynamicExtensionsURL(HttpServletRequest request)
	{
		//Get Dynamic extensions URL
		String dExtionsURL = request.getContextPath() + WebUIManager.getCreateContainerURL();
		//append container id if any
		if (request.getParameter("containerId") != null)
		{
			dExtionsURL = dExtionsURL + "?" + WebUIManager.CONATINER_IDENTIFIER_PARAMETER_NAME
					+ "=" + request.getParameter("containerId");
			dExtionsURL = dExtionsURL + "&" + WebUIManager.getCallbackURLParamName() + "="
					+ request.getContextPath()
					+ AnnotationConstants.CALLBACK_URL_PATH_ANNOTATION_DEFN;
		}
		else
		{
			//append call back parameter
			dExtionsURL = dExtionsURL + "?" + WebUIManager.getCallbackURLParamName() + "="
					+ request.getContextPath()
					+ AnnotationConstants.CALLBACK_URL_PATH_ANNOTATION_DEFN;
		}
		return dExtionsURL;
	}
}
