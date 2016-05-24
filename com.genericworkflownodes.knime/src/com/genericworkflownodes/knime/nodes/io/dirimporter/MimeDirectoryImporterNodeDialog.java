/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 *
 * History
 *   Oct 30, 2012 (Patrick Winter): created
 */
package com.genericworkflownodes.knime.nodes.io.dirimporter;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.knime.base.filehandling.NodeUtils;
import org.knime.base.filehandling.remote.connectioninformation.port.ConnectionInformation;
import org.knime.base.filehandling.remote.connectioninformation.port.ConnectionInformationPortObjectSpec;
import org.knime.base.filehandling.remote.dialog.RemoteFileChooser;
import org.knime.base.filehandling.remote.dialog.RemoteFileChooserPanel;
import org.knime.base.node.io.listfiles.ListFiles.Filter;
import org.knime.base.util.WildcardMatcher;
import org.knime.core.node.FlowVariableModel;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.util.ConvenientComboBoxRenderer;
import org.knime.core.node.util.FilesHistoryPanel;
import org.knime.core.node.util.FilesHistoryPanel.LocationValidation;
import org.knime.core.node.workflow.FlowVariable;

/**
 * <code>NodeDialog</code> for the node.
 *
 *
 * @author Patrick Winter, KNIME.com, Zurich, Switzerland
 */
public class MimeDirectoryImporterNodeDialog extends NodeDialogPane {

    private static final int HORIZ_SPACE = 10;

    private static final int PANEL_WIDTH = 585;

    private ConnectionInformation m_connectionInformation;

    private JLabel m_info;

    private RemoteFileChooserPanel m_directory;

    private JCheckBox m_recursive;

    private FilesHistoryPanel m_localdirectory;

    private JPanel m_localdirectoryPanel;

    private JComboBox m_extensionField;
    
    private JComboBox m_expressionField;

    private JCheckBox m_caseSensitive;

    private JRadioButton m_filterALLRadio;

    //private JRadioButton m_filterExtensionsRadio;

    private JRadioButton m_filterRegExpRadio;

    private JRadioButton m_filterWildCardsRadio;

    /**
     * New pane for configuring the node dialog.
     */
    public MimeDirectoryImporterNodeDialog() {
        // Info
        m_info = new JLabel();

        FlowVariableModel fvm = createFlowVariableModel("directory", FlowVariable.Type.STRING);
        // Directory (remote location)
        m_directory =
            new RemoteFileChooserPanel(getPanel(), "Directory", true, "directoryHistory", RemoteFileChooser.SELECT_DIR,
                fvm, m_connectionInformation);
        // Directory (local location)
        m_localdirectory =
            new FilesHistoryPanel(fvm,
                "localDirectoryHistory", LocationValidation.DirectoryInput, new String[]{});
        m_localdirectory.setSelectMode(JFileChooser.DIRECTORIES_ONLY);

        // Recursive
        m_recursive = new JCheckBox("Recursive");
        // Set layout
        addTab("Options", initLayout());
    }

    /**
     * Create and fill panel for the dialog.
     *
     *
     * @return The panel for the dialog
     */
    private JPanel initLayout() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        // Directory
        NodeUtils.resetGBC(gbc);
        m_localdirectoryPanel = new JPanel(new GridBagLayout());
        gbc.weightx = 1;
        m_localdirectoryPanel.add(m_localdirectory, gbc);
        gbc.weightx = 0;
        gbc.gridx++;
        gbc.insets = new Insets(5, 0, 5, 5);
        m_localdirectoryPanel.setBorder(new TitledBorder(new EtchedBorder(), "Directory"));
        // Outer panel
        NodeUtils.resetGBC(gbc);
        gbc.weightx = 1;
        panel.add(m_info, gbc);
        gbc.gridy++;
        panel.add(m_directory.getPanel(), gbc);
        gbc.gridy++;
        panel.add(m_localdirectoryPanel, gbc);
        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridy++;
        panel.add(m_recursive, gbc);
        createFiltersModels();
        gbc.gridy++;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(createFilterBox(), gbc);
        return panel;
    }

    /**
     * This method create the Filter-Box.
     *
     * @return Filter-Box
     */
    private Box createFilterBox() {
        Box panel2 = Box.createVerticalBox();
        panel2.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Filter:"));

        // Get the same height for Location and extension field.
        int buttonHeight = new JButton("Browse...").getPreferredSize().height;

        m_extensionField = new JComboBox();
        m_extensionField.setEditable(true);
        m_extensionField.setRenderer(new ConvenientComboBoxRenderer());
        m_extensionField
                .setMaximumSize(new Dimension(PANEL_WIDTH, buttonHeight));
        m_extensionField.setMinimumSize(new Dimension(250, buttonHeight));
        m_extensionField.setPreferredSize(new Dimension(250, buttonHeight));
        
        m_expressionField = new JComboBox();
        m_expressionField.setEditable(true);
        m_expressionField.setRenderer(new ConvenientComboBoxRenderer());
        m_expressionField
                .setMaximumSize(new Dimension(PANEL_WIDTH, buttonHeight));
        m_expressionField.setMinimumSize(new Dimension(250, buttonHeight));
        m_expressionField.setPreferredSize(new Dimension(250, buttonHeight));

        Box extBox = Box.createHorizontalBox();
        extBox.add(Box.createHorizontalStrut(HORIZ_SPACE));
        extBox.add(new JLabel("Extension:"));
        extBox.add(Box.createHorizontalStrut(HORIZ_SPACE));
        extBox.add(m_extensionField);
        extBox.add(Box.createHorizontalStrut(HORIZ_SPACE));
        extBox.add(new JLabel("(Required, starting with dot.)"));
        
        Box expBox = Box.createHorizontalBox();
        expBox.add(new JLabel("Filter expression:"));
        expBox.add(Box.createHorizontalStrut(HORIZ_SPACE));
        expBox.add(m_expressionField);
        expBox.add(Box.createHorizontalStrut(HORIZ_SPACE));

        m_caseSensitive = new JCheckBox();
        m_caseSensitive.setText("case sensitive");

        JPanel filterBox = new JPanel(new GridLayout(2, 3));
        filterBox.add(m_filterALLRadio);
        //filterBox.add(m_filterExtensionsRadio);
        filterBox.add(m_caseSensitive);
        filterBox.add(m_filterRegExpRadio);
        filterBox.add(m_filterWildCardsRadio);

        Box filterBox2 = Box.createHorizontalBox();
        filterBox2.add(Box.createHorizontalStrut(HORIZ_SPACE));
        filterBox2.add(filterBox);
        filterBox2.add(Box.createHorizontalStrut(PANEL_WIDTH / 4));

        panel2.add(extBox);
        panel2.add(expBox);
        panel2.add(filterBox2);

        panel2.setMaximumSize(new Dimension(PANEL_WIDTH, 120));
        panel2.setMinimumSize(new Dimension(PANEL_WIDTH, 120));

        return panel2;
    }

    /** creates the filter radio buttons. */
    private void createFiltersModels() {
        m_filterALLRadio = new JRadioButton();
        m_filterALLRadio.setText("none");
        m_filterALLRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent arg0) {
                m_expressionField.setEnabled(false);
            }
        });
        //m_filterExtensionsRadio = new JRadioButton();
        //m_filterExtensionsRadio.setText("file extension(s)");
//        m_filterExtensionsRadio.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(final ActionEvent arg0) {
//                m_extensionField.setEnabled(true);
//            }
//        });

        m_filterRegExpRadio = new JRadioButton();
        m_filterRegExpRadio.setText("regular expression");
        m_filterRegExpRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent arg0) {
                m_expressionField.setEnabled(true);
            }
        });

        m_filterWildCardsRadio = new JRadioButton();
        m_filterWildCardsRadio.setText("wildcard pattern");
        m_filterWildCardsRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent arg0) {
                m_expressionField.setEnabled(true);
            }
        });

        ButtonGroup group = new ButtonGroup();
        group.add(m_filterALLRadio);
        //group.add(m_filterExtensionsRadio);
        group.add(m_filterRegExpRadio);
        group.add(m_filterWildCardsRadio);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
        throws NotConfigurableException {
        // Check if a port object is available
        if (specs[0] != null) {
            ConnectionInformationPortObjectSpec object = (ConnectionInformationPortObjectSpec)specs[0];
            m_connectionInformation = object.getConnectionInformation();
            // Check if the port object has connection information
            if (m_connectionInformation == null) {
                throw new NotConfigurableException("No connection information available");
            }
            m_info.setText("List from: " + m_connectionInformation.toURI());
        } else {
            m_connectionInformation = null;
            m_info.setText("List from: local machine");
        }
        // Show only one of the location panels
        m_directory.getPanel().setVisible(m_connectionInformation != null);
        m_localdirectoryPanel.setVisible(m_connectionInformation == null);
        m_directory.setConnectionInformation(m_connectionInformation);
        // Load configuration
        ListDirectoryConfiguration config = new ListDirectoryConfiguration();
        config.loadSettingsInDialog(settings);
        m_directory.setSelection(config.getDirectory());
        m_localdirectory.setSelectedFile(config.getDirectory());
        m_recursive.setSelected(config.getRecursive());

        // add previous selections to the extension textfield
        String[] history_ext = ListDirectoryConfiguration.getExtensionHistory();
        m_extensionField.removeAllItems();
        for (String str : history_ext) {
            m_extensionField.addItem(str);
        }
        
        // add previous selections to the expression textfield
        String[] history_exp = ListDirectoryConfiguration.getExpressionHistory();
        m_expressionField.removeAllItems();
        for (String str : history_exp) {
            m_expressionField.addItem(str);
        }

        m_caseSensitive.setSelected(config.isCaseSensitive());
        String ext = config.getExtensionsString();
        m_extensionField.getEditor().setItem(ext == null ? "" : ext);
        switch (config.getFilter()) {
            case RegExp:
                m_filterRegExpRadio.doClick();
                break;
            case Wildcards:
                m_filterWildCardsRadio.doClick();
                break;
            default:
                m_filterALLRadio.doClick();
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        ListDirectoryConfiguration config = new ListDirectoryConfiguration();
        // Set setting only from the correct panel
        if (m_connectionInformation != null) {
            config.setDirectory(m_directory.getSelection());
        } else {
            config.setDirectory(m_localdirectory.getSelectedFile());
        }
        config.setRecursive(m_recursive.isSelected());

        config.setCaseSensitive(m_caseSensitive.isSelected());
        String extension = m_extensionField.getEditor().getItem().toString();
        config.setExtensionsString(extension);
        String expression = m_expressionField.getEditor().getItem().toString();
        config.setExpressionsString(expression);

        //Filter extfilter;
        //extfilter = Filter.Extensions;
        //config.setExtFilter(extfilter);
        
        // save the selected radio-Button
        Filter filter;
        if (m_filterALLRadio.isSelected()) {
            filter = Filter.None;
        } else if (m_filterRegExpRadio.isSelected()) {
            if (expression.trim().isEmpty()) {
                throw new InvalidSettingsException(
                        "Enter valid regular expressin pattern");
            }
            try {
                String pattern = expression;
                Pattern.compile(pattern);
            } catch (PatternSyntaxException pse) {
                throw new InvalidSettingsException("Error in pattern: ('"
                        + pse.getMessage(), pse);
            }
            filter = Filter.RegExp;
        } else if (m_filterWildCardsRadio.isSelected()) {

            if ((expression).length() <= 0) {
                throw new InvalidSettingsException(
                        "Enter valid wildcard pattern");
            }
            try {
                String pattern = expression;
                pattern = WildcardMatcher.wildcardToRegex(pattern);
                Pattern.compile(pattern);
            } catch (PatternSyntaxException pse) {
                throw new InvalidSettingsException("Error in pattern: '"
                        + pse.getMessage(), pse);
            }
            filter = Filter.Wildcards;
        } else { // one button must be selected though
            filter = Filter.None;
        }
        config.setFilter(filter);

        if (extension.isEmpty() | !extension.startsWith(".")){
            throw new InvalidSettingsException("Enter valid extension");
        }
        
        config.saveSettingsTo(settings);
        m_localdirectory.addToHistory();
    }
}
