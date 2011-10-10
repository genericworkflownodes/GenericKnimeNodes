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

import java.io.File;
import java.io.IOException;
import org.ballproject.knime.GenericNodesPlugin;
import org.ballproject.knime.base.mime.MIMEtypeRegistry;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.url.MIMEType;
import org.knime.core.data.url.URLContent;
import org.knime.core.data.url.port.MIMEURLPortObject;
import org.knime.core.data.url.port.MIMEURLPortObjectSpec;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;

/**
 * This is the model implementation of MimeFileImporter.
 * 
 * 
 * @author roettig
 */
public class MimeFileImporterNodeModel extends NodeModel
{

	// the logger instance
	private static final NodeLogger logger = NodeLogger.getLogger(MimeFileImporterNodeModel.class);
	
	static final String CFG_FILENAME = "FILENAME";

	private SettingsModelString  m_filename = MimeFileImporterNodeDialog.createFileChooserModel();
	

	/**
	 * Constructor for the node model.
	 */
	protected MimeFileImporterNodeModel()
	{
		super(new PortType[]{}, new PortType[]{new PortType(MIMEURLPortObject.class)});
	}
		
	protected MIMEtypeRegistry resolver = GenericNodesPlugin.getMIMEtypeRegistry();

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings)
	{
		m_filename.saveSettingsTo(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException
	{
		// TODO load (valid) settings from the config object.
		// It can be safely assumed that the settings are valided by the
		// method below.
		m_filename.loadSettingsFrom(settings);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException
	{

		// TODO check if the settings could be applied to our model
		// e.g. if the count is in a certain range (which is ensured by the
		// SettingsModel).
		// Do not actually set any values of any member variables.

		m_filename.validateSettings(settings);
		String filename = settings.getString(CFG_FILENAME);
		if(resolver.getMIMEtype(filename)==null)
		{
			throw new InvalidSettingsException("file of unknown MIMEtype selected "+filename);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadInternals(final File internDir, final ExecutionMonitor exec) throws IOException, CanceledExecutionException
	{
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveInternals(final File internDir, final ExecutionMonitor exec) throws IOException, CanceledExecutionException
	{
	}

	protected MIMEType mt = null;

	@Override
	protected PortObjectSpec[] configure(PortObjectSpec[] inSpecs)
			throws InvalidSettingsException
	{
		try
		{
			 mt = resolver.getMIMEtype(this.m_filename.getStringValue());
		} 
		catch (Exception e)
		{
			throw new InvalidSettingsException("could not resolve MIME type of file");
		}
		
		if(mt==null)
			return new DataTableSpec[]{null};		
		
		return new PortObjectSpec[]{ new MIMEURLPortObjectSpec(mt) };
	}


	@Override
	protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec)
			throws Exception
	{
		List<URL> urls = new ArrayList<URL>();
		urls.add(new File(m_filename.getStringValue()).toURI().toURL());
		URLContent content = new URLContent(urls); 
		return new PortObject[]{new MIMEURLPortObject(content,mt)};
	}

	
}
