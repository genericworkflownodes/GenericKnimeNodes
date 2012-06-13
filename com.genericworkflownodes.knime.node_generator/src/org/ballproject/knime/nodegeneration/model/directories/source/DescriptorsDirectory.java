package org.ballproject.knime.nodegeneration.model.directories.source;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.ballproject.knime.base.config.CTDNodeConfigurationReaderException;
import org.ballproject.knime.base.model.Directory;
import org.ballproject.knime.nodegeneration.exceptions.DuplicateNodeNameException;
import org.ballproject.knime.nodegeneration.exceptions.InvalidNodeNameException;
import org.ballproject.knime.nodegeneration.model.files.CTDFile;
import org.ballproject.knime.nodegeneration.model.files.MimeTypesFile;
import org.ballproject.knime.nodegeneration.util.Utils;
import org.dom4j.DocumentException;
import org.jaxen.JaxenException;

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
			throws IOException, InvalidNodeNameException,
			DuplicateNodeNameException {
		super(sourcesDirectory);

		File mimeTypeFile = new File(this, "mimetypes.xml");
		try {
			this.mimeTypesFile = new MimeTypesFile(mimeTypeFile);
		} catch (JaxenException e) {
			throw new IOException("Error reading MIME types from "
					+ mimeTypeFile.getPath(), e);
		} catch (DocumentException e) {
			throw new IOException("Error reading MIME types from "
					+ mimeTypeFile.getPath(), e);
		}

		this.ctdFiles = new LinkedList<CTDFile>();
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

					if (this.ctdFiles.contains(ctdFile)) {
						throw new DuplicateNodeNameException(nodeName);
					}

					this.ctdFiles.add(ctdFile);
				} catch (CTDNodeConfigurationReaderException e) {
					throw new IOException("Error reading " + file.getPath(), e);
				}
			}
		}
	}

	public List<CTDFile> getCTDFiles() {
		return ctdFiles;
	}

	public MimeTypesFile getMimeTypesFile() {
		return mimeTypesFile;
	}

}
