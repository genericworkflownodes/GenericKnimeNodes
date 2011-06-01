package org.ballproject.knime.base.port;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

import org.knime.core.data.DataCell;

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
