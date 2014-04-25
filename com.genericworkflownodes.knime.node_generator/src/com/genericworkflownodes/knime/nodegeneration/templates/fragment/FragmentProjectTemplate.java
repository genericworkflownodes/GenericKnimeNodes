package com.genericworkflownodes.knime.nodegeneration.templates.fragment;

import java.io.IOException;

import com.genericworkflownodes.knime.nodegeneration.NodeGenerator;
import com.genericworkflownodes.knime.nodegeneration.templates.Template;

public class FragmentProjectTemplate extends Template {

    public FragmentProjectTemplate(final String fragmentName)
            throws IOException {
        super(
                NodeGenerator.class
                        .getResourceAsStream("templates/fragment/fragment.project.template"));

        replace("%FRAGMENT_NAME%", fragmentName);
    }
}
