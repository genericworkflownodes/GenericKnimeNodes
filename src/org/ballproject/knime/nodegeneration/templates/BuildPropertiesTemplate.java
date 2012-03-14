package org.ballproject.knime.nodegeneration.templates;

import java.io.IOException;

import org.ballproject.knime.nodegeneration.NodeGenerator;

public class BuildPropertiesTemplate extends Template {

	public BuildPropertiesTemplate() throws IOException {
		super(NodeGenerator.class
				.getResourceAsStream("templates/build.properties.template"));
	}

}
