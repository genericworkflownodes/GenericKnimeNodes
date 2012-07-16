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
 * Test for {@link IntegerListParameter}.
 * 
 * @author aiche
 */
public class IntegerListParameterTest {

	List<Integer> ints = Arrays.asList(2, 3, 4);

	@Test
	public void testGetMnemonic() {
		IntegerListParameter dlp = new IntegerListParameter("ilp", ints);
		assertEquals(dlp.getMnemonic(), "integer list [-inf:+inf]");
		dlp.setLowerBound(-3);
		assertEquals(dlp.getMnemonic(), "integer list [-3:+inf]");
		dlp.setUpperBound(3);
		assertEquals(dlp.getMnemonic(), "integer list [-3:3]");
	}

	@Test
	public void testFillFromString() throws InvalidParameterValueException {
		IntegerListParameter dlp = new IntegerListParameter("ilp", ints);
		dlp.fillFromString("-19@@@__@@@16@@@__@@@44@@@__@@@");
		assertEquals(3, dlp.getValue().size());
		assertEquals(new Integer(-19), dlp.getValue().get(0));
		assertEquals(new Integer(16), dlp.getValue().get(1));
		assertEquals(new Integer(44), dlp.getValue().get(2));
	}

	@Test(expected = InvalidParameterValueException.class)
	public void testFillFromInvalidString()
			throws InvalidParameterValueException {
		IntegerListParameter dlp = new IntegerListParameter("ilp", ints);
		dlp.fillFromString("-19aa.2@@@__@@@16@@@__@@@44@@@__@@@");
	}

	@Test
	public void testGetStringRep() {
		IntegerListParameter dlp = new IntegerListParameter("ilp", ints);
		assertEquals("2@@@__@@@3@@@__@@@4@@@__@@@", dlp.getStringRep());
	}

	@Test
	public void testIntegerListParameter() {
		IntegerListParameter dlp = new IntegerListParameter("ilp", ints);
		assertEquals(3, dlp.getValue().size());
		assertEquals(new Integer(2), dlp.getValue().get(0));
		assertEquals(new Integer(3), dlp.getValue().get(1));
		assertEquals(new Integer(4), dlp.getValue().get(2));
	}

	@Test
	public void testValidateListOfInteger() {
		IntegerListParameter dlpNull = new IntegerListParameter("ilp", null);
		assertEquals(true, dlpNull.validate(null));

		IntegerListParameter dlp = new IntegerListParameter("ilp", ints);
		List<Integer> moreIntegers = Arrays.asList(-1, 2, 5);
		assertEquals(true, dlp.validate(moreIntegers));

		dlp.setLowerBound(0);
		assertEquals(false, dlp.validate(moreIntegers));
		dlp.setLowerBound(Integer.MIN_VALUE);
		assertEquals(true, dlp.validate(moreIntegers));

		dlp.setUpperBound(4);
		assertEquals(false, dlp.validate(moreIntegers));
		dlp.setUpperBound(Integer.MAX_VALUE);
		assertEquals(true, dlp.validate(moreIntegers));
	}

	@Test
	public void testGetStrings() {
		IntegerListParameter dlp = new IntegerListParameter("ilp", ints);
		List<String> strings = dlp.getStrings();
		assertEquals(3, strings.size());
		assertEquals("2", strings.get(0));
		assertEquals("3", strings.get(1));
		assertEquals("4", strings.get(2));
	}

	@Test
	public void testFillFromStrings() {
		IntegerListParameter dlp = new IntegerListParameter("ilp", null);
		String[] strings = { "2", "3", "5" };
		dlp.fillFromStrings(strings);
		assertEquals(3, dlp.getValue().size());
		assertEquals(new Integer(2), dlp.getValue().get(0));
		assertEquals(new Integer(3), dlp.getValue().get(1));
		assertEquals(new Integer(5), dlp.getValue().get(2));
	}

	@Test
	public void testToString() {
		IntegerListParameter dlp = new IntegerListParameter("ilp", ints);
		assertEquals("[2, 3, 4]", dlp.toString());
	}

}
