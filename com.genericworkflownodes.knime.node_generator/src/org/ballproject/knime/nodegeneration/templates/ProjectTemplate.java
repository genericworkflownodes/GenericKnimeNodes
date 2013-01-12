package org.ballproject.knime.nodegeneration.templates;

import java.io.IOException;

import org.ballproject.knime.nodegeneration.NodeGenerator;

public class ProjectTemplate extends Template {

	public ProjectTemplate(String packageName) throws IOException {
		super(NodeGenerator.class
				.getResourceAsStream("templates/project.template"));

		this.replace("%PACKAGE_NAME%", packageName);
	}

}
