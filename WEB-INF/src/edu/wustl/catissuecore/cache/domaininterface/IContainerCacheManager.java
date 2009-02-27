/**
 * <p>Title: IContainerCacheManager Interface>
 * <p>Description:  An interface for Container Cache management</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author ashish_gupta
 * @version 1.00
 */
package edu.wustl.catissuecore.cache.domaininterface;
import java.util.List;

import edu.wustl.catissuecore.cache.ContainerCache;
import edu.wustl.catissuecore.cache.ContainerPositionDetails;


public interface IContainerCacheManager
{

	/**
	 * @param containerId - The container identifier to which position is to be added.
	 * @param xPosition - The x- position to be added
	 * @param yPosition - The y - position to be added
	 */
	public void addPosition(Long containerId, short xPosition, short yPosition);

	/**
	 * @param containerName - The container name to which position is to be added.
	 * @param xPosition - The x- position to be added
	 * @param yPosition - The y - position to be added
	 */
	public void addPosition(String containerName, short xPosition, short yPosition);

	/**
	 * @param cpId
	 * @param specimenClass
	 * @param userId
	 * @param count
	 * @return
	 */
	public List<ContainerPositionDetails> getContainerFreePositions(Long cpId, String specimenClass, Long userId,
			int count);

	/**
	 * @return
	 */
	public ContainerPositionDetails getNextFreePosition(Long cpId, String specimenClass, Long userId);

	/**
	 * @param cpId
	 * @param userId
	 * @param spClass
	 * @return
	 */
	public List<ContainerCache> getContainerCacheObjects(Long cpId, Long userId, String spClass);

	/**
	 * @param containerId
	 * @param xPosition
	 * @param yPosition
	 */
	public void removePosition(Long containerId, short xPosition, short yPosition);

	/**
	 * @param containerName
	 * @param xPosition
	 * @param yPosition
	 */
	public void removePosition(String containerName, short xPosition, short yPosition);

	/**
	 * @param conatinerId
	 * @param xPosition
	 * @param yPosition
	 * @return
	 */
	public boolean isPositionFree(Long conatinerId, short xPosition, short yPosition);

	/**
	 * @param conatinerName
	 * @param xPosition
	 * @param yPosition
	 * @return
	 */
	public boolean isPositionFree(String conatinerName, short xPosition, short yPosition);

	/**
	 * 
	 */
	public void loadCache();
	/**
	 * 
	 */
//	public void addContainerToCache(Long cpId, Long userId, String spClass, List<ContainerPositionDetails> containerPositionDetailsList);
	
	
	
}
