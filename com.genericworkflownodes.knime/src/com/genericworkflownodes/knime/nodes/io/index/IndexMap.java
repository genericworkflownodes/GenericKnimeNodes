package com.genericworkflownodes.knime.nodes.io.index;

import java.util.ArrayList;
import java.util.Arrays;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.knime.core.node.NodeLogger;

/**
 * Utility class for a singleton <code>IndexTypesFileTypeMap</code>.
 *
 *
 * @author Kerstin Neubert, FU Berlin
 */
public final class IndexMap {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(IndexMap.class);

    private static final String EXTENSIONPOINT_ID = "com.genericworkflownodes.knime.mime.filesuffix.Checker";

    static {
        // Add -Types defined by other plugins
        addFromExtensions();
    }

    private IndexMap() {
        // Disable default constructor
    }

    /**
     * @param fileextension The file extension to search for
     * @return MIME-Type for the given file extension
     */
    public static String getIndexType(final String fileextension) {
        return FileTypeMap.getDefaultFileTypeMap().getContentType("." + fileextension.toLowerCase());
    }
   
    /**
     * Searches for all Index-Types registered through the extension point.
     *
     * @return Index-Types
     */
    private static IndexTypeEntry[] getTypesFromExtensions() {
        // Get extensions
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint point = registry.getExtensionPoint(EXTENSIONPOINT_ID);
        IExtension[] extensions = point.getExtensions();
        LOGGER.debug("Found " + extensions.length + " extensions.");
        
        // Add all configuration elements to one list
        ArrayList<IConfigurationElement> allElements = new ArrayList<IConfigurationElement>();
        for (IExtension ext : extensions) {
            IConfigurationElement[] elements = ext.getConfigurationElements();
            allElements.addAll(Arrays.asList(elements));
        }
        IndexTypeEntry[] entries = new IndexTypeEntry[allElements.size()];
        // Add each element
        for (int i = 0; i < entries.length; i++) {
            // Get Index-Type
            String type = allElements.get(i).getAttribute("name");
            entries[i] = new IndexTypeEntry(type);
            IConfigurationElement[] children = allElements.get(i).getChildren();
            
            // Get file extensions (suffix_string)
            for (int j = 0; j < children.length; j++) {
                String suffix = children[j].getAttribute("suffix_string").toLowerCase();
                entries[i].addExtension(suffix);
                LOGGER.debug("Found Type \"" + type + "\" for file extension \"" + suffix + "\"");
            }
        }
        return entries;
    }

    /**
     * @return All the registered Index-Types
     */
    public static IndexTypeEntry[] getAllTypes() {
        IndexTypeEntry[] fromExtension = getTypesFromExtensions();
        IndexTypeEntry[] result = Arrays.copyOf(fromExtension, fromExtension.length);

        return result;
    }

    /**
     * Adds Index-Types added through the extension point into the Index map.
     */
    private static void addFromExtensions() {
        IndexTypeEntry[] types = getTypesFromExtensions();
        for (int i = 0; i < types.length; i++) {
            // org.knime.core sets the default map to a MimetypesFileTypeMap
            ((MimetypesFileTypeMap) FileTypeMap.getDefaultFileTypeMap()).addMimeTypes(types[i].toString());
        }
    }

}
