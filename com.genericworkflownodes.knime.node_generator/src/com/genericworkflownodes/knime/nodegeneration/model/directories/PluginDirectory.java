/**
 * Copyright (c) 2013, Stephan Aiche, Björn Kahlert.
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
package com.genericworkflownodes.knime.nodegeneration.model.directories;

import java.io.File;
import java.io.FileNotFoundException;

import org.ballproject.knime.base.model.Directory;

/**
 * @author aiche, bkahlert
 * 
 */
public class PluginDirectory extends Directory {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8934613520286247501L;

	private File buildProperties;
	private File pluginXml;
	private File manifestMf;
	private File projectFile;

	public PluginDirectory(File directory) throws FileNotFoundException {
		super(directory);

		buildProperties = new File(this, "build.properties");
		pluginXml = new File(this, "plugin.xml");
		manifestMf = new File(this, "META-INF" + File.separator + "MANIFEST.MF");
		projectFile = new File(this, ".project");
	}

	/**
	 * Returns the build.properties file.
	 * 
	 * @return
	 */
	public File getBuildProperties() {
		return buildProperties;
	}

	/**
	 * Returns plugin.xml file.
	 * 
	 * @return
	 */
	public File getPluginXml() {
		return pluginXml;
	}

	/**
	 * Returns the META-INF/MANIFEST.MF file.
	 * 
	 * @return
	 */
	public File getManifestMf() {
		return manifestMf;
	}

	/**
	 * Returns the .project file.
	 * 
	 * @return
	 */
	public File getProjectFile() {
		return projectFile;
	}
}
