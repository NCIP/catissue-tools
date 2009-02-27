import java.util.ArrayList;
import java.util.List;


/**
 * 
 */

/**
 * @author ashish_gupta
 *
 */
public class SiteCacheDetails
{
	private Long siteId = null;
	private List<Long> containerIds = new ArrayList<Long>();
	private List<UserCacheDetails> userCacheDetails = new ArrayList<UserCacheDetails>();
	
	/**
	 * @return the containerIds
	 */
	public List<Long> getContainerIds()
	{
		return containerIds;
	}
	
	/**
	 * @param containerIds the containerIds to set
	 */
	public void setContainerIds(List<Long> containerIds)
	{
		this.containerIds = containerIds;
	}

	
	/**
	 * @return the userCacheDetails
	 */
	public List<UserCacheDetails> getUserCacheDetails()
	{
		return userCacheDetails;
	}

	
	/**
	 * @param userCacheDetails the userCacheDetails to set
	 */
	public void setUserCacheDetails(List<UserCacheDetails> userCacheDetails)
	{
		this.userCacheDetails = userCacheDetails;
	}

	
	/**
	 * @return the siteId
	 */
	public Long getSiteId()
	{
		return siteId;
	}

	
	/**
	 * @param siteId the siteId to set
	 */
	public void setSiteId(Long siteId)
	{
		this.siteId = siteId;
	}
	
	
	
	
}
