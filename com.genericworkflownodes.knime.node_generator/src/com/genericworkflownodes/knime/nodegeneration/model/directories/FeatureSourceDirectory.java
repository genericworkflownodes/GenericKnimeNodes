package com.genericworkflownodes.knime.nodegeneration.model.directories;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.dom4j.DocumentException;

import com.genericworkflownodes.knime.nodegeneration.exceptions.DuplicateNodeNameException;
import com.genericworkflownodes.knime.nodegeneration.exceptions.InvalidNodeNameException;
import com.genericworkflownodes.knime.nodegeneration.model.directories.source.ContributingPluginsDirectory;
import com.genericworkflownodes.knime.nodegeneration.model.meta.GeneratedPluginMeta;

public class FeatureSourceDirectory extends Directory {

    public static final String CONTRIBUTING_PLUGINS_DIRECTORY = "contributing-plugins";
    public static final String LICENSE_FILE = "LICENSE";
    public static final String COPYRIGHT_FILE = "COPYRIGHT";
    public static final String DESCRIPTION_FILE = "DESCRIPTION";
    public static final String FEATURE_PROPERTIES_FILE = "feature.properties";
    
    private static final long serialVersionUID = -2772836144406225644L;
    private ContributingPluginsDirectory contributingPluginsDirectory = null;

    private File descriptionFile;
    private File copyrightFile;
    private File licenseFile;

    private Properties properties = null;
    
    public FeatureSourceDirectory(NodesSourceDirectory nodeSourceDirectory)
            throws PathnameIsNoDirectoryException, IOException {
        super(nodeSourceDirectory, true);

        try {
            contributingPluginsDirectory = new ContributingPluginsDirectory(
                    new File(nodeSourceDirectory, CONTRIBUTING_PLUGINS_DIRECTORY));
        } catch (PathnameIsNoDirectoryException e) {
        }

        //this.properties = new Properties();

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

    public FeatureSourceDirectory(File nodeSourceDirectory)
            throws PathnameIsNoDirectoryException, IOException {
        super(nodeSourceDirectory, true);

        try {
            contributingPluginsDirectory = new ContributingPluginsDirectory(
                    new File(nodeSourceDirectory, CONTRIBUTING_PLUGINS_DIRECTORY));
        } catch (PathnameIsNoDirectoryException e) {
        } catch (FileNotFoundException e) {
        }

        File propertyFile = new File(nodeSourceDirectory, FEATURE_PROPERTIES_FILE);
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

    public ContributingPluginsDirectory getContributingPluginsDirectory() {
        return contributingPluginsDirectory;
    }

    public Properties getProperties() {
        return properties;
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public Properties getToolProperites(){
    	Properties p = new Properties();
    	for(String key : properties.stringPropertyNames()) {
    		if(key.startsWith("tool.")){
    			p.put(key, properties.get(key));
    		}
    	}
    	return p;
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
    
    public ArrayList<GeneratedPluginMeta> getGeneratedSubPluginMetas(String nodeGeneratorLastChangeDate)
    		throws PathnameIsNoDirectoryException, IOException, DocumentException, InvalidNodeNameException, DuplicateNodeNameException
    {
		ArrayList<GeneratedPluginMeta> pmetas = new ArrayList<GeneratedPluginMeta>();
		for (File dir2 : this.listFiles())
		{
    		if (dir2.isDirectory() && (this.getContributingPluginsDirectory() == null || !dir2.getName().equals(this.getContributingPluginsDirectory().getName())))
    		{
    			NodesSourceDirectory pdir = new NodesSourceDirectory(dir2);
    			pmetas.add(new GeneratedPluginMeta(pdir, nodeGeneratorLastChangeDate));
    		}
		}
		return pmetas;
    }

}
