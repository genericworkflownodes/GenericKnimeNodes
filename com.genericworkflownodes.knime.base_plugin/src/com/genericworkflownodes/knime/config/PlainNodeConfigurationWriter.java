package com.genericworkflownodes.knime.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class PlainNodeConfigurationWriter {
	private INodeConfigurationStore store;

	private static String LINESEP = System.getProperty("line.separator");

	public void init(INodeConfigurationStore store) {
		this.store = store;
	}

	public void write(String filename) throws IOException {
		FileWriter out = new FileWriter(new File(filename));

		for (String key : store.getParameterKeys()) {
			List<String> values = store.getMultiParameterValue(key);
			StringBuffer sb = new StringBuffer();
			for (String value : values) {
				sb.append(String.format("\"%s\"\t", value));
			}
			out.write(key + ":" + sb.toString() + LINESEP);
		}
		out.close();
	}
}
