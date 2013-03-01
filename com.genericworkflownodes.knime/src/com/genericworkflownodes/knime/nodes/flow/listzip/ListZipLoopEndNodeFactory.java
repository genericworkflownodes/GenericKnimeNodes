/**
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
package com.genericworkflownodes.knime.nodes.flow.listzip;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * Node Factory for the ListZipEnd node.
 * 
 * @author roettig
 */
public class ListZipLoopEndNodeFactory extends
		NodeFactory<ListZipLoopEndNodeModel> {

	@Override
	protected NodeDialogPane createNodeDialogPane() {
		return null;
	}

	@Override
	public ListZipLoopEndNodeModel createNodeModel() {
		return new ListZipLoopEndNodeModel();
	}

	@Override
	public NodeView<ListZipLoopEndNodeModel> createNodeView(int arg0,
			ListZipLoopEndNodeModel arg1) {
		return null;
	}

	@Override
	protected int getNrNodeViews() {
		return 0;
	}

	@Override
	protected boolean hasDialog() {
		return false;
	}

}
