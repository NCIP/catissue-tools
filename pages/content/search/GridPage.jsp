<!-- dataList and columnList are to be set in the main JSP file -->
<link rel="STYLESHEET" type="text/css" href="dhtml_comp/css/dhtmlXGrid.css"/>
<script  src="dhtml_comp/js/dhtmlXCommon.js"></script>
<script  src="dhtml_comp/js/dhtmlXGrid.js"></script>		
<script  src="dhtml_comp/js/dhtmlXGridCell.js"></script>	
<script  src="dhtml_comp/js/dhtmlXGrid_mcol.js"></script>	
<script>
	var myData = [<%int i;%><%for (i=0;i<(dataList.size()-1);i++){%>
	<%
		List row = (List)dataList.get(i);
	  	int j;
	%>
	<%="\""%><%for (j=0;j < (row.size()-1);j++){%><%=Utility.toNewGridFormat(row.get(j))%>,<%}%><%=Utility.toNewGridFormat(row.get(j))%><%="\""%>,<%}%>
	<%
		List row = (List)dataList.get(i);
	  	int j;
	%>
	<%="\""%><%for (j=0;j < (row.size()-1);j++){%><%=Utility.toNewGridFormat(row.get(j))%>,<%}%><%=Utility.toNewGridFormat(row.get(j))%><%="\""%>
	];
		
	var columns = <%="\""%><%int col;%><%for(col=0;col<(columnList.size()-1);col++){%><%=columnList.get(col)%>,<%}%><%=columnList.get(col)%><%="\""%>;
	
	var colWidth = <%="\""%><%for(col=0;col<(columnList.size()-1);col++){%><%=100%>,<%}%><%=100%><%="\""%>;
	
	var colTypes = <%="\""%><%=Variables.prepareColTypes(dataList)%><%="\""%>;
</script>

<%
	/**
	 * Name: Chetan Patil
	 * Reviewer: Sachin Lale
	 * Bug ID: SpecimenEventSpaceUtilization
	 * Patch ID: SpecimenEventSpaceUtilization_1
	 * See also: SpecimenEventSpaceUtilization_2
	 * Description: The height of each row and column of grid is 20 pixel. Hence to make the height of grid dynamic, the (number of rows + 2) is multiplied 
	 				by (number of pixels for height).
	 */
	int noOfRows = dataList.size();
	int heightOfGrid = (noOfRows + 2) * 20;

	// If the height of grid is more than the height required to display the grid with 12 rows then 
	// make the heightOfGrid constant so that only 11 rows including the header row will be displayed.
	if(heightOfGrid > 240)
	{
		heightOfGrid = 230; // 230px is the exact height to display 11 rows, including the header.
	}
%>

<table width="100%" cellpadding="3" cellspacing="0" border="0"	>
	<tr>
		<td>
			<script>	
				//for derive only
				if(useFunction == "derivedSpecimenGrid")	// useFunction == "eventParametersGrid" ||
				{
					document.write("<div id='gridbox' width='100%' height='150px' style='background-color:#d7d7d7;overflow:hidden'></div>");
				
				}
				// Patch ID: SpecimenEventSpaceUtilization_2
				else if(useFunction == "eventParametersGrid") 
				{
					document.write("<div id='gridbox' width='100%' height='125px' border='0' style='background-color:#d7d7d7;overflow:hidden'></div>");
				
				}
				else
				{
					document.write("<div id='gridbox' width='100%' height='340px' style='background-color:#d7d7d7;overflow:hidden'></div>");
				
				}
			</script>
		</td>
	</tr>
</table>

<script>
	/*	
	function rowClick(id)
	{
		var colid = <%=identifierFieldIndex.intValue()%>;
		var cl = mygrid.cells(id,colid);
		var searchId = cl.getValue();
		var url = "SearchObject.do?pageOf=<%=pageOf%>&operation=search&id="+searchId;
		window.location.href = url;
	}
	*/

	// function modified to display distribution array.
	// problem in query data.
	function rowClick(id)
	{
		var colid = <%=identifierFieldIndex.intValue()%>;
		/*
		var x=document.getElementsByName("pageOf");
		//alert(x);
		if(x[0].value == "pageOfArrayDistribution")
		{
			colid=0;
		}
		//alert(colid);
		*/
		var cl = mygrid.cells(id,colid);
		var searchId = cl.getValue();
		var isHashedOut = "false";
		for(var col=0;col < <%=columnList.size()%> && isHashedOut=="false";col++)
	    {
		   var c2 = mygrid.cells(id,col);
		   var colData = c2.getValue();
		   if(colData=="##")
		   { 
			   isHashedOut="true";
	       }  
	    }
		if(isHashedOut=="false")
		{
			var url = "SearchObject.do?pageOf=<%=pageOf%>&operation=search&id="+searchId;
			if(<%=pageOf.equals("pageOfCollectionProtocol")%>)
			{
				url = "RetrieveCollectionProtocol.do?&id="+searchId;
			}
			window.location.href = url;
		}
		else
		{
			var url = "AccessDenied.do";
			window.location.href = url;
		}
		
	}
	/**
	 * Name : Vijay_Pande
	 * Bug ID: Sachin_Lale
	 * Patch ID: 4060_1 
	 * See also: --
	 * Description: Grid was appearindg disordered since it was not refreshed/resized.
	 */
function init_grid()
{			
	var funcName = "rowClick";
	if(useDefaultRowClickHandler == 1)
	{
		funcName = "rowClick";	
	}
	else
	{
		funcName=useFunction;
	}

	mygrid = new dhtmlXGridObject('gridbox');
	mygrid.setImagePath("dhtml_comp/imgs/");
	mygrid.enableAlterCss("even","uneven");
	mygrid.setSkin("light");
	mygrid.enableRowsHover(true,'grid_hover');
	mygrid.setHeader(columns);
	mygrid.setEditable("FALSE");
	mygrid.enableAutoHeigth(false);

	//document.write("<hr>"+colWidth+"<hr>");
	if(useFunction == "eventParametersGrid" || useFunction == "derivedSpecimenGrid")
	{
		colWidth = "130,130,130,130,0";
	}

	if(useFunction == "goToConflictDetails")
	{
		colWidth = "130,130,200,130,130,130,200";
	}

	//document.write("<hr>"+colWidth+"<hr>");

	mygrid.setInitWidths(colWidth);

	//mygrid.setColAlign("left,left")
	mygrid.setColSorting(colTypes);
	//mygrid.enableMultiselect(true)
	mygrid.init();

	/*
	mygrid.loadXML("dhtmlxgrid/grid.xml");
	// clears the dummy data and refreshes the grid.
	// fix for grid display on IE for first time.
	mygrid.clearAll();
	*/

	for(var row=0;row<myData.length;row++)
	{
		mygrid.addRow(row+1,myData[row],row+1);
	}

	if(useFunction == "eventParametersGrid" || useFunction == "derivedSpecimenGrid" )
	{
		mygrid.setHeaderCol(4,"");
		mygrid.setColumnHidden(4,true);
	}

	//mygrid.setOnRowSelectHandler(funcName);
	mygrid.setOnRowDblClickedHandler(funcName);
	// :To hide ID columns by kalpana
	function getIDColumns()
		{
			var hiddenColumnNumbers = new Array();
			var i=0;
			<%
				int cols = 0;
				
				for(col=0;col<columnList.size();col++)
				{
					if (columnList.get(col).toString().trim().equals("ID") || columnList.get(col).toString().trim().equals("Status")
					 || columnList.get(col).toString().trim().equals("Site Name")|| columnList.get(col).toString().trim().equals("Report Collection Date"))
					{%>
						hiddenColumnNumbers[i] = <%=col%>;
						i++;
					<%}
				}
			%>
			return hiddenColumnNumbers;
		}
	
	
	// :To hide ID columns
		var hideCols = getIDColumns();
		for(i=0;i<hideCols.length;i++)
		{
			mygrid.setHeaderCol(hideCols[i],"");
			mygrid.setColumnHidden(hideCols[i],true);
		}


	mygrid.setSizes();
}

window.onload=init_grid;
</script>