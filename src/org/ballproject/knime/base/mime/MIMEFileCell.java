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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.knime.core.data.DataType;
import org.knime.core.data.container.BlobDataCell;
import org.ballproject.knime.base.port.*;

/**
 * The abstract MIMEFileCell class is the base class for any MIME-based cells within GenericKnimeNodes.
 * 
 * @author roettig
 *
 */
public abstract class MIMEFileCell extends BlobDataCell implements MIMEFileValue, MimeMarker
{
	public transient DataType TYPE;
	protected MIMEFileDelegate data;
	
	public DataType getDataType()
	{
		return TYPE;
	}
	
	public MIMEFileCell()
	{
		data = new MIMEFileDelegate(); 
	}
	
	/**
	 * read in the byte data contained in the supplied file.
	 * 
	 * @param file file to read
	 * 
	 * @throws IOException
	 */
	public void read(File file) throws IOException
	{
		FileInputStream fin = new FileInputStream(file);		 
		
		int    len = (int) file.length();
		byte[] b   = new byte[len];
		
		fin.read(b);
		fin.close();
		
		setData(b);
	}
		
	@Override
	public int hashCode()
	{
		return data.getHash();
	}	
	
	@Override
	public byte[] getData()
	{
		return data.getByteArrayReference();
	}

	@Override
	public void setData(byte[] dat)
	{
		data.setContent(dat);
	}
	
	@Override
	public MIMEFileDelegate getDelegate()
	{
		return data;
	}	
}