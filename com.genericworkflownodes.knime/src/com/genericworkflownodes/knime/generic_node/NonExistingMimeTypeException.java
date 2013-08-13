/**
 * Copyright (c) 2011-2012, Marc Röttig, Stephan Aiche.
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
package com.genericworkflownodes.knime.generic_node;

/**
 * Exception indicating that no MIMType was given.
 * 
 * @author aiche
 */
public class NonExistingMimeTypeException extends Exception {

	/**
	 * The serialVersionUID.
	 */
	private static final long serialVersionUID = 7338966496428115378L;

	/**
	 * 
	 * @param someFileName
	 *            The file with non-existing mime type.
	 */
	public NonExistingMimeTypeException(String someFileName) {
		super("No matching registered MIME type for " + someFileName
				+ " found.");
	}

}
