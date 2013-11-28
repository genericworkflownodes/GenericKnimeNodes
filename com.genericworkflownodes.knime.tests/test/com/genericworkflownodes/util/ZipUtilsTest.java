package com.genericworkflownodes.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.Test;

import com.genericworkflownodes.knime.custom.payload.ZipUtils;
import com.genericworkflownodes.knime.test.data.TestDataSource;

public class ZipUtilsTest {

    class TestMonitor implements IProgressMonitor {

        @Override
        public void beginTask(String name, int totalWork) {
        }

        @Override
        public void done() {
        }

        @Override
        public void internalWorked(double work) {
        }

        @Override
        public boolean isCanceled() {
            return false;
        }

        @Override
        public void setCanceled(boolean value) {
        }

        @Override
        public void setTaskName(String name) {
        }

        @Override
        public void subTask(String name) {
        }

        @Override
        public void worked(int work) {
        }

    }

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
    public void testDecompressTo() throws IOException {
        File targetDir = createTempDirectory();
        ZipUtils.decompressTo(targetDir,
                TestDataSource.class.getResourceAsStream("testing.zip"),
                new TestMonitor());

        assertTrue(new File(targetDir, "subir1").exists());
        assertTrue(new File(targetDir, "subir1").isDirectory());
        assertTrue(new File(new File(targetDir, "subir1"), "tesing_out.tmp")
                .exists());
        assertTrue(new File(targetDir, "tesing_in.tmp").exists());
        assertTrue(new File(targetDir, "test.png").exists());

        FileUtils.deleteDirectory(targetDir);
    }

    @Test
    public void testCountEntries() {
        int numEntries = ZipUtils.countEntries(TestDataSource.class
                .getResourceAsStream("testing.zip"));
        assertEquals(4, numEntries);
    }
}
