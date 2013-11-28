package com.genericworkflownodes.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(value = Parameterized.class)
public class FileStashTest {

    private static final String random(int numChars) {
        return RandomStringUtils.randomAlphanumeric(numChars);
    }

    private static final File TMP = new File(
            System.getProperty("java.io.tmpdir"));

    @Parameters
    public static Collection<Object[]> data() throws Exception {
        List<Object[]> parameters = new ArrayList<Object[]>();

        // different temp stashes
        parameters.add(new Object[] { FileStashFactory.createTemporary(),
                FileStashFactory.createTemporary(), false });

        // same temp stash
        IFileStash tmpStash = FileStashFactory.createTemporary();
        parameters.add(new Object[] { tmpStash, tmpStash, true });

        // different persistent stashes
        parameters.add(new Object[] {
                FileStashFactory.createPersistent(new File(TMP, random(8))),
                FileStashFactory.createPersistent(new File(TMP, random(8))),
                false });

        // same persistent stash
        IFileStash persistentStash = FileStashFactory
                .createPersistent(new File(TMP, random(8)));
        parameters.add(new Object[] { persistentStash, persistentStash, true });

        return parameters;
    }

    private IFileStash fileStash1;
    private IFileStash fileStash2;
    private boolean equals;

    public FileStashTest(IFileStash fileStash1, IFileStash fileStash2,
            boolean equals) {
        this.fileStash1 = fileStash1;
        this.fileStash2 = fileStash2;
        this.equals = equals;
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateEmptyBasename() throws IOException {
        fileStash1.getFile(null, "txt");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateEmptyBasename2() throws IOException {
        fileStash1.getFile("", "txt");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateEmptyExtension() throws IOException {
        fileStash1.getFile("test", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateEmptyExtension2() throws IOException {
        fileStash1.getFile("test", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateAndDeleteNonManaged() throws IOException {
        File file = fileStash1.getFile(random(8), "txt");
        assertNotNull(file);
        assertTrue(file.exists());
        assertTrue(file.canRead());
        assertTrue(file.canWrite());

        // leave the file stash's directory
        String[] parts = file.getAbsolutePath().split(File.separator);
        parts[parts.length - 2] = "INVALID_DIR";
        File newFile = new File(StringUtils.join(Arrays.asList(parts),
                File.separator));

        fileStash1.deleteFile(newFile);
    }

    @Test
    public void testCreateAndDelete() throws IOException {
        String basename1 = random(8);
        String basename2 = basename1;
        if (!equals)
            basename2 = random(8);

        File file1 = fileStash1.getFile(basename1, "txt");
        File file2 = fileStash2.getFile(basename2, "txt");

        assertNotNull(file1);
        assertTrue(file1.exists());
        assertTrue(file1.canRead());
        assertTrue(file1.canWrite());

        assertNotNull(file2);
        assertTrue(file2.exists());
        assertTrue(file2.canRead());
        assertTrue(file2.canWrite());

        fileStash1.deleteFile(file1);

        assertNotNull(file1);
        assertFalse(file1.exists());
        assertFalse(file1.canRead());
        assertFalse(file1.canWrite());

        // file2 must exist as it was created with an attached random number to
        // ensure multiple files can be created with basename extension
        // combination
        assertNotNull(file2);
        assertTrue(file2.exists());
        assertTrue(file2.canRead());
        assertTrue(file2.canWrite());
    }

    @Test
    // TODO test fileStash2
    public void testCreateMultipleAndDelete() throws IOException {
        File file1 = fileStash1.getFile(random(8), "txt");
        assertNotNull(file1);
        assertTrue(file1.exists());
        assertTrue(file1.canRead());
        assertTrue(file1.canWrite());

        File file2 = fileStash1.getFile(random(8), "txt");
        assertNotNull(file2);
        assertTrue(file2.exists());
        assertTrue(file2.canRead());
        assertTrue(file2.canWrite());

        fileStash1.deleteFile(file1);
        assertNotNull(file1);
        assertFalse(file1.exists());
        assertFalse(file1.canRead());
        assertFalse(file1.canWrite());

        assertNotNull(file2);
        assertTrue(file2.exists());
        assertTrue(file2.canRead());
        assertTrue(file2.canWrite());

        fileStash1.deleteFile(file2);
        assertNotNull(file2);
        assertFalse(file2.exists());
        assertFalse(file2.canRead());
        assertFalse(file2.canWrite());
    }

    @Test
    // TODO test fileStash2
    public void testCreateMultipleSameBasenameAndDelete() throws IOException {
        String basename = random(8);

        File file1 = fileStash1.getFile(basename, "txt");
        assertNotNull(file1);
        assertTrue(file1.exists());
        assertTrue(file1.canRead());
        assertTrue(file1.canWrite());

        File file2 = fileStash1.getFile(basename, "gkn");
        assertNotNull(file2);
        assertTrue(file2.exists());
        assertTrue(file2.canRead());
        assertTrue(file2.canWrite());

        fileStash1.deleteFile(file1);
        assertNotNull(file1);
        assertFalse(file1.exists());
        assertFalse(file1.canRead());
        assertFalse(file1.canWrite());

        assertNotNull(file2);
        assertTrue(file2.exists());
        assertTrue(file2.canRead());
        assertTrue(file2.canWrite());

        fileStash1.deleteFile(file2);
        assertNotNull(file2);
        assertFalse(file2.exists());
        assertFalse(file2.canRead());
        assertFalse(file2.canWrite());
    }

    @Test
    // TODO test fileStash2
    public void testCreateMultipleSameBasenameAndDeleteBasenameBased()
            throws IOException {
        String basename = random(8);

        File file1 = fileStash1.getFile(basename, "txt");
        assertNotNull(file1);
        assertTrue(file1.exists());
        assertTrue(file1.canRead());
        assertTrue(file1.canWrite());

        File file2 = fileStash1.getFile(basename, "gkn");
        assertNotNull(file2);
        assertTrue(file2.exists());
        assertTrue(file2.canRead());
        assertTrue(file2.canWrite());

        String basename2 = random(8);
        File file3 = fileStash1.getFile(basename2, "gkn");
        assertNotNull(file3);
        assertTrue(file3.exists());
        assertTrue(file3.canRead());
        assertTrue(file3.canWrite());

        fileStash1.deleteFiles(basename);

        assertFalse(file1.exists());
        assertFalse(file1.canRead());
        assertFalse(file1.canWrite());

        assertNotNull(file2);
        assertFalse(file2.exists());
        assertFalse(file2.canRead());
        assertFalse(file2.canWrite());

        assertNotNull(file3);
        assertTrue(file3.exists());
        assertTrue(file3.canRead());
        assertTrue(file3.canWrite());
    }

    @Test
    // TODO test fileStash2
    public void testCreateMultipleSameBasenameAndDeleteAll() throws IOException {
        String basename = random(8);

        File file1 = fileStash1.getFile(basename, "txt");
        assertNotNull(file1);
        assertTrue(file1.exists());
        assertTrue(file1.canRead());
        assertTrue(file1.canWrite());

        File file2 = fileStash1.getFile(basename, "gkn");
        assertNotNull(file2);
        assertTrue(file2.exists());
        assertTrue(file2.canRead());
        assertTrue(file2.canWrite());

        String basename2 = random(8);
        File file3 = fileStash1.getFile(basename2, "gkn");
        assertNotNull(file3);
        assertTrue(file3.exists());
        assertTrue(file3.canRead());
        assertTrue(file3.canWrite());

        fileStash1.deleteAllFiles();

        assertFalse(file1.exists());
        assertFalse(file1.canRead());
        assertFalse(file1.canWrite());

        assertNotNull(file2);
        assertFalse(file2.exists());
        assertFalse(file2.canRead());
        assertFalse(file2.canWrite());

        assertNotNull(file3);
        assertFalse(file3.exists());
        assertFalse(file3.canRead());
        assertFalse(file3.canWrite());
    }

    @Test
    public void testIsolation() throws IOException {
        File file1 = fileStash1.getFile("test", "txt");
        FileUtils.writeStringToFile(file1, "Hello World!");

        File file2 = fileStash2.getFile("test", "txt");
        String content2 = FileUtils.readFileToString(file2);
        assertEquals("", content2);
    }
}
