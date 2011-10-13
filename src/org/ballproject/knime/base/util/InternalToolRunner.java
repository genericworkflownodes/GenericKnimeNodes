package org.ballproject.knime.base.util;

public class InternalToolRunner extends ToolRunner
{
	private String paramSwitch;
	private String executablePath;
	
	public void setParamSwitch(String paramSwitch)
	{
		this.paramSwitch = paramSwitch;
	}
	
	public void setExecutablePath(String executablePath)
	{
		this.executablePath = executablePath;
	}
	
	@Override
	public int run(String... cmds) throws Exception
	{
		String[] cmds_ = new String[3];
		
		cmds_[0] = executablePath;
		cmds_[1] = paramSwitch;
		cmds_[2] = "params.xml";
		
		return super.run(cmds_);
	}
}
