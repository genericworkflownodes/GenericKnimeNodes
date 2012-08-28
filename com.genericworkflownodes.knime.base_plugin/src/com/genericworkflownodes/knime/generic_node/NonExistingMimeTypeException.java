package com.genericworkflownodes.knime.generic_node;

public class NonExistingMimeTypeException extends Exception {

	private static final long serialVersionUID = 1L;

	public NonExistingMimeTypeException(String someFileName) {
		super("No matching registered MIME type for " + someFileName
				+ " found.");
	}

}
