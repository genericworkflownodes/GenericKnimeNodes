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

        String[] versionParts = fragmentMeta.getVersion().split("\\.");
        this.replace("@@fragmentVersion@@", versionParts[0]+"."+versionParts[1]+"."+versionParts[2]);
        this.replace("@@fragmentId@@", fragmentMeta.getId());
        this.replace("@@packageName@@", fragmentMeta.getHostMeta().getId());
        this.replace("@@os@@", fragmentMeta.getOs().toOsgiOs());
        this.replace("@@arch@@", fragmentMeta.getArch().toOsgiArch());
    }
}
