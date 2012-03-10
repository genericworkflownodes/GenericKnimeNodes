package org.ballproject.knime.nodegeneration.model;

import java.io.File;
import java.io.FileNotFoundException;

public class Directory extends File {

	private static final long serialVersionUID = -3535393317046918930L;

	public Directory(File directory) throws FileNotFoundException {
		super(directory.getAbsolutePath());

		if (!directory.isDirectory()) {
			throw new FileNotFoundException();
		}
	}

}
