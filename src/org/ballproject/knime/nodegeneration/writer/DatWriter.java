package org.ballproject.knime.nodegeneration.writer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.ballproject.knime.nodegeneration.model.files.CTDFileX;
import org.ballproject.knime.nodegeneration.util.Utils;

public class DatWriter {

	private File datFile;

	public DatWriter(File dateFile) {
		this.datFile = dateFile;
	}

	public void write(List<CTDFileX> ctdFiles) throws IOException {
		FileWriter fileWriter = new FileWriter(this.datFile);
		for (CTDFileX ctdFile : ctdFiles) {
			String fixedNodeName = Utils.fixKNIMENodeName(ctdFile
					.getNodeConfiguration().getName());
			fileWriter.write(fixedNodeName + "\n");
		}
		fileWriter.close();
	}
}
