/**
 * Copyright (c) by GKN team
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
package com.genericworkflownodes.knime.nodes.io.index;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.knime.core.node.NodeLogger;

/**
 * Utility class  <code>IndexMap</code> for a file index.
 *
 *
 * @author Kerstin Neubert, FU Berlin
 */
public final class IndexMap {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(IndexMap.class);

    private static final String EXTENSIONPOINT_ID = "com.genericworkflownodes.knime.mime.filesuffix.Checker";

    private IndexMap() {
        // Disable default constructor
    }
   
    /**
     * Searches for all Index-Types registered through the extension point.
     *
     * @return Index-Types
     */
    private static IndexTypeEntry[] getTypesFromExtensions() {
        // Get extensions
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint point = registry.getExtensionPoint(EXTENSIONPOINT_ID);
        IExtension[] extensions = point.getExtensions();
        LOGGER.debug("Found " + extensions.length + " extensions.");
        
        // Add all configuration elements to one list
        ArrayList<IConfigurationElement> allElements = new ArrayList<IConfigurationElement>();
        for (IExtension ext : extensions) {
            IConfigurationElement[] elements = ext.getConfigurationElements();
            allElements.addAll(Arrays.asList(elements));
        }
        IndexTypeEntry[] entries = new IndexTypeEntry[allElements.size()];
        // Add each element
        for (int i = 0; i < entries.length; i++) {
            // Get Index-Type
            String type = allElements.get(i).getAttribute("name");
            entries[i] = new IndexTypeEntry(type);
            IConfigurationElement[] children = allElements.get(i).getChildren();
            
            // Get file extensions (suffix_string)
            for (int j = 0; j < children.length; j++) {
                String suffix = children[j].getAttribute("suffix_string").toLowerCase();
                entries[i].addExtension(suffix);
                LOGGER.debug("Found Type \"" + type + "\" for file extension \"" + suffix + "\"");
            }
        }
        return entries;
    }

    /**
     * @return All the registered Index-Types
     */
    public static IndexTypeEntry[] getAllTypes() {
        IndexTypeEntry[] fromExtension = getTypesFromExtensions();
        IndexTypeEntry[] result = Arrays.copyOf(fromExtension, fromExtension.length);

        return result;
    }

}
