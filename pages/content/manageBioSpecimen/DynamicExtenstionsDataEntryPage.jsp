
<%@
	page import="edu.wustl.clinportal.action.annotations.AnnotationConstants,edu.wustl.clinportal.util.global.Constants,edu.wustl.clinportal.util.EventTreeObject"
%>
<%

String participantId = session.getAttribute(AnnotationConstants.SELECTED_STATIC_ENTITY_RECORDID).toString();
String protocol = (String)session.getAttribute(Constants.CP_SEARCH_CP_ID);
String 	treeViewKey = (String)session.getAttribute(AnnotationConstants.TREE_VIEW_KEY);

String dynExtRecordId = request.getParameter("recordId");
String[] keyArray = treeViewKey.split("_");

	if (dynExtRecordId == null)
	{
		dynExtRecordId = keyArray[keyArray.length - 1];
		//dynExtRecordId = (String)session.getAttribute(AnnotationConstants.DYNAMIC_ENTITY_RECORD_ID);
		//session.removeAttribute(AnnotationConstants.DYNAMIC_ENTITY_RECORD_ID);
	}
	
	//if (dynExtRecordId == null)
	//		dynExtRecordId = "0";
	
	if (keyArray[0].equals(Constants.FORM_CONTEXT_OBJECT))
	{
		treeViewKey = "";
		for (int i=0; i<keyArray.length - 1; i++)
		{
			treeViewKey = treeViewKey + keyArray[i] + "_";
		}
		treeViewKey = treeViewKey + dynExtRecordId;
	}
		
	//System.out.println("Treeview key : "+ treeViewKey );
	
Long dynEntContainerId = (Long)session.getAttribute(AnnotationConstants.FORM_ID);

String treeViewUrl = "showEventTree.do?" + Constants.CP_SEARCH_PARTICIPANT_ID + "="+ participantId + "&"+ Constants.CP_SEARCH_CP_ID + "="+protocol;
if (dynEntContainerId != null)
{
	treeViewUrl = treeViewUrl + "&nodeId=" + treeViewKey; 
}
String dynamicExtensionsDataEntryURL =(String) request.getAttribute(AnnotationConstants.DYNAMIC_EXTN_DATA_ENTRY_URL_ATTRIB);

%>
<html>
<table align="center">
<tr>
<td align="center">
	<span id=" spnLoading"  style=" display:inline;" ><font face="verdana" size="2">Loading...please wait </font></span>	
</td>
</tr><tr><td> <img src="images/loading1.gif" width="155"/></td></tr>
</table>
<!-- 
 <span id=" spnLoading"  style=" display:inline;" >Loading... Please wait
 <img src="images/await.gif" alt="image"/> </span> 
 <img src="images/images.jpg" />
 <img src="images/loading.gif" />
 -->
<head>
<script language=" javascript" >
		function hideLoadingMessage(){
			document.getElementById(' spnLoading' ).style.display = " none" ;
		}

</script>
</head>
<script src="jss/javaScript.js" type="text/javascript"></script>
<script>
	//window.parent.frames[1].location = "<%=treeViewUrl%>";
//refreshTree('<%=Constants.CP_AND_PARTICIPANT_VIEW%>','<%=Constants.CP_TREE_VIEW%>','<%=Constants.CP_SEARCH_CP_ID%>','<%=Constants.CP_SEARCH_PARTICIPANT_ID%>','<%=treeViewKey%>');
selectTreeNodeOnly('<%=treeViewKey%>');
	document.location.href = "<%=dynamicExtensionsDataEntryURL%>";
</script>

</html>