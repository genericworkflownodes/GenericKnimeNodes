/*
 * Copyright (c) 2011, Marc Röttig.
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

package org.ballproject.knime.base.node;

import org.ballproject.knime.base.config.INodeConfiguration;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

import com.genericworkflownodes.knime.config.IPluginConfiguration;

/**
 * <code>NodeFactory</code> for the "GenericKnimeNode" Node.
 * 
 * 
 * @author
 */
public abstract class GenericKnimeNodeFactory extends
		NodeFactory<GenericKnimeNodeModel> {

	protected INodeConfiguration config;
	protected IPluginConfiguration pluginConfig;

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

}
