/**
 * Copyright (c) 2023, GKN Team.
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
package com.genericworkflownodes.knime.nodegeneration.templates;

import java.io.IOException;

import com.genericworkflownodes.knime.nodegeneration.NodeGenerator;
import com.genericworkflownodes.knime.nodegeneration.model.meta.GeneratedPluginMeta;

/**
 * Abstraction of the PluginActivator template.
 * 
 * @author jpfeuffer
 */
public class GenericResourceProviderClassTemplate extends Template {

    /**
     * Constructor.
     * 
     * @param generatedPluginMeta
     *            The name of the package, where the GenericResourceProvider implementation will be
     *            located.
     * @throws IOException
     *             Will be thrown if the access to the template file fails.
     */
    public GenericResourceProviderClassTemplate(final GeneratedPluginMeta generatedPluginMeta)
    		throws IOException {
        super(NodeGenerator.class
                .getResourceAsStream("templates/GenericResourceProvider.java.template"));

        replace("__PACKAGE__", generatedPluginMeta.getPackageRoot());
        replace("__PAYLOAD__", generatedPluginMeta.sourceDir.getPayloadDirectory().getName());
    }
}
