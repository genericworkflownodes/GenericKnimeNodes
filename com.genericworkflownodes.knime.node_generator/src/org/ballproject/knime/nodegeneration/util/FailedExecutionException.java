package org.ballproject.knime.nodegeneration.util;

public class FailedExecutionException extends Exception {

	private static final long serialVersionUID = 9098832559268555765L;

	public FailedExecutionException(String message) {
		super(message);
	}

	public FailedExecutionException(String message, Exception ex) {
		super(message, ex);
	}
}
