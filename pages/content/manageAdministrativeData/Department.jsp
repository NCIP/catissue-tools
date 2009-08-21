<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="edu.wustl.clinportal.util.global.Constants"%>
	
<%
        String operation = (String) request.getAttribute(Constants.OPERATION);
        String formName = Constants.DEPARTMENT_ADD_ACTION;
		
		String submittedFor=(String)request.getAttribute(Constants.SUBMITTED_FOR);
		
        boolean readOnlyValue;
        if (operation.equals(Constants.EDIT))
        {
            formName = Constants.DEPARTMENT_EDIT_ACTION;
            readOnlyValue = true;
        }
        else//Add
        {
            formName = Constants.DEPARTMENT_ADD_ACTION;
            readOnlyValue = false;
        }
%>	
<!-- sneha: removed html:errors and messages and included actionerrors.jsp in later part -->
<table width="100%" border="0" cellpadding="0" cellspacing="0" class="maintable"><!-- <table> tag as per catissue -->

<html:form action="<%=formName%>">
<!-- NEW Institution REGISTRATION BEGINS-->
<!-- sneha: complete <tr> as per catissue,except for "key" attribute value-->
	<tr>
	 <td class="td_color_bfdcf3">
		<table border="0" cellpadding="0" cellspacing="0">
			<tr>
			<td class="td_table_head"><span class="wh_ar_b"><bean:message key="Department.header"/></span></td>
			<td><img src="images/uIEnhancementImages/table_title_corner2.gif" alt="Page Title - Department" width="31" height="24" />
				<html:hidden property="operation" value="<%=operation%>"/>
				<html:hidden property="submittedFor" value="<%=submittedFor%>"/>
		</td>
			</tr>
		</table>
	</td>
</tr>
       <!-- sneha: complete <tr> as per catissue-->
		<tr>
			<td><html:hidden property="id" /></td>
		</tr>
		<tr>
			 <td class="tablepadding">
				<table width="100%" border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td width="4%" class="td_tab_bg" ><img src="images/spacer.gif" alt="spacer" width="50" height="1"></td>
			 				<logic:equal name="operation" value="<%=Constants.ADD%>">
								 <td  valign="bottom" ><img src="images/uIEnhancementImages/tab_add_selected.jpg" alt="Add" width="57" height="22" /></td>
								<td  valign="bottom"><html:link page="/SimpleQueryInterface.do?pageOf=pageOfDepartment&aliasName=Department"><img src="images/uIEnhancementImages/tab_edit_notSelected.jpg" alt="Edit" width="59" height="22" border="0" /></html:link></td>
							</logic:equal>
							<logic:equal name="operation" value="<%=Constants.EDIT%>">
								<td  valign="bottom" ><html:link page="/Department.do?operation=add&pageOf=pageOfDepartment"><img src="images/uIEnhancementImages/tab_add_notSelected.jpg" alt="Add" width="57" height="22" border ="0" /></html:link></td>
								<td  valign="bottom" ><img src="images/uIEnhancementImages/tab_edit_selected.jpg" alt="Edit" width="59" height="22"/></td>
							</logic:equal>
							<td width="90%" valign="bottom" class="td_tab_bg">&nbsp;</td>
						 </tr>
					</table>
					<!-- sneha: <table> tag and 1st <tr> as per catissue-->
					<table width="100%" border="0" cellpadding="3" cellspacing="0" class="whitetable_bg">
							 <tr>
								<td align="left" class="bottomtd">
									<%@ include file="/pages/content/common/ActionErrors.jsp" %>
								</td>
							</tr>
								<!-- Name of the Department -->
								<!-- changed class attribute values-->
							 <tr>
       							<td align="left" class="tr_bg_blue1"><span class="blue_ar_b"> &nbsp;<logic:equal name="operation" value="<%=Constants.ADD%>" >
       							<bean:message key="department.title"/></logic:equal><logic:equal name="operation"  value="<%=Constants.EDIT%>" >
       							<bean:message key="department.editTitle"/></logic:equal></span>
								</td>
					        </tr>
							 <!-- sneha: <tr> as per catissue ,except for "key" attribute value-->
							<tr>
							    <td align="left" class="showhide">
									<table width="100%" border="0" cellpadding="3" cellspacing="0">
										 <tr>
											<td width="1%" align="center" class="black_ar_new"><span class="blue_ar_b"><img src="images/uIEnhancementImages/star.gif" alt="Mandatory" width="6" height="6" hspace="0" vspace="0" /></span></td>
											<td width="15%" align="left" class="black_ar_new"><bean:message key="department.name"/> </td>
											<td width="84%" align="left"><label>
											<html:text styleClass="black_ar_new" maxlength="255"  size="30" styleId="name" property="name"/>
											</label>
											</td>
										</tr>
									</table>
								</td>
							  </tr>
							  <!-- sneha: changed class attribute values-->
							 <tr>
								<td class="buttonbg"><html:submit styleClass="blue_ar_b">
									<bean:message  key="buttons.submit" />
									</html:submit>
								</td>
							</tr>
					</table>
				 </td>
			</tr>
		  </html:form>
	</table>
		
