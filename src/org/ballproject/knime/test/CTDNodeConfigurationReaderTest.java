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

import java.util.ArrayList;
import java.util.List;

import org.ballproject.knime.base.config.CTDNodeConfigurationReader;
import org.ballproject.knime.base.config.NodeConfiguration;
import org.ballproject.knime.base.mime.MIMEtype;
import org.ballproject.knime.base.parameter.BoolParameter;
import org.ballproject.knime.base.parameter.DoubleListParameter;
import org.ballproject.knime.base.parameter.DoubleParameter;
import org.ballproject.knime.base.parameter.IntegerListParameter;
import org.ballproject.knime.base.parameter.IntegerParameter;
import org.ballproject.knime.base.parameter.Parameter;
import org.ballproject.knime.base.parameter.StringChoiceParameter;
import org.ballproject.knime.base.parameter.StringListParameter;
import org.ballproject.knime.base.parameter.StringParameter;
import org.ballproject.knime.base.port.Port;
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

		assertNotNull(config.getParameter("x"));
		assertNotNull(config.getParameter("y"));
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
		assertNotNull(config.getParameter("1.2.z"));
		
		assertNull(config.getParameter("1.par"));
		assertNull(config.getParameter("1.write_par"));
		assertNull(config.getParameter("1.help"));
		
		Parameter<?> p1 =  config.getParameter("1.h");
		assertTrue(p1 instanceof StringParameter);
		
		Parameter<?> p2 =  config.getParameter("1.port");
		assertTrue(p2 instanceof IntegerParameter);
		
		
		DoubleParameter p3 =  (DoubleParameter) config.getParameter("1.min_logP");
		assertTrue(p3 instanceof DoubleParameter);
		assertEquals(new Double(-10.0),p3.getLowerBound());
		assertEquals(new Double(10.0),p3.getUpperBound());
		
		DoubleParameter p6 =  (DoubleParameter) config.getParameter("1.max_logP");
		assertTrue(p6 instanceof DoubleParameter);
		assertEquals(new Double(Double.NEGATIVE_INFINITY),p6.getLowerBound());
		assertEquals(new Double(10.0),p6.getUpperBound());
		
		DoubleParameter p7 =  (DoubleParameter) config.getParameter("1.min_MW");
		assertTrue(p7 instanceof DoubleParameter);
		assertEquals(new Double(Double.POSITIVE_INFINITY),p7.getUpperBound());
		assertEquals(new Double(-10.0),p7.getLowerBound());
		
		DoubleParameter p8 =  (DoubleParameter) config.getParameter("1.max_MW");
		assertTrue(p8 instanceof DoubleParameter);
		assertEquals(new Double(Double.POSITIVE_INFINITY),p8.getUpperBound());
		assertEquals(new Double(Double.NEGATIVE_INFINITY),p8.getLowerBound());
		
		Parameter<?> p4 =  config.getParameter("1.flag");
		assertTrue(p4 instanceof BoolParameter);
		
		Parameter<?> p5 =  config.getParameter("1.choice");
		assertTrue(p5 instanceof StringChoiceParameter);
		
		List<MIMEtype> mimetypes = config.getOutputPorts()[0].getMimeTypes();
		
		assertEquals("1.o",config.getOutputPorts()[0].getName());
		assertEquals("output file",config.getOutputPorts()[0].getDescription());
		
		String [] test = {"mol2", "sdf", "drf"};
		int idx = 0;
		for(MIMEtype mt: mimetypes)
		{
			assertEquals(test[idx],mt.getExt());
			idx++;
		}
		
		mimetypes = config.getInputPorts()[0].getMimeTypes();
		String[] test2 = {"mol2", "sdf", "drf", "pdb", "ac", "ent", "brk", "hin", "mol", "xyz", "mol2.gz", "sdf.gz", "drf.gz", "pdb.gz", "ac.gz", "ent.gz", "brk.gz", "hin.gz", "mol.gz", "xyz.gz"};
		idx = 0;
		for(MIMEtype mt: mimetypes)
		{
			assertEquals(test2[idx],mt.getExt());
			idx++;
		}
		
		assertEquals("mol2",mimetypes.get(0).getExt());
		assertEquals("1.q",config.getInputPorts()[0].getName());
		assertEquals("query molecules for similarity searching",config.getInputPorts()[0].getDescription());
		
		mimetypes = config.getInputPorts()[1].getMimeTypes();
		assertEquals("txt",mimetypes.get(0).getExt());
		assertEquals("1.smarts_file",config.getInputPorts()[1].getName());
		assertEquals("SMARTS pattern",config.getInputPorts()[1].getDescription());
	}
	
	@Test
	public void testReader2() throws Exception
	{
		NodeConfiguration config = null;
		CTDNodeConfigurationReader reader = new CTDNodeConfigurationReader();
		config = reader.read(TestDataSource.class.getResourceAsStream("test2.ctd"));
		assertNotNull(config.getParameter("c"));
	}

	@Test
	public void testReader3() throws Exception
	{
		NodeConfiguration config = null;
		CTDNodeConfigurationReader reader = new CTDNodeConfigurationReader();
		config = reader.read(TestDataSource.class.getResourceAsStream("test3.ctd"));
		
		boolean found = false;
		for(Port port : config.getInputPorts())
		{
			if(port.getName().equals("MascotAdapter.1.in2"))
			{
				found = true;
				assertTrue(port.isMultiFile());
			}
		}
		assertTrue(found);
		
		Parameter<?> p1 = config.getParameter("MascotAdapter.1.charges");
		assertNotNull(p1);
		
		assertTrue(p1 instanceof StringListParameter);
		StringListParameter slp = (StringListParameter) p1;
		assertEquals("the different charge states",slp.getDescription());
		assertEquals(3,slp.getValue().size());
		assertEquals("1+",slp.getValue().get(0));
		assertEquals("2+",slp.getValue().get(1));
		assertEquals("3+",slp.getValue().get(2));
		
		Parameter<?> p2 = config.getParameter("MascotAdapter.1.charge");
		assertNotNull(p2);
		
		assertTrue(p2 instanceof IntegerListParameter);
		IntegerListParameter ilp = (IntegerListParameter) p2;
		assertEquals("List of charge states; required if 'in_seq' is given",ilp.getDescription());
		assertEquals(3,ilp.getValue().size());
		assertEquals(new Integer(0),ilp.getValue().get(0));
		assertEquals(new Integer(1),ilp.getValue().get(1));
		assertEquals(new Integer(2),ilp.getValue().get(2));
		
		Parameter<?> p3 = config.getParameter("MascotAdapter.1.somefloats");
		assertNotNull(p3);
		
		assertTrue(p3 instanceof DoubleListParameter);
		DoubleListParameter dlp = (DoubleListParameter) p3;
		assertEquals("List of charge states; required if 'in_seq' is given",dlp.getDescription());
		assertEquals(3,dlp.getValue().size());
		assertEquals(new Double(0.22),dlp.getValue().get(0));
		assertEquals(new Double(1.4),dlp.getValue().get(1));
		assertEquals(new Double(-2.2),dlp.getValue().get(2));
		assertEquals(new Double(-3),dlp.getLowerBound());
		assertEquals(new Double(5),dlp.getUpperBound());
	}
	
}
