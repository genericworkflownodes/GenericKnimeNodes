package org.ballproject.knime.nodegeneration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * This class is responsible for packaging prepared KNIME nodes to usables ones.
 * 
 * @author bkahlert
 * 
 */
public class NodePackager {

	private static Logger logger = Logger.getLogger(NodePackager.class
			.getCanonicalName());

	/**
	 * Zips a given {@link File directory} to the given {@link File}.
	 * 
	 * @param knimeNodeDirectory
	 * @param jar
	 * @throws IOException
	 */
	public static void zip(File knimeNodeDirectory, File jar)
			throws IOException {
		logger.info("Zipping KNIME plugin to: " + jar);
		Utils.zipDirectory(knimeNodeDirectory, jar);
		logger.info("KNIME plugin successfully zipped to: " + jar);
	}

	/**
	 * Zips the prepared plugin directory of a {@link NodeGenerator} to the
	 * {@link NodeGenerator#getPluginDirectory() source's parent directory}.
	 * 
	 * @param nodeGenerator
	 * @throws IOException
	 */
	public static void zip(NodeGenerator nodeGenerator) throws IOException {
		File jar = new File(nodeGenerator.getPluginDirectory().getParent(),
				nodeGenerator.getPluginName() + "_"
						+ nodeGenerator.getPluginVersion() + ".jar");
		logger.info("Zipping KNIME plugin to: " + jar);
		Utils.zipDirectory(nodeGenerator.getPreparedPluginDirectory(), jar);
		logger.info("KNIME plugin successfully zipped to: " + jar);
	}
}
