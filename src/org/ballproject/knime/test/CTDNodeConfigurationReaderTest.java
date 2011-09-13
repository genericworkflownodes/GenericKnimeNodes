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
