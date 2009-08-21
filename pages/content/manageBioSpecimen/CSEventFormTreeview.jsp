<%@page import="edu.wustl.clinportal.util.global.Utility"%>
<html>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/nlevelcombo.tld" prefix="ncombo"%>
<%@ page import="java.util.*"%>
<%@ page import="edu.wustl.common.tree.QueryTreeNodeData"%>
<%@ page import="edu.wustl.clinportal.util.global.Constants"%>

<%
			String participantId = (String) request
			.getAttribute(Constants.CP_SEARCH_PARTICIPANT_ID);
	String cpId = (String) request.getAttribute(Constants.CP_SEARCH_CP_ID);
	request.getSession().setAttribute(Constants.CP_SEARCH_CP_ID, cpId);
	String access = null;
	access = (String) session.getAttribute("Access");
	String divHeight = "200";
	if (access != null && access.equals("Denied"))
	{
		divHeight = "280";
	}
	if (participantId == null || participantId.equals(""))
		participantId = "0";
	if (cpId == null || cpId.equals(""))
		cpId = "0";
%>
<head>
<link rel="stylesheet" type="text/css" href="css/styleSheet.css" />
<title>DHTML Tree samples. dhtmlXTree - Action handlers</title>
<link rel="STYLESHEET" type="text/css"
	href="dhtml_comp/css/dhtmlXTree.css">
<script language="JavaScript" type="text/javascript"
	src="dhtml_comp/js/dhtmXTreeCommon.js"></script>
<script language="JavaScript" type="text/javascript"
	src="dhtml_comp/js/dhtmlXTree.js"></script>
<script language="JavaScript" type="text/javascript"
	src="jss/javaScript.js"></script>
</head>

<body>
<table border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td class="formLabelAllBorder" colspan="2" width="161"><b>Event
		Details :</b></td>
	</tr>
	<tr>
		<td align="left" colspan="2">
		<div id="treeboxbox_tree"
			style="width: 166; height: <%= divHeight %>; background-color: #f5f5f5; border: 1px solid Silver;; overflow: auto;" />
		</td>

	</tr>
	<tr>
		<td colspan="2"><input type="hidden" name="participantId"
			value="<%=participantId%>" /> <input type="hidden" name="cpId"
			value="<%=cpId%>" /> <input type="hidden" id="parentKeyId" /></td>
	</tr>
</table>

<script language="javascript">
			//This function is called when any of the node is selected in tree 
			function tonclick(id)
			{
				var str = id;
				var name = tree.getItemText(id);
				//alert("String : "+str + "--"+name);
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
					//var formIndex = id1.indexOf('_');
					//formId = id1.substring(0, formIndex);
					//alert("Id1: "+id1+"-"+formId);
					//formContextId = id1.substring(formIndex+2, id1.indexOf('_'));
					//alert("Object Name: "+obj1+" -:- FormId: "+formId+" -:- FormContext Id: "+formContextId + "-:- RecordId: "+recordId);
				}
				else
				{
					formId = id1;
					//alert("Object Name: "+obj1+" -:- EventId: "+formId);
				}
				
				if(obj1 == "<%=Constants.CLINICAL_STUDY_EVENT_OBJECT%>")
				{
					<%if(access != null && access.equals("Denied"))
					{%>
					//window.parent.frames[2].location = "CPQuerySpecimenCollectionGroupForTech.do?pageOf=pageOfSpecimenCollectionGroupCPQuery&operation=edit&id="+id1+"&name="+name;
					<%}else {%>
					window.parent.frames[2].location = "LoadEventDetails.do?"+"<%=Constants.CP_SEARCH_PARTICIPANT_ID%>="+<%=participantId%>+"&<%=Constants.CP_SEARCH_CP_ID%>="+<%=cpId%> +"&treeViewKey="+id+ "&<%=Constants.PROTOCOL_EVENT_ID%>=" +eventId+"&<%=Utility.attachDummyParam()%>";
					<%}%>
				}
				else if(obj1 == "<%=Constants.FORM_CONTEXT_OBJECT%>")
				{
					var urlString = "LoadDynamicExtentionsDataEntryPage.do?formId="+formId+"&<%=Constants.CP_SEARCH_PARTICIPANT_ID%>="+<%=participantId%>+"&<%=Constants.CP_SEARCH_CP_ID%>="+<%=cpId%>+"&"+"<%=Constants.FORWARD_CONTROLLER%>"+"=CSBasedSearch&formContextId="+formContextId+"&treeViewKey="+id+"&<%=Constants.PROTOCOL_EVENT_ID%>=" + eventId+"&<%=Utility.attachDummyParam()%>";
					if (recordId != "0")
					{
						urlString = urlString + "&recordId="+recordId;
					}
					window.parent.frames[2].location = urlString;
				}
				else  if(obj1 == "<%=Constants.EVENT_ENTRY_OBJECT%>")
				{
					window.parent.frames[2].location = "LoadEventDetails.do?"+"<%=Constants.CP_SEARCH_PARTICIPANT_ID%>="+<%=participantId%>+"&<%=Constants.CP_SEARCH_CP_ID%>="+<%=cpId%> + "&<%=Constants.PROTOCOL_EVENT_ID%>=" +eventId+"&entryId="+formId+"&treeViewKey="+id+"&<%=Utility.attachDummyParam()%>";
				}
				

			};
									
			// Creating the tree object								
			tree=new dhtmlXTreeObject("treeboxbox_tree","100%","100%",0);


			tree.setImagePath("dhtml_comp/imgs/");
			tree.setOnClickHandler(tonclick);
			<%-- in this tree for root node parent node id is "0" --%>
			<%-- creating the nodes of the tree --%>
			<% Vector treeData = (Vector)request.getAttribute("treeData");
				if(treeData != null && treeData.size() != 0)
				{
					Iterator itr  = treeData.iterator();
					while(itr.hasNext())
					{
						QueryTreeNodeData data = (QueryTreeNodeData) itr.next();
						String parentId = "0";	
						if(!data.getParentIdentifier().equals("0"))
						{
							parentId = data.getParentObjectName() + "_"+ data.getParentIdentifier().toString();
		
						}
						String nodeId = data.getObjectName() + "_"+data.getIdentifier().toString();
						String img = "results.gif";
						if(data.getObjectName().equals(Constants.CLINICAL_STUDY_EVENT_OBJECT))
						{
							img = "Specimen.GIF";
							//nodeId = data.getObjectName() + "_"+data.getIdentifier().toString();
						}
						else if (data.getObjectName().startsWith(Constants.FORM_CONTEXT_OBJECT))
						{
							String objName = data.getObjectName();
							int index = objName.lastIndexOf("_");
							String recordId = objName.substring(index+1);
							if (recordId != null && recordId.equals("0"))
								img = "SpecimenCollectionGroupFuture.jpg";
							else
								img = "SpecimenCollectionGroup.GIF";
													
							nodeId = data.getObjectName();
						}
				
			%>
					tree.insertNewChild("<%=parentId%>","<%=nodeId%>","<%=data.getDisplayName()%>",0,"<%=img%>","<%=img%>","<%=img%>","");
					tree.setUserData("<%=nodeId%>","<%=nodeId%>","<%=data%>");	
			<%	
					}
				}

			%>
			tree.closeAllItems("0");
			<%if(request.getParameter("nodeId") != null)
			{
				String nodeId = request.getParameter("nodeId");
				
			%>
			<%-- opening and selecting the node which is selected by user --%>
			var parentId = tree.getParentId("<%=nodeId%>");
			tree.openItem(parentId);
			tree.selectItem("<%=nodeId%>",false);
			tree.openItem("<%=nodeId%>");
			document.getElementById('parentKeyId').value = parentId;
			
			<%}%>	
										
	
			
	</script>


</body>

</html>