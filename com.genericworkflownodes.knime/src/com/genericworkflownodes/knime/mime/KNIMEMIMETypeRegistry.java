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
package com.genericworkflownodes.knime.mime;

import org.knime.base.filehandling.mime.MIMEMap;
import org.knime.base.filehandling.mime.MIMETypeEntry;

/**
 * Abstracts the KNIME filehandling mime types in form of a
 * {@link IMIMEtypeRegistry}.
 * 
 * @author aiche
 */
public class KNIMEMIMETypeRegistry implements IMIMEtypeRegistry {

	@Override
	public String getMIMEtype(String filename) {
		// extract extension
		String type = null;
		String foundExtension = "";
		for (MIMETypeEntry entry : MIMEMap.getAllTypes()) {
			for (String ext : entry.getExtensions()) {
				if (filename.toLowerCase().endsWith(
						"." + ext.trim().toLowerCase())
						&& ext.length() > foundExtension.length()) {
					type = entry.getType();
					foundExtension = ext.trim();
				}
			}
		}
		return type;
	}

	@Override
	public String getMIMETypeByExtension(String extension) {
		for (MIMETypeEntry entry : MIMEMap.getAllTypes()) {
			for (String ext : entry.getExtensions()) {
				if (ext.toLowerCase().equals(extension.toLowerCase())) {
					return entry.getType();
				}
			}
		}
		return null;
	}

	@Override
	public void registerMIMEtype(String mt) {
	}

}
