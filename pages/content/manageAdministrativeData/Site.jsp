<!-- sneha: added missing files from catissue-->
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ page import="edu.wustl.clinportal.util.global.Constants"%>
<%@ page import="edu.wustl.clinportal.actionForm.SiteForm"%>

<%@ page language="java" isELIgnored="false"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script language="JavaScript" type="text/javascript" src="jss/ajax.js"></script>

<%@ include file="/pages/content/common/AdminCommonCode.jsp"%>
<%@ include file="/pages/content/common/AutocompleterCommon.jsp"%>
<link rel="stylesheet" type="text/css" href="css/catissue_suite.css" />
<script language="JavaScript" type="text/javascript" src="jss/ajax.js"></script>
<script language="JavaScript" src="jss/script.js" type="text/javascript"></script>
<!-- Mandar : 434 : for tooltip -->
<script language="JavaScript" type="text/javascript"
	src="jss/javaScript.js"></script>

<%
        String operation = (String) request.getAttribute(Constants.OPERATION);
		String pageOf = (String) request.getAttribute(Constants.PAGEOF);

		String submittedFor=(String)request.getAttribute(Constants.SUBMITTED_FOR);
		
        String formName;

        boolean readOnlyValue;
        if (operation.equals(Constants.EDIT))
        {
            formName = Constants.SITE_EDIT_ACTION;
            readOnlyValue = true;
        }
        else
        {
            formName = Constants.SITE_ADD_ACTION;
            readOnlyValue = false;
        }
		SiteForm form = new SiteForm();
		Object obj = request.getAttribute("siteForm");
		if(obj != null && obj instanceof SiteForm)
			{
				form = (SiteForm)obj;
		}
    
//****************  Delete below commented code later  ***********************
    // ----------- add new    
       	//String reqPath = (String)request.getAttribute(Constants.REQ_PATH);
		//String appendingPath = "/Site.do?operation=add&pageOf=pageOfSite";
		//if (reqPath != null)
		//	appendingPath = reqPath + "|/Site.do?operation=add&pageOf=pageOfSite";
	
	   	//if(!operation.equals("add") )
	   	//{
	   	//	Object obj = request.getAttribute("siteForm");
		//	if(obj != null && obj instanceof SiteForm)
		//	{
		//		SiteForm form = (SiteForm)obj;
		//  		appendingPath = "/SiteSearch.do?operation=search&pageOf=pageOfSite&id="+form.getId() ;
		//   		System.out.println("---------- Site JSP appendingPath -------- : "+ appendingPath);
		//   	}
	   	//}
		
        
%>

<script language="JavaScript" type="text/javascript" src="jss/ajax.js"></script>
<script>


function onCoordinatorChange()
{
	var submittedForValue = document.forms[0].submittedFor.value;
	var action = "Site.do?"+"operation="+document.forms[0].operation.value+"&pageOf=pageOfSite&isOnChange=true&coordinatorId="+document.getElementById("coordinatorId").value+"&submittedFor="+submittedForValue;
	document.forms[0].action = action;
	document.forms[0].submit();
}
	
</script>
<script language="JavaScript" src="jss/script.js" type="text/javascript"></script>
<!-- Mandar : 434 : for tooltip -->
<script language="JavaScript" type="text/javascript" src="jss/javaScript.js"></script>

<table width="100%" border="0" cellpadding="0" cellspacing="0" class="maintable"><!-- sneha:<table> tag from catissue-->
<html:form action="<%=formName%>">
<!-- sneha: complete <tr> from catiisue,except for "key" attribute value-->
<tr>
    <td class="td_color_bfdcf3"><table border="0" cellpadding="0" cellspacing="0">
      <tr>
        <td class="td_table_head"><span class="wh_ar_b"><bean:message key="Site.header" /></span></td>
        <td><img src="images/uIEnhancementImages/table_title_corner2.gif" alt="Page Title - Site" width="31" height="24" /></td>
      </tr>
    </table></td>
  </tr>
<!-- sneha: <tr> from catissue-->
  <tr>
    <td class="tablepadding"><table width="100%" border="0" cellpadding="0" cellspacing="0">
      <tr>
        <td width="4%" class="td_tab_bg" ><img src="images/uIEnhancementImages/spacer.gif" alt="spacer" width="50" height="1"></td>
	<logic:equal name="operation" value="add">
        <td valign="bottom" ><img src="images/uIEnhancementImages/tab_add_selected.jpg" alt="Add" width="57" height="22" /></td>
        <td valign="bottom"><html:link
			page="/SimpleQueryInterface.do?pageOf=pageOfSite&aliasName=Site"><img src="images/uIEnhancementImages/tab_edit_notSelected.jpg" alt="Edit" width="59" height="22" border="0" /></html:link></td>
	</logic:equal>
	<logic:equal name="operation" value="edit">
		 <td valign="bottom" ><html:link
			page="/Site.do?operation=add&pageOf=pageOfSite"><img src="images/uIEnhancementImages/tab_add_notSelected.jpg" alt="Add" width="57" height="22" /></html:link></td>
        <td valign="bottom"><img src="images/uIEnhancementImages/tab_edit_selected.jpg" alt="Edit" width="59" height="22" border="0" /></td>
	</logic:equal>
        <td width="90%" valign="bottom" class="td_tab_bg">&nbsp;</td>
      </tr>
    </table>
	<!-- sneha: <table> tag and 1st <tr> from catissue-->
	<table width="100%" border="0" cellpadding="3" cellspacing="0" class="whitetable_bg">
        <tr>
          <td align="left" class="bottomtd"><%@ include file="/pages/content/common/ActionErrors.jsp" %></td>
        </tr>
	<!-- NEW SITE REGISTRATION BEGINS-->
		
		<tr>
			<td align="left" class="tr_bg_blue1"><span class="blue_ar_b">
					&nbsp;<logic:equal name="operation" value="<%=Constants.ADD%>">
						<bean:message key="site.title" />
					</logic:equal><logic:equal name="operation" value="<%=Constants.EDIT%>">
						<bean:message key="site.editTitle" />
					</logic:equal></span>
			</td>
		</tr>
			<tr>
			 <td align="left" class="showhide">
				<table width="100%" border="0" cellpadding="3" cellspacing="0">
				<tr>
					<td>
						<html:hidden property="operation" value="<%=operation%>" />
						<html:hidden property="submittedFor" value="<%=submittedFor%>"/>	
					</td>
				</tr>
				
				<tr>
					<td><html:hidden property="id" /></td>
					<td><html:hidden property="onSubmit"/></td>
				</tr>

				<tr>
					<td><html:hidden property="pageOf" value="<%=pageOf%>"/></td>
				</tr>
				<tr>
                  <td width="1%" align="right" class="black_ar_new"><span class="blue_ar_b"><img src="images/uIEnhancementImages/star.gif" alt="Mandatory" height="6" hspace="0" vspace="0" /></span></td>
                  <td width="16%" align="left" class="black_ar_new"><bean:message key="site.name" /> </td>
                  <td width="17%" align="left"><label>
				  <html:text
												styleClass="black_ar_new" maxlength="255" size="30"
												styleId="name" property="name" />
                    
                  </label></td>
                  <td width="16%" align="left">&nbsp;</td>
                  <td width="1%" align="right"><span class="blue_ar_b"><img src="images/uIEnhancementImages/star.gif" alt="Mandatory" width="6" height="6" hspace="0" vspace="0" /></span></td>
                  <td width="16%" align="left"><label for="type" class="black_ar_new"><bean:message key="site.type" /> </label></td>
                  <td width="21%" align="left" nowrap class="black_ar_new"><autocomplete:AutoCompleteTag
												property="type" optionsList='${siteTypeList}'
												initialValue='${siteForm.type}'
												styleClass="black_ar_new"
												size="27"/></td>
                  <td width="11%" align="left" valign="top">&nbsp;</td>
                </tr>
				<tr>
                  <td align="right" class="black_ar_new"><span class="blue_ar_b"><img src="images/uIEnhancementImages/star.gif" alt="Mandatory" width="6" height="6" hspace="0" vspace="0" /></span></td>
                  <td align="left" class="black_ar_new"><label
												for="coordinator"> <bean:message
												key="site.coordinator" /> </label></td>
                  <td align="left" nowrap class="black_new"><html:select
												property="coordinatorId" styleClass="formFieldSizedNew"
												styleId="coordinatorId" size="1"
												onchange="onCoordinatorChange()"
												onmouseover="showTip(this.id)" onmouseout="hideTip(this.id)">
												<html:options collection="userList" labelProperty="name"
													property="value" />
											</html:select></td>
                  <td align="left"><html:link href="#" styleId="newCoordinator"
												styleClass="view"
												onclick="addNewAction('SiteAddNew.do?addNewForwardTo=coordinator&forwardTo=site&addNewFor=coordinator')">
												<bean:message key="buttons.addNew" />
											</html:link></td>
                  <td align="center" class="black_ar_new">&nbsp;</td>
                  <td align="left" class="black_ar_new"><label
												for="emailAddress"> <bean:message
												key="site.emailAddress" /> </label></td>
                  <td align="left"><html:text styleClass="black_ar_new"
												maxlength="255" size="30" styleId="emailAddress"
												property="emailAddress" /></td>
                  <td align="left" valign="top">&nbsp;</td>
                </tr>
				<tr>
                  <td align="right" class="black_ar_new"><span class="blue_ar_b"><img src="images/uIEnhancementImages/star.gif" alt="Mandatory" width="6" height="6" hspace="0" vspace="0" /></span></td>
                  <td align="left" class="black_ar_new"><label for="street">
											<bean:message key="site.street" /> </label> </td>
                  <td align="left"><html:text styleClass="black_ar_new"
												maxlength="50" size="30" styleId="street" property="street" /></td>
                  <td align="left" class="black_ar_new">&nbsp;</td>
                  <td align="right" class="black_ar_new"><span class="blue_ar_b"><img src="images/uIEnhancementImages/star.gif" alt="Mandatory" width="6" height="6" hspace="0" vspace="0" /></span></td>
                  <td align="left" class="black_ar_new"><label for="city">
											<bean:message key="site.city" /> </label> </td>
                  <td align="left"><html:text styleClass="black_ar_new"
												maxlength="50" size="30" styleId="city" property="city" /></td>
                  <td align="left" valign="top">&nbsp;</td>
                </tr>
				<tr>
                  <td align="right" class="black_ar_new"><span class="blue_ar_b"><img src="images/uIEnhancementImages/star.gif" alt="Mandatory" width="6" height="6" hspace="0" vspace="0" /></span></td>
                  <td align="left" class="black_ar_new"><label for="state">
											<bean:message key="site.state" /> </label> </td>
                  <td align="left" nowrap class="black_ar_new"><autocomplete:AutoCompleteTag
												property="state" optionsList='${stateList}'
												initialValue='${siteForm.state}'
												styleClass="black_ar_new" 
												size="27" /></td>
                  <td align="left" class="black_ar_new">&nbsp;</td>
                  <td align="right" class="black_ar_new"><span class="blue_ar_b"><img src="images/uIEnhancementImages/star.gif" alt="Mandatory" width="6" height="6" hspace="0" vspace="0" /></span></td>
                  <td align="left" class="black_ar_new"><label for="zipCode">
											<bean:message key="site.zipCode" /> </label></td>
                  <td align="left"><html:text styleClass="black_ar_new"
												maxlength="30" size="30" styleId="zipCode"
												property="zipCode"  style="text-align:right"/></td>
                  <td align="left" valign="top">&nbsp;</td>
                </tr>
					<tr>
                  <td align="right" class="black_ar_new"><span class="blue_ar_b"><img src="images/uIEnhancementImages/star.gif" alt="Mandatory" width="6" height="6" hspace="0" vspace="0" /></span></td>
                  <td align="left" class="black_ar_new"><label for="country">
											<bean:message key="site.country" /> </label></td>
                  <td align="left" nowrap class="black_ar_new"><autocomplete:AutoCompleteTag
												property="country" optionsList='${countryList}'
												initialValue='${siteForm.country}'
												styleClass="black_ar_new" 
												size="27"/></td>
                  <td align="left" class="black_ar_new">&nbsp;</td>
                  <td align="center" class="black_ar_new">&nbsp;</td>
                  <td align="left" class="black_ar_new"><label
												for="phoneNumber"> <bean:message
												key="site.phoneNumber" /> </label></td>
                  <td align="left"><html:text styleClass="black_ar_new"
												maxlength="50" size="30" styleId="phoneNumber"
												property="phoneNumber" style="text-align:right"/></td>
                  <td align="left" valign="top">&nbsp;</td>
                </tr>
					  <tr>
                  <td align="center" class="black_ar_new">&nbsp;</td>
                  <td align="left" class="black_ar_new"><label for="faxNumber">
											<bean:message key="site.faxNumber" /> </label></td>
                  <td align="left"><html:text styleClass="black_ar_new"
												maxlength="50" size="30" styleId="faxNumber"
												property="faxNumber" style="text-align:right"/></td>
				<td align="left" class="black_ar_new">&nbsp;</td>
											<logic:equal
												name='${requestScope.operationForActivityStatus}'
												value='${requestScope.operationEdit}'>
												<td align="left" class="black_ar_new"><span
													class="blue_ar_b"><img
													src="images/uIEnhancementImages/star.gif" alt="Mandatory"
													width="6" height="6" hspace="0" vspace="0" /></span></td>
												<td align="left" class="black_ar_new"><label
													for="activityStatus"> <bean:message
													key="site.activityStatus" /> </label></td>
												<td align="left" class="black_ar_new"><autocomplete:AutoCompleteTag
													property="activityStatus"
													optionsList='${activityStatusList}'
													initialValue='${siteForm.activityStatus}'
													styleClass="black_ar_new" size="27" onChange='${strCheckStatus}' />
												</td>
												<td align="left" valign="top">&nbsp;</td>
											</logic:equal>
											<logic:notEqual
												name='${requestScope.operationForActivityStatus}'
												value='${requestScope.operationEdit}'>

												<td align="left" class="black_ar_new">&nbsp;</td>
												<td align="left" class="black_ar_new"></td>
												<td align="left">&nbsp;</td>
												<td align="left">&nbsp;</td>
											</logic:notEqual>
                </tr>
				<!-- action buttons begins -->
						<tr>
							<td class="buttonbg" colspan="10">
							<html:submit styleClass="blue_ar_b">
								<bean:message key="buttons.submit" />
							</html:submit>           
							</td>
						 </tr>
						<!-- action buttons end -->
						</td>
					</tr>
			</table>
		</td></tr>
		<!-- NEW SITE REGISTRATION ends-->
</table>
</html:form>