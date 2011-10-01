package org.ballproject.knime.base.wrapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.ballproject.knime.base.config.CTDNodeConfigurationReader;
import org.ballproject.knime.base.config.DefaultNodeConfigurationStore;
import org.ballproject.knime.base.config.NodeConfiguration;
import org.ballproject.knime.base.config.NodeConfigurationStore;
import org.ballproject.knime.base.util.Helper;
import org.ballproject.knime.nodegeneration.TemplateFiller;
import org.ballproject.knime.test.data.TestDataSource;

public class GenericToolWrapper extends Project
{
	private Map<String,String> switches = new HashMap<String,String>();
	
	public void addSwitch(String name, String value)
	{
		switches.put(name,value);
	}
	
	public Map<String,String> getSwitches()
	{
		return switches;
	}
	
	public GenericToolWrapper(NodeConfiguration config, NodeConfigurationStore store)
	{
		File buildFile = prepareFile(config.getMapping());
		setUserProperty("ant.file", buildFile.getAbsolutePath());
		
		for(String key: store.getParameterKeys())
		{
			System.out.println("setting property "+key);
			String value = store.getParameterValue(key);
			setProperty(key,value);
		}
		
		
		// ANT stuff
		init();
		ProjectHelper helper = ProjectHelper.getProjectHelper();
		addReference("ant.projectHelper", helper);
		helper.parse(this, buildFile);
		executeTarget(getDefaultTarget());
	}
	
	private File prepareFile(String commands)
	{
		String filename = null;
		TemplateFiller tf = new TemplateFiller();
		try
		{
			filename = Helper.getTemporaryFilename("buildxml", true);
			tf.read(this.getClass().getResourceAsStream("build.xml"));
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		tf.replace("<!-- __TASKS__ -->", commands);
		try
		{
			tf.write(filename);
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return new File(filename);
	}
	
	public List<String> getSwitchesList()
	{
		List<String> ret = new ArrayList<String>();
		for(String key : switches.keySet())
		{
			if(!key.startsWith("-"))
				ret.add("-"+key);
			else
				ret.add(key);
			ret.add(switches.get(key));
		}
		return ret;
	}
	
	public static void main(String[] args) throws Exception
	{
		String cmds = "<if>\n<matches string=\"${1.p}\" pattern=\"^$\"/>\n<then>\n<SetParam name=\"baz\" value=\"fuzz\"/>\n</then>\n</if>\n";
		
		NodeConfiguration config = null;
		CTDNodeConfigurationReader reader = new CTDNodeConfigurationReader();
		config = reader.read(TestDataSource.class.getResourceAsStream("test5.ctd"));
		
		NodeConfigurationStore store = new DefaultNodeConfigurationStore();
		store.setParameterValue("bl2seq.i", "in1.FASTA");
		store.setParameterValue("bl2seq.j", "in2.FASTA");
		
		GenericToolWrapper at = new GenericToolWrapper(config,store);
		
		for(String key: at.getSwitches().keySet())
		{
			System.out.println(key+" -> "+at.getSwitches().get(key));
		}
	}
}
