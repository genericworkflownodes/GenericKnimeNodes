/**
 * Copyright (c) 2011-2013, Marc Röttig, Stephan Aiche.
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
package com.genericworkflownodes.knime.generic_node;

import java.io.InputStream;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.config.reader.CTDConfigurationReader;

/**
 * <code>NodeFactory</code> for the "GenericKnimeNode" Node.
 * 
 * @author roettig, aiche
 */
public abstract class GenericKnimeNodeFactory extends
        NodeFactory<GenericKnimeNodeModel> {

    /**
     * Reads the node configuration from configured input stream.
     * 
     * @return
     * @throws Exception
     */
    protected INodeConfiguration getNodeConfiguration() throws Exception {
        return new CTDConfigurationReader().read(getConfigAsStream());
    }

    /**
     * @param configStream
     */
    public GenericKnimeNodeFactory() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract GenericKnimeNodeModel createNodeModel();

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
    public NodeView<GenericKnimeNodeModel> createNodeView(final int viewIndex,
            final GenericKnimeNodeModel nodeModel) {
        return new GenericKnimeNodeView(nodeModel);
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
    public abstract NodeDialogPane createNodeDialogPane();

    /**
     * Returns a new stream pointing to the node configuration file.
     * 
     * @return
     */
    protected abstract InputStream getConfigAsStream();

}
