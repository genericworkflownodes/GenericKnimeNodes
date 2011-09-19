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

package org.ballproject.knime.nodegeneration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * The TemplateFiller class is used to replace within a string template designated tokens
 * with variable text.
 * 
 * @author roettig
 *
 */
public class TemplateFiller
{
	private String data;
	
	/**
	 * reads in the template from file.
	 * 
	 * @param file template filename
	 * @throws IOException
	 */
	public void read(String file) throws IOException
	{
		read(new File(file));
	}
	
	/**
	 * reads in the template from file.
	 * 
	 * @param file file
	 * @throws IOException
	 */
	public void read(File file) throws IOException
	{
		FileInputStream in = new FileInputStream(file);
		read(in);
		in.close();
	}
	
	/**
	 * reads in the template from input stream.
	 * 
	 * @param in input stream
	 * @throws IOException
	 */
	public void read(InputStream in) throws IOException
	{
		StringBuffer buf = new StringBuffer();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		while( (line=reader.readLine())!=null )
		{
			buf.append(line+System.getProperty("line.separator"));
		}
		data = buf.toString();
	}
	
	/**
	 * replaces the specified token within the template with the supplied text.
	 * 
	 * @param token target token
	 * @param value text to fill in
	 */
	public void replace(String token, String value)
	{
		data = data.replace(token,value);
	}
	
	/**
	 * writes the filled template out to supplied stream.  
	 * 
	 * @param out output stream
	 * @throws IOException
	 */
	public void write(OutputStream out) throws IOException
	{
		out.write(data.getBytes());
	}

	/**
	 * writes the filled template out to supplied file.  
	 * 
	 * @param file output file
	 * @throws IOException
	 */
	public void write(File file) throws IOException
	{
		file.getParentFile().mkdirs();
		FileOutputStream out = new FileOutputStream(file);
		write(out);
		out.close();
	}
	
	/**
	 * writes the filled template out to supplied file.  
	 * 
	 * @param file file name
	 * @throws IOException
	 */
	public void write(String file) throws IOException
	{
		write(new File(file));
	}
}
