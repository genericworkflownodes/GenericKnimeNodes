package com.genericworkflownodes.knime.nodegeneration.templates.fragment;

import java.io.IOException;

import com.genericworkflownodes.knime.nodegeneration.NodeGenerator;
import com.genericworkflownodes.knime.nodegeneration.model.meta.FragmentMeta;
import com.genericworkflownodes.knime.nodegeneration.templates.Template;

public class FragmentManifestMFTemplate extends Template {

    public FragmentManifestMFTemplate(FragmentMeta fragmentMeta)
            throws IOException {
        super(
                NodeGenerator.class
                        .getResourceAsStream("templates/fragment/Fragment_MANIFEST.MF.template"));

        this.replace("@@hostName@@", fragmentMeta.getHostMeta().getName());

        this.replace("@@hostId@@", fragmentMeta.getHostMeta().getId());
        this.replace("@@hostVersion@@", fragmentMeta.getHostMeta().getVersion());

        this.replace("@@fragmentId@@", fragmentMeta.getId());
        this.replace("@@fragmentVersion@@", fragmentMeta.getVersion());

        this.replace("@@os@@", fragmentMeta.getOs().toOsgiOs());
        this.replace("@@archosgi@@", fragmentMeta.getArchStringForManifest());
        this.replace("@@arch@@", fragmentMeta.getArch().toOsgiArch());
    }
}
