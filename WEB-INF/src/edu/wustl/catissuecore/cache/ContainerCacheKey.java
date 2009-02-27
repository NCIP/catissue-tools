package edu.wustl.catissuecore.cache;

/**
 * 
 */

/**
 * @author ashish_gupta
 *
 */
public class ContainerCacheKey
{
	Long userId;
	String specimenClass;
	Long cpId;
	
	/**
	 * @return the cpId
	 */
	public Long getCpId()
	{
		return cpId;
	}
	
	/**
	 * @param cpId the cpId to set
	 */
	public void setCpId(Long cpId)
	{
		this.cpId = cpId;
	}
	
	/**
	 * @return the specimenClass
	 */
	public String getSpecimenClass()
	{
		return specimenClass;
	}
	
	/**
	 * @param specimenClass the specimenClass to set
	 */
	public void setSpecimenClass(String specimenClass)
	{
		this.specimenClass = specimenClass;
	}
	
	/**
	 * @return the userId
	 */
	public Long getUserId()
	{
		return userId;
	}
	
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(Long userId)
	{
		this.userId = userId;
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
		ContainerCacheKey key = (ContainerCacheKey)obj;
		if(key.getCpId().intValue() == cpId.intValue()
				&& key.getSpecimenClass().equalsIgnoreCase(specimenClass)
				&& key.getUserId().intValue() == userId.intValue())
		{
			return true;
		}
		return false;
	}
	
}
