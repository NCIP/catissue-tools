<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/nlevelcombo.tld" prefix="ncombo" %>
<%@ page import="edu.wustl.clinportal.util.global.Constants"%>
<%@ page import="edu.wustl.clinportal.actionForm.ClinicalStudyRegistrationForm"%>
<%@ page import="edu.wustl.clinportal.util.global.Utility"%>
<%@page import="edu.wustl.clinportal.actionForm.ConsentTierData"%>
<%@page import="java.util.List"%>
<%@ include file="/pages/content/common/AutocompleterCommon.jsp" %>
<%@ include file="/pages/content/common/BioSpecimenCommonCode.jsp" %>

<script src="jss/script.js" type="text/javascript"></script>
<script src="jss/calendarComponent.js"></script>

<SCRIPT>var imgsrc="images/";</SCRIPT>
<LINK href="css/calanderComponent.css" type=text/css rel=stylesheet>

<%	
			String pageOf=(String)request.getAttribute(Constants.PAGEOF);
			Object obj = request.getAttribute("clinicalStudyRegistrationForm");
			ClinicalStudyRegistrationForm form =null;
			String currentRegistrationDate = "";
			String signedConsentDate = "";

			if(obj != null && obj instanceof ClinicalStudyRegistrationForm)
			{
				form = (ClinicalStudyRegistrationForm)obj;
				currentRegistrationDate = form.getRegistrationDate();  
				
				if(currentRegistrationDate == null)
				{
					currentRegistrationDate = "";
				}
				if(signedConsentDate == null)
				{
					signedConsentDate = "";
				}
			}
		
				String submittedFor=(String)request.getAttribute(Constants.SUBMITTED_FOR);
				boolean isAddNew = false;

			    String operation = (String) request.getAttribute(Constants.OPERATION);

		        String reqPath = (String)request.getAttribute(Constants.REQ_PATH);
		        
		        String formName=null;

                boolean readOnlyValue;
		        if (operation.equals(Constants.EDIT))
		        {						
		            formName = Constants.CLINICAL_STUDY_REGISTRATION_EDIT_ACTION;
					if(pageOf.equals(Constants.PAGE_OF_CLINICAL_STUDY_REGISTRATION_CP_QUERY))
					{
						formName = Constants.CP_QUERY_CLINICAL_STUDY_REGISTRATION_EDIT_ACTION + "?pageOf="+pageOf;
					}
		            readOnlyValue = false;
		        }
		        else
		        {
		            formName = Constants.CLINICAL_STUDY_REGISTRATION_ADD_ACTION;
					if(pageOf.equals(Constants.PAGE_OF_CLINICAL_STUDY_REGISTRATION_CP_QUERY))
					{
						formName = Constants.CP_QUERY_CLINICAL_STUDY_REGISTRATION_ADD_ACTION + "?pageOf="+pageOf;
					}
		            readOnlyValue = false;
		        }

%>
<head>
<script language="JavaScript" type="text/javascript" src="jss/javaScript.js"></script>

<%if(pageOf.equals(Constants.PAGE_OF_CLINICAL_STUDY_REGISTRATION_CP_QUERY))
	{
	String participantId = (String)request.getAttribute(Constants.CP_SEARCH_PARTICIPANT_ID);
	%>
		<script language="javascript">
			var csId = window.parent.frames['<%=Constants.CP_AND_PARTICIPANT_VIEW%>'].document.getElementById("csId").value;
			<%if(participantId != null){%>
			window.parent.frames['<%=Constants.CP_AND_PARTICIPANT_VIEW%>'].location="showCsAndParticipants.do?csId="+csId+"&participantId=<%=participantId%>";
			//window.parent.frames['<%=Constants.CP_TREE_VIEW%>'].location="showEventTree.do?<%=Constants.CP_SEARCH_CP_ID%>="+csId+"&<%=Constants.CP_SEARCH_PARTICIPANT_ID%>=<%=participantId%>";
			<%} else{%>
			window.parent.frames['<%=Constants.CP_AND_PARTICIPANT_VIEW%>'].location="showCsAndParticipants.do?csId="+csId;
			<%}%>
		</script>
	<%}%>

<script language="JavaScript">

		function submitform()
		{
		  var action ="ClinicalStudyRegistration.do?pageOf=pageOfClinicalStudyRegistration&operation=<%=operation%>";
		  document.forms[0].action = action;
		  document.forms[0].submit();
		}

</script>		
</head>
<%
%>

<html:errors />
<html:messages id="messageKey" message="true" header="messages.header" footer="messages.footer">
	<%=messageKey%>
</html:messages>

<html:form action="<%=formName%>">
	
	<%
			String normalSubmitFunctionName = "setSubmittedFor('" + submittedFor+ "','" + Constants.PROTOCOL_REGISTRATION_FORWARD_TO_LIST[0][1]+"')";
			String confirmDisableFuncName = "confirmDisable('" + formName +"',document.forms[0].activityStatus)";
			String normalSubmit = normalSubmitFunctionName + ","+confirmDisableFuncName;
	%>	

	
	<table summary="" cellpadding="0" cellspacing="0" border="0" class="contentPage" width="600">
	

		<tr>
		<td>
			<table summary="" cellpadding="3" cellspacing="0" border="0">
				<tr>
					<td>
						<html:hidden property="operation" value="<%=operation%>" />
						<html:hidden property="submittedFor" value="<%=submittedFor%>"/>
						<html:hidden property="forwardTo" value=""/>
						<html:hidden property="participantID" />

					</td>
					<td><html:hidden property="id"/>
					<td><html:hidden property="onSubmit"/></td>
					<html:hidden property="redirectTo" value="<%=reqPath%>"/>
					
				</tr>

				<tr>
					<td class="formMessage" colspan="3">* indicates a required field</td>
				</tr>
					
				<tr>
					<td class="formTitle" height="20" colspan="3">
						<logic:equal name="operation" value="<%=Constants.ADD%>">
							<bean:message key="clinicalStudyReg.add.title"/>
						</logic:equal>
						<logic:equal name="operation" value="<%=Constants.EDIT%>">
							<bean:message key="clinicalStudyReg.edit.title"/>
						</logic:equal>

					</td>
				</tr>
					
				<tr>
  			    	<td class="formRequiredNotice" width="5">*</td>					
				   	<td class="formRequiredLabel">
						<label for="name">
							<bean:message key="clinicalStudyReg.protocolTitle" />
						</label>
				   	</td>
					<td class="formField">

						<html:select property="clinicalStudyId" styleClass="formFieldSized" styleId="clinicalStudyId" size="1"
						 onmouseover="showTip(this.id)" onmouseout="hideTip(this.id)" >
						    <html:options collection="<%=Constants.CLINICAL_STUDY_LIST%>" labelProperty="name" property="value"/>															
					    </html:select>
						&nbsp;	 
						<logic:notEqual name="<%=Constants.PAGEOF%>" value="<%=Constants.PAGE_OF_CLINICAL_STUDY_REGISTRATION_CP_QUERY%>">
							<html:link href="#" styleId="newClinicalStudy" onclick="addNewAction('ParticipantRegistrationAddNewForCS.do?addNewForwardTo=clinicalStudy&forwardTo=participantRegistrationForCS&addNewFor=clinicalStudyId')">
								<bean:message key="buttons.addNew" />
							</html:link>					   
						</logic:notEqual>
					</td>
				</tr>
					
				<tr id="row0">				
			    	<td class="formRequiredNotice" width="5">&nbsp;</td>					
      	 	       	<td class="formLabel" nowrap>
                   		
							<label for="participantID">
								<bean:message key="clinicalStudyReg.participantName" />
							</label>
						
					</td>
					<td class="formField">
						<html:text styleClass="formFieldSized" maxlength="10"  size="30" styleId="participantName" 
					     		property="participantName" disabled="true"/>	
						&nbsp;
						<logic:notEqual name="<%=Constants.PAGEOF%>" value="<%=Constants.PAGE_OF_CLINICAL_STUDY_REGISTRATION_CP_QUERY%>">
						<html:link href="#" styleId="newParticipant" onclick="addNewAction('ParticipantRegistrationAddNewForCS.do?addNewForwardTo=participant&forwardTo=participantRegistrationForCS&addNewFor=participantId')">
							<bean:message key="buttons.addNew" />
						</html:link>				   
						</logic:notEqual>
						<logic:equal name="<%=Constants.PAGEOF%>" value="<%=Constants.PAGE_OF_CLINICAL_STUDY_REGISTRATION_CP_QUERY%>">
						<html:link href="#" styleId="newParticipant" onclick="addNewAction('CPQueryParticipantRegistrationAddNewForCS.do?addNewForwardTo=participant&forwardTo=participantRegistrationForCS&addNewFor=participantId')">
							<bean:message key="buttons.addNew" />
						</html:link>			
						</logic:equal>
						
							   
					</td>
				</tr>

				<tr id="row1">					
						
						<td class="formRequiredNotice" width="5">&nbsp;</td>					
						<td class="formLabel">
						
						<label for="name">
							<bean:message key="clinicalStudyReg.participantProtocolID" />
						</label>
					</td>
					<td class="formField">
						<html:text styleClass="formFieldSized" maxlength="255"  size="30" styleId="participantClinicalStudyID" property="participantClinicalStudyID" readonly="<%=readOnlyValue%>" />
					</td>
				</tr>
	
 				<tr>
            		<td class="formRequiredNotice" width="5">*</td>					
					<td class="formRequiredLabel">
					    
						<label for="name">
							<bean:message key="clinicalStudyReg.participantRegistrationDate" />
						</label>
					</td>
					<td class="formField">

<%
	 if(currentRegistrationDate.trim().length() > 0)
	{
			Integer registrationYear = new Integer(Utility.getYear(currentRegistrationDate ));
			Integer registrationMonth = new Integer(Utility.getMonth(currentRegistrationDate ));
			Integer registrationDay = new Integer(Utility.getDay(currentRegistrationDate ));
%>
			<ncombo:DateTimeComponent name="registrationDate"
									  id="registrationDate"
 									  formName="clinicalStudyRegistrationForm"	
									  month= "<%=registrationMonth %>"
									  year= "<%=registrationYear %>"
									  day= "<%= registrationDay %>" 
									  value="<%=currentRegistrationDate %>"
									  styleClass="formDateSized10"
											 />		
<% 
	}
	else
	{  
 %>
			<ncombo:DateTimeComponent name="registrationDate"
									  id="registrationDate"
 									  formName="clinicalStudyRegistrationForm"	
									  styleClass="formDateSized10" 
											 />		
<%
	}
%>
	<bean:message key="page.dateFormat" />&nbsp;

				 	</td>
				</tr>
	
				<!-- activitystatus -->	
				<logic:equal name="<%=Constants.OPERATION%>" value="<%=Constants.EDIT%>">
				<tr>
					<td class="formRequiredNotice" width="5">*</td>
					<td class="formRequiredLabel" >
						<label for="activityStatus">
							<bean:message key="clinicalStudyReg.activityStatus" />
						</label>
					</td>
					<td class="formField">
						<html:select property="activityStatus" styleClass="formFieldSized10" styleId="activityStatus" size="1" 
						 onmouseover="showTip(this.id)" onmouseout="hideTip(this.id)" onchange="<%=strCheckStatus%>">
							<html:options name="<%=Constants.ACTIVITYSTATUSLIST%>" labelName="<%=Constants.ACTIVITYSTATUSLIST%>" />
						</html:select>
					</td>
				</tr>
				</logic:equal>
				
				<tr>
					<td align="right" colspan="3">
					<%
    					String changeAction = "setFormAction('" + formName + "');";
			        %> 
				</tr>
			</table>
	
	<table>
		<tr><td></td></tr><tr><td></td></tr>
	</table>
	<div id="container">
	</div>
	</td>
</tr>

</table>
				
	<%@ include file="CollectionProtocolRegistrationPageButtons.jsp" %>

</html:form>