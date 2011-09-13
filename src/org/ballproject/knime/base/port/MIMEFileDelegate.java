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

package org.ballproject.knime.base.port;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;


public class MIMEFileDelegate implements Serializable
{
	private byte[] m_content;
	
	
	public byte[] getByteArrayReference()
	{
		return m_content;
	}

	public void setContent(byte[] content)
	{
		int len = content.length;
		this.m_content = new byte[len];
		System.arraycopy(content, 0, this.m_content, 0, len);
	}
	
	public boolean isEqual(MIMEFileDelegate del)
	{
		return false;
	}
	
	
	public int getHash()
	{
		return m_content.hashCode();
	}
	
	public void write(String filename) throws IOException
	{
		FileOutputStream out = new FileOutputStream(new File(filename)); 
		out.write(m_content);
		out.close();
	}
}
