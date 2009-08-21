<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/nlevelcombo.tld" prefix="ncombo" %>

<!--sneha changed classes only -->
<%-- Main table Start --%>
<table style="display:none;" cellpadding="4" cellspacing="0" border="0" class="contentPage" width="100%" id="consentTierTable">
	<tr>
		<td>
			<%-- First Table for Unsigned URL   Start--%>
			<table summary="" cellpadding="3" cellspacing="0" border="0"  width="100%" id="table10">
				
				<%-- Title Add Consents --%>
				<tr>
					<td colspan="10" align="left" class="tr_bg_blue1"><span
								class="blue_ar_b">&nbsp;
						<bean:message key="consent.addconsents" />
					</td>
				</tr>
				
				<%-- Title Unsigned URL form --%>					
				<tr>
					<td colspan="2">&nbsp;</td>
					<td class="black_new" colspan="3">
						<label for="unsignedConsentURLName">
							<bean:message key="consent.unsignedformurl" />
						</label>
					</td>
					<td class="black_new" colspan="2">
						<html:text styleClass="black_new" maxlength="200" size="30" styleId="unsignedConsentURLName" property="unsignedConsentURLName"/>
					</td>
				</tr>
			</table>	
			<%-- First Table for Unsigned URL   End--%>					
		</td>
	</tr>		

	<tr>
		<td style="width:99%;">
			<%-- Outer Table for Add Consents  Start --%>			
			<table summary="" cellpadding="0" cellspacing="0" border="0"  width="100%">

				<%-- Title Consent Tier--%>
				<tr>
					<td colspan="10" align="left" class="tr_bg_blue1"><span
								class="blue_ar_b">&nbsp;
						<bean:message key="consent.consenttiers" />
					</td>
				</tr>
	<tr>
					<td colspan="2">
						<div style="overflow:auto;height:250px;">
							<%-- Inner Table for Add Consents  Start --%>											
							<table summary="" cellpadding="3" cellspacing="0" border="0" width="60%" id="innertable">
							<tbody id="consent">	
								<tr>
									<%-- Title Serial No --%>											
									<td class="black_new" colspan="1">
										<div align="right">
											<b><bean:message key="requestlist.dataTable.serialNo.label" /></b>
										</div>
									</td>	
									
									<%-- Title CheckBox --%>	
									<%
									if(operation.equals(Constants.EDIT))
									{
									%>
									<td class="black_new" width="5%">
										<div align="center">
											<input type=checkbox name="selectAll" onclick="checkAll(this)" disabled="disabled"/>
										</div>	
									</td>	
									<%
									}
									else
									{
									%>
									<td class="black_new" width="5%">
										<div align="centre">
											<input type=checkbox name="selectAll" onclick="checkAll(this)"/>
										</div>	
									</td>	
									<%
									}
									%>
									<%-- Title Statements --%>
									<td class="black_new" colspan="3">
										&nbsp;<b><bean:message key="consent.statements" /></b>
									</td>
								</tr>
							 <%
								ClinicalStudyForm clinicalStudyForm = null;
								clinicalStudyForm =(ClinicalStudyForm)request.getAttribute(Constants.CLINICAL_STUDY_FORM);
								if(clinicalStudyForm != null && clinicalStudyForm instanceof ClinicalStudyForm)
								{
									noOfConsents = clinicalStudyForm.getConsentTierCounter();								
								}
								for(int counter=1;counter<=noOfConsents;counter++)
								{		
									String consentName="consentValue(ConsentBean:"+counter+"_statement)";
									String consentKey="consentValue(ConsentBean:"+counter+"_consentTierID)";
									String consentCheck="consentcheckBoxs";
									String consentDisableKey="ConsentBean:"+counter+"_disableConsentKey";
									Boolean disable = (Boolean)clinicalStudyForm.getConsentValue(consentDisableKey);
									String condn = "";
									if(disable != null && disable == true)
										condn="disabled='disabled'";									
									
								%>
									
								<tr>
									<td class="tabrightmostcell" width="3%" align="right" width="5%">
										<%=counter%>.
									</td>
									<td class="formField" width="10%" align="center" width="5%">
																
									<input type="checkbox" name="<%=consentCheck%>" id="<%=consentCheck %>" <%=condn%> />
									
									</td>
									<html:hidden property="<%=consentKey%>"/>
									<td class="black_new" width="60%"  colspan="2">
										<html:textarea styleClass="black_new"  style="width:60%;" rows="2" property="<%=consentName%>"/>
									</td>
								</tr>
								<%
								}
								%>
								</tbody>
							</table>
						<%-- Inner Table for Add Consents  End --%>										
						</div>
					</td>
				</tr>
				<%-- Outer Table for Add Consents End --%>
				
			</table>	
			<table cellpadding="0" cellspacing="0" border="0" width = "100%" id="submittable">
				<tr>
				<%-- Add and Remove action button--%>							
					<td align="left" class="tr_bg_blue1">
					<html:button property="addButton" styleClass="black_new" disabled="<%=consentAddDisabled%>" onclick="addConsentTier()" value="Add More"/>
						<html:hidden property="consentTierCounter"/>
						<%
						String delConsent = "deleteSelected();";
						
						%>	
						<html:button property="removeButton" styleClass="black_new" disabled="<%=consentAddDisabled%>" onclick="<%=delConsent%>" value="Delete"/>
					
						<html:button styleClass="ar_bg_blue" property="submitPage" onclick="clinicalstudyPage()">
							<< <bean:message key="clinicalstudy.clinicalstudy" />
						</html:button>
					</td>
				</tr>
			</table>
			&nbsp;
		</td>
	</tr>	
</table>	
<%-- Main table End --%>