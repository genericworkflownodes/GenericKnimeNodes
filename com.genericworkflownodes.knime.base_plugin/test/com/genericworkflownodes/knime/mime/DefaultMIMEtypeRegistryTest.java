/**
 * Copyright (c) 2011-2012, Stephan Aiche.
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
package com.genericworkflownodes.knime.mime;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.knime.core.data.url.MIMEType;

/**
 * Test for {@link DefaultMIMEtypeRegistry}.
 * 
 * @author aiche
 */
public class DefaultMIMEtypeRegistryTest {

	/**
	 * Test c'tor.
	 */
	@Test
	public void testDefaultMIMEtypeRegistry() {
		IMIMEtypeRegistry registry = new DefaultMIMEtypeRegistry();
		assertNull(registry.getMIMEtype("/this/file/does/not-exist.file"));
	}

	@Test
	public void testRegisterMIMEtype() {
		IMIMEtypeRegistry registry = new DefaultMIMEtypeRegistry();
		registry.registerMIMEtype(new MIMEType("fa"));
		registry.registerMIMEtype(new MIMEType("fasta"));
		registry.registerMIMEtype(new MIMEType("mock"));

		assertNotNull(registry.getMIMEtype("fa"));
		assertNotNull(registry.getMIMEtype("fasta"));
		assertNotNull(registry.getMIMEtype("mock"));

		assertNull(registry.getMIMEtype("unknown"));
	}

	@Test
	public void testGetMIMEtype() {
		IMIMEtypeRegistry registry = new DefaultMIMEtypeRegistry();
		registry.registerMIMEtype(new MIMEType("fa"));
		registry.registerMIMEtype(new MIMEType("fasta"));
		registry.registerMIMEtype(new MIMEType("mock"));

		assertNotNull(registry.getMIMEtype("fa"));
		assertNotNull(registry.getMIMEtype("fasta"));
		assertNotNull(registry.getMIMEtype("mock"));

		assertNull(registry.getMIMEtype("unknown"));
	}
}
