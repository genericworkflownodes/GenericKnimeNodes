package com.genericworkflownodes.knime.nodegeneration.templates;

import java.io.IOException;

import com.genericworkflownodes.knime.nodegeneration.NodeGenerator;
import com.genericworkflownodes.knime.nodegeneration.model.meta.GeneratedPluginMeta;

public class ManifestMFTemplate extends Template {

    public ManifestMFTemplate(GeneratedPluginMeta pluginMeta)
            throws IOException {
        super(NodeGenerator.class
                .getResourceAsStream(pluginMeta.isResourceOnly() ? "templates/MANIFEST.MF-ResourceOnly.template" : "templates/MANIFEST.MF.template"));

        replace("@@pluginName@@", pluginMeta.getName());
        replace("@@pluginVersion@@", pluginMeta.getGeneratedPluginVersion());
        replace("@@packageName@@", pluginMeta.getPackageRoot());
        replace("@@dependencies@@", pluginMeta.getExtraDependenciesAsConcatString());
    }

}
