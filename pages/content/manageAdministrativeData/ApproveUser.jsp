<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/PagenationTag.tld" prefix="custom"%>
<%@ page import="edu.wustl.clinportal.util.global.Constants, edu.wustl.clinportal.domain.Address, edu.wustl.clinportal.domain.User"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<SCRIPT LANGUAGE="JavaScript" SRC="jss/javaScript.js"></SCRIPT>
<%
	  int pageNum = Integer.parseInt((String)request.getAttribute(Constants.PAGE_NUMBER));
	  int totalResults = Integer.parseInt((String)session.getAttribute(Constants.TOTAL_RESULTS));
	  int numResultsPerPage = Integer.parseInt((String)session.getAttribute(Constants.RESULTS_PER_PAGE));
%>

</br>
<!-- target of anchor to skip menus -->
<table width="100%" border="0" cellpadding="0" cellspacing="0" class="maintable"><!-- sneha: table tag as per catissue-->
<html:form action="/ApproveUser">
	<html:hidden property="id" />
	<html:hidden property="operation" />
  <tr><!--complete row as per catissue except key attribute value-->
    <td><table border="0" cellpadding="0" cellspacing="0">
      <tr>
        <td class="td_table_head"><span class="wh_ar_b"><bean:message key="user.name" /></span></td>
        <td><img src="images/uIEnhancementImages/table_title_corner2.gif" alt="Page Title" width="31" height="24" /></td>
      </tr>
    </table></td>
  </tr>
  <tr><!-- sneha:complete row as per catissue-->
    <td class="tablepadding"><table width="100%" border="0" cellpadding="0" cellspacing="0">
      <tr>
        <td class="td_tab_bg" ><img src="images/uIEnhancementImages/spacer.gif" alt="spacer" width="50" height="1"></td>
        <td valign="bottom" ><html:link page="/User.do?operation=add&pageOf=pageOfUserAdmin&menuSelected=1"><img src="images/uIEnhancementImages/tab_add_notSelected.jpg" alt="Add" width="57" height="22" border="0" /></html:link><a href="#"></a></td>
        <td valign="bottom"><html:link page="/SimpleQueryInterface.do?pageOf=pageOfUserAdmin&aliasName=User&menuSelected=1"><img src="images/uIEnhancementImages/tab_edit_notSelected.jpg" alt="Edit" width="59" height="22" border="0" /></html:link><a href="#"></a></td>
        <td valign="bottom"><img src="images/uIEnhancementImages/tab_approve_user1.jpg" alt="Approve New Users" width="139" height="22" border="0" /></td>
        <td width="90%" valign="bottom" class="td_tab_bg" >&nbsp;</td>
      </tr>
    </table>
      <table width="100%" border="0" cellpadding="3" cellspacing="0" class="whitetable_bg">
        <tr>
          <td align="left" class="toptd">
				<%@ include file="/pages/content/common/ActionErrors.jsp" %>
		  </td>
        </tr>
        <tr>
          <td align="left" class="tr_bg_blue1"><span class="blue_ar_b"> &nbsp;<bean:message key="approveUser.title" />  </span><span class="black_ar_s">(<%=totalResults%> records		found)</span></td>
        </tr>
		<!-- paging begins -->
		<tr>
			<td align="right" colspan = "2" >
			<%
				int re = totalResults;
				if(re > 10)
				{
			%>
			<custom:test name=" "pageNum="<%=pageNum%>" totalResults="<%=totalResults%>"	numResultsPerPage="<%=numResultsPerPage%>" pageName="ApproveUserShow.do" showPageSizeCombo="<%=true%>" recordPerPageList="<%=Constants.RESULT_PERPAGE_OPTIONS%>"/>
			<%
				}
			%>
			</td>
		</tr>
		<!-- paging ends -->
        <tr>
          <td align="center" class="showhide"><!-- sneha: changed class attribute value-->
		  <table width="99%" border="0" cellspacing="0" cellpadding="4">
					<% int a =	totalResults ;
						System.out.print(a);
						if(a > 0)
						{
					%>
              <tr>
                <td width="2%" class="tableheading"><strong>#</strong></td>
                <td width="25%" class="tableheading"><strong><bean:message key="user.loginName" /> </strong></td>
                <td width="20%" class="tableheading"><strong><bean:message key="user.userName" /></strong></td>
                <td width="28%" class="tableheading"><strong><bean:message key="user.emailAddress" /></strong></td>
                <td width="25%" class="tableheading"><strong><bean:message key="approveUser.registrationDate" /> </strong> <span class="grey_ar_s">[<bean:message key="page.dateFormat" />] </span></td>
              </tr>
				<%
						}
				%>
			  <logic:empty name="showDomainObjectList">
			  <tr>
				<td>&nbsp;</td>
				<td colspan="5" align="center" class="grey_ar">
					<bean:message key="approveUser.newUsersNotFound" />
				</td>
			  </tr>
			</logic:empty>		
						<%int i=1;%>
						<logic:iterate id="currentUser" name="showDomainObjectList">
						
							<tr class="dataRowLight">
								<%
        								User user = (User) currentUser;
										String identifier = user.getId().toString();
										String userDetailsLink = Constants.USER_DETAILS_SHOW_ACTION+"?"+Constants.SYSTEM_IDENTIFIER+"="+identifier;
        						%>
								<td class="black_new">
									<%=i%>
								</td>
								<td class="black_new">
									<a href="<%=userDetailsLink%>" >
										<bean:write	name="currentUser" property="loginName" />
									</a>
								</td>
								<td class="black_new">
									<bean:write name="currentUser" property="lastName" />,
									<bean:write name="currentUser" property="firstName" />
								</td>
								<td class="black_new">
									<bean:write name="currentUser" property="emailAddress"/>
								</td>
								<td class="black_new">
									<bean:write name="currentUser" property="startDate" />
								</td>
							</tr>
							<%i++;%>
						</logic:iterate>
					 </table>
					</td>
				</tr>
			</html:form>
		</table>
		</td>
	</tr>
</table>