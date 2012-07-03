/**
 * Copyright (c) 2012, Stephan Aiche.
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
package com.genericworkflownodes.knime.parameter;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * Test for {@link BoolParameter}.
 * 
 * @author aiche
 */
public class DoubleListParameterTest {

	List<Double> doubles = Arrays.asList(2.0, 3.0, 4.0);

	@Test
	public void testGetMnemonic() {
		DoubleListParameter dlp = new DoubleListParameter("dlp", doubles);
		assertEquals(dlp.getMnemonic(), "double list [-inf:+inf]");
		dlp.setLowerBound(-3.0);
		assertEquals(dlp.getMnemonic(), "double list [-3.000000:+inf]");
		dlp.setUpperBound(3.0);
		assertEquals(dlp.getMnemonic(), "double list [-3.000000:3.000000]");
	}

	@Test
	public void testFillFromString() throws InvalidParameterValueException {
		DoubleListParameter dlp = new DoubleListParameter("dlp", doubles);
		dlp.fillFromString("-19.2@@@__@@@16@@@__@@@44@@@__@@@");
		assertEquals(3, dlp.getValue().size());
		assertEquals(new Double(-19.2), dlp.getValue().get(0));
		assertEquals(new Double(16), dlp.getValue().get(1));
		assertEquals(new Double(44), dlp.getValue().get(2));
	}

	@Test(expected = InvalidParameterValueException.class)
	public void testFillFromInvalidString()
			throws InvalidParameterValueException {
		DoubleListParameter dlp = new DoubleListParameter("dlp", doubles);
		dlp.fillFromString("-19aa.2@@@__@@@16@@@__@@@44@@@__@@@");
	}

	@Test
	public void testGetStringRep() {
		DoubleListParameter dlp = new DoubleListParameter("dlp", doubles);
		assertEquals("2.000000@@@__@@@3.000000@@@__@@@4.000000@@@__@@@",
				dlp.getStringRep());
	}

	@Test
	public void testDoubleListParameter() {
		DoubleListParameter dlp = new DoubleListParameter("dlp", doubles);
		assertEquals(3, dlp.getValue().size());
		assertEquals(new Double(2.0), dlp.getValue().get(0));
		assertEquals(new Double(3.0), dlp.getValue().get(1));
		assertEquals(new Double(4.0), dlp.getValue().get(2));
	}

	@Test
	public void testValidateListOfDouble() {
		DoubleListParameter dlpNull = new DoubleListParameter("dlp", null);
		assertEquals(true, dlpNull.validate(null));

		DoubleListParameter dlp = new DoubleListParameter("dlp", doubles);
		List<Double> moreDoubles = Arrays.asList(-1.0, 2.0, 5.0);
		assertEquals(true, dlp.validate(moreDoubles));

		dlp.setLowerBound(0.0);
		assertEquals(false, dlp.validate(moreDoubles));
		dlp.setLowerBound(Double.NEGATIVE_INFINITY);
		assertEquals(true, dlp.validate(moreDoubles));

		dlp.setUpperBound(4.5);
		assertEquals(false, dlp.validate(moreDoubles));
		dlp.setUpperBound(Double.POSITIVE_INFINITY);
		assertEquals(true, dlp.validate(moreDoubles));
	}

	@Test
	public void testGetStrings() {
		DoubleListParameter dlp = new DoubleListParameter("dlp", doubles);
		List<String> strings = dlp.getStrings();
		assertEquals(3, strings.size());
		assertEquals("2.000000", strings.get(0));
		assertEquals("3.000000", strings.get(1));
		assertEquals("4.000000", strings.get(2));
	}

	@Test
	public void testFillFromStrings() {
		DoubleListParameter dlp = new DoubleListParameter("dlp", null);
		String[] strings = { "2.0", "3.0", "5.0" };
		dlp.fillFromStrings(strings);
		assertEquals(3, dlp.getValue().size());
		assertEquals(new Double(2.0), dlp.getValue().get(0));
		assertEquals(new Double(3.0), dlp.getValue().get(1));
		assertEquals(new Double(5.0), dlp.getValue().get(2));
	}

	@Test
	public void testToString() {
		DoubleListParameter dlp = new DoubleListParameter("dlp", doubles);
		assertEquals("[2.0, 3.0, 4.0]", dlp.toString());
	}

}
