package com.genericworkflownodes.knime.nodegeneration.templates.fragment;

import java.io.IOException;

import com.genericworkflownodes.knime.nodegeneration.NodeGenerator;
import com.genericworkflownodes.knime.nodegeneration.model.meta.FragmentMeta;
import com.genericworkflownodes.knime.nodegeneration.templates.Template;

public class FragmentPomXmlTemplate extends Template {

    public FragmentPomXmlTemplate(FragmentMeta fragmentMeta)
            throws IOException {
        super(
                NodeGenerator.class
                        .getResourceAsStream("templates/fragment/fragment.pom.xml.template"));
        this.replace("@@fragmentVersion@@", fragmentMeta.getHostMeta().getGeneratedPluginVersion().replace(".qualifier", "-SNAPSHOT"));
        this.replace("@@fragmentId@@", fragmentMeta.getId());
        this.replace("@@packageName@@", fragmentMeta.getHostMeta().getId());
        this.replace("@@os@@", fragmentMeta.getOs().toOsgiOs());
        this.replace("@@ws@@", fragmentMeta.getOs().getOsgiWs());
        this.replace("@@arch@@", fragmentMeta.getArch().toOsgiArch());
    }
}
