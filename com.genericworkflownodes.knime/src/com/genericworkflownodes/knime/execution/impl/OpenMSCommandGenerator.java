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
import java.util.ArrayList;
import java.util.List;

import com.genericworkflownodes.knime.commandline.CommandLineElement;
import com.genericworkflownodes.knime.commandline.impl.CommandLineCTDFile;
import com.genericworkflownodes.knime.commandline.impl.CommandLineOptionIdentifier;
import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.config.NodeConfiguration;
import com.genericworkflownodes.knime.config.writer.CTDConfigurationWriter;
import com.genericworkflownodes.knime.custom.config.IPluginConfiguration;
import com.genericworkflownodes.knime.execution.ICommandGenerator;

/**
 * Implements the OpenMS specific generation of a command line.
 * 
 * @author aiche
 */
public class OpenMSCommandGenerator implements ICommandGenerator {

    /**
     * The command line switch needed to provide a ini file.
     */
    private static final String INI_SWITCH = "-ini";
    /**
     * The name of the generated ini file.
     */
    private static final String INI_FILE_NAME = "params.ini";

    @Override
    public List<CommandLineElement> generateCommands(
            INodeConfiguration nodeConfiguration,
            IPluginConfiguration pluginConfiguration, File workingDirectory)
            throws Exception {

        File iniFile = createINIFile(nodeConfiguration, workingDirectory);

        List<CommandLineElement> commands = new ArrayList<CommandLineElement>();
        commands.add(new CommandLineOptionIdentifier(INI_SWITCH));
        commands.add(new CommandLineCTDFile(iniFile));

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
     * @return The {@link File} where the ini file was stored.
     * @throws Exception
     */
    private File createINIFile(INodeConfiguration nodeConfiguration,
            File workingDirectory) throws Exception {
        File iniFile = new File(workingDirectory, INI_FILE_NAME);
        CTDConfigurationWriter writer = new CTDConfigurationWriter(iniFile);
        writer.write(nodeConfiguration);
        return iniFile;
    }
}
