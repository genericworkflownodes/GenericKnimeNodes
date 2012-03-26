/*
 * Copyright (c) 2011, Marc Röttig.
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

package org.ballproject.knime.base.parameter;

/**
 * The InvalidParameterValueException is thrown when the supplied value for a
 * parameter could not be validated.
 * 
 * @author roettig
 * 
 */
public class InvalidParameterValueException extends Exception {
	private static final long serialVersionUID = 5408531919859345420L;

	public InvalidParameterValueException(String msg) {
		super(msg);
	}

	public InvalidParameterValueException(String msg, Throwable t) {
		super(msg, t);
	}
}
