/**
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

package com.genericworkflownodes.knime.port;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The Port class represents a incoming or outgoing port of a KNIME node.
 * 
 * Getters can be used to find out the: <br/>
 * <ul>
 * <li>name of the port</li>
 * <li>description for the port</li>
 * <li>list of file extensions supported by this port</li>
 * </ul>
 * 
 * @author roettig, aiche
 * 
 */
public class Port implements Serializable {

	/**
	 * The serialVersionUID.
	 */
	private static final long serialVersionUID = -3038975820102785198L;
	
	/**
	 * Flag to show if the port is optional for the tool.
	 */
	protected boolean isOptional;
	
	/**
	 * The name of the port.
	 */
	protected String name;
	
	/**
	 * The description of the port.
	 */
	protected String description;
	
	/**
	 * Flag to indicate if this port can handle lists of files. 
	 */
	protected boolean isMultiFile;

	/**
	 * The list of supported file extensions.
	 */
	protected List<String> types = new ArrayList<String>();

	/**
	 * Adds a supported {@link MIMEType} to the port.
	 * 
	 * @param MIMEtype
	 *            A new {@link MIMEType} supported by this port.
	 */
	public void addMimeType(String type) {
		types.add(type);
	}

	/**
	 * Returns the list of supported MIMEtypes of this port.
	 * 
	 * @return List of all {@link MIMEType}s supported by this port.
	 */
	public List<String> getMimeTypes() {
		return types;
	}

	/**
	 * Returns whether this port is optional or needs a mandatory incoming
	 * connection.
	 * 
	 * @return True if the port is optional, false otherwise.
	 */
	public boolean isOptional() {
		return isOptional;
	}

	/**
	 * Sets whether this port is optional or needs a mandatory incoming
	 * connection.
	 * 
	 * @param isOptional
	 *            New indicator if the given port is optional or not.
	 * 
	 */
	public void setOptional(boolean isOptional) {
		this.isOptional = isOptional;
	}

	/**
	 * Returns the name of the port
	 * 
	 * @return port The name of the port.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the port.
	 * 
	 * @param name
	 *            The new port name.
	 * 
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the description for this port.
	 * 
	 * @return The port description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description for this port.
	 * 
	 * @param description
	 *            The new description of the port.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns whether this port allows multiple files of a given MIMEtype.
	 * 
	 * @return True if the port allows multiple files, false otherwise.
	 */
	public boolean isMultiFile() {
		return isMultiFile;
	}

	/**
	 * Set whether this port allows multiple files of a given MIMEtype.
	 * 
	 * @param isMultiFile
	 *            New indicator if more then one value for the given port are
	 *            allowed.
	 */
	public void setMultiFile(boolean isMultiFile) {
		this.isMultiFile = isMultiFile;
	}
}
