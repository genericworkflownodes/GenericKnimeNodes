package org.ballproject.knime.nodegeneration.model.directories.source;

import java.io.File;
import java.io.FileNotFoundException;

import org.ballproject.knime.nodegeneration.model.directories.Directory;

public class ExecutablesDirectory extends Directory {

	private static final long serialVersionUID = -1007613562337029689L;
	private File bin;

	public ExecutablesDirectory(File executablesDirectory)
			throws FileNotFoundException {
		super(executablesDirectory);

		this.bin = new File(this, "bin");
		if (!this.bin.isDirectory())
			throw new FileNotFoundException("Could no find bin directory in "
					+ this);
	}

	public File getBin() {
		return this.bin;
	}

	public File getCTD(String name) {
		return new File(this, name);
	}

}
