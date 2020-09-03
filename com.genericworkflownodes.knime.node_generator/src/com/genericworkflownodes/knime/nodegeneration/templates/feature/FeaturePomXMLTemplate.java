package com.genericworkflownodes.knime.nodegeneration.templates.feature;

import java.io.IOException;

import com.genericworkflownodes.knime.nodegeneration.NodeGenerator;
import com.genericworkflownodes.knime.nodegeneration.model.meta.GeneratedPluginMeta;
import com.genericworkflownodes.knime.nodegeneration.templates.Template;

public class FeaturePomXMLTemplate extends Template {

    public FeaturePomXMLTemplate(GeneratedPluginMeta pluginMeta)
            throws IOException {
        super(NodeGenerator.class
                .getResourceAsStream("templates/feature/feature.pom.xml.template"));
        
        String[] versionParts = pluginMeta.getGeneratedPluginVersion().split("\\.");
        replace("@@packageVersion@@", versionParts[0]+"."+versionParts[1]+"."+versionParts[2]);
        replace("@@packageName@@", pluginMeta.getPackageRoot());
    }

}