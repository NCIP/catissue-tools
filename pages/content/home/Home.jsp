<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ page import="edu.wustl.clinportal.util.global.Constants,
edu.wustl.common.util.global.ApplicationProperties,
edu.wustl.clinportal.util.global.Variables,
edu.wustl.common.util.XMLPropertyHandler,
edu.wustl.common.beans.SessionDataBean"%>
<link href="css/catissue_suite.css" type=text/css rel=stylesheet>
<style type="text/css">
table#browserDetailsContainer
{
	font-family:arial,helvetica,verdana,sans-serif;
	font-size:0.7em;
	padding-left:10px;
}
</style>
<table width="100%" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td colspan="5" class="td_orange_line" height="1"></td>
	</tr>
	<tr>
		<td width="0%" rowspan="3" valign="top">
			</td>
		<td width="49%" rowspan="3" align="center" valign="top"></td>

		<td width="0%" rowspan="3" valign="top"></td>
		<td width="28%" align="center" valign="top"><br />

		<table width="95%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td class="td_boxborder">
				<table width="100%" border="0" cellpadding="0" cellspacing="0">
					<tr class="wh_ar_b">
						<td width="20" colspan="3" align="left">
						<table border="0" cellpadding="0" cellspacing="0">
							<tr>

								<td nowrap="nowrap" class="td_table_head"><span
									class="wh_ar_b"><bean:message key="app.loginMessage" /></span></td>
								<td><img
									src="images/uIEnhancementImages/table_title_corner.gif"
									alt="Title" width="31" height="24" /></td>
							</tr>
						</table>
						</td>
					</tr>
					<tr>
						<td colspan="3" align="left" class="showhide1">
						<%@ include file="/pages/content/common/ActionErrors.jsp" %>
						<logic:empty scope="session" name="<%=Constants.SESSION_DATA%>">
							<html:form styleId="form1" styleClass="whitetable_bg"
								action="/Login.do">
								<table width="98%" border="0" cellpadding="4" cellspacing="0">
									<tr>
										<td class="black_ar_new"><bean:message key="app.loginId" />
										</td>
										<td><html:text styleClass="blue_ar_c" property="loginName"
											size="20"/></td>
									</tr>
									<tr>
										<td class="black_ar_new"><bean:message key="app.password" />
										</td>
										<td><html:password styleClass="blue_ar_c"
											property="password" size="20" /></td>
									</tr>
									<tr>
										<td>&nbsp;</td>
										<td align="left" valign="middle">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td><input name="Submit" type="submit"
													class="blue_ar_b" value="Login" /> <a href="#"
													class="blue"><span class="wh_ar_b"></span></a></td>
												<td><img src="images/uIEnhancementImages/or_dot.gif"
												 alt="Divider line"
													width="1" height="15" hspace="5" /></td>
												<td><a
													href="SignUp.do?operation=add&amp;pageOf=pageOfSignUp"
													class="view"><bean:message key="app.signup" /></a></td>
											</tr>
										</table>
										</td>
									</tr>
									<tr>
										<td>&nbsp;</td><td>&nbsp;</td>
									</tr>
								</table>
							</html:form>
						</logic:empty> <logic:notEmpty scope="session"
							name="<%=Constants.SESSION_DATA%>">
							<tr>
								<TD class="welcomeContent">
								<%
													Object obj = request.getSession().getAttribute(
													Constants.SESSION_DATA);
											if (obj != null) {
												SessionDataBean sessionData = (SessionDataBean) obj;
										%> Dear <%=sessionData.getLastName()%>, &nbsp;<%=sessionData.getFirstName()%><br>
								<%
										}
										%> <bean:message key="app.welcomeNote"
									arg0="<%=ApplicationProperties.getValue("app.name")%>"
									arg1="<%=XMLPropertyHandler.getValue("application.titleversion")%>"
									arg2="<%=Variables.applicationCvsTag%>" /></TD>
							</tr>
						</logic:notEmpty></td>
					</tr>
				</table>
				</td>
				<td width="5"
					background="images/uIEnhancementImages/shadow_right.gif"></td>
			</tr>
			<tr>
				<td background="images/uIEnhancementImages/shadow_down.gif"></td>
				<td><img src="images/uIEnhancementImages/shadow_corner.gif"
				alt="Shadow Corner"
					width="5" height="5" /></td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
			<td colspan="5" align="center" valign="top" bgcolor="#bcbcbc"><img
			src="images/uIEnhancementImages/spacer.gif" alt="Spacer" width="1" height="1" /></td>
		</tr>
</table>
