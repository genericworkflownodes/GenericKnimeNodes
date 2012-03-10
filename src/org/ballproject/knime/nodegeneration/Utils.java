package org.ballproject.knime.nodegeneration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
}
