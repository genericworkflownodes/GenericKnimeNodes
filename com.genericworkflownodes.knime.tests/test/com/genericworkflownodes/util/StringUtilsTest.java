/**
 * Copyright (c) 2012, Marc Röttig, Stephan Aiche.
 *
 * This file is part of GenericKnimeNodes.
 * 
 * GenericKnimeNodes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.genericworkflownodes.util;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import com.genericworkflownodes.util.ranges.DoubleRangeExtractor;
import com.genericworkflownodes.util.ranges.IntegerRangeExtractor;


;

/**
 * Test for util collection {@link StringUtils}.
 * 
 * @author aiche
 */
public class StringUtilsTest {

	@Test
	public void testJoin() {
		assertEquals("a,b,c,d",
				StringUtils.join(Arrays.asList("a", "b", "c", "d"), ","));
		assertEquals("a;b;c;d",
				StringUtils.join(Arrays.asList("a", "b", "c", "d"), ";"));

	}

	@Test
	public void testLowerBoundExtractionDouble() {
		assertEquals(new Double(2.0),
				new DoubleRangeExtractor().getLowerBound("2.0:100"));
		assertEquals(new Double(2.0),
				new DoubleRangeExtractor().getLowerBound("2.0:"));
		assertEquals(new Double(2.0),
				new DoubleRangeExtractor().getLowerBound("2.0"));

		assertEquals(new Double(Double.NEGATIVE_INFINITY),
				new DoubleRangeExtractor().getLowerBound(":100"));
		assertEquals(new Double(Double.NEGATIVE_INFINITY),
				new DoubleRangeExtractor().getLowerBound(":"));
		assertEquals(new Double(Double.NEGATIVE_INFINITY),
				new DoubleRangeExtractor().getLowerBound(""));
	}

	@Test
	public void testLowerBoundExtractionInteger() {
		assertEquals(new Integer(2),
				new IntegerRangeExtractor().getLowerBound("2:100"));
		assertEquals(new Integer(2),
				new IntegerRangeExtractor().getLowerBound("2:"));
		assertEquals(new Integer(2),
				new IntegerRangeExtractor().getLowerBound("2"));

		assertEquals(new Integer(Integer.MIN_VALUE),
				new IntegerRangeExtractor().getLowerBound(":100"));
		assertEquals(new Integer(Integer.MIN_VALUE),
				new IntegerRangeExtractor().getLowerBound(":"));
		assertEquals(new Integer(Integer.MIN_VALUE),
				new IntegerRangeExtractor().getLowerBound(""));
	}

	@Test
	public void testUpperBoundExtractionDouble() {
		assertEquals(new Double(2.0),
				new DoubleRangeExtractor().getUpperBound("2.0:2.0"));
		assertEquals(new Double(2.0),
				new DoubleRangeExtractor().getUpperBound(":2.0"));

		assertEquals(new Double(Double.POSITIVE_INFINITY),
				new DoubleRangeExtractor().getUpperBound("100:"));
		assertEquals(new Double(Double.POSITIVE_INFINITY),
				new DoubleRangeExtractor().getUpperBound(":"));
	}

	@Test
	public void testUpperBoundExtractionInteger() {
		assertEquals(new Integer(2),
				new IntegerRangeExtractor().getUpperBound("2:2"));
		assertEquals(new Integer(2),
				new IntegerRangeExtractor().getUpperBound(":2"));

		assertEquals(new Integer(Integer.MAX_VALUE),
				new IntegerRangeExtractor().getUpperBound("100:"));
		assertEquals(new Integer(Integer.MAX_VALUE),
				new IntegerRangeExtractor().getUpperBound(":"));
	}
}
