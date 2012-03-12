package org.ballproject.knime.nodegeneration;

import java.io.File;
import java.util.logging.Logger;

public class Main {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(NodeGenerator.class
			.getCanonicalName());

	public static void main(String[] args) {
		File pluginDir = new File((args.length > 0) ? args[0] : ".")
				.getAbsoluteFile();

		NodeGenerator nodeGenerator;
		try {
			nodeGenerator = new NodeGenerator(pluginDir);
			NodePackager.zip(nodeGenerator);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
