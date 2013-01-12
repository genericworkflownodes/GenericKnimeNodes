package com.genericworkflownodes.knime.nodegeneration.exceptions;

public class UnknownMimeTypeException extends Exception {

	private static final long serialVersionUID = 598884824362988075L;

	public UnknownMimeTypeException(String type) {
		super("Unknown MIME type: " + type);
	}
}
