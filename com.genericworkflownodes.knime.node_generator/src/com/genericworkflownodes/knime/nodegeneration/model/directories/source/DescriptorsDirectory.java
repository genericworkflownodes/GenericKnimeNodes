package com.genericworkflownodes.knime.nodegeneration.model.directories.source;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.genericworkflownodes.knime.nodegeneration.exceptions.DuplicateNodeNameException;
import com.genericworkflownodes.knime.nodegeneration.exceptions.InvalidNodeNameException;
import com.genericworkflownodes.knime.nodegeneration.model.directories.Directory;
import com.genericworkflownodes.knime.nodegeneration.model.files.CTDFile;
import com.genericworkflownodes.knime.nodegeneration.model.files.MimeTypesFile;
import com.genericworkflownodes.knime.nodegeneration.util.Utils;

/**
 * Directory containing the node descriptors and the mimetypes.xml file.
 * 
 * @author bkahlert, aiche
 */
public class DescriptorsDirectory extends Directory {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = -3535393317046918930L;

    /**
     * The list of all {@link CTDFile}s contained in this directory.
     */
    private List<CTDFile> ctdFiles;

    /**
     * The {@link MimeTypesFile} contained in the directory.
     */
    private MimeTypesFile mimeTypesFile;

    /**
     * Constructor based on a {@link File} representing the location of the
     * descriptor directory.
     * 
     * @param sourcesDirectory
     *            The directory where the descriptors are stored.
     * @throws IOException
     *             In case of IO problems.
     * @throws InvalidNodeNameException
     *             If one of the contained nodes as an invalid node name.
     * @throws DuplicateNodeNameException
     *             If there are duplicate ctd files inside the descriptors
     *             directory.
     */
    public DescriptorsDirectory(final File sourcesDirectory)
            throws FileNotFoundException, PathnameIsNoDirectoryException, IOException,
            InvalidNodeNameException, DuplicateNodeNameException {
        super(sourcesDirectory, true);

        File mimeTypeFile = new File(this, "mime.types");
        mimeTypesFile = new MimeTypesFile(mimeTypeFile.getAbsolutePath());
        
        boolean failed = false;
        ctdFiles = new LinkedList<CTDFile>();
        for (File file : this.listFiles()) {
            if (file.getName().endsWith(".ctd")) {
                try {
                    CTDFile ctdFile = new CTDFile(file);
                    String nodeName = ctdFile.getNodeConfiguration().getName();

                    if (!Utils.checkKNIMENodeName(nodeName)) {
                        throw new InvalidNodeNameException("The node name \""
                                + nodeName + "\" in file \"" + file
                                + "\" is invalid.");
                    }

                    if (ctdFiles.contains(ctdFile)) {
                        throw new DuplicateNodeNameException(nodeName);
                    }

                    ctdFiles.add(ctdFile);
                } catch (Exception e) {
                    // throw new IOException("Error reading " + file.getPath(),
                    // e);
                    System.err.println(String.format("Error processing CTD File: %s",
                            file.getName()));
                    System.err.println(String.format("Reason: %s",
                            e.getMessage()));
                    System.err.println("WARNING: File will be skipped and no source code produced.");
                    failed = true;
                }
            }
        }
        if (failed) {
        	throw new IOException("Error while reading or parsing the CTDs in folder: "
        						+ this.getAbsolutePath()
        						+ "\n See error messages for details.");
        }
    }

    public List<CTDFile> getCTDFiles() {
        return ctdFiles;
    }

    public MimeTypesFile getMimeTypesFile() {
        return mimeTypesFile;
    }

}
