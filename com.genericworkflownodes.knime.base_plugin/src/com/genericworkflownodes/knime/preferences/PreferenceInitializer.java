package com.genericworkflownodes.knime.preferences;

import org.ballproject.knime.GenericNodesPlugin;
import org.ballproject.knime.base.util.FileStash;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class PreferenceInitializer extends AbstractPreferenceInitializer {
	public static final String PREF_FILE_STASH_LOCATION = "knime.gkn.filestashlocation";
	public static final String PREF_DEBUG_MODE = "knime.gkn.debug";

	@Override
	public void initializeDefaultPreferences() {
		// get the preference store for the UI plugin
		IPreferenceStore store = GenericNodesPlugin.getDefault()
				.getPreferenceStore();

		// set default values
		store.setDefault(PREF_FILE_STASH_LOCATION, FileStash.getInstance()
				.getStashDirectory());
		store.setDefault(PREF_DEBUG_MODE, GenericNodesPlugin.isDebug());
	}

}
