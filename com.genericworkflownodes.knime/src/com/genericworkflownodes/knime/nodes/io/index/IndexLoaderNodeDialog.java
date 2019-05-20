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
     * New pane for configuring the YaraIndexReader node.
     */
    protected IndexLoaderNodeDialog(Object obj) {
    	
        super();
        
        final SettingsModelString file_selection = IndexLoaderNodeModel.createSettingsModelFileSelection();
        final DialogComponentIndexChooser index_chooser =  new DialogComponentIndexChooser(file_selection, // SettingsModelString
                "IndexReaderNodeDialog", IndexLoaderNodeModel.available_index_types);
        
        addDialogComponent(index_chooser); 

        /*
        final SettingsModelString index_selection = IndexLoaderNodeModel.createSettingsModelSelection();

        addDialogComponent(new DialogComponentStringSelection(index_selection, "Index type", IndexLoaderNodeModel.available_index_types));
        
        */
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

