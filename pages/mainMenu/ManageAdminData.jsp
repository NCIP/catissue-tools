<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>

	<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
		<table summary="" cellpadding="0" cellspacing="0" border="0" height="20">
            <tr>
              <td width="1"><!-- anchor to skip main menu --><a href="#content"><img src="images/shim.gif" alt="Skip Menu" width="1" height="1" border="0" /></a></td>
              <!-- link 1 begins -->
              <td height="20" class="mainMenuItem" onmouseover="changeMenuStyle(this,'mainMenuItemOver'),showCursor()" onmouseout="changeMenuStyle(this,'mainMenuItem'),hideCursor()" onclick="document.location.href='Home.do'">
                <html:link styleClass="mainMenuLink" page="/Home.do">
					<bean:message key="app.home" />
				</html:link>
              </td>
              <!-- link 1 ends -->
			   <td><img src="images/mainMenuSeparator.gif" width="1" height="16" alt="" /></td>
			  <!-- link 2 begins -->
              <td height="20" class="mainMenuItemSelected" onclick="document.location.href='ManageAdministrativeData.do'">
                <html:link styleClass="mainMenuLink" page="/ManageAdministrativeData.do">
                	<bean:message key="app.administrativeData" />
                </html:link>
              </td>
              <!-- link 2 ends -->
			 <td><img src="images/mainMenuSeparator.gif" width="1" height="16" alt="" /></td>
			  
              <!-- link 3 begins -->
              <td height="20" class="mainMenuItem" onmouseover="changeMenuStyle(this,'mainMenuItemOver'),showCursor()" onmouseout="changeMenuStyle(this,'mainMenuItem'),hideCursor()" onclick="document.location.href='ManageBioSpecimen.do'">
                <html:link styleClass="mainMenuLink" page="/ManageBioSpecimen.do">
                	<bean:message key="app.dataentry" />
                </html:link>
              </td>
             <!-- link 3 ends -->
              <td><img src="images/mainMenuSeparator.gif" width="1" height="16" alt="" /></td>
     <!-- Deepali changes -->         
     <!--         <td height="20" class="mainMenuItem" onmouseover="changeMenuStyle(this,'mainMenuItemOver'),showCursor()" onmouseout="changeMenuStyle(this,'mainMenuItem'),hideCursor()" onclick="document.location.href='ManageReports.do'">
                <html:link styleClass="mainMenuLink" page="/ManageReports.do">
                	<bean:message key="app.reports" />
                </html:link>
              </td>
              <td><img src="images/mainMenuSeparator.gif" width="1" height="16" alt="" /></td> --> 
       <!-- Deepali changes end-->         
		      <!-- link 4 begins -->
              <td height="20" class="mainMenuItem" onmouseover="changeMenuStyle(this,'mainMenuItemOver'),showCursor()" onmouseout="changeMenuStyle(this,'mainMenuItem'),hideCursor()" onclick="document.location.href='SimpleQueryInterface.do?pageOf=pageOfSimpleQueryInterface&menuSelected=17'">
                <html:link styleClass="mainMenuLink" page="/RetrieveQueryAction.do">
                	<bean:message key="app.search" />
                </html:link>
              </td>
             
              
            		  
			  <!-- link 5 ends -->
              <td>
              	<img src="images/mainMenuSeparator.gif" width="1" height="16" alt="" />
              </td>
			</tr>
          </table>