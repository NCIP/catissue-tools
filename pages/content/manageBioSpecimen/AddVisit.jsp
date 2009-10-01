<%

List studyFormMap = (List)request.getAttribute("studyFormMap");
int size = 0;
if(studyFormMap != null)
{
	size = studyFormMap.size();
}

%>

<table class="table_grey" width="100%" align="left" border='0' cellspacing='0' cellpadding='2'>
	
	<tr>
		<td colspan='5' class="formTitle" height="25">
			<bean:message key="eventEntry.addVisit" />
		</td>
	</tr>

	<tr>
		<td width="80" align="left" style='font-family:Tahoma;font-size:11px;font-weight:bold;'>
			Visit
		</td>
		 <td align="left">
				<select name="visitNumber" style='width:150px;font-family:Tahoma;font-size:11px;' onchange="verifyVisitSelection()">
			<% if(infiniteEntry != null && infiniteEntry)
				{
			%>
					<option value="0" >-- Add New Visit --</option>
					<option value="-1">-------------------------</option>
			<% 
				}
			%>

			<%	for(int i =1;i<=Integer.parseInt(numberOfVisits);i++)
				{
			%>
					<option value="<%= i %>" >Visit <%= i %></option>
			<%
				}
			%>
				</select>  
		 </td>

		<td width="120" align="right" style='font-family:Tahoma;font-size:11px;font-weight:bold;'>&nbsp;
			<label for="startDate" >
					<bean:message key="eventEntry.encountedDate" />
			</label>
		</td>
		<td align="right" >
				<ncombo:DateTimeComponent name="encounterDate"
					id="encounterDate" formName="eventEntryForm"	
					styleClass="black_new" />		
			
			<!--<bean:message key="page.dateFormat" />-->
		 </td>
		<td align="left" style='font-family:Tahoma;font-size:11px;'>[MM-DD-YYYY]</td>
	</tr>
	<tr>
		 <td width="80" align="left" style='font-family:Tahoma;font-size:11px;font-weight:bold;'> 
		 	<bean:message key="eventEntry.addVisit.showForm" />
		
		 </td>
		<td  align="left">
				<select name="studyForm"  style='width:150px;font-family:Tahoma;font-size:11px;' >
			<%	
				if(size == 0)
				{
			%>
					<option value="-1" >-- No Forms Available --</option>
			<%	}

				else
				{
				for(int i =0;i<studyFormMap.size();i++)
				{
					NameValueBean vb = (NameValueBean) studyFormMap.get(i);
			%>
					<option value="<%=vb.getName() %>" ><%=vb.getValue() %></option>
			<% 
				}
				}
			%>
				</select>  
		 </td>
				
			
	</tr>
	<tr>
		
		<td align="center" colspan="5"><input type="button" name="addEntry" id="addBtn"
			value="GO" class="actionButton" onclick="onClickSubmit()"/>
		</td>
	</tr>
</table>