package com.genericworkflownodes.knime.nodes.flow.image2file;

import org.knime.core.node.NodeView;

/**
 * <code>NodeView</code> for the "Image2FilePort" Node.
 * Converts an Image Port to a File port by saving it as either png or svg.
 *
 * @author GenericKnimeNodes
 */
public class Image2FilePortNodeView extends NodeView<Image2FilePortNodeModel> {

    /**
     * Creates a new view.
     * 
     * @param nodeModel The model (class: {@link Image2FilePortNodeModel})
     */
    protected Image2FilePortNodeView(final Image2FilePortNodeModel nodeModel) {
        super(nodeModel);

        // TODO instantiate the components of the view here.

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void modelChanged() {

        // TODO retrieve the new model from your nodemodel and 
        // update the view.
        Image2FilePortNodeModel nodeModel = 
            (Image2FilePortNodeModel)getNodeModel();
        assert nodeModel != null;
        
        // be aware of a possibly not executed nodeModel! The data you retrieve
        // from your nodemodel could be null, emtpy, or invalid in any kind.
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onClose() {
    
        // TODO things to do when closing the view
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onOpen() {

        // TODO things to do when opening the view
    }

}

