<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ page import="edu.wustl.clinportal.util.global.Constants"%>
<%@ page import="edu.wustl.common.util.global.Variables"%>
<%@ page import="edu.wustl.common.util.global.ApplicationProperties"%>
<%@ page import="edu.wustl.common.util.XMLPropertyHandler"%>
<%@ page import="javax.servlet.http.HttpSession"%>
<%@ page import="java.text.MessageFormat"%>

<tiles:importAttribute />

<html>
<head>
<title><tiles:getAsString name="title" ignore="true" /></title>
<link rel="stylesheet" type="text/css" href="css/styleSheet.css" />
<link rel="STYLESHEET" type="text/css" href="css/dhtmlxtabbar.css" />
<link rel="stylesheet" type="text/css" href="css/styleSheet1.css" />
<link rel="stylesheet" type="text/css" href="css/CascadeMenu.css" />
<link rel="stylesheet" type="text/css" href="css/catissue_suite.css" />
<link rel="stylesheet" type="text/css" href="css/styleSheet.css" />

<script src="jss/script.js" type="text/javascript"></script>
<script src="jss/overlib_mini.js" type="text/javascript"></script>
<script src="jss/calender.js" type="text/javascript"></script>
<script src="jss/splitter.js" type="text/javascript" ></script>
<script src="jss/ajax.js" type="text/javascript"></script>

<%
	/*
     * Name : Kapil Kaveeshwar
     * Reviewer: Sachin Lale
     * Bug ID: Menu_Splitter
     * Patch ID: Menu_Splitter_1
     * See also: All Menu_Splitter
     * Description: Last state of menu splitter is recorded in the session of the user, sot that next time user
     * navigates to the page the state can be preserved. This part of the code the reads the menu status from 
     * session. 
     */
	Object splitterOpenStatusObj = request.getSession().getAttribute(Constants.SPLITTER_STATUS_REQ_PARAM);
	String splitterOpenStatus = "true";
	if(splitterOpenStatusObj!=null)
	{
		splitterOpenStatus = (String)splitterOpenStatusObj;		
	}
%>
		
<!--Jitendra -->
<script language="JavaScript">
	var timeOut;
		var advanceTime;
		var lastRefreshTime;//timestamp in millisecond of last accessed through child page
		var pageLoadTime;
		<%
			int timeOut = -1;
			int advanceTime = Integer.parseInt(XMLPropertyHandler.getValue(Constants.SESSION_EXPIRY_WARNING_ADVANCE_TIME));
			String tempMsg = ApplicationProperties.getValue("app.session.advanceWarning");
			Object[] args = new Object[] {"" + advanceTime};
			String advanceTimeoutMesg = MessageFormat.format(tempMsg,args);
			
			timeOut = -1;
				
			if(request.getSession().getAttribute(Constants.SESSION_DATA) != null) //if user is logged in
			{
				timeOut = request.getSession().getMaxInactiveInterval();
			}
		%>


		timeOut = "<%= timeOut%>";	
		advanceTime = "<%= advanceTime%>";
		pageLoadTime = new Date().getTime(); //timestamp in millisecond of last pageload
		lastRefreshTime = pageLoadTime ; // last refreshtime in millisecond
		setAdvanceSessionTimeout(timeOut);
		
		function warnBeforeSessionExpiry()
		{	
			//check for the last refresh time,whether page is refreshed in child frame after first load.		
			if(lastRefreshTime > pageLoadTime)
			{
				
				var newTimeout = (lastRefreshTime - pageLoadTime)*0.001;
				newTimeout = newTimeout + (advanceTime*60.0);
				
				pageLoadTime = lastRefreshTime ;
				setAdvanceSessionTimeout(newTimeout);
				
			}
			else
			{
				var defTimeout = setTimeout('sendToHomePage()', advanceTime*60*1000);
				var choice = confirm("<%= advanceTimeoutMesg %>");
				if(choice) //ok pressed, extend session
				{
					clearTimeout(defTimeout);
					sendBlankRequest();
					setAdvanceSessionTimeout(timeOut);
				}else
				{
					sendToHomePage();
				}

			}
		}
		
		function setAdvanceSessionTimeout(ptimeOut) 
		{
			
			if(ptimeOut > 0)
			{
				var time = (ptimeOut - (advanceTime*60)) * 1000;
				setTimeout('warnBeforeSessionExpiry()', time); //if session timeout, then redirect to Home page
			}
		}
		
		
		function sendToHomePage()
		{			
				<% 
				   Object obj = request.getSession().getAttribute(Constants.SESSION_DATA);			  			
				   if(obj != null) 
				   {
				%>			
				   var timeoutMessage = "<%= ApplicationProperties.getValue("app.session.timeout") %>";
				   alert(timeoutMessage);			  
			   
				   window.location.href = "Logout.do";
				<%
				   }
				%>		  
		}	

		function detectApplicationUsageActivity()
		{
			if (lastRefreshTime <= pageLoadTime)
			{
				lastRefreshTime = new Date().getTime();
				clearTimeout(advanceTime*60*1000);
				sendBlankRequest();
			}
		}
	
</script>
<!--Jitendra -->

<!-- Mandar 11-Aug-06 : For calendar changes -->
<script src="jss/calendarComponent.js"></script>
<SCRIPT>var imgsrc="images/";</SCRIPT>
<LINK href="css/calanderComponent.css" type=text/css rel=stylesheet>
<!-- Mandar 11-Aug-06 : calendar changes end -->

<!-- For Favicon 
<link rel="shortcut icon" href="images/favicon.ico" type="image/vnd.microsoft.icon"/>
<link rel="icon" href="images/favicon.ico" type="image/vnd.microsoft.icon"/>
-->

</head>
<body>
 <table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
	<tr height="10%">
		<td>
			<table width="100%" border="0" cellpadding="0" cellspacing="0"
			bgcolor="#FFFFFF">
			<tr>
			<td width="20%" rowspan="2" style="border-top:4px solid #558dc0;"><tiles:insert
					attribute="applicationheader">
				</tiles:insert></td>

				<td valign="top"><tiles:insert attribute="header"></tiles:insert></td>	
		</tr>
		<tr>
		<td width="80%" align="right" valign="top"><tiles:insert
					attribute="mainmenu">
				</tiles:insert></td>
			</tr>
		</table>
	</td>
</tr>
	<tr height="90%">
		<td>
			<table width="100%" border="0" cellspacing="0" cellpadding="0"  height="100%">
				<tr>
				   <td colspan="2" width="100%" valign="top"><!-- target of anchor to skip menus -->
				     	<a name="content" /> <tiles:insert attribute="content"></tiles:insert></td>
				</tr>
			</table>
		</td>
	</tr>
</table>
</body>
</html>