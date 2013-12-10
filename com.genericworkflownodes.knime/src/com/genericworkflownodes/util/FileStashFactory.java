/**
 * Copyright (c) 2012, Björn Kahlert, Stephan Aiche.
 *
 * This file is part of GenericKnimeNodes.
 * 
 * GenericKnimeNodes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.genericworkflownodes.util;

import java.io.File;

import org.apache.commons.lang.RandomStringUtils;
import org.knime.core.node.NodeLogger;

/**
 * Factory for {@link IFileStash}.
 * 
 * @author bkahlert, aiche
 */
public final class FileStashFactory {

    /**
     * Length of the random identifier appended to any newly created file stash.
     */
    private static final int randomIdentifierLength = 16;

    /**
     * The logger.
     */
    private static final NodeLogger logger = NodeLogger
            .getLogger(FileStashFactory.class);

    /**
     * Directory used to store temporary {@link IFileStash}s.
     */
    private static File stashParentDirectory = new File(new File(
            System.getProperty("java.io.tmpdir")), "GKN-STASH");

    /**
     * Returns the current root directory for of the file stash factory.
     * 
     * @return The root directory of the file stashes.
     */
    public static File getTempParentDirectory() {
        return stashParentDirectory;
    }

    /**
     * Set's the root temporary directory for file stashes created by the
     * factory.
     * 
     * @param tempParentDirectory
     *            The new root directory.
     */
    public static void setTempParentDirectory(File tempParentDirectory) {
        stashParentDirectory = tempParentDirectory;

        // ensure the directory exists
        if (!stashParentDirectory.exists()) {
            logger.debug("Stash parent directory doesn't exist. Try create.");
            boolean success = stashParentDirectory.mkdirs();
            if (!success) {
                logger.warn(String.format(
                        "Unable to create stash parent directory: %s",
                        stashParentDirectory));
            }
        }
    }

    /**
     * Creates a new {@link IFileStash} that stores its files somewhere in the
     * OS's temporary directory.
     * <p>
     * It is extremely unlikely that two consecutive calls result in two
     * {@link IFileStash}s that work on the same directory.
     * 
     * @return A new file stash located in the root directory of this factory.
     */
    public static IFileStash createTemporary() {
        File stashDirectory = new File(stashParentDirectory, "GKN-STASH-"
                + RandomStringUtils.randomAlphanumeric(randomIdentifierLength));
        return new FileStash(stashDirectory);
    }

    /**
     * Creates a new {@link IFileStash} in the given directory.
     * 
     * @param stashDirectory
     *            The directory where the file stash should be created.
     * @return A new file stash in the given directory.
     */
    public static IFileStash createPersistent(File stashDirectory) {
        return new FileStash(stashDirectory);
    }

    /**
     * Private c'tor to avoid instantiation
     */
    private FileStashFactory() {
    }
}
