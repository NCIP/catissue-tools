
package edu.wustl.clinportal.action;

//import java.util.List;
//import java.util.Map;

//import java.io.IOException;

//import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

//import edu.wustl.clinportal.actionForm.AdvanceSearchForm;
//import edu.wustl.clinportal.util.ConditionMapParser;
import edu.wustl.common.action.BaseAction;
//import edu.wustl.common.util.global.Constants;
//import edu.wustl.common.util.logger.Logger;

/**
 * @author rukhsana_sameer
 *
 *This class is Advanced Search Action which forms the root node to form the query. 
 * 
 */
public class AdvanceSearchAction extends BaseAction//DispatchAction 
{

	/**
	 * 
	 * @param mapping object of ActionMapping
	 * @param form object of ActionForm
	 * @param request object of HttpServletRequest
	 * @param response object of HttpServletResponse
	 * @throws Exception exception
	 * @return value for ActionForward object
	 */
	protected ActionForward executeAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Overrides the execute method of Action class.
	 * @param mapping object of ActionMapping
	 * @param form object of ActionForm
	 * @param request object of HttpServletRequest
	 * @param response object of HttpServletResponse
	 * @throws Exception generic exception
	 */
	/*public ActionForward executeAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		AdvanceSearchForm advanceSearchForm = (AdvanceSearchForm) form;

		//Get the aliasName.

		String target = Constants.SUCCESS;

		//Get the Map values from UI
		Map conditionsMap = advanceSearchForm.getValues();
		Map eventParamCondMap = advanceSearchForm.getEventValues();
		Logger.out.debug("eventParametersConditionsMap " + eventParamCondMap);

		//Parse the conditions to for the advancedConditionNode
		ConditionMapParser parser = new ConditionMapParser();
		//List conditionNodeCollectionForView = parser.parseConditionForQueryView(map);
		List condNodeCollQuery = parser.parseConditionForQuery(conditionsMap);
		//Get the conditions list for event parameters if there are any conditions selected from the UI
		if (!eventParamCondMap.isEmpty())
		{
			String value = (String) eventParamCondMap.get("EventName_1");
			Logger.out.debug("value of first key:" + value);
			if (!"-1".equals(value))
			{
				//Parse the eventParameters map suitable for parseConditionForQuery method.
				Map eventMap = ConditionMapParser.parseEventParameterMap(eventParamCondMap);
				List eventConditions = parser.parseConditionForQuery(eventMap);
				condNodeCollQuery.addAll(eventConditions);
			}
		}
		HttpSession session = request.getSession();
		
		
		Map advCondNodesMap = (Map) session.getAttribute(edu.wustl.clinportal.util.global.Constants.ADVANCED_CONDITION_NODES_MAP);

		// Delete function
		//Represents whether delete is true or not
		String strDelete = request.getParameter("delete");

		//Represents node to be deleted
		String deleteNode = request.getParameter("itemId");
//		session object for query results
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) session
				.getAttribute(edu.wustl.clinportal.util.global.Constants.ADVANCED_CONDITIONS_ROOT);
		
		if ((strDelete != null && deleteNode != null))
		{
			//Delete function
			parser.deleteSelectedNode(deleteNode, advCondNodesMap);
		}
		else
		{
			String selectedNode = getSelectedNode(advanceSearchForm, session);
			String objectName = advanceSearchForm.getObjectName();
//			ItemNode Id represents id of checked check box used in Edit operation
			Integer nodeId = getNodeId(advanceSearchForm);
			//Add or Edit function
			root = parser.createAdvancedQueryObj(condNodeCollQuery, root, objectName, selectedNode,
					advCondNodesMap, nodeId, session);
		}

		session.setAttribute(edu.wustl.clinportal.util.global.Constants.ADVANCED_CONDITIONS_ROOT, root);
		//session.setAttribute(Constants.ADVANCED_CONDITIONS_QUERY_VIEW,root);

		return mapping.findForward(target);
	}

	*//**
	 * 
	 * @param advanceSearchForm
	 * @param session
	 * @return
	 *//*
	private String getSelectedNode(AdvanceSearchForm advanceSearchForm, HttpSession session )
	{
		String selectedNode = advanceSearchForm.getSelectedNode();
		Logger.out.debug("selectedNode--" + selectedNode);
		
		String temp = (String) session.getAttribute("lastNodeId");
		if (temp != null && selectedNode.equals(""))
		{
			selectedNode = temp;
		}

		if ("-1".equals(selectedNode))
		{
			selectedNode = "";
		}
		
		return selectedNode;
	}
	
	*//**
	 * 
	 * @param advanceSearchForm
	 * @return
	 *//*
	private Integer getNodeId(AdvanceSearchForm advanceSearchForm)
	{
		String editStr = advanceSearchForm.getItemNodeId();
		Integer nodeId = null; // NOPMD - DD anomaly
		if (!"".equals(editStr))
		{
			nodeId = Integer.decode(editStr);
		}
		
		return nodeId;
	}*/

}
