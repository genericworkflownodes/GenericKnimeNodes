package org.ballproject.knime.base.util;

import java.util.concurrent.Callable;

public class AsyncToolRunner implements Callable<Integer>
{
	private ToolRunner tr;
	
	public ToolRunner getToolRunner()
	{
		return tr;
	}
	
	public AsyncToolRunner(ToolRunner tr)
	{
		this.tr = tr;
	}
	
	@Override
	public Integer call() throws Exception
	{
		try
		{
			tr.run();
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return tr.getReturnCode();
	}
	
	public void kill()
	{
		tr.kill();
	}
}