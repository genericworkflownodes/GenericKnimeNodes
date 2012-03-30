package org.ballproject.knime.base.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtils {
	public static void decompressTo(File targetDir, InputStream in) {
		targetDir.mkdirs();
		try {
			ZipInputStream zin = new ZipInputStream(in);
			ZipEntry ze = null;

			byte[] buffer = new byte[2048];

			while ((ze = zin.getNextEntry()) != null) {
				File targetFile = new File(targetDir, ze.getName());

				if (ze.isDirectory()) {
					targetFile.mkdirs();
				} else {
					targetFile.getParentFile().mkdirs();

					FileOutputStream fout = new FileOutputStream(targetFile);

					int size;
					while ((size = zin.read(buffer, 0, 2048)) != -1) {
						fout.write(buffer, 0, size);
					}

					zin.closeEntry();
					fout.flush();
					fout.close();
				}
			}
			zin.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
