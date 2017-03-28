package com.genericworkflownodes.knime.dynamic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSetFactory;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.config.ConfigRO;

import com.genericworkflownodes.knime.config.reader.CTDConfigurationReader;

public abstract class DynamicGenericNodeSetFactory implements NodeSetFactory {

    private static final NodeLogger logger = NodeLogger.getLogger(DynamicGenericNodeSetFactory.class);
    private static String[] CTD_EXT = new String[]{"ctd", "xml"};
    
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

    static File resolveSourceFile(Class<?> clazz, String relPath) {
        return Paths.get(clazz
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath(), relPath).toFile();
    }
    
    protected abstract String getIdForTool(String relPath);
    
    @Override
    public Collection<String> getNodeFactoryIds() {
        if (m_idToFile == null) {
            m_idToFile = new LinkedHashMap<>();

            for (File f : FileUtils.listFiles(m_folderFile, CTD_EXT, true)) {
                if (f.isFile()) {
                    String name = f.getName();
                    if (name.endsWith(".xml") && !name.equals("config.xml")) {
                        continue;
                    }
                    Path parent = Paths.get(m_folderFile.getAbsolutePath());
                    Path filePath = Paths.get(f.getAbsolutePath());
                    String p = parent.relativize(filePath).toString();
                    m_idToFile.put(getIdForTool(p), p);
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
        File f = Paths.get(m_folderFile.getAbsolutePath()).resolve(m_idToFile.get(id)).toAbsolutePath().toFile();
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
