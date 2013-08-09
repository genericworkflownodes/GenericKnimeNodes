/**
 * 
 */
package com.genericworkflownodes.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class HelperTest {

	@Test
	public void testArray2dcopy() {
		String[][] src = new String[][] { { "a", "b" }, { "c" } };
		String[][] dst = new String[src.length][];

		Helper.array2dcopy(src, dst);

		for (int i = 0; i < src.length; ++i) {
			assertTrue(src[i].length == dst[i].length);
			assertArrayEquals(src[i], dst[i]);
		}
	}

}
