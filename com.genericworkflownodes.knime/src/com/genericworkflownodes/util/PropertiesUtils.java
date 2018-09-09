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
       throw new AssertionError("Constructor of utility class PropertiesUtils called!");
    }

    /**
     * Loads {@link Properties} from a given {@link File}.
     *
     * @param file The {@link File} from which the {@link Properties} should be read from
     * @return {@link Properties} that are read from the input {@link File}.
     *         The returned {@link Properties} object will be empty if the input {@link File}
     *         is not a properties file.
     * @throws IOException
     *             If method fails to open or read the file.
     * @throws NullPointerException
     *             If {@code file} is null.
     */
    public static Properties load(File file) throws IOException {
        final Properties properties = new Properties();
        if (file.isFile()) {
            try (final FileReader fr = new FileReader(file)) {
                properties.load(fr);
            }
        }
        return properties;
    }

    /**
     * Saves the given {@link Properties} to the given {@link File}. If
     * necessary the {@link File} is automatically created.
     *
     * @param file
     *            The {@link File} where the {@link Properties} should be saved to.
     * @param properties
     *            The {@link Properties} object to save.
     * @throws IOException
     *             If method fails to open or write to the file.
     * @throws NullPointerException
     *             If {@code file} or {@code properties} is null
     */
    public static void save(File file, Properties properties)
            throws IOException {

        // Create safe copy of Properties to prevent concurrency issues when
        // properties are changed while this method is running
        final Properties newProps = new Properties();
        newProps.putAll(properties);

        // Creates the target file and the parent directory of the target
        // file if these files do not already exist
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
        try (final FileWriter fileWriter = new FileWriter(file)) {
            newProps.store(fileWriter, null);
        }
    }
}
