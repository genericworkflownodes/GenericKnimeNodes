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
 * Test for {@link DoubleParameter}.
 * 
 * @author aiche
 */
public class DoubleParameterTest {

    @Test
    public void testGetMnemonic() {
        DoubleParameter dp = new DoubleParameter("dp", 2.9);
        assertEquals(dp.getMnemonic(), "double [-inf:+inf]");
        dp.setLowerBound(-3.0);
        assertEquals(dp.getMnemonic(), "double [-3.0:+inf]");
        dp.setUpperBound(3.0);
        assertEquals(dp.getMnemonic(), "double [-3.0:3.0]");
    }

    @Test
    public void testFillFromString() throws InvalidParameterValueException {
        DoubleParameter dp = new DoubleParameter("dp", 2.9);
        dp.fillFromString("10.14331");
        assertEquals(new Double(10.14331), dp.getValue());
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testFromOutOfBoundsString()
            throws InvalidParameterValueException {
        DoubleParameter dp = new DoubleParameter("dp", 2.9);
        dp.setUpperBound(4.0);

        thrown.expect(InvalidParameterValueException.class);
        thrown.expectMessage("parameter " + dp.getKey()
                + " value is out of bounds");

        dp.fillFromString("10.14331");
    }

    @Test
    public void testFillFromInvalidString()
            throws InvalidParameterValueException {
        DoubleParameter dp = new DoubleParameter("dp", 2.9);

        thrown.expect(InvalidParameterValueException.class);
        thrown.expectMessage("parameter " + dp.getKey()
                + " value is not a double");

        dp.fillFromString("10.1foobar4331");
    }

    @Test
    public void testDoubleParameterStringDouble() {
        DoubleParameter dp = new DoubleParameter("dp", 2.9);
        assertEquals(new Double(2.9), dp.getValue());
        assertEquals(dp.getMnemonic(), "double [-inf:+inf]");
    }

    @Test
    public void testDoubleParameterStringString() {
        DoubleParameter dp = new DoubleParameter("dp", "2.9");
        assertEquals(new Double(2.9), dp.getValue());
        assertEquals(dp.getMnemonic(), "double [-inf:+inf]");
    }

    @Test
    public void testToString() {
        DoubleParameter dp = new DoubleParameter("dp", 2.9);
        assertEquals(new Double(2.9), dp.getValue());
        assertEquals("2.9", dp.toString());
    }

    @Test
    public void testValidateDouble() {
        Double dNull = null;
        DoubleParameter dpNull = new DoubleParameter("dp", dNull);
        assertEquals(true, dpNull.validate(null));

        DoubleParameter dp = new DoubleParameter("dp", 2.9);
        Double anotherDouble = new Double(25.0);
        assertEquals(true, dp.validate(anotherDouble));

        dp.setLowerBound(26.0);
        assertEquals(false, dp.validate(anotherDouble));
        dp.setLowerBound(Double.NEGATIVE_INFINITY);
        assertEquals(true, dp.validate(anotherDouble));

        dp.setUpperBound(24.5);
        assertEquals(false, dp.validate(anotherDouble));
        dp.setUpperBound(Double.POSITIVE_INFINITY);
        assertEquals(true, dp.validate(anotherDouble));
    }

}
