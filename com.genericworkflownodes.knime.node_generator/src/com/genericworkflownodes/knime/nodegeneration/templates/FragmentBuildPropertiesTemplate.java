package com.genericworkflownodes.knime.nodegeneration.templates;

import java.io.IOException;

import com.genericworkflownodes.knime.nodegeneration.NodeGenerator;

public class FragmentBuildPropertiesTemplate extends Template {

	public FragmentBuildPropertiesTemplate() throws IOException {
		super(
				NodeGenerator.class
						.getResourceAsStream("templates/fragment.build.properties.template"));
	}

}
