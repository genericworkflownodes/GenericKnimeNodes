package org.ballproject.knime.nodegeneration.model.nodes.source;

import java.io.File;
import java.io.FileNotFoundException;

import org.ballproject.knime.nodegeneration.model.Directory;

public class ExecutablesDirectory extends Directory {

	private static final long serialVersionUID = -1007613562337029689L;

	public ExecutablesDirectory(File executablesDirectory)
			throws FileNotFoundException {
		super(executablesDirectory);
	}

}
