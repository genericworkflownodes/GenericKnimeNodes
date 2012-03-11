package org.ballproject.knime.nodegeneration.model.files;

import java.io.File;

public class CtdFile extends File {

	private static final long serialVersionUID = 7823704234803605729L;

	public CtdFile(File file) {
		super(file.getAbsolutePath());
	}
}
