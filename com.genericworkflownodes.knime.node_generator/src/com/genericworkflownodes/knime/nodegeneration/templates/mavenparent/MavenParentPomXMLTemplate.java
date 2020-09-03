package com.genericworkflownodes.knime.nodegeneration.templates.mavenparent;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class MavenParentPomXMLTemplate extends Template {

    private static final Logger LOGGER = Logger
            .getLogger(MavenParentPomXMLTemplate.class.getName());


    public MavenParentPomXMLTemplate(GeneratedPluginMeta pluginMeta,
    		List<FragmentMeta> fragmentMetas,
            List<ContributingPluginMeta> contributingPluginMetas,
            boolean hasTestingFeature)
            throws IOException {
        super(NodeGenerator.class
                .getResourceAsStream("templates/mavenparent/parent.pom.xml.template"));

        replace("@@packageName@@", pluginMeta.getPackageRoot());

        registerPluginModule(pluginMeta, hasTestingFeature);
        registerFragmentModules(fragmentMetas);
        registerContributingPluginModules(contributingPluginMetas);
    }


    private void registerPluginModule(GeneratedPluginMeta pluginMeta, boolean hasTestingFeature) {
        String pluginList = 
        		String.format("\t\t<module>../%s</module>\n",
        				pluginMeta.getId());
        //
        pluginList += 
        		String.format("\t\t<module>../%s.feature</module>\n",
                        pluginMeta.getId());
        if (hasTestingFeature)
        {
            pluginList += 
            		String.format("\t\t<module>../%s.feature.testing</module>\n",
                            pluginMeta.getId());
        }
        replace("@@PLUGIN@@", pluginList);
    }

    private void registerFragmentModules(List<FragmentMeta> fragmentMetas) {
        String fragmentList = "";
        for (FragmentMeta fragmentMeta : fragmentMetas) {
            fragmentList += String.format("\t\t<module>../%s</module>\n",
                    fragmentMeta.getId());
        }

        replace("@@FRAGMENTS@@", fragmentList);
    }

    private void registerContributingPluginModules(
            List<ContributingPluginMeta> contributingPluginMetas) {
        String contributingPluginList = "";
        for (ContributingPluginMeta contributingPluginMeta : contributingPluginMetas) {
            contributingPluginList += String.format("\t\t<module>../%s</module>\n",
                    contributingPluginMeta.getId());
        }

        replace("@@CONTRIBUTING_PLUGINS@@", contributingPluginList);
    }
}
