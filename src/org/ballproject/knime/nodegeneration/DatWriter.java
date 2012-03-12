package org.ballproject.knime.nodegeneration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

public class DatWriter {

	private File datFile;

	public DatWriter(File dateFile) {
		this.datFile = dateFile;
	}

	public void write(Set<String> ext_tools) throws IOException {
		FileWriter fileWriter = new FileWriter(this.datFile);
		for (String line : ext_tools) {
			fileWriter.write(line + "\n");
		}
		fileWriter.close();
	}
}
