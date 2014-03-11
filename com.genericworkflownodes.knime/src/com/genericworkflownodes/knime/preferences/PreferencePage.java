/**
 * Copyright (c) 2012, Marc Röttig, Stephan Aiche.
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

import java.io.File;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.genericworkflownodes.knime.GenericNodesPlugin;
import com.genericworkflownodes.util.FileStashFactory;

/**
 * GKN preferences page.
 * 
 * @author aiche
 */
public class PreferencePage extends FieldEditorPreferencePage implements
        IWorkbenchPreferencePage {

    /**
     * Debug mode UI element.
     */
    private BooleanFieldEditor debugModeFieldEditor;

    /**
     * File stash location UI element.
     */
    private DirectoryFieldEditor fileStashLocationFieldEditor;

    /**
     * Default c'tor.
     */
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

        fileStashLocationFieldEditor = new DirectoryFieldEditor(
                PreferenceInitializer.PREF_FILE_STASH_LOCATION,
                "File stash directory:", parent);
        debugModeFieldEditor = new BooleanFieldEditor(
                PreferenceInitializer.PREF_DEBUG_MODE, "Debug mode", parent);

        addField(fileStashLocationFieldEditor);
        addField(debugModeFieldEditor);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
    }

    @Override
    public boolean performOk() {
        IPreferenceStore store = GenericNodesPlugin.getDefault()
                .getPreferenceStore();
        //
        String dir = fileStashLocationFieldEditor.getStringValue();
        FileStashFactory.setTempParentDirectory(new File(dir));
        store.setValue(PreferenceInitializer.PREF_FILE_STASH_LOCATION, dir);

        //
        boolean flag = debugModeFieldEditor.getBooleanValue();
        GenericNodesPlugin.setDebug(flag);

        return true;
    }

}
