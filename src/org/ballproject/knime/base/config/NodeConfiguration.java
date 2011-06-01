package org.ballproject.knime.base.config;

import java.util.List;
import org.ballproject.knime.base.parameter.Parameter;
import org.ballproject.knime.base.port.Port;

public interface NodeConfiguration
{
	public String getName();
	
	public String getDescription();
	public String getManual();
	public String getDocUrl();
	public String getVersion();
	public String getXML();
	public String getCategory();
	
	public int getNumberOfOutputPorts();
	public int getNumberOfInputPorts();
	
	public Port[] getInputPorts();
	public Port[] getOutputPorts();
		
	public Parameter<?> getParameter(String key);
	public List<String> getParameterKeys();
	public List<Parameter<?>> getParameters();
}
