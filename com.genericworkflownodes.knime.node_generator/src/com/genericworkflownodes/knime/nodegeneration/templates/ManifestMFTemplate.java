package com.genericworkflownodes.knime.nodegeneration.templates;

import java.io.IOException;

import com.genericworkflownodes.knime.nodegeneration.NodeGenerator;
import com.genericworkflownodes.knime.nodegeneration.model.meta.GeneratedPluginMeta;

public class ManifestMFTemplate extends Template {

	public ManifestMFTemplate(GeneratedPluginMeta pluginMeta) throws IOException {
		super(NodeGenerator.class
				.getResourceAsStream("templates/MANIFEST.MF.template"));

		this.replace("@@pluginName@@", pluginMeta.getName());
		this.replace("@@pluginVersion@@", pluginMeta.getVersion());
		this.replace("@@packageName@@", pluginMeta.getPackageRoot());
	}

}
