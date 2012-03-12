package org.ballproject.knime.nodegeneration.templates;

import java.io.IOException;

import org.ballproject.knime.nodegeneration.NodeGenerator;

public class PluginActivatorTemplate extends Template {

	public PluginActivatorTemplate(String packageName) throws IOException {
		super(NodeGenerator.class
				.getResourceAsStream("templates/PluginActivator.template"));

		this.replace("__BASE__", packageName);
		this.replace("__NAME__", packageName);
	}

}
