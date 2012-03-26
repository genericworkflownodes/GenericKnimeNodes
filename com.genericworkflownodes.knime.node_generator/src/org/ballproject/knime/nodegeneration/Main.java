package org.ballproject.knime.nodegeneration;

import java.io.File;
import java.util.logging.Logger;

public class Main {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(NodeGenerator.class
			.getCanonicalName());

	/**
	 * 
	 * @param args
	 *            #1: plugin directory; #2: build directory
	 */
	public static void main(String[] args) {
		File pluginDir = new File((args.length > 0) ? args[0] : ".")
				.getAbsoluteFile();
		File buildDir = (args.length > 1) ? new File(args[1]).getAbsoluteFile()
				: null;
		if (buildDir != null)
			buildDir.mkdirs();

		try {
			new NodeGenerator(pluginDir, buildDir);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
