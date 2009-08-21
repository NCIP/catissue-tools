<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/PagenationTag.tld" prefix="custom" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ page import="java.util.List,edu.wustl.clinportal.util.global.Constants,edu.wustl.clinportal.util.global.Utility"%>
<%@ page import="edu.wustl.clinportal.util.global.Variables"%>
<%@ page language="java" isELIgnored="false"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
	List columnList = (List) request.getAttribute(edu.wustl.common.util.global.Constants.SPREADSHEET_COLUMN_LIST);
	List dataList = (List) request.getAttribute(Constants.PAGINATION_DATA_LIST);
	String pageOf = (String)request.getAttribute(Constants.PAGEOF);
	Integer identifierFieldIndex = (Integer)request.getAttribute(Constants.IDENTIFIER_FIELD_INDEX);
	String title = pageOf + ".searchResultTitle";
	int pageNum = Integer.parseInt((String)request.getAttribute(Constants.PAGE_NUMBER));
	int totalResults = ((Integer)session.getAttribute(Constants.TOTAL_RESULTS)).intValue();
	int numResultsPerPage = Integer.parseInt((String)session.getAttribute(Constants.RESULTS_PER_PAGE));
	String pageName = "SpreadsheetView.do";	
%>
<script>
function callAction(action)
{
	document.forms[0].action = action;
	document.forms[0].submit();
}

</script>


<table width="100%" border="0" cellpadding="0" cellspacing="0" class="maintable">
<tr>
		<td class="td_color_bfdcf3">
			<table border="0" cellpadding="0" cellspacing="0">
		      <tr>
				<td class="td_table_head">
					<span class="wh_ar_b">
						<bean:message key="<%=title%>" />
					</span>
				</td>
		        <td>
					<img src="images/uIEnhancementImages/table_title_corner2.gif" alt="Page Title - Search Results" width="31" height="24" hspace="0" vspace="0" />
				</td>
		      </tr>
		    </table>
		</td>
	  </tr>
	 <tr>
		<td class="tablepadding">
			<table width="100%" border="0" cellpadding="0" cellspacing="0">
			<logic:notEqual name="pageOf" value="<%=Constants.PAGEOF_SIMPLE_QUERY_INTERFACE%>">
			<tr>
				 <td width="4%" class="td_tab_bg" >
				<img src="images/uIEnhancementImages/spacer.gif" alt="spacer" width="50" height="1">
				</td>

				 <td valign="bottom"><html:link href="#" onclick="callAction('CommonTab.do')">
					<img src="images/uIEnhancementImages/tab_add_notSelected.jpg" alt="Add" width="57" height="22" /></html:link>
				</td>

				<td valign="bottom">
				<img src="images/uIEnhancementImages/tab_edit_selected.jpg" alt="Edit" width="59" height="22" border="0" />
				</td>

			<logic:equal name="pageOf" value="<%=Constants.PAGEOF_USER_ADMIN%>">
				<td valign="bottom"><html:link page="/ApproveUserShow.do?pageNum=1"><img src="images/uIEnhancementImages/tab_approve_user.jpg" alt="Approve New Users" width="139" height="22" border="0" /></html:link></td>
			</logic:equal>

			<td width="90%" valign="bottom" class="td_tab_bg">&nbsp;</td>
		</tr>
	</logic:notEqual>

	<logic:equal name="pageOf" value="<%=Constants.PAGEOF_SIMPLE_QUERY_INTERFACE%>">
      <tr>
        <td width="90%" valign="bottom" class="td_tab_bg">&nbsp;</td>
      </tr>
	  </logic:equal>
</table>
<table width="100%" border="0" cellpadding="0" cellspacing="0" class="whitetable_bg">
	<tr>
        <td align="left" class="toptd"></td>
      </tr>

	  <tr>
        <td align="left" class="tr_bg_blue1"><span class="blue_ar_b"> &nbsp;<bean:message key="<%=title%>" /></span></td>

      </tr>
	  <html:form action="/SpreadsheetExport">	
	<tr>
		<td class="black_ar_new">
				
						
				<custom:test name="Search Results" pageNum="<%=pageNum%>" totalResults="<%=totalResults%>" numResultsPerPage="<%=numResultsPerPage%>" pageName="<%=pageName%>" showPageSizeCombo="<%=true%>" recordPerPageList="<%=Constants.RESULT_PERPAGE_OPTIONS%>"/>
				<html:hidden property="<%=Constants.PAGEOF%>" value="<%=pageOf%>"/>
				<html:hidden property="isPaging" value="true"/>
				<html:hidden property="identifierFieldIndex" value="<%=identifierFieldIndex.toString()%>"/>
				
			
		</td>
	</tr>
	</html:form>

<script>
	/* 
		to be used when you want to specify another javascript function for row selection.
		useDefaultRowClickHandler =1 | any value other than 1 indicates you want to use another row click handler.
		useFunction = "";  Function to be used. 	
	*/
	var useDefaultRowClickHandler =1;
	var gridFor="<%=Constants.GRID_FOR_EDIT_SEARCH%>";
	var useFunction = "";	
</script>
<tr>
<td width="100%">
<%@ include file="/pages/content/search/GridPage.jsp" %>
</td>
</tr>
</table>
</table>
</td>
</tr>
</table>

