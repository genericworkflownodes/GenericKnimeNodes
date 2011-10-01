package org.ballproject.knime.base.config;

import java.io.IOException;

public interface NodeConfigurationWriter
{
	void setParameterValue(String name, String value);
	void setMultiParameterValue(String name, String value);
	void write(String filename) throws IOException;
	void writeINI(String filename) throws IOException;
}
