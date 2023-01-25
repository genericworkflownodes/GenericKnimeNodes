package com.genericworkflownodes.knime.nodegeneration.model.directories;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.dom4j.DocumentException;

import com.genericworkflownodes.knime.nodegeneration.exceptions.DuplicateNodeNameException;
import com.genericworkflownodes.knime.nodegeneration.exceptions.InvalidNodeNameException;

public class UpdateSiteSourceDirectory extends Directory {
    public static final String FEATURE_PROPERTIES_FILE = "updatesite.properties";
    
    private static final long serialVersionUID = -2772836144406225644L;

    private Properties properties = null;

    public UpdateSiteSourceDirectory(File nodeSourceDirectory)
            throws PathnameIsNoDirectoryException, IOException,
            DocumentException, InvalidNodeNameException,
            DuplicateNodeNameException {
        super(nodeSourceDirectory, true);

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

}
