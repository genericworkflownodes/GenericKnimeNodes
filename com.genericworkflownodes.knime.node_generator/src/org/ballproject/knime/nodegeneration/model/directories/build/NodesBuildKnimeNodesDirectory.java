package org.ballproject.knime.nodegeneration.model.directories.build;

import java.io.File;
import java.io.FileNotFoundException;

import org.ballproject.knime.base.model.Directory;

public class NodesBuildKnimeNodesDirectory extends Directory {

	private static final long serialVersionUID = -3535393317046918930L;

	public NodesBuildKnimeNodesDirectory(File knimeNodesDirectory)
			throws FileNotFoundException {
		super(knimeNodesDirectory);
	}

}
