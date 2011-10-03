package org.ballproject.knime.base.preferences;

import org.ballproject.knime.GenericNodesPlugin;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.widgets.Composite;

public class GKNPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage 
{

	public GKNPreferencePage()
	{
		super (GRID);
		setPreferenceStore(GenericNodesPlugin.getDefault().getPreferenceStore());
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
        IntegerFieldEditor gknFileSizeLimit =
                new IntegerFieldEditor( GKNPreferenceInitializer.PREF_FILE_SIZE_LIMIT, "maximal file size ", parent);
        addField(gknFileSizeLimit);
	}

}
