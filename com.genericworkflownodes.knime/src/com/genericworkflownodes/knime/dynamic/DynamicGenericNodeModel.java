/**
 * Copyright (c) 2017-2019, Julianus Pfeuffer, Alexander Fillbrunn
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
package com.genericworkflownodes.knime.dynamic;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.custom.config.IPluginConfiguration;
import com.genericworkflownodes.knime.generic_node.GenericKnimeNodeModel;

public class DynamicGenericNodeModel extends GenericKnimeNodeModel {

    protected DynamicGenericNodeModel(INodeConfiguration nodeConfig,
            IPluginConfiguration pluginConfig, String[][] fileEndingsInPorts,
            String[][] fileEndingsOutPorts) {
        super(nodeConfig, pluginConfig, fileEndingsInPorts, fileEndingsOutPorts);
        // TODO Auto-generated constructor stub
    }

}
