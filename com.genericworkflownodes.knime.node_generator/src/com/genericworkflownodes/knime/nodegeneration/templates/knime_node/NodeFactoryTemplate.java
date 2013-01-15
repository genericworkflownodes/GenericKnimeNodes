package com.genericworkflownodes.knime.nodegeneration.templates.knime_node;

import java.io.IOException;

import com.genericworkflownodes.knime.nodegeneration.NodeGenerator;
import com.genericworkflownodes.knime.nodegeneration.templates.Template;

public class NodeFactoryTemplate extends Template {

	public NodeFactoryTemplate(String packageName, String nodeName)
			throws IOException {
		super(
				NodeGenerator.class
						.getResourceAsStream("templates/knime_nodes/NodeFactory.template"));

		this.replace("__BASE__", packageName);
		this.replace("__NODENAME__", nodeName);
	}

}
