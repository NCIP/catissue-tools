/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */


package edu.wustl.clinportal.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import net.sf.ehcache.CacheException;
import edu.wustl.clinportal.domain.Participant;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.util.logger.Logger;

/**
 * This class handles all participantCache Related operations
 * @author rukhsana
 *
 */
public class ParticipantCache
{

	//	participantsMap is map which contains all participants information.
	Map participantsMap;

	public ParticipantCache()
	{
		this.participantsMap = getParticipantMapFromCache();
	}

	/**
	 * This function returns the participantMap from the cache
	 * @return Map
	 */
	private Map getParticipantMapFromCache()
	{
		HashMap participantMap = new HashMap();
		try
		{
			//getting instance of catissueCoreCacheManager and getting participantMap from cache
			CatissueCoreCacheManager catCoreCacheMger = CatissueCoreCacheManager.getInstance();
			participantMap = (HashMap) catCoreCacheMger
					.getObjectFromCache(Constants.MAP_OF_PARTICIPANTS);
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
		return participantMap;
	}

	/**
	 * This method returns the participant object form participants Map where
	 * key = participantId
	 * @param participantId
	 * @return
	 */
	public Participant getParticipantInfo(Long participantId)
	{
		//This method returns the participant object form participants Map where key = participantId
		return (Participant) participantsMap.get(participantId);
	}

	/**
	 * This method returns the particpantList for a given ID List
	 * @param participantIdList - Participant Id List
	 * @return List which contains participant objects
	 */
	public List getParticipantsList(List participantIdList)
	{
		List participantList = new ArrayList();
		Iterator itr = participantIdList.iterator();
		while (itr.hasNext())
		{
			Long participantId = (Long) itr.next();
			Participant participant = (Participant) participantsMap.get(participantId);
			if (participant != null)
			{
				participantList.add(participant);
			}
		}
		return participantList;
	}

	/**
	 * This method returns participant names for given ID List
	 * @param participantIdList Participant ID List
	 * @return List which contains Participant Names in format ID:lastName:firstName
	 */
	public List getParticpantNamesWithID(List partInfoList)
	{
		//This method returns the participant names with ID
		List participantList = new ArrayList();
		Iterator itr = partInfoList.iterator();
		while (itr.hasNext())
		{
			String participantInfo = (String) itr.next();
			Long participantId = null;
			String protoPartId = null;
			StringTokenizer stoken = new StringTokenizer(participantInfo, ":");
			while (stoken.hasMoreTokens())
			{
				participantId = Long.valueOf(stoken.nextToken());
				if (stoken.hasMoreTokens())
				{
					protoPartId = stoken.nextToken();
				}
			}
			Participant participant = (Participant) participantsMap.get(participantId);
			if (participant != null)
			{
				String info = participant.getId().toString();
				String partDispInfo = "";
				if (participant.getLastName() != null && !participant.getLastName().equals(""))
				{
					partDispInfo = participant.getLastName();
				}
				if (participant.getFirstName() != null && !participant.getFirstName().equals(""))
				{
					if (participant.getLastName() == null || participant.getLastName().equals(""))
					{
						partDispInfo = participant.getFirstName();
					}
					else
					{
						partDispInfo = partDispInfo + " , " + participant.getFirstName();
					}
				}
				if (partDispInfo.equals(""))
				{
					partDispInfo = "N/A";
				}
				if (protoPartId != null && !protoPartId.equals(""))
				{
					partDispInfo = partDispInfo + " (" + protoPartId + ")";
				}
				else
				{
					partDispInfo = partDispInfo + " (N/A)";
				}

				if (partDispInfo.equals(""))
				{
					partDispInfo = "N/A";
				}
				info = info + ":" + partDispInfo;
				participantList.add(info);
			}
		}
		return participantList;
	}

	/**
	 * This method adds the participant in Map
	 * @param participant the object which is to be added in map
	 */
	public void addParticipant(Participant participant)
	{
		if (participant != null && participant.getId() != null)
		{
			participantsMap.put(participant.getId(), participant);
		}
	}

	/**
	 * This method removes the participant from the map
	 * @param participantId - Participant Id which is to be removed from ParticipantMap
	 */
	public void removeParticipant(Long participantId)
	{
		participantsMap.remove(participantId);
	}
}
