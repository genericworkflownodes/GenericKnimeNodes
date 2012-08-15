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
package com.genericworkflownodes.knime.mime.demangler;

import java.util.List;

/**
 * Collection of provided {@link IDemangler}.
 * 
 * @author roettig, aiche
 */
public interface IDemanglerProvider {

	/**
	 * Returns the list of provided {@link IDemangler}.
	 * 
	 * @return The list of {@link IDemangler}.
	 */
	List<IDemangler> getDemanglers();
}
