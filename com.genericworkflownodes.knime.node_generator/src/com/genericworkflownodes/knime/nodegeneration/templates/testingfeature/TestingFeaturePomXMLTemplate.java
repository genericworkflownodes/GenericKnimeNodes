package com.genericworkflownodes.knime.nodegeneration.templates.testingfeature;

import java.io.IOException;

import com.genericworkflownodes.knime.nodegeneration.NodeGenerator;
import com.genericworkflownodes.knime.nodegeneration.model.meta.GeneratedPluginMeta;
import com.genericworkflownodes.knime.nodegeneration.templates.Template;

public class TestingFeaturePomXMLTemplate extends Template {

    public TestingFeaturePomXMLTemplate(GeneratedPluginMeta pluginMeta)
            throws IOException {
        super(NodeGenerator.class
                .getResourceAsStream("templates/testingfeature/testingfeature.pom.xml.template"));

        String[] versionParts = pluginMeta.getGeneratedPluginVersion().split("\\.");
        replace("@@packageVersion@@", versionParts[0]+"."+versionParts[1]+"."+versionParts[2]);
        replace("@@pluginVersion@@", pluginMeta.getGeneratedPluginVersion());
    }

}