package com.genericworkflownodes.knime.dynamic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSetFactory;
import org.knime.core.node.config.ConfigRO;
import org.osgi.framework.Version;

public class VersionedNodeSetFactoryManager implements NodeSetFactory {

    List<VersionedNodeSetFactory> m_factories = null;
    Map<String, VersionedNodeSetFactory> m_idToFac = new HashMap<>();
    
    /**
     * The id of the used extension point.
     */
    private static final String EXTENSION_POINT_ID = "com.genericworkflownodes.knime.dynamic.VersionedNodeSetFactory";

    /**
     * The central static logger.
     */
    private static final NodeLogger LOGGER = NodeLogger
            .getLogger(VersionedNodeSetFactoryManager.class);
    
    /*
     * Searchs through the eclipse extension point registry for registered
     * {@link VersionedNodeSetFactory}s.
     */
    public List<VersionedNodeSetFactory> getAvailableVersionedNodeSetFactories() {
        if (m_factories == null) {
            m_factories = new ArrayList<VersionedNodeSetFactory>();
            Map<String, PriorityQueue<VersionedNodeSetFactory>> pluginIDQueues = new HashMap<>();
            IExtensionRegistry reg = Platform.getExtensionRegistry();
            IConfigurationElement[] elements = reg
                    .getConfigurationElementsFor(EXTENSION_POINT_ID);
            try {
                for (IConfigurationElement elem : elements) {
                    final DynamicGenericNodeSetFactory o = (DynamicGenericNodeSetFactory)elem.createExecutableExtension("class");
                    // cast is guaranteed to work based on the extension point
                    // definition
                    VersionedNodeSetFactory versionedFac = new VersionedNodeSetFactory(o);
                    m_factories.add(versionedFac);
                    String pluginID = o.getPluginConfig().getPluginId();
                    if (!pluginIDQueues.containsKey(pluginID)) {
                        pluginIDQueues.put(pluginID, new PriorityQueue<VersionedNodeSetFactory>(10,new Comparator<VersionedNodeSetFactory>(){

                            @Override
                            public int compare(VersionedNodeSetFactory o1,
                                    VersionedNodeSetFactory o2) {
                                return o1.getVersion().compareTo(o2.getVersion());
                            }
                            
                        }));
                    }
                    pluginIDQueues.get(pluginID).add(versionedFac);
                    for (String nodeID : versionedFac.getNodeFactoryIds()) {
                        if (m_idToFac.containsKey(nodeID)) {
                            LOGGER.warn("Node with ID " + nodeID + " already registered");
                        }
                        m_idToFac.put(nodeID, versionedFac);
                    }
                }
                
                for (PriorityQueue<VersionedNodeSetFactory> q : pluginIDQueues.values()) {
                    q.poll(); //Newest version stays undeprecated
                    for (VersionedNodeSetFactory f : q) {
                        f.setIsDeprecated(true);
                    }
                }
            } catch (CoreException e) {
                LOGGER.warn(e.getMessage());
            }
        }
        return m_factories;
    }
    
    @Override
    public Collection<String> getNodeFactoryIds() {
        ArrayList<String> totalFactories = new ArrayList<>();
        for (VersionedNodeSetFactory fac : getAvailableVersionedNodeSetFactories()) {
            totalFactories.addAll(fac.getNodeFactoryIds());
        }
        return totalFactories;
    }

    @Override
    public Class<? extends NodeFactory<? extends NodeModel>> getNodeFactory(
            String id) {
        VersionedNodeSetFactory fac = getFactory(id);
        return fac.getNodeFactory(id);
    }

    @Override
    public String getCategoryPath(String id) {
        VersionedNodeSetFactory fac = getFactory(id);
        return fac.getCategoryPath(id);
    }

    @Override
    public String getAfterID(String id) {
        VersionedNodeSetFactory fac = getFactory(id);
        return fac.getAfterID(id);
    }

    @Override
    public ConfigRO getAdditionalSettings(String id) {
        VersionedNodeSetFactory fac = getFactory(id);
        return fac.getAdditionalSettings(id);
    }

    private VersionedNodeSetFactory getFactory(String id) {
        VersionedNodeSetFactory fac = m_idToFac.get(id);
        if (fac == null) {
            throw new IllegalArgumentException("Node with ID " + id + " is not registered.");
        }
        return fac;
    }

}
