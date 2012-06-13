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

package org.ballproject.knime.base.treetabledialog;

import java.util.ArrayList;
import java.util.List;

/**
 * The Node class represents any nodes within a tree-like structure to be
 * displayed by tree table widgets.
 * 
 * @author roettig
 * 
 * @param <T>
 */
public class Node<T> {
	protected Node<T> parent;
	protected T payload;

	protected List<Node<T>> children = new ArrayList<Node<T>>();

	protected String name;

	public Node() {
		parent = null;
		name = "root";
	}

	public Node(Node<T> p, T payload, String name) {
		parent = p;
		this.payload = payload;
		this.name = name;
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

	public String toString() {
		return name;
	}
}
