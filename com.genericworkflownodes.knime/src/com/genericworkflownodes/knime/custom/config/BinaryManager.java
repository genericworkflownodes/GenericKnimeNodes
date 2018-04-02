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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
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
     * Path inside the bundle where the descriptors should be located.
     */
    private static final String DESCRIPTORS_PATH = BUNDLE_PATH + File.separator + "descriptors";

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
        // try to find the shipped binary
        File shippedBinary = findShippedBinary(executableName);
        if (shippedBinary != null) {
            return shippedBinary;
        } else {
            throw new NoBinaryAvailableException(executableName);
        }
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
                    v = v.replace(ROOT_REPLACEMENT, rootPath);
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
        File shippedBinary = findFileInBundle(executableName);
        if (shippedBinary != null) {
            return shippedBinary;
        }

        // try to find the shipped binary again, but this time with the
        // addition of ".exe" for windows platform
        shippedBinary = findFileInBundle(String
                .format("%s.exe", executableName));
        if (shippedBinary != null) {
            return shippedBinary;
        }

        // and another attempt using ".bat" as extension
        shippedBinary = findFileInBundle(String
                .format("%s.bat", executableName));
        if (shippedBinary != null) {
            return shippedBinary;
        } else {
            return null;
        }
    }
    
    public File resolveToolDescriptorPath(final String relToolPath) {
        Bundle bundle = FrameworkUtil.getBundle(classInBundle);
        try {
            return new File(FileLocator.toFileURL(bundle.getResource(DESCRIPTORS_PATH + File.separator + relToolPath)).getFile());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return null;
        }
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

        if (e == null || !e.hasMoreElements()) {
            return null;
        } else {
            try {
                // we found the requested file
                URL url = e.nextElement();
                return new File(FileLocator.toFileURL(url).getFile());
            } catch (IOException ex) {
                LOGGER.info("Could not locate packaged executable", ex);
                return null;
            }
        }
    }
    
    /**
     * Search the bundle for CTDs and list them in a List of Files.
     * 
     * @return List of CTD Files in the bundle
     * @throws URISyntaxException 
     */
    public Iterable<String> listTools() {
        Bundle bundle = FrameworkUtil.getBundle(classInBundle);
        Enumeration<URL> e = bundle.findEntries(DESCRIPTORS_PATH, "*.ctd", true);
        ArrayList<String> files = new ArrayList<>();
        Path p;
        try {
            p = Paths.get(FileLocator.toFileURL(bundle.getResource(DESCRIPTORS_PATH)).toString());
        } catch (Exception ex) {
            LOGGER.error(ex);
            return Collections.emptyList();
        }
        while (e.hasMoreElements()){
            try {
                Path el = Paths.get(FileLocator.toFileURL(e.nextElement()).toString());
                LOGGER.info("Loading CTD from " + el.toString());
                files.add(p.relativize(el).toString());
            } catch (IOException e1) {
                LOGGER.error(e1);
            }
        }
        return files;
    }
}
