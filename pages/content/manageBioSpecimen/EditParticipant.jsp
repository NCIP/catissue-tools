
<link rel="stylesheet" type="text/css" href="css/catissue_suite.css" />

<script language="JavaScript" type="text/javascript"
	src="jss/javaScript.js"></script>
<script language="JavaScript" src="jss/script.js" type="text/javascript"></script>
<%
	String[] activityStatusList = (String[])request.getAttribute(Constants.ACTIVITYSTATUSLIST);
	boolean activityStatusPrivilege =((Boolean)request.getAttribute(Constants.PARTICIPANT_ACTIVITY_PRIVILEGE)).booleanValue();
%>

<%if(pageOf.equals(Constants.PAGE_OF_PARTICIPANT_CP_QUERY))
	{
	strCheckStatus= "checkActivityStatus(this,'" + Constants.CP_QUERY_BIO_SPECIMEN + "')";
}%>


<script language="JavaScript">
function participantRegRow(subdivtag)
		{
			var collectionProtocolRegistrationVal = parseInt(document.forms[0].collectionProtocolRegistrationValueCounter.value);
			collectionProtocolRegistrationVal = collectionProtocolRegistrationVal + 1;
			document.forms[0].collectionProtocolRegistrationValueCounter.value = collectionProtocolRegistrationVal;
			
			var rows = new Array(); 
			rows = document.getElementById(subdivtag).rows;
			var cprSize = rows.length;
			var row = document.getElementById(subdivtag).insertRow(cprSize);
			
			// First Cell
			var cprTitle=row.insertCell(0);
			cprTitle.className="black_new";
			sname="";
			var name = "collectionProtocolRegistrationValue(ClinicalStudyRegistration:" + (cprSize+1) + "_ClinicalStudy_id)";
			var keyValue = name;
			sname = sname +"<select name='" + name + "' size='1' class='black_new' id='" + name + "'>";
			<%
				if(collectionProtocolList!=null)
				{
					Iterator iterator = collectionProtocolList.iterator();
					while(iterator.hasNext())
					{
						NameValueBean bean = (NameValueBean)iterator.next();
			%>
						sname = sname + "<option value=\"<%=bean.getValue()%>\"><%=bean.getName()%></option>";
			<%		}
				}
			%>
			sname = sname + "</select>";
			var collectionProtocolTitleValue = "collectionProtocolRegistrationValue(ClinicalStudyRegistration:" + (cprSize+1) + "_ClinicalStudy_shortTitle)";
			sname = sname + "<input type='hidden' name='" + collectionProtocolTitleValue + "' value='' id='" + collectionProtocolTitleValue + "'>";
			cprTitle.innerHTML="" + sname;
			
			//Second Cell
			var cprParticipantId=row.insertCell(1);
			cprParticipantId.className="black_new";
			sname="";
			name = "collectionProtocolRegistrationValue(ClinicalStudyRegistration:" + (cprSize+1) + "_clinicalStudyParticipantIdentifier)";
			sname="<input type='text' name='" + name + "' maxlength='30'  class='black_new' id='" + name + "'>";
			cprParticipantId.innerHTML="" + sname;
			
			<%
				String registrationDate = edu.wustl.common.util.Utility.parseDateToString(Calendar.getInstance().getTime(), Constants.DATE_PATTERN_MM_DD_YYYY);
    		%>
    		
			//Third Cell
			var cprRegistrationDate=row.insertCell(2);
			cprRegistrationDate.className="black_new";
			cprRegistrationDate.colSpan=2;
			sname="";
			var name = "collectionProtocolRegistrationValue(ClinicalStudyRegistration:" + (cprSize+1) + "_registrationDate)";
			//sname = "<input type='text' name='" + name + "' class='formFieldSized15' id='" + name + "' value = 'MM-DD-YYYY or MM/DD/YYYY' onclick = \"this.value = ''\" onblur = \"if(this.value=='') {this.value = 'MM-DD-YYYY or MM/DD/YYYY';}\" onkeypress=\"return titliOnEnter(event, this, document.getElementById('" + name + "'))\">";
			sname = "<input type='text' name='" + name + "' size="+ 10 +" class='black_new' id='" + name + "' value = '<%=registrationDate%>'>";
			cprRegistrationDate.innerHTML=sname;
			
			//Fourth Cell
			var cprActivityStatus=row.insertCell(3);
			cprActivityStatus.className="black_new";
			sname="";
			var name = "collectionProtocolRegistrationValue(ClinicalStudyRegistration:" + (cprSize+1) +"_activityStatus)";
			sname = sname +"<select name='" + name + "' size='1' class='black_new' id='" + name + "' disabled='disabled' onmouseover=showTip(this.id) onmouseout=hideTip(this.id) >";
			<%
				for(int i=0 ; i<activityStatusList.length; i++)
				{
					String selected= "";
					if(i==1)
					{
						selected="selected='selected'";
					}
			%>
					sname = sname + "<option value='<%=activityStatusList[i]%>' <%=selected%> ><%=activityStatusList[i]%></option>";
			<%	
				}
			%>
			sname = sname + "</select>";
			cprActivityStatus.innerHTML=sname;
											
			//Fifth Cell
			var consent=row.insertCell(4);
			consent.className="black_new";
			sname="";
			
			var spanTag=document.createElement("span");
			var consentCheckStatus="consentCheckStatus_"+(cprSize+1);
			spanTag.setAttribute("id",consentCheckStatus);
				
			var name = "CollectionProtocolConsentChk_"+ (cprSize+1);
			var anchorTagKey = "ConsentCheck_"+ (cprSize+1);
			var collectionProtocolValue = "collectionProtocolRegistrationValue(ClinicalStudyRegistration:" + (cprSize+1) + "_ClinicalStudy_id)";
			var collectionProtocolTitleValue = "collectionProtocolRegistrationValue(ClinicalStudyRegistration:" + (cprSize+1) + "_ClinicalStudy_shortTitle)";
			var anchorTag = document.createElement("a");
			anchorTag.setAttribute("id",anchorTagKey);
			spanTag.innerHTML="<%=Constants.NO_CONSENTS_DEFINED%>"+"<input type='hidden' name='" + name + "' value='Consent' id='" + name + "'>";
			spanTag.appendChild(anchorTag);
			consent.appendChild(spanTag);
			document.getElementById(keyValue).onchange=function(){getConsent(name,collectionProtocolValue,collectionProtocolTitleValue,(cprSize+1),anchorTagKey,consentCheckStatus)};
					
			
			//sixth Cell
			var cprCheckb=row.insertCell(5);
			cprCheckb.className="black_new";
			sname="";
			
			var identifier = "collectionProtocolRegistrationValue(ClinicalStudyRegistration:" + (cprSize+1) +"_id)";
			sname = sname + "<input type='hidden' name='" + identifier + "' value='' id='" + identifier + "'>";
			
			var name = "CollectionProtocolRegistrationChk_"+(cprSize+1);
			sname = sname +"<input type='checkbox' name='" + name +"' id='" + name +"' value='C' onClick=\"enableButton(document.forms[0].deleteParticipantRegistrationValue,document.forms[0].collectionProtocolRegistrationValueCounter,'CollectionProtocolRegistrationChk_')\">";
			cprCheckb.innerHTML=""+sname;
		}


		function setSubmittedForParticipant(submittedFor,forwardTo)
		{
			document.forms[0].submittedFor.value = submittedFor;
			document.forms[0].forwardTo.value    = forwardTo;
			
			<%if(request.getAttribute(Constants.SUBMITTED_FOR)!=null && request.getAttribute(Constants.SUBMITTED_FOR).equals("AddNew")){%>
				document.forms[0].submittedFor.value = "AddNew";
			<%}%>			
			<%if(request.getAttribute(Constants.SPREADSHEET_DATA_LIST)!=null && dataList.size()>0){%>	

				if(document.forms[0].radioValue.value=="Add")
				{
					document.forms[0].action="<%=Constants.PARTICIPANT_ADD_ACTION%>";
					<%
					if(pageOf.equals(Constants.PAGE_OF_PARTICIPANT_CP_QUERY))
					{
							if(operation.equals(Constants.ADD))
							{
						%>
							document.forms[0].action="<%=Constants.CP_QUERY_PARTICIPANT_ADD_ACTION%>";
						<%
							}
						else
							{ 
						%>
							document.forms[0].action="<%=Constants.CP_QUERY_PARTICIPANT_EDIT_ACTION%>";
						<%
							}
					}
					%>
				}
				else
				{
					if(document.forms[0].radioValue.value=="Lookup")
					{
				
						document.forms[0].action="<%=Constants.PARTICIPANT_LOOKUP_ACTION%>"+"?continueLookup=yes";
						<%if(pageOf.equals(Constants.PAGE_OF_PARTICIPANT_CP_QUERY))
						{ %>
							document.forms[0].action="<%=Constants.CP_QUERY_PARTICIPANT_LOOKUP_ACTION%>"+"?continueLookup=yes";
						<%}%>												
						document.forms[0].submit();
					}
				}		
			<%}%>	
	
	if((document.forms[0].activityStatus != undefined) && (document.forms[0].activityStatus.value == "Disabled"))
   	{
	    var go = confirm("Disabling any data will disable ALL its associated data also. Once disabled you will not be able to recover any of the data back from the system. Please refer to the user manual for more details. \n Do you really want to disable?");
		if (go==true)
		{
			document.forms[0].submit();
		}
	} 
	else
	{
			checkActivityStatusForCPR();	
	}
}

	

	function checkActivityStatusForCPR()
		{
			var collectionProtocolRegistrationVal = parseInt(document.forms[0].collectionProtocolRegistrationValueCounter.value);
			var isAllActive = true;
			for(i = 1 ; i <= collectionProtocolRegistrationVal ; i++)
			{
				var name = "collectionProtocolRegistrationValue(ClinicalStudyRegistration:" + i +"_activityStatus)";
				if((document.getElementById(name) != undefined) && document.getElementById(name).value=="Disabled")
				{
					isAllActive = false;
					var go = confirm("Disabling any data will disable ALL its associated data also. Once disabled you will not be able to recover any of the data back from the system. Please refer to the user manual for more details. \n Do you really want to disable?");
					if (go==true)
					{
						document.forms[0].submit();
					}
					else
					{
						break;
					}
				}
			}
			
			if (isAllActive==true)
			{
				document.forms[0].submit();
			}
		}


</script>
<!-- sneha: deleted html:errors and message tags completely,included actionerrors.jsp in later part -->

<table width="100%" border="0" cellpadding="0" cellspacing="0"
	class="maintable" height="100%"><!-- sneha:-->
<logic:notEqual name="<%=Constants.PAGEOF%>"
	value="<%=Constants.PAGE_OF_PARTICIPANT_CP_QUERY%>">
<tr>
	<td>
		<table border="0" cellpadding="0" cellspacing="0">
			 <tr>
				<td class="td_table_head"><span class="wh_ar_b"><bean:message
						key="app.participant" /></span></td><!--sneha: td and span tag as per catissue-->
				<td><img
						src="images/uIEnhancementImages/table_title_corner2.gif"
						alt="Page Title - Participant" width="31" height="24" /></td><!-- sneha: complete <td> as per catissue -->
			 </tr>
		</table>
	</td>
 </tr>
</logic:notEqual>

<tr height="98%">
		<td class="tablepadding">
		<table width="100%" border="0" cellpadding="0" cellspacing="0">
		<tr>
		<!-- sneha: complete <td> as per catissue -->
		 <td width="4%" class="td_tab_bg" ><img src="images/spacer.gif" alt="spacer" width="50" height="1"></td>
        <logic:equal name="operation" value="add">
			<logic:notEqual name="<%=Constants.PAGE_OF%>" value="<%=Constants.PAGE_OF_PARTICIPANT_CP_QUERY%>">
                      <td  valign="bottom" ><img src="images/uIEnhancementImages/tab_add_selected.jpg" alt="Add" width="57" height="22" /></td>
                      <td  valign="bottom" ><html:link page="/SimpleQueryInterface.do?pageOf=pageOfParticipant&aliasName=Participant&menuSelected=5"><img src="images/uIEnhancementImages/tab_edit_notSelected.jpg" alt="Edit" width="57" height="22" border="0" /></html:link></td>
 			</logic:notEqual>
          </logic:equal>
		  <logic:equal name="operation" value="edit">
					<logic:notEqual name="<%=Constants.PAGE_OF%>" value="<%=Constants.PAGE_OF_PARTICIPANT_CP_QUERY%>">	
						<td  valign="bottom"  ><html:link page="/Participant.do?operation=add&pageOf=pageOfParticipant"><img src="images/uIEnhancementImages/tab_add_notSelected.jpg" alt="Add" width="57" height="22" border ="0" /></html:link></td>
                      <td  valign="bottom"><img src="images/uIEnhancementImages/tab_edit_selected.jpg" alt="Edit" width="59" height="22"/></td>
					</logic:notEqual>
           </logic:equal>
        <td width="90%" valign="bottom" class="td_tab_bg">&nbsp;</td>   		
		<!-- If operation is equal to edit or search but,the page is for query the identifier field is not shown -->
		<%--logic:notEqual name="<%=Constants.OPERATION%>" value="<%=Constants.ADD%>">
			<logic:notEqual name="<%=Constants.PAGEOF%>" value="<%=Constants.QUERY%>">
			<!-- ENTER IDENTIFIER BEGINS-->	
			  <br/>	
  	    	  <tr>
    		   <td class="td_color_bfdcf3">
			 	 <table border="0" cellpadding="0" cellspacing="0">
				  <tr>
				     <td class="formTitle" height="20" colspan="3">
				     	<bean:message key="user.searchTitle"/>
				     </td><!-- sneha: complete <tr> as per catissue except for "key" -->
				  </tr>
				  
				  <tr>
						<td class="formRequiredNotice" width="5">*</td>
						<td class="formRequiredLabel">
							<label for="identifier">
								<bean:message key="user.identifier"/>
							</label>
						</td>
					    <td class="formField">
					    	<html:text styleClass="formFieldSized" size="30" styleId="identifier" property="identifier" readonly="<%=readOnlyForAll%>"/>
					    </td>
				  </tr>	
							
				 <%
					String changeAction = "setFormAction('"+Constants.PARTICIPANT_SEARCH_ACTION+"');setOperation('"+edu.wustl.simplequery.global.Constants.SEARCH+"');";
				 %>
 
				  <tr>
				   <td align="right" colspan="3">
					 <table cellpadding="4" cellspacing="0" border="0">
						 <tr>
						    <td>
						    	<html:submit styleClass="actionButton" value="Search" onclick="<%=changeAction%>"/></td>
						 </tr>
					 </table>
				   </td>
				  </tr>

				 </table>
			    </td>
			  </tr>
			  <!-- ENTER IDENTIFIER ENDS-->
			  	</logic:notEqual>
			  </logic:notEqual--%>

			  	<table width="985" border="0" cellpadding="3" cellspacing="0"
				class="whitetable_bg" height="95%"><!-- sneha: complete <table> tag not the contents as per catissue -->
				<tr>
				<td colspan="2" align="left" class="bottomtd"><%@ include file="/pages/content/common/ActionErrors.jsp" %></td>
			</tr><!-- sneha: complete <tr> as per catissue -->


	  <!-- NEW PARTICIPANT REGISTRATION BEGINS-->
		<tr>
			<td>
			 <table summary="" cellpadding="3" cellspacing="0" border="0" width="100%"><!-- sneha: complete <table> tag as per catissue contents unchaged -->
				 <tr>
					<td>
						<input type="hidden" name="participantId" value="<%=participantId%>"/>
						<input type="hidden" name="cpId" id="cpId"/>
						<input type="hidden" name="radioValue"/>
						<html:hidden property="<%=Constants.OPERATION%>" value="<%=operation%>"/>
						<html:hidden property="submittedFor" value="<%=submittedFor%>"/>
						<html:hidden property="forwardTo" value="<%=forwardTo%>"/>
					</td>
					<td><html:hidden property="valueCounter"/></td>
					<td><html:hidden property="collectionProtocolRegistrationValueCounter"/></td>
					<td class="blue_ar_b"><html:hidden property="onSubmit" /></td>
					<td><html:hidden property="id" /><html:hidden property="redirectTo"/></td>
					<td><html:hidden property="pageOf" value="<%=pageOf%>"/></td>
				 </tr>
				<logic:notEqual name="<%=Constants.OPERATION%>" value="<%=edu.wustl.simplequery.global.Constants.SEARCH%>">
				 		
				 <%--<tr>
				     <td class="formTitle" height="20" colspan="6">
				     <%title = "participant."+pageView+".title";%>
				     <bean:message key="participant.add.title"/>
					<%
						if(pageView.equals("edit"))
						{
					%>
				     &nbsp;<bean:message key="for.identifier"/>&nbsp;<bean:write name="participantForm" property="id" />
					<%
						}
					%>
				     </td>
				 </tr>--%>
				 
				 <tr>
				 	<% 
				 		if (pageView.equals("add"))
				 		{
				 	%>
				 			<td colspan="10" align="left" class="tr_bg_blue1"><span
							class="blue_ar_b">&nbsp
						     <%title = "participant."+pageView+".title";%>
						     <bean:message key="participant.add.title"/>
						     </td>
				 	<%	}
				 		else
				 		{
				 	%>
				 		<td colspan="10" align="left" class="tr_bg_blue1"><span
							class="blue_ar_b">&nbsp						     
						     <bean:message key="participant.details"/>
						     </td>
				 	<%
				 		}
				 	%>
				</tr>
				 
				 <%-- <tr>
				 	<td class="formTitle" height="20" colspan="7">
					 <logic:equal name="operation" value="<%=Constants.ADD%>">
						<bean:message key="participant.add.title"/>
					</logic:equal>
					<logic:equal name="operation" value="<%=Constants.EDIT%>">
						<bean:message key="participant.edit.title"/>
					</logic:equal>
					</td>
				</tr>--%>
				

				<!-- Social Security Number
				 <tr>
					 <td class="black_ar_new" colspan="10">
				     	<label for="socialSecurityNumber">
				     		<bean:message key="participant.socialSecurityNumber"/>
				     	</label>
						&nbsp;&nbsp;&nbsp;&nbsp;
				     	<html:text styleClass="black_new" size="3" maxlength="3" styleId="socialSecurityNumberPartA" property="socialSecurityNumberPartA" readonly="<%=readOnlyForAll%>" onkeypress="intOnly(this);" onchange="intOnly(this);" onkeyup="intOnly(this);moveToNext(this,this.value,'socialSecurityNumberPartB');"/>
				     	-
				     	<html:text styleClass="black_new" size="2" maxlength="2" styleId="socialSecurityNumberPartB" property="socialSecurityNumberPartB" readonly="<%=readOnlyForAll%>" onkeypress="intOnly(this);" onchange="intOnly(this);" onkeyup="intOnly(this);moveToNext(this,this.value,'socialSecurityNumberPartC');"/>
				     	-
				     	<html:text styleClass="black_new" size="4" maxlength="4" styleId="socialSecurityNumberPartC" property="socialSecurityNumberPartC" readonly="<%=readOnlyForAll%>" onkeypress="intOnly(this);" onchange="intOnly(this);" onkeyup="intOnly(this);"/>
				     </td>
				 </tr>
				 	-->
					<tr>
						<td>&nbsp;</td>
					</tr>
					<tr>
						 <td align="left" class="black_ar_new"><label
								for="clinicalStudyId"> <bean:message
																	key="clinicalStudyReg.participantProtocolID" /> </label>
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									  <html:text styleClass="black_ar_new"
																	maxlength="50" size="30" styleId="clinicalStudyId"
																	property="clinicalStudyId" style="text-align:right"/>
								&nbsp;&nbsp;&nbsp;&nbsp;
								<label for="registrationDate">
											<bean:message key="participant.cpr.msg" /> </label>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                  <html:text styleClass="black_ar_new"
												maxlength="50" size="30" styleId="registrationDate" property="registrationDate" /></td>
                  </tr>
					

					 <tr>
					<td class="black_new" >
						<table summary="" cellpadding="3" cellspacing="0" border="0">
							<tr>
							<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							</td>
					     	
							 <td class="black_new">
								<label for="firstName">
									<bean:message key="participant.firstName"/>
								</label>
							  </td>
							<td class="black_new">
								<label for="lastName">
									<bean:message key="participant.lastName"/>
								</label>
							 </td>
							<td class="black_new">
								<label for="middleName">
									<bean:message key="participant.middleName"/>
								</label>
							 </td>
							 <td class="black_new">
								<label for="familyName">
									<bean:message key="participant.familyName"/>
								</label>
							 </td>
						</tr>
					 	<tr>
							
							<td class="black_new">
								<span class="blue_ar_b"><img src="images/uIEnhancementImages/star.gif" alt="Mandatory" width="6" height="6" hspace="0" vspace="0" /></span>
									<label for="Name">
										<bean:message key="participant.name"/>
									</label>
							</td>
							<td class="black_new">
					     		<html:text styleClass="black_new" maxlength="255" size="15" styleId="firstName" property="firstName" readonly="<%=readOnlyForAll%>" onkeyup="moveToNext(this,this.value,'lastName')"/>
							</td>
				
							 <td class="black_new">
								<html:text styleClass="black_new" maxlength="255" size="15" styleId="lastName" name="participantForm" property="lastName" readonly="<%=readOnlyForAll%>" onkeyup="moveToNext(this,this.value,'middleName')"/>
							 </td>
					     	 <td class="black_new">
					     		<html:text styleClass="black_new" maxlength="255" size="15" styleId="middleName" property="middleName" readonly="<%=readOnlyForAll%>"/>
							</td>
							<td class="black_new">
					     		<html:text styleClass="black_new" maxlength="255" size="15" styleId="familyName" property="familyName" readonly="<%=readOnlyForAll%>"/>
							</td>
						</tr>
					 </table>
				    </td>
				 </tr>
				 <tr>
		         	  <td align="left" class="black_new"><label 
		         	  for="businessField"><bean:message key="participant.businessField" /></label>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				  		<html:text styleClass="black_new" maxlength="255" size="30" styleId="businessField" property="businessField" readonly="<%=readOnlyForAll%>"/>
				  	</td>
				 </tr>
				 
				 <tr>
					<td class="black_new">
					<label for="gender"><bean:message key="participant.gender"/></label>
		         	  	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<!-- Mandar : 434 : for tooltip -->
						<logic:iterate id="nvb" name="<%=Constants.GENDER_LIST%>">
						<%	NameValueBean nameValueBean=(NameValueBean)nvb;%>
						<html:radio property="gender" value="<%=nameValueBean.getValue()%>"><%=nameValueBean.getName()%> </html:radio>
						</logic:iterate>				
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;						         	
						<label for="birthDate">
							<bean:message key="participant.birthDate"/>
						</label>
<%
	 if(currentBirthDate.trim().length() > 0)
	{
			Integer birthYear = new Integer(Utility.getYear(currentBirthDate ));
			Integer birthMonth = new Integer(Utility.getMonth(currentBirthDate ));
			Integer birthDay = new Integer(Utility.getDay(currentBirthDate ));
%>
			<ncombo:DateTimeComponent name="birthDate"
									  id="birthDate"
									  formName="participantForm"	
									  month= "<%=birthMonth %>"
									  year= "<%=birthYear %>"
									  day= "<%= birthDay %>" 
									  value="<%=currentBirthDate %>"
									  pattern="dd-MM-yyyy"
									  styleClass="formDateSized10"
									  />		
<% 
	}
	else
	{  
 %>
			<ncombo:DateTimeComponent name="birthDate"
									  id="birthDate"
 									  formName="participantForm"	
									  pattern="dd-MM-yyyy"
									  styleClass="formDateSized10" 
											 />		
<%
	}
%>
<bean:message key="page.dateFormat" />&nbsp;
					 </td>
				 </tr>			
				 <tr>
				
					<td class="black_new">
				     	<label for="vitalStatus">
				     		<bean:message key="participant.vitalStatus"/>
				     	</label>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<%--
				     	<html:select property="vitalStatus" styleClass="formFieldSized" styleId="vitalStatus" size="1" disabled="<%=readOnlyForAll%>"
						 onmouseover="showTip(this.id)" onmouseout="hideTip(this.id)">
							<html:options collection="<%=Constants.VITAL_STATUS_LIST%>" labelProperty="name" property="value"/>
						</html:select>--%>
						<logic:iterate id="nvb" name="<%=Constants.VITAL_STATUS_LIST%>">
						<%	NameValueBean nameValueBean=(NameValueBean)nvb;%>
						<html:radio property="vitalStatus" onclick="onVitalStatusRadioButtonClick(this)" value="<%=nameValueBean.getValue()%>"><%=nameValueBean.getName()%> </html:radio>
						</logic:iterate>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								<label for="deathDate">
							<bean:message key="participant.deathDate"/>
								</label>
						<%
						
							ParticipantForm form = (ParticipantForm) request.getAttribute("participantForm");
							Boolean deathDisable = new Boolean("false");
							if(form.getVitalStatus()!= null && !form.getVitalStatus().trim().equals("Dead"))
							{
								deathDisable = new Boolean("true");
							}
							 if(currentDeathDate.trim().length() > 0)
							{
									Integer deathYear = new Integer(Utility.getYear(currentDeathDate ));
									Integer deathMonth = new Integer(Utility.getMonth(currentDeathDate ));
									Integer deathDay = new Integer(Utility.getDay(currentDeathDate ));
						%>
									<ncombo:DateTimeComponent name="deathDate"
															  id="deathDate"
						 									  formName="participantForm"	
															  month= "<%=deathMonth %>"
															  year= "<%=deathYear %>"
															  day= "<%= deathDay %>" 
															  value="<%=currentDeathDate %>"
															  styleClass="formDateSized10"
															  disabled="<%=deathDisable%>"
															  pattern="dd-MM-yyyy"
																	 />		
						<% 
							}
							else
							{  
						 %>
									<ncombo:DateTimeComponent name="deathDate"
															  id="deathDate"
						 									  formName="participantForm"	
															  styleClass="formDateSized10" 
															  disabled="<%=deathDisable%>"
															  pattern="dd-MM-yyyy"
																	 />		
						<%
							}
						%>
						<bean:message key="page.dateFormat" />&nbsp;
								
		        	  </td>
				 </tr>
				 
				
				<%-- added by chetan for death date --%>
				 
				 
				<!-- Sex Genotype, Race and Ethnicity
				 <tr>
					<td class="black_new">
						<label for="genotype"><bean:message key="participant.genotype"/></label>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					  <autocomplete:AutoCompleteTag property="genotype"
										  optionsList = "<%=request.getAttribute(Constants.GENOTYPE_LIST)%>"
										  initialValue="<%=form.getGenotype()%>"
										  styleClass="black_new" size="27"
									    />

		        	  </td>
				 </tr>

				 

				 <tr>
					<td class="formFieldNoBordersSimple">
					     <label for="race"><bean:message key="participant.race"/></label>
				     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<!-- Mandar : 434 : for tooltip 
				     	<html:select property="raceTypes" styleClass="formFieldSized" styleId="race" size="4" multiple="true" disabled="<%=readOnlyForAll%>"
						 onmouseover="showTip(this.id)" onmouseout="hideTip(this.id)">
							<html:options collection="<%=Constants.RACELIST%>" labelProperty="name" property="value"/>
						</html:select>
		        	  </td>
				 </tr>
				 <tr>
					<td class="black_new">
				     	<label for="ethnicity">
				     		<bean:message key="participant.ethnicity"/>
				     	</label>
				    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					   <autocomplete:AutoCompleteTag property="ethnicity"
										  optionsList = "<%=request.getAttribute(Constants.ETHNICITY_LIST)%>"
										  initialValue="<%=form.getEthnicity()%>"
										  styleClass="black_new" size="27"
									    />

		        	  </td>
				 </tr>

				 -->

				 <!-------------- New Fields -->

		<!-- Street and City --> 
				<tr>
				<td colspan="10" align="left" class="tr_bg_blue1"><span
							class="blue_ar_b">&nbsp						     
						     <bean:message key="participant.address"/>
				</td>
				 </tr>
				 <tr>
		             <td align="left" class="black_ar_new">
                   		<label for="street"><bean:message key="site.street" /> </label>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				  <html:textarea property="street" rows="2" styleClass="black_new"  styleId="street" cols="27" />
				  </td>
				 </tr>

				 <tr>
                  <td align="left" class="black_ar_new">
					<label for="city">
											<bean:message key="site.city" /> </label>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<html:text styleClass="black_ar_new"
												maxlength="50" size="30" styleId="city" property="city" />

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				 <label for="state"><bean:message key="site.state" /> </label>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                  <html:text styleClass="black_ar_new"	maxlength="50" size="30" styleId="state" property="state" />
					</td>
                 </tr>
				
                <!-- Country and Phone No -->
				<tr>
                  <td align="left" class="black_ar_new">
				  <label for="country">
											<bean:message key="site.country" /> </label>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                  <html:text styleClass="black_ar_new"
												maxlength="50" size="30" styleId="country" property="country" />
				&nbsp;&nbsp;&nbsp;&nbsp;
                  
				     <label for="zipCode">
											<bean:message key="site.zipCode" /> </label>
				&nbsp;&nbsp;
                  <html:text styleClass="black_ar_new"
												maxlength="30" size="30" styleId="zipCode"
												property="zipCode"  style="text-align:right"/>
                  </td>
                </tr>
				
				<tr>
                  <td align="left" class="black_ar_new"><label
												for="phoneNumber"> <bean:message
												key="site.phoneNumber" /> </label>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

                  <html:text styleClass="black_ar_new"
												maxlength="50" size="30" styleId="phoneNumber"
												property="phoneNumber" style="text-align:right"/>
                  </td>

                </tr>

				 

				 <!-------------- New Fields -->
				 
				 <!-- activitystatus -->	
							<% if(activityStatusPrivilege) {%>
				<logic:equal name="<%=Constants.OPERATION%>" value="<%=Constants.EDIT%>">
				<tr>
				<td class="black_new" >
					<!--<td  class="black_ar_new"> -->
					<span
								class="blue_ar_b"><img
								src="images/uIEnhancementImages/star.gif" alt="Mandatory"
								 height="6" hspace="0" vspace="0" /></span>
								 
					
						<label for="activityStatus">
							<bean:message key="participant.activityStatus" />
						</label>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

<!-- Mandar : 434 : for tooltip -->
						<html:select property="activityStatus" styleClass="formFieldSized10" styleId="activityStatus" size="1" onchange="<%=strCheckStatus%>">
							<html:options name="<%=Constants.ACTIVITYSTATUSLIST%>" labelName="<%=Constants.ACTIVITYSTATUSLIST%>" />
						</html:select>
					</td>
				</tr>
				</logic:equal>
				<%} %>
	
					</table>
				</div>
				</td>
			</tr>

	<tr>

	<%
				long csId = form.getCpId();
				System.out.println("CS ID:>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+ csId);
	%>
				<input type="hidden" name="cpId" id="cpId" value="<%=csId%>"/>
   </tr>
				
						</table>
				</div>
				</td>
			</tr>
			<tr>
				<td colspan="2" class="bottomtd"></td>
			</tr>
				  <!---Following is the code for Data Grid. Participant Lookup Data is displayed-->
				<%if(request.getAttribute(Constants.SPREADSHEET_DATA_LIST)!=null && dataList.size()>0){
					isRegisterButton=true;
					if(request.getAttribute(Constants.SUBMITTED_FOR)!=null && request.getAttribute(Constants.SUBMITTED_FOR).equals("AddNew")){
						isRegisterButton=false;
					}%>	
				
			
				<tr><td colspan="7"><table summary="" cellpadding="0" cellspacing="0" border="0">
				<tr>
				    <td colspan="10" align="left" class="tr_bg_blue1"><span
							class="blue_ar_b">&nbsp;
				     	<bean:message key="participant.lookup"/>
				     </td>
       		    </tr>				
	  			<tr height=110 valign=top>
					<td valign=top class="formFieldAllBorders">
<!--  **************  Code for New Grid  *********************** -->
			<script>
					function participant(id)
					{
						//do nothing
						//mandar for grid
						var cl = mygrid.cells(id,mygrid.getColumnCount()-1);
						var pid = cl.getValue();
						//alert(pid);
						/* 
							 Resolved bug# 4240
	                    	 Name: Virender Mehta
	                    	 Reviewer: Sachin Lale
	                    	 Description: removed URL On  onclick
	                     */
						 document.forms[0].submittedFor.value = "AddNew";
						 var pageOf = "<%=pageOf%>";
						if(pageOf == "<%=Constants.PAGE_OF_PARTICIPANT_CP_QUERY%>")
						{
							window.location.href = 'CPQueryParticipantSelect.do?submittedFor=AddNew&operation=add&participantId='+pid
						}
						else
						{
							window.location.href = 'ParticipantLookup.do?submittedFor=AddNew&operation=add&participantId='+pid
						}						
					} 				

					/* 
						to be used when you want to specify another javascript function for row selection.
						useDefaultRowClickHandler =1 | any value other than 1 indicates you want to use another row click handler.
						useFunction = "";  Function to be used. 	
					*/
					var useDefaultRowClickHandler =2;
					var useFunction = "participant";	
			</script>
			<%@ include file="/pages/content/search/AdvanceGrid.jsp" %>
<!--  **************  Code for New Grid  *********************** -->

					</td>
				  </tr>
				  <tr>
				 	<td align="center" colspan="7" class="formFieldWithNoTopBorder">
						<INPUT TYPE='RADIO' NAME='chkName' value="Add" onclick="CreateNewClick()"><font size="2">Ignore matches and create new participant </font></INPUT>&nbsp;&nbsp;
						<INPUT TYPE='RADIO' NAME='chkName' value="Lookup" onclick="LookupAgain()" checked=true><font size="2">Lookup again </font></INPUT>
					</td>
				</tr>		
				</table></td></tr>
				<%}%>
				<!--Participant Lookup end-->				
								 <!-----action buttons-->
				 <tr>
				 	<td align="left" colspan="3" valign="top">
						<%
							String changeAction = "setFormAction('"+formName+"')";
						%>
						<!-- action buttons begins -->

						<table cellpadding="4" cellspacing="0" border="0" width="100%">
							<logic:equal name="<%=Constants.SUBMITTED_FOR%>" value="AddNew">
							<% 
								isAddNew=true;
							%>
							</logic:equal>
							
							<tr>
								<%--
									String normalSubmitFunctionName = "setSubmittedForParticipant('" + submittedFor+ "','" + Constants.PARTICIPANT_FORWARD_TO_LIST[0][1]+"')";
									String forwardToSubmitFunctionName = "setSubmittedForParticipant('ForwardTo','" + Constants.PARTICIPANT_FORWARD_TO_LIST[1][1]+"')";									
									String confirmDisableFuncName = "confirmDisable('" + formName +"',document.forms[0].activityStatus)";
									String normalSubmit = normalSubmitFunctionName + ","+confirmDisableFuncName;
									String forwardToSubmit = forwardToSubmitFunctionName + ","+confirmDisableFuncName;
								--%>
								<%
								    
									String normalSubmitFunctionName = "setSubmittedForParticipant('" + submittedFor+ "','" + Constants.PARTICIPANT_FORWARD_TO_LIST[0][1]+"')";
									String forwardToSubmitFunctionName = "setSubmittedForParticipant('ForwardTo','" + Constants.PARTICIPANT_FORWARD_TO_LIST[3][1]+"')";
									String forwardToSCGFunctionName = "setSubmittedForParticipant('ForwardTo','" + Constants.PARTICIPANT_FORWARD_TO_LIST[2][1]+"')";
									String normalSubmit = normalSubmitFunctionName ;
									String forwardToSubmit = forwardToSubmitFunctionName ;
									String forwardToSCG = forwardToSCGFunctionName ;

								%>
																
								<!-- PUT YOUR COMMENT HERE -->

								
								<logic:equal name="<%=Constants.PAGEOF%>" value="<%=Constants.PAGE_OF_PARTICIPANT_CP_QUERY%>">
								<td nowrap class="buttonbg" align="left" colspan="10">									
									<html:button styleClass="blue_ar_b"  
											property="registratioPage" 
											title="Submit Only"
											value="<%=Constants.PARTICIPANT_FORWARD_TO_LIST[0][0]%>"
											onclick="<%=forwardToSubmit%>">
									</html:button>
								</td>
								</logic:equal>
								<logic:notEqual name="<%=Constants.PAGEOF%>" value="<%=Constants.PAGE_OF_PARTICIPANT_CP_QUERY%>">
								<td nowrap class="buttonbg" align="left">									
									<html:button styleClass="blue_ar_b"  
											property="registratioPage" 
											title="Submit Only"
											value="<%=Constants.PARTICIPANT_FORWARD_TO_LIST[0][0]%>"
											onclick="<%=normalSubmit%>">
									</html:button>
								</td>
								</logic:notEqual>						
								<%--<td>
									<html:submit styleClass="actionButton" disabled="true">
							   		<bean:message key="buttons.getClinicalData"/>
									</html:submit>
								</td>	--%>
							</tr>
						</table>
							<!-- action buttons end -->
			  		</td>
			 	 </tr>
								 
				<!--	extra </logic:notEqual>-->
				
				 <!-- end --> 
			</table>
		</td></tr>
	</table>			
