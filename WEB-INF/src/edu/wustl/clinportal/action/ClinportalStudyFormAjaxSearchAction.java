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

import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.clinportal.action.annotations.AnnotationConstants;
import edu.wustl.clinportal.bizlogic.ClinicalStudyBizLogic;
import edu.wustl.clinportal.util.CatissueCoreCacheManager;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.clinportal.util.global.Utility;
import edu.wustl.common.action.BaseAction;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.dao.exception.DAOException;

public class ClinportalStudyFormAjaxSearchAction extends BaseAction
{

	@Override
	protected ActionForward executeAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String varlimit = request.getParameter("limit");
		String varquery = request.getParameter("query");
		String varstart = request.getParameter("start");
		Integer varlimitFetch = Integer.parseInt(varlimit);
		Integer varstartFetch = Integer.parseInt(varstart);
		JSONArray varjsonArray = new JSONArray();
		JSONObject varmainJsonObject = new JSONObject();

		Integer total = varlimitFetch + varstartFetch;
		List vardataList = new ArrayList();

		vardataList = getStudyForms();

		List<NameValueBean> varquerySpecificNVBeans = new ArrayList<NameValueBean>();
		Utility.populateQuerySpecificNameValueBeansList(varquerySpecificNVBeans, vardataList,
				varquery);
		varmainJsonObject.put("totalCount", varquerySpecificNVBeans.size());

		for (int i = varstartFetch; i < total && i < varquerySpecificNVBeans.size(); i++)
		{
			JSONObject jsonObject = new JSONObject();
			Locale locale = CommonServiceLocator.getInstance().getDefaultLocale();

			if (varquery == null
					|| varquerySpecificNVBeans.get(i).getName().toLowerCase(locale).contains(
							varquery.toLowerCase(locale)) || varquery.length() == 0)
			{
				jsonObject.put("id", varquerySpecificNVBeans.get(i).getValue());
				jsonObject.put("field", varquerySpecificNVBeans.get(i).getName());
				varjsonArray.put(jsonObject);
			}
		}

		varmainJsonObject.put("row", varjsonArray);
		response.flushBuffer();
		PrintWriter out = response.getWriter();
		out.write(varmainJsonObject.toString());

		return null;
	}

	/**
	 * 
	 * @return returns the user list present in the system
	 * @throws DAOException
	 * @throws BizLogicException
	 * @throws DynamicExtensionsApplicationException 
	 * @throws DynamicExtensionsSystemException 
	 * @throws NumberFormatException 
	 */
	private List getStudyForms() throws DAOException, BizLogicException, NumberFormatException,
			DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		Long varstaticEntityId = (Long) CatissueCoreCacheManager.getInstance().getObjectFromCache(
				AnnotationConstants.RECORD_ENTRY_ENTITY_ID);
		IFactory varfactory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
		ClinicalStudyBizLogic varcsBizLogic = (ClinicalStudyBizLogic) varfactory
				.getBizLogic(Constants.CLINICALSTUDY_FORM_ID);
		List collection = varcsBizLogic.getStudyFormsList(varstaticEntityId);
		return collection;
	}

}
