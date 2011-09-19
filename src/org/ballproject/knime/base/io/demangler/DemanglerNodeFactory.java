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

package org.ballproject.knime.base.io.demangler;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "MimeFileImporter" Node.
 * 
 *
 * @author roettig
 */
public class DemanglerNodeFactory extends NodeFactory<DemanglerNodeModel> 
{

    /**
     * {@inheritDoc}
     */
    @Override
    public DemanglerNodeModel createNodeModel() 
    {
        return new DemanglerNodeModel();
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
    public NodeView<DemanglerNodeModel> createNodeView(final int viewIndex, final DemanglerNodeModel nodeModel) 
    {
        return new DemanglerNodeView(nodeModel);
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
        return new DemanglerNodeDialog(new Object());
    }

}