package org.ballproject.knime.base.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ballproject.knime.base.util.Helper.OS;

public class ToolRunner
{
	
	private String shell      = "/bin/sh";
	private String shell_opts = "-c";
	private String command    = "";
	private File   jobdir;
	private int    retcode;
	
	private Map<String,String> env = new HashMap<String,String>();
	
	private String output;
	
	public ToolRunner()
	{
		if(Helper.getOS()==OS.MAC || Helper.getOS()==OS.UNIX)
		{
			shell      = "/bin/sh";
			shell_opts = "-c";
		}
		else
		{
			// FixMe
			shell      = "command.com";
			shell_opts ="/C";
		}
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
		opts.add(shell);
		opts.add(shell_opts);
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
			Process p = builder.start();


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
	
	
	public static void main(String[] args) throws Exception
	{
		ToolRunner tr = new ToolRunner();
		tr.addEnvironmentEntry("LD_LIBRARY_PATH", "/tmp/CADD/lib");
		tr.setJobDir("/tmp/CADD");
		tr.run("bin/GridBuilder.bin","-write_par /tmp/raus.xml");
		
		System.out.println(tr.getReturnCode());
		System.out.println(tr.getOutput());

	}

}
