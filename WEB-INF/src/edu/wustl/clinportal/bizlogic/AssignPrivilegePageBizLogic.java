
package edu.wustl.clinportal.bizlogic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.wustl.clinportal.actionForm.AssignPrivilegesForm;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.exception.BizLogicException;

/**
 * @author falguni_sachde
 *
 */
public class AssignPrivilegePageBizLogic extends ClinportalDefaultBizLogic
{

	/**
	 * @param recordIds
	 * @param privilegesForm
	 * @return
	 * @throws BizLogicException 
	 */
	public List getRecordNames(Set recordIds, AssignPrivilegesForm privilegesForm)
			throws BizLogicException
	{
		List recordNames = new ArrayList();
		//Bug: 2508:  to display name in alphabetically order.
		if (!recordIds.isEmpty())
		{
			Object[] whereColumn = new String[recordIds.size()];
			Iterator itr = recordIds.iterator();
			int cnt = 0;
			while (itr.hasNext())
			{
				NameValueBean nameValueBean = (NameValueBean) itr.next();
				whereColumn[cnt] = nameValueBean.getValue();
				cnt++;
			}

			String sourceObjectName = privilegesForm.getObjectType();
			String[] selectColumnName = new String[2];
			String[] whereColumnName = {"id"};
			String[] whrColCondn = {"in"};
			Object[] whereColumnValue = {whereColumn};

			if (privilegesForm.getObjectType().equals(
					"edu.wustl.clinportal.domain.CollectionProtocol"))
			{
				selectColumnName[0] = "title";
				selectColumnName[1] = "id";
			}
			else
			{
				selectColumnName[0] = "name";
				selectColumnName[1] = "id";
			}

			List list = retrieve(sourceObjectName, selectColumnName, whereColumnName, whrColCondn,
					whereColumnValue, null);

			if (!list.isEmpty())
			{
				for (cnt = 0; cnt < list.size(); cnt++)
				{
					Object[] obj = (Object[]) list.get(cnt);
					recordNames.add(new NameValueBean(obj[0], obj[1]));
				}
			}
			Collections.sort(recordNames);
		}
		return recordNames;
	}
}
