package com.genericworkflownodes.knime.nodes.io.index;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "IndexLoader" Node.
 * 
 *
 * @author Kerstin Neubert, FU Berlin
 */
public class IndexLoaderNodeFactory 
        extends NodeFactory<IndexLoaderNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public IndexLoaderNodeModel createNodeModel() {
        return new IndexLoaderNodeModel();
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
    public NodeView<IndexLoaderNodeModel> createNodeView(final int viewIndex,
            final IndexLoaderNodeModel nodeModel) {
        return null;
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
        return new IndexLoaderNodeDialog(new Object());
    }

}

