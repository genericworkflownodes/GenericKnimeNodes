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



import java.util.LinkedList;
import java.util.List;


/**
 * Represents an Index-Type and its registered extensions.
 * 
 * 
 * @author Kerstin Neubert, FU Berlin
 */
public class IndexTypeEntry {

    private String m_type;

    private List<String> m_extensions;

    /**
     * @param type Name of this Index-Type
     */
    IndexTypeEntry(final String type) {
        m_type = type;
        m_extensions = new LinkedList<String>();
    }

    /**
     * @return The Index-Types name
     */
    public String getType() {
        return m_type;
    }

    /**
     * @return The extensions of this Index-Type
     */
    public List<String> getExtensions() {
        return m_extensions;
    }

    /**
     * @param extension Extension to register with this type
     */
    void addExtension(final String extension) {
        m_extensions.add(extension);
    }

    /**
     * @return String for all extensions
     */
    public String toString() {
        StringBuffer result = new StringBuffer(m_type);
        for (int i = 0; i < m_extensions.size(); i++) {
            result.append(" " + m_extensions.get(i));
        }
        return result.toString();
    }

}
