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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test for {@link IntegerParameter}.
 * 
 * @author aiche
 */
public class IntegerParameterTest {

	@Test
	public void testGetMnemonic() {
		IntegerParameter ip = new IntegerParameter("ip", 2);
		assertEquals(ip.getMnemonic(), "integer [-inf:+inf]");
		ip.setLowerBound(-3);
		assertEquals(ip.getMnemonic(), "integer [-3:+inf]");
		ip.setUpperBound(3);
		assertEquals(ip.getMnemonic(), "integer [-3:3]");
	}

	@Test
	public void testFillFromString() throws InvalidParameterValueException {
		IntegerParameter ip = new IntegerParameter("ip", 2);
		ip.fillFromString("10");
		assertEquals(new Integer(10), ip.getValue());
	}

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testFromOutOfBoundsString()
			throws InvalidParameterValueException {
		IntegerParameter ip = new IntegerParameter("ip", 2);
		ip.setUpperBound(4);

		thrown.expect(InvalidParameterValueException.class);
		thrown.expectMessage("parameter " + ip.getKey()
				+ " value is out of bounds");

		ip.fillFromString("10");
	}

	@Test
	public void testFillFromInvalidString()
			throws InvalidParameterValueException {
		IntegerParameter ip = new IntegerParameter("ip", 2);

		thrown.expect(InvalidParameterValueException.class);
		thrown.expectMessage("parameter " + ip.getKey()
				+ " value is not an integer");

		ip.fillFromString("10.1foobar4331");
	}

	@Test
	public void testIntegerParameterStringInteger() {
		IntegerParameter ip = new IntegerParameter("ip", 2);
		assertEquals(new Integer(2), ip.getValue());
		assertEquals(ip.getMnemonic(), "integer [-inf:+inf]");
	}

	@Test
	public void testIntegerParameterStringString() {
		IntegerParameter ip = new IntegerParameter("ip", "2");
		assertEquals(new Integer(2), ip.getValue());
		assertEquals(ip.getMnemonic(), "integer [-inf:+inf]");
	}

	@Test
	public void testToString() {
		IntegerParameter ip = new IntegerParameter("ip", 2);
		assertEquals(new Integer(2), ip.getValue());
		assertEquals("2", ip.toString());
	}

	@Test
	public void testValidateInteger() {
		Integer iNull = null;
		IntegerParameter ipNull = new IntegerParameter("ip", iNull);
		assertEquals(true, ipNull.validate(null));

		IntegerParameter ip = new IntegerParameter("ip", 2);
		Integer anotherInteger = new Integer(25);
		assertEquals(true, ip.validate(anotherInteger));

		ip.setLowerBound(26);
		assertEquals(false, ip.validate(anotherInteger));
		ip.setLowerBound(Integer.MIN_VALUE);
		assertEquals(true, ip.validate(anotherInteger));

		ip.setUpperBound(24);
		assertEquals(false, ip.validate(anotherInteger));
		ip.setUpperBound(Integer.MAX_VALUE);
		assertEquals(true, ip.validate(anotherInteger));
	}
}
