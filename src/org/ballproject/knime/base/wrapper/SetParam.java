package org.ballproject.knime.base.wrapper;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

public class SetParam extends Task
{
	private String name;
	private String value;
	
	public void execute() throws BuildException
	{
		GenericToolWrapper at = (GenericToolWrapper) this.getProject();
		at.addSwitch(name, value);
	}
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	@Override
	public Project getProject()
	{
		return super.getProject();
	}	
}
