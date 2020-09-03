package com.genericworkflownodes.knime.nodegeneration.templates;

import java.io.IOException;

import com.genericworkflownodes.knime.nodegeneration.NodeGenerator;
import com.genericworkflownodes.knime.nodegeneration.model.meta.GeneratedPluginMeta;

public class PomXMLTemplate extends Template {

    public PomXMLTemplate(GeneratedPluginMeta pluginMeta)
            throws IOException {
        super(NodeGenerator.class
                .getResourceAsStream("templates/pom.xml.template"));

        String[] versionParts = pluginMeta.getGeneratedPluginVersion().split("\\.");
        replace("@@packageVersion@@", versionParts[0]+"."+versionParts[1]+"."+versionParts[2]);
        replace("@@packageName@@", pluginMeta.getPackageRoot());
    }

}