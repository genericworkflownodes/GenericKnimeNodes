/**
 * Copyright (c) 2012, Marc Röttig.
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
package com.genericworkflownodes.knime.nodes.io.listimporter;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DialogComponent;
import org.knime.core.node.defaultnodesettings.SettingsModelStringArray;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.util.FilesHistoryPanel;
import org.knime.core.node.util.FilesHistoryPanel.LocationValidation;
import org.knime.core.node.util.MultipleURLList;

/**
 * Dialog component to choose multiple files at once, as input for a KNIME
 * workflow.
 * 
 * @author roettig, aiche
 */
public class DialogComponentMultiFileChooser extends DialogComponent {

    /**
     * Width of the scroll pane containing the list of files.
     */
    private static final int SCROLLPANE_WIDTH = 400;

    /**
     * Height of the scroll pane containing the list of files.
     */
    private static final int SCROLL_PANE_HEIGHT = 200;

    /**
     * Custom ListModel for the file list.
     * 
     * @author aiche
     */
    private static class FileListModel extends AbstractListModel {
        /**
         * The serialVersionUID.
         */
        private static final long serialVersionUID = 1080200522143129018L;

        /**
         * The resulting files.
         */
        private List<File> files;

        @Override
        public Object getElementAt(final int index) {
            return files.get(index).getAbsolutePath();
        }

        @Override
        public int getSize() {
            return files.size();
        }

        /**
         * Transfers values from the {@link SettingsModelStringArray} into the
         * underlying model.
         * 
         * @param model
         *            The data model.
         */
        public FileListModel(final SettingsModelStringArray model) {
            files = new ArrayList<File>();
            updateFromSettingsModel(model);
            model.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(final ChangeEvent event) {
                    if (event.getSource() instanceof SettingsModelStringArray) {
                        SettingsModelStringArray eventSource = (SettingsModelStringArray) event
                                .getSource();
                        updateFromSettingsModel(eventSource);
                    }

                }
            });
        }

        /**
         * Updates the content of the Model based on
         * {@link SettingsModelStringArray}.
         * 
         * @param model
         *            The model source to update from.
         */
        private void updateFromSettingsModel(
                final SettingsModelStringArray model) {
            files.clear();
            for (String f : model.getStringArrayValue()) {
                files.add(new File(f));
            }
            fireContentsChanged(this, 0, getSize());
        }

        /**
         * Updates the underlying list with new values.
         * 
         * @param newFiles
         *            The new files to display.
         */
        public void updateFileList(final File[] newFiles) {
            for (File f : newFiles) {
                files.add(f);
            }
            fireContentsChanged(this, 0, getSize());
        }

        /**
         * Gives direct access to the underlying file list.
         * 
         * @return The list of stored files.
         */
        public List<File> getFiles() {
            return files;
        }

        /**
         * Clears the stored data.
         */
        public void clear() {
            files.clear();
            fireContentsChanged(this, 0, getSize());
        }

        /**
         * Removes the given values.
         * 
         * @param selectedIndices
         *            Indices of the values to remove.
         */
        public void remove(int[] selectedIndices) {
            // ensure we have a sorted list of indices
            Arrays.sort(selectedIndices);
            for (int index = selectedIndices.length - 1; index >= 0; --index) {
                files.remove(selectedIndices[index]);
            }
            fireContentsChanged(this, 0, getSize());
        }
    }

    /**
     * File chooser.
     */
    private JFileChooser chooser;

    /**
     * The browse button.
     */
    private JButton addButton;

    /**
     * The remove button.
     */
    private JButton removeButton;

    /**
     * But to remove all currently loaded files.
     */
    private JButton clearButton;

    /**
     * The list to visualize the files.
     */
    private JList listbox;

    /**
     * C'tor.
     * 
     * @param model
     *            The model to store the files.
     */
    public DialogComponentMultiFileChooser(SettingsModelStringArray model) {
        super(model);

        SpringLayout springLayout = new SpringLayout();
        getComponentPanel().setLayout(springLayout);

        // Create some items to add to the list
        listbox = new JList(new FileListModel(model));
        listbox.setLayoutOrientation(JList.VERTICAL);
        listbox.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        listbox.setVisibleRowCount(-1);

        JScrollPane listScroller = new JScrollPane(listbox);
        listScroller.setPreferredSize(new Dimension(SCROLLPANE_WIDTH,
                SCROLL_PANE_HEIGHT));

        getComponentPanel().add(listScroller);

        addButton = new JButton("Add");
        removeButton = new JButton("Remove");
        clearButton = new JButton("Clear");

        // adjust size for all three buttons to the widest (remove)

        getComponentPanel().add(addButton);
        getComponentPanel().add(removeButton);
        getComponentPanel().add(clearButton);

        setupLayout(springLayout, listScroller);
        addListeners();
    }

    /**
     * Adds the event listeners to all gui elements.
     */
    private void addListeners() {
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent arg0) {
                final int returnVal = chooser.showDialog(getComponentPanel()
                        .getParent(), null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File[] files = chooser.getSelectedFiles();
                    ((FileListModel) listbox.getModel()).updateFileList(files);
                }
            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                // remove file selected in list -> -1 should never happen
                // but we want to be sure
                if (listbox.getSelectedIndex() != -1) {
                    int[] selectedIndices = listbox.getSelectedIndices();
                    ((FileListModel) listbox.getModel())
                            .remove(selectedIndices);
                }
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                ((FileListModel) listbox.getModel()).clear();
            }
        });

        // make the remove button only available if something is selected
        listbox.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(final ListSelectionEvent event) {
                if (!event.getValueIsAdjusting()) {
                    removeButton.setEnabled(listbox.getSelectedIndex() != -1);
                }
            }
        });
    }

    /**
     * Initializes the {@link SpringLayout} for the content of this pane.
     * 
     * @param springLayout
     *            The used {@link SpringLayout}.
     * @param listScroller
     *            The scroller containing the {@link #listbox}.
     */
    private void setupLayout(SpringLayout springLayout, JScrollPane listScroller) {
        // arrange all items in the spring layout
        springLayout.putConstraint(SpringLayout.WEST, listScroller, 5,
                SpringLayout.WEST, getComponentPanel());
        springLayout.putConstraint(SpringLayout.NORTH, listScroller, 5,
                SpringLayout.NORTH, getComponentPanel());
        springLayout.putConstraint(SpringLayout.SOUTH, listScroller, -5,
                SpringLayout.SOUTH, getComponentPanel());

        springLayout.putConstraint(SpringLayout.WEST, addButton, 10,
                SpringLayout.EAST, listScroller);
        springLayout.putConstraint(SpringLayout.WEST, removeButton, 10,
                SpringLayout.EAST, listScroller);
        springLayout.putConstraint(SpringLayout.WEST, clearButton, 10,
                SpringLayout.EAST, listScroller);

        springLayout.putConstraint(SpringLayout.NORTH, addButton, 10,
                SpringLayout.NORTH, getComponentPanel());
        springLayout.putConstraint(SpringLayout.NORTH, removeButton,
                12 + springLayout.getConstraints(addButton).getHeight()
                        .getPreferredValue(), SpringLayout.NORTH,
                getComponentPanel());
        springLayout.putConstraint(SpringLayout.NORTH, clearButton,
                14 + 2 * springLayout.getConstraints(addButton).getHeight()
                        .getPreferredValue(), SpringLayout.NORTH,
                getComponentPanel());

        SpringLayout.Constraints addCst = springLayout
                .getConstraints(addButton);
        SpringLayout.Constraints removeCst = springLayout
                .getConstraints(removeButton);
        SpringLayout.Constraints clearCst = springLayout
                .getConstraints(clearButton);
        Spring maxSpring = Spring.max(addCst.getWidth(),
                Spring.max(removeCst.getWidth(), clearCst.getWidth()));
        addCst.setWidth(maxSpring);
        removeCst.setWidth(maxSpring);
        clearCst.setWidth(maxSpring);

        springLayout.putConstraint(SpringLayout.EAST, addButton, -10,
                SpringLayout.EAST, getComponentPanel());
        springLayout.putConstraint(SpringLayout.EAST, removeButton, -10,
                SpringLayout.EAST, getComponentPanel());
        springLayout.putConstraint(SpringLayout.EAST, clearButton, -10,
                SpringLayout.EAST, getComponentPanel());

        springLayout.putConstraint(SpringLayout.EAST, listScroller, -20
                - maxSpring.getMaximumValue(), SpringLayout.EAST,
                getComponentPanel());
    }

    @Override
    protected void checkConfigurabilityBeforeLoad(final PortObjectSpec[] arg0)
            throws NotConfigurableException {
        // we're always good - independent of the incoming spec
    }

    @Override
    protected void setEnabledComponents(final boolean flag) {
        chooser.setEnabled(flag);
    }

    @Override
    public void setToolTipText(final String tt) {
        chooser.setToolTipText(tt);
    }

    @Override
    protected void updateComponent() {
    }

    @Override
    protected void validateSettingsBeforeSave() throws InvalidSettingsException {
        String[] filenames = new String[listbox.getModel().getSize()];
        int idx = 0;

        for (File file : ((FileListModel) listbox.getModel()).getFiles()) {
            String filename = file.getAbsolutePath();
            filenames[idx] = filename;
            idx++;
        }

        ((SettingsModelStringArray) getModel()).setStringArrayValue(filenames);
    }

}
