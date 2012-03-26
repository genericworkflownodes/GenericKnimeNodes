package org.ballproject.knime.base.util;

import java.util.List;

public class ExternalToolRunner extends ToolRunner
{
	private String       executablePath;
	private List<String> switches;
	
	public void setExecutablePath(String executablePath)
	{
		this.executablePath = executablePath;
	}
	
	public void setSwitches(List<String> switches)
	{
		this.switches = switches;
	}

	@Override
	public int run(String... cmds) throws Exception
	{
		int K = switches.size();
		int N = 1+K;
		String[] cmds_ = new String[N];
		cmds_[0] = executablePath;
		for(int i=0;i<K;i+=2)
		{
			cmds_[1+i] = switches.get(i);
			cmds_[2+i] = switches.get(i+1);
		}
		return super.run(cmds_);
	}
	
	
}
