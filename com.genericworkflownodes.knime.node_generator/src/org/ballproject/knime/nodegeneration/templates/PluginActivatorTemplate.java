package org.ballproject.knime.nodegeneration.templates;

import java.io.IOException;
import java.util.List;

import org.ballproject.knime.base.util.StringUtils;
import org.ballproject.knime.nodegeneration.NodeGenerator;

public class PluginActivatorTemplate extends Template {

	public PluginActivatorTemplate(String packageName, List<String> nodeNames)
			throws IOException {
		super(NodeGenerator.class
				.getResourceAsStream("templates/PluginActivator.template"));

		this.replace("__BASE__", packageName);
		this.replace("__NODENAMES__",
				"\"" + StringUtils.join(nodeNames, "\", \"") + "\"");
	}

}
