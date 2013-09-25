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

import java.util.List;
import java.util.Map;

import edu.wustl.clinportal.domain.Participant;

/**
 * This class handles Cache related operations
 * @author falguni_sachde
 *
 */
public class ParticipantRegistrationCacheManager
{

	ParticipantRegistrationCache partiRegCache;
	ParticipantCache participantCahe;

	public ParticipantRegistrationCacheManager()
	{
		partiRegCache = new ParticipantRegistrationCache();
		participantCahe = new ParticipantCache();
	}

	/**
	 * This method adds the Collection Protocol ID and title in cache.
	 * @param csId cs id
	 * @param csTitle Title
	 * @param csShortTitle Short Title
	 */
	public synchronized void addNewCS(Long csId, String csTitle, String csShortTitle)
	{
		partiRegCache.addNewCS(csId, csTitle, csShortTitle);
	}

	/**
	 * This method updates the cs Title 
	 * @param csId
	 * @param newTitle
	 */
	public synchronized void updateCSTitle(Long csId, String newTitle)
	{
		partiRegCache.updateCSTitle(csId, newTitle);
	}

	/**
	 * This method updates the cs Title
	 * 
	 * @param csId
	 * @param newShortTitle
	 */
	public synchronized void updateCSShortTitle(Long csId, String newShortTitle)
	{
		partiRegCache.updateCSShortTitle(csId, newShortTitle);
	}

	/**
	 * This mehod removes the cs from cache
	 * @param csId
	 */
	public synchronized void removeCS(Long csId)
	{
		partiRegCache.removeCS(csId);
	}

	/**
	 * this method add the registered participant in cache.
	 * @param csId
	 * @param participantID
	 */
	public synchronized void registerParticipant(Long csId, Long participantID, String protoPartID)
	{
		partiRegCache.registerParticipant(csId, participantID, protoPartID);
	}

	/**
	 * This method removes the participant from the cache
	 * @param csId
	 * @param participantID
	 */
	public synchronized void deRegisterParticipant(Long csId, Long participantID,
			String protoPartiID)
	{
		partiRegCache.deRegisterParticipant(csId, participantID, protoPartiID);
	}

	/**
	 * This method gets the participantCollection for particular Collection Protocol
	 * @param csId
	 * @return
	 */
	/*public List getParticipantCollection(Long csId)
	{
		return participantRegCache.getParticipantIDCollection(csId);
	}*/

	/**
	 * This method returns the cs ids and cs titles
	 * @return
	 */
	public List getCSDetailCollection()
	{
		return partiRegCache.getCSDetailCollection();
	}

	/**
	 * This method returns a list of cs ids and cs titles 
	 * from the participantRegistrationInfoList
	 * @return
	 */
	public Map<Long, String> getCSIDTitleMap()
	{
		return partiRegCache.getCSIDTitleMap();
	}

	/**
	 * This method returns the Participant information for particular Participant Id
	 * @param participantID
	 * @return
	 */
	public Participant getParticipantInfo(Long participantID)
	{
		return participantCahe.getParticipantInfo(participantID);
	}

	/**
	 * @param csId
	 * @return
	 */
	public List getParticipantNames(Long csId)
	{
		List partInfoList = partiRegCache.getParticipantInfoCollection(csId);
		List participantNames = participantCahe.getParticpantNamesWithID(partInfoList);
		return participantNames;
	}

	/**
	 * This method adds the participant in Map
	 * @param participant the object which is to be added in map
	 */
	public void addParticipant(Participant participant)
	{
		participantCahe.addParticipant(participant);
	}

}
