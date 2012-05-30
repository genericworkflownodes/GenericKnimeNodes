package com.genericworkflownodes.knime.preferences;

import org.ballproject.knime.GenericNodesPlugin;
import org.ballproject.knime.base.util.FileStash;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	private StringFieldEditor PREF_PATHES;
	private BooleanFieldEditor PREF_DEBUG_MODE;
	private DirectoryFieldEditor PREF_FILE_STASH_LOCATION;

	public PreferencePage() {
		super(GRID);
		IPreferenceStore store = GenericNodesPlugin.getDefault()
				.getPreferenceStore();
		setPreferenceStore(store);
	}

	@Override
	public void init(IWorkbench wb) {
	}

	@Override
	protected void createFieldEditors() {
		Composite parent = getFieldEditorParent();

		PREF_FILE_STASH_LOCATION = new DirectoryFieldEditor(
				PreferenceInitializer.PREF_FILE_STASH_LOCATION,
				"File stash directory:", parent);
		PREF_DEBUG_MODE = new BooleanFieldEditor(
				PreferenceInitializer.PREF_DEBUG_MODE, "Debug mode", parent);
		PREF_PATHES = new StringFieldEditor(PreferenceInitializer.PREF_PATHES,
				"Pathes:", parent);

		addField(PREF_FILE_STASH_LOCATION);
		addField(PREF_PATHES);
		addField(PREF_DEBUG_MODE);
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
	}

	@Override
	public boolean performOk() {
		IPreferenceStore store = GenericNodesPlugin.getDefault()
				.getPreferenceStore();
		//
		String dir = PREF_FILE_STASH_LOCATION.getStringValue();
		FileStash.getInstance().setStashDirectory(dir);
		store.setValue(PreferenceInitializer.PREF_FILE_STASH_LOCATION, dir);

		//
		boolean flag = PREF_DEBUG_MODE.getBooleanValue();
		GenericNodesPlugin.setDebug(flag);

		String pathes = PREF_PATHES.getStringValue();

		store.setValue(PreferenceInitializer.PREF_PATHES, pathes);

		return true;
	}

}
