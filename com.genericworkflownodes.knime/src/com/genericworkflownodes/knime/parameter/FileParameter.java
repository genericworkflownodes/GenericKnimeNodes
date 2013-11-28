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
package com.genericworkflownodes.knime.parameter;

import com.genericworkflownodes.knime.port.Port;

/**
 * Abstraction of in-/output file parameters.
 * 
 * @author aiche
 */
public class FileParameter extends StringParameter implements IFileParameter {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 3067243738185781393L;

    /**
     * C'tor.
     * 
     * @param key
     *            Parameter key.
     * @param value
     *            Parameter value.
     */
    public FileParameter(String key, String value) {
        super(key, value);
    }

    /**
     * The port associated to the file parameter.
     */
    private Port port;

    @Override
    public void setPort(final Port port) {
        this.port = port;
    }

    @Override
    public Port getPort() {
        return port;
    }

}
