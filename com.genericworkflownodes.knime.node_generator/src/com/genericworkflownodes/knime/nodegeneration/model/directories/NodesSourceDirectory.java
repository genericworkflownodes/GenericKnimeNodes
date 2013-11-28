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
import com.genericworkflownodes.knime.nodegeneration.model.files.MimeTypesFile.MIMETypeEntry;

public class NodesSourceDirectory extends Directory {

    public static final String CONTRIBUTING_PLUGINS_DIRECTORY = "contributing-plugins";
    public static final String LICENSE_FILE = "LICENSE";
    public static final String COPYRIGHT_FILE = "COPYRIGHT";
    public static final String DESCRIPTION_FILE = "DESCRIPTION";
    public static final String PLUGIN_PROPERTIES_FILE = "plugin.properties";
    public static final String ICONS_DIRECTORY = "icons";
    public static final String PAYLOAD_DIRECTORY = "payload";
    public static final String DESCRIPTORS_DIRECTORY = "descriptors";
    
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
            descriptorsDirectory = new DescriptorsDirectory(new File(
                    nodeSourceDirectory, DESCRIPTORS_DIRECTORY));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(
                    "Could not find descriptors directory "
                            + new File(nodeSourceDirectory, DESCRIPTORS_DIRECTORY)
                                    .getPath());
        }

        try {
            payloadDirectory = new PayloadDirectory(new File(
                    nodeSourceDirectory, PAYLOAD_DIRECTORY));
        } catch (PathnameIsNoDirectoryException e) {

        }

        try {
            iconsDirectory = new IconsDirectory(new File(nodeSourceDirectory,
                    ICONS_DIRECTORY));
        } catch (FileNotFoundException e) {

        }

        try {
            contributingPluginsDirectory = new ContributingPluginsDirectory(
                    new File(nodeSourceDirectory, CONTRIBUTING_PLUGINS_DIRECTORY));
        } catch (PathnameIsNoDirectoryException e) {
        }

        File propertyFile = new File(nodeSourceDirectory, PLUGIN_PROPERTIES_FILE);
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(propertyFile));
            this.properties = properties;
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Could not find properties file "
                    + propertyFile.getPath());
        } catch (IOException e) {
            throw new IOException("Could not load properties file", e);
        }

        descriptionFile = new File(nodeSourceDirectory, DESCRIPTION_FILE);
        if (!descriptionFile.exists()) {
            throw new FileNotFoundException(
                    "DESCRIPTION file not found in source directory. Expected in: "
                            + descriptionFile.getAbsolutePath());
        }

        copyrightFile = new File(nodeSourceDirectory, COPYRIGHT_FILE);
        if (!copyrightFile.exists()) {
            throw new FileNotFoundException(
                    "COPYRIGHT file not found in source directory. Expected in: "
                            + copyrightFile.getAbsolutePath());
        }

        licenseFile = new File(nodeSourceDirectory, LICENSE_FILE);
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
        return descriptorsDirectory.getCTDFiles();
    }

    public List<MIMETypeEntry> getMIMETypes() {
        return descriptorsDirectory.getMimeTypesFile().getMIMETypeEntries();
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
