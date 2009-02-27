
package edu.wustl.catissuecore.cache.junit;

import java.util.List;

import junit.framework.TestCase;
import edu.wustl.catissuecore.cache.ContainerCache;
import edu.wustl.catissuecore.cache.ContainerCacheManagerImpl;
import edu.wustl.catissuecore.cache.ContainerPositionDetails;
import edu.wustl.catissuecore.cache.domaininterface.IContainerCacheManager;


/**
 * @author ashish_gupta
 *
 */
public class TestContainerCacheManager extends TestCase
{

	IContainerCacheManager containerCacheManager = new ContainerCacheManagerImpl();

	//	public void testLoadContainerCache()
	//	{
	//		
	//		
	//	}
	@Override
	protected void setUp() throws Exception
	{
		Long start = new Long(System.currentTimeMillis());
		containerCacheManager.loadCache();
		Long end = new Long(System.currentTimeMillis());
		System.out
				.println("Time required to populate container cache "
						+ "for 200 CPs, 50 users and 4 Specimen classes(ie 40K keys)and 2K containers for each key, is "
						+ (end - start) + "milliseconds");
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception
	{
		// TODO Auto-generated method stub
		super.tearDown();
	}

	/**
	 * 
	 */
	public void testAddPosition()
	{
		short xPos = 10;
		short yPos = 5;
		Long containerId = new Long(1);
		String containerName = "1";

		containerCacheManager.addPosition(containerId, xPos, yPos);

		boolean isPositionFree = containerCacheManager.isPositionFree(containerId, xPos, yPos);

		if (!isPositionFree)
		{
			fail();
		}

		//		containerCacheManager.addPosition(containerName, xPos , yPos);
		//		isPositionFree = containerCacheManager.isPositionFree(containerName, xPos, yPos);
		//		
		//		if(!isPositionFree)
		//		{
		//			fail();
		//		}
	}

	/**
	 * 
	 */
	public void testGetContainerCacheObjects()
	{

		//TODO add container cache objects
		Long cpId = 1L;
		Long userId = 1L;
		String spClass = "4";
		List<ContainerCache> cntCacheList = containerCacheManager.getContainerCacheObjects(cpId,
				userId, spClass);

		if (cntCacheList.size() == 0)
		{
			fail();
		}
	}

	/**
	 * 
	 */
	public void testIsPositionFree()
	{
		short xPos = 11;
		short yPos = 23;
		Long containerId = 18L;

		containerCacheManager.addPosition(containerId, xPos, yPos);

		boolean isPositionFree = containerCacheManager.isPositionFree(containerId, xPos, yPos);

		if (!isPositionFree)
		{
			fail();
		}
	}

	/**
	 * 
	 */
	public void testRemovePosition()
	{
		short xPos = 18;
		short yPos = 26;
		Long containerId = 10L;
		containerCacheManager.addPosition(containerId, xPos, yPos);

		containerCacheManager.removePosition(containerId, xPos, yPos);

		boolean isPositionFree = containerCacheManager.isPositionFree(containerId, xPos, yPos);
		if (isPositionFree)
		{
			fail();
		}
	}

	/**
	 * This test case gets the specimfied number of free positions from one or multiple containers 
	 */
	public void testGetContainerFreePositions()
	{
		Long cpId = 1L;
		Long userId = 1L;
		String spClass = "4";

		int count = 2;
		List<ContainerPositionDetails> containerPos = containerCacheManager
				.getContainerFreePositions(cpId, spClass, userId, count);

		if (containerPos.size() != 2)
		{
			fail();
		}

	}

	/**
	 * This test case gets the next free position
	 */
	public void testGetNextFreePosition()
	{
		Long cpId = 1L;
		Long userId = 1L;
		String spClass = "4";

		ContainerPositionDetails containerPos = containerCacheManager.getNextFreePosition(cpId,
				spClass, userId);

		if (containerPos == null)
		{
			fail();
		}
	}

}
