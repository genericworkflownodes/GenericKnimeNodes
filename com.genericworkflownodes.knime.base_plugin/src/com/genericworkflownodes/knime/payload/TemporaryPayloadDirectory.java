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
package com.genericworkflownodes.knime.payload;

import java.io.File;
import java.io.FileNotFoundException;

import org.ballproject.knime.base.model.TempDirectory;

/**
 * Represents a payload directory that stores the payload data only temporarily
 * 
 * @author aiche
 */
public class TemporaryPayloadDirectory extends AbstractPayloadDirectory
		implements IPayloadDirectory {

	/**
	 * A (hopefully) unique identifier to ensure that the temporary directory is
	 * also unique.
	 */
	private TempDirectory tmpDirectory;

	public TemporaryPayloadDirectory(String prefix) {
		try {
			this.tmpDirectory = new TempDirectory(prefix);
		} catch (FileNotFoundException e) {
			// TODO: how do we log inside of such a plugin
			e.printStackTrace();
		}
	}

	@Override
	public File getPath() {
		return tmpDirectory;
	}
}
