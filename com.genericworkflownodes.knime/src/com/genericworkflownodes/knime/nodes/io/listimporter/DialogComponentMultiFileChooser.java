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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXTipOfTheDay.ShowOnStartupChoice;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DialogComponent;
import org.knime.core.node.defaultnodesettings.SettingsModelStringArray;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.util.FileUtil;

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
    private static final class FileListModel extends AbstractListModel<String> {
        /**
         * The serialVersionUID.
         */
        private static final long serialVersionUID = 1080200522143129018L;

        /**
         * The resulting files.
         */
        private final List<String> files;
        
        @Override
        public String getElementAt(final int index) {
            return files.get(index);
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
            files = new ArrayList<String>();
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
                files.add(f);
            }
            fireContentsChanged(this, 0, getSize());
        }

        /**
         * Updates the underlying list with new file values. Uses the files' absolute paths.
         *
         * @param newFiles
         *            The new files to display.
         */
        public void updateFileList(final File[] newFiles) {
            for (File f : newFiles) {
                files.add(f.getAbsolutePath());
            }
            fireContentsChanged(this, 0, getSize());
        }
        
        /**
         * Updates the underlying list with new String values.
         *
         * @param newFiles
         *            The new files to display.
         */
        public void updateFileList(final String[] newFiles) {
            for (String f : newFiles) {
                files.add(f);
            }
            fireContentsChanged(this, 0, getSize());
        }

        /**
         * Gives direct access to the underlying file list.
         *
         * @return The list of stored files.
         */
        public List<String> getFiles() {
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
    public DialogComponentMultiFileChooser(SettingsModelStringArray model, boolean showPathConversion) {
        super(model);

        chooser = new JFileChooser();

        // enable multiple selections
        chooser.setMultiSelectionEnabled(true);

        //SpringLayout springLayout = new SpringLayout();
        //getComponentPanel().setLayout(springLayout);
        getComponentPanel().setLayout(new BorderLayout());
        
        // Create some items to add to the list
        final FileListModel listModel = new FileListModel(model);
        listbox = new JList(listModel);
        listbox.setLayoutOrientation(JList.VERTICAL);
        listbox.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        listbox.setVisibleRowCount(-1);

        JScrollPane listScroller = new JScrollPane(listbox);
        listScroller.setPreferredSize(new Dimension(SCROLLPANE_WIDTH,
                SCROLL_PANE_HEIGHT));

        getComponentPanel().add(listScroller, BorderLayout.CENTER);

        addButton = new JButton("Add");
        removeButton = new JButton("Remove");
        clearButton = new JButton("Clear");

        // adjust size for all three buttons to the widest (remove)
        
        JPanel fileButtons = new JPanel();
        fileButtons.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        
        fileButtons.add(addButton, gbc);
        fileButtons.add(removeButton, gbc);
        fileButtons.add(clearButton, gbc);
        
        if (showPathConversion) {
            JButton convert = new JButton("Convert...");
            convert.addActionListener((ae) -> {
                Object[] options = {"Absolute", "Mountpoint Name", "Mountpoint", "Workflow"};
                
                int choice = JOptionPane.showOptionDialog(null,
                        "Select the conversion mode for the file paths.\n"
                        + "Workflow: Converts the file paths to workflow relative paths\n"
                        + "Mountpoint: Converts the file paths to mountpoint relativ paths\n"
                        + "Mountpoint Name: Converts the file paths to paths relative to a specific mountpoint\n"
                        + "Absolute: Converts the file paths back to absolute paths",
                        "Path Conversion", JOptionPane.DEFAULT_OPTION, 
                        JOptionPane.INFORMATION_MESSAGE,  null, options, options[3]);
                String[] fp = getFilePaths();
                listModel.clear();
                
                switch (choice) {
                    case 0:
                        listModel.updateFileList(convertToNone(fp));
                        break;
                    case 1:
                        String mp = JOptionPane.showInputDialog(null, "Choose a mountpoint", "Mountpoint Selection", JOptionPane.PLAIN_MESSAGE);
                        listModel.updateFileList(convert(fp, mp));
                        break;
                    case 2:
                        listModel.updateFileList(convert(fp, "knime.mountpoint"));
                        break;
                    case 3:
                        listModel.updateFileList(convert(fp, "knime.workflow"));
                        break;
                }
            });
            fileButtons.add(convert);
        }
        getComponentPanel().add(fileButtons, BorderLayout.LINE_END);
        
        addListeners();
    }
    
    private String knimeToNormal(String path) throws InvalidPathException, MalformedURLException {
        if (path.startsWith("knime:")) {
            return FileUtil.getFileFromURL(FileUtil.toURL(path)).getAbsolutePath();
        } else {
            return path;
        }
    }
    
    private String[] convertToNone(String[] files) {
        String[] converted = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            String path = files[i];
            try {
                converted[i] = knimeToNormal(path);
            } catch (InvalidPathException | MalformedURLException e) {
                JOptionPane.showMessageDialog(null, path + " cannot be converted", "Error", JOptionPane.ERROR_MESSAGE);
                converted[i] = path;
            }
        }
        return converted;
    }
    
    private String shorten(String s, int maxLength) {
        if (s.length() <= maxLength) {
            return s;
        }
        int l1 = (int)Math.ceil((double)maxLength / 2) - 3;
        int l2 = (int)Math.floor((double)maxLength / 2) - 2;
        return s.substring(0, l1) + "[...]" + s.substring(s.length() - l2);
    }
    
    private String[] convert(String[] files, String mode) {
        String[] converted = new String[files.length];
        // First we make normal paths from all of the files
        List<String> errors = new ArrayList<String>();
        for (int i = 0; i < files.length; i++) {
            String path = files[i];
            try {
                converted[i] = knimeToNormal(path);
            } catch (InvalidPathException | MalformedURLException e) {
                errors.add(shorten(path, 50));
            }
        }
        if (errors.size() > 0) {
            String paths = String.join(",\n", errors.toArray(new String[0]));
            JOptionPane.showMessageDialog(null, "The following paths cannot be converted:\n" + paths, "Error", JOptionPane.ERROR_MESSAGE);
            // All or nothing
            return files;
        }
        
        // Now we resolve the file paths to the relative paths
        String prefix = "knime://" + mode + "/";
        URL url;
        try {
            url = FileUtil.toURL(prefix);
        } catch (InvalidPathException | MalformedURLException e) {
            // Should never happen
            JOptionPane.showMessageDialog(null, "Cannot resolve KNIME relative paths.", "Error", JOptionPane.ERROR_MESSAGE);
            return files;
        }
         // Not sure why, but resolveToPath does not work for specific mountpoints (e.g. knime://LOCAL/)
        Path localPath = Paths.get(FileUtil.getFileFromURL(url).toURI());
        for (int i = 0; i < files.length; i++) {
            Path relative = localPath.relativize(Paths.get(converted[i]));
            converted[i] = prefix + relative.toString();
        }
        return converted;
    }
    
    public DialogComponentMultiFileChooser(SettingsModelStringArray model) {
        this(model, false);
    }
    

    /**
     * @return the current file paths
     */
    public String[] getFilePaths() {
        return ((FileListModel)listbox.getModel()).getFiles().toArray(new String[0]);
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

        for (String file : ((FileListModel) listbox.getModel()).getFiles()) {
            String filename = file;
            filenames[idx] = filename;
            idx++;
        }

        ((SettingsModelStringArray) getModel()).setStringArrayValue(filenames);
    }

}
