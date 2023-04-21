/**
 * Copyright (c) 2023, Julianus Pfeuffer and the GKN team.
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
package com.genericworkflownodes.knime.nodes.io.nioimporter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.Optional;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.io.FilenameUtils;
import org.knime.core.node.ConfigurableNodeFactory;
import org.knime.core.node.FlowVariableModel;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeView;
import org.knime.core.data.uri.IURIPortObject;
import org.knime.core.data.uri.URIPortObject;
import org.knime.core.node.context.NodeCreationConfiguration;
import org.knime.core.node.context.ports.PortsConfiguration;
import org.knime.filehandling.core.connections.FSCategory;
import org.knime.filehandling.core.connections.FSPath;
import org.knime.filehandling.core.data.location.variable.FSLocationVariableType;
import org.knime.filehandling.core.defaultnodesettings.EnumConfig;
import org.knime.filehandling.core.defaultnodesettings.filechooser.reader.DialogComponentReaderFileChooser;
import org.knime.filehandling.core.defaultnodesettings.filechooser.reader.SettingsModelReaderFileChooser;
import org.knime.filehandling.core.defaultnodesettings.filtermode.SettingsModelFilterMode.FilterMode;
import org.knime.filehandling.core.defaultnodesettings.status.NodeModelStatusConsumer;
import org.knime.filehandling.core.defaultnodesettings.status.StatusMessage.MessageType;
import org.knime.filehandling.core.port.FileSystemPortObject;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
//import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentLabel;
import org.knime.core.node.defaultnodesettings.DialogComponentOptionalString;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.port.PortTypeRegistry;

import com.genericworkflownodes.util.MIMETypeHelper;

/**
 * <code>NodeFactory</code> for the "MimeFileImporter" Node.
 *
 * @author jpfeuffer
 */
public final class MimeFileNioImporterNodeFactory extends
ConfigurableNodeFactory<MimeFileNioImporterNodeModel> {
    
    private static final String FS_PORT_ID = "File System Connection";
    
    /**
     *  Represents the MIMETypeListener that allows Live preview of the MIMe Type that
     *  the user is going to select on the Dialog of the Importer Node
     */
    private final static class NewMimeTypeListener implements ChangeListener, ActionListener, KeyListener {

        private final JCheckBox checkbox;
        private final JTextField opt;
        private final DialogComponentReaderFileChooser fileChooser;
        private final JLabel label;

        private NewMimeTypeListener(JCheckBox checkBox, JTextField opt, DialogComponentReaderFileChooser fileChooser, JLabel label) {
            this.checkbox = checkBox;
            this.opt = opt;
            this.fileChooser = fileChooser;
            this.label = label;
        }

        // Updates the label of the Dialog with the MIME type
        private void update() {
            // Determine the MIME Type
            Optional<String> mime = Optional.empty();
            if (checkbox.isSelected())
            {
                mime = MIMETypeHelper.getMIMEtypeByExtension(opt.getText());
                this.label.setText("MIME Type: " + mime.orElse("unregistered ('" + opt.getText() + "')"));
            }
            else
            {
                Path firstFilePath;
                String ext = "unknown";
                try {
                    firstFilePath = fileChooser.getSettingsModel().createReadPathAccessor().getPaths(new NodeModelStatusConsumer(EnumSet.of(MessageType.ERROR, MessageType.WARNING))).get(0);
                    mime = MIMETypeHelper.getMIMEtype(firstFilePath.toString());
                    ext = FilenameUtils.getExtension(firstFilePath.toString());
                } catch (IOException | InvalidSettingsException e) {
                }
                this.label.setText("MIME Type: " + mime.orElse("unregistered ('" + ext + "')"));
            }
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            this.update();
        }

        @Override
        public void keyTyped(KeyEvent e) {
            this.update();
        }

        @Override
        public void keyPressed(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {
            this.update();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            this.update();
        }

    }
    
    /*
     * Registers the MimeType Change Listener when the Dialog is created
     */
    private void registerMimeTypeChangeListener(
            final DialogComponentReaderFileChooser fileChooser,
            final DialogComponentOptionalString fileExtension,
            final DialogComponentLabel fileLabel) {

        // Get all the Components for which the Event Listener needs to be added
        final JCheckBox checkbox = (JCheckBox) fileExtension.getComponentPanel().getComponent(0);
        final JTextField opt = (JTextField) fileExtension.getComponentPanel().getComponent(1);
        final JLabel label = (JLabel) fileLabel.getComponentPanel().getComponent(0);

        // Create the MimeTypeListener
        final NewMimeTypeListener mimeTypeListener = new NewMimeTypeListener(checkbox, opt, fileChooser, label);
        //final MimeTypeListener mimeTypeListender = new MimeTypeListener(checkbox, opt, fileBox, label);
        final KeyListener keyListener = (KeyListener) mimeTypeListener;
        
        // Register the MimeType Listener as ChangeListener
        fileChooser.getSettingsModel().addChangeListener(mimeTypeListener);

        // Register the MimeType Listener as ActionListener
        mimeTypeListener.update();
        checkbox.addActionListener(mimeTypeListener);
        opt.addActionListener(mimeTypeListener);

        // Register the MimeType Listener as Key Listener
        opt.addKeyListener(keyListener);
    }

    @Override
    protected Optional<PortsConfigurationBuilder> createPortsConfigBuilder() {
        final PortsConfigurationBuilder builder = new PortsConfigurationBuilder();
        builder.addOptionalInputPortGroup(FS_PORT_ID, FileSystemPortObject.TYPE);
        // TODO decide between URI Port and FileStoreURIPort
        builder.addFixedOutputPortGroup("loaded files", PortTypeRegistry.getInstance().getPortType(IURIPortObject.class));
        return Optional.of(builder);
    }
    
    @Override
    protected MimeFileNioImporterNodeModel createNodeModel(final NodeCreationConfiguration creationConfig) {
        PortsConfiguration portsConfiguration = creationConfig.getPortConfig().orElseThrow(IllegalStateException::new);
        return new MimeFileNioImporterNodeModel(portsConfiguration, createSettings(portsConfiguration));
    }
    
    private static MimeFileNioImporterNodeConfiguration createSettings(final PortsConfiguration portsConfiguration) {
        return new MimeFileNioImporterNodeConfiguration(
                new SettingsModelReaderFileChooser(
                        "file_chooser",
                        portsConfiguration,
                        MimeFileNioImporterNodeFactory.FS_PORT_ID,
                        EnumConfig.create(FilterMode.FILE, FilterMode.FILES_IN_FOLDERS),
                        EnumSet.of(FSCategory.LOCAL, FSCategory.MOUNTPOINT, FSCategory.RELATIVE)));

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrNodeViews() {
        return 0;
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
    public NodeDialogPane createNodeDialogPane(final NodeCreationConfiguration config) {

        final DefaultNodeSettingsPane pane = new DefaultNodeSettingsPane();
        MimeFileNioImporterNodeConfiguration extConfig = createSettings(config.getPortConfig().orElseThrow(IllegalStateException::new));
        
        final FlowVariableModel readFvm = pane.createFlowVariableModel(
                extConfig.getFileChooserSettings().getKeysForFSLocation(), FSLocationVariableType.INSTANCE);
        
        final DialogComponentReaderFileChooser fileChooser1 = 
                new DialogComponentReaderFileChooser(
                        extConfig.getFileChooserSettings(),
                        "list_files_history",
                        readFvm);
        
        
        // The Label for Displaying the MIME Type
        final DialogComponentLabel label = new DialogComponentLabel("MIME Type: ");
        
        final DialogComponentOptionalString fileExtension = 
                new DialogComponentOptionalString(
                    extConfig.overwriteFileExtension(),
                    "File extension (override)");

        // Registration of the ChangeListener of the MIME Type
        this.registerMimeTypeChangeListener(fileChooser1, fileExtension, label);

        // Add all the Dialog components
        pane.addDialogComponent(fileChooser1);
        pane.addDialogComponent(fileExtension);
        pane.addDialogComponent(label);
        return pane;
    }

    @Override
    public NodeView<MimeFileNioImporterNodeModel> createNodeView(int viewIndex,
            MimeFileNioImporterNodeModel nodeModel) {
        // TODO Auto-generated method stub
        return null;
    }
}
