<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ page import="edu.wustl.clinportal.util.global.Constants,edu.wustl.clinportal.actionForm.UserForm"%>
<%@ include file="/pages/content/common/AutocompleterCommon.jsp" %> 

<%@ page import="edu.wustl.common.beans.NameValueBean"%>
<%@ page import="edu.wustl.clinportal.util.global.Constants"%>
<%@ page import="org.apache.struts.action.Action"%>
<%@ page import="org.apache.struts.action.ActionError"%>
<%@ page import="edu.wustl.common.util.global.ApplicationProperties"%>
<%@ page import="org.apache.struts.action.ActionErrors"%>

<%@ page
	import="org.apache.struts.action.Action,org.apache.struts.action.ActionError,edu.wustl.common.util.global.ApplicationProperties,org.apache.struts.action.ActionErrors"%>

<%@ page language="java" isELIgnored="false"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<script src="jss/script.js" type="text/javascript"></script>
<script type="text/javascript" src="jss/dhtmlwindow.js"></script>
<script type="text/javascript" src="jss/modal.js"></script>
<link href="css/catissue_suite.css" rel="stylesheet" type="text/css" />
<script language="JavaScript" type="text/javascript"
	src="jss/caTissueSuite.js"></script>

<script type="text/javascript" src="jss/wz_tooltip.js"></script>
<script type="text/javascript" src="jss/queryModule.js"></script>
<%
        String operation = (String) request.getAttribute(Constants.OPERATION);
        String formName,prevPage=null,nextPage=null;
		
		String submittedFor=(String)request.getAttribute(Constants.SUBMITTED_FOR);

		String pageOf = (String)request.getAttribute(Constants.PAGEOF);   
		
		String reqPath = (String)request.getAttribute(Constants.REQ_PATH);  

        boolean readOnlyValue,roleStatus=false;

		if (pageOf.equals(Constants.PAGEOF_APPROVE_USER))
		{
			Long identifier = (Long)request.getAttribute(Constants.PREVIOUS_PAGE);
			prevPage = Constants.USER_DETAILS_SHOW_ACTION+"?"+Constants.SYSTEM_IDENTIFIER+"="+identifier;
			identifier = (Long)request.getAttribute(Constants.NEXT_PAGE);
			nextPage = Constants.USER_DETAILS_SHOW_ACTION+"?"+Constants.SYSTEM_IDENTIFIER+"="+identifier;
		}

        if (operation.equals(Constants.EDIT))
        {
			if (pageOf.equals(Constants.PAGEOF_APPROVE_USER))
			{
				formName = Constants.APPROVE_USER_EDIT_ACTION;
			}
			else if (pageOf.equals(Constants.PAGEOF_USER_PROFILE))
			{
				formName = Constants.USER_EDIT_PROFILE_ACTION;
			}
			else
			{
            	formName = Constants.USER_EDIT_ACTION;
			}
            readOnlyValue = true;
        }
        else
        {
			if (pageOf.equals(Constants.PAGEOF_APPROVE_USER))
			{
				formName = Constants.APPROVE_USER_ADD_ACTION;
			}
			else
			{
            	formName = Constants.USER_ADD_ACTION;
				if (pageOf.equals(Constants.PAGEOF_SIGNUP))
				{
					formName = Constants.SIGNUP_USER_ADD_ACTION;
				}
			}

            readOnlyValue = false;
        }
        String crgId= (String)request.getAttribute(Constants.CRGID);
        UserForm userForm = new UserForm();
		Object obj = request.getAttribute("userForm");
		if(obj != null && obj instanceof UserForm)
		{
		
			userForm = (UserForm)obj;
			if (pageOf.equals(Constants.PAGEOF_APPROVE_USER) &&
			   (Constants.APPROVE_USER_PENDING_STATUS.equals(userForm.getStatus()) || 
				Constants.APPROVE_USER_REJECT_STATUS.equals(userForm.getStatus()) ||
				Constants.SELECT_OPTION.equals(userForm.getStatus())))
			{
				roleStatus = true;
				if (userForm.getStatus().equals(Constants.APPROVE_USER_PENDING_STATUS))
				{
					operation = Constants.EDIT;
				}
			}
			crgId=String.valueOf(userForm.getCancerResearchGroupId());
		}
		
		if (pageOf.equals(Constants.PAGEOF_USER_PROFILE))
		{
				roleStatus = true;
		}
%>
<script src="jss/script.js" type="text/javascript"></script>
<!-- Mandar : 434 : for tooltip -->
<script language="JavaScript" type="text/javascript" src="jss/javaScript.js"></script>
<script>
//If the administrator keeps the user status pending update the user record and disable role.
function handleStatus(status)
{
	document.forms[0].role.value=<%=Constants.SELECT_OPTION_VALUE%>;
	document.forms[0].role.readOnly=true;
	document.getElementById("displayrole").readOnly=true;
	if (status.value == "<%=Constants.APPROVE_USER_APPROVE_STATUS%>")
	{
    	document.forms[0].role.readOnly=false;
	   	document.getElementById("displayrole").readOnly=false;
	}
	else
	{
		document.getElementById("displayrole").value="";
		document.getElementById("role").value="";
	}
}
</script>
<table width="100%" border="0" cellpadding="0" cellspacing="0"
	class="maintable">

	<html:form action="<%=formName%>">

		<logic:equal name="pageOf" value="pageOfSignUp">
			<html:hidden property="activityStatus" />
		</logic:equal>
		<tr>
			<td class="td_color_bfdcf3">
			<table border="0" cellpadding="0" cellspacing="0">

				<tr>
					<td class="td_table_head"><span class="wh_ar_b"><bean:message
						key="user.name" /></span></td>
					<td align="right"><img
						src="images/uIEnhancementImages/table_title_corner2.gif"
						alt="Page Title - User" width="31" height="24" /></td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td><logic:equal name="pageOf"
				value="pageOfSignUp">
				<table width="90%" border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td valign="top" class="td_color_bfdcf3">&nbsp;</td>
						<td valign="top" class="td_color_bfdcf3">&nbsp;</td>
						<td valign="top" class="td_color_bfdcf3">&nbsp;</td>
						<td valign="top" class="td_color_bfdcf3">&nbsp;</td>
						<td width="90%" valign="top" class="td_color_bfdcf3">&nbsp;</td>
					</tr>
				</table>
				</logic:equal> <logic:notEqual name="pageOf" value="<%=Constants.PAGEOF_SIGNUP%>">
					<tr>
						<td class="td_tab_bg"><img
							src="images/uIEnhancementImages/spacer.gif" alt="spacer"
							width="50" height="1"></td>
						<logic:equal name="operation" value="add">
							<td valign="bottom"><img
								src="images/uIEnhancementImages/tab_add_selected.jpg" alt="Add"
								width="57" height="22" /><a href="#"></a></td>
							<logic:notEqual parameter="openInCPFrame" value='yes'>
							<td valign="bottom"><html:link page="/SimpleQueryInterface.do?pageOf=pageOfUserAdmin&aliasName=User&menuSelected=1">
									<img src="images/uIEnhancementImages/tab_edit_notSelected.jpg"
										alt="Edit" width="59" height="22" border="0" />
								</html:link><a href="#"></a></td>
								<td valign="bottom"><html:link
									page="/ApproveUserShow.do?pageNum=1&menuSelected=1">
									<img src="images/uIEnhancementImages/tab_approve_user.jpg"
										alt="Approve New Users" width="139" height="22" border="0" />
								</html:link><a href="#"></a></td>
							</logic:notEqual>
						</logic:equal>
						<logic:equal name="operation" value="edit">
							<td valign="bottom"><html:link
								page="/User.do?operation=add&pageOf=pageOfUserAdmin&menuSelected=1">
								<img src="images/uIEnhancementImages/tab_add_notSelected.jpg"
									alt="Add" width="57" height="22" />
							</html:link><a href="#"></a></td>
							<logic:notEqual parameter="openInCPFrame" value='yes'>
								<td valign="bottom"><img
									src="images/uIEnhancementImages/tab_edit_selected.jpg"
									alt="Edit" width="59" height="22" border="0" /></td>
								<td valign="bottom"><html:link
									page="/ApproveUserShow.do?pageNum=1&menuSelected=1">
									<img src="images/uIEnhancementImages/tab_approve_user.jpg"
										alt="Approve New Users" width="139" height="22" border="0" />
								</html:link><a href="#"></a></td>
							</logic:notEqual>
						</logic:equal>
						
						<td width="90%" valign="bottom" class="td_tab_bg">&nbsp;</td>
					</tr>
				</table>
				 </logic:notEqual>
			<table width="80%" border="0" cellpadding="3" cellspacing="0" class="tablepadding">
			<tr>
			 <td>
			    <table width="80%" border="0" cellpadding="3" cellspacing="0" class="whitetable_bg">
      			<tr>
					<td colspan="2" align="left" class="bottomtd"><%@ include
						file="/pages/content/common/ActionErrors.jsp"%>
					</td>
				</tr>
				<tr>
					<td colspan="2" class="messagetexterror">
					<div id="errorMessImgDiv" style="display:none">

					<table>
						<tr>
							<td><c:if
								test="${requestScope['org.apache.struts.action.ERROR'] == null }">
								<tr>
									<td valign="top"><img
										src="images/uIEnhancementImages/alert-icon.gif"
										alt="error messages" width="16" vspace="0" hspace="0"
										height="18" valign="top"></td>
									<td class="messagetexterror" align="left"><strong><bean:message
										key="errors.title" /></strong></td>
								</tr>
							</c:if></td>
						</tr>
					</table>
					</div>
					<div id="errorMess" style="display:none"></div>
					</td>
				</tr>	
				<tr>
					<td colspan="2" align="left" class="tr_bg_blue1"><span
						class="blue_ar_b">&nbsp;<logic:equal name="operation"
						value='${requestScope.addforJSP}'>
					</logic:equal><bean:message key="user.details.title" /> </td></tr>
					<logic:equal name="operation" value='${requestScope.editforJSP}'>
						<tr>
						<td class="formTitle" height="20" colspan="3">
							<logic:equal name="operation" value="<%=Constants.ADD%>">
								<bean:message key="user.title"/>
							</logic:equal>
							<logic:equal name="operation" value="<%=Constants.EDIT%>">
								<bean:message key="user.editTitle"/>
							</logic:equal>
						</td>
					</tr>
					</logic:equal> </span></td>
				</tr>
				<tr>
					<td colspan="2" align="left" class="showhide" width="60%">
		
		<!-- NEW USER REGISTRATION BEGINS-->
				<table width="100%" border="0" cellpadding="3" cellspacing="0">
				<tr>
					<td>
						<html:hidden property="operation" value="<%=operation%>" />
						<html:hidden property="submittedFor" value="<%=submittedFor%>"/>	
					</td>
				</tr>
				
				<tr>
					<td>
						<html:hidden property="id" />
						<html:hidden property="csmUserId" />
						<html:hidden property="<%=Constants.REQ_PATH%>" value="<%=reqPath%>" />
					</td>
				</tr>
				
				<tr>
					<td>
						<html:hidden property="<%=Constants.PAGEOF%>" value="<%=pageOf%>" />
					</td>
				</tr>

				<logic:notEqual name="<%=Constants.OPERATION%>" value="<%=edu.wustl.simplequery.global.Constants.SEARCH%>">
				
					<%
						boolean readOnlyEmail = false;
						if (operation.equals(Constants.EDIT) && pageOf.equals(Constants.PAGEOF_USER_PROFILE))
						{
							readOnlyEmail = true;
						}
					%>
					<tr>
							<td width="1%" align="center" class="black_new"><span
								class="blue_ar_b"><img
								src="images/uIEnhancementImages/star.gif" alt="Mandatory"
								width="6" height="6" hspace="0" vspace="0" /></span></td>
							<td width="17%" align="left" class="black_new"><bean:message
								key="user.emailAddress" /></td>
							<td width="19%" align="left"><html:text
								styleClass="black_new" maxlength="255" size="30"
								styleId="emailAddress" property="emailAddress"
								readonly='${requestScope.readOnlyEmail}' /></td>
							<td width="13%" align="left">&nbsp;</td>

							<logic:notEqual name="pageOf"
								value='${requestScope.pageOfUserProfile}'>
								<td width="1%" align="center"><span class="blue_ar_b"><img
									src="images/uIEnhancementImages/star.gif" alt="Mandatory"
									width="6" height="6" hspace="0" vspace="0" /></span></td>
								<td width="17%" align="left"><label
									for="confirmEmailAddress" class="black_new"><bean:message
									key="user.confirmemailAddress" /></label></td>
								<td width="19%" align="left"><html:text
									styleClass="black_new" maxlength="255" size="30"
									styleId="confirmEmailAddress" property="confirmEmailAddress"
									readonly='${requestScope.readOnlyEmail}' /></td>
								<td width="13%" align="left" valign="top">&nbsp;</td>
							</logic:notEqual>
							<logic:equal name="pageOf"
								value='${requestScope.pageOfUserProfile}'>
								<td width="32%" align="left" colspan="3" valign="top">&nbsp;</td>
							</logic:equal>
<!-- Mandar 24-Apr-06 : bugid 972 : Confirm Email address end -->

					<tr>
							<td align="center" class="black_new"><span class="blue_ar_b"><img
								src="images/uIEnhancementImages/star.gif" alt="Mandatory"
								width="6" height="6" hspace="0" vspace="0" /></span></td>
							<td align="left" class="black_new"><bean:message
								key="user.lastName" /></td>
							<td align="left"><html:text styleClass="black_new"
								maxlength="255" size="30" styleId="lastName" property="lastName" /></td>
							<td align="left" class="black_new">&nbsp;</td>
							<td align="center" class="black_new"><span class="blue_ar_b"><img
								src="images/uIEnhancementImages/star.gif" alt="Mandatory"
								width="6" height="6" hspace="0" vspace="0" /></span></td>
							<td align="left" class="black_new"><bean:message
								key="user.firstName" /></td>
							<td align="left"><html:text styleClass="black_new"
								maxlength="255" size="30" styleId="firstName"
								property="firstName" /></td>
							<td align="left" valign="top">&nbsp;</td>
						</tr>
					<logic:equal name="<%=Constants.PAGEOF%>" value="<%=Constants.PAGEOF_USER_ADMIN%>">
					<logic:equal name="<%=Constants.OPERATION%>" value="<%=Constants.EDIT%>">
					<tr>
					<td align="center" class="black_new"><span
										class="blue_ar_b"><img
										src="images/uIEnhancementImages/star.gif" alt="Mandatory"
										width="6" height="6" hspace="0" vspace="0" /></span></td>
					<td align="left" class="black_new"><label for="newPassword">
									<bean:message key="user.newPassword" /> </label></td>
					<td align="left"><html:password styleClass="black_new"
										size="30" styleId="newPassword" property="newPassword"/></td>
				<td align="left" class="black_new">&nbsp;</td>
									<td align="center" class="black_new"><span
										class="blue_ar_b"><img
										src="images/uIEnhancementImages/star.gif" alt="Mandatory"
										width="6" height="6" hspace="0" vspace="0" /></span></td>
									<td align="left" class="black_new"><label
										for="confirmNewPassword"> <bean:message
										key="user.confirmNewPassword" /> </label></td>
									<td align="left"><html:password styleClass="black_new"
										size="30" styleId="confirmNewPassword"
										property="confirmNewPassword"/></td>
									<td align="left" valign="top">&nbsp;</td>
				</tr>
				</logic:equal>
				</logic:equal>
					
					<tr>
							<td align="center" class="black_new">&nbsp;</td>
							<td align="left" class="black_new"><bean:message
								key="user.street" /></td>
							<td align="left"><html:text styleClass="black_new"
								maxlength="255" size="30" styleId="street" property="street" /></td>
							<td align="left" class="black_new">&nbsp;</td>
							<td align="center" class="black_new"><span class="blue_ar_b"><img
								src="images/uIEnhancementImages/star.gif" alt="Mandatory"
								width="6" height="6" hspace="0" vspace="0" /></span></td>
							<td align="left" class="black_new"><bean:message
								key="user.city" /></td>
							<td align="left"><html:text styleClass="black_new"
								maxlength="50" size="30" styleId="city" property="city" /></td>
							<td align="left" valign="top">&nbsp;</td>
						</tr>
							<tr>
							<td align="center" class="black_new"><span class="blue_ar_b"><img
								src="images/uIEnhancementImages/star.gif" alt="Mandatory"
								width="6" height="6" hspace="0" vspace="0" /></span></td>
							<td align="left" class="black_new"><bean:message
								key="user.state" /></td>
							<td align="left" nowrap class="black_new"><autocomplete:AutoCompleteTag
								property="state" optionsList='${requestScope.stateList}'
								initialValue='${userForm.state}' styleClass="black_new" size="27" /></td>
							<td align="left" class="black_new">&nbsp;</td>
							<td align="center" class="black_new"><span class="blue_ar_b"><img
								src="images/uIEnhancementImages/star.gif" alt="Mandatory"
								width="6" height="6" hspace="0" vspace="0" /></span></td>
							<td align="left" class="black_new"><bean:message
								key="user.zipCode" /></td>
							<td align="left" class="black_new"><html:text
								style="text-align:right" styleClass="black_new" maxlength="30"
								size="30" styleId="zipCode" property="zipCode" /></td>
							<td align="left" valign="top">&nbsp;</td>
						</tr>
					
				<tr>
							<td align="center" class="black_new"><span class="blue_ar_b"><img
								src="images/uIEnhancementImages/star.gif" alt="Mandatory"
								width="6" height="6" hspace="0" vspace="0" /></span></td>
							<td align="left" class="black_new"><bean:message
								key="user.country" /></td>
							<td align="left" nowrap class="black_new"><autocomplete:AutoCompleteTag
								property="country" optionsList='${requestScope.countryList}'
								initialValue="United States" styleClass="black_new"
								size="27" /></td>
							<td align="left" class="black_new">&nbsp;</td>
							<td align="center" class="black_new"><span class="blue_ar_b"><img
								src="images/uIEnhancementImages/star.gif" alt="Mandatory"
								width="6" height="6" hspace="0" vspace="0" /></span></td>
							<td align="left" class="black_new"><bean:message
								key="user.phoneNumber" /></td>
							<td align="left"><html:text styleClass="black_new"
								style="text-align:right" maxlength="50" size="30"
								styleId="phoneNumber" property="phoneNumber" /></td>
							<td align="left" valign="top">&nbsp;</td>
						</tr>
					
					<tr>
							<td align="center" class="black_new">&nbsp;</td>
							<td align="left" class="black_new"><bean:message
								key="user.faxNumber" /></td>
							<td align="left"><html:text styleClass="black_new"
								style="text-align:right" maxlength="50" size="30"
								styleId="faxNumber" property="faxNumber" /></td>
							<td align="left" class="black_new">&nbsp;</td>
							<td align="center" class="black_new"><span class="blue_ar_b"><img
								src="images/uIEnhancementImages/star.gif" alt="Mandatory"
								width="6" height="6" hspace="0" vspace="0" /></span></td>
							<td align="left" class="black_new"><label for="institutionId"><bean:message
								key="user.institution" /></label></td>
							<td align="left" class="black_new"><autocomplete:AutoCompleteTag property="institutionId"
										  optionsList = "<%=request.getAttribute("institutionList")%>"
										  initialValue="<%=new Long(userForm.getInstitutionId())%>"
										styleClass="black_new"
											staticField="false" size="27" />
										  
							 </td>
							<td align="left"><logic:notEqual name="pageOf"
								value="<%=Constants.PAGEOF_SIGNUP%>">
								<html:link href="#" styleClass="view" styleId="newInstitution"
								onclick="addNewAction('UserAddNew.do?addNewForwardTo=institution&forwardTo=user&addNewFor=institution')">
									<bean:message key="buttons.addNew" />
								</html:link>
							</logic:notEqual></td>
						</tr>
					
					<tr>
							<td align="center" class="black_new"><span class="blue_ar_b"><img
								src="images/uIEnhancementImages/star.gif" alt="Mandatory"
								width="6" height="6" hspace="0" vspace="0" /></span></td>
							<td align="left" class="black_new"><label for="departmentId"><bean:message
								key="user.department" /> </label></td>
							<td align="left" class="black_new"><autocomplete:AutoCompleteTag
								property="departmentId"
								optionsList='${requestScope.departmentList}'
								initialValue='${userForm.departmentId}' styleClass="black_new"
								staticField="false" size="27" /></td>
							<td align="left"><a href="#" class="view"> <logic:notEqual
								name="pageOf" value="<%=Constants.PAGEOF_SIGNUP%>">
								<html:link href="#" styleClass="view" styleId="newDepartment"
		onclick="addNewAction('UserAddNew.do?addNewForwardTo=department&forwardTo=user&addNewFor=department')">
									<bean:message key="buttons.addNew" />
								</html:link>
							</logic:notEqual></a></td>
							<td align="center" class="black_new"><span class="blue_ar_b"><img
								src="images/uIEnhancementImages/star.gif" alt="Mandatory"
								width="6" height="6" hspace="0" vspace="0" /></span></td>
							<td align="left" class="black_new"><label
								for="cancerResearchGroupId"><bean:message
								key="user.cancerResearchGroup" /> </label></td>
							<td align="left" class="black_new">
							<autocomplete:AutoCompleteTag
								property="cancerResearchGroupId"
								optionsList='${requestScope.cancerResearchGroupList}'
								initialValue="<%=new Long(crgId)%>"
								styleClass="black_new" staticField="false" size="27" /></td>
							<td align="left"><a href="#" class="view"> <logic:notEqual
								name="pageOf" value="<%=Constants.PAGEOF_SIGNUP%>">
								<html:link href="#" styleClass="view"
									styleId="newCancerResearchGroup" onclick="addNewAction('UserAddNew.do?addNewForwardTo=cancerResearchGroup&forwardTo=user&addNewFor=cancerResearchGroup')">
									<bean:message key="buttons.addNew" />
								</html:link>
							</logic:notEqual></a></td>
						</tr>		
					<logic:equal name="<%=Constants.PAGEOF%>" value="<%=Constants.PAGEOF_USER_PROFILE%>">
					<tr>
						<td width="1%" align="center" class="black_new"><span
										class="blue_ar_b"><img
										src="images/uIEnhancementImages/star.gif" alt="Mandatory"
										width="6" height="6" hspace="0" vspace="0" /></span></td>
						<td width="17%" align="left" class="black_new"><label><bean:message
										key="user.role"/></label></td>
									
						<td class="black_ar_s">
                                        <autocomplete:AutoCompleteTag property="role"
										  optionsList = "<%=request.getAttribute("roleList")%>"
										  initialValue="<%=userForm.getRole()%>"
										  styleClass="black_ar_s"
                                          staticField="false" size="32"
										  readOnly="<%=roleStatus + ""%>"/>
										</td>
									</tr>	
								</logic:equal>
							</logic:notEqual>
						</table>
					</td>
				</tr>
		 	<tr>
			  <td>
				<table summary="" cellpadding="3" cellspacing="0" border="0" width="990">
					
					<logic:notEqual name="<%=Constants.PAGEOF%>" value="<%=Constants.PAGEOF_SIGNUP%>">
					<logic:notEqual name="<%=Constants.PAGEOF%>" value="<%=Constants.PAGEOF_USER_PROFILE%>">
					<tr>
						<td colspan="10" align="left" class="tr_bg_blue1"><span
								class="blue_ar_b">&nbsp;
							<bean:message key="user.administrativeDetails.title" /></span></td>
							<td colspan="20"align="right" class="tr_bg_blue1"></td>	
							</tr>
					<logic:equal name="<%=Constants.PAGEOF%>" value="<%=Constants.PAGEOF_APPROVE_USER%>">
					<tr>
						<td width="1%" height="25" align="left" class="black_ar_t">
						<span class="blue_ar_b"><img
													src="images/uIEnhancementImages/star.gif" alt="Mandatory"
													width="6" height="6" hspace="0" vspace="0" /></span></td>
							<td width="17%" align="left" class="black_ar_t">
								<label for="status">
									<bean:message key="user.approveOperation" />
								</label>
							</td>
						<td width="19%" colspan="1" align="left" valign="top"
													class="black_ar_t">
                              	<html:select property="status" styleClass="formFieldSizedNew" styleId="status" size="1" onchange="javascript:handleStatus(this)">
								<html:options name="statusList" labelName="statusList" />
							</html:select>
						</td>
					</tr>
					</logic:equal>						
					<tr>
						<td width="1%" align="center" class="black_new"><span
										class="blue_ar_b"><img
										src="images/uIEnhancementImages/star.gif" alt="Mandatory"
										width="6" height="6" hspace="0" vspace="0" /></span></td>
						<td width="17%" align="left" class="black_new"><label><bean:message
										key="user.role" /></label></td>
									
						<td class="black_ar_s">
                                        <autocomplete:AutoCompleteTag property="role"
										  optionsList = "<%=request.getAttribute("roleList")%>"
										  initialValue="<%=userForm.getRole()%>"
										  styleClass="black_ar_s"
                                          staticField="false" size="32"
										  readOnly="<%=roleStatus + ""%>"/>
						</td>
					</tr>
    				 <tr>
			     		<td width="1%" height="25" align="left" class="black_ar_t">&nbsp;</td>
						<td width="17%" align="left" class="black_ar_t"><label
						for="comments"> <bean:message key="user.comments" />
						</label></td>
				    	<td width="19%" colspan="1" align="left" valign="top"><label>
						<html:textarea styleClass="black_ar_s" cols="33" rows="3"
						styleId="comments" property="comments" /> </label></td>
				 	 </tr>
					</logic:notEqual>
					</logic:notEqual>
					
					<logic:equal name="<%=Constants.PAGEOF%>" value="<%=Constants.PAGEOF_USER_ADMIN%>">
					<logic:equal name="<%=Constants.OPERATION%>" value="<%=Constants.EDIT%>">
					<tr>
					<td width="1%" height="25" align="left" class="black_ar_t">
													<span class="blue_ar_b"><img
														src="images/uIEnhancementImages/star.gif" alt="Mandatory"
														width="6" height="6" hspace="0" vspace="0" /></span></td>
													<td width="17%" align="left" class="black_ar_t"><label
														for="activityStatus"> <bean:message
														key="user.activityStatus" /> </label></td>
													<td width="19%" align="left" nowrap class="black_ar_t"><autocomplete:AutoCompleteTag
														property="activityStatus"
														optionsList='${requestScope.activityStatusList}'
														initialValue='${userForm.activityStatus}'
														styleClass="black_new" size="27" /></td>
													<td width="13%" align="left">&nbsp;</td>
					</tr>
					</logic:equal>
					</logic:equal>
					<tr>
						<!-- action buttons begins -->
						
									<td colspan="30" class="buttonbg"><html:submit
										styleClass="blue_ar_b">
										<bean:message key="buttons.submit" />
									</html:submit></td>
						<!-- action buttons end -->
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<!-- NEW USER REGISTRATION ends-->
	</html:form>
</table>