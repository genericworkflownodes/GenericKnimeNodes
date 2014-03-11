/**
 * Copyright (c) 2012, Stephan Aiche.
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
package com.genericworkflownodes.knime.preferences;

import java.io.File;

import com.genericworkflownodes.knime.GenericNodesPlugin;
import com.genericworkflownodes.knime.toolfinderservice.ExternalTool;
import com.genericworkflownodes.knime.toolfinderservice.IToolLocator;
import com.genericworkflownodes.knime.toolfinderservice.IToolLocator.ToolPathType;
import com.genericworkflownodes.knime.toolfinderservice.PluginPreferenceToolLocator;

/**
 * Abstraction of all settings related to single tool.
 * 
 * @author aiche
 */
public class ExternalToolSettings {

    /**
     * The path to the executable of the tool.
     */
    private String m_localToolPath;

    /**
     * True if a binary of this tool was shipped with the plugin.
     */
    private boolean m_hasShippedBinary;

    /**
     * The type of path selected (shipped, local).
     */
    private IToolLocator.ToolPathType m_selectedToolPathType;

    /**
     * The tool that is represented.
     */
    private final ExternalTool m_tool;

    /**
     * Constructor initializing the settings object.
     * 
     * @param tool
     *            The tool.
     */
    public ExternalToolSettings(ExternalTool tool) {
        m_tool = tool;

        m_localToolPath = "";
        m_hasShippedBinary = false;
        m_selectedToolPathType = ToolPathType.UNKNOWN;

        load();
    }

    /**
     * Loads all related settings for the tool.
     */
    private final void load() {
        // we load the path from the IToolLocator
        try {
            m_selectedToolPathType = PluginPreferenceToolLocator
                    .getToolLocatorService().getConfiguredToolPathType(m_tool);

            m_hasShippedBinary = PluginPreferenceToolLocator
                    .getToolLocatorService().hasValidToolPath(m_tool,
                            ToolPathType.SHIPPED);

            File lToolPath = PluginPreferenceToolLocator
                    .getToolLocatorService().getToolPath(m_tool,
                            IToolLocator.ToolPathType.USER_DEFINED);
            if (lToolPath != null && lToolPath.exists()) {
                m_localToolPath = lToolPath.getAbsolutePath();
            }
        } catch (Exception e) {
            GenericNodesPlugin
                    .log("Could not load user-defined tool path for tool: "
                            + m_tool);
            GenericNodesPlugin.log(e.getMessage());
        }
    }

    /**
     * Saves all related settings for the tool.
     */
    public void save() {
        try {
            PluginPreferenceToolLocator.getToolLocatorService().setToolPath(
                    m_tool, new File(m_localToolPath),
                    ToolPathType.USER_DEFINED);
            PluginPreferenceToolLocator.getToolLocatorService()
                    .updateToolPathType(m_tool, m_selectedToolPathType);
        } catch (Exception e) {
            GenericNodesPlugin
                    .log("Could not load user-defined tool path for tool: "
                            + m_tool);
            GenericNodesPlugin.log(e.getMessage());
        }
    }

    /**
     * Returns the local tool path.
     * 
     * @return the localToolPath
     */
    public String getLocalToolPath() {
        return m_localToolPath;
    }

    /**
     * Sets the local tool path to a new value.
     * 
     * @param localToolPath
     *            the localToolPath to set
     */
    public void setLocalToolPath(String localToolPath) {
        m_localToolPath = localToolPath;
    }

    /**
     * @return True if the tool has a shipped binary, false otherwise.
     */
    public boolean hasShippedBinary() {
        return m_hasShippedBinary;
    }

    /**
     * @param hasShippedBinary
     *            the m_hasShippedBinary to set
     */
    public void setHasShippedBinary(boolean hasShippedBinary) {
        m_hasShippedBinary = hasShippedBinary;
    }

    /**
     * Returns which kind of tool path is selected for this tool
     * 
     * @return the selectedToolPathType
     */
    public IToolLocator.ToolPathType getSelectedToolPathType() {
        return m_selectedToolPathType;
    }

    /**
     * Sets the tool path type.
     * 
     * @param selectedToolPathType
     *            The new selected tool path type.
     */
    public void setSelectedToolPathType(
            IToolLocator.ToolPathType selectedToolPathType) {
        m_selectedToolPathType = selectedToolPathType;
    }

    /**
     * Get the tool name.
     * 
     * @return The tool name.
     */
    public ExternalTool getTool() {
        return m_tool;
    }
}
