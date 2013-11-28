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
package com.genericworkflownodes.knime.execution;

import java.io.File;
import java.util.List;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.config.IPluginConfiguration;

/**
 * Create based on a {@link INodeConfiguration} and list of arguments that
 * should be send to a custom tool executor.
 * 
 * @author aiche
 */
public interface ICommandGenerator {

    /**
     * Create a list of command line arguments needed to execute the associated
     * node.
     * 
     * @param nodeConfiguration
     * @param pluginConfiguration
     * @param workingDirectory
     *            The directory where the tool will be executed. Make sure that
     *            the JVM has access to this directory. This gets particularly
     *            important if the process will be executed on a different host.
     * @return The generated series of command line arguments.
     * @throws Exception
     *             An exception is thrown if the construction fails.
     */
    List<String> generateCommands(INodeConfiguration nodeConfiguration,
            IPluginConfiguration pluginConfiguration, File workingDirectory)
            throws Exception;

}
