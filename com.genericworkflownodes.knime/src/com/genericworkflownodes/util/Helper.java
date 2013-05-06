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

public class Helper {

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

	private static Random RANDOM_NUMBER_GENERATOR = new Random();

	public static synchronized File getTempFile(String directory,
			String suffix, boolean autodelete) throws IOException {

		int num = Math.abs(RANDOM_NUMBER_GENERATOR.nextInt());
		File file = new File(directory + File.separator
				+ String.format("%06d.%s", num, suffix));
		while (file.exists()) {
			num = Math.abs(RANDOM_NUMBER_GENERATOR.nextInt());
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

		int num = Math.abs(RANDOM_NUMBER_GENERATOR.nextInt());
		File dir = new File(directory + File.separator
				+ String.format("%s%06d", prefix, num));
		while (dir.exists()) {
			num = Math.abs(RANDOM_NUMBER_GENERATOR.nextInt());
			dir = new File(directory + File.separator
					+ String.format("%s%06d", prefix, num));
		}
		dir.mkdirs();

		if (autodelete) {
			dir.deleteOnExit();
		}

		return dir;
	}

	public static synchronized File getTempDir(String prefix,
			boolean autodelete) throws IOException {
		return getTempDir(System.getProperty("java.io.tmpdir"), prefix,
				autodelete);
	}

	public static String readFileSummary(File file, int maxLines)
			throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		StringBuffer sb = new StringBuffer();

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

		return sb.toString();
	}

}
