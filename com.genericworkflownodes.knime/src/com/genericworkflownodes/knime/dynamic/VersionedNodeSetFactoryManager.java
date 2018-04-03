package com.genericworkflownodes.knime.dynamic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

public class VersionedNodeSetFactoryManager implements NodeSetFactory {

    List<GenericNodeSetFactory> m_factories = null;
    Map<String, GenericNodeSetFactory> m_idToFac = new HashMap<>();
    
    /**
     * The id of the used extension point.
     */
    private static final String EXTENSION_POINT_ID =
            "com.genericworkflownodes.knime.dynamic.VersionedNodeSetFactory";

    /**
     * The central static logger.
     */
    private static final NodeLogger LOGGER = NodeLogger
            .getLogger(VersionedNodeSetFactoryManager.class);
    
    /*
     * Searchs through the eclipse extension point registry for registered
     * {@link VersionedNodeSetFactory}s.
     */
    public synchronized List<GenericNodeSetFactory> getAvailableVersionedNodeSetFactories() {
        if (m_factories == null) {
            m_factories = new ArrayList<GenericNodeSetFactory>();
            Map<String, PriorityQueue<GenericNodeSetFactory>> pluginIDQueues = new HashMap<>();
            IExtensionRegistry reg = Platform.getExtensionRegistry();
            IConfigurationElement[] elements = reg
                    .getConfigurationElementsFor(EXTENSION_POINT_ID);
            LOGGER.debug("Loading GenericNodeSetFactories");
            try {
                for (IConfigurationElement elem : elements) {
                    final GenericNodeSetFactory o = (GenericNodeSetFactory)elem.createExecutableExtension("class");
                    // cast is guaranteed to work based on the extension point
                    // definition
                    m_factories.add(o);
                    String pluginID = o.getPluginConfig().getPluginId();
                    LOGGER.debug("Checking plugin " + pluginID);
                    if (!pluginIDQueues.containsKey(pluginID)) {
                        pluginIDQueues.put(pluginID, new PriorityQueue<GenericNodeSetFactory>(10, new Comparator<GenericNodeSetFactory>(){
                            @Override
                            public int compare(GenericNodeSetFactory o1,
                                    GenericNodeSetFactory o2) {
                                return o2.getVersion().compareTo(o1.getVersion());
                            }
                        }));
                    }
                    pluginIDQueues.get(pluginID).add(o);
                    for (String nodeID : o.getNodeFactoryIds()) {
                        if (m_idToFac.containsKey(nodeID)) {
                            LOGGER.warn("Node with ID " + nodeID + " already registered");
                        }
                        m_idToFac.put(nodeID, o);
                    }
                }
                
                for (PriorityQueue<GenericNodeSetFactory> q : pluginIDQueues.values()) {
                    //Newest version stays undeprecated
                    GenericNodeSetFactory latest = q.poll();
                    m_nondeprecatedFactories.add(latest.getId());
                }
                synchronized (m_nondeprecatedFactories) {
                    m_loaded = true;
                    m_nondeprecatedFactories.notifyAll();
                }
            } catch (CoreException e) {
                LOGGER.warn(e);
            }
        }
        return m_factories;
    }
    
    private static boolean m_loaded = false;
    private static Set<String> m_nondeprecatedFactories = new HashSet<>();
    
    public static boolean isFactoryDeprecated(String id) throws InterruptedException {
        // We need to wait here until all nodes are loaded to determine which ones are deprecated
        synchronized (m_nondeprecatedFactories) {
            while (!m_loaded) {
                m_nondeprecatedFactories.wait();
            }
        }
        return !m_nondeprecatedFactories.contains(id);
    }
    
    @Override
    public Collection<String> getNodeFactoryIds() {
        ArrayList<String> totalFactories = new ArrayList<>();
        for (GenericNodeSetFactory fac : getAvailableVersionedNodeSetFactories()) {
            totalFactories.addAll(fac.getNodeFactoryIds());
        }
        return totalFactories;
    }

    @Override
    public Class<? extends NodeFactory<? extends NodeModel>> getNodeFactory(
            String id) {
        GenericNodeSetFactory fac = getFactory(id);
        return fac.getNodeFactory(id);
    }

    @Override
    public String getCategoryPath(String id) {
        GenericNodeSetFactory fac = getFactory(id);
        return fac.getCategoryPath(id);
    }

    @Override
    public String getAfterID(String id) {
        GenericNodeSetFactory fac = getFactory(id);
        return fac.getAfterID(id);
    }

    @Override
    public ConfigRO getAdditionalSettings(String id) {
        GenericNodeSetFactory fac = getFactory(id);
        return fac.getAdditionalSettings(id);
    }

    private GenericNodeSetFactory getFactory(String id) {
        GenericNodeSetFactory fac = m_idToFac.get(id);
        if (fac == null) {
            throw new IllegalArgumentException("Node with ID " + id + " is not registered.");
        }
        return fac;
    }

}
