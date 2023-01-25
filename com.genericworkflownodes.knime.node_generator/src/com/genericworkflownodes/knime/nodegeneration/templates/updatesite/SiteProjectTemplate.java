package com.genericworkflownodes.knime.nodegeneration.templates.updatesite;

import java.io.IOException;

import com.genericworkflownodes.knime.nodegeneration.NodeGenerator;
import com.genericworkflownodes.knime.nodegeneration.templates.Template;

public class SiteProjectTemplate extends Template {

    public SiteProjectTemplate(/*String packageName*/) throws IOException {
        super(NodeGenerator.class
                .getResourceAsStream("templates/mavenparent/parent.project.template"));

        // For now we call the parent pom artifact just project
        this.replace("%PACKAGE_NAME%", "project");
    }

}
