package com.genericworkflownodes.knime.nodegeneration.model.directories;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.dom4j.DocumentException;

import com.genericworkflownodes.knime.nodegeneration.exceptions.DuplicateNodeNameException;
import com.genericworkflownodes.knime.nodegeneration.exceptions.InvalidNodeNameException;
import com.genericworkflownodes.knime.nodegeneration.model.directories.source.ContributingPluginsDirectory;
import com.genericworkflownodes.knime.nodegeneration.model.directories.source.DescriptorsDirectory;
import com.genericworkflownodes.knime.nodegeneration.model.directories.source.IconsDirectory;
import com.genericworkflownodes.knime.nodegeneration.model.directories.source.PayloadDirectory;
import com.genericworkflownodes.knime.nodegeneration.model.files.CTDFile;
import com.genericworkflownodes.knime.nodegeneration.model.mime.MimeType;

public class NodesSourceDirectory extends Directory {

	private static final long serialVersionUID = -2772836144406225644L;
	private DescriptorsDirectory descriptorsDirectory = null;
	private PayloadDirectory payloadDirectory = null;
	private IconsDirectory iconsDirectory = null;
	private ContributingPluginsDirectory contributingPluginsDirectory = null;

	private File descriptionFile;
	private File copyrightFile;
	private File licenseFile;

	private Properties properties = null;

	public NodesSourceDirectory(File nodeSourceDirectory)
			throws PathnameIsNoDirectoryException, IOException,
			DocumentException, InvalidNodeNameException,
			DuplicateNodeNameException {
		super(nodeSourceDirectory);

		try {
			this.descriptorsDirectory = new DescriptorsDirectory(new File(
					nodeSourceDirectory, "descriptors"));
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException(
					"Could not find descriptors directory "
							+ new File(nodeSourceDirectory, "descriptors")
									.getPath());
		}

		try {
			this.payloadDirectory = new PayloadDirectory(new File(
					nodeSourceDirectory, "payload"));
		} catch (PathnameIsNoDirectoryException e) {

		}

		try {
			this.iconsDirectory = new IconsDirectory(new File(
					nodeSourceDirectory, "icons"));
		} catch (FileNotFoundException e) {

		}

		try {
			this.contributingPluginsDirectory = new ContributingPluginsDirectory(
					new File(nodeSourceDirectory, "contributing-plugins"));
		} catch (PathnameIsNoDirectoryException e) {
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

		descriptionFile = new File(nodeSourceDirectory, "DESCRIPTION");
		if (!descriptionFile.exists()) {
			throw new FileNotFoundException(
					"DESCRIPTION file not found in source directory. Expected in: "
							+ descriptionFile.getAbsolutePath());
		}

		copyrightFile = new File(nodeSourceDirectory, "COPYRIGHT");
		if (!copyrightFile.exists()) {
			throw new FileNotFoundException(
					"COPYRIGHT file not found in source directory. Expected in: "
							+ copyrightFile.getAbsolutePath());
		}

		licenseFile = new File(nodeSourceDirectory, "LICENSE");
		if (!licenseFile.exists()) {
			throw new FileNotFoundException(
					"LICENSE file not found in source directory. Expected in: "
							+ licenseFile.getAbsolutePath());
		}

	}

	public DescriptorsDirectory getDescriptorsDirectory() {
		return descriptorsDirectory;
	}

	public PayloadDirectory getPayloadDirectory() {
		return payloadDirectory;
	}

	public IconsDirectory getIconsDirectory() {
		return iconsDirectory;
	}

	public ContributingPluginsDirectory getContributingPluginsDirectory() {
		return contributingPluginsDirectory;
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

	public File getDescriptionFile() {
		return descriptionFile;
	}

	public File getCopyrightFile() {
		return copyrightFile;
	}

	public File getLicenseFile() {
		return licenseFile;
	}

}
