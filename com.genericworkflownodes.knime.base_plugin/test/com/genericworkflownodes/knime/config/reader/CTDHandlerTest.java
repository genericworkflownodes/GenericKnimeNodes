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

import java.io.IOException;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.junit.Test;
import org.xml.sax.SAXException;

import com.genericworkflownodes.knime.cliwrapper.CLIElement;
import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.outputconverter.config.Converter;
import com.genericworkflownodes.knime.outputconverter.config.OutputConverters;
import com.genericworkflownodes.knime.parameter.BoolParameter;
import com.genericworkflownodes.knime.parameter.IntegerListParameter;
import com.genericworkflownodes.knime.parameter.StringChoiceParameter;
import com.genericworkflownodes.knime.parameter.StringParameter;
import com.genericworkflownodes.knime.schemas.SchemaProvider;
import com.genericworkflownodes.knime.test.data.TestDataSource;

/**
 * Test for {@link CTDHandler}.
 * 
 * @author aiche
 */
public class CTDHandlerTest {

	@Test
	public void testCTDHandler() throws ParserConfigurationException,
			SAXException, IOException {
		SAXParserFactory spfac = SAXParserFactory.newInstance();

		// Now use the parser factory to create a SAXParser object
		SAXParser sp = spfac.newSAXParser();

		CTDHandler handler = new CTDHandler(sp.getXMLReader());
		sp.parse(TestDataSource.class.getResourceAsStream("test5.ctd"), handler);

		INodeConfiguration config = handler.getNodeConfiguration();

		assertEquals(2, config.getCLI().getCLIElement().size());

		CLIElement firstCLIElement = config.getCLI().getCLIElement().get(0);

		assertEquals("-i", firstCLIElement.getOptionIdentifier());
		assertEquals(true, firstCLIElement.isList());
		assertEquals(false, firstCLIElement.isRequired());

		assertEquals(1, firstCLIElement.getMapping().size());
		assertEquals("blastall.i", firstCLIElement.getMapping().get(0)
				.getReferenceName());

		CLIElement secondCLIElement = config.getCLI().getCLIElement().get(1);
		assertEquals("-d", secondCLIElement.getOptionIdentifier());
		assertEquals(false, secondCLIElement.isList());
		assertEquals(false, secondCLIElement.isRequired());

		assertEquals(1, secondCLIElement.getMapping().size());
		assertEquals("blastall.d", secondCLIElement.getMapping().get(0)
				.getReferenceName());

		// test converter
		OutputConverters converters = config.getOutputConverters();
		List<Converter> availableConverters = (List<Converter>) converters
				.getConverters();

		assertEquals(2, availableConverters.size());

		assertEquals("DummyConverter", availableConverters.get(0).getClazz());
		assertEquals("blastall.o", availableConverters.get(0).getRef());
		assertEquals(0, availableConverters.get(0).getConverterProperties()
				.size());

		assertEquals("DummyConverter2", availableConverters.get(1).getClazz());
		assertEquals("blastall.o", availableConverters.get(1).getRef());
		assertEquals(2, availableConverters.get(1).getConverterProperties()
				.size());

		assertEquals(true, availableConverters.get(1).getConverterProperties()
				.containsKey("prop1"));
		assertEquals("val1", availableConverters.get(1)
				.getConverterProperties().getProperty("prop1"));

		assertEquals(true, availableConverters.get(1).getConverterProperties()
				.containsKey("prop2"));
		assertEquals("val2", availableConverters.get(1)
				.getConverterProperties().getProperty("prop2"));

	}

	@Test
	public void testParamHandler() throws ParserConfigurationException,
			SAXException, IOException {
		SchemaFactory schemaFactory = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema ctdSchema = schemaFactory.newSchema(SchemaProvider.class
				.getResource("CTD.xsd"));
		SAXParserFactory spfac = SAXParserFactory.newInstance();
		spfac.setValidating(false);
		spfac.setSchema(ctdSchema);

		// Now use the parser factory to create a SAXParser object
		SAXParser sp = spfac.newSAXParser();

		CTDHandler handler = new CTDHandler(sp.getXMLReader());
		sp.parse(TestDataSource.class.getResourceAsStream("FileFilter.ctd"),
				handler);

		INodeConfiguration config = handler.getNodeConfiguration();

		StringParameter mz = (StringParameter) config
				.getParameter("FileFilter.1.mz");
		assertEquals("m/z range to extract", mz.getDescription());
		assertEquals(":", mz.getValue());
		assertEquals("mz", mz.getKey());

		IntegerListParameter levels = (IntegerListParameter) config
				.getParameter("FileFilter.1.level");
		assertEquals("MS levels to extract", levels.getDescription());
		assertEquals(3, levels.getValue().size());
		assertEquals(1, levels.getValue().get(0).intValue());
		assertEquals(2, levels.getValue().get(1).intValue());
		assertEquals(3, levels.getValue().get(2).intValue());
		assertEquals(false, levels.isAdvanced());

		StringChoiceParameter int_precision = (StringChoiceParameter) config
				.getParameter("FileFilter.1.int_precision");
		assertEquals("32", int_precision.getValue());
		assertEquals(3, int_precision.getAllowedValues().size());

		BoolParameter no_progress = (BoolParameter) config
				.getParameter("FileFilter.1.no_progress");
		assertEquals(false, no_progress.getValue());
		assertEquals(true, no_progress.isAdvanced());

		assertEquals(1, config.getInputPorts().length);
		assertEquals("FileFilter.1.in", config.getInputPorts()[0].getName());

	}
}
