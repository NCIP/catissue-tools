package edu.wustl.catissuecore.cache;

import java.util.List;



public class ContainerCache
{
	List<Position> freePositions;
	
	ContainerNameIdKey containerNameIdKey;
	
	
	
	
	/**
	 * @return the containerNameIdKey
	 */
	public ContainerNameIdKey getContainerNameIdKey()
	{
		return containerNameIdKey;
	}


	
	/**
	 * @param containerNameIdKey the containerNameIdKey to set
	 */
	public void setContainerNameIdKey(ContainerNameIdKey containerNameIdKey)
	{
		this.containerNameIdKey = containerNameIdKey;
	}


	/**
	 * @return the availablePos
	 */
	public List<Position> getFreePositions()
	{
		return freePositions;
	}

	
	/**
	 * @param availablePos the availablePos to set
	 */
	public void setFreePositions(List<Position> freePositions)
	{
		this.freePositions = freePositions;
	}
	
	public void addPosition(short pos1,short pos2)
	{
		Position pos = new Position();
		pos.setPosition1(pos1);
		pos.setPosition2(pos2);
		
		freePositions.add(pos);
	}
	public void removePosition(short pos1, short pos2)
	{
		Position pos = new Position();
		pos.setPosition1(pos1);
		pos.setPosition2(pos2);
		
		freePositions.remove(pos);
	}
}
