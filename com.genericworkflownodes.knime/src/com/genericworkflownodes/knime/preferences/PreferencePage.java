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

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.genericworkflownodes.knime.GenericNodesPlugin;

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
        debugModeFieldEditor = new BooleanFieldEditor(
                PreferenceInitializer.PREF_DEBUG_MODE, "Debug mode", parent);
        addField(debugModeFieldEditor);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
    }

    @Override
    public boolean performOk() {
        IPreferenceStore store = GenericNodesPlugin.getDefault()
                .getPreferenceStore();
        boolean flag = debugModeFieldEditor.getBooleanValue();
        store.setValue(PreferenceInitializer.PREF_DEBUG_MODE, flag);
        GenericNodesPlugin.setDebug(flag);
        return true;
    }

}
