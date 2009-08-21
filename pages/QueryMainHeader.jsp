<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ page import="edu.wustl.query.util.global.AQConstants"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td width="5%" valign="top"><img
			src="images/advQuery/uIEnhancementImages/top_bg1.jpg" width="53" height="20" /></td>
		<td width="95%" valign="top"
			background="images/advQuery/uIEnhancementImages/top_bg.jpg"
			style="background-repeat:repeat-x;">
		<table width="100%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<logic:notEmpty scope="session" name="<%=AQConstants.SESSION_DATA%>">
					<td width="70%" align="right" valign="top" ><a
						href="QueryWizard.do?access=access" class="white"> 
						<img
						src="images/advQuery/uIEnhancementImages/ic_report.gif" name="Image1"
						width="15" height="12" id="Image1" hspace="2" vspace="0"/>
						<bean:message key="query.name" />
						 </a>&nbsp;</td>
				</logic:notEmpty>
				<logic:notEmpty scope="session" name="<%=AQConstants.SESSION_DATA%>">
					<td width="16%" align="right" valign="top" ><a
						href="RetrieveQueryAction.do?access=access" class="white"> 
						<img
						src="images/advQuery/uIEnhancementImages/ic_report.gif" name="Image1"
						width="15" height="12" id="Image1" hspace="2" vspace="0"/>
						<bean:message key="advQuery.app.savedQuery" />
						 </a>&nbsp;</td>
				</logic:notEmpty>
				<logic:notEmpty scope="session" name="<%=AQConstants.SESSION_DATA%>">
					<td width="14%" align="right" valign="top"><a
						href="QueryLogout.do"> <img
						src="images/advQuery/uIEnhancementImages/logout_button1.gif" name="Image1"
						width="86" height="19" id="Image1"
						onmouseover="MM_swapImage('Image1','','images/advQuery/uIEnhancementImages/logout_button.gif',1)"
						onmouseout="MM_swapImgRestore()" /> </a></td>
				</logic:notEmpty>
			</tr>
		</table>
		</td>
	</tr>
</table>