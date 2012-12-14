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
package com.genericworkflownodes.knime.mime.demangler;

import java.util.List;

import org.knime.core.data.DataTableSpec;

/**
 * Provides central access to registered {@link IDemangler}s and helps in
 * associating them with a given {@link DataTableSpec}.
 * 
 * @author aiche
 */
public interface IDemanglerRegistry {

	/**
	 * Returns a {@link List} of {@link IDemangler} for the selected
	 * {@link MIMEType}.
	 * 
	 * @param mType
	 *            The requested {@link MIMEType}.
	 * @return A {@link List} of available {@link IDemangler}.
	 */
	List<IDemangler> getDemangler(String mType);

	/**
	 * Given a {@link DataTableSpec} returns a {@link List} of
	 * {@link IDemangler} that can translate this {@link DataTableSpec} into a
	 * file.
	 * 
	 * @param spec
	 *            The {@link DataTableSpec} to translate.
	 * @return The {@link IDemangler} that can transform this
	 *         {@link DataTableSpec}.
	 */
	List<IDemangler> getMangler(DataTableSpec spec);

	/**
	 * Returns a list of a registered {@link IDemangler}s.
	 * 
	 * @return A list of available {@link IDemangler}s.
	 */
	List<IDemangler> getAvailableDemangler();
}
