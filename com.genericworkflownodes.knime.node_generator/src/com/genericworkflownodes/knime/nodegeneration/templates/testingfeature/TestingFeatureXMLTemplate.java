/**
 * Copyright (c) 2014, Stephan Aiche.
 *
 * This file is part of GenericKnimeNodes.
 * 
 * GenericKnimeNodes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.genericworkflownodes.knime.nodegeneration.templates.testingfeature;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

import com.genericworkflownodes.knime.nodegeneration.NodeGenerator;
import com.genericworkflownodes.knime.nodegeneration.model.meta.ContributingPluginMeta;
import com.genericworkflownodes.knime.nodegeneration.model.meta.FeatureMeta;
import com.genericworkflownodes.knime.nodegeneration.model.meta.FragmentMeta;
import com.genericworkflownodes.knime.nodegeneration.model.meta.GeneratedPluginMeta;
import com.genericworkflownodes.knime.nodegeneration.templates.Template;

/**
 * Template file for the feature.xml file of the testing feature.
 * 
 * @author aiche
 */
public class TestingFeatureXMLTemplate extends Template {

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

    public TestingFeatureXMLTemplate(GeneratedPluginMeta pluginMeta,
            FeatureMeta featureMeta, List<FragmentMeta> fragmentMetas,
            List<ContributingPluginMeta> contributingPluginMetas)
            throws IOException {
        super(
                NodeGenerator.class
                        .getResourceAsStream("templates/testingfeature/testingfeature.xml.template"));

        replace("@@pluginName@@", pluginMeta.getName());

        // we will ensure that the version ends with a qualifier to make sure
        // that the qualifier is properly updated when something changes
        Matcher m = matchVersion(pluginMeta.getVersion());

        // assemble a complete version
        String newVersion = m.group(1)
                + (m.group(2) != null ? m.group(2) : ".0")
                + (m.group(3) != null ? m.group(3) : ".0")
                + findLatestQualifier(m.group(4), fragmentMetas,
                        contributingPluginMetas);

        replace("@@pluginVersion@@", newVersion);
        replace("@@packageName@@", pluginMeta.getPackageRoot());

        replace("@@description@@",
                StringEscapeUtils.escapeXml(featureMeta.getDescription()));
        replace("@@copyright@@",
                StringEscapeUtils.escapeXml(featureMeta.getCopyright()));
        replace("@@license@@",
                StringEscapeUtils.escapeXml(featureMeta.getLicense()));
    }

    private Matcher matchVersion(final String version) {
        Matcher m = VERSION_PATTERN.matcher(version);

        // via definition this has to be true
        boolean found = m.find();
        assert found : "Version should be compliant to the pattern ^(\\d+)(\\.\\d+)?(\\.\\d+)?(.[a-zA-Z0-9-_]+)?$";
        assert m.groupCount() == 4 : "Something went wrong when matching the version.";

        return m;
    }
}
