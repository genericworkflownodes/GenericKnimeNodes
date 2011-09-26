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

package org.ballproject.knime.base.flow.beanshell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.ballproject.knime.GenericNodesPlugin;
import org.ballproject.knime.base.mime.MIMEtypeRegistry;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;

import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;


import bsh.Interpreter;



/**
 * This is the model implementation of DemanglerNodeModel.
 * 
 * 
 * @author roettig
 */
public class BeanShellNodeModel extends NodeModel
{

	// the logger instance
	private static final NodeLogger logger = NodeLogger.getLogger(BeanShellNodeModel.class);
	
	
	protected MIMEtypeRegistry resolver = GenericNodesPlugin.getMIMEtypeRegistry();
	
	/**
	 * Constructor for the node model.
	 */
	protected BeanShellNodeModel()
	{
		super(1,1);
	}
	
	
	private static class Row
	{
		private DataCell[] row;
		
		public Row(DataCell[] row)
		{
			this.row = row;
		}
		
		public int getNumCols()
		{
			if(row!=null)
				return row.length;
			return 0;
		}
		
		public Class<?> getColumnClass(int idx)
		{
			if(row!=null && idx>=0 && idx<row.length)
				return row[idx].getClass();
			return null;
		}
		
		public DataCell getCell(int idx)
		{
			if(row!=null && idx>=0 && idx<row.length)
				return row[idx];
			return null;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec) throws Exception
	{
		BufferedDataContainer container1  = null; 
		
		Interpreter ip = new Interpreter();
		ip.eval(script_init); 
		
		Iterator<DataRow> iter = inData[0].iterator();
		
		while(iter.hasNext())
		{
			ip.set("cell", iter.next().getCell(0));
			ip.eval(script_firstPass);
		}
		
		
		iter = inData[0].iterator();
		int idx = 1;
		boolean first = true;
		int C = 0;
		
		while(iter.hasNext())
		{
			ip.set("cell", iter.next().getCell(0));
			ip.eval(script_secondPass);
			Object out = ip.get("OUT");
			if(out==null)
				continue;
			if(first)
			{
				if(out instanceof Object[])
				{
					C = ((Object[]) out).length;
				}
				else
				{
					C = 1;
				}
				
				//container1 = exec.createDataContainer(getDataTableSpec(C));
				container1 = exec.createDataContainer(getDataTableSpec2(out));
				first = false;
			}
			
			
			Object[] values = null; 
			if(out instanceof Object[])
			{
				values = (Object[]) ip.get("OUT");
			}
			else
			{
				values = new String[]{(String) ip.get("OUT")};
			}
			int N = values.length;
			int i = 0;
			DataCell[] cells = new DataCell[N];
			for(Object value: values)
			{
				cells[i++] = getCell(value); //new StringCell(value);
			}
			DefaultRow row = new DefaultRow("Row "+idx++, cells);
			container1.addRowToTable(row);
		}
		
		container1.close();
		
		BufferedDataTable out1 = container1.getTable();

		
		return new BufferedDataTable[]{ out1};
	}
	
	private DataCell getCell(Object in)
	{
		if(in instanceof Integer)
		{
			return new IntCell((Integer) in);	
		}
		else
		if(in instanceof Double)
		{
			return new DoubleCell((Double) in);
		}
		else
		if(in instanceof String)
		{
			return new StringCell((String) in);
		}				
		else
		{
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset()
	{
		// TODO Code executed on reset.
		// Models build during execute are cleared here.
		// Also data handled in load/saveInternals will be erased here.
	}

	
	protected List<DataType> inTypes;
	protected DataType       inType;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException
	{
		return new DataTableSpec[]{null};
	}

	private DataTableSpec outspec;
	
	
	private DataTableSpec getDataTableSpec(int N) throws InvalidSettingsException
	{
		DataColumnSpec[] allColSpecs = new DataColumnSpec[N];
		
		for(int i=0;i<N;i++)
		{
			allColSpecs[i]   =  new DataColumnSpecCreator("column "+i,  StringCell.TYPE).createSpec();	
		}
		
        DataTableSpec outputSpec = new DataTableSpec(allColSpecs);

        // save this internally
       	outspec = outputSpec;
        
        return outputSpec;
	}
	
	private DataTableSpec getDataTableSpec2(Object out) throws InvalidSettingsException
	{
		
		int C = 1;
		Object[] data;
		if(out instanceof Object[])
		{
			C    = ((Object[]) out).length;
			data = ((Object[]) out);
		}
		else
		{
			data = new Object[]{out};
		}
		
		
		
		DataColumnSpec[] allColSpecs = new DataColumnSpec[C];
		
		for(int i=0;i<C;i++)
		{
			if(data[i] instanceof Integer)
			{
				allColSpecs[i]   =  new DataColumnSpecCreator("column "+i,  IntCell.TYPE).createSpec();	
			}
			else
			if(data[i] instanceof Double)
			{
				allColSpecs[i]   =  new DataColumnSpecCreator("column "+i,  DoubleCell.TYPE).createSpec();
			}
			else
			if(data[i] instanceof Double)
			{
				allColSpecs[i]   =  new DataColumnSpecCreator("column "+i,  StringCell.TYPE).createSpec();
			}				
			else
			{
				throw new InvalidSettingsException("invalid type was supplied at column # "+i);
			}
		}
		
        DataTableSpec outputSpec = new DataTableSpec(allColSpecs);

        // save this internally
       	outspec = outputSpec;
        
        return outputSpec;
        
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings)
	{
		settings.addString("script_init", script_init);
		settings.addString("script_firstPass", script_firstPass);
		settings.addString("script_secondPass", script_secondPass);
	}

	private String script_init;
	private String script_firstPass;
	private String script_secondPass;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException
	{
		script_init       = settings.containsKey("script_init") ? settings.getString("script_init") : "";
		script_firstPass  = settings.containsKey("script_firstPass") ? settings.getString("script_firstPass") : "";
		script_secondPass = settings.containsKey("script_secondPass") ? settings.getString("script_secondPass") : "";
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException
	{
		//String script_ = settings.getString("script");
		/*
	    try
		{
			engine.eval(script_);
		} 
        catch (ScriptException e1)
		{
			throw new InvalidSettingsException(e1);
		}
		*/
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadInternals(final File internDir, final ExecutionMonitor exec) throws IOException, CanceledExecutionException
	{
		File f = new File(internDir, "scripts");
		// load the settings from the file 
		NodeSettingsRO settings = NodeSettings.loadFromXML(new FileInputStream(f));
		// retrieve the stored values
		try
		{
			this.script_init = settings.getString("script_init");
			this.script_firstPass = settings.getString("script_firstPass");
			this.script_secondPass = settings.getString("script_secondPass");
		} 
		catch (InvalidSettingsException e)
		{
			e.printStackTrace();
		}
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveInternals(final File internDir, final ExecutionMonitor exec) throws IOException, CanceledExecutionException
	{
		// create a settings object with a config name
		NodeSettings settings = new NodeSettings("source");
		// store your values under a certain key
		settings.addString("script_init", this.script_init);
		settings.addString("script_firstPass", this.script_firstPass);
		settings.addString("script_secondPass", this.script_secondPass);
		// create a file in the given directory
		File f = new File(internDir, "scripts");
		// and save it
		settings.saveToXML(new FileOutputStream(f));
	}

}
