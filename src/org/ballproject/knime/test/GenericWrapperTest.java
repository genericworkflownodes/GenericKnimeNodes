package org.ballproject.knime.test;

import org.ballproject.knime.base.config.CTDFileNodeConfigurationReader;
import org.ballproject.knime.test.data.TestDataSource;
import org.junit.Test;

public class GenericWrapperTest {

	@Test
	public void test() throws Exception {
		CTDFileNodeConfigurationReader reader = new CTDFileNodeConfigurationReader();
		reader.read(TestDataSource.class.getResourceAsStream("test5.ctd"));
	}

}
