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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

import edu.wustl.cab2b.client.ui.util.ClientConstants;
import edu.wustl.cab2b.common.util.Utility;
import edu.wustl.common.querysuite.queryobject.ICondition;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IRule;
import edu.wustl.common.querysuite.queryobject.RelationalOperator;

/**
 * @author falguni_sachde
 *
 */
public class DAGNode implements Externalizable, Comparable<DAGNode>
{

	private String nodeName = "";
	private int expressionId = 0;
	private String toolTip = "";
	private String operatorBetweenAttrAndAssociation = "";
	private String nodeType = DAGConstant.CONSTRAINT_VIEW_NODE;
	public List<DAGNode> associationList = new ArrayList<DAGNode>();
	public List<String> operatorList = new ArrayList<String>();
	//private List<String> pathList  = new ArrayList<String>();
	public List<DAGPath> dagpathList = new ArrayList<DAGPath>();
	private String errorMsg = "";

	private int x;
	private int y;

	/**
	 * @return
	 */
	public int getX()
	{
		return x;
	}

	/**
	 * @param xValue
	 */
	public void setX(int xValue)
	{
		this.x = xValue;
	}

	/**
	 * @return
	 */
	public int getY()
	{
		return y;
	}

	/**
	 * @param yValue
	 */
	public void setY(int yValue)
	{
		this.y = yValue;
	}

	/**
	 * 
	 */
	public DAGNode()
	{
		//setOperatorBetweenAttrAndAssociation(ClientConstants.OPERATOR_AND); commented to remove PMD error
		this.operatorBetweenAttrAndAssociation = ClientConstants.OPERATOR_AND;
	}

	/**
	 * @return
	 */
	public String getNodeName()
	{
		return nodeName;
	}

	/**
	 * @param nodeName
	 */
	public void setNodeName(String nodeName)
	{
		this.nodeName = nodeName;
	}

	/**
	 * @param expression
	 *            Expression to set
	 */
	public void setExpressionId(int expressionId)
	{
		this.expressionId = expressionId;

	}

	/**
	 * @return
	 */
	public int getExpressionId()
	{
		return expressionId;
	}

	/**
	 * @param expression
	 */
	public void setToolTip(IExpression expression)
	{
		StringBuffer sbuffer = new StringBuffer();
		IRule rule = null;
		if (!((IExpression) expression).containsRule())
		{
			return;
		}
		else
		{
			rule = (IRule) expression.getOperand(0);
		}
		int totalConditions = rule.size();

		sbuffer.append("Condition(s) on  ").append("\n");
		for (int i = 0; i < totalConditions; i++)
		{
			ICondition condition = rule.getCondition(i);
			sbuffer.append((i + 1)).append(") ");
			String formatdAttName = Utility.getFormattedString(condition.getAttribute()
					.getName());
			sbuffer.append(formatdAttName).append(" ");
			List<String> values = condition.getValues();
			RelationalOperator operator = condition.getRelationalOperator();
			 sbuffer.append(operator.getStringRepresentation()).append(" ");
			int size = values.size();
			if (size > 0)// Special case for 'Not Equals and Equals
			{
				if (size == 1)
				{
					sbuffer.append(values.get(0));
				}
				else
				{
					sbuffer.append("(");
					if (values.get(0).indexOf(",") != -1)
					{
						sbuffer.append("\"");
						sbuffer.append(values.get(0));
						sbuffer.append("\"");
					}
					else
					{
						sbuffer.append(values.get(0));
					}
					for (int j = 1; j < size; j++)
					{
						sbuffer.append(", ");
						if (values.get(j).indexOf(",") != -1)
						{
							sbuffer.append("\"");
							sbuffer.append(values.get(j));
							sbuffer.append("\"");
						}
						else
						{
							sbuffer.append(values.get(j));
						}
					}
					sbuffer.append(")");
				}
			}
			sbuffer.append("\n");
		}
		this.toolTip = sbuffer.toString();
	}

	/**
	 * @return
	 */
	public String getToolTip()
	{
		return toolTip;
	}

	/**
	 * @return
	 */
	public String getOperatorBetweenAttrAndAssociation()
	{
		return operatorBetweenAttrAndAssociation;
	}

	/**
	 * @param optBtnAttrAndAssn
	 */
	public void setOperatorBetweenAttrAndAssociation(String optBtnAttrAndAssn)
	{
		this.operatorBetweenAttrAndAssociation = optBtnAttrAndAssn;
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	public void readExternal(ObjectInput inObj) throws IOException, ClassNotFoundException
	{
		// TODO Auto-generated method stub
		nodeName = inObj.readUTF();
		toolTip = inObj.readUTF();
		expressionId = inObj.readInt();
		operatorBetweenAttrAndAssociation = inObj.readUTF();
		nodeType = inObj.readUTF();
		associationList = (List<DAGNode>) inObj.readObject();
		operatorList = (List<String>) inObj.readObject();
		//	pathList = (List<String>) in.readObject();
		dagpathList = (List<DAGPath>) inObj.readObject();
		errorMsg = inObj.readUTF();
		x = inObj.readInt();
		y = inObj.readInt();
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	public void writeExternal(ObjectOutput out) throws IOException
	{
		// TODO Auto-generated method stub
		out.writeUTF(nodeName);
		out.writeUTF(toolTip);
		out.writeInt(expressionId);
		out.writeUTF(operatorBetweenAttrAndAssociation);
		out.writeUTF(nodeType);
		out.writeObject(associationList);
		out.writeObject(operatorList);
		//out.writeObject(pathList);
		out.writeObject(dagpathList);
		out.writeUTF(errorMsg);
		out.writeInt(x);
		out.writeInt(y);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringBuffer buff = new StringBuffer("");
		buff.append("\n nodeName: ").append(nodeName).append("\n toolTip: ").append(toolTip)
				.append("\n expressionId: ").append(expressionId).append(
						"\n operatorBetweenAttrAndAssociation:").append(
						operatorBetweenAttrAndAssociation);
		return buff.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(DAGNode node)
	{
		// TODO Auto-generated method stub

		return 0;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj)
	{
		DAGNode node = (DAGNode) obj;
		boolean equal = false;
		if (((DAGNode) this).expressionId == node.expressionId)
		{
			equal = true;
		}
		return equal;
	}

	/**
	 * @return
	 */
	public List<DAGNode> getAssociationList()
	{
		return associationList;
	}

	/**
	 * @param node
	 */
	public void setAssociationList(DAGNode node)
	{
		this.associationList.add(node);
	}

	/**
	 * @return
	 */
	public List<String> getOperatorList()
	{
		return operatorList;
	}

	/**
	 * @param operator
	 */
	public void setOperatorList(String operator)
	{
		this.operatorList.add(operator);
	}

	/**
	 * @return
	 */
	public String getNodeType()
	{
		return nodeType;
	}

	/**
	 * @param nodeType
	 */
	public void setNodeType(String nodeType)
	{
		this.nodeType = nodeType;
	}

	/**
	 * @return
	 */
	public List<DAGPath> getDagpathList()
	{
		return dagpathList;
	}

	/**
	 * @param dagpath
	 */
	public void setDagpathList(DAGPath dagpath)
	{
		this.dagpathList.add(dagpath);
	}

	/**
	 * @return
	 */
	public String getErrorMsg()
	{
		return errorMsg;
	}

	/**
	 * @param errorMsg
	 */
	public void setErrorMsg(String errorMsg)
	{
		this.errorMsg = errorMsg;
	}
}