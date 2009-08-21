<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ page
	import="edu.wustl.common.util.global.ApplicationProperties,edu.wustl.query.util.global.Variables,edu.wustl.common.util.XMLPropertyHandler;"%>

<table width="100%" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td valign="top"><img
			src="images/advQuery/uIEnhancementImages/top_bg1.jpg" alt="Top corner"
			width="53" height="20" /></td>
		<td width="95%" valign="top"
			background="images/advQuery/uIEnhancementImages/top_bg.jpg"
			style="background-repeat:repeat-x;">
		<table width="100%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td width="50%" valign="middle"><span class="wh_ar_b"><bean:message
					key="app.welcomeNote"
					arg0="<%=ApplicationProperties.getValue("app.name")%>"
					arg1="<%=XMLPropertyHandler.getValue("application.titleversion")%>"
					arg2="<%=Variables.applicationCvsTag%>" /> </span></td>
			</tr>
		</table>
		</td>
	</tr>
</table>



