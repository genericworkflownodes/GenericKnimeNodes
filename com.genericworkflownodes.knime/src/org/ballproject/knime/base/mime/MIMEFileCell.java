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
import java.io.IOException;

import org.knime.core.data.DataType;
import org.knime.core.data.container.BlobDataCell;

import com.genericworkflownodes.util.Helper;

/**
 * The abstract MIMEFileCell class is the base class for any MIME-based cells
 * within GenericKnimeNodes.
 * 
 * @author roettig
 * 
 */
public abstract class MIMEFileCell extends BlobDataCell implements
		MIMEFileValue, MimeMarker {
	private static final long serialVersionUID = 1533146970730706846L;
	public transient DataType TYPE;
	protected MIMEFileDelegate data_delegate;

	/**
	 * Size limit for read operation.
	 */
	private static final long SIZELIMIT = 20000000;

	public DataType getDataType() {
		return TYPE;
	}

	public MIMEFileCell() {
		data_delegate = new DefaultMIMEFileDelegate();
	}

	/**
	 * read in the byte data contained in the supplied file.
	 * 
	 * @param file
	 *            filename to read
	 * 
	 * @throws IOException
	 */
	public void read(File file) throws IOException {
		if (file.length() > SIZELIMIT) {
			data_delegate = new ReferenceMIMEFileDelegate();
		} else {
			data_delegate = new DefaultMIMEFileDelegate();
		}

		data_delegate.read(file);
	}

	/**
	 * write the byte data stored out into the supplied file.
	 * 
	 * @param file
	 *            filename to write
	 * 
	 * @throws IOException
	 */
	public void write(String filename) throws IOException {
		data_delegate.write(filename);
	}

	public File writeTemp(String directory) throws IOException {
		File file = Helper.getTempFile(directory, getExtension(), false);
		return data_delegate.writeTemp(file.getAbsolutePath());
	}

	@Override
	public int hashCode() {
		// hashCode is considered to give hash code of
		// wrapped content
		return data_delegate.getHash();
	}

	@Override
	public byte[] getData() {
		return data_delegate.getByteArrayReference();
	}

	@Override
	public MIMEFileDelegate getDelegate() {
		return data_delegate;
	}
}