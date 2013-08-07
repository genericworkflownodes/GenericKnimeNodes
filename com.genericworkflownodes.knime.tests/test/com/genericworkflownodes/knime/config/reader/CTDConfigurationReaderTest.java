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
package com.genericworkflownodes.knime.config.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.parameter.FileListParameter;
import com.genericworkflownodes.knime.parameter.FileParameter;
import com.genericworkflownodes.knime.parameter.StringListParameter;
import com.genericworkflownodes.knime.test.data.TestDataSource;

/**
 * Test for {@link CTDConfigurationReader}.
 * 
 * @author aiche
 */
public class CTDConfigurationReaderTest {

	@Test
	public void testRead() throws Exception {
		CTDConfigurationReader reader = new CTDConfigurationReader();
		assertNotNull(reader);
		INodeConfiguration config = reader.read(TestDataSource.class
				.getResourceAsStream("FeatureLinkerUnlabeled.ctd"));

		assertEquals("Map Alignment", config.getCategory());
		assertNotNull(config.getParameter("FeatureLinkerUnlabeled.1.in"));
		assertTrue(config.getParameter("FeatureLinkerUnlabeled.1.in") instanceof FileListParameter);
		assertNotNull(config.getInputPortByName("FeatureLinkerUnlabeled.1.in"));
		assertEquals("1.11.0", config.getVersion());

		FileListParameter flp = (FileListParameter) config
				.getParameter("FeatureLinkerUnlabeled.1.in");
		assertEquals(0, flp.getValue().size());
	}

	@Test
	public void testReadTMTAnalyzer() throws Exception {
		CTDConfigurationReader reader = new CTDConfigurationReader();
		assertNotNull(reader);
		INodeConfiguration config = reader.read(TestDataSource.class
				.getResourceAsStream("TMTAnalyzer.ctd"));
		assertEquals("Quantitation", config.getCategory());
		assertEquals("1.11.0", config.getVersion());
		assertNotNull(config.getParameter("TMTAnalyzer.1.in"));
		assertTrue(config.getParameter("TMTAnalyzer.1.in") instanceof FileParameter);

		assertTrue(config
				.getParameter("TMTAnalyzer.1.algorithm.Extraction.channel_active") instanceof StringListParameter);
		StringListParameter slp = (StringListParameter) config
				.getParameter("TMTAnalyzer.1.algorithm.Extraction.channel_active");
		assertEquals(2, slp.getValue().size());
		assertEquals("126:liver", slp.getValue().get(0));
		assertEquals("131:lung", slp.getValue().get(1));
		assertTrue(config
				.getParameter(
						"TMTAnalyzer.1.algorithm.Quantification.isotope_correction.tmt-6plex")
				.isAdvanced());
		assertTrue(config
				.getParameter(
						"TMTAnalyzer.1.algorithm.Quantification.isotope_correction.tmt-6plex")
				.isOptional());
		assertFalse(config.getParameter("TMTAnalyzer.1.in").isAdvanced());
		assertFalse(config.getParameter("TMTAnalyzer.1.in").isOptional());
	}
}
