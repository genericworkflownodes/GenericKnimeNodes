package org.ballproject.knime.base;

import org.ballproject.knime.base.config.CTDNodeConfigurationReaderTest;
import org.ballproject.knime.base.config.CTDNodeConfigurationWriterTest;
import org.ballproject.knime.base.config.GalaxyNodeConfigurationReaderTest;
import org.ballproject.knime.base.mime.MIMEFileStuffTest;
import org.ballproject.knime.base.parameter.ParameterTest;
import org.ballproject.knime.base.wrapper.GenericToolWrapperTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ CTDNodeConfigurationReaderTest.class,
		CTDNodeConfigurationWriterTest.class,
		GalaxyNodeConfigurationReaderTest.class, GenericToolWrapperTest.class,
		MIMEFileStuffTest.class, ParameterTest.class })
public class AllTests {
}
