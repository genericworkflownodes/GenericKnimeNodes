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


import javax.swing.JFileChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;
import org.knime.core.node.defaultnodesettings.DialogComponentOptionalString;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelOptionalString;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import com.genericworkflownodes.knime.nodes.io.index.DialogComponentIndexChooser;

/**
 * <code>NodeDialog</code> for the "IndexLoader" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Kerstin Neubert, FU Berlin
 */
public class IndexLoaderNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring the IndexLoader node.
     */
    protected IndexLoaderNodeDialog(Object obj) {
    	
        super();
        
        // Index File selection depends on available index types
        // Index types must be available from the extension point "com.genericworkflownodes.knime.mime.filesuffix.Checker"
        final SettingsModelString file_selection = IndexLoaderNodeModel.createSettingsModelFileSelection();
        final DialogComponentIndexChooser index_chooser =  new DialogComponentIndexChooser(file_selection, 
                "IndexLoaderNodeDialog", IndexLoaderNodeModel.available_index_types);        
        addDialogComponent(index_chooser); 
        
    
        // add listeners to the Settings model
        // index will be determined by the selected file type
        index_chooser.addChangeListener(new ChangeListener() {
            public void stateChanged(final ChangeEvent e) {
                String index_type = IndexTypeHelper.getIndextype(file_selection.getStringValue());
                if (index_type != null) {
                    index_chooser.setToolTipText(index_type + " Index");
                }
                else {
                    index_chooser.setToolTipText("unknown Index type");
                }  
            }

        });
    
    }
    
}

