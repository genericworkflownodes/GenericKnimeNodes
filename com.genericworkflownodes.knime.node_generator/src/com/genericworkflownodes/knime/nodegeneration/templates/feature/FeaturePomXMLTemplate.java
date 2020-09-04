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

    private static final Logger LOGGER = Logger
            .getLogger(FeaturePomXMLTemplate.class.getName());
    
    private final static Pattern VERSION_PATTERN = Pattern
            .compile("^(\\d+)(\\.\\d+)?(\\.\\d+)?(.[a-zA-Z0-9]+)?$");

    private String findLatestQualifier(String pluginQualifier,
            List<FragmentMeta> fragmentMetas,
            List<ContributingPluginMeta> contributingPluginMetas) {

        String highestQualifier = "";
        if (pluginQualifier != null)
            highestQualifier = pluginQualifier;

        for (FragmentMeta fMeta : fragmentMetas) {
            Matcher m = matchVersion(fMeta.getVersion());
            if (m.group(4) != null
                    && m.group(4).compareTo(highestQualifier) > 0) {
                highestQualifier = m.group(4);
            }
        }

        for (ContributingPluginMeta cMeta : contributingPluginMetas) {
            Matcher m = matchVersion(cMeta.getVersion());
            if (m.group(4) != null
                    && m.group(4).compareTo(highestQualifier) > 0) {
                highestQualifier = m.group(4);
            }

        }

        return highestQualifier;
    }

    public FeaturePomXMLTemplate(GeneratedPluginMeta pluginMeta,
            FeatureMeta featureMeta, List<FragmentMeta> fragmentMetas,
            List<ContributingPluginMeta> contributingPluginMetas)
            throws IOException {
        super(NodeGenerator.class
                .getResourceAsStream("templates/feature/feature.pom.xml.template"));

        // we will ensure that the version ends with a qualifier to make sure
        // that the qualifier is properly updated when something changes
        Matcher m = matchVersion(pluginMeta.getVersion());

        // return value is either "" empty string if there is no qualifier in
        // any fragment or it is ".qualifier" with a dot in front.
        String quali = findLatestQualifier(m.group(4), fragmentMetas,
                contributingPluginMetas);
        
        if (quali == ".qualifier")
        {
        	quali = "-SNAPSHOT";
        }
        
        // assemble a complete version
        String newVersion = m.group(1)
                + (m.group(2) != null ? m.group(2) : ".0")
                + (m.group(3) != null ? m.group(3) : ".0")
                + quali;

        replace("@@packageVersion@@", newVersion);
        replace("@@packageName@@", pluginMeta.getPackageRoot());
    }

    private Matcher matchVersion(final String version) {
        Matcher m = VERSION_PATTERN.matcher(version);

        // via definition this has to be true
        boolean found = m.matches();
        if (!found || m.groupCount() != 4)
        {
        	LOGGER.log(Level.SEVERE, "Version should be compliant to the pattern ^(\\d+)(\\.\\d+)?(\\.\\d+)?(.[a-zA-Z0-9-_]+)?$."
        			+ "This should not happen since it was checked during reading of the files. Please report as bug.");
        }

        return m;
    }
}
