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

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * Test for {@link StringChoiceParameter}.
 * 
 * @author aiche
 */
public class StringChoiceParameterTest {

    List<String> choices = Arrays.asList("c1", "c2", "c3");
    List<String> labels = Arrays.asList("l1", "l2", "l3");

    String key = "scp";

    @Test
    public void testGetMnemonic() {
        StringChoiceParameter scp = new StringChoiceParameter(key, choices);
        assertEquals("string choice", scp.getMnemonic());
    }

    @Test
    public void testFillFromString() throws InvalidParameterValueException {
        StringChoiceParameter scp = new StringChoiceParameter(key, choices);
        scp.fillFromString("");
        assertEquals(null, scp.getValue());
        scp.fillFromString("c1");
        assertEquals("c1", scp.getValue());
        scp.fillFromString(null);
        assertNull(scp.getValue());
    }

    @Test(expected = InvalidParameterValueException.class)
    public void testFillFromNotContainedString()
            throws InvalidParameterValueException {
        StringChoiceParameter scp = new StringChoiceParameter(key, choices);
        scp.fillFromString("c4");
    }

    @Test
    public void testStringChoiceParameterStringString() {
        StringChoiceParameter scp = new StringChoiceParameter(key,
                choices.get(0));
        assertEquals("c1", scp.getValue());
        // since the empty string is allowed
        assertEquals(1, scp.getAllowedValues().size());
        assertEquals(1, scp.getLabels().size());

        // since the empty string is not allowed anymore
        scp.setIsOptional(false);
        assertEquals(0, scp.getAllowedValues().size());
        assertEquals(0, scp.getLabels().size());
    }

    @Test
    public void testStringChoiceParameterStringListOfString() {
        StringChoiceParameter scp = new StringChoiceParameter(key, choices);
        assertEquals("", scp.getValue());
        assertEquals(4, scp.getAllowedValues().size());
        assertEquals("", scp.getAllowedValues().get(0));
        assertEquals("c1", scp.getAllowedValues().get(1));
        assertEquals(4, scp.getLabels().size());
        assertEquals("", scp.getLabels().get(0));
        assertEquals("c1", scp.getLabels().get(1));
    }

    @Test
    public void testStringChoiceParameterStringStringArray() {
        StringChoiceParameter scp = new StringChoiceParameter(key,
                (String[]) choices.toArray());
        assertEquals("", scp.getValue());
        assertEquals(4, scp.getAllowedValues().size());
        assertEquals("", scp.getAllowedValues().get(0));
        assertEquals("c1", scp.getAllowedValues().get(1));
        assertEquals(4, scp.getLabels().size());
    }

    @Test
    public void testStringChoiceParameterStringListOfStringListOfString() {
        StringChoiceParameter scp = new StringChoiceParameter(key, choices,
                labels);
        assertEquals(null, scp.getValue());
        assertEquals(4, scp.getAllowedValues().size());
        assertEquals("", scp.getAllowedValues().get(0));
        assertEquals("c1", scp.getAllowedValues().get(1));
        assertEquals(4, scp.getLabels().size());
        assertEquals("", scp.getLabels().get(0));
        assertEquals("l1", scp.getLabels().get(1));
    }

    @Test
    public void testStringChoiceParameterStringStringArrayStringArray() {
        StringChoiceParameter scp = new StringChoiceParameter(key,
                (String[]) choices.toArray(), (String[]) labels.toArray());
        assertEquals(null, scp.getValue());
        assertEquals(4, scp.getAllowedValues().size());
        assertEquals("", scp.getAllowedValues().get(0));
        assertEquals("c1", scp.getAllowedValues().get(1));
        assertEquals(4, scp.getLabels().size());
        assertEquals("", scp.getLabels().get(0));
        assertEquals("l1", scp.getLabels().get(1));
    }

    @Test
    public void testSetValueString() {
        StringChoiceParameter scp = new StringChoiceParameter(key, choices);
        scp.setValue("");
        assertEquals("", scp.getValue());
        scp.setValue("c1");
        assertEquals("c1", scp.getValue());
        scp.setValue("c2");
        assertEquals("c2", scp.getValue());
        scp.setValue("c3");
        assertEquals("c3", scp.getValue());

        // invalid values will have no effect
        scp.setValue("c4");
        assertEquals("c3", scp.getValue());

        scp.setIsOptional(false);
        scp.setValue("");
        assertEquals("c3", scp.getValue());
    }

    @Test
    public void testGetAllowedValues() {
        StringChoiceParameter scp = new StringChoiceParameter(key, choices);
        assertEquals(4, scp.getAllowedValues().size());
        assertEquals("", scp.getAllowedValues().get(0));
        assertEquals("c1", scp.getAllowedValues().get(1));
        assertEquals("c2", scp.getAllowedValues().get(2));
        assertEquals("c3", scp.getAllowedValues().get(3));

        scp.setIsOptional(false);
        assertEquals(3, scp.getAllowedValues().size());
        assertEquals("c1", scp.getAllowedValues().get(0));
        assertEquals("c2", scp.getAllowedValues().get(1));
        assertEquals("c3", scp.getAllowedValues().get(2));
    }

    @Test
    public void testGetLabels() {
        StringChoiceParameter scp = new StringChoiceParameter(key, choices,
                labels);
        assertEquals(4, scp.getLabels().size());
        assertEquals("", scp.getLabels().get(0));
        assertEquals("l1", scp.getLabels().get(1));
        assertEquals("l2", scp.getLabels().get(2));
        assertEquals("l3", scp.getLabels().get(3));

        scp.setIsOptional(false);
        assertEquals(3, scp.getLabels().size());
        assertEquals("l1", scp.getLabels().get(0));
        assertEquals("l2", scp.getLabels().get(1));
        assertEquals("l3", scp.getLabels().get(2));

        // if no labels are given, the values are the labels
        StringChoiceParameter scp2 = new StringChoiceParameter(key, choices);
        assertEquals(4, scp2.getLabels().size());
        assertEquals("", scp2.getLabels().get(0));
        assertEquals("c1", scp2.getLabels().get(1));
        assertEquals("c2", scp2.getLabels().get(2));
        assertEquals("c3", scp2.getLabels().get(3));

        scp2.setIsOptional(false);
        assertEquals(3, scp2.getLabels().size());
        assertEquals("c1", scp2.getLabels().get(0));
        assertEquals("c2", scp2.getLabels().get(1));
        assertEquals("c3", scp2.getLabels().get(2));
    }

    @Test
    public void testToString() {
        StringChoiceParameter scp = new StringChoiceParameter(key, choices);
        assertEquals("", scp.toString());
        scp.setValue("c2");
        assertEquals("c2", scp.toString());
    }

    @Test
    public void testValidateString() {
        StringChoiceParameter scp = new StringChoiceParameter(key, choices);
        assertTrue(scp.validate(""));
        assertTrue(scp.validate(null));
        assertTrue(scp.validate("abebebrsbrsbrsb"));
    }

    @Test
    public void testSetIsOptional() {
        StringChoiceParameter scp = new StringChoiceParameter(key, choices);
        assertEquals("", scp.getValue());
        scp.setIsOptional(false);
        assertEquals("c1", scp.getValue());
        scp.setIsOptional(true);
        assertEquals("c1", scp.getValue());
        scp.setValue("c2");
        scp.setIsOptional(false);
        assertEquals("c2", scp.getValue());
    }

}
