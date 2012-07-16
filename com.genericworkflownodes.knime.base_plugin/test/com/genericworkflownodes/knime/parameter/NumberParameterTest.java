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
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * Test for {@link NumberListParameter}.
 * 
 * @author aiche
 */
public class NumberParameterTest {

	private class NumberParameterImpl extends NumberParameter<Integer> {

		public NumberParameterImpl(String key, Integer value) {
			super(key, value, Integer.MIN_VALUE, Integer.MAX_VALUE);
		}

		// the following methods will not be tested
		@Override
		public String getMnemonic() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void fillFromString(String s)
				throws InvalidParameterValueException {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean validate(Integer val) {
			// TODO Auto-generated method stub
			return false;
		}

	}

	Integer ints = new Integer(2);

	@Test
	public void testNumberListParameter() {
		NumberParameterImpl nlp = new NumberParameterImpl("nlp", ints);
		assertNotNull(nlp);
	}

	@Test
	public void testGetLowerBound() {
		NumberParameterImpl nlp = new NumberParameterImpl("nlp", ints);
		assertEquals(new Integer(Integer.MIN_VALUE), nlp.getLowerBound());
		nlp.setLowerBound(2);
		assertEquals(new Integer(2), nlp.getLowerBound());
	}

	@Test
	public void testSetLowerBound() {
		NumberParameterImpl nlp = new NumberParameterImpl("nlp", ints);
		nlp.setLowerBound(2);
		assertEquals(new Integer(2), nlp.getLowerBound());
	}

	@Test
	public void testGetUpperBound() {
		NumberParameterImpl nlp = new NumberParameterImpl("nlp", ints);
		assertEquals(new Integer(Integer.MAX_VALUE), nlp.getUpperBound());
		nlp.setUpperBound(2);
		assertEquals(new Integer(2), nlp.getUpperBound());
	}

	@Test
	public void testSetUpperBound() {
		NumberParameterImpl nlp = new NumberParameterImpl("nlp", ints);
		nlp.setUpperBound(2);
		assertEquals(new Integer(2), nlp.getUpperBound());
	}

}
