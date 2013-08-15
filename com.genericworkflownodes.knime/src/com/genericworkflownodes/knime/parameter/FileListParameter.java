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

import com.genericworkflownodes.knime.port.Port;

/**
 * The FileListParameter class is used to store lists of filenames.
 * 
 * This is a convenience class to handle user supplied output filenames.
 * 
 * @author roettig
 */
public class FileListParameter extends StringListParameter implements
		IFileParameter {

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = 3010211738983269403L;

	/**
	 * The m_port associated to the file list.
	 */
	private Port m_port;

	/**
	 * Constructor.
	 * 
	 * @param key
	 *            The unique key of the parameter.
	 * @param value
	 *            The value of the parameter.
	 */
	public FileListParameter(final String key, final List<String> value) {
		super(key, value);
	}

	@Override
	public void setPort(final Port port) {
		m_port = port;
	}

	@Override
	public Port getPort() {
		return m_port;
	}
}
