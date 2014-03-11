package com.genericworkflownodes.knime.nodegeneration.templates.feature;

import java.io.IOException;

import com.genericworkflownodes.knime.nodegeneration.NodeGenerator;
import com.genericworkflownodes.knime.nodegeneration.templates.Template;

public class FeatureBuildPropertiesTemplate extends Template {

    public FeatureBuildPropertiesTemplate() throws IOException {
        super(
                NodeGenerator.class
                        .getResourceAsStream("templates/feature/feature.build.properties.template"));
    }

}
