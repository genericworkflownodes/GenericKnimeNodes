/**
 * Copyright (c) 2012, Stephan Aiche.
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Content provider for ExternalTools table.
 * 
 * @author aiche
 */
public class ExternalToolSettingsContentProvider implements
        IStructuredContentProvider {

    @Override
    public void dispose() {
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object[] getElements(Object inputElement) {
        LinkedHashMap<String, ExternalToolSettings> input = (LinkedHashMap<String, ExternalToolSettings>) inputElement;

        // convert to array of ExternalToolSettings
        List<ExternalToolSettings> settingsAsList = new ArrayList<ExternalToolSettings>();
        for (Map.Entry<String, ExternalToolSettings> settingsEntry : input
                .entrySet()) {
            settingsAsList.add(settingsEntry.getValue());
        }
        return settingsAsList.toArray();
    }

}
