<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ page import="edu.wustl.clinportal.util.global.Constants"%>
<html>
<html:errors/>

<script language="JavaScript" type="text/javascript" src="jss/javaScript.js"></script>
<script language="javascript">
function refresh(){ 
		
//top.frames["cpAndParticipantView"].refreshParticipants();
}
</script>
<body onload="refresh()">
<table summary="" cellpadding="0" cellspacing="0" border="0" class="contentPage" width="600">
	<tr>
		<td colspan="2">
		   <ul>
				<li><font color="#000000" size="3" face="Verdana"><strong>This is user Workspace</strong></font></li>
		   </ul>
		</td>
	</tr>
</table>

<%
String participantId=(String)request.getAttribute("participantId");
	%>
	<script language="javascript">	
	 if(top.frames["cpAndParticipantView"] != undefined)
	 {
	 	top.frames["cpAndParticipantView"].refreshCpParticipants(<%=participantId%>);	  
	 }
	</script>
	
</body>
</html>