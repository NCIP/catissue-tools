<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/nlevelcombo.tld" prefix="ncombo" %>

<%@ page import="edu.wustl.clinportal.util.global.Constants"%>
<%@ page import="java.util.*"%>

<SCRIPT>var imgsrc="images/";</SCRIPT>
<script src="jss/script.js" type="text/javascript"></script>

	<%	
		String pageOf = (String)request.getAttribute(Constants.PAGEOF);
		String participantId=(String)request.getAttribute("participantId");
		String cpId=(String)request.getAttribute("cpId");
		
		if(pageOf != null && pageOf.equals(Constants.PAGE_OF_PARTICIPANT_CP_QUERY))
		{
	%>
	<script language="javascript">
			<%
				if(participantId != null)
				{
			%>
			window.parent.frames['<%=Constants.CP_AND_PARTICIPANT_VIEW%>'].location="showCsAndParticipants.do?csId=<%=cpId%>&participantId=<%=participantId%>";
			window.parent.frames['<%=Constants.CP_TREE_VIEW%>'].location="showEventTree.do";
			
			<%
				}
				else
				{
			%>
			window.parent.frames['<%=Constants.CP_AND_PARTICIPANT_VIEW%>'].location="showCsAndParticipants.do?csId=<%=cpId%>";
			<%
				}
			%>
	</script>
	<%
		}
	%>