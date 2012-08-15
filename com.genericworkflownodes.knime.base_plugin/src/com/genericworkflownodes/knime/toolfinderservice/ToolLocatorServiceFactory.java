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
package com.genericworkflownodes.knime.toolfinderservice;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

import com.genericworkflownodes.knime.GenericNodesPlugin;

/**
 * {@link AbstractServiceFactory} for the TooFinderService
 * 
 * @author aiche
 */
public class ToolLocatorServiceFactory extends AbstractServiceFactory {

	private IToolLocatorService toolLocatorService;

	private IToolLocatorService getToolLocatorService() {
		if (toolLocatorService == null) {
			toolLocatorService = new PluginPreferenceToolLocator();

			// configure the tool locator service using the base plugin
			// PreferenceStore
			IPreferenceStore store = GenericNodesPlugin.getDefault()
					.getPreferenceStore();
			((PluginPreferenceToolLocator) toolLocatorService).init(store);
		}
		return toolLocatorService;
	}

	/**
	 * 
	 */
	public ToolLocatorServiceFactory() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see org.eclipse.ui.services.AbstractServiceFactory#create(java.lang.Class,
	 *      org.eclipse.ui.services.IServiceLocator,
	 *      org.eclipse.ui.services.IServiceLocator)
	 */
	@Override
	public Object create(@SuppressWarnings("rawtypes") Class serviceInterface,
			IServiceLocator parentLocator, IServiceLocator locator) {
		if (serviceInterface == IToolLocatorService.class) {
			return getToolLocatorService();
		}
		return null;
	}

}
