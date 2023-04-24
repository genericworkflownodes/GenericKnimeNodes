/**
 * Copyright (c) 2022, GKN Team.
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.knime.core.node.NodeLogger;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.custom.config.DLLRegistry;
import com.genericworkflownodes.knime.custom.config.IPluginConfiguration;
import com.genericworkflownodes.util.PlatformUtils;

/**
 * The LocalToolExecutor handles the basic tasks associated with the execution
 * of a tool on the command line.
 * This is specific to OpenMS tools to load the libs and share in OpenMSDLLProvider
 * 
 * @author jpfeuffer
 */
public class OpenMSToolExecutor extends LocalToolExecutor {

    /**
     * NodeLogger used for this executor.
     */
    protected static final NodeLogger LOGGER = NodeLogger
            .getLogger(OpenMSToolExecutor.class);

    @Override
    public void prepareExecution(final INodeConfiguration nodeConfiguration,
            final IPluginConfiguration pluginConfiguration) throws Exception {
        findExecutable(nodeConfiguration, pluginConfiguration);
        ArrayList<String> requiredLibBundles = new ArrayList<String>();
        requiredLibBundles.add("OpenMS");
        
        Map<String, String> addEnv = new HashMap<String, String>();
        for (Path path : DLLRegistry.getDLLRegistry()
                .getAvailableDLLFoldersFor(requiredLibBundles)) {
            // TODO handle multiple providers. Currently it only "uses" the last registered entry.
            if (path.endsWith("lib")) {
                switch (PlatformUtils.getOS()) {
                case WINDOWS:
                    addEnv.put("PATH", path.toString());
                    addEnv.put("Path", path.toString());
                    break;
                case MAC:
                    try {
                        // TODO how to add multiple sources for dylibs without DYLD_LIBRARY_PATH and without hardcoding multiple RPATHs?
                        // We probably need to use install_name_tool --add_rpath, but this feels so hacky.
                        Path liblink = m_executable.toPath().getParent().resolve("../lib");
                        if (liblink.toFile().exists() || Files.isSymbolicLink(liblink)) // might be a dangling softlink if user moved KNIME
                        {
                            liblink.toFile().delete();
                        }
                        Path tgt = path;
                        LOGGER.debug("Trying to create link from " + liblink.toString() + " to " + tgt.toString());
                        Files.createSymbolicLink(liblink, tgt);
                        // DYLD_LIBRARY_PATH is removed from the environments in child processes in non-debug mode since macOS Big Sur.
                        //addEnv.put("DYLD_LIBRARY_PATH", path);
                    } catch(IOException e) {
                        LOGGER.debug("Failed IO");
                        e.printStackTrace();
                    } catch(UnsupportedOperationException e) {
                        LOGGER.debug("Failed UnsupportedOp");
                        e.printStackTrace();
                    } catch(SecurityException e) {
                        LOGGER.debug("Failed Security");
                        e.printStackTrace();
                    }

                    break;
                default: //LINUX, SOLARIS etc.
                    addEnv.put("LD_LIBRARY_PATH", path.toString());
                    break;
                }
            }
            else if (path.endsWith("share"))
            {
              addEnv.put("OPENMS_DATA_PATH", path + System.getProperty("file.separator") + "OpenMS");
            }
        }
        addEnvironmentVariables(addEnv, true);
        super.prepareExecution(nodeConfiguration, pluginConfiguration);
    }
}
