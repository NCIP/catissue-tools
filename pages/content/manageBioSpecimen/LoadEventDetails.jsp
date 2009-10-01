<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">

<%-- TagLibs --%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/nlevelcombo.tld" prefix="ncombo"%>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c"%>
<%@ page import="java.util.List"%>
<%-- Imports --%>
<%@ page language="java" contentType="text/html"%>
<%@ page import="edu.wustl.clinportal.util.global.Constants"%>
<%@ page import="edu.wustl.clinportal.util.global.Utility"%>
<%@ page import="edu.wustl.clinportal.actionForm.EventEntryForm"%>
<%@ page import="edu.wustl.clinportal.action.annotations.AnnotationConstants"%>
<%@ page import="edu.wustl.common.beans.NameValueBean"%>

<script src="jss/calendar.js"></script>
<script src="jss/calendarComponent.js"></script>
<SCRIPT>var imgsrc="images/";</SCRIPT>
<LINK href="css/calanderComponent.css" type=text/css rel=stylesheet>
<link rel="STYLESHEET" type="text/css"
	href="dhtml_comp/css/dhtmlXGrid.css" />
<link href="css/catissue_suite.css" type=text/css rel=stylesheet>

<script
	src="<%=request.getContextPath()%>/dhtml_comp/js/dhtmlXCommon.js"></script>
<script src="<%=request.getContextPath()%>/dhtml_comp/js/dhtmlXGrid.js"></script>
<script src="<%=request.getContextPath()%>/jss/breadcrumb.js"></script>
<script
	src="<%=request.getContextPath()%>/dhtml_comp/js/dhtmlXGridCell.js"></script>
<script
	src="<%=request.getContextPath()%>/dhtml_comp/js/dhtmlXGrid_excell_link.js"></script>
<script src="<%=request.getContextPath()%>/jss/ajax.js" type="text/javascript"></script>

<script src="<%=request.getContextPath()%>/jss/javaScript.js"
	type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/jss/script.js"
	type="text/javascript"></script>


<%
	String eventId = request.getParameter(Constants.PROTOCOL_EVENT_ID);

	String studyId = request.getParameter(Constants.CP_SEARCH_CP_ID);
	String participantId = request.getParameter(Constants.CP_SEARCH_PARTICIPANT_ID);
	if (eventId == null)
	{
		eventId = (String) request.getSession().getAttribute(Constants.PROTOCOL_EVENT_ID);
		studyId = (String) request.getSession().getAttribute(Constants.CP_SEARCH_CP_ID);
		participantId = (String) request.getSession().getAttribute(
		AnnotationConstants.SELECTED_STATIC_ENTITY_RECORDID);

	}

	String XMLFileName = (String) request.getAttribute("XMLFileName");
	
	String key = (String) request.getSession().getAttribute(
			AnnotationConstants.TREE_VIEW_KEY);
	String[] keys = key.split("_");

	String objectName = keys[0];

	request.getSession().setAttribute(Constants.PROTOCOL_EVENT_ID, eventId);
		request.getSession().setAttribute(Constants.CP_SEARCH_CP_ID, studyId);
	
	boolean isDateSubmitted = Boolean.valueOf(request.getParameter("isDateSubmit")).booleanValue();
	boolean isComingFromDE = Boolean.valueOf(request.getParameter("comingFromDE")).booleanValue();

	String strRefreshTree = (String)request.getAttribute(Constants.IS_TO_REFRESH_TREE);
	boolean isToRefreshTree = Boolean.valueOf(strRefreshTree).booleanValue();
	request.removeAttribute(Constants.IS_TO_REFRESH_TREE);

	Boolean infiniteEntry = (Boolean) request.getAttribute(Constants.INFINITE_ENTRY);
	String treeViewUrl = "showEventTree.do?" + Constants.CP_SEARCH_PARTICIPANT_ID + "="
			+ participantId + "&" + Constants.CP_SEARCH_CP_ID + "=" + studyId + "&nodeId="
			+ key+ "&" + Utility.attachDummyParam();

	String flag = "disabled";
	if (infiniteEntry != null && infiniteEntry.booleanValue())
	{
		flag = "";
	}
	Object obj = request.getAttribute("eventEntryForm");
	String encounterDate = "";
	if(obj != null && obj instanceof EventEntryForm)
	{
			EventEntryForm form = (EventEntryForm)obj;
			encounterDate = form.getEncounterDate();			
	}

	String numberOfVisits = (String)request.getAttribute("numberOfVisits");
%>
<html>
<head>

<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/styleSheet.css" />
<link rel="STYLESHEET" type="text/css" href="<%=request.getContextPath()%>/dhtml_comp/css/dhtmlXGrid.css" />
<link rel="STYLESHEET" type="text/css" href="dhtml_comp/css/dhtmlXMenu.css">

<script src="<%=request.getContextPath()%>/jss/javaScript.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/jss/script.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/jss/ajax.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/dhtml_comp/jss/dhtmlXCommon.js"></script>
<script src="<%=request.getContextPath()%>/dhtml_comp/jss/dhtmlXGrid.js"></script>
<script src="<%=request.getContextPath()%>/dhtml_comp/jss/dhtmlXGridCell.js"></script>
<script src="<%=request.getContextPath()%>/dhtml_comp/jss/dhtmlXGrid_excell_link.js"></script>
<script src="<%=request.getContextPath()%>/jss/util-functions.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/jss/clear-default-text.js" type="text/javascript"></script>

<script language="JavaScript" src="<%=request.getContextPath()%>/dhtml_comp/jss/dhtmlXProtobar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/dhtml_comp/jss/dhtmlXMenuBar.js"></script>

<script>

	function verifyVisitSelection()
	{
		var visit = document.forms[0].visitNumber.value;
		if(visit == -1)
		{
			document.forms[0].visitNumber.value = "0";
		}
		return;
	}

			function initializeAnnotationsForm()
			{

			<%
			if (objectName != null && (objectName.equals("EventEntry")
					|| objectName.equals(Constants.FORM_CONTEXT_OBJECT)
					|| objectName.equals(Constants.FORM_ENTRY_OBJECT))
				)
			{
			%>	

			CSEntitiesGrid = new dhtmlXGridObject('gridForCSEntities');
			CSEntitiesGrid.setImagePath("dhtml_comp/imgs/");
			CSEntitiesGrid.enableAutoHeigth(true);
			CSEntitiesGrid.setHeader("Study Form Label,<div style='width:100%; text-align:center;'>Action</div>");
			CSEntitiesGrid.setInitWidthsP("*,30");
			CSEntitiesGrid.setColAlign("left,center");
			CSEntitiesGrid.setSkin("light");
			CSEntitiesGrid.setColTypes("ro,link");
			CSEntitiesGrid.enableAlterCss("even","uneven");
			CSEntitiesGrid.enableRowsHover(true,'grid_hover');
			CSEntitiesGrid.setColSorting("str,str");
			CSEntitiesGrid.setOnLoadingEnd(deleteXml);			
			CSEntitiesGrid.init();
			
			CSEntitiesGrid.loadXML("<%=XMLFileName%>");//,function(){CSEntitiesGrid.selectRow(0);  });

			<%
			}
			else
			{
			%>
				
			CSEntitiesGrid = new dhtmlXGridObject('gridForCSEntities');
			CSEntitiesGrid.setImagePath("dhtml_comp/imgs/");
			CSEntitiesGrid.enableAutoHeigth(true);
			CSEntitiesGrid.setHeader("<div style='width:100%; text-align:center;'>Visit #</div>,<div style='width:100%; text-align:center;'>Encountered Date</div>,Study Form Label,<div style='width:100%; text-align:center;'>Action</div>");
			CSEntitiesGrid.setInitWidthsP("15,25,*,15");
			CSEntitiesGrid.setColAlign("center,center,left,center");
			CSEntitiesGrid.setSkin("light");
			CSEntitiesGrid.setColTypes("ro,ro,ro,link");
			CSEntitiesGrid.enableAlterCss("even","uneven");
			CSEntitiesGrid.enableRowsHover(true,'grid_hover');
			CSEntitiesGrid.setColSorting("str,str,str,str");
			CSEntitiesGrid.setOnLoadingEnd(deleteXml);			
			CSEntitiesGrid.init();
		
			CSEntitiesGrid.loadXML("<%=XMLFileName%>");//,function(){CSEntitiesGrid.selectRow(0);  });
		<%
		}
		%>				
			<%
			    if(objectName!= null && objectName.equals(Constants.FORM_ENTRY_OBJECT))
			    {
			    flag="";
			%>
			    var btn = document.getElementById("addBtn");
			    btn.value="Add Records";
				
			<%}%>
			
			}


function deleteXml(rowId)
{

	var request = newXMLHTTPReq();	
	request.open("POST","LoadEventDetails.do",true);
	request.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
	request.send("&operation=deleteXML&XMLFileName=<%=XMLFileName%>");
}


			function onClickSubmit()
			{
			
				if(document.forms[0].CSActivityStatus.value == 'Closed')
				{
					alert('Clinical Study marked as closed');
				}
				else if(document.forms[0].participantActivityStatus.value == 'Closed')
				{
					alert('Participant marked as closed');
				}
				else if(document.forms[0].CSRActivityStatus.value == 'Closed')
				{
					alert('Clinical Study Registration marked as closed');
				}
				else
				{
			
					var str = "<%=key%>";
					var id =str;
					var i = str.indexOf('_');
					var obj1 = str.substring(0,i);
					var id1 = str.substring(i+1);
					var arr = id1.split('_');
					var eventId = arr[0];
					//alert('event: '+eventId);
					var formId = arr[1];
					var formContextId = arr[2];
					//recordId = arr[3];
				
					var recordId = null; 
					var recordIndex = id1.lastIndexOf('_')

					if (recordIndex !=  -1)
					{
						recordId = id1.substring(recordIndex+1);					
					}
					else
					{
						formId = id1;
						//alert("Object Name: "+obj1+" -:- EventId: "+formId);
					}

					 var btn = document.getElementById("addBtn");
					 var str= btn.value;
					 
					 if(str == "Add Records")
					 {
						 var action="";
						 recordId="0";
						 var urlString = "LoadDynamicExtentionsDataEntryPage.do?formId="+formId+"&<%=Constants.CP_SEARCH_PARTICIPANT_ID%>="+"<%=participantId%>"+"&<%=Constants.CP_SEARCH_CP_ID%>="+"<%=studyId%>"+"&"+"<%=Constants.FORWARD_CONTROLLER%>"+"=CSBasedSearch&formContextId="+formContextId+"&treeViewKey="+"<%=key%>"+"&<%=Constants.PROTOCOL_EVENT_ID%>=" + eventId+"&<%=Utility.attachDummyParam()%>";
				
				
						if (recordId != "0")
						{
							urlString = urlString + "&recordId="+recordId;
						}
						//window.parent.frames[2].location = urlString;
						document.forms[0].action = urlString;
						document.forms[0].submit();

					}
					else if(str == "GO")
					{

						var studyFormId = document.forms[0].studyForm.value;
						if(studyFormId == "-1")
						{
							return;
						}
						else
						{

							//var action="LoadEventDetails.do?addEntry=true&"+"<%=Constants.PROTOCOL_EVENT_ID%>"+"="+"<%=eventId%>"+"&"+"<%=Constants.CP_SEARCH_CP_ID%>" + "=" + "<%=studyId%>" + "&" + "<%= Constants.CP_SEARCH_PARTICIPANT_ID%>" + "=" + "<%=participantId%>";
							var action="LoadDynamicExtentionsDataEntryPage.do?formId="+formId+"&<%=Constants.CP_SEARCH_PARTICIPANT_ID%>="+"<%=participantId%>"+"&<%=Constants.CP_SEARCH_CP_ID%>="+"<%=studyId%>"+"&"+"<%=Constants.FORWARD_CONTROLLER%>"+"=CSBasedSearch&formContextId="+formContextId+"&treeViewKey="+"<%=key%>"+"&<%=Constants.PROTOCOL_EVENT_ID%>=" + eventId+"&<%=Utility.attachDummyParam()%>";

							document.forms[0].action = action;
							document.forms[0].submit();
						}
					}
				}
			}

			function disableEvent()
			{
				var action = "DisableEvents.do?"+"<%=Constants.PROTOCOL_EVENT_ID%>"+ "="+"<%=eventId%>";

				var go = confirm("Disabling any data will disable ALL its associated data also. Once disabled you will not be able to recover any of the data back from the system. Please refer to the user manual for more details. \n Do you really want to disable?");
				
				if (go==true)
				{	
					document.forms[0].action = action;
					document.forms[0].submit();
				}
				
				showTree();
				
			}
			function showTree()
			{
				window.parent.frames['<%=Constants.CP_TREE_VIEW%>'].location = "showEventTree.do?"+"<%=Constants.CP_SEARCH_PARTICIPANT_ID%>="+"<%=participantId%>"+"&<%=Constants.CP_SEARCH_CP_ID%>="+"<%=studyId%>";
			}

			function dateSubmit()
			{
				if(document.forms[0].CSActivityStatus.value == 'Closed')
				{
					alert('Clinical Study marked as closed');
				}
				else if(document.forms[0].participantActivityStatus.value == 'Closed')
				{
					alert('Participant marked as closed');
				}
				else if(document.forms[0].CSRActivityStatus.value == 'Closed')
				{
					alert('Clinical Study Registration marked as closed');
				}
				else
				{
				if (document.forms[0].encounterDate.value == "")
				{
					alert('Please enter date and submit');
					return false;
				}
				else
				{
					document.forms[0].submit();
				}
				}
			}
						
		</script>
<script>
	breadCrumbrequest('<%=key%>',<%=participantId%>,<%=studyId%>,null,<%=eventId%>,null);
</script>
</head>

<html:form styleId='eventEntryForm' action='EventEntryDateSubmit.do'>
	<body onload="initializeAnnotationsForm()">
	<html:errors />
	<%
		if ((objectName != null && objectName.equals("EventEntry"))
				|| (objectName != null && objectName.equals(Constants.FORM_CONTEXT_OBJECT)))
		{
	%>
	<table summary="" cellpadding="0" cellspacing="0" border="0"
		id="table1" class="contentPage" width="600">
		<tr>
			<td>
			<table valign="top" border="0" width="100%" cellspacing="0"
				cellpadding="3">
				<tr>
					<td>
						<html:hidden property="protocolEventId" value="<%= eventId%>"/>
						<html:hidden property="cpSearchCpId" value="<%= studyId%>"/>
						<html:hidden property="cpSearchParticipantId" value="<%= participantId%>"/>
						<html:hidden property="isDateSubmit" value="true"/>

					</td>
				</tr>
				<tr>
					<td class="formTitle" width="100%" colspan="4">&nbsp;</td>
				</tr>
				<tr>
					<td class="formRequiredNotice">&nbsp</td>
					
					<td class="formLabel">
					<label for="startDate">
							<bean:message key="eventEntry.encountedDate" />
					</label>
					</td>
				
				

					<td class="formFieldNoBordersSimpleGrayBackground">
						<%
							 if(encounterDate.trim().length() > 0)
							{
									Integer birthYear = new Integer(Utility.getYear(encounterDate ));
									Integer birthMonth = new Integer(Utility.getMonth(encounterDate ));
									Integer birthDay = new Integer(Utility.getDay(encounterDate ));
						%>
									<ncombo:DateTimeComponent name="encounterDate"
															  id="encounterDate"
															  formName="eventEntryForm"	
															  month= "<%=birthMonth %>"
															  year= "<%=birthYear %>"
															  day= "<%= birthDay %>" 
															  value="<%=encounterDate %>"
															  pattern="dd-MM-yyyy"
															  styleClass="formDateSized10"
																	 />		
						<% 
							}
							else
							{  
						 %>
									<ncombo:DateTimeComponent name="encounterDate"
															  id="encounterDate"
															  formName="eventEntryForm"	
															  pattern="dd-MM-yyyy"
															  styleClass="formDateSized10" 
																	 />		
						<%
							}
						%>
						<bean:message key="page.dateFormat" />&nbsp;
					 </td>

					<td class="formField">
					<!--<html:submit styleClass="actionButton" property="submit" onclick="dateSubmit()"></html:submit>-->
					<input type="button" name="disable"	value="Submit" class="actionButton"
								onclick="dateSubmit()" />
					</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
	<%
	}
	%>
	<table valign="top" border="0" height="80%" width="600" cellspacing="0"
		cellpadding="3">

		<tr valign="top" height="80%">
			<td width="2%"></td>
			<td><br>
			<table valign="top" width="100%" border='0' cellspacing="0"
				cellpadding="0">
				<!-- Main Page heading -->

				<tr height="2%">
					<td class="formTitle" height="25" colspan="3"><bean:message
						key="app.availableGrp" /></td>
				</tr>
				<tr valign="top" width="100%" height="100%">
					<td align="left">
					<table class="hdr" border="0" cellspacing="0"
						cellpadding="0" valign="top" width="100%" height="80%">

						<tr height="80%" valign="top">

							<td align="left" valign="top"><!--List of entities-->
							<div id="gridForCSEntities" width="100%" height="100%"
								style="background-color: white; overflow: hidden" />
							</td>
						</tr>
					</table>
					</td>
				</tr>
				<% 
				if(!"disabled".equals(flag)) {
					if(objectName!= null && objectName.equals(Constants.FORM_ENTRY_OBJECT)) {
					
				%>
				<tr align="center">
					<table align="center" border='0'>
						<tr>
							<br>
							<td align="center"><input type="button" name="addEntry" id="addBtn"
								value="Add Records" class="actionButton" onclick="onClickSubmit()"/>
							</td>
						</tr>
					</table>
				</tr>
				<%
					}
					else
					{
						
				%>
				<tr>
					<td > &nbsp;
					</td>
				</tr>
				
				
				<tr align="center">
					<td>
						<%@ include file="/pages/content/manageBioSpecimen/AddVisit.jsp" %>
					</td>
				</tr>

				<%
					}
				}
				else if(objectName!= null && objectName.equals(Constants.CLINICAL_STUDY_EVENT_OBJECT))
				{
				%>
					<tr>
						<td > &nbsp;
						</td>
					</tr>
					
					
					<tr align="center">
						<td>
							<%@ include file="/pages/content/manageBioSpecimen/AddVisit.jsp" %>
						</td>
					</tr>
				<%
				}
				%>
			</table>
			</td>
		</tr>

	</table>

	<html:hidden styleId="selectedStaticEntityId"
		property="selectedStaticEntityId" value="216" />
		
	<html:hidden styleId="participantActivityStatus"
		property="participantActivityStatus" />
		
		<html:hidden styleId="CSRActivityStatus"
		property="CSRActivityStatus" />

		<html:hidden styleId="CSActivityStatus"
		property="CSActivityStatus" />

	

<!--	<% if (!(isDateSubmitted && isComingFromDE && isToRefreshTree)) { %>-->
		
		<script language="javascript">
		//Added by Falguni to refresh participant tree 

<% if(isComingFromDE||isDateSubmitted||isToRefreshTree) {%>

		top.frames["cpAndParticipantView"].editParticipant();
		refreshTree('<%=Constants.CP_AND_PARTICIPANT_VIEW%>','<%=Constants.CP_TREE_VIEW%>','<%=Constants.CP_SEARCH_CP_ID%>','<%=Constants.CP_SEARCH_PARTICIPANT_ID%>','<%=key%>');	
<%}else {%> 

		selectTreeNodeOnly('<%=key%>');
<%}%>
		</script>
<!--	<%}	%>-->

	</body>

</html:form>
</html>