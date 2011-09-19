package org.ballproject.knime.base.mime.demangler;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.ballproject.knime.base.mime.MIMEFileCell;
import org.knime.chem.types.SdfCell;
import org.knime.chem.types.SdfCellFactory;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataType;

public class SDFileDemangler implements Demangler, Iterator<DataCell>
{
	private byte[] data;
	private BufferedReader br;
	
	@Override
	public Iterator<DataCell> demangle(MIMEFileCell cell)
	{
		data = cell.getData();
		InputStream in   = new ByteArrayInputStream(data);
		br  = new BufferedReader(new InputStreamReader(in));
		return this;
	}
	
	@Override
	public boolean hasNext()
	{
		boolean ready = false;
		try
		{
			ready = br.ready();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return ready;
	}

	@Override
	public DataCell next()
	{
		String line;
		StringBuffer buf = new StringBuffer();
		try
		{
			while((line=br.readLine())!=null)
			{
				buf.append(line+"\n");
				if(line.equals("$$$$"))
				{
					break;
				}
			}
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return SdfCellFactory.create(buf.toString());
	}

	@Override
	public void remove()
	{
		// NOP
	}

	@Override
	public void close()
	{
		try
		{
			br.close();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public DataType getType()
	{
		return SdfCell.TYPE;
	}
}
