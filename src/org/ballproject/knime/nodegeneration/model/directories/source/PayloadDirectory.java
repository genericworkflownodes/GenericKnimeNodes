package org.ballproject.knime.nodegeneration.model.directories.source;

import java.io.File;
import java.io.FileNotFoundException;

import org.ballproject.knime.nodegeneration.model.directories.Directory;

public class PayloadDirectory extends Directory {

	private static final long serialVersionUID = -400249694994228712L;

	public PayloadDirectory(File payloadDirectory) throws FileNotFoundException {
		super(payloadDirectory);
	}

}
