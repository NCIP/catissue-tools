/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */


package edu.wustl.clinportal.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.action.BaseAction;

/**
 * 
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright: (c) Washington University, School of Medicine 2005</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *@author Aarti Sharma
 *@version 1.0
 */
public class LogoutAction extends BaseAction
{

	/**
	 * @param mapping object of ActionMapping
	 * @param form object of ActionForm
	 * @param request object of HttpServletRequest
	 * @param response object of HttpServletResponse
	 * @throws Exception generic exception
	 */
	public ActionForward executeAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception
	{

		HttpSession session = request.getSession();
//		Deepti : following code deletes the old advance query temporary table.Commenting
		//it as adv query is no more used
		/*HttpSession session = request.getSession();
		
		//Delete Advance Query table if exists
	 	SessionDataBean sessionData = getSessionData(request);
	 	//Advance Query table name with userID attached
		String tempTableName = Constants.QUERY_RESULTS_TABLE+"_"+sessionData.getUserId();

 		JDBCDAO jdbcDao = (JDBCDAO)DAOFactory.getInstance().getDAO(0);
        jdbcDao.openSession(sessionData);
       	jdbcDao.delete(tempTableName);
        jdbcDao.closeSession();*/

		session.invalidate();

		return mapping.findForward(Constants.SUCCESS);
	}

}