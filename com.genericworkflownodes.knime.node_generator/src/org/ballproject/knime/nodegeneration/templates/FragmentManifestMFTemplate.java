package org.ballproject.knime.nodegeneration.templates;

import java.io.IOException;

import org.ballproject.knime.nodegeneration.NodeGenerator;
import org.ballproject.knime.nodegeneration.model.KNIMEPluginMeta;

import com.genericworkflownodes.knime.custom.Architecture;
import com.genericworkflownodes.knime.custom.OperatingSystem;

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
