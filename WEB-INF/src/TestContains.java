

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import java.util.List;
import java.util.Map;

import edu.wustl.catissuecore.cache.ContainerCache;
import edu.wustl.catissuecore.cache.ContainerCacheKey;
import edu.wustl.catissuecore.cache.Position;





public class TestContains
{
//	private static Map<Long, SiteCacheDetails> siteIdsVsSiteDetails = null;
//	private static Map<Long, UserCacheDetails> userIdsVsSiteDetails = null;
	public static void main(String[] args)
	{
		//isPositionPresent();
		//populateMap();
		//populateMapUsingApproach2();
		populateContainerCacheMap();
	}

	/**
	 * @return
	 */
	private static boolean isPositionPresent()
	{
		ContainerCache cc = new ContainerCache();

		Position pos = new Position();
		
		short position1 = 3;
		short position2 = 4;
		pos.setPosition1(position1);
		pos.setPosition2(position2);

		cc.addPosition(position1, position2);

		Collection<Position> avaiPosColl = cc.getFreePositions();
		System.out.println("Is Position available : " + avaiPosColl.contains(pos));
		return avaiPosColl.contains(pos);
	}
	
	/**
	 * 
	 */
	private static void populateMap()
	{
		Long start = new Long(System.currentTimeMillis());
		
		Map<Long, List<Long>> userIdsVsContainerIds = new HashMap<Long, List<Long>>(10000);
		Map<Long, List<Long>> siteIdsVsContainerIds = new HashMap<Long, List<Long>>(10000);
		
		List<Long> containerIds = new ArrayList<Long>();
		for(int i=1; i<=5000; i++)
		{
			containerIds.add(new Long(i));
		}
		for(int i=1; i<=10; i++)
		{
			userIdsVsContainerIds.put(new Long(i), containerIds);
		}
		Long end = new Long(System.currentTimeMillis());
		System.out.println("Time required to populate userIdsVsContainerIds " +
				"for 10 users and 5K containers per user is " + (end - start) 
				+ "milliseconds");
		start = new Long(System.currentTimeMillis());
		
		userIdsVsContainerIds.get(new Long(9));
		end = new Long(System.currentTimeMillis());
		System.out.println("Time required to RETRIEVE ContainerIds " +
				(end - start) 
				+ "milliseconds" );		
	}
	/**
	 * 
	 */
//	private static void populateMapUsingApproach2()
//	{		
//		siteIdsVsSiteDetails = new HashMap<Long, SiteCacheDetails>(10000);
//		userIdsVsSiteDetails = new HashMap<Long, UserCacheDetails>(10000);
//		
//		List<Long> containerIds = new ArrayList<Long>();
//		for(int i=1; i<=5000; i++)
//		{
//			containerIds.add(new Long(i));
//		}
//		for(int i=1; i<=10; i++)
//		{
//			SiteCacheDetails site = new SiteCacheDetails();
//			site.setContainerIds(containerIds);
//			site.setSiteId(new Long(i));
//			
//			List<UserCacheDetails> userList = new ArrayList<UserCacheDetails>();
//			for(int j=1; j<=15; j++)
//			{
//				UserCacheDetails user = new UserCacheDetails();
//				user.setContainerIds(containerIds);
//				user.setUserId(new Long(j));
//				userList.add(user);
//				userIdsVsSiteDetails.put(new Long(j), user);
//			}
//			site.setUserCacheDetails(userList);			
//			siteIdsVsSiteDetails.put(new Long(i), site);			
//		}
//		Long start = new Long(System.currentTimeMillis());
//		
//		containerIds= getContainerIdsForSite(9);
//		Long end = new Long(System.currentTimeMillis());
//		System.out.println("Time required to RETRIEVE ContainerIds given siteId = " +
//				(end - start) 
//				+ "milliseconds" );		
//	}
//	/**
//	 * @param siteId
//	 */
//	private static List<Long> getContainerIdsForSite(int siteId)
//	{
//		SiteCacheDetails site = siteIdsVsSiteDetails.get(new Long(siteId));
//		return site.getContainerIds();
//	}

	/**
	 * @param cpId
	 * @param specimenClass
	 * @param userId
	 * @return
	 */
	private static ContainerCacheKey getContainerCacheKey(Long cpId, String specimenClass, Long userId)
	{
		ContainerCacheKey key = new ContainerCacheKey();
		key.setCpId(cpId);
		key.setSpecimenClass(specimenClass);
		key.setUserId(userId);
		
		return key;
	}
	/**
	 * 
	 */
	private static void populateContainerCacheMap()
	{
		Long start = new Long(System.currentTimeMillis());
		
		Map<ContainerCacheKey,List<ContainerCache>> containerCacheMap = new HashMap<ContainerCacheKey,List<ContainerCache>>(20);
		List<ContainerCache> cntList = new ArrayList<ContainerCache>();
		
		for(short i=1; i<=2000; i++)
		{
			ContainerCache cc = new ContainerCache();
			Position pos = new Position();
			pos.setPosition1(i);
			pos.setPosition2(i);		

			List<Position> avaiPosColl = new ArrayList<Position>();
			avaiPosColl.add(pos);
			cc.setFreePositions(avaiPosColl);
			
			cntList.add(cc);
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
					containerCacheMap.put(getContainerCacheKey(CPId,spClass,userID),cntList);					
				}
			}
		}
		Long end = new Long(System.currentTimeMillis());
		System.out.println("Time required to populate populateContainerCacheMap = " +
				(end - start) 
				+ "milliseconds" );	
		
		List<ContainerCache> ccList = containerCacheMap.get(getContainerCacheKey(199L,"4",49L));
//		ccList.get(index)
	}
	
}
