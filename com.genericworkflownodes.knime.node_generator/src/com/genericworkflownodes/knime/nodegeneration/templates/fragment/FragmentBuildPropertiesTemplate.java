package com.genericworkflownodes.knime.nodegeneration.templates.fragment;

import java.io.IOException;

import com.genericworkflownodes.knime.nodegeneration.NodeGenerator;
import com.genericworkflownodes.knime.nodegeneration.templates.Template;

public class FragmentBuildPropertiesTemplate extends Template {

	public FragmentBuildPropertiesTemplate() throws IOException {
		super(
				NodeGenerator.class
						.getResourceAsStream("templates/fragment/fragment.build.properties.template"));
	}

}
