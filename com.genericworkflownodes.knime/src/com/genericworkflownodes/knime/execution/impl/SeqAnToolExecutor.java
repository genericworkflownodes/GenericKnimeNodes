package com.genericworkflownodes.knime.execution.impl;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

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
     * Defines the name of the PATH environment variable.
     */
    private static final String PATH_ENVIRONMENT = "PATH";

    /**
     * Defines an alternative name of the PATH environment variable for windows.
     */
    private static final String PATH_ENVIRONMENT_WINDOWS = "Path";

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

    // TODO somehow create an abstract class DLLLoadingToolExecutor to merge with the OpenMSToolExecutor
    @Override
    protected void setupProcessEnvironment(ProcessBuilder builder) {
        ArrayList<String> requiredLibBundles = new ArrayList<String>();
        requiredLibBundles.add("SeqAn");
        
        // Get the dlls from the DLLRegistry.
        StringBuilder lib_paths = new StringBuilder();
        try {
            for (Path lib_path : DLLRegistry.getDLLRegistry()
                    .getAvailableDLLFoldersFor(requiredLibBundles)) {
                lib_paths.append(lib_path).append(File.pathSeparator);
            }
        } catch (CoreException e) {
            LOGGER.error("Could not extract lib search paths.", e);
            return;
        }
    
        if (lib_paths.length() != 0) {
            String lib_paths_str = lib_paths.toString();
            LOGGER.debug("Adding lib paths: " + lib_paths_str);
            addLibsToPathEnvironment(lib_paths_str, PATH_ENVIRONMENT);
    
            if (System.getProperty("os.name").startsWith("Windows")) {
                addLibsToPathEnvironment(lib_paths_str,
                        PATH_ENVIRONMENT_WINDOWS);
            }
        }
        super.setupProcessEnvironment(builder);
    }

    /**
     * Adds the passed lib paths to the given path environment.
     *
     * @param lib_paths
     *            The paths for the shared libs to be added to the process
     *            environment.
     * @param path_key
     *            The key of the path environment variable.
     */
    private void addLibsToPathEnvironment(String lib_paths,
            String path_key) {
        String pathWithLibs = "";
        if (m_environmentVariables.containsKey(path_key)) {
            pathWithLibs = m_environmentVariables.get(path_key)
                    + File.pathSeparator;
        }
        pathWithLibs += lib_paths;
        m_environmentVariables.put(path_key, pathWithLibs);
    }
}
