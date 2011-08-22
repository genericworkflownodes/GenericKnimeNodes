package org.ballproject.knime;


import java.util.Properties;

import org.eclipse.jface.preference.IPreferenceStore;
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

	public static void log(String message)
	{
		if(GenericNodesPlugin.DEBUG)
			System.out.println(message);
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
		Properties props = new Properties();
		props.load(GenericNodesPlugin.class.getResourceAsStream("baseplugin.properties"));
		DEBUG = (props.getProperty("debug","false").toLowerCase().equals("true") ? true : false);
		log("starting plugin: GenericNodesPlugin");
	}
	
	public static boolean DEBUG = false;

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
