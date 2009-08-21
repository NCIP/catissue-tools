<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/nlevelcombo.tld" prefix="ncombo" %>
<%@ taglib uri="/WEB-INF/dynamicExtensions.tld" prefix="dynamicExtensions" %>

<%@ page import="edu.wustl.clinportal.util.global.Constants"%>
<%@ page import="edu.wustl.clinportal.actionForm.ClinicalStudyForm"%>
<%@ page import="edu.wustl.clinportal.util.global.Utility"%>
<%@ page import="java.util.List,java.util.Map"%>
<%@ page import="edu.wustl.common.beans.NameValueBean"%>
<%@ page import="edu.wustl.clinportal.action.annotations.AnnotationConstants"%>
<%@ page import="edu.wustl.clinportal.util.CatissueCoreCacheManager"%>
<%@ page import="edu.wustl.clinportal.util.global.Variables"%>
<%@ page import="edu.wustl.clinportal.util.global.Utility"%>

<link rel="stylesheet" type="text/css" href="/clinportal/css/styleSheet.css" />

<link rel="stylesheet" type="text/css" href="/clinportal/css/clinicalstudyext-all.css" />

<script>var imgsrc="/clinportal/de/images/";</script>

<script language="JavaScript" type="text/javascript" src="/clinportal/de/jss/prototype.js"></script>

<script language="JavaScript" type="text/javascript" src="/clinportal/de/jss/scr.js"></script>
<script language="JavaScript" type="text/javascript" src="/clinportal/de/jss/combobox.js"></script>
<script language="JavaScript" type="text/javascript" src="/clinportal/jss/ext-base.js"></script>

<script language="JavaScript" type="text/javascript" src="/clinportal/jss/ext-all.js"></script>
<script language="JavaScript" type="text/javascript" src="/clinportal/de/jss/combos.js"></script>

<script language="JavaScript" type="text/javascript" src="/clinportal/de/jss/ajax.js"></script>
<script language="JavaScript" type="text/javascript" src="/clinportal/jss/ajax.js"></script>
<%
ClinicalStudyForm form = (ClinicalStudyForm) request.getAttribute(Constants.CLINICAL_STUDY_FORM);
Long  piId = form.getPrincipalInvestigatorId();
String selectText = "--Select--";
String opr = (String) request.getAttribute(Constants.OPERATION);
System.out.println("form pi id is ----------"+piId);
if(piId!=0)
{
	selectText = Utility.getNameValue((List)request.getAttribute(Constants.USERLIST),piId+"");

}

%>

<script>Ext.onReady(function(){var myUrl= 'AjaxSearchClinicalStudy.do?';var ds = new Ext.data.Store({proxy: new Ext.data.HttpProxy({url: myUrl}),reader: new Ext.data.JsonReader({root: 'row',totalProperty: 'totalCount',id: 'id'}, [{name: 'id', mapping: 'id'},{name: 'excerpt', mapping: 'field'}])});var combo = new Ext.form.ComboBox({store: ds,hiddenName: 'CB_coord',displayField:'excerpt',valueField: 'id',typeAhead: 'false',pageSize:15,forceSelection: 'true',queryParam : 'query',mode: 'remote',triggerAction: 'all',minChars : 3,queryDelay:500,lazyInit:true,emptyText:'--Select--',valueNotFoundText:'',selectOnFocus:'true',applyTo: 'coord'});combo.on("expand", function() {if(Ext.isIE || Ext.isIE7){combo.list.setStyle("width", "250");combo.innerList.setStyle("width", "250");}else{combo.list.setStyle("width", "auto");combo.innerList.setStyle("width", "auto");}}, {single: true});ds.on('load',function(){if (this.getAt(0) != null && this.getAt(0).get('excerpt').toLowerCase().startsWith(combo.getRawValue().toLowerCase())) {combo.typeAheadDelay=50;} else {combo.typeAheadDelay=60000}});});</script>

<script>
Ext.onReady(function(){
	var myUrl= 'AjaxSearchClinicalStudy.do?';
	var ds = new Ext.data.Store({proxy: new Ext.data.HttpProxy({url: myUrl}),
	reader: new Ext.data.JsonReader({root: 'row',totalProperty: 'totalCount',id: 'id'}, [{name: 'id', mapping: 'id'},{name: 'excerpt', mapping: 'field'}])});
	var combo = new Ext.form.ComboBox({store: ds,hiddenName: 'principalInvestigatorId',displayField:'excerpt',valueField: 'id',typeAhead: 'true',pageSize:15,forceSelection: 'true',queryParam : 'query',mode: 'remote',lazyInit:'true',triggerAction: 'all',minChars : 3,emptyText:'<%=selectText%>',selectOnFocus:'true',applyTo: 'txtprincipalInvestigatorId'});
	var firsttimePI="true";
	combo.on("expand", function() {
		if(Ext.isIE || Ext.isIE7)
		{
			combo.list.setStyle("width", "250");
			combo.innerList.setStyle("width", "250");
		}
	else{
		combo.list.setStyle("width", "auto");
		combo.innerList.setStyle("width", "auto");
		}		
		}, 		{single: true}); 
		ds.on('load',function(){
			if (this.getAt(0) != null && this.getAt(0).get('excerpt').toLowerCase().startsWith(combo.getRawValue().toLowerCase()))
			{combo.typeAheadDelay=50;
			} 
			else
			{combo.typeAheadDelay=60000}
			});
		
			
<%	 if (opr.equals(Constants.EDIT))
    {

%>                   ds.load({params:{start:0, limit:9999,query:''}});
                        ds.on('load',function(){
							if(firsttimePI == "true")
										{ combo.setValue('<%=piId%>',false); firsttimePI="false";}
                        });
<%
}
%>						
			});

	</script>
<head>

<%

	String pageOf = (String)request.getAttribute(Constants.PAGEOF);
	String operation = (String) request.getAttribute(Constants.OPERATION);
	String submittedFor=(String)request.getAttribute(Constants.SUBMITTED_FOR);
	String consentTab = (String)request.getParameter(Constants.IS_TO_DELETE);
	int noOfConsents=1;
	boolean consentAddDisabled = true;
	List studyFormsList = (List)request.getAttribute(Constants.STUDY_FORMS_LIST);

	boolean readOnlyValue=false;
    
    String formName, pageView=operation;
	    
	String currentClinicalStudyFormDate="";
	String endDateClinicalStudy="";
	//ClinicalStudyForm form = (ClinicalStudyForm) request.getAttribute(Constants.CLINICAL_STUDY_FORM);
	if(form != null)
	{	
		currentClinicalStudyFormDate = form.getStartDate();
		endDateClinicalStudy = form.getEndDate();
		if(currentClinicalStudyFormDate == null)
		    currentClinicalStudyFormDate = "";				
		if(endDateClinicalStudy == null)
		    endDateClinicalStudy = "";				
		String csStatus = form.getActivityStatus();
		if(csStatus.equals("Active"))
		{
				consentAddDisabled=false;
		}
		noOfConsents=form.getConsentTierCounter();
		if(consentTab!=null && consentTab.equals(Constants.CONSENT_DELETE))
		{
			consentTab="consentPage()";
		}
		else
		{
			consentTab="";
		}

	}

	Long dynEntContainerId = (Long)session.getAttribute(AnnotationConstants.FORM_ID);
	String addNewEventCounter = (String)request.getSession().getAttribute(Constants.EVENT_COUNTER);

	String strDynEntContainerId = "";
	if (dynEntContainerId != null && addNewEventCounter != null)
	{
		strDynEntContainerId = dynEntContainerId.toString();
		request.getSession().removeAttribute(AnnotationConstants.FORM_ID);
		request.getSession().removeAttribute(Constants.EVENT_COUNTER);
	}
	//System.out.println("Form Id: "+dynEntContainerId);
    if (operation.equals(Constants.EDIT))
    {    	
    	formName = Constants.CLINICAL_STUDY_EDIT_ACTION;
    }
    else
    {
        formName = Constants.CLINICAL_STUDY_ADD_ACTION;
       
    }

//	System.out.println("Action: "+);
%>


<script src="jss/script.js" type="text/javascript"></script>
<script language="JavaScript" type="text/javascript" src="jss/javaScript.js"></script>

<script>



var insno=1;
var tblColor = "#FFFFFF";
function localExt()
{
	var ID= document.getElementById("id").value;
	var op=null;
	document.forms[0].forwardTo.value    = "localExtensions";
	var appender= "?localExt="+ID+"&operation="+op;
	document.forms[0].action=document.forms[0].action+appender;
	
//	document.forms[0].action="DefineAnnotations.do?localExt="+ID+"&operation="+op;

	document.forms[0].submit();
}

</script>
<!-- %@ include file="/pages/content/common/CommonScripts.jsp" %-->

<SCRIPT LANGUAGE="JavaScript">
	var search1='`';
</script>
<script src="jss/script.js" type="text/javascript"></script>
<script language="JavaScript" type="text/javascript" src="jss/javaScript.js"></script>

<SCRIPT LANGUAGE="JavaScript">
<!--
	// functions for add more

	

// variable to count the outer blocks
var insno=1;
var tblColor = "#123456";
function addBlock(tmpdiv,tmpd0)
{
var d0 = document.getElementById(tmpd0);
var outCount = document.forms[0].outerCounter.value;

var val = parseInt(outCount);
		val = val + 1;
		document.forms[0].outerCounter.value = val;
		//alert('outercount after incr.: '+val);		
		if(val%2 == 0)
			tblColor = "<%=Constants.EVEN_COLOR%>";
		else
			tblColor = "<%=Constants.ODD_COLOR%>";
	var z = d0.innerHTML;
	insno =val;
	var mm = z.indexOf('`');
	for(var cnt=0;cnt<mm;cnt++)
	{
		z = z.replace('`',insno);
		mm = z.indexOf('`');
	}
// --------21 mar start
	var r = new Array(); 
	r = document.getElementById(tmpdiv).rows;
	var q = r.length;
	var newRow=document.getElementById(tmpdiv).insertRow(q);
	var newCol=newRow.insertCell(0);
	newCol.innerHTML = z;

	var tb = document.getElementById("itable_"+val);
	tb.bgColor = tblColor;
	var eventCombo = document.getElementsByName('formValue(ClinicalStudyEvent:'+val+'_relToCPEId)');
	var chkInstance = document.getElementsByName("isRelatedtoCP");

	if(eventCombo!=null)
	{
		eventCombo[0].disabled = !chkInstance[0].checked;
		
	}
// --------21 mar end

}
//this function will allow only alphanumeric ,underscore and hash sign for Id field
function alphaNumOnly(field) 
{
	if(field.value.length>0) 
	{
		field.value = field.value.replace(/[^a-zA-Z 0-9-_  # ]+/g,''); 
	}
		
}
function addDiv(div,adstr)
{
	var x = div.innerHTML;
	div.innerHTML = div.innerHTML +adstr;
}

//  function to insert a row in the inner block
function insRow(subdivtag,iCounter,blockCounter)
{
	
	//alert(subdivtag);
	var cnt = document.getElementById(iCounter);
	var val = parseInt(cnt.value);
	val = val + 1;
	cnt.value = val;
	
	var sname = "";
	
	var r = new Array(); 
	r = document.getElementById(subdivtag).rows;
	var q = r.length;
	var x=document.getElementById(subdivtag).insertRow(q);
	
	//setSubDivCount(subdivtag);
	var subdivname = ""+ subdivtag;

	// srno
	var spreqno=x.insertCell(0)
	spreqno.className="tabrightmostcellAddMore";
	var rowno=(q);
	//var srIdentifier = subdivname + rowno + "_id)";
	var cell1 = "";//"<input type='hidden' name='" + srIdentifier + "' value='' id='" + srIdentifier + "'>";
	spreqno.innerHTML="" + rowno+"." + cell1;
	
		
	//subtype
	var spreqsubtype=x.insertCell(1)
	spreqsubtype.className="formFieldSizedNew";
	sname="";
	objname = "formValue(ClinicalStudyEvent:"+blockCounter+"_StudyFormContext:"+ rowno +"_containerId)";
	
	sname= "<select name='" + objname + "' size='1' class='formFieldSizedNew' id='" + objname + " >";
	sname = sname + "<option value='1'> -- Select-- </option>";
	
	<%for(int i=0;i<studyFormsList.size();i++)
		{
			String formLabel = "" + ((NameValueBean)studyFormsList.get(i)).getName();
			String formValue = "" + ((NameValueBean)studyFormsList.get(i)).getValue();
			String selected = "";
			if (dynEntContainerId != null && formValue.equals(strDynEntContainerId))
				selected = "selected";
		%>
		sname = sname + "<option value='<%=formValue%>' <%=selected%>><%=formLabel%></option>";
		<%}%>
		sname = sname + "</select> &nbsp;";
		//sname = sname + '<a href="#" styleId="newUser" //onclick="addNewAction("ClinicalStudyAddNew.do?addNewForwardTo=defineEntity&forwardTo=clinicalStudy&addNewFor=defineEntity")>';
		//sname = sname + "Add New" + "</a>";

	spreqsubtype.innerHTML="" + sname;
	
		
	//qty
	var spreqqty=x.insertCell(2)
	spreqqty.className="formFieldAddMore";//sneha:changed class
	sname="";
	objname = "formValue(ClinicalStudyEvent:"+blockCounter+"_StudyFormContext:"+ rowno +"_studyFormLabel)";
//sneha:changed class
	sname="<input type='text' name='" + objname + "' class='black_new' size='30' id='" + objname + "' onkeypress='alphaNumOnly(this);' onchange='alphaNumOnly(this);' onkeyup='alphaNumOnly(this);' >" ;       	

	sname = sname + "&nbsp;";
					
	spreqqty.innerHTML="" + sname;

	//chkbox
	var checkbformultirec=x.insertCell(3)
	checkbformultirec.className="formFieldAddMore";
	sname="";
	objname = "formValue(ClinicalStudyEvent:"+blockCounter+"_StudyFormContext:"+ rowno +"_canHaveMultipleRecords)";
	sname="<input type='checkbox' name='" + objname +"' id='" + objname +"' value='true'   onclick=\""+  "handleMultipleRecCheckbox(this);\" />"; 
	checkbformultirec.innerHTML=""+sname;
	
	//Fourth Cell
	var checkb=x.insertCell(4);
	checkb.className="formFieldAddMore";
	checkb.colSpan=3;
	sname="";
	var name = "chk_spec_"+ blockCounter +"_"+rowno;
	//var func = "enableButton(document.forms[0].deleteSpecimenReq,'ivl("+ blockCounter + ")','" +"chk_spec_" + blockCounter + "_ "+ "')";
	sname="<input type='checkbox' name='" + name +"' id='" + name +"' value='C'>"; // onClick=" + func + ">";
	sname = sname + "<input type='hidden' name='formValue(ClinicalStudyEvent:"+blockCounter+"_StudyFormContext:"+ rowno +"_id)' />";
	checkb.innerHTML=""+sname;

}


function checkForDuplicates(action)
{
		//Count noof checkbox of consent and set the consenttier counter
		var chkBox = document.getElementsByName('consentcheckBoxs');
		var lengthChk=chkBox.length;
		document.forms[0].consentTierCounter.value = lengthChk;

		confirmDisable(action,document.forms[0].activityStatus);

}

function checkForDuplicateFormLabels(action)
{
	//alert('action : '+action);
	var outCount = document.forms[0].outerCounter.value;
	
	var array = new Array();
	var k = 0;
		
	for (var i=1; i<=outCount; i++)
	{
		var subdivtag = 'formValue(ClinicalStudyEvent:'+i;
		
		var r = document.getElementById(subdivtag).rows;
		var innerCount = r.length -1;
		for (var j=1; j<=innerCount; j++)
		{
			var label = document.getElementById('formValue(ClinicalStudyEvent:'+i+'_StudyFormContext:'+ j +'_studyFormLabel)');
			if(label.value == "")
			{
				alert('Enter FormLabel value for event '+i);
				return;
			}
			array[k] = label.value;
			if (array[k] != "")
			k++;
		}

	}
	
	for (i=0; i< k; i++)
	{
		for (var j=i; j< k; j++)
		{
			if (i != j && array[i] == array[j])
			{
				alert('FormLabel Cannot contain duplicate values.');
				return false;
			}
			
		}
	}
	// Submit the action here
	return true;//confirmDisable(action,document.forms[0].activityStatus);

}
function checkForDuplicateEventLabels(action)
{
	//alert('action : '+action);
	var outCount = document.forms[0].outerCounter.value;
	
	var array = new Array();
	var k = 0;
		
	for (var i=1; i<=outCount; i++)
	{
		var eventLabel = document.getElementById('formValue(ClinicalStudyEvent:'+i+'_collectionPointLabel)');
		array[k] = eventLabel.value;
		if (array[k] != "")
			k++;
	}
	
	for (i=0; i< k; i++)
	{
		for (var j=i; j< k; j++)
		{
			if (i != j && array[i] == array[j])
			{
				alert('EventLabel Cannot contain duplicate values.');
				return false;
			}
			
		}
	}
	
	return true;//confirmDisable(action,document.forms[0].activityStatus);

}
//On calling this function the tab will be switched to ClinicalStudy Page
	function clinicalstudyPage()
	{
		switchToTab("clinicalstudyTab");
	}

	//On calling this function the tab will be switched to Consent Page	
	function consentPage()
	{
		
		switchToTab("consentTab");
	}
	//This function will the called while switching between Tabs
	function switchToTab(selectedTab)
	{
		var displayKey="block";
		
		if(!document.all)
			displayKey="table";
			
		var displayTable=displayKey;
		var tabSelected="none";
		
		if(selectedTab=="clinicalstudyTab")
		{
			tabSelected=displayKey;
			displayTable="none";
		}	
		
		var display=document.getElementById('table1');
		display.style.display=tabSelected;
		var display=document.getElementById('table2');
		display.style.display=tabSelected;
		var display=document.getElementById('table3');
		display.style.display=tabSelected;
				
		var display4=document.getElementById('consentTierTable');
		display4.style.display=displayTable;	

		var display5=document.getElementById('submittable');
		display5.style.display=tabSelected;


		var csTab=document.getElementById('clinicalstudyTab');
		var consentTab=document.getElementById('consentTab');
		
		if(selectedTab=="clinicalstudyTab")
		{
			updateTab(csTab,consentTab);
		}
		else		
		{
			updateTab(consentTab,csTab);
		}
	}
	//This function is for changing the behaviour of TABs
		function updateTab(tab1, tab2)
		{
			//sneha:removed classnames from tab.className as well as function param,to remove the black boxes around consent and clinical study buttons
			tab1.onmouseover=null;
			tab1.onmouseout=null;
			tab1.className="";
		
			tab2.className="";
			tab2.onmouseover=function() { changeMenuStyle(this,''),showCursor();};
			tab2.onmouseout=function() {changeMenuStyle(this,''),hideCursor();};
		}
	//This Function will add more consent Tier 
	function addConsentTier()
	{		
		var val = parseInt(document.getElementById('innertable').rows.length);
		
		//increment consent tier counter
		var counter = parseInt(document.forms[0].consentTierCounter.value);
		counter = counter + 1;
		document.forms[0].consentTierCounter.value = counter;

		var rowCount = counter;
		//create row for new consenttier
		var createRow = document.getElementById('innertable').insertRow(val);
		var createSerialNo=createRow.insertCell(0);
		var createCheckBox=createRow.insertCell(1);
		var createTextArea=createRow.insertCell(2);
		
		var iCount = rowCount;
		var consentName="consentValue(ConsentBean:"+iCount+"_statement)";
		var consentKey="consentValue(ConsentBean:"+iCount+"_consentTierID)";
		var consentCheck="consentcheckBoxs";
		createSerialNo.className="black_new";//sneha:changed class of serial no,check box and text area
		createSerialNo.setAttribute('align','right');
		createCheckBox.className="black_new";
		createCheckBox.setAttribute('align','center');
		createTextArea.className="black_new";

		var sname = "<input type='hidden' id='" + consentKey + "'>";				
		createSerialNo.innerHTML=val+".";
		createCheckBox.innerHTML="<input type='checkbox' name='" + consentCheck+"' id='"+consentCheck+"'>";
		createTextArea.innerHTML= sname+"<textarea onKeyPress='return maxLength(this,500);' onpaste='return maxLengthPaste(this,500);' rows='2'class='formFieldSized' style='width:90%;' name="+consentName+"></textarea>";
	}

	function maxLengthPaste(field,maxChars)
	{       
	       if((field.value.length +  window.clipboardData.getData("Text").length) > maxChars) {
	         return false;
	       }    
	}
	
	function maxLength(field,maxChars)
	{
	       if(field.value.length >= maxChars) {          
	          return false;
	       }
	}	//This function will delete the selected consent Tier
	function deleteSelected()
	{
		var rowIndex = 0;	
		var rowCount=document.getElementById('innertable').rows.length;
		var removeButton = document.getElementsByName('removeButton');
		
		/** creating checkbox name**/
		var chkBox = document.getElementsByName('consentcheckBoxs');
		var lengthChk=chkBox.length;
		var j = 0;
		for(var i=0;i<lengthChk;i++)
		{
			if(chkBox[j].checked==true)
			{
				var gettable = document.getElementById('innertable');
				var currentRow = chkBox[j].parentNode.parentNode;
				
				rowIndex = currentRow.rowIndex;
				var consentKey="consentValue(ConsentBean:"+rowIndex+"_consentTierID)";
				document.getElementsByName(consentKey).value = null;				
				gettable.deleteRow(rowIndex);
			}
			else
			{
				j++;
			}	
		}
		resetConsentno();
	}	
	function resetConsentno()
	{
				
		/** Set serial no of consent according to checkbox list length**/
		var chkBox = document.getElementsByName('consentcheckBoxs');
		var lengthChk=chkBox.length;
		for(var i=0;i<lengthChk;i++)
		{
			var currentRow = chkBox[i].parentNode.parentNode;
			var cell = currentRow.cells[0];
			cell.innerHTML = (i+1)+".";
			
		}
	}
	//On selecting Select All CheckBox all the associted check box wiil be selected
	function checkAll(chkInstance)
	{
		var chkCount= document.getElementsByName('consentcheckBoxs').length;
		for (var i=0;i<chkCount;i++)
		{
			var elements = document.getElementsByName('consentcheckBoxs');
			elements[i].checked = chkInstance.checked;
		}
	}
	//this function will be called when can have Multiple Record Checkbox value getting changed,to
	//set correct value in form parameter
	function handleMultipleRecCheckbox(chkBoxInstance)
	{
		var element = document.getElementsByName(chkBoxInstance.name);
		for(var k=0;k<element.length;k++)
		{
			element[k].value = chkBoxInstance.checked;
		}
	}
//function to show CP select box
	function showAllCP(chkInstance)
	{   
		if(document.getElementById('colprotocolIds')!=null)
		{
			document.getElementById('colprotocolIds').disabled=!chkInstance.checked;
			var cnt =  document.forms[0].outerCounter.value;
			for(var k=1;k<=cnt;k++)
			{
				var element = document.getElementsByName('formValue(ClinicalStudyEvent:'+k+'_relToCPEId)');
				element[0].disabled = !chkInstance.checked;
			}
		}

	}
	function onCPChange()
	{
	  
	  if(document.getElementById('colprotocolIds')!=null)
		{
			
			var val = document.getElementById("colprotocolIds").value;
			//alert("==select cp id=="+val);
			if(val != "-1")
			{
				selectPresentCoordinators();
				selectPresentPI();
				var submittedForValue = "CPEvent";
				var action = "ClinicalStudy.do?operation=<%=operation%>&pageOf=pageOfClinicalStudy&cpId="+val;
				document.forms[0].action = action;
				document.forms[0].submit();
			}
		}
	}
//-->
</SCRIPT>
</head>
<body onload="<%=consentTab%>">
	<html:form action="<%=formName%>" >
	<table width="100%" border="0" cellpadding="0" cellspacing="0"
	class="maintable" height="100%"><!--sneha: changed table def as per catissue -->
	<tr><!--sneha: added this row from catiisue -->
	<td class="td_color_bfdcf3">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="td_table_head"><span class="wh_ar_b"><bean:message
						key="app.clinicalstudy" /></span></td>
					<td><img
						src="images/uIEnhancementImages/table_title_corner2.gif"
						alt="Page Title - Participant" width="31" height="24" /></td>
					</tr>
				</table>
				</td>
			</tr>
			<tr height="98%">
			<td class="tablepadding"><!--sneha:added this table from catissue -->
				<table width="100%" border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td width="4%" class="td_tab_bg" ><img src="images/spacer.gif" alt="spacer" width="50" height="1"></td>
			 				<logic:equal name="operation" value="<%=Constants.ADD%>">
								 <td  valign="bottom" ><img src="images/uIEnhancementImages/tab_add_selected.jpg" alt="Add" width="57" height="22" /></td>
								<td  valign="bottom"><html:link page="/SimpleQueryInterface.do?pageOf=pageOfClinicalStudy&aliasName=ClinicalStudy"><img src="images/uIEnhancementImages/tab_edit_notSelected.jpg" alt="Edit" width="59" height="22" border="0" /></html:link></td>
							</logic:equal>
							<logic:equal name="operation" value="<%=Constants.EDIT%>">
								<td  valign="bottom" ><html:link page="/ClinicalStudy.do?operation=add&pageOf=pageOfClinicalStudy"><img src="images/uIEnhancementImages/tab_add_notSelected.jpg" alt="Add" width="57" height="22" border ="0" /></html:link></td>
								<td  valign="bottom" ><img src="images/uIEnhancementImages/tab_edit_selected.jpg" alt="Edit" width="59" height="22"/></td>
							</logic:equal>
							<td width="90%" valign="bottom" class="td_tab_bg">&nbsp;</td>
						 </tr>
					</table>
	
			
				<table width="100%" border="0" cellpadding="0" cellspacing="0"
			class="whitetable_bg" height="95%">

					<tr><!--sneha: added this row from catissue -->
				<td colspan="2" align="left" class="bottomtd"><%@ include file="/pages/content/common/ActionErrors.jsp" %></td>
			</tr>
			
				<tr><!--sneha: added image instead of bean:message,removed OnMouseOver and OnMouseOut functions,removed class attributes added blue line at the bootom-->
				<td  height="20" nowrap id="clinicalstudyTab" onclick="clinicalstudyPage()" align="left">
 				<a href="#"><img src="images/uIEnhancementImages/clinicalstudytab1.gif" alt="Consents" height="20" /></a>   
 				</td>
        		<td height="20" onclick="consentPage()" id="consentTab"><a href="#"><img src="images/uIEnhancementImages/cp_consents1.gif" alt="Consents" height="20"/></a>   
        		</td>
				 <td width="90%" class="cp_tabbg">&nbsp;</td>
		</tr>
	<tr>
   <td class="showhide" colspan="6">

	<!-- table 1 -->
	<table summary="" cellpadding="0" cellspacing="0" border="0" id="table1" width="100%">
			<tr>
			<td colspan="3">
				<table summary="" cellpadding="3" cellspacing="0" border="0" width="100%">
					<tr>
						<td>
							<html:hidden property="operation" value="<%=operation%>" />
							<html:hidden property="submittedFor" value="<%=submittedFor%>"/>
							<html:hidden property="forwardTo" value=""/>
						</td>
						<td><html:hidden property="onSubmit"/></td>
					</tr>
					
					<tr>
						<td><html:hidden property="id" styleId="id" />
					</tr>
	<!-- page title -->	
						<tr><!--sneha: changed class atteribute value-->
							<td colspan="5" align="left" class="tr_bg_blue1"><span
					class="blue_ar_b">&nbsp;
								<%String title = "clinicalstudy."+pageView+".title";%>
								<bean:message key="<%=title%>"/></span>							
							</td>
						</tr>			
	<!-- principal investigator -->	
						<tr><!--sneha: changed class and added image-->
							<td width="1%" align="center" class="black_ar_new"><span class="blue_ar_b"><img src="images/uIEnhancementImages/star.gif" alt="Mandatory" width="6" height="6" hspace="0" vspace="0" /></span></td>
								<td width="16%" align="left" class="black_ar_new">
								<label for="txtprincipalInvestigatorId">
									<bean:message key="clinicalstudy.principalinvestigator" />
								</label></td>
								
							
							<td class="black_new"><!--sneha: changed class -->
							<!--	<html:select property="principalInvestigatorId" styleClass="formFieldSizedNew" styleId="principalInvestigatorId" size="1">
									<html:options collection="<%=Constants.USERLIST%>" labelProperty="name" property="value"/>
								</html:select>
								&nbsp;-->

								<input type='text' id='txtprincipalInvestigatorId' name='txtprincipalInvestigatorId'  value ='' size='20'/>

				
					<html:link href="#" styleId="newUser" styleClass="black_ar_new" onclick="addNewAction('ClinicalStudyAddNew.do?addNewForwardTo=principalInvestigator&forwardTo=clinicalStudy&addNewFor=principalInvestigator')">
						<bean:message key="buttons.addNew" />
					</html:link>
				
				

								
								<!--<html:link href="#" styleId="newUser" onclick="selectPresentPI();addNewAction('ClinicalStudyAddNew.do?addNewForwardTo=principalInvestigator&forwardTo=clinicalStudy&addNewFor=principalInvestigator')">
									<bean:message key="buttons.addNew" />
								</html:link>	 					-->
							</td>
							</tr>
							<tr>
	<!-- protocol coordinators -->	<!--sneha: changed class only -->
							<td width="1%" align="center" class="black_ar_new">&nbsp;</td>
							<td class="black_ar_new">
								<label for="protocolCoordinatorIds">
									<bean:message key="clinicalstudy.protocolcoordinator" />
								</label>
							</td>

							<td width="35%" class="black_ar_new">
								<dynamicExtensions:multiSelectUsingCombo addNewUserActionName="addNewAction('ClinicalStudyAddNew.do?addNewForwardTo=protocolCoordinator&forwardTo=clinicalStudy&addNewFor=protocolCoordinator')"/>
							</td>
						</tr>
	
	<!-- title -->						
						<tr><!--sneha: changed classes and added image -->
							<td width="1%" align="center" class="black_ar_new"><span class="blue_ar_b"><img 		src="images/uIEnhancementImages/star.gif" alt="Mandatory" width="6" height="6" 		hspace="0" vspace="0" /></span></td>
							<td width="16%" align="left" class="black_ar_new">
								<label for="title">
									<bean:message key="clinicalstudy.protocoltitle" />
								</label></td>

							<td class="black_new" colspan=2>								
								<!--<html:text styleClass="black_new" maxlength="255" size="30" styleId="title" property="title"  onkeypress="alphaNumOnly(this);" onchange="alphaNumOnly(this);" onkeyup="alphaNumOnly(this);" />-->

								<html:textarea property="title" rows="2" styleClass="black_new"  styleId="title" cols="69" />
								  
							</td>

							</tr>
	<!-- short title -->	<tr>					
						<!--sneha: changed classes and added image -->
							<td width="1%" align="center" class="black_ar_new"><span class="blue_ar_b"><img 		src="images/uIEnhancementImages/star.gif" alt="Mandatory" width="6" height="6" 		hspace="0" vspace="0" /></span></td>
							<td width="16%" align="left" class="black_new">
								<label for="shortTitle">
									<bean:message key="clinicalstudy.shorttitle" />
								</label>
							</td>
							<td class="black_new" colspan=2><!--sneha: changed class -->								
								<html:text styleClass="black_new" maxlength="255" size="30" styleId="shortTitle" property="shortTitle"  onkeypress="alphaNumOnly(this);" onchange="alphaNumOnly(this);" onkeyup="alphaNumOnly(this);" />
							</td>
						</tr>
						
	<!-- irb id -->						
						<tr><!--sneha: changed classes and added image -->
							<td width="1%" align="center" class="black_ar_new"><span class="blue_ar_b"><img 		src="images/uIEnhancementImages/star.gif" alt="Mandatory" width="6" height="6" 		hspace="0" vspace="0" /></span></td>
							<td width="16%" align="left" class="black_ar_new">
								<label for="irbID">
									<bean:message key="clinicalstudy.irbid" />
								</label>
							</td>
							<td class="black_new" colspan=2><!--sneha: changed class -->								
								<html:text styleClass="black_new" maxlength="255" size="30" styleId="irbID" property="irbID" onkeypress="alphaNumOnly(this);" onchange="alphaNumOnly(this);" onkeyup="alphaNumOnly(this);" />
							</td>
						</tr>
	
	<!-- startdate -->						
						<tr><!--sneha: changed classes and added image -->
							<td width="1%" align="center" class="black_ar_new"><span class="blue_ar_b"><img 		src="images/uIEnhancementImages/star.gif" alt="Mandatory" width="6" height="6" 		hspace="0" vspace="0" /></span></td>
							<td width="16%" align="left" class="black_ar_new">
								<label for="startDate">
									<bean:message key="clinicalstudy.startdate" />
								</label>
							</td>
				
							<td class="black_new" colspan=2>
					<%
					if(currentClinicalStudyFormDate.trim().length() > 0)
					{
						Integer clinicalStudyYear = new Integer(Utility.getYear(currentClinicalStudyFormDate ));
						Integer clinicalStudyMonth = new Integer(Utility.getMonth(currentClinicalStudyFormDate ));
						Integer clinicalStudyDay = new Integer(Utility.getDay(currentClinicalStudyFormDate ));
											
					%>
					<ncombo:DateTimeComponent name="startDate"
								  id="startDate"
								  formName="clinicalStudyForm"
								  month= "<%= clinicalStudyMonth %>"
								  year= "<%= clinicalStudyYear %>"
								  day= "<%= clinicalStudyDay %>"
								  value="<%=currentClinicalStudyFormDate %>"
									pattern="dd-MM-yyyy"
								  styleClass="black_new"
					
					/>
					<% 
						}
						else
						{  
					 %>
					<ncombo:DateTimeComponent name="startDate"
								  id="startDate"
								  formName="clinicalStudyForm"
								  pattern="dd-MM-yyyy"
								  styleClass="black_new"
										/>
					<% 
						} 
					%> 
					<bean:message key="page.dateFormat" />&nbsp;
					
							</td>
						</tr>
	
	<!-- enddate: Should be displayed only in case of edit -->
				
					<logic:equal name="<%=Constants.OPERATION%>" value="<%=Constants.EDIT%>">					
						<tr><!--sneha: changed classes  -->
							<td width="1%" align="center" class="black_ar_new">&nbsp;</td>
							<td class="black_ar_new">
								<label for="endDate">
									<bean:message key="clinicalstudy.enddate" />
								</label>
							</td>
				
							 <td class="black_new" colspan=2>
					<%
					if(endDateClinicalStudy.trim().length() > 0)
					{
						Integer clinicalStudyYear1 = new Integer(Utility.getYear(endDateClinicalStudy ));
						Integer clinicalStudyMonth1 = new Integer(Utility.getMonth(endDateClinicalStudy ));
						Integer clinicalStudyDay1 = new Integer(Utility.getDay(endDateClinicalStudy ));
											
					%>
					<ncombo:DateTimeComponent name="endDate"
								  id="endDate"
								  formName="clinicalStudyForm"
								  month= "<%= clinicalStudyMonth1 %>"
								  year= "<%= clinicalStudyYear1 %>"
								  day= "<%= clinicalStudyDay1 %>"
								  value="<%=endDateClinicalStudy %>"
								  pattern="dd-MM-yyyy"
								  styleClass="black_new"
								  
					/>
					<% 
						}
						else
						{  
					 %>
					<ncombo:DateTimeComponent name="endDate"
								  id="endDate"
								  formName="clinicalStudyForm"
								  pattern="dd-MM-yyyy"
								  styleClass="black_new"
										/>
					<% 
						} 
					%> 
					<bean:message key="page.dateFormat" />&nbsp;
					
							</td>
						</tr>
					</logic:equal>
						
	<!-- descriptionurl -->						
						<tr><!--sneha: changed class -->
							<td width="1%" align="center" class="black_ar_new">&nbsp;</td>
							<td class="black_ar_new">
								<label for="descriptionURL">
									<bean:message key="clinicalstudy.descriptionurl" />
								</label>
							</td>
							<td class="black_new" colspan=2>
								<html:text styleClass="black_new" maxlength="255" size="30" styleId="descriptionURL" property="descriptionURL" />
							</td>
						</tr>
	
	<!-- activitystatus -->	
						<logic:equal name="<%=Constants.OPERATION%>" value="<%=Constants.EDIT%>">
						<tr><!--sneha: changed classes and added image -->
							<td width="1%" align="center" class="black_ar_new"><span class="blue_ar_b"><img 		src="images/uIEnhancementImages/star.gif" alt="Mandatory" width="6" height="6" 		hspace="0" vspace="0" /></span></td>
							<td width="16%" align="left" class="black_ar_new">
								<label for="activityStatus">
									<bean:message key="site.activityStatus" />
								</label>
							</td>
							<td class="black_new">
	<!-- Mandar : 434 : for tooltip --><!--sneha: changed class -->
								<html:select property="activityStatus" styleClass="black_new" styleId="activityStatus" size="1" >
									<html:options name="<%=Constants.ACTIVITYSTATUSLIST%>" labelName="<%=Constants.ACTIVITYSTATUSLIST%>" />
								</html:select>
							</td>
						</tr>
						</logic:equal>
					<% 
						if(Variables.isCatissueModelAvailable)
						{
						%>
 <!--added for ColProto List-->
						<tr><!--sneha: changed classes  -->
							<td width="1%" align="center" class="black_ar_new">&nbsp;</td>
							<td class="black_ar_new">
									<label for="isRelatedtoCP">
										Is Related to Catissue CP?
									</label>
							</td>							
								
							<td class="black_new"><!--sneha: changed class-->
							<html:checkbox  property="isRelatedtoCP" onclick="showAllCP(this)" ></html:checkbox>
							</td>
						</tr>
											
						<tr><!--sneha: changed classes  -->
							<td width="1%" align="center" class="black_ar_new">&nbsp;</td>
							<td class="black_ar_new">
							<label for="colprotocolId">
								collection protocol 
							</label>
							</td>
							<td class="black_new"><!--sneha: changed class -->
							<html:select property="colprotocolId" styleClass="formFieldSizedNew" styleId="colprotocolIds" size="1" disabled="<%=!form.getIsRelatedtoCP()%>" onchange="onCPChange()" >
								<html:options collection="CPLIST" labelProperty="name" property="value"/>
							</html:select>
							</td>
						</tr>
						<%
						}
						%>
								
					</table> 	<!-- table 4 end -->
				</td>
			</tr>
			<tr><td>&nbsp;</td></tr> <!-- SEPARATOR -->
	</table>




	


<table summary="" cellpadding="0" cellspacing="0" border="0"  id ="table2" width="100%">
	<tr bgcolor="#ffffff">
			<td colspan="3">
				<table summary="" cellpadding="3" cellspacing="0" border="0" width="100%">
				<tr><!--sneha: changed classes -->
					<td colspan="5" align="left" class="tr_bg_blue1"><span
					class="blue_ar_b">&nbsp;
							<b><bean:message key="clinicalStudyEvent.eventTitle" /></b></span>
					</td>
					<td align="right" class="tr_bg_blue1"><!--sneha: changed classes-->	
					<html:button property="addCollectionProtocolEvents" styleClass="black_new" onclick="addBlock('outerdiv','d1')">Add More</html:button>
					<html:hidden property="outerCounter"/>	
					</td>
					
					<td class="tr_bg_blue1" align="Right"><!--sneha: changed classes -->
						<html:button property="deleteCollectionProtocolEvents" styleClass="black_new" onclick="deleteChecked('outerdiv','ClinicalStudy.do?operation=<%=operation%>&pageOf=pageOfClinicalStudy&status=true&button=deleteCollectionProtocolEvents',document.forms[0].outerCounter,'chk_proto_',true)" disabled="true">
							<bean:message key="buttons.delete"/>
						</html:button>
					</td>
			  </tr>
		   </table>
		</td>
	</tr>
</table>

<!--  Outer Begins -->


<table width="100%" id="table3">
<tbody id="outerdiv">


<%! Map map; %>
<%
		int maxCount=1;
		int maxIntCount=1;
				
		ClinicalStudyForm colForm = null;
		
		Object obj = request.getAttribute(Constants.CLINICAL_STUDY_FORM);
		//Map map = null;
		
		if(obj != null && obj instanceof ClinicalStudyForm)
		{
			colForm = (ClinicalStudyForm)obj;
			maxCount = colForm.getOuterCounter();
			map = colForm.getFormValues();
			
		}
	
		for(int counter=1;counter<=maxCount;counter++)
		{
			String commonLabel = "formValue(ClinicalStudyEvent:" + counter;
			
			String commonName = "ClinicalStudyEvent:" + counter;
			String cid = "ivl(" + counter + ")";
			String functionName = "insRow('" + commonLabel + "','" + cid +"'," + counter+ ")";
			String cpeIdentifier= commonLabel + "_id)";
			String check = "chk_proto_"+ counter;
			String tableId = "table_" + counter;
			
			if(colForm!=null)
			{
				Object o = colForm.getIvl(""+counter);
				if(o!=null)
					maxIntCount = Integer.parseInt(o.toString());
			}
		String tblColor = Constants.ODD_COLOR;
		if(counter%2 == 0)
			tblColor = Constants.EVEN_COLOR;
						
%>
<!-- sneha:made color change to the def of formfieldsized,formFieldAddMore and tabrightmostcellAddMore classes-->
<tr>
<td>
<table summary="" cellpadding="5" cellspacing="0" border="0"  width="100%" id="<%=tableId%>">

<tr><td>
<table summary="" cellpadding="3" cellspacing="0" border="0" width="100%" bgcolor="#FFFFFF"  id="<%=tableId%>"><!-- sneha: added border to the table def-->
	<tr>
		<td rowspan=2 class="tabrightmostcellAddMore"><%=counter%></td>

		<td class="formFieldAddMore">
			<table summary="" cellpadding="3" cellspacing="0" border="0" width="100%">
				<tr><!-- sneha: added image-->
					<td width="1%" align="center" class="black_ar_new"><span class="blue_ar_b"><img 		src="images/uIEnhancementImages/star.gif" alt="Mandatory" width="6" height="6" hspace="0" vspace="0" /></span></td>
						<html:hidden property="<%=cpeIdentifier%>" />
					</td>
					
					<td class="black_ar_new"><!--sneha: changed class -->
					<%
						String fldName = commonLabel + "_collectionPointLabel)";
					%>
						<label for="<%=fldName%>">
							<bean:message key="clinicalStudyEvent.label" />
						</label>
					</td>
					
					<td class="black_new" colspan=4><!--sneha: changed class -->

						<html:text property="<%=fldName%>" styleClass="black_new" styleId="<%=fldName%>" size="30" maxlength="255"  onkeypress="alphaNumOnly(this);" onchange="alphaNumOnly(this);" onkeyup="alphaNumOnly(this);">
						</html:text>
					</td>
					
<!--sneha: changed classes and added image --><td width="1%" align="center" class="black_ar_new"><span class="blue_ar_b"><img 		src="images/uIEnhancementImages/star.gif" alt="Mandatory" width="6" height="6" hspace="0" vspace="0" /></span></td>
					<%
						fldName="";
						fldName = commonLabel + "_studyCalendarEventPoint)";
						String keyStudyPoint = commonName + "_studyCalendarEventPoint";
						String valueStudyPoint = (String)colForm.getFormValue(keyStudyPoint);
						
					
						if(valueStudyPoint == null)
							valueStudyPoint = "1";
						
					%>

			        <td class="black_ar_new" ><!--sneha: changed class -->
			        	<label for="<%=fldName%>">
							<bean:message key="collectionprotocol.studycalendartitle" />
						</label>
			        </td>
					
					 <td  class="Black_new"><!--sneha: changed classes -->
			        	<html:text styleClass="black_new" size="30" 
			        			styleId="<%=fldName%>"  maxlength="10" 
			        			property="<%=fldName%>" 
			        			readonly="<%=readOnlyValue%>"
			        			value="<%=valueStudyPoint%>"   onkeypress="alphaNumOnly(this);" onchange="alphaNumOnly(this);" onkeyup="alphaNumOnly(this);" />

			        	<bean:message key="collectionprotocol.studycalendarcomment"/>
					</td>
				</tr>
				
			    <tr><!--sneha: changed classes and added image-->
					<td width="1%" align="center" class="black_ar_new"><span class="blue_ar_b"><img 		src="images/uIEnhancementImages/star.gif" alt="Mandatory" width="6" height="6" hspace="0" vspace="0" /></span></td>
					<td  align="left" class="black_ar_new">
						<bean:message key="clinicalStudyEvent.noOfVisits" />
					
<%
						fldName="";
						fldName = commonLabel + "_noOfEntries)";
						 keyStudyPoint = commonName + "_noOfEntries";
						 valueStudyPoint = (String)colForm.getFormValue(keyStudyPoint);
						
					
						if(valueStudyPoint == null)
							valueStudyPoint = "1";
						
					%>
					<td class="black_new" colspan="4"> <!--sneha: changed classes -->
					<html:text styleClass="black_new" size="6" 
			        			styleId="<%=fldName%>"  maxlength="10" 
			        			property="<%=fldName%>" 
			        			readonly="<%=readOnlyValue%>"
			        			value="<%=valueStudyPoint%>"  onkeypress="alphaNumOnly(this);" onchange="alphaNumOnly(this);" onkeyup="alphaNumOnly(this);" /> 				
								<bean:message key="clinicalStudyEvent.eventVisitComment" /></td>
  <%  
				   	if(Variables.isCatissueModelAvailable)
						{
								   fldName="";
								   fldName = commonLabel+"_relToCPEId)";
								   keyStudyPoint = commonName + "_relToCPEId)";
								   valueStudyPoint = (String)colForm.getFormValue(keyStudyPoint);

					%><!--sneha: changed classes -->
<td width="1%" align="center" class="black_ar_new"><span class="blue_ar_b"></span></td>
					
					<td align="left" class="black_ar_new">
						Related to CP Event
					</td>
				   <td class="black_ar_new" >
					<%
						boolean disable = true;
					    if(form.getIsRelatedtoCP())
						{ 
							if(readOnlyValue)
							{
								disable = true;
							}
							else
							{
								disable = false;
							}
						}

					%>	
					 
			<!-----put select box here -->
			<html:select property="<%=fldName%>"  styleClass="formFieldSizedNew"  styleId="<%=fldName%>" size="1" disabled="<%=disable%>" >
								<html:options collection="CPELIST" labelProperty="name" property="value"/>
							</html:select>

				   </td>
				<%}%> 
				 
					<%
							String outerKey = "ClinicalStudyEvent:" + counter + "_id";
							// Commented, for not to disable in any condition. --Srinivas
							//boolean outerBool = Utility.isPersistedValue(map,outerKey);
							String disableEventKey = "ClinicalStudyEvent:" + counter + "_disableEventKey";
							Boolean outerBool = (Boolean)colForm.getFormValue(disableEventKey);
							String outerCondition = "";
							if(outerBool!=null && outerBool == true)
								outerCondition = "disabled='disabled'";
//System.out.println("------------>"+outerCondition);
						%>
						
			    </tr>
				
			</TABLE>
		</td>
		<td rowspan=2 class="tabrightmostcellAddMore">
			<input type=checkbox name="<%=check%>" id="<%=check %>" <%=outerCondition%> onClick="enableButton(document.forms[0].deleteCollectionProtocolEvents,document.forms[0].outerCounter,'chk_proto_')">		
		</td>
	</tr>

	<!-- 2nd row -->
	<tr>
		<td class="tr_bg_blue1"><!--sneha: changed class -->
			<table summary="" cellpadding="0" cellspacing="0" border="0"  id ="table2" width="100%">
				<tr>
					<td colspan="3">
						<table summary="" cellpadding="3" cellspacing="0" border="0" width="100%">
							<tr><!--sneha: changed classes -->
								<td colspan="5" align="left" class="tr_bg_blue1"><span
								class="blue_ar_b">&nbsp;
			        			<b><bean:message key="clinicalStudyEvent.studyFormReq"/></b></span>
								 </td>
						<td class="tr_bg_blue1"><!--sneha: changed classes -->	
			     		<html:button property="addDeleteStudyForm" styleClass="black_new" value="Add More" onclick="<%=functionName%>"/>
			     		
			     		<html:hidden styleId="<%=cid%>" property="<%=cid%>" value="<%=""+maxIntCount%>"/>
			        </td>
			        <td class="tr_bg_blue1" align="Right"><!--sneha: changed class -->
			        		<% String temp = "deleteChecked('";
			        			temp = temp + commonLabel+"',";
			        			temp = temp + "'ClinicalStudy.do?operation="+operation+"&pageOf=pageOfClinicalStudy&status=true&button=deleteStudyForm&blockCounter="+counter+"',";
			        			temp = temp +"'"+ cid + "'" +",";
			        			temp = temp + "'chk_spec_"+ counter +"_',false)";
			        			
			        		%>  <!--sneha: changed class -->
							<html:button property="deleteStudyForm" styleClass="black_new" onclick="<%=temp%>">
								<bean:message key="buttons.delete"/>
							</html:button>
					</td>
			    </tr>
			    
			    <TBODY id="<%=commonLabel%>">
			    <TR> <!-- SUB TITLES -->
			        <b><td class="black_ar_new"><!--sneha: changed class -->
		        		<bean:message key="collectionprotocol.specimennumber" />
			      </td></b>
			       <!--sneha: changed classes and added image -->
			        <td class="black_ar_new"><img src="images/uIEnhancementImages/star.gif" alt="Mandatory" width="6" height="6" hspace="0" vspace="0" />
			        	<b><bean:message key="clinicalStudyEvent.studyForm"/></b>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						
			        </td>
			        
						<!--sneha: changed class and added image -->
			          <td class="black_ar_new"><img src="images/uIEnhancementImages/star.gif" alt="Mandatory" width="6" height="6" hspace="0" vspace="0" />
			        	<b><bean:message key="clinicalStudyEvent.studyFormLabel" /></b>
			        </td>
					<td class="black_new" colspan="2"><!--sneha: changed class -->
						<b><bean:message key="app.canHaveMultiRec" /></b>
					</td>
			        <td class="black_new"><!--sneha: changed class -->
						<label for="delete" align="center">
							<b><bean:message key="addMore.delete" /></b>
						</label>
					</td>
			    </TR><!-- SUB TITLES END -->
				
				<%
					
					for(int innerCounter=1;innerCounter<=maxIntCount;innerCounter++)
					{
				%>
				
				<TR>	<!-- SPECIMEN REQ DATA -->
			        <td class="tabrightmostcellAddMore"><%=innerCounter%>.</td>
			        <%
						
						int iCnt = innerCounter;

						String name123 = "formValue(ClinicalStudyEvent:"+counter+"_StudyFormContext:"+ iCnt;
											
						String formContextIdkey = name123 + "_id)";
					
						String innerCheck = "chk_spec_" + counter + "_"+ iCnt;
						//System.out.println("formContextIdkey: "+formContextIdkey);

						String formContextId = (String)form.getFormValue(formContextIdkey);
						String canHaveMultRec = "formValue(ClinicalStudyEvent:"+counter+"_StudyFormContext:"+ iCnt +"_canHaveMultipleRecords)";
						
						String canHaveMultRecKey = "ClinicalStudyEvent:"+counter+"_StudyFormContext:"+ iCnt +"_canHaveMultipleRecords";
						String canHaveMultRecVal="";
						if(form.getFormValue(canHaveMultRecKey)!=null)
							 canHaveMultRecVal = form.getFormValue(canHaveMultRecKey).toString();						
						
						
						String disableFrmContextKey =  "ClinicalStudyEvent:"+counter+"_StudyFormContext:"+ iCnt +"_disableFrmContextKey";
							Boolean innerBool = (Boolean)colForm.getFormValue(disableFrmContextKey);
							//boolean innerBool = false;
							
							String innerCondition = "";
							if(innerBool!=null)
								innerCondition = "disabled='disabled'";

					%>
			        
			     		<html:hidden property="<%=formContextIdkey%>"  />	        			        
			        <td class="formFieldAddMore">
						<%
																
								boolean subListEnabled = false;
								//System.out.println("-->>"+counter +"%%%%"+maxIntCount);								
								String fName="";
								fName = name123 + "_containerId)";
								String lname = name123 + "_studyFormLabel)";
								String hiddenVal="";
						%>
					
			        	<select name="<%=fName%>" id="<%=fName%>" class="formFieldSizedNew" <%=innerCondition%> >
		<%for(int i=0;i<studyFormsList.size();i++)
		{
			String formLabel = "" + ((NameValueBean)studyFormsList.get(i)).getName();
			String formValue = "" + ((NameValueBean)studyFormsList.get(i)).getValue();
			String selected = "";
			
	
			if (formValue.equals(strDynEntContainerId) && counter == Integer.parseInt(addNewEventCounter))
			{
					selected = "selected";
					addNewEventCounter = null;
					hiddenVal=formValue;
			}
	
			else
			{
				String formIdkey = "ClinicalStudyEvent:"+counter+"_StudyFormContext:"+ iCnt +"_containerId";
				if (formValue.equals(form.getFormValue(formIdkey)))
				{
					selected = "selected";
					hiddenVal=formValue;
				}
			}

			

		%>
		<option value='<%=formValue%>' <%=selected%>><%=formLabel%></option>;
		
		<%		
			}
		

if(innerBool!=null && !hiddenVal.equals(""))
			{
				%>
<input type="hidden" name="<%=fName%>" id="<%=fName%>"  value="<%=hiddenVal%>" />
		<%
			}
%>

		</select>
						
					
						&nbsp;
						
			        </td>
			       	       
				 <td class="formFieldAddMore">
						<html:text styleClass="black_new" size="30" onkeypress="alphaNumOnly(this);" onchange="alphaNumOnly(this);" onkeyup="alphaNumOnly(this);"
			        			styleId="<%=lname%>"   
			        			property="<%=lname%>" 
			        			readonly="<%=readOnlyValue%>" maxlength="255"/> 
					 </td>
			        
			      
					<%
							String innerKey = "ClinicalStudyEvent:"+counter+"_StudyFormContext:"+ iCnt +"_id";
							// Commented, for not to disable in any condition. --Srinivas
							//boolean innerBool = Utility.isPersistedValue(map,innerKey);
							

						

						if(canHaveMultRecVal.equals("true"))
						{
						%>
						<td class="formFieldAddMore" width="5" colspan="2">
							<input type=checkbox name="<%=canHaveMultRec%>" id="<%=canHaveMultRec%>" property="<%=canHaveMultRec%>" value="true" checked="checked" <%=innerCondition%>  onclick="handleMultipleRecCheckbox(this);">
						</td>
						<input type="hidden" name="<%=canHaveMultRec%>" id="<%=canHaveMultRec%>"  value="true" />
						<%}else{%>
						
						<td class="formFieldAddMore" width="5" colspan="2">
							<input type=checkbox name="<%=canHaveMultRec%>" id="<%=canHaveMultRec%>" property="<%=canHaveMultRec%>" value="true" <%=innerCondition%> onclick="handleMultipleRecCheckbox(this);" >
						</td>
						<input type="hidden" name="<%=canHaveMultRec%>" id="<%=canHaveMultRec%>"  value="false" />
						<%}%>


						<td class="formFieldAddMore" colspan="2">
						
							<input type=checkbox name="<%=innerCheck%>" id="<%=innerCheck %>" <%=innerCondition%>>
						</td>
				</TR>	<!-- SPECIMEN REQ DATA END -->
				<%
					} // inner for block
										
				%>
				</TBODY>
			</TABLE>
		</td>
	</tr>
</table> <!-- outer table for CPE ends -->
</td></tr>
</table>
</td></tr>
</table>
</td></tr>
<%
} // outer for 						

%>
</tbody></table>
<!-- outermosttable  -->

<table cellpadding="0" cellspacing="0" border="0" width = "100%" id="submittable">		
				<tr>
				<td align="left" class="buttonbg">
					<%
					   	//String action = "confirmDisable('" + formName +"',document.forms[0].activityStatus)";
						String action = "checkForDuplicates('"+formName+"')";
						String selCordAction = "selectCoordinators('" + formName +"')";
						//String selCordAction = "selectCoordinators('" + formName +"', " + pageView +")";
					%><!--sneha: changed class -->
						<html:button styleClass="blue_ar_b" property="submitPage" onclick="<%=selCordAction%>">
							<bean:message key="buttons.submit"/>
						</html:button>
					</td>
				</tr>
			</table>

<%@ include file="/pages/content/ConsentTracking/DefineConsent.jsp" %>
</html:form>

<!--  Outer Ends   -->
<html:form action="DummyClinicalStudy.do">
<div id="d1">
<table summary="" cellpadding="0" cellspacing="0" border="0"  width="100%" id="table_`">
<tr><td>
<table cellpadding="3" class="formFieldAddMore" width="100%">
<tr><td>
<table summary="" cellpadding="3" cellspacing="0" border="0" width="100%" id="itable_`">
	<tr>
		<td rowspan=2 class="tabrightmostcellAddMore">`</td>
		<td class="formFieldAddMore">
			<table summary="" cellpadding="3" cellspacing="0" border="0" width="100%">
				<tr><!--sneha: changed class and added image -->						 

					<td width="1%" align="center" class="black_ar_new"><span class="blue_ar_b"><img 			src="images/uIEnhancementImages/star.gif" alt="Mandatory" width="6" height="6" hspace="0" vspace="0" /></span></td>
						
					</td>
					
					<td class="black_ar_new"><!--sneha: changed class -->
					<label for="formValue(ClinicalStudyEvent:`_collectionPointLabel)">
							<bean:message key="clinicalStudyEvent.label" />
						</label>
					</td>

					
					<td class="black_new" colspan=4><!--sneha: changed class -->						
						<html:text property="formValue(ClinicalStudyEvent:`_collectionPointLabel)" styleClass="black_new" styleId="formValue(ClinicalStudyEvent:`_collectionPointLabel)" size="30" onkeypress="alphaNumOnly(this);" onchange="alphaNumOnly(this);" onkeyup="alphaNumOnly(this);">
						</html:text>
					</td>
						<!--sneha: changed class  and added image-->
					 <td class="black_ar_new"><img src="images/uIEnhancementImages/star.gif" alt="Mandatory" width="6" height="6" hspace="0" vspace="0" />
					 </td>
					 <td class="black_ar_new" >
			        	<label for="formValue(ClinicalStudyEvent:`_studyCalendarEventPoint)">
							<bean:message key="collectionprotocol.studycalendartitle" />
						</label>
			        </td>
			        
			        <td class="black_new"><!--sneha: changed classes -->
			        	<html:text styleClass="black_new" size="30" 
			        			styleId="formValue(ClinicalStudyEvent:`_studyCalendarEventPoint)"  maxlength="10" onkeypress="alphaNumOnly(this);" onchange="alphaNumOnly(this);" onkeyup="alphaNumOnly(this);"
			        			property="formValue(ClinicalStudyEvent:`_studyCalendarEventPoint)" 
			        			value="1" /> 
			        	<bean:message key="collectionprotocol.studycalendarcomment"/>
					</td>
				</tr>
				
			    <tr><!--sneha: changed class and added image -->
				 <td class="black_ar_new"><img src="images/uIEnhancementImages/star.gif" alt="Mandatory" width="6" height="6" hspace="0" vspace="0" />
				 </td>
				 <td  align="left" class="black_ar_new">

						<bean:message key="clinicalStudyEvent.noOfVisits" />
					</td>
	<% 
						String colspanValue = "0";
					if(Variables.isCatissueModelAvailable)
						{
						colspanValue="4";			 
						}
					%><!--sneha: changed classes -->
					<td class="black_new" colspan="4">
					<html:text styleClass="black_new" size="6" onkeypress="alphaNumOnly(this);" onchange="alphaNumOnly(this);" onkeyup="alphaNumOnly(this);"
			        			styleId= "formValue(ClinicalStudyEvent:`_noOfEntries)"  maxlength="10" 
			        			property="formValue(ClinicalStudyEvent:`_noOfEntries)" 
			        			readonly="formValue(ClinicalStudyEvent:`_noOfEntries)"
			        			value="1" /> 
							<bean:message key="clinicalStudyEvent.eventVisitComment" />
			       </td>
				   <td width="1%" align="center" class="black_ar_new"><span class="blue_ar_b"></span></td>
	<%if(Variables.isCatissueModelAvailable)
						{
						
						%><!--sneha: changed class -->
				<td  align="left" class="black_ar_new"><span
					class="black_ar_new">
						Related to CP Event
					</td>
					<td class="black_ar_new">
					<html:select property="formValue(ClinicalStudyEvent:`_relToCPEId)"  styleClass="formFieldSizedNew"  styleId="formValue(ClinicalStudyEvent:`_relToCPEId)" size="1" disabled="formValue(ClinicalStudyEvent:`_relToCPEId)" >
								<html:options collection="CPELIST" labelProperty="name" property="value"/>
					</html:select>
			       </td>
					<%} %>
			    </tr>
			</TABLE>
		</td>
			<td rowspan=2 class="tabrightmostcellAddMore">
				<input type=checkbox name="chk_proto_`" id="chk_proto_`" onClick="enableButton(document.forms[0].deleteCollectionProtocolEvents,document.forms[0].outerCounter,'chk_proto_')">		
			</td>
		</tr>
	<!-- 2nd row -->
	<tr>
		<td class="tr_bg_blue1"><!--sneha: changed class -->
			<table summary="" cellpadding="3" cellspacing="0" border="0" width="100%">
			    <tr><!--sneha: changed classes -->
			        <td colspan="5" align="left" class="tr_bg_blue1"><span
								class="blue_ar_b">&nbsp;
			        	<b><bean:message key="clinicalStudyEvent.studyFormReq"/></b>
			        </td>
			        <td class="tr_bg_blue1">	
			        <%
				        String hiddenCounter = "ivl(`)";
			        %><!--sneha: changed class -->
			     		<html:button property="addSpecimenReq" styleClass="black_new" value="Add More" onclick="insRow('formValue(ClinicalStudyEvent:`','ivl(`)','`')"/>
			     		
			     		<html:hidden styleId="<%=hiddenCounter%>" property="<%=hiddenCounter%>" value="1"/>
			        </td>
					<!--sneha: changed class -->
			       <td class="tr_bg_blue1" align="Right">
							<html:button property="deleteStudyForm" styleClass="black_new" onclick="deleteChecked('formValue(ClinicalStudyEvent:`','ClinicalStudy.do?operation=<%=operation%>&pageOf=pageOfClinicalStudy&status=true&button=deleteStudyForm&blockCounter=`','ivl(`)','chk_spec_`_',false)">
								<bean:message key="buttons.delete"/>
							</html:button>
					</td>
			    </tr>
			    <TBODY id="formValue(ClinicalStudyEvent:`">
			    <TR> <!-- SUB TITLES -->
			       <td class="black_new"><!--sneha: changed class -->
		        		<b><bean:message key="collectionprotocol.specimennumber" /></b>
			        </td>
			       <!--sneha: changed class and added image -->
			      <td class="black_ar_new"><img src="images/uIEnhancementImages/star.gif" alt="Mandatory" width="6" height="6" hspace="0" vspace="0" />
			        	<b><bean:message key="clinicalStudyEvent.studyForm"/></b>					
			        </td>
			        
			      <!--sneha: changed class and added image -->
			      <td class="black_new"><img src="images/uIEnhancementImages/star.gif" alt="Mandatory" width="6" height="6" hspace="0" vspace="0" />
			        	<b><bean:message key="clinicalStudyEvent.studyFormLabel" /></b>
			        </td><!--sneha: changed class -->
						<td class="black_new">
						<b><bean:message key="app.canHaveMultiRec" /></b>
					</td><!--sneha: changed class -->
			       <td class="black_new">
						<label for="delete" align="center">
							<b><bean:message key="addMore.delete" /></b>
						</label>
					</td>
			    </TR><!-- SUB TITLES END -->
				
				<TR class="formFieldAddMore">	<!-- SPECIMEN REQ DATA -->
			       <td class="formFieldAddMore">1.
			        	<html:hidden property="formValue(ClinicalStudyEvent:`_StudyFormContext:1_id)" />
			        </td>
			        <td class="formFieldAddMore">		
			        	<html:select property="formValue(ClinicalStudyEvent:`_StudyFormContext:1_containerId)" 
										styleClass="formFieldSizedNew" 
										styleId="formValue(ClinicalStudyEvent:`_StudyFormContext:1_containerId)" size="1">
							<html:options collection="<%=Constants.STUDY_FORMS_LIST%>" labelProperty="name" property="value"/>
						</html:select>
						&nbsp;
			        </td>
			        <td class="formFieldAddMore">
			        	<html:text styleClass="black_new" size="30" onkeypress="alphaNumOnly(this);" onchange="alphaNumOnly(this);" onkeyup="alphaNumOnly(this);"
			        			styleId="formValue(ClinicalStudyEvent:`_StudyFormContext:1_studyFormLabel)" 
			        			property="formValue(ClinicalStudyEvent:`_StudyFormContext:1_studyFormLabel)" 
			        			readonly="<%=readOnlyValue%>"/>
					</td>
					<td class="formFieldAddMore">
			        	<input type=checkbox name="formValue(ClinicalStudyEvent:`_StudyFormContext:1_canHaveMultipleRecords)"  
						id="formValue(ClinicalStudyEvent:`_StudyFormContext:1_canHaveMultipleRecords)" 
						property="formValue(ClinicalStudyEvent:`_StudyFormContext:1_canHaveMultipleRecords)" 
						value='true'/>
					</td>
						<td class="formFieldAddMore" width="5" colspan="3">
							<input type=checkbox name="chk_spec_`_1" id="chk_spec_`_1" ><!-- onClick="enableButton(document.forms[0].deleteSpecimenReq,'ivl(`)','chk_spec_`_ ')"-->	
						</td>
						</TR>	<!-- SPECIMEN REQ DATA END -->
				</TBODY>
				</TABLE>
				</td></tr>
</table> <!-- outer table for CPE ends -->
</td></tr>
</table></td></tr>
</table></div></td>
</tr></table>
</html:form></body>