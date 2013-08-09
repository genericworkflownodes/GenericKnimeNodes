/**
 * Copyright (c) 2011, Marc Röttig, Stephan Aiche.
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

package com.genericworkflownodes.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Collection of Helper methods.
 * 
 * @author roettig, aiche
 */
public class Helper {

	/**
	 * Copies the content of the {@link InputStream} in to the {@link File}
	 * dest.
	 * 
	 * @param in
	 *            Strean to copy.
	 * @param dest
	 * @throws IOException
	 */
	public static void copyStream(InputStream in, File dest) throws IOException {
		FileOutputStream out = new FileOutputStream(dest);
		BufferedInputStream bin = new BufferedInputStream(in);

		try {
			byte[] buffer = new byte[2048];
			int len;
			while ((len = bin.read(buffer, 0, 2048)) != -1) {
				out.write(buffer, 0, len);
			}
			out.close();
			bin.close();
		} catch (IOException ex) {
			// try to close the streams
			out.close();
			bin.close();
		}

	}

	/**
	 * Local random number generator to ensure uniqueness of file names.
	 */
	private static Random RANDOM_NUMBER_GENERATOR = new Random();

	public static synchronized File getTempFile(String directory,
			String suffix, boolean autodelete) throws IOException {

		int num = RANDOM_NUMBER_GENERATOR.nextInt(Integer.MAX_VALUE);
		File file = new File(directory + File.separator
				+ String.format("%06d.%s", num, suffix));
		while (file.exists()) {
			num = RANDOM_NUMBER_GENERATOR.nextInt(Integer.MAX_VALUE);
			file = new File(directory + File.separator
					+ String.format("%06d.%s", num, suffix));
		}
		file.createNewFile();

		if (autodelete) {
			file.deleteOnExit();
		}

		return file;
	}

	public static synchronized File getTempFile(String suffix,
			boolean autodelete) throws IOException {
		File file = File.createTempFile("GKN", suffix);
		if (autodelete)
			file.deleteOnExit();
		return file;
	}

	public static synchronized File getTempDir(String directory, String prefix,
			boolean autodelete) throws IOException {

		int num = RANDOM_NUMBER_GENERATOR.nextInt(Integer.MAX_VALUE);
		File dir = new File(directory + File.separator
				+ String.format("%s%06d", prefix, num));
		while (dir.exists()) {
			num = RANDOM_NUMBER_GENERATOR.nextInt(Integer.MAX_VALUE);
			dir = new File(directory + File.separator
					+ String.format("%s%06d", prefix, num));
		}
		dir.mkdirs();

		if (autodelete) {
			dir.deleteOnExit();
		}

		return dir;
	}

	public static synchronized File getTempDir(String prefix, boolean autodelete)
			throws IOException {
		return getTempDir(System.getProperty("java.io.tmpdir"), prefix,
				autodelete);
	}

	/**
	 * Reads the first lines of the file into a string. At maximum maxLines will
	 * be read.
	 * 
	 * @param file
	 *            The file to read.
	 * @param maxLines
	 *            The number of maximal lines to read.
	 * @return The string containing the first maxLines lines.
	 * @throws IOException
	 *             if the file does not exist or cannot be opened or read.
	 */
	public static String readFileSummary(File file, int maxLines)
			throws IOException {
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);

		StringBuffer sb = new StringBuffer();
		try {

			String line = "";

			int cnt = 0;

			sb.append("File path: " + file.getAbsolutePath()
					+ System.getProperty("line.separator"));
			sb.append("File size: " + file.length() + " bytes"
					+ System.getProperty("line.separator"));

			Date date = new Date(file.lastModified());
			Format formatter = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss");
			String s = formatter.format(date);

			sb.append("File time: " + s + System.getProperty("line.separator"));

			sb.append(String.format(
					"File content (first %d lines):"
							+ System.getProperty("line.separator"), maxLines));

			while ((line = br.readLine()) != null) {
				sb.append(line + System.getProperty("line.separator"));
				cnt++;
				if (cnt > maxLines) {
					sb.append("######### OUTPUT TRUNCATED #########"
							+ System.getProperty("line.separator"));
					break;
				}
			}
		} catch (IOException ex) {
			// close readers
			br.close();
			fr.close();
			// rethrow
			throw ex;
		}

		// close readers
		br.close();
		fr.close();

		return sb.toString();
	}

	/**
	 * Copies the content of to_copy into target. Assumes that src and target
	 * have the same size.
	 * 
	 * @param src
	 * @param target
	 */
	public static void array2dcopy(final String[][] src, final String[][] target) {
		assert (target.length == src.length);

		for (int i = 0; i < target.length; ++i) {
			int array_length = src[i].length;
			target[i] = new String[src[i].length];
			System.arraycopy(src[i], 0, target[i], 0, array_length);
		}
	}
}
