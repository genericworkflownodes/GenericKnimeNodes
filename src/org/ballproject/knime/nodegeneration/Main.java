package org.ballproject.knime.nodegeneration;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Logger;

public class Main {

	private static Logger logger = Logger.getLogger(NodeGenerator.class
			.getCanonicalName());

	public static void main(String[] args) {
		File pluginDir = new File((args.length > 0) ? args[0] : ".")
				.getAbsoluteFile();

		try {
			NodeGenerator nodeGenerator = new NodeGenerator(pluginDir);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
