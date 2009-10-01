<%@ page import="edu.wustl.clinportal.util.global.Constants"%>

<%
      String access = (String)session.getAttribute("Access");
	  boolean mac = false;
      Object os = request.getHeader("user-agent");
      if(os!=null && os.toString().toLowerCase().indexOf("mac")!=-1)
      {
          mac = true;
      }
	
	String frame1Ysize = "100%";
	String frame2Ysize = "100%";
	String frame3Ysize = "95%";
	String cpAndParticipantViewFrameHeight="44%";
	if(access != null && access.equals("Denied"))
	{
		cpAndParticipantViewFrameHeight="15%";
	}
	
	if(mac)
	{
		frame1Ysize = "200";
		frame2Ysize = "200";
		frame3Ysize = "400";
		

		if(access != null && access.equals("Denied"))
		{
			frame1Ysize = "80";
			frame2Ysize = "320";
		}
	}
%>
<script src="<%=request.getContextPath()%>/jss/breadcrumb.js"></script>
<script src="<%=request.getContextPath()%>/jss/ajax.js" type="text/javascript"></script>
<script language="JavaScript" type="text/javascript" src="jss/javaScript.js"></script>
<table border="0" height="100%" width="100%" cellpadding="0" cellspacing="0">
	<tr height="100%">
		<td width="27%" valign="top">
			<table border="0" width="100%" height="100%">
				<tr height="<%=cpAndParticipantViewFrameHeight%>">
					<td valign="top" width="25%">
					<iframe id="<%=Constants.CP_AND_PARTICIPANT_VIEW%>" name="<%=Constants.CP_AND_PARTICIPANT_VIEW%>" src="<%=Constants.SHOW_CS_AND_PARTICIPANTS_ACTION%>?pageOf=<%=Constants.PAGE_OF_CP_QUERY_RESULTS%>" scrolling="no" frameborder="0" width="100%" height="<%=frame3Ysize%>" marginheight=0 marginwidth=0>
						Your Browser doesn't support IFrames.
					</iframe>
					</td>
				</tr>	
			</table>	
		</td>
		<!--P.G. - Start 24May07:Bug 4291:Added source as initial action for blank screen-->
		<td width="73%" height="100%" valign="top">
			<div id="breadCrumbDiv" style="padding-left: 10px;" width="100%"> 
			</div>
			<iframe name="<%=Constants.DATA_DETAILS_VIEW%>" src="<%=Constants.BLANK_SCREEN_ACTION%>" scrolling="auto" frameborder="0" width="100%" height="<%=frame3Ysize%>">
				Your Browser doesn't support IFrames.
			</iframe>
		</td>
		<!--P.G. - End -->
	</tr>
</table>			

