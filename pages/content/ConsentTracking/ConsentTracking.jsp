 <jsp:directive.page import="edu.wustl.clinportal.domain.ClinicalStudyConsentTier"/>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/nlevelcombo.tld" prefix="ncombo" %>
<%@ page import="edu.wustl.clinportal.util.global.Utility"%>
<%@ page import="edu.wustl.clinportal.actionForm.*"%>
<%@ page import="java.text.DateFormat"%>
<%@ page import="edu.wustl.clinportal.util.global.Constants"%>
<%@ page import="edu.wustl.common.beans.NameValueBean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<LINK href="css/catissue_suite.css" type=text/css rel=stylesheet>
<LINK href="css/calanderComponent.css" type=text/css rel=stylesheet>
<LINK href="css/styleSheet1.css" type=text/css rel=stylesheet>
<script language="JavaScript">

var consentIDArray=new Array(<%=form.getConsentTierCounter()%>);
var changeInStatus=false;

function changeInResponse(responseIdkey)
{
	var index=-1;
	var flag=false;
	for(i=0;i<consentIDArray.length;i++)
	{
		if(consentIDArray[i]==responseIdkey)
		{
			flag=true;
			break;
		}
		if(consentIDArray[i]!=null&&consentIDArray[i]!="")
		{
			index=i;
		}
	}
	if(flag==false)
	{
		consentIDArray[index+1]=responseIdkey;
		changeInStatus=true;
	}	
}

function submitString()
{
	var str="";
	str=consentIDArray[0];
	for(i=1;i<consentIDArray.length;i++)
	{
		if(consentIDArray[i]!=null)
		{
			str=str+","+consentIDArray[i];
		}
	}
	document.forms[0].stringOfResponseKeys.value=str;
	//document.forms[0].submit();
}


<%-- On calling this function all the response dropdown value set to "Withdraw" --%>
function withdrawAll(element)
{	
	var withdraw = "<%=form.getConsentTierMap()%>";
	for(var i=0;i<element;i++)
	{
		var withdrawId = withdraw.replace(/`/,i);
		var withdrawObject = document.getElementById(withdrawId);
		if(withdrawObject.options.length==1)
		{
			withdrawObject.selectedIndex=0; 
		}
		else
		{
			withdrawObject.selectedIndex=3; 
		}
	
	}
}

<%--Popup Window will open up on calling this function--%>	
function popupWindow(nofConsentTiers)
{
	var withdraw = "<%=form.getConsentTierMap()%>";
	var iCount=nofConsentTiers;
	for(var i=0;i<nofConsentTiers;i++)
	{
		var withdrawId = withdraw.replace(/`/,i);
		var withdrawObject = document.getElementById(withdrawId);
		if(withdrawObject!=null && withdrawObject.value=="Withdrawn")
		{
			iCount--;
		}
	}	
	<%--When Withdraw All button is clicked--%>	
	if(iCount==0)
	{
		var url="pages/content/ConsentTracking/consentDialog.jsp?withrawall=true&response=withdraw&pageOf="+"<%=pageOf%>";
		window.open(url,'WithdrawAll','height=200,width=550,scrollbars=1,resizable=1');
	}
	else if(iCount==nofConsentTiers)
	{	
		
		<%
			if(pageOf.equals("pageOfConsent"))
			{
		%>			
		if(document.getElementById("exitBtn")!=null && document.getElementById("exitBtn").value!='Close')
		{
			document.forms[0].submit();
		}
		self.close();		
		<%
			}
		%>
		
		if(changeInStatus==false)
		{
			return <%=normalSubmit%>;
		}
		else
		{
			submitString();
		}
		
	}	
	else
	{
		var url="pages/content/ConsentTracking/consentDialog.jsp?withrawall=false&response=withdraw&pageOf="+"<%=pageOf%>";
		window.open(url,'Withdraw','height=200,width=550,scrollbars=1,resizable=1');
	}
}	

</script>							

<%-- Set Variables according to the pages --%>
<%
	String collection="";
	String width="";
	String innerWidth="";
	boolean readOnlyReg=false;
	if(pageOf!=null)
	{
		if(pageOf.equals("pageOfClinicalStudyRegistration"))
		{
			width="81%";
			innerWidth="100%";
			collection="responseList";
		}
		if(pageOf.equals("pageOfConsent"))
		{
			width="100%";
			innerWidth="100%";
			collection="responseList";
		}
	}
	String showHideStatus=request.getParameter("showHideStatus");
	Map csActivityStatus = (HashMap)session.getAttribute(Constants.CS_ACTIVITY_STATUS);
	String status = (String)csActivityStatus.get(new Long(collectionProtocolID));
	if(status.equals(Constants.ACTIVITY_STATUS_CLOSED) ||
			request.getParameter("partActivityStatus").equals(Constants.ACTIVITY_STATUS_CLOSED))
	{
		readOnlyReg=true;
	}
	List<String> authorizedCS =(ArrayList)session.getAttribute("csForUser");
	String cpID = collectionProtocolID+"";
	if(!authorizedCS.contains(cpID))
	{
		readOnlyReg=true;
	}

	if(request.getParameter("csrActivityStatus")!=null && request.getParameter("csrActivityStatus").equals(Constants.ACTIVITY_STATUS_CLOSED))
	{
		readOnlyReg=true;
	}

%>
	<%-- Main table Start --%>
	<table summary="" cellpadding="0" cellspacing="0" border="0" width="<%=width%>" id="consentTable">
		<%--Title of the form i.e Consent Form --%>				
		<tr>
			<td align="left" class="tr_bg_blue1"><span class="blue_ar_b">
				<%
				ConsentTierData consentTierForm =(ConsentTierData)form;
				List consentTierList=(List)consentTierForm.getConsentTiers();
				boolean withdrawAllDisabled=false;
				if(consentTierList==null||consentTierList.isEmpty())
				{
					consentTierList =new ArrayList();
					withdrawAllDisabled=true;
				}
				if(operation.equals(Constants.EDIT))
				{
				String str = "withdrawAll('"+ consentTierList.size()+"')";
				%>
					<%-- <div style="float:right;">
						<html:button property="addButton" disabled="<%=withdrawAllDisabled%>" styleClass="actionButton" onclick="<%=str%>" value="Withdraw All"/>
					</div>	--%>	
				<%
				}
				%>
				<div style="margin-top:2px;">
					<bean:message key="collectionprotocolregistration.consentform"/>
				</div>
				</span>
			</td>
		</tr>
		<tr>
			<%-- If page of Clinical Study Registration --%>						
			<%
			if(pageOf.equals("pageOfClinicalStudyRegistration") || pageOf.equals("pageOfConsent"))
			{
			%>
			<tr>
				<td  align="left" class="showhide">
					<table summary="" cellpadding="3" cellspacing="0" border="0"  width="100%" id="consentAttributes">
						<%--Signed URL --%>				
						<tr>
							<td class="noneditable" width="39%">
								&nbsp;&nbsp;&nbsp;<bean:message key="collectionprotocolregistration.signedurlconsent"/>
							</td>
							<td class="noneditable">
								<html:text styleClass="formFieldSized" property="signedConsentUrl" readonly="<%=readOnlyReg%>"/>
							</td>
						</tr>
						<%--Witness Name --%>									
						<tr>
							<td class="noneditable">
								&nbsp;&nbsp;&nbsp;<bean:message key="collectionprotocolregistration.witnessname"/>
							</td>	
							<td class="noneditable">
							<% if(!readOnlyReg)
							   {	
				
							%>
								<html:select property="witnessId" styleClass="formFieldSized18" styleId="witnessId" size="1"
									onmouseover="showTip(this.id)" onmouseout="hideTip(this.id)" disabled="<%=readOnlyReg%>">
								<html:options collection="witnessList" labelProperty="name" property="value" />
								</html:select>
							<%
							   }
							 	else
								{
									//find witness name from witnessid from list											
									String witnessName = Constants.SELECT_OPTION;
									Iterator itr = witnessList.iterator();
						        	while(itr.hasNext())
        							{
						        		NameValueBean bean = (NameValueBean)itr.next();
										if(java.lang.Long.valueOf(bean.getValue()) == form.getWitnessId())
										{
											
											witnessName = bean.getName();

										}
									}
							%>
									<%=witnessName%>
							<%
								}
							%>
							</td>
						</tr>
						<%--Consent Date --%>									
						<tr>
							<td class="noneditable">
								&nbsp;&nbsp;&nbsp;<bean:message key="collectionprotocolregistration.consentdate"/>
							</td>	
							<td class="noneditable">
							<%
							if(pageOf.equals("pageOfConsent"))
							{
								if(signedConsentDate.trim().length() > 0 && !readOnlyReg)
								{
									Integer consentYear = new Integer(Utility.getYear(signedConsentDate ));
									Integer consentMonth = new Integer(Utility.getMonth(signedConsentDate ));
									Integer consentDay = new Integer(Utility.getDay(signedConsentDate ));
								%>
								<ncombo:DateTimeComponent name="consentDate"
									id="consentDate"
									formName="consentForm"	
									month= "<%=consentMonth %>"
									year= "<%=consentYear %>"
									day= "<%= consentDay %>" 
									value="<%=signedConsentDate %>"
									styleClass="black_ar"
									disabled="<%=readOnlyReg%>"
								/>		
								<bean:message key="page.dateFormat" />&nbsp;
								<% 
								}
								else if (signedConsentDate.trim().length() > 0 && readOnlyReg)
								{
									Integer consentYear = new Integer(Utility.getYear(signedConsentDate ));
									Integer consentMonth = new Integer(Utility.getMonth(signedConsentDate ));
									Integer consentDay = new Integer(Utility.getDay(signedConsentDate ));
									DateFormat dd = DateFormat.getDateInstance();
									signedConsentDate= dd.format(new Date(consentYear-1900,consentMonth-1,consentDay));
								%>
								
								
								<html:hidden property="consentDate" value="<%=signedConsentDate%>"/>
								
								<%=signedConsentDate%>
								

								<%}
								else
								{  
								%>
								<ncombo:DateTimeComponent name="consentDate"
									id="consentDate"
									formName="consentForm"	
									styleClass="black_ar"
									disabled="<%=readOnlyReg%>"
								/>	
									<span class="grey_ar_s">
								<bean:message key="page.dateFormat" />&nbsp;
								</span>
								<%
								}
							}
							else
							{
								if(signedConsentDate.trim().length() > 0 && !readOnlyReg)
								{
									Integer consentYear = new Integer(Utility.getYear(signedConsentDate ));
									Integer consentMonth = new Integer(Utility.getMonth(signedConsentDate ));
									Integer consentDay = new Integer(Utility.getDay(signedConsentDate ));
								%>
								<ncombo:DateTimeComponent name="consentDate"
									id="consentDate"
									formName="collectionProtocolRegistrationForm"	
									month= "<%=consentMonth %>"
									year= "<%=consentYear %>"
									day= "<%= consentDay %>" 
									value="<%=signedConsentDate %>"
									styleClass="black_ar"
									disabled="<%=readOnlyReg%>"
								/>		
								<span class="grey_ar_s">
								<bean:message key="page.dateFormat" />&nbsp;
								</span>
								<% 
								}
								else if (signedConsentDate.trim().length() > 0 && readOnlyReg )
								{
									Integer consentYear = new Integer(Utility.getYear(signedConsentDate ));
									Integer consentMonth = new Integer(Utility.getMonth(signedConsentDate ));
									Integer consentDay = new Integer(Utility.getDay(signedConsentDate ));
									DateFormat dd = DateFormat.getDateInstance();
									String strConsentDate = dd.format(new Date(consentYear-1900,consentMonth-1,consentDay));
								%>							
								<html:hidden property="consentDate" value="<%=signedConsentDate%>"/>
								<%=strConsentDate%>
								<%
								}
								else
								{  
								%>
								<ncombo:DateTimeComponent name="consentDate"
									id="consentDate"
									formName="collectionProtocolRegistrationForm"	
									styleClass="formDateSized10"
									disabled="<%=readOnlyReg%>" 
								/>		
								<span class="grey_ar_s">
								<bean:message key="page.dateFormat" />&nbsp;
								</span>
								<%
								}
							}
							%>
							
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<%-- If page of Specimen Collection Group or New Specimen --%>						
			<%
			}
			%>				
			<tr >
				<td >
				<%-- Inner table that will show Consents Start--%>
					<div id="" style="overflow:auto;"> <table summary="" cellpadding="4" cellspacing="0" border="0" width="<%=innerWidth%>" id="consentTable">
						<%-- Serial No # --%>	
						<tr class="tableheading">
							<td class="noneditable">
						<strong>
									<bean:message key="requestlist.dataTable.serialNo.label" />
								</strong>
							</td>
							<%-- Title ( Consent Tiers) --%>									
							<td class="noneditable">
								<strong>
									<bean:message key="collectionprotocolregistration.consentTiers" />
								</strong>
							</td>
							<%--Title (Participant response) --%>										
							<td  class="noneditable">
								<strong>
									<bean:message key="collectionprotocolregistration.participantResponses" />
								</strong>
							</td>
						</tr>
						<%-- Get Consents and Responses from DB --%>	
						<%-- For loop Start --%>							
						<%	
						
						for(int counter=0;counter<consentTierList.size();counter++)
						{
							 String[] stringArray =	(String[])consentTierList.get(counter);
							 String responseKey=null;
							 String responseIdKey=null;
							 String consentIDKey = stringArray[0];
							 String consents = stringArray[1];
							 String participantResponseKey =stringArray[2];
							 String participantResponseIDKey=stringArray[3];
							 
							 String consentStatementKey ="ConsentBean:"+counter+"_statement";
							 String participantKey ="ConsentBean:"+counter+"_participantResponse";
							 
							 Object formObject = form;
							 String consentResponseDisplay="";
							 String responseDisplay="";
							 
							 String idKey="";
							 String statusKey="";
							 String statusDisplay="";
							 if(formObject instanceof ClinicalStudyRegistrationForm)
								{
									
									consentResponseDisplay=(String)((ClinicalStudyRegistrationForm)formObject).getConsentResponseValue(consentStatementKey);
									responseDisplay=(String)((ClinicalStudyRegistrationForm)formObject).getConsentResponseValue(participantKey);
								}
								else if(formObject instanceof ConsentResponseForm)
								{
									consentResponseDisplay=(String)((ConsentResponseForm)formObject).getConsentResponseValue(consentStatementKey);
									responseDisplay=(String)((ConsentResponseForm)formObject).getConsentResponseValue(participantKey);
																		
								}
															
						%>		
						<%-- Serial No # --%>				
						<c:set var="style" value="black_ar" scope="page" />	
							<c:if test='${pageScope.count % 2 == 0}'>
								<c:set var="style" value="tabletd1" scope="page" />
							</c:if>

						<tr >
							<td  align="left" class='${pageScope.style}'	  >
								<%=counter+1%>.
							</td>
							<%-- Get Consents # --%>										
							<td class='${pageScope.style}'>
							<html:hidden property="<%=consentIDKey%>"/>
							<html:hidden property="<%=consents%>"/>
							<%=consentResponseDisplay%>
							</td>
							<%-- If Page of Collection Protocol Reg then show drop down --%>										
							<%
							if(pageOf.equals("pageOfCollectionProtocolRegistration")||pageOf.equals("pageOfCollectionProtocolRegistrationCPQuery")  || pageOf.equals("pageOfConsent"))
							{ 
								if(!readOnlyReg)
								{
							%>

								<td class='${pageScope.style}' >
									<html:hidden property="<%=participantResponseIDKey%>"/>
									<html:select property="<%=participantResponseKey%>" styleClass="formFieldSized18" styleId="<%=participantResponseKey%>" size="1"
									>
									<html:options collection="<%=collection%>" labelProperty="name" property="value"/>
									</html:select>
								</td>
							<%-- If Page of SCG or New Specimen or Distribution then show participant Response. --%>																			
							<%
								}//readonly
								else
								{
							%>

									<td class='${pageScope.style}' >
									<%=responseDisplay%>
										<html:hidden property="<%=participantResponseIDKey%>"/>
										<html:hidden property="<%=participantResponseKey%>" value="<%=responseDisplay%>"/>
									</td>
							<%	} 	
							}
							%>
							<%-- For loop Ends --%>						
						<% 
						}
						if(pageOf.equals("pageOfConsent"))
						{
							String caption = "Done";
							if(readOnlyReg)
							{
								caption = "Close";
							}
						%>
							<%-- action button --%>																
							<tr width="10%" >
								<td class="buttonbg" align="left" >
									<input type="button" id="exitBtn"  name="doneConsentButton"  value="<%=caption%>"  class="blue_ar_c"  onclick="submitConsentResponses()"/>
								</td>
							</tr>
						<% 
						}
						%>
					   </table>	</div>
					<%-- Inner table that will show Consents--%>
				</td>	
			</tr>	
	</table>
	<%-- Main table End --%>