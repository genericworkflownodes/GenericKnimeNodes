package com.genericworkflownodes.knime.nodegeneration.templates.feature;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

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

		this.replace("@@description@@",
				StringEscapeUtils.escapeXml(featureMeta.getDescription()));
		this.replace("@@copyright@@",
				StringEscapeUtils.escapeXml(featureMeta.getCopyright()));
		this.replace("@@license@@",
				StringEscapeUtils.escapeXml(featureMeta.getLicense()));

		this.registerGeneratedPlugin(pluginMeta);
		this.registerFragments(fragmentMetas);
		this.registerContributingPlugins(contributingPluginMetas);
	}

	private void registerGeneratedPlugin(GeneratedPluginMeta pluginMeta) {
		String pluginList = String.format("\t<plugin\n" + "\t\tid=\"%s\"\n"
				+ "\t\tdownload-size=\"0\"\n" + "\t\tinstall-size=\"0\"\n"
				+ "\t\tversion=\"0.0.0\"\n" + "\t\tunpack=\"false\"/>\n\n",
				pluginMeta.getId());

		this.replace("@@PLUGIN@@", pluginList);
	}

	private void registerFragments(List<FragmentMeta> fragmentMetas) {
		String fragmentList = "";
		for (FragmentMeta fragmentMeta : fragmentMetas) {
			fragmentList += String.format(
					"\t<plugin id=\"%s\"\n" + "\t\tos=\"%s\"\n"
							+ "\t\tarch=\"%s\"\n" + "\t\tdownload-size=\"0\"\n"
							+ "\t\tinstall-size=\"0\"\n"
							+ "\t\tversion=\"0.0.0\"\n"
							+ "\t\tfragment=\"true\"/>\n\n",
					fragmentMeta.getId(), fragmentMeta.getOs().toOsgiOs(),
					fragmentMeta.getArch().toOsgiArch());
		}

		this.replace("@@FRAGMENTS@@", fragmentList);
	}

	private void registerContributingPlugins(
			List<ContributingPluginMeta> contributingPluginMetas) {
		String contributingPluginList = "";
		for (ContributingPluginMeta contributingPluginMeta : contributingPluginMetas) {
			contributingPluginList += String.format("\t<plugin\n" + "\t\tid=\"%s\"\n"
					+ "\t\tdownload-size=\"0\"\n" + "\t\tinstall-size=\"0\"\n"
					+ "\t\tversion=\"%s\"\n" + "\t\tunpack=\"false\"/>\n\n",
					contributingPluginMeta.getId(),
					contributingPluginMeta.getVersion());
		}

		this.replace("@@CONTRIBUTING_PLUGINS@@", contributingPluginList);
	}
}
