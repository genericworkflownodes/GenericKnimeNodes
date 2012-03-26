package org.ballproject.knime.nodegeneration.exceptions;

import org.ballproject.knime.base.mime.MIMEtype;

public class UnknownMimeTypeException extends Exception {

	private static final long serialVersionUID = 598884824362988075L;

	public UnknownMimeTypeException(MIMEtype type) {
		super("Unknown MIME type: " + type.getExt());
	}
}
