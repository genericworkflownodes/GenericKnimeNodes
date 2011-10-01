package org.ballproject.knime.base.preferences;

import org.ballproject.knime.GenericNodesPlugin;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class GKNPreferenceInitializer extends AbstractPreferenceInitializer
{
	public static final String PREF_FILE_SIZE_LIMIT = "knime.gkn.filesizelimit";
	
	@Override
	public void initializeDefaultPreferences()
	{
		 // get the preference store for the UI plugin
        IPreferenceStore store = GenericNodesPlugin.getDefault().getPreferenceStore();
        System.out.println("store="+store);
        // set default values
        store.setDefault(PREF_FILE_SIZE_LIMIT, 200000);
	}

}
