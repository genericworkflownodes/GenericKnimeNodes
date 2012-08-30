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

package com.genericworkflownodes.knime.generic_node.dialogs.param_dialog.itemlist;

/**
 * The Validator interface defines all methods a validator needs to validate a
 * user supplied string for validity (i.e. float or int value-ness).
 * 
 * @author roettig
 * 
 */
public interface Validator {
	/**
	 * tries to validate the supplied string value.
	 * 
	 * @param s
	 *            string to validate
	 * @return is string valid
	 */
	boolean validate(String s);

	/**
	 * returns the name of the validator (i.e. float validator).
	 * 
	 * @return name of validator
	 */
	String getName();

	/**
	 * returns the last reason of failure from the validator.
	 * 
	 * @return reason of validation failure
	 */
	String getReason();
}