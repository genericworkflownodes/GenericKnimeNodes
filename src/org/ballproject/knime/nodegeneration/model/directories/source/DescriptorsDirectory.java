package org.ballproject.knime.nodegeneration.model.directories.source;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.ballproject.knime.nodegeneration.model.directories.Directory;
import org.ballproject.knime.nodegeneration.model.files.CtdFile;
import org.ballproject.knime.nodegeneration.model.files.MimeTypesFile;
import org.dom4j.DocumentException;
import org.jaxen.JaxenException;

public class DescriptorsDirectory extends Directory {

	private static final long serialVersionUID = -3535393317046918930L;
	private List<CtdFile> ctdFiles;
	private MimeTypesFile mimeTypesFile;

	public DescriptorsDirectory(File sourcesDirectory) throws IOException {
		super(sourcesDirectory);

		File mimeTypeFile = new File(this, "mimetypes.xml");
		try {
			this.mimeTypesFile = new MimeTypesFile(mimeTypeFile);
		} catch (JaxenException e) {
			throw new IOException("Error reading MIME types from "
					+ mimeTypeFile.getPath());
		} catch (DocumentException e) {
			throw new IOException("Error reading MIME types from "
					+ mimeTypeFile.getPath());
		}

		ctdFiles = new LinkedList<CtdFile>();
		for (File file : this.listFiles()) {
			if (file.getName().endsWith(".ctd"))
				ctdFiles.add(new CtdFile(file));
		}
	}

	public List<CtdFile> getCtdFiles() {
		return ctdFiles;
	}

	public MimeTypesFile getMimeTypesFile() {
		return mimeTypesFile;
	}

}
