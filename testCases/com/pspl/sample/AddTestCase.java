/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */


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


