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

package org.ballproject.knime.base.mime;

/**
 * The interface MIMEtypeRegistry defines methods needed to build a (recursive) database or registry of MIME types known
 * to GenericKnimeNodes.
 * 
 * @author roettig
 *
 */
public interface MIMEtypeRegistry
{
	/**
	 * recursively add an additional MIMEtype registry to an existing one.
	 *  
	 * This allows each new plugin the addition of new MIMEtypes into an existing global registry.  
	 *  
	 * @param resolver another registry
	 */
	void addResolver(MIMEtypeRegistry resolver);
	
	/**
	 * returns the MIMEFileCell corresponding to a given filename.
	 * 
	 * @param filename name of the file
	 * 
	 * @return MIMEFileCell
	 */
	MIMEFileCell getCell(String filename);
	
	/**
	 * returns MIMEtype of a given filename.
	 * 
	 * @param filename name of the file
	 * 
	 * @return MIMEtype
	 */
	MIMEtype getMIMEtype(String filename);
}
