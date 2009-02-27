package edu.wustl.catissuecore.cache;


public class Position
{
short position1;
short position2;

/**
 * @return the position1
 */
	public short getPosition1()
	{
		return position1;
	}
	
	/**
	 * @param position1 the position1 to set
	 */
	public void setPosition1(short position1)
	{
		this.position1 = position1;
	}
	
	/**
	 * @return the position2
	 */
	public short getPosition2()
	{
		return position2;
	}
	
	/**
	 * @param position2 the position2 to set
	 */
	public void setPosition2(short position2)
	{
		this.position2 = position2;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		Position pos = (Position)obj;
		if(pos.getPosition1() == position1 && pos.getPosition2() == position2)
		{
			return true;
		}
		return false;
		
	}
	@Override
	public int hashCode()
	{
		
		return 1;
	}

}
