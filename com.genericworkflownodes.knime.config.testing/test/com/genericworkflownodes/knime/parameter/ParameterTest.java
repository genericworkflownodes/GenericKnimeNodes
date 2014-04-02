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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test for {@link Parameter}.
 * 
 * @author aiche
 */
public class ParameterTest {

    private static class ParameterImpl extends Parameter<Integer> {

        public static String DEFAULT_KEY = "p-key";
        public static Integer DEFAULT_VALUE = new Integer(10);

        public ParameterImpl() {
            super(DEFAULT_KEY, DEFAULT_VALUE);
        }

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

    @Test
    public void testParameter() {
        ParameterImpl p = new ParameterImpl();
        assertNotNull(p);
    }

    @Test
    public void testGetKey() {
        ParameterImpl p = new ParameterImpl();
        assertEquals(ParameterImpl.DEFAULT_KEY, p.getKey());
    }

    @Test
    public void testSetKey() {
        ParameterImpl p = new ParameterImpl();
        assertEquals(ParameterImpl.DEFAULT_KEY, p.getKey());
        p.setKey("other-key");
        assertEquals("other-key", p.getKey());
    }

    @Test
    public void testGetValue() {
        ParameterImpl p = new ParameterImpl();
        assertEquals(ParameterImpl.DEFAULT_VALUE, p.getValue());
    }

    @Test
    public void testSetValue() {
        ParameterImpl p = new ParameterImpl();
        assertEquals(ParameterImpl.DEFAULT_VALUE, p.getValue());
        p.setValue(new Integer(20));
        assertEquals(new Integer(20), p.getValue());
    }

    @Test
    public void testGetDescription() {
        ParameterImpl p = new ParameterImpl();
        assertEquals("", p.getDescription());
    }

    @Test
    public void testSetDescription() {
        ParameterImpl p = new ParameterImpl();
        assertEquals("", p.getDescription());
        p.setDescription("This is a new description");
        assertEquals("This is a new description", p.getDescription());
    }

    @Test
    public void testGetSection() {
        ParameterImpl p = new ParameterImpl();
        assertEquals("default", p.getSection());
    }

    @Test
    public void testSetSection() {
        ParameterImpl p = new ParameterImpl();
        assertEquals("default", p.getSection());
        p.setSection("new-section");
        assertEquals("new-section", p.getSection());
    }

    @Test
    public void testIsNull() {
        ParameterImpl p = new ParameterImpl();
        assertFalse(p.isNull());
        p.setValue(null);
        assertTrue(p.isNull());
    }

    @Test
    public void testIsOptional() {
        ParameterImpl p = new ParameterImpl();
        assertTrue(p.isOptional());
    }

    @Test
    public void testSetIsOptional() {
        ParameterImpl p = new ParameterImpl();
        assertTrue(p.isOptional());
        p.setIsOptional(false);
        assertFalse(p.isOptional());
    }

    @Test
    public void testGetStringRep() {
        ParameterImpl p = new ParameterImpl();
        assertEquals(p.toString(), p.getStringRep());
    }

    @Test
    public void testIsAdvanced() {
        ParameterImpl p = new ParameterImpl();
        assertFalse(p.isAdvanced());
    }

    @Test
    public void testSetAdvanced() {
        ParameterImpl p = new ParameterImpl();
        assertFalse(p.isAdvanced());
        p.setAdvanced(true);
        assertTrue(p.isAdvanced());
    }
}
