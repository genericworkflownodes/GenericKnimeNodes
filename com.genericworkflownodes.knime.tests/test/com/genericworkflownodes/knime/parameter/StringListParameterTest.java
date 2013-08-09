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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * Test for {@link StringListParameter}.
 * 
 * @author aiche
 */
public class StringListParameterTest {

	private static final String VALUE_STRING = "this-is-a-single-value@@@__@@@other-value@@@__@@@third-value";
	String key = "slp-key";
	String valueString = "";

	@Test
	public void testGetMnemonic() {
		StringListParameter slp = new StringListParameter(key, null);
		assertEquals("string list", slp.getMnemonic());
	}

	@Test
	public void testFillFromString() throws InvalidParameterValueException {
		StringListParameter slp = new StringListParameter(key, null);
		slp.fillFromString("");
		assertEquals(0, slp.getValue().size());
		slp.fillFromString(null);
		assertEquals(0, slp.getValue().size());
		slp.fillFromString("this-is-a-single-value");
		assertEquals(1, slp.getValue().size());
		assertEquals("this-is-a-single-value", slp.getValue().get(0));

		slp.fillFromString(VALUE_STRING);
		assertEquals(3, slp.getValue().size());
		assertEquals("this-is-a-single-value", slp.getValue().get(0));
		assertEquals("other-value", slp.getValue().get(1));
		assertEquals("third-value", slp.getValue().get(2));
	}

	@Test
	public void testGetStringRep() throws InvalidParameterValueException {
		StringListParameter slp = new StringListParameter(key, null);
		assertEquals("", slp.getStringRep());
		slp.fillFromString(VALUE_STRING);
		assertEquals(
				"this-is-a-single-value@@@__@@@other-value@@@__@@@third-value@@@__@@@",
				slp.getStringRep());
	}

	@Test
	public void testStringListParameter() {
		StringListParameter slp = new StringListParameter(key, null);
		assertEquals(key, slp.getKey());
		assertNull(slp.getValue());

		slp = new StringListParameter(key, new ArrayList<String>());
		assertEquals(key, slp.getKey());
		assertNotNull(slp.getValue());
	}

	@Test
	public void testValidateListOfString() {
		StringListParameter slp = new StringListParameter(key, null);
		assertTrue(slp.validate(null));
		assertTrue(slp.validate(new ArrayList<String>()));
	}

	@Test
	public void testToString() throws InvalidParameterValueException {
		StringListParameter slp = new StringListParameter(key, null);
		slp.fillFromString(VALUE_STRING);
		assertEquals("[this-is-a-single-value, other-value, third-value]",
				slp.toString());
	}

	@Test
	public void testGetStrings() throws InvalidParameterValueException {
		StringListParameter slp = new StringListParameter(key, null);
		slp.fillFromString(VALUE_STRING);
		List<String> strings = slp.getStrings();
		assertEquals(3, strings.size());
		assertEquals("this-is-a-single-value", strings.get(0));
		assertEquals("other-value", strings.get(1));
		assertEquals("third-value", strings.get(2));
	}

	@Test
	public void testFillFromStrings() throws InvalidParameterValueException {
		StringListParameter slp = new StringListParameter(key, null);
		String[] stringsToInclude = new String[] { "a", "b", "c" };
		slp.fillFromStrings(stringsToInclude);
		assertEquals(3, slp.getValue().size());
		assertEquals("a", slp.getValue().get(0));
		assertEquals("b", slp.getValue().get(1));
		assertEquals("c", slp.getValue().get(2));
	}

	@Test
	public void testGetSetRestrictions() {
		StringListParameter slp = new StringListParameter(key, null);
		List<String> restrictions = new ArrayList<String>();
		restrictions.add("valid-value-a");
		restrictions.add("valid-value-b");
		restrictions.add("valid-value-c");
		// setRestrictions does not guarantee uniqueness
		restrictions.add("valid-value-c");
		slp.setRestrictions(restrictions);

		String[] res = slp.getRestrictions();
		assertArrayEquals(new String[] { "valid-value-a", "valid-value-b",
				"valid-value-c", "valid-value-c" }, res);
	}

	@Test
	public void testAddRestrictions() {
		StringListParameter slp = new StringListParameter(key, null);
		slp.addRestrictions("valid-value-a");
		slp.addRestrictions("valid-value-b");
		slp.addRestrictions("valid-value-c");
		// check if slp ensures uniqueness
		slp.addRestrictions("valid-value-c");

		String[] res = slp.getRestrictions();
		assertArrayEquals(new String[] { "valid-value-a", "valid-value-b",
				"valid-value-c" }, res);
	}
}
