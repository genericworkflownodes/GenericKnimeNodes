package com.genericworkflownodes.knime.nodes.io.outputfolder;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.port.PortObjectSpec;

/**
 * <code>NodeDialog</code> for the "OutputFolder" Node. Writes all the incoming
 * files to the given output folder.
 * 
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author The GKN Team
 */
public class OutputFolderNodeDialog extends NodeDialogPane {

    private final JPanel m_dialogPanel;
    private final JPanel m_componentContainer;
    private final JTextField m_textField;
    private final JButton m_searchButton;
    private final JPanel m_checkboxes;
    private final JCheckBox m_createIfNotExistsCb;
    private final JCheckBox m_overwrite;

    /**
     * New pane for configuring the OutputFolder node.
     */
    protected OutputFolderNodeDialog() {
        m_dialogPanel = new JPanel();
        m_componentContainer = new JPanel();
        m_checkboxes = new JPanel();
        m_textField = new JTextField();
        //m_textField.setPreferredSize(new Dimension(300, m_textField
        //        .getPreferredSize().height));
        m_searchButton = new JButton("Browse");
        m_searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFileChooser jfc = new JFileChooser();
                if (!"".equals(m_textField.getText().trim())
                        && new File(m_textField.getText().trim()).getParent() != null) {
                    jfc.setCurrentDirectory(new File(m_textField.getText()
                            .trim()).getParentFile());
                }

                jfc.setAcceptAllFileFilterUsed(false);

                // int returnVal = jfc.showSaveDialog(m_dialogPanel);
                jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = jfc.showDialog(m_dialogPanel,
                        "Select output folder");
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    // validate extension
                    m_textField
                            .setText(jfc.getSelectedFile().getAbsolutePath());
                }
            }
        });
        m_createIfNotExistsCb = new JCheckBox("Create folder if it does not exist");
        m_overwrite = new JCheckBox("Overwrite existing files");
        setLayout();
        addComponents();

        addTab("Choose Output Folder", m_dialogPanel);
    }


    private void setLayout() {
        m_componentContainer.setLayout(new BorderLayout());
        m_dialogPanel.setLayout(new GridBagLayout());
        m_checkboxes.setLayout(new BoxLayout(m_checkboxes, BoxLayout.Y_AXIS));
    }

    private void addComponents() {
        m_componentContainer.add(m_textField, BorderLayout.CENTER);
        m_componentContainer.add(m_searchButton, BorderLayout.LINE_END);
        m_componentContainer.add(m_checkboxes, BorderLayout.SOUTH);
        
        m_componentContainer.setBorder(BorderFactory
                .createTitledBorder("Selected output file:"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        m_dialogPanel.add(m_componentContainer, gbc);
        
        m_checkboxes.add(m_createIfNotExistsCb);
        m_checkboxes.add(m_overwrite);
    }

    @Override
    protected void saveSettingsTo(NodeSettingsWO settings)
            throws InvalidSettingsException {
        settings.addString(OutputFolderNodeModel.CFG_FOLDER_NAME, m_textField
                .getText().trim());
        settings.addBoolean(OutputFolderNodeModel.CFG_CREATE_FOLDER, m_createIfNotExistsCb.isSelected());
        settings.addBoolean(OutputFolderNodeModel.CFG_OVERWRITE, m_overwrite.isSelected());
    }

    @Override
    protected void loadSettingsFrom(NodeSettingsRO settings,
            PortObjectSpec[] specs) throws NotConfigurableException {
        // get information from settings and inspec
        m_textField.setText(settings.getString(
                OutputFolderNodeModel.CFG_FOLDER_NAME, ""));
        m_createIfNotExistsCb.setSelected(settings.getBoolean(OutputFolderNodeModel.CFG_CREATE_FOLDER, false));
        m_overwrite.setSelected(settings.getBoolean(OutputFolderNodeModel.CFG_OVERWRITE, false));
    }

}
