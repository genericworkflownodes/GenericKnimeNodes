package com.genericworkflownodes.knime.nodegeneration.templates;

import java.io.IOException;

import com.genericworkflownodes.knime.nodegeneration.NodeGenerator;

public class BuildPropertiesTemplate extends Template {

    public BuildPropertiesTemplate(boolean hasIcons) throws IOException {
        super(NodeGenerator.class.getResourceAsStream(hasIcons ? "templates/build.properties.template" : "templates/build.properties-noicons.template"));
    }

}
