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

package org.ballproject.knime.test;

import static org.junit.Assert.*;

import org.ballproject.knime.base.config.CTDNodeConfigurationReader;
import org.ballproject.knime.base.config.NodeConfiguration;
import org.ballproject.knime.test.data.TestDataSource;
import org.junit.Test;

public class CTDNodeConfigurationReaderTest
{

	@Test
	public void testReader() throws Exception
	{
		NodeConfiguration config = null;
		CTDNodeConfigurationReader reader = new CTDNodeConfigurationReader();
		config = reader.read(TestDataSource.class.getResourceAsStream("test.ctd"));
		
		assertEquals("internal",config.getStatus());
		assertEquals("Get Data",config.getCategory());
		assertEquals("export molecules from data base",config.getDescription());
		assertEquals("http://www.google.de",config.getDocUrl());
		assertEquals("manual text.",config.getManual());
		assertEquals("DBExporter",config.getName());
		assertEquals("0.9.6 (ob)",config.getVersion());
		assertEquals(2,config.getNumberOfInputPorts());
		assertEquals(1,config.getNumberOfOutputPorts());

		assertNotNull(config.getParameter("1.start_id"));
		assertNotNull(config.getParameter("1.end_id"));
		assertNotNull(config.getParameter("1.min_logP"));
		assertNotNull(config.getParameter("1.max_logP"));
		assertNotNull(config.getParameter("1.min_MW"));
		assertNotNull(config.getParameter("1.max_MW"));
		assertNotNull(config.getParameter("1.max_mols"));
		assertNotNull(config.getParameter("1.target"));
		assertNull(config.getParameter("1.q"));
		assertNotNull(config.getParameter("1.min_sim"));
		assertNotNull(config.getParameter("1.max_sim"));
		assertNotNull(config.getParameter("1.smarts"));
		assertNotNull(config.getParameter("1.uck"));
		assertNotNull(config.getParameter("1.d"));
		assertNotNull(config.getParameter("1.u"));
		assertNotNull(config.getParameter("1.h"));
		assertNotNull(config.getParameter("1.port"));
		assertNotNull(config.getParameter("1.p"));
		assertNotNull(config.getParameter("1.s"));
		
		
	}

}
