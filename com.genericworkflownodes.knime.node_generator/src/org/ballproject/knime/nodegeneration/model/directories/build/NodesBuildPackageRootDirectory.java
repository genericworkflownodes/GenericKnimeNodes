package org.ballproject.knime.nodegeneration.model.directories.build;

import java.io.File;
import java.io.FileNotFoundException;

import org.ballproject.knime.base.model.Directory;

public class NodesBuildPackageRootDirectory extends Directory {

	private static final long serialVersionUID = -1007613562337029689L;

	public NodesBuildPackageRootDirectory(File packageRootDirectory)
			throws FileNotFoundException {
		super(packageRootDirectory);
	}

}
