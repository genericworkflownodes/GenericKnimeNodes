package org.ballproject.knime.test;

import org.ballproject.knime.base.config.CTDNodeConfigurationReader;
import org.ballproject.knime.test.data.TestDataSource;
import org.junit.Test;

public class GenericWrapperTest {

	@Test
	public void test() throws Exception {
		CTDNodeConfigurationReader reader = new CTDNodeConfigurationReader();
		reader.read(TestDataSource.class.getResourceAsStream("test5.ctd"));
	}

}
