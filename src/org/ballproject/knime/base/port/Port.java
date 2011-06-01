package org.ballproject.knime.base.port;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Port implements Serializable
{

	protected boolean  isOptional;
	protected String   name;
	protected String   description;
		
	protected List<MIMEtype> types = new ArrayList<MIMEtype>();
	
	public void addMimeType(MIMEtype type)
	{
		types.add(type);
	}
	
	public List<MIMEtype> getMimeTypes()
	{
		return types;
	}
	
	public boolean isOptional()
	{
		return isOptional;
	}

	public void setOptional(boolean isOptional)
	{
		this.isOptional = isOptional;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

}
