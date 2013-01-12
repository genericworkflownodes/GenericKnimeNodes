package com.genericworkflownodes.knime.nodegeneration.templates;

import java.io.IOException;


import com.genericworkflownodes.knime.custom.Architecture;
import com.genericworkflownodes.knime.custom.OperatingSystem;
import com.genericworkflownodes.knime.nodegeneration.NodeGenerator;
import com.genericworkflownodes.knime.nodegeneration.model.KNIMEPluginMeta;

public class FragmentManifestMFTemplate extends Template {

	public FragmentManifestMFTemplate(KNIMEPluginMeta pluginMeta,
			OperatingSystem os, Architecture arch) throws IOException {
		super(NodeGenerator.class
				.getResourceAsStream("templates/Fragment_MANIFEST.MF.template"));

		this.replace("@@pluginName@@", pluginMeta.getName());
		this.replace("@@pluginVersion@@", pluginMeta.getVersion());
		this.replace("@@packageName@@", pluginMeta.getPackageRoot());

		this.replace("@@os@@", os.toOSGIOS());
		this.replace("@@arch@@", arch.toOSGIArch());
	}
}
