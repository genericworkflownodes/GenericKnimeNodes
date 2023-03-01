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

        replace("@@packageVersion@@", pluginMeta.getVersion().replace(".qualifier", "-SNAPSHOT"));
        replace("@@pluginVersion@@", pluginMeta.getGeneratedPluginVersion());
    }

}