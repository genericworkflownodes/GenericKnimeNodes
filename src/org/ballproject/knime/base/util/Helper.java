package org.ballproject.knime.base.util;

import java.io.File;

public class Helper
{
	public static class OS
	{
		public static int WIN  = 0;
		public static int MAC  = 1;
		public static int UNIX = 2;
	}
	
	public static int getOS()
	{
		String os   = System.getProperty("os.name");
		
		// might be to lax
		
		if(os.toLowerCase().contains("nux")||os.toLowerCase().contains("nix"))
		{
			return OS.UNIX;
		}
		if(os.toLowerCase().contains("mac"))
		{
			return OS.MAC;
		}
		
		return OS.WIN;
	}

	public static String getExecutableName(String nodename, String path)
	{
		String test = path+File.separator+nodename; 
		if(new File(test).exists())
			return test;
		
		test = path+File.separator+nodename+".bin";
		if(new File(test).exists())
			return test;
		
		test = path+File.separator+nodename+".exe";
		if(new File(test).exists())
			return test;
		
		return null;
	}

	static public boolean deleteDirectory(File path)
	{
		if (path.exists())
		{
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++)
			{
				if (files[i].isDirectory())
				{
					deleteDirectory(files[i]);
				}
				else
				{
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}
}
