package org.ballproject.knime.nodegeneration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.ballproject.knime.nodegeneration.model.files.CTDFile;

public class DatWriter {

	private File datFile;

	public DatWriter(File dateFile) {
		this.datFile = dateFile;
	}

	public void write(List<CTDFile> ctdFiles) throws IOException {
		FileWriter fileWriter = new FileWriter(this.datFile);
		for (CTDFile ctdFile : ctdFiles) {
			String fixedNodeName = Utils.fixKNIMENodeName(ctdFile
					.getNodeConfiguration().getName());
			fileWriter.write(fixedNodeName + "\n");
		}
		fileWriter.close();
	}
}
