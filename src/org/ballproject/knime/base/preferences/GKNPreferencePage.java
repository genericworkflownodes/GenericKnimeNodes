package org.ballproject.knime.base.preferences;

import org.ballproject.knime.GenericNodesPlugin;
import org.ballproject.knime.base.util.FileStash;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.swt.widgets.Composite;

public class GKNPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage 
{

	public GKNPreferencePage()
	{
		super (GRID);
		IPreferenceStore store = GenericNodesPlugin.getDefault().getPreferenceStore();
		setPreferenceStore(store);
        setDescription("KNIME GKN preferences");
	}
	
	@Override
	public void init(IWorkbench wb)
	{
	}

	@Override
	protected void createFieldEditors()
	{
		Composite parent = getFieldEditorParent();
		addField(new DirectoryFieldEditor(GKNPreferenceInitializer.PREF_FILE_STASH_LOCATION, "File stash directory:", parent));
	}

	@Override
	public void propertyChange(PropertyChangeEvent event)
	{
		String dir = event.getNewValue().toString();
		FileStash.getInstance().setStashDirectory(dir);
	}

}
