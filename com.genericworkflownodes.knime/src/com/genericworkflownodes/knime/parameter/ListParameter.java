/**
 * Copyright (c) 2012, Marc Röttig.
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
package com.genericworkflownodes.knime.parameter;

import java.util.List;

/**
 * The ListParameter interface is implemented by {@link Parameter} classes that
 * store lists of plain datatypes.
 * 
 * @author roettig
 * 
 */
public interface ListParameter {
    /**
     * returns a list of string representations of the stored values.
     * 
     * This is mainly for display purposes within GUIs and console.
     * 
     * @return list of strings
     */
    List<String> getStrings();

    /**
     * fill the {@link Parameter} object from a list of strings.
     * 
     * @param values
     *            list of strings
     * @throws InvalidParameterValueException
     */
    void fillFromStrings(String[] values) throws InvalidParameterValueException;
}
