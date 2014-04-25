/**
 * Copyright (c) 2014, Stephan Aiche.
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
package com.genericworkflownodes.knime.custom.config;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.FileLocator;
import org.knime.core.node.NodeLogger;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.genericworkflownodes.util.PropertiesUtils;

/**
 * @author aiche
 */
public final class BinaryManager {

    /**
     * The logger.
     */
    private static final NodeLogger LOGGER = NodeLogger
            .getLogger(BinaryManager.class);

    /**
     * Path inside the bundle where the binaries should be located.
     */
    private static final String BUNDLE_PATH = "payload";

    /**
     * File that should be present to identify the correct path.
     */
    private static final String BINARIES_INI = "binaries.ini";

    /**
     * String constant that will be replaced in binaries.ini values with the
     * path to the binaries.ini
     */
    private static final String ROOT_REPLACEMENT = "$ROOT";

    @SuppressWarnings("rawtypes")
    private final Class classInBundle;

    /**
     * C'tor.
     * 
     * @param clazzInBundle
     *            A class inside the bundle where the files should be located.
     */
    @SuppressWarnings("rawtypes")
    public BinaryManager(Class clazzInBundle) {
        classInBundle = clazzInBundle;
    }

    public File findBinary(final String executableName)
            throws NoBinaryAvailableException {
        // first try to find the shipped binary
        File shippedBinary = findShippedBinary(executableName);
        if (shippedBinary != null) {
            return shippedBinary;
        } else {
            throw new NoBinaryAvailableException(executableName);
        }
    }

    public boolean isShippedBinary(final String executableName) {
        return findShippedBinary(executableName) != null;
    }

    /**
     * Returns a set of environment variables required by the executable. Will
     * be an empty map if we use the system version of the tool.
     * 
     * @param executableName
     *            The name of the executable for which the process environment
     *            should be returned.
     * @return A map containing for each environment variable name the
     *         corresponding value.
     */
    public Map<String, String> getProcessEnvironment(final String executableName) {
        Map<String, String> environmentVariables = new HashMap<String, String>();

        // we only fill the environment variables if the shipped binary is used
        if (findShippedBinary(executableName) == null) {
            return environmentVariables;
        }

        // find binaries.ini
        File iniFile = findFileInBundle(BINARIES_INI);

        // check if the requested file exists
        if (iniFile == null) {
            return environmentVariables;
        }

        // path were ini is located == $ROOT path of the binaries
        final String rootPath = iniFile.getParent();

        // load the properties file
        Properties envProperites;
        try {
            envProperites = PropertiesUtils.load(iniFile);
            for (Object key : envProperites.keySet()) {
                String k = key.toString();
                String v = envProperites.getProperty(k);

                // fix value
                if (v.contains(ROOT_REPLACEMENT)) {
                    v.replace(ROOT_REPLACEMENT, rootPath);
                }

                // transfer the environment variables into the generic activator
                environmentVariables.put(k, v);
            }
        } catch (IOException e) {
            LOGGER.warn(
                    String.format("Failed to load properties file %s",
                            iniFile.getAbsolutePath()), e);
        }

        return environmentVariables;
    }

    private File findShippedBinary(final String executableName) {
        return findFileInBundle(executableName);
    }

    /**
     * Search the bundle for the given file name.
     * 
     * @param fileName
     *            The name of the file to find.
     * @return A File object pointing to the requested file or null if the file
     *         wasn't found.
     */
    private File findFileInBundle(final String fileName) {
        Bundle bundle = FrameworkUtil.getBundle(classInBundle);
        Enumeration<URL> e = bundle.findEntries(BUNDLE_PATH, fileName, true);

        if (!e.hasMoreElements()) {
            return null;
        } else {
            try {
                // we found the binaries.ini
                URL url = e.nextElement();
                return new File(FileLocator.toFileURL(url).getFile());
            } catch (IOException ex) {
                LOGGER.info("Could not locate packaged executable", ex);
                return null;
            }
        }
    }
}
