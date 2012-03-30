package org.ballproject.knime.nodegeneration.model.directories.build;

import java.io.File;
import java.io.FileNotFoundException;

import org.ballproject.knime.base.model.Directory;

public class NodesBuildKnimeDirectory extends Directory {

	private static final long serialVersionUID = 5024903143191264115L;

	public NodesBuildKnimeDirectory(File knimeDirectory)
			throws FileNotFoundException {
		super(knimeDirectory);
	}

}
