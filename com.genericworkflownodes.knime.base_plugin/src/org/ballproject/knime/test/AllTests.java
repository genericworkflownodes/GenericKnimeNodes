package org.ballproject.knime.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({CTDNodeConfigurationReaderTest.class, CTDNodeConfigurationWriterTest.class, GalaxyNodeConfigurationReaderTest.class, GenericWrapperTest.class, MIMEFileStuffTest.class, ParameterTest.class})
public class AllTests {

}
