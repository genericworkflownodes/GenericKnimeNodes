/**
 * Copyright (c) 2012, Marc Röttig, Stephan Aiche.
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
package com.genericworkflownodes.knime.custom.payload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Utility class that provides convenience functions to handle zip files.
 * 
 * @author roettig, aiche
 */
public class ZipUtils {

	private static final int BUFFER_SIZE = 2048;

	/**
	 * Decompress the content of @p zipStream to the directory @p targetDir.
	 * 
	 * @param targetDir
	 *            The directory where the zip should be extracted.
	 * @param zipStream
	 *            The zip file stream.
	 * @param monitor
	 *            A already started progress monitor.
	 */
	public static void decompressTo(File targetDir, InputStream zipStream,
			IProgressMonitor monitor) {
		targetDir.mkdirs();
		FileOutputStream fout = null;
		try {
			ZipInputStream zin = new ZipInputStream(zipStream);
			ZipEntry ze = null;

			byte[] buffer = new byte[BUFFER_SIZE];

			while ((ze = zin.getNextEntry()) != null) {
				File targetFile = new File(targetDir, ze.getName());

				if (ze.isDirectory()) {
					targetFile.mkdirs();
					monitor.subTask("Extracting " + ze.getName());
				} else {
					if (!targetFile.getParentFile().exists()) {
						monitor.subTask("Extracting " + ze.getName());
						targetFile.getParentFile().mkdirs();
					}

					fout = new FileOutputStream(targetFile);

					int size;
					while ((size = zin.read(buffer, 0, BUFFER_SIZE)) != -1) {
						fout.write(buffer, 0, size);
					}

					zin.closeEntry();
					fout.flush();
					fout.close();
				}
				monitor.worked(1);
			}
			zin.close();
		} catch (Exception e) {
			// try to close the streams
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			e.printStackTrace();
		}
	}

	/**
	 * Do not extract the stream, just count the number of zip entries.
	 * 
	 * @param zipStream
	 *            The input stream containing the zip file.
	 * @return The number of files/folders in the zip stream.
	 */
	public static int countEntries(InputStream zipStream) {
		int numEntries = 0;
		try {
			ZipInputStream zin = new ZipInputStream(zipStream);
			while ((zin.getNextEntry()) != null) {
				++numEntries;
			}
			zin.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return numEntries;
	}
}
