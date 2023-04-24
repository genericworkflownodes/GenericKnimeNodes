package com.genericworkflownodes.knime.nodegeneration.model.directories;

import java.io.File;
import java.io.FileNotFoundException;

public class Directory extends File {

    private static final long serialVersionUID = -3535393317046918930L;

    public class PathnameIsNoDirectoryException extends Exception {

        private static final long serialVersionUID = -9000829355911897465L;

        public PathnameIsNoDirectoryException(File file) {
            super(String.format("Path %s is not a directory",
                    file.getAbsolutePath()));
        }
    }

    /**
     * Wraps an existing directory
     * 
     * @param directory
     * @throws FileNotFoundException
     */
    public Directory(File directory, boolean mustExist) throws PathnameIsNoDirectoryException, FileNotFoundException {
        super(directory.getAbsolutePath());
        if (mustExist && !directory.exists())
        {
        	throw new FileNotFoundException(directory.getAbsolutePath());
        }

        if (directory.exists() && !directory.isDirectory()) {
            throw new PathnameIsNoDirectoryException(directory);
        }
    }

    /**
     * Creates a temporary directory
     * 
     * @param prefix
     */
    public Directory(String prefix) {
        super(new File(System.getProperty("java.io.tmpdir"), prefix + "-"
                + Long.toString(System.nanoTime())).getAbsolutePath());

        this.mkdirs();
        this.deleteOnExit();
    }

}
