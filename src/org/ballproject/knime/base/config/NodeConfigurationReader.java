package org.ballproject.knime.base.config;

import java.io.InputStream;

public interface NodeConfigurationReader
{
	NodeConfiguration read(InputStream in) throws Exception;
}
