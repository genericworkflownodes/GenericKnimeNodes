package org.ballproject.knime.base.node;

import java.io.FileNotFoundException;

import org.ballproject.knime.base.config.NodeConfiguration;
import org.ballproject.knime.base.parameter.InvalidParameterValueException;
import org.ballproject.knime.base.parameter.Parameter;
import org.ballproject.knime.base.treetabledialog.MimeTypeChooserDialog;
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
	private MimeTypeChooserDialog mtc;
	
	public GenericKnimeNodeDialog(NodeConfiguration config)
	{
		this.config = config;
		try
		{
			dialog = new ParameterDialog(config);
			this.addTab("Parameters", dialog);
			mtc = new MimeTypeChooserDialog(config);
			this.addTab("OutputTypes", mtc);
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
		for(Parameter<?> param: config.getParameters())
		{
			settings.addString(param.getKey(), param.toString());
		}		
		
		int[] sel_ports = mtc.getSelectedTypes();
		
		for(int i=0;i<this.config.getNumberOfOutputPorts();i++)
		{
			settings.addInt("GENERIC_KNIME_NODES_outtype#"+i, sel_ports[i]);
		}
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
				throw new NotConfigurableException(e.getMessage());
			}
		}
		
		int nP = this.config.getNumberOfOutputPorts();
		int[] sel_ports = new int[nP];
		
		for(int i=0;i<nP;i++)
		{
			try
			{
				int idx = settings.getInt("GENERIC_KNIME_NODES_outtype#"+i);
				sel_ports[i] = idx;
			} 
			catch (InvalidSettingsException e)
			{
				throw new NotConfigurableException(e.getMessage());
			}
		}
		mtc.setSelectedTypes(sel_ports);
	}
}
