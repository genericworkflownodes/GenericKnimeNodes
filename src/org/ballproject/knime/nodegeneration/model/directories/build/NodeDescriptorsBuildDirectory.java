package org.ballproject.knime.nodegeneration.model.directories.build;

import java.io.File;
import java.io.FileNotFoundException;

import org.ballproject.knime.nodegeneration.model.Directory;

/**
 * {@link Directory} where the creation of the KNIME node descriptions (*.ctd)
 * occurs.
 * 
 * @author bkahlert
 * 
 */
public class NodeDescriptorsBuildDirectory extends Directory {

	private static final long serialVersionUID = -2276614395767929245L;

	public NodeDescriptorsBuildDirectory() throws FileNotFoundException {
		super(new File(System.getProperty("java.io.tmpdir"), "GKN-descriptors-"
				+ Long.toString(System.nanoTime())));

		this.deleteOnExit();
	}
}
