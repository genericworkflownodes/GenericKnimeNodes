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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.config.reader.CTDConfigurationReader;
import com.genericworkflownodes.knime.parameter.BoolParameter;
import com.genericworkflownodes.knime.parameter.IntegerListParameter;
import com.genericworkflownodes.knime.parameter.StringChoiceParameter;
import com.genericworkflownodes.knime.parameter.StringParameter;
import com.genericworkflownodes.knime.test.data.TestDataSource;

/**
 * Test for {@link CTDConfigurationWriter}.
 * 
 * @author aiche
 */
public class CTDConfigurationWriterTest {

	@Test
	public void testCTDConfigurationWriter() throws IOException {
		File tmp = File.createTempFile("testing_", ".ini");
		tmp.deleteOnExit();
		CTDConfigurationWriter writer = new CTDConfigurationWriter(tmp);
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
		writer.write(config);

		config = reader.read(new FileInputStream(tmp));

		StringParameter mz = (StringParameter) config
				.getParameter("FileFilter.1.mz");
		assertEquals("m/z range to extract (applies to ALL ms levels!)",
				mz.getDescription());
		assertEquals(":", mz.getValue());
		assertEquals("mz", mz.getKey());

		IntegerListParameter levels = (IntegerListParameter) config
				.getParameter("FileFilter.1.peak_options.level");
		assertNotNull(levels);
		assertEquals("MS levels to extract", levels.getDescription());
		assertEquals(3, levels.getValue().size());
		assertEquals(1, levels.getValue().get(0).intValue());
		assertEquals(2, levels.getValue().get(1).intValue());
		assertEquals(3, levels.getValue().get(2).intValue());
		assertEquals(false, levels.isAdvanced());

		StringChoiceParameter int_precision = (StringChoiceParameter) config
				.getParameter("FileFilter.1.peak_options.int_precision");
		assertEquals("32", int_precision.getValue());
		assertEquals(3, int_precision.getAllowedValues().size());

		BoolParameter no_progress = (BoolParameter) config
				.getParameter("FileFilter.1.no_progress");
		assertEquals(false, no_progress.getValue());
		assertEquals(true, no_progress.isAdvanced());

		assertEquals(3, config.getInputPorts().size());
		assertEquals("FileFilter.1.in", config.getInputPorts().get(0).getName());
	}
}
