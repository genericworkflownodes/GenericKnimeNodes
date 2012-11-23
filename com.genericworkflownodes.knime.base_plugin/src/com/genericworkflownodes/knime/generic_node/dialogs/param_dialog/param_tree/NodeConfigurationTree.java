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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.parameter.IFileParameter;
import com.genericworkflownodes.knime.parameter.Parameter;

/**
 * Wraps the Parameters of an {@link INodeConfiguration} into a tree of
 * parameters.
 * 
 * @author aiche
 */
public class NodeConfigurationTree {

	/**
	 * The wrapped configuration.
	 */
	private INodeConfiguration config;

	/**
	 * The root node of the parameter tree.
	 */
	private ParameterNode root;

	/**
	 * Flag to indicate if advanced parameters should be shown or not.
	 */
	private boolean showAdvanced;

	/**
	 * C'tor.
	 * 
	 * @param config
	 *            The {@link INodeConfiguration} to wrap.
	 */
	public NodeConfigurationTree(INodeConfiguration config, boolean showAdvanced) {
		this.config = config;
		this.showAdvanced = showAdvanced;
		// initialize the root node
		root = new ParameterNode(null, null, "root", "");
		// create param tree below the root node
		update();
	}

	/**
	 * Sets whether or not the advanced parameter should be shown or not.
	 * 
	 * @param showAdvanced
	 *            Flag indicating if the advanced parameter should be shown or
	 *            not.
	 */
	public void setShowAdvanced(boolean showAdvanced) {
		this.showAdvanced = showAdvanced;
	}

	/**
	 * Returns the root of the Parameter tree.
	 * 
	 * @return The root.
	 */
	public ParameterNode getRoot() {
		return root;
	}

	/**
	 * Constructs a list of prefixes from a given strin.
	 * 
	 * @param key
	 *            The string to process.
	 * @return
	 */
	private static List<String> getPrefixes(String key) {
		List<String> ret = new ArrayList<String>();
		String[] toks = key.split("\\.");
		String pref = "";
		int currentToken = 0;
		for (String tok : toks) {
			// skip the "1" node in OpenMS/CADDSuite
			if (currentToken == 1 && "1".equals(tok))
				continue;
			pref += tok;
			ret.add(pref);
			pref += ".";
			++currentToken;
		}
		return ret;
	}

	/**
	 * Given a "." separated string the method will returns the last part
	 * separated by ".". E.g., foo.bar => bar.
	 * 
	 * @param s
	 *            The string.
	 * @return The last part separated by a ".".
	 */
	public static String getSuffix(String s) {
		String[] toks = s.split("\\.");
		return toks[toks.length - 1];
	}

	/**
	 * Given a parameter key the method extracts the section of the key.
	 * 
	 * @param key
	 *            The parameter key from which the section should be extracted.
	 * @return The section.
	 */
	private String getSection(String key) {
		return key.substring(0, key.lastIndexOf('.'));
	}

	/**
	 * Creates the parameter tree starting from the root.
	 */
	public void update() {
		Map<String, ParameterNode> key2node = new HashMap<String, ParameterNode>();

		// reset the root node for update
		root.clear();

		for (String key : config.getParameterKeys()) {
			Parameter<?> p = config.getParameter(key);

			if (p.isAdvanced() && !showAdvanced)
				continue;

			// we do not show file parameters in the gui
			if (p instanceof IFileParameter)
				continue;

			List<String> prefixes = getPrefixes(key);

			// OpenMS/CADDSuite workaround for leading/second '1' NODE
			if (prefixes.size() > 0 && prefixes.get(0).equals("1")) {
				prefixes.remove(0);
			}

			ParameterNode last = root;

			for (int i = 0; i < prefixes.size() - 1; i++) {
				String prefix = prefixes.get(i);

				if (!key2node.containsKey(prefix)) {
					ParameterNode nn = new ParameterNode(last, null,
							getSuffix(prefix),
							config.getSectionDescription(getSection(key)));
					last.addChild(nn);
					last = nn;
					key2node.put(prefix, last);
				} else {
					last = key2node.get(prefix);
				}
			}

			ParameterNode n = new ParameterNode(last, p, p.getKey(),
					p.getDescription());
			last.addChild(n);
		}
	}
}
