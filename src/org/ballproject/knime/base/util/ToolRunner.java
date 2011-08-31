package org.ballproject.knime.base.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;


public class ToolRunner
{
	private File   jobdir;
	private int    retcode;
	private Process p;
	
	private Map<String,String> env = new HashMap<String,String>();
	
	private String output;
	
	public ToolRunner()
	{
	}
	
	public void addEnvironmentEntry(String key, String value)
	{
		this.env.put(key, value);
	}
	
	public void setEnvironment(Map<String,String> env)
	{
		this.env = env;
	}
	
	public void setJobDir(String path)
	{
		jobdir = new File(path);
	}
	
	public int getReturnCode()
	{
		return retcode;
	}
	
	public String getOutput()
	{
		return output;
	}
	
	public int run(String... cmds) throws Exception
	{
		List<String> opts = new ArrayList<String>();

		for(String cmd: cmds)
			opts.add(cmd);

		try
		{
			// build process
			ProcessBuilder builder = new ProcessBuilder(opts);

			for(String key: env.keySet())
			{
				builder.environment().put(key, env.get(key));
			}

			builder.redirectErrorStream(true);

			if(jobdir!=null)
				builder.directory( jobdir );

			// execute
			p = builder.start();


			// fetch output data (stdio+stderr)
			InputStreamReader isr = new InputStreamReader(p.getInputStream());
			BufferedReader    br  = new BufferedReader(isr);

			String line = null;
			StringBuffer out = new StringBuffer();

			while ( (line = br.readLine()) != null)
			{
				out.append(line+System.getProperty("line.separator"));
			}

			output = out.toString();

			// fetch return code
			retcode = p.waitFor();
		}
		catch(Exception e)
		{
			
		}
		
		return retcode;
	}
	
	public void kill()
	{
		p.destroy();
	}
	
	public static class AsyncToolRunner implements Callable<Integer>
	{
		private ToolRunner tr = new ToolRunner();
		private String[]   cmds = new String[]{};
		
		public ToolRunner getToolRunner()
		{
			return tr;
		}
		
		public AsyncToolRunner(String... cmds)
		{
			this.cmds = cmds;
		}
		
		@Override
		public Integer call() throws Exception
		{
			try
			{
				tr.run(cmds);
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

}
