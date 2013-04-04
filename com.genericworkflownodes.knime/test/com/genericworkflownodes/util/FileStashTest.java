package com.genericworkflownodes.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

public class FileStashTest {

	private static final Random RANDOM = new Random();

	@Test(expected = IllegalArgumentException.class)
	public void testCreateEmptyBasename() throws IOException {
		FileStash fileStash = FileStash.getInstance();
		fileStash.getFile(null, "txt");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateEmptyBasename2() throws IOException {
		FileStash fileStash = FileStash.getInstance();
		fileStash.getFile("", "txt");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateEmptyExtension() throws IOException {
		FileStash fileStash = FileStash.getInstance();
		fileStash.getFile("test", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateEmptyExtension2() throws IOException {
		FileStash fileStash = FileStash.getInstance();
		fileStash.getFile("test", "");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateAndDeleteNonManaged() throws IOException {
		FileStash fileStash = FileStash.getInstance();
		File file = fileStash.getFile(String.valueOf(RANDOM.nextInt()), "txt");
		assertNotNull(file);
		assertTrue(file.exists());
		assertTrue(file.canRead());
		assertTrue(file.canWrite());

		String[] parts = file.getAbsolutePath().split(File.separator);
		parts[parts.length - 2] = "INVALID_DIR";
		File newFile = new File(StringUtils.join(Arrays.asList(parts),
				File.separator));

		fileStash.deleteFile(newFile);
	}

	@Test
	public void testCreateAndDelete() throws IOException {
		FileStash fileStash = FileStash.getInstance();
		File file = fileStash.getFile(String.valueOf(RANDOM.nextInt()), "txt");
		assertNotNull(file);
		assertTrue(file.exists());
		assertTrue(file.canRead());
		assertTrue(file.canWrite());

		fileStash.deleteFile(file);
		assertNotNull(file);
		assertFalse(file.exists());
		assertFalse(file.canRead());
		assertFalse(file.canWrite());
	}

	@Test
	public void testCreateMultipleAndDelete() throws IOException {
		FileStash fileStash = FileStash.getInstance();
		File file1 = fileStash.getFile(String.valueOf(RANDOM.nextInt()), "txt");
		assertNotNull(file1);
		assertTrue(file1.exists());
		assertTrue(file1.canRead());
		assertTrue(file1.canWrite());

		File file2 = fileStash.getFile(String.valueOf(RANDOM.nextInt()), "txt");
		assertNotNull(file2);
		assertTrue(file2.exists());
		assertTrue(file2.canRead());
		assertTrue(file2.canWrite());

		fileStash.deleteFile(file1);
		assertNotNull(file1);
		assertFalse(file1.exists());
		assertFalse(file1.canRead());
		assertFalse(file1.canWrite());

		assertNotNull(file2);
		assertTrue(file2.exists());
		assertTrue(file2.canRead());
		assertTrue(file2.canWrite());

		fileStash.deleteFile(file2);
		assertNotNull(file2);
		assertFalse(file2.exists());
		assertFalse(file2.canRead());
		assertFalse(file2.canWrite());
	}

	@Test
	public void testCreateMultipleSameBasenameAndDelete() throws IOException {
		FileStash fileStash = FileStash.getInstance();
		String basename = String.valueOf(RANDOM.nextInt());

		File file1 = fileStash.getFile(basename, "txt");
		assertNotNull(file1);
		assertTrue(file1.exists());
		assertTrue(file1.canRead());
		assertTrue(file1.canWrite());

		File file2 = fileStash.getFile(basename, "gkn");
		assertNotNull(file2);
		assertTrue(file2.exists());
		assertTrue(file2.canRead());
		assertTrue(file2.canWrite());

		fileStash.deleteFile(file1);
		assertNotNull(file1);
		assertFalse(file1.exists());
		assertFalse(file1.canRead());
		assertFalse(file1.canWrite());

		assertNotNull(file2);
		assertTrue(file2.exists());
		assertTrue(file2.canRead());
		assertTrue(file2.canWrite());

		fileStash.deleteFile(file2);
		assertNotNull(file2);
		assertFalse(file2.exists());
		assertFalse(file2.canRead());
		assertFalse(file2.canWrite());
	}

	@Test
	public void testCreateMultipleSameBasenameAndDeleteBasenameBased()
			throws IOException {
		FileStash fileStash = FileStash.getInstance();
		String basename = String.valueOf(RANDOM.nextInt());

		File file1 = fileStash.getFile(basename, "txt");
		assertNotNull(file1);
		assertTrue(file1.exists());
		assertTrue(file1.canRead());
		assertTrue(file1.canWrite());

		File file2 = fileStash.getFile(basename, "gkn");
		assertNotNull(file2);
		assertTrue(file2.exists());
		assertTrue(file2.canRead());
		assertTrue(file2.canWrite());

		fileStash.deleteFiles(basename);

		assertFalse(file1.exists());
		assertFalse(file1.canRead());
		assertFalse(file1.canWrite());

		assertNotNull(file2);
		assertFalse(file2.exists());
		assertFalse(file2.canRead());
		assertFalse(file2.canWrite());
	}

}
