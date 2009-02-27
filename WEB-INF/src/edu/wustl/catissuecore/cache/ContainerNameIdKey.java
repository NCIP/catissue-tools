package edu.wustl.catissuecore.cache;

/**
 * 
 */

/**
 * @author ashish_gupta
 *
 */
public class ContainerNameIdKey
{
	public ContainerNameIdKey(Long containerId,	String containerName)
	{
		this.containerId = containerId;
		this.containerName = containerName;
	}
	
	Long containerId;
	String containerName;
	
	/**
	 * @return the containerId
	 */
	public Long getContainerId()
	{
		return containerId;
	}
	
	/**
	 * @param containerId the containerId to set
	 */
	public void setContainerId(Long containerId)
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return 1;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		ContainerNameIdKey key = (ContainerNameIdKey)obj;
		if((key.getContainerId() != null && (key.getContainerId().intValue() == containerId.intValue() || (key.getContainerName() != null && key.getContainerName().equalsIgnoreCase(containerName)))) 
				|| (key.getContainerName() != null && key.getContainerName().equalsIgnoreCase(containerName)))
		{
			return true;
		}
		return false;
	}
	
}
