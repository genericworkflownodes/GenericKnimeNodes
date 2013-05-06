package com.genericworkflownodes.util;

import java.io.File;
import java.io.IOException;

/**
 * Implementations of this interface allow to read, save and delete resources in
 * a managed {@link File} location.
 * 
 * @author bkahlert
 */
public interface IFileStash {

	public abstract File getLocation();

	/**
	 * Returns a {@link File} identified by the given basename and extension. If
	 * the file does not exist yet it is created.
	 * <p>
	 * Each {@link File} returned by this method is uniquely identified by a
	 * basename and an extension within the scope of the directory this
	 * {@link FileStash} works on.
	 * 
	 * @param basename
	 *            by which the returned {@link File} is identified; all
	 *            characters are allowed as long as the system's JVM knows MD5;
	 *            otherwise only filename save characters should be used
	 * @param extension
	 *            of the file to be returned (without preceding period)
	 * @return
	 * @throws IOException
	 */
	public abstract File getFile(String basename, String extension)
			throws IOException;

	/**
	 * Deletes the given {@link File} if it's in the responsibility of this
	 * {@link FileStash}.
	 * 
	 * @param file
	 */
	public abstract void deleteFile(File file);

	/**
	 * Deletes <strong>all</strong> {@link File}s that share the same given
	 * basename but differ in their extensions.
	 * 
	 * @param file
	 */
	public abstract void deleteFiles(String basename);

}