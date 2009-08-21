/*
 * Created on September 1, 2005
 * This class is used to display tree in the Query view in the Advance Search Action
 */

package edu.wustl.clinportal.action;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.clinportal.query.TreeView;
import edu.wustl.common.action.BaseAction;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;

/**
 * @author rukhsana sameer
 *
 * This class is used to display tree in the Query view in the Advance Search Action
 */
public class AdvanceQueryView extends BaseAction
{

	public ActionForward executeAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		HttpSession session = request.getSession();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) session
				.getAttribute(edu.wustl.clinportal.util.global.Constants.ADVANCED_CONDITIONS_ROOT);
		int childCount = root.getChildCount();
		Logger.out.debug("child count in tree view action" + childCount);
		Vector tree = new Vector();
		String tempCheckedNode = request.getParameter("itemId");
		String operator = request.getParameter("operator");
		Map advCondNodesMap = new HashMap();
		advCondNodesMap.put(Integer.valueOf(0), root);
		//First time the Advance Search page loads, the tree is empty
		if (childCount == 0)
		{
			tree.add("");
		}
		else
		{
			//Create tree for Query View
			TreeView view = new TreeView();
			//To use Pseudo AND
			if (tempCheckedNode != null && operator != null)
			{
				int checkedNode = Integer.parseInt(tempCheckedNode);
				view.arrangeTree(root, 0, tree, advCondNodesMap, checkedNode, operator, session);
			}
			else
			{
				view.arrangeTree(root, 0, tree, advCondNodesMap, 0, null, session);
			}
			Logger.out.debug("advancedConditionNodesMap in AdvanceQueryViewAction"
					+ advCondNodesMap);
			Logger.out.debug("Size of advancedConditionNodesMap in AdvanceQueryViewAction"
					+ advCondNodesMap.size());
		}

		session.setAttribute(edu.wustl.clinportal.util.global.Constants.ADVANCED_CONDITIONS_ROOT, root);
		session.setAttribute(edu.wustl.clinportal.util.global.Constants.ADVANCED_CONDITION_NODES_MAP, advCondNodesMap);
		Logger.out.debug("Vector size of the tree" + tree);
		request.setAttribute(edu.wustl.clinportal.util.global.Constants.TREE_VECTOR, tree);
		return mapping.findForward(Constants.SUCCESS);
	}
}
