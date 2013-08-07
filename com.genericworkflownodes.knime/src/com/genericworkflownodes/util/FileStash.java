package com.genericworkflownodes.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.Assert;

public class FileStash implements IFileStash {

	private static final Logger LOGGER = Logger.getLogger(FileStash.class
			.getName());
	private static Random RANDOM_NUMBER_GENERATOR = new Random();

	private File location;

	public FileStash(File stashDirectory) {
		Assert.isLegal(stashDirectory != null
				&& ((stashDirectory.exists() && stashDirectory.isDirectory()) || !stashDirectory
						.exists()));
		location = stashDirectory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.genericworkflownodes.util.IFileStash#getStashDirectory()
	 */
	@Override
	public File getLocation() {
		return location;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.genericworkflownodes.util.IFileStash#getFile(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public File getFile(String basename, String extension) throws IOException {
		Assert.isLegal(basename != null && !basename.isEmpty());
		Assert.isLegal(extension != null && !extension.isEmpty());

		// add some uniqueness w.r.t. to loops to the names
		int num = Math.abs(RANDOM_NUMBER_GENERATOR.nextInt());
		basename = String.format("%s-%06d", basename, num);

		String filename = basename + "." + extension;
		File file = new File(location, filename);
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		return file;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.genericworkflownodes.util.IFileStash#deleteFile(java.io.File)
	 */
	@Override
	public void deleteFile(File file) {
		Assert.isLegal(file != null);
		try {
			File parent = file.getAbsoluteFile().getCanonicalFile()
					.getParentFile();
			Assert.isLegal(
					this.getLocation().getCanonicalFile().equals(parent),
					FileStash.class.getSimpleName()
							+ " is not responsible for " + file + "!");
			if (file.exists())
				file.delete();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Error while deleting file " + file, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.genericworkflownodes.util.IFileStash#deleteFiles(java.lang.String)
	 */
	@Override
	public void deleteFiles(final String basename) {
		Assert.isLegal(basename != null && !basename.isEmpty());
		for (File file : this.getLocation().listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File stashDir, String filename) {
				return (filename.startsWith(basename));
			}
		})) {
			if (!file.isFile())
				continue;
			file.delete();
		}
	}

	@Override
	public void deleteAllFiles() throws IOException {
		FileUtils.cleanDirectory(this.getLocation());
	}
}
