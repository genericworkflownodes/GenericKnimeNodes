package com.genericworkflownodes.knime.nodegeneration.templates;

import java.io.IOException;

import com.genericworkflownodes.knime.nodegeneration.NodeGenerator;

public class NodeFactoryTemplate extends Template {

	public NodeFactoryTemplate(String packageName, String nodeName)
			throws IOException {
		super(NodeGenerator.class
				.getResourceAsStream("templates/NodeFactory.template"));

		this.replace("__BASE__", packageName);
		this.replace("__NODENAME__", nodeName);
	}

}
