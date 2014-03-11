/**
 * Copyright (c) 2012, Marc Röttig, Stephan Aiche.
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
package com.genericworkflownodes.knime.toolfinderservice;

/**
 * ExternalTool holds the information for each individual tool that is
 * encapsulated into the generic KNIME nodes.
 * 
 * @author aiche
 */
public final class ExternalTool {

    /**
     * The name of the external tool.
     */
    private String m_toolName;

    /**
     * The name of the plugin containing this tool.
     */
    private String m_pluginName;

    /**
     * The expected name of the executable file of the tool.
     */
    private String m_executableName;

    public ExternalTool(String pluginName, String toolName,
            String executableName) {
        m_pluginName = pluginName;
        m_toolName = toolName;
        m_executableName = executableName;
    }

    /**
     * Get the tool name.
     * 
     * @return The name of the tool.
     */
    public String getToolName() {
        return m_toolName;
    }

    /**
     * Get the plugin name.
     * 
     * @return The name of the plugin.
     */
    public String getPluginName() {
        return m_pluginName;
    }

    /**
     * Get the executable name.
     * 
     * @return The name of the executable.
     */
    public String getExecutableName() {
        return m_executableName;
    }

    /**
     * Get the key, which represents the tool (<pluginname>_<toolname>).
     * 
     * @return The key.
     */
    public String getKey() {
        return String.format("%s_%s", m_pluginName, m_toolName);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || !(obj instanceof ExternalTool)) {
            return false;
        }
        ExternalTool eTool = (ExternalTool) obj;
        return (m_pluginName.equals(eTool.m_pluginName)
                && m_toolName.equals(eTool.m_toolName) && m_executableName
                    .equals(eTool.m_executableName));
    }

    @Override
    public int hashCode() {
        return m_pluginName.hashCode() ^ m_toolName.hashCode()
                ^ m_executableName.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s_%s", m_pluginName, m_toolName);
    }
}