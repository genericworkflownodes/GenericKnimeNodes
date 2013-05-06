package com.genericworkflownodes.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.Assert;

public class FileStash implements IFileStash {

	private static final Logger LOGGER = Logger.getLogger(FileStash.class
			.getName());

	private static MessageDigest MD5_DIGEST;
	{
		try {
			MD5_DIGEST = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			MD5_DIGEST = null;
		}
	}

	private static String hash(String string) {
		if (MD5_DIGEST != null) {
			MD5_DIGEST.reset();
			MD5_DIGEST.update(string.getBytes());
			String hash = new BigInteger(1, MD5_DIGEST.digest()).toString(16);
			while (hash.length() < 32) {
				hash = "0" + hash;
			}
			return hash;
		}
		return string;
	}

	public static IFileStash instance;

	private static File STASH_DIR;

	public static IFileStash getInstance() {
		if (instance == null) {
			instance = new FileStash();
		}
		return instance;
	}

	public FileStash() {
		File tempDir = new File(System.getProperty("java.io.tmpdir"));
		STASH_DIR = new File(tempDir, "GKN_STASH");
	}

	public FileStash(File stashDirectory) {
		Assert.isLegal(stashDirectory != null
				&& ((stashDirectory.exists() && stashDirectory.isDirectory()) || !stashDirectory
						.exists()));
		STASH_DIR = stashDirectory;
	}

	/* (non-Javadoc)
	 * @see com.genericworkflownodes.util.IFileStash#getStashDirectory()
	 */
	@Override
	public File getStashDirectory() {
		return STASH_DIR;
	}

	/* (non-Javadoc)
	 * @see com.genericworkflownodes.util.IFileStash#setStashDirectory(java.lang.String)
	 */
	@Override
	public void setStashDirectory(String dir) {
		File sdir = new File(dir);
		if (!sdir.exists()) {
			sdir.mkdirs();
		}
		if (!sdir.isDirectory()) {
			throw new IllegalArgumentException(
					"no valid directory path was supplied");
		} else {
			STASH_DIR = sdir;
		}
	}

	/* (non-Javadoc)
	 * @see com.genericworkflownodes.util.IFileStash#getFile(java.lang.String, java.lang.String)
	 */
	@Override
	public File getFile(String basename, String extension) throws IOException {
		Assert.isLegal(basename != null && !basename.isEmpty());
		Assert.isLegal(extension != null && !extension.isEmpty());

		if (MD5_DIGEST != null) {
			MD5_DIGEST.reset();
			MD5_DIGEST.update(basename.getBytes());
			BigInteger bigInt = new BigInteger(1, MD5_DIGEST.digest());
			String hashtext = bigInt.toString(16);
			// Now we need to zero pad it if you actually want the full 32
			// chars.
			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}
		}

		String filename = hash(basename) + "." + extension;
		File file = new File(STASH_DIR, filename);
		if (!file.exists()) {
			File parentDirectory = new File(
					FilenameUtils.getFullPathNoEndSeparator(file
							.getAbsolutePath()));
			parentDirectory.mkdirs();
			file.createNewFile();
		}
		return file;
	}

	/* (non-Javadoc)
	 * @see com.genericworkflownodes.util.IFileStash#deleteFile(java.io.File)
	 */
	@Override
	public void deleteFile(File file) {
		Assert.isLegal(file != null);
		try {
			File parent = file.getAbsoluteFile().getCanonicalFile()
					.getParentFile();
			Assert.isLegal(
					this.getStashDirectory().getCanonicalFile().equals(parent),
					FileStash.class.getSimpleName()
							+ " is not responsible for " + file + "!");
			if (file.exists())
				file.delete();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Error while deleting file " + file, e);
		}
	}

	/* (non-Javadoc)
	 * @see com.genericworkflownodes.util.IFileStash#deleteFiles(java.lang.String)
	 */
	@Override
	public void deleteFiles(String basename) {
		Assert.isLegal(basename != null && !basename.isEmpty());
		final String hash = hash(basename);
		for (File file : this.getStashDirectory().listFiles(
				new FilenameFilter() {
					@Override
					public boolean accept(File stashDir, String filename) {
						String[] parts = filename.split("\\.");
						return parts.length > 0 ? parts[0].equals(hash) : false;
					}
				})) {
			if (!file.isFile())
				continue;
			file.delete();
		}
	}
}
