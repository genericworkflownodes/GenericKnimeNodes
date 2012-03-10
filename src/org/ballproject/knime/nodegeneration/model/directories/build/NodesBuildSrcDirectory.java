package org.ballproject.knime.nodegeneration.model.directories.build;

import java.io.File;
import java.io.FileNotFoundException;

import org.ballproject.knime.nodegeneration.model.directories.Directory;

public class NodesBuildSrcDirectory extends Directory {

	private static final long serialVersionUID = -400249694994228712L;

	public NodesBuildSrcDirectory(File srcDirectory)
			throws FileNotFoundException {
		super(srcDirectory);
	}

}
