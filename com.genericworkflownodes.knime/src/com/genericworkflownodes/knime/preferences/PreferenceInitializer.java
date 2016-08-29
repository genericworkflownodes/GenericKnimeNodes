/**
 * Copyright (c) 2012, Marc Röttig.
 *
 * This file is part of GenericKnimeNodes.
 * 
 * GenericKnimeNodes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.genericworkflownodes.knime.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.genericworkflownodes.knime.GenericNodesPlugin;
import com.genericworkflownodes.util.Helper;
/**
 * Initializer for the GKN preferences.
 * 
 * @author roettig, aiche
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {
    /**
     * Preferences key for the file stash location.
     */
    public static final String PREF_FILE_STASH_LOCATION = "knime.gkn.filestashlocation";

    /**
     * Preferences key for the debug mode flag.
     */
    public static final String PREF_DEBUG_MODE = "knime.gkn.debug";
    
    /**
     * Preferences key for the Docker-Machine installation directory.
     */
    public static final String DOCKER_MACHINE_INSTALLATION_DIRECTORY = "knime.gkn.dockerMachineInstallationDir";
    
    /**
     * Preferences key for the Docker-Machine usage.
     */
    public static final String DOCKER_MACHINE_USAGE = "knime.gkn.dockerMachineUsage";
    
    /**
     * Preferences key for the VM installation directory.
     */
    public static final String VM_INSTALLATION_DIRECTORY = "knime.gkn.vmInstallationDir";
    
    @Override
    public void initializeDefaultPreferences() {
        // get the preference store for the UI plugin
        IPreferenceStore store = GenericNodesPlugin.getDefault()
                .getPreferenceStore();

        // set default values
        store.setDefault(PREF_DEBUG_MODE, GenericNodesPlugin.isDebug());
        store.setDefault(DOCKER_MACHINE_USAGE, GenericNodesPlugin.isDebug());
        
        store.setDefault(DOCKER_MACHINE_INSTALLATION_DIRECTORY,
                    GenericNodesPlugin.getDockerInstallationDir()); //$NON-NLS-1$
        store.setDefault(VM_INSTALLATION_DIRECTORY,
                    GenericNodesPlugin.getVmInstllationDir()); //$NON-NLS-1$


    }
}
