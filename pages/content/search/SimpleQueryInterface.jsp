<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/nlevelcombo.tld" prefix="ncombo" %>
<%@ page import="edu.wustl.clinportal.util.global.Constants,edu.wustl.simplequery.actionForm.SimpleQueryInterfaceForm,java.util.List,edu.wustl.common.beans.NameValueBean"%>
<%@ page import="edu.wustl.simplequery.query.Operator"%>
<%@ page import="edu.wustl.clinportal.util.global.Utility"%>
<%@ page import="edu.wustl.common.util.global.ApplicationProperties"%>

<%@ page import="java.util.List,java.util.StringTokenizer"%>

<head>
<style>
.hideTD
{
	display:none;
}
</style>
<!-- Mandar : 434 : for tooltip -->
<script language="JavaScript" type="text/javascript" src="jss/javaScript.js"></script>
<script language="JavaScript" type="text/javascript" src="jss/scwcalendar.js"></script>
</head>
<%
	String[] attributeConditionArray = (String[])request.getAttribute(edu.wustl.simplequery.global.Constants.ATTRIBUTE_CONDITION_LIST);
	String aliasName = (String)request.getAttribute(edu.wustl.common.util.global.Constants.TABLE_ALIAS_NAME);
	String noOfRows="1";
	String showCal = "";
	String dateClass = "hideTD";
	Object obj = request.getAttribute("simpleQueryInterfaceForm");
	String pageOf = (String)request.getAttribute(Constants.PAGEOF);
	
	String selectMenu = (String) request.getAttribute(edu.wustl.common.util.global.Constants.MENU_SELECTED);
	String objectChanged="";
	if(obj != null && obj instanceof SimpleQueryInterfaceForm)
	{
		SimpleQueryInterfaceForm form = (SimpleQueryInterfaceForm)obj;
		noOfRows = form.getCounter();		
		objectChanged = request.getParameter("objectChanged");
       	
		if(objectChanged != null && !objectChanged.equals(""))
		{
			form.setValue("SimpleConditionsNode:"+Integer.parseInt(noOfRows)+"_Condition_DataElement_field",null);		
		}		
	}
        
	String title = (String)request.getAttribute(Constants.SIMPLE_QUERY_INTERFACE_TITLE);
	String header;	
	String alias = (String)request.getParameter("aliasName");
	if(pageOf.equals(edu.wustl.simplequery.global.Constants.PAGEOF_SIMPLE_QUERY_INTERFACE))
	{
		title="simpleQuery.title";
		header = "SimpleQuery.header";
	}
	else
	{
		header = alias + ".header";
		title=alias+".Search";
	}	
	
%>
<script>

function callAction(action)
{
	document.forms[0].action = action;
	document.forms[0].submit();
}

function onObjectChange(element,action)
{
	var index = element.name.indexOf("(");
	var lastIndex = element.name.lastIndexOf(")");
	
	var saveObject = document.getElementById("objectChanged");	
	saveObject.value = element.name.substring(index+1,lastIndex);
	
	callAction(action);
}

function setPropertyValue(propertyName, value)
{
	for (var i=0;i < document.forms[0].elements.length;i++)
    {
    	var e = document.forms[0].elements[i];
        if (e.name == propertyName)
        {
        	document.forms[0].elements[i].value = value;
        }
    }
}
function incrementCounter()
{
	document.forms[0].counter.value = parseInt(document.forms[0].counter.value) + 1;
}
function decrementCounter()
{
	document.forms[0].counter.value = parseInt(document.forms[0].counter.value) - 1;
} 

function showDateColumn(element,valueField,columnID,showCalendarID,fieldValue,overDiv)
{
	var dataStr = element.options[element.selectedIndex].value;
	var dataValue = new String(dataStr);
	var lastInd = dataValue.lastIndexOf(".");
	if(lastInd == -1)
		return;
	else
	{
		var dataType = dataValue.substr(lastInd+1);

		var txtField = document.getElementById(valueField);
		txtField.value="";

		var calendarShow = document.getElementById(showCalendarID);

		if (dataType == "<%=edu.wustl.simplequery.global.Constants.FIELD_TYPE_DATE%>" || dataType == "<%=edu.wustl.simplequery.global.Constants.FIELD_TYPE_TIMESTAMP_DATE%>")
		{
			var td = document.getElementById(columnID);
			txtField.readOnly="";
			calendarShow.value = "Show";
			var innerStr = "<div id='"+ overDiv +"' style='position:absolute; visibility:hidden; z-index:1000;'></div>";
			//innerStr = innerStr + "<a href=\"javascript:show_calendar('"+fieldValue+"',null,null,'MM-DD-YYYY');\">";
			//innerStr = innerStr + "<img src=\"images//calendar.gif\" width=24 height=22 border=0>";
			//innerStr = innerStr + "</a>";

			innerStr = innerStr + "<img src=\"images//calendar.gif\" width=24 height=22 border=0 onclick='scwShow("+ fieldValue + ",event);'>";

			td.innerHTML = innerStr;
		}
		else
		{
			var td = document.getElementById(columnID);
			td.innerHTML = "&nbsp;";
			txtField.readOnly="";
			calendarShow.value = "";
		}	
	}	
}

function onAttributeChange(element,opComboName,txtFieldID)
{
	var columnValue = element.options[element.selectedIndex].value;
	var index = columnValue.lastIndexOf(".");
	
	var opCombo = document.getElementById(opComboName);
	opCombo.options.length=0;

	var txtField = document.getElementById(txtFieldID);
	txtField.disabled = false;
	
	if(element.value == "<%=Constants.SELECT_OPTION%>")
	{
		opCombo.options[0] = new Option("<%=Constants.SELECT_OPTION%>","-1");
	}
	else
	{
		//If the datatype of selected column "varchar" or "text"
		if(columnValue.match("varchar") == "varchar" || columnValue.match("text") == "text")
		{
			opCombo.options[0] = new Option("<%=Operator.STARTS_WITH%>","<%=Operator.STARTS_WITH%>");
			opCombo.options[1] = new Option("<%=Operator.ENDS_WITH%>","<%=Operator.ENDS_WITH%>");
			opCombo.options[2] = new Option("<%=Operator.CONTAINS%>","<%=Operator.CONTAINS%>");
			opCombo.options[3] = new Option("Equals","<%=Operator.EQUAL%>");
			opCombo.options[4] = new Option("Not Equals","<%=Operator.NOT_EQUALS%>");
		}
		else if (columnValue.match("<%=edu.wustl.simplequery.global.Constants.FIELD_TYPE_TINY_INT%>") == "<%=edu.wustl.simplequery.global.Constants.FIELD_TYPE_TINY_INT%>")
		{
			opCombo.options[0] = new Option("Equals","<%=Operator.EQUAL%>");
			opCombo.options[1] = new Option("Not Equals","<%=Operator.NOT_EQUALS%>");
		}
		else
		{
			opCombo.options[0] = new Option("Equals","<%=Operator.EQUAL%>");
			opCombo.options[1] = new Option("Not Equals","<%=Operator.NOT_EQUALS%>");
			opCombo.options[2] = new Option("<%=Operator.LESS_THAN%>","<%=Operator.LESS_THAN%>");
			opCombo.options[3] = new Option("<%=Operator.LESS_THAN_OR_EQUALS%>","<%=Operator.LESS_THAN_OR_EQUALS%>");
			opCombo.options[4] = new Option("<%=Operator.GREATER_THAN%>","<%=Operator.GREATER_THAN%>");
			opCombo.options[5] = new Option("<%=Operator.GREATER_THAN_OR_EQUALS%>","<%=Operator.GREATER_THAN_OR_EQUALS%>");
		}

		opCombo.options[opCombo.options.length] = new Option("<%=Operator.IS_NULL%>","<%=Operator.IS_NULL%>");
		opCombo.options[opCombo.options.length] = new Option("<%=Operator.IS_NOT_NULL%>","<%=Operator.IS_NOT_NULL%>");
	}
}

function showDatafield(element,txtFieldID,columnID,showCalendarID,fieldValue,overDiv,attributeNameID)
{
	var dataStr = element.options[element.selectedIndex].value;
	var dataValue = new String(dataStr);

	var attributeName = document.getElementById(attributeNameID).value;
	//var attributeName = document.getElementsByName(attributeNameID).value;
	var attributeNameValue = new String(attributeName);

	var lastInd = attributeNameValue.lastIndexOf(".");
	var dataType = "";
	if(lastInd != -1)
		dataType = attributeNameValue.substr(lastInd+1);
	 
	var txtField = document.getElementById(txtFieldID);
	
	// changed for bug#10042
	var calendarShow = document.getElementById(showCalendarID);
	var td = document.getElementById(columnID);


	if(dataValue == "<%=Operator.IS_NULL%>" || dataValue == "<%=Operator.IS_NOT_NULL%>")
	{
		txtField.value = "";
		txtField.disabled = true;
		// changed for bug#10042
		if (dataType == "<%=edu.wustl.simplequery.global.Constants.FIELD_TYPE_DATE%>" || dataType == "<%=edu.wustl.simplequery.global.Constants.FIELD_TYPE_TIMESTAMP_DATE%>")
		{
			td.innerHTML = "&nbsp;";
			calendarShow.value = "";
		}
	}
	else
	{	
		txtField.disabled = false;

		if (dataType == "<%=edu.wustl.simplequery.global.Constants.FIELD_TYPE_DATE%>" || dataType == "<%=edu.wustl.simplequery.global.Constants.FIELD_TYPE_TIMESTAMP_DATE%>")
		{
			// changed for bug#10042
			calendarShow.value = "Show";
			var innerStr = "<div id='"+ overDiv +"' style='position:absolute; visibility:hidden; z-index:1000;'></div>";
			//innerStr = innerStr + "<a href=\"javascript:show_calendar('"+fieldValue+"',null,null,'MM-DD-YYYY');\">";
			//innerStr = innerStr + "<img src=\"images//calendar.gif\" width=24 height=22 border=0>";
			//innerStr = innerStr + "</a>";
			innerStr = innerStr + "<img src=\"images//calendar.gif\" width=24 height=22 border=0 onclick='scwShow("+ fieldValue + ",event);'>";

			td.innerHTML = innerStr;
		}
	}
}

function enableLastCheckbox()
{
	var lastRowNo = document.forms[0].counter.value;
	var chkBox = document.getElementById("chk_"+lastRowNo);
	if(lastRowNo>1)
		chkBox.disabled = false;
}

function enablePreviousCheckBox(element)
{
   	var elementName = element.name;
   	var index = elementName.indexOf('_');
   	var previousRowNo = parseInt(elementName.substring(index+1))-1;
    if (element.checked == true)
    {
      	if(previousRowNo > 1)
      	{
			var previousElement = document.getElementById("chk_"+previousRowNo);
			previousElement.disabled = false;     
		}
    }
    else if(element.checked == false)
    {
      	for(var i=previousRowNo;i>=1;i--)
      	{
			var previousElement = document.getElementById("chk_"+i);
			previousElement.checked = false;
			previousElement.disabled = true;     
		}
	}
}

</script>


<body onload="enableLastCheckbox()">
<table width="100%" border="0" cellpadding="0" cellspacing="0" class="maintable">
	
	<html:form action="<%=Constants.SIMPLE_SEARCH_ACTION%>">
	
		<!-- SIMPLE QUERY INTERFACE BEGINS-->
		<tr>
		   <td class="td_color_bfdcf3">
				 <table border="0" cellpadding="0" cellspacing="0" class="whitetable_bg">
					<tr>
        <td class="td_table_head"><span class="wh_ar_b"><bean:message key="<%=header%>" /></span></td>
      <% 
	       String pageTitle= "Page Title - "+ ApplicationProperties.getValue(header);
	  %> 	   
		
			<td align="right"><img src="images/uIEnhancementImages/table_title_corner2.gif" alt="<%=pageTitle%>"  width="31" height="24" />
			</td>  
		</tr>

	</table>
			</td>
		 </tr>
			
				<tr>
					<td>
						<html:hidden property="aliasName" value="<%=aliasName%>"/>
						<html:hidden property="<%=edu.wustl.common.util.global.Constants.MENU_SELECTED%>" value="<%=selectMenu%>"/>
						<input type="hidden" name="objectChanged" id="objectChanged" value="">
					</td>
				</tr>
				<tr>
					<td>
						<html:hidden property="counter" value="<%=noOfRows%>"/>
					</td>
				</tr>
				<tr>
					<td>
						<html:hidden property="pageOf" value="<%=pageOf%>"/>
					</td>
				</tr>
				<tr>
					<td>
						<html:hidden property="andOrOperation" />
					</td>
				</tr>

			<tr>
					  <td class="tablepadding"><table width="100%" border="0" cellpadding="0" cellspacing="0">
	<logic:notEqual name="pageOf" value="<%=Constants.PAGEOF_SIMPLE_QUERY_INTERFACE%>">
	<tr>
	<td class="td_tab_bg" ><img src="images/uIEnhancementImages/spacer.gif" alt="spacer" width="50" height="1"></td>
	<!----Add tab hidden for the Specimen Search----->
	<logic:notEqual name="pageOf" value="<%=Constants.PAGEOF_DISTRIBUTION%>">
		<logic:notEqual name="pageOf" value="<%=Constants.PAGEOF_NEW_SPECIMEN%>">
        <td valign="bottom"><html:link href="#" onclick="callAction('CommonTab.do')">
		<img src="images/uIEnhancementImages/tab_add_notSelected.jpg" alt="Add" width="57" height="22" /></html:link></td>
		</logic:notEqual>       
		<td valign="bottom"><img src="images/uIEnhancementImages/tab_edit_selected.jpg" alt="Edit" width="59" height="22" border="0" /></td>
	</logic:notEqual>

	<logic:equal name="pageOf" value="<%=Constants.PAGEOF_USER_ADMIN%>">
        <td valign="bottom"><html:link page="/ApproveUserShow.do?pageNum=1"><img src="images/uIEnhancementImages/tab_approve_user.jpg" alt="Approve New Users" width="139" height="22" border="0" /></html:link></td>
		</logic:equal>
		<logic:equal name="pageOf" value="pageOfStorageContainer">
		<td  valign="bottom"><a href="#"><img src="images/uIEnhancementImages/view_map2.gif" alt="View Map"width="76" height="22" border="0" onclick="vieMapTabSelected()"/></a></td>
		</logic:equal>
<!-- These tabs are visible in case of specimen page--->
		 <logic:equal name="pageOf" value="<%=Constants.PAGEOF_NEW_SPECIMEN%>">
			<td valign="bottom"><html:link page="/CreateSpecimen.do?operation=add&amp;pageOf=&virtualLocated=true">	<img src="images/uIEnhancementImages/tab_derive2.gif" alt="Derive" width="56" height="22" border="0"/>	</html:link></td>
			<td valign="bottom"><html:link page="/Aliquots.do?pageOf=pageOfAliquot"><img src="images/uIEnhancementImages/tab_aliquot2.gif" alt="Aliquot" width="66" height="22" border="0" >		</html:link></td>
			<td valign="bottom"><html:link page="/QuickEvents.do?operation=add"><img src="images/uIEnhancementImages/tab_events2.gif" alt="Events" width="56" height="22" border="0" />		</html:link></td>
			<td valign="bottom"><html:link page="/MultipleSpecimenFlexInitAction.do?pageOf=pageOfMultipleSpWithMenu"><img src="images/uIEnhancementImages/tab_multiple2.gif" alt="Multiple" width="66" height="22" border="0" />	</html:link></td>
		</logic:equal>
		<td width="90%" valign="bottom" class="td_tab_bg">&nbsp;</td>

	</tr>
	</logic:notEqual>
	<logic:equal name="pageOf" value="<%=Constants.PAGEOF_SIMPLE_QUERY_INTERFACE%>">
      <tr>
        <td width="90%" valign="bottom" class="td_tab_bg">&nbsp;</td>
      </tr>
	  </logic:equal>
    </table>
	 <table width="100%" border="0" cellpadding="0" cellspacing="0" class="whitetable_bg">
      
      <tr>
        <td colspan="2" align="left" class="bottomtd"><%@ include file="/pages/content/common/ActionErrors.jsp" %></td>
      </tr>
      <tr>
        <td colspan="2" align="left" class="tr_bg_blue1"><span class="blue_ar_b"> &nbsp;<bean:message key="<%=title%>" /></span></td>

      </tr>
					
				<tr>

				<tr>
        <td align="right"><table width="100%" border="0" cellspacing="0" cellpadding="2">
		 <%
 		if(pageOf.equals(edu.wustl.simplequery.global.Constants.PAGEOF_SIMPLE_QUERY_INTERFACE))
							{
	String configAction = "callAction('"+Constants.CONFIGURE_SIMPLE_QUERY_VALIDATE_ACTION+"?pageOf=pageOfSimpleQueryInterface')";%>

            <tr>

              <td width="6%" align="right"  nowrap="nowrap"><span class="link"><html:link href="#" onclick="<%=configAction%>" styleClass="view">Define View</html:link></span></td>
            </tr>
<% }%>

        </table></td>
        <td width="1%" align="right"></td>
      </tr>

						<tr>
        <td colspan="2" align="center" class="showhide_new"><table width="99%" border="0" cellspacing="0" cellpadding="5">
            <tr class="tableheading">
<logic:equal name="pageOf" value="<%=Constants.PAGEOF_SIMPLE_QUERY_INTERFACE%>">
              <td width="6%" align="left" class="black_ar_b">Select</td>
              <td width="26%" align="left" class="black_ar_b"><label for="object"> <bean:message
											key="query.object" /> </label> </td>
	</logic:equal>
              <td width="26%" class="black_ar_b"><label for="attributes"> <bean:message key="query.attributes" />
									</label></td>
              <td width="12%" class="black_ar_b"><label for="conditions"> <bean:message
										key="query.conditions" /> </label></td>
			  <td width="4%">&nbsp;</td>
              <td width="15%" class="black_ar_b" ><label for="value">
									<bean:message key="query.value" /> </label></td>
	<logic:equal name="pageOf" value="<%=Constants.PAGEOF_SIMPLE_QUERY_INTERFACE%>">
              <td width="11%" class="black_ar_b"><label for="attributes">
										<bean:message key="query.operator" /> </label></td>
	</logic:equal>
	        </tr>
				<tbody id="simpleQuery">
				<%
					for (int i=1;i<=Integer.parseInt(noOfRows);i++){
						String objectName = "value(SimpleConditionsNode:"+i+"_Condition_DataElement_table)";
						String attributeName = "value(SimpleConditionsNode:"+i+"_Condition_DataElement_field)";
						
						String attributeCondition = "value(SimpleConditionsNode:"+i+"_Condition_Operator_operator)";
						String attributeValue = "value(SimpleConditionsNode:"+i+"_Condition_value)";
						String attributeValueID = "SimpleConditionsNode"+i+"_Condition_value_ID";
						String nextOperator = "value(SimpleConditionsNode:"+i+"_Operator_operator)";			
						String attributeNameList = "attributeNameList"+i;
						String attributeDisplayNameList = "attributeDisplayNameList"+i;
						String objectNameList = "objectList"+i;
						String objectDisplayNameList = "objectDisplayNameList"+i;
						SimpleQueryInterfaceForm form = (SimpleQueryInterfaceForm)obj;
						String nextOperatorValue = (String)form.getValue("SimpleConditionsNode:"+i+"_Operator_operator");
						String columnID = "calTD"+i;
						String showCalendarKey = "SimpleConditionsNode:"+i+"_showCalendar";			
						String showCalendarValue = "showCalendar(SimpleConditionsNode:"+i+"_showCalendar)";
						String fieldName = "simpleQueryInterfaceForm."+attributeValueID;
						String overDiv = "overDiv";
						String check = "chk_"+i;
						if(i>1)
						{
							overDiv = overDiv + "" + i;
						}
						String functionName = "showDateColumn(this,'"+ attributeValueID +"','" + columnID + "','" + showCalendarValue + "','"+fieldName+"','"+overDiv+"')";
						String attributeId = "attribute" + i;
						String operatorId = "operator" + i;
						String onAttributeChange = "onAttributeChange(this,'" + operatorId + "','" + attributeValueID + "'); " + functionName;
						String operatorFunction = "showDatafield(this,'" + attributeValueID + "','" + columnID + "','" + showCalendarValue + "','"+fieldName+"','"+overDiv + "','" + attributeId + "')";
						String attributeConditionKey = "SimpleConditionsNode:"+i+"_Condition_Operator_operator";
				%>	
				 <tr>
			<logic:equal name="pageOf" value="<%=Constants.PAGEOF_SIMPLE_QUERY_INTERFACE%>">
              <td align="left" class="black_ar_new" ><input type=checkbox
												name="<%=check %>" id="<%=check %>" disabled="true"
												onClick="enablePreviousCheckBox(this);enableButton(document.forms[0].deleteValue,document.forms[0].counter,'chk_')"></td>
			</logic:equal>
					<%
						String attributeAction = "javascript:onObjectChange(this,'SimpleQueryInterface.do?pageOf="+pageOf;
						if (aliasName != null)
							attributeAction = attributeAction + "&aliasName="+aliasName+"')";
						else
							attributeAction = attributeAction + "')";
					%>
<!-- Mandar : 434 : for tooltip -->
						<logic:equal name="pageOf" value="<%=edu.wustl.simplequery.global.Constants.PAGEOF_SIMPLE_QUERY_INTERFACE%>">
						<td class="black_new">
						<html:select property="<%=objectName%>" styleClass="formFieldSized18" styleId="<%=objectName%>" size="1" onchange="<%=attributeAction%>"
>
							<logic:notPresent name="<%=objectNameList%>">			
								<html:options collection="objectNameList" labelProperty="name" property="value"/>
							</logic:notPresent>	
							<logic:present name="<%=objectNameList%>">		
								<html:options collection="<%=objectNameList%>" labelProperty="name" property="value"/>
							</logic:present>	
						</html:select>
						</td>
						</logic:equal>
						<logic:notEqual name="pageOf" value="<%=edu.wustl.simplequery.global.Constants.PAGEOF_SIMPLE_QUERY_INTERFACE%>">
							<html:hidden property="<%=objectName%>" value="<%=aliasName%>"/>
						</logic:notEqual>
					 <td nowrap class="black_new">
<!-- Mandar : 434 : for tooltip -->
						<html:select property="<%=attributeName%>" styleClass="formFieldSized18" styleId="<%=attributeId%>" size="1" onchange="<%=onAttributeChange%>"
>
							<logic:notPresent name="<%=attributeNameList%>">
								<html:options name="attributeNameList" labelName="attributeNameList" />
							</logic:notPresent>	
							<logic:present name="<%=attributeNameList%>">		
								<html:options collection="<%=attributeNameList%>" labelProperty="name" property="value"/>
							</logic:present>	
						</html:select>
					</td>
					  <td nowrap class="black_new">
<!-- Mandar : 434 : for tooltip -->
						<html:select property="<%=attributeCondition%>" styleClass="formFieldSized10" styleId="<%=operatorId%>" size="1" onchange="<%=operatorFunction%>"
>
						<%
							String attributeNameKey = "SimpleConditionsNode:"+i+"_Condition_DataElement_field";
							String attributeNameValue = (String)form.getValue(attributeNameKey);
							String attributeType = null;	
							List columnNameValueBeanList = (List) request.getAttribute(attributeNameList);		
							//System.out.println("---------"+attributeNameValue);
							if(columnNameValueBeanList != null && !columnNameValueBeanList.isEmpty() && i==Integer.parseInt(noOfRows)  && attributeNameValue == null 	 && (objectChanged == null || !objectChanged.equals("")))
							{								
								NameValueBean nameValueBean = (NameValueBean) columnNameValueBeanList.get(0);
								attributeNameValue = nameValueBean.getValue();
								
							}	
							if(attributeNameValue != null)
							{
								StringTokenizer tokenizer = new StringTokenizer(attributeNameValue,".");
								int tokenCount = 1;
								while(tokenizer.hasMoreTokens())
								{
									attributeType = tokenizer.nextToken();
									if(tokenCount == 3) break;
									tokenCount++;
								}
								//System.out.println("attributeType-----------"+attributeType);							
								if(attributeType.equals("varchar") || attributeType.equals("text"))
								{
							%>
								<html:option value="<%=Operator.STARTS_WITH%>"><%=Operator.STARTS_WITH%></html:option>
								<html:option value="<%=Operator.ENDS_WITH%>"><%=Operator.ENDS_WITH%></html:option>
								<html:option value="<%=Operator.CONTAINS%>"><%=Operator.CONTAINS%></html:option>
								<html:option value="<%=Operator.EQUAL%>">Equals</html:option>
								<html:option value="<%=Operator.NOT_EQUALS%>">Not Equals</html:option>
							<%
								}
								else if (attributeType.equals(edu.wustl.simplequery.global.Constants.FIELD_TYPE_TINY_INT))
								{
							%>
								<html:option value="<%=Operator.EQUAL%>">Equals</html:option>
								<html:option value="<%=Operator.NOT_EQUALS%>">Not Equals</html:option>
							<%
								}
								else if (attributeType.equals(edu.wustl.simplequery.global.Constants.FIELD_TYPE_BIGINT) || attributeType.equals("double"))
								{
							%>
								<html:option value="<%=Operator.EQUAL%>">Equals</html:option>
								<html:option value="<%=Operator.NOT_EQUALS%>">Not Equals</html:option>
								<html:option value="<%=Operator.LESS_THAN%>"><%=Operator.LESS_THAN%></html:option>
								<html:option value="<%=Operator.LESS_THAN_OR_EQUALS%>"><%=Operator.LESS_THAN_OR_EQUALS%></html:option>
								<html:option value="<%=Operator.GREATER_THAN%>"><%=Operator.GREATER_THAN%></html:option>
								<html:option value="<%=Operator.GREATER_THAN_OR_EQUALS%>"><%=Operator.GREATER_THAN_OR_EQUALS%></html:option>
							<%
								}
								else if (attributeType.equals(edu.wustl.simplequery.global.Constants.FIELD_TYPE_DATE) || attributeType.equals(edu.wustl.simplequery.global.Constants.FIELD_TYPE_TIMESTAMP_DATE))
								{
									form.setShowCalendar(showCalendarKey, "Show");
							%>
								<html:option value="<%=Operator.EQUAL%>">Equals</html:option>
								<html:option value="<%=Operator.NOT_EQUALS%>">Not Equals</html:option>
								<html:option value="<%=Operator.LESS_THAN%>"><%=Operator.LESS_THAN%></html:option>
								<html:option value="<%=Operator.LESS_THAN_OR_EQUALS%>"><%=Operator.LESS_THAN_OR_EQUALS%></html:option>
								<html:option value="<%=Operator.GREATER_THAN%>"><%=Operator.GREATER_THAN%></html:option>
								<html:option value="<%=Operator.GREATER_THAN_OR_EQUALS%>"><%=Operator.GREATER_THAN_OR_EQUALS%></html:option>
							<%
								}
								else{
								
							%>
								<html:option value="<%=Operator.STARTS_WITH%>"><%=Operator.STARTS_WITH%></html:option>
								<html:option value="<%=Operator.ENDS_WITH%>"><%=Operator.ENDS_WITH%></html:option>
								<html:option value="<%=Operator.CONTAINS%>"><%=Operator.CONTAINS%></html:option>
								<html:option value="<%=Operator.EQUAL%>">Equals</html:option>
								<html:option value="<%=Operator.NOT_EQUALS%>">Not Equals</html:option>
							<%
								}
							}
							else
							{
						%>
							<html:option value="<%=Operator.STARTS_WITH%>"><%=Operator.STARTS_WITH%></html:option>
							<html:option value="<%=Operator.ENDS_WITH%>"><%=Operator.ENDS_WITH%></html:option>
							<html:option value="<%=Operator.CONTAINS%>"><%=Operator.CONTAINS%></html:option>
							<html:option value="<%=Operator.EQUAL%>">Equals</html:option>
							<html:option value="<%=Operator.NOT_EQUALS%>">Not Equals</html:option>
						<%
							}
						%>
							<html:option value="<%=Operator.IS_NULL%>"><%=Operator.IS_NULL%></html:option>
							<html:option value="<%=Operator.IS_NOT_NULL%>"><%=Operator.IS_NOT_NULL%></html:option>
						</html:select>
						
				</td>
				<html:hidden property="<%=showCalendarValue%>" styleId="<%=showCalendarValue%>" />
					<td nowrap class="black_new" id="<%=columnID%>"  size=3>
				<!--  ********************* Mandar Code ********************** -->	
				<!-- ***** Code added to check multiple rows for Calendar icon ***** -->
				
				<%
					showCal = (String)form.getShowCalendar(showCalendarKey);
					//System.out.println("showCal-----------"+showCal);	
					if(showCal != null && showCal.trim().length()>0)
					{
						String currentOperator = (String)form.getValue(attributeConditionKey);
						if((currentOperator != null) && !(currentOperator.equals(Operator.IS_NOT_NULL) || currentOperator.equals(Operator.IS_NULL)))
						{
				%>
						
						<img src="images/calendar.gif" width=24 height=22 border=0 onclick="scwShow(<%=fieldName%>,event)">
				<%	
						}
						else
						{
				%>
						&nbsp;	

				<%
						}
					}
					else
					{
				%>
						&nbsp;					
				<%						
					}

				%>
				</td>
						  <td nowrap class="black_new">
					<%
						String currentOperatorValue = (String)form.getValue(attributeConditionKey);
						if((currentOperatorValue != null) && (currentOperatorValue.equals(Operator.IS_NOT_NULL) || currentOperatorValue.equals(Operator.IS_NULL)))
						{
					%>
						<html:text styleClass="formFieldSized10" size="30" styleId="<%=attributeValueID%>" property="<%=attributeValue%>" disabled="true"/>
					<%
						}
						else
						{
					%>
						<html:text styleClass="formFieldSized10" size="30" styleId="<%=attributeValueID%>" property="<%=attributeValue%>" />
					<%
						}
					%>
					</td>


					<!--html:hidden property="<%=nextOperator%>"/-->
				<logic:equal name="pageOf" value="<%=edu.wustl.simplequery.global.Constants.PAGEOF_SIMPLE_QUERY_INTERFACE%>">
				 <td nowrap class="black_ar_new">
					<html:select property="<%=nextOperator%>"  styleClass="formFieldSized8">
						<html:option value="And"> 
							<bean:message key="simpleQuery.and" />
						</html:option>
						<html:option value="Or"> 
							<bean:message key="simpleQuery.or" />
						</html:option>
					</html:select>
					</td>
				</logic:equal>
				</tr>
				<%}%>
				</tbody>
				<logic:equal name="pageOf"
										value="<%=edu.wustl.simplequery.global.Constants.PAGEOF_SIMPLE_QUERY_INTERFACE%>">
										<%
													String addAction = "setPropertyValue('andOrOperation','true');"
													+ "incrementCounter();callSerachAction('SimpleQueryInterfaceValidate.do?pageOf="
													+ pageOf + "');";
										%>
				<tr>
			 <td colspan="2" align="left" class="black_ar" >
			   <tr>
                    <td width="9%"><html:button
											property="addKeyValue" styleClass="black_ar"
											onclick="<%=addAction%>">
											<bean:message key="buttons.addMore" />
										</html:button></td>

																	<%
 		String deleteAction = "decrementCounter();setPropertyValue('value(SimpleConditionsNode:"
 		+ (Integer.parseInt(noOfRows) - 1)
 		+ "_Operator_operator)','');"
 		+ "callSerachAction('SimpleQueryInterface.do?pageOf="
 		+ pageOf + "');";

									
 %> 
                    <td a width="19%" lign="left"><html:button property="deleteValue" styleClass="black_ar"
										onclick="deleteChecked('simpleQuery','SimpleQueryInterface.do?pageOf=<%=pageOf%>',document.forms[0].counter,'chk_',false,document.forms[0].deleteValue)"
										disabled="true">
										<bean:message key="buttons.delete" />
									</html:button></td>
								

								
                  </tr>
              </table></td>
            </tr>
		</logic:equal>
			
					</table></td>
      </tr>
      <tr>
        <td  class="buttonbg"><%
												String searchAction = "callSerachAction('"
												+ Constants.SIMPLE_SEARCH_ACTION + "')";
									%> <html:button styleClass="blue_ar_b" property="searchButton"
										onclick="<%=searchAction%>">
										<bean:message key="buttons.search" />
									</html:button></td>

									
      </tr>
    </table></td>
  </tr>
 </html:form>
</table>