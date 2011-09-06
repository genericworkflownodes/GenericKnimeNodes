package org.ballproject.knime.base.mime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.knime.core.data.DataType;
import org.knime.core.data.container.BlobDataCell;
import org.ballproject.knime.base.port.*;

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
	
	//public abstract MIMEFileCell createMimeFileCell(final File file) throws IOException;
	
}