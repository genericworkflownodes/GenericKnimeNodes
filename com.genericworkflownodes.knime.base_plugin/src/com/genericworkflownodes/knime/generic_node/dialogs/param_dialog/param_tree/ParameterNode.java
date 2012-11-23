package com.genericworkflownodes.knime.generic_node.dialogs.param_dialog.param_tree;

import com.genericworkflownodes.knime.parameter.Parameter;

/**
 * Typedef of {@link Node} with generic argument {@link Parameter}.
 * 
 * @author roettig, aiche
 */
public class ParameterNode extends Node<Parameter<?>> {

	/**
	 * Forward of {@link Node#Node(Node, Object, String, String)}.
	 * 
	 * @param parent
	 *            The parent node of this node (null if root).
	 * @param payload
	 *            The payload, i.e., the actual parameter.
	 * @param name
	 *            The name of the node.
	 * @param description
	 *            The description of the node.
	 */
	public ParameterNode(Node<Parameter<?>> parent, Parameter<?> payload,
			String name, String description) {
		super(parent, payload, name, description);
	}

}
