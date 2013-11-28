package com.genericworkflownodes.util;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class FileStashProperties {

    /**
     * Given a {@link File} returns the {@link File} to be effectively used to
     * store {@link Properties}.
     * 
     * @param file
     * @return
     */
    private static File getPropertiesLocation(File file) {
        File propertiesFile;
        if (file.isDirectory()) {
            propertiesFile = new File(file, "stash.properties");
        } else {
            propertiesFile = file;
        }
        return propertiesFile;
    }

    /**
     * Reads the {@link FileStash} location from a properties file or a
     * directory containing a properties file.
     * 
     * <ul>
     * <li>If the file is a directory the file named
     * <code>stash.properties</code> is used.</li>
     * </ul>
     * 
     * @param propertiesFile
     * @return
     * @throws IOException
     */
    public static File readLocation(File file) throws IOException {
        File propertiesFile = getPropertiesLocation(file);
        Properties properties = PropertiesUtils.load(propertiesFile);
        String location = properties.getProperty("location", null);
        return location != null ? new File(location) : null;
    }

    /**
     * Saves the {@link FileStash} location to a properties file or a directory
     * containing a properties file.
     * 
     * <ul>
     * <li>If the file does not exists it gets created.</li>
     * <li>If the file is a directory a properties file named
     * <code>stash.properties</code> is created.</li>
     * </ul>
     * 
     * @param fileStash
     * @param file
     * @throws IOException
     */
    public static void saveLocation(IFileStash fileStash, File file)
            throws IOException {
        File propertiesFile = getPropertiesLocation(file);
        Properties properties = PropertiesUtils.load(propertiesFile);
        properties.put("location", fileStash.getLocation().getAbsolutePath());
        PropertiesUtils.save(propertiesFile, properties);
    }

}
