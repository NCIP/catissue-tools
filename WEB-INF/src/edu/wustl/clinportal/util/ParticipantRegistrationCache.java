
package edu.wustl.clinportal.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import net.sf.ehcache.CacheException;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.util.logger.Logger;

/**
 * This class handles all ParticipantRegistration Cache related operations
 * @author Falguni_Sachde
 */
class ParticipantRegistrationCache
{

	//participantRegistrationInfoList contains list of ParticipantRegistration Info objects
	List partRegInfoLst;

	/**
	 * Constructor which gets the participantRegistrationInfo list from cache and stores in participantRegistrationInfoList  
	 *
	 */
	public ParticipantRegistrationCache()
	{
		this.partRegInfoLst = getParticipantRegInfoListFromCache();
	}

	/**
	 * This function gets the ParticipantRegInifoList from the cache.
	 * @return list
	 */
	private List getParticipantRegInfoListFromCache()
	{
		List partRegInfoList = null;
		try
		{
			//getting instance of catissueCoreCacheManager and getting participantMap from cache
			CatissueCoreCacheManager catCoreCacheMger = CatissueCoreCacheManager.getInstance();
			partRegInfoList = (Vector) catCoreCacheMger
					.getObjectFromCache(Constants.LIST_OF_REGISTRATION_INFO);
		}
		catch (IllegalStateException e)
		{
			e.printStackTrace();
			Logger.out.info("Error while accessing cache");
		}
		catch (CacheException e)
		{
			e.printStackTrace();
			Logger.out.info("Error while accessing cache");
		}
		return partRegInfoList;
	}

	/**
	 *	This method adds the csID and csTitle in the ParticipantRegistrationInfo object
	 *	and add this object to participantRegistrationInfoList; 
	 * 	@param csId collection protocol ID
	 * 	@param csTitle collection protocol title
	 */
	public void addNewCS(Long csId, String csTitle, String csShortTitle)
	{
		//This method adds the csID and csTitle in the ParticipantRegistrationInfo object 
		//and add this object to participantRegistrationInfoList;

		//Creating the new ParticipantRegInfo object and storing the collection protocol info 
		ParticipantRegistrationInfo partRegInfo = new ParticipantRegistrationInfo();
		partRegInfo.setCsId(csId);
		partRegInfo.setCsTitle(csTitle);
		partRegInfo.setCsShortTitle(csShortTitle);
		List partInfoList = new ArrayList();
		partRegInfo.setParticipantInfoCollection(partInfoList);
		partRegInfoLst.add(partRegInfo);
	}

	/**
	 *	This method updates the title of the collection protocol.
	 *	first find out the participantRegistrationInfo object in participantRegistrationInfoList
	 *	where csID = csId and the updates the csTitle with newTitle.
	 * 	@param csId
	 * 	@param newTitle
	 */
	public void updateCSTitle(Long csId, String newTitle)
	{
		//This method updates the title of the collection protocol.
		//first find out the participantRegistrationInfo object in participantRegistrationInfoList 
		//where csID = csId and the updates the csTitle with newTitle.

		Iterator itr = partRegInfoLst.iterator();
		// Iterating thru whole participsantRegistrationInfoList and 
		//get the object in which csId = csId
		while (itr.hasNext())
		{
			ParticipantRegistrationInfo partRegInfo = (ParticipantRegistrationInfo) itr.next();
			if (partRegInfo.getCsId().longValue() == csId.longValue())
			{
				//Set the new cs title
				partRegInfo.setCsTitle(newTitle);
				break;
			}
		}
	}

	/**
	 *	This method updates the title of the collection protocol.
	 *	first find out the participantRegistrationInfo object in participantRegistrationInfoList
	 *	where csID = csId and the updates the csTitle with newTitle.
	 * 	@param csId
	 * 	@param newShortTitle
	 */
	public void updateCSShortTitle(Long csId, String newShortTitle)
	{
		//This method updates the title of the collection protocol.
		//first find out the participantRegistrationInfo object in participantRegistrationInfoList 
		//where csID = csId and the updates the csTitle with newTitle.

		Iterator itr = partRegInfoLst.iterator();
		// Iterating thru whole participsantRegistrationInfoList and 
		//get the object in which csId = csId
		while (itr.hasNext())
		{
			ParticipantRegistrationInfo partRegInfo = (ParticipantRegistrationInfo) itr.next();
			if (partRegInfo.getCsId().longValue() == csId.longValue())
			{
				partRegInfo.setCsShortTitle(newShortTitle);
				break;
			}
		}
	}

	/**
	 * 	This method searches the participantRegistrationInfo object in the
	 *  participantRegistrationInfoList where csID = csId and removes this object
	 *  from the List.
	 * 	@param csId
	 */
	public void removeCS(Long csId)
	{
		//This method searches the participantRegistrationInfo object in the 
		//participantRegistrationInfoList where csID = csId and removes this object
		//from the List.
		Iterator itr = partRegInfoLst.iterator();
		while (itr.hasNext())
		{
			ParticipantRegistrationInfo partiRegInfo = (ParticipantRegistrationInfo) itr.next();
			if (partiRegInfo.getCsId().longValue() == csId.longValue())
			{
				partRegInfoLst.remove(partiRegInfo);
				break;
			}
		}
	}

	/**
	 *	This method searches the participantRegistrationInfo object in the 
	 *	participantRegistrationInfoList where csID = csId and adds the participantId in 
	 *	participantIdCollection
	 * 	@param csId
	 * 	@param participantID
	 */
	public void registerParticipant(Long csId, Long participantID, String protPartipantID)
	{
		//This method searches the participantRegistrationInfo object in the 
		//participantRegistrationInfoList where csID = csId and adds the participantId in 
		//participantIdCollection

		Iterator itr = partRegInfoLst.iterator();

		//Iterate thru whole list and check weather any ParticipantRegInfo object is there in list with given collection protol Id.
		//If it is present then add particpantId in participantCollection 

		while (itr.hasNext())
		{
			ParticipantRegistrationInfo partiRegInfo = (ParticipantRegistrationInfo) itr.next();
			if (partiRegInfo.getCsId().longValue() == csId.longValue())
			{
				List partiInfoList = (List) partiRegInfo.getParticipantInfoCollection();
				updateParticipantList(partiInfoList, participantID, protPartipantID);

				break;
			}
		}

	}

	/**
	 * @param partInfoList
	 * @param participantID
	 * @param protoPartipantID
	 */
	private void updateParticipantList(List partInfoList, Long participantID,
			String protoPartipantID)
	{
		if (participantID != null)
		{
			StringBuffer partInfo = new StringBuffer();
			partInfo.append(participantID.toString() + ":");

			if (protoPartipantID != null && !protoPartipantID.equals(""))
			{
				partInfo.append(protoPartipantID);
			}
			String participantInfo = partInfo.toString();
			partInfoList.add(participantInfo);
		}

	}

	/**
	 * 	This method searches the participantRegistrationInfo object in the 
	 *	participantRegistrationInfoList where csID = csId and removes the participantId from
	 *	participantIdCollection
	 * @param csId
	 * @param participantID
	 * @param protopartiID
	 */
	public void deRegisterParticipant(Long csId, Long participantID, String protopartiID)
	{
		//This method searches the participantRegistrationInfo object in the 
		//participantRegistrationInfoList where csID = csId and removes the participantId from
		//participantIdCollection

		Iterator itr = partRegInfoLst.iterator();

		//Iterate thru whole list and check weather any ParticipantRegInfo object is there in list with given collection protol Id.
		//If it is present then remove given add particpantId from participantCollection 

		while (itr.hasNext())
		{
			ParticipantRegistrationInfo partiRegInfo = (ParticipantRegistrationInfo) itr.next();
			if (partiRegInfo.getCsId().longValue() == csId.longValue())
			{
				List partiInfoList = (List) partiRegInfo.getParticipantInfoCollection();
				updateParticipantDeRegister(partiInfoList, participantID, protopartiID);

				break;
			}
		}
	}

	/**
	 * @param partiInfoList
	 * @param participantID
	 * @param protopantID
	 */
	private void updateParticipantDeRegister(List partiInfoList, Long participantID,
			String protopantID)
	{
		if (participantID != null)
		{
			StringBuffer strPart = new StringBuffer();
			strPart.append(participantID.toString() + ":");
			//String participantInfo = participantID.toString() + ":";
			if (protopantID != null && !protopantID.equals(""))
			{
				strPart.append(protopantID);
			}

			partiInfoList.remove(strPart.toString());
		}

	}

	/**
	 * 	This method searches the participantRegistrationInfo object in the 
	 *	participantRegistrationInfoList where csID = csId and returns the Participant 
	 *	Collection
	 * 	@param csId
	 * 	@return
	 */
	public List getParticipantInfoCollection(Long csId)
	{
		//This method searches the participantRegistrationInfo object in the 
		//participantRegistrationInfoList where csID = csId and returns the Participant 
		//Collection
		List partiInfoList = null;
		Iterator itr = partRegInfoLst.iterator();
		while (itr.hasNext())
		{
			ParticipantRegistrationInfo partRegInfo = (ParticipantRegistrationInfo) itr.next();
			if (partRegInfo.getCsId().longValue() == csId.longValue())
			{
				partiInfoList = (List) partRegInfo.getParticipantInfoCollection();
				break;
			}
		}
		return partiInfoList;
	}

	/**
	 * This method returns a list of cs ids and cs short titles 
	 * from the participantRegistrationInfoList
	 * @return
	 */
	public List getCSDetailCollection()
	{
		//This method returns a list of cs ids and cs titles from the participantRegistrationInfoList
		List csDetailsList = new ArrayList();
		Iterator itr = partRegInfoLst.iterator();
		while (itr.hasNext())
		{
			ParticipantRegistrationInfo partiRegInfo = (ParticipantRegistrationInfo) itr.next();
			NameValueBean csDetails = new NameValueBean(partiRegInfo.getCsShortTitle(),
					partiRegInfo.getCsId());
			csDetailsList.add(csDetails);
		}
		return csDetailsList;
	}

	/**
	 * This method returns a list of cs ids and cs titles 
	 * from the participantRegistrationInfoList
	 * @return
	 */
	public Map<Long, String> getCSIDTitleMap()
	{
		//This method returns a list of cs ids and cs titles from the participantRegistrationInfoList
		Map<Long, String> csIDTitleMap = new HashMap<Long, String>();
		Iterator itr = partRegInfoLst.iterator();

		while (itr.hasNext())
		{
			ParticipantRegistrationInfo partiRegInfo = (ParticipantRegistrationInfo) itr.next();
			//NameValueBean csDetails = new NameValueBean(participantRegInfo.getCsShortTitle(), participantRegInfo.getCsId());
			csIDTitleMap.put(partiRegInfo.getCsId(), partiRegInfo.getCsTitle());
		}

		return csIDTitleMap;
	}

}
