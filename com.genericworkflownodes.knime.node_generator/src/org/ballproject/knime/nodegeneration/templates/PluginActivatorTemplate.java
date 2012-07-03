/**
 * Copyright (c) 2012, Björn Kahlert.
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
package org.ballproject.knime.nodegeneration.templates;

import java.io.IOException;
import java.util.List;

import org.ballproject.knime.base.util.StringUtils;
import org.ballproject.knime.nodegeneration.NodeGenerator;

/**
 * Abstraction of the PluginActivator template.
 * 
 * @author bkahlert, aiche
 */
public class PluginActivatorTemplate extends Template {

	/**
	 * Constructor.
	 * 
	 * @param packageName
	 *            The name of the package, where the PluginActivator will be
	 *            located.
	 * @param nodeNames
	 *            The nodes that will be contained in the plugin.
	 * @throws IOException
	 *             Will be thrown if the access to the template file fails.
	 */
	public PluginActivatorTemplate(final String packageName,
			final List<String> nodeNames) throws IOException {
		super(NodeGenerator.class
				.getResourceAsStream("templates/PluginActivator.template"));

		this.replace("__BASE__", packageName);
		this.replace("__NODENAMES__",
				"\"" + StringUtils.join(nodeNames, "\", \"") + "\"");
	}
}
