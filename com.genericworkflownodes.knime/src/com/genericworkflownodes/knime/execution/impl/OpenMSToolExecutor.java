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
import java.nio.file.Files;
import java.nio.file.Path;
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
        String sep = System.getProperty("file.separator");
        
        Map<String, String> addEnv = new HashMap<String, String>();
        for (String path : DLLRegistry.getDLLRegistry()
                .getAvailableDLLFoldersFor(requiredLibBundles)) {
            if (path.endsWith("lib")) {
                switch (PlatformUtils.getOS()) {
                case WINDOWS:
                    addEnv.put("PATH", path);
                    addEnv.put("Path", path);
                    break;
                case MAC:
                    File liblink = new File(m_executable.getParent()+sep+".."+sep+"lib");
                    File tgt = new File(path);
                    Files.createSymbolicLink(liblink.toPath(), tgt.toPath());
                    //addEnv.put("DYLD_LIBRARY_PATH", path);
                    break;
                default: //LINUX, SOLARIS etc.
                    addEnv.put("LD_LIBRARY_PATH", path);
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
