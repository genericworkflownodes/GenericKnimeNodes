package com.genericworkflownodes.knime.nodes.io.outputfolder;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "OutputFolder" Node.
 * Writes all the incoming files to the given output folder.
 *
 * @author The GKN Team
 */
public class OutputFolderNodeFactory 
        extends NodeFactory<OutputFolderNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public OutputFolderNodeModel createNodeModel() {
        return new OutputFolderNodeModel();
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
    public NodeView<OutputFolderNodeModel> createNodeView(final int viewIndex,
            final OutputFolderNodeModel nodeModel) {
        return new OutputFolderNodeView(nodeModel);
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
        return new OutputFolderNodeDialog();
    }

}

