/**
 * Copyright (c) 2011-2013, Marc Röttig, Stephan Aiche.
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
package com.genericworkflownodes.knime.nodes.io.outputfile;

import javax.swing.JFileChooser;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * <code>NodeDialog</code> for the "OutputFile" Node.
 * 
 * 
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author roettig
 */
public class OutputFileNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New pane for configuring MimeFileExporter node dialog.
	 */
	protected OutputFileNodeDialog() {
		super();
		DialogComponentFileChooser dfc = new DialogComponentFileChooser(
				OutputFileNodeDialog.createFileChooserModel(),
				"OutputFileNodeDialog", JFileChooser.SAVE_DIALOG);
		dfc.setBorderTitle("");
		addDialogComponent(dfc);
	}

	static SettingsModelString createFileChooserModel() {
		return new SettingsModelString(OutputFileNodeModel.CFG_FILENAME, "");
	}

}