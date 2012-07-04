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

import java.util.Arrays;
import java.util.List;

import org.ballproject.knime.base.port.Port;
import org.junit.Test;

/**
 * Test for {@link FileListParameter}.
 * 
 * @author aiche
 */
public class FileListParameterTest {

	List<String> strings = Arrays.asList("f1", "f2", "f3");

	@Test
	public void testFileListParameter() {
		FileListParameter flp = new FileListParameter("flp", strings);
		assertEquals(3, flp.getValue().size());
		assertEquals("f1", flp.getValue().get(0));
		assertEquals("f2", flp.getValue().get(1));
		assertEquals("f3", flp.getValue().get(2));
	}

	@Test
	public void testSetPort() {
		FileListParameter flp = new FileListParameter("flp", strings);
		flp.setPort(new Port());
		assertNotNull(flp.getPort());
	}

	@Test
	public void testGetPort() {
		FileListParameter flp = new FileListParameter("flp", strings);
		Port p = new Port();
		p.setName("p1");
		flp.setPort(p);
		assertEquals("p1", flp.getPort().getName());
	}

}
