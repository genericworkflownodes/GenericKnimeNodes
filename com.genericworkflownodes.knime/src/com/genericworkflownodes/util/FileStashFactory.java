package com.genericworkflownodes.util;

import java.io.File;

import org.apache.commons.lang.RandomStringUtils;

/**
 * Factory for {@link IFileStash}
 * 
 * @author bkahlert
 * 
 */
public class FileStashFactory {

    /**
     * Directory used to store temporary {@link IFileStash}s.
     */
    private static File TEMP_PARENT_DIR = new File(new File(
            System.getProperty("java.io.tmpdir")), "GKN-STASH");

    public static File getTempParentDirectory() {
        return TEMP_PARENT_DIR;
    }

    public static void setTempParentDirectory(File tempParentDirectory) {
        TEMP_PARENT_DIR = tempParentDirectory;
    }

    /**
     * Creates a new {@link IFileStash} that stores its files somewhere in the
     * OS's temporary directory.
     * <p>
     * It is extremely unlikely that two consecutive calls result in two
     * {@link IFileStash}s that work on the same directory.
     * 
     * @return
     */
    public static IFileStash createTemporary() {
        File stashDirectory = new File(TEMP_PARENT_DIR, "GKN-STASH-"
                + RandomStringUtils.randomAlphanumeric(16));
        return new FileStash(stashDirectory);
    }

    /**
     * Creates a new {@link IFileStash} in the given directory.
     * 
     * @param stashDirectory
     * @return
     */
    public static IFileStash createPersistent(File stashDirectory) {
        return new FileStash(stashDirectory);
    }

    private FileStashFactory() {

    }
}
