/**
 * Copyright (c) 2013, Stephan Aiche, Björn Kahlert.
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
package com.genericworkflownodes.knime.nodegeneration.model.meta;

/**
 * 
 * Meta information of a eclipse plugin.
 * 
 * @author aiche, bkahlert
 */
public class PluginMeta {

    private final String id;
    private final String version;

    /**
     * @param id
     * @param version
     */
    public PluginMeta(String id, String version) {
        super();
        this.id = id;
        this.version = version;
    }

    /**
     * Returns the plugin id.
     * 
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the plugin version.
     * 
     * @return
     */
    public String getVersion() {
        return version;
    }

}
