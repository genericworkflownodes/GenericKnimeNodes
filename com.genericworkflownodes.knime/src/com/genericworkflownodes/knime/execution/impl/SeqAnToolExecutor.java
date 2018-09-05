package com.genericworkflownodes.knime.execution.impl;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.knime.core.node.NodeLogger;

import com.genericworkflownodes.knime.custom.config.DLLRegistry;

/**
 * A local tool executor for SeqAn nodes that need to register external dlls for
 * the windows binaries.
 *
 * @author rmaerker
 *
 */
public class SeqAnToolExecutor extends LocalToolExecutor {

    /**
     * NodeLogger used for this executor.
     */
    protected static final NodeLogger LOGGER = NodeLogger
            .getLogger(SeqAnToolExecutor.class);

    /**
     * The default constructor.
     */
    public SeqAnToolExecutor() {
        super();
    }

    @Override
    protected void setupProcessEnvironment(ProcessBuilder builder) {
        super.setupProcessEnvironment(builder);

        // Get the dlls from the DLLRegistry.
        String dll_paths = "";
        try {
            for (String dll_path : DLLRegistry.getDLLRegistry()
                    .getAvailableDLLs()) {
                dll_paths += dll_path + File.pathSeparator;
            }
        } catch (CoreException e) {
            LOGGER.error("Could not extract dll search paths.", e);
            return;
        }

        // We would expect the dlls only on Windows.
        if (dll_paths.isEmpty())
            return;

        if (!m_environmentVariables.containsKey("Path")) {
            LOGGER.warn(
                    "Not setting dll search paths! Expected Path environment on Windows systems!");
            return;
        }

        String path_key = "Path";
        m_environmentVariables.put(path_key,
                expandEnvironmentVariables(m_environmentVariables.get(path_key))
                        + File.pathSeparator
                        + dll_paths);
    }
}
