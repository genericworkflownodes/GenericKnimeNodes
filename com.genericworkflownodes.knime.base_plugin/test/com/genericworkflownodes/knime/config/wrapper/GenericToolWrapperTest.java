package com.genericworkflownodes.knime.config.wrapper;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.genericworkflownodes.knime.cliwrapper.CLIElement;
import com.genericworkflownodes.knime.config.CTDNodeConfigurationReader;
import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.outputconverter.config.Converter;
import com.genericworkflownodes.knime.outputconverter.config.OutputConverters;
import com.genericworkflownodes.knime.test.data.TestDataSource;

public class GenericToolWrapperTest {

	@Test
	public void testCTDLoading() throws Exception {
		CTDNodeConfigurationReader reader = new CTDNodeConfigurationReader();
		INodeConfiguration config = reader.read(TestDataSource.class
				.getResourceAsStream("test5.ctd"));

		assertEquals(2, config.getCLI().getCLIElement().size());

		CLIElement firstCLIElement = config.getCLI().getCLIElement().get(0);

		assertEquals("-i", firstCLIElement.getOptionIdentifier());
		assertEquals(false, firstCLIElement.isList());
		assertEquals(false, firstCLIElement.isRequired());

		assertEquals(1, firstCLIElement.getMapping().size());
		assertEquals("blastall.i", firstCLIElement.getMapping().get(0)
				.getRefName());

		CLIElement secondCLIElement = config.getCLI().getCLIElement().get(1);
		assertEquals("-d", secondCLIElement.getOptionIdentifier());
		assertEquals(false, secondCLIElement.isList());
		assertEquals(false, secondCLIElement.isRequired());

		assertEquals(1, secondCLIElement.getMapping().size());
		assertEquals("blastall.d", secondCLIElement.getMapping().get(0)
				.getRefName());

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
}
