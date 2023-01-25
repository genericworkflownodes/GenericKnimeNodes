package com.genericworkflownodes.knime.nodegeneration.templates.mavenparent;

import java.io.IOException;

import com.genericworkflownodes.knime.nodegeneration.NodeGenerator;
import com.genericworkflownodes.knime.nodegeneration.model.meta.ContributingPluginMeta;
import com.genericworkflownodes.knime.nodegeneration.model.meta.FeatureMeta;
import com.genericworkflownodes.knime.nodegeneration.model.meta.FragmentMeta;
import com.genericworkflownodes.knime.nodegeneration.model.meta.GeneratedPluginMeta;
import com.genericworkflownodes.knime.nodegeneration.model.meta.UpdateSiteMeta;
import com.genericworkflownodes.knime.nodegeneration.templates.Template;

/**
 * Template file for the parent pom.xml file.
 * 
 * @author jpfeuffer
 */
public class MavenParentPomXMLTemplate extends Template {

    public MavenParentPomXMLTemplate(UpdateSiteMeta siteMeta)
            throws IOException {
        super(NodeGenerator.class
                .getResourceAsStream("templates/mavenparent/parent.pom.xml.template"));

        replace("@@groupId@@", siteMeta.getGroupId());

        String moduleString = "";
        for (FeatureMeta feature : siteMeta.featureMetas) {
        	for (GeneratedPluginMeta plugin : feature.generatedPluginMetas) {
        		for (FragmentMeta fragment : plugin.generatedFragmentMetas) {
        			moduleString += String.format("\t\t<module>%s</module>\n", fragment.getId());
        		}
        		moduleString += String.format("\t\t<module>%s</module>\n", plugin.getId());
        	}
        	for (ContributingPluginMeta plugin : feature.contributingPluginMetas) {
        		moduleString += String.format("\t\t<module>%s</module>\n", plugin.getId());
        	}
        	moduleString += String.format("\t\t<module>%s</module>\n", feature.getId());
        }
        replace("@@MODULES@@", moduleString);
    }
}
