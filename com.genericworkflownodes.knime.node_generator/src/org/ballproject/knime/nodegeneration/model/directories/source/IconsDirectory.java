package org.ballproject.knime.nodegeneration.model.directories.source;

import java.io.File;
import java.io.IOException;

import org.ballproject.knime.base.config.INodeConfiguration;
import org.ballproject.knime.base.model.Directory;
import org.ballproject.knime.nodegeneration.exceptions.DuplicateNodeNameException;
import org.ballproject.knime.nodegeneration.exceptions.InvalidNodeNameException;

/**
 * Abstraction of the directory containing all icons related to the plugin that
 * should be generated.
 * 
 * @author aiche
 * 
 */
public class IconsDirectory extends Directory {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -3535393317046918930L;

	/**
	 * The icon used for the categories.
	 */
	private File categoryFile;

	/**
	 * The icon that will be added to the KNIME splash screen.
	 */
	private File splashFile;

	/**
	 * 
	 * @param sourcesDirectory
	 * @throws IOException
	 * @throws InvalidNodeNameException
	 * @throws DuplicateNodeNameException
	 */
	public IconsDirectory(final File sourcesDirectory) throws IOException,
			InvalidNodeNameException, DuplicateNodeNameException {
		super(sourcesDirectory);

		File categoryFile = new File(this, "category.png");
		this.categoryFile = (categoryFile.canRead()) ? categoryFile : null;

		File splashFile = new File(this, "splash.png");
		this.splashFile = (splashFile.canRead()) ? splashFile : null;
	}

	/**
	 * Returns the category icon contained in the {@link IconsDirectory}.
	 * 
	 * @return The category icon contained in the {@link IconsDirectory}.
	 */
	public final File getCategoryIcon() {
		return categoryFile;
	}

	/**
	 * Returns the splash screen icon contained in the {@link IconsDirectory}.
	 * 
	 * @return The splash screen icon contained in the {@link IconsDirectory}.
	 */
	public final File getSplashIcon() {
		return splashFile;
	}

	/**
	 * Tries to find a node icon for a given node configuration.
	 * 
	 * @param nodeConfiguration
	 *            The node for which an icon should be found.
	 * @return A {@link File} pointing to the icon if an icon was found,
	 *         <code>null</code> otherwise.
	 */
	public final File getNodeIcon(final INodeConfiguration nodeConfiguration) {
		File nodeFile = new File(this, nodeConfiguration.getName() + ".png");
		if (nodeFile.canRead()) {
			return nodeFile;
		}
		return null;
	}

}
