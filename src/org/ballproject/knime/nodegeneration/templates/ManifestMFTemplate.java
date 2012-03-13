package org.ballproject.knime.nodegeneration.templates;

import java.io.IOException;

import org.ballproject.knime.nodegeneration.NodeGenerator;
import org.ballproject.knime.nodegeneration.model.KNIMEPluginMeta;

public class ManifestMFTemplate extends Template {

	public ManifestMFTemplate(KNIMEPluginMeta pluginMeta) throws IOException {
		super(NodeGenerator.class
				.getResourceAsStream("templates/MANIFEST.MF.template"));

		this.replace("@@pluginname@@", pluginMeta.getName());
		this.replace("@@pluginversion@@", pluginMeta.getVersion());
		this.replace("@@packageName@@", pluginMeta.getPackageRoot());
	}

}
