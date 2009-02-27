package edu.wustl.catissuecore.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.wustl.catissuecore.cache.domaininterface.IContainerCacheManager;
import edu.wustl.catissuecore.cache.util.ContainerCacheUtility;



/**
 * @author ashish_gupta
 *
 */
public class ContainerCacheManagerImpl implements IContainerCacheManager
{
	Map<ContainerNameIdKey, ContainerCache> cntCacheNameIdKeyVScntCacheMasterMap = new HashMap<ContainerNameIdKey, ContainerCache>(2000);
	Map<ContainerCacheKey, List<ContainerCache>> cntCacheMap = new HashMap<ContainerCacheKey, List<ContainerCache>>(40000);
	
	/**
	 * This method adds a freed up position to the master map for the given container id.
	 */	
	public void addPosition(Long containerId, short xPosition, short yPosition)
	{		
		ContainerNameIdKey nameIdkey = new ContainerNameIdKey(containerId, null);
		ContainerCache containerCache = cntCacheNameIdKeyVScntCacheMasterMap.get(nameIdkey);
		if(containerCache != null)
		{
			containerCache.addPosition(xPosition, yPosition);
		}
	}
	/**
	 * This method adds a freed up position to the master map for the given container name.
	 */
	public void addPosition(String containerName, short xPosition, short yPosition)
	{
		ContainerNameIdKey nameIdkey = new ContainerNameIdKey(null, containerName);
		ContainerCache containerCache = cntCacheNameIdKeyVScntCacheMasterMap.get(nameIdkey);
		if(containerCache != null)
		{
			containerCache.addPosition(xPosition, yPosition);
		}		
	}
	
	/**
	 * This method returns the ContainerCache object for the given key
	 */
	public List<ContainerCache> getContainerCacheObjects(Long cpId, Long userId, String spClass)
	{
		ContainerCacheKey containerCacheKey = ContainerCacheUtility.getContainerCacheKey(cpId,userId,spClass);
		List<ContainerCache> containerCacheList = cntCacheMap.get(containerCacheKey);
		return containerCacheList;
	}
	
	/**
	 * This method returns true if the given position is present in the container cache i.e. it is free
	 */
	public boolean isPositionFree(Long containerId, short xPosition, short yPosition)
	{
		Position pos = ContainerCacheUtility.getPosition(xPosition, yPosition);
		ContainerNameIdKey nameIdKey = new ContainerNameIdKey(containerId, null);
		ContainerCache containerCache = cntCacheNameIdKeyVScntCacheMasterMap.get(nameIdKey);
		Collection<Position> freePositions = containerCache.getFreePositions(); 
		boolean isPositionFree = freePositions.contains(pos);
		
		return isPositionFree;
	}
	
	/**
	 * This method returns true if the given position is present in the container cache i.e. it is free
	 */
	public boolean isPositionFree(String conatinerName, short xPosition, short yPosition)
	{
		Position pos = ContainerCacheUtility.getPosition(xPosition, yPosition);
		ContainerNameIdKey nameIdKey = new ContainerNameIdKey(null, conatinerName);
		ContainerCache containerCache = cntCacheNameIdKeyVScntCacheMasterMap.get(nameIdKey);
		Collection<Position> freePositions = containerCache.getFreePositions(); 
		boolean isPositionFree = freePositions.contains(pos);
		
		return isPositionFree;
	}
	/**
	 * This method loads the container cache from the db.
	 */
	public void loadCache()
	{
		/* CP                 - 200
		 * Users              - 50
		 * SpecimenClass      - 4
		 * Containers per key = 2000
		 */ 
		//For 1st map
		List<ContainerCache> cntList = new ArrayList<ContainerCache>();
		
		for(short i=1; i<=2000; i++)
		{
			ContainerCache cc = new ContainerCache();
			
			ContainerNameIdKey nameIdKey = new ContainerNameIdKey(new Long(i), ""+i);
			
			
			cc.setContainerNameIdKey(nameIdKey);
			
			Position pos = new Position();
			pos.setPosition1(i);
			pos.setPosition2(i);		

			List<Position> avaiPosColl = new ArrayList<Position>();
			avaiPosColl.add(pos);
			
			cc.setFreePositions(avaiPosColl);			
			
			cntList.add(cc);
			
			//For second map	
			
			cntCacheNameIdKeyVScntCacheMasterMap.put(nameIdKey, cc);
		}
		
		
		for(int cpId = 1; cpId <= 200; cpId++)
		{
			for(int userId = 1; userId <= 50; userId++)
			{
				for(int specimenClass = 1; specimenClass <= 4; specimenClass ++)
				{
					String spClass = ""+ specimenClass;
					Long userID = new Long(userId);
					Long CPId = new Long(cpId);
					cntCacheMap.put(ContainerCacheUtility.getContainerCacheKey(CPId,userID,spClass),cntList);					
				}
			}
		}
	
	}
	/**
	 * This method removes a position for the given container id.
	 */
	public void removePosition(Long containerId, short xPosition, short yPosition)
	{
		ContainerNameIdKey nameIdkey = new ContainerNameIdKey(containerId, null);
		ContainerCache containerCache = cntCacheNameIdKeyVScntCacheMasterMap.get(nameIdkey);
		if(containerCache != null)
		{
			containerCache.removePosition(xPosition, yPosition);
		}
		
	}
	/**
	 * This method removes a position for the given container name.
	 */
	public void removePosition(String containerName, short xPosition, short yPosition)
	{
		ContainerNameIdKey nameIdkey = new ContainerNameIdKey(null, containerName);
		ContainerCache containerCache = cntCacheNameIdKeyVScntCacheMasterMap.get(nameIdkey);
		if(containerCache != null)
		{
			containerCache.removePosition(xPosition, yPosition);
		}
		
	}
	/**
	 * This method returns the requested number of free positions from one or multiple containers.
	 */
	public List<ContainerPositionDetails> getContainerFreePositions(Long cpId, String specimenClass, Long userId, int count)
	{
		ContainerCacheKey containerCacheKey = ContainerCacheUtility.getContainerCacheKey(cpId,userId,specimenClass);
		List<ContainerCache> containerCacheList = cntCacheMap.get(containerCacheKey);
				
		List<ContainerPositionDetails> containerPositionDetailsList = new ArrayList<ContainerPositionDetails>();
		
		int posCounter = count;
		for(int i=0; i < containerCacheList.size(); i++)
		{
			ContainerCache containerCache = containerCacheList.get(i);
			List<Position> freePositions = containerCache.getFreePositions();
			
			for(int j = 0; j < freePositions.size(); j++)
			{
				if(posCounter != 0)
				{
					Position position = freePositions.get(j);
					
					ContainerNameIdKey containerNameIdKey = containerCache.getContainerNameIdKey();
									
					ContainerPositionDetails containerPositionDetails = ContainerCacheUtility.getContainerPositionDetails(position, containerNameIdKey);
					
					containerPositionDetailsList.add(containerPositionDetails);
					posCounter--;
				}
			}			
		}
		
		return containerPositionDetailsList;
	}
	/**
	 *This method returns the next free position for the given key from the cache 
	 */
	public ContainerPositionDetails getNextFreePosition(Long cpId, String specimenClass, Long userId)
	{
		ContainerCacheKey containerCacheKey = ContainerCacheUtility.getContainerCacheKey(cpId,userId,specimenClass);
		List<ContainerCache> containerCacheList = cntCacheMap.get(containerCacheKey);
		
		for(ContainerCache containerCache : containerCacheList)
		{
			if(containerCache.getFreePositions().size() > 0)
			{
				Position pos = containerCache.getFreePositions().get(0);
				
				ContainerPositionDetails containerPositionDetails = ContainerCacheUtility.getContainerPositionDetails(pos, containerCache.getContainerNameIdKey());
				
				return containerPositionDetails;
			}
		}
		return null;
	}
	
//	public void addContainerToCache(Long cpId, Long userId, String spClass, List<ContainerPositionDetails> containerPositionDetailsList)
//	{
//		ContainerCacheKey containerCacheKey = ContainerCacheUtility.getContainerCacheKey(cpId,userId,spClass);
//		List<ContainerCache> containerCacheList = cntCacheMap.get(containerCacheKey);
//		
//		ContainerCache 
//		containerCacheList.add(o)
//	}


}
