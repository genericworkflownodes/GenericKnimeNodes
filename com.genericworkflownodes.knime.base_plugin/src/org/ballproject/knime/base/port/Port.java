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

package org.ballproject.knime.base.port;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.ballproject.knime.base.mime.MIMEtype;

/**
 * The Port class represents a incoming or outgoing port of a KNIME node.
 * 
 * Getters can be used to find out the: <br/>
 * <ul>
 * <li>name of the port</li>
 * <li>description for the port</li>
 * <li>list of MIMEtypes supported by this port</li>
 * </ul>
 * 
 * @author roettig
 * 
 */
public class Port implements Serializable {

	private static final long serialVersionUID = 5932681718132094413L;
	protected boolean isOptional;
	protected String name;
	protected String description;
	protected boolean isMultiFile;

	protected List<MIMEtype> types = new ArrayList<MIMEtype>();

	/**
	 * adds a supported MIMEtype to the port
	 * 
	 * @param MIMEtype
	 */
	public void addMimeType(MIMEtype type) {
		types.add(type);
	}

	/**
	 * returns the list of supported MIMEtypes of this port
	 * 
	 * @return MIMEtypes list
	 */
	public List<MIMEtype> getMimeTypes() {
		return types;
	}

	/**
	 * returns whether this port is optional or needs a mandatory incoming
	 * connection
	 * 
	 * @return isOptional
	 */
	public boolean isOptional() {
		return isOptional;
	}

	/**
	 * sets whether this port is optional or needs a mandatory incoming
	 * connection
	 * 
	 * @param isOptional
	 *            boolean
	 */
	public void setOptional(boolean isOptional) {
		this.isOptional = isOptional;
	}

	/**
	 * returns the name of the port
	 * 
	 * @return port name
	 */
	public String getName() {
		return name;
	}

	/**
	 * sets the name of the port
	 * 
	 * @param port
	 *            name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * returns the description for this port
	 * 
	 * @return description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * sets the description for this port
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * returns whether this port allows multiple files of a given MIMEtype
	 * 
	 * @return flag
	 */
	public boolean isMultiFile() {
		return isMultiFile;
	}

	/**
	 * set whether this port allows multiple files of a given MIMEtype
	 * 
	 * @param isMultiFile
	 *            flag
	 */
	public void setMultiFile(boolean isMultiFile) {
		this.isMultiFile = isMultiFile;
	}
}
