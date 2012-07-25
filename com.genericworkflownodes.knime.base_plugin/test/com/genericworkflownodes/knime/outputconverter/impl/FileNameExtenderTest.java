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
package com.genericworkflownodes.knime.outputconverter.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;

import org.junit.Test;

/**
 * Test for {@link FileNameExtender}.
 * 
 * @author aiche
 */
public class FileNameExtenderTest {

	/**
	 * Test c'tor.
	 */
	@Test
	public void testFileNameExtender() {
		FileNameExtender extender = new FileNameExtender(new Properties());
		assertNotNull(extender);
	}

	/**
	 * Test convert method.
	 * 
	 * @throws IOException
	 *             Should not happen.
	 */
	@Test
	public void testConvert() throws IOException {
		File tmp = File.createTempFile("test-", "suffix");
		tmp.deleteOnExit();
		Properties props = new Properties();
		String extensionToAdd = "unusual-extension-that-should-not-occur-in-temp-file-name";
		props.setProperty("file-extension", extensionToAdd);
		FileNameExtender extender = new FileNameExtender(props);

		URI convertedURI = extender.convert(tmp.toURI());
		assertTrue(convertedURI.getPath().endsWith(extensionToAdd));
		assertTrue(convertedURI.getPath().startsWith(tmp.getAbsolutePath()));
	}
}
