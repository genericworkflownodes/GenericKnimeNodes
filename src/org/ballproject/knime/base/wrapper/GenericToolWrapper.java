/*
 * Copyright (c) 2011, Marc Röttig.
 *
 * This file is part of GenericKnimeNodes.
 * 
 * GenericKnimeNodes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
}
