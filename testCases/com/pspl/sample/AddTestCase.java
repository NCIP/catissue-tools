
package com.pspl.sample;

import com.pspl.sample.Add;

import junit.framework.TestCase;
import junit.framework.Assert;

public class AddTestCase extends TestCase
{

	public void testAdd()
	{
		int a = 4;
		int b = 5;
		Add add = new Add();
		Assert.assertEquals(9, add.add(4, 5));

	}

}


