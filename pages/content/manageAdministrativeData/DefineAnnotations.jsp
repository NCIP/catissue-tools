<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">

<%-- TagLibs --%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%-- Imports --%>
<%@
	page language="java" contentType="text/html"
	import="java.util.List"
	import="edu.wustl.common.beans.NameValueBean"
%>
<%@ page import="edu.wustl.clinportal.util.global.Constants"%>
<%@ page import="edu.wustl.clinportal.util.CatissueCoreCacheManager"%>
<%@ page import="java.util.Iterator"%>
<html>
<c:set var="groupsXML" value="${annotationForm.annotationGroupsXML}"/>
<jsp:useBean id="groupsXML" type="java.lang.String"/>

<c:set var="conditionalInstancesList" value="${annotationForm.conditionalInstancesList}"/>
<jsp:useBean id="conditionalInstancesList" type="java.util.List"/>

<%
String link = request.getParameter("link");
String containerId = request.getParameter("containerId");
String selectedStaticEntityId=request.getParameter("selectedStaticEntityId");
CatissueCoreCacheManager catissueCoreCacheManager = CatissueCoreCacheManager.getInstance();
String participantStaticEntityId = catissueCoreCacheManager.getObjectFromCache("participantEntityId").toString();
System.out.println("containerId:"+containerId);
%>
	<head>
		<%-- Stylesheet --%>
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/styleSheet.css" />
		<link rel="STYLESHEET" type="text/css" href="<%=request.getContextPath()%>/dhtml_comp/css/dhtmlXGrid.css"/>
		<script src="<%=request.getContextPath()%>/jss/javaScript.js" type="text/javascript"></script>
		<script src="<%=request.getContextPath()%>/jss/ajax.js" type="text/javascript"></script>
		<script  src="<%=request.getContextPath()%>/dhtml_comp/js/dhtmlXCommon.js"></script>
		<script  src="<%=request.getContextPath()%>/dhtml_comp/js/dhtmlXGrid.js"></script>
		<script  src="<%=request.getContextPath()%>/dhtml_comp/js/dhtmlXGridCell.js"></script>
	<script  src="<%=request.getContextPath()%>/dhtml_comp/js/dhtmlXGrid_excell_link.js"></script>

	<link rel="STYLESHEET" type="text/css" href="<%=request.getContextPath()%>/dhtml_comp/css/dhtmlXMenu.css">
		<script language="JavaScript" src="<%=request.getContextPath()%>/dhtml_comp/js/dhtmlXProtobar.js"></script>
		<script language="JavaScript" src="<%=request.getContextPath()%>/dhtml_comp/js/dhtmlXMenuBar.js"></script>
		<script>
			function initializeAnnotationsForm()
			{
				//document.getElementById('optionSelect').disabled=false;
				if(top.location!=location)
				{
					top.location.href = location.href;
				}
				<%  if(link!=null && link.equals("editCondn"))   {%>
			//		document.getElementById('optionSelect').disabled=true;
					<%}else{%>
				initializeGridForGroups("<%=groupsXML%>");
				initializeGridForEntities();
				<%}%>
			//	initializeStaticEntities();
			}
			
			function showList()
			{
				if(document.getElementById('chkbox').checked)	     
				{
					document.getElementById('cpList').disabled=false;
				}
				else
				{
					document.getElementById('cpList').disabled=true;
				}		

			}
			function editAnno()
			{
				//alert('EditAnno');
				document.forms[0].action="DefineAnnotations.do?link=edit";
				document.forms[0].submit();
			}
			
			function editCondition()
			{     
				document.forms[0].action="SaveAnnotationsConditions.do?containerId="+<%=containerId%>+"&selectedStaticEntityId="+<%=selectedStaticEntityId%>;
				document.forms[0].submit();

			}
			

		</script>
	</head>

	<html:form styleId='annotationForm' action='/DefineAnnotations'>
	
	<body onload = "initializeAnnotationsForm()">



<table summary="" cellpadding="3" cellspacing="0" border="0" width="100%">	
	<tr height = 20>
      	   <td></td>
      	   <td></td>
    </tr>
	<tr align="center">
	<td>
		<%if (link==null){ %>
			
			<html:button styleId="go1btn"  styleClass="actionButton" property = "delete" onclick="submitForm()" >
				<bean:message key="buttons.newAnnotation"/>
			</html:button>
		<%} %>
		<%if (link!=null && link.equals("editCondn")){ %>
			<html:button   styleClass="actionButton" property = "delete" onclick="editCondition()" >
				<bean:message key="buttons.submit"/>
			</html:button>	
								
			<html:button   styleClass="actionButton" property = "delete" onclick="javascript:history.go(-1);" >
				<bean:message key="buttons.cancel"/>
			</html:button>								
		<%} %>

	 </td>
								
    </tr>
	 <tr height = 20>
      	   <td></td>
      	   <td></td>
     </tr>
  </table>

   <table align="center" valign="top" border="0" height="80%" width = "600" cellspacing="0" cellpadding="3">

   	<%if (link==null){ %>

	<tr valign="top" height = "80%">
	 <td  width="2%"></td>	
       <td>
				<table valign="top" width="100%" border='0' height="80%" cellspacing="0" cellpadding="0" >
			<!-- Main Page heading -->				    
				 <tr height="2%">
					   <td class="formTitle" height="25" colspan="3">
						  <bean:message key="app.availableGrp"/> 
						</td>
				 </tr>
					<tr valign="top" width="100%" height="100%">
						<td  align="left">
							<table  class = "tbBordersAllbordersBlack" border="1" cellspacing="0" cellpadding="0" valign="top" width="100%" height="80%">						
								<tr height="100%" valign="top">								
									<td align="left" width="18%" height="100%" valign="top" rowspan="2">
										<!--Groups list-->
										<div id="divForGroups" width="100%" height="100%" style="background-color:white;overflow:hidden"/>
									</td>								
									<td align="left" valign="top">
										<!--List of entities-->
										<div id="gridForEnities" width="100%" height="100%" style="background-color:white;overflow:hidden"/>
									</td>									
								</tr>
							</table>
						</td>
					</tr>	
					
			  </table>
	   </td>	 
	</tr>
	
	<%}%>
		
</table>

	<html:hidden styleId="selectedStaticEntityId" property="selectedStaticEntityId" value="<%=participantStaticEntityId%>"/>
	</body>
	</html:form>
</html>
