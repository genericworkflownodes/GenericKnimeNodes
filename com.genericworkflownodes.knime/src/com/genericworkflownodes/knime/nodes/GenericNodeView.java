package com.genericworkflownodes.knime.nodes;

import java.awt.Component;
import java.util.function.Function;

import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeView;

/**
 * Implementation of {@link NodeView} which provided empty bodies for the
 * methods that need to be overridden.
 *
 * @author Lukas Zimmermann
 *
 * @param <T>
 *
 */
public final class GenericNodeView<T extends NodeModel> extends NodeView<T> {

    public GenericNodeView(final T nodeModel, final Function<T, Component> init) {
        super(nodeModel);
        this.setComponent(init.apply(nodeModel));
    }

    @Override
    protected void onClose() {}

    @Override
    protected void onOpen() {}

    @Override
    protected void modelChanged() {}
}
