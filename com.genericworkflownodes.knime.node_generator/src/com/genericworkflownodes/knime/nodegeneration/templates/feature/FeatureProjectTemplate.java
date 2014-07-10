package com.genericworkflownodes.knime.nodegeneration.templates.feature;

import java.io.IOException;

import com.genericworkflownodes.knime.nodegeneration.NodeGenerator;
import com.genericworkflownodes.knime.nodegeneration.templates.Template;

/**
 * Template file for the .project file of the feature.
 * 
 * @author aiche
 */
public class FeatureProjectTemplate extends Template {

    public FeatureProjectTemplate(String packageName) throws IOException {
        super(
                NodeGenerator.class
                        .getResourceAsStream("templates/feature/feature.project.template"));

        replace("%PACKAGE_NAME%", packageName);
    }

}
