package com.genericworkflownodes.knime.nodegeneration.model.directories.build;

import java.io.File;
import java.io.FileNotFoundException;

import com.genericworkflownodes.knime.nodegeneration.model.directories.Directory;

public class NodesBuildPackageRootDirectory extends Directory {

    private static final long serialVersionUID = -1007613562337029689L;

    public NodesBuildPackageRootDirectory(File packageRootDirectory)
            throws PathnameIsNoDirectoryException, FileNotFoundException {
        super(packageRootDirectory, false);
    }

}
