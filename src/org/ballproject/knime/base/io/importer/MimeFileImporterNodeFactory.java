package org.ballproject.knime.base.io.importer;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "MimeFileImporter" Node.
 * 
 *
 * @author roettig
 */
public class MimeFileImporterNodeFactory extends NodeFactory<MimeFileImporterNodeModel> 
{

    /**
     * {@inheritDoc}
     */
    @Override
    public MimeFileImporterNodeModel createNodeModel() 
    {
        return new MimeFileImporterNodeModel();
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
    public NodeView<MimeFileImporterNodeModel> createNodeView(final int viewIndex, final MimeFileImporterNodeModel nodeModel) 
    {
        return new MimeFileImporterNodeView(nodeModel);
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
        return new MimeFileImporterNodeDialog(new Object());
    }

}