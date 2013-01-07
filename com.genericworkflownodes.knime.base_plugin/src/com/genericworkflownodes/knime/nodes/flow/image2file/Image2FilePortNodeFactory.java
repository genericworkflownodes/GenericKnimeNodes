package com.genericworkflownodes.knime.nodes.flow.image2file;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "Image2FilePort" Node.
 * Converts an Image Port to a File port by saving it as either png or svg.
 *
 * @author GenericKnimeNodes
 */
public class Image2FilePortNodeFactory 
        extends NodeFactory<Image2FilePortNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Image2FilePortNodeModel createNodeModel() {
        return new Image2FilePortNodeModel();
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
    public NodeView<Image2FilePortNodeModel> createNodeView(final int viewIndex,
            final Image2FilePortNodeModel nodeModel) {
        return new Image2FilePortNodeView(nodeModel);
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
        return new Image2FilePortNodeDialog();
    }

}

