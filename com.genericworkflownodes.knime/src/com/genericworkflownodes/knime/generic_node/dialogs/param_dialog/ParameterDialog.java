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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.TableColumn;
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
import com.genericworkflownodes.knime.config.citation.Citation;
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
     * TreeModelListener for netbeans.swing.Outline
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
                // Gets description from hidden third row
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
     * @author aiche, jpfeuffer
     */
    private final class ParamDialogDataProvider implements RenderDataProvider {

        @Override
        public Color getBackground(Object node) {
          //Set through the ParamCellRenderer.
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
            //Set through the ParamCellRenderer.
            return null;
        }

        @Override
        public Icon getIcon(Object arg0) {
            // TODO Auto-generated method stub
            // Return empty Icon for "no icon"
            //return new ImageIcon();
            // Return null for "standard" file and folder icons
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
    private JTextPane header;
    private JPanel headerpanel;
    private Outline table;
    private JTextPane help;
    private JPanel helppanel;
    private JCheckBox toggle;
    private OutlineModel model;
    private ParameterDialogTreeModel treemdl;

    private static final Font MAND_FONT = new Font("Dialog", Font.BOLD, 12);

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

        // create the NetBeans Outline object (basically a TreeTable)
        createTable();

        // adjust size of columns initially to fit the screen
        updateTableView();

        // create (citation text) header
        if(config.getCitations() != null && !config.getCitations().isEmpty()) {
            createHeader();
            StringBuilder sb = new StringBuilder();
            sb.append("<html>\n");
            for (Citation c : config.getCitations()) {
                if (c.getDoi() != null || c.getDoi().isEmpty()) {
                    try {
                        sb.append("<a href=\"");
                        sb.append(c.getDoiLink());
                        sb.append("\"> doi:");
                        sb.append(c.getDoi());
                        sb.append("</a>");
                    } catch (MalformedURLException e) {
                        sb.append("doi: ");
                        sb.append(c.getDoi());
                        e.printStackTrace();
                    }
                    sb.append("<br>\n");
                }
            }
            header.setText(sb.toString());
        }

        // create the sub controls on the bottom (documentation and toggle for advanced)
        createHelpPane();
        createShowAdvancedToggle();

        // set the custom renderer for the first column (the "tree" or "parameter" column)
        TableColumn treecol = table.getColumnModel().getColumn(0);
        treecol.setCellRenderer(treemdl.getCellRenderer());

        // finally add controls to panel
        addControlsToPanel();
        updateTableView();
    }

    private void createHeader() {
        // use JPanel to be able to create a border
        headerpanel = new JPanel();
        headerpanel.setLayout(new BorderLayout());
        TitledBorder b = new TitledBorder ( new EtchedBorder (), "Please cite:" );
        b.setTitleFont(MAND_FONT);
        headerpanel.setBorder(b);
        headerpanel.setPreferredSize(new Dimension(table.getWidth(), 50));
        // fill the Panel with a non-editable HTML TextPane
        header = new JTextPane();
        header.setContentType("text/html");
        header.setEditable(false);
        header.addHyperlinkListener(new HyperlinkListener() {
            // Open Desktop browser when clicking links (e.g. dois)
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    if (Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().browse(e.getURL().toURI());
                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        } catch (URISyntaxException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });
        headerpanel.add(new JScrollPane(header));
    }

    private void createTable() {
        table = new Outline();
        // under some circumstances the cellEditor gets lost, therefore we
        // register a default for parameter objects
        table.setDefaultEditor(Parameter.class, treemdl.getCellEditor());
        table.setRenderDataProvider(new ParamDialogDataProvider());
        table.setMinimumSize(new Dimension(1000, 500));
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //Disabled Auto resizing, otherwise it sometimes closes the Comboboxes right
        //away because of a redraw caused by the dropdown arrow increasing the col width
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        //Root node has no param and no function
        table.setRootVisible(false);
        table.setModel(model);
        //Set MinWidth here, set Preferred width in the TableAdjuster
        table.getColumnModel().getColumn(0).setMinWidth(50);
        table.getColumnModel().getColumn(1).setMinWidth(50);
        table.getColumnModel().getColumn(2).setMinWidth(50);

        addSelectionListener();
    }

    private void addSelectionListener() {
        table.getSelectionModel().addListSelectionListener(
                new ParamDialogListSelectionListener());
    }

    private void addControlsToPanel() {
        // only display citation header if there are citations
        if (headerpanel != null){
            add(headerpanel, new GridBagConstraints(0, 0, 1, 1, 1.0, .2f,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(2, 2, 2, 2), 0, 0));
        }
        add(new JScrollPane(table), new GridBagConstraints(0, 1, 1, 1, 1.0, .79f,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(2, 2, 2, 2), 0, 0));
        add(helppanel, new GridBagConstraints(0, 2, 1, 3, 1.0, .2f,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(2, 2, 2, 2), 0, 0));
        add(toggle, new GridBagConstraints(0, 5, 1, 1, 1.0, .01f,
                GridBagConstraints.SOUTHEAST, GridBagConstraints.VERTICAL,
                new Insets(2, 2, 2, 2), 0, 0));
    }

    private void createHelpPane() {
        helppanel = new JPanel();
        helppanel.setLayout(new BorderLayout());
        TitledBorder b = new TitledBorder ( new EtchedBorder (), "Parameter description:" );
        b.setTitleFont(MAND_FONT);
        helppanel.setBorder(b);
        helppanel.setPreferredSize(new Dimension(table.getWidth(), 50));
        help = new JTextPane();
        help.setEditable(false);
        helppanel.add(new JScrollPane(help));
    }

    private void createShowAdvancedToggle() {
        toggle = new JCheckBox("Show advanced parameter");
        toggle.setFont(new Font("Dialog", Font.PLAIN, 10));
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
        table.setSize(table.getSize());
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
