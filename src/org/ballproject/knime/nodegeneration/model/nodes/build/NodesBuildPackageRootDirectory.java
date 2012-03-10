package org.ballproject.knime.nodegeneration.model.nodes.build;

import java.io.File;
import java.io.FileNotFoundException;

import org.ballproject.knime.nodegeneration.model.Directory;

public class NodesBuildPackageRootDirectory extends Directory {

	private static final long serialVersionUID = -1007613562337029689L;

	public NodesBuildPackageRootDirectory(File executablesDirectory)
			throws FileNotFoundException {
		super(executablesDirectory);
	}

}
