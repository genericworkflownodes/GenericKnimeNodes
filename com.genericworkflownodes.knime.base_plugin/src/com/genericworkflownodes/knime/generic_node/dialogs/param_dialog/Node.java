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

package com.genericworkflownodes.knime.generic_node.dialogs.param_dialog;

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

	public Node() {
		parent = null;
		name = "root";
		description = "";
	}

	public Node(Node<T> parent, T payload, String name) {
		this.parent = parent;
		this.payload = payload;
		this.name = name;
	}

	public Node(Node<T> parent, T payload, String name, String description) {
		this.parent = parent;
		this.payload = payload;
		this.name = name;
		this.description = description;
	}

	public void addChild(Node<T> child) {
		children.add(child);
	}

	public Node<T> getChild(int idx) {
		return children.get(idx);
	}

	public int getNumChildren() {
		return children.size();
	}

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

	public boolean isLeaf() {
		return (children.size() == 0);
	}

	public T getPayload() {
		return payload;
	}

	public void setPayload(T payload) {
		this.payload = payload;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	public String getDescription() {
		return description;
	}
}
