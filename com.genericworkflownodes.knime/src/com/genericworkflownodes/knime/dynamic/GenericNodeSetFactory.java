package com.genericworkflownodes.knime.dynamic;

import org.knime.core.node.NodeSetFactory;
import org.osgi.framework.Version;

import com.genericworkflownodes.knime.custom.config.IPluginConfiguration;

public interface GenericNodeSetFactory extends NodeSetFactory {
    /**
     * The version of the node's created by this factory.
     * @return the version
     */
    Version getVersion();
    
    /**
     * The ID identifying the group of nodes provided by this factory.
     * @return an ID that is the same for all factories providing
     *          versions of the same node set.
     */
    String getId();
    
    /**
     * The plugin configuration of this node set factory.
     * @return the configuration
     */
    IPluginConfiguration getPluginConfig();
}
