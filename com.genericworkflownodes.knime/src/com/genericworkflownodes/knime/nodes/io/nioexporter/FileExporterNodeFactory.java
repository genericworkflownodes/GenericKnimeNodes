package com.genericworkflownodes.knime.nodes.io.nioexporter;

import java.util.Optional;

import org.knime.core.data.uri.IURIPortObject;
import org.knime.core.node.ConfigurableNodeFactory;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeView;
import org.knime.core.node.context.NodeCreationConfiguration;
import org.knime.core.node.context.ports.PortsConfiguration;
import org.knime.core.node.port.PortTypeRegistry;
import org.knime.filehandling.core.port.FileSystemPortObject;

/**
 * The factory of the FileExporter node.
 *
 * @author jpfeuffer
 */
public final class FileExporterNodeFactory extends ConfigurableNodeFactory<FileExporterNodeModel> {

    /** The name of the optional connection input port group. */
    public static final String CONNECTION_INPUT_PORT_GRP_NAME = "File System Connection";

    /** The name of the data table input port group. */
    static final String DATA_TABLE_INPUT_PORT_GRP_NAME = "URI File Port";

    @Override
    protected Optional<PortsConfigurationBuilder> createPortsConfigBuilder() {
        final PortsConfigurationBuilder builder = new PortsConfigurationBuilder();
        builder.addOptionalInputPortGroup(CONNECTION_INPUT_PORT_GRP_NAME, FileSystemPortObject.TYPE);
        builder.addFixedInputPortGroup(DATA_TABLE_INPUT_PORT_GRP_NAME, PortTypeRegistry.getInstance().getPortType(IURIPortObject.class));
        return Optional.of(builder);
    }

    @Override
    protected FileExporterNodeModel createNodeModel(final NodeCreationConfiguration creationConfig) {
        return new FileExporterNodeModel(getPortsConfig(creationConfig), CONNECTION_INPUT_PORT_GRP_NAME);
    }

    @Override
    protected NodeDialogPane createNodeDialogPane(final NodeCreationConfiguration creationConfig) {
        return new FileExporterNodeDialog(getPortsConfig(creationConfig), CONNECTION_INPUT_PORT_GRP_NAME);
    }

    @Override
    protected int getNrNodeViews() {
        return 0;
    }

    @Override
    public NodeView<FileExporterNodeModel> createNodeView(final int viewIndex, final FileExporterNodeModel nodeModel) {
        return null;
    }

    @Override
    protected boolean hasDialog() {
        return true;
    }

    private static final PortsConfiguration getPortsConfig(final NodeCreationConfiguration creationConfig) {
        return creationConfig.getPortConfig().orElseThrow(IllegalStateException::new);
    }
}