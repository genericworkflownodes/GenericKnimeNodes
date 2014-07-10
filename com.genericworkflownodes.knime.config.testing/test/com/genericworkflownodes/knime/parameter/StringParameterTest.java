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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test for {@link StringParameter}.
 * 
 * @author aiche
 */
public class StringParameterTest {

    String key = "sp-key";
    String value = "sp-value";

    @Test
    public void testGetMnemonic() {
        StringParameter sp = new StringParameter(key, value);
        assertEquals("string", sp.getMnemonic());
    }

    @Test
    public void testFillFromString() {
        StringParameter sp = new StringParameter(key, null);
        assertNull(sp.getValue());
        sp.fillFromString(value);
        assertEquals(value, sp.getValue());
    }

    @Test
    public void testStringParameter() {
        StringParameter sp = new StringParameter(key, value);
        assertEquals(key, sp.getKey());
        assertEquals(value, sp.getValue());
    }

    @Test
    public void testToString() {
        StringParameter sp = new StringParameter(key, value);
        assertEquals(value, sp.getValue());
    }

    @Test
    public void testValidateString() {
        StringParameter sp = new StringParameter(key, value);
        assertTrue(sp.validate(value));
        assertTrue(sp.validate(null));
        assertTrue(sp.validate(""));
    }

}
