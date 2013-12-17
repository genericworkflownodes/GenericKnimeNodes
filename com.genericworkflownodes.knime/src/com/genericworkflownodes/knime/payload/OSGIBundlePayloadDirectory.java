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
package com.genericworkflownodes.knime.payload;

import java.io.File;
import java.io.FileFilter;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;

/**
 * A payload directory abstraction that uses the private bundle storage area
 * provided by the OSGI framework ({@link BundleContext#getDataFile(String)}) to
 * store the payload persistently. It also checks on startup if previouse
 * installations are still available in the same directory.
 * 
 * @author aiche
 */
public class OSGIBundlePayloadDirectory extends AbstractPayloadDirectory
        implements IPayloadDirectory {

    private static final class VersionDirectoriesFilter implements FileFilter {
        @Override
        public boolean accept(File fileToTest) {
            if (fileToTest.isDirectory()) {
                return (Version.parseVersion(fileToTest.getName()) != Version.emptyVersion);
            } else {
                return false;
            }
        }
    }

    private static final String PAYLOAD_DIR_NAME = "payload";

    private BundleContext pluginBundleContext;
    private File pluginPayloadDirectory;
    private File pluginVersionedPayloadDirectory;

    /**
     * Constructs a payload area inside the private data area of the current
     * package.
     * 
     * @param bundleContext
     *            The OSGI bundle context.
     */
    public OSGIBundlePayloadDirectory(BundleContext bundleContext) {
        pluginBundleContext = bundleContext;

        File root = pluginBundleContext.getDataFile("");
        pluginPayloadDirectory = new File(root, PAYLOAD_DIR_NAME);
        pluginVersionedPayloadDirectory = new File(pluginPayloadDirectory,
                getVersion().toString());

        // ensure that the directory exists
        pluginVersionedPayloadDirectory.mkdirs();

        cleanUpBundleStorageArea();
    }

    /**
     * Check if previous versions of the payload exist and delete it if they
     * exist.
     */
    private void cleanUpBundleStorageArea() {

        File[] versionedDirectories = pluginPayloadDirectory
                .listFiles(new VersionDirectoriesFilter());

        if (versionedDirectories != null) {
            for (File versionedPayloadDirectory : versionedDirectories) {
                Version v = Version.parseVersion(versionedPayloadDirectory
                        .getName());
                if (v != getVersion() && v.compareTo(getVersion()) < 0) {
                    deleteDirectory(versionedPayloadDirectory);
                }
            }
        }
    }

    private Version getVersion() {
        return pluginBundleContext.getBundle().getVersion();
    }

    @Override
    public File getPath() {
        return pluginVersionedPayloadDirectory;
    }

    /**
     * Helper method to recursively delete a directory including its content
     * (taken from <a
     * href="http://www.rgagnon.com/javadetails/java-0483.html">here</a>)
     * 
     * @param path
     *            The directory to delete.
     * @return $true$ if the directory was deleted, $false$ otherwise.
     */
    private static boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }
}
