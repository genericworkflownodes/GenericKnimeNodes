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

import java.io.Serializable;

/**
 * The MIMEtype class represents a classical MIME type of a file identified by
 * the associated file extension.
 *  
 * @author roettig
 *
 */
public class MIMEtype implements Serializable
{
	
	protected String  file_extension;
	protected boolean binary;
	
	/**
	 * constructs a new MIMEtype object associated with supplied file extension.
	 * 
	 * @param extension
	 */
	public MIMEtype(String extension)
	{
		this.file_extension = extension;
		this.binary = false;
	}
	
	/**
	 * constructs a new MIMEtype object associated with supplied file extension and binary flag.
	 * 
	 * @param extension
	 */
	public MIMEtype(String extension, boolean binary)
	{
		this.file_extension = extension;
		this.binary = binary;
	}
	
	/**
	 * returns the file extension associated with the MIME type.
	 * 
	 * @return file extension
	 */
	public String getExt()
	{
		return file_extension;
	}
	
	/**
	 * returns whether the file stores data in binary format.
	 * 
	 * @return isBinary
	 */
	public boolean isBinary()
	{
		return binary;
	}

	/**
	 * sets whether the file stores data in binary format.
	 * 
	 * @param binary
	 */
	public void setBinary(boolean binary)
	{
		this.binary = binary;
	}

	public static boolean equals(MIMEtype type1, MIMEtype type2)
	{
		return type1.getExt().equals(type2.getExt());
	}
}
