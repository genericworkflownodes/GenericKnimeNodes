package com.genericworkflownodes.knime.nodegeneration.model.directories.build;

import java.io.File;

import com.genericworkflownodes.knime.nodegeneration.model.directories.Directory;

public class NodesBuildKnimeDirectory extends Directory {

    private static final long serialVersionUID = 5024903143191264115L;

    public NodesBuildKnimeDirectory(File knimeDirectory)
            throws PathnameIsNoDirectoryException {
        super(knimeDirectory);
    }

}
