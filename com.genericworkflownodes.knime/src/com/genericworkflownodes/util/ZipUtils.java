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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;


/**
 * Utility class for reading and writing data from and to zip files.
 *
 * @author Lukas Zimmermann
 *
 */
public final class ZipUtils {

    private ZipUtils() {

        throw new AssertionError("Constructor for utility class ZipUtil called!");
    }
    private static final int BUFFSIZE = 1024;
    private static final String DEFAULT_ENTRY_NAME = "rawdata.bin";

    /**
     * Reads data from a zip File using a particular Name for the entry;
     *
     * @param zipFile The {@link File} from which the data should be read
     * @param entryName The name of the {@link ZipEntry}.
     * @return Byte array that contains the data from the {@link ZipEntry} in the {@link File}
     * @throws IOException When opening or reading the input file {@code zipFile} fails.
     */
    public static byte[] read(final File zipFile, final String entryName) throws IOException {

        try (
                final ZipFile zip = new ZipFile(zipFile);
                final InputStream in = zip.getInputStream(zip.getEntry(entryName));
                final ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

            final byte[] data = new byte[BUFFSIZE];
            int nRead;
            while ((nRead = in.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            return buffer.toByteArray();
        }
    }

    /**
     * Reads data from the input zip {@link File} using a default {@link ZipEntry}.
     *
     * @param zipFile {@link File} from which the data should be read
     * @return Byte array that contains the data from the {@link ZipEntry} in the {@link File}
     * @throws IOException IOException When opening or reading the input file {@code zipFile} fails.
     */
    public static byte[] read(final File zipFile) throws IOException {
        return read(zipFile, DEFAULT_ENTRY_NAME);
    }


    /**
     * Writes Byte Array to target {@link File}.
     *
     * @param data The Byte Array that should be written to the file
     * @param file The target {@link File} to which the data should be written to
     * @param entryName The ZIP entry name to which the data should be written to.
     * @throws IOException When writing to the target {@link File} fails.
     */
    public static void write(
            final byte[] data,
            final File file,
            final String entryName) throws IOException {

        try (final ZipOutputStream out = new ZipOutputStream(new FileOutputStream(file))) {

            out.putNextEntry(new ZipEntry(entryName));
            out.write(data);
            out.flush();
            out.closeEntry();
        }
    }

    /**
     * Writes Byte Array to target {@link File}.
     *
     * @param data The Byte Array that should be written to the file
     * @param file The target {@link File} to which the data should be written to
     * @throws IOException When writing to the target {@link File} fails
     */
    public static void write(final byte[] data, final File file) throws IOException {

            write(data, file, DEFAULT_ENTRY_NAME);
    }
}
