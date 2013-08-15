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
package org.knime.core.data.uri.candidate;

import java.util.List;

import org.knime.core.data.filestore.FileStore;
import org.knime.core.data.uri.URIContent;
import org.knime.core.data.uri.URIPortObject;
import org.knime.core.data.uri.URIPortObjectSpec;

/**
 * URIPortObject which files are managed by the KNIME {@link FileStore}
 * functionality.
 * 
 * @author aiche
 */
public class URIFileStorePortObject extends URIPortObject {

	/**
	 * Constructor for new URI port objects.
	 * 
	 * @param spec
	 *            The non null spec. All file extensions in the spec must be
	 *            present in the list argument.
	 * @param uriContents
	 *            The contend for this object. Must not be null or contain null.
	 */
	public URIFileStorePortObject(URIPortObjectSpec spec,
			final List<URIContent> uriContents) {
		super(spec, uriContents);
	}

	/**
	 * Constructor for new URI port objects.
	 * 
	 * @param uriContents
	 *            The contend for this object. Must not be null or contain null.
	 */
	public URIFileStorePortObject(final List<URIContent> uriContents) {
		super(uriContents);
	}

}
