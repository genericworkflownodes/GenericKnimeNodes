package com.genericworkflownodes.knime.nodegeneration.templates.fragment;

import java.io.IOException;

import com.genericworkflownodes.knime.custom.Architecture;
import com.genericworkflownodes.knime.custom.OperatingSystem;
import com.genericworkflownodes.knime.nodegeneration.NodeGenerator;
import com.genericworkflownodes.knime.nodegeneration.model.meta.GeneratedPluginMeta;
import com.genericworkflownodes.knime.nodegeneration.templates.Template;

public class FragmentManifestMFTemplate extends Template {

	public FragmentManifestMFTemplate(GeneratedPluginMeta pluginMeta,
			OperatingSystem os, Architecture arch) throws IOException {
		super(
				NodeGenerator.class
						.getResourceAsStream("templates/fragment/Fragment_MANIFEST.MF.template"));

		this.replace("@@pluginName@@", pluginMeta.getName());
		this.replace("@@pluginVersion@@", pluginMeta.getVersion());
		this.replace("@@packageName@@", pluginMeta.getPackageRoot());

		this.replace("@@os@@", os.toOsgiOs());
		this.replace("@@arch@@", arch.toOsgiArch());
	}
}
