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
package com.genericworkflownodes.knime.mime.servicefactory;

import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

import com.genericworkflownodes.knime.mime.demangler.DemanglerRegistry;
import com.genericworkflownodes.knime.mime.demangler.IDemanglerRegistry;

/**
 * Service wrapper for {@link IDemanglerRegistry}.
 * 
 * @author aiche
 */
public class DemanglerRegistryServiceFactory extends AbstractServiceFactory {

	/**
	 * The managed registry.
	 */
	private IDemanglerRegistry registry;

	/**
	 * 
	 */
	public DemanglerRegistryServiceFactory() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.services.AbstractServiceFactory#create(java.lang.Class,
	 * org.eclipse.ui.services.IServiceLocator,
	 * org.eclipse.ui.services.IServiceLocator)
	 */
	@Override
	public Object create(@SuppressWarnings("rawtypes") Class serviceInterface,
			IServiceLocator parentLocator, IServiceLocator locator) {
		if (serviceInterface == IDemanglerRegistry.class) {
			return getDemanglerRegistry();
		}
		return null;
	}

	private IDemanglerRegistry getDemanglerRegistry() {
		if (registry == null) {
			registry = new DemanglerRegistry();
		}
		return registry;
	}

}
