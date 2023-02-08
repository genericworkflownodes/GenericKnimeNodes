/**
 * Copyright (c) 2012, Björn Kahlert, Stephan Aiche.
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
package com.genericworkflownodes.knime.custom;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.knime.core.node.NodeLogger;
import org.osgi.framework.BundleContext;

import com.genericworkflownodes.knime.custom.config.IPluginConfiguration;
import com.genericworkflownodes.knime.toolfinderservice.ExternalTool;

/**
 * This class is an abstract bundle activator which holds the code necessary to
 * register a generated plugin.
 * 
 * @author jpfeuffer
 */
public abstract class GenericActivator extends AbstractUIPlugin {

    /**
     * The logger.
     */
    private static final NodeLogger LOGGER = NodeLogger
            .getLogger(GenericActivator.class);

    /**
     * Plugin properties.
     */
    private Properties props = new Properties();

    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
    }

    @Override
    public void stop(final BundleContext context) throws Exception {
        super.stop(context);
    }

    /**
     * This method carries out all tasks needed to initialize a plugin.
     * 
     * <ul>
     * <li>registerNodes contained in the plugin</li>
     * <li>extract contained binaries</li>
     * <li>register extracted binaries in the run time</li>
     * </ul>
     * 
     * @throws IOException
     *             In case of io errors.
     */
    public final void initializePlugin() throws IOException {
        loadPluginProperties();
    }

    /**
     * Loads the plugin.properties file from the plugin.jar.
     * 
     * @throws IOException
     *             In case of IO errors.
     */
    private void loadPluginProperties() throws IOException {
        props.load(this.getClass().getResourceAsStream("plugin.properties"));
    }

    /**
     * Get the plugin specific properties stored in the plugin.properties file.
     * 
     * @return The properties loaded for this plugin.
     */
    public final Properties getProperties() {
        return props;
    }

    /**
     * Returns a {@link List} of {@link ExternalTool}s contained in the plugin.
     * 
     * @return the list of tools in this plugin.
     */
    public abstract List<ExternalTool> getTools();

    /**
     * Gives access to the plugin config of the derived plugin.
     * 
     * @return The plugin config.
     */
    public abstract IPluginConfiguration getPluginConfiguration();

}
