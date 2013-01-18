package com.genericworkflownodes.knime.nodegeneration.templates.feature;

import java.io.IOException;
import java.util.List;

import com.genericworkflownodes.knime.nodegeneration.NodeGenerator;
import com.genericworkflownodes.knime.nodegeneration.model.meta.ContributingPluginMeta;
import com.genericworkflownodes.knime.nodegeneration.model.meta.FeatureMeta;
import com.genericworkflownodes.knime.nodegeneration.model.meta.FragmentMeta;
import com.genericworkflownodes.knime.nodegeneration.model.meta.GeneratedPluginMeta;
import com.genericworkflownodes.knime.nodegeneration.templates.Template;

public class FeatureXMLTemplate extends Template {

	public FeatureXMLTemplate(GeneratedPluginMeta pluginMeta,
			FeatureMeta featureMeta, List<FragmentMeta> fragmentMetas,
			List<ContributingPluginMeta> contributingPluginMetas)
			throws IOException {
		super(NodeGenerator.class
				.getResourceAsStream("templates/feature/feature.xml.template"));

		this.replace("@@pluginName@@", pluginMeta.getName());
		this.replace("@@pluginVersion@@", pluginMeta.getVersion());
		this.replace("@@packageName@@", pluginMeta.getPackageRoot());

		this.replace("@@description@@", featureMeta.getDescription());
		this.replace("@@copyright@@", featureMeta.getCopyright());
		this.replace("@@license@@", featureMeta.getLicense());

		this.registerGeneratedPlugin(pluginMeta);
		this.registerFragments(fragmentMetas);
		this.registerContributingPlugins(contributingPluginMetas);
	}

	private void registerGeneratedPlugin(GeneratedPluginMeta pluginMeta) {
		String pluginList = String
				.format("\t<plugin id=\"%s\" download-size=\"0\" install-size=\"0\" version=\"0.0.0\" unpack=\"false\"/>\n",
						pluginMeta.getId());

		this.replace("@@PLUGIN@@", pluginList);
	}

	private void registerFragments(List<FragmentMeta> fragmentMetas) {
		String fragmentList = "";
		for (FragmentMeta fragmentMeta : fragmentMetas) {
			fragmentList += String
					.format("\t<plugin id=\"%s\" os=\"%s\" arch=\"%s\" download-size=\"0\" install-size=\"0\" version=\"0.0.0\" unpack=\"false\" fragment=\"true\"/>\n",
							fragmentMeta.getId(), fragmentMeta.getOs()
									.toOsgiOs(), fragmentMeta.getArch()
									.toOsgiArch());
		}

		this.replace("@@FRAGMENTS@@", fragmentList);
	}

	private void registerContributingPlugins(
			List<ContributingPluginMeta> contributingPluginMetas) {
		String fragmentList = "";
		for (ContributingPluginMeta contributingPluginMeta : contributingPluginMetas) {
			fragmentList += String
					.format("\t<plugin id=\"%s\" download-size=\"0\" install-size=\"0\" version=\"%s\" unpack=\"false\"/>\n",
							contributingPluginMeta.getId(),
							contributingPluginMeta.getVersion());
		}

		this.replace("@@CONTRIBUTING_PLUGINS@@", fragmentList);
	}
}
