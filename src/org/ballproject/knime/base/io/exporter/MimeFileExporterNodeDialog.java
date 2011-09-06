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
		System.out.println("MimeFileExporterNodeDialog");
		DialogComponentFileChooser dfc = new DialogComponentFileChooser(MimeFileExporterNodeDialog.createFileChooserModel(), "dunno","*");
		dfc.setBorderTitle("");
		addDialogComponent(dfc);
	}
	
	
	
	@Override
	protected void loadSettingsFrom(NodeSettingsRO settings, DataTableSpec[] specs) throws NotConfigurableException
	{
		System.out.println("Dialog::loadSettingsFrom "+settings.getChildCount());
		for(String key: settings.keySet())
		{
			try
			{
				System.out.println(settings.getString(key));
			} catch (InvalidSettingsException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
	static SettingsModelString createFileChooserModel()
	{
		return new SettingsModelString(MimeFileExporterNodeModel.CFG_FILENAME, "");
	}
}
