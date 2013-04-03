package com.genericworkflownodes.util;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileStash {
	public static FileStash instance;

	private static File STASH_DIR;
	private static DateFormat fmt = new SimpleDateFormat("MM-dd-yyyy");

	public static FileStash getInstance() {
		if (instance == null) {
			instance = new FileStash();
		}
		return instance;
	}

	private FileStash() {
		File tempDir = new File(System.getProperty("java.io.tmpdir"));
		STASH_DIR = new File(tempDir, "GKN_STASH");
	}

	public File allocateFile(String extension) throws IOException {
		String slot = fmt.format(new Date());
		String slotpath = STASH_DIR + File.separator + slot;
		File slotfile = new File(slotpath);
		slotfile.mkdirs();
		return Helper.getTempFile(STASH_DIR + File.separator + slot, extension,
				false);
	}

	public File getStashDirectory() {
		return STASH_DIR;
	}

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

}
