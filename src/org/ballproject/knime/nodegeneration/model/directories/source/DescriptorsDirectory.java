package org.ballproject.knime.nodegeneration.model.directories.source;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.ballproject.knime.base.config.CTDNodeConfigurationReaderException;
import org.ballproject.knime.nodegeneration.exceptions.DuplicateNodeNameException;
import org.ballproject.knime.nodegeneration.exceptions.InvalidNodeNameException;
import org.ballproject.knime.nodegeneration.model.directories.Directory;
import org.ballproject.knime.nodegeneration.model.files.CTDFileX;
import org.ballproject.knime.nodegeneration.model.files.MimeTypesFile;
import org.ballproject.knime.nodegeneration.util.Utils;
import org.dom4j.DocumentException;
import org.jaxen.JaxenException;

public class DescriptorsDirectory extends Directory {

	private static final long serialVersionUID = -3535393317046918930L;

	private List<CTDFileX> ctdFiles;
	private List<CTDFileX> internalCtdFiles;
	private List<CTDFileX> externalCtdFiles;
	private MimeTypesFile mimeTypesFile;

	public DescriptorsDirectory(File sourcesDirectory) throws IOException,
			InvalidNodeNameException, DuplicateNodeNameException {
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

		this.ctdFiles = new LinkedList<CTDFileX>();
		this.internalCtdFiles = new LinkedList<CTDFileX>();
		this.externalCtdFiles = new LinkedList<CTDFileX>();
		for (File file : this.listFiles()) {
			if (file.getName().endsWith(".ctd"))
				try {
					CTDFileX ctdFile = new CTDFileX(file);
					String nodeName = ctdFile.getNodeConfiguration().getName();

					if (!Utils.checkKNIMENodeName(nodeName))
						throw new InvalidNodeNameException("The node name \""
								+ nodeName + "\" in file \"" + file
								+ "\" is invalid.");

					if (this.internalCtdFiles.contains(ctdFile)
							|| this.externalCtdFiles.contains(ctdFile))
						throw new DuplicateNodeNameException(nodeName);

					if (ctdFile.getNodeConfiguration().getStatus()
							.equals("internal")) {
						this.internalCtdFiles.add(ctdFile);
					} else {
						this.externalCtdFiles.add(ctdFile);
					}
					this.ctdFiles.add(ctdFile);
				} catch (CTDNodeConfigurationReaderException e) {
					throw new IOException("Error reading " + file.getPath(), e);
				}
		}
	}

	public List<CTDFileX> getCTDFiles() {
		return ctdFiles;
	}

	public List<CTDFileX> getInternalCtdFiles() {
		return internalCtdFiles;
	}

	public List<CTDFileX> getExternalCtdFiles() {
		return externalCtdFiles;
	}

	public MimeTypesFile getMimeTypesFile() {
		return mimeTypesFile;
	}

}
