package com.genericworkflownodes.knime.nodegeneration.templates.feature;

import java.io.IOException;

import com.genericworkflownodes.knime.nodegeneration.NodeGenerator;
import com.genericworkflownodes.knime.nodegeneration.model.meta.FeatureMeta;
import com.genericworkflownodes.knime.nodegeneration.templates.Template;

/**
 * Template file for the feature.xml file of the feature.
 * 
 * @author jpfeuffer
 */
public class FeaturePomXMLTemplate extends Template {


    public FeaturePomXMLTemplate(FeatureMeta featureMeta)
            throws IOException {
        super(NodeGenerator.class
                .getResourceAsStream("templates/feature/feature.pom.xml.template"));

        replace("@@featureVersion@@", featureMeta.getVersion().replace(".qualifier", "-SNAPSHOT"));
        replace("@@featureId@@", featureMeta.getId());
        replace("@@groupId@@", featureMeta.getGroupid());
    }
}
