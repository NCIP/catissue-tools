import java.util.ArrayList;
import java.util.List;


/**
 * 
 */

/**
 * @author ashish_gupta
 *
 */
public class UserCacheDetails
{
	private Long userId = null;
	private List<Long> containerIds = new ArrayList<Long>();
	
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

}
