package org.ballproject.knime.nodegeneration.templates;

import java.io.IOException;

import org.ballproject.knime.nodegeneration.NodeGenerator;
import org.ballproject.knime.nodegeneration.model.KNIMEPluginMeta;

public class ProjectTemplate extends Template {

	public ProjectTemplate(KNIMEPluginMeta meta) throws IOException {
		super(NodeGenerator.class
				.getResourceAsStream("templates/project.template"));

		this.replace("%PACKAGE_NAME%", meta.getPackageRoot());
	}

}
