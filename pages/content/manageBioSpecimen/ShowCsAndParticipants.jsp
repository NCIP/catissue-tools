
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/nlevelcombo.tld" prefix="ncombo" %>

<%@ page import="edu.wustl.clinportal.util.global.Constants"%>
<%@ page import="edu.wustl.clinportal.util.global.Utility"%>

<%
	String access = null;
	access = (String)session.getAttribute("Access");
		

%>

<head>
<link rel="stylesheet" type="text/css" href="css/styleSheet.css" />
<script language="JavaScript" type="text/javascript" src="jss/javaScript.js"></script>
<script language="JavaScript">
		function onCpChange(element)
		{
			var csId = document.getElementById("csId");
			var participantId = document.getElementById("participantId");
			var action = "showCsAndParticipants.do?cpChange=true";
			document.forms[0].action = action;
			
			document.forms[0].submit();
			window.parent.frames['<%=Constants.CP_TREE_VIEW%>'].location = "showEventTree.do";
		
		}
		
		function onParticipantChange(element)
		{
			var csId = document.getElementById("csId");
			var participantId = document.getElementById("participantId");
			
			if (participantId.value != "")
			{
				window.parent.frames[2].location = "QueryParticipantSearch.do?pageOf=pageOfParticipantCPQueryEdit&operation=edit&<%=Constants.CP_SEARCH_CP_ID%>="+csId.value+"&id="+participantId.value;

				window.parent.frames['<%=Constants.CP_TREE_VIEW%>'].location = "showEventTree.do?<%=Constants.CP_SEARCH_PARTICIPANT_ID%>="+participantId.value+"&<%=Constants.CP_SEARCH_CP_ID%>="+csId.value;			

			}
			
			
		}
		
		function RegisterParticipants()
		{
			var csId = document.getElementById("csId");
			if(csId.value == "-1")
			{
				alert("please select clinical study.");
			}
			else
			{
				window.parent.frames[2].location = "CPQueryParticipant.do?operation=add&pageOf=pageOfParticipantCPQuery&<%=Constants.CP_SEARCH_CP_ID%>="+csId.value;
			
			}
		}	
		

</script>	
	
</head>

<html:messages id="messageKey" message="true" header="messages.header" footer="messages.footer">
	<%=messageKey%>
</html:messages>

<html:errors/>

<html:form action="showCsAndParticipants.do">

<table summary="" cellpadding="0" cellspacing="0" border="0">
	<tr>

		<td class="formLabelBorderlessLeft">
			<b><bean:message key="clinicalStudyReg.protocolTitle" ></bean:message> :</b> <%if(access == null || !access.equals("Denied"))
	{%> <html:link href="#" styleId="register" onclick="RegisterParticipants()"><bean:message key="app.register"></bean:message></html:link><%}%>
		</td>
	</tr>		
	<tr>

		<td class="formField" nowrap>
			<html:select property="csId" styleClass="formFieldSized15" styleId="csId" size="1" onchange="onCpChange(this)">
			<html:options collection="<%=Constants.CS_LIST%>" labelProperty="name" property="value"/>
			</html:select>
			
		</td>
	</tr>
		
	
	 <%if(access != null && access.equals("Denied"))
	{%>
	<tr>

		<td nowrap>
			<html:hidden property="participantId" styleId="participantId" value="-1"/>
		</td>
	</tr>
	<%} else {%>	
	<tr>

		<td class="formLabelBorderlessLeft">
			<b>Participant :</b> Name (ClinicalStudy Id) 
		</td>
	</tr>	
	
	<tr>

		<td class="formField" nowrap>
			<html:select property="participantId" styleClass="formFieldSized15" styleId="participantId" size="11" onclick="onParticipantChange(this)"
			 onmouseover="showTip(this.id)" onmouseout="hideTip(this.id)">
			<html:options collection="<%=Constants.REGISTERED_PARTICIPANT_LIST%>" labelProperty="name" property="value"/>
			</html:select>
	
		</td>
	</tr>
		<%}%>
</table>		
</html:form>