/**
 * Copyright (c) 2013, Stephan Aiche.
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
package org.ballproject.knime.nodegeneration.model.directories;

import java.io.File;
import java.io.FileNotFoundException;

import org.ballproject.knime.base.model.Directory;

import com.genericworkflownodes.knime.custom.Architecture;
import com.genericworkflownodes.knime.custom.OperatingSystem;

/**
 * Abstract representation of a fragment project/directory.
 * 
 * @author aiche
 */
public class FragmentDirectory extends GenericPluginDirectory {

	/**
	 * The serialVersionUID.
	 */
	private static final long serialVersionUID = 4561247274907458731L;

	/**
	 * Create the directory.
	 * 
	 * @param directory
	 * @param payload
	 * @param
	 * @throws FileNotFoundException
	 */
	public FragmentDirectory(Directory directory, Architecture arch,
			OperatingSystem os, String packageRoot)
			throws FileNotFoundException {
		super(new File(directory, String.format("%s.%s.%s", packageRoot,
				os.toOSGIOS(), arch.toOSGIArch())), packageRoot);
	}
}
