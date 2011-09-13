package org.ballproject.knime.test;

import static org.junit.Assert.*;

import org.ballproject.knime.base.config.GalaxyNodeConfigurationReader;
import org.ballproject.knime.base.config.NodeConfiguration;
import org.ballproject.knime.base.parameter.DoubleParameter;
import org.ballproject.knime.base.parameter.Parameter;
import org.ballproject.knime.base.parameter.StringParameter;
import org.ballproject.knime.test.data.TestDataSource;
import org.junit.Test;

public class GalaxyNodeConfigurationReaderTest
{

	@Test
	public void testReader() throws Exception
	{
		NodeConfiguration config = null;
		GalaxyNodeConfigurationReader reader = new GalaxyNodeConfigurationReader();
		config = reader.read(TestDataSource.class.getResourceAsStream("emboss_water.xml"));
		
		assertEquals("Smith-Waterman local alignment",config.getDescription());
		assertEquals("water",config.getName());
		assertEquals("5.0.0",config.getVersion());
		assertEquals("help text",config.getManual());
		
		assertEquals(2,config.getNumberOfInputPorts());
		assertEquals(1,config.getNumberOfOutputPorts());
		
		assertNotNull(config.getParameter("gapopen"));
		assertNotNull(config.getParameter("gapextend"));
		
		Parameter<?> p1 = config.getParameter("gapopen");
		Parameter<?> p2 = config.getParameter("gapextend");
		
		assertTrue( p1 instanceof StringParameter);
		assertTrue( p2 instanceof DoubleParameter);
		assertEquals(p1.getValue(),"10.0");
		assertEquals(p2.getValue(),0.5);
		
	}
}