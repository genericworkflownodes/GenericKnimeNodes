/**
 * Copyright (c) 2011-2012, Marc Röttig, Stephan Aiche.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of {@link IMIMEtypeRegistry}.
 * 
 * @author roettig, aiche
 */
public class DefaultMIMEtypeRegistry implements IMIMEtypeRegistry {

	/**
	 * Internal mapping of known file extensions to the associated
	 * {@link MIMEType}.
	 */
	private Map<String, String> fileExtensionToMIMEType;

	/**
	 * Default constructor.
	 */
	public DefaultMIMEtypeRegistry() {
		fileExtensionToMIMEType = new HashMap<String, String>();
	}

	@Override
	public void registerMIMEtype(final String mt) {
		String extension = mt;
		fileExtensionToMIMEType.put(extension, mt);
	}

	@Override
	public String getMIMEtype(final String fileName) {
		String mt = null;

		List<String> candidates = new ArrayList<String>();

		for (String ext : fileExtensionToMIMEType.keySet()) {
			if (fileName.toLowerCase().endsWith(ext)) {
				candidates.add(fileExtensionToMIMEType.get(ext));
			}
		}

		Collections.sort(candidates, new Comparator<String>() {
			@Override
			public int compare(final String lhs, final String rhs) {
				return lhs.compareToIgnoreCase(rhs);
			}
		});

		if (candidates.size() > 0) {
			mt = candidates.get(0);
		}
		return mt;
	}

	@Override
	public String getMIMETypeByExtension(final String extension) {
		if (fileExtensionToMIMEType.containsKey(extension)) {
			return fileExtensionToMIMEType.get(extension);
		} else {
			return null;
		}
	}
}
