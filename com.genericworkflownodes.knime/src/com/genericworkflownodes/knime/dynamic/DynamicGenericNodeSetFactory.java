package com.genericworkflownodes.knime.dynamic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSetFactory;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.config.ConfigRO;

import com.genericworkflownodes.knime.config.reader.CTDConfigurationReader;

public abstract class DynamicGenericNodeSetFactory implements NodeSetFactory {

    private static final NodeLogger logger = NodeLogger.getLogger(DynamicGenericNodeSetFactory.class);
    
    /**
     * Creates a new <code>DynamicGenericNodeSetFactory</code>
     * that loads nodes from CTD files in the given source folder.
     * @param folder the source folder to search for CTDs in.
     */
    public DynamicGenericNodeSetFactory(String folder) {
        m_folder = folder;
        m_folderFile = resolveSourceFile(getClass(), folder);
    }
    
    private File m_folderFile;
    private String m_folder;
    private Map<String, String> m_idToFile;
    
    /**
     * @return The class of the node factory to use.
     */
    protected abstract Class<? extends DynamicGenericNodeFactory> getNodeFactory();
    
    protected abstract String getCategoryPath();
    
    protected abstract String getNodeIdPrefix();
    
    static File resolveSourceFile(Class<?> clazz, String relPath) {
        return Paths.get(clazz
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath(), relPath).toFile();
    }
    
    String getIdForTool(String toolName) {
        return getNodeIdPrefix() + "." + toolName.toLowerCase().replaceAll("[^a-zA-Z0-9_]", "_");
    }
    
    @Override
    public Collection<String> getNodeFactoryIds() {
        if (m_idToFile == null) {
            m_idToFile = new LinkedHashMap<>();
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".ctd");
                }
            };
            for (File f : m_folderFile.listFiles(filter)) {
                if (f.isFile()) {
                    String name = f.getName();
                    m_idToFile.put(getIdForTool(name), name);
                }
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
        File f = Paths.get(m_folderFile.getAbsolutePath()).resolve(id + ".ctd").toAbsolutePath().toFile();
        try (InputStream cfgStream = new FileInputStream(f)) {
            category = new CTDConfigurationReader().read(cfgStream).getCategory();
        } catch(Exception e) {
            logger.error("Could not read node category from CTD, using '/' instead.", e);
            category = "";
        }
        return getCategoryPath() + "/" + category;
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
                    Paths.get(m_folder).resolve(m_idToFile.get(id)).toString());
        return ns;
    }

}
