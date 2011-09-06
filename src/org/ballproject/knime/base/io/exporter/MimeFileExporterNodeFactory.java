package org.ballproject.knime.base.io.exporter;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "MimeFileExporter" Node.
 * 
 *
 * @author roettig
 */
public class MimeFileExporterNodeFactory extends NodeFactory<MimeFileExporterNodeModel> 
{

    /**
     * {@inheritDoc}
     */
    @Override
    public MimeFileExporterNodeModel createNodeModel() 
    {
        return new MimeFileExporterNodeModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrNodeViews() 
    {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeView<MimeFileExporterNodeModel> createNodeView(final int viewIndex, final MimeFileExporterNodeModel nodeModel) 
    {
        return new MimeFileExporterNodeView(nodeModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDialog() 
    {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeDialogPane createNodeDialogPane() 
    {
        return new MimeFileExporterNodeDialog();
    }

}
