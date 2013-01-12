package com.genericworkflownodes.knime.nodegeneration.templates.feature;

import java.io.IOException;
import java.util.logging.Logger;

import com.genericworkflownodes.knime.nodegeneration.NodeGenerator;
import com.genericworkflownodes.knime.nodegeneration.model.KNIMEPluginMeta;
import com.genericworkflownodes.knime.nodegeneration.templates.Template;

public class FeatureXMLTemplate extends Template {

	private static final Logger LOGGER = Logger
			.getLogger(FeatureXMLTemplate.class.getCanonicalName());

	public FeatureXMLTemplate(KNIMEPluginMeta pluginMeta, String[] plugins)
			throws IOException {
		super(NodeGenerator.class
				.getResourceAsStream("templates/feature/feature.xml.template"));

		this.replace("@@pluginName@@", pluginMeta.getName());
		this.replace("@@pluginVersion@@", pluginMeta.getVersion());
		this.replace("@@packageName@@", pluginMeta.getPackageRoot());

		registerPlugins(plugins, pluginMeta.getPackageRoot());
	}

	private void registerPlugins(String[] plugins, String basePackageName) {
		String pluginList = "";

		for (String pluginName : plugins) {
			if (basePackageName.equals(pluginName)) {
				LOGGER.info("Register base plugin: " + pluginName);
				pluginList += String
						.format("\t<plugin id=\"%s\" download-size=\"0\" install-size=\"0\" version=\"0.0.0\" unpack=\"false\"/>\n",
								pluginName);
			} else {
				LOGGER.info("Register fragment: " + pluginName);
				String[] info = pluginName.split("\\.");
				pluginList += String
						.format("\t<plugin id=\"%s\" os=\"%s\" arch=\"%s\" download-size=\"0\" install-size=\"0\" version=\"0.0.0\" unpack=\"false\" fragment=\"true\"/>\n",
								pluginName, info[info.length - 2],
								info[info.length - 1]);
			}
		}

		this.replace("@@PLUGINS@@", pluginList);
	}
}
