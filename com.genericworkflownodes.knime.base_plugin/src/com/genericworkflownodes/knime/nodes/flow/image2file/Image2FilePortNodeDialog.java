package com.genericworkflownodes.knime.nodes.flow.image2file;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;

/**
 * <code>NodeDialog</code> for the "Image2FilePort" Node. Converts an Image Port
 * to a File port by saving it as either png or svg.
 * 
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author GenericKnimeNodes
 */
public class Image2FilePortNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New pane for configuring Image2FilePort node dialog. This is just a
	 * suggestion to demonstrate possible default dialog components.
	 */
	protected Image2FilePortNodeDialog() {
		super();
	}
}
