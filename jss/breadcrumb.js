String.prototype.startsWith = function(str)
	{return (this.match("^"+str)==str)}

	function breadcrumb(treeKey,participantId,cpId,eventId,canHaveMultipleRecord)
	{
		var urlString = null;
		if(treeKey.startsWith('ClinicalStudyEvent'))
		{
			if(window.parent.frames[1].document.getElementById('isDirty') != null && window.parent.frames[1].document.getElementById('isDirty').value == 'true')
			{
				var choice = confirm("Your changes have not been saved.\n\nDiscard your changes?");
				if(choice)
				{
					urlString = "LoadEventDetails.do?cpSearchParticipantId="+ participantId+"&cpSearchCpId=" +		cpId+"&treeViewKey=" +treeKey+"&protocolEventId=" + eventId;
					goToPage(urlString,treeKey,participantId,cpId,eventId);
				}
			}
			else
			{
				urlString = "LoadEventDetails.do?cpSearchParticipantId="+ participantId+"&cpSearchCpId=" +		cpId+"&treeViewKey=" +treeKey+"&protocolEventId=" + eventId;
				goToPage(urlString,treeKey,participantId,cpId,eventId);
			}
		}
		else if(treeKey.startsWith('EventEntry'))
		{
			if(window.parent.frames[1].document.getElementById('isDirty') != null && window.parent.frames[1].document.getElementById('isDirty').value == 'true')
			{
				var choice = confirm("Your changes have not been saved.\n\nDiscard your changes?");
				if(choice)
				{
					urlString = "LoadEventDetails.do?cpSearchParticipantId="+ participantId+"&cpSearchCpId=" +		cpId+"&protocolEventId=" + eventId+"&entryId=null&treeViewKey=" + treeKey;
					goToPage(urlString,treeKey,participantId,cpId,eventId);
				}
			}
			else
			{
				urlString = "LoadEventDetails.do?cpSearchParticipantId="+ participantId+"&cpSearchCpId=" +		cpId+"&protocolEventId=" + eventId+"&entryId=null&treeViewKey=" + treeKey;
				goToPage(urlString,treeKey,participantId,cpId,eventId);
			}
		}
		else if(treeKey.startsWith('FormEntry'))
		{
			if(window.parent.frames[1].document.getElementById('isDirty') != null && window.parent.frames[1].document.getElementById('isDirty').value == 'true')
			{
				var choice = confirm("Your changes have not been saved.\n\nDiscard your changes?");
				if(choice)
				{
					urlString = "LoadEventDetails.do?cpSearchParticipantId="+ participantId+"&cpSearchCpId=" +		cpId+"&protocolEventId=" + eventId+"&entryId=null&treeViewKey=" + treeKey;
					goToPage(urlString,treeKey,participantId,cpId,eventId);
				}
			}
			else
			{
				urlString = "LoadEventDetails.do?cpSearchParticipantId="+ participantId+"&cpSearchCpId=" +		cpId+"&protocolEventId=" + eventId+"&entryId=null&treeViewKey=" + treeKey;
				goToPage(urlString,treeKey,participantId,cpId,eventId);
			}
		}
	}

	function breadCrumbrequest(treeKey,participantId,cpSearchCpId,formId,eventId,formContextId)
	{
		var request = newXMLHTTPReq();
		var handlerFunction = getReadyStateHandler(request,breadCrumbResponse,true);
		request.onreadystatechange = handlerFunction;
		//Open connection to servlet
		request.open("POST","BreadCrumbAction.do",true);
		request.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
		request.send("&operation=getBreadCrumbString&participantId="+participantId+"&treeKey="+treeKey+"&cpSearchCpId="+cpSearchCpId+"&formId="+formId+"&eventId="+eventId+"&formContextId="+formContextId);
	}
	function breadCrumbResponse(breadCrumbString)
	{
		window.parent.document.getElementById ("breadCrumbDiv").style.height="5%";
		window.parent.document.getElementById ("breadCrumbDiv").style.fontSize="12px";
		window.parent.document.getElementById ("breadCrumbDiv").innerHTML =breadCrumbString;
	}
	function viewParticipantDetails(pId,cpId)
	{
		if(window.parent.frames[1].document.getElementById('isDirty') != null && window.parent.frames[1].document.getElementById('isDirty').value == 'true')
		{
			var choice = confirm("Your changes have not been saved.\n\nDiscard your changes?");
			if(choice)
			{
				goToParticipantPage(pId,cpId);
			}
		}
		else
		{
			goToParticipantPage(pId,cpId);
		}
	}

	function checkForModifiedData(position)
	{
		if(window.parent.frames[1].document.getElementById('isDirty').value == 'true')
		{
			var choice = confirm("Your changes have not been saved.\n\nDiscard your changes?");
			if(choice)
			{
				window.parent.frames[1].document.getElementById('dataEntryOperation').value = "insertChildData";
				window.parent.frames[1].document.getElementById('breadCrumbPosition').value = position;
				cancelInsertData();
			}
		}
		else
		{
			window.parent.frames[1].document.getElementById('dataEntryOperation').value = "insertChildData";
			window.parent.frames[1].document.getElementById('breadCrumbPosition').value = position;
			cancelInsertData();
			
		}
	}

	function cancelInsertData()
	{
		window.parent.frames[1].document.getElementById('mode').value = "cancel";
		var dataEntryForm = window.parent.frames[1].document.getElementById('dataEntryForm');
		dataEntryForm.submit();
	}
	
	function goToPage(urlString,treeKey,participantId,cpId,eventId)
	{
		window.parent.frames[1].location = urlString;
		breadCrumbrequest(treeKey,participantId,cpId, null,eventId,null);
	}

	function goToParticipantPage(pId,cpId)
	{
		window.parent.document.getElementById ("breadCrumbDiv").style.height="0px";
		window.parent.document.getElementById ("breadCrumbDiv").style.fontSize="0px";
		window.parent.document.getElementById ("breadCrumbDiv").innerHTML =""; 
		window.parent.frames[1].location ="QueryParticipantSearch.do?pageOf=pageOfParticipantCPQueryEdit&operation=edit&<%=Constants.CP_SEARCH_CP_ID%>="+cpId+"&id="+pId;
	}