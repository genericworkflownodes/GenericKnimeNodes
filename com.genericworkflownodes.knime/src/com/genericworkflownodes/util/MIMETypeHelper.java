/**
 * Copyright (c) 2013, aiche.
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
package com.genericworkflownodes.util;

import java.io.File;
import java.util.Optional;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.knime.base.filehandling.mime.MIMEMap;
import org.knime.base.filehandling.mime.MIMETypeEntry;

/**
 * Helper class to compare and get MIMEtypes from file names.
 *
 * @author aiche
 */
public final class MIMETypeHelper {

    /**
     * Utility class should have private c'tor.
     */
    private MIMETypeHelper() {

        throw new AssertionError("Contructor for utility class MIMETypeHelper called!");
    }

    private static final String EXTENSION_EXECUTABLE = "exe";

    /**
     * Extracts the {@link MIMETypeEntry} from the given filename.
     *
     * @param filename
     *            The file for which the mime type should be extracted.
     * @return An Optional with the MIME type of the file.
     * @throws NullPointerException
     *            If the provided {@code filename} is null
     */
    public static Optional<String> getMIMEtype(String filename) {

        // Ensure that comparison is in lowercase
        filename = filename.toLowerCase();

        // check existing mimetypes
        String type = null;
        String foundExtension = "";
        
        
        Logger.getLogger(org.knime.base.filehandling.mime.MIMEMap.class.getName()).setLevel(Level.OFF);

        for (MIMETypeEntry entry : MIMEMap.getAllTypes()) {
            for (String ext : entry.getExtensions()) {
                if (filename.endsWith(
                        "." + ext.trim().toLowerCase())
                        && ext.length() > foundExtension.length()) {
                    type = entry.getType();
                    foundExtension = ext.trim();
                }
            }
        }

        // If the type is still null, we check if the file is executable
        if (type == null) {
            final File f = new File(filename);

            if (f.canExecute() &&  f.isFile()) {
                return getMIMEtypeByExtension(EXTENSION_EXECUTABLE);
            }
        }

        return Optional.ofNullable(type);
    }

    /**
     * Extracts the {@link MIMETypeEntry} from the given extension.
     *
     * @param extension
     *            The file extension for which the mime type should be
     *            extracted.
     *
     * @return The MIME type of the file.
     * @throws NullPointerException
     *            If the provided {@code extension} is null
     *
     */
    public static Optional<String> getMIMEtypeByExtension(String extension) {

        // Ensure Lowercase comparison
        extension = extension.toLowerCase();

        Logger.getLogger(org.knime.base.filehandling.mime.MIMEMap.class.getName()).setLevel(Level.OFF);
        
        // check existing mimetypes
        for (MIMETypeEntry entry : MIMEMap.getAllTypes()) {
            for (String ext : entry.getExtensions()) {
                // some mimetypes are stored with spaces around them so .trim()
                if (extension.equals(ext.trim())) {
                    return Optional.of(entry.getType());
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Extracts the file extension that was used to get the
     * {@link MIMETypeEntry} from the given filename.
     *
     * @param filename
     *            The file for which the extension should be extracted.
     * @return The MIME type extension of the file, null if no mimetype was
     *         extracted.
     * @throws NullPointerException
     *         If the provided {@code filename} is null.
     */
    public static Optional<String> getMIMEtypeExtension(String filename) {
        // Ensure lowercase comparison
        filename = filename.toLowerCase();

        Logger.getLogger(org.knime.base.filehandling.mime.MIMEMap.class.getName()).setLevel(Level.OFF);
        
        // check existing mimetypes
        String type = null;
        String foundExtension = "";
        for (MIMETypeEntry entry : MIMEMap.getAllTypes()) {
            for (String ext : entry.getExtensions()) {
                if (filename.endsWith(
                        "." + ext.trim().toLowerCase())
                        && ext.length() > foundExtension.length()) {
                    type = entry.getType();
                    foundExtension = ext.trim();
                }
            }
        }
        // If the type is still null, we check if the file is executable
        if (type == null) {
            final File f = new File(filename);

            if (f.canExecute() &&  f.isFile()) {
                return Optional.of(EXTENSION_EXECUTABLE);
            }
        }
        return (type != null ? Optional.of(foundExtension) : Optional.empty());
    }

    /**
     * Extracts the extension from the given path.
     *
     * @param path
     *            The path from which the extension should be extracted.
     * @return The file extension of the given path.
     * @throws NullPointerException
     *            If the provided {@code path} is null.
     */
    public static String getExtension(String path) {
        int lastIndex = path.lastIndexOf('.');
        return lastIndex == -1 ? "" : path.substring(lastIndex + 1);
    }
}
