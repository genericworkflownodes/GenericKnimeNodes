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
package com.genericworkflownodes.knime.config.writer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.junit.Test;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.config.reader.CTDConfigurationReader;
import com.genericworkflownodes.knime.test.data.TestDataSource;

/**
 * Test for {@link CTDConfigurationWriter}.
 * 
 * @author aiche
 */
public class CTDConfigurationWriterTest {

	@Test
	public void testCTDConfigurationWriter() {
		CTDConfigurationWriter writer = new CTDConfigurationWriter(null);
		assertNotNull(writer);
	}

	@Test
	public void testWrite() throws Exception {
		CTDConfigurationReader reader = new CTDConfigurationReader();
		INodeConfiguration config = reader.read(TestDataSource.class
				.getResourceAsStream("FileFilter.ctd"));

		File tmp = File.createTempFile("testing_", ".ini");
		tmp.deleteOnExit();
		BufferedWriter bWriter = new BufferedWriter(new FileWriter(tmp));
		CTDConfigurationWriter writer = new CTDConfigurationWriter(bWriter);
		writer.write(config, null);

		fail("Not yet implemented");
	}
}
