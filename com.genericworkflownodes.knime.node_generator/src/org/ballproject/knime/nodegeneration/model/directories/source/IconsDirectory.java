package org.ballproject.knime.nodegeneration.model.directories.source;

import java.io.File;
import java.io.IOException;

import org.ballproject.knime.base.config.INodeConfiguration;
import org.ballproject.knime.base.model.Directory;
import org.ballproject.knime.nodegeneration.exceptions.DuplicateNodeNameException;
import org.ballproject.knime.nodegeneration.exceptions.InvalidNodeNameException;

public class IconsDirectory extends Directory {

	private static final long serialVersionUID = -3535393317046918930L;

	private File categoryFile;
	private File splashFile;

	public IconsDirectory(File sourcesDirectory) throws IOException,
			InvalidNodeNameException, DuplicateNodeNameException {
		super(sourcesDirectory);

		File categoryFile = new File(this, "category.png");
		this.categoryFile = (categoryFile.canRead()) ? categoryFile : null;

		File splashFile = new File(this, "splash.png");
		this.splashFile = (splashFile.canRead()) ? splashFile : null;
	}

	public File getCategoryIcon() {
		return categoryFile;
	}

	public File getSplashIcon() {
		return splashFile;
	}

	public File getNodeIcon(INodeConfiguration nodeConfiguration) {
		File nodeFile = new File(this, nodeConfiguration.getName() + ".png");
		if (nodeFile.canRead())
			return nodeFile;
		return null;
	}

}
