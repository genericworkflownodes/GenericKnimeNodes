package org.ballproject.knime.base.node;

import java.io.FileNotFoundException;

import org.ballproject.knime.base.config.NodeConfiguration;
import org.ballproject.knime.base.parameter.InvalidParameterValueException;
import org.ballproject.knime.base.parameter.Parameter;
import org.ballproject.knime.base.treetabledialog.ParameterDialog;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;

public class GenericKnimeNodeDialog extends NodeDialogPane
{
	private NodeConfiguration config;
	private ParameterDialog   dialog;
	
	public GenericKnimeNodeDialog(NodeConfiguration config)
	{
		this.config = config;
		try
		{
			dialog = new ParameterDialog(config);
			this.addTab("Parameters", dialog);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
		
	@Override
	protected void saveSettingsTo(NodeSettingsWO settings) throws InvalidSettingsException
	{
		System.out.println("Dialog"+config.getName()+" saveSettingsTo START");
		for(Parameter<?> param: config.getParameters())
		{
			System.out.println("processing Param "+param.getKey()+" value="+param.toString());
			settings.addString(param.getKey(), param.toString());
		}		
		System.out.println("Dialog"+config.getName()+" saveSettingsTo END");
		
	}

	@Override
	protected void loadSettingsFrom(NodeSettingsRO settings, DataTableSpec[] specs) throws NotConfigurableException
	{
		for(Parameter<?> param: config.getParameters())
		{
			String value = null;
			try
			{
				 value = settings.getString(param.getKey());
			}
			catch (InvalidSettingsException e)
			{
				e.printStackTrace();
			}
			try
			{
				param.fillFromString(value);
			}
			catch (InvalidParameterValueException e)
			{
				e.printStackTrace();
			}
		}
	}
}
