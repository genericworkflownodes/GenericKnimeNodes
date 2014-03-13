/**
 * Copyright (c) 2013, Björn Kahlert, Stephan Aiche.
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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 * Utility class to easily handle {@link Properties}.
 * 
 * @author bkahlert
 */
public final class PropertiesUtils {

    /**
     * Utility class should have private c'tor.
     */
    private PropertiesUtils() {
    }

    /**
     * Loads {@link Properties} from a given {@link File}.
     * 
     * @param file
     * @return empty if file is no {@link File}
     * @throws IOException
     *             If method fails to open or read the file.
     */
    public static Properties load(File file) throws IOException {
        Properties properties = new Properties();
        if (file.isFile()) {
            FileReader fr = new FileReader(file);
            try {
                properties.load(fr);
            } catch (IOException ex) {
                // close file reader and rethrow
                fr.close();
                throw ex;
            }
            // ensure closed file reader
            fr.close();
        }
        return properties;
    }

    /**
     * Saves the given {@link Properties} to the given {@link File}. If
     * necessary the {@link File} is automatically created.
     * 
     * @param file
     *            The file where the object should be saved.
     * @param properties
     *            The {@link Properties} object to save.
     * @throws IOException
     *             If method fails to open or write to the file.
     */
    public static void save(File file, Properties properties)
            throws IOException {
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                boolean mkdirs = file.getParentFile().mkdirs();
                if (!mkdirs) {
                    throw new IOException(String.format(
                            "Failed to create parent directories of file %s",
                            file.getAbsolutePath()));
                }
            }
            boolean createFile = file.createNewFile();
            if (!createFile) {
                throw new IOException(String.format("Failed to create file %s",
                        file.getAbsolutePath()));
            }
        }
        FileWriter f_writer = new FileWriter(file);
        try {
            properties.store(f_writer, null);
        } catch (IOException ex) {
            // close file writer and rethrow
            f_writer.close();
            throw ex;
        }
        f_writer.close();
    }
}
