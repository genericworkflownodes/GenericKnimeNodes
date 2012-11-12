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

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.knime.core.data.url.MIMEType;
import org.knime.core.data.uri.URIContent;


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
	private Map<String, MIMEType> fileExtensionToMIMEType;

	/**
	 * Default constructor.
	 */
	public DefaultMIMEtypeRegistry() {
		fileExtensionToMIMEType = new HashMap<String, MIMEType>();
	}

	@Override
	public void registerMIMEtype(final MIMEType mt) {
		String extension = mt.getExtension();
		fileExtensionToMIMEType.put(extension, mt);
	}

	@Override
	public MIMEType getMIMEtype(final String fileName) {
		MIMEType mt = null;

		List<MIMEType> candidates = new ArrayList<MIMEType>();

		for (String ext : fileExtensionToMIMEType.keySet()) {
			if (fileName.toLowerCase().endsWith(ext)) {
				candidates.add(fileExtensionToMIMEType.get(ext));
			}
		}

		Collections.sort(candidates, new Comparator<MIMEType>() {
			@Override
			public int compare(final MIMEType lhs, final MIMEType rhs) {
				return lhs.getExtension().compareToIgnoreCase(
						rhs.getExtension());
			}
		});

		if (candidates.size() > 0) {
			mt = candidates.get(0);
		}
		return mt;
	}

	@Override
	public MIMEType getMIMETypeByExtension(final String extension) {
		if (fileExtensionToMIMEType.containsKey(extension)) {
			return fileExtensionToMIMEType.get(extension);
		} else {
			return null;
		}
	}
}
