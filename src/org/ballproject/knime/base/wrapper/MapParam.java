package org.ballproject.knime.base.wrapper;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class MapParam extends Task
{
	private String CLISwitch;
	private String name;
	
	public void execute() throws BuildException
	{
		GenericToolWrapper at = (GenericToolWrapper) this.getProject();
		at.addSwitch(CLISwitch, at.getProperty(name));
	}

	public String getCLISwitch()
	{
		return CLISwitch;
	}

	public void setCLISwitch(String cLISwitch)
	{
		CLISwitch = cLISwitch;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	
}
