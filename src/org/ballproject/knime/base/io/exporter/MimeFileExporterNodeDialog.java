package org.ballproject.knime.base.io.exporter;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * <code>NodeDialog</code> for the "MimeFileExporter" Node.
 * 
 * 
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author roettig
 */
public class MimeFileExporterNodeDialog extends DefaultNodeSettingsPane
{

	/**
	 * New pane for configuring MimeFileExporter node dialog. This is just a
	 * suggestion to demonstrate possible default dialog components.
	 */
	protected MimeFileExporterNodeDialog()
	{
		super();
		DialogComponentFileChooser dfc = new DialogComponentFileChooser(MimeFileExporterNodeDialog.createFileChooserModel(), "MimeFileExporterNodeDialog");
		dfc.setBorderTitle("");
		addDialogComponent(dfc);
	}
		
	static SettingsModelString createFileChooserModel()
	{
		return new SettingsModelString(MimeFileExporterNodeModel.CFG_FILENAME, "");
	}
}
