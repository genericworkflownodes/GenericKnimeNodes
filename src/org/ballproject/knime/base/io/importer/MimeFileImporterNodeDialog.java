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

package org.ballproject.knime.base.io.importer;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * <code>NodeDialog</code> for the "MimeFileImporter" Node.
 * 
 * 
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author roettig
 */
public class MimeFileImporterNodeDialog extends DefaultNodeSettingsPane
{

	/**
	 * New pane for configuring MimeFileImporter node dialog. This is just a
	 * suggestion to demonstrate possible default dialog components.
	 */
	protected MimeFileImporterNodeDialog(Object obj)
	{
		super();
		addDialogComponent(new DialogComponentFileChooser(MimeFileImporterNodeDialog.createFileChooserModel(),"MimeFileImporterNodeDialog"));
	}
	
	static SettingsModelString createFileChooserModel()
	{
		return new SettingsModelString(MimeFileImporterNodeModel.CFG_FILENAME, "");
	}
}