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

import org.junit.Test;

/**
 * Test for {@link BoolParameter}.
 * 
 * @author aiche
 */
public class BoolParameterTest {

	@Test
	public void testGetMnemonic() {
		BoolParameter bp = new BoolParameter("new-bp", "true");
		assertEquals("bool", bp.getMnemonic());
	}

	@Test
	public void testFillFromString() throws InvalidParameterValueException {
		BoolParameter bp = new BoolParameter("new-bp", "true");
		assertEquals(true, bp.getValue());

		bp.fillFromString("false");
		assertEquals(false, bp.getValue());

		bp.fillFromString("true");
		assertEquals(true, bp.getValue());
	}

	@Test(expected = InvalidParameterValueException.class)
	public void testFillFromInvalidString()
			throws InvalidParameterValueException {
		BoolParameter bp = new BoolParameter("new-bp", "true");
		assertEquals(true, bp.getValue());

		bp.fillFromString("not-a-boolean-value");
	}

	@Test
	public void testGetStringRep() {
		BoolParameter bpTrue = new BoolParameter("new-bp", "true");
		BoolParameter bpFalse = new BoolParameter("new-bp", "false");

		assertEquals("true", bpTrue.getStringRep());
		assertEquals("false", bpFalse.getStringRep());
	}

	@Test
	public void testBoolParameterStringBoolean() {
		BoolParameter bpTrue = new BoolParameter("new-bp", true);
		BoolParameter bpFalse = new BoolParameter("new-bp", false);

		assertEquals(true, bpTrue.getValue());
		assertEquals(false, bpFalse.getValue());
	}

	@Test
	public void testBoolParameterStringString() {
		BoolParameter bpTrue = new BoolParameter("new-bp", "true");
		BoolParameter bpFalse = new BoolParameter("new-bp", "false");

		assertEquals(true, bpTrue.getValue());
		assertEquals(false, bpFalse.getValue());
	}

	@Test
	public void testToString() {
		BoolParameter bpTrue = new BoolParameter("new-bp", "true");
		BoolParameter bpFalse = new BoolParameter("new-bp", "false");

		assertEquals("true", bpTrue.toString());
		assertEquals("false", bpFalse.toString());
	}

	@Test
	public void testValidateBoolean() {
		BoolParameter bp = new BoolParameter("new-bp", "true");
		assertEquals(true, bp.validate(true));
		assertEquals(true, bp.validate(false));
	}

}
