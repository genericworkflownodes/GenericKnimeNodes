package org.ballproject.knime.nodegeneration;

import java.util.logging.Logger;

public class KNIMENode {

	private static Logger logger = Logger.getLogger(NodeGenerator.class
			.getCanonicalName());

	public static boolean checkNodeName(String name) {
		if (!name.matches("[[A-Z]|[a-z]][[0-9]|[A-Z]|[a-z]]+"))
			return false;
		return true;
	}

	public static String fixNodeName(String name) {
		logger.info("trying to fix node class name " + name);
		name = name.replace(".", "");
		name = name.replace("-", "");
		name = name.replace("_", "");
		name = name.replace("#", "");
		name = name.replace("+", "");
		name = name.replace("$", "");
		name = name.replace(":", "");
		logger.info("fixed node name " + name);
		return name;
	}
}
