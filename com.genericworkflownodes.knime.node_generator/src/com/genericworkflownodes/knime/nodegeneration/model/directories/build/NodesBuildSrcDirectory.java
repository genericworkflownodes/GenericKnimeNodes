package com.genericworkflownodes.knime.nodegeneration.model.directories.build;

import java.io.File;

import com.genericworkflownodes.knime.nodegeneration.model.directories.Directory;

public class NodesBuildSrcDirectory extends Directory {

	private static final long serialVersionUID = -400249694994228712L;

	public NodesBuildSrcDirectory(File srcDirectory)
			throws PathnameIsNoDirectoryException {
		super(srcDirectory);
	}

}
