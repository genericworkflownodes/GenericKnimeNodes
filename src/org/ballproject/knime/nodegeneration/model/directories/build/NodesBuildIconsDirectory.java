package org.ballproject.knime.nodegeneration.model.directories.build;

import java.io.File;
import java.io.FileNotFoundException;

import org.ballproject.knime.nodegeneration.model.directories.Directory;

public class NodesBuildIconsDirectory extends Directory {

	private static final long serialVersionUID = -400249624994228712L;

	public NodesBuildIconsDirectory(File iconsDirectory)
			throws FileNotFoundException {
		super(iconsDirectory);
	}

}
