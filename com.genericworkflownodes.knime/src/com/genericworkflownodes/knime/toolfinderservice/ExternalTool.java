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

import java.util.Objects;

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
    private final String toolName;

    /**
     * The name of the plugin containing this tool.
     */
    private final String pluginName;

    /**
     * The expected name of the executable file of the tool.
     */
    private final String executableName;

    /**
     * C'tor.
     *
     * @param pluginName
     *            Name of the plugin.
     * @param toolName
     *            Name of the tool.
     * @param executableName
     *            Name of the executable.
     */
    public ExternalTool(String pluginName, String toolName,
            String executableName) {
        this.pluginName = pluginName;
        this.toolName = toolName;
        this.executableName = executableName;
    }

    /**
     * Get the tool name.
     *
     * @return The name of the tool.
     */
    public String getToolName() {
        return this.toolName;
    }

    /**
     * Get the plugin name.
     *
     * @return The name of the plugin.
     */
    public String getPluginName() {
        return this.pluginName;
    }

    /**
     * Get the executable name.
     *
     * @return The name of the executable.
     */
    public String getExecutableName() {
        return this.executableName;
    }

    /**
     * Get the key, which represents the tool (<pluginname>_<toolname>).
     *
     * @return The key.
     */
    public String getKey() {
        return String.format("%s_%s", this.pluginName, this.toolName);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ExternalTool)) {
            return false;
        }
        final ExternalTool other = (ExternalTool) obj;
        return     this.pluginName.equals(other.pluginName)
                && this.toolName.equals(other.toolName)
                && this.executableName.equals(other.executableName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.pluginName, this.toolName, this.executableName);
    }

    @Override
    public String toString() {
        return String.format("%s_%s", this.pluginName, this.toolName);
    }
}
