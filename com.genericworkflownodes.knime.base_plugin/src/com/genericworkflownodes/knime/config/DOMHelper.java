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

package com.genericworkflownodes.knime.config;

import java.util.List;

import org.dom4j.Node;

public class DOMHelper {
	@SuppressWarnings("unchecked")
	public static List<Node> selectNodes(Node root, String query)
			throws Exception {
		List<Node> result = root.selectNodes(query);
		return result;
	}

	public static Node selectSingleNode(Node root, String query)
			throws Exception {
		Node result = root.selectSingleNode(query);
		if (result == null) {
			throw new Exception("XPath query yielded null result");
		}
		return result;
	}

	public static String valueOf(Node n, String query) throws Exception {
		String val = n.valueOf(query);
		if (val == null) {
			throw new Exception("XPath query yielded null result");
		}
		return val;
	}
}
