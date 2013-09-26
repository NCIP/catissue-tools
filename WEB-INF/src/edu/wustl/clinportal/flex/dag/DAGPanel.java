/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */


package edu.wustl.clinportal.flex.dag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.cab2b.client.ui.dag.PathLink;
import edu.wustl.cab2b.client.ui.dag.ambiguityresolver.AmbiguityObject;
import edu.wustl.cab2b.client.ui.query.IClientQueryBuilderInterface;
import edu.wustl.cab2b.client.ui.query.IPathFinder;
import edu.wustl.cab2b.server.cache.EntityCache;
//import edu.wustl.clinportal.util.querysuite.QueryModuleUtil;
import edu.wustl.clinportal.util.querysuite.QueryModuleError;
import edu.wustl.common.query.queryobject.locator.Position;
import edu.wustl.common.query.queryobject.locator.QueryNodeLocator;
import edu.wustl.common.querysuite.exceptions.CyclicException;
import edu.wustl.common.querysuite.factory.QueryObjectFactory;
import edu.wustl.common.querysuite.metadata.associations.IAssociation;
import edu.wustl.common.querysuite.metadata.associations.IIntraModelAssociation;
import edu.wustl.common.querysuite.metadata.path.IPath;
import edu.wustl.common.querysuite.queryobject.IConstraints;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IJoinGraph;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.common.querysuite.queryobject.IQueryEntity;
import edu.wustl.common.querysuite.queryobject.LogicalOperator;
import edu.wustl.common.querysuite.queryobject.impl.Expression;
import edu.wustl.common.querysuite.queryobject.impl.JoinGraph;
import edu.wustl.common.util.logger.Logger;

/**
 *The class is responsible controlling all activities of Flex DAG
 *  
 *@author rukhsana sameer
 */

public class DAGPanel
{

	private IClientQueryBuilderInterface m_queryObject;
	private IPathFinder m_pathFinder;
	private IExpression expression;
	private HashMap<String, IPath> m_pathMap = new HashMap<String, IPath>();
	private Map<Integer, Position> positionMap;

	/**
	 * @param pathFinder
	 */
	public DAGPanel(IPathFinder pathFinder)
	{
		m_pathFinder = pathFinder;
	}

	/**
	 * 
	 * @param expressionId
	 * @param isOutputView
	 * @return
	 */
	private DAGNode createNode(int expressionId, boolean isOutputView)
	{
		IExpression expression = m_queryObject.getQuery().getConstraints().getExpression(
				expressionId);
		IQueryEntity constraintEntity = expression.getQueryEntity();
		DAGNode dagNode = new DAGNode();
		dagNode.setNodeName(edu.wustl.cab2b.common.util.Utility.getOnlyEntityName(constraintEntity
				.getDynamicExtensionsEntity()));
		dagNode.setExpressionId(expression.getExpressionId());
		if (isOutputView)
		{
			dagNode.setNodeType(DAGConstant.VIEW_ONLY_NODE);
		}
		else
		{
			dagNode.setToolTip(expression);
		}
		return dagNode;
	}

	/**
	 * 
	 * @param strToCreteQryObj
	 * @param entityName
	 * @param mode
	 * @return
	 */
	@SuppressWarnings("unchecked")
//	public DAGNode createQueryObject(String strToCreteQryObj, String entityName, String mode)
//	{
//		Map ruleDetailsMap = null;
//		int expressionId;
//		DAGNode node = null;
//
//		HttpServletRequest request = flex.messaging.FlexContext.getHttpRequest();
//		HttpSession session = request.getSession();
//
//		IQuery query = (IQuery) session.getAttribute(DAGConstant.QUERY_OBJECT);// Get existing Query object from server  
//
//		if (query != null)
//		{
//			m_queryObject.setQuery(query);
//		}
//		else
//		{
//			query = m_queryObject.getQuery();
//		}
//		session.setAttribute(DAGConstant.QUERY_OBJECT, query);
//
//		try
//		{
//			Long entityId = Long.parseLong(entityName);
//			EntityInterface entity = EntityCache.getCache().getEntityById(entityId);
//
//			CreateQueryObjectBizLogic queryBizLogic = new CreateQueryObjectBizLogic();
//			if (!strToCreteQryObj.equalsIgnoreCase(""))
//			{
//				ruleDetailsMap = queryBizLogic.getRuleDetailsMap(strToCreteQryObj, entity
//						.getEntityAttributesForQuery());
//				List<AttributeInterface> attributes = (List<AttributeInterface>) ruleDetailsMap
//						.get(AppletConstants.ATTRIBUTES);
//				List<String> attriOperators = (List<String>) ruleDetailsMap
//						.get(AppletConstants.ATTRIBUTE_OPERATORS);
//				List<List<String>> conditionValues = (List<List<String>>) ruleDetailsMap
//						.get(AppletConstants.ATTR_VALUES);
//				String errMsg = (String) ruleDetailsMap.get(AppletConstants.ERROR_MESSAGE);
//				if (errMsg.equals(""))
//				{
//					if (mode.equals("Edit"))
//					{
//						Rule rule = ((Rule) (expression.getOperand(0)));
//						rule.removeAllConditions();
//						
//						/* //TODO ____ERROR DUE TO CAB2B 
//						 * List<ICondition> conditionsList = ((ClientQueryBuilder) m_queryObject)
//								.getConditions(attributes, attriOperators, conditionValues);*/
//						List<ICondition> conditionsList = new ArrayList<ICondition>();
//						for (ICondition condition : conditionsList)
//						{
//							rule.addCondition(condition);
//						}
//						expressionId = expression.getExpressionId();
//						node = createNode(expressionId, false);
//					}
//					else
//					{
//						expressionId = m_queryObject.addRule(attributes, attriOperators,
//								conditionValues, entity);
//						node = createNode(expressionId, false);
//					}
//					node.setErrorMsg(errMsg);
//				}
//				else
//				{
//					node = new DAGNode();
//					node.setErrorMsg(errMsg);
//				}
//
//			}
//		}
//		catch (DynamicExtensionsSystemException e)
//		{
//			e.printStackTrace();
//		}
//		catch (DynamicExtensionsApplicationException e)
//		{
//			e.printStackTrace();
//		}
//		return node;
//	}

	/**
	 * Sets ClientQueryBuilder object
	 * @param queryObject
	 */
	public void setQueryObject(IClientQueryBuilderInterface queryObject)
	{
		m_queryObject = queryObject;
	}

	/**
	 * Sets Expression
	 * @param expression
	 */
	public void setExpression(IExpression expression)
	{
		this.expression = expression;
	}

	/**
	 * Links two nodes
	 * @param sourceNode
	 * @param destNode
	 * @param paths
	 */
	public List<DAGPath> linkNode(final DAGNode sourceNode, final DAGNode destNode,
			List<IPath> paths)
	{

		List<DAGPath> dagPathList = null;
		if (paths != null && !paths.isEmpty())
		{
			dagPathList = new ArrayList<DAGPath>();
			int sourceExpresnId = sourceNode.getExpressionId();
			int destExpressionId = destNode.getExpressionId();
			if (!m_queryObject.isPathCreatesCyclicGraph(sourceExpresnId, destExpressionId, paths
					.get(0)))
			{
				for (int i = 0; i < paths.size(); i++)
				{
					IPath path = paths.get(i);
					linkTwoNode(sourceNode, destNode, paths.get(i), new ArrayList());
					String pathStr = String.valueOf(path.getPathId());
					DAGPath dagPath = new DAGPath();
					dagPath.setToolTip(getPathDisplayString(path));
					dagPath.setId(pathStr);
					dagPath.setSourceExpId(sourceNode.getExpressionId());
					dagPath.setDestinationExpId(destNode.getExpressionId());
					dagPathList.add(dagPath);
					String key = pathStr + "_" + sourceNode.getExpressionId() + "_"
							+ destNode.getExpressionId();
					m_pathMap.put(key, path);
				}
			}
		}
		return dagPathList;
	}

	/**
	 * Gets list of paths between two nodes
	 * @param sourceNode
	 * @param destNode
	 * @return
	 */
	public List<IPath> getPaths(DAGNode sourceNode, DAGNode destNode)
	{
		Map<AmbiguityObject, List<IPath>> map = null;
		AmbiguityObject ambiguityObject = null;
		try
		{
			IQuery query = m_queryObject.getQuery();
			IConstraints constraints = query.getConstraints();
			int expressionId = sourceNode.getExpressionId();
			IExpression expression = constraints.getExpression(expressionId);
			IQueryEntity sourceEntity = expression.getQueryEntity();
			expressionId = destNode.getExpressionId();
			expression = constraints.getExpression(expressionId);
			IQueryEntity destinationEntity = expression.getQueryEntity();

			ambiguityObject = new AmbiguityObject(sourceEntity.getDynamicExtensionsEntity(),
					destinationEntity.getDynamicExtensionsEntity());
			//			ResolveAmbiguity resolveAmbigity = new ResolveAmbiguity(
			//			ambiguityObject, m_pathFinder);
			DAGResolveAmbiguity resolveAmbigity = new DAGResolveAmbiguity(ambiguityObject,
					m_pathFinder);
			map = resolveAmbigity.getPathsForAllAmbiguities();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return map.get(ambiguityObject);
	}

	/**
	 * Link 2 nodes
	 * @param sourceNode
	 * @param destNode
	 * @param path
	 * @param intermediteExprns
	 */
	private void linkTwoNode(final DAGNode sourceNode, final DAGNode destNode, final IPath path,
			List<Integer> intermediteExprns)
	{

		try
		{
			int srceexpresnId = sourceNode.getExpressionId();
			int destexpressionId = destNode.getExpressionId();
			intermediteExprns = m_queryObject.addPath(srceexpresnId, destexpressionId, path);
			//DAGPath dag path  
			PathLink link = new PathLink();
			link.setAssociationExpressions(intermediteExprns);
			link.setDestinationExpressionId(destexpressionId);
			link.setSourceExpressionId(srceexpresnId);
			link.setPath(path);
			updateQueryObject(link, sourceNode, destNode);
		}
		catch (CyclicException e)
		{
			e.printStackTrace();

		}
	}

	/**
	 * Updates query object
	 * @param link
	 * @param sourceNode
	 * @param destNode
	 */
	private void updateQueryObject(PathLink link, DAGNode sourceNode, DAGNode destNode)
	{
		//TODO required to modify code logic will not work for multiple association
		int srceexpresnId = sourceNode.getExpressionId();

		// If the first association is added, put operator between attribute condition and association
		String operator = null;

		operator = sourceNode.getOperatorBetweenAttrAndAssociation();

		// Get the expressionId between which to add logical operator
		int destId = link.getLogicalConnectorExpressionId();

		m_queryObject.setLogicalConnector(srceexpresnId, destId,
				edu.wustl.cab2b.client.ui.query.Utility.getLogicalOperator(operator), false);

	}

	/**
	 * Gets display path string
	 * @param path
	 * @return
	 */
	public static String getPathDisplayString(IPath path)
	{

		String text = "";
		List<IAssociation> pathList = path.getIntermediateAssociations();
		text = text.concat(edu.wustl.cab2b.common.util.Utility.getOnlyEntityName(path
				.getSourceEntity()));
		for (int i = 0; i < pathList.size(); i++)
		{
			text = text.concat(">>");
			IAssociation association = pathList.get(i);
			if (association instanceof IIntraModelAssociation)
			{
				IIntraModelAssociation iAssociation = (IIntraModelAssociation) association;
				AssociationInterface dyExtensAssociatn = iAssociation
						.getDynamicExtensionsAssociation();
				String role = "(" + dyExtensAssociatn.getTargetRole().getName() + ")";
				text = text.concat(role + ">>");
			}
			text = text.concat(edu.wustl.cab2b.common.util.Utility.getOnlyEntityName(association
					.getTargetEntity()));
		}
		Logger.out.debug(text);
		return text;

	}

	/**
	 * Generates sql query
	 * @return
	 */
//	public int search()
//	{
//		QueryModuleError status = QueryModuleError.SUCCESS;
//		IQuery query = m_queryObject.getQuery();
//		HttpServletRequest request = flex.messaging.FlexContext.getHttpRequest();
//		boolean isRulePresInDag = QueryModuleUtil.checkIfRulePresentInDag(query);
//		if (isRulePresInDag)
//		{
//			status = QueryModuleUtil.searchQuery(request, query, null);
//		}
//		else
//		{
//			status = QueryModuleError.EMPTY_DAG;
//		}
//		return status.getErrorCode();
//	}

	/**
	 * Repaints DAG
	 * @return
	 */
	public List<DAGNode> repaintDAG()
	{
		List<DAGNode> nodeList = new ArrayList<DAGNode>();
		HttpServletRequest request = flex.messaging.FlexContext.getHttpRequest();
		HttpSession session = request.getSession();
		IQuery query = (IQuery) session.getAttribute(DAGConstant.QUERY_OBJECT);
		m_queryObject.setQuery(query);
		IConstraints constraints = query.getConstraints();

		positionMap = new QueryNodeLocator(400, query).getPositionMap();

		HashSet<Integer> visibleExpression = new HashSet<Integer>();
		for (IExpression expression : constraints)
		{
			if (expression.isVisible())
			{
				visibleExpression.add(Integer.valueOf(expression.getExpressionId()));
			}
		}
		for (Integer expressionId : visibleExpression)
		{

			IExpression exp = constraints.getExpression(expressionId);

			IQueryEntity constraintEntity = exp.getQueryEntity();
			String nodeDisplayName = edu.wustl.cab2b.common.util.Utility
					.getOnlyEntityName(constraintEntity.getDynamicExtensionsEntity());
			DAGNode dagNode = new DAGNode();
			dagNode.setExpressionId(exp.getExpressionId());
			dagNode.setNodeName(nodeDisplayName);
			dagNode.setToolTip(exp);
			Position position = positionMap.get(exp.getExpressionId());
			if (position != null)
			{
				dagNode.setX(position.getX());
				dagNode.setY(position.getY());
			}
			if (!exp.containsRule())
			{
				dagNode.setNodeType(DAGConstant.VIEW_ONLY_NODE);
			}
			if (!exp.isInView())
			{
				dagNode.setNodeType(DAGConstant.CONSTRAINT_ONLY_NODE);
			}

			nodeform(expressionId, dagNode, constraints, new ArrayList<IIntraModelAssociation>());

			int numOperands = exp.numberOfOperands();
			int numOperator = numOperands - 1;
			for (int i = 0; i < numOperator; i++)
			{
				String operator = exp.getConnector(i, i + 1).getOperator().toString();
				dagNode.setOperatorList(operator.toUpperCase());
			}

			nodeList.add(dagNode);

		}
		return nodeList;

	}

	/**
	 * 
	 * @param expressionId
	 * @param node
	 * @param constraints
	 * @param path
	 */
	private void nodeform(int expressionId, DAGNode node, IConstraints constraints,
			List<IIntraModelAssociation> intraModAstionLst)
	{
		IJoinGraph graph = constraints.getJoinGraph();
		IExpression expression = constraints.getExpression(expressionId);

		List<IExpression> childList = graph.getChildrenList(expression);

		for (IExpression exp : childList)
		{

			IQueryEntity constraintEntity = exp.getQueryEntity();
			/*	Code to get IPath Object*/
			IIntraModelAssociation association = (IIntraModelAssociation) (graph.getAssociation(
					expression, exp));

			intraModAstionLst.add(association);

			if (exp.isVisible())
			{
				IPath pathObj = (IPath) m_pathFinder.getPathForAssociations(intraModAstionLst);
				long pathId = pathObj.getPathId();

				DAGNode dagNode = new DAGNode();
				dagNode.setExpressionId(exp.getExpressionId());
				dagNode.setNodeName(edu.wustl.cab2b.common.util.Utility
						.getOnlyEntityName(constraintEntity.getDynamicExtensionsEntity()));
				dagNode.setToolTip(exp);

				/*	Adding Dag Path in each visible list which have children*/
				String pathStr = String.valueOf(pathId);
				DAGPath dagPath = new DAGPath();
				dagPath.setToolTip(getPathDisplayString(pathObj));
				dagPath.setId(pathStr);
				dagPath.setSourceExpId(node.getExpressionId());
				dagPath.setDestinationExpId(dagNode.getExpressionId());

				String key = pathStr + "_" + node.getExpressionId() + "_"
						+ dagNode.getExpressionId();
				m_pathMap.put(key, pathObj);

				node.setDagpathList(dagPath);
				node.setAssociationList(dagNode);
				intraModAstionLst.clear();

			}
			else
			{
				nodeform(exp.getExpressionId(), node, constraints, intraModAstionLst);
			}
		}
	}

	/**
	 * 
	 * @param parentExpId
	 * @param parentIndex
	 * @param operator
	 */

	public void updateLogicalOperator(int parentExpId, int parentIndex, String operator)
	{
		int parentExprenId = parentExpId;
		IQuery query = m_queryObject.getQuery();
		IExpression parentExpression = query.getConstraints().getExpression(parentExprenId);
		LogicalOperator logicOperator = edu.wustl.cab2b.client.ui.query.Utility
				.getLogicalOperator(operator);
		int childIndex = parentIndex + 1;
		parentExpression.setConnector(parentIndex, childIndex, QueryObjectFactory
				.createLogicalConnector(logicOperator));
		m_queryObject.setQuery(query);

	}

	/**
	 * 
	 * @param expId
	 * @return
	 */
//	public Map editAddLimitUI(int expId)
//	{
//		Map<String, Object> map = new HashMap<String, Object>();
//		int expressionId = expId;
//		IExpression expression = m_queryObject.getQuery().getConstraints().getExpression(
//				expressionId);
//		EntityInterface entity = expression.getQueryEntity().getDynamicExtensionsEntity();
//		GenerateHtmlForAddLimitsBizLogic gnertHTMLBLogic = new GenerateHtmlForAddLimitsBizLogic();
//		Rule rule = ((Rule) (expression.getOperand(0)));
//		List<ICondition> conditions = Collections.list(rule);
//		String html = gnertHTMLBLogic.generateHTML(entity, conditions);
//		map.put(DAGConstant.HTML_STR, html);
//		map.put(DAGConstant.EXPRESSION, expression);
//		return map;
//	}

	/**
	 * 
	 * @param nodesStr
	 * @return
	 */
	public DAGNode addNodeToOutPutView(String identifier)
	{
		DAGNode node = null;
		if (!identifier.equalsIgnoreCase(""))
		{
			Long entityId = Long.parseLong(identifier);
			EntityInterface entity = EntityCache.getCache().getEntityById(entityId);
			//TODO ____ERROR DUE TO CAB2B 
			/* 
			 * int expressionId = ((ClientQueryBuilder) m_queryObject).addExpression(entity);
			node = createNode(expressionId, true);*/
		}
		return node;
	}

	/**
	 * 
	 *
	 */
	public void restoreQueryObject()
	{
		HttpServletRequest request = flex.messaging.FlexContext.getHttpRequest();
		HttpSession session = request.getSession();
		IQuery query = (IQuery) session.getAttribute(DAGConstant.QUERY_OBJECT);
		m_queryObject.setQuery(query);
	}

	/**
	 * 
	 * @param expId
	 */
	public void deleteExpression(int expId)
	{
		int expressionId = expId;
		m_queryObject.removeExpression(expressionId);
	}

	/**
	 * 
	 * @param expId
	 */
	public void addExpressionToView(int expId)
	{
		int expressionId = expId;
		Expression expression = (Expression) m_queryObject.getQuery().getConstraints()
				.getExpression(expressionId);
		expression.setInView(true);
	}

	/**
	 * 
	 * @param expId
	 */
	public void deleteExpressionFormView(int expId)
	{
		int expressionId = expId;
		Expression expression = (Expression) m_queryObject.getQuery().getConstraints()
				.getExpression(expressionId);
		expression.setInView(false);
	}

	/**
	 * 
	 * @param path
	 * @param linkedNodeList
	 */
	public void deletePath(String pathName, List<DAGNode> linkedNodeList)
	{
		IPath path = m_pathMap.remove(pathName);
		int sourceexpresnId = linkedNodeList.get(0).getExpressionId();
		int destexpressionId = linkedNodeList.get(1).getExpressionId();
		List<IAssociation> associations = path.getIntermediateAssociations();
		IConstraints constraints = m_queryObject.getQuery().getConstraints();
		JoinGraph graph = (JoinGraph) constraints.getJoinGraph();
		IExpression srcExpression = constraints.getExpression(sourceexpresnId);
		IExpression destExpression = constraints.getExpression(destexpressionId);
		List<IExpression> expressions = graph.getIntermediateExpressions(srcExpression,
				destExpression, associations);
		// If the association is direct association, remove the respective association 
		if (0 == expressions.size())
		{
			m_queryObject.removeAssociation(sourceexpresnId, destexpressionId);
		}
		else
		{
			for (int i = 0; i < expressions.size(); i++)
			{
				m_queryObject.removeExpression(expression.getExpressionId());
			}
		}

	}

}
