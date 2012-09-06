/**
 * Copyright (c) 2012, Stephan Aiche.
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
package com.genericworkflownodes.knime.execution.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.genericworkflownodes.knime.config.CTDNodeConfigurationWriter;
import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.config.INodeConfigurationStore;
import com.genericworkflownodes.knime.config.IPluginConfiguration;
import com.genericworkflownodes.knime.execution.ICommandGenerator;

/**
 * Implements the BALL specific generation of a command line.
 * 
 * @author aiche
 */
public class BALLCommandGenerator implements ICommandGenerator {

	private static final String PAR_SWITCH = "-par";
	private static final String PAR_FILE_NAME = "params.xml";

	@Override
	public List<String> generateCommands(INodeConfiguration nodeConfiguration,
			INodeConfigurationStore configStore,
			IPluginConfiguration pluginConfiguration, File workingDirectory)
			throws Exception {

		File paramFile = writePARFile(nodeConfiguration, configStore,
				workingDirectory);

		List<String> commands = new ArrayList<String>();
		commands.add(PAR_SWITCH);
		commands.add(paramFile.getCanonicalPath());

		return commands;
	}

	/**
	 * @param nodeConfiguration
	 * @param configStore
	 * @param workingDirectory
	 * @return
	 * @throws IOException
	 */
	private File writePARFile(INodeConfiguration nodeConfiguration,
			INodeConfigurationStore configStore, File workingDirectory)
			throws IOException {
		File paramFile = new File(workingDirectory, PAR_FILE_NAME);

		CTDNodeConfigurationWriter ctdWriter = new CTDNodeConfigurationWriter(
				nodeConfiguration.getXML());
		ctdWriter.init(configStore, nodeConfiguration);
		ctdWriter.write(paramFile);
		return paramFile;
	}

}
