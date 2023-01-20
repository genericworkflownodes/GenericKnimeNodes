package com.genericworkflownodes.knime.nodegeneration.templates.feature;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.genericworkflownodes.knime.nodegeneration.NodeGenerator;
import com.genericworkflownodes.knime.nodegeneration.model.meta.ContributingPluginMeta;
import com.genericworkflownodes.knime.nodegeneration.model.meta.FeatureMeta;
import com.genericworkflownodes.knime.nodegeneration.model.meta.FragmentMeta;
import com.genericworkflownodes.knime.nodegeneration.model.meta.GeneratedPluginMeta;
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

        
        //////// TODO should be checked at "parsing"/construction time!
        // we will ensure that the version ends with a qualifier to make sure
        // that the qualifier is properly updated when something changes
        Matcher m = featureMeta.matchVersion(featureMeta.getVersion());

        // return value is either "" empty string if there is no qualifier in
        // any fragment or it is ".qualifier" with a dot in front.
        String quali = featureMeta.findLatestQualifier(m.group(4));
        
        if (quali == ".qualifier")
        {
        	quali = "-SNAPSHOT";
        }
        
        // assemble a complete version
        String newVersion = m.group(1)
                + (m.group(2) != null ? m.group(2) : ".0")
                + (m.group(3) != null ? m.group(3) : ".0")
                + quali;
        
        
        //////////////////


        replace("@@featureVersion@@", newVersion);
        replace("@@featureId@@", featureMeta.getId());
        replace("@@groupId@@", featureMeta.getGroupid());
    }
}
