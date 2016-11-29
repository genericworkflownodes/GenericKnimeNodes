/**
 * Copyright (c) 2011, Marc Röttig.
 * Copyright (c) 2012-2014, Stephan Aiche.
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

package com.genericworkflownodes.knime.generic_node.dialogs.param_dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.knime.core.node.NodeLogger;
import org.netbeans.swing.outline.DefaultOutlineModel;
import org.netbeans.swing.outline.Outline;
import org.netbeans.swing.outline.OutlineModel;
import org.netbeans.swing.outline.RenderDataProvider;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.generic_node.dialogs.param_dialog.param_tree.ParameterNode;
import com.genericworkflownodes.knime.generic_node.dialogs.param_dialog.ui_helper.TableColumnAdjuster;
import com.genericworkflownodes.knime.parameter.Parameter;

/**
 * Parameter dialog visualizing the tree like parameter structure.
 * 
 * @author roettig, aiche, bkahlert, jpfeuffer
 */
public class ParameterDialog extends JPanel {

    /**
     * Logger instance.
     */
    private static final NodeLogger LOGGER = NodeLogger
            .getLogger(ParameterDialog.class);

    /**
     * TreeModelListener for netbeans.swing.Outline .
     * 
     * @author aiche
     */
    private final class ParamDialogTreeModelListener implements
            TreeModelListener {
        @Override
        public void treeStructureChanged(TreeModelEvent e) {
            // defer column adjustment till all the recreation events are
            // handled
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TableColumnAdjuster tca = new TableColumnAdjuster(table);
                    tca.adjustColumns();
                }
            });
        }

        @Override
        public void treeNodesRemoved(TreeModelEvent e) {
        }

        @Override
        public void treeNodesInserted(TreeModelEvent e) {
        }

        @Override
        public void treeNodesChanged(TreeModelEvent e) {
        }
    }

    /**
     * ListSelectionListener for the NetBeans Outline object.
     * 
     * @author aiche
     */
    private final class ParamDialogListSelectionListener implements
            ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) {
                return;
            }
            if (event.getSource() == table.getSelectionModel()) {
                int row = table.getSelectedRow();
                Object val = table.getModel().getValueAt(row, 3);
                if (val instanceof String) {
                    updateDocumentationSection((String) val);
                }
            }
        }
    }

    /**
     * The high-lighter for the NetBeans Outline object.
     * 
     * @author aiche
     */
    private final class ParamDialogDataProvider implements RenderDataProvider {

        @Override
        public Color getBackground(Object node) {
            return null;
        }

        @Override
        public String getDisplayName(Object node) {
            ParameterNode paramnode = (ParameterNode) node;
            if (paramnode.getPayload() == null) {
                return paramnode.getName();
            } else {
                return paramnode.getPayload().getKey();
            }
        }

        @Override
        public Color getForeground(Object node) {
            boolean optional = true;
            boolean advanced = false;
            ParameterNode paramnode = (ParameterNode) node;
            if (paramnode.getPayload() != null) {
                optional = paramnode.getPayload().isOptional();
                advanced = paramnode.getPayload().isAdvanced();
            }
            if (!optional) {
                return Color.blue;
                //TODO Manage to set fonts in another way
                //comp.setFont(MAND_FONT);
            } else {
                //comp.setFont(OPT_FONT);
                if (advanced) {
                    return Color.GRAY;
                }
            }
            return Color.GRAY;
        }

        @Override
        public Icon getIcon(Object arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getTooltipText(Object arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean isHtmlDisplayName(Object arg0) {
            // TODO Auto-generated method stub
            return false;
        }
    }

    private static final long serialVersionUID = 8098990326681120709L;
    private Outline table;
    private JTextPane help;
    private JCheckBox toggle;
    private OutlineModel model;
    private ParameterDialogTreeModel treemdl;

    private static final Font MAND_FONT = new Font("Dialog", Font.BOLD, 12);
    private static final Font OPT_FONT = new Font("Dialog", Font.ITALIC, 12);

    /**
     * Construct dialog from a NodeConfiguration.
     * 
     * @param config
     *            The configuration that should be represented by the dialog.
     */
    public ParameterDialog(INodeConfiguration config) {
        setLayout(new GridBagLayout());

        // create the data model for the table
        treemdl = new ParameterDialogTreeModel(config);
        treemdl.addTreeModelListener(new ParamDialogTreeModelListener());
        model = DefaultOutlineModel.createOutlineModel(treemdl, 
                new ParameterDialogRowModel(), true, "Parameter");

        // create the NetBeans Outline object
        createTable();

        // adjust size of columns initially to fit the screen
        updateTableView();

        // create the sub controls (documentation and toggle for advanced)
        createHelpPane();
        createShowAdvancedToggle();

        // finally add controls to panel
        addControlsToPanel();
    }

    private void createTable() {
        table = new Outline();
        table.setRenderDataProvider(new ParamDialogDataProvider());
        table.setMinimumSize(new Dimension(1000, 500));
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setRootVisible(false);
        table.setModel(model);
        
        // under some circumstances the cellEditor gets lost, therefore we
        // register a default for parameter objects
        table.setDefaultEditor(Parameter.class, treemdl.getCellEditor());

        addSelectionListener();
    }

    private void addSelectionListener() {
        table.getSelectionModel().addListSelectionListener(
                new ParamDialogListSelectionListener());
    }

    private void addControlsToPanel() {
        add(new JScrollPane(table), new GridBagConstraints(0, 0, 1, 1, 1.0,
                .79f, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(2, 2, 2, 2), 0, 0));
        add(new JScrollPane(help), new GridBagConstraints(0, 1, 1, 2, 1.0, .2f,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                        2, 2, 2, 2), 0, 0));
        add(toggle, new GridBagConstraints(0, 3, 1, 1, 1.0, .01f,
                GridBagConstraints.SOUTHEAST, GridBagConstraints.VERTICAL,
                new Insets(2, 2, 2, 2), 0, 0));
    }

    private void createHelpPane() {
        help = new JTextPane();
        help.setPreferredSize(new Dimension(table.getWidth(), 50));
    }

    private void createShowAdvancedToggle() {
        toggle = new JCheckBox("Show advanced parameter");
        toggle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                treemdl.setShowAdvanced(toggle.isSelected());
                treemdl.refresh();
                model.getLayout().setModel(treemdl);
                updateTableView();
            }
        });
    }

    private void updateTableView() {
        // expand full tree by default
        for (int rowid = 0; rowid < model.getRowCount(); rowid++){       
            model.getLayout().setExpandedState(model.getLayout().getPathForRow(rowid), true);
        }
        // adjust column widths
        TableColumnAdjuster tca = new TableColumnAdjuster(table);
        tca.adjustColumns();
        // plot new
        table.updateUI();
    }

    private void updateDocumentationSection(String description) {
        StyledDocument doc = (StyledDocument) help.getDocument();
        Style style = doc.addStyle("StyleName", null);
        StyleConstants.setFontFamily(style, "SansSerif");

        try {
            doc.remove(0, doc.getLength());
            doc.insertString(0, description, style);
        } catch (BadLocationException e) {
            LOGGER.warn("Documentation update failed.", e);
        }
    }

    /**
     * Ensures that all edit operations are finalized.
     */
    public void stopEditing() {
        treemdl.getCellEditor().stopCellEditing();
    }

}
