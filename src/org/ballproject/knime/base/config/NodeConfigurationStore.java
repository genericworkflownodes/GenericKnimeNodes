package org.ballproject.knime.base.config;

import java.util.List;
import java.util.Set;

public interface NodeConfigurationStore
{
	void setParameterValue(String name, String value);
	void setMultiParameterValue(String name, String value);
	String getParameterValue(String name);
	List<String> getMultiParameterValue(String name);
	Set<String> getParameterKeys();
}
