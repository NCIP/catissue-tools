package edu.wustl.catissuecore.cache;
import edu.wustl.catissuecore.cache.domaininterface.IContainerPositionDetails;



/**
 * @author ashish_gupta
 *
 */
public class ContainerPositionDetails implements IContainerPositionDetails
{
	String containerName;
	long containerId; 
		
	//We are keeping x and y positions instead of List<Position> so as to avoid iteration in bizlogic calling the cache.
	short xPosition;
	short yPosition;
	
	/**
	 * @return the containerId
	 */
	public long getContainerId()
	{
		return containerId;
	}
	
	/**
	 * @param containerId the containerId to set
	 */
	public void setContainerId(long containerId)
	{
		this.containerId = containerId;
	}
	
	/**
	 * @return the containerName
	 */
	public String getContainerName()
	{
		return containerName;
	}
	
	/**
	 * @param containerName the containerName to set
	 */
	public void setContainerName(String containerName)
	{
		this.containerName = containerName;
	}
	
	/**
	 * @return the xPosition
	 */
	public short getXPosition()
	{
		return xPosition;
	}
	
	/**
	 * @param position the xPosition to set
	 */
	public void setXPosition(short position)
	{
		xPosition = position;
	}
	
	/**
	 * @return the yPosition
	 */
	public short getYPosition()
	{
		return yPosition;
	}
	
	/**
	 * @param position the yPosition to set
	 */
	public void setYPosition(short position)
	{
		yPosition = position;
	}
	
	
	
	
	
}
