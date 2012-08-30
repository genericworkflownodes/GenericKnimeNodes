package org.ballproject.knime.nodegeneration.exceptions;

import org.knime.core.data.url.MIMEType;

public class UnknownMimeTypeException extends Exception {

	private static final long serialVersionUID = 598884824362988075L;

	public UnknownMimeTypeException(MIMEType type) {
		super("Unknown MIME type: " + type.getExtension());
	}
}
