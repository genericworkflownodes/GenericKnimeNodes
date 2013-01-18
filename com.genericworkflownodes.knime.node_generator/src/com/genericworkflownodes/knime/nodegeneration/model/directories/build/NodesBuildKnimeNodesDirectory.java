package com.genericworkflownodes.knime.nodegeneration.model.directories.build;

import java.io.File;

import com.genericworkflownodes.knime.nodegeneration.model.directories.Directory;

public class NodesBuildKnimeNodesDirectory extends Directory {

	private static final long serialVersionUID = -3535393317046918930L;

	public NodesBuildKnimeNodesDirectory(File knimeNodesDirectory)
			throws PathnameIsNoDirectoryException {
		super(knimeNodesDirectory);
	}

}
