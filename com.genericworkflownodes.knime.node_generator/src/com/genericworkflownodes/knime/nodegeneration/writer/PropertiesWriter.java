package com.genericworkflownodes.knime.nodegeneration.writer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class PropertiesWriter {
	private File propertiesFile;

	public PropertiesWriter(File propertiesFile) {
		this.propertiesFile = propertiesFile;
	}

	public void write(Map<String, String> pairs) throws IOException {
		Properties p = new Properties();
		for (String key : pairs.keySet()) {
			p.put(key, pairs.get(key));
		}
		p.store(new FileOutputStream(propertiesFile), null);
	}
}
