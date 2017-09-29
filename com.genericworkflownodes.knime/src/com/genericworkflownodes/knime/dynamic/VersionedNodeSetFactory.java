package com.genericworkflownodes.knime.dynamic;

import java.util.ArrayList;
import java.util.Collection;

import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSetFactory;
import org.knime.core.node.config.ConfigRO;
import org.osgi.framework.Version;

public class VersionedNodeSetFactory implements NodeSetFactory {
    
    private final DynamicGenericNodeSetFactory m_nodeSetFactory;
    private final String m_versionSuffix;
    private final Version m_version;

    public Version getVersion() {
        return m_version;
    }

    public NodeSetFactory getNodeSetFactory() {
        return m_nodeSetFactory;
    }
    
    public VersionedNodeSetFactory(DynamicGenericNodeSetFactory factory) {
        m_version = new Version(factory.getPluginConfig().getPluginVersion());
        m_nodeSetFactory = factory;
        m_versionSuffix = "_" + factory.getPluginConfig().getPluginVersion().replaceAll("\\.", "_");
    }

    @Override
    public Collection<String> getNodeFactoryIds() {
        ArrayList<String> ids = new ArrayList<>();
        for (String id : m_nodeSetFactory.getNodeFactoryIds()) {
            ids.add(id + m_versionSuffix);
        }
        return ids;
    }
    
    private String removeSuffix(String id) {
        return id.substring(0, id.length() - m_versionSuffix.length());
    }

    @Override
    public Class<? extends NodeFactory<? extends NodeModel>> getNodeFactory(
            String id) {
        return m_nodeSetFactory.getNodeFactory(removeSuffix(id));
    }

    @Override
    public String getCategoryPath(String id) {
        return m_nodeSetFactory.getCategoryPath(removeSuffix(id));
    }

    @Override
    public String getAfterID(String id) {
        return m_nodeSetFactory.getAfterID(removeSuffix(id));
    }

    @Override
    public ConfigRO getAdditionalSettings(String id) {
        return m_nodeSetFactory.getAdditionalSettings(removeSuffix(id));
    }

    public void setIsDeprecated(boolean isDeprecated) {
        m_nodeSetFactory.setIsDeprecated(isDeprecated);
    }

}
