package org.ballproject.knime.nodegeneration.model.directories;

import java.io.File;
import java.io.FileNotFoundException;

import org.ballproject.knime.base.model.Directory;

/**
 * {@link Directory} where the creation of the KNIME nodes occurs.
 * 
 * @author bkahlert
 * 
 */
public class NodesBuildDirectory extends GenericPluginDirectory {

	private static final long serialVersionUID = -2772836144406225644L;

	public NodesBuildDirectory(File buildDir, String packageRoot)
			throws FileNotFoundException {
		// we create subfolders for the package and the fragments
		super(new File(buildDir, packageRoot), packageRoot);
	}

}
