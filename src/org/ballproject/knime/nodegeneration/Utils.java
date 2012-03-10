package org.ballproject.knime.nodegeneration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Utils {
	/**
	 * returns all prefix paths of a given path.
	 * 
	 * /foo/bar/baz --> [/foo/bar/,/foo/,/]
	 * 
	 * @param path
	 * @return
	 */
	public static List<String> getPathPrefixes(String path) {
		List<String> ret = new ArrayList<String>();
		File pth = new File(path);
		ret.add(path);
		while (pth.getParent() != null) {
			ret.add(pth.getParent());
			pth = pth.getParentFile();
		}
		return ret;
	}

	/**
	 * returns the prefix path of the given path.
	 * 
	 * /foo/bar/baz ---> /foo/bar/
	 * 
	 * @param path
	 * @return
	 */
	public static String getPathPrefix(String path) {
		File pth = new File(path);
		return pth.getParent();
	}

	/**
	 * returns the path suffix for a given path.
	 * 
	 * /foo/bar/baz --> baz
	 * 
	 * @param path
	 * @return
	 */
	public static String getPathSuffix(String path) {
		File pth = new File(path);
		return pth.getName();
	}

	public static void zipDirectory(File directory, File zipFile)
			throws IOException {
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
		addDir(directory, directory, out);
		out.close();
	}

	private static void addDir(File root, File directory, ZipOutputStream out)
			throws IOException {
		File[] files = directory.listFiles();
		byte[] tmpBuf = new byte[1024];

		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				addDir(root, files[i], out);
				continue;
			}
			FileInputStream in = new FileInputStream(files[i].getAbsolutePath());
			out.putNextEntry(new ZipEntry(files[i].getAbsolutePath().substring(
					root.getAbsolutePath().length())));
			int len;
			while ((len = in.read(tmpBuf)) > 0) {
				out.write(tmpBuf, 0, len);
			}
			out.closeEntry();
			in.close();
		}
	}
}
