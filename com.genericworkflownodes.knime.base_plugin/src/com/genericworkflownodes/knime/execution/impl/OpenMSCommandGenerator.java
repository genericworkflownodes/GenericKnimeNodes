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

import org.ballproject.knime.base.config.CTDNodeConfigurationWriter;
import org.ballproject.knime.base.config.INodeConfiguration;
import org.ballproject.knime.base.config.NodeConfiguration;
import org.ballproject.knime.base.config.NodeConfigurationStore;

import com.genericworkflownodes.knime.config.IPluginConfiguration;
import com.genericworkflownodes.knime.execution.ICommandGenerator;

/**
 * Implements the OpenMS specific generation of a command line.
 * 
 * @author aiche
 */
public class OpenMSCommandGenerator implements ICommandGenerator {

	private static final String INI_SWITCH = "-ini";
	private static final String INI_FILE_NAME = "params.ini";

	@Override
	public List<String> generateCommands(INodeConfiguration nodeConfiguration,
			NodeConfigurationStore configStore,
			IPluginConfiguration pluginConfiguration, File workingDirectory)
			throws Exception {

		File iniFile = createINIFile(nodeConfiguration, configStore,
				workingDirectory);

		List<String> commands = new ArrayList<String>();
		commands.add(INI_SWITCH);
		commands.add(iniFile.getCanonicalPath());

		return commands;
	}

	/**
	 * Writes an OpenMS-ini-File for the given {@link NodeConfiguration} to the
	 * <code>workingDirectory</code>.
	 * 
	 * @param nodeConfiguration
	 *            Holds parts of the current node status.
	 * @param configStore
	 *            Holds parts of the current node status.
	 * @param workingDirectory
	 *            The directory where the tool will be executed. Make sure that
	 *            the JVM has access to this directory.
	 * @return
	 * @throws IOException
	 */
	private File createINIFile(INodeConfiguration nodeConfiguration,
			NodeConfigurationStore configStore, File workingDirectory)
			throws IOException {
		File iniFile = new File(workingDirectory, INI_FILE_NAME);
		CTDNodeConfigurationWriter ctdWriter = new CTDNodeConfigurationWriter(
				nodeConfiguration.getXML());
		ctdWriter.init(configStore);
		ctdWriter.writeParametersOnly(iniFile);
		return iniFile;
	}
}
