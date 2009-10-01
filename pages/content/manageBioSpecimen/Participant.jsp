						 <%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/nlevelcombo.tld" prefix="ncombo" %>

<%@ page import="java.util.List,java.util.Iterator"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Hashtable"%>
<%@ page import="java.util.Calendar"%>
<%@ page import="java.util.Collection"%>
<%@ page import="edu.wustl.common.beans.NameValueBean"%>
<%@ page import="edu.wustl.clinportal.bean.ConsentResponseBean"%>
<%@ page import="edu.wustl.clinportal.bean.ConsentBean"%>
<%@ page import="edu.wustl.clinportal.util.global.Constants"%>

<%@ page import="edu.wustl.clinportal.actionForm.ParticipantForm"%>
<%@ page import="edu.wustl.clinportal.util.global.Utility"%>
<%@ page import="edu.wustl.clinportal.util.global.Variables"%>
<%@ page import="edu.wustl.clinportal.util.CatissueCoreCacheManager"%>
<%@ page import="edu.wustl.clinportal.bizlogic.AnnotationUtil"%>
<%@ page import="edu.wustl.clinportal.action.annotations.AnnotationConstants"%>

<%@ include file="/pages/content/common/AutocompleterCommon.jsp" %> 


<script src="jss/ajax.js"></script>	  	
<script src="jss/script.js"></script>
<!-- Mandar 11-Aug-06 : For calendar changes --> 
<script src="jss/calendar.js"></script>
<script src="jss/calendarComponent.js"></script>
<script src="jss/titli.js"></script>
<script src="jss/wz_tooltip.js"></script>
<SCRIPT>var imgsrc="images/";</SCRIPT>
<LINK href="css/calanderComponent.css" type=text/css rel=stylesheet>
<LINK href="css/catissue_suite.css" type=text/css rel=stylesheet>
<!-- Mandar 11-Aug-06 : calendar changes end -->

<style>
.active-column-0 {width:30px}
tr#hiddenCombo
{
 display:none;
}
</style>

<script src="jss/script.js" type="text/javascript"></script>
<%@ include file="/pages/content/common/BioSpecimenCommonCode.jsp" %>
<% 
		String parentUrl = null;
		String cpId = null;
		List siteList = (List)request.getAttribute(Constants.SITELIST);
		List collectionProtocolList = (List)request.getAttribute(Constants.PROTOCOL_LIST);
		
// List of clinical study IDs for which current user is PI or coordinator
		List clinicalStudiesForUserList = new ArrayList();
		
		if(collectionProtocolList!=null)
		{
			Iterator iterator = collectionProtocolList.iterator();
			while(iterator.hasNext())
			{
				NameValueBean bean = (NameValueBean)iterator.next();
				clinicalStudiesForUserList.add(bean.getValue());
			}
		}
		session.setAttribute("csForUser",clinicalStudiesForUserList);
		String participantId=(String)request.getAttribute("participantId");
		
		String submittedFor=(String)request.getAttribute(Constants.SUBMITTED_FOR);		
		String forwardTo=(String)request.getAttribute(Constants.FORWARD_TO);		
		boolean isRegisterButton = false;
		boolean isAddNew = false;
		boolean isSpecimenRegistration = true;
		String operation = (String)request.getAttribute(Constants.OPERATION);
		String formName, pageView=operation,editViewButton="buttons."+Constants.EDIT;
		boolean readOnlyValue=false,readOnlyForAll=false;
		String pageOf = (String)request.getAttribute(Constants.PAGEOF);
				
		String staticEntityName=null;
		staticEntityName = AnnotationConstants.ENTITY_NAME_PARTICIPANT;
		
		Long participantEntityId = null;
				if (CatissueCoreCacheManager.getInstance().getObjectFromCache("participantEntityId") != null)
		{
			participantEntityId = (Long) CatissueCoreCacheManager.getInstance().getObjectFromCache("participantEntityId");
		}
		else
		{
			participantEntityId = AnnotationUtil.getEntityId(AnnotationConstants.ENTITY_NAME_PARTICIPANT);
			CatissueCoreCacheManager.getInstance().addObjectToCache("participantEntityId",participantEntityId);		
		}
		
		String id = request.getParameter("id");

		 pageView=operation;
		if(operation.equals(Constants.EDIT))
		{
			editViewButton="buttons."+Constants.VIEW;
			formName = edu.wustl.clinportal.util.global.Constants.PARTICIPANT_EDIT_ACTION;
			readOnlyValue=true;
			if(pageOf.equals(edu.wustl.common.util.global.Constants.QUERY))
				formName = Constants.QUERY_PARTICIPANT_EDIT_ACTION + "?pageOf="+pageOf;
			if(pageOf.equals(Constants.PAGE_OF_PARTICIPANT_CP_QUERY))
			{
				formName = Constants.CP_QUERY_PARTICIPANT_EDIT_ACTION + "?pageOf="+pageOf;
			}
		}
		else
		{
			formName = Constants.PARTICIPANT_LOOKUP_ACTION;
			if(pageOf.equals(Constants.PAGE_OF_PARTICIPANT_CP_QUERY))
			{
				formName = Constants.CP_QUERY_PARTICIPANT_LOOKUP_ACTION;
			}

			readOnlyValue=false;
		}


		Object obj = request.getAttribute("participantForm");
		int noOfRows=0;
		int noOrRowsCollectionProtocolRegistration = 0;
		Map map = null;
		Map mapCollectionProtocolRegistration = null;
		String currentBirthDate = "";
		String currentDeathDate = "";
		String partActivityStatus="";
		int regID = 0;
		if(obj != null && obj instanceof ParticipantForm)
		{
			ParticipantForm form = (ParticipantForm)obj;
			map = form.getValues();
			mapCollectionProtocolRegistration = form.getCollectionProtocolRegistrationValues();
			noOfRows = form.getValueCounter();
			noOrRowsCollectionProtocolRegistration = form.getCollectionProtocolRegistrationValueCounter();
			currentBirthDate = form.getBirthDate(); 
			currentDeathDate = form.getDeathDate();
			partActivityStatus=form.getActivityStatus();
		}
	
		if(noOfRows == 0)
		{
			noOfRows =1;
		}
		
		if(operation.equals(Constants.ADD))
		{
			if(noOrRowsCollectionProtocolRegistration == 0)
			{
				noOrRowsCollectionProtocolRegistration =1;
			}
		}
		
	
%>

<head>
	<script language="JavaScript" type="text/javascript" src="jss/javaScript.js"></script>
	
	<%
	String participantIdentifier="0";
	List columnList = (List) request.getAttribute(edu.wustl.common.util.global.Constants.SPREADSHEET_COLUMN_LIST);
	List dataList = (List) request.getAttribute(Constants.SPREADSHEET_DATA_LIST);

		
	String title = "ParticipantList";

	boolean isSpecimenData = false;

	int IDCount = 0;
	%>
	
	<script language="JavaScript">
		//function to insert a row in the inner block
		function insRow(subdivtag)
		{
			var val = parseInt(document.forms[0].valueCounter.value);
			val = val + 1;
			document.forms[0].valueCounter.value = val;
			
			var r = new Array(); 
			r = document.getElementById(subdivtag).rows;
			var q = r.length;
			var x=document.getElementById(subdivtag).insertRow(q);
			
			// First Cell

			//Second Cell
			var spreqtype=x.insertCell(0);
			spreqtype.className="black_ar_s";
			sname="";
			var identifier = "value(ParticipantMedicalIdentifier:" + (q+1) +"_id)";
			sname = sname + "<input type='hidden' name='" + identifier + "' value='' id='" + identifier + "'>";
			var name = "value(ParticipantMedicalIdentifier:" + (q+1) + "_Site_id)";
// Mandar : 434 : for tooltip 
			sname = sname +"<select name='" + name + "' size='1' class='black_new' id='" + name + "'>";
			<%
				if(siteList!=null)
				{
					Iterator iterator = siteList.iterator();
					while(iterator.hasNext())
					{
						NameValueBean bean = (NameValueBean)iterator.next();
			%>
						sname = sname + "<option value='<%=bean.getValue()%>'><%=bean.getName()%></option>";
			<%		}
				}
			%>
			sname = sname + "</select>";
			spreqtype.innerHTML="" + sname;
		
			//Second Cellvalue(ParticipantMedicalIdentifier:1_medicalRecordNumber)
			var spreqsubtype=x.insertCell(1);
			spreqsubtype.className="black_new";
			spreqsubtype.colSpan=2;
			sname="";
		
			name = "value(ParticipantMedicalIdentifier:" + (q+1) + "_medicalRecordNumber)";
			sname= "";// sneha: changed only class  attribute to "black_new"			
			sname="<input type='text' name='" + name + "' maxlength='50'  class='black_new' id='" + name + "'  onkeypress='alphaNumOnly(this);' onchange='alphaNumOnly(this);' onkeyup='alphaNumOnly(this);' >";
			spreqsubtype.innerHTML="" + sname;
			
			//Third Cell
			var checkb=x.insertCell(2);
			checkb.className="black_new";
			checkb.colSpan=3;
			sname="";
			
			var identifier = "value(ParticipantMedicalIdentifier:" + (q+1) +"_id)";
			sname = sname + "<input type='hidden' name='" + identifier + "' value='' id='" + identifier + "'>";

			var name = "chk_"+(q+1);
			sname = sname +"<input type='checkbox' name='" + name +"' id='" + name +"' value='C' onClick=\"enableButton(document.forms[0].deleteMedicalIdentifierValue,document.forms[0].valueCounter,'chk_')\">";
			checkb.innerHTML=""+sname;
		}
		
		
		
		
		function getConsent(identifier,collectionProtocolId,collectionProtocolTitle,index,anchorTagKey,consentCheckStatus)
		{
			var collectionProtocolIdValue;
			var select = document.getElementById(collectionProtocolId); 
			collectionProtocolIdValue=document.getElementById(collectionProtocolId).value;
			var dataToSend="showConsents=yes&<%=Constants.CP_SEARCH_CP_ID%>="+collectionProtocolIdValue;
			ajaxCall(dataToSend, collectionProtocolId, identifier, anchorTagKey, index,consentCheckStatus);
		}
		
		function openConsentPage(collectionProtocolId,index,responseString){
			var  csrStatus = "";
			if(document.getElementById("csrStatus")!= null && document.getElementById("csrStatus").value != null )
			{
				if(document.getElementById("csrStatus").value == 'Closed')
				{
					csrStatus= 'Closed';
				}

			}
			
			if(responseString == "<%=Constants.NO_CONSENTS_DEFINED%>")
			{
				return;
			}
			
			var collectionProtocolIdValue=document.getElementById(collectionProtocolId).value;
			if(collectionProtocolIdValue=="-1")
			{
				alert("Please select collection protocol");
				return;
			}
			
			var url ="ConsentDisplay.do?operation=<%=operation%>&pageOf=pageOfConsent&index="+index+"&<%=Constants.CP_SEARCH_CP_ID%>="+collectionProtocolIdValue+"&partActivityStatus=<%=partActivityStatus%>"+"&csrActivityStatus="+csrStatus;
			window.open(url,'ConsentForm','height=300,width=800,scrollbars=1,resizable=1');
		}
		
		var flag=false;
		//Ajax Code Start
		function ajaxCall(dataToSend, collectionProtocolId, identifier,anchorTagKey,index,consentCheckStatus)
		{
			if(flag==true)
			{
				return;
			}
			flag=true;
			var request = newXMLHTTPReq();
			request.onreadystatechange=function(){checkForConsents(request, collectionProtocolId, identifier,anchorTagKey,index,consentCheckStatus)};
			//send data to ActionServlet
			//Open connection to servlet
			request.open("POST","CheckConsents.do",true);
			request.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
			request.send(dataToSend);
		}

		function checkForConsents(request, collectionProtocolId,  verificationKey, anchorTagKey,index,consentCheckStatus)
		{
			
			if(request.readyState == 4)
			{  
				//Response is ready
				if(request.status == 200)
				{
					var responseString = request.responseText;
					validateBarcodeLable=responseString;
					var anchorTag = document.getElementById(anchorTagKey);
					var spanTag = document.getElementById(consentCheckStatus);
					var consentResponseKey = "collectionProtocolRegistrationValue(ClinicalStudyRegistration:" + index +"_isConsentAvailable)";;
					if(responseString=="<%=Constants.PARTICIPANT_CONSENT_ENTER_RESPONSE%>")
					{
						spanTag.innerHTML="";
						if(anchorTag == null)
						{
							anchorTag = document.createElement("a");
						}
						anchorTag.setAttribute("id",anchorTagKey);
						anchorTag.setAttribute("href", "javascript:openConsentPage('"+collectionProtocolId+"','"+index+"','"+responseString+"')");
						anchorTag.innerHTML=responseString+"<input type='hidden' name='" + verificationKey + "' value='Consent' id='" + verificationKey + "'/><input type='hidden' name='" + consentResponseKey+ "' value='" +responseString+ "' id='" + consentResponseKey+ "'/>";
						spanTag.appendChild(anchorTag);
					}
					else //No Consent
					{
						spanTag.innerHTML=responseString+"<input type='hidden' name='" + verificationKey + "' value='Consent' id='" + verificationKey + "'/><input type='hidden' name='" + consentResponseKey+ "' value='" +responseString+ "' id='" + consentResponseKey+ "'/>";
					}
					
					
					flag=false;
				}
			}
		}
		//this function will allow only alphanumeric ,underscore and hash sign for Id field
		function alphaNumOnly(field) 
		{
			if(field.value.length>0) 
			{
				field.value = field.value.replace(/[^a-zA-Z 0-9-_  # ]+/g,''); 
			}
				
		}
		
		function textLimit(field) 
		{
			if(field.value.length>0) 
				field.value = field.value.replace(/[^\d]+/g, '');
				
			/*if (element.value.length > maxlen + 1)
				alert('your input has been truncated!');*/
			/*if (field.value.length > maxlen)
			{
				//field.value = field.value.substring(0, maxlen);
				field.value = field.value.replace(/[^\d]+/g, '');
			}*/
		}
		function intOnly(field) 
		{
			if(field.value.length>0) 
			{
				field.value = field.value.replace(/[^\d]+/g, ''); 
			}
		}
		//this function is called when participant clicks on radiao button 
		function onParticipantClick(participant_id)
		{
			//mandar for grid
			var cl = mygrid.cells(participant_id,mygrid.getColumnCount()-1);
			var pid = cl.getValue();			
			//alert(pid);
			//participant_id = pid;
			//------------
			//document.forms[0].participantId.value=participant_id;
			document.forms[0].participantId.value=pid;
			document.forms[0].id.value=pid;
			document.forms[0].forwardTo.value="pageOfParticipant";
			document.forms[0].action="ParticipantRegistrationSelect.do?operation=edit&pageOf=pageOfParticipant";
			
			<%if(pageOf.equals(Constants.PAGE_OF_PARTICIPANT_CP_QUERY))
			{%>
					document.forms[0].forwardTo.value="pageOfParticipantCPQuery";
					document.forms[0].action="CPQueryParticipantRegistrationSelect.do?operation=edit&pageOf=pageOfParticipantCPQuery";
			<%
			}
			%>
			
			document.forms[0].radioValue.value="";
			for (var i = 0 ; i < document.forms[0].chkName.length; i++)
			{
				document.forms[0].chkName[i].checked = false;
			}
			
		}
		//This Function is called when user clicks on 'Add New Participant' Button
		function AddParticipant()
		{
			document.forms[0].action="<%=Constants.PARTICIPANT_ADD_ACTION%>";
			<%if(pageOf.equals(Constants.PAGE_OF_PARTICIPANT_CP_QUERY))
			{%>
			document.forms[0].action="<%=Constants.CP_QUERY_PARTICIPANT_ADD_ACTION%>";
			<%}%>
			document.forms[0].submit();
		}
		//This function is called when user clicks on 'Use Selected Participant' Button
		function UseSelectedParticipant()
		{
		
			if(document.forms[0].participantId.value=="" || document.forms[0].participantId.value=="0")
			{
				alert("Please select the Participant from the list");
			}
			else
			{
			
				document.forms[0].action="ParticipantSelect.do?operation=add&id="+document.forms[0].participantId.value;
				alert(document.forms[0].action);
				document.forms[0].submit();
				//window.location.href="ParticipantSelect.do?operation=add&participantId="+document.forms[0].participantId.value+"&submittedFor="+document.forms[0].submittedFor.value+"&forwardTo="+document.forms[0].forwardTo.value;
			}
			
		}
		
		function CreateNewClick()
		{
			document.forms[0].radioValue.value="Add";
			document.forms[0].action="<%=Constants.PARTICIPANT_ADD_ACTION%>";
			<%if(pageOf.equals(Constants.PAGE_OF_PARTICIPANT_CP_QUERY))
			{%>
				document.forms[0].action="<%=Constants.CP_QUERY_PARTICIPANT_ADD_ACTION%>";
			<%}%>
		}
	
		function LookupAgain()
		{
			document.forms[0].radioValue.value="Lookup";
		}
		
		
		function onVitalStatusRadioButtonClick(element)
		{
		
			if(element.value == "Dead")
			{
				document.forms[0].deathDate.disabled = false;				
			}
			else
			{
				document.forms[0].deathDate.disabled = true;
			}
		}		 
		
		//View SPR Vijay pande
		function viewSPR()
		{
			<% Long reportId=(Long)session.getAttribute(Constants.IDENTIFIED_REPORT_ID); %>
			var reportId='<%=reportId%>';
			if(reportId==null || reportId==-1)
			{
				alert("There is no associate report in the system!");
			}
			else
			{
				var tempId=document.forms[0].id.value;
		    	var action="<%=Constants.VIEW_SPR_ACTION%>?operation=viewSPR&pageOf=<%=pageOf%>&id="+tempId;
				document.forms[0].action=action;
				document.forms[0].submit();
			}
		}
        function editParticipant()
		{
			var tempId=document.forms[0].id.value;
			var action="SearchObject.do?pageOf=<%=pageOf%>&operation=search&id="+tempId;
			if('<%=pageOf%>'=='<%=Constants.PAGE_OF_PARTICIPANT_CP_QUERY%>')
			{
				action="QueryParticipantSearch.do?pageOf=pageOfParticipantCPQueryEdit&operation=search&id="+tempId;
			}
			document.forms[0].action=action;
			document.forms[0].submit();
		}	 
		function showAnnotations()
		{
			var fwdPage="<%=pageOf%>";				
			var action="DisplayAnnotationDataEntryPage.do?entityId=<%=participantEntityId%>&entityRecordId=<%=id%>&staticEntityName=<%=staticEntityName%>&pageOf="+fwdPage+"&operation=viewAnnotations";		
			document.forms[0].action=action;
			document.forms[0].submit();
		}
		function setTarget()
		{
			var fwdPage="<%=pageOf%>";
			if(!fwdPage=="pageOfParticipantCPQuery")
				document.forms[0].target = '_top';
		}
		
	</script>
</head>

<html:form action="<%=formName%>">
<html:hidden property="cpId" />
	<%
	if(pageView.equals("add"))
	{
	%>
		<%@ include file="EditParticipant.jsp" %>		
	<%
	}
	%>

	<%
	if(pageView.equals("edit"))
	{
	%>

	<%@ include file="EditParticipant.jsp" %>
	<%
	}
	%>
	
	<%-- this is done at the end beacuse we want to set CpId value --%>
	
	
		<%
	
	String refreshTree = (String)request.getAttribute("refresh");
   
	if(refreshTree==null || refreshTree.equalsIgnoreCase("true"))
	{
	%>
	<script language="javascript">
	//Modified for flex by Baljeet
	//Modified by Falguni Sachde
	//Bug:6072 In case of LHS menu selection this property will not available.	
	 if(top.frames["cpAndParticipantView"] != undefined)
	 {
	 	top.frames["cpAndParticipantView"].refreshCpParticipants(<%=participantId%>);
	  //   refreshTree('<%=Constants.CP_AND_PARTICIPANT_VIEW%>','<%=Constants.CP_TREE_VIEW%>','<%=Constants.CP_SEARCH_CP_ID%>','<%=Constants.CP_SEARCH_PARTICIPANT_ID%>','');
	 }
	</script>

	<%}
	%>
		
	</html:form>
