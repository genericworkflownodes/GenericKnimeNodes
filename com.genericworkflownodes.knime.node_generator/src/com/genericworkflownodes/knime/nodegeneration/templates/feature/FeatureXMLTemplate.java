package com.genericworkflownodes.knime.nodegeneration.templates.feature;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang.StringEscapeUtils;

import com.genericworkflownodes.knime.nodegeneration.NodeGenerator;
import com.genericworkflownodes.knime.nodegeneration.model.meta.ContributingPluginMeta;
import com.genericworkflownodes.knime.nodegeneration.model.meta.FeatureMeta;
import com.genericworkflownodes.knime.nodegeneration.model.meta.FragmentMeta;
import com.genericworkflownodes.knime.nodegeneration.model.meta.GeneratedPluginMeta;
import com.genericworkflownodes.knime.nodegeneration.templates.Template;

/**
 * Template file for the feature.xml file of the feature.
 * 
 * @author aiche
 */
public class FeatureXMLTemplate extends Template {

    private static final Logger LOGGER = Logger
            .getLogger(FeatureXMLTemplate.class.getName());
    

    public FeatureXMLTemplate(FeatureMeta featureMeta)
            throws IOException {
        super(NodeGenerator.class
                .getResourceAsStream("templates/feature/feature.xml.template"));


        replace("@@featureVersion@@", featureMeta.getVersion());
        replace("@@featureName@@", featureMeta.getName());
        replace("@@featureID@@", featureMeta.getId());

        replace("@@description@@",
                StringEscapeUtils.escapeXml(featureMeta.getDescription()));
        replace("@@copyright@@",
                StringEscapeUtils.escapeXml(featureMeta.getCopyright()));
        replace("@@license@@",
                StringEscapeUtils.escapeXml(featureMeta.getLicense()));

        registerGeneratedPlugins(featureMeta.generatedPluginMetas);
        ArrayList<FragmentMeta> fragMetas = new ArrayList<FragmentMeta>();
		for (GeneratedPluginMeta pluginMeta : featureMeta.generatedPluginMetas)
        {
        	fragMetas.addAll(pluginMeta.generatedFragmentMetas);
        }
		registerFragments(fragMetas);
        registerContributingPlugins(featureMeta.contributingPluginMetas);
    }



    private void registerGeneratedPlugins(List<GeneratedPluginMeta> pluginMetas) {
    	String pluginList = "";
    	for (GeneratedPluginMeta pluginMeta : pluginMetas) {
            pluginList += String.format("\t<plugin\n" + "\t\tid=\"%s\"\n"
                    + "\t\tdownload-size=\"0\"\n" + "\t\tinstall-size=\"0\"\n"
                    + "\t\tversion=\"0.0.0\"\n" + "\t\tunpack=\"false\"/>\n\n",
                    pluginMeta.getId());
    	}

        replace("@@PLUGINS@@", pluginList);
    }

    private void registerFragments(List<FragmentMeta> fragmentMetas) {
        String fragmentList = "";
        for (FragmentMeta fragmentMeta : fragmentMetas) {
            fragmentList += String.format(
                    "\t<plugin id=\"%s\"\n" + "\t\tos=\"%s\"\n"
                            + "%s"
                    		+ "\t\tdownload-size=\"0\"\n"
                            + "\t\tinstall-size=\"0\"\n"
                            + "\t\tversion=\"0.0.0\"\n"
                            + "\t\tfragment=\"true\"/>\n\n",
                    fragmentMeta.getId(), fragmentMeta.getOs().toOsgiOs(),
                    // For macOS we currently just label as "all architectures" and hope
                    // for Rosetta emulation on Silicon macs.
                    !(fragmentMeta.getOs().toOsgiOs()=="macosx") ? "\t\tarch=\""+fragmentMeta.getArch().toOsgiArch()+"\"\n" : "");
        }

        replace("@@FRAGMENTS@@", fragmentList);
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

        replace("@@CONTRIBUTING_PLUGINS@@", contributingPluginList);
    }
}
