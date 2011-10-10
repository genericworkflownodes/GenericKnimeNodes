package org.ballproject.knime.base.util;


import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

public class FileStash
{
	public static FileStash instance;
	
	private static String STASH_DIR_ROOT = System.getProperty("java.io.tmpdir");
	private static String STASH_DIR;
	private static URLStreamHandler handler;
	
	public static FileStash getInstance()
	{
		if(instance==null)
		{
			instance = new FileStash();
			handler  = new URLFileStashHandler(STASH_DIR);
			//FileStashFactory filestashFactory = new FileStashFactory(STASH_DIR);
			//URL.setURLStreamHandlerFactory(filestashFactory);
		}
		return instance;
	}
	
	private FileStash()
	{	
		STASH_DIR = STASH_DIR_ROOT+File.separator+"GKN_STASH";
		try
		{
			baseURL = new URL("file://"+STASH_DIR);
		} catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		try
		{
			STASH_DIR = Helper.getTemporaryDirectory(STASH_DIR_ROOT, "STASH", true);
		} 
		catch (IOException e)
		{
			e.printStackTrace();
			throw new RuntimeException("FileStash object could not allocate directory");
		}
		*/
	}
	
	public String allocateFile(String extension) throws IOException
	{
		return Helper.getTemporaryFilename(STASH_DIR, extension, true);
	}
	
	private URL baseURL;
	
	public URL getAbsoluteURL(String relURL)
	{
		URL ret = null;
		try
		{
			ret = new URL(baseURL,relURL);
		} 
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	/*
	public URL allocatePortableFile2(String extension) throws IOException
	{
		String file =  Helper.getRelativeTemporaryFilename(STASH_DIR, extension, true);
		return new URL(new File(file);
	}
	*/
	public URL allocatePortableFile(String extension) throws IOException
	{
		String file =  Helper.getRelativeTemporaryFilename(STASH_DIR, extension, true);
		//return  new URL("filestash://"+file, handler);
		return  new URL("filestash://localhost/"+file);
	}
	
	public static void main(String[] args) throws IOException
	{
		URL url = FileStash.getInstance().allocatePortableFile("pdb");
		System.out.println(url.openConnection().getURL());
		System.out.println(url);
	}
	
	public static class URLFileStashHandler extends URLStreamHandler 
	{
		private String stashbasedir;
		
		public URLFileStashHandler(String basedir)
		{
			stashbasedir = basedir;
		}
		
		public URLConnection openConnection(URL url) throws IOException 
		{
			 URL newURL = new URL("file://"+stashbasedir+"/"+url.getPath());
			 return newURL.openConnection();
		}

		@Override
		protected String toExternalForm(URL u)
		{
			return String.format("%s://%s/%s",u.getProtocol(),u.getHost(),u.getPath());
		}
	}
}
