package com.genericworkflownodes.knime.filesplitter;


import javax.swing.JPanel;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;

/**
*
* @author Alexander Fillbrunn
*/
public abstract class SplitterPanel extends JPanel {
   /**
    * Saves settings for this panel.
    * @param settings the settings to save to
    * @throws InvalidSettingsException when there was a problem saving the settings
    */
   public abstract void saveSettingsTo(NodeSettingsWO settings) throws InvalidSettingsException;

   /**
    * Loads settings into this panel.
    * @param settings the settings to load from
    * @param specs the specs of the input of the node that displays the panel
    * @throws NotConfigurableException if SettingModels used by the panel cannot be configured
    * @throws InvalidSettingsException if an error occurs during loading
    */
   public abstract void loadSettingsFrom(NodeSettingsRO settings, PortObjectSpec[] specs)
                               throws NotConfigurableException, InvalidSettingsException;
}