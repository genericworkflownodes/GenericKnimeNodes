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

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;

import org.knime.base.filehandling.mime.MIMEMap;
import org.knime.base.filehandling.mime.MIMETypeEntry;

/**
 * Helper class to compare and get MIMEtypes from file names.
 * 
 * @author aiche
 */
public class MIMETypeHelper {

    private static final String UNKNOWN_MIMETYPE = "UNKNOWN";
    /**
     * Utility class should have private c'tor.
     */
    private MIMETypeHelper() {
    }

    /**
     * Extracts the {@link MIMETypeEntry} from the given filename.
     * 
     * @param filename
     *            The file for which the mime type should be extracted.
     * @return The MIME type of the file. If not found in registry
     * returns shortest extension of the file
     */
    public static String getMIMEtype(String filename) {
        // check existing mimetypes
        String type = "";
        String foundExtension = "";
        for (MIMETypeEntry entry : MIMEMap.getAllTypes()) {
            for (String ext : entry.getExtensions()) {
                if (filename.toLowerCase().endsWith(
                        "." + ext.trim().toLowerCase())
                        && ext.length() > foundExtension.length()) {
                    type = entry.getType();
                    foundExtension = ext.trim();
                }
            }
        }
        if (foundExtension.isEmpty())
        {
            //Fallback, use shortest extension as temporary mimetype
            return filename.substring(filename.lastIndexOf('.') + 1);
        }
        return type;
    }

    /**
     * Extracts the {@link MIMETypeEntry} from the given extension.
     * 
     * @param extension
     *            The file extension for which the mime type should be
     *            extracted.
     * 
     * @return The MIME type of the file. If not found in registry,
     *  returns the extension itself
     */
    public static String getMIMEtypeByExtension(String extension) {
        // check existing mimetypes and pick first
        for (MIMETypeEntry entry : MIMEMap.getAllTypes()) {
            for (String ext : entry.getExtensions()) {
                // some mimetypes are stored with spaces around them so .trim()
                if (extension.toLowerCase().equals(ext.trim())) {
                    return entry.getType();
                }
            }
        }
        //Fallback, return extension itself
        return extension;
    }

    /**
     * Extracts the file extension that was used to get the
     * {@link MIMETypeEntry} from the given filename.
     * 
     * @param filename
     *            The file for which the extension should be extracted.
     * @return The MIME type extension of the file, null if no mimetype was
     *         extracted.
     */
    public static String getMIMEtypeExtension(String filename) {
        // check existing mimetypes
        String type = "";
        String foundExtension = "";
        for (MIMETypeEntry entry : MIMEMap.getAllTypes()) {
            for (String ext : entry.getExtensions()) {
                if (filename.toLowerCase().endsWith(
                        "." + ext.trim().toLowerCase())
                        && ext.length() > foundExtension.length()) {
                    type = entry.getType();
                    foundExtension = ext.trim();
                }
            }
        }
        if (foundExtension.isEmpty())
        {
            //Fallback, use shortest extension as temporary mimetype
            return filename.substring(filename.lastIndexOf('.') + 1);
        }
        return type;
    }

    /**
     * Extracts the extension from the given path.
     * 
     * @param path
     *            The path from which the extension should be extracted.
     * @return The file extension of the given path.
     */
    public static String getExtension(String path) {
        if (path.lastIndexOf('.') == -1) {
            return "";
        } else {
            return path.substring(path.lastIndexOf('.') + 1);
        }
    }
}
