package com.genericworkflownodes.knime.nodegeneration.templates.mavenparent;

import java.io.IOException;

import com.genericworkflownodes.knime.nodegeneration.NodeGenerator;
import com.genericworkflownodes.knime.nodegeneration.templates.Template;

public class MavenParentProjectTemplate extends Template {

    public MavenParentProjectTemplate(String packageName) throws IOException {
        super(NodeGenerator.class
                .getResourceAsStream("templates/mavenparent/parent.project.template"));

        this.replace("%PACKAGE_NAME%", packageName);
    }

}
