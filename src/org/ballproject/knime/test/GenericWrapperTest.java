package org.ballproject.knime.test;

import static org.junit.Assert.*;

import org.ballproject.knime.base.config.CTDNodeConfigurationReader;
import org.ballproject.knime.base.config.NodeConfiguration;
import org.ballproject.knime.base.wrapper.GenericToolWrapper;
import org.ballproject.knime.test.data.TestDataSource;
import org.junit.Test;

public class GenericWrapperTest
{

	@Test
	public void test() throws Exception
	{
		
		NodeConfiguration          config = null;
		CTDNodeConfigurationReader reader = new CTDNodeConfigurationReader();
		config = reader.read(TestDataSource.class.getResourceAsStream("test5.ctd"));
	}

}
