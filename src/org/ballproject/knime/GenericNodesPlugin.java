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

package org.ballproject.knime;

import java.util.Properties;
import org.ballproject.knime.base.mime.DefaultMIMEtypeRegistry;
import org.ballproject.knime.base.mime.MIMEtypeRegistry;
import org.ballproject.knime.base.mime.demangler.Demangler;
import org.ballproject.knime.base.mime.demangler.DemanglerProvider;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * This is the eclipse bundle activator. Note: KNIME node developers probably
 * won't have to do anything in here, as this class is only needed by the
 * eclipse platform/plugin mechanism. If you want to move/rename this file, make
 * sure to change the plugin.xml file in the project root directory accordingly.
 * 
 * @author roettig
 */
public class GenericNodesPlugin extends AbstractUIPlugin
{
	// The shared instance.
	private static GenericNodesPlugin plugin;

	public  static boolean DEBUG = false;
	private static DefaultMIMEtypeRegistry registry = new DefaultMIMEtypeRegistry();
	
	public static void log(String message)
	{
		if(GenericNodesPlugin.DEBUG)
			System.out.println(message);
	}
	
	public static boolean isDebug()
	{
		return GenericNodesPlugin.DEBUG;
	}
	
	 
	
	public static MIMEtypeRegistry getMIMEtypeRegistry()
	{
		return registry;
	}
	
	/**
	 * The constructor.
	 */
	public GenericNodesPlugin()
	{
		super();
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation.
	 * 
	 * @param context
	 *            The OSGI bundle context
	 * @throws Exception
	 *             If this plugin could not be started
	 */
	@Override
	public void start(final BundleContext context) throws Exception
	{
		super.start(context);
		System.setProperty("java.protocol.handler.pkgs", "org.ballproject.knime.base.protocols");
		Properties props = new Properties();
		props.load(GenericNodesPlugin.class.getResourceAsStream("baseplugin.properties"));
		DEBUG = (props.getProperty("debug","false").toLowerCase().equals("true") ? true : false);
		log("starting plugin: GenericNodesPlugin");
        
		IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor("org.ballproject.knime.base.mime.demangler.DemanglerProvider");
		try 
		{
			for (IConfigurationElement e : config) 
			{
				final Object o = e.createExecutableExtension("class");
				if (o instanceof DemanglerProvider) 
				{
					DemanglerProvider dp = (DemanglerProvider) o;
					for(Demangler dm : dp.getDemanglers())
					{
						log("registering Demangler for data type "+dm.getSourceType().toString());
						registry.addDemangler(dm);
					}
				}
			}
		} 
		catch (CoreException e) 
		{
			e.printStackTrace();
			throw new Exception(e);
		}
		
	}

	/**
	 * This method is called when the plug-in is stopped.
	 * 
	 * @param context
	 *            The OSGI bundle context
	 * @throws Exception
	 *             If this plugin could not be stopped
	 */
	@Override
	public void stop(final BundleContext context) throws Exception
	{
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return Singleton instance of the Plugin
	 */
	public static GenericNodesPlugin getDefault()
	{
		return plugin;
	}

}
