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
package com.genericworkflownodes.knime.nodes.io.dirloader;


import javax.swing.JFileChooser;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;
import org.knime.core.node.defaultnodesettings.SettingsModelString;


/**
 * <code>NodeDialog</code> for the "DirectoryLoader" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Kerstin Neubert, FU Berlin
 */
public class DirectoryLoaderNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring the IndexReader node.
     */
    protected DirectoryLoaderNodeDialog() {
    	
        super();
        addDialogComponent(new DialogComponentFileChooser(
                new SettingsModelString(DirectoryLoaderNodeModel.CFG_DIRECTORYNAME,""), 
                "MimeFileImporterNodeDialog",
        		JFileChooser.OPEN_DIALOG,
        		true));
        
    	
    }
    
    

    
    
}

