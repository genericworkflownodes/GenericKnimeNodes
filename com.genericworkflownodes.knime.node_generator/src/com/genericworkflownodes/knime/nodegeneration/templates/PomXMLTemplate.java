package com.genericworkflownodes.knime.nodegeneration.templates;

import java.io.IOException;

import com.genericworkflownodes.knime.nodegeneration.NodeGenerator;
import com.genericworkflownodes.knime.nodegeneration.model.meta.GeneratedPluginMeta;

public class PomXMLTemplate extends Template {

    public PomXMLTemplate(GeneratedPluginMeta pluginMeta)
            throws IOException {
        super(NodeGenerator.class
                .getResourceAsStream("templates/pom.xml.template"));
        replace("@@packageVersion@@", pluginMeta.getGeneratedPluginVersion().replace(".qualifier", "-SNAPSHOT"));
        replace("@@packageName@@", pluginMeta.getPackageRoot());
    }

}