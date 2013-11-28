/*
 * Copyright (c) 2011, Marc Röttig.
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

package com.genericworkflownodes.knime.nodes.flow.beanshell;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;

/**
 * <code>NodeDialog</code> for the "Demangler" Node.
 * 
 * 
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author roettig
 */
public class BeanShellNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring Demangler node dialog. This is just a suggestion
     * to demonstrate possible default dialog components.
     */
    protected BeanShellNodeDialog(Object obj) {
        super();
        editor = new EditorPanel();
        addTab("Java Snippet", editor);
    }

    private EditorPanel editor;

    @Override
    protected void loadSettingsFrom(NodeSettingsRO settings,
            DataTableSpec[] specs) throws NotConfigurableException {
        try {
            editor.setInitScript(settings.getString("script_init"));
            editor.setFirstPassScript(settings.getString("script_firstPass"));
            editor.setSecondPassScript(settings.getString("script_secondPass"));
        } catch (InvalidSettingsException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void saveAdditionalSettingsTo(NodeSettingsWO settings)
            throws InvalidSettingsException {
        settings.addString("script_init", editor.getInitScript());
        settings.addString("script_firstPass", editor.getFirstPassScript());
        settings.addString("script_secondPass", editor.getSecondPassScript());
    }

    @Override
    public void loadAdditionalSettingsFrom(NodeSettingsRO settings,
            DataTableSpec[] specs) throws NotConfigurableException {
        try {
            editor.setInitScript(settings.getString("script_init"));
            editor.setFirstPassScript(settings.getString("script_firstPass"));
            editor.setSecondPassScript(settings.getString("script_secondPass"));
        } catch (InvalidSettingsException e) {
            e.printStackTrace();
        }
    }
}