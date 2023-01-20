package com.genericworkflownodes.knime.custom.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.knime.core.node.NodeLogger;

public class DLLRegistry {

	/**
	 * The id of the used extension point.
	 */
    private static final String EXTENSION_POINT_ID = "com.genericworkflownodes.knime.custom.config.DLLProvider";

	/**
	 * The central static logger.
	 */
	private static final NodeLogger LOGGER = NodeLogger.getLogger(DLLRegistry.class);

	/**
	 * Default constructor
	 */
	private DLLRegistry() {
	}

    /**
     * Initialization-on-demand holder idiom holder for the DllRegistry
     * instance.
     *
     */
    private static class LazyHolder {
        private static final DLLRegistry INSTANCE = new DLLRegistry();
    }

    /**
     * Gets the instance of this class.
     *
     * @return An instance of this class.
     */
    public static DLLRegistry getDLLRegistry() {
        return LazyHolder.INSTANCE;
    }

    /**
     * Searches all extensions for registered dlls.
     *
     * @return A list of paths ({@link String}), or an empty list if no dll was
     *         found.
     */
    public HashMap<String, ArrayList<String>> getAllAvailableDLLFolders() throws CoreException {

        HashMap<String, ArrayList<String>> res = new HashMap<String,ArrayList<String>>();

		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = reg.getConfigurationElementsFor(EXTENSION_POINT_ID);

		// TODO cache this??
        for (IConfigurationElement elem : elements) {
            Set<String> dllPaths = new HashSet<String>();
            //System.out.println(elem.getName() + elem.getValue());
            final String name = elem.getAttribute("name");
            final Object o = elem.createExecutableExtension("class");
            // cast is guaranteed to work based on the extension point
            // definition
            for (File f : ((IDLLProvider) o).getDLLs()) {
                try {
                    String path = f.getCanonicalFile().getParent();

                    if (path != null && !path.isEmpty()
                            && !dllPaths.contains(path)) {
                        dllPaths.add(path);
                    }
                } catch (IOException e) {
                    LOGGER.warn("The path of the dll could not be resolved ["
                            + f.toString() + "]" + e.getMessage());
                }
			}
            for (File fol : ((IDLLProvider) o).getDLLFolders()) {
                if (fol != null) {
                    dllPaths.add(fol.toString());
                }
            }
            
            if (dllPaths.isEmpty()) {
                res.put(name, new ArrayList<String>()); 
            } else {
                res.put(name, new ArrayList<String>(dllPaths));
            }
		}

       return res;
	}
    
    public List<String> getAvailableDLLFoldersFor(List<String> names) throws CoreException {
        ArrayList<String> res = new ArrayList<String>();
        for (String n : names)
        {
            res.addAll(getAllAvailableDLLFolders().get(n));
        }
        return res;
    }

}
