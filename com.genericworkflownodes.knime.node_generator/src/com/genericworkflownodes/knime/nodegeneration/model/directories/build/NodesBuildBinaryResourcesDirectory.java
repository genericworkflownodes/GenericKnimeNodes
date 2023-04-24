package com.genericworkflownodes.knime.nodegeneration.model.directories.build;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;

import com.genericworkflownodes.knime.nodegeneration.model.directories.Directory;
import com.genericworkflownodes.knime.nodegeneration.util.UnZipFailureException;
import com.genericworkflownodes.knime.nodegeneration.util.ZipUtils;

public class NodesBuildBinaryResourcesDirectory extends Directory {

    private static final long serialVersionUID = 5024903143191264115L;

    public NodesBuildBinaryResourcesDirectory(File knimeDirectory)
            throws PathnameIsNoDirectoryException, FileNotFoundException {
        super(knimeDirectory, false);
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
    public List<String> copyPayload(final File zipFile) throws IOException,
            UnZipFailureException {

        // create this directory
        if (!exists()) {
            boolean mk = mkdirs();
            if (!mk) {
                throw new IOException(String.format(
                        "failed to create payload directory %s", getName()));
            }
        }

        List<String> paths = new ArrayList<String>();

        if (zipFile != null && zipFile.exists()) {
            // extract content into 'this'
            FileInputStream fis = new FileInputStream(zipFile);
            ZipUtils.decompressTo(this, fis);
            fis.close();

            // make executable
            makeExecutable(paths, "bin");
            makeExecutable(paths, "lib");
        } else {
            // add a dummy file containing informations on how to add your own
            // payload, check resources/EMPTY_PAYLOAD_README for the text
            FileUtils.copyInputStreamToFile(
                    getClass().getResourceAsStream(
                            "resources/EMPTY_PAYLOAD_README"), new File(this,
                            "README"));
        }

        return paths;
    }

    /**
     * @param paths
     */
    private void makeExecutable(List<String> paths, String subdir) {
        File dir = new File(this, subdir);
        if (dir.exists()) {
            Iterator<File> fit = FileUtils.iterateFiles(new File(this, subdir),
                    FileFileFilter.FILE, DirectoryFileFilter.INSTANCE);
            while (fit.hasNext()) {
                File f = fit.next();
                f.setExecutable(true, false);

                // get relative path to package root
                String relative = toURI().relativize(f.toURI()).getPath();
                paths.add("payload/" + relative);
            }
        }
    }
}
