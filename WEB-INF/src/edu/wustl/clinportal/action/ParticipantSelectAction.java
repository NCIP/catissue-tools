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
 * <p>Title: ParticipantSelectAction Class>
 * <p>Description:	This Class is used when participant is selected from the list.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author vaishali_khandelwal
 * @Created on June 06, 2006
 */

package edu.wustl.clinportal.action;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import edu.wustl.clinportal.actionForm.ParticipantForm;
import edu.wustl.clinportal.domain.Participant;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.action.BaseAction;
import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.beans.AddNewSessionDataBean;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.bizlogic.IBizLogic;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IDomainObjectFactory;
import edu.wustl.common.factory.IFactory;
import edu.wustl.common.factory.IForwordToFactory;
import edu.wustl.common.util.AbstractForwardToProcessor;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.logger.Logger;

public class ParticipantSelectAction extends BaseAction
{

	private transient Logger logger = Logger.getCommonLogger(ParticipantSelectAction.class);

	/**
	 * @param mapping object of ActionMapping
	 * @param form object of ActionForm
	 * @param request object of HttpServletRequest
	 * @param response object of HttpServletResponse
	 * @throws Exception generic exception
	 * @return value for ActionForward object
	 */
	public ActionForward executeAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		AbstractDomainObject abstractDomain = null;
		ActionMessages messages = null;
		String target = null;
		AbstractActionForm abstractForm = (AbstractActionForm) form;
		ParticipantForm participantForm = (ParticipantForm) form;

		IDomainObjectFactory domainObjectFactory = AbstractFactoryConfig.getInstance()
				.getDomainObjectFactory();
		IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();

		IBizLogic bizLogic = factory.getBizLogic(abstractForm.getFormId());

		String objectName = domainObjectFactory.getDomainObjectName(abstractForm.getFormId());

		logger.info("Participant Id-------------------" + request.getParameter("participantId"));

		List participants = bizLogic.retrieve(objectName, Constants.SYSTEM_IDENTIFIER, Long
				.valueOf(request.getParameter("participantId")));
		request.removeAttribute("participantForm");
		abstractDomain = (AbstractDomainObject) participants.get(0);
		Participant participant = (Participant) abstractDomain;

		logger.info("Last name in ParticipantSelectAction:" + participant.getLastName());
		/**
		 * Instead of setAllValues() method retrieveFroEditMode() method is called to bypass lazy loading error in domain object
		 */

		DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
		defaultBizLogic.populateUIBean(Participant.class.getName(), participant.getId(),
				participantForm);

		//Setting the ParticipantForm in request for storing selected participant's data.
		//ParticipantSelect attribute is used for deciding in next action weather that action is called after ParticipantSelectAction or not
		request.setAttribute("participantForm1", participantForm);
		request.setAttribute("participantSelect", "yes");

		//Attributes to decide AddNew action
		String submittedFor = (String) request.getParameter(Constants.SUBMITTED_FOR);

		logger.info("submittedFor in ParticipantSelectAction:" + submittedFor);
		//------------------------------------------------ AddNewAction Starts----------------------------
		//if AddNew action is executing, load FormBean from Session and redirect to Action which initiated AddNew action
		if ((submittedFor != null) && (submittedFor.equals("AddNew")))
		{
			HttpSession session = request.getSession();
			Stack formBeanStack = (Stack) session
					.getAttribute(edu.wustl.common.util.global.Constants.FORM_BEAN_STACK);

			if (formBeanStack != null)
			{
				//Retrieving AddNewSessionDataBean from Stack
				AddNewSessionDataBean addNewSesDataBean = (AddNewSessionDataBean) formBeanStack
						.pop();

				if (addNewSesDataBean != null)
				{
					//Retrieving FormBean stored into AddNewSessionDataBean 
					AbstractActionForm sessionFormBean = addNewSesDataBean.getAbstractActionForm();

					String forwardTo = addNewSesDataBean.getForwardTo();
					logger.debug("forwardTo in ParticipantSelectAction--------->" + forwardTo);

					logger.info("Id-----------------" + abstractDomain.getId());
					//Setting Identifier of new object into the FormBean to populate it on the JSP page 
					sessionFormBean.setAddNewObjectIdentifier(addNewSesDataBean.getAddNewFor(),
							abstractDomain.getId());

					sessionFormBean.setMutable(false);

					//cleaning FORM_BEAN_STACK from Session if no AddNewSessionDataBean available... Storing appropriate value of SUBMITTED_FOR attribute
					if (formBeanStack.isEmpty())
					{
						session
								.removeAttribute(edu.wustl.common.util.global.Constants.FORM_BEAN_STACK);
						request.setAttribute(Constants.SUBMITTED_FOR, "Default");
						logger
								.debug("SubmittedFor set as Default in ParticipantSelectAction===========");

						logger.debug("cleaning FormBeanStack from session*************");
					}
					else
					{
						request.setAttribute(Constants.SUBMITTED_FOR, "AddNew");
					}

					//Storing FormBean into Request to populate data on the page being forwarded after AddNew activity, 
					//FormBean should be stored with the name defined into Struts-Config.xml to populate data properly on JSP page 
					String formBeanName = Utility.getFormBeanName(sessionFormBean);
					request.setAttribute(formBeanName, sessionFormBean);

					logger.debug("InitiliazeAction operation=========>"
							+ sessionFormBean.getOperation());

					//Storing Success messages into Request to display on JSP page being forwarded after AddNew activity
					if (messages != null)
					{
						saveMessages(request, messages);
					}

					//Status message key.
					String statusMessageKey = String.valueOf(abstractForm.getFormId() + "."
							+ abstractForm.isAddOperation());
					request.setAttribute(edu.wustl.common.util.global.Constants.STATUS_MESSAGE_KEY,
							statusMessageKey);

					//Changing operation attribute in parth specified in ForwardTo mapping, If AddNew activity started from Edit page
					if (sessionFormBean.getOperation().equals("edit"))
					{
						logger
								.debug("Edit object Identifier while AddNew is from Edit operation==>"
										+ sessionFormBean.getId());
						ActionForward editForward = new ActionForward();

						String addPath = mapping.findForward(forwardTo).getPath();
						logger.debug("Operation before edit==========>" + addPath);

						String editPath = addPath.replaceFirst("operation=add", "operation=edit");
						logger.debug("Operation edited=============>" + editPath);
						editForward.setPath(editPath);

						return editForward;
					}

					return mapping.findForward(forwardTo);
				}
				//Setting target as FAILURE if AddNewSessionDataBean is null
				else
				{
					target = new String(Constants.FAILURE);

					ActionErrors errors = new ActionErrors();
					ActionError error = new ActionError("errors.item.unknown", AbstractDomainObject
							.parseClassName(objectName));
					errors.add(ActionErrors.GLOBAL_ERROR, error);
					saveErrors(request, errors);
				}
			}
		}
		//------------------------------------------------ AddNewAction Ends----------------------------                
		//----------ForwardTo Starts----------------
		else if ((submittedFor != null) && (submittedFor.equals("ForwardTo")))
		{
			logger.debug("SubmittedFor is ForwardTo in CommonAddEditAction...................");

			//Storing appropriate value of SUBMITTED_FOR attribute
			request.setAttribute(Constants.SUBMITTED_FOR, "Default");

			//storing HashMap of forwardTo data into Request
			request.setAttribute("forwardToHashMap", generateForwardToHashMap(abstractForm,
					abstractDomain));
		}
		//----------ForwardTo Ends----------------

		//setting target to ForwardTo attribute of submitted Form 
		if (abstractForm.getForwardTo() != null && abstractForm.getForwardTo().trim().length() > 0)
		{
			String forwardTo = abstractForm.getForwardTo();
			logger.debug("ForwardTo in Add :-- : " + forwardTo);
			target = forwardTo;
			//return (mapping.findForward(forwardTo));
		}

		logger.info("target in ParticipantSelectAction:" + target);
		return mapping.findForward(target);

	}

	/**
	 * This method generates HashMap of data required to be forwarded if Form is submitted for ForwardTo request
	 * @param abstractForm	Form submitted
	 * @param abstractDomain	DomainObject Added/Edited
	 * @return	HashMap of data required to be forwarded
	 */
	private HashMap generateForwardToHashMap(AbstractActionForm abstractForm,
			AbstractDomainObject abstractDomain) throws BizLogicException
	{
		IForwordToFactory factory = AbstractFactoryConfig.getInstance().getForwToFactory();
		AbstractForwardToProcessor fwrdToProcessor = factory.getForwardToProcessor();
		//Populating HashMap of the data required to be forwarded on next page
		HashMap forwardToHashMap = (HashMap) fwrdToProcessor.populateForwardToData(abstractForm,
				abstractDomain);

		return forwardToHashMap;
	}
}
