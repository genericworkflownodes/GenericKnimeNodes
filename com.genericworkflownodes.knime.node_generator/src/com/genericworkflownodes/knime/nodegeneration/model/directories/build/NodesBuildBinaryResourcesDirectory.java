package com.genericworkflownodes.knime.nodegeneration.model.directories.build;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;

import com.genericworkflownodes.knime.nodegeneration.model.directories.Directory;
import com.genericworkflownodes.knime.nodegeneration.util.UnZipFailureException;
import com.genericworkflownodes.knime.nodegeneration.util.ZipUtils;

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
     * @throws UnZipFailureException
     */
    public void copyPayload(final File zipFile) throws IOException,
            UnZipFailureException {
        // extract content into 'this'
        FileInputStream fis = new FileInputStream(zipFile);
        ZipUtils.decompressTo(this, fis);
        fis.close();

        // make executable
        Iterator<File> fit = FileUtils.iterateFiles(new File(this, "bin"),
                FileFileFilter.FILE, DirectoryFileFilter.INSTANCE);
        while (fit.hasNext()) {
            fit.next().setExecutable(true, false);
        }
    }
}
