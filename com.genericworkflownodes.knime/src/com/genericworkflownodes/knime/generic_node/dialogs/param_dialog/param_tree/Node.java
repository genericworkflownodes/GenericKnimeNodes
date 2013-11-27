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

package com.genericworkflownodes.knime.generic_node.dialogs.param_dialog.param_tree;

import java.util.ArrayList;
import java.util.List;

/**
 * The Node class represents any nodes within a tree-like structure to be
 * displayed by tree table widgets.
 * 
 * @author roettig
 * 
 * @param <T>
 *            The data represented by this node.
 */
public class Node<T> {
    /**
     * Pointer to parent node.
     */
    protected Node<T> parent;

    /**
     * The actual data collected at this node.
     */
    protected T payload;

    /**
     * A list of child nodes.
     */
    protected List<Node<T>> children = new ArrayList<Node<T>>();

    /**
     * The name of the node.
     */
    protected String name;

    /**
     * Description of this node.
     */
    protected String description;

    /**
     * C'tor for an empty node.
     */
    public Node() {
        parent = null;
        name = "root";
        description = "";
    }

    /**
     * Node with a pointer to the parent, a value (payload) and a name.
     * 
     * @param parent
     *            The parent of this node.
     * @param payload
     *            The actual value located at this node.
     * @param name
     *            The name of the node.
     */
    public Node(Node<T> parent, T payload, String name) {
        this.parent = parent;
        this.payload = payload;
        this.name = name;
    }

    /**
     * Node with a pointer to the parent, a value (payload), a name, and a
     * description.
     * 
     * @param parent
     *            The parent of this node.
     * @param payload
     *            The actual value located at this node.
     * @param name
     *            The name of the node.
     * @param description
     *            The description of the node.
     */
    public Node(Node<T> parent, T payload, String name, String description) {
        this.parent = parent;
        this.payload = payload;
        this.name = name;
        this.description = description;
    }

    /**
     * Adds a child to this node.
     * 
     * @param child
     *            The new child.
     */
    public void addChild(Node<T> child) {
        children.add(child);
    }

    /**
     * Returns the ith child of this node.
     * 
     * @param i
     *            The number of the requested child.
     * @return The ith child or null if no ith child exists.
     */
    public Node<T> getChild(int i) {
        return children.get(i);
    }

    public int getNumChildren() {
        return children.size();
    }

    /**
     * Given a child returns the number of this child.
     * 
     * @param child
     *            The child.
     * @return The number of the child or -1 if the given {@link Node} is not a
     *         child of this node.
     */
    public int getChildIndex(Node<T> child) {
        int idx = 0;
        for (Node<T> c : children) {
            if (child.equals(c)) {
                return idx;
            }
            idx++;
        }
        return -1;
    }

    /**
     * True if this node has no children.
     * 
     * @return True if it is a leaf, false otherwise.
     */
    public boolean isLeaf() {
        return (children.size() == 0);
    }

    /**
     * Returns the value stored at this node.
     * 
     * @return The value stored at the node.
     */
    public T getPayload() {
        return payload;
    }

    /**
     * Overwrite the value at the node.
     * 
     * @param payload
     *            The new value stored at the node.
     */
    public void setPayload(T payload) {
        this.payload = payload;
    }

    /**
     * Returns the name of the node.
     * 
     * @return The name of the node.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the description of the node.
     * 
     * @return The description of the node.
     */
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Removes all children of this node.
     */
    public void clear() {
        children.clear();
    }
}
