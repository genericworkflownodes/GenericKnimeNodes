/*
 * Copyright (c) 2011, Marc Röttig.
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

package com.genericworkflownodes.knime.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.genericworkflownodes.knime.parameter.DoubleParameter;
import com.genericworkflownodes.knime.parameter.Parameter;
import com.genericworkflownodes.knime.parameter.StringChoiceParameter;
import com.genericworkflownodes.knime.parameter.StringParameter;
import com.genericworkflownodes.knime.test.data.TestDataSource;

public class GalaxyNodeConfigurationReaderTest {

	@Test
	public void testReader() throws Exception {
		INodeConfiguration config = null;
		GalaxyNodeConfigurationReader reader = new GalaxyNodeConfigurationReader();
		config = reader.read(TestDataSource.class.getResourceAsStream("emboss_water.xml"));

		assertEquals("Smith-Waterman local alignment", config.getDescription());
		assertEquals("water", config.getName());
		assertEquals("5.0.0", config.getVersion());
		assertEquals("help text", config.getManual());

		assertEquals(2, config.getNumberOfInputPorts());
		assertEquals(1, config.getNumberOfOutputPorts());

		assertNotNull(config.getParameter("gapopen"));
		assertNotNull(config.getParameter("gapextend"));

		Parameter<?> p1 = config.getParameter("gapopen");
		Parameter<?> p2 = config.getParameter("gapextend");
		StringChoiceParameter p3 = (StringChoiceParameter) config.getParameter("menu");

		assertTrue(p1 instanceof StringParameter);
		assertTrue(p2 instanceof DoubleParameter);
		assertTrue(p3 instanceof StringChoiceParameter);

		assertEquals(p1.getValue(), "10.0");
		assertEquals(p2.getValue(), 0.5);
		assertEquals("1", p3.getValue());

		assertEquals("A", p3.getLabels().get(0));
		assertEquals("B", p3.getLabels().get(1));
		assertEquals("C", p3.getLabels().get(2));
	}
}