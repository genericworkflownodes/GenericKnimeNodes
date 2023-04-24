package com.genericworkflownodes.knime.nodegeneration.model.directories;

import java.io.File;
import java.io.FileNotFoundException;

import com.genericworkflownodes.knime.nodegeneration.model.meta.GeneratedPluginMeta;

/**
 * {@link Directory} where the creation of the KNIME nodes occurs.
 * 
 * @author bkahlert
 * 
 */
public class NodesBuildDirectory extends GenericPluginDirectory {

    private static final long serialVersionUID = -2772836144406225644L;

    public NodesBuildDirectory(File buildDir, GeneratedPluginMeta meta)
            throws PathnameIsNoDirectoryException, FileNotFoundException {
        // we create subfolders for the package and the fragments
        super(new File(buildDir, meta.getId()), meta);
    }

}
