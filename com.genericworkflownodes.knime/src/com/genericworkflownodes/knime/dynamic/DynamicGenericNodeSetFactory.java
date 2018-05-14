package com.genericworkflownodes.knime.dynamic;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.config.ConfigRO;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;

import com.genericworkflownodes.knime.config.reader.CTDConfigurationReader;
import com.genericworkflownodes.knime.custom.config.IPluginConfiguration;

public abstract class DynamicGenericNodeSetFactory implements GenericNodeSetFactory {

    private static final NodeLogger logger = NodeLogger.getLogger(DynamicGenericNodeSetFactory.class);
    
    /**
     * Creates a new <code>DynamicGenericNodeSetFactory</code>
     * that loads nodes from CTD files in the given source folder.
     * @param folder the source folder to search for CTDs in.
     */
    public DynamicGenericNodeSetFactory() {
        Version v = getVersion();
        m_versionSuffix = String.format("_%d_%d_%d", v.getMajor(), v.getMinor(), v.getMicro());
    }
    
    private String m_versionSuffix;
    private Map<String, String> m_idToFile;
    
    /**
     * @return The class of the node factory to use.
     */
    protected abstract Class<? extends GenericNodeFactory> getNodeFactory();
    
    /**
     * Implement this method and return the configuration of the plugin
     * the nodes are hosted in.
     * @return the plugin configuration.
     */
    public abstract IPluginConfiguration getPluginConfig();
    
    protected abstract String getCategoryPath();
    
    protected abstract String getIdForTool(String relPath);
    
    @Override
    public Collection<String> getNodeFactoryIds() {
        if (m_idToFile == null) {
            m_idToFile = new LinkedHashMap<>();
            for (String s : getPluginConfig().getBinaryManager().listTools()) {
                m_idToFile.put(getIdForTool(s) + m_versionSuffix, s);
            }
        }
        return m_idToFile.keySet();
    }
    
    @Override
    public Class<? extends NodeFactory<? extends NodeModel>> getNodeFactory(String id) {
        return getNodeFactory();
    }

    @Override
    public String getCategoryPath(String id) {
        String category;
        File f = getPluginConfig().getBinaryManager().resolveToolDescriptorPath(m_idToFile.get(id));
        try (InputStream cfgStream = new FileInputStream(f)) {
            category = new CTDConfigurationReader().read(cfgStream).getCategory();
        } catch(Exception e) {
            logger.error("Could not read node category from CTD, using '/' instead.", e);
            category = "";
        }
        return getCategoryPath() + "/" + getPluginConfig().getPluginVersion() + "/" + category;
    }

    @Override
    public String getAfterID(String id) {
        return "";
    }

    @Override
    public ConfigRO getAdditionalSettings(String id) {
        NodeSettings ns = new NodeSettings("");
        ns.addString(DynamicGenericNodeFactory.ID_CFG_KEY, id);
        ns.addString(DynamicGenericNodeFactory.CTD_FILE_CFG_KEY,
                m_idToFile.get(id));
        ns.addString(DynamicGenericNodeFactory.NSFID_CFG_KEY, getId());
        return ns;
    }
    
    @Override
    public String getId() {
        return getClass().getCanonicalName() + m_versionSuffix;
    }
    
    @Override
    public Version getVersion() {
        return FrameworkUtil.getBundle(getClass()).getVersion();
    }
}
