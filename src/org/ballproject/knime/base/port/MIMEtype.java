package org.ballproject.knime.base.port;

import java.io.Serializable;

public class MIMEtype implements Serializable
{
	
	protected String type;
	
	public MIMEtype(String type)
	{
		this.type = type;
	}
	
	public String getExt()
	{
		return type;
	}
	
	public static MIMEtype resolveMIMEtype(String filename)
	{
		String toks[] = filename.split("\\.");
		
		//if(toks.length==0||filename.isEmpty())
		//	throw new Exception("filename is invalid");
			
		return new MIMEtype(toks[toks.length-1].toLowerCase());
	}
}
