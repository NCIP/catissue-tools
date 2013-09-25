/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */


package edu.wustl.clinportal.flex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

//import edu.wustl.cab2b.client.ui.query.ClientQueryBuilder;
import edu.wustl.cab2b.client.ui.query.IClientQueryBuilderInterface;
import edu.wustl.cab2b.client.ui.query.IPathFinder;
import edu.wustl.clinportal.bean.CpAndParticipentsBean;
import edu.wustl.clinportal.bizlogic.EventTreeBizLogic;
import edu.wustl.clinportal.bizlogic.UserBizLogic;
import edu.wustl.clinportal.domain.ClinicalStudy;
import edu.wustl.clinportal.flex.dag.DAGConstant;
import edu.wustl.clinportal.flex.dag.DAGNode;
import edu.wustl.clinportal.flex.dag.DAGPanel;
import edu.wustl.clinportal.flex.dag.DAGPath;
import edu.wustl.clinportal.util.ParticipantRegistrationCacheManager;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.cde.CDE;
import edu.wustl.common.cde.CDEManager;
import edu.wustl.common.cde.PermissibleValue;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.querysuite.metadata.path.IPath;
import edu.wustl.common.querysuite.metadata.path.Path;
import edu.wustl.common.query.impl.CommonPathFinder;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.security.manager.SecurityManagerFactory;
import edu.wustl.security.exception.SMException;
import edu.wustl.security.global.Permissions;
import edu.wustl.common.util.global.Constants;
import gov.nih.nci.security.authorization.domainobjects.Role;

public class FlexInterface
{

	/**
	 * @throws Exception
	 */
	public FlexInterface() throws Exception
	{
		//constructor
	}

	/**
	 * @param nvBeanList
	 * @return
	 */
	private List<String> toStrList(List<NameValueBean> nvBeanList)
	{
		List<String> strList = new ArrayList<String>();
		for (NameValueBean bean : nvBeanList)
		{
			strList.add(bean.getName());
		}
		return strList;
	}

	/**
	 * @return
	 */
	public List<String> getTissueSidePVList()
	{
		List<NameValueBean> aList = CDEManager.getCDEManager().getPermissibleValueList(
				"Tissue Side", null);
		return toStrList(aList);
	}

	/**
	 * @return
	 */
	public List<String> getTissueSitePVList()
	{
		List<NameValueBean> aList = CDEManager.getCDEManager().getPermissibleValueList(
				"Tissue Site", null);
		return toStrList(aList);
	}

	/**
	 * @return
	 */
	public List<String> getPathologicalStatusPVList()
	{
		List<NameValueBean> aList = CDEManager.getCDEManager().getPermissibleValueList(
				"Pathological Status", null);
		return toStrList(aList);
	}

	/**
	 * @return
	 */
	public List<String> getSpecimenClassStatusPVList()
	{
		Map specimenTypeMap = getSpecimenTypeMap();
		Set specimenKeySet = specimenTypeMap.keySet();
		List<NameValueBean> specimenClassList = new ArrayList<NameValueBean>();

		Iterator itr1 = specimenKeySet.iterator();
		while (itr1.hasNext())
		{
			String specimenKey = (String) itr1.next();
			specimenClassList.add(new NameValueBean(specimenKey, specimenKey));
		}
		return toStrList(specimenClassList);
	}

	/**
	 * @return
	 */
	public List<String> getFluidSpecimenTypePVList()
	{
		Map specimenTypeMap = getSpecimenTypeMap();
		List<NameValueBean> aList = (List) specimenTypeMap.get("Fluid");
		return toStrList(aList);
	}

	/**
	 * @return
	 */
	public List<String> getTissueSpecimenTypePVList()
	{
		Map specimenTypeMap = getSpecimenTypeMap();
		List<NameValueBean> aList = (List) specimenTypeMap.get("Tissue");
		return toStrList(aList);
	}

	/**
	 * @return
	 */
	public List<String> getMolecularSpecimenTypePVList()
	{
		Map specimenTypeMap = getSpecimenTypeMap();
		List<NameValueBean> aList = (List<NameValueBean>) specimenTypeMap.get("Molecular");
		return toStrList(aList);
	}

	/**
	 * @return
	 */
	public List<String> getCellSpecimenTypePVList()
	{
		Map specimenTypeMap = getSpecimenTypeMap();
		List<NameValueBean> aList = (List<NameValueBean>) specimenTypeMap.get("Cell");
		return toStrList(aList);
	}

	/**
	 * @return
	 */
	private Map getSpecimenTypeMap()
	{
		CDE specimenClassCDE = CDEManager.getCDEManager().getCDE("Specimen");
		Set setPV = specimenClassCDE.getPermissibleValues();
		Iterator itr = setPV.iterator();

		//List specimenClassList = CDEManager.getCDEManager().getPermissibleValueList("Specimen", null);
		Map<String, List> subTypeMap = new HashMap<String, List>();
		//specimenClassList.add(new NameValueBean(Constants.SELECT_OPTION, "-1"));

		while (itr.hasNext())
		{
			List<NameValueBean> innerList = new ArrayList<NameValueBean>();
			Object obj = itr.next();
			PermissibleValue permissibleValue = (PermissibleValue) obj;

			Set list1 = permissibleValue.getSubPermissibleValues();
			Iterator itr1 = list1.iterator();
			innerList.add(new NameValueBean(Constants.SELECT_OPTION, "-1"));

			while (itr1.hasNext())
			{
				Object obj1 = itr1.next();
				PermissibleValue pv1 = (PermissibleValue) obj1;
				//Setting Specimen Type
				String tmpInnerStr = pv1.getValue();
				innerList.add(new NameValueBean(tmpInnerStr, tmpInnerStr));
			}

			subTypeMap.put(permissibleValue.getValue(), innerList);
		}

		return subTypeMap;
	}

	/**
	 * @return
	 */
	public List getSpecimenTypeStatusPVList()
	{
		return CDEManager.getCDEManager().getPermissibleValueList("Specimen Type", null);
	}

	/**
	 * @return
	 */
	public List getSCGList()
	{
		return null;
	}

	/**
	 * @return
	 * @throws BizLogicException
	 */
	public List getUserList() throws BizLogicException
	{
		UserBizLogic userBizLogic = new UserBizLogic();
		List userList = userBizLogic.getUsers(Constants.ADD);
		return toStrList(userList);

	}

	/**
	 * @return
	 */
	public List getProcedureList()
	{
		List procedureList = CDEManager.getCDEManager().getPermissibleValueList(
				edu.wustl.clinportal.util.global.Constants.CDE_NAME_COLLECTION_PROCEDURE, null);
		return toStrList(procedureList);
	}

	/**
	 * @return
	 */
	public List getContainerList()
	{
		List containerList = CDEManager.getCDEManager().getPermissibleValueList(
				edu.wustl.clinportal.util.global.Constants.CDE_NAME_CONTAINER, null);
		return toStrList(containerList);
	}

	/**
	 * @return
	 */
	public List getReceivedQualityList()
	{
		List qualityList = CDEManager.getCDEManager().getPermissibleValueList(
				edu.wustl.clinportal.util.global.Constants.CDE_NAME_RECEIVED_QUALITY, null);
		return toStrList(qualityList);
	}

	/**
	 * @return
	 */
	public List getBiohazardTypeList()
	{
		List biohazardList = CDEManager.getCDEManager().getPermissibleValueList(
				edu.wustl.clinportal.util.global.Constants.CDE_NAME_BIOHAZARD, null);
		return toStrList(biohazardList);

	}

	//--------------DAG-----------------------------
	/**
	 * 
	 */
	public void restoreQueryObject()
	{
		if (dagPanel == null)
		{
			this.initFlexInterface();
		}
		else
		{
			dagPanel.restoreQueryObject();
		}
	}

	/**
	 * Add Nodes in define Result view
	 * @param nodesStr
	 * @return
	 */
	public DAGNode addNodeToView(String nodesStr)
	{
		return dagPanel.addNodeToOutPutView(nodesStr);
	}

	/**
	 * Repaints DAG
	 * @return
	 */
	public List<DAGNode> repaintDAG()
	{
		return dagPanel.repaintDAG();
	}

	/**
	 * @return
	 */
	/*public int getSearchResult()
	{
		return dagPanel.search();
	}*/

	/**
	 * create DAG Node
	 * @param createQueryObjStr
	 * @param entityName
	 */
	/*public DAGNode createNode(String createQueryObjStr, String entityName)
	{
		return dagPanel.createQueryObject(createQueryObjStr, entityName, "Add");
	}*/

	/**
	 * 
	 * @param expressionId
	 * @return
	 */
	/*public String getLimitUI(int expressionId)
	{
		Map map = dagPanel.editAddLimitUI(expressionId);
		String htmlStr = (String) map.get(DAGConstant.HTML_STR);
		IExpression expression = (IExpression) map.get(DAGConstant.EXPRESSION);
		dagPanel.setExpression(expression);
		return htmlStr;
	}*/

	/**
	 * Edit Node
	 * @param createQueryObjStr
	 * @param entityName
	 * @return
	 */
	/*public DAGNode editNode(String createQueryObjStr, String entityName)
	{
		return dagPanel.createQueryObject(createQueryObjStr, entityName, "Edit");
	}*/

	/**
	 * Deletes node from output view
	 * @param expId
	 */
	public void deleteFromView(int expId)
	{
		dagPanel.deleteExpressionFormView(expId);
	}

	/**
	 * Adds node to output view
	 * @param expId
	 */
	public void addToView(int expId)
	{
		dagPanel.addExpressionToView(expId);
	}

	/**
	 * Deletes node from DAG
	 * @param expId
	 */
	public void deleteNode(int expId)
	{
		dagPanel.deleteExpression(expId);//delete Expression 
	}

	/**
	 * Gets path List between nodes
	 * @param linkedNodeList
	 * @return
	 */
	private List<IPath> getPathList(List<DAGNode> linkedNodeList)
	{
		DAGNode sourceNode = linkedNodeList.get(0);
		DAGNode destinationNode = linkedNodeList.get(1);
		return dagPanel.getPaths(sourceNode, destinationNode);
	}

	/**
	 * Gets association(path) between 2 nodes
	 * @param linkedNodeList
	 * @return
	 */
	public List getpaths(List<DAGNode> linkedNodeList)
	{
		List<IPath> pathsList = getPathList(linkedNodeList);
		List<DAGPath> pathsListStr = new ArrayList<DAGPath>();
		for (int i = 0; i < pathsList.size(); i++)
		{
			Path path = (Path) pathsList.get(i);
			DAGPath dagPath = new DAGPath();
			dagPath.setToolTip(DAGPanel.getPathDisplayString(pathsList.get(i)));
			dagPath.setId(String.valueOf(path.getPathId()));
			pathsListStr.add(dagPath);
		}
		return pathsListStr;
	}

	/**
	 * Links 2 nodes
	 * @param linkedNodeList
	 * @param selectedPaths
	 */

	public List<DAGPath> linkNodes(List<DAGNode> linkedNodeList, List<DAGPath> selectedPaths)
	{
		DAGNode sourceNode = linkedNodeList.get(0);
		DAGNode destinationNode = linkedNodeList.get(1);
		List<IPath> pathsList = getPathList(linkedNodeList);
		List<IPath> selectedList = new ArrayList<IPath>();
		for (int j = 0; j < selectedPaths.size(); j++)
		{
			for (int i = 0; i < pathsList.size(); i++)
			{
				IPath path = pathsList.get(i);
				String pathStr = String.valueOf(path.getPathId());
				DAGPath dagPath = selectedPaths.get(j);
				String pathId = dagPath.getId();
				if (pathStr.equals(pathId))
				{
					selectedList.add(path);
					break;
				}

			}
		}
		return dagPanel.linkNode(sourceNode, destinationNode, selectedList);
	}

	/**
	 * Deletes association between 2 nodes
	 * @param linkedNodeList
	 * @param linkName
	 */
	public void deleteLink(List<DAGNode> linkedNodeList, String linkName)
	{
		dagPanel.deletePath(linkName, linkedNodeList);
	}

	/**
	 * Sets logical operator set from UI
	 * @param node
	 * @param operandIndex
	 * @param operator
	 */
	public void setLogicalOperator(DAGNode node, int operandIndex, String operator)
	{
		int parentExpId = node.getExpressionId();
		dagPanel.updateLogicalOperator(parentExpId, operandIndex, operator);
	}

	/**
	 *Initalize DAG 
	 *
	 */
	public void initFlexInterface()
	{
		//TODO ____ERROR DUE TO CAB2B 
		//queryObject = new ClientQueryBuilder();
		queryObject = null;
		IPathFinder pathFinder = new CommonPathFinder();
		dagPanel = new DAGPanel(pathFinder);
		dagPanel.setQueryObject(queryObject);
	}

	private IClientQueryBuilderInterface queryObject = null;
	private DAGPanel dagPanel = null;
	private HttpSession session = null;
	private HttpServletRequest request = null;

	//----- END DAG ------- 

	/**
	 * This method retrieves the List if all Collection Protocols
	 * @return The cp List.
	 */
	public List getCpList()
	{

		List<CpAndParticipentsBean> cpList = new ArrayList<CpAndParticipentsBean>();

		//Getting the instance of participantRegistrationCacheManager
		ParticipantRegistrationCacheManager partiRegCacheMgr = new ParticipantRegistrationCacheManager();

		//Getting the CP List 
		List cpColl = partiRegCacheMgr.getCSDetailCollection();
		Collections.sort(cpColl);

		//Converting From NameValueBean to CpAndParticipentsBean
		Iterator itr = cpColl.iterator();
		while (itr.hasNext())
		{
			CpAndParticipentsBean cpBean = new CpAndParticipentsBean();
			NameValueBean bean = (NameValueBean) itr.next();
			cpBean.setName(bean.getName());
			cpBean.setValue(bean.getValue());

			//Adding CpAndParticipentsBean to cpList
			cpList.add(cpBean);
		}
		return cpList;
	}

	/**
	 * This method retrieves the List if all Collection Protocols
	 * @return The cp List.
	 */
	public List getCsList()
	{

		List<CpAndParticipentsBean> csList = new ArrayList<CpAndParticipentsBean>();

		//Getting the instance of participantRegistrationCacheManager
		ParticipantRegistrationCacheManager partiRegCacheMgr = new ParticipantRegistrationCacheManager();

		//Getting the CP List 
		List<NameValueBean> csColl = partiRegCacheMgr.getCSDetailCollection();
		Collections.sort(csColl);

		session = flex.messaging.FlexContext.getHttpRequest().getSession();
		SessionDataBean sessionDataBean = (SessionDataBean) session
				.getAttribute(Constants.SESSION_DATA);
		//String userName = sessionDataBean.getUserName();
		try
		{
			Role role = SecurityManagerFactory.getSecurityManager().getUserRole(
					Long.parseLong(sessionDataBean.getCsmUserId()));

			for (NameValueBean bean : csColl)
			{
				CpAndParticipentsBean csBean = new CpAndParticipentsBean();
				/*if (role.getName().equals(edu.wustl.security.global.Constants.ADMINISTRATOR)
						|| SecurityManager.getInstance(this.getClass()).isAuthorized(
								sessionDataBean.getUserName(),
								ClinicalStudy.class.getName() + "_" + bean.getValue(),
								Permissions.PHI))*/
				if (role.getName().equals(edu.wustl.security.global.Constants.ADMINISTRATOR)
						|| ((edu.wustl.clinportal.security.SecurityManager) SecurityManagerFactory
								.getSecurityManager()).isAuthorized(sessionDataBean.getUserName(),
								ClinicalStudy.class.getName() + "_" + bean.getValue(),
								Permissions.PHI))
				{
					csBean.setName(bean.getName());
					csBean.setValue(bean.getValue());
					csList.add(csBean);
				}
			}
		}
		catch (SMException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return csList;
	}

	/**
	 * This method retrieves the List of participants associated with a cp
	 * @param cpId :Collection protocol Id
	 * @return the list of Participants
	 */
	public List getParticipantsList(String csId, String cpTitle)
	{
		//Setting the cp title in session
		session = flex.messaging.FlexContext.getHttpRequest().getSession();
		session.setAttribute("cpTitle", cpTitle);
		List<CpAndParticipentsBean> participantsList = new ArrayList<CpAndParticipentsBean>();
		List<NameValueBean> partiBeanList = new ArrayList<NameValueBean>();

		//Getting the instance of participantRegistrationCacheManager
		ParticipantRegistrationCacheManager partiRegCacheMgr = new ParticipantRegistrationCacheManager();

		//getting the list of participants from cache for particular CP.
		List<String> partiNamesWithId = partiRegCacheMgr.getParticipantNames(Long.parseLong(csId));

		//Values in participantNamesWithID will be in format (ID:lastName firstName) 
		//tokenizer the value and create nameValueBean with name as (lastName firstName) and value as participantId 
		//and store in the list

		// if (participantNamesWithId != null && participantNamesWithId.size() > 0)
		if (partiNamesWithId != null && !partiNamesWithId.isEmpty())
		{
			for (String participantIdAndName : partiNamesWithId)
			{
				int index = participantIdAndName.indexOf(":");
				Long participantId = Long.valueOf(participantIdAndName.substring(0, index));
				String name = participantIdAndName.substring(index + 1);
				partiBeanList.add(new NameValueBean(name, participantId.toString()));
			}
		}

		//Sorting the participants
		Collections.sort(partiBeanList);
		copy(partiBeanList, participantsList);
		return participantsList;
	}

	/**
	 * method copies the one NameValueBean list to other CpAndParticipentsBean list
	 * @param partpantBeanLst
	 * @param participantsList
	 */
	private void copy(List<NameValueBean> partpantBeanLst,
			List<CpAndParticipentsBean> participantsList)
	{
		for (NameValueBean bean : partpantBeanLst)
		{
			participantsList.add(new CpAndParticipentsBean(bean.getName(), bean.getValue()));
		}
	}

	/**
	 * This method returns the XML String for generating tree 
	 * @param cpId : Selected Collection Protocol ID
	 * @param pId : Selected Participant Id
	 * @return : The XML String for tree data 
	 * @throws Exception
	 */
	public String getTreeData(String csId, String pId, String nodeId) throws Exception
	{
		request = flex.messaging.FlexContext.getHttpRequest();
		EventTreeBizLogic bizlogic = new EventTreeBizLogic();
		return bizlogic.getCSEvents(Long.parseLong(csId), Long.parseLong(pId), nodeId, request);
	}

}