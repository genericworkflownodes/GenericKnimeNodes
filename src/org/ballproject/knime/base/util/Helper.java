package org.ballproject.knime.base.util;

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
	
	public static void main(String[] args)
	{
		System.out.println(getOS());

	}

}
