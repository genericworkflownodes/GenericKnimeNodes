package org.ballproject.knime.nodegeneration.model.directories;

import java.io.File;
import java.io.FileNotFoundException;

import org.ballproject.knime.nodegeneration.model.Directory;

public class TempDirectory extends Directory {

	private static final long serialVersionUID = 1887091568726734763L;

	public TempDirectory(String prefix) throws FileNotFoundException {
		super(new File(System.getProperty("java.io.tmpdir"), prefix + "-"
				+ Long.toString(System.nanoTime())));
	}

}
