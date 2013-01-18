package com.genericworkflownodes.knime.nodegeneration.model.directories.build;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.genericworkflownodes.knime.nodegeneration.model.directories.Directory;

public class NodesBuildBinaryResourcesDirectory extends Directory {

	private static final long serialVersionUID = 5024903143191264115L;

	public NodesBuildBinaryResourcesDirectory(File knimeDirectory)
			throws PathnameIsNoDirectoryException {
		super(knimeDirectory);
	}

	/**
	 * Copy the given zip file to payload directory and rename it to
	 * binaries.zip
	 * 
	 * @param zipFile
	 *            The zip file to copy.
	 * @throws IOException
	 *             If copy operation fails.
	 */
	public void copyPayload(final File zipFile) throws IOException {
		FileUtils.copyFile(zipFile, getBinariesFile());
	}

	/**
	 * Returns the file name of the binaries file inside the payload directory.
	 * 
	 * @return
	 */
	public File getBinariesFile() {
		return new File(this, "binaries.zip");
	}

}
