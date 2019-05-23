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
     * New pane for configuring the YaraIndexLoader node.
     */
    protected IndexLoaderNodeDialog(Object obj) {
    	
        super();
        
        // Index File selection dependent on available index types
        final SettingsModelString file_selection = IndexLoaderNodeModel.createSettingsModelFileSelection();
        final DialogComponentIndexChooser index_chooser =  new DialogComponentIndexChooser(file_selection, // SettingsModelString
                "IndexLoaderNodeDialog", IndexLoaderNodeModel.available_index_types);
        
        addDialogComponent(index_chooser); 
        
    	
    }
    
    

    
    
}

