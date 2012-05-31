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

package org.ballproject.knime.base.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.Random;

public class Helper {
	public static class OS {
		public static int WIN = 0;
		public static int MAC = 1;
		public static int UNIX = 2;
	}

	public static int getOS() {
		String os = System.getProperty("os.name");

		// TODO might be to lax
		if (os.toLowerCase().contains("nux")
				|| os.toLowerCase().contains("nix")) {
			return OS.UNIX;
		}
		if (os.toLowerCase().contains("mac")) {
			return OS.MAC;
		}

		return OS.WIN;
	}

	static public boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	public static void copyStream(InputStream in, File dest) throws IOException {
		FileOutputStream out = new FileOutputStream(dest);
		BufferedInputStream bin = new BufferedInputStream(in);
		byte[] buffer = new byte[2048];
		int len;
		while ((len = bin.read(buffer, 0, 2048)) != -1) {
			out.write(buffer, 0, len);
		}
		out.close();
		bin.close();
	}

	public static void copyFile(File in, File out) throws IOException {
		FileChannel inChannel = new FileInputStream(in).getChannel();
		FileChannel outChannel = new FileOutputStream(out).getChannel();
		try {
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} catch (IOException e) {
			throw e;
		} finally {
			if (inChannel != null)
				inChannel.close();
			if (outChannel != null)
				outChannel.close();
		}
	}

	static private Random rng = new Random();

	static synchronized public String getTemporaryFilename(String directory,
			String suffix, boolean autodelete) throws IOException {

		int num = Math.abs(rng.nextInt());
		File f = new File(directory + File.separator
				+ String.format("%06d.%s", num, suffix));
		while (f.exists()) {
			num = Math.abs(rng.nextInt());
			f = new File(directory + File.separator
					+ String.format("%06d.%s", num, suffix));
		}
		f.createNewFile();

		if (autodelete)
			f.deleteOnExit();

		return f.getAbsolutePath();
	}

	static synchronized public String getRelativeTemporaryFilename(
			String directory, String suffix, boolean autodelete)
			throws IOException {

		int num = Math.abs(rng.nextInt());
		File f = new File(directory + File.separator
				+ String.format("%06d.%s", num, suffix));
		while (f.exists()) {
			num = Math.abs(rng.nextInt());
			f = new File(directory + File.separator
					+ String.format("%06d.%s", num, suffix));
		}
		f.createNewFile();

		if (autodelete)
			f.deleteOnExit();

		return f.getName();
	}

	static synchronized public String getTemporaryFilename(String suffix,
			boolean autodelete) throws IOException {
		return getTemporaryFilename(System.getProperty("java.io.tmpdir"),
				suffix, autodelete);
	}

	static synchronized public String getTemporaryDirectory(String directory,
			String prefix, boolean autodelete) throws IOException {

		int num = Math.abs(rng.nextInt());
		File f = new File(directory + File.separator
				+ String.format("%s%06d", prefix, num));
		while (f.exists()) {
			num = Math.abs(rng.nextInt());
			f = new File(directory + File.separator
					+ String.format("%s%06d", prefix, num));
		}
		f.mkdirs();

		if (autodelete)
			f.deleteOnExit();

		return f.getAbsolutePath();
	}

	static synchronized public String getTemporaryDirectory(String prefix,
			boolean autodelete) throws IOException {
		return getTemporaryDirectory(System.getProperty("java.io.tmpdir"),
				prefix, autodelete);
	}

	public static Object createObject(String className) {
		Object object = null;
		try {
			Class<?> classDefinition = Class.forName(className);
			object = classDefinition.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return object;
	}
}
