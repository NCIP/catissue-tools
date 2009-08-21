<!-- 
Name        : Virender Mehta
Reviewer    : Sachin lale
Description : This is the common jsp which will show tooltip for collection protocol title on CPR,SCG and CP base view page.
-->
<script language="JavaScript" type="text/javascript" src="jss/javaScript.js"></script>
<%
	String tempIDTitleArray = ""; 
	Map cpIDTitleMap = (Map)request.getAttribute(Constants.CP_ID_TITLE_MAP);
	if(cpIDTitleMap != null)
	{
		Iterator itr = cpIDTitleMap.keySet().iterator();
		while(itr.hasNext())
		{
			String keyValue = itr.next().toString();
			String mapValue = (String)cpIDTitleMap.get(new Long(keyValue));
			tempIDTitleArray += "[" + "\"" + keyValue + "\""  + "," + "\""  + mapValue + "\"" + "]," ; 
		}
	}
	if(tempIDTitleArray != null && tempIDTitleArray.length() != 0)
		tempIDTitleArray = tempIDTitleArray.substring(0, tempIDTitleArray.length() -1);
%>

<script>
		
//Change by virender for showing Long Title in tooltip   [ Start ]
		var cpidTitleArray = [ <%=tempIDTitleArray%> ];
		var cpObj;
		var interval= self.setInterval("showToolTip(cpObj)",timeInterval);

		function showToolTip(obj)
		{
			if(obj != null)
			{
				cpObj = obj;
				var tooltip = "";
				if(obj.selectedIndex == 0)
				{
					tooltip = "Select";
					showGivenTip(obj.id, tooltip);
				}
				else
				{
					//alert(tooltip);
					cpid = obj.options[obj.selectedIndex].value;
					for (var i=0;i<cpidTitleArray.length;i++)
					{
						if(cpidTitleArray[i][0] == cpid)
						{
							tooltip = cpidTitleArray[i][1]; 
							showGivenTip(obj.id, tooltip);
							break;
						}
					}
				}
			}
     	}	
		//Change by virender for showing Long Title in tooltip   [ Stop ]		

</script>