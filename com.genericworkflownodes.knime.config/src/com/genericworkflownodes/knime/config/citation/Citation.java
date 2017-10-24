package com.genericworkflownodes.knime.config.citation;
/**
 * Copyright (c) 2017, Julianus Pfeuffer
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

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Represents a Citation for a tool.
 * 
 * @author jpfeuffer
 */
public class Citation {
	private String m_doi;
	private URL m_url;
	
	public Citation(String doi, URL url) {
		m_doi = doi;
		m_url = url;
	}

	public String getDoi() {
		return m_doi;
	}
	
	public URL getDoiLink() throws MalformedURLException {
		return new URL("https://doi.org/" + m_doi);
	}

	public void setDoi(String doi) {
		m_doi = doi;
	}

	public URL getUrl() {
		return m_url;
	}

	public void setUrl(URL url) {
		m_url = url;
	}
	
}
