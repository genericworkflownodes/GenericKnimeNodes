package org.ballproject.knime.base.util;


import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileStash
{
	public static FileStash instance;
	
	private static String STASH_DIR_ROOT = System.getProperty("java.io.tmpdir");
	private static String STASH_DIR;
	private static DateFormat fmt = new SimpleDateFormat("MM-dd-yyyy");
	
	public static FileStash getInstance()
	{
		if(instance==null)
		{
			instance = new FileStash();
		}
		return instance;
	}
	
	private FileStash()
	{	
		STASH_DIR = STASH_DIR_ROOT+File.separator+"GKN_STASH";
	}
	
	public String allocateFile(String extension) throws IOException
	{
		String slot     = fmt.format(new Date());
		String slotpath = STASH_DIR+File.separator+slot;
		File   slotfile = new File(slotpath);
		slotfile.mkdirs();
		return Helper.getTemporaryFilename(STASH_DIR+File.separator+slot, extension, false);
	}
	
	public String getAbsolutePath(String relURI)
	{
		return new File(STASH_DIR,relURI).getAbsolutePath();
	}
	
	public URI getAbsoluteURI(URI relURI)
	{
		URI ret = null;
		try
		{
			ret = new URI(STASH_DIR+File.separator+relURI.getPath());
		} 
		catch (URISyntaxException e)
		{
			e.printStackTrace();
		}
		return ret;
	}
	
	public String getStashDirectory()
	{
		return STASH_DIR;
	}
	
	public void setStashDirectory(String dir)
	{
		File sdir = new File(dir);
		if(!sdir.exists())
		{
			sdir.mkdirs();
		}
		if(!sdir.isDirectory())
		{
			throw new IllegalArgumentException("no valid directory path was supplied");
		}
		else
			STASH_DIR = dir;
	}

	public URI allocatePortableFile(String extension) throws IOException
	{
		String file =  Helper.getRelativeTemporaryFilename(STASH_DIR, extension, true);
		URI ret = null;
		try
		{
			ret = new URI(file);
		} 
		catch (URISyntaxException e)
		{
			e.printStackTrace();
		}
		return ret;
	}
		
	
}
