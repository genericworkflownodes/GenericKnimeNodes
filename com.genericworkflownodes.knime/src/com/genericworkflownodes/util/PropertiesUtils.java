package com.genericworkflownodes.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 * Utility class to easily handle {@link Properties}.
 * 
 * @author bkahlert
 */
public class PropertiesUtils {
	/**
	 * Loads {@link Properties} from a given {@link File}.
	 * 
	 * @param file
	 * @return empty if file is no {@link File}
	 * @throws IOException
	 */
	public static Properties load(File file) throws IOException {
		Properties properties = new Properties();
		if (file.isFile()) {
			FileReader fr = new FileReader(file);
			try {
				properties.load(fr);
			} catch (IOException ex) {
				// close file reader and rethrow
				fr.close();
				throw ex;
			}
			// ensure closed file reader
			fr.close();
		}
		return properties;
	}

	/**
	 * Saves the given {@link Properties} to the given {@link File}. If
	 * necessary the {@link File} is automatically created.
	 * 
	 * @param file
	 * @param properties
	 * @throws IOException
	 */
	public static void save(File file, Properties properties)
			throws IOException {
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		FileWriter f_writer = new FileWriter(file);
		try {
			properties.store(f_writer, null);
		} catch (IOException ex) {
			// close file writer and rethrow
			f_writer.close();
			throw ex;
		}
		f_writer.close();
	}
}
