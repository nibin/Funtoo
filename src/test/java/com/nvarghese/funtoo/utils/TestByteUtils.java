package com.nvarghese.funtoo.utils;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.nvarghese.funtoo.utils.ByteUtils;


public class TestByteUtils {
	
	@Test
	public void testToBytes() {
		
		byte[] b = ByteUtils.toBytes("ba");		
		Assert.assertEquals(b[0], (byte)(-70));
		b = ByteUtils.toBytes("0f");
		Assert.assertEquals(b[0], (byte)(15));
		
	}

}

