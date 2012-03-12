package org.ballproject.knime.nodegeneration.model.directories;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.ballproject.knime.nodegeneration.exceptions.DuplicateNodeNameException;
import org.ballproject.knime.nodegeneration.exceptions.InvalidNodeNameException;
import org.ballproject.knime.nodegeneration.model.directories.source.DescriptorsDirectory;
import org.ballproject.knime.nodegeneration.model.directories.source.ExecutablesDirectory;
import org.ballproject.knime.nodegeneration.model.directories.source.PayloadDirectory;
import org.ballproject.knime.nodegeneration.model.files.CTDFile;
import org.ballproject.knime.nodegeneration.model.mime.MimeType;
import org.dom4j.DocumentException;

public class NodesSourceDirectory extends Directory {

	private static final long serialVersionUID = -2772836144406225644L;
	private DescriptorsDirectory descriptorsDirectory = null;
	private ExecutablesDirectory executablesDirectory = null;
	private PayloadDirectory payloadDirectory = null;
	private Properties properties = null;

	public NodesSourceDirectory(File nodeSourceDirectory) throws IOException,
			DocumentException, InvalidNodeNameException,
			DuplicateNodeNameException {
		super(nodeSourceDirectory);

		try {
			this.descriptorsDirectory = new DescriptorsDirectory(new File(
					nodeSourceDirectory, "descriptors"));
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("Could not find payload directory "
					+ payloadDirectory.getPath());
		}

		try {
			this.executablesDirectory = new ExecutablesDirectory(new File(
					nodeSourceDirectory, "executables"));
		} catch (FileNotFoundException e) {

		}

		try {
			this.payloadDirectory = new PayloadDirectory(new File(
					nodeSourceDirectory, "payload"));
		} catch (FileNotFoundException e) {

		}

		File propertyFile = new File(nodeSourceDirectory, "plugin.properties");
		try {
			Properties properties = new Properties();
			properties.load(new FileInputStream(propertyFile));
			this.properties = properties;
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("Could not find property file "
					+ propertyFile.getPath());
		} catch (IOException e) {
			throw new IOException("Could not load property file", e);
		}
	}

	public DescriptorsDirectory getDescriptorsDirectory() {
		return descriptorsDirectory;
	}

	public ExecutablesDirectory getExecutablesDirectory() {
		return executablesDirectory;
	}

	public PayloadDirectory getPayloadDirectory() {
		return payloadDirectory;
	}

	public Properties getProperties() {
		return properties;
	}

	public String getProperty(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}

	public List<CTDFile> getCtdFiles() {
		return this.descriptorsDirectory.getCTDFiles();
	}

	public List<MimeType> getMimeTypes() {
		return this.descriptorsDirectory.getMimeTypesFile().getMimeTypes();
	}
}
