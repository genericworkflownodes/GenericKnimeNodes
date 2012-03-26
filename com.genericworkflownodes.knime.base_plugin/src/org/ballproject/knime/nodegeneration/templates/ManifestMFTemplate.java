package org.ballproject.knime.nodegeneration.templates;

import java.io.IOException;

import org.ballproject.knime.nodegeneration.NodeGenerator;
import org.ballproject.knime.nodegeneration.model.KNIMEPluginMeta;

public class ManifestMFTemplate extends Template {

	public ManifestMFTemplate(KNIMEPluginMeta pluginMeta) throws IOException {
		super(NodeGenerator.class
				.getResourceAsStream("templates/MANIFEST.MF.template"));

		this.replace("@@pluginName@@", pluginMeta.getName());
		this.replace("@@pluginVersion@@", pluginMeta.getVersion());
		this.replace("@@packageName@@", pluginMeta.getPackageRoot());
	}

}
