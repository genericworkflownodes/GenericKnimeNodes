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
import com.genericworkflownodes.util.FileStashFactory;

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

    @Override
    public void initializeDefaultPreferences() {
        // get the preference store for the UI plugin
        IPreferenceStore store = GenericNodesPlugin.getDefault()
                .getPreferenceStore();

        // set default values
        store.setDefault(PREF_FILE_STASH_LOCATION, FileStashFactory
                .getTempParentDirectory().getAbsolutePath());
        store.setDefault(PREF_DEBUG_MODE, GenericNodesPlugin.isDebug());
    }

}
