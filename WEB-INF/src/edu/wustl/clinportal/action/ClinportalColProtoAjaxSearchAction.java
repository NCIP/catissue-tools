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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.JSONArray;
import org.json.JSONObject;

import edu.wustl.clinportal.bizlogic.ClinicalStudyBizLogic;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.clinportal.util.global.Utility;
import edu.wustl.clinportal.util.global.Variables;
import edu.wustl.common.action.BaseAction;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.dao.exception.DAOException;

public class ClinportalColProtoAjaxSearchAction extends BaseAction
{

	@Override
	protected ActionForward executeAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String limit = request.getParameter("limit");
		String query = request.getParameter("query");
		String start = request.getParameter("start");
		String selcpid = request.getParameter("selcpId");
		Integer limitFetch = Integer.parseInt(limit);
		Integer startFetch = Integer.parseInt(start);

		JSONArray jsonArray = new JSONArray();
		JSONObject mainJsonObject = new JSONObject();

		Integer total = limitFetch + startFetch;
		List dataList = new ArrayList();
		if (Variables.isCatissueModelAvailable && selcpid == null)
		{
			dataList = getCProtocols();

		}
		else if (Variables.isCatissueModelAvailable && selcpid != null)
		{
			dataList = getCPEvents(Long.valueOf(selcpid));
		}
		List<NameValueBean> querySpecificNVBeans = new ArrayList<NameValueBean>();
		Utility.populateQuerySpecificNameValueBeansList(querySpecificNVBeans, dataList, query);
		mainJsonObject.put("totalCount", querySpecificNVBeans.size());

		for (int i = startFetch; i < total && i < querySpecificNVBeans.size(); i++)
		{
			JSONObject jsonObject = new JSONObject();
			Locale locale = CommonServiceLocator.getInstance().getDefaultLocale();

			if (query == null
					|| querySpecificNVBeans.get(i).getName().toLowerCase(locale).contains(
							query.toLowerCase(locale)) || query.length() == 0)
			{
				jsonObject.put("id", querySpecificNVBeans.get(i).getValue());
				jsonObject.put("field", querySpecificNVBeans.get(i).getName());
				jsonArray.put(jsonObject);
			}
		}

		mainJsonObject.put("row", jsonArray);
		response.flushBuffer();
		PrintWriter out = response.getWriter();
		out.write(mainJsonObject.toString());

		return null;
	}

	/**
	 * 
	 * @return returns the user list present in the system
	 * @throws DAOException
	 * @throws BizLogicException
	 */
	private List getCProtocols() throws DAOException, BizLogicException
	{
		IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
		ClinicalStudyBizLogic csBizLogic = (ClinicalStudyBizLogic) factory
				.getBizLogic(Constants.CLINICALSTUDY_FORM_ID);
		List cpCollection = csBizLogic.getCatissueCP();
		return cpCollection;
	}

	private List getCPEvents(Long cpId) throws DAOException, BizLogicException
	{
		IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
		ClinicalStudyBizLogic csBizLogic = (ClinicalStudyBizLogic) factory
				.getBizLogic(Constants.CLINICALSTUDY_FORM_ID);
		List cpCollection = csBizLogic.getCatissueCPEvent(cpId);
		return cpCollection;
	}

}
