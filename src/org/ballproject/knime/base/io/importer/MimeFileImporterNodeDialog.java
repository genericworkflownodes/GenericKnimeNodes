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