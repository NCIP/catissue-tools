<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/nlevelcombo.tld" prefix="ncombo" %>
<%@ page import="edu.wustl.clinportal.util.global.Constants"%>
<%@ page import="edu.wustl.clinportal.util.global.Utility"%>
<%@ include file="/pages/content/common/CollectionProtocolCommon.jsp" %>
<%@ page import="java.util.*"%>

<%
   String participantId = null;
   String cpId = null;
   String access = null;
   access = (String)session.getAttribute("Access");    
%>
 
<html>
<head>
<link rel="stylesheet" type="text/css" href="css/styleSheet.css" />
<script language="JavaScript" type="text/javascript" src="jss/javaScript.js"></script>

  <script language="JavaScript">

	//Added By Baljeet For flex
	var interfaceObj;
    var jsReady = false;
	function callFlexMethod()
	{
       if(navigator.appName.indexOf("Microsoft")!=-1)
		{
			interfaceObj = window.document.getElementById("Layout");
		}
		else
		{
			interfaceObj = document["Layout"];
		}
	}
	
	// called to check if the page has initialized and JavaScript is available, called by flex
	function isReady() 
	{
		
		return jsReady;
	}
	
	function refreshParticipants()
	{       
        jsReady = true;
		callFlexMethod();
		
		//Invoking the Action script method passing the Selected Tree node Id 
		interfaceObj.refreshParticipants();
	}


	function selectTreeNodeOnly(nodeId)
	{

		jsReady = true;
		callFlexMethod();
		
		//Invoking the Action script method passing the Selected Tree node Id 
		interfaceObj.selectNode(nodeId);
	}



	function pageInit(nodeId)
	{
       
		//alert("Node id is:"+nodeId);
        jsReady = true;
		callFlexMethod();
		
		//Invoking the Action script method passing the Selected Tree node Id 
		interfaceObj.myFlexMethod(nodeId);
	}
	function refreshCpParticipants(participantId)
	{
        
		 callFlexMethod();
	     interfaceObj.editParticipant(participantId,"");

	}



	/**
	Added treeNode so that on node from tree is selected 
	*/
	function editParticipant(participantId,treeNodeId)
	{
		 callFlexMethod();
	     interfaceObj.editParticipant(participantId,treeNodeId);
	
	}

	function onParticipantClick(pId,cpId,refParticipants)
	{
	  window.parent.frames[1].location ="QueryParticipantSearch.do?pageOf=pageOfParticipantCPQueryEdit&operation=edit&<%=Constants.CP_SEARCH_CP_ID%>="+cpId+"&id="+pId;
	}
  
    /*
       To register a participant for a CP
	*/
	function RegisterParticipants(cpId,refParticipants)
	{
      
		if(cpId == "-1")
		{
				alert("please select collection protocol.");
		}    
        else
		{
			window.parent.frames[1].location = "QueryParticipant.do?operation=add&pageOf=<%=Constants.PAGE_OF_PARTICIPANT_CP_QUERY%>&clearConsentSession=true&<%=Constants.CP_SEARCH_CP_ID%>="+cpId+"&refresh="+refParticipants;
		} 
	}

    /*
       This method is invoked on click of a tree node
	*/
	function tonclick(id, cpId, pId, name)
	{    

				var str = id;
			//	var name = tree.getItemText(id);
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
				}
				else
				{
					formId = id1;
				}

		if(obj1 == "<%=Constants.CLINICAL_STUDY_EVENT_OBJECT%>")
				{
					<%if(access != null && access.equals("Denied"))
					{%>
					//window.parent.frames[2].location = "CPQuerySpecimenCollectionGroupForTech.do?pageOf=pageOfSpecimenCollectionGroupCPQuery&operation=edit&id="+id1+"&name="+name;
					<%}else {%>
					window.parent.frames[1].location = "LoadEventDetails.do?"+"<%=Constants.CP_SEARCH_PARTICIPANT_ID%>="+pId+"&<%=Constants.CP_SEARCH_CP_ID%>="+cpId+"&treeViewKey="+id+ "&<%=Constants.PROTOCOL_EVENT_ID%>=" +eventId+"&<%=Utility.attachDummyParam()%>";
					<%}%>
				}
				else if(obj1 == "<%=Constants.FORM_CONTEXT_OBJECT%>" || obj1 == "<%=Constants.SINGLE_RECORD_FORM%>" || obj1 == "<%=Constants.SINGLE_RECORD_FORM_EDIT%>")
				{
					var urlString = "LoadDynamicExtentionsDataEntryPage.do?formId="+formId+"&<%=Constants.CP_SEARCH_PARTICIPANT_ID%>="+pId+"&<%=Constants.CP_SEARCH_CP_ID%>="+cpId+"&"+"<%=Constants.FORWARD_CONTROLLER%>"+"=CSBasedSearch&formContextId="+formContextId+"&treeViewKey="+id+"&<%=Constants.PROTOCOL_EVENT_ID%>=" + eventId+"&<%=Utility.attachDummyParam()%>";
					if (recordId != "0")
					{
						urlString = urlString + "&recordId="+recordId;
					}
					
					window.parent.frames[1].location = urlString;
				}
				else
				{
					window.parent.frames[1].location = "LoadEventDetails.do?"+"<%=Constants.CP_SEARCH_PARTICIPANT_ID%>="+pId+"&<%=Constants.CP_SEARCH_CP_ID%>="+cpId + "&<%=Constants.PROTOCOL_EVENT_ID%>=" +eventId+"&entryId="+formId+"&treeViewKey="+id+"&<%=Utility.attachDummyParam()%>";
				}
				

	};
 </script> 

</head>
<body>
   <html:messages id="messageKey" message="true" header="messages.header" footer="messages.footer">
	
   </html:messages>

   <html:errors/>
   
 <object classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000" id="Layout" width="100%" height="99.9%">
		<param name=movie value="flexclient/biospecimen/Layout.swf"> 
		<param name=quality value=high>
		<param name="swliveconnect" value="true">
				 
		<embed src="flexclient/biospecimen/Layout.swf" quality="high" bgcolor="#ffffff"
			width="100%" height="99.9%" id="Layout" name="Layout" align="middle"
			play="true"
			loop="false"
			quality="high"
			allowScriptAccess="sameDomain"
			type="application/x-shockwave-flash"
			pluginspage="http://www.adobe.com/go/getflashplayer">
		</embed>
   
   </object>
   <!--
  <input type="hidden" name="participantId" id="participantId"/>
<input type="hidden" name="cpId" id="cpId"/>
-->
</body>
</html>