package com.genericworkflownodes.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.RandomStringUtils;

/**
 * Factory for {@link IFileStash}
 * 
 * @author bkahlert
 * 
 */
public class FileStashFactory {

	/**
	 * Creates a new {@link IFileStash} that stores its files somewhere in the
	 * OS's temporary directory.
	 * <p>
	 * It is extremely unlikely that two consecutive calls result in two
	 * {@link IFileStash}s that work on the same directory.
	 * 
	 * @return
	 */
	public static IFileStash createTemporary() {
		File tempDirectory = new File(System.getProperty("java.io.tmpdir"));
		File stashDirectory = new File(tempDirectory, "GKN-STASH-"
				+ RandomStringUtils.randomAlphanumeric(16));
		return new FileStash(stashDirectory);
	}

	/**
	 * Creates a new {@link IFileStash} in the given directory.
	 * 
	 * @param stashDirectory
	 * @return
	 */
	public static IFileStash createPersistent(File stashDirectory) {
		return new FileStash(stashDirectory);
	}

	/**
	 * Creates a new {@link IFileStash} that stores its files somewhere in the
	 * OS' temporary directory but remembers the used location.
	 * <p>
	 * The used location is stored in a properties file in the given directory.
	 * Using the same directory might open the old {@link IFileStash} but could
	 * also result in a clear {@link IFileStash} - depending on the OS's
	 * temporary directory policy.
	 * 
	 * @param configDirectory
	 * @return
	 * @throws IOException
	 */
	public static IFileStash createSemiPersistent(File configDirectory)
			throws IOException {
		File configFile = new File(configDirectory, "stash.properties");
		if (!configFile.exists()) {
			configFile.getParentFile().mkdirs();
			configFile.createNewFile();
		}
		Properties properties = new Properties();
		properties.load(new FileReader(configFile));
		String fileStashPath = properties.getProperty("location", null);

		IFileStash fileStash;
		if (fileStashPath != null) {
			fileStash = createPersistent(new File(fileStashPath));
		} else {
			fileStash = createTemporary();
			properties.setProperty("location", fileStash.getStashDirectory()
					.getAbsolutePath());
			properties.store(new FileWriter(configFile), "Written by "
					+ FileStashFactory.class.getName());
		}

		System.err.println(fileStash.getStashDirectory());
		return fileStash;
	}

	private FileStashFactory() {

	}
}
