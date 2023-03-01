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


    public TestingFeatureXMLTemplate(GeneratedPluginMeta pluginMeta,
            FeatureMeta featureMeta, List<FragmentMeta> fragmentMetas,
            List<ContributingPluginMeta> contributingPluginMetas)
            throws IOException {
        super(
                NodeGenerator.class
                        .getResourceAsStream("templates/testingfeature/testingfeature.xml.template"));

        replace("@@pluginName@@", pluginMeta.getName());

        replace("@@pluginVersion@@", pluginMeta.getVersion());
        replace("@@packageName@@", pluginMeta.getPackageRoot());

        replace("@@description@@",
                StringEscapeUtils.escapeXml(featureMeta.getDescription()));
        replace("@@copyright@@",
                StringEscapeUtils.escapeXml(featureMeta.getCopyright()));
        replace("@@license@@",
                StringEscapeUtils.escapeXml(featureMeta.getLicense()));
    }
}
