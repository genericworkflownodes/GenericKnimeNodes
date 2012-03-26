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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.ballproject.knime.base.config.INodeConfiguration;
import org.ballproject.knime.base.config.NodeConfigurationStore;
import org.ballproject.knime.base.util.Helper;

public class GenericToolWrapper extends Project {
	private Map<String, String> switches = new HashMap<String, String>();

	public void addSwitch(String name, String value) {
		switches.put(name, value);
	}

	public Map<String, String> getSwitches() {
		return switches;
	}

	public GenericToolWrapper(INodeConfiguration config,
			NodeConfigurationStore store) throws IOException {
		File buildFile = prepareFile(config.getMapping());
		setUserProperty("ant.file", buildFile.getAbsolutePath());

		for (String key : store.getParameterKeys()) {
			String value = store.getParameterValue(key);
			setProperty(key, value);
		}

		// ANT stuff
		init();
		ProjectHelper helper = ProjectHelper.getProjectHelper();
		addReference("ant.projectHelper", helper);
		helper.parse(this, buildFile);
		executeTarget(getDefaultTarget());
	}

	private File prepareFile(String commands) throws IOException {
		// TODO remove ant based CLI mapping
		StringWriter writer = new StringWriter();
		IOUtils.copy(this.getClass().getResourceAsStream("build.xml"), writer,
				"UTF-8");
		String antBuildFileString = writer.toString();
		antBuildFileString = antBuildFileString.replace("<!-- __TASKS__ -->",
				commands);

		File file = new File(Helper.getTemporaryFilename("buildxml", true));
		FileOutputStream out = new FileOutputStream(file);
		out.write(antBuildFileString.getBytes());
		out.close();
		return file;
	}

	public List<String> getSwitchesList() {
		List<String> ret = new ArrayList<String>();
		for (String key : switches.keySet()) {
			if (!key.startsWith("-"))
				ret.add("-" + key);
			else
				ret.add(key);
			ret.add(switches.get(key));
		}
		return ret;
	}
}
