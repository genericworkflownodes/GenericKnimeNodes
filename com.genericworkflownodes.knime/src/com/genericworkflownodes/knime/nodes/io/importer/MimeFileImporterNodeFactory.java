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
package com.genericworkflownodes.knime.nodes.io.importer;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Optional;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.commons.io.FilenameUtils;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;
import org.knime.core.node.defaultnodesettings.DialogComponentLabel;
import org.knime.core.node.defaultnodesettings.DialogComponentOptionalString;

import com.genericworkflownodes.knime.nodes.GenericNodeView;
import com.genericworkflownodes.util.MIMETypeHelper;

/**
 * <code>NodeFactory</code> for the "MimeFileImporter" Node.
 *
 * @author roettig
 */
@Deprecated
public final class MimeFileImporterNodeFactory extends
NodeFactory<MimeFileImporterNodeModel> {

    /**
     *  Represents the MIMETypeListener that allows Live preview of the MIMe Type that
     *  the user is going to select on the Dialog of the Importer Node
     */
    private final static class MimeTypeListener implements ActionListener, KeyListener {

        private final JCheckBox checkbox;
        private final JTextField opt;
        private final JComboBox<String> fileBox;
        private final JLabel label;

        private  MimeTypeListener(JCheckBox checkBox, JTextField opt, JComboBox<String> fileBox, JLabel label) {
            this.checkbox = checkBox;
            this.opt = opt;
            this.fileBox = fileBox;
            this.label = label;
        }

        // Updates the label of the Dialog with the MIME type
        private void update() {
            // Determine the MIME Type
            final Optional<String> mime = checkbox.isSelected() ? MIMETypeHelper.getMIMEtypeByExtension(opt.getText())
                    : MIMETypeHelper.getMIMEtype((String) fileBox.getEditor().getItem());
            final String ext = checkbox.isSelected() ? opt.getText() :
                FilenameUtils.getExtension((String)fileBox.getEditor().getItem());
            this.label.setText("MIME Type: " + mime.orElse("unregistered ('" + ext + "')"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            this.update();
        }
        @Override
        public void keyPressed(KeyEvent e) {
            this.update();
        }
        @Override
        public void keyReleased(KeyEvent e) {
            this.update();
        }
        @Override
        public void keyTyped(KeyEvent e) {
            this.update();
        }
    }

    /*
     * Registers the MimeType Change Listener when the Dialog is created
     */
    private void registerMimeTypeChangeListener(
            final DialogComponentFileChooser fileChooser,
            final DialogComponentOptionalString fileExtension,
            final DialogComponentLabel fileLabel) {

        // Get all the Components for which the Event Listener needs to be added
        @SuppressWarnings("unchecked")
        final JComboBox<String> fileBox = (JComboBox<String>) ((JPanel) fileChooser
                .getComponentPanel().getComponent(0)).getComponent(0);
        final JCheckBox checkbox = (JCheckBox) fileExtension.getComponentPanel().getComponent(0);
        final JTextField opt = (JTextField) fileExtension.getComponentPanel().getComponent(1);
        final JLabel label = (JLabel) fileLabel.getComponentPanel().getComponent(0);

        // Create the MimeTypeListener
        final MimeTypeListener mimeTypeListender = new MimeTypeListener(checkbox, opt, fileBox, label);
        final KeyListener keyListener = (KeyListener) mimeTypeListender;

        // Register the MimeType Listener as ActionListener
        mimeTypeListender.update();
        fileBox.addActionListener(mimeTypeListender);
        checkbox.addActionListener(mimeTypeListender);
        opt.addActionListener(mimeTypeListender);

        // Register the MimeType Listener as Key Listener
        opt.addKeyListener(keyListener);
        fileBox.getEditor().getEditorComponent().addKeyListener(keyListener);
        fileBox.addKeyListener(keyListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MimeFileImporterNodeModel createNodeModel() {
        return new MimeFileImporterNodeModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrNodeViews() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeView<MimeFileImporterNodeModel> createNodeView(
            final int viewIndex, final MimeFileImporterNodeModel nodeModel) {

        return new GenericNodeView<MimeFileImporterNodeModel>(nodeModel, (model) -> {
            String toDisplay = "File does not exist!";
            if (model.getContent() != null)
            {
                toDisplay = new String (model.getContent());
            }
            final JTextArea text = new JTextArea(toDisplay, 40, 80);
            text.setFont(new Font("Monospaced", Font.BOLD, 12));
            return new JScrollPane(text);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDialog() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeDialogPane createNodeDialogPane() {

        // The Dialog components for selecting the file and setting the file extension
        final DefaultNodeSettingsPane pane = new DefaultNodeSettingsPane();
        final DialogComponentFileChooser fileChooser = new DialogComponentFileChooser(
                MimeFileImporterNodeModel.filename(),
                "MimeFileImporterNodeDialog");
        final DialogComponentOptionalString fileExtension = new DialogComponentOptionalString(
                MimeFileImporterNodeModel.fileExtension(),
                "File extension (override)");

        // The Label for Displaying the MIME Type
        final DialogComponentLabel label = new DialogComponentLabel("MIME Type: ");

        // Registration of the ChangeListener of the MIME Type
        this.registerMimeTypeChangeListener(fileChooser, fileExtension, label);

        // Add all the Dialog components
        pane.addDialogComponent(fileChooser);
        pane.addDialogComponent(fileExtension);
        pane.addDialogComponent(label);
        return pane;
    }
}
