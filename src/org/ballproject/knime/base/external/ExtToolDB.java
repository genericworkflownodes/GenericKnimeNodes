package org.ballproject.knime.base.external;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtToolDB
{
	private Map<ExternalTool,String> tool2path = new HashMap<ExternalTool,String>();

	public static ExtToolDB instance = null;
	
	public static ExtToolDB getInstance()
	{
		if(instance==null)
			instance = new ExtToolDB();
		return instance;
	}
	
	private ExtToolDB()
	{
	}
	
	public void registerTool(ExternalTool tool)
	{
		tool2path.put(tool, "");
	}
	
	public Collection<ExternalTool> getTools()
	{
		return tool2path.keySet();
	}
	
	public void setToolPath(ExternalTool tool, String path)
	{
		tool2path.put(tool, path);
	}
	
	public String getToolPath(ExternalTool tool)
	{
		String path = tool2path.get(tool);
		return path;
	}
	
	public Map<String,List<ExternalTool>> getToolsByPlugin()
	{
		Map<String,List<ExternalTool>> plugin2tools = new HashMap<String,List<ExternalTool>>();
		for(ExternalTool tool: tool2path.keySet())
		{
			if(!plugin2tools.containsKey(tool.getPluginname()))
				plugin2tools.put(tool.getPluginname(), new ArrayList<ExternalTool>());
			plugin2tools.get(tool.getPluginname()).add(tool);
		}
		return plugin2tools;
	}
	
	public static final class ExternalTool
	{
		private String toolname;
		private String pluginname;
		
		public ExternalTool(String pluginname, String toolname)
		{
			this.pluginname = pluginname;
			this.toolname   = toolname;
		}
		
		public String getToolname()
		{
			return toolname;
		}
		
		public String getPluginname()
		{
			return pluginname;
		}
		
		public String getKey()
		{
			return String.format("%s_%s",this.pluginname,this.toolname);
		}

		@Override
		public boolean equals(Object obj)
		{
			if(obj==this)
				return true;
			if(obj==null||!(obj instanceof ExternalTool))
			{
				return false;
			}
			ExternalTool eTool = (ExternalTool) obj;
			return (pluginname.equals(eTool.pluginname)&&toolname.equals(eTool.toolname));
		}

		@Override
		public int hashCode()
		{
			return pluginname.hashCode() ^ toolname.hashCode();
		}
		
		public String toString()
		{
			return String.format("%s_%s", pluginname, toolname);
		}
	}
}
