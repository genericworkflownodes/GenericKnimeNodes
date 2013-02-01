package org.ballproject.knime.base.mime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * The MIMEFileDelegate stores the byte data contained within a MIME-typed file.
 * 
 * @author roettig
 * 
 */
public class DefaultMIMEFileDelegate implements MIMEFileDelegate {
	private static final long serialVersionUID = -8196537893700127336L;
	private byte[] m_content;

	public byte[] getByteArrayReference() {
		return m_content;
	}

	public void setContent(byte[] content) {
		int len = content.length;
		this.m_content = new byte[len];
		System.arraycopy(content, 0, this.m_content, 0, len);
	}

	public boolean isEqual(MIMEFileDelegate del) {
		return false;
	}

	public int getHash() {
		String s = new String(m_content);
		return s.hashCode();
	}

	public void read(File file) throws IOException {
		FileInputStream fin = new FileInputStream(file);

		int len = (int) file.length();
		m_content = new byte[len];

		fin.read(m_content);
		fin.close();
	}

	public void write(String filename) throws IOException {
		FileOutputStream out = new FileOutputStream(new File(filename));
		out.write(m_content);
		out.close();
	}

	@Override
	public File writeTemp(String filename) throws IOException {
		write(filename);
		return new File(filename);
	}
}
