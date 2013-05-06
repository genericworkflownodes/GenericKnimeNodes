package com.genericworkflownodes.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class FileStashPropertiesTest {

	@Test
	public void testSaveLocationToNonExistingFile() throws IOException {
		IFileStash fileStash = FileStashFactory.createTemporary();
		String expected = fileStash.getLocation().getAbsolutePath();

		File propertiesFile = File.createTempFile("test", ".properties");
		propertiesFile.delete();

		FileStashProperties.saveLocation(fileStash, propertiesFile);

		assertTrue(FileUtils.readFileToString(propertiesFile)
				.contains(expected));
		assertEquals(expected, FileStashProperties.readLocation(propertiesFile)
				.getAbsolutePath());
	}

	@Test
	public void testSaveLocationToFile() throws IOException {
		IFileStash fileStash = FileStashFactory.createTemporary();
		String expected = fileStash.getLocation().getAbsolutePath();

		File propertiesFile = File.createTempFile("test", ".properties");
		FileStashProperties.saveLocation(fileStash, propertiesFile);
		assertTrue(FileUtils.readFileToString(propertiesFile)
				.contains(expected));
		assertEquals(expected, FileStashProperties.readLocation(propertiesFile)
				.getAbsolutePath());
	}

	@Test
	public void testSaveLocationToDirectory() throws IOException {
		IFileStash fileStash = FileStashFactory.createTemporary();
		String expected = fileStash.getLocation().getAbsolutePath();

		File propertiesDirectory = File.createTempFile("test", ".properties");
		propertiesDirectory.delete();
		propertiesDirectory.mkdir();
		FileStashProperties.saveLocation(fileStash, propertiesDirectory);
		assertTrue(FileUtils.readFileToString(
				new File(propertiesDirectory, "stash.properties")).contains(
				expected));
		assertEquals(
				expected,
				FileStashProperties.readLocation(
						new File(propertiesDirectory, "stash.properties"))
						.getAbsolutePath());
	}
}
