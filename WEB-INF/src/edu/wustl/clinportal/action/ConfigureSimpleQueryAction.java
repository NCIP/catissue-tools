
package edu.wustl.clinportal.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.clinportal.bizlogic.BizLogicFactory;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.action.BaseAction;
import edu.wustl.simplequery.actionForm.SimpleQueryInterfaceForm;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;
import edu.wustl.simplequery.bizlogic.QueryBizLogic;
import edu.wustl.common.util.MapDataParser;
import edu.wustl.common.util.logger.Logger;

/**
 * @author falguni_sachde
 *
 */
public class ConfigureSimpleQueryAction extends BaseAction
{

	/**
	 * This is the initialization action class for configuring Simple Search
	 * 
	 * @param mapping object of ActionMapping
	 * @param form object of ActionForm
	 * @param request object of HttpServletRequest
	 * @param response object of HttpServletResponse
	 * @throws Exception generic exception
	 * @return value for ActionForward object
	 */
	protected ActionForward executeAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		//Set the tables for the configuration 
		String pageOf = request.getParameter(Constants.PAGEOF);
		if (pageOf.equals(Constants.PAGEOF_SIMPLE_QUERY_INTERFACE))
		{
			SimpleQueryInterfaceForm simpQueryIfaceFrm = (SimpleQueryInterfaceForm) form;
			HttpSession session = request.getSession();
			Map map = simpQueryIfaceFrm.getValuesMap();
			Logger.out.debug("Form map size" + map.size());
			Logger.out.debug("Form map" + map);
			if (map.size() == 0)
			{
				map = (Map) session.getAttribute(edu.wustl.simplequery.global.Constants.SIMPLE_QUERY_MAP);
				Logger.out.debug("Session map size" + map.size());
				Logger.out.debug("Session map" + map);
			}
			Iterator iterator = map.keySet().iterator();

			//Retrieve the size of the condition list to set size of array of tables.
			MapDataParser parser = new MapDataParser("edu.wustl.simplequery.query");

			Collection simpCondNodeCol = parser.generateData(map, true);
			int counter = simpCondNodeCol.size();
			String[] selectedTables = new String[counter];
			int tableCount = 0;
			while (iterator.hasNext())
			{
				String key = (String) iterator.next();
				Logger.out.debug("map key" + key);
				if (key.endsWith("_table"))
				{
					String table = (String) map.get(key);
					boolean exists = false;
					for (int arrayCount = 0; arrayCount < selectedTables.length; arrayCount++)
					{
						if (selectedTables[arrayCount] != null
								&& selectedTables[arrayCount].equals(table))
						{

							exists = true;

						}
					}
					if (!exists)
					{
						selectedTables[tableCount] = table;
						tableCount++;
					}
				}
			}
			//Set the selected columns for population in the list of ConfigureResultView.jsp
			String[] selectedColumns = simpQueryIfaceFrm.getSelectedColumnNames();
			if (selectedColumns == null)
			{
				selectedColumns = (String[]) session
						.getAttribute(edu.wustl.simplequery.global.Constants.CONFIGURED_SELECT_COLUMN_LIST);
				if (selectedColumns == null)
				{
					IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
					/*QueryBizLogic bizLogic = (QueryBizLogic) BizLogicFactory.getInstance()
							.getBizLogic(edu.wustl.simplequery.global.Constants.SIMPLE_QUERY_INTERFACE_ID);*/
					QueryBizLogic bizLogic = (QueryBizLogic) factory
							.getBizLogic(edu.wustl.simplequery.global.Constants.SIMPLE_QUERY_INTERFACE_ID);
					List colNameValBeans = new ArrayList();
					int cnt;
					for (cnt = 0; cnt < selectedTables.length; cnt++)
					{
						colNameValBeans.addAll(bizLogic.getColumnNames(selectedTables[cnt], true));

					}
					selectedColumns = new String[colNameValBeans.size()];
					Iterator colNameValBeanItr = colNameValBeans.iterator();
					cnt = 0;
					while (colNameValBeanItr.hasNext())
					{
						selectedColumns[cnt] = ((NameValueBean) colNameValBeanItr.next())
								.getValue();
						cnt++;
					}
				}
				simpQueryIfaceFrm.setSelectedColumnNames(selectedColumns);
			}
			session.setAttribute(edu.wustl.common.util.global.Constants.TABLE_ALIAS_NAME, selectedTables);

			//Counter required for redefining the query
			map.put("counter", new String("" + counter));
			session.setAttribute(edu.wustl.simplequery.global.Constants.SIMPLE_QUERY_MAP, map);
		}

		request.setAttribute(Constants.PAGEOF, pageOf);
		return mapping.findForward(pageOf);
	}

}
