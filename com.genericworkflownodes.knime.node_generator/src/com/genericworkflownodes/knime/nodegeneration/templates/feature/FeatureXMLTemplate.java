package com.genericworkflownodes.knime.nodegeneration.templates.feature;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

		// we will ensure that the version ends with a qualifier to make sure
		// that the qualifier is properly updated when something changes

		Pattern versionPattern = Pattern
				.compile("^(\\d+)(\\.\\d+)?(\\.\\d+)?(.[a-zA-Z0-9]+)?$");
		Matcher m = versionPattern.matcher(pluginMeta.getVersion());

		// via definition this has to be true
		boolean found = m.find();
		assert found : "Version should be compliant to the pattern ^\\d+(\\.\\d+(\\.\\d+(.[a-zA-Z0-9]+)?)?)?$";
		assert m.groupCount() == 4 : "Something went wrong when matching the version.";

		// assemble a complete version
		String newVersion = m.group(1)
				+ (m.group(2) != null ? m.group(2) : ".0")
				+ (m.group(3) != null ? m.group(3) : ".0") + ".qualifier";

		this.replace("@@pluginVersion@@", newVersion);
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
			contributingPluginList += String.format("\t<plugin\n"
					+ "\t\tid=\"%s\"\n" + "\t\tdownload-size=\"0\"\n"
					+ "\t\tinstall-size=\"0\"\n" + "\t\tversion=\"0.0.0\"\n"
					+ "\t\tunpack=\"false\"/>\n\n",
					contributingPluginMeta.getId());
		}

		this.replace("@@CONTRIBUTING_PLUGINS@@", contributingPluginList);
	}
}
