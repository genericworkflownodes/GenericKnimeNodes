package com.genericworkflownodes.knime.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.genericworkflownodes.knime.parameter.ListParameter;
import com.genericworkflownodes.knime.parameter.Parameter;

public class PlainNodeConfigurationWriter {
	private INodeConfiguration store;

	private static String LINESEP = System.getProperty("line.separator");

	public void init(INodeConfiguration store) {
		this.store = store;
	}

	public void write(String filename) throws IOException {
		FileWriter out = new FileWriter(new File(filename));

		for (String key : store.getParameterKeys()) {
			Parameter<?> p = store.getParameter(key);
			StringBuffer sb = new StringBuffer();
			if (p instanceof ListParameter) {
				ListParameter lp = (ListParameter) p;
				for (String value : lp.getStrings()) {
					sb.append(String.format("\"%s\"\t", value));
				}
			} else {
				sb.append(String.format("\"%s\"\t", p.getStringRep()));
			}
			out.write(key + ":" + sb.toString() + LINESEP);
		}
		out.close();
	}
}
