/**
 * Copyright (c) 2014, Stephan Aiche.
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

/**
 * Indicates that no tool path was configured for this tool.
 * 
 * @author aiche
 */
public class UnknownToolPathException extends Exception {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = -7437711126155913809L;

    /**
     * C'tor.
     * 
     * @param toolname
     *            The name of the tool that was not properly configured.
     */
    public UnknownToolPathException(String toolname) {
        super(
                "There is no path (shipped or user-defined) stored for the tool: "
                        + toolname);
    }

}
