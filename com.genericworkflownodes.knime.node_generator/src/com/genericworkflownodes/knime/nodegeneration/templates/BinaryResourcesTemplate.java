package com.genericworkflownodes.knime.nodegeneration.templates;

import java.io.IOException;

import com.genericworkflownodes.knime.nodegeneration.NodeGenerator;

public class BinaryResourcesTemplate extends Template {

	public BinaryResourcesTemplate(String packageName) throws IOException {
		super(NodeGenerator.class
				.getResourceAsStream("templates/BinaryResources.template"));

		this.replace("__BASE__", packageName);
		this.replace("__BINPACKNAME__", packageName);
	}

}
