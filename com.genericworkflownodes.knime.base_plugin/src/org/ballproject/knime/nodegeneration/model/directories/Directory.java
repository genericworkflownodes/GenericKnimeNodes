package org.ballproject.knime.nodegeneration.model.directories;

import java.io.File;
import java.io.FileNotFoundException;

public class Directory extends File {

	private static final long serialVersionUID = -3535393317046918930L;

	/**
	 * Wraps an existing directory
	 * 
	 * @param directory
	 * @throws FileNotFoundException
	 */
	public Directory(File directory) throws FileNotFoundException {
		super(directory.getAbsolutePath());

		if (!directory.isDirectory()) {
			throw new FileNotFoundException();
		}
	}

	/**
	 * Creates a temporary directory
	 * 
	 * @param prefix
	 */
	public Directory(String prefix) {
		super(new File(System.getProperty("java.io.tmpdir"), prefix + "-"
				+ Long.toString(System.nanoTime())).getAbsolutePath());

		this.mkdirs();
		this.deleteOnExit();
	}

}
