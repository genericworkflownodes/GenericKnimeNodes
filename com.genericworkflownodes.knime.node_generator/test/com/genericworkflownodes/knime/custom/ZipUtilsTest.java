/**
 * Copyright (c) 2012-2014, Stephan Aiche.
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
package com.genericworkflownodes.knime.custom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.genericworkflownodes.knime.nodegeneration.util.UnZipFailureException;
import com.genericworkflownodes.knime.nodegeneration.util.ZipUtils;

/**
 * Test for {@link ZipUtils}.
 * 
 * @author aiche
 */
public class ZipUtilsTest {

    public static File createTempDirectory() throws IOException {
        final File temp;

        temp = File.createTempFile("temp", Long.toString(System.nanoTime()));

        if (!(temp.delete())) {
            throw new IOException("Could not delete temp file: "
                    + temp.getAbsolutePath());
        }

        if (!(temp.mkdir())) {
            throw new IOException("Could not create temp directory: "
                    + temp.getAbsolutePath());
        }

        return (temp);
    }

    @Test
    public void testDecompressTo() throws IOException, UnZipFailureException {
        File targetDir = createTempDirectory();
        ZipUtils.decompressTo(targetDir,
                getClass().getResourceAsStream("testing.zip"));

        assertTrue(new File(targetDir, "subir1").exists());
        assertTrue(new File(targetDir, "subir1").isDirectory());
        assertTrue(new File(new File(targetDir, "subir1"), "tesing_out.tmp")
                .exists());
        assertTrue(new File(targetDir, "tesing_in.tmp").exists());
        assertTrue(new File(targetDir, "test.png").exists());

        FileUtils.deleteDirectory(targetDir);
    }

    @Test
    public void testCountEntries() throws UnZipFailureException {
        int numEntries = ZipUtils.countEntries(getClass().getResourceAsStream(
                "testing.zip"));
        assertEquals(4, numEntries);
    }

    @Test(expected = UnZipFailureException.class)
    public void testCorruptCountEntries() throws UnZipFailureException {
        ZipUtils.countEntries(getClass().getResourceAsStream("corrupt.zip"));
    }

    @Test(expected = UnZipFailureException.class)
    public void testCorruptDecompressTo() throws IOException,
            UnZipFailureException {
        File targetDir = createTempDirectory();
        ZipUtils.decompressTo(targetDir,
                getClass().getResourceAsStream("corrupt.zip"));
    }
}
