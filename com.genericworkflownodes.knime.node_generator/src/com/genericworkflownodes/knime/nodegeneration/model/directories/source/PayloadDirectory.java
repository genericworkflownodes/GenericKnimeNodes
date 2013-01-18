package com.genericworkflownodes.knime.nodegeneration.model.directories.source;

import java.io.File;

import com.genericworkflownodes.knime.nodegeneration.model.directories.Directory;

/**
 * Abstraction of the payload directory inside the generated plugin.
 * 
 * @author bkahlert, aiche
 */
public class PayloadDirectory extends Directory {

	private static final long serialVersionUID = -400249694994228712L;

	public PayloadDirectory(File payloadDirectory)
			throws PathnameIsNoDirectoryException {
		super(payloadDirectory);
	}

}
