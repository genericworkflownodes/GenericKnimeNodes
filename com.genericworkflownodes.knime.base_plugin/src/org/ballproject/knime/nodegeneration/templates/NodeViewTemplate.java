package org.ballproject.knime.nodegeneration.templates;

import java.io.IOException;

import org.ballproject.knime.nodegeneration.NodeGenerator;

public class NodeViewTemplate extends Template {

	public NodeViewTemplate(String packageName, String nodeName)
			throws IOException {
		super(NodeGenerator.class
				.getResourceAsStream("templates/NodeView.template"));

		this.replace("__BASE__", packageName);
		this.replace("__NODENAME__", nodeName);
	}

}
