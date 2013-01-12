package com.genericworkflownodes.knime.nodegeneration.templates;

import java.io.IOException;

import com.genericworkflownodes.knime.nodegeneration.NodeGenerator;

public class NodeDialogTemplate extends Template {

	public NodeDialogTemplate(String packageName, String nodeName)
			throws IOException {
		super(NodeGenerator.class
				.getResourceAsStream("templates/NodeDialog.template"));

		this.replace("__BASE__", packageName);
		this.replace("__NODENAME__", nodeName);
	}

}
