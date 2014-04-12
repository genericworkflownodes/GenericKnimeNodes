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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * Meta information of a eclipse plugin.
 * 
 * @author aiche, bkahlert
 */
public class PluginMeta {

    private final String id;
    private String version;

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

    /**
     * Updates the version qualifier of the plug-in meta. Update the qualifier
     * part of the plug-in version, e.g., 0.1.0.20000101 update with 20100101 ->
     * 0.1.0.20100101
     * 
     * @param qualifier
     *            The potentially higher qualifier.
     */
    protected void updateVersion(String qualifier) {
        final Pattern p = Pattern
                .compile("^(\\d+)(\\.\\d+)?(\\.\\d+)?(.[a-zA-Z0-9]+)?$");
        Matcher m = p.matcher(getVersion());
        boolean found = m.find();
        assert found : "Version should be compliant to the pattern ^(\\d+)(\\.\\d+)?(\\.\\d+)?(.[a-zA-Z0-9-_]+)?$";

        // version has no qualifier
        String newVersion = m.group(1)
                + (m.group(2) != null ? m.group(2) : ".0")
                + (m.group(3) != null ? m.group(3) : ".0");
        // append qualifier
        if (m.group(4) == null
                || qualifier.compareTo(m.group(4).substring(1)) > 0) {
            // external qualifier
            newVersion += "." + qualifier;
        } else {
            // our own
            newVersion += m.group(4);
        }
        version = newVersion;
    }

}
