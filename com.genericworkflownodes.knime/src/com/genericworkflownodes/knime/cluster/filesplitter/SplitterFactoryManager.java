package com.genericworkflownodes.knime.cluster.filesplitter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.knime.core.node.NodeLogger;

public final class SplitterFactoryManager {

    public static final String EXT_POINT_ID = "com.genericworkflownodes.knime.filesplitter";
    public static final String EXT_POINT_ATTR_DEF = "class";

    private static final NodeLogger LOGGER = NodeLogger
            .getLogger(SplitterFactoryManager.class);
    private static SplitterFactoryManager instance;

    private List<SplitterFactory> m_factories = new ArrayList<SplitterFactory>();

    public static SplitterFactoryManager getInstance() {
        if (instance == null) {
            instance = new SplitterFactoryManager();
        }
        return instance;
    }

    private SplitterFactoryManager() {
        registerExtensionPoints();
    }

    /**
     * Registers all extension point implementations.
     */
    private void registerExtensionPoints() {
        try {
            final IExtensionRegistry registry = Platform.getExtensionRegistry();
            final IExtensionPoint point = registry
                    .getExtensionPoint(EXT_POINT_ID);
            if (point == null) {
                LOGGER.error("Invalid extension point: " + EXT_POINT_ID);
                throw new IllegalStateException("ACTIVATION ERROR: "
                        + " --> Invalid extension point: " + EXT_POINT_ID);
            }
            for (final IConfigurationElement elem : point
                    .getConfigurationElements()) {
                final String operator = elem.getAttribute(EXT_POINT_ATTR_DEF);
                final String decl = elem.getDeclaringExtension()
                        .getUniqueIdentifier();

                if ((operator == null) || operator.isEmpty()) {
                    LOGGER.error("The extension '" + decl
                            + "' doesn't provide the required attribute '"
                            + EXT_POINT_ATTR_DEF + "'");
                    LOGGER.error("Extension " + decl + " ignored.");
                    continue;
                }

                try {
                    final SplitterFactory factory = (SplitterFactory) elem
                            .createExecutableExtension(EXT_POINT_ATTR_DEF);
                    addSplitterFactory(factory);
                } catch (final Exception t) {
                    LOGGER.error(
                            "Problems during initialization of missing cell handler (with id '"
                                    + operator + "'.)");
                    if (decl != null) {
                        LOGGER.error("Extension " + decl + " ignored.", t);
                    }
                }
            }
        } catch (final Exception e) {
            LOGGER.error(
                    "Exception while registering MissingCellHandler extensions");
        }
    }

    private void addSplitterFactory(SplitterFactory factory) {
        m_factories.add(factory);
    }

    /**
     * @return all factories registered in this instance
     */
    public List<SplitterFactory> getFactories() {
        return Collections.unmodifiableList(m_factories);
    }
    
    /**
     * Retrieves a factory with a given unique identifier.
     * @param id the factory id
     * @return the factory or null if it does not exist
     */
    public SplitterFactory getFactory(String id) {
        for (SplitterFactory fac : m_factories) {
            if (id.equals(fac.getID())){
                return fac;
            }
        }
        return null;
    }

    /**
     * Retrieves all factories compatible with the given mimetypes.
     * @param mimetypes the mimetypes
     * @return a list of matching factories
     */
    public List<SplitterFactory> getFactories(String... mimetypes) {

        Set<String> factoryIds = new HashSet<String>();
        List<SplitterFactory> factories = new ArrayList<SplitterFactory>();
        if (mimetypes == null)
        {
            return factories;
        }
        
        for (SplitterFactory fac : m_factories) {
            for (String mimetype : mimetypes) {
                if (fac.isApplicable(mimetype) && !factoryIds.contains(fac.getID())) {
                    factories.add(fac);
                    factoryIds.add(fac.getID());
                    break;
                }
            }
        }
        return factories;
    }
}
