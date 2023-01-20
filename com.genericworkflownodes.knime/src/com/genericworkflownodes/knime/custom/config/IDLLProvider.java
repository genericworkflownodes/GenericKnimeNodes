package com.genericworkflownodes.knime.custom.config;

import java.io.File;
import java.util.List;

/**
 * Provides access to globally stored DLLs shared by multiple plug-ins, in order
 * to keep the size of the shipped binaries low.
 *
 * @author rmaerker, jpfeuffer
 *
 */
public interface IDLLProvider {

	/**
	 * Returns a list of all DLLs contained in the bundle.
	 *
	 * @return A {@link List} of DLL {@link File}s in the current bundle.
	 */
    List<File> getDLLs();

    /**
     * Returns a list of all folders containing DLLs or other
     * shared resources contained in the bundle.
     *
     * @return A {@link List} of DLL Folders as {@link File} objects
     *  in the current bundle.
     */
    List<File> getDLLFolders();
}
